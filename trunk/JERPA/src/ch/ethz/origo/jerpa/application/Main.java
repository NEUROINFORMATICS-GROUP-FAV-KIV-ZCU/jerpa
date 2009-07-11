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
 *    Main.java
 *    Copyright (C) 2009 University of West Bohemia, 
 *                       Department of Computer Science and Engineering, 
 *                       Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.application;

import javax.swing.SwingUtilities;

import ch.ethz.origo.jerpa.prezentation.MainFrame;
/**
 * Main class of this application. Contains main method for application startup.
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 04/16/2009 
 * @since 0.1 (app. version)
 *     
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainFrame();				
			}
		});
	}
}