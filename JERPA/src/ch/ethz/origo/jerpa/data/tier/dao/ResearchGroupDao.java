package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.ResearchGroup;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for ResearchGroup type.
 */
public class ResearchGroupDao extends GenericDao<ResearchGroup, Integer> {

    public ResearchGroupDao() {
        super(ResearchGroup.class);
    }

    @Override
    public ResearchGroup get(Integer identifier) throws DaoException {

        String hql = "from ResearchGroup  r where r.researchGroupId = :identifier";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            return (ResearchGroup) session.createQuery(hql).setInteger("identifier",identifier).uniqueResult();
        } finally {
            transaction.commit();
            session.close();
        }
    }
}
