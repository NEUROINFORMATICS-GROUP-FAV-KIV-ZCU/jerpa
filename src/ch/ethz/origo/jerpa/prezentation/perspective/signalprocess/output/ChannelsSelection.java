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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import ch.ethz.origo.jerpa.data.Channel;

/**
 * T��da reprezentuje "vnit�ek" dialogov�ho okna pro v�b�r kan�l�, jejich�
 * pr�m�ry se maj� zobrazit v exportovan�m dokumentu. Zobrazen� se pak prov�d�
 * p�es metodu <code>JOptionPane.showOptionDialog</code>.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
class ChannelsSelection extends JPanel {
	/**
	 * Konstanty definuj�c� zp�sob zobrazen� ovl�dac�ch prvk�.
	 */
	private static final int COLS = 3, ROWS = 0, BORDER = 5;
	/**
	 * Panel, na kter�m jsou zobrazov�ny ovl�dac� prvky.
	 */
	private JPanel channelsPanel;
	/**
	 * Pole ovl�dac�ch prvk� pro zobrazen�/vypu�t�n� pr�m�ru kan�l� v/z
	 * exportovan�m dokumentu.
	 */
	private JCheckBox[] channels;
	/**
	 * Po�ad� v�ech kan�l� ve vstupn�m souboru.
	 */
	private int[] channelsOrderInInputFile;

	/**
	 * Vytv��� nov� "vnit�ek" dialogov�ho okna pro v�b�r kan�l�, jejich� pr�m�ry
	 * jsou zobrazeny v exportovan�m dokumentu.
	 * 
	 * @param provider
	 *          Reference na programov� rozhran� pro komunikaci s aplika�n�
	 *          vrstvou.
	 */
	ChannelsSelection(ExportFrameProvider provider) {
		super(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(Color.BLACK);

		TitledBorder titleBorder = BorderFactory
				.createTitledBorder("Averaged channels");
		titleBorder.setTitleColor(Color.RED);
		CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
				titleBorder, BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER,
						BORDER));

		channelsPanel = new JPanel(new GridLayout(ROWS, COLS));
		channelsPanel.setOpaque(false);
		channelsPanel.setBorder(compoundBorder);

		channels = new JCheckBox[provider.getAveragingDataManager()
				.getAveragedSignalsIndexes().size()];
		channelsOrderInInputFile = new int[channels.length];

		for (int i = 0; i < channels.length; i++) {
			channelsOrderInInputFile[i] = provider.getAveragingDataManager()
					.getAveragedSignalsIndexes().get(i);
			Channel channel = provider.getHeader().getChannels()
					.get(
							provider.getAveragingDataManager().getAveragedSignalsIndexes()
									.get(i));
			channels[i] = new JCheckBox(channel.getName());
			channels[i]
					.setSelected(provider.getChannelsToExport()[channelsOrderInInputFile[i]]);
			channels[i].setForeground(Color.YELLOW);
			channels[i].setBackground(Color.BLACK);
			channelsPanel.add(channels[i]);
		}

		this.add(scrollPane);
		centerPanel.add(channelsPanel, BorderLayout.CENTER);
		scrollPane.setViewportView(centerPanel);
		this.setVisible(true);
	}

	/**
	 * Vrac� referenci na atribut <code>channels</code>.
	 * 
	 * @return Ovl�dac� prvky v�b�ru kan�l�.
	 */
	JCheckBox[] getChannels() {
		return channels;
	}

	/**
	 * Vrac� referenci na atribut <code>channelsOrderInInputFile</code>.
	 * 
	 * @return Po�ad� v�ech kan�l� ve vstupn�m souboru.
	 */
	int[] getChannelsOrderInInputFile() {
		return channelsOrderInInputFile;
	}
}
