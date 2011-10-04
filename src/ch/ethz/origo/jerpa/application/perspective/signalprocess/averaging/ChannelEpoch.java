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
package ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging;

/**
 * Objekt pro p�epravu dat jedn� epochy pro jeden kan�l uvnit� ��sti aplika�n�
 * vrstvy ur�en� pro pr�m�rov�n� epoch.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * 
 */
final class ChannelEpoch {
	/**
	 * Po�ad� kan�lu, ke kter�mu data v atributech pat��, ve vstupn�m souboru.
	 */
	private int channelOrderInInputFile;
	/**
	 * Pozice st�edu epochy (po�ad� sn�mku - framu).
	 */
	private long frame;
	/**
	 * V�ha epochy. Pokud se t��da pou�ije pro p�enost n�jak�ho pr�m�ru, pak je
	 * t�eba zn�t jeho v�hu.
	 */
	private int weight;
	/**
	 * Funk�n� hodnoty epochy dan� atributem <code>frame</code> pro kan�l dan�
	 * atributem <code>channelOrderInInputFile</code>.
	 */
	private float[] values;
	/**
	 * Pokud se t��da pou�ije pro p�enost n�jak�ho pr�m�ru, pak��k�, jestli je
	 * pr�m�r, jeho� funk�n� hodnoty jsou ulo�eny v atributu <code>values</code>,
	 * podle statistick�ho t-testu ANOVA d�veryhodn�. Nab�v� jedn� z n�sleduj�c�ch
	 * konstant:
	 * <ul>
	 * <li><code>Averages.UNKNOWN</code>, kdy� nen� zn�mo, zda je pr�m�r
	 * d�v�ryhodn�.
	 * <li><code>Averages.TRUSTFUL</code>, kdy� je pr�m�r d�v�ryhodn�.
	 * <li><code>Averages.NOT_TRUSTFUL</code>, kdy� je pr�m�r ned�v�ryhodn�.
	 * </ul>
	 */
	private int trustful;

	/**
	 * Vytv��� instance t��dy pro p�enos dat uvnit� ��sti aplika�n� vrstvy pro
	 * pr�m�rov�n� epoch. Atributy jsou inicializov�ny na hodnoty, kter�ch nemohou
	 * nab�vat.
	 */
	ChannelEpoch() {
		channelOrderInInputFile = -1;
		frame = -1;
		weight = 0;
		values = null;
		trustful = Averages.UNKNOWN;

	}

	/**
	 * Vytv��� instance t��dy pro p�enos dat uvnit� ��sti aplika�n� vrstvy pro
	 * pr�m�rov�n� epoch.
	 * 
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu, ke kter�mu data v atributech pat��, ve vstupn�m
	 *          souboru.
	 * @param frame
	 *          Pozice st�edu epochy (po�ad� sn�mku - framu).
	 * @param weight
	 *          V�ha epochy (viz dokumenta�n� koment�� atributu
	 *          <code>weight</code>.
	 */
	ChannelEpoch(int channelOrderInInputFile, long frame, int weight) {
		this.channelOrderInInputFile = channelOrderInInputFile;
		this.frame = frame;
		this.weight = weight;
		values = null;
		trustful = Averages.UNKNOWN;
	}

	/**
	 * Nastavuje hodnotu atributu <code>values</code>.
	 * 
	 * @param values
	 *          Funk�n� hodnoty epochy.
	 */
	void setValues(float[] values) {
		this.values = values;
	}

	/**
	 * Vrac� hodnotu atributu <code>values</code>.
	 * 
	 * @return Funk�n� hodnoty epochy.
	 */
	float[] getValues() {
		return values;
	}

	/**
	 * Vrac� hodnotu atributu <code>channelOrderInInputFile</code>.
	 * 
	 * @return Po�ad� kan�lu, ke kter�mu data v atributech pat��, ve vstupn�m
	 *         souboru.
	 */
	int getChannelOrderInInputFile() {
		return channelOrderInInputFile;
	}

	/**
	 * Vrac� hodnotu atributu <code>frame</code>.
	 * 
	 * @return Pozice st�edu epochy (po�ad� sn�mku - framu).
	 */
	long getFrame() {
		return frame;
	}

	/**
	 * Vrac� hodnotu atributu <code>weight</code>.
	 * 
	 * @return V�ha epochy (viz dokumenta�n� koment�� atributu <code>weight</code>.
	 */
	int getWeight() {
		return weight;
	}

	/**
	 * Nastavuje hodnotu atributu <code>weight</code>.
	 * 
	 * @param weight
	 *          V�ha epochy (viz dokumenta�n� koment�� atributu
	 *          <code>weight</code>.
	 */
	void setWeigh(int weight) {
		this.weight = weight;
	}

	/**
	 * Vrac� hodnotu atributu <code>trustful</code>.
	 * 
	 * @return D�v�ryhodnost pr�m�ru (viz dokumenta�n� koment�� atributu
	 *         <code>trustful</code>).
	 */
	int getTrustful() {
		return trustful;
	}

	/**
	 * Nastavuje hodnotu atributu <code>trustful</code>.
	 * 
	 * @param trustful
	 *          D�v�ryhodnost pr�m�ru (viz dokumenta�n� koment�� atributu
	 *          <code>trustful</code>).
	 */
	void setTrustful(int trustful) {
		this.trustful = trustful;
	}

	/**
	 * Nastavuje hodnotu atributu <code>channelOrderInInputFile</code>.
	 * 
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu, ke kter�mu data v atributech pat��, ve vstupn�m
	 *          souboru.
	 */
	void setChannelOrderInInputFile(int channelOrderInInputFile) {
		this.channelOrderInInputFile = channelOrderInInputFile;
	}

	/**
	 * Nastavuje hodnotu atributu <code>frame</code>.
	 * 
	 * @param frame
	 *          Pozice st�edu epochy (po�ad� sn�mku - framu).
	 */
	void setFrame(long frame) {
		this.frame = frame;
	}
}
