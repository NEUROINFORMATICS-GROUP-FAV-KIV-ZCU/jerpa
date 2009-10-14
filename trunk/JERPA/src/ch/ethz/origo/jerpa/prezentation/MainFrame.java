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

import noname.ConfigPropertiesLoader;
import noname.PerspectiveLoader;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.exceptions.PerspectiveException;
import ch.ethz.origo.juigle.application.listener.AppButtonsEvent;
import ch.ethz.origo.juigle.application.listener.AppButtonsListener;
import ch.ethz.origo.juigle.prezentation.JUIGLEFrame;
import ch.ethz.origo.juigle.prezentation.JUIGLEMainMenu;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 05/07/2009
 * @since 0.1.0 (05/07/2009)
 */
public class MainFrame implements AppButtonsListener {

	private JUIGLEFrame mainFrame;

	/**
	 * Initialize main graphic frame
	 */
	public MainFrame() {
		try {
			initGui();
		} catch (PerspectiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialize GUI
	 * 
	 * @throws PerspectiveException
	 * @version 0.1.0
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
						.getSystemResourceAsStream("ch/ethz/origo/jerpa/data/images/Jerpa_icon.png"));
		mainFrame.setCopyrightTitle(ConfigPropertiesLoader.getAppCopyright());
		mainFrame.addMainMenu(getMainMenu());
		mainFrame.setPerspectives(PerspectiveLoader.getInstance());
		// PerspectiveLoader<T>
		mainFrame.setVisible(true);
	}
	
	private JUIGLEMainMenu getMainMenu() {
		JUIGLEMainMenu mainMenu = new JUIGLEMainMenu();
		mainMenu.setLocalizedResourceBundle(LangUtils.MAIN_FILE_PATH);
		return mainMenu;
	}

	@Override
	public void closeAppButton(AppButtonsEvent e) {

	}

	@Override
	public void maximalizeAppButton(AppButtonsEvent e) {

	}

	@Override
	public void minimalizelizeAppButton(AppButtonsEvent e) {

	}

}