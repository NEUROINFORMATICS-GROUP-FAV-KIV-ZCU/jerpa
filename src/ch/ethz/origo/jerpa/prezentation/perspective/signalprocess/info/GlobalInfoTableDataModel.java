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
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.info;

import java.util.Calendar;

import javax.swing.table.AbstractTableModel;

import ch.ethz.origo.jerpa.data.Header;

/**
 * Datov� model pro tabulku s informacemi o souboru.
 * 
 * @author Jiri Kucera (original class from jERP Studio)
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/17/09)
 * @since 0.1.0
 */
public class GlobalInfoTableDataModel extends AbstractTableModel {

	/** Only for serialization */
	private static final long serialVersionUID = 1528095116692137961L;
	
	private Header header;

	public GlobalInfoTableDataModel(Header header) {
		this.header = header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	@Override
	public int getRowCount() {
		return 8;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (header == null && columnIndex == 1) {
			return "";
		}

		switch (rowIndex) {
		case 0:
			return columnIndex == 1 ? header.getSubjectName().trim() : "Subject Name";

		case 1:
			return columnIndex == 1 ? header.getPersonalNumber().trim()
					: "Personal Number";

		case 2:
			return columnIndex == 1 ? header.getDocName().trim() : "Doctor Name";

		case 3:
			return columnIndex == 1 ? calendarToString() : "Acquisition Time";

		case 4:
			return columnIndex == 1 ? header.getSamplingInterval()
					: "Sampling Interval";

		case 5:
			return columnIndex == 1 ? header.getNumberOfSamples()
					: "Number Of Samples";

		case 6:
			return columnIndex == 1 ? header.getSegmentLength() : "Segment Length";

		case 7:
			return columnIndex == 1 ? header.getLength().trim() : "Recording Length";

		default:
			return "unknown row";
		}

	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Property";

		case 1:
			return "Value";

		default:
			return "Unknown";
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return false;
		} else {
			switch (rowIndex) {
			case 0:
			case 1:
			case 2:
			case 6:
				return true;

			default:
				return false;
			}

		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (rowIndex) {
		case 0:
			header.setSubjectName(String.valueOf(aValue).trim());
			break;

		case 1:
			header.setPersonalNumber(String.valueOf(aValue).trim());
			break;

		case 2:
			header.setDocName(String.valueOf(aValue).trim());
			break;

		case 6:
			header.setSegmentLength(Integer.valueOf(String.valueOf(aValue).trim()));
			break;
		}
		// System.out.println(columnIndex + ":" + rowIndex + ":" +
		// aValue.toString());
	}

	private String calendarToString() {
		Calendar c = header.getDateOfAcquisition();
		String str = String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "."
				+ String.valueOf(c.get(Calendar.MONTH) + 1) + "."
				+ String.valueOf(c.get(Calendar.YEAR)) + " "
				+ String.valueOf(c.get(Calendar.HOUR_OF_DAY)) + ":"
				+ String.valueOf(c.get(Calendar.MINUTE)) + ":"
				+ String.valueOf(c.get(Calendar.SECOND)) + "."
				+ String.valueOf(c.get(Calendar.MILLISECOND));

		return str;
	}
}
