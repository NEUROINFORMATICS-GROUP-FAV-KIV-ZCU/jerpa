package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Petr Miko
 */
public class UserTableModelTest {

    private static UserTableModel tableModel;

    /**
     * Init method for test class
     * @throws Exception 
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        tableModel = new UserTableModel();

        System.out.print("* EDEDB - UserTableModel test");
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

        System.out.println("- data handling test - 4 rows (2 valid, 2 invalid), 2 error attempt to change data");

        addData();

        tableModel.addRow(new Object[]{"tt", null});
        tableModel.addRow(new Object[]{});
        //at Row3, the null is in not existing column, so row will be submitted
        assertEquals(3, tableModel.getRowCount());

        System.out.println("-- " + tableModel.getRowCount() + " rows added");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                assertNotNull(tableModel.getValueAt(i, j));
            }
        }

        System.out.println("-- " + tableModel.getRowCount() + " rows properly displayed");

        tableModel.setValueAt("sdffew", 0, 0);
        assertEquals("sdffew", tableModel.getValueAt(0, 0));
        
        tableModel.setValueAt(null, 0, 0);
        assertEquals("sdffew", tableModel.getValueAt(0, 0));

        System.out.println("-- table data consistency checked");

        System.out.println("- data handling checked");
    }

    /**
     * Non-testing method, for filling table purposes only.
     */
    private void addData() {
        if (tableModel.getRowCount() == 0) {

            tableModel.addRow("Row 1");
            tableModel.addRow("Row 2");
        }
    }
}
