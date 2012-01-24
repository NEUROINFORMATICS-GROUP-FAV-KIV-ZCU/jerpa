package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for Experiment type manipulation.
 */
public class ExperimentDao extends GenericDao<Experiment, Integer> {

    public ExperimentDao() {
        super(Experiment.class);
    }

    public List<Experiment> getAll() {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<Experiment> allRecords = session.createCriteria(Experiment.class).setFetchMode("scenario", FetchMode.JOIN).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        transaction.commit();
        return allRecords;
    }

}
