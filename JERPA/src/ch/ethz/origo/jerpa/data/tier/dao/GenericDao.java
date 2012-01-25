package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 22.1.12
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class GenericDao<T, PK extends Serializable> {

    private Class<T> type;

    public GenericDao(Class<T> type) {
        this.type = type;
    }

    public PK save(T newRecord) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        PK primaryKey = (PK) session.save(newRecord);
        transaction.commit();
        return primaryKey;
    }

    public void update(T transientRecord) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(transientRecord);
        transaction.commit();
    }

    public T get(PK identifier) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        T object = (T) session.get(type, identifier);
        transaction.commit();
        return object;
    }

    public long getLastRevision() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Long version = (Long) session.createCriteria(type).setProjection(Projections.max("version")).uniqueResult();
        return (version != null ? version : 0);
    }
}
