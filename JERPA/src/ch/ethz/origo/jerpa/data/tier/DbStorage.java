package ch.ethz.origo.jerpa.data.tier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.data.tier.border.DataFile;
import ch.ethz.origo.jerpa.data.tier.border.Experiment;
import ch.ethz.origo.jerpa.data.tier.border.Person;
import ch.ethz.origo.jerpa.data.tier.border.ResearchGroup;
import ch.ethz.origo.jerpa.data.tier.border.Scenario;
import ch.ethz.origo.jerpa.data.tier.border.Weather;

public class DbStorage implements Storage {

	private final static Logger log = Logger.getLogger(DbStorage.class);

	public DbStorage() throws StorageException {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();

			DriverManager.getConnection("jdbc:derby:db/derby;create=true");

			if (!tablesExists())
				createTables();

			log.info("Database initialized.");

		}
		catch (Exception e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
	}

	private boolean tablesExists() throws StorageException {
		Connection connection = null;
		DatabaseMetaData meta = null;
		ResultSet set = null;

		try {
			connection = getConnection();
			meta = connection.getMetaData();
			set = meta.getTables(null, "APP", "EXPERIMENT", null);

			if (!set.next())
				return false;
			else
				return true;
		}
		catch (Exception e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (set != null)
					set.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private void createTables() throws StorageException {

		Connection connection = null;
		Statement statement = null;
		FileInputStream inStream = null;
		StringBuilder builder = new StringBuilder();

		try {
			inStream = new FileInputStream("config/create_tables2.sql");

			int read;

			while ((read = inStream.read()) != -1) {

				builder.append((char) read);
			}

			connection = getConnection();
			statement = connection.createStatement();

			String[] sqls = builder.toString().split(";");

			for (String sql : sqls) {
				statement.addBatch(sql);
			}

			statement.executeBatch();

			log.info("Database tables created successfully.");
		}
		catch (Exception e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private synchronized Connection getConnection() throws StorageException {

		try {
			return DriverManager.getConnection("jdbc:derby:db/derby");
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
	}

	@Override
	public synchronized void writeDataFile(DataFile fileInfo, InputStream inStream) throws StorageException {
		Connection connection = null;
		PreparedStatement statement = null;
		String sql;

		try {
			connection = getConnection();

			sql = "UPDATE DATA_FILE SET file_content = ? where data_file_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setBinaryStream(1, inStream, (int) fileInfo.getFileLength());
			statement.setInt(2, fileInfo.getFileId());

			statement.execute();
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public File getFile(int fileId) throws StorageException {
		Connection connection = null;
		Statement statement = null;
		ResultSet set = null;
		String sql = "select FILE_CONTENT from DATA_FILE where data_file_id = " + fileId;

		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			set = statement.executeQuery(sql);

			Blob file = null;

			while (set.next()) {
				file = set.getBlob("FILE_CONTENT");
			}

			File tmpFile = null;
			try {
				tmpFile = File.createTempFile("ededb_vizualize", ".tmp");

				InputStream inStream = file.getBinaryStream();
				OutputStream outStream = new FileOutputStream(tmpFile);

				int in = -1;

				while ((in = inStream.read()) != -1) {
					outStream.write(in);
				}
			}
			catch (FileNotFoundException e) {
				throw new StorageException(e);
			}
			catch (IOException e) {
				throw new StorageException(e);
			}

			return tmpFile;
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public long getFileLength(int fileId) throws StorageException {
		Connection connection = null;
		Statement statement = null;
		ResultSet set = null;
		String sql = "select FILE_CONTENT from DATA_FILE where data_file_id = " + fileId;

		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			set = statement.executeQuery(sql);

			Blob file = null;

			while (set.next()) {
				file = set.getBlob("FILE_CONTENT");
			}

			if (file != null)
				return file.length();
			else
				return 0;
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public FileState getFileState(DataFile fileInfo) throws StorageException {

		if (getFileLength(fileInfo.getFileId()) == fileInfo.getFileLength()) {
			return FileState.HAS_COPY;
		}
		else if (getFileLength(fileInfo.getFileId()) == 0)
			return FileState.NO_COPY;

		else
			return FileState.CORRUPTED;
	}

	@Override
	public void removeFile(int fileId) throws StorageException {
		Connection connection = null;
		PreparedStatement statement = null;
		String sql;

		try {
			connection = getConnection();

			sql = "DELETE FROM DATA_FILE WHERE data_file_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, fileId);

			statement.execute();
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public List<DataFile> getDataFiles(List<Integer> experimentsId) throws StorageException {
		Connection connection = null;
		Statement statement = null;
		ResultSet set = null;
		List<DataFile> dataFiles = new ArrayList<DataFile>();

		String sql = "select * from DATA_FILE where EXPERIMENT_ID = ";

		for (int i = 0; i < experimentsId.size(); i++) {
			if (i > 0)
				sql += " OR EXPERIMENT_ID = ";
			sql += experimentsId.get(i);
		}

		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			set = statement.executeQuery(sql);

			while (set.next()) {
				DataFile file = new DataFile();

				file.setFileId(set.getInt("DATA_FILE_ID"));
				file.setSamplingRate(set.getFloat("SAMPLING_RATE"));
				file.setExperimentId(set.getInt("EXPERIMENT_ID"));
				file.setMimeType(set.getString("MIMETYPE"));
				file.setFileName(set.getString("FILENAME"));
				file.setFileLength(set.getLong("FILE_LENGTH"));

				dataFiles.add(file);
			}

			return dataFiles;
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public List<Experiment> getExperiments() throws StorageException {
		Connection connection = null;
		Statement statement = null;
		ResultSet set = null;
		List<Experiment> experiments = new ArrayList<Experiment>();

		String sql = "select * from EXPERIMENT";
		try {
			connection = getConnection();
			connection.setAutoCommit(false);
			statement = connection.createStatement();
			set = statement.executeQuery(sql);

			while (set.next()) {
				Experiment experiment = new Experiment();

				experiment.setExperimentId(set.getInt("EXPERIMENT_ID"));
				experiment.setOwnerId(set.getInt("OWNER_ID"));
				experiment.setSubjectPersonId(set.getInt("SUBJECT_PERSON_ID"));
				experiment.setScenarioId(set.getInt("SCENARIO_ID"));
				experiment.setWeatherId(set.getInt("WEATHER_ID"));
				experiment.setResearchGroupId(set.getInt("RESEARCH_GROUP_ID"));
				experiment.setStartTime(set.getTimestamp("START_TIME"));
				experiment.setEndTime(set.getTimestamp("END_TIME"));
				experiment.setTemperature(set.getInt("TEMPERATURE"));
				experiment.setWeatherNote(set.getString("WEATHERNOTE"));
				experiment.setPrivateFlag(set.getInt("PRIVATE"));
				experiment.setTitle(set.getString("TITLE"));

				experiments.add(experiment);
			}

			return experiments;
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void setPeople(List<Person> people) throws StorageException {

		Connection connection = null;
		Statement statement = null;
		PreparedStatement prepStatement = null;

		String sql_clean = "delete from PERSON";
		String sql_insert = "INSERT INTO PERSON (PERSON_ID, DEFAULT_GROUP_ID, GIVENNAME, SURNAME, GENDER) VALUES (?, ?, ?, ?, ?)";
		try {

			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql_clean);

			for (Person person : people) {

				prepStatement = connection.prepareStatement(sql_insert);
				prepStatement.setInt(1, person.getPersonId());

				if (person.getDefaultGroupId() != -1)
					prepStatement.setInt(2, person.getDefaultGroupId());
				else
					prepStatement.setNull(2, java.sql.Types.INTEGER);

				prepStatement.setString(3, person.getGivenName());
				prepStatement.setString(4, person.getSurname());

				prepStatement.setString(5, "" + person.getGender());

				prepStatement.executeUpdate();

				prepStatement.close();
			}
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (prepStatement != null) {
					prepStatement.close();
				}
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}

				log.info("Person records updated.");
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void setExperiments(List<Experiment> experiments) throws StorageException {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement prepStatement = null;

		String sql_clean = "delete from EXPERIMENT";
		String sql_insert = "INSERT INTO EXPERIMENT (EXPERIMENT_ID, OWNER_ID, SUBJECT_PERSON_ID, SCENARIO_ID, WEATHER_ID, RESEARCH_GROUP_ID, START_TIME, END_TIME"
		        + ", TEMPERATURE, WEATHERNOTE, PRIVATE, TITLE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {

			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql_clean);

			for (Experiment experiment : experiments) {

				prepStatement = connection.prepareStatement(sql_insert);
				prepStatement.setInt(1, experiment.getExperimentId());
				prepStatement.setInt(2, experiment.getOwnerId());
				prepStatement.setInt(3, experiment.getSubjectPersonId());
				prepStatement.setInt(4, experiment.getScenarioId());
				prepStatement.setInt(5, experiment.getWeatherId());
				prepStatement.setInt(6, experiment.getResearchGroupId());
				prepStatement.setTimestamp(7, experiment.getStartTime());
				prepStatement.setTimestamp(8, experiment.getEndTime());
				prepStatement.setInt(9, experiment.getTemperature());
				prepStatement.setString(10, experiment.getWeatherNote());
				prepStatement.setInt(11, experiment.getPrivateFlag());
				prepStatement.setString(12, experiment.getTitle());

				prepStatement.executeUpdate();

				prepStatement.close();
			}
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (prepStatement != null) {
					prepStatement.close();
				}
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}

				log.info("Experiments records updated.");
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void setScenarios(List<Scenario> scenarios) throws StorageException {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement prepStatement = null;

		String sql_clean = "delete from SCENARIO";
		String sql_insert = "INSERT INTO SCENARIO (SCENARIO_ID, OWNER_ID, RESEARCH_GROUP_ID, TITLE, SCENARIO_LENGTH, DESCRIPTION, SCENARIO_NAME, MIMETYPE) "
		        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {

			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql_clean);

			for (Scenario scenario : scenarios) {

				prepStatement = connection.prepareStatement(sql_insert);
				prepStatement.setInt(1, scenario.getScenarioId());
				prepStatement.setInt(2, scenario.getOwnerId());
				prepStatement.setInt(3, scenario.getResearchGroupId());
				prepStatement.setString(4, scenario.getTitle());
				prepStatement.setInt(5, scenario.getScenarioLength());
				prepStatement.setString(6, scenario.getDescription());
				prepStatement.setString(7, scenario.getScenarioName());
				prepStatement.setString(8, scenario.getMimeType());

				prepStatement.executeUpdate();

				prepStatement.close();
			}
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (prepStatement != null) {
					prepStatement.close();
				}
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}

				log.info("Scenario records updated.");
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void setWeathers(List<Weather> weathers) throws StorageException {

		Connection connection = null;
		Statement statement = null;
		PreparedStatement prepStatement = null;

		String sql_clean = "delete from WEATHER";
		String sql_insert = "INSERT INTO WEATHER VALUES (?, ?, ?)";
		try {

			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql_clean);

			for (Weather weather : weathers) {

				prepStatement = connection.prepareStatement(sql_insert);
				prepStatement.setInt(1, weather.getWeatherId());
				prepStatement.setString(2, weather.getDescription());
				prepStatement.setString(3, weather.getTitle());

				prepStatement.executeUpdate();

				prepStatement.close();
			}
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (prepStatement != null) {
					prepStatement.close();
				}
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}

				log.info("Weather records updated.");
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void setResearchGroups(List<ResearchGroup> groups) throws StorageException {
		Connection connection = null;
		Statement statement = null;
		PreparedStatement prepStatement = null;

		String sql_clean = "delete from RESEARCH_GROUP";
		String sql_insert = "INSERT INTO RESEARCH_GROUP VALUES (?, ?, ?, ?)";
		try {

			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql_clean);

			for (ResearchGroup group : groups) {

				prepStatement = connection.prepareStatement(sql_insert);

				prepStatement.setInt(1, group.getResearchGroupId());
				prepStatement.setInt(2, group.getOwnerId());
				prepStatement.setString(3, group.getTitle());
				prepStatement.setString(4, group.getDescription());
				prepStatement.executeUpdate();

				prepStatement.close();
			}
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (prepStatement != null) {
					prepStatement.close();
				}
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}

				log.info("Research groups records updated.");
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void setDataFiles(List<DataFile> dataFiles) throws StorageException {

		Connection connection = null;
		Statement statement = null;
		PreparedStatement prepStatement = null;

		String sql_clean = "delete from DATA_FILE";
		String sql_insert = "INSERT INTO DATA_FILE (DATA_FILE_ID, SAMPLING_RATE, EXPERIMENT_ID, MIMETYPE, FILENAME, FILE_LENGTH) VALUES (?, ?, ?, ?, ?, ?)";
		try {

			connection = getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(sql_clean);

			for (DataFile dataFile : dataFiles) {

				prepStatement = connection.prepareStatement(sql_insert);

				prepStatement.setInt(1, dataFile.getFileId());
				prepStatement.setDouble(2, dataFile.getSamplingRate());
				prepStatement.setInt(3, dataFile.getExperimentId());
				prepStatement.setString(4, dataFile.getMimeType());
				prepStatement.setString(5, dataFile.getFileName());
				prepStatement.setLong(6, dataFile.getFileLength());

				prepStatement.executeUpdate();

				prepStatement.close();
			}
		}
		catch (SQLException e) {
			StorageException exception = new StorageException(e);
			throw exception;
		}
		finally {
			try {
				if (prepStatement != null) {
					prepStatement.close();
				}
				if (statement != null)
					statement.close();
				if (connection != null) {
					connection.commit();
					connection.close();
				}

				log.info("Data files records updated.");
			}
			catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}
