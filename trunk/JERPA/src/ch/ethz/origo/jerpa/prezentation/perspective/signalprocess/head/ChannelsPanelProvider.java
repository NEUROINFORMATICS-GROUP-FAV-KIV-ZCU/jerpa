package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.head;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SingnalPerspectiveObservable;
import ch.ethz.origo.jerpa.data.Channel;
import ch.ethz.origo.jerpa.data.Header;

/**
 * Rozhran� mezi aplika�n� a prezenta�n� vrstvou. Slou�� pro komunikaci mezi
 * oknem s rozm�st�n�mi elektrodami, aplika�n� vrstvou a mezi ostatn�mi
 * pot�ebn�mi komponentami
 * 
 * @author Petr Soukal
 */
public class ChannelsPanelProvider implements Observer {

	private ChannelsPanel channelsPanel;
	private SingnalPerspectiveObservable spObservable;
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
	 */
	public ChannelsPanelProvider(SignalSessionManager session) {
		this.session = session;
		channelsPanel = new ChannelsPanel(this);
		countSelectedSignals = 0;
		spObservable = SingnalPerspectiveObservable.getInstance();
	}

	/**
	 * P�ij�m� zpr�vy pos�l�n� pomoc� guiControlleru.(Komunikace mezi providery)
	 */
	@Override
	public void update(Observable o, Object arg) {
		int msg;

		if (arg instanceof java.lang.Integer) {
			msg = ((Integer) arg).intValue();
		} else {
			return;
		}

		switch (msg) {
		case SingnalPerspectiveObservable.MSG_PROJECT_CLOSED:
			channelsPanel.electrodesPanel.removeAll();
			channelsPanel.electrodesPanel.repaint();
			channelsPanel.electrodesPanel.validate();
			break;

		case SingnalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
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
		spObservable.setState(SingnalPerspectiveObservable.MSG_CHANNEL_SELECTED);
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

}
