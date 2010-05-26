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
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.head;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.data.Channel;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.application.observers.PerspectiveObservable;

/**
 * Rozhran� mezi aplika�n� a prezenta�n� vrstvou. Slou�� pro komunikaci mezi
 * oknem s rozm�st�n�mi elektrodami, aplika�n� vrstvou a mezi ostatn�mi
 * pot�ebn�mi komponentami
 * 
 * @author Petr Soukal
 * @author Vaclav Souhrada
 * @version 0.2.1 (4/05/2010)
 * @since 0.1.0 (1/30/2010)
 * @see IObserver
 */
public class ChannelsPanelProvider implements IObserver {

	private ChannelsPanel channelsPanel;
	private SignalPerspectiveObservable spObservable;
	private SignalSessionManager session;
	private ArrayList<String> channelsNames;
	private int countSelectedSignals;

	/**
	 * Vytv��� instance t��dy.
	 * 
	 * @param appCore
	 *          - j�dro aplikace udr�uj�c� vztah mezi aplika�n� a prezenta�n�
	 *          vrstvou.
	 * @param guiController
	 * @throws JUIGLELangException
	 */
	public ChannelsPanelProvider(SignalSessionManager session)
			throws JUIGLELangException {
		this.session = session;
		channelsPanel = new ChannelsPanel(this);
		countSelectedSignals = 0;
		spObservable = SignalPerspectiveObservable.getInstance();
		spObservable.attach(this);
	}

	/**
	 * P�ij�m� zpr�vy pos�l�n� pomoc� guiControlleru.(Komunikace mezi providery)
	 */
	@Override
	public void update(IObservable o, Object arg) {
		int msg;
		if (arg instanceof java.lang.Integer) {
			msg = ((Integer) arg).intValue();
		} else {
			return;
		}

		switch (msg) {
		case SignalPerspectiveObservable.MSG_PROJECT_CLOSED:
		case PerspectiveObservable.MSG_PROJECT_CLOSED:
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					channelsPanel.electrodesPanel.removeAll();
					channelsPanel.electrodesPanel.repaint();
					channelsPanel.electrodesPanel.validate();
				}
			});
			break;
		case SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
			setChannelsNames();
			break;

		default:
			break;
		}
	}

	/**
	 * Vytv��� Checkboxy v�ech kan�l� v souboru a nastavuje jejich jm�na.
	 */
	private void setChannelsNames() {
		Header header = session.getCurrentHeader();
		channelsNames = new ArrayList<String>();
		ArrayList<Integer> visibleChannels = (ArrayList<Integer>) session
				.getCurrentProject().getSelectedChannels();
		FunctionElectrodesCheckBoxes actionCheckBoxes = new FunctionElectrodesCheckBoxes();

		for (Channel channel : header.getChannels()) {
			channelsNames.add(channel.getName());
		}

		channelsPanel.electrodesPanel.removeAll();
		JCheckBox[] electrodes = new JCheckBox[channelsNames.size()];

		for (int i = 0; i < channelsNames.size(); i++) {
			if (channelsNames.get(i).length() > channelsPanel.MAX_ELECTRODE_LENGTH) {
				electrodes[i] = new JCheckBox(channelsNames.get(i).substring(0,
						channelsPanel.MAX_ELECTRODE_LENGTH));
			} else {
				electrodes[i] = new JCheckBox(channelsNames.get(i));
			}
			electrodes[i].setToolTipText(channelsNames.get(i));
			electrodes[i].addActionListener(actionCheckBoxes);
			electrodes[i].setName(channelsNames.get(i));
			electrodes[i].setOpaque(false);
			electrodes[i].setForeground(Color.ORANGE);
			channelsPanel.electrodes = electrodes;
			channelsPanel.electrodesPanel.add(channelsPanel.electrodes[i]);
		}

		channelsPanel.electrodesPanel.repaint();
		channelsPanel.electrodesPanel.validate();

		if (visibleChannels == null) {
			for (int i = 0; i < channelsPanel.electrodes.length; i++) {
				channelsPanel.electrodes[i].setSelected(true);
			}
			countSelectedSignals = channelsPanel.electrodes.length;
			channelsPanel.showChannels.setEnabled(true);
		} else {
			for (Integer index : visibleChannels) {
				channelsPanel.electrodes[index].setSelected(true);
			}

			countSelectedSignals = visibleChannels.size();
			channelsPanel.showChannels.setEnabled(true);
		}

		channelsPanel.showChannels.doClick();

	}

	/**
	 * Nastavuje indexy kan�l�, kter� se maj� zobrazit.
	 * 
	 * @param selectedChannels
	 */
	protected void setVisibleChannels(ArrayList<Integer> selectedChannels) {
		session.getCurrentProject().setSelectedChannels(selectedChannels);
		spObservable.setState(SignalPerspectiveObservable.MSG_CHANNEL_SELECTED);
	}

	/**
	 * Zji��uje ozna�en� kan�ly a nastavuje je do aktu�ln�ho projektu.
	 */
	protected void changeSelectedChannels() {
		ArrayList<Integer> selectedChannels = new ArrayList<Integer>();

		for (int i = 0; i < channelsPanel.electrodes.length; i++) {
			if (channelsPanel.electrodes[i].isSelected()) {
				int indexSignal = channelsNames.indexOf(channelsPanel.electrodes[i]
						.getName());

				if (indexSignal >= 0) {
					selectedChannels.add(indexSignal);
				}
			}
		}
		Collections.sort(selectedChannels);

		setVisibleChannels(selectedChannels);

	}

	/**
	 * @return instanci tohoto panelu channelsWindow.
	 */
	public JPanel getPanel() {
		return channelsPanel;
	}

	/**
	 * Obsluhuje funkci CheckBox� jednotliv�ch kan�l�. Podle nich povoluje nebo
	 * zakazuje tla��tko k ulo�en� vybran�ch sign�l�.
	 */
	private class FunctionElectrodesCheckBoxes implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox actualElectrode = (JCheckBox) e.getSource();

			if (actualElectrode.isSelected()) {
				countSelectedSignals++;
			} else {
				countSelectedSignals--;
			}

			if (countSelectedSignals == 0) {
				channelsPanel.showChannels.setEnabled(false);
			} else {
				channelsPanel.showChannels.setEnabled(true);
			}
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Object state) {
		// TODO Auto-generated method stub

	}

}
