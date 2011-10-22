package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import java.awt.Container;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXTable;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataCellRenderer;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataRowSorter;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ExpTableModel;

/**
 * Class for displaying experiments and its data in online mode.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class ExperimentViewer extends JSplitPane {

	private static final long serialVersionUID = -4454099765721853413L;

	protected ExpTableModel expModel;
	protected DataTableModel dataModel;
	protected JXTable expTable;
	protected JXTable dataTable;

	/**
	 * Constructor creating basic JSplitPane interface.
	 * 
	 * @param controller EDEDB EDEDBController class
	 * @param session EDEDSession session
	 */
	public ExperimentViewer() {
		super(JSplitPane.HORIZONTAL_SPLIT, true);

		setResizeWeight(.5d);

		setLeftComponent(createExpTable());
		setRightComponent(createDataTable());

		this.setDividerLocation(300 + this.getInsets().left);

	}

	/**
	 * Method creating Experiment JXTable within a JScrollPane.
	 * 
	 * @return Experiment view table
	 */
	private Container createExpTable() {
		expModel = new ExpTableModel();
		expTable = new JXTable(expModel);

		expTable.setAutoCreateRowSorter(true);
		expTable.setFillsViewportHeight(true);
		expTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		expTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		return new JScrollPane(expTable);
	}

	/**
	 * Method creating Data JXTable within a JScrollPane.
	 * 
	 * @return Experiment's data view table
	 */
	private Container createDataTable() {
		dataModel = new DataTableModel();
		dataTable = new JXTable(dataModel);
		dataTable.setDefaultRenderer(Object.class, new DataCellRenderer());

		dataTable.setRowSorter(new DataRowSorter(dataModel));
		dataTable.setFillsViewportHeight(true);
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		return new JScrollPane(dataTable);
	}
}
