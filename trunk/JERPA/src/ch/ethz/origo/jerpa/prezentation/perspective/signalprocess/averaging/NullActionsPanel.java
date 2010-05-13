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
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

/**
 * Panel akc�, kter� nenab�z� ��dn� akce. Existuje pouze z toho d�vodu, �e nelze
 * do pr�m�rovac�ch panel� vlo�it <b>null</b> m�sto komponenty. Pokud by
 * nedo�lo k vlo�en� ��dn�ho pr�m�rovac�ho panelu, pak by p�i vol�n� metod
 * pr�m�rovac�ch panel� doch�zelo k <code>NullPointerException</code>.
 * 
 * @author Tom� �ond�k
 */
@SuppressWarnings("serial")
class NullActionsPanel extends ActionsPanel {
	/**
	 * Vytv��� pr�zdn� panel akc�.
	 * 
	 * @param meanPanel
	 *          Pr�m�rovac� panel, ve kter�m je panel akc� um�st�n.
	 */
	NullActionsPanel(MeanPanel meanPanel) {
		super(meanPanel);
	}
}
