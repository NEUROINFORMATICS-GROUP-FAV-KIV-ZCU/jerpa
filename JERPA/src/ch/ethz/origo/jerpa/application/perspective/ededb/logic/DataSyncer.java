package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.data.tier.Storage;
import ch.ethz.origo.jerpa.data.tier.StorageException;
import ch.ethz.origo.jerpa.data.tier.border.*;
import ch.ethz.origo.jerpa.ededclient.generated.*;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DataSyncer {

    private final EDEDClient session;
    private final Storage storage;
    private final EDEDBController controller;

    private final static Logger log = Logger.getLogger(DataSyncer.class);

    public DataSyncer(EDEDClient session, EDEDBController controller, Storage storage) {
        this.session = session;
        this.storage = storage;
        this.controller = controller;

        SyncThread syncThread = new SyncThread();

        syncThread.start();
    }

    private class SyncThread extends Thread {

        long SLEEP_INTERVAL = 10000;
        private final Object lock = new Object();

        @Override
        public void run() {
            setName("DataSyncThread");

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

                    Working.setActivity(true, "Weather data update");
                    updateWeather(storage.getLastRevision("weather"));
                    Working.setActivity(false, "Weather data update");

                    Working.setActivity(true, "Person data update");
                    updatePerson(storage.getLastRevision("person"));
                    Working.setActivity(false, "Person data update");

                    Working.setActivity(true, "Research group data update");
                    updateResearchGroup(storage.getLastRevision("researchGroup"));
                    Working.setActivity(false, "Research group data update");

                    Working.setActivity(true, "Scenario data update");
                    updateScenario(storage.getLastRevision("scenario"));
                    Working.setActivity(false, "Scenario data update");

                    Working.setActivity(true, "Experiment data update");
                    updateExperiment(storage.getLastRevision("experiment"));
                    Working.setActivity(false, "Experiment data update");

                    Working.setActivity(true, "Data file data update");
                    updateDataFile(storage.getLastRevision("dataFile"));
                    Working.setActivity(false, "Data file data update");

                } catch (StorageException e) {
                    log.error(e.getMessage(), e);
                }

                controller.update();
            } while (true);

        }

        private void updateResearchGroup(long lastScn) throws StorageException {
            List<ResearchGroupInfo> groups = session.getService().getResearchGroups(lastScn);
            List<ResearchGroup> tempGroups = new ArrayList<ResearchGroup>();

            log.debug("Research Group update size: " + groups.size() + " | revision: " + lastScn);

            for (ResearchGroupInfo group : groups) {
                ResearchGroup temp = new ResearchGroup();

                temp.setDescription(group.getDescription());
                temp.setOwnerId(group.getOwnerId());
                temp.setResearchGroupId(group.getResearchGroupId());
                temp.setTitle(group.getTitle());
                temp.setRevision(group.getScn());

                tempGroups.add(temp);
            }

            if (!groups.isEmpty())
                storage.setResearchGroups(tempGroups);

        }

        private void updateDataFile(long lastScn) throws StorageException {
            try {
                List<DataFileInfo> files = session.getService().getDataFiles(lastScn);
                List<DataFile> tempFiles = new ArrayList<DataFile>();

                log.debug("Data files update size: " + files.size() + " | revision: " + lastScn);

                for (DataFileInfo file : files) {
                    DataFile temp = new DataFile();

                    temp.setExperimentId(file.getExperimentId());
                    temp.setFileId(file.getFileId());
                    temp.setFileLength(file.getFileLength());
                    temp.setFileName(file.getFileName());
                    temp.setMimeType(file.getMimeType());
                    temp.setSamplingRate(file.getSamplingRate());
                    temp.setRevision(file.getScn());

                    tempFiles.add(temp);
                }

                if (!files.isEmpty())
                    storage.setDataFiles(tempFiles);

            } catch (DataDownloadException_Exception exception) {
                throw new StorageException(exception);
            }

        }

        private void updateExperiment(long lastScn) throws StorageException {
            List<ExperimentInfo> experiments = session.getService().getExperiments(lastScn);
            List<Experiment> tempExperiments = new ArrayList<Experiment>();

            log.debug("Experiments update size: " + experiments.size() + " | revision: " + lastScn);

            for (ExperimentInfo experiment : experiments) {
                Experiment temp = new Experiment();

                temp.setEndTime(new Timestamp(experiment.getEndTimeInMillis()));
                temp.setStartTime(new Timestamp(experiment.getStartTimeInMillis()));
                temp.setExperimentId(experiment.getExperimentId());
                temp.setOwnerId(experiment.getOwnerId());
                temp.setPrivateFlag(experiment.getPrivateFlag());
                temp.setResearchGroupId(experiment.getResearchGroupId());
                temp.setScenarioId(experiment.getScenarioId());
                temp.setSubjectPersonId(experiment.getSubjectPersonId());
                temp.setTemperature(experiment.getTemperature());
                temp.setWeatherId(experiment.getWeatherId());
                temp.setWeatherNote(experiment.getWeatherNote());
                temp.setTitle(experiment.getTitle());
                temp.setRevision(experiment.getScn());

                tempExperiments.add(temp);
            }

            if (!experiments.isEmpty())
                storage.setExperiments(tempExperiments);

        }

        private void updateScenario(long lastScn) throws StorageException {
            List<ScenarioInfo> scenarios = session.getService().getScenarios(lastScn);
            List<Scenario> tempScenarios = new ArrayList<Scenario>();

            log.debug("Scenarios update size: " + scenarios.size() + " | revision: " + lastScn);


            for (ScenarioInfo scenario : scenarios) {
                Scenario temp = new Scenario();

                temp.setDescription(scenario.getDescription());
                temp.setMimeType(scenario.getMimeType());
                temp.setOwnerId(scenario.getOwnerId());
                temp.setResearchGroupId(scenario.getResearchGroupId());
                temp.setScenarioId(scenario.getScenarioId());
                temp.setScenarioLength(scenario.getScenarioLength());
                temp.setScenarioName(scenario.getScenarioName());
                temp.setTitle(scenario.getTitle());
                temp.setRevision(scenario.getScn());

                tempScenarios.add(temp);
            }

            if (!scenarios.isEmpty())
                storage.setScenarios(tempScenarios);

        }

        private void updatePerson(long lastScn) throws StorageException {
            List<PersonInfo> people = session.getService().getPeople(lastScn);
            List<Person> tempPeople = new ArrayList<Person>();

            log.debug("People update size: " + people.size() + " | revision: " + lastScn);


            for (PersonInfo person : people) {
                Person temp = new Person();

                temp.setDefaultGroupId(person.getDefaultGroupId());
                temp.setGender((char) person.getGender());
                temp.setGivenName(person.getGivenName());
                temp.setPersonId(person.getPersonId());
                temp.setSurname(person.getSurname());
                temp.setRevision(person.getScn());

                tempPeople.add(temp);
            }

            if (!people.isEmpty())
                storage.setPeople(tempPeople);

        }

        private void updateWeather(long lastScn) throws StorageException {
            List<WeatherInfo> weatherService = session.getService().getWeather(lastScn);
            List<Weather> weathers = new ArrayList<Weather>();

            log.debug("Weather update size: " + weatherService.size() + " | revision: " + lastScn);

            for (WeatherInfo weather : weatherService) {

                Weather temp = new Weather();

                temp.setDescription(weather.getDescription());
                temp.setTitle(weather.getTitle());
                temp.setWeatherId(weather.getWeatherId());
                temp.setRevision(weather.getScn());

                weathers.add(temp);
            }

            if (!weathers.isEmpty())
                storage.setWeathers(weathers);

        }
    }
}
