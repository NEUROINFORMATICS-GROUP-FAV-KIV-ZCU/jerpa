package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.Scenario;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Petr Miko
 *
 * DAO for Scenario manipulation.
 */
public class ScenarioDao extends GenericDao<Scenario, Integer> {

    public ScenarioDao(){
        super(Scenario.class);
    }

    @Override
    public Scenario get(Integer identifier) throws DaoException {

        String hql = "from Scenario s where s.scenarioId = :identifier";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            return (Scenario) session.createQuery(hql).setInteger("identifier",identifier).uniqueResult();
        } finally {
            transaction.commit();
            session.close();
        }
    }
}
