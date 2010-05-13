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
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.ObjectBroadcaster;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;

/**
 * Komponenta zobrazuj�c� pr�m�rov�n� jednoho sign�lu v epo�e. V komponent� je
 * sign�l zobrazovan� epochy um�st�n vlevo dole pod informa�n�m panelem a
 * pr�m�ry vpravo pod sebou. Pomoc� instance t��dy <i>SignalViewer</i>
 * zobrazuje pr�b�h sign�lu v pr�v� vybran� epo�e, v�sledn� pr�m�r se zahrnut�m
 * t�to epochy a v�sledn� pr�m�r bez zahrnut� t�to epochy. Ke ka�d�mu sign�lu
 * zobrazuje jeho z�kladn� informace, kter� je mo�n� z�skat z hlavi�kov�ho
 * souboru (t��da <i>Header</i>).
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 */
@SuppressWarnings("serial")
final class MeanPanel2_2 extends MeanPanel {
	private JPanel underActionsPanel;
	/**
	 * Zobrazen� pr�b�hu sign�lu v aktu�ln� epo�e.
	 */
	private SignalViewerPanel currentEpochViewer;

	/**
	 * Zobrazen� celkov�ho pr�m�ru sign�lu po zahrnut� aktu�ln� epochy do pr�m�ru.
	 */
	private SignalViewerPanel withCurrentEpochViewer;

	/**
	 * Zobrazen� celkov�ho pr�m�ru sign�lu bez zahrnut� aktu�ln� epochy do
	 * pr�m�ru.
	 */
	private SignalViewerPanel withoutCurrentEpochViewer;

	private ObjectBroadcaster signalViewerBroadcaster;

	/**
	 * Vytv��� instance t��dy. Nastavuje r�me�ek kolem panelu a do titulku r�me�ku
	 * vkl�d� jm�no p��slu�n�ho kan�lu.
	 * 
	 * @param channelOrderInInputFile
	 *          po�ad� p��slu�n�ho kan�lu ve vstupn�m souboru.
	 * @param averagingWindowProvider
	 *          rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	MeanPanel2_2(int channelOrderInInputFile,
			AveragingPanelProvider averagingWindowProvider) {
		super(channelOrderInInputFile, averagingWindowProvider);

		this.setLayout(layoutInit());
		this.signalViewerBroadcaster = new ObjectBroadcaster();
		createInside();

		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(this.channelName), BorderFactory.createEmptyBorder(
				-5, 0, 0, 0)));

		addActionListeners();
	}

	/**
	 * Vytvo�en� a nastaven� atribut� layoutu.
	 * 
	 * @return layout komponenty
	 */
	private LayoutManager layoutInit() {
		GridLayout layoutManager = new GridLayout(2, 2);
		layoutManager.setHgap(10);
		return layoutManager;
	}

