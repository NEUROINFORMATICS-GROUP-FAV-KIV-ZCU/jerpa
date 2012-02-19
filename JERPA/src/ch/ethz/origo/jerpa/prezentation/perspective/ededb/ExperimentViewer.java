package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.DataTableModel;
import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ExpTableModel;

import javax.swing.*;
import java.awt.*;

/**
 * Class for displaying experiments and its data.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class ExperimentViewer extends JSplitPane {

	private static final long serialVersionUID = -4454099765721853413L;

    protected ExpTableModel expModel;
    protected DataTableModel dataModel;
	protected ExperimentsTable expTable;
	protected DataFilesTable dataTable;

	/**
	 * Constructor creating basic JSplitPane interface.
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
		expTable = new ExperimentsTable(expModel);
		return new JScrollPane(expTable);
	}

	/**
	 * Method creating Data JXTable within a JScrollPane.
	 * 
	 * @return Experiment's data view table
	 */
	private Container createDataTable() {
		dataModel = new DataTableModel();
		dataTable = new DataFilesTable(dataModel);
		return new JScrollPane(dataTable);
	}
}
