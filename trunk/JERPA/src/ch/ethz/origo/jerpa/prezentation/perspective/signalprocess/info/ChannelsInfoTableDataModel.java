package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.info;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.ethz.origo.jerpa.data.Channel;
import ch.ethz.origo.jerpa.data.Header;

/**
 * Datov� model pro tabulku s informacemi o kan�lech.
 * 
 * @author Jiri Kucera (original class from jERP Studio)
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/17/09)
 * @since 0.1.0
 * @see AbstractTableModel
 */
@SuppressWarnings("serial")
public class ChannelsInfoTableDataModel extends AbstractTableModel {

	private List<Channel> channels;

	public ChannelsInfoTableDataModel(Header header) {
		if (header == null) {
			channels = null;
		} else {
			this.channels = header.getChannels();
		}
	}

	@Override
	public int getRowCount() {
		if (channels == null) {
			return 0;
		}
		return channels.size();
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (channels == null) {
			return "";
		}

		switch (columnIndex) {
		case 0:
			return channels.get(rowIndex).getName();

		case 1:
			return channels.get(rowIndex).getFrequency();

		case 2:
			return channels.get(rowIndex).getPeriod();

		case 3:
			return channels.get(rowIndex).getOriginal();

		case 4:
			return channels.get(rowIndex).getUnit();
		}

		return "unknown";
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Name";

		case 1:
			return "Freq. [Hz]";

		case 2:
			return "Period [ms]";

		case 3:
			return "Original";

		case 4:
			return "Unit";

		default:
			return "Unknown";
		}
	}

	public void setHeader(Header header) {
		if (header == null) {
			channels = null;
		} else {
			this.channels = header.getChannels();
		}
	}
}
