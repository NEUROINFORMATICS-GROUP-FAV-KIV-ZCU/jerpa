package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import org.junit.Ignore;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import java.io.File;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.prezentation.perspective.EDEDBPerspective;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Petr Miko
 */
public class EDEDBControllerTest {

    private EDEDBPerspective perspective;
    private EDEDSession session;
    private EDEDBController EDEDBController;

    /**
     * Test constructor, initializing needed variables.
     */
    public EDEDBControllerTest() {

        System.out.println("* EDEDB Test - EDEDBController - class setup hit");

        perspective = new EDEDBPerspective();
        session = new EDEDSession();
        EDEDBController = new EDEDBController(perspective, session);

    }

    /**
     * Test of getter/setter of rights handling.
     */
    @Test
    public void checkRights() {
        System.out.println("* EDEDB Test - EDEDBController - Rights check");
        EDEDBController.setRights(Rights.SUBJECT);
        assertTrue(EDEDBController.getRights() == Rights.SUBJECT);
    }

    /**
     * Test whether EDEDBController creates actions properly and doesn't return null.
     */
    @Test
    public void checkActions() {
        System.out.println("* EDEDB Test - EDEDBController - Actions hit");

        assertTrue(
                EDEDBController.getActionAnalyseSelected() != null
                && EDEDBController.getActionChooseDownloadFolder() != null
                && EDEDBController.getActionConnect() != null
                && EDEDBController.getActionDeleteSelected() != null
                && EDEDBController.getActionDisconnect() != null
                && EDEDBController.getActionDownloadSelected() != null
                && EDEDBController.getActionOpenDownloadPath() != null);
    }

    /**
     * Test of proper recognition of first start.
     */
    @Test
    public void checkFirstRun(){
        System.out.println("* EDEDB Test - EDEDBController - Check first run hit");
        String path = "pokus";
        File configFile = new File(EDEDBController.getConfigFilePath());
        
        if(configFile.exists()){
            if (EDEDBController.getDownloadPath() != null) {
                path = EDEDBController.getDownloadPath();
            }
            configFile.delete();
        }
        
        assertTrue(EDEDBController.isFirstRun());
        EDEDBController.setDownloadPath(path);
        assertFalse(EDEDBController.isFirstRun());
    }
    
    /**
     * Test of getter/setter download path.
     */
    @Test
    public void checkDownloadPath() {
        System.out.println("* EDEDB Test - EDEDBController - Download path change hit");

        String path = "pokus";
        if ((EDEDBController.getDownloadPath()) != null) {
            path = EDEDBController.getDownloadPath();
        }

        EDEDBController.setDownloadPath("pokus");
        assertTrue(EDEDBController.getDownloadPath().equals("pokus"));
        EDEDBController.setDownloadPath(path);
    }
    
    /**
     * Creating of nonsense file and checking whether it has a local copy.
     */
    @Test
    public void checkFilePresence(){
        System.out.println("* EDEDB Test - EDEDBController - Checking file presence hit");
        DataFileInfo tmp = new DataFileInfo();
        tmp.setExperimentId(Integer.MAX_VALUE);
        tmp.setFilename("'.[.[");
        tmp.setFileId(Integer.MAX_VALUE);
        tmp.setLength(Long.MAX_VALUE);
        tmp.setMimeType("mime/nonsense");
        tmp.setScenarioName("Knock knock, who's there?");
        
        assertTrue(DataRowModel.NO_LOCAL_COPY == EDEDBController.isAlreadyDownloaded(tmp));
    }    
}
