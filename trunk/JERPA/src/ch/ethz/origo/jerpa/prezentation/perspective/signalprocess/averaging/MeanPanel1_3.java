package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;

/**
 * Komponenta zobrazuj�c� pr�m�rov�n� jednoho sign�lu v epo�e. V komponent� jsou
 * sign�ly zobrazov�ny v jedn� ��dce vedle sebe a panel informac� jako jedna
 * ��dka nad nimi. Pomoc� instance t��dy <i>SignalViewer</i> zobrazuje pr�b�h
 * sign�lu v pr�v� vybran� epo�e, v�sledn� pr�m�r se zahrnut�m t�to epochy a
 * v�sledn� pr�m�r bez zahrnut� t�to epochy. Ke ka�d�mu sign�lu zobrazuje jeho
 * z�kladn� informace, kter� je mo�n� z�skat z hlavi�kov�ho souboru (t��da
 * <i>Header</i>).
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 * 
 */
@SuppressWarnings("serial")
final class MeanPanel1_3 extends MeanPanel {

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

	/**
	 * Vytv��� instance t��dy. Nastavuje r�me�ek kolem panelu a do titulku r�me�ku
	 * vkl�d� jm�no p��slu�n�ho kan�lu.
	 * 
	 * @param channelOrderInInputFile
	 *          po�ad� p��slu�n�ho kan�lu ve vstupn�m souboru.
	 * @param averagingWindowProvider
	 *          rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	MeanPanel1_3(int channelOrderInInputFile,
			AveragingPanelProvider averagingWindowProvider) {
		super(channelOrderInInputFile, averagingWindowProvider);
		this.setLayout(layoutInit());
		createInside();

		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder(this.channelName), BorderFactory.createEmptyBorder(
				-9, 0, 0, 0))); // "-9" pro eliminaci "b�l�ho m�sta"

		addActionListeners();
	}

	/**
	 * Vytvo�en� a nastaven� atribut� layoutu.
	 * 
	 * @return layout komponenty
	 */
	private LayoutManager layoutInit() {
		return new BorderLayout();
	}

	/**
	 * Vytvo�en� panelu, kter� zobrazuje z�kladn� informace o sign�lu + obsahuje
	 * za�krt�vac� pol��ko pro zahrnut� sign�lu do v�sledn�ho pr�m�ru.
	 * 
	 * @return panel z�kladn�ch informac� o sign�lu
	 */
	private Container infoPanel() {
		underActionsPanel = new JPanel(new BorderLayout());

		FlowLayout infosLayout = new FlowLayout(FlowLayout.LEFT);
		JPanel infos = new JPanel(infosLayout);

		underActionsPanel.add(actionsPanel, BorderLayout.WEST);
		infos.add(new JLabel("      |  Order in input file: "
				+ (channelOrderInInputFile + Const.ZERO_INDEX_SHIFT) + "  |  "));
		infos.add(new JLabel("Frequency: " + channelFrequency + " Hz" + "  |  "));
		infos.add(new JLabel("Period: " + channelPeriod + " \u03bcs" + "  |  "));
		infos.add(new JLabel("Original: " + channelOriginal + "  |"));

		underActionsPanel.add(infos, BorderLayout.CENTER);

		return underActionsPanel;
	}

	/**
	 * Vytvo�en�, inicializace a um�st�n� <i>SignalViewer</i>� a p�id�n�
	 * p��slu�n�ch popisk�.
	 */
	private void createInside() {
		JPanel under = new JPanel();
		GridLayout underLayout = new GridLayout(1, 3);
		underLayout.setHgap(10);
		under.setLayout(underLayout);

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

		currentEpochJP.setMinimumSize(new Dimension(currentEpochViewer.getWidth(),
				100));
		currentEpochJP.setMaximumSize(new Dimension(currentEpochViewer.getWidth(),
				100));
		currentEpochJP.setPreferredSize(new Dimension(
				currentEpochViewer.getWidth(), 100));

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

		this.add(infoPanel(), BorderLayout.NORTH);
		under.add(currentEpochJP);
		under.add(epochComplexAvgJP);
		under.add(epochPartAvgJP);
		this.add(under, BorderLayout.CENTER);
	}

	/**
	 * P�i�azen� ActionListener� ovl�dac�m prvk�m.
	 */
	private void addActionListeners() {
		objectBroadcaster.addObserver(withoutCurrentEpochViewer
				.getCommunicationProvider());
		objectBroadcaster.addObserver(withCurrentEpochViewer
				.getCommunicationProvider());
		objectBroadcaster
				.addObserver(currentEpochViewer.getCommunicationProvider());

		withoutCurrentEpochViewer.registerObserver(objectBroadcaster);
		withCurrentEpochViewer.registerObserver(objectBroadcaster);
		currentEpochViewer.registerObserver(objectBroadcaster);
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
		underActionsPanel.add(this.actionsPanel, BorderLayout.WEST);
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
