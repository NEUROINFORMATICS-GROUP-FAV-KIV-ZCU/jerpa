package ch.ethz.origo.jerpa.data.dbtools;

/**
 * Interface a db creator.
 * @author Petr Miko
 */
public interface DbTool {

    public void createDb();

    public void rebuildDb();

    public void removeDb();

    public boolean dbExists();
}
