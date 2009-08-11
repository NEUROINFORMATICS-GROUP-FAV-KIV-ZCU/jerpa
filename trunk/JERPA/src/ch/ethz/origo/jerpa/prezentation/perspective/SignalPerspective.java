/**
 * 
 */
package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import nezarazeno.JUIGLEMenuException;
import nezarazeno.PerspectiveException;

import org.jdesktop.swingx.JXTitledPanel;

import ch.ethz.origo.jerpaui.prezentation.JUIGLEMenu;
import ch.ethz.origo.jerpaui.prezentation.JUIGLEMenuItem;
import ch.ethz.origo.jerpaui.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 07/16/09
 * @since 0.1.0 (05/18/09)
 *
 */
public class SignalPerspective extends Perspective {

	/** Only for serialization */
	private static final long serialVersionUID = 3313465073940475745L;
	
	//
	private JUIGLEMenuItem fileItem;
	private JUIGLEMenuItem openFileItem;
	private JUIGLEMenuItem saveFileItem;
	private JUIGLEMenuItem saveAsFileItem;
	private JUIGLEMenuItem closeItem;
	private JUIGLEMenuItem importItem;
	private JUIGLEMenuItem exportItem;
	private JUIGLEMenuItem exitItem;
	//
	private JUIGLEMenuItem editItem;
	//
	private JUIGLEMenuItem viewItem;
	//
	private JUIGLEMenuItem helpItem;
	private JUIGLEMenuItem keyboardShortcutItem;
	private JUIGLEMenuItem aboutItem;
	
	
	@Override
	public String getTitle() {
		return "Signal Processing";
	}
	

	@Override
	public void initPerspectiveMenuPanel() throws PerspectiveException {
		if (menuPanel == null) {
			menuPanel = new JXTitledPanel();
			menuPanel.setOpaque(false);
			
			// initalize menu
			menu = new JUIGLEMenu(JUIGLEMenu.MENU_LOCATION_TOP, resource);
			menu.setFloatable(false);
			menu.setRollover(true);
			// initialize and add menu items
			initAndAddMenuItems();
		}
	}

	private void initAndAddMenuItems() throws PerspectiveException {
		try {
			// add items to menu
			menu.addItem(initAndGetFileMenuItem());
			menu.addItem(initAndGetEditMenuItem());
			menu.addItem(initAndGetViewMenuItem());
			menu.addItem(initAndGetHelpMenuItem());
			menu.addMenuSeparator();
			/*menu.addHeaderHideButton();
			menu.addFooterHideButton();*/
			menuPanel.add(menu);
		} catch (JUIGLEMenuException e1) {
			throw new PerspectiveException(e1);
		}
	}
	
	public void setLocalizedResource(Locale locale) {
		resource = ResourceBundle.getBundle("ch.ethz.origo.jerpa.jerpalang.perspective.signalprocess.SignalProcessing", locale);
	}
	
	public void updateText() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				menu.updateText(resource);
			}
		});
	}

	private JUIGLEMenuItem initAndGetFileMenuItem() {
		fileItem = new JUIGLEMenuItem(getLocalizedString("menu.file"));
		// initialize subItems of file menu
		openFileItem = new JUIGLEMenuItem();
		saveFileItem = new JUIGLEMenuItem();
		saveAsFileItem = new JUIGLEMenuItem();
		closeItem = new JUIGLEMenuItem();
		importItem = new JUIGLEMenuItem();
		exportItem = new JUIGLEMenuItem();
		exitItem = new JUIGLEMenuItem();
		//
		fileItem.setResourceBundleKey("menu.file");
		openFileItem.setResourceBundleKey("menu.open");
		saveFileItem.setResourceBundleKey("menu.save");
		saveAsFileItem.setResourceBundleKey("menu.saveAs");
		closeItem.setResourceBundleKey("menu.close");
		importItem.setResourceBundleKey("menu.import");
		exportItem.setResourceBundleKey("menu.export");
		exitItem.setResourceBundleKey("menu.exit");
		//
		setFileMenuActions();
		// add subitems to file menu
		fileItem.addSubItem(openFileItem);
		fileItem.addSubItem(saveFileItem);
		fileItem.addSubItem(saveAsFileItem);
		fileItem.addSubItem(closeItem);
		fileItem.addSubItem(importItem);
		fileItem.addSubItem(exportItem);
		fileItem.addSubItem(exitItem);
		return fileItem;
	}
	
	private JUIGLEMenuItem initAndGetEditMenuItem() {
		editItem = new JUIGLEMenuItem(getLocalizedString("menu.edit"));

		return editItem;
	}
	
	private JUIGLEMenuItem initAndGetViewMenuItem() {
		viewItem = new JUIGLEMenuItem(getLocalizedString("menu.view"));

		return viewItem;
	}
	
	private JUIGLEMenuItem initAndGetHelpMenuItem() {
		helpItem = new JUIGLEMenuItem(getLocalizedString("menu.help"));
		keyboardShortcutItem = new JUIGLEMenuItem();
		aboutItem = new JUIGLEMenuItem();
		
		keyboardShortcutItem.setResourceBundleKey("menu.help.keyboard.shortcuts");
		aboutItem.setResourceBundleKey("menu.help.about.signalprocessing");
		
		helpItem.addSubItem(keyboardShortcutItem);
		helpItem.addSubItem(aboutItem);
		return helpItem;
	}
	
	private void setFileMenuActions() {
		Action open = new AbstractAction() {
			private static final long serialVersionUID = -6603743681967057946L;

			@Override
			public void actionPerformed(ActionEvent e) {
				setLocalizedResource(new Locale("en"));
				menu.updateText(resource);
				updateText();
			}		
		};
		
		openFileItem.setAction(open);
	}
	
}