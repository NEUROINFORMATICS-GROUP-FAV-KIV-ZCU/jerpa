package ch.ethz.origo.jerpa.application.perspective.filter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import ch.ethz.origo.jerpa.data.perspective.filter.AlgorithmRecord;
import ch.ethz.origo.jerpa.data.perspective.filter.AlgorithmTreeTableModel;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/13/2010)
 * @since 0.1.0 (3/13/2010)
 * 
 */
public class AlgorithmTreeTableProvider implements ILanguage {

	private JXTreeTable treeTable;
	private String resourceBundlePath;
	private ResourceBundle resourceBundle;

	public AlgorithmTreeTableProvider(JXTreeTable table, String resourceBundlePath) {
		this.treeTable = table;
		setLocalizedResourceBundle(resourceBundlePath);
		LanguageObservable.getInstance().attach(this);
		this.treeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					applyAlgorithm();
				}
			}
		});
	}

	public void applyAlgorithm() {
		TreePath selp = treeTable.getTreeSelectionModel().getSelectionPath();
		if (selp == null) {
			showWarningDialog();
		} else {
			DefaultMutableTreeTableNode toBeApplyNode = (DefaultMutableTreeTableNode) selp
					.getLastPathComponent();
			if (toBeApplyNode.isLeaf()) {
				// FIXME osetrit jestlize to nebude instance AlgorithmRecord
				AlgorithmRecord ar = (AlgorithmRecord) toBeApplyNode.getUserObject();
				// be nice and ask about it
				int confirm = JOptionPane.showConfirmDialog(SwingUtilities
						.windowForComponent(treeTable),
						getLocalizedText("table.dialog.algh.select.confirm")
								+ ar.toString() + "?");
				if (confirm == JOptionPane.OK_OPTION) {
					AlgorithmTreeTableModel myModel = (AlgorithmTreeTableModel) treeTable
							.getTreeTableModel();
					myModel.getPlugEngineTreeTabModel().startPluggable(
							ar.getAlgorithmClass());
				}
			} else {
				showWarningDialog();
			}
		}
	}

	private void buildAlgorithm() {
		// TODO Auto-generated method stub

	}

	private void showWarningDialog() {
		JOptionPane.showMessageDialog(treeTable,
				getLocalizedText("table.dialog.algh.select.no"),
				getLocalizedText("table.dialog.warning"), JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourceBundlePath = path;
		this.resourceBundle = ResourceBundle.getBundle(resourceBundlePath);

	}

	@Override
	public void setResourceBundleKey(String key) {
		/* NOT IMPLEMENTED */

	}

	@Override
	public void updateText() throws JUIGLELangException {
		setLocalizedResourceBundle(getResourceBundlePath());
	}

	private String getLocalizedText(String key) {
		return resourceBundle.getString(key);
	}

}