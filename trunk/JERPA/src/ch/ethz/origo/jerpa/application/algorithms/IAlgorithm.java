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
 *    IAlgorithm.java
 *    Copyright (C) 2009 - 2010 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.application.algorithms;

/**
 * This interface have to implement all methods / algorithms. If not, than
 * filter not be used.
 * 
 * @author Vaclav Souhrada
 * @version 0.3.3 (3/07/2010)
 * @since JERPA version 0.1.0 (11/26/09)
 * 
 */
public interface IAlgorithm {

	/**
	 * Main method for algorithms. This method will be called by JERPA algorithm
	 * loader after select alg. by user.
	 */
	public void buildAlgorithm();

}
