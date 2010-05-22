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
 *    Copyright (C) 2009 - 2010  
 *    University of West Bohemia, 
 *    Department of Computer Science and Engineering, 
 *    Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.application;

import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.data.ConfigPropertiesLoader;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.jerpa.prezentation.MainFrame;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.JUIGLEErrorParser;
import ch.ethz.origo.juigle.application.LanguagePropertiesLoader;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.exception.PropertiesException;
import ch.ethz.origo.juigle.plugin.PluginEngine;
import ch.ethz.origo.juigle.plugin.exception.PluginEngineException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import ch.ethz.origo.juigle.prezentation.splashscreen.SplashScreen;

/**
 * Main class of this application. Contains main method for application startup.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.1 (3/28/2010)
 * @since 0.1.0 (04/16/2009 - JERPA birthday)
 * 
 */
public class Main {

	public static Logger rootLogger = Logger.getRootLogger();

	/**
	 * Main method - application start
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedImage image = JUIGLEGraphicsUtils
			.getImage(JERPAUtils.IMAGE_PATH + "Jerpa_logo.png");
			final SplashScreen splashScreen = new SplashScreen(image);
			final Thread config = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						LanguagePropertiesLoader.loadProperties();
						ConfigPropertiesLoader.loadProperties();
						setLocale(LanguagePropertiesLoader.getApplicationLocale());
						PluginEngine plugEngine = PluginEngine.getInstance();
						plugEngine.setCurrentVersion(ConfigPropertiesLoader
								.getAppMajorVersionAsInt(), ConfigPropertiesLoader
								.getAppMinorVersionAsInt(), ConfigPropertiesLoader
								.getAppRevisionVersionAsInt());
						plugEngine.init(ConfigPropertiesLoader.getPluginXMLLocation());
					} catch (PropertiesException e) {
						String msg = JUIGLEErrorParser.getErrorMessage(e.getMessage(),
								LangUtils.JERPA_ERROR_LIST_PATH);
						JUIGLErrorInfoUtils.showErrorDialog("JERPA ERROR", msg, e,
								Level.WARNING);
						Main.rootLogger.warn(e.getMessage(), e);
					} catch (PluginEngineException e) {
						Main.rootLogger.warn(e.getMessage(), e);
					}
				}
			});
			Thread splash = new Thread(new Runnable() {
				@Override
				public void run() {
					splashScreen.show();
					try {
						config.join();
						Thread.sleep(1000);
						splashScreen.close();
					} catch (InterruptedException e) {
						Main.rootLogger.error(e.getMessage(), e);
					}
				}
			});
			splash.start();
			config.start();
			splash.join();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new MainFrame();
				}
			});
			
		} catch (PerspectiveException e) {
			Main.rootLogger.warn(e);
		} catch (InterruptedException e) {
			Main.rootLogger.error(e.getMessage(), e);
		}
	
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
}