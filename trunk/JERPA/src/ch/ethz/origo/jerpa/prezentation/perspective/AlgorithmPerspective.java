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
package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import ch.ethz.origo.jerpa.application.perspective.filter.AlgorithmTreeTableProvider;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.data.perspective.filter.AlgorithmRecord;
import ch.ethz.origo.jerpa.data.perspective.filter.AlgorithmTreeTableModel;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.exception.DataStoreException;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;
import ch.ethz.origo.juigle.prezentation.tables.JUIGLETreeTable;
import ch.ethz.origo.juigle.prezentation.tables.model.JUIGLETreeTableModel;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.3 (3/13/2010)
 * @since 0.1.0 (05/18/09)
 * @see Perspective
 * @see IObserver
 */
public class AlgorithmPerspective extends Perspective implements IObserver {

	/** Only for serialization */
	private static final long serialVersionUID = -3671126854840073832L;

	private JUIGLEMenuItem algorithmMenu;
	private JUIGLEMenuItem applyAlgItem;
	
	private AlgorithmTreeTableProvider attp;

	/**
	 * Construct algorithm perspective. Attach perspective to perspective
	 * observable and set up resource bundle path for localization.
	 */
	public AlgorithmPerspective() {
		perspectiveObservable.attach(this);
		resourcePath = LangUtils
				.getPerspectiveLangPathProp("perspective.algorithmmanager.lang");
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
			menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP,
					resourcePath);
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
		JUIGLETreeTableModel ttm = new AlgorithmTreeTableModel(resourcePath);
		try {
			ttm.fillByValues();
		} catch (DataStoreException e) {
			throw new PerspectiveException(e);
		}
		JUIGLETreeTable table = new JUIGLETreeTable(ttm);
		table.setEditable(false);
		table.setTreeCellRenderer(new DefaultTreeCellRenderer() {
			/** Only for serialization */
			private static final long serialVersionUID = 1L;

			public java.awt.Component getTreeCellRendererComponent(
					javax.swing.JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
						row, hasFocus);
				if (value instanceof DefaultMutableTreeTableNode) {
					DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) value;
					AlgorithmRecord fr = (AlgorithmRecord) node.getUserObject();
					setText(fr.getName());
					if (node.isLeaf() && node.getParent() == tree.getModel().getRoot()) {
						setIcon(getDefaultClosedIcon());
					} else if (!node.isLeaf()) {
						setText(fr.getCategory());

					}
				}
				return this;
			};
		});
		attp = new AlgorithmTreeTableProvider(table, resourcePath);
		JScrollPane sp = new JScrollPane(table);
		mainPanel.add(sp, BorderLayout.CENTER);
	}

	public Icon getIcon() throws PerspectiveException {
		return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH
				+ "filterPerspectiveIcon.png", 32, 32);
	}

	private void initAndAddMenuItems() {
		menu.addItem(initAngGetAlghMenu());
	}

	private JUIGLEMenuItem initAngGetAlghMenu() {
		if (algorithmMenu == null) {
			algorithmMenu = new JUIGLEMenuItem(
					getLocalizedString("menu.algorithm.algorithm"));
			// initialize subItems of algh. menu
			applyAlgItem = new JUIGLEMenuItem();
			// set Resource bundles
			algorithmMenu.setResourceBundleKey("menu.algorithm.algorithm");
			applyAlgItem.setResourceBundleKey("menu.algorithm.apply");
			// set actions for subitems
			setAlgorithmMenuActions();
		  // add subitems to file menu
			algorithmMenu.addSubItem(applyAlgItem);
		}
		return algorithmMenu;
	}
	
	private void setAlgorithmMenuActions() {
		Action applyAct = new AbstractAction() {
			/** Only for serialization */
			private static final long serialVersionUID = 1988403661055144409L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				attp.applyAlgorithm();
			}
		};
		applyAlgItem.setAction(applyAct);
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

	}
	
	@Override
	public void updateText() {
		super.updateText();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// update menu text
				menu.updateText();
			}
		});
	}

}
