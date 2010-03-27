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

import ch.ethz.origo.jerpa.application.perspective.signalprocess.ObjectBroadcaster;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;

/**
 * Panel pro skupinov� v�b�r epoch pro pr�m�rov�n�.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 */
@SuppressWarnings("serial")
final class GroupEpochsMeanPanel extends MeanPanel {
	private JPanel underActionsPanel;
	/**
	 * Zobrazen� pr�m�ru pr�b�hu sign�lu v aktu�ln�ch epoch�ch.
	 */
	private SignalViewerPanel currentEpochsViewer;

	/**
	 * Zobrazen� celkov�ho pr�m�ru sign�lu po zahrnut� aktu�ln�ch epoch do
	 * pr�m�ru.
	 */
	private SignalViewerPanel afterCurrentEpochsAddition;

	/**
	 * Zobrazen� celkov�ho pr�m�ru sign�lu po odebr�n� aktu�ln�ch epoch z pr�m�ru.
	 */
	private SignalViewerPanel currentAverigeViewer;
	/**
	 * Zobrazen� aktu�ln�ho pr�m�ru sign�lu.
	 */
	private SignalViewerPanel afterCurrentEpochsRemove;

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
	GroupEpochsMeanPanel(int channelOrderInInputFile,
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
		return new BorderLayout();
	}

	/**
	 * Vytvo�en� panelu, kter� zobrazuje z�kladn� informace o sign�lu + obsahuje
	 * za�krt�vac� pol��ko pro zahrnut� sign�lu do v�sledn�ho pr�m�ru.
	 * 
	 * @return panel z�kladn�ch informac� o sign�lu
	 */
	private Container infoPanel() {
		underActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		// BoxLayout underActionsPanelLayout = new BoxLayout(underActionsPanel,
		// BoxLayout.X_AXIS);
		// underActionsPanel.setLayout(underActionsPanelLayout);

		FlowLayout infosLayout = new FlowLayout(FlowLayout.LEFT);
		JPanel infos = new JPanel(infosLayout);

		underActionsPanel.add(actionsPanel);
		infos.add(new JLabel("      |  Order in input file: "
				+ (channelOrderInInputFile + Const.ZERO_INDEX_SHIFT) + "  |  "));
		infos.add(new JLabel("Frequency: " + channelFrequency + " Hz" + "  |  "));
		infos.add(new JLabel("Period: " + channelPeriod + " \u03bcs" + "  |  "));
		infos.add(new JLabel("Original: " + channelOriginal + "  |"));

		underActionsPanel.add(infos);
		return underActionsPanel;
	}

	/**
	 * Vytvo�en�, inicializace a um�st�n� <i>SignalViewer</i>� a p�id�n�
	 * p��slu�n�ch popisk�.
	 */
	private void createInside() {
		GridLayout viewersJPLayout = new GridLayout(2, 2);
		viewersJPLayout.setHgap(10);

		JPanel viewersJP = new JPanel(viewersJPLayout);

		JPanel afterAddingJP = new JPanel();
		JPanel currentAvgJP = new JPanel();
		JPanel currentEpochsJP = new JPanel();
		JPanel afterRemovingJP = new JPanel();

		afterAddingJP.setLayout(new BoxLayout(afterAddingJP, BoxLayout.Y_AXIS));
		currentAvgJP.setLayout(new BoxLayout(currentAvgJP, BoxLayout.Y_AXIS));
		currentEpochsJP.setLayout(new BoxLayout(currentEpochsJP, BoxLayout.Y_AXIS));
		afterRemovingJP.setLayout(new BoxLayout(afterRemovingJP, BoxLayout.Y_AXIS));

		afterCurrentEpochsAddition = new SignalViewerPanel(afterAddingJP);
		currentAverigeViewer = new SignalViewerPanel(currentAvgJP);
		currentEpochsViewer = new SignalViewerPanel(currentEpochsJP);
		afterCurrentEpochsRemove = new SignalViewerPanel(afterRemovingJP);

		currentEpochsJP.setMinimumSize(new Dimension(
				currentEpochsViewer.getWidth(), 100));
		currentEpochsJP.setMaximumSize(new Dimension(
				currentEpochsViewer.getWidth(), 100));
		currentEpochsJP.setPreferredSize(new Dimension(currentEpochsViewer
				.getWidth(), 100));

		final JLabel complexJL = new JLabel("After adding current epochs");
		complexJL.setLabelFor(afterCurrentEpochsAddition);
		afterAddingJP.add(complexJL);
		afterAddingJP.add(afterCurrentEpochsAddition);

		final JLabel partJL = new JLabel("Current average");
		partJL.setLabelFor(currentAverigeViewer);
		currentAvgJP.add(partJL);
		currentAvgJP.add(currentAverigeViewer);

		final JLabel currentJL = new JLabel("Current epochs");
		currentJL.setLabelFor(currentEpochsViewer);
		currentEpochsJP.add(currentJL);
		currentEpochsJP.add(currentEpochsViewer);

		final JLabel afterJL = new JLabel("After removing current epochs");
		afterJL.setLabelFor(afterCurrentEpochsRemove);
		afterRemovingJP.add(afterJL);
		afterRemovingJP.add(afterCurrentEpochsRemove);

		this.add(infoPanel(), BorderLayout.NORTH);

		viewersJP.add(currentAvgJP);
		viewersJP.add(afterRemovingJP);
		viewersJP.add(currentEpochsJP);
		viewersJP.add(afterAddingJP);

		this.add(viewersJP, BorderLayout.CENTER);
	}

