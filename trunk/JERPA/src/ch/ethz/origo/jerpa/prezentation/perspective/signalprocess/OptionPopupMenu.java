package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * T��da vytv��ej�c� popup-menu, kter� umo��uje funkce segmentace sign�l� a
 * ozna�en� artefakt�.
 * 
 * @author Petr Soukal (original class from jERP Studio is <code>OptionMenu</code>)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 11/17/2009 
 * @since 0.1.0
 */
public class OptionPopupMenu extends JPopupMenu {
	private SignalsPanelProvider signalsProvider;
	private JMenuItem selectEpoch;
	private JMenuItem setPlaybackIndicator;
	private JMenuItem unselectEpoch;
	private JMenuItem unselectAllEpochs;
	private JMenuItem unselectArtefact;
	private JMenuItem unselectAllArtefacts;
	private JMenuItem unselectAll;
	private long frame;

	/**
	 * Vytv��� objekt dan� t��dy a tla��tka menu.
	 * 
	 * @param signalsWindowProvider -
	 *          objekt t��dy SignalsWindowProvider pro komunikaci s ostatn�mi
	 *          t��dami prezenta�n� vrstvy.
	 */
	public OptionPopupMenu(SignalsPanelProvider signalsProvider) {
		this.signalsProvider = signalsProvider;
		selectEpoch = new JMenuItem("Select Epoch");
		selectEpoch.addActionListener(new FunctionSelectEpoch());
		setPlaybackIndicator = new JMenuItem("Set Playback Indicator");
		setPlaybackIndicator.addActionListener(new FunctionSetPlaybackIndicator());
		unselectEpoch = new JMenuItem("Unselect Epoch");
		unselectEpoch.addActionListener(new FunctionUnselectEpoch());
		unselectAllEpochs = new JMenuItem("Unselect All Epochs");
		unselectAllEpochs.addActionListener(new FunctionUnselectAllEpochs());
		unselectArtefact = new JMenuItem("Unselect Artefact");
		unselectArtefact.addActionListener(new FunctionUnselectArtefact());
		unselectAllArtefacts = new JMenuItem("Unselect All Artefacts");
		unselectAllArtefacts.addActionListener(new FunctionUnselectAllArtefacts());
		unselectAll = new JMenuItem("Unselect All");
		unselectAll.addActionListener(new FunctionUnselectAll());

		this.add(setPlaybackIndicator);
		this.add(selectEpoch);
		this.addSeparator();
		this.add(unselectEpoch);
		this.add(unselectArtefact);
		this.addSeparator();
		this.add(unselectAllEpochs);
		this.add(unselectAllArtefacts);
		this.add(unselectAll);
	}

	/**
	 * Nastavuje zobrazen� popup-menu a jeho um�st�n�.
	 * 
	 * @param visualComponent -
	 *          komponenta, ke kter� se menu v�e.
	 * @param xAxis -
	 *          x-ov� sou�adnice zobrazen� menu.
	 * @param yAxis -
	 *          y-ov� sou�adnice zobrazen� menu.
	 * @param frame -
	 *          m�sto v souboru, p�epo��tan� ze sou�adnic kliku.
	 */
	public void setVisibleMenu(JComponent visualComponent, int xAxis, int yAxis,
			long frame) {
		this.frame = frame;
		this.show(visualComponent, xAxis, yAxis);
	}

	/**
	 * Nastavuje povolen�/zak�z�n� jednotliv�ch tla��tek.
	 * 
	 * @param enabledSelEpoch -
	 *          povolen�/zak�z�n� ozna�en� epochy.
	 * @param enabledUnselEpoch -
	 *          povolen�/zak�z�n� odzna�en� epochy.
	 * @param enabledUnselArtefact -
	 *          povolen�/zak�z�n� odzna�en� artefaktu.
	 * @param enabledUnselAllEpochs -
	 *          povolen�/zak�z�n� odzna�en� v�ech epoch.
	 * @param enabledUnselAllArtefacts -
	 *          povolen�/zak�z�n� odzna�en� v�ech artefakt�.
	 * @param enabledUnselAll -
	 *          povolen�/zak�z�n� odzna�en� v�eho.
	 */
	public void setEnabledItems(boolean enabledSelEpoch,
			boolean enabledUnselEpoch, boolean enabledUnselArtefact,
			boolean enabledUnselAllEpochs, boolean enabledUnselAllArtefacts,
			boolean enabledUnselAll) {
		selectEpoch.setEnabled(enabledSelEpoch);
		unselectEpoch.setEnabled(enabledUnselEpoch);
		unselectArtefact.setEnabled(enabledUnselArtefact);
		unselectAllEpochs.setEnabled(enabledUnselAllEpochs);
		unselectAllArtefacts.setEnabled(enabledUnselAllArtefacts);
		unselectAll.setEnabled(enabledUnselAll);
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka ozna�en� epochy.
	 */
	private class FunctionSelectEpoch implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.selectEpoch(frame);
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka nastaven� ukazatele p�ehr�v�n�.
	 */
	private class FunctionSetPlaybackIndicator implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.getDrawingComponent().setPlaybackIndicatorPosition(
					frame);
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� epochy.
	 */
	private class FunctionUnselectEpoch implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectEpoch(frame);
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� v�ech epoch.
	 */
	private class FunctionUnselectAllEpochs implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectAllEpochs();
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� artefaktu.
	 */
	private class FunctionUnselectArtefact implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectConcreteArtefact(frame);
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� v�ech artefakt�.
	 */
	private class FunctionUnselectAllArtefacts implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectAllArtefacts();
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� v�eho.
	 */
	private class FunctionUnselectAll implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectAllEpochsAndArtefacts();
		}
	}
}