	/**
	 * Vytvo�en� panelu, kter� zobrazuje z�kladn� informace o sign�lu + obsahuje
	 * za�krt�vac� pol��ko pro zahrnut� sign�lu do v�sledn�ho pr�m�ru.
	 * 
	 * @return panel z�kladn�ch informac� o sign�lu
	 */
	private Container infoPanel() {
		underActionsPanel = new JPanel();
		underActionsPanel.setLayout(new BorderLayout());

		JPanel infoJP = new JPanel();
		infoJP.setLayout(new BoxLayout(infoJP, BoxLayout.Y_AXIS));

		infoJP.add(new JLabel("Order in input file: "
				+ (channelOrderInInputFile + Const.ZERO_INDEX_SHIFT)));
		infoJP.add(new JLabel("Frequency: " + channelFrequency + " Hz"));
		infoJP.add(new JLabel("Period: " + channelPeriod + " \u03bcs"));
		infoJP.add(new JLabel("Original: " + channelOriginal));

		infoJP.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Basic informations"), BorderFactory
				.createEmptyBorder(-7, 0, 0, 0)));

		underActionsPanel.add(actionsPanel, BorderLayout.NORTH);
		underActionsPanel.add(infoJP, BorderLayout.CENTER);

		return underActionsPanel;
	}

	/**
	 * Vytvo�en�, inicializace a um�st�n� <i>SignalViewer</i>� a p�id�n�
	 * p��slu�n�ch popisk�.
	 */
	private void createInside() {
		JPanel epochComplexAvgJP = new JPanel();
		JPanel epochPartAvgJP = new JPanel();
		JPanel currentEpochJP = new JPanel();

		epochComplexAvgJP.setLayout(new BoxLayout(epochComplexAvgJP,
				BoxLayout.Y_AXIS));
		epochPartAvgJP.setLayout(new BoxLayout(epochPartAvgJP, BoxLayout.Y_AXIS));
		currentEpochJP.setLayout(new BoxLayout(currentEpochJP, BoxLayout.Y_AXIS));

		withCurrentEpochViewer = new SignalViewerPanel(epochComplexAvgJP);
		withoutCurrentEpochViewer = new SignalViewerPanel(epochPartAvgJP);
		currentEpochViewer = new SignalViewerPanel(currentEpochJP);

		final JLabel complexJL = new JLabel("Average with current epoch(s)");
		complexJL.setLabelFor(withCurrentEpochViewer);
		epochComplexAvgJP.add(complexJL);
		epochComplexAvgJP.add(withCurrentEpochViewer);

		final JLabel partJL = new JLabel("Average without current epoch(s)");
		partJL.setLabelFor(withoutCurrentEpochViewer);
		epochPartAvgJP.add(partJL);
		epochPartAvgJP.add(withoutCurrentEpochViewer);

		final JLabel currentJL = new JLabel("Current epoch(s)");
		currentJL.setLabelFor(currentEpochViewer);
		currentEpochJP.add(currentJL);
		currentEpochJP.add(currentEpochViewer);

		this.add(infoPanel());
		this.add(epochPartAvgJP);
		this.add(currentEpochJP);
		this.add(epochComplexAvgJP);
	}

	/**
	 * P�i�azen� ActionListener� ovl�dac�m prvk�m.
	 */
	private void addActionListeners() {
		signalViewerBroadcaster.addObserver(withoutCurrentEpochViewer
				.getCommunicationProvider());
		signalViewerBroadcaster.addObserver(withCurrentEpochViewer
				.getCommunicationProvider());
		signalViewerBroadcaster.addObserver(currentEpochViewer
				.getCommunicationProvider());

		withoutCurrentEpochViewer.registerObserver(signalViewerBroadcaster);
		withCurrentEpochViewer.registerObserver(signalViewerBroadcaster);
		currentEpochViewer.registerObserver(signalViewerBroadcaster);
	}

	/**
	 * P�ed�n� nov� epochy k zobrazen�. Pro ka�d� <i>SignalViewer</i> jsou
	 * vybr�na p��slu�n� data k vykreslen�. CheckBox <b>addThisEpoch</b> se
	 * nastav� na za�krtnut�/neza�krtnut� podle toho, zda tato epocha je nebo nen�
	 * zahrnut� do pr�m�ru.
	 */
	@Override
	void setEpochDataSet(EpochDataSet epochDataSet) {
		currentEpochViewer.setValues(epochDataSet.getCurrentEpochValues());
		withCurrentEpochViewer.setValues(epochDataSet
				.getAvgWithCurrentEpochValues());
		withoutCurrentEpochViewer.setValues(epochDataSet
				.getAvgWithoutCurrentEpochValues());

		actionsPanel.setEpochSelected(epochDataSet.isChecked());
		actionsPanel.setTrustFul(epochDataSet.getTrustful());
	}

	/**
	 * Nastavuje zp�sob zobrazen� sign�lu jednotliv�ch <i>SignalViewer</i>�.
	 * 
	 * @param modeOfViewersRepresentation
	 *          zp�sob zobrazen� sign�lu (pou��vaj� se konstanty t��dy
	 *          <i>SignalViewer</i>).
	 */
	@Override
	void setModeOfViewersRepresentation(int modeOfViewersRepresentation) {
		withCurrentEpochViewer.setModeOfRepresentation(modeOfViewersRepresentation);
		withoutCurrentEpochViewer
				.setModeOfRepresentation(modeOfViewersRepresentation);
		currentEpochViewer.setModeOfRepresentation(modeOfViewersRepresentation);
	}

	/**
	 * P�ibl�en� sign�l� zobrazen�ch <i>SignalViewer</i>y o hodnotu atributu
	 * <b>value</b>. Odd�len� se prov�d� p�ed�n�m parametru se z�pornou hodnotou.
	 * 
	 * @param value
	 *          hodnota, o kolik se m� prov�st p�ibl�en�.
	 */
	@Override
	void zoomBy(float value) {
		float newZoomValue = currentEpochViewer.getZoomY() + value;
		currentEpochViewer.setZoomY(newZoomValue);
		withCurrentEpochViewer.setZoomY(newZoomValue);
		withoutCurrentEpochViewer.setZoomY(newZoomValue);
	}

	/**
	 * P�ibl�en� sign�l� zobrazen�ch <i>SignalViewer</i>y o hodnotu atributu
	 * <b>value</b>. Odd�len� se prov�d� p�ed�n�m parametru se z�pornou hodnotou.
	 * 
	 * @param value
	 *          hodnota, na kterou se m� prov�st p�ibl�en�.
	 */
	@Override
	void zoomTo(float value) {
		currentEpochViewer.setZoomY(value);
		withCurrentEpochViewer.setZoomY(value);
		withoutCurrentEpochViewer.setZoomY(value);
	}

	/**
	 * Nastavuje, zda maj� b�t zobrazen� sign�ly invertov�ny.
	 * 
	 * @param inverted
	 *          pokud m� b�t sign�l invertov�n, pak <i>true</i>, jinak <i>false</i>.
	 */
	@Override
	void invertedSignal(boolean inverted) {
		currentEpochViewer.setInvertedSignal(inverted);
		withCurrentEpochViewer.setInvertedSignal(inverted);
		withoutCurrentEpochViewer.setInvertedSignal(inverted);
	}

	/**
	 * Nastavuje panel akc�.
	 * 
	 * @param actionsPanel
	 *          Nov� panel akc�.
	 */
	@Override
	void setActionsPanel(ActionsPanel actionsPanel) {
		this.remove(this.actionsPanel);
		this.actionsPanel = actionsPanel;
		underActionsPanel.add(this.actionsPanel, BorderLayout.NORTH);
		this.validate();
		this.repaint();
	}

	/**
	 * Povolen�/zakaz�n� ovl�dac�ch prvk� pohled�.
	 * 
	 * @param enabled
	 *          <i>true</i> pro povolen�, <i>false</i> pro zak�z�n� ovl�dac�ch
	 *          prvk�.
	 */
	@Override
	void setEnabledOperatingElements(boolean enabled) {
		actionsPanel.setEnabledOperatingElements(enabled);
	}

	/**
	 * Vrac�, zda jsou ovl�dac� prvky pr�m�rovac�ho panelu povoleny �i zak�z�ny.
	 * 
	 * @return <code>true</code> pokud jsou povoleny, jinak <code>false</code>.
	 */
	@Override
	boolean isEnabledOperatingElements() {
		return actionsPanel.isEnabledOperatingElements();
	}

	/**
	 * Nastavuje po��tek sou�adn� soustavy v zobrazova��ch sign�lu (instance t��dy
	 * SignalViewer).
	 * 
	 * @param coordinateBasicOriginFrame
	 *          pozice po��tku soustavy sou�adnic ve framech.
	 */
	@Override
	void setSignalViewersCoordinateBasicOrigin(int coordinateBasicOriginFrame) {
		currentEpochViewer.setCoordinateBasicOrigin(coordinateBasicOriginFrame);
		withCurrentEpochViewer.setCoordinateBasicOrigin(coordinateBasicOriginFrame);
		withoutCurrentEpochViewer
				.setCoordinateBasicOrigin(coordinateBasicOriginFrame);
	}
}
