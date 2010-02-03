package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.ObjectBroadcaster;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.Averages;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;

/**
 * Pohled, kter� slou�� pro zobrazen� aktu�ln�ch pr�m�r� ve v�ech pr�m�rovan�ch
 * kan�lech.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 * 
 */
final class AveragesPreView extends AveragingPanelView {
	/**
	 * V�ka n�strojov� li�ty v pixelech.
	 */
	private static final int COMPONENT_HEIGHT = 20;
	/**
	 * <i>JScrollPane</i> na kter� je um�st�n vnit�ek pohledu.
	 */
	private JScrollPane meanPanelsScroll;
	/**
	 * Pole panel� (<i>MeanPanel</i>) pro pr�m�rov�n� epoch jednoho sign�lu. V
	 * poli jsou za sebou panely se�azeny podle po�ad� v�skytu ve vstupn�m
	 * souboru.
	 */
	private MeanPanel[] meanPanels;
	/**
	 * V�b�r zp�sobu zobrazen� sign�l�.
	 */
	private JComboBox modeOfRepresentationJCB;
	/**
	 * Tla�tko pro proveden� p�ibl�en� sign�lu na v�ech <i>SignalViewer</i>ech.
	 */
	private JButton zoomInJB;

	/**
	 * Tla�tko pro proveden� odd�len� sign�lu na v�ech <i>SignalViewer</i>ech.
	 */
	private JButton zoomOutJB;

	/**
	 * Tla��tko pro navr�cen� p�ibl�en� �i odd�len� <i>SignalViewer</i>� do
	 * p�vodn�ho zobrazen�.
	 */
	private JButton zoomOriginalJB;

	/**
	 * P�vodn� hodnota, na kterou jsou nastaveny <i>SignalViewer</i>y v
	 * <i>meanPanel</i>u.
	 */
	private float zoomOriginal;

	/**
	 * Vyvol�v� export pr�m�r� sign�l�.
	 */
	private JButton exportJB;
	/**
	 * Krok, o kter� prob�h� p�ibl�en� nebo odd�len� sign�lu v <i>SignalViewer</i>ech.
	 */
	private float zoomStep;
	/**
	 * Container n�strojov� li�ty.
	 */
	private Container toolBarPanelJP;
	/**
	 * Zaji��uje p�epos�l�n� informac� mezi pr�m�rovac�mi panely.
	 */
	private ObjectBroadcaster broadcaster;

	/**
	 * Vytv��� pohled pr�m�rovac�ho okna, kter� zobrazuje aktu�ln� pr�m�ry.
	 * 
	 * @param averagingWindow
	 *          Reference na <code>AveragingWindow</code>, ve kter�m je
	 *          vytv��en� pohled um�st�n.
	 */
	AveragesPreView(AveragingPanel averagingWindow) {
		super(averagingWindow);
		this.meanPanelsScroll = null;
		this.zoomStep = 0.03f;
		this.zoomOriginal = SignalViewerPanel.ZOOM_Y_ORIGINAL;
		toolBarInit();
		addActionListeners(); // p�i�azen� actionListener� komponent�m

		setEnabledOperatingElements(false);
	}

	/**
	 * Vytvo�en�, inicializace a nastaven� layoutu pohledu.
	 * 
	 * @return Layout pohledu.
	 */
	private LayoutManager layoutInit() {
		int cols = 3; // pr�m�ry jsou zobrazov�ny ve t�ech sloupc�ch
		// po�et sign�l� na jeden sloupec - desetinn� ��slo
		float signalsCountToCols = averagingWindow.getAveragingWindowProvider()
				.getAvaragedSignalsIndexes().size()
				/ ((float) cols);
		// po�et sign�l� na jeden sloupec - cel� ��slo
		int rows = (int) signalsCountToCols;
		/*
		 * Kdy� pod�l sloupc� v��i po�tu kan�l� je desetinn� ��slo s nenulovou
		 * desetinnou ��st�, pak je pot�eba p�idat jeden ��dek nav�c.
		 */
		if (signalsCountToCols - rows > 0) {
			rows += 1;
		}

		GridLayout layout = new GridLayout(rows, cols);
		layout.setHgap(10);
		layout.setVgap(10);

		return layout;
	}

