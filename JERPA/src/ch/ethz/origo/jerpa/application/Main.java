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
 *
 *    Copyright (C) 2009 - 2011  
 *    University of West Bohemia, 
 *    Department of Computer Science and Engineering, 
 *    Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.application;

import java.util.Locale;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.data.ConfigPropertiesLoader;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.jerpa.prezentation.MainFrame;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.JUIGLEApplication;
import ch.ethz.origo.juigle.application.JUIGLEErrorParser;
import ch.ethz.origo.juigle.application.LanguagePropertiesLoader;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.exception.PropertiesException;
import ch.ethz.origo.juigle.context.exceptions.ApplicationException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

/**
 * Main class of this application. Contains main method for application startup.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 2.0.0 (4/25/2011)
 * @since 0.1.0 (04/16/2009 - JERPA birthday)
 * 
 */
public class Main {

	public static Logger rootLogger = Logger.getRootLogger();

	private static JUIGLEApplication application;

	/** */
	private static final String PERSPECTIVE_PATH_XML = "config/perspectives.xml";
	/** */
	private static final String PERSPECTIVE_PATH_PROPERTIES = "config/perspectives.properties";

	/**
	 * Main method - application start
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create application instance
		application = JUIGLEApplication.getInstance();

		// load configures
		final Thread config = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LanguagePropertiesLoader.loadProperties();
					ConfigPropertiesLoader.loadProperties();
					setLocale(LanguagePropertiesLoader.getApplicationLocale());
					application.setVersion(Main.getAppVersion());
					// init and load all plug-ins
					application.initPluginEngine(
							ConfigPropertiesLoader.getAppMajorVersionAsInt(),
							ConfigPropertiesLoader.getAppMinorVersionAsInt(),
							ConfigPropertiesLoader.getAppRevisionVersionAsInt());
					application.loadPlugins(ConfigPropertiesLoader.getPluginsLocation());
					application.setMainFrame(new MainFrame());
				} catch (PropertiesException e) {
					String msg = JUIGLEErrorParser.getErrorMessage(e.getMessage(),
							LangUtils.JERPA_ERROR_LIST_PATH);
					JUIGLErrorInfoUtils.showErrorDialog("JERPA ERROR", msg, e,
							Level.WARNING);
					rootLogger.warn(e.getMessage(), e);
				} catch (ApplicationException e) {
					rootLogger.warn(e.getMessage(), e);
				}
			}
		});

		// show splashscreen
		Thread splash = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					application.startSplashScreen(JUIGLEGraphicsUtils
							.getImage(JERPAUtils.IMAGE_PATH + "Jerpa_logo.png"));
					config.join();
					Thread.sleep(1000);
					application.stopSplashScreen();
				} catch (InterruptedException e) {
					rootLogger.error(e.getMessage(), e);
					// FIXME ADD ERROR TO THE DIALOG
				} catch (PerspectiveException e) {
					rootLogger.error(e.getMessage(), e);
				}
			}
		});

		splash.start();
		config.start();
		try {
			splash.join();
		} catch (InterruptedException e) {
			rootLogger.error(e.getMessage(), e);
			// FIXME ADD ERROR TO THE DIALOG
		}

		// now invoke a Main Frame of JERPA application
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					application.startApplication(true, PERSPECTIVE_PATH_PROPERTIES);
				} catch (PerspectiveException e) {
					String msg = JUIGLEErrorParser.getErrorMessage(e.getMessage(),
							LangUtils.JERPA_ERROR_LIST_PATH);
					JUIGLErrorInfoUtils.showErrorDialog("JERPA ERROR", msg, e,
							Level.WARNING);
					rootLogger.error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Set application locale
	 * 
	 * @param applicationLocale
	 *          name of locale
	 */
	private static void setLocale(String applicationLocale) {
		Locale locale = null;
		if (applicationLocale.equals(ILanguage.ENGLISH)) {
			locale = new Locale("en");
		} else if (applicationLocale.equals(ILanguage.CZECH)) {
			locale = new Locale("cs", "CZ");
		}
		Locale.setDefault(locale);
	}

	/**
	 * 
	 * 
	 * @since 2.0.0 (4/25/2011)
	 * @return
	 */
	public static String getAppVersion() {
		StringBuffer titleBuff = new StringBuffer();
		titleBuff.append(ConfigPropertiesLoader.getApplicationTitle());
		titleBuff.append(" ");
		titleBuff.append(ConfigPropertiesLoader.getAppMajorVersion());
		titleBuff.append(".");
		titleBuff.append(ConfigPropertiesLoader.getAppMinorVersion());
		titleBuff.append(".");
		titleBuff.append(ConfigPropertiesLoader.getAppRevisionVersion());
		return titleBuff.toString();
	}
}