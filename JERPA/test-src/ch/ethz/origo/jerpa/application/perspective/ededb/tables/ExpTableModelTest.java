package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.jerpa.ededclient.generated.ExperimentInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Petr Miko
 */
public class ExpTableModelTest {

    private static ExpTableModel tableModel;
    private ExperimentInfo info;

    /**
     * Init method for test class
     * @throws Exception 
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        tableModel = new ExpTableModel();

        System.out.print("* EDEDB - ExpTableModel test");
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
        assertEquals(tableModel.getRowCount(), 0);
        System.out.println("- clearing data checked");

    }

    /**
     * Checking editability of JTable cells.
     */
    @Test
    public void dataEditability() {

        addData();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
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

        System.out.println("- data handling test - 4 rows (2 valid, 2 invalid), 1 error attempt to change data");

        addData();
        
        info = new ExperimentInfo();
        info.setExperimentId(Integer.MAX_VALUE);
        info.setScenarioId(666);
        info.setScenarioName(null);

        tableModel.addRow(info);

        tableModel.addRow(null);

        assertEquals(2, tableModel.getRowCount());

        System.out.println("-- " + tableModel.getRowCount() + " rows added");
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                if (i == 0 && j == ExpTableModel.NAME_COLUMN) {
                    if (!tableModel.getValueAt(i, j).equals("nonsense")) {
                        fail("Wrong content!");
                    }
                }
                if (i == 1 && j == ExpTableModel.ID_COLUMN) {
                    if (!tableModel.getValueAt(i, j).equals(Integer.MIN_VALUE)) {
                        fail();
                    }
                }
                assertNotNull(tableModel.getValueAt(i, j));
            }
        }

        System.out.println("-- " + tableModel.getRowCount() + " rows properly displayed");
        
        tableModel.setValueAt("sdffew", 0, ExpTableModel.ID_COLUMN);
        
        assertNotSame("sdffew",tableModel.getValueAt(0, ExpTableModel.ID_COLUMN));
        assertEquals(Integer.MAX_VALUE, tableModel.getValueAt(0, ExpTableModel.ID_COLUMN));
        
        System.out.println("-- table data consistency checked");
        
        System.out.println("- data handling checked");
    }
    
    /**
     * Non-testing method, for filling table purposes only.
     */
    private void addData() {
        if (tableModel.getRowCount() == 0) {
            info = new ExperimentInfo();
            info.setExperimentId(Integer.MAX_VALUE);
            info.setScenarioId(Integer.MAX_VALUE);
            info.setScenarioName("nonsense");
            
            tableModel.addRow(info);

            info = new ExperimentInfo();
            info.setExperimentId(Integer.MIN_VALUE);
            info.setScenarioId(Integer.MIN_VALUE);
            info.setScenarioName("tum tum tum tum-tu-tum tum-tu-tum...");

            tableModel.addRow(info);
        }
    }
}
