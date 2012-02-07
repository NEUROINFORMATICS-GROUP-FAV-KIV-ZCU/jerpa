package ch.ethz.origo.jerpa.data.tier;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static final Logger log = Logger.getLogger(HibernateUtil.class);

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration cfg = new Configuration().configure();
            SessionFactory factory = cfg.buildSessionFactory();
            return factory;
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