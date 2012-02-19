package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Projections;

import java.io.Serializable;
import java.util.List;

/**
 * @param <T>  Object type
 * @param <PK> Identifier, i.e. primary key
 * @author Petr Miko
 *         <p/>
 *         Class with functionality shared among all the DAO instances.
 */
public class GenericDao<T, PK extends Serializable> {

    private Class<T> type;

    /**
     * Constructor.
     *
     * @param type object/table type
     */
    public GenericDao(Class<T> type) {
        this.type = type;
    }

    /**
     * Method for saving new record into database.
     *
     * @param newRecord new object
     * @return object's identifier.
     */
    @SuppressWarnings("unchecked")
    public PK save(T newRecord) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        PK primaryKey = (PK) session.save(newRecord);
        transaction.commit();
        return primaryKey;
    }

    /**
     * Method for updating existing record in database.
     *
     * @param transientRecord updated object
     */
    public void update(T transientRecord) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(transientRecord);
        transaction.commit();
    }

    /**
     * Method for retrieving object from DB by its identifier.
     *
     * @param identifier identifier, i.e. primary key
     * @return specified object
     */
    @SuppressWarnings("unchecked")
    public T get(PK identifier) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        T object = (T) session.get(type, identifier);
        transaction.commit();
        return object;
    }

    /**
     * Getter of the highest revision value from table specified by object's type.
     *
     * @return newest revision value
     */
    public long getLastRevision() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Long version = (Long) session.createCriteria(type).setProjection(Projections.max("version")).uniqueResult();
        return (version != null ? version : 0);
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<T> allRecords = session.createCriteria(type).list();
        transaction.commit();
        return allRecords;
    }
}
