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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

    private final static Logger log = Logger.getLogger(DataSyncer.class);

    public DataSyncer(EDEDClient session, EDEDBController controller) {
        this.session = session;
        this.controller = controller;

        SyncThread syncThread = new SyncThread();

        syncThread.start();
    }

    @SuppressWarnings("unchecked")
    private class SyncThread extends Thread {

        long SLEEP_INTERVAL = 10000;
        private final Object lock = new Object();

        @Override
        public void run() {
            setName("DataSyncThread");


            List<ResearchGroupInfo> groupsInfo;
            List<PersonInfo> peopleInfo;
            List<ScenarioInfo> scenariosInfo;
            List<ExperimentInfo> experimentsInfo;
            List<DataFileInfo> filesInfo;
            List<WeatherInfo> weathersInfo;

            List<ResearchGroup> groups;
            List<Person> people;
            List<Scenario> scenarios;
            List<Experiment> experiments;
            List<DataFile> dataFiles;
            List<Weather> weathers;

            Person tmpPerson = null;
            List<Person> tmpPeople;
            Iterator<Person> personIterator;

            boolean found[] = new boolean[4];

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

                    Working.setActivity(true, "Database update");

                    groupsInfo = session.getService().getResearchGroups(researchGroupDao.getLastRevision());
                    weathersInfo = session.getService().getWeather(weatherDao.getLastRevision());
                    peopleInfo = session.getService().getPeople(personDao.getLastRevision());
                    scenariosInfo = session.getService().getScenarios(scenarioDao.getLastRevision());
                    experimentsInfo = session.getService().getExperiments(experimentDao.getLastRevision());
                    filesInfo = session.getService().getDataFiles(dataFileDao.getLastRevision());
                    Working.setActivity(false, "Database update");


                    log.debug("DB update");
                    log.debug(groupsInfo.size() + " new research groups");
                    log.debug(peopleInfo.size() + " new people");
                    log.debug(scenariosInfo.size() + " new scenarios");
                    log.debug(experimentsInfo.size() + " new experiments");
                    log.debug(filesInfo.size() + " new data files");
                    log.debug(weathersInfo.size() + " new weather types");

                    groups = new ArrayList<ResearchGroup>();
                    people = new ArrayList<Person>();
                    scenarios = new ArrayList<Scenario>();
                    experiments = new ArrayList<Experiment>();
                    dataFiles = new ArrayList<DataFile>();
                    weathers = new ArrayList<Weather>();

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

                        for (ResearchGroup group : groups) {
                            if (group.getResearchGroupId() == scenarioInfo.getResearchGroupId()) {
                                scenario.setResearchGroup(group);
                                break;
                            }
                        }

                        scenarios.add(scenario);
                    }

                    commitCollection(scenarios);

                    for (WeatherInfo weatherInfo : weathersInfo) {
                        Weather weather = new Weather();
                        weather.setWeatherId(weatherInfo.getWeatherId());
                        weather.setDescription(weatherInfo.getDescription());
                        weather.setVersion(weatherInfo.getScn());
                        weather.setTitle(weatherInfo.getTitle());
                        weathers.add(weather);
                    }

                    commitCollection(weathers);

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

                        for (ResearchGroup group : groups) {
                            if (expInfo.getResearchGroupId() == group.getResearchGroupId()) {
                                exp.setResearchGroup(group);
                                break;
                            }
                        }

                        experiments.add(exp);
                    }

                    commitCollection(experiments);

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

                } catch (DataDownloadException_Exception e) {
                    log.error(e.getMessage(), e);
                }

                controller.update();
            } while (!Thread.interrupted());

        }

        private void commitCollection(Collection<?> collection) {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.getCurrentSession();
            Transaction transaction = session.beginTransaction();

            for (Object ob : collection) {
                session.saveOrUpdate(ob);
            }
            try {
                transaction.commit();
            } catch (HibernateException e) {
                log.error(e);
                transaction.rollback();
            }
        }
    }
}
