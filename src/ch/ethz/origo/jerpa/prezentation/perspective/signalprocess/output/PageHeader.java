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

import java.awt.Dimension;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import ch.ethz.origo.jerpa.data.Header;

/**
 * Z�hlav� exportovan�ho dokumentu. Obsahuje hlavi�kov� informace o vstupn�m
 * souboru.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
class PageHeader extends JPanel {
	/**
	 * Programov� rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	private ExportFrameProvider provider;

	/**
	 * Vytv��� nov� z�hlav� exportovan�ho dokumentu.
	 * 
	 * @param provider
	 *          Programov� rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	PageHeader(ExportFrameProvider provider) {
		super();
		this.provider = provider;
		createInside();
	}

	/**
	 * Vytv��� "vnit�ek" z�hlav� - vkl�d� do n�j hlavi�kov� informace o vstupn�m
	 * souboru.
	 */
	private void createInside() {
		BoxLayout inputFileInfoLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(inputFileInfoLayout);
		this.setBackground(provider.getCanvasColor());

		Header headerDataForExport = provider.getHeader();

		this
				.add(new JLabel("Subject name: " + headerDataForExport.getSubjectName()));
		this.add(new JLabel("Personal number: "
				+ headerDataForExport.getPersonalNumber()));
		this.add(new JLabel("Doctor name: " + headerDataForExport.getDocName()));
		Calendar calendar = headerDataForExport.getDateOfAcquisition();
		this.add(new JLabel("Date: " + calendar.get(Calendar.DAY_OF_MONTH) + ". "
				+ calendar.get(Calendar.MONTH) + ". " + calendar.get(Calendar.YEAR)));
		this.add(new JLabel("Length: " + headerDataForExport.getLength()));

		JSeparator separator = new JSeparator();
		separator.setBackground(provider.getCanvasColor());
		Dimension separatorDimension = new Dimension(1, 10);
		separator.setMinimumSize(separatorDimension);
		separator.setMaximumSize(separatorDimension);
		separator.setPreferredSize(separatorDimension);

		this.add(separator);
	}
}
