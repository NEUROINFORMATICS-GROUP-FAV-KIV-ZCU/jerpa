package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.Hardware;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for Hardware type.
 */
public final class HardwareDao extends GenericDao<Hardware, Integer> {

    private static HardwareDao instance = new HardwareDao();

    /**
     * Getter of HardwareDao instance.
     * @return dao instance
     */
    public static HardwareDao getInstance(){
        return instance;
    }

    private HardwareDao() {
        super(Hardware.class);
    }

    @Override
    public Hardware get(Integer identifier) throws DaoException {

        String hql = "from Hardware h where h.hardwareId = :identifier";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            return (Hardware) session.createQuery(hql).setInteger("identifier",identifier).uniqueResult();
        } finally {
            transaction.commit();
            session.close();
        }
    }
}
