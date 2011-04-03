package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import org.junit.BeforeClass;
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

    private static EDEDBPerspective perspective;
    private static EDEDSession session;
    private static EDEDBController EDEDBController;

    @BeforeClass
    public static void setupClass() throws Exception {

        perspective = new EDEDBPerspective();
        session = new EDEDSession();
        EDEDBController = new EDEDBController(perspective, session);
        
        System.out.println("* EDEDB - EDEDBController test");

    }

    /**
     * Test of getter/setter of rights handling.
     */
    @Test
    public void checkRights() {
        EDEDBController.setRights(Rights.SUBJECT);
        assertTrue(EDEDBController.getRights() == Rights.SUBJECT);
        System.out.println("- Rights checked");
    }

    /**
     * Test whether EDEDBController creates actions properly and doesn't return null.
     */
    @Test
    public void checkActions() {
        assertTrue(
                EDEDBController.getActionAnalyseSelected() != null
                && EDEDBController.getActionChooseDownloadFolder() != null
                && EDEDBController.getActionConnect() != null
                && EDEDBController.getActionDeleteSelected() != null
                && EDEDBController.getActionDisconnect() != null
                && EDEDBController.getActionDownloadSelected() != null
                && EDEDBController.getActionOpenDownloadPath() != null);
        System.out.println("- Actions checked");
    }

    /**
     * Test of proper recognition of first start.
     */
    @Test
    public void checkFirstRun(){
        
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
        
        System.out.println("- First run checked");
    }
    
    /**
     * Test of getter/setter download path.
     */
    @Test
    public void checkDownloadPath() {
        String path = "pokus";
        if ((EDEDBController.getDownloadPath()) != null) {
            path = EDEDBController.getDownloadPath();
        }

        EDEDBController.setDownloadPath("pokus");
        assertTrue(EDEDBController.getDownloadPath().equals("pokus"));
        EDEDBController.setDownloadPath(path);
        
        System.out.println("- Download path change checked");
    }
    
    /**
     * Creating of nonsense file and checking whether it has a local copy.
     */
    @Test
    public void checkFilePresence(){
        
        DataFileInfo tmp = new DataFileInfo();
        tmp.setExperimentId(Integer.MAX_VALUE);
        tmp.setFilename("'.[.[");
        tmp.setFileId(Integer.MAX_VALUE);
        tmp.setLength(Long.MAX_VALUE);
        tmp.setMimeType("mime/nonsense");
        tmp.setScenarioName("Knock knock, who's there?");
        
        assertTrue(DataRowModel.NO_LOCAL_COPY == EDEDBController.isAlreadyDownloaded(tmp));
        
        System.out.println("- File presence checked");
    }    
}
