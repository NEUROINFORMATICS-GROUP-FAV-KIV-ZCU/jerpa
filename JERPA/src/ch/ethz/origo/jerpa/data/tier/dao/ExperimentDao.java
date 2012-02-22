package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Projections;

import java.util.Collections;
import java.util.List;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for Experiment type manipulation.
 */
public class ExperimentDao extends GenericDao<Experiment, Integer> {

    private static final Logger log = Logger.getLogger(ExperimentDao.class);

    public ExperimentDao() {
        super(Experiment.class);
    }

    @SuppressWarnings("unchecked")
    public List<Experiment> getAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        List<Experiment> allRecords = session.createCriteria(Experiment.class).setFetchMode("scenario", FetchMode.JOIN).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        try {
            transaction.commit();
            return allRecords;
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            transaction.rollback();
            return Collections.emptyList();
        } finally {
            session.close();
        }
    }

    /**
     * Getter of highest actual identifier + 1
     *
     * @return next primary key value
     */
    public int getNextAvailableId() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            return Integer.MAX_VALUE - (Integer) session.createCriteria(Experiment.class).setProjection(Projections.max("experimentId")).uniqueResult() + 1;
        }  finally {
            transaction.commit();
            session.close();
        }
    }


    public List getAdded() {
        String hql = "from Experiment e where e.added = true";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery(hql);
        try {
            if (query == null) {
                return Collections.<Experiment>emptyList();
            } else
                return query.list();
        } finally {
            transaction.commit();
            session.close();
        }
    }

    public List getChanged() {
        String hql = "from Experiment e where e.changed = true";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery(hql);
        try {
            if (query == null) {
                return Collections.<Experiment>emptyList();
            } else
                return query.list();
        } finally {
            transaction.commit();
            session.close();
        }
    }

//    @Override
//    public Experiment get(Integer identifier) throws DaoException {
//
//        String hql = "from Experiment e where e.experimentId = :identifier";
//
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = session.beginTransaction();
//        try {
//            return (Experiment) session.createQuery(hql).setInteger("identifier",identifier).uniqueResult();
//        } finally {
//            transaction.commit();
//            session.close();
//        }
//    }
}
