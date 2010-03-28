package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.juigle.application.exception.DataStoreException;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;
import ch.ethz.origo.juigle.prezentation.tables.PluginsTreeTable;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.3 (3/28/2010)
 * @since 0.1.0 (05/18/09)
 * @see Perspective
 * 
 */
public final class JERPAPerspective extends Perspective {

	/** Only for serialization */
	private static final long serialVersionUID = -8946236261770444906L;

	private static String resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.jerpa.JERPAPerspective";

	private JUIGLEMenuItem languageMenu;

	private JUIGLEMenuItem viewMenu;
	private JUIGLEMenuItem helpMenu;
	private JUIGLEMenuItem aboutItem;
	
	private PluginsTreeTable pluginsTable;

	@Override
	public String getTitle() {
		return resource.getString(getRBPerspectiveTitleKey());
	}

	@Override
	public String getRBPerspectiveTitleKey() {
		return "perspective.jerpa.title";
	}

	@Override
	public Icon getPerspectiveIcon() throws PerspectiveException {
		return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH
				+ "configure-48.png");
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
			// initalize menu
			menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP,
					resourcePath);
			menu.setFloatable(false);
			menu.setRollover(true);
			// initialize menu items
			initAndAddMenuItems();
		}
	}

	@Override
	public void initPerspectivePanel() throws PerspectiveException {
		super.initPerspectivePanel();
		mainPanel.setLayout(new BorderLayout());
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
			// menu.addItem(initAndGetHelpMenuItem());
		 menu.addItem(initAndGetViewMenuItem());
			menu.addMenuSeparator();
			// menu.addHeaderHideButton(true);
			// menu.addFooterHideButton();
			// menuTitledPanel.add(menu);
			menuTaskPane.add(menu);
		} catch (JUIGLEMenuException e1) {
			throw new PerspectiveException(e1);
		}
	}

	private JUIGLEMenuItem initAndGetViewMenuItem() {
		viewMenu = new JUIGLEMenuItem(getLocalizedString("menu.view"));
		JUIGLEMenuItem pluginsItem = new JUIGLEMenuItem();
		// initialize resource bundle keys
		pluginsItem.setResourceBundleKey("menu.view.plugins");
		viewMenu.setResourceBundleKey("menu.view");
		// add actions to items
		pluginsItem.setAction(getViewPluginsAction());
		// add items to view menu
		viewMenu.addSubItem(pluginsItem);
		return viewMenu;
	}

	private Action getViewPluginsAction() {
		Action pluginAct = new AbstractAction() {
			/** */
			private static final long serialVersionUID = -3956658187607985009L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainPanel.removeAll();
				try {
					pluginsTable = new PluginsTreeTable();
					JScrollPane sp = new JScrollPane(pluginsTable);
					mainPanel.add(sp, BorderLayout.CENTER);
					mainPanel.revalidate();
					mainPanel.repaint();
				} catch (DataStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		return pluginAct;
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
					Locale.setDefault(new Locale("cs", "CZ"));
				} else if (e.getSource().equals(en)) {
					Locale.setDefault(new Locale("en"));
				}
				updateText();
				LanguageObservable.getInstance().setState(
						LanguageObservable.MSG_LANGUAGE_CHANGED);
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