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

import org.jdesktop.swingx.JXPanel;

/**
 * Abstraktn� t��da, kter� je rodi�em ka�d�ho panelu akc� pro pr�m�rovac�
 * panely.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 * @see JXPanel
 * 
 */
abstract class ActionsPanel extends JXPanel {

	/** Only for serialization */
	private static final long serialVersionUID = -9190090626318917343L;
	
	/**
	 * Reference na pr�m�rovac� panel, ve kter�m je panel akc� um�st�n.
	 */
	MeanPanel meanPanel;

	/**
	 * Konstruktor abstraktn� t��dy. Zaji��uje inicializaci atributu
	 * <code>meanPanel</code>.
	 * 
	 * @param meanPanel
	 *          Pr�m�rovac� panel, ve kter�m je panel akc� um�st�n.
	 */
	ActionsPanel(MeanPanel meanPanel) {
		super();
		this.meanPanel = meanPanel;
	}

	/**
	 * Nastavuje zda se m� na panelu zobrazit, �e je aktu�ln� epocha za�azena do
	 * pr�m�ru.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud je aktu�ln� epocha za�azena do
	 *          pr�m�ru, jinak <code>false</code>.
	 */
	void setEpochSelected(boolean selected) {
	}

	/**
	 * Nastavuje, zda se m� na panelu zobrazit, �e se kan�l ��astn� hromadn�ho
	 * za�azov�n�/odeb�r�n� epoch do/z pr�m�ru.
	 * 
	 * @param applyChanges
	 *          <code>true</code>, pokud se kan�l ��astn� hromadn�ho
	 *          za�azov�n�/odeb�r�n� epoch, jinak <code>false</code>.
	 */
	void setApplyChanges(boolean applyChanges) {
	}

	/**
	 * Nastavuje, jak� informace o d�v�ryhodnosti pr�m�ru kan�lu se m� zobrazit.
	 * 
	 * @param trustful
	 *          N�kter� z konstant t��dy Averages.
	 */
	void setTrustFul(int trustful) {
	}

	/**
	 * Povolen�/zakaz�n� ovl�dac�ch prvk� pohled�.
	 * 
	 * @param enabled
	 *          <i>true</i> pro povolen�, <i>false</i> pro zak�z�n� ovl�dac�ch
	 *          prvk�.
	 */
	void setEnabledOperatingElements(boolean enabled) {
	}

	/**
	 * Vrac�, zda jsou ovl�dac� prvky panelu akc� povoleny �i zak�z�ny.
	 * 
	 * @return <code>true</code>, pokud jsou ovl�dac� prvky povoleny, jinak
	 *         <code>false</code>.
	 */
	boolean isEnabledOperatingElements() {
		return false;
	}
}