	/**
	 * Registrace poslucha�� atribut� pohledu.
	 */
	private void addActionListeners() {
		modeOfRepresentationJCB.addActionListener(new ALmodeOfRepresentationJCB());
		exportJB.addActionListener(new ALexportJB());

		zoomInJB.addActionListener(new ALzoomInJB());
		zoomOutJB.addActionListener(new ALzoomOutJB());
		zoomOriginalJB.addActionListener(new ALzoomOriginalJB());
	}

	/**
	 * Vytv��� pr�m�rovac� panely pohledu a pos�l� zpr�vu
	 * <code>INSIDE_READY</code>.
	 */
	@Override
	void createMeanPanels() {
		int meanPanelsCount = averagingWindow.getAveragingWindowProvider()
				.getAvaragedSignalsIndexes().size();

		if (meanPanelsCount > 0) {
			JPanel meansPanel = new JPanel(layoutInit());
			meansPanel.setBackground(Color.LIGHT_GRAY);
			meanPanels = new MeanPanel[meanPanelsCount];
			this.broadcaster = new ObjectBroadcaster();
			boolean invertedSignal = averagingWindow.getAveragingWindowProvider()
					.getInvertedSignalsView();

			for (int i = 0; i < meanPanelsCount; i++) {
				meanPanels[i] = new AveragesPreViewMeanPanel(averagingWindow
						.getAveragingWindowProvider().getAvaragedSignalsIndexes().get(i),
						averagingWindow.getAveragingWindowProvider());

				meanPanels[i].setModeOfViewersRepresentation(modeOfRepresentationJCB
						.getSelectedIndex());
				meanPanels[i].invertedSignal(invertedSignal);
				meanPanels[i].setSignalViewersCoordinateBasicOrigin(averagingWindow
						.getAveragingWindowProvider().getLeftEpochBorderInFrames());
				meansPanel.add(meanPanels[i]);
				broadcaster.addObserver(meanPanels[i].getObjectBroadcaster());
				meanPanels[i].getObjectBroadcaster().addObserver(broadcaster);
			}

			meanPanelsScroll = new JScrollPane(meansPanel);

			sendObjectToObservers(INSIDE_READY);
		}
	}

	/**
	 * Vytv��� pr�m�rovac� panely pohledu a pos�l� zpr�vu
	 * <code>TOOL_BAR_READY</code>.
	 */
	private void toolBarInit() {
		toolBarPanelJP = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JToolBar signalToolBarJTB = new JToolBar();
		zoomInJB = new JButton();
		zoomInJB.setToolTipText("Zoom in");
		zoomInJB.setIcon(averagingWindow.getAveragingWindowProvider().getIcon(
				"magvp.png"));
		zoomInJB.setPreferredSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomInJB.setMinimumSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomInJB.setMaximumSize(new Dimension(30, COMPONENT_HEIGHT));

		zoomOutJB = new JButton();
		zoomOutJB.setToolTipText("Zoom out");
		zoomOutJB.setIcon(averagingWindow.getAveragingWindowProvider().getIcon(
				"magvm.png"));
		zoomOutJB.setPreferredSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomOutJB.setMinimumSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomOutJB.setMaximumSize(new Dimension(30, COMPONENT_HEIGHT));

		zoomOriginalJB = new JButton("Zoom original");
		zoomOriginalJB.setPreferredSize(new Dimension(70, COMPONENT_HEIGHT));
		zoomOriginalJB.setMinimumSize(new Dimension(70, COMPONENT_HEIGHT));
		zoomOriginalJB.setMaximumSize(new Dimension(70, COMPONENT_HEIGHT));

		modeOfRepresentationJCB = new JComboBox(
				SignalViewerPanel.MODE_OF_REPRESENTATION_TYPES);
		modeOfRepresentationJCB.setSelectedIndex(1);
		modeOfRepresentationJCB.setPreferredSize(new Dimension(140,
				COMPONENT_HEIGHT));
		modeOfRepresentationJCB
				.setMinimumSize(new Dimension(140, COMPONENT_HEIGHT));
		modeOfRepresentationJCB
				.setMaximumSize(new Dimension(140, COMPONENT_HEIGHT));

		exportJB = new JButton("Export averages");
		exportJB.setMinimumSize(new Dimension(100, COMPONENT_HEIGHT));
		exportJB.setPreferredSize(new Dimension(100, COMPONENT_HEIGHT));
		exportJB.setMaximumSize(new Dimension(100, COMPONENT_HEIGHT));

		signalToolBarJTB.add(zoomOutJB);
		signalToolBarJTB.add(zoomInJB);
		signalToolBarJTB.add(zoomOriginalJB);
		final JLabel viewJL = new JLabel("   View: ");
		viewJL.setLabelFor(modeOfRepresentationJCB);
		signalToolBarJTB.add(viewJL);
		signalToolBarJTB.add(modeOfRepresentationJCB);
		signalToolBarJTB.add(exportJB);

		signalToolBarJTB.setFloatable(false);

		toolBarPanelJP.add(signalToolBarJTB);

		sendObjectToObservers(TOOL_BAR_READY);
	}

