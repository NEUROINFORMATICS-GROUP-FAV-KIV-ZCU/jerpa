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
package ch.ethz.origo.jerpa.application.perspective.signalprocess.project;

import ch.ethz.origo.juigle.application.observers.AbstractJUIGLEObservable;
import ch.ethz.origo.juigle.application.observers.JUIGLEObservable;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.1 (4/01/2010)
 * @since 0.1.0 (08/15/09)
 * @see JUIGLEObservable
 * @see AbstractJUIGLEObservable
 */
public class SignalPerspectiveObservable extends JUIGLEObservable {

	public static final int MSG_SAVE = 1;
	public static final int MSG_SAVE_AS = 2;
	public static final int MSG_OPEN = 3;
	public static final int MSG_CLOSE = 4;
	public static final int MSG_IMPORT = 5;
	public static final int MSG_PROJECT_CLOSED = 5;
	public static final int MSG_PERSPECTIVE_CHANGED = 6;
	public static final int MSG_CURRENT_PROJECT_CHANGED = 7;
	
	/**
	 * Spu�t�no p�ehr�v�n� sign�lu.
	 */
	public static final int MSG_SIGNAL_PLAYBACK_START = 8;
	/**
	 * Zastaveno p�ehr�v�n� sign�lu.
	 */
	public static final int MSG_SIGNAL_PLAYBACK_STOP = 9;
	/**
	 * Pozastaveno p�ehr�v�n� sign�lu.
	 */
	public static final int MSG_SIGNAL_PLAYBACK_PAUSE = 10;
	/**
	 * P�ehr�v�n� sign�lu po pozastaven� op�t spu�t�no.
	 */
	public static final int MSG_SIGNAL_PLAYBACK_RESUME = 11;
	
	/**
	 * Byl zm�n�n v�b�r kan�l� ur�en�ch k zobrazen�.
	 */
	public static final int MSG_CHANNEL_SELECTED = 12;
	/**
	 * Jsou k dispozici nov� pr�m�ry.
	 */
	public static final int MSG_NEW_AVERAGES_AVAILABLE = 13;
	/**
	 * Je po�adov�no spu�t�n� exportn�ho okna.
	 */
	public static final int MSG_RUN_AVERAGES_EXPORT = 14;
	/**
	 * Byla vyvol�no undo, redo a nebo akce, kter� undo/redo umo�n�.
	 */
	public static final int MSG_UNDOABLE_COMMAND_INVOKED = 15;
	/**
	 * Bylo stisknuto tla��tko pro automatick� ozna�ov�n� artefakt�.
	 */
	public static final int MSG_AUTOMATIC_ARTEFACT_SELECTION = 16;
	/**
	 * Jsou dostupn� indexy epoch, kter� jsou ur�eny k pr�m�rov�n�.
	 */
	public static final int MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE = 17;
	/**
	 * Byla provedena oprava base-line a vytvo�il se nov� buffer.
	 */
	public static final int MSG_NEW_BUFFER = 18;
	/**
	 * Byla ozna�en interval pro opravu baseliny.
	 */
	public static final int MSG_BASELINE_CORRECTION_INTERVAL_SELECTED = 19;
	/**
	 * Zm�na ve zp�sobu zobrazen� pr�b�hu sign�lu.
	 */
	public static final int MSG_INVERTED_SIGNALS_VIEW_CHANGED = 20;
	/**
	 * Zobrazen� okna pro import.
	 */
	public static final int MSG_SHOW_IMPORT_DIALOG = 21;
	/**
	 * Byl zav�en mod�ln� dialog.
	 */
	public static final int MSG_MODAL_DIALOG_CLOSED = 22;
	
	public static final int MSG_SEND_CURRENT_PROJECT = 23;
	
	private static SignalPerspectiveObservable instance;
	
	/**
	 * 
	 * @return
	 * @version 0.1.0
	 * @since 0.1.0
	 */
	public static synchronized SignalPerspectiveObservable getInstance() {
		if (instance == null) {
			instance = new SignalPerspectiveObservable();
		}
		return instance;
	}
	
	public void sendObjectToObservers(Object object) {
		notifyObserver(object);
	}
	
}
