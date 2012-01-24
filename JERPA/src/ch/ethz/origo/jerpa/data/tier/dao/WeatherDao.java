package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.pojo.Weather;

/**
 * @author Petr Miko
 *
 * DAO for Weather type.
 */
public class WeatherDao extends GenericDao<Weather, Integer> {

    public WeatherDao(){
        super(Weather.class);
    }
}
