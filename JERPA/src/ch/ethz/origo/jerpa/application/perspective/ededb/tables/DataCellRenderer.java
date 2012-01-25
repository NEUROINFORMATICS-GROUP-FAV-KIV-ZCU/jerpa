/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.origo.jerpa.application.perspective.ededb.tables;

import java.awt.Color;
import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * A cell renderer class used in Online Data Table. It sets cell's background
 * and foreground in accordance to the isDownloaded variable.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class DataCellRenderer extends JLabel implements TableCellRenderer, ILanguage {

	private static final long serialVersionUID = -2963552666207764494L;
	private String resourceBundlePath;
	private ResourceBundle resource;

	/**
	 * Constructor method, sets opaquity to true.
	 */
	public DataCellRenderer() {
		setOpaque(true);

		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");
	}

	/**
	 * Most important method of cell renderer. In accordance to incoming
	 * parameters, cell is set up properly.
	 * 
	 * @param table JTable or JXTable of Data Table
	 * @param value Content inside the cell
	 * @param isSelected Boolean if is cell selected: true/false
	 * @param hasFocus Boolean whether is cell clicked on: true/false
	 * @param row Row index of selected cell
	 * @param column Column index of selected cell
	 * @return Properly set up cell
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		Color background = table.getBackground();

		if (column == DataTableModel.DOWNLOADED_COLUMN) {
			if (FileState.HAS_COPY.equals(table.getModel().getValueAt(table.getRowSorter().convertRowIndexToModel(row), column))) {
				value = resource.getString("table.ededb.datatable.state.yes");
				background = Color.GREEN;
			}
			else if (FileState.CORRUPTED.equals(table.getModel().getValueAt(table.getRowSorter().convertRowIndexToModel(row), column))) {
				value = resource.getString("table.ededb.datatable.state.error");
				background = Color.RED;
			}
			else if (FileState.DOWNLOADING.equals(table.getModel().getValueAt(table.getRowSorter().convertRowIndexToModel(row), column))) {
				value = resource.getString("table.ededb.datatable.state.downloading");
				background = Color.ORANGE;
			}
			else {
				value = resource.getString("table.ededb.datatable.state.no");
			}
		}

		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		}
		else {
			setBackground(background);
			setForeground(table.getForeground());
		}

		setText((value == null) ? "" : value.toString());

		return this;
	}

	/**
	 * Setter of localization resource bundle path
	 * 
	 * @param path path to localization source file.
	 */
	public void setLocalizedResourceBundle(String path) {
		resourceBundlePath = path;
		resource = ResourceBundle.getBundle(path);
	}

	/**
	 * Getter of path to resource bundle.
	 * 
	 * @return path to localization file.
	 */
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	/**
	 * Setter of resource bundle key.
	 * 
	 * @param string key
	 */
	public void setResourceBundleKey(String string) {
		throw new UnsupportedOperationException("Method is not implemented yet...");
	}

	/**
	 * Method invoked by change of LanguageObservable.
	 * 
	 * @throws JUIGLELangException
	 */
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {}
		});

	}
}
