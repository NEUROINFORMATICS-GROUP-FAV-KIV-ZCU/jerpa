package ch.ethz.origo.jerpa.data.perspective.filter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.juigle.application.ClassFinder;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.plugin.IPluggable;
import ch.ethz.origo.juigle.plugin.PluginEngine;
import ch.ethz.origo.juigle.prezentation.tables.model.JUIGLETreeTableModel;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.3.3 (3/28/2010)
 * @since 0.1.0 (11/26/09)
 * @see JUIGLETreeTableModel
 * 
 */
public class AlgorithmTreeTableModel extends JUIGLETreeTableModel {

	/** Only for serialization */
	private static final long serialVersionUID = -1729007145954323938L;

	private static final int NUM_OF_COLUMNS = 4;

	private ResourceBundle resource;
	private String resourcePath;

	private PluginEngine plugEngine;

	/** Map of algorithms */
	private Map<String, AlgorithmRecord> treeOfFilters;

	public AlgorithmTreeTableModel(String resourceBundlePath) {
		setLocalizedResourceBundle(resourceBundlePath);
		LanguageObservable.getInstance().attach(this);
	}

	private List<IPluggable> getPlugins() {
		plugEngine = PluginEngine.getInstance();
		return plugEngine.getAllCorrectPluggables(JERPAUtils.PLUGIN_ALGORITHMS_KEY);
	}

	@Override
	public void fillByValues() {
		treeOfFilters = new TreeMap<String, AlgorithmRecord>();
		List<IPluggable> listOfPlugins = getPlugins();

		for (IPluggable plug : listOfPlugins) {
			AlgorithmRecord fr = new AlgorithmRecord();
			fr.setAuthor(plug.getAuthorName());
			fr.setName(plug.getPluginName());
			fr.setVersion(plug.getPluginVersion());
			fr.setCategory(plug.getCategory());
			fr.setBasicDescription(plug.getPluginBasicDescription());
			fr.setAlgorithmClass(plug);
			treeOfFilters.put(plug.getPluginName(), fr);
		}

		String prevLast = "";
		String currentLast = "";

		FilterTreeTableNode currentNameNode = null;
		DefaultMutableTreeTableNode root = new FilterTreeTableNode(
				new AlgorithmRecord());
		Set<Entry<String, AlgorithmRecord>> map = treeOfFilters.entrySet();
		for (Entry<String, AlgorithmRecord> item : map) {
			currentLast = item.getValue().getCategory();
			if (currentLast.equals(prevLast)) {
				currentNameNode.add(new FilterTreeTableNode(item.getValue()));
			} else {
				if (currentNameNode != null) {
					root.add(currentNameNode);
				}
				AlgorithmRecord fr = new AlgorithmRecord();
				fr.setCategory(currentLast);
				currentNameNode = new FilterTreeTableNode(fr);
				currentNameNode.add(new FilterTreeTableNode(item.getValue()));
				prevLast = currentLast;
				root.add(currentNameNode);
			}
		}
		setRoot(root);
	}

	/**
	 * Return instance of current <code>PlugEngine</code> for this algorithm tree
	 * table model.
	 * 
	 * @return instance of current <code>PlugEngine</code> for this algorithm tree
	 *         table model.
	 */
	public PluginEngine getPlugEngineTreeTabModel() {
		return plugEngine;
	}

	@Deprecated
	private void fillValues() {
		// treeOfFilters = new TreeSet<AlgorithmRecord>();
		ClassFinder cf = new ClassFinder();
		Vector<Class<?>> listOfSubclasses = cf
				.findSubclasses("ch.ethz.origo.jerpa.data.algorithms.IAlgorithm");
		for (Class<?> item : listOfSubclasses) {
			try {
				AlgorithmRecord fr = new AlgorithmRecord();
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
				// fr.setAlgorithmClass(item);
				// treeOfFilters.add(fr);
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
	}

	@Override
	public int getColumnCount() {
		return AlgorithmTreeTableModel.NUM_OF_COLUMNS;
	}

	@Override
	public String getColumnName(int column) {
		String name = "";
		switch (column) {
		case 0:
			name = resource.getString("table.column.name");
			break;
		case 1:
			name = resource.getString("table.column.version");
			break;
		case 2:
			name = resource.getString("table.column.author");
			break;
		case 3:
			name = resource.getString("table.column.basic.desc");
			break;
		default:
			name = "n/a";
			break;
		}
		return name;
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

		public FilterTreeTableNode(AlgorithmRecord record) {
			super(record);
		}

		@Override
		public Object getValueAt(int column) {
			Object toBeDisplayed = "n/a";
			if (getUserObject() instanceof AlgorithmRecord) {
				AlgorithmRecord fr = (AlgorithmRecord) getUserObject();
				switch (column) {
				case 0:
					toBeDisplayed = fr.getName();
					break;
				case 1:
					toBeDisplayed = fr.getVersion();
					break;
				case 2:
					toBeDisplayed = fr.getAuthor();
					break;
				case 3:
					if (fr.getBasicDescription() != null) {
						toBeDisplayed = fr.getBasicDescription();
					} else {
						toBeDisplayed = "";
					}
				default:
					break;
				}
			}
			return toBeDisplayed;
		}

		@Override
		public int getColumnCount() {
			return AlgorithmTreeTableModel.NUM_OF_COLUMNS;
		}

	}

	@Override
	public String getResourceBundlePath() {
		return resourcePath;
	}

	@Override
	public void setLocalizedResourceBundle(String path) {
		resourcePath = path;
		resource = ResourceBundle.getBundle(path);
	}

	@Override
	public void setResourceBundleKey(String key) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setLocalizedResourceBundle(getResourceBundlePath());
			}
		});
	}

}