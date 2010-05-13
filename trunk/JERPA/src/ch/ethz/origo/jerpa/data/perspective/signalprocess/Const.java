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
package ch.ethz.origo.jerpa.data.perspective.signalprocess;

import java.awt.Color;
import java.awt.Font;

/**
 * Obsahuje konstanty pou��van� v aplikaci.
 * 
 * @author Jiri Kucera
 * @author Petr Soukal
 * @author Tomas Rondik
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.1 (3/27/2010) 
 * @since 0.1.0 (11/17/2009)
 */
public class Const {
		
	// FIXME Tuto tridu mozna sloucit s JERPAUtils

	/**
	 * Koncovka souboru ve form�tu ascii.
	 */
	public static final String ASCII_FILE_EXTENSION = "asc";

	/**
	 * Koncovka souboru ve form�tu edf.
	 */
	public static final String EDF_FILE_EXTENSION = "rec";

	/**
	 * Koncovka souboru ve form�tu kiv edf.
	 */
	public static final String EDF_FILE_EXTENSION2 = "edf";

	/**
	 * Koncovka souboru ve form�tu kiv.
	 */
	public static final String KIV_FILE_EXTENSION = "xml";

	/**
	 * Koncovka souboru projektu.
	 */
	public static final String PROJECT_FILE_EXTENSION = "esp";

	/**
	 * Koncovka souboru ve form�tu BrainStudio.
	 */
	public static final String KIV_ASCII = "-ascii";

	/**
	 * Koncovka souboru ve form�tu BrainStudio.
	 */
	public static final String KIV_BINARY = "-binary";

	/**
	 * Koncovka slou��c� k pseudo gener�toru sign�lu.
	 */
	public static final String GENERATOR_EXTENSION = "generator";

	/**
	 * Koncovka souboru ve form�tu VDEF.
	 */
	public static final String VHDR_EXTENSION = "vhdr";

	/**
	 * Verze EDF formatu.
	 */
	public static final int EDF_VERSION = 0;

	public static final Color[] DC_SIGNALS_COLORS = { Color.BLACK, Color.BLUE,
			Color.DARK_GRAY };

	public static final Color DC_PLAYBACK_POINTER_COLOR = Color.BLUE;

	public static final Color DC_GRID_COLOR = Color.GRAY;

	public static final Color DC_BACKGROUND_COLOR = Color.WHITE;

	public static final String DC_GRID_FONT_FAMILY = "SansSerif";
	public static final int DC_GRID_FONT_STYLE = Font.PLAIN;
	public static final int DC_GRID_FONT_SIZE = 12;
	public static final Color DC_GRID_FONT_COLOR = Color.RED;

	public static final Color SW_COLOR_CHECKBOX_PANEL = Color.LIGHT_GRAY;
	public static final Color SW_COLOR_DC_PANEL = Color.WHITE;

	/**
	 * ���ka hlavn�ho okna.
	 */
	public static final int MAIN_WINDOW_WIDTH = 1024;

	/**
	 * V�ka hlavn�ho okna.
	 */
	public static final int MAIN_WINDOW_HEIGHT = 400;

	/**
	 * Ur�uje mo�n� intervaly vykreslovan� sou�adnicov� m��ky v kreslic� kompoent�
	 * (v milisekund�ch).
	 */
	public static final int[] DC_GRID_RANGE_TIMES = { 50, 100, 250, 500, 1000,
			2000, 5000, 10000 };

	/**
	 * Optim�ln� vzd�lenost mezi �arami sou�adnicov� m��ky v kreslic� komponent�
	 * (v pixelech).
	 */
	public static final int DC_OPTIMAL_GRID_RANGE = 100;
	/**
	 * Posunut� nulov�ho indexu p�i proch�zen� epochami. P�i nastaven� na "0" bude
	 * m�t prvn� epocha index "0", p�i nastaven� na "1" bude m�t prvn� epocha
	 * index "1" atd. Pro spr�vnou funk�nost je nutn�, aby hodnota t�to konstanty
	 * byla v�dy kladn� ��slo.
	 */
	public static final int ZERO_INDEX_SHIFT = 1;

	public static final int MS_IN_MIN = 60000;

	public static final int MS_IN_SEC = 1000;

	/**
	 * Konstanty pro v�b�r funkce, kterou chce u�ivatel vyu��t p�i pr�ci se
	 * sign�ly.
	 */
	public static final int SELECT_NOTHING = -1;
	public static final int SELECT_EPOCH = 0;
	public static final int UNSELECT_EPOCH = 1;
	public static final int SELECT_ARTEFACT = 2;
	public static final int UNSELECT_ARTEFACT = 3;
	public static final int SELECT_PLAYBACK = 4;
	public static final int BASELINE_CORRECTION = 5;

}
