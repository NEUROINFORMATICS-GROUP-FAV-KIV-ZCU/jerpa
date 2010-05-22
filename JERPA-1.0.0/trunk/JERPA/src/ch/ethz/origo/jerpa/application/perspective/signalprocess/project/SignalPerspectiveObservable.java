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
 * This class implemented design pattern called <strong>Observer
 * Pattern</strong>. Send messages to all registered listeners about changed
 * state of perspective <strong>Signal Processing</strong>.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.1 (4/01/2010)
 * @since 0.1.0 (08/15/09)
 * @see JUIGLEObservable
 * @see AbstractJUIGLEObservable
 */
public class SignalPerspectiveObservable extends JUIGLEObservable {

	/** Project saved */
	public static final int MSG_SAVE = 1;
	/** Project saved */
	public static final int MSG_SAVE_AS = 2;
	/** Opened project */
	public static final int MSG_OPEN = 3;
	/** Project is closing */
	public static final int MSG_CLOSE = 4;
	/** Import project */
	public static final int MSG_IMPORT = 5;
	/** Project closed */
	public static final int MSG_PROJECT_CLOSED = 5;
	/** Perspective changed */
	public static final int MSG_PERSPECTIVE_CHANGED = 6;
	/** Current project was changed */
	public static final int MSG_CURRENT_PROJECT_CHANGED = 7;
	/** The playing of the singals will be started. */
	public static final int MSG_SIGNAL_PLAYBACK_START = 8;
	/** Player of the displays signals was stopped. */
	public static final int MSG_SIGNAL_PLAYBACK_STOP = 9;
	/** Player of the displays signals was paused. */
	public static final int MSG_SIGNAL_PLAYBACK_PAUSE = 10;
	/** Player of the displays signals was paused and now it will be started */
	public static final int MSG_SIGNAL_PLAYBACK_RESUME = 11;
	/** Channel selection was changed */
	public static final int MSG_CHANNEL_SELECTED = 12;
	/** New averages are now avaible */
	public static final int MSG_NEW_AVERAGES_AVAILABLE = 13;
	/** It is required to run a window of export */
	public static final int MSG_RUN_AVERAGES_EXPORT = 14;
	/** UNDO/REDO operation */
	public static final int MSG_UNDOABLE_COMMAND_INVOKED = 15;
	/** Key was pressed for auto-tagging artefacts. */
	public static final int MSG_AUTOMATIC_ARTEFACT_SELECTION = 16;
	/** Indices are available eras, which are designed to averaging */
	public static final int MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE = 17;
	/** Correction of baselines was provided and a new buffer was created */
	public static final int MSG_NEW_BUFFER = 18;
	/** Interval for baseline correction was be marked */
	public static final int MSG_BASELINE_CORRECTION_INTERVAL_SELECTED = 19;
	/** Changes in how the waveform */
	public static final int MSG_INVERTED_SIGNALS_VIEW_CHANGED = 20;
	/** The dialog of import GUI will be showed */
	public static final int MSG_SHOW_IMPORT_DIALOG = 21;
	/** Modal dialog was closed */
	public static final int MSG_MODAL_DIALOG_CLOSED = 22;
	/** Current project will be sent */
	public static final int MSG_SEND_CURRENT_PROJECT = 23;

	private static SignalPerspectiveObservable instance;

	/**
	 * Return instance of this class. (Implemented Design Pattern called
	 * Singleton)
	 * 
	 * @return instance of this class
	 * @version 0.1.0
	 * @since 0.1.0
	 */
	public static synchronized SignalPerspectiveObservable getInstance() {
		if (instance == null) {
			instance = new SignalPerspectiveObservable();
		}
		return instance;
	}

	/**
	 * Send object to all registered listeners.
	 * 
	 * @param object
	 *          instance of Object class which will be sent.
	 */
	public void sendObjectToObservers(Object object) {
		notifyObserver(object);
	}

}
