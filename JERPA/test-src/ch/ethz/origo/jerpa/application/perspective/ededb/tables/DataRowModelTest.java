package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Petr Miko
 */
public class DataRowModelTest {

    private static DataRowModel rowModel;
    private static DataFileInfo info;

    /**
     * Init class for testing
     * @throws Exception 
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        info = new DataFileInfo();
        info.setExperimentId(Integer.MAX_VALUE);
        info.setFileId(Integer.MAX_VALUE);
        info.setFilename("nonsense");
        info.setLength(Long.MAX_VALUE);
        info.setMimeType("nonsense");
        info.setScenarioName("nonsense");

        rowModel = new DataRowModel(info, DataRowModel.NO_LOCAL_COPY, "/nonsense");

        System.out.print("* EDEDB - DataRowModel test");
    }

    /**
     * Testing is/get of selected variable
     */
    @Test
    public void selected() {

        rowModel.setSelected(true);
        assertEquals(rowModel.isSelected(), true);

        System.out.println("- selected is/set checked");
    }

    /**
     * Testing getter of file info
     */
    @Test
    public void getFileInfo() {

        assertEquals(info, rowModel.getDataFile());
        System.out.println("- get file info checked");
    }

    /**
     * Testing get/set of downloaded variable
     */
    @Test
    public void downloaded() {
        assertNotSame(rowModel.getDownloaded(), DataRowModel.HAS_LOCAL_COPY);
        rowModel.setDownloaded(DataRowModel.DOWNLOADING);
        assertEquals(rowModel.getDownloaded(), DataRowModel.DOWNLOADING);
        
        System.out.print("- get/set downloaded checked");
    }
    
    /**
     * Testing of getter extension.
     */
    @Test
    public void getExtension(){
        assertEquals(rowModel.getExtension(),"nonsense");
        
        info.setFilename("pokus.tmp");
        rowModel = new DataRowModel(info,DataRowModel.ERROR,"/tmp");
        
        assertEquals(rowModel.getExtension(),"tmp");
        
        System.out.println("- get extension checked");
    }
    
    /**
     * Testing generating of extension variable.
     */
    @Test
    public void getLocation(){
        rowModel = new DataRowModel(info, DataRowModel.ERROR, "/tmp");
        assertEquals(rowModel.getLocation(), "/tmp");
        rowModel = new DataRowModel(info, DataRowModel.ERROR, "/new/location");
        assertNotSame(rowModel.getLocation(), "/tmp");
        assertEquals(rowModel.getLocation(), "/new/location");
        
        System.out.println("- get location checked");
    }
    
    
}
