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

import ch.ethz.origo.juigle.application.db.Database;
import ch.ethz.origo.juigle.prezentation.database.DatabaseDialog;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.2.0.00 (11/14/2010)
 * @since 1.0.0 (1/28/2010)
 * @see DatabaseDialog
 * 
 */
public class LoginDialog extends DatabaseDialog {

	/** Only for serialization */
	private static final long serialVersionUID = -8148301402485470747L;

	public LoginDialog(Database database) {
		super(database);
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

	/** Only for test */
	public static void main(String[] args) {
		Database db = new Database();
		db.connect("", "");
		LoginDialog ld = new LoginDialog(db);
	}

}
