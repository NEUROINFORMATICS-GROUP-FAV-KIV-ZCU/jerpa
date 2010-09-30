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
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.prezentation.perspective.db;

import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginListener;

import ch.ethz.origo.juigle.prezentation.database.JUIGLELoginPane;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/28/2010)
 * @since 0.1.0 (1/28/2010)
 * @see 
 * @see LoginListener
 *
 */
public class LoginDialog extends JUIGLELoginPane implements LoginListener {

	/** Only for serialization */
	private static final long serialVersionUID = -8148301402485470747L;
	
	private String title;
	
	public LoginDialog(String title) {
		this.title = title;
		initialize();
	}

	private void initialize() {
		JUIGLELoginPane.JXLoginFrame frame = JUIGLELoginPane.showLoginFrame(this);
	  frame.setTitle(title);
	  frame.setAlwaysOnTop(true);
	  frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		LoginDialog ld = new LoginDialog("Nazdar koco");
	}

	@Override
	public void loginCanceled(LoginEvent source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loginFailed(LoginEvent source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loginStarted(LoginEvent source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loginSucceeded(LoginEvent source) {
		// TODO Auto-generated method stub
		
	}

}
