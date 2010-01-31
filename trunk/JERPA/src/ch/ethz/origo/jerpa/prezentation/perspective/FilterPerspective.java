package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.BorderLayout;
import java.util.Observable;

import javax.swing.Icon;
import javax.swing.JScrollPane;

import noname.JERPAUtils;

import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.jerpa.data.perspective.filter.FilterTreeTableModel;
import ch.ethz.origo.juigle.application.exception.DataStoreException;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.data.tables.model.JUIGLETreeTableModel;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
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
 * @see IObserver
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
		JUIGLETreeTableModel ttm = new FilterTreeTableModel();
		try {
			ttm.fillByValues();
		} catch (DataStoreException e) {
			throw new PerspectiveException(e);
		}
		JUIGLETreeTable table = new JUIGLETreeTable(ttm);
		JScrollPane sp = new JScrollPane(table);
		mainPanel.add(sp, BorderLayout.WEST);
	}
	
	@Override
	public String getResourceBundlePath() {
		return FilterPerspective.resourcePath;
	}
	
	public Icon getIcon() throws PerspectiveException {
		return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "filterPerspectiveIcon.png", 32, 32);
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
	public void update(IObservable o, Object state) {
		// TODO Auto-generated method stub
		
	}
		
}
