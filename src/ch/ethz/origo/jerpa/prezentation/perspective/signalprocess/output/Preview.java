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
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * Panel pro uk�z�n� v�ech str�nek exportovan�ho dokumentu. Definuje, jak budou
 * zobrazeny str�nky exportovan�ho dokumentu pro n�hled u�ivateli.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
class Preview extends JPanel {
	/**
	 * Barva pozad� panelu.
	 */
	static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
	/**
	 * Konstanta vzhledu n�hledu - n�hled pro tisk.
	 */
	static final int PRINT = 0;
	/**
	 * Konstanta vzhledu n�hledu - n�hled pro ulo�en� do JPEG nebo PNG.
	 */
	static final int IMAGE = 1;
	/**
	 * Seznam str�nek exportovan�ho dokumentu.
	 */
	private List<Page> pages;
	/**
	 * ��k�, jak� typ n�hledu je pro konkr�tn� n�hled pou�it.
	 */
	private int previewType;

	/**
	 * Vytv��� n�hled pro zobrazen� str�nek exportovan�ho dokumentu.
	 * 
	 * @param pages
	 *          Str�nky exportovan�ho dokumentu.
	 * @param previewType
	 *          Typ n�hledu.
	 */
	Preview(List<Page> pages, int previewType) {
		this.pages = pages;
		this.previewType = previewType;
		layoutInit();
		createInside();
	}

	/**
	 * Inicializace a nastaven� parametr� layoutu n�hledu s ohledem na hodnotu v
	 * atributu <code>previewType</code>.
	 */
	private void layoutInit() {
		switch (previewType) {
		case IMAGE:
			BoxLayout imageLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
			this.setLayout(imageLayout);
			break;
		default: // this.PRINT
			GridLayout printLayout = new GridLayout(pages.size(), 1);
			printLayout.setVgap(30);
			this.setLayout(printLayout);
		}
	}

	/**
	 * Vytv��� "vnit�ek" n�hledu. Umi�tuje str�nky exportovan�ho dokumentu do
	 * n�hledu podle pou�it�ho layoutu.
	 */
	private void createInside() {
		this.setBackground(BACKGROUND_COLOR);
		for (Page page : pages) {
			this.add(page);
		}
	}
}
