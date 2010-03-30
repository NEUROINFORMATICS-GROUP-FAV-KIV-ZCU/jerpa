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

import noname.PerspectiveLoader;
import ch.ethz.origo.jerpa.data.ConfigPropertiesLoader;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.application.observers.JUIGLEObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEFrame;
import ch.ethz.origo.juigle.prezentation.JUIGLEMainMenu;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.1.4 (3/29/2010)
 * @since 0.1.0 (05/07/2009)
 */
public class MainFrame implements IObserver {

	public static int HEIGHT;

	private JUIGLEFrame mainFrame;

	/**
	 * Initialize main graphic frame
	 */
	public MainFrame() {
		try {
			initGui();
			JUIGLEObservable.getInstance().attach(this);
		} catch (PerspectiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
						.getSystemResourceAsStream("ch/ethz/origo/jerpa/data/images/Jerpa_icon.png"));
		mainFrame.setCopyrightTitle(ConfigPropertiesLoader.getAppCopyright());
		mainFrame.addMainMenu(getMainMenu());
		mainFrame.setPerspectives(PerspectiveLoader.getInstance(), "menu.main.perspectives");
		mainFrame.setVisible(true);
		mainFrame.setFullScreen(true);
		MainFrame.HEIGHT = mainFrame.getHeight();
		// PerspectiveLoader<T>
	}

	private JUIGLEMainMenu getMainMenu() throws PerspectiveException {
		JUIGLEMainMenu mainMenu = new JUIGLEMainMenu(LangUtils.MAIN_FILE_PATH);
		mainMenu.addHomePageItem(null, ConfigPropertiesLoader.getJERPAHomePage());
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

	private void appClosing() {
		JERPAUtils.deleteFilesFromDeleteList();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainFrame.dispose();
	}

}