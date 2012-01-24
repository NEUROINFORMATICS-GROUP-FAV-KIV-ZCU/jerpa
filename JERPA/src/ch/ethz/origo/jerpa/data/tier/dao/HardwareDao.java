package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.pojo.Hardware;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for Hardware type.
 */
public class HardwareDao extends GenericDao<Hardware, Integer> {

    public HardwareDao() {
        super(Hardware.class);
    }
}
