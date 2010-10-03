/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    MainFrame.java
 *    Copyright (C) 2009 University of West Bohemia, 
 *                       Department of Computer Science and Engineering, 
 *                       Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.prezentation;

import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.application.perspective.PerspectiveLoader;
import ch.ethz.origo.jerpa.data.ConfigPropertiesLoader;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.JUIGLEErrorParser;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.application.observers.JUIGLEObservable;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEFrame;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import ch.ethz.origo.juigle.prezentation.dialogs.AboutDialog;
import ch.ethz.origo.juigle.prezentation.dialogs.AboutRecord;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMainMenu;

/**
 * Main Frame (GUI) of application JERPA. It is based on the class from
 * <code>JUIGLE</code> called <code>JUIGLEFrame</code>.
 * 
 * @author Vaclav Souhrada
 * @version 0.1.5 (5/20/2010)
 * @since 0.1.0 (05/07/2009)
 * @see IObserver
 */
public class MainFrame implements IObserver, ILanguage {

	/** HEIGHT of application frame */
	public static int HEIGHT;

	private JUIGLEFrame mainFrame;
	
	private ResourceBundle mainJERPAresource;
	
	private String mainJERPAResourcePath;

	private Logger logger = Logger.getLogger(MainFrame.class);

	/**
	 * Initialize main graphic frame
	 */
	public MainFrame() {
		try {
			setLocalizedResourceBundle(LangUtils.MAIN_FILE_PATH);
			initGui();
			updateText();
			LanguageObservable.getInstance().attach((ILanguage)this);
			JUIGLEObservable.getInstance().attach(this);
		} catch (PerspectiveException e) {
			String msg = JUIGLEErrorParser.getErrorMessage(e.getMessage(),
					LangUtils.JERPA_ERROR_LIST_PATH);
			JUIGLErrorInfoUtils.showErrorDialog("JERPA ERROR", msg, e, Level.WARNING);
			logger.error(e.getMessage(), e);
		} catch (JUIGLELangException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Initialize GUI
	 * 
	 * @throws PerspectiveException
	 * @version 0.2.0 (10/18/09)
	 * @since 0.1.0
	 */
	private void initGui() throws PerspectiveException {
		StringBuffer titleBuff = new StringBuffer();
		titleBuff.append(ConfigPropertiesLoader.getApplicationTitle());
		titleBuff.append(" ");
		titleBuff.append(ConfigPropertiesLoader.getAppMajorVersion());
		titleBuff.append(".");
		titleBuff.append(ConfigPropertiesLoader.getAppMinorVersion());
		titleBuff.append(".");
		titleBuff.append(ConfigPropertiesLoader.getAppRevisionVersion());
		// create frame
		mainFrame = new JUIGLEFrame(
				titleBuff.toString(),
				ClassLoader
						.getSystemResourceAsStream(JERPAUtils.IMAGE_PATH + "Jerpa_icon.png"));
		mainFrame.setMainMenu(getMainMenu());
		mainFrame.setPerspectives(PerspectiveLoader.getInstance(),
				"menu.main.perspectives");
		mainFrame.setVisible(true);
		mainFrame.setFullScreen(true);
		MainFrame.HEIGHT = mainFrame.getHeight();
		// PerspectiveLoader<T>
	}

	private JUIGLEMainMenu getMainMenu() throws PerspectiveException {
		JUIGLEMainMenu mainMenu = new JUIGLEMainMenu(LangUtils.MAIN_FILE_PATH);
		mainMenu.addHomePageItem(null, ConfigPropertiesLoader.getJERPAHomePage());
		try {
			mainMenu.addAboutItem(null, getAboutDialog(), null);
		} catch (JUIGLELangException e) {
			logger.warn(e.getMessage(), e);
		}
		// mainMenu.addCalendarItem(null);
		return mainMenu;
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
		if ((o instanceof JUIGLEObservable) && (state instanceof Integer)) {
			int msg = (Integer) state;

			switch (msg) {
			case JUIGLEObservable.MSG_APPLICATION_CLOSING:
				appClosing();
				break;

			default:
				break;
			}
		}
	}
	
	/**
	 * Return about dialog
	 * @return about dialog
	 * @throws JUIGLELangException
	 */
	private JDialog getAboutDialog() throws JUIGLELangException {
		AboutDialog ad = new AboutDialog(LangUtils
				.getPerspectiveLangPathProp("about.dialog.lang"), JUIGLEGraphicsUtils
				.createImageIcon(JERPAUtils.IMAGE_PATH + "Jerpa_icon.png"), true);
		
		String[] authors = ConfigPropertiesLoader.getListOfAuthors();
		String[] contributions = ConfigPropertiesLoader.getListOfContributions();
		AboutRecord ar = new AboutRecord();
		for (String auth : authors) {
			ar.addAuthor(auth);
		}
		for (String contri : contributions) {
			ar.addContribution(contri);
		}
		ad.setAboutRecord(ar);
		return ad;
	}

	/**
	 * Close application
	 */
	private void appClosing() {
		JERPAUtils.deleteFilesFromDeleteList();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.warn(e);
		}
		mainFrame.dispose();
		System.exit(0);
	}

	@Override
	public String getResourceBundlePath() {
		return mainJERPAResourcePath;
	}

	@Override
	public void setLocalizedResourceBundle(String path) {
		this.mainJERPAResourcePath = path;
		this.mainJERPAresource = ResourceBundle.getBundle(path);
		
	}

	@Override
	public void setResourceBundleKey(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setLocalizedResourceBundle(getResourceBundlePath());
				mainFrame.setCopyrightTitle(mainJERPAresource.getString("jerpa.application.copyright"));
			}
		});		
	}

}