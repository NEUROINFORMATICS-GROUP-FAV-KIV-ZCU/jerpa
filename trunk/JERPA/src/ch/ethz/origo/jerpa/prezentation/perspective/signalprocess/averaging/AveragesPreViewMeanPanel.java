package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.ethz.origo.jerpa.application.Const;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;

/**
 * Pr�m�rovac� panel pro pou�it� v pohledu <code>AveragesPreView</code>.
 * Zobrazuje pouze pr�b�h aktu�ln�ho pr�m�ru pro dan� kan�l a jako panel akc�
 * obsahuje <code>NullActionsPanel</code>.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 */
@SuppressWarnings("serial")
final class AveragesPreViewMeanPanel extends MeanPanel {
	/**
	 * Zobrazen� doposud vytvo�en�ho pr�m�ru jednoho kan�lu.
	 */
	private SignalViewerPanel averageViewer;

	/**
	 * Vytv��� pr�m�rovac� panel pro pou�it� v pohledu
	 * <code>AveragesPreView</code>.
	 * 
	 * @param channelOrderInInputFile
	 * @param averagingWindowProvider
	 */
	AveragesPreViewMeanPanel(int channelOrderInInputFile,
			AveragingPanelProvider averagingWindowProvider) {
		super(channelOrderInInputFile, averagingWindowProvider);
		this.setLayout(layoutInit());
		this.setMinimumSize(new Dimension(300, 100));
		// this.setMaximumSize(new Dimension(300, 200));
		this.setPreferredSize(new Dimension(300, 100));

		averageViewer = new SignalViewerPanel(this);
		objectBroadcaster.addObserver(averageViewer.getCommunicationProvider());
		averageViewer.registerObserver(objectBroadcaster);

		JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		infoPanel.add(new JLabel(this.channelName));
		infoPanel.add(new JLabel(" ("
				+ (this.channelOrderInInputFile + Const.ZERO_INDEX_SHIFT)
				+ "rd in input file)"));
		this.add(infoPanel, BorderLayout.NORTH);
		this.add(averageViewer, BorderLayout.CENTER);
	}

	/**
	 * Vrac� layout pro tento pr�m�rovac� panel.
	 * 
	 * @return Layout pr�m�rovac�ho panelu.
	 */
	private LayoutManager layoutInit() {
		return new BorderLayout();
	}

	/**
	 * Nastavuje, zda maj� b�t zobrazen� sign�ly invertov�ny.
	 * 
	 * @param inverted
	 *          pokud m� b�t sign�l invertov�n, pak <i>true</i>, jinak <i>false</i>.
	 */
	@Override
	void invertedSignal(boolean inverted) {
		averageViewer.setInvertedSignal(inverted);
	}

	/**
	 * Metoda slou�� k p�ed�n� nov�ch dat k zobrazen�.
	 * 
	 * @param epochDataSet
	 *          nov� data k zobrazen�.
	 */
	@Override
	void setEpochDataSet(EpochDataSet epochDataSet) {
		if (epochDataSet.isChecked()) {
			averageViewer.setValues(epochDataSet.getAvgWithCurrentEpochValues());
		} else {
			averageViewer.setValues(epochDataSet.getAvgWithoutCurrentEpochValues());
		}
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
		averageViewer.setModeOfRepresentation(modeOfViewersRepresentation);
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
		float newZoomValue = averageViewer.getZoomY() + value;
		averageViewer.setZoomY(newZoomValue);
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
		averageViewer.setZoomY(value);
	}

	/**
	 * Nastavuje panel akc�.
	 * 
	 * @param actionsPanel
	 *          Nov� panel akc�.
	 */
	@Override
	void setActionsPanel(ActionsPanel actionsPanel) {
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
	}

	/**
	 * Vrac�, zda jsou ovl�dac� prvky pr�m�rovac�ho panelu povoleny �i zak�z�ny.
	 * 
	 * @return <code>true</code> pokud jsou povoleny, jinak <code>false</code>.
	 */
	@Override
	boolean isEnabledOperatingElements() {
		// TODO Auto-generated method stub
		return false;
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
		averageViewer.setCoordinateBasicOrigin(coordinateBasicOriginFrame);
	}
}
