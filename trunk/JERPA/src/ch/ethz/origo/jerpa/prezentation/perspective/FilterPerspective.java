package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.Painter;

import ch.ethz.origo.juigle.application.exceptions.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.GraphicsUtilities;
import ch.ethz.origo.juigle.prezentation.JUIGLEFrame;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 08/14/09
 * @since 0.1.0 (05/18/09)
 *
 */
public class FilterPerspective extends Perspective {

	/** Only for serialization */
	private static final long serialVersionUID = -3671126854840073832L;

	private static String resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.filter.Filter";
	
	@Override
	public String getTitle() {
		return resource.getString("perspective.title");
	}
	
	@Override
	public void initPerspectiveMenuPanel() throws PerspectiveException {
		if (menuPanel == null) {
			menuPanel = new JXTitledPanel();
			menuPanel.setOpaque(false);
			
			// initalize menu
			menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP, resourcePath);
			menu.setFloatable(false);
			menu.setRollover(true);
			// initialize and add menu items
			initAndAddMenuItems();
		}
	}
	
	@Override
	public String getResourceBundlePath() {
		return FilterPerspective.resourcePath;
	}

	private void initAndAddMenuItems() {
		
	}
	
	
	
	
	
}
