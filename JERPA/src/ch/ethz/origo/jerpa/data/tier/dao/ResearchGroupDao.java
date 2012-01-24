package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.pojo.ResearchGroup;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for ResearchGroup type.
 */
public class ResearchGroupDao extends GenericDao<ResearchGroup, Integer> {

    public ResearchGroupDao() {
        super(ResearchGroup.class);
    }
}
