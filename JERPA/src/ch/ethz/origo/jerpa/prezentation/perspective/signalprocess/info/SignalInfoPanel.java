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

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;

/**
 * Panel with informations about EEG record.
 * 
 * @author Jiri Kucera (original class from jERP Studio)
 * @author Vaclav Souhrada
 * @version 0.1.1 (1/31/2010)
 * @since 0.1.0 (11/17/09)
 * @see JXPanel
 */
public class SignalInfoPanel extends JXPanel {

	/** Only for serialization */
	private static final long serialVersionUID = -3776887971435045462L;

	private SignalInfoProvider infoProvider;
	private JXTable channelsInfoTable;
	private JXTable infoTable;
	private JScrollPane infoTableScrollPane;
	private JScrollPane jScrollPane1;

	/**
	 * Creates new form InfoWindow
	 * 
	 * @param infoWindowProvider
	 */
	public SignalInfoPanel(final SignalInfoProvider infoProvider) {
		this.infoProvider = infoProvider;
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {
		infoTableScrollPane = new JScrollPane();
		infoTable = new JXTable();
		infoTable.setColumnControlVisible(true);
		infoTable.setHighlighters(JUIGLEGraphicsUtils
				.getHighlighterInstance());
		infoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jScrollPane1 = new JScrollPane();
		channelsInfoTable = new JXTable();
		channelsInfoTable.setColumnControlVisible(true);
		channelsInfoTable.setHighlighters(JUIGLEGraphicsUtils
				.getHighlighterInstance());
		channelsInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		infoTable.setModel(infoProvider.getGlobalInfoDataModel());
		infoTable.setEnabled(false);
		infoTableScrollPane.setViewportView(infoTable);

		channelsInfoTable.setModel(infoProvider.getChannelsInfoDataModel());
		channelsInfoTable.setEnabled(false);
		jScrollPane1.setViewportView(channelsInfoTable);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap()
						.addGroup(
								layout.createParallelGroup(
										javax.swing.GroupLayout.Alignment.LEADING).addComponent(
										jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING,
										javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
										.addComponent(infoTableScrollPane,
												javax.swing.GroupLayout.Alignment.TRAILING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 457,
												Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						infoTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 158,
						javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
						javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
						jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 465,
						Short.MAX_VALUE).addContainerGap()));
	}

	public void refresh() {
		infoTable.clearSelection();
		infoTable.revalidate();
		infoTable.repaint();
		channelsInfoTable.clearSelection();
		channelsInfoTable.revalidate();
		channelsInfoTable.repaint();
	}

	public void setItemsEnabled(boolean enabled) {
		infoTable.setEnabled(enabled);
		channelsInfoTable.setEnabled(enabled);
	}

}