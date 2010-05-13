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
package ch.ethz.origo.jerpa.data.formats;

import java.io.File;
import java.io.IOException;

import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.Header;

/**
 * Rozhran� modul� pro ukl�d�n� datov�ch soubor�.
 * 
 * @author Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 */
public interface DataFormatWriter {
	/**
	 * Zap�e data do souboru.
	 * 
	 * @param header
	 *          Hlavi�ka s informacemi o souboru.
	 * @param buffer
	 *          T��da s daty.
	 * @param outputFile
	 *          V�stupn� soubor.
	 * @throws java.io.IOException
	 */
	public void write(Header header, Buffer buffer, File outputFile)
			throws IOException;

}
