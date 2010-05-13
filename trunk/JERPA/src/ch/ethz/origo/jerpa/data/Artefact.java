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
package ch.ethz.origo.jerpa.data;

/**
 * T��da reprezentuj�c� artefakt.
 * 
 * @author Petr - Soukal
 * @author Vaclav Souhrada 
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 */
public class Artefact {
	private int artefactStart;
	private int artefactEnd;

	/**
	 * Vytv��� instanci t��dy.
	 * 
	 * @param artefactStart -
	 *          po��te�n� hodnota intervalu artefaktu.
	 * @param artefactEnd -
	 *          kone�n� hodnota intervalu artefaktu.
	 */
	public Artefact(int artefactStart, int artefactEnd) {
		this.artefactStart = artefactStart;
		this.artefactEnd = artefactEnd;
	}

	/**
	 * Nastavuje po��te�n� hodnotu intervalu artefaktu.
	 * 
	 * @param artefactStart -
	 *          po��te�n� hodnota intervalu artefaktu.
	 */
	public void setArtefactStart(int artefactStart) {
		this.artefactStart = artefactStart;
	}

	/**
	 * Nastavuje kone�nou hodnotu intervalu artefaktu.
	 * 
	 * @param artefactEnd -
	 *          kone�nou hodnota intervalu artefaktu.
	 */
	public void setArtefactEnd(int artefactEnd) {
		this.artefactEnd = artefactEnd;
	}

	/**
	 * @return po��te�n� hodnota intervalu artefaktu.
	 */
	public int getArtefactStart() {
		return artefactStart;
	}

	/**
	 * @return kone�n� hodnota intervalu artefaktu.
	 */
	public int getArtefactEnd() {
		return artefactEnd;
	}
}
