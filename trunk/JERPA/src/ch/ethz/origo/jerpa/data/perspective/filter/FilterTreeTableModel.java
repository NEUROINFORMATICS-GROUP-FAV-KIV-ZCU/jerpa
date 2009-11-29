/**
 * 
 */
package ch.ethz.origo.jerpa.data.perspective.filter;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import ch.ethz.origo.juigle.application.exception.DataStoreException;
import ch.ethz.origo.juigle.data.tables.model.TreeTableModel;

/**
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/26/09)
 * @since 0.1.0 (11/26/09)
 * @see TreeTableModel
 *
 */
public class FilterTreeTableModel extends TreeTableModel {

	
	@Override
	public void fillByValues() throws DataStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getColumnName(int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getHierarchicalColumn() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValueAt(Object node, int column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getChild(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildCount(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLeaf(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}