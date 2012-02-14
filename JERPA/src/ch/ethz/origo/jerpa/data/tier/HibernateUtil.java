package ch.ethz.origo.jerpa.data.tier;

import ch.ethz.origo.jerpa.data.dbtools.DbTool;
import ch.ethz.origo.jerpa.data.dbtools.DbToolFactory;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static final Logger log = Logger.getLogger(HibernateUtil.class);

    private static SessionFactory buildSessionFactory() {
        try {
            DbTool dbTools = DbToolFactory.getDerbyCreator();
            if(!dbTools.dbExists()){
                dbTools.createDb();
            }
            Configuration cfg = new Configuration().configure();
            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            ex.printStackTrace();
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Method for rebinding object with session. Workaround for Lazy loading issues.
     *
     * @param transientObject transient object
     * @return rebinded transient object
     */
    public static Object rebind(Object transientObject) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.refresh(transientObject);

        return transientObject;
    }
}