package ch.ethz.origo.jerpa.data.tier;

import ch.ethz.origo.jerpa.data.tier.dao.*;

/**
 * @author Petr Miko
 *         <p/>
 *         Class for obtaining DAO objects.
 */
public class DaoFactory {

    private static DataFileDao dataFileDao = new DataFileDao();
    private static ExperimentDao experimentDao = new ExperimentDao();
    private static ScenarioDao scenarioDao = new ScenarioDao();
    private static ResearchGroupDao researchGroupDao = new ResearchGroupDao();
    private static HardwareDao hardwareDao = new HardwareDao();
    private static WeatherDao weatherDao = new WeatherDao();
    private static PersonDao personDao = new PersonDao();

    public static DataFileDao getDataFileDao() {
        return dataFileDao;
    }

    public static ExperimentDao getExperimentDao() {
        return experimentDao;
    }

    public static ScenarioDao getScenarioDao() {
        return scenarioDao;
    }

    public static ResearchGroupDao getResearchGroupDao() {
        return researchGroupDao;
    }

    public static HardwareDao getHardwareDao() {
        return hardwareDao;
    }

    public static WeatherDao getWeatherDao() {
        return weatherDao;
    }

    public static PersonDao getPersonDao() {
        return personDao;
    }
}
