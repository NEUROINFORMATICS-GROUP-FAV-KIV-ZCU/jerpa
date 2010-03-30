package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.prezentation.perspective.db.DbChooserDialog;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.2 (3/29/2010)
 * @since 0.1.0 (10/16/2010)
 * @see Perspective
 * 
 */
public class DBPerspective extends Perspective {
	
	// Database menu
	private JUIGLEMenuItem databaseItem;
	private JUIGLEMenuItem dbConnectItem;
	private JUIGLEMenuItem dbCreateItem;
	private JUIGLEMenuItem dbDeleteItem;
	private JUIGLEMenuItem dbInfoItem;
	// Tools menu
	private JUIGLEMenuItem toolsItem;

	public DBPerspective() {
		resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.database.DBPerspective";
	}
	
	@Override
	public Icon getPerspectiveIcon() throws PerspectiveException {
		return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "database_48.png", 32, 32);
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
			menuTaskPane.add(getMenuItems());
		}
	}
	
	@Override
	public void updateText() {
		super.updateText();
		/*SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				menu.updateText();
			}
		});*/
	}

	/**
	 * Add items to perspective menu.
	 * 
	 * @throws PerspectiveException
	 * @since 0.1.0
	 */
	private JUIGLEMenu getMenuItems() throws PerspectiveException {
		try {
			// add items to menu
			menu.addItem(initAndGetDatabaseItem());
			menu.addItem(initAndGetToolsItem());
			menu.addMenuSeparator();
			menu.addHeaderHideButton(true);
			menu.addFooterHideButton(true);
		} catch (JUIGLEMenuException e1) {
			throw new PerspectiveException(e1);
		}
		return menu;
	}

	private JUIGLEMenuItem initAndGetDatabaseItem() {
		databaseItem = new JUIGLEMenuItem(getLocalizedString("menu.database"));
		// initialize subItems of file menu
		dbConnectItem = new JUIGLEMenuItem();
		dbCreateItem = new JUIGLEMenuItem();
		dbDeleteItem = new JUIGLEMenuItem();
		dbInfoItem = new JUIGLEMenuItem();
		// set Resource bundles
		databaseItem.setResourceBundleKey("menu.database");
		dbConnectItem.setResourceBundleKey("menu.database.connect");
		dbCreateItem.setResourceBundleKey("menu.database.create");
		dbDeleteItem.setResourceBundleKey("menu.database.delete");
		dbInfoItem.setResourceBundleKey("menu.database.info");
		//
		setDatabaseItemsAction();
		// add subitems to dabase item
		databaseItem.addSubItem(dbConnectItem);
		databaseItem.addSubItem(dbCreateItem);
		databaseItem.addSubItem(dbDeleteItem);
		databaseItem.addSubItem(dbInfoItem);
		return databaseItem;
	}

	private void setDatabaseItemsAction() {
		Action connect = new AbstractAction() {

			private static final long serialVersionUID = 6405057295379917043L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new DbChooserDialog();
			}	
		};
		dbConnectItem.setAction(connect);
	}

	private JUIGLEMenuItem initAndGetToolsItem() {
		toolsItem = new JUIGLEMenuItem(getLocalizedString("menu.tools"));
	// initialize subItems of file menu
	// set Resource bundles
		toolsItem.setResourceBundleKey("menu.tools");
	// add subitems to dabase item
		return toolsItem;
	}

}