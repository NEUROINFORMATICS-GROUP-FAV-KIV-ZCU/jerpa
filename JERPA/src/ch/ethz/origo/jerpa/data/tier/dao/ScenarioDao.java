package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.pojo.Scenario;

/**
 * @author Petr Miko
 *
 * DAO for Scenario manipulation.
 */
public class ScenarioDao extends GenericDao<Scenario, Integer> {

    public ScenarioDao(){
        super(Scenario.class);
    }
}
