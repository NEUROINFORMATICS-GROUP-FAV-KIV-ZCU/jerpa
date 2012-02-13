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

    private long SLEEP_INTERVAL = 60000;

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

    private class SyncThread extends Thread {

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

                log.debug("DB syncing begins");

                try {
                    /*First we obtain all needed information from server in collections.*/
                    Working.setActivity(true, "working.ededb.dbsync");

                    log.debug("Obtaining data from server");
                    groupsInfo = session.getService().getResearchGroups(researchGroupDao.getLastRevision());
                    weathersInfo = session.getService().getWeather(weatherDao.getLastRevision());
                    peopleInfo = session.getService().getPeople(personDao.getLastRevision());
                    scenariosInfo = session.getService().getScenarios(scenarioDao.getLastRevision());
                    experimentsInfo = session.getService().getExperiments(experimentDao.getLastRevision());
                    filesInfo = session.getService().getDataFiles(dataFileDao.getLastRevision());
                    hardwareInfo = session.getService().getHardware(hardwareDao.getLastRevision());
                    log.debug("Data obtained - proceeding to update");

                    changed = (!groupsInfo.isEmpty()
                            && !peopleInfo.isEmpty()
                            && !scenariosInfo.isEmpty()
                            && !experimentsInfo.isEmpty()
                            && !filesInfo.isEmpty()
                            && !weathersInfo.isEmpty());

                    if (!peopleInfo.isEmpty())
                        importNewPeople(peopleInfo);
                    if (!groupsInfo.isEmpty())
                        importNewResearchGroups(groupsInfo);
                    if (!peopleInfo.isEmpty())
                        updatePeopleGroupsRelations(peopleInfo);
                    if (!scenariosInfo.isEmpty())
                        importNewScenarios(scenariosInfo);
                    if (!weathersInfo.isEmpty())
                        importNewWeather(weathersInfo);
                    if (!experimentsInfo.isEmpty())
                        importNewExperiments(experimentsInfo);
                    if (!filesInfo.isEmpty())
                        importNewDataFiles(filesInfo);
                    if (!hardwareInfo.isEmpty())
                        importNewHardware(hardwareInfo);
                    if (!experimentsInfo.isEmpty())
                        updateExperimentHwRelations(experimentsInfo);

                } catch (DataDownloadException_Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    Working.setActivity(false, "working.ededb.dbsync");
                }
                if (changed)
                    controller.update();
                log.debug("DB syncing done");
            } while (!Thread.interrupted());

        }
    }

    /**
     * This method imports new people, without any references.
     *
     * @param peopleInfo people info from server
     */
    private void importNewPeople(List<PersonInfo> peopleInfo) {
        log.debug(peopleInfo.size() + " new people");

        List<Person> people = new ArrayList<Person>();
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
    }

    /**
     * Imports new Research groups and sets its owner.
     * Requires people imported first!
     *
     * @param groupsInfo research groups info from server
     */
    private void importNewResearchGroups(List<ResearchGroupInfo> groupsInfo) {
        log.debug(groupsInfo.size() + " new research groups");

        List<ResearchGroup> groups = new ArrayList<ResearchGroup>();

        for (ResearchGroupInfo groupInfo : groupsInfo) {
            ResearchGroup group = new ResearchGroup();
            group.setResearchGroupId(groupInfo.getResearchGroupId());
            group.setVersion(groupInfo.getScn());
            group.setTitle(groupInfo.getTitle());
            group.setDescription(groupInfo.getDescription());

            Person owner = personDao.get(groupInfo.getOwnerId());
            if (owner != null) {
                group.setOwner(owner);
            }
            groups.add(group);
        }

        commitCollection(groups);
    }

    /**
     * Method for wiring references between people and research groups.
     * Requires people and research groups imported first!
     *
     * @param peopleInfo information about people from server
     */
    private void updatePeopleGroupsRelations(List<PersonInfo> peopleInfo) {
        log.debug("Linking people and research groups");

        List<Person> people = new ArrayList<Person>();

        for (PersonInfo personInfo : peopleInfo) {
            Person person = personDao.get(personInfo.getPersonId());
            ResearchGroup group = researchGroupDao.get(personInfo.getDefaultGroupId());

            if (person != null && group != null) {
                person.setDefaultGroup(group);
                people.add(person);
            }
        }

        commitCollection(people);
    }

    /**
     * Method for importing new scenarios and sets its owner and research group.
     * Requires people and research groups imported first!
     *
     * @param scenariosInfo scenarios info from server
     */
    private void importNewScenarios(List<ScenarioInfo> scenariosInfo) {
        log.debug(scenariosInfo.size() + " new scenarios");

        List<Scenario> scenarios = new ArrayList<Scenario>();

        for (ScenarioInfo scenarioInfo : scenariosInfo) {
            Scenario scenario = new Scenario();
            scenario.setScenarioId(scenarioInfo.getScenarioId());
            scenario.setScenarioName(scenarioInfo.getScenarioName());
            scenario.setScenarioLength((short) scenarioInfo.getScenarioLength());
            scenario.setVersion(scenarioInfo.getScn());
            scenario.setTitle(scenarioInfo.getTitle());
            scenario.setDescription(scenarioInfo.getDescription());
            scenario.setMimetype(scenarioInfo.getMimeType());

            Person owner = personDao.get(scenarioInfo.getOwnerId());
            if (owner != null) {
                scenario.setOwner(owner);
            }

            ResearchGroup group = researchGroupDao.get(scenarioInfo.getResearchGroupId());
            if (group != null) {
                scenario.setResearchGroup(group);
            }

            scenarios.add(scenario);
        }

        commitCollection(scenarios);
    }

    /**
     * Weather import method.
     * Does not require any other data from server.
     *
     * @param weathersInfo weather info from server
     */
    private void importNewWeather(List<WeatherInfo> weathersInfo) {
        log.debug(weathersInfo.size() + " new weather types");

        List<Weather> weathers = new ArrayList<Weather>();

        for (WeatherInfo weatherInfo : weathersInfo) {
            Weather weather = new Weather();
            weather.setWeatherId(weatherInfo.getWeatherId());
            weather.setDescription(weatherInfo.getDescription());
            weather.setVersion(weatherInfo.getScn());
            weather.setTitle(weatherInfo.getTitle());
            weathers.add(weather);
        }

        commitCollection(weathers);
    }

    /**
     * Method for importing new experiments.
     * Requires people, research groups, scenarios and weather data imported first!
     *
     * @param experimentsInfo experiment information from server
     */
    private void importNewExperiments(List<ExperimentInfo> experimentsInfo) {
        log.debug(experimentsInfo.size() + " new experiments");

        List<Experiment> experiments = new ArrayList<Experiment>();

        for (ExperimentInfo expInfo : experimentsInfo) {
            Experiment exp = new Experiment();
            exp.setExperimentId(expInfo.getExperimentId());
            exp.setEndTime(new java.sql.Date(expInfo.getEndTimeInMillis()));
            exp.setStartTime(new java.sql.Date(expInfo.getStartTimeInMillis()));
            exp.setVersion(expInfo.getScn());
            exp.setTemperature((short) expInfo.getTemperature());
            exp.setWeathernote(expInfo.getWeatherNote());

            Scenario scn = scenarioDao.get(expInfo.getScenarioId());
            if (scn != null) {
                exp.setScenario(scn);
            }

            Weather weather = weatherDao.get(expInfo.getWeatherId());
            if (weather != null) {
                exp.setWeather(weather);
            }

            Person owner = personDao.get(expInfo.getOwnerId());
            if (owner != null) {
                exp.setOwner(owner);
            }

            Person subject = personDao.get(expInfo.getSubjectPersonId());
            if (subject != null) {
                exp.setSubject(subject);
            }

            ResearchGroup group = researchGroupDao.get(expInfo.getResearchGroupId());
            if (group != null) {
                exp.setResearchGroup(group);
            }

            experiments.add(exp);
        }

        commitCollection(experiments);
    }

    /**
     * Method for importing new data files from server.
     * Requires experiments data imported first!
     *
     * @param filesInfo data files information from server
     */
    private void importNewDataFiles(List<DataFileInfo> filesInfo) {
        log.debug(filesInfo.size() + " new data files");

        List<DataFile> dataFiles = new ArrayList<DataFile>();

        for (DataFileInfo fileInfo : filesInfo) {
            DataFile file = new DataFile();
            file.setDataFileId(fileInfo.getFileId());
            file.setFileLength(fileInfo.getFileLength());
            file.setMimetype(fileInfo.getMimeType());
            file.setSamplingRate(fileInfo.getSamplingRate());
            file.setVersion(fileInfo.getScn());
            file.setFilename(fileInfo.getFileName());

            Experiment exp = experimentDao.get(fileInfo.getExperimentId());
            if (exp != null) {
                file.setExperiment(exp);
                dataFiles.add(file);
            }
        }
        commitCollection(dataFiles);
    }

    /**
     * Method for importing new hardware types from server.
     * Does not require any other previous data.
     *
     * @param hardwareInfo hardware types information from server
     */
    private void importNewHardware(List<HardwareInfo> hardwareInfo) {
        log.debug(hardwareInfo.size() + " new hardware types");

        List<Hardware> hardwareTypes = new ArrayList<Hardware>();

        for (HardwareInfo hw : hardwareInfo) {
            Hardware hardware = new Hardware();
            hardware.setTitle(hw.getTitle());
            hardware.setType(hw.getType());
            hardware.setDescription(hw.getDescription());
            hardware.setHardwareId(hw.getHardwareId());
            hardware.setVersion(hw.getScn());

            hardwareTypes.add(hardware);
        }
        commitCollection(hardwareTypes);
    }

    /**
     * Method for wiring references between experiments and hardware.
     * Requires experiments and hardware types data imported first!
     *
     * @param experimentsInfo experiments information from server
     */
    private void updateExperimentHwRelations(List<ExperimentInfo> experimentsInfo) {
        log.debug("Linking experiments and hardware");

        List<Experiment> experiments = new ArrayList<Experiment>();
        for (ExperimentInfo expInfo : experimentsInfo) {
            Experiment exp = experimentDao.get(expInfo.getExperimentId());
            Set<Hardware> hws;
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
