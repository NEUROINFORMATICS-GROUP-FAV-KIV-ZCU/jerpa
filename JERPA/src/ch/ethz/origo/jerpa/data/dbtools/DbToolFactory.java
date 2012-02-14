package ch.ethz.origo.jerpa.data.dbtools;

/**
 * Factory class for getting specified DbTool instance.
 * @author Petr Miko
 */
public class DbToolFactory {

    public static DbTool getDerbyCreator(){
        return new DerbyTools();
    }
}
