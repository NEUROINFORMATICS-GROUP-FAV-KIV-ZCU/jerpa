package ch.ethz.origo.jerpa.prezentation.perspective;

import javax.swing.Icon;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTitledPanel;

import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtilities;
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

	private static String resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.filter.FilterPerspective";
	
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
			
			menuTaskPane.add(menu);
		}
	}
	
	@Override
	public String getResourceBundlePath() {
		return FilterPerspective.resourcePath;
	}
	
	public Icon getIcon() throws PerspectiveException {
		return JUIGLEGraphicsUtilities.createImageIcon("ch/ethz/origo/jerpa/data/images/filterPerspectiveIcon.png", 32, 32);
	}
	

	private void initAndAddMenuItems() {
		
	}
	
	
	
	
	
}
