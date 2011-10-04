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
	public void writeFile(DataFile fileInfo, InputStream inStream) throws StorageException {
		write(fileInfo, inStream, false);
	}

	@Override
	public void overwriteFile(DataFile fileInfo, InputStream inStream) throws StorageException {
		write(fileInfo, inStream, true);
	}

	private synchronized void write(DataFile fileInfo, InputStream inStream, boolean overwrite) throws StorageException {
		Connection connection = null;
		PreparedStatement statement = null;
		String sql;

		try {
			connection = getConnection();

			if (overwrite) {
				sql = "UPDATE DATA_FILE SET sampling_rate = ?, file_content = ?, experiment_id = ?, mimetype = ?, filename = ? where data_file_id = ?";
				statement = connection.prepareStatement(sql);
				statement.setFloat(1, 0);
				statement.setBinaryStream(2, inStream, (int) fileInfo.getFileLength());
				statement.setInt(3, fileInfo.getExperimentId());
				statement.setString(4, fileInfo.getMimeType());
				statement.setString(5, fileInfo.getFileName());
				statement.setInt(6, fileInfo.getFileId());
			}
			else {
				sql = "INSERT INTO DATA_FILE VALUES (?, ?, ?, ?, ?, ?)";
				statement = connection.prepareStatement(sql);
				statement.setInt(1, fileInfo.getFileId());
				statement.setFloat(2, 0);
				statement.setBinaryStream(3, inStream, (int) fileInfo.getFileLength());
				statement.setInt(4, fileInfo.getExperimentId());
				statement.setString(5, fileInfo.getMimeType());
				statement.setString(6, fileInfo.getFileName());
			}
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
				file.setFileLength(getFileLength(file.getFileId()));

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
				experiment.setStartTime(set.getDate("START_TIME"));
				experiment.setEndTime(set.getDate("END_TIME"));
				experiment.setTemperature(set.getInt("TEMPERATURE"));
				experiment.setWeatherNote(set.getString("WEATHERNOTE"));
				experiment.setPrivateFlag(set.getInt("PRIVATE"));

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
}
