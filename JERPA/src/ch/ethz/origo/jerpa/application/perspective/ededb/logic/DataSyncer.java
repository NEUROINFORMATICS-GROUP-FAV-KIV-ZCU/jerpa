package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.dao.*;
import ch.ethz.origo.jerpa.data.tier.pojo.*;
import ch.ethz.origo.jerpa.ededclient.generated.*;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import org.apache.log4j.Logger;
import org.hibernate.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.*;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Petr Miko
 *         Class for basic synchronization of information about contents of EEG base database.
 *         <p/>
 *         Still under development, so far only downloads information.
 */
public class DataSyncer {

    private final EDEDClient service;
    private final EDEDBController controller;

    private long sleepInterval = 60000;

    public static boolean dataUpdated = false;
    public static boolean experimentsUpdated = false;

    private DataFileDao dataFileDao = DataFileDao.getInstance();
    private ExperimentDao experimentDao = ExperimentDao.getInstance();
    private HardwareDao hardwareDao = HardwareDao.getInstance();
    private ResearchGroupDao researchGroupDao = ResearchGroupDao.getInstance();
    private ScenarioDao scenarioDao = ScenarioDao.getInstance();
    private WeatherDao weatherDao = WeatherDao.getInstance();
    private PersonDao personDao = PersonDao.getInstance();

    private final static Logger log = Logger.getLogger(DataSyncer.class);

    public DataSyncer(EDEDClient service, EDEDBController controller) {
        this.service = service;
        this.controller = controller;

        String tmpSleep = ConfigPropertiesLoader.getProperty("ededb.properties","ededb.sync.miliseconds");
        if (tmpSleep != null)
            sleepInterval = Long.parseLong(tmpSleep);

        SyncThread syncThread = new SyncThread();
        syncThread.start();
    }

    public void syncNow() {
        synchronized (DataSyncer.class) {
            DataSyncer.class.notify();
        }
    }

    private class SyncThread extends Thread {

