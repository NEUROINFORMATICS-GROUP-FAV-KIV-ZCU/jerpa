package ch.ethz.origo.jerpa.data.dbtools;

import ch.ethz.origo.juigle.application.PropertiesLoader;
import org.apache.log4j.Logger;
import org.hibernate.cfg.NotYetImplementedException;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * Class for managing Derby database.
 *
 * @author Petr Miko
 */
public class DerbyTools implements DbTool {

    private static final Logger log = Logger.getLogger(DerbyTools.class);

    public void createDb() {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName(PropertiesLoader.getProperty("db.derby.driver", "config.properties")).newInstance();
            String url = PropertiesLoader.getProperty("db.derby.url", "config.properties") + ";create=true";
            connection = DriverManager.getConnection(url);

            String sql = getSqlFromFile(PropertiesLoader.getProperty("db.derby.sql", "config.properties"));
            String[] commands = sql.split(";");

            statement = connection.createStatement();

            for(String command : commands)
            {
                if(!command.trim().isEmpty())
                {
                    log.debug(command);
                    statement.executeUpdate(command);
                }
            }
            connection.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException e1) {
                log.error(e.getMessage(), e);
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.error(e);
                }
            }
        }
    }

    public void rebuildDb() {
        throw new NotYetImplementedException("Not yet implemented");
    }

    public void removeDb() {
        throw new NotYetImplementedException("Not yet implemented");
    }

    public boolean dbExists() {
        Connection connection = null;

        try {
            Class.forName(PropertiesLoader.getProperty("db.derby.driver", "config.properties")).newInstance();
            connection = DriverManager.getConnection(PropertiesLoader.getProperty("db.derby.url", "config.properties"));
            return true;
        } catch (Exception e) {
            log.error(e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error(e);
                }
            }
        }
    }

    private Connection getConnection() {
        try {
            Class.forName(PropertiesLoader.getProperty("db.derby.driver", "config.properties")).newInstance();
            return DriverManager.getConnection(PropertiesLoader.getProperty("db.derby.url", "config.properties"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private String getSqlFromFile(String path) throws FileNotFoundException {
        String sql = "", read;
        BufferedReader reader = new BufferedReader(new FileReader(path));

        try {
            while ((read = reader.readLine()) != null) {
                sql += read;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return sql;
    }
}
