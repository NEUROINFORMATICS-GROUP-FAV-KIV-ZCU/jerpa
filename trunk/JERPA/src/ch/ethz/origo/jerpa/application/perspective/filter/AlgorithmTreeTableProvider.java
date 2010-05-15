/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *  
 *    Copyright (C) 2009 - 2010 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.application.perspective.filter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.data.perspective.filter.AlgorithmRecord;
import ch.ethz.origo.jerpa.data.perspective.filter.AlgorithmTreeTableModel;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * Thiss class construct and display instance of <code>JXTreeTable</code> form
 * library <code>SwingX</code>. Table constains list of all method for EEG
 * processing.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.1 (3/14/2010)
 * @since 0.1.0 (3/13/2010)
 * @see ILanguage
 * @see IObserver
 * 
 */
public class AlgorithmTreeTableProvider implements ILanguage, IObserver {

	private JXTreeTable treeTable;
	private String resourceBundlePath;
	private ResourceBundle resourceBundle;
	private SignalProject currProject;

	boolean isProjectSend = false;

	public AlgorithmTreeTableProvider(JXTreeTable table, String resourceBundlePath) {
		this.treeTable = table;
		setLocalizedResourceBundle(resourceBundlePath);
		// attach to signal observable
		SignalPerspectiveObservable.getInstance().attach(this);
		// attach to language observable
		LanguageObservable.getInstance().attach((ILanguage) this);
		this.treeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					applyAlgorithm();
				}
			}
		});
	}

	/**
	 * This method is called, when user select method for EEG processing.
	 */
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

					SignalPerspectiveObservable.getInstance().setState(
							SignalPerspectiveObservable.MSG_SEND_CURRENT_PROJECT);
					// FIXME zvazit toto vlakno je nejspise zbytecne
					Thread algThread = new Thread(new Runnable() {
						@Override
						public void run() {
							while (!isProjectSend) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// FIXME Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					});
					algThread.start();
					try {
						algThread.join();
					} catch (InterruptedException e) {
						// FIXME Auto-generated catch block
						e.printStackTrace();
					}
					if (currProject != null) {
						myModel.getPlugEngineTreeTabModel().startPluggable(
								ar.getAlgorithmClass(), currProject);
					} else {
						JOptionPane.showMessageDialog(treeTable,
								getLocalizedText("table.dialog.algh.select.noproject"),
								getLocalizedText("table.dialog.error"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			} else {
				showWarningDialog();
			}
		}
	}

	private void showWarningDialog() {
		JOptionPane.showMessageDialog(treeTable,
				getLocalizedText("table.dialog.algh.select.no"),
				getLocalizedText("table.dialog.warning"), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourceBundlePath = path;
		this.resourceBundle = ResourceBundle.getBundle(resourceBundlePath);

	}

	/**
	 * {@inheritDoc} NOT IMPLEMENTED FOR THIS CLASS
	 */
	@Override
	public void setResourceBundleKey(String key) {
		/* NOT IMPLEMENTED */

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateText() throws JUIGLELangException {
		setLocalizedResourceBundle(getResourceBundlePath());
	}

	private String getLocalizedText(String key) {
		return resourceBundle.getString(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Object state) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(IObservable o, Object state) {
		if (o instanceof SignalPerspectiveObservable) {
			if (state instanceof SignalProject) {
				this.currProject = (SignalProject) state;
				System.out.println("project obdrzen!!!!!!1");
			}
			isProjectSend = true;
		}

	}

}