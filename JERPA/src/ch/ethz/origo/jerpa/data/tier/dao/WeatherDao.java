package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.Weather;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Petr Miko
 *
 * DAO for Weather type.
 */
public final class WeatherDao extends GenericDao<Weather, Integer> {

    private static WeatherDao instance = new WeatherDao();

    /**
     * Getter of WeatherDao instance.
     * @return dao instance
     */
    public static WeatherDao getInstance(){
        return instance;
    }

    private WeatherDao(){
        super(Weather.class);
    }

    @Override
    public Weather get(Integer identifier) throws DaoException {

        String hql = "from Weather  w where w.weatherId = :identifier";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            return (Weather) session.createQuery(hql).setInteger("identifier",identifier).uniqueResult();
        } finally {
            transaction.commit();
            session.close();
        }
    }
}