	/**
	 * Vrac� referenci na atribut <code>toolBarPanelJP</code>.
	 * 
	 * @return N�strojov� li�ta pohledu.
	 */
	@Override
	Container getToolBar() {
		return toolBarPanelJP;
	}

	/**
	 * Vrac� referenci na atribut <code>meanPanelsScroll</code>.
	 * 
	 * @return Kontejner s pr�m�rovac�mi panely.
	 */
	@Override
	Container getInside() {
		return meanPanelsScroll;
	}

	/**
	 * Nastavuje ��slo pr�v� zobrazovan� epochy. T�lo metody je pr�zdn�.
	 * 
	 * @param currentEpochNumber
	 *          ��slo aktu�ln� epochy.
	 */
	@Override
	void setCurrentEpochNumber(int currentEpochNumber) {
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
		modeOfRepresentationJCB.setEnabled(enabled);
		exportJB.setEnabled(enabled);
		zoomInJB.setEnabled(enabled);
		zoomOutJB.setEnabled(enabled);
		zoomOriginalJB.setEnabled(enabled);
	}

	/**
	 * Nastavuje po�et epoch, kter� byly p�ed�ny k pr�m�rov�n�. T�lo metody je
	 * pr�zdn�.
	 * 
	 * @param epochCount
	 *          po�et epoch p�edan�ch k pr�m�rov�n�.
	 */
	@Override
	void setEpochCount(int epochCount) {
	}

	/**
	 * Nastavuje zobrazen� pohledu v z�vislosti na atributech pr�v� zobrazovan�ho
	 * projektu.
	 */
	@Override
	void viewSetupByProject() {
		averagingWindow.getAveragingWindowProvider().updateEpochDataSet();
	}

	/**
	 * Metoda je vol�na metodou <b>update</b> t��dy <i>CommunicationProvider</i>
	 * a p�ed�v� tak obdr�en� objekty do t�to metody pro dal�� zpracov�n�. T�lo
	 * metody je pr�zdn�.
	 * 
	 * @param arg0
	 *          reference na t��du, kter� pos�l� objekt.
	 * @param arg1
	 *          objekt popisuj�c� zm�nu.
	 */
	@Override
	void update(Observable arg0, Object arg1) {
	}

	/**
	 * Metoda slou�� k p�ed�n� nov�ch dat k zobrazen�.
	 * 
	 * @param epochDataSet
	 *          nov� data k zobrazen�.
	 */
	@Override
	void updateEpochDataSet(List<EpochDataSet> epochDataSet) {
		if (meanPanelsScroll == null) {
			createMeanPanels();
		}

		if (epochDataSet.size() >= meanPanels.length) {
			for (int i = 0; i < meanPanels.length; i++) {
				meanPanels[i].setEpochDataSet(epochDataSet.get(i));
			}
		} else {
			for (int i = 0; i < meanPanels.length; i++) {
				meanPanels[i].setEpochDataSet(new EpochDataSet(-1, false,
						Averages.UNKNOWN, null, null, null, null));
			}
		}

	}

	/**
	 * Nastavuje, zda se maj� sign�ly zobrazovat invertovan�.
	 * 
	 * @param inverted
	 *          <b>true</b> pro invertovan� zobrazen�, <b>false</b> pro norm�ln�
	 *          zobrazen�.
	 */
	@Override
	void setInvertedSignalsView(boolean inverted) {
		for (MeanPanel panel : meanPanels) {
			panel.invertedSignal(inverted);
		}
	}

	/**
	 * Nastavuje zp�sob zobrazen� sign�l� pro ka�d� zobrazen� pr�m�rovac� panel.
	 * 
	 * @param modeOfMeanPanelsRepresentation
	 *          zp�sob zobrazen� sign�l� (pou��vaj� se konstanty t��dy
	 *          <i>SingalViewer</i>).
	 */
	private void setModeOfMeanPanelsRepresentation(
			int modeOfMeanPanelsRepresentation) {
		for (MeanPanel meanPanel : meanPanels) {
			meanPanel.setModeOfViewersRepresentation(modeOfMeanPanelsRepresentation);
		}
	}

	/**
	 * Vol� metody <i>MeanPanel</i>� pro zoomov�n� o hodnotu parametru <b>value</b>.
	 * 
	 * @param value
	 *          hodnota zm�ny zoomu.
	 */
	private void zoomBy(float value) {
		for (MeanPanel panel : meanPanels) {
			panel.zoomBy(value);
		}
	}

	/**
	 * Vol� metody <i>MeanPanel</i>� pro zoomov�n� na hodnotu parametru <b>value</b>.
	 * 
	 * @param value
	 *          nov� hodnota zoomu.
	 */
	private void zoomTo(float value) {
		for (MeanPanel panel : meanPanels) {
			panel.zoomTo(value);
		}
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
		if (meanPanels != null) {
			for (MeanPanel panel : meanPanels) {
				panel.setSignalViewersCoordinateBasicOrigin(coordinateBasicOriginFrame);
			}
		}
	}

	/**
	 * ActionListener v�b�rov�ho boxu <b>modeOfRepresentationJCB</b>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALmodeOfRepresentationJCB implements ActionListener {
		/**
		 * Na z�klad� vybran� polo�ky v <b>modeOfRepresentationJCB</b> vol� metodu
		 * pro nastaven� zp�sobu zobrazen� sign�l� v <i>MeanPanel</i>ech s
		 * p��slu�n�m parametrem.
		 * 
		 * @param event
		 *          nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			int choice = modeOfRepresentationJCB.getSelectedIndex();

			/*
			 * dalo by se ps�t "setModeOfMeanPanelsRepresentation(choice);" a cel�
			 * switch vynechat za p�edpokladu, �e index po�adovan�ho zp�sobu zobrazen�
			 * ve v�b�rov�m menu odpov�d� hodnot� p��slu�n� konstanty - co� obecn�
			 * neplat�
			 */
			switch (choice) {
			case 0:
				setModeOfMeanPanelsRepresentation(SignalViewerPanel.FUNCTIONAL_VALUES);
				break;
			case 1:
				setModeOfMeanPanelsRepresentation(SignalViewerPanel.INTERPOLATION);
				break;
			default:
				setModeOfMeanPanelsRepresentation(SignalViewerPanel.FUNCTIONAL_VALUES_AND_INTERPOLATION);
			}

		}
	}

	/**
	 * ActionListener tla��tka <b>exportJB</b>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALexportJB implements ActionListener {
		/**
		 * Vol� metodu pro spu�t�n� exportu t��dy <i>AveragingWindow</i>.
		 * 
		 * @param event
		 *          nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			averagingWindow.runAveragesExport();
		}
	}

	/**
	 * ActionListener tla��tka <b>zoomInJB</b>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALzoomInJB implements ActionListener {
		/**
		 * Vol� metodu <b>zoomBy</b> s parametrem <b>zoomStep</b>.
		 * 
		 * @param event
		 *          nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			zoomBy(zoomStep);
		}
	}

	/**
	 * ActionListener tla��tka <b>zoomOutJB</b>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALzoomOutJB implements ActionListener {
		/**
		 * Vol� metodu <b>zoomBy</b> s parametrem <b>-zoomStep</b>.
		 * 
		 * @param event
		 *          nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			zoomBy(-zoomStep);
		}
	}

	/**
	 * ActionListener tla��tka <b>zoomOriginalJB</b>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALzoomOriginalJB implements ActionListener {
		/**
		 * Vol� metodu <b>zoomTo</b>.
		 * 
		 * @param event
		 *          nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			zoomTo(zoomOriginal);
		}
	}
}