	/**
	 * P�i�azen� ActionListener� ovl�dac�m prvk�m.
	 */
	private void addActionListeners() {
		signalViewerBroadcaster.addObserver(currentAverigeViewer
				.getCommunicationProvider());
		signalViewerBroadcaster.addObserver(afterCurrentEpochsAddition
				.getCommunicationProvider());
		signalViewerBroadcaster.addObserver(currentEpochsViewer
				.getCommunicationProvider());
		signalViewerBroadcaster.addObserver(afterCurrentEpochsRemove
				.getCommunicationProvider());

		currentAverigeViewer.registerObserver(signalViewerBroadcaster);
		afterCurrentEpochsAddition.registerObserver(signalViewerBroadcaster);
		currentEpochsViewer.registerObserver(signalViewerBroadcaster);
		afterCurrentEpochsRemove.registerObserver(signalViewerBroadcaster);
	}

	/**
	 * P�ed�n� nov� epochy k zobrazen�. Pro ka�d� <i>SignalViewer</i> jsou
	 * vybr�na p��slu�n� data k vykreslen�. CheckBox <b>addThisEpoch</b> se
	 * nastav� na za�krtnut�/neza�krtnut� podle toho, zda tato epocha je nebo nen�
	 * zahrnut� do pr�m�ru.
	 */
	@Override
	void setEpochDataSet(EpochDataSet epochDataSet) {
		currentEpochsViewer.setValues(epochDataSet.getCurrentEpochValues());
		afterCurrentEpochsAddition.setValues(epochDataSet
				.getAvgWithCurrentEpochValues());
		currentAverigeViewer.setValues(epochDataSet.getCurrentAvgValues());
		afterCurrentEpochsRemove.setValues(epochDataSet
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
		afterCurrentEpochsAddition
				.setModeOfRepresentation(modeOfViewersRepresentation);
		currentAverigeViewer.setModeOfRepresentation(modeOfViewersRepresentation);
		currentEpochsViewer.setModeOfRepresentation(modeOfViewersRepresentation);
		afterCurrentEpochsRemove
				.setModeOfRepresentation(modeOfViewersRepresentation);
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
		float newZoomValue = currentEpochsViewer.getZoomY() + value;
		currentEpochsViewer.setZoomY(newZoomValue);
		afterCurrentEpochsAddition.setZoomY(newZoomValue);
		currentAverigeViewer.setZoomY(newZoomValue);
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
		currentEpochsViewer.setZoomY(value);
		afterCurrentEpochsAddition.setZoomY(value);
		currentAverigeViewer.setZoomY(value);
	}

	/**
	 * Nastavuje, zda maj� b�t zobrazen� sign�ly invertov�ny.
	 * 
	 * @param inverted
	 *          pokud m� b�t sign�l invertov�n, pak <i>true</i>, jinak <i>false</i>.
	 */
	@Override
	void invertedSignal(boolean inverted) {
		currentEpochsViewer.setInvertedSignal(inverted);
		afterCurrentEpochsAddition.setInvertedSignal(inverted);
		currentAverigeViewer.setInvertedSignal(inverted);
	}

	@Override
	void setActionsPanel(ActionsPanel actionsPanel) {
		this.remove(this.actionsPanel);
		this.actionsPanel = actionsPanel;
		underActionsPanel.add(this.actionsPanel, BorderLayout.NORTH);
		this.validate();
		this.repaint();
	}

	@Override
	void setEnabledOperatingElements(boolean enabled) {
		actionsPanel.setEnabledOperatingElements(enabled);
	}

	@Override
	boolean isEnabledOperatingElements() {
		return actionsPanel.isEnabledOperatingElements();
	}

	@Override
	void setSignalViewersCoordinateBasicOrigin(int coordinateBasicOriginFrame) {
		currentEpochsViewer.setCoordinateBasicOrigin(coordinateBasicOriginFrame);
		afterCurrentEpochsAddition
				.setCoordinateBasicOrigin(coordinateBasicOriginFrame);
		currentAverigeViewer.setCoordinateBasicOrigin(coordinateBasicOriginFrame);
		afterCurrentEpochsRemove
				.setCoordinateBasicOrigin(coordinateBasicOriginFrame);
	}
}
