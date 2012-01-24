package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.pojo.Person;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for manipulation with Person types.
 */
public class PersonDao extends GenericDao<Person, Integer> {

    public PersonDao(){
        super(Person.class);
    }

}
