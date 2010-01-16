package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.1 01/16/2010
 * @since 0.1.0 (05/18/09)
 * 
 */
public final class JERPAPerspective extends Perspective {

	/** Only for serialization */
	private static final long serialVersionUID = -8946236261770444906L;
	
	private static String resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.jerpa.JERPAPerspective";
	
	private JUIGLEMenuItem languageMenu;
		
	private JUIGLEMenuItem helpMenu;
	private JUIGLEMenuItem aboutItem;
	
	
	@Override
	public String getTitle() {
		return resource.getString(getRBPerspectiveTitleKey());
	}
	
	@Override
	public String getRBPerspectiveTitleKey() {
		return "perspective.jerpa.title";
	}
	
	@Override
	public String getResourceBundlePath() {
		return JERPAPerspective.resourcePath;
	}
	
	@Override
	public void initPerspectiveMenuPanel() throws PerspectiveException {
		if (menuTaskPane == null) {
			menuTaskPane = new JXTaskPane();
			menuTaskPane.setOpaque(false);
/*
			final Paint menuBackground = JUIGLEGraphicsUtilities.createBackgroundTexture(
					new Color(0, 100, 137), new Color(104, 255, 222), 35);
			Painter<Component> p = new Painter<Component>() {

				@Override
				public void paint(Graphics2D g, Component c, int width, int height) {
					Graphics2D g2d = g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setPaint(menuBackground);

					if (JUIGLEFrame.getFrameState() != JXFrame.MAXIMIZED_BOTH) {
						g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
					} else {
						g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
					}
				}
			};
			menuTitledPane.setBackgroundPainter(p);*/
			
			// initalize menu
			menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP, resourcePath);
			menu.setFloatable(false);
			menu.setRollover(true);
			// initialize menu items
			initAndAddMenuItems();
/*
			try {
				helpItem
						.setIcon(ImageIO
								.read(ClassLoader
										.getSystemResourceAsStream("ch/ethz/origo/jerpa/data/images/icon.gif")));
			}*/
		}
	}
	
	/**
	 * 
	 * @throws PerspectiveException
	 * @since 0.1.0
	 */
	private void initAndAddMenuItems() throws PerspectiveException {
		try {
			// add items to menu
			menu.addItem(initAndGetLangueageMenuItem());
//			menu.addItem(initAndGetHelpMenuItem());
//			menu.addItem(initAndGetPluginMenuItem());
			menu.addMenuSeparator();
			//menu.addHeaderHideButton(true);
			//menu.addFooterHideButton();
			//menuTitledPanel.add(menu);
			menuTaskPane.add(menu);
		} catch (JUIGLEMenuException e1) {
			throw new PerspectiveException(e1);
		}
	}

	private JUIGLEMenuItem initAndGetPluginMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	private JUIGLEMenuItem initAndGetHelpMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	private JUIGLEMenuItem initAndGetLangueageMenuItem() {
		languageMenu = new JUIGLEMenuItem(getLocalizedString("menu.language"));
		final JUIGLEMenuItem en = new JUIGLEMenuItem();
		final JUIGLEMenuItem cz = new JUIGLEMenuItem();

		en.setResourceBundleKey("menu.language.english");
		cz.setResourceBundleKey("menu.language.czech");
		languageMenu.setResourceBundleKey("menu.language");
		
		Action langChange = new AbstractAction() {
			private static final long serialVersionUID = -6603743681967057946L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(cz)) {
					Locale.setDefault(new Locale("cs","CZ"));					
				} else if (e.getSource().equals(en)) {
					Locale.setDefault(new Locale("en"));	
				}
				updateText();
			}		
		};
		en.setAction(langChange);
		cz.setAction(langChange);
		
		languageMenu.addSubItem(en);
		languageMenu.addSubItem(cz);
		
		return languageMenu;
	}
	
	@Override
	public void updateText() {
		super.updateText();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				menu.updateText();
			}
		});
	}

}