package ch.ethz.origo.jerpa.prezentation.perspective;

import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 10/16/2010
 * @since 0.1.0 (10/16/2010)
 * @see Perspective
 * 
 */
public class DBPerspective extends Perspective {

	public DBPerspective() {
		resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.database.DBPerspective";
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
			menuTaskPane.add(getMenuItems());
		}
	}
	
	/**
	 * Add items to perspective menu.
	 * 
	 * @throws PerspectiveException
	 * @since 0.1.0
	 */
	private JUIGLEMenu getMenuItems() throws PerspectiveException {
		try {
			menu.addMenuSeparator();
			menu.addHeaderHideButton(true);
			menu.addFooterHideButton(true);
		} catch (JUIGLEMenuException e1) {
			throw new PerspectiveException(e1);
		}
		return menu;
	}
		
}
