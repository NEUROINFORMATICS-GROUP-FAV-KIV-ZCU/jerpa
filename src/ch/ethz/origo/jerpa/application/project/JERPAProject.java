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
package ch.ethz.origo.jerpa.application.project;

import ch.ethz.origo.juigle.application.project.Project;
import ch.ethz.origo.juigle.application.project.ProjectMementoCaretaker;

/**
 * Class represented the main project of the application <strong>JERPA</strong>.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.1 (2/21/2010)
 * @since 0.1.0 (06/07/2009)
 * @see Project
 */
public class JERPAProject extends Project {

	@Override
	public ProjectMementoCaretaker createMemento() {
		JERPAProject project = new JERPAProject();

		return new ProjectMementoCaretaker(project);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMemento(ProjectMementoCaretaker memento) {
		JERPAProject state = (JERPAProject) memento.getState();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void openFile() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveFile() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeBuffers() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

}