        @Override
        public void run() {
            setName("DataSyncThread");
            boolean changed;
            do {

                try {
                    synchronized (DataSyncer.class) {
                        try {
                            DataSyncer.class.wait(sleepInterval);
                        } catch (InterruptedException e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                    if (!service.isConnected()) {
                        continue;
                    }

                    log.debug("DB syncing begins");

                    Working.setActivity(true, "working.ededb.dbsync");
                    //upload section of syncing
                    upload();
                    //download section of syncing
                    changed = download();

                    Working.setActivity(false, "working.ededb.dbsync");
                    if (changed)
                        controller.update();
                    log.debug("DB syncing done");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            } while (!Thread.interrupted());

        }

    }

    /**
     * Upload synchronization method.
     */
    @SuppressWarnings("unchecked")
    private void upload() {
        log.debug("Upload - upload process started");
        try {
            uploadExperiments(experimentDao.getChanged());
            uploadExperiments(experimentDao.getAdded());
            uploadDataFiles(dataFileDao.getChanged());
            uploadDataFiles(dataFileDao.getAdded());
            controller.update();
        } catch (DaoException e) {
            log.error(e.getMessage(), e);
        }
        log.debug("Upload - upload process finished");
    }

    /**
     * Method for uploading new and updated experiments information.
     *
     * @param experiments experiment information
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void uploadExperiments(List<Experiment> experiments) throws DaoException {
        UserDataService uService = service.getService();
        Date startTime, endTime;
        Person owner, subject;
        ResearchGroup group;
        Scenario scenario;
        Weather weather;

        for (Experiment exp : experiments) {
            Session session = HibernateUtil.getActiveSession();
            ExperimentInfo info = new ExperimentInfo();
            startTime = exp.getStartTime();
            endTime = exp.getEndTime();
            owner = (Person) HibernateUtil.reattachObject(session, exp.getOwner());
            subject = (Person) HibernateUtil.reattachObject(session, exp.getSubject());
            group = (ResearchGroup) HibernateUtil.reattachObject(session, exp.getResearchGroup());
            scenario = (Scenario) HibernateUtil.reattachObject(session, exp.getScenario());
            weather = (Weather) HibernateUtil.reattachObject(session, exp.getWeather());

            info.setTemperature(exp.getTemperature());
            if (endTime != null)
                info.setEndTimeInMillis(endTime.getTime());
            if (startTime != null)
                info.setStartTimeInMillis(startTime.getTime());
            info.setAdded(exp.getAdded());
            info.setChanged(exp.getChanged());
            info.setExperimentId(exp.getExperimentId());
            if (owner != null)
                info.setOwnerId(owner.getPersonId());
            if (subject != null)
                info.setSubjectPersonId(subject.getPersonId());
            if (group != null)
                info.setResearchGroupId(group.getResearchGroupId());
            if (scenario != null)
                info.setScenarioId(scenario.getScenarioId());
            if (weather != null)
                info.setWeatherId(weather.getWeatherId());
            info.setWeatherNote(exp.getWeathernote());
            info.setTemperature(exp.getTemperature());

            DigitizationInfo digitizationInfo = new DigitizationInfo();
            digitizationInfo.setSamplingRate(exp.getSamplingRate());
            digitizationInfo.setFilter(exp.getFilter());
            digitizationInfo.setGain(exp.getGain());
            info.setDigitizationInfo(digitizationInfo);

//            not used yet
//            info.setSubjectGroupId();

            HibernateUtil.reattachObject(session, exp);
            for (Hardware hardware : exp.getHardwares()) {
                info.getHwIds().add(hardware.getHardwareId());
            }

            exp.setExperimentId(uService.addOrUpdateExperiment(info));
            Transaction transaction = session.getTransaction();

            try {
                exp.setAdded(false);
                exp.setChanged(false);

                session.update(exp);
                transaction.commit();
            } catch (HibernateException e) {
                log.error(e.getMessage(), e);
                transaction.rollback();
            } finally {
                session.close();
                experimentsUpdated = true;
            }
        }
    }

    private void uploadDataFiles(List<DataFile> files) throws DaoException {
        Experiment exp;
        try {

            for (DataFile file : files) {
                DataFileInfo info = new DataFileInfo();
                Session session = HibernateUtil.getActiveSession();

                HibernateUtil.reattachObject(session, file);
                exp = (Experiment) HibernateUtil.reattachObject(session, file.getExperiment());
                if (exp != null)
                    info.setExperimentId(exp.getExperimentId());

                info.setFileId(file.getDataFileId());
                info.setFileName(file.getFilename());
                info.setFileLength(file.getFileLength());
                info.setMimeType(file.getMimetype());
                info.setAdded(file.getAdded());
                info.setChanged(file.getChanged());
//                info.setDescription(file.getDescription());

                final InputStream in = file.getFileContent().getBinaryStream();
                DataSource rawData = new DataSource() {
                    public String getContentType() {
                        return "application/octet-stream";
                    }

                    public java.io.InputStream getInputStream() throws IOException {
                        return in;
                    }

                    public String getName() {
                        return "application/octet-stream";
                    }

                    public OutputStream getOutputStream() throws IOException {
                        return null;
                    }
                };
                DataHandler handler = new DataHandler(rawData);
                file.setDataFileId(service.getService().addOrUpdateDataFile(info, handler));

                Transaction transaction = session.getTransaction();

                try {
                    file.setAdded(false);
                    file.setChanged(false);

                    session.update(file);
                    transaction.commit();
                } catch (HibernateException e) {
                    transaction.rollback();
                    log.error(e.getMessage(), e);
                } finally {
                    session.close();
                    dataUpdated = true;
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (UserDataServiceException_Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Download synchronization method.
     *
     * @return data in DB had changed
     */
    private boolean download() {
        log.debug("Download - Obtaining data from server");

        List<ResearchGroupInfo> groupsInfo = null;
        List<WeatherInfo> weathersInfo = null;
        List<PersonInfo> peopleInfo = null;
        List<ScenarioInfo> scenariosInfo = null;
        List<ExperimentInfo> experimentsInfo = null;
        List<DataFileInfo> filesInfo = null;
        List<HardwareInfo> hardwareInfo = null;

        try {
            groupsInfo = service.getService().getResearchGroups(researchGroupDao.getLastRevision());
            weathersInfo = service.getService().getWeather(weatherDao.getLastRevision());
            peopleInfo = service.getService().getPeople(personDao.getLastRevision());
            scenariosInfo = service.getService().getScenarios(scenarioDao.getLastRevision());
            experimentsInfo = service.getService().getExperiments(experimentDao.getLastRevision());
            filesInfo = service.getService().getDataFiles(dataFileDao.getLastRevision());
            hardwareInfo = service.getService().getHardware(hardwareDao.getLastRevision());
            log.debug("Download - Data obtained: proceeding to update");

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

            log.debug("Download - DB updated");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return !isListEmpty(groupsInfo)
                && !isListEmpty(peopleInfo)
                && !isListEmpty(scenariosInfo)
                && !isListEmpty(experimentsInfo)
                && !isListEmpty(filesInfo)
                && !isListEmpty(weathersInfo)
                && !isListEmpty(hardwareInfo);
    }

    /**
     * Method for simple checking whether the list is empty or not.
     *
     * @param list java.util.List implementation
     * @return empty state
     */
    private boolean isListEmpty(List list) {
        return !(list != null && !list.isEmpty());
    }

    /**
     * This method imports new people, without any references.
     *
     * @param peopleInfo people info from server
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void importNewPeople(List<PersonInfo> peopleInfo) throws DaoException {
        log.debug(peopleInfo.size() + " new people");
        for (PersonInfo personInfo : peopleInfo) {
            Person person = personDao.get(personInfo.getPersonId());
            if (person == null) {
                person = new Person();
                person.setPersonId(personInfo.getPersonId());
            }

            person.setGender((char) personInfo.getGender());
            person.setName(personInfo.getGivenName());
            person.setSurname(personInfo.getSurname());
            person.setVersion(personInfo.getScn());
            saveOrUpdate(HibernateUtil.getActiveSession(), person);
        }
    }

    /**
     * Imports new Research groups and sets its owner.
     * Requires people imported first!
     *
     * @param groupsInfo research groups info from server
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void importNewResearchGroups(List<ResearchGroupInfo> groupsInfo) throws DaoException {
        log.debug(groupsInfo.size() + " new research groups");
        for (ResearchGroupInfo groupInfo : groupsInfo) {
            ResearchGroup group = researchGroupDao.get(groupInfo.getResearchGroupId());
            if (group == null) {
                group = new ResearchGroup();
                group.setResearchGroupId(groupInfo.getResearchGroupId());
            }
            group.setVersion(groupInfo.getScn());
            group.setTitle(groupInfo.getTitle());
            group.setDescription(groupInfo.getDescription());

            Person owner = personDao.get(groupInfo.getOwnerId());
            if (owner != null) {
                group.setOwner(owner);
            }
            saveOrUpdate(HibernateUtil.getActiveSession(), group);
        }
    }

    /**
     * Method for wiring references between people and research groups.
     * Requires people and research groups imported first!
     *
     * @param peopleInfo information about people from server
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void updatePeopleGroupsRelations(List<PersonInfo> peopleInfo) throws DaoException {
        log.debug("Linking people and research groups");

        for (PersonInfo personInfo : peopleInfo) {
            Person person = personDao.get(personInfo.getPersonId());
            ResearchGroup group = researchGroupDao.get(personInfo.getDefaultGroupId());

            if (person != null && group != null) {
                person.setDefaultGroup(group);
                saveOrUpdate(HibernateUtil.getActiveSession(), person);
            }
        }
    }

    /**
     * Method for importing new scenarios and sets its owner and research group.
     * Requires people and research groups imported first!
     *
     * @param scenariosInfo scenarios info from server
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void importNewScenarios(List<ScenarioInfo> scenariosInfo) throws DaoException {
        log.debug(scenariosInfo.size() + " new scenarios");

        for (ScenarioInfo scenarioInfo : scenariosInfo) {
            Scenario scenario = scenarioDao.get(scenarioInfo.getScenarioId());
            if (scenario == null) {
                scenario = new Scenario();
                scenario.setScenarioId(scenarioInfo.getScenarioId());
            }

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

            saveOrUpdate(HibernateUtil.getActiveSession(), scenario);
        }

    }

    /**
     * Weather import method.
     * Does not require any other data from server.
     *
     * @param weathersInfo weather info from server
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void importNewWeather(List<WeatherInfo> weathersInfo) throws DaoException {
        log.debug(weathersInfo.size() + " new weather types");


        for (WeatherInfo weatherInfo : weathersInfo) {
            Weather weather = weatherDao.get(weatherInfo.getWeatherId());
            if (weather == null) {
                weather = new Weather();
                weather.setWeatherId(weatherInfo.getWeatherId());
            }

            weather.setDescription(weatherInfo.getDescription());
            weather.setVersion(weatherInfo.getScn());
            weather.setTitle(weatherInfo.getTitle());
            saveOrUpdate(HibernateUtil.getActiveSession(), weather);
        }

    }

    /**
     * Method for importing new experiments.
     * Requires people, research groups, scenarios and weather data imported first!
     *
     * @param experimentsInfo experiment information from server
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void importNewExperiments(List<ExperimentInfo> experimentsInfo) throws DaoException {
        log.debug(experimentsInfo.size() + " new experiments");

        for (ExperimentInfo expInfo : experimentsInfo) {
            Experiment exp = experimentDao.get(expInfo.getExperimentId());
            if (exp == null) {
                exp = new Experiment();
                exp.setExperimentId(expInfo.getExperimentId());
            }

            exp.setEndTime(new java.sql.Date(expInfo.getEndTimeInMillis()));
            exp.setStartTime(new java.sql.Date(expInfo.getStartTimeInMillis()));
            exp.setVersion(expInfo.getScn());
            exp.setTemperature((short) expInfo.getTemperature());
            exp.setWeathernote(expInfo.getWeatherNote());

            DigitizationInfo digitizationInfo = expInfo.getDigitizationInfo();

            if (digitizationInfo != null) {
                exp.setSamplingRate(digitizationInfo.getSamplingRate());
                exp.setFilter(digitizationInfo.getFilter());
                exp.setGain(digitizationInfo.getGain());
            }

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

            saveOrUpdate(HibernateUtil.getActiveSession(), exp);
        }
    }

    /**
     * Method for importing new data files from server.
     * Requires experiments data imported first!
     *
     * @param filesInfo data files information from server
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void importNewDataFiles(List<DataFileInfo> filesInfo) throws DaoException {
        log.debug(filesInfo.size() + " new data files");

        for (DataFileInfo fileInfo : filesInfo) {

            Session session = HibernateUtil.getActiveSession();
            DataFile file = dataFileDao.get(fileInfo.getFileId());
            if (file == null) {
                file = new DataFile();
                file.setDataFileId(fileInfo.getFileId());
            } else {
                HibernateUtil.reattachObject(session, file);
            }
            file.setFileLength(fileInfo.getFileLength());
            file.setMimetype(fileInfo.getMimeType());
            file.setVersion(fileInfo.getScn());
            file.setFilename(fileInfo.getFileName());

            Experiment exp = experimentDao.get(fileInfo.getExperimentId());
            if (exp != null) {
                file.setExperiment(exp);
                saveOrUpdate(session, file);
            }
        }
    }

    /**
     * Method for importing new hardware types from server.
     * Does not require any other previous data.
     *
     * @param hardwareInfo hardware types information from server
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void importNewHardware(List<HardwareInfo> hardwareInfo) throws DaoException {
        log.debug(hardwareInfo.size() + " new hardware types");

        for (HardwareInfo hw : hardwareInfo) {
            Hardware hardware = hardwareDao.get(hw.getHardwareId());
            if (hardware == null) {
                hardware = new Hardware();
                hardware.setTitle(hw.getTitle());
            }

            hardware.setType(hw.getType());
            hardware.setDescription(hw.getDescription());
            hardware.setHardwareId(hw.getHardwareId());
            hardware.setVersion(hw.getScn());

            saveOrUpdate(HibernateUtil.getActiveSession(), hardware);
        }
    }

    /**
     * Method for wiring references between experiments and hardware.
     * Requires experiments and hardware types data imported first!
     *
     * @param experimentsInfo experiments information from server
     * @throws ch.ethz.origo.jerpa.data.tier.dao.DaoException
     *          error during communication with database
     */
    private void updateExperimentHwRelations(List<ExperimentInfo> experimentsInfo) throws DaoException {
        log.debug("Linking experiments and hardware");

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
            saveOrUpdate(HibernateUtil.getActiveSession(), exp);
        }
    }

    /**
     * Method for saving or updating data in database using collection input.
     *
     * @param session active hibernate session
     * @param o       object to be commited
     */
    private void saveOrUpdate(Session session, Object o) {
        Transaction transaction = session.beginTransaction();

        try {
            session.saveOrUpdate(o);
            transaction.commit();
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            transaction.rollback();
        }

        session.close();
    }
}
