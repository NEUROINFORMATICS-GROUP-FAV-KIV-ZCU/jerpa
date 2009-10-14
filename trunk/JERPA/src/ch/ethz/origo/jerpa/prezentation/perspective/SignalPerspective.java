/**
 * 
 */
package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.juigle.application.exceptions.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exceptions.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtilities;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.dialogs.AboutDialog;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

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
	
	private static String resourcePath = "ch.ethz.origo.jerpa.jerpalang.perspective.signalprocess.SignalProcessing";
	
	//
	private JUIGLEMenuItem fileMenu;
	private JUIGLEMenuItem openFileItem;
	private JUIGLEMenuItem saveFileItem;
	private JUIGLEMenuItem saveAsFileItem;
	private JUIGLEMenuItem closeItem;
	private JUIGLEMenuItem importItem;
	private JUIGLEMenuItem exportItem;
	private JUIGLEMenuItem exitItem;
	//
	private JUIGLEMenuItem editMenu;
	private JUIGLEMenuItem undoItem;
	private JUIGLEMenuItem redoItem;
	private JUIGLEMenuItem baselineCorrItem;
	private JUIGLEMenuItem autoArteSelItem; 
	//
	private JUIGLEMenuItem viewMenu;
	private JUIGLEMenuItem channelItem;
	private JUIGLEMenuItem editInfoWinItem;
	private JUIGLEMenuItem signalsWinItem;
	private JUIGLEMenuItem averagingWinItem;
	//
	private JUIGLEMenuItem helpItem;
	private JUIGLEMenuItem keyboardShortcutItem;
	private JUIGLEMenuItem aboutItem;
	
	
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
			
			//menuTitledPanel = new JXTitledPanel();
			//menuTitledPanel.setOpaque(false);
			menuTaskPane = new JXTaskPane();
			menuTaskPane.setOpaque(false);
			
			// initalize menu
			menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP, resourcePath);
			menu.setFloatable(false);
			menu.setRollover(true);
			// initialize and add menu items
			initAndAddMenuItems();
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
			menu.addItem(initAndGetFileMenuItem());
		  menu.addItem(initAndGetEditMenuItem());
			menu.addItem(initAndGetViewMenuItem());
			menu.addItem(initAndGetHelpMenuItem());
			menu.addMenuSeparator();
			menu.addHeaderHideButton(true);
			menu.addFooterHideButton(true);
			//menuTitledPanel.add(menu);
			menuTaskPane.add(menu);
		} catch (JUIGLEMenuException e1) {
			throw new PerspectiveException(e1);
		}
	}
	
	@Override
	public String getResourceBundlePath() {
		return SignalPerspective.resourcePath;
	}
	
	public Icon getIcon() {
		return JUIGLEGraphicsUtilities.createImageIcon("ch/ethz/origo/jerpa/data/images/icon.gif", "aaaaaaaaaaaaaa");
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
	
	/**
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetFileMenuItem() {
		fileMenu = new JUIGLEMenuItem(getLocalizedString("menu.file"));
		// initialize subItems of file menu
		openFileItem = new JUIGLEMenuItem();
		saveFileItem = new JUIGLEMenuItem();
		saveAsFileItem = new JUIGLEMenuItem();
		closeItem = new JUIGLEMenuItem();
		importItem = new JUIGLEMenuItem();
		exportItem = new JUIGLEMenuItem();
		exitItem = new JUIGLEMenuItem();
		//
		openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		//
		fileMenu.setResourceBundleKey("menu.file");
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
		fileMenu.addSubItem(openFileItem);
		fileMenu.addSubItem(saveFileItem);
		fileMenu.addSubItem(saveAsFileItem);
		fileMenu.addSubItem(closeItem);
		fileMenu.addSubItem(importItem);
		fileMenu.addSubItem(exportItem);
		fileMenu.addSubItem(exitItem);
		
		return fileMenu;
	}
	
	/**
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetEditMenuItem() {
		editMenu = new JUIGLEMenuItem(getLocalizedString("menu.edit"));
		undoItem = new JUIGLEMenuItem();
		redoItem = new JUIGLEMenuItem();
		baselineCorrItem = new JUIGLEMenuItem();
		autoArteSelItem = new JUIGLEMenuItem(); 
		//
		editMenu.setResourceBundleKey("menu.edit");
		undoItem.setResourceBundleKey("menu.edit.undo");
		redoItem.setResourceBundleKey("menu.edit.redo");
		baselineCorrItem.setResourceBundleKey("menu.edit.baselinecorrection");
		autoArteSelItem.setResourceBundleKey("menu.edit.autoselarte");
		//
		setEditMenuActions();
		//
		editMenu.addSubItem(undoItem);
		editMenu.addSubItem(redoItem);
		editMenu.addSubItem(baselineCorrItem);
		editMenu.addSubItem(autoArteSelItem);
		
		return editMenu;
	}
	
	/**
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetViewMenuItem() {
		viewMenu = new JUIGLEMenuItem(getLocalizedString("menu.view"));
		channelItem = new JUIGLEMenuItem();
		signalsWinItem = new JUIGLEMenuItem();
		editInfoWinItem = new JUIGLEMenuItem();
		averagingWinItem = new JUIGLEMenuItem();
		//
		viewMenu.setResourceBundleKey("menu.view");
		channelItem.setResourceBundleKey("menu.view.channelwin");
		signalsWinItem.setResourceBundleKey("menu.view.signalwin");
		editInfoWinItem.setResourceBundleKey("menu.view.infowin");
		averagingWinItem.setResourceBundleKey("menu.view.averagewin");
		//
		setViewMenuActions();
		//
		viewMenu.addSubItem(channelItem);
		viewMenu.addSubItem(editInfoWinItem);
		viewMenu.addSubItem(signalsWinItem);
		viewMenu.addSubItem(averagingWinItem);
		
		return viewMenu;
	}
	
	/**
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetHelpMenuItem() {
		helpItem = new JUIGLEMenuItem(getLocalizedString("menu.help"));
		keyboardShortcutItem = new JUIGLEMenuItem();
		aboutItem = new JUIGLEMenuItem();
		//
		keyboardShortcutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.CTRL_MASK));
		//
		helpItem.setResourceBundleKey("menu.help");
		keyboardShortcutItem.setResourceBundleKey("menu.help.keyboard.shortcuts");
		aboutItem.setResourceBundleKey("menu.help.about.signalprocessing");
		//
		setHelpMenuActions();
		helpItem.addSubItem(keyboardShortcutItem);
		helpItem.addSubItem(aboutItem);
		return helpItem;
	}
	
	/**
	 * 
	 * @since 0.1.0
	 */
	private void setFileMenuActions() {
		Action open = new AbstractAction() {
			private static final long serialVersionUID = -6603743681967057946L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("cs","CZ"));
				updateText();
			}		
		};
		closeItem.setAction(open);
	}
	
	/**
	 * @since 0.1.0
	 */
	private void setEditMenuActions() {
		
	}
	
	/**
	 * @since 0.1.0
	 */
	private void setViewMenuActions() {
		
	}
	
	/**
	 * @since 0.1.0
	 */
	private void setHelpMenuActions() {
		Action about = new AbstractAction() {
			/**  */
			private static final long serialVersionUID = -1644285485867277600L;

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new AboutDialog();						
					}
				});
			}
		};
		aboutItem.setAction(about);
	}
	
}