/**
 * 
 */
package ch.ethz.origo.jerpa.data.perspective.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import nezarazeno.ClassFinder;
import ch.ethz.origo.juigle.application.exception.DataStoreException;
import ch.ethz.origo.juigle.data.tables.model.JUIGLETreeTableModel;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/26/09)
 * @since 0.1.0 (11/26/09)
 * @see JUIGLETreeTableModel
 * 
 */
public class FilterTreeTableModel extends JUIGLETreeTableModel {

	/** Only for serialization */
	private static final long serialVersionUID = -1729007145954323938L;

	private List<FilterRecord> values = new ArrayList<FilterRecord>();

	@Override
	public void fillByValues() throws DataStoreException {
		ClassFinder cf = new ClassFinder();
		Vector<Class<?>> listOfSubclasses = cf
				.findSubclasses("ch.ethz.origo.jerpa.data.filters.IFilter");
		for (Class<?> item : listOfSubclasses) {
			try {
				FilterRecord fr = new FilterRecord();
				Field author = item.getDeclaredField("AUTHOR");
				Field version = item.getDeclaredField("VERSION");
				Field name = item.getDeclaredField("NAME");
				author.setAccessible(true);
				version.setAccessible(true);
				name.setAccessible(true);
				fr.setAuthor((String) author.get(null));
				fr.setName((String) name.get(null));
				fr.setVersion((String) version.get(null));
				values.add(fr);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
/*		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FilterRecord());
		DefaultMutableTreeNode currentNameNode = null;
		String prevLast = "";
		String currentLast = "";
		for (FilterRecord fr : values) {
			currentLast = fr.getName();
			if (currentLast.equals(prevLast)) {
				currentNameNode.add(new DefaultMutableTreeNode(fr));
			} else {
				if (currentNameNode != null) {
					root.add(currentNameNode);
				}
				currentNameNode = new DefaultMutableTreeNode(new FilterRecord("", "",
						testPerson.getLastName(), ""));
				currentNameNode.add(new DefaultMutableTreeNode(fr));
				prevLast = currentLast;
			}
		}*/
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(Object node, int column) {
		Object res = "n/a";
		/*if (node instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode defNode = (DefaultMutableTreeNode) node;
			if (defNode.getUserObject() instanceof FilterRecord) {
				FilterRecord filter = (FilterRecord) defNode.getUserObject();
				switch (column) {
				case 0:
					res = filter.getName();
					break;
				case 1:
					res = filter.getVersion();
					break;
				case 2:
					res = filter.getAuthor();
					break;
				default:
					break;
				}
			}
		}*/
		return res;
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
	public String getColumnName(int column) {
		String name = "";
		switch (column) {
		case 0:
			name = "Name / Path";
			break;
		case 1:
			name = "Version";
			break;
		case 2:
			name = "Author";
			break;
		default:
			name = "NONAME";
			break;
		}
		return name;
	}

}