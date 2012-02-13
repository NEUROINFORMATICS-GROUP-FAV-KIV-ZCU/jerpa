package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.dao.*;
import ch.ethz.origo.jerpa.data.tier.pojo.*;
import ch.ethz.origo.jerpa.ededclient.generated.*;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.*;

/**
 * @author Petr Miko
 *         Class for basic synchronization of information about contents of EEG base database.
 *         <p/>
 *         Still under development, so far only downloads information.
 */
public class DataSyncer {

    private final EDEDClient session;
    private final EDEDBController controller;

    private DataFileDao dataFileDao = DaoFactory.getDataFileDao();
    private ExperimentDao experimentDao = DaoFactory.getExperimentDao();
    private HardwareDao hardwareDao = DaoFactory.getHardwareDao();
    private ResearchGroupDao researchGroupDao = DaoFactory.getResearchGroupDao();
    private ScenarioDao scenarioDao = DaoFactory.getScenarioDao();
    private WeatherDao weatherDao = DaoFactory.getWeatherDao();
    private PersonDao personDao = DaoFactory.getPersonDao();

    private final Object lock = new Object();

    private final static Logger log = Logger.getLogger(DataSyncer.class);

    public DataSyncer(EDEDClient session, EDEDBController controller) {
        this.session = session;
        this.controller = controller;

        SyncThread syncThread = new SyncThread();
        syncThread.start();
    }

    public void syncNow() {
        synchronized (lock) {
            lock.notify();
        }
    }

    @SuppressWarnings("unchecked")
    private class SyncThread extends Thread {

        long SLEEP_INTERVAL = 60000;

        @Override
        public void run() {
            setName("DataSyncThread");


            List<ResearchGroupInfo> groupsInfo;
            List<PersonInfo> peopleInfo;
            List<ScenarioInfo> scenariosInfo;
            List<ExperimentInfo> experimentsInfo;
            List<DataFileInfo> filesInfo;
            List<WeatherInfo> weathersInfo;
            List<HardwareInfo> hardwareInfo;

            List<ResearchGroup> groups = new ArrayList<ResearchGroup>();
            List<Person> people = new ArrayList<Person>();
            List<Scenario> scenarios = new ArrayList<Scenario>();
            List<Experiment> experiments = new ArrayList<Experiment>();
            List<DataFile> dataFiles = new ArrayList<DataFile>();
            List<Weather> weathers = new ArrayList<Weather>();
            List<Hardware> hardwares = new ArrayList<Hardware>();

            Set<Experiment> exps;
            Set<Hardware> hws;

            Person tmpPerson = null;
            List<Person> tmpPeople;
            Iterator<Person> personIterator;

            boolean found[] = new boolean[4];
            boolean changed = false;

            do {

                synchronized (lock) {
                    try {
                        lock.wait(SLEEP_INTERVAL);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                }

                if (!session.isConnected()) {
                    continue;
                }

                try {
                    /*First we obtain all needed information from server in collections.*/
                    Working.setActivity(true, "working.ededb.dbsync");
                    groupsInfo = session.getService().getResearchGroups(researchGroupDao.getLastRevision());
                    weathersInfo = session.getService().getWeather(weatherDao.getLastRevision());
                    peopleInfo = session.getService().getPeople(personDao.getLastRevision());
                    scenariosInfo = session.getService().getScenarios(scenarioDao.getLastRevision());
                    experimentsInfo = session.getService().getExperiments(experimentDao.getLastRevision());
                    filesInfo = session.getService().getDataFiles(dataFileDao.getLastRevision());
                    hardwareInfo = session.getService().getHardware(hardwareDao.getLastRevision());
                    Working.setActivity(false, "working.ededb.dbsync");

                    groups.clear();
                    people.clear();
                    scenarios.clear();
                    experiments.clear();
                    dataFiles.clear();
                    weathers.clear();
                    hardwares.clear();

                    log.debug("DB update");
                    log.debug(groupsInfo.size() + " new research groups");
                    log.debug(peopleInfo.size() + " new people");
                    log.debug(scenariosInfo.size() + " new scenarios");
                    log.debug(experimentsInfo.size() + " new experiments");
                    log.debug(filesInfo.size() + " new data files");
                    log.debug(weathersInfo.size() + " new weather types");
                    log.debug(hardwareInfo.size() + " new hardwares");

                    changed = (!groupsInfo.isEmpty()
                            && !peopleInfo.isEmpty()
                            && !scenariosInfo.isEmpty()
                            && !experimentsInfo.isEmpty()
                            && !filesInfo.isEmpty()
                            && !weathersInfo.isEmpty());

                    /* Registered people sync */
                    for (PersonInfo personInfo : peopleInfo) {
                        Person person = new Person();
                        person.setPersonId(personInfo.getPersonId());
                        person.setGender((char) personInfo.getGender());
                        person.setName(personInfo.getGivenName());
                        person.setSurname(personInfo.getSurname());
                        person.setVersion(personInfo.getScn());
                        people.add(person);
                    }

                    commitCollection(people);

                    /* Research groups sync */
                    for (ResearchGroupInfo groupInfo : groupsInfo) {
                        ResearchGroup group = new ResearchGroup();
                        group.setResearchGroupId(groupInfo.getResearchGroupId());
                        group.setVersion(groupInfo.getScn());
                        group.setTitle(groupInfo.getTitle());
                        group.setDescription(groupInfo.getDescription());

                        if (people.isEmpty()) {
                            Session hibSession = HibernateUtil.getSessionFactory().getCurrentSession();
                            Transaction transaction = hibSession.beginTransaction();
                            people = hibSession.createCriteria(Person.class).list();
                            transaction.commit();
                        }

                        for (Person person : people) {
                            if (person.getPersonId() == groupInfo.getOwnerId()) {
                                group.setOwner(person);
                                break;
                            }
                        }
                        groups.add(group);
                    }

                    commitCollection(groups);

                    /*
                     Now we have people and groups. People's default group must be set.
                     */
                    tmpPeople = new ArrayList<Person>();
                    for (PersonInfo personInfo : peopleInfo) {
                        personIterator = people.iterator();
                        while (personIterator.hasNext()) {
                            if ((tmpPerson = personIterator.next()).getPersonId() == personInfo.getPersonId()) {
                                break;
                            }
                        }

                        if (tmpPerson != null)
                            for (ResearchGroup group : groups) {
                                if (group.getResearchGroupId() == personInfo.getDefaultGroupId()) {
                                    tmpPerson.setDefaultGroup(group);
                                    tmpPeople.add(tmpPerson);
                                    break;
                                }
                            }
                    }

                    commitCollection(tmpPeople);

                    /*
                    Scenarios sync.
                     */
                    for (ScenarioInfo scenarioInfo : scenariosInfo) {
                        Scenario scenario = new Scenario();
                        scenario.setScenarioId(scenarioInfo.getScenarioId());
                        scenario.setScenarioName(scenarioInfo.getScenarioName());
                        scenario.setScenarioLength((short) scenarioInfo.getScenarioLength());
                        scenario.setVersion(scenarioInfo.getScn());
                        scenario.setTitle(scenarioInfo.getTitle());
                        scenario.setDescription(scenarioInfo.getDescription());
                        scenario.setMimetype(scenarioInfo.getMimeType());

                        if (people.isEmpty()) {
                            Session hibSession = HibernateUtil.getSessionFactory().getCurrentSession();
                            Transaction transaction = hibSession.beginTransaction();
                            people = hibSession.createCriteria(Person.class).list();
                            transaction.commit();
                        }

                        /*
                        Setting owner.
                         */
                        for (Person person : people) {
                            if (person.getPersonId() == scenarioInfo.getOwnerId()) {
                                scenario.setOwner(person);
                                break;
                            }
                        }

                        if (groups.isEmpty()) {
                            Session hibSession = HibernateUtil.getSessionFactory().getCurrentSession();
                            Transaction transaction = hibSession.beginTransaction();
                            groups = hibSession.createCriteria(ResearchGroup.class).list();
                            transaction.commit();
                        }

                        /*
                        Setting proper research group.
                         */
                        for (ResearchGroup group : groups) {
                            if (group.getResearchGroupId() == scenarioInfo.getResearchGroupId()) {
                                scenario.setResearchGroup(group);
                                break;
                            }
                        }

                        scenarios.add(scenario);
                    }

                    commitCollection(scenarios);

                    /*
                    Weathers sync.
                     */
                    for (WeatherInfo weatherInfo : weathersInfo) {
                        Weather weather = new Weather();
                        weather.setWeatherId(weatherInfo.getWeatherId());
                        weather.setDescription(weatherInfo.getDescription());
                        weather.setVersion(weatherInfo.getScn());
                        weather.setTitle(weatherInfo.getTitle());
                        weathers.add(weather);
                    }

                    commitCollection(weathers);

                    /*
                    Experiments sync.
                     */
                    for (ExperimentInfo expInfo : experimentsInfo) {
                        Experiment exp = new Experiment();
                        exp.setExperimentId(expInfo.getExperimentId());
                        exp.setEndTime(new java.sql.Date(expInfo.getEndTimeInMillis()));
                        exp.setStartTime(new java.sql.Date(expInfo.getStartTimeInMillis()));
                        exp.setVersion(expInfo.getScn());
                        exp.setTemperature((short) expInfo.getTemperature());
                        exp.setWeathernote(expInfo.getWeatherNote());

                        if (scenarios.isEmpty()) {
                            Session hibSession = HibernateUtil.getSessionFactory().getCurrentSession();
                            Transaction transaction = hibSession.beginTransaction();
                            scenarios = hibSession.createCriteria(Scenario.class).list();
                            transaction.commit();
                        }

                        /*
                        Every experiment must belong to some scenario.
                         */
                        for (Scenario scn : scenarios) {
                            if (scn.getScenarioId() == expInfo.getScenarioId()) {
                                exp.setScenario(scn);
                                break;
                            }
                        }

                        if (weathers.isEmpty()) {
                            Session hibSession = HibernateUtil.getSessionFactory().getCurrentSession();
                            Transaction transaction = hibSession.beginTransaction();
                            weathers = hibSession.createCriteria(Weather.class).list();
                            transaction.commit();
                        }

                        /*
                       Weather conditions are usually set too.
                        */
                        for (Weather weather : weathers) {
                            if (weather.getWeatherId() == expInfo.getWeatherId()) {
                                exp.setWeather(weather);
                                break;
                            }
                        }

                        if (people.isEmpty()) {
                            Session hibSession = HibernateUtil.getSessionFactory().getCurrentSession();
                            Transaction transaction = hibSession.beginTransaction();
                            people = hibSession.createCriteria(Person.class).list();
                            transaction.commit();
                        }

                        /*
                        Experiment has owner and subject of measurement.
                        If both are found, search loop breaks.
                         */
                        found[0] = found[1] = false;
                        for (Person person : people) {
                            if ((found[0] && found[1])) {
                                break;
                            }

                            if (expInfo.getSubjectPersonId() == person.getPersonId()) {
                                exp.setSubject(person);
                                found[0] = true;
                            }

                            if (expInfo.getOwnerId() == person.getPersonId()) {
                                exp.setOwner(person);
                                found[1] = true;
                            }
                        }

                        if (groups.isEmpty()) {
                            Session hibSession = HibernateUtil.getSessionFactory().getCurrentSession();
                            Transaction transaction = hibSession.beginTransaction();
                            groups = hibSession.createCriteria(ResearchGroup.class).list();
                            transaction.commit();
                        }

                        /*
                        Setting proper research group.
                         */
                        for (ResearchGroup group : groups) {
                            if (expInfo.getResearchGroupId() == group.getResearchGroupId()) {
                                exp.setResearchGroup(group);
                                break;
                            }
                        }

                        experiments.add(exp);
                    }

                    commitCollection(experiments);

                    /*
                    Data files sync.
                     */
                    for (DataFileInfo fileInfo : filesInfo) {
                        DataFile file = new DataFile();
                        file.setDataFileId(fileInfo.getFileId());
                        file.setFileLength(fileInfo.getFileLength());
                        file.setMimetype(fileInfo.getMimeType());
                        file.setSamplingRate(fileInfo.getSamplingRate());
                        file.setVersion(fileInfo.getScn());
                        file.setFilename(fileInfo.getFileName());

                        if (experiments.isEmpty()) {
                            Session hibSession = HibernateUtil.getSessionFactory().getCurrentSession();
                            Transaction transaction = hibSession.beginTransaction();
                            experiments = hibSession.createCriteria(Experiment.class).list();
                            transaction.commit();
                        }

                        /*
                        Every file belongs to an experiment.
                         */
                        for (Experiment exp : experiments) {
                            if (exp.getExperimentId() == fileInfo.getExperimentId()) {
                                file.setExperiment(exp);
                                break;
                            }
                        }

                        //if experiment was private, its reference will be null
                        if (file.getExperiment() != null) {
                            dataFiles.add(file);
                        }
                    }

                    commitCollection(dataFiles);


                    /*
                     * HW sync
                     */

                    for (HardwareInfo hw : hardwareInfo) {
                        Hardware hardware = new Hardware();
                        hardware.setTitle(hw.getTitle());
                        hardware.setType(hw.getType());
                        hardware.setDescription(hw.getDescription());
                        hardware.setHardwareId(hw.getHardwareId());
                        hardware.setVersion(hw.getScn());

                        hardwares.add(hardware);
                    }

                    commitCollection(hardwares);

                    /**
                     * Every time there is a new experiment, HW relations must be updated
                     */

                    experiments.clear();
                    for(ExperimentInfo expInfo : experimentsInfo){
                        Experiment exp = experimentDao.get(expInfo.getExperimentId());

                        hws = new HashSet<Hardware>();
                        for (Integer hwId : expInfo.getHwIds()) {
                            Hardware hw = hardwareDao.get(hwId);
                            if (hw != null)
                                hws.add(hw);
                        }

                        exp.setHardwares(hws);
                        experiments.add(exp);
                    }

                    commitCollection(experiments);

                } catch (DataDownloadException_Exception e) {
                    log.error(e.getMessage(), e);
                }
                if (changed)
                    controller.update();
            } while (!Thread.interrupted());

        }

        /**
         * Method for saving or updating data in database using collection input.
         *
         * @param collection objects created in accordance to sync data from server
         */
        private void commitCollection(Collection<?> collection) {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.getCurrentSession();
            Transaction transaction = session.beginTransaction();

            try {

                for (Object ob : collection) {
                    session.saveOrUpdate(ob);
                    session.flush();
                }
                transaction.commit();
            } catch (HibernateException e) {
                log.error(e.getMessage(), e);
                transaction.rollback();
            }
        }
    }
}
