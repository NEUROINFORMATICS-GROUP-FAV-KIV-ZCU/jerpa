package ch.ethz.origo.jerpa.prezentation.perspective;

import java.util.Observable;

import javax.swing.Icon;

import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.jerpa.application.perspective.weka.project.WEKAProject;
import ch.ethz.origo.juigle.application.exceptions.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtilities;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.1 10/25/09
 * @since 0.1.0 (05/18/09)
 * @see Perspective
 *
 */
public class WEKAPerspective extends Perspective implements IObserver {
	
	private static String resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.weka.WekaPerspective";
	
	private WEKAProject project;
	
	public WEKAPerspective() {
		perspectiveObservable.attach(this);
		project = new WEKAProject();
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
	public String getResourceBundlePath() {
		return WEKAPerspective.resourcePath;
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
	

	public Icon getIcon() throws PerspectiveException {
		return JUIGLEGraphicsUtilities.createImageIcon("ch/ethz/origo/jerpa/data/images/weka_logo.gif", 64, 32);
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
	public void update(Object object, int state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
}