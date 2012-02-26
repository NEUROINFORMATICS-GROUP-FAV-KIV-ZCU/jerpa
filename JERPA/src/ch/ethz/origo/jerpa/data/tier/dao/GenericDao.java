package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Projections;

import java.io.Serializable;
import java.lang.reflect.GenericDeclaration;
import java.util.List;

/**
 * @param <T>  Object type
 * @param <PK> Identifier, i.e. primary key
 * @author Petr Miko
 *         <p/>
 *         Class with functionality shared among all the DAO instances.
 */
public abstract class GenericDao<T, PK extends Serializable> {

    private Class<T> type;

    /**
     * Constructor.
     *
     * @param type object/table type
     */
    protected GenericDao(Class<T> type) {
        this.type = type;
    }

    /**
     * Method for saving new record into database.
     *
     * @param newRecord new object
     * @return object's identifier.
     * @throws DaoException error during save
     */
    @SuppressWarnings("unchecked")
    public PK save(T newRecord) throws DaoException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        PK primaryKey = (PK) session.save(newRecord);
        try {
            transaction.commit();
            return primaryKey;
        } catch (HibernateException e) {
            transaction.rollback();
            throw new DaoException(e);
        } finally {
            session.close();
        }
    }

    /**
     * Method for updating existing record in database.
     *
     * @param transientRecord updated object
     * @throws DaoException error during update
     */
    public void update(T transientRecord) throws DaoException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.update(transientRecord);
        try {
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            throw new DaoException(e);
        } finally {
            session.close();
        }
    }

    /**
     * Method for retrieving object from DB by its identifier.
     *
     * @param identifier identifier, i.e. primary key
     * @return specified object
     * @throws DaoException error during getting object
     */
    @SuppressWarnings("unchecked")
    public abstract T get(PK identifier) throws DaoException; {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = session.beginTransaction();
//        T object = (T) session.get(type, identifier);
//        try {
//            transaction.commit();
//            return object;
//        } catch (HibernateException e) {
//            transaction.rollback();
//            throw new DaoException(e);
//        } finally {
//            session.close();
//        }
    }

    /**
     * Getter of the highest revision value from table specified by object's type.
     *
     * @return newest revision value
     */
    public long getLastRevision() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            Long version = (Long) session.createCriteria(type).setProjection(Projections.max("version")).uniqueResult();
            return (version != null ? version : 0);
        } finally {
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() throws DaoException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        List<T> allRecords = session.createCriteria(type).list();
        try {
            transaction.commit();
            return allRecords;
        } catch (HibernateException e) {
            transaction.rollback();
            throw new DaoException(e);
        } finally {
            session.close();
        }
    }

    public void remove(PK primaryKey) throws DaoException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        Object object = session.get(type, primaryKey);
        session.delete(object);

        try {
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            throw new DaoException(e);
        } finally {
            session.close();
        }
    }
}
