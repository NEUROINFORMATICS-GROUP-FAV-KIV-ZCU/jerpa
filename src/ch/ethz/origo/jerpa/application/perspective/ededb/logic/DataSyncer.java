package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.data.tier.Storage;
import ch.ethz.origo.jerpa.data.tier.StorageException;
import ch.ethz.origo.jerpa.data.tier.border.DataFile;
import ch.ethz.origo.jerpa.data.tier.border.Experiment;
import ch.ethz.origo.jerpa.data.tier.border.Person;
import ch.ethz.origo.jerpa.data.tier.border.ResearchGroup;
import ch.ethz.origo.jerpa.data.tier.border.Scenario;
import ch.ethz.origo.jerpa.data.tier.border.Weather;
import ch.ethz.origo.jerpa.ededclient.generated.DataDownloadException_Exception;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import ch.ethz.origo.jerpa.ededclient.generated.ExperimentInfo;
import ch.ethz.origo.jerpa.ededclient.generated.PersonInfo;
import ch.ethz.origo.jerpa.ededclient.generated.ResearchGroupInfo;
import ch.ethz.origo.jerpa.ededclient.generated.ScenarioInfo;
import ch.ethz.origo.jerpa.ededclient.generated.SyncChangesInfo;
import ch.ethz.origo.jerpa.ededclient.generated.WeatherInfo;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;

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

		private final Object lock = new Object();

		private Timestamp lastUpdate = Timestamp.valueOf("1989-03-18 13:18:22");

		List<SyncChangesInfo> changes = null;
		boolean changed = false;

		@Override
		public void run() {

			setName("DataSyncThread");
			Timestamp tmpUpdate = Timestamp.valueOf("1989-03-18 13:18:22");

			while (true) {

				changed = false;

				synchronized (lock) {
					try {
						lock.wait(5000);
					}
					catch (InterruptedException e) {
						log.error(e.getMessage(), e);
					}
				}

				if (!session.isConnected()) {
					continue;
				}

				changes = session.getService().getSyncChanges();

				try {
					for (SyncChangesInfo change : changes) {

						if ("weather".equalsIgnoreCase(change.getTableName()) && lastUpdate.getTime() < change.getLastChangeInMillis()) {

							updateWeather();

							if (tmpUpdate.getTime() < change.getLastChangeInMillis())
								tmpUpdate.setTime(change.getLastChangeInMillis());
							changed = true;

							continue;
						}

						if ("person".equalsIgnoreCase(change.getTableName()) && lastUpdate.getTime() < change.getLastChangeInMillis()) {

							updatePerson();

							if (tmpUpdate.getTime() < change.getLastChangeInMillis())
								tmpUpdate.setTime(change.getLastChangeInMillis());
							changed = true;

							continue;
						}

						if ("research_group".equalsIgnoreCase(change.getTableName()) && lastUpdate.getTime() < change.getLastChangeInMillis()) {

							updateResearchGroup();

							if (tmpUpdate.getTime() < change.getLastChangeInMillis())
								tmpUpdate.setTime(change.getLastChangeInMillis());
							changed = true;

							continue;
						}

						if ("scenario".equalsIgnoreCase(change.getTableName()) && lastUpdate.getTime() < change.getLastChangeInMillis()) {

							updateScenario();

							if (tmpUpdate.getTime() < change.getLastChangeInMillis())
								tmpUpdate.setTime(change.getLastChangeInMillis());
							changed = true;

							continue;
						}

						if ("experiment".equalsIgnoreCase(change.getTableName()) && lastUpdate.getTime() < change.getLastChangeInMillis()) {

							updateExperiment();

							if (tmpUpdate.getTime() < change.getLastChangeInMillis())
								tmpUpdate.setTime(change.getLastChangeInMillis());
							changed = true;

							continue;
						}

						if ("data_file".equalsIgnoreCase(change.getTableName()) && lastUpdate.getTime() < change.getLastChangeInMillis()) {

							updateDataFile();

							if (tmpUpdate.getTime() < change.getLastChangeInMillis())
								tmpUpdate.setTime(change.getLastChangeInMillis());
							changed = true;

							continue;
						}

					}
				}
				catch (StorageException e) {
					log.error(e.getMessage(), e);
				}

				if (changed) {
					lastUpdate = tmpUpdate;

					controller.update();
				}

			}

		}

		private void updateResearchGroup() throws StorageException {
			List<ResearchGroupInfo> groups = session.getService().getResearchGroups();
			List<ResearchGroup> tempGroups = new ArrayList<ResearchGroup>();

			for (ResearchGroupInfo group : groups) {
				ResearchGroup temp = new ResearchGroup();

				temp.setDescription(group.getDescription());
				temp.setOwnerId(group.getOwnerId());
				temp.setResearchGroupId(group.getResearchGroupId());
				temp.setTitle(group.getTitle());

				tempGroups.add(temp);
			}

			storage.setResearchGroups(tempGroups);

		}

		private void updateDataFile() throws StorageException {
			try {
				List<DataFileInfo> files = session.getService().getDataFiles();
				List<DataFile> tempFiles = new ArrayList<DataFile>();

				for (DataFileInfo file : files) {
					DataFile temp = new DataFile();

					temp.setExperimentId(file.getExperimentId());
					temp.setFileId(file.getFileId());
					temp.setFileLength(file.getFileLength());
					temp.setFileName(file.getFileName());
					temp.setMimeType(file.getMimeType());
					temp.setSamplingRate(file.getSamplingRate());

					tempFiles.add(temp);
				}

				storage.setDataFiles(tempFiles);

			}
			catch (DataDownloadException_Exception exception) {
				throw new StorageException(exception);
			}

		}

		private void updateExperiment() throws StorageException {
			List<ExperimentInfo> experiments = session.getService().getExperiments();
			List<Experiment> tempExperiments = new ArrayList<Experiment>();

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

				tempExperiments.add(temp);
			}

			storage.setExperiments(tempExperiments);

		}

		private void updateScenario() throws StorageException {
			List<ScenarioInfo> scenarios = session.getService().getScenarios();
			List<Scenario> tempScenarios = new ArrayList<Scenario>();

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

				tempScenarios.add(temp);
			}

			storage.setScenarios(tempScenarios);

		}

		private void updatePerson() throws StorageException {
			List<PersonInfo> people = session.getService().getPeople();
			List<Person> tempPeople = new ArrayList<Person>();

			for (PersonInfo person : people) {
				Person temp = new Person();

				temp.setDefaultGroupId(person.getDefaultGroupId());
				temp.setGender((char) person.getGender());
				temp.setGivenName(person.getGivenName());
				temp.setPersonId(person.getPersonId());
				temp.setSurname(person.getSurname());

				tempPeople.add(temp);
			}

			storage.setPeople(tempPeople);

		}

		private void updateWeather() throws StorageException {
			List<WeatherInfo> weatherService = session.getService().getWeather();
			List<Weather> weathers = new ArrayList<Weather>();

			for (WeatherInfo weather : weatherService) {

				Weather temp = new Weather();

				temp.setDescription(weather.getDescription());
				temp.setTitle(weather.getTitle());
				temp.setWeatherId(weather.getWeatherId());

				weathers.add(temp);
			}

			storage.setWeathers(weathers);

		}
	}
}
