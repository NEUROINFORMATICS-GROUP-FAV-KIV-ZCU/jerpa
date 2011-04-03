package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import ch.ethz.origo.jerpa.ededclient.generated.DataFileInfo;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Petr Miko
 */
public class DataTableModelTest {

    private static DataTableModel tableModel;
    private DataFileInfo info;

    /**
     * Init method for test class
     * @throws Exception 
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        tableModel = new DataTableModel();

        System.out.print("* EDEDB - DataTableModel test");
    }

    /**
     * Testing whether column classes are right.
     * First column must be boolean in order to display JCheckBox.
     */
    @Test
    public void getColumnClass() {
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            if (i == 0) {
                assertEquals(tableModel.getColumnClass(0), Boolean.class);
            } else {
                assertEquals(tableModel.getColumnClass(i), String.class);
            }
        }
        System.out.println("- column class type checked");
    }

    /**
     * Testing whether model returns not null column names.
     */
    @Test
    public void columnCountAndNames() {

        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            assertTrue(tableModel.getColumnName(i) != null);
        }

        System.out.println("- column names not null in " + tableModel.getColumnCount()
                + " cases - checked");
    }
    
    /**
     * Adding data and clearing table afterwards. Checking if it's empty.
     */
    @Test
    public void clearData() {

        addData();
        tableModel.clear();
        assertEquals(tableModel.getData().size(), 0);
        System.out.println("- clearing data checked");

    }

    /**
     * Checking editability of JTable cells.
     */
    @Test
    public void dataEditability() {
        
        addData();
        for (int i= 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++){
                if(j == DataTableModel.ACTION_COLUMN)
                    assertTrue(tableModel.isCellEditable(i, j));
                else
                    assertFalse(tableModel.isCellEditable(i, j));
            }
        }
        tableModel.clear();
        System.out.println("- data editability checked");
    }

    /**
     * This test consists of three parts.
     * 1) adding data + attempt to add wrong format of data
     * 2) testing if table contains nulls
     * 3) attempts to change table cell to wrong contents
     */
    @Test
    public void dataHandling() {

        System.out.println("- data handling test - 4 rows (2 valid, 2 invalid), 4 error attempts to change data");

        addData();
        
        tableModel.addRow(info, DataRowModel.ERROR, null);
        tableModel.addRow(null, 99, "/null");

        assertEquals(2, tableModel.getRowCount());

        System.out.println("-- " + tableModel.getRowCount() + " rows added");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                if (i == 0 && j == 4) {
                    if (!tableModel.getValueAt(i, j).equals("No")
                            && !tableModel.getValueAt(i, j).equals("Ne")) {
                        fail();
                    }
                }
                if (i == 1 && j == 4) {
                    if (!tableModel.getValueAt(i, j).equals("Yes")
                            && !tableModel.getValueAt(i, j).equals("Ano")) {
                        fail();
                    }
                }
                assertNotNull(tableModel.getValueAt(i, j));
            }
        }

        System.out.println("-- " + tableModel.getRowCount() + " rows properly displayed");

        tableModel.setValueAt(Boolean.TRUE, 0, DataTableModel.ACTION_COLUMN);
        assertTrue((Boolean) tableModel.getValueAt(0, DataTableModel.ACTION_COLUMN));

        tableModel.setValueAt("nonsense", 0, DataTableModel.ACTION_COLUMN);
        assertTrue((Boolean) tableModel.getValueAt(0, DataTableModel.ACTION_COLUMN));

        String temp = (String) tableModel.getValueAt(0, DataTableModel.NAME_COLUMN);

        //this is ok, because only action/download status can be changed after insertio into table
        tableModel.setValueAt("pokus", 0, DataTableModel.NAME_COLUMN);
        assertEquals(temp, tableModel.getValueAt(0, DataTableModel.NAME_COLUMN));

        temp = (String) tableModel.getValueAt(0, DataTableModel.DOWNLOADED_COLUMN);

        tableModel.setValueAt(temp, 0, DataTableModel.DOWNLOADED_COLUMN);
        assertEquals(tableModel.getValueAt(0, DataTableModel.DOWNLOADED_COLUMN), temp);

        tableModel.setValueAt(null, 0, DataTableModel.DOWNLOADED_COLUMN);
        assertEquals(tableModel.getValueAt(0, DataTableModel.DOWNLOADED_COLUMN), temp);

        tableModel.setValueAt("tri sta tricet tri stribrnych strikacek", 0, DataTableModel.DOWNLOADED_COLUMN);
        assertEquals(tableModel.getValueAt(0, DataTableModel.DOWNLOADED_COLUMN), temp);

        System.out.println("-- table data consistency checked");

        System.out.println("- data handling checked");
    }

    private void addData() {

        if (tableModel.getRowCount() == 0) {
            DataFileInfo info = new DataFileInfo();

            info.setExperimentId(Integer.MAX_VALUE);
            info.setFileId(Integer.MAX_VALUE);
            info.setFilename("nonsense");
            info.setLength(Long.MAX_VALUE);
            info.setMimeType("nonsense");
            info.setScenarioName("One ring to rule them all");

            tableModel.addRow(info, DataRowModel.NO_LOCAL_COPY, "/tmp");
            tableModel.addRow(info, DataRowModel.HAS_LOCAL_COPY, "C:\\temp\\kernel.sys");
        }
    }
    
    /**
     * Non-testing method, for filling table purposes only.
     */
    @Test
    public void fileSizeComputation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        Method countFileSize = DataTableModel.class.getDeclaredMethod("countFileSize", long.class);
        countFileSize.setAccessible(true);
        String output = (String) countFileSize.invoke(tableModel, 1024);
        assertEquals(output, "1,0 KiB");
        
        System.out.println("- file size computation checked");
    }
}
