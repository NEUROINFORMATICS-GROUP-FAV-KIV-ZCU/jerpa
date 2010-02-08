package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.BorderLayout;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultTreeCellRenderer;

import noname.JERPAUtils;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import ch.ethz.origo.jerpa.data.perspective.filter.FilterRecord;
import ch.ethz.origo.jerpa.data.perspective.filter.FilterTreeTableModel;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.data.ClassFinder;
import ch.ethz.origo.juigle.data.tables.model.JUIGLETreeTableModel;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;
import ch.ethz.origo.juigle.prezentation.tables.JUIGLETreeTable;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 08/14/09
 * @since 0.1.0 (05/18/09)
 * @see Perspective
 * @see IObserver
 */
public class FilterPerspective extends Perspective implements IObserver {

	/** Only for serialization */
	private static final long serialVersionUID = -3671126854840073832L;

	private static String resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.filter.FilterPerspective";
	
	public FilterPerspective() {
		perspectiveObservable.attach(this);
		
	}
	
	@Override
	public String getTitle() {
		return resource.getString(getRBPerspectiveTitleKey());
	}
	
	@Override
	public String getRBPerspectiveTitleKey() {
		return "perspective.title";
	}
	
	@Override
	public void initPerspectiveMenuPanel() throws PerspectiveException {
		if (menuTaskPane == null) {
			menuTaskPane = new JXTaskPane();
			menuTaskPane.setOpaque(false);
			// initalize menu
			menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP, resourcePath);
			menu.setFloatable(false);
			menu.setRollover(true);
			// initialize and add menu items
			initAndAddMenuItems();
			try {
				menu.addMenuSeparator();
			} catch (JUIGLEMenuException e) {
				throw new PerspectiveException(e);
			}
			menu.addHeaderHideButton(false);
			menu.addFooterHideButton(false);
			
			menuTaskPane.add(menu);
		}
	}
	
	@Override
	public void initPerspectivePanel() throws PerspectiveException {
		super.initPerspectivePanel();
		mainPanel.setLayout(new BorderLayout());
		JUIGLETreeTableModel ttm = new FilterTreeTableModel(fillByValues());
		/*try {
			ttm.fillByValues();
		} catch (DataStoreException e) {
			throw new PerspectiveException(e);
		}*/
		JUIGLETreeTable table = new JUIGLETreeTable(ttm);
		table.setEditable(false);
		table.setTreeCellRenderer(new DefaultTreeCellRenderer() {
			/** Only for serialization */
			private static final long serialVersionUID = 1L;

			public java.awt.Component getTreeCellRendererComponent(
					javax.swing.JTree tree, Object value, boolean sel,
					boolean expanded, boolean leaf, int row,
					boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel,
						expanded, leaf, row, hasFocus);
				if (value instanceof DefaultMutableTreeTableNode) {
					DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) value;
					FilterRecord fr = (FilterRecord) node
							.getUserObject();
					setText(fr.getName());
					if (node.isLeaf()
							&& node.getParent() == tree.getModel()
									.getRoot()) {
						setIcon(getDefaultClosedIcon());
					}
				}

				return this;
			};
		});
		JScrollPane sp = new JScrollPane(table);
		mainPanel.add(sp, BorderLayout.CENTER);
	}
	
	//@Override
	public DefaultMutableTreeTableNode fillByValues() {
		Set<FilterRecord> treeOfFilters = new TreeSet<FilterRecord>();
		ClassFinder cf = new ClassFinder();
		Vector<Class<?>> listOfSubclasses = cf
				.findSubclasses("ch.ethz.origo.jerpa.data.filters.IAlgorithmDescriptor");
		for (Class<?> item : listOfSubclasses) {
			try {
				FilterRecord fr = new FilterRecord();
				Field author = item.getDeclaredField("AUTHOR");
				Field version = item.getDeclaredField("VERSION");
				Field name = item.getDeclaredField("NAME");
				Field category = item.getDeclaredField("CATEGORY");
				author.setAccessible(true);
				version.setAccessible(true);
				name.setAccessible(true);
				category.setAccessible(true);
				fr.setAuthor((String) author.get(null));
				fr.setName((String) name.get(null));
				fr.setVersion((String) version.get(null));
				fr.setCategory((String) category.get(null));
				treeOfFilters.add(fr);
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
		String prevLast = "";
		String currentLast = "";

		FilterTreeTableNode currentNameNode = null;
		DefaultMutableTreeTableNode root = new FilterTreeTableNode(
				new FilterRecord());
		for (FilterRecord item : treeOfFilters) {
			currentLast = item.getCategory();
			if (currentLast.equals(prevLast)) {
				currentNameNode.add(new FilterTreeTableNode(item));
			} else {
				if (currentNameNode != null) {
					root.add(currentNameNode);
				}
				FilterRecord fr = new FilterRecord();
				fr.setCategory(item.getCategory());
				currentNameNode = new FilterTreeTableNode(fr);
				currentNameNode.add(new FilterTreeTableNode(item));
				prevLast = currentLast;
				root.add(currentNameNode);
			}
		}
		return root;
	}

	
	@Override
	public String getResourceBundlePath() {
		return FilterPerspective.resourcePath;
	}
	
	public Icon getIcon() throws PerspectiveException {
		return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "filterPerspectiveIcon.png", 32, 32);
	}
	

	private void initAndAddMenuItems() {
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Object state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(IObservable o, Object state) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @author Vaclav Souhrada
	 * @version 0.1.0 (7/08/2010)
	 * @since 0.2.0 (7/08/2010)
	 * @see DefaultMutableTreeTableNode
	 * 
	 */
	class FilterTreeTableNode extends DefaultMutableTreeTableNode {

		public FilterTreeTableNode(FilterRecord record) {
			super(record);
		}

		@Override
		public Object getValueAt(int column) {
			Object toBeDisplayed = "nn";

			return toBeDisplayed;
		}
		
		@Override
		public int getColumnCount() {
			return 3;
		}
		
		
	}
		
}
