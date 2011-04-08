package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import org.junit.BeforeClass;
import org.junit.Ignore;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowModel;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import java.io.File;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.prezentation.perspective.EDEDBPerspective;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Petr Miko
 */
public class EDEDBControllerTest {

    private static EDEDBPerspective perspective;
    private static EDEDClient session;
    private static EDEDBController controller;

    @BeforeClass
    public static void setupClass() throws Exception {

        perspective = new EDEDBPerspective();
        session = new EDEDClient();
        controller = new EDEDBController(perspective, session);

        System.out.println("* EDEDB - EDEDBController test");

    }

    /**
     * Test of getter/setter of rights handling.
     */
    @Test
    public void checkRights() {
        controller.setRights(Rights.SUBJECT);
        assertTrue(controller.getRights() == Rights.SUBJECT);
        System.out.println("- Rights checked");
    }

    /**
     * Test whether EDEDBController creates actions properly and doesn't return null.
     */
    @Test
    public void checkActions() {
        assertTrue(
                controller.getActionAnalyseSelected() != null
                && controller.getActionChooseDownloadFolder() != null
                && controller.getActionConnect() != null
                && controller.getActionDeleteSelected() != null
                && controller.getActionDisconnect() != null
                && controller.getActionDownloadSelected() != null
                && controller.getActionOpenDownloadPath() != null);
        System.out.println("- Actions checked");
    }

    /**
     * Test of proper recognition of first start.
     */
    @Test
    public void checkFirstRun() {

        File configFile = new File(controller.getConfigFilePath());
        
        if (configFile.exists()) {
            assertFalse(controller.isFirstRun());
        } else {
            assertTrue(controller.isFirstRun());
        }


        System.out.println("- First run checked");
    }

    /**
     * Test of getter/setter download path.
     */
    @Test
    public void checkDownloadPath() {
        String path = "pokus";
        if ((controller.getDownloadPath()) != null) {
            path = controller.getDownloadPath();
        }

        controller.setDownloadPath("pokus");
        assertTrue(controller.getDownloadPath().equals("pokus"));
        controller.setDownloadPath(path);

        System.out.println("- Download path change checked");
    }

    /**
     * Creating of nonsense file and checking whether it has a local copy.
     */
    @Test
    public void checkFilePresence() {

        DataFileInfo tmp = new DataFileInfo();
        tmp.setExperimentId(Integer.MAX_VALUE);
        tmp.setFilename("'.[.[");
        tmp.setFileId(Integer.MAX_VALUE);
        tmp.setLength(Long.MAX_VALUE);
        tmp.setMimeType("mime/nonsense");
        tmp.setScenarioName("Knock knock, who's there?");

        assertTrue(DataRowModel.NO_LOCAL_COPY == controller.isAlreadyDownloaded(tmp));

        System.out.println("- File presence checked");
    }

    @Test
    public void checkDownloading() {

        System.out.println("- Downloading count testing");

        controller.addDownloading(99);

        assertTrue(controller.isDownloading(99));
        assertFalse(controller.isDownloading(666));

        System.out.println("-- existence of file id downloading");

        controller.addDownloading(99);

        assertEquals(1, controller.getDownloadingSize());

        System.out.println("-- no duplicities");

        controller.removeDownloading(666);

        assertEquals(1, controller.getDownloadingSize());

        controller.removeDownloading(99);

        assertEquals(0, controller.getDownloadingSize());

        System.out.println("-- removing from downloading set");

        System.out.println("- Downloading count checked");
    }
}
