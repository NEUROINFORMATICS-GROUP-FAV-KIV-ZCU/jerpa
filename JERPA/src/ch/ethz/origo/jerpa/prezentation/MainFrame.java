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
 *    Copyright (C) 2009 - 2011
 *                       University of West Bohemia, 
 *                       Department of Computer Science and Engineering, 
 *                       Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.prezentation;

import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.data.ConfigPropertiesLoader;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.JUIGLEObservable;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.IMainFrame;
import ch.ethz.origo.juigle.prezentation.JUIGLEFrame;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.dialogs.AboutDialog;
import ch.ethz.origo.juigle.prezentation.dialogs.AboutRecord;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMainMenu;

/**
 * Main Frame (GUI) of application JERPA. It is based on the class from
 * <code>JUIGLE</code> called <code>JUIGLEFrame</code>.
 * 
 * @author Vaclav Souhrada
 * @version 2.0.0 (4/25/2011)
 * @since 0.1.0 (05/07/2009)
 * @see IMainFrame
 */
public class MainFrame implements IMainFrame {

	/** HEIGHT of application frame */
	public static int HEIGHT;

	private JUIGLEFrame juigleMainFrame;

	private ResourceBundle mainJERPAresource;

	private String mainJERPAResourcePath;

	private Logger logger = Logger.getLogger(MainFrame.class);

	/**
	 * Initialize main graphic frame
	 */
	public MainFrame() {

	}

	/**
	 * Initialize GUI
	 * 
	 * @throws PerspectiveException
	 * @since 2.0.0 (4/25/2011)
	 */
	public void initGUI(JUIGLEFrame frame) throws PerspectiveException {
		this.juigleMainFrame = frame;
		setLocalizedResourceBundle(LangUtils.MAIN_FILE_PATH);
		LanguageObservable.getInstance().attach((ILanguage) this);
		JUIGLEObservable.getInstance().attach(this);
	  MainFrame.HEIGHT = juigleMainFrame.getHeight();
	  try {
			updateText();
		} catch (JUIGLELangException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public JUIGLEMainMenu getMainMenu() throws PerspectiveException {
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
				applicationClose();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Return about dialog
	 * 
	 * @return about dialog
	 * @throws JUIGLELangException
	 */
	private JDialog getAboutDialog() throws JUIGLELangException {
		AboutDialog ad = new AboutDialog(
				LangUtils.getPerspectiveLangPathProp("about.dialog.lang"),
				JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH
						+ "Jerpa_icon.png"), true);

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
	@Override
	public void applicationClose() {
		JERPAUtils.deleteFilesFromDeleteList();

		if (EDEDClient.getInstance() != null) {
			EDEDClient.getInstance().userLogout();
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			logger.warn(e);
		}
		juigleMainFrame.dispose();
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
				juigleMainFrame.setCopyrightTitle(mainJERPAresource
						.getString("jerpa.application.copyright"));
			}
		});
	}
	
	@Override
	public String getLogoPath() {
		return JERPAUtils.IMAGE_PATH + "Jerpa_logo.png";
	}

}