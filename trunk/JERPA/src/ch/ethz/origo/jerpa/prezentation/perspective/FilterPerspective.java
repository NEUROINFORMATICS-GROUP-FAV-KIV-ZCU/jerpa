package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.BorderLayout;
import java.util.Observable;

import javax.swing.Icon;

import noname.JERPAUtils;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;

import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.SignalsPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.head.ChannelsPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.info.SignalInfoProvider;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtilities;
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
			
			menuTaskPane.add(menu);
		}
	}
	
	@Override
	public void initPerspectivePanel() {
		super.initPerspectivePanel();
		mainPanel.setLayout(new BorderLayout());
		JUIGLETreeTable table = new JUIGLETreeTable();
		mainPanel.add(table, BorderLayout.EAST);
		
	}
	
	@Override
	public String getResourceBundlePath() {
		return FilterPerspective.resourcePath;
	}
	
	public Icon getIcon() throws PerspectiveException {
		return JUIGLEGraphicsUtilities.createImageIcon(JERPAUtils.IMAGE_PATH + "filterPerspectiveIcon.png", 32, 32);
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
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
