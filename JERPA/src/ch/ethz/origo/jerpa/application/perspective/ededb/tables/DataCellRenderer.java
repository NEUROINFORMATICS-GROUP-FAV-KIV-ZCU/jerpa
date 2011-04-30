/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import java.awt.Color;
import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

/**
 * A cell renderer class used in Online Data Table.
 * It sets cell's background and forground in accordance to the isDownloaded variable.
 * @author Petr Miko
 */
public class DataCellRenderer extends JLabel implements TableCellRenderer, ILanguage {

    private String resourceBundlePath;
    private ResourceBundle resource;

    /**
     * Constructor method, sets opaquity to true.
     * @param model DataTableModel of Online Table
     */
    public DataCellRenderer() {
        this.setOpaque(true);

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
    }

    /**
     * Most important method of cell renderer. In accordance to incomming parameters,
     * cell is set up properly.
     * 
     * @param table JTable or JXTable of Data Table
     * @param value Content inside the cell
     * @param isSelected Boolean if is cell selected: true/false
     * @param hasFocus Boolean whether is cell clicked on: true/false
     * @param row Row index of selected cell
     * @param column Column index of selected cell
     * @return Properly set up cell
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            this.setForeground(table.getSelectionForeground());
            this.setBackground(table.getSelectionBackground());
        } else {
            if (column == DataTableModel.DOWNLOADED_COLUMN) {

                if (value.equals(resource.getString("table.ededb.datatable.state.yes"))) {
                    this.setBackground(Color.GREEN);
                } else if (value.equals(resource.getString("table.ededb.datatable.state.error"))) {
                    this.setBackground(Color.RED);
                } else if (value.equals(resource.getString("table.ededb.datatable.state.downloading"))) {
                    this.setBackground(Color.ORANGE);
                } else {
                    this.setBackground(table.getBackground());
                }

                this.setForeground(table.getForeground());
            } else {
                this.setBackground(table.getBackground());
                this.setForeground(table.getForeground());
            }
        }
        setText((value == null) ? "" : value.toString());

        return this;
    }

    /**
     * Setter of localization resource budle path
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource budle key.
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     * @throws JUIGLELangException
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            }
        });

    }
}
