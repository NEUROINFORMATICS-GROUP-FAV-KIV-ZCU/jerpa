package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.Person;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for manipulation with Person types.
 */
public final class PersonDao extends GenericDao<Person, Integer> {

    private static PersonDao instance = new PersonDao();

    /**
     * Getter of PersonDao instance.
     * @return dao instance
     */
    public static PersonDao getInstance(){
        return instance;
    }

    private PersonDao(){
        super(Person.class);
    }

    @Override
    public Person get(Integer identifier) throws DaoException {

        String hql = "from Person p where p.personId = :identifier";

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            return (Person) session.createQuery(hql).setInteger("identifier",identifier).uniqueResult();
        } finally {
            transaction.commit();
            session.close();
        }
    }

}
