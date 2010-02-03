package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.ethz.origo.jerpa.application.Const;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.Averages;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;

/**
 * Pohled slou��c� k pr�m�rov�n� sign�l�. Umo��uje pohyb v epoch�ch vp�ed a
 * vzad, skok na po�adov�m ��slem zadanou epochu, vybr�n� epoch, kter� se maj�
 * zahrnout do v�sledn�ho pr�m�ru. Funk�nost poskytovan� instancemi t��dy
 * <i>MeanPanel</i>: zobrazuje pr�b�h sign�lu v pr�v� vybran� epo�e, v�sledn�
 * pr�m�r se zahrnut�m t�to epochy a v�sledn� pr�m�r bez zahrnut� t�to epochy.
 * Ke ka�d�mu kan�lu zobrazuje jeho z�kladn� informace, kter� je mo�n� z�skat z
 * hlavi�kov�ho souboru (t��da <i>Header</i>).
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 * 
 */
@SuppressWarnings("serial")
final class EpochByEpochView extends AveragingPanelView {
	/**
	 * Pole n�zv� typ� pr�m�rovac�ch panel�.
	 */
	static final String[] MEAN_PANELS_LOOK_TYPES = { "1-3", "2-2" };
	/**
	 * Konstanta popisuj�c� vzhled panelu t��dy <i>MeanPanel1_3</i>.
	 */
	static final int MEAN_PANELS_LOOK_TYPE_1_3 = 0;
	/**
	 * Konstanta popisuj�c� vzhled panelu t��dy <i>MeanPanel2_2</i>.
	 */
	static final int MEAN_PANELS_LOOK_TYPE_2_2 = 1;
	/**
	 * ��t�t�zec odd�luj�c� ��slo prvn� zahrnovan� epochy od ��sla posledn�
	 * zahrnovan� epochy.
	 */
	private static final String EPOCH_END_PREFIX = " - ";
	/**
	 * �et�zec odd�luj�c� ��slo posledn� zahrnovan� epochy od celkov�ho po�tu
	 * epoch.
	 */
	private static final String EPOCH_END_POSTFIX = " ";
	/**
	 * �et�tec uvozuj�c� zobrazen� informace o po�tu epoch p�edan�ch k
	 * pr�m�rov�n�.
	 */
	private static final String EPOCH_COUNT_PREFIX = " / ";
	/**
	 * �et�tec ukon�uj�c� zobrazen� informace o po�tu epoch p�edan�ch k
	 * pr�m�rov�n�.
	 */
	private static final String EPOCH_COUNT_POSTFIX = "  ";
	/**
	 * V�ka n�strojov� li�ty v pixelech.
	 */
	private static final int PREVIOUS_NEXT_HEIGHT = 20;
	/**
	 * Tla��tko p�echodu na p�edchoz� epochu.
	 */
	private JButton previousJB;
	/**
	 * Tla��tko p�echodu na n�sleduj�c� epochu.
	 */
	private JButton nextJB;
	/**
	 * Slou�� k v�b�ru pou�it�ho pr�m�ru.
	 */
	private JComboBox averageTypeJCB;
	/**
	 * Nastaven� po�tu epoch, po kolika se proch�zej� a zahrnuj� do pr�m�ru.
	 */
	private JSpinner stepJS;
	/**
	 * Vyvol�v� export pr�m�r� sign�l�.
	 */
	private JButton exportJB;
	/**
	 * Pole panel� (<i>MeanPanel</i>) pro pr�m�rov�n� epoch jednoho sign�lu. V
	 * poli jsou za sebou panely se�azeny podle po�ad� v�skytu ve vstupn�m
	 * souboru.
	 */
	private MeanPanel[] meanPanels;

	/**
	 * <i>JScrollPane</i> na kter� je um�st�n vnit�ek pohledu.
	 */
	private JScrollPane meanPanelsScroll;

	/**
	 * V�b�r zp�sobu zobrazen� sign�l�.
	 */
	private JComboBox modeOfRepresentationJCB;

	/**
	 * V�b�r uspo��d�n� <i>SignalViewer</i>� a panelu informac� v panelech
	 * jednotliv�ch kan�l�.
	 */
	private JComboBox meanPanelsLookJCB;

	/**
	 * Zobrazuje ��slo pr�v� pr�m�rovan� epochy.
	 */
	private JSpinner epochNumberJS;

	/**
	 * Zobrazuje informaci o celkov�m po�tu epoch, kter� byly p�ed�ny k
	 * pr�m�rov�n�.
	 */
	private JLabel epochCountJL;

	/**
	 * Index posledn� epochy.
	 */
	private int epochMaxNumber;

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
	 * Krok, o kter� prob�h� p�ibl�en� nebo odd�len� sign�lu v <i>SignalViewer</i>ech.
	 */
	private float zoomStep;

	/**
	 * Container n�strojov� li�ty.
	 */
	private Container toolBarPanelJP;

	/**
	 * Nastavuje zobrazen� pohledu v z�vislosti na atributech pr�v� zobrazovan�ho
	 * projektu.
	 */
	@Override
	void viewSetupByProject() {
		averagingWindow.getAveragingWindowProvider().updateEpochDataSet();
		stepJS.setValue(averagingWindow.getAveragingWindowProvider()
				.getEpochWalkingStep());
	}

	/**
	 * Vytv��� novou instanci t��dy. Bu� inicializje atributy t��dy p��mo nebo
	 * vol� metody, kter� je inicializuj�. Vol� metodu pro p�i�azen�
	 * <i>ActionListener</i>� ovl�dac�m prvk�m pohledu. Zakazuje pou�it�
	 * ovl�dac�ch prvk� pohledu.
	 * 
	 * @param averagingWindow
	 *          komponenta, v n� je pohled um�st�n.
	 */
	EpochByEpochView(AveragingPanel averagingWindow) {
		super(averagingWindow);
		this.meanPanelsScroll = null;
		this.zoomStep = 0.03f;
		this.zoomOriginal = SignalViewerPanel.ZOOM_Y_ORIGINAL;
		toolBarInit();
		addActionListeners(); // p�i�azen� actionListener� komponent�m
	}

	/**
	 * Vytvo�en� pr�m�rovac�ch panel�. Pro ka�d� sign�l je vytvo�en jeden
	 * pr�m�rovac� panel. Vytvo�en� pr�m�rovac� panely jsou p�i�azeny do
	 * <b>meanPanelsScroll</b> a <i>AveragingWindow</i> je informov�no o
	 * p�ipravenosti vnit�ku pohledu k zobrazen�.
	 */
	@Override
	void createMeanPanels() {
		int meanPanelsCount = averagingWindow.getAveragingWindowProvider()
				.getAvaragedSignalsIndexes().size();

		if (meanPanelsCount > 0) {
			JPanel meansPanel = new JPanel();
			meansPanel.setLayout(new BoxLayout(meansPanel, BoxLayout.Y_AXIS));

			meanPanels = new MeanPanel[meanPanelsCount];

			boolean invertedSignal = averagingWindow.getAveragingWindowProvider()
					.getInvertedSignalsView();

			for (int i = 0; i < meanPanelsCount; i++) {
				switch (meanPanelsLookJCB.getSelectedIndex()) {
				case MEAN_PANELS_LOOK_TYPE_1_3:
					meanPanels[i] = new MeanPanel1_3(averagingWindow
							.getAveragingWindowProvider().getAvaragedSignalsIndexes().get(i),
							averagingWindow.getAveragingWindowProvider());
					break;
				default:
					meanPanels[i] = new MeanPanel2_2(averagingWindow
							.getAveragingWindowProvider().getAvaragedSignalsIndexes().get(i),
							averagingWindow.getAveragingWindowProvider());
				}

				meanPanels[i].setActionsPanel(new EpochAddingAP(meanPanels[i]));
				meanPanels[i].setModeOfViewersRepresentation(modeOfRepresentationJCB
						.getSelectedIndex());
				meanPanels[i].invertedSignal(invertedSignal);
				meanPanels[i].setSignalViewersCoordinateBasicOrigin(averagingWindow
						.getAveragingWindowProvider().getLeftEpochBorderInFrames());
				meansPanel.add(meanPanels[i]);
			}

			meanPanelsScroll = new JScrollPane(meansPanel);
			sendObjectToObservers(INSIDE_READY);
		}
	}

	/**
	 * Vytv��� n�strojovou li�tu pohledu pro pohyb mezi jednotliv�mi epochami.
	 * D�le slou�� pro ovl�d�n� v�ech funkc� pohledu krom zahrnov�n� sign�l� do
	 * pr�m�ru (toto za�krt�vac� tla��tko se nach�z� na jednotliv�ch <i>MeanPanel</i>ech).
	 * Po vytvo�en� n�strojov� li�ty je <i>AveragingWindow</i> informov�no o
	 * p�ipravenosti n�strojov� li�ty k zobrazen�.
	 */
	private void toolBarInit() {
		toolBarPanelJP = new JPanel(new GridLayout(2, 1));
		JToolBar firstToolBarJTB = new JToolBar();
		JToolBar epochWalkingJTB = new JToolBar();
		JToolBar averagingJTB = new JToolBar();
		JToolBar otherJTB = new JToolBar();
		// toolBar.setAutoscrolls(true);
		epochWalkingJTB.setRollover(true);
		averagingJTB.setRollover(true);
		otherJTB.setRollover(true);
		previousJB = new JButton("Previous");
		// Zaji�t�n� konstantn� velikosti tla��tka.
		previousJB.setPreferredSize(new Dimension(70, PREVIOUS_NEXT_HEIGHT));
		previousJB.setMinimumSize(new Dimension(70, PREVIOUS_NEXT_HEIGHT));
		previousJB.setMaximumSize(new Dimension(70, PREVIOUS_NEXT_HEIGHT));

		nextJB = new JButton("Next");
		// Zaji�t�n� konstantn� velikosti tla��tka.
		nextJB.setPreferredSize(new Dimension(70, PREVIOUS_NEXT_HEIGHT));
		nextJB.setMinimumSize(new Dimension(70, PREVIOUS_NEXT_HEIGHT));
		nextJB.setMaximumSize(new Dimension(70, PREVIOUS_NEXT_HEIGHT));

		epochNumberJS = new JSpinner(new SpinnerNumberModel(0, 0, 0, 1));
		// Zaji�t�n� konstantn� velikosti komponenty.
		epochNumberJS.setMinimumSize(new Dimension(50, PREVIOUS_NEXT_HEIGHT));
		epochNumberJS.setPreferredSize(new Dimension(50, PREVIOUS_NEXT_HEIGHT));
		epochNumberJS.setMaximumSize(new Dimension(50, PREVIOUS_NEXT_HEIGHT));
		epochCountJL = new JLabel(EPOCH_COUNT_PREFIX + "0" + EPOCH_COUNT_POSTFIX);

		stepJS = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
		stepJS.setMinimumSize(new Dimension(50, PREVIOUS_NEXT_HEIGHT));
		stepJS.setPreferredSize(new Dimension(50, PREVIOUS_NEXT_HEIGHT));
		stepJS.setMaximumSize(new Dimension(50, PREVIOUS_NEXT_HEIGHT));

		averageTypeJCB = new JComboBox(Averages.AVERAGE_TYPES);
		averageTypeJCB.setSelectedIndex(averagingWindow
				.getAveragingWindowProvider().getAverageType());
		averageTypeJCB.setPreferredSize(new Dimension(100, PREVIOUS_NEXT_HEIGHT));
		averageTypeJCB.setMinimumSize(new Dimension(100, PREVIOUS_NEXT_HEIGHT));
		averageTypeJCB.setMaximumSize(new Dimension(100, PREVIOUS_NEXT_HEIGHT));

		meanPanelsLookJCB = new JComboBox(MEAN_PANELS_LOOK_TYPES);
		meanPanelsLookJCB.setSelectedIndex(1);
		meanPanelsLookJCB.setPreferredSize(new Dimension(45, PREVIOUS_NEXT_HEIGHT));
		meanPanelsLookJCB.setMinimumSize(new Dimension(45, PREVIOUS_NEXT_HEIGHT));
		meanPanelsLookJCB.setMaximumSize(new Dimension(45, PREVIOUS_NEXT_HEIGHT));

		epochWalkingJTB.add(previousJB);
		epochWalkingJTB.add(nextJB);
		final JLabel epochNumberJL = new JLabel(" Epoch number: ");
		epochNumberJL.setLabelFor(epochNumberJS);
		epochWalkingJTB.add(epochNumberJL);
		epochWalkingJTB.add(epochNumberJS);
		epochWalkingJTB.add(epochCountJL);
		final JLabel stepJL = new JLabel("  Step: ");
		stepJL.setLabelFor(stepJS);
		epochWalkingJTB.add(stepJL);
		epochWalkingJTB.add(stepJS);
		final JLabel averageTypeJL = new JLabel(" Average type: ");
		averageTypeJL.setLabelFor(averageTypeJCB);
		averagingJTB.add(averageTypeJL);
		averagingJTB.add(averageTypeJCB);
		final JLabel panelsLookJL = new JLabel(" Panels look: ");
		panelsLookJL.setLabelFor(meanPanelsLookJCB);
		otherJTB.add(panelsLookJL);
		otherJTB.add(meanPanelsLookJCB);

		epochWalkingJTB.setFloatable(false);
		averagingJTB.setFloatable(false);

		otherJTB.setFloatable(false);
		firstToolBarJTB.add(epochWalkingJTB);
		firstToolBarJTB.add(averagingJTB);
		firstToolBarJTB.add(otherJTB);
		firstToolBarJTB.setFloatable(false);

		JToolBar secondToolBarJTB = new JToolBar();
		JToolBar signalToolBarJTB = new JToolBar();
		zoomInJB = new JButton();
		zoomInJB.setToolTipText("Zoom in");
		zoomInJB.setIcon(averagingWindow.getAveragingWindowProvider().getIcon(
				"magvp.png"));
		zoomInJB.setPreferredSize(new Dimension(30, PREVIOUS_NEXT_HEIGHT));
		zoomInJB.setMinimumSize(new Dimension(30, PREVIOUS_NEXT_HEIGHT));
		zoomInJB.setMaximumSize(new Dimension(30, PREVIOUS_NEXT_HEIGHT));

		zoomOutJB = new JButton();
		zoomOutJB.setToolTipText("Zoom out");
		zoomOutJB.setIcon(averagingWindow.getAveragingWindowProvider().getIcon(
				"magvm.png"));
		zoomOutJB.setPreferredSize(new Dimension(30, PREVIOUS_NEXT_HEIGHT));
		zoomOutJB.setMinimumSize(new Dimension(30, PREVIOUS_NEXT_HEIGHT));
		zoomOutJB.setMaximumSize(new Dimension(30, PREVIOUS_NEXT_HEIGHT));

		zoomOriginalJB = new JButton("Zoom original");
		zoomOriginalJB.setPreferredSize(new Dimension(70, PREVIOUS_NEXT_HEIGHT));
		zoomOriginalJB.setMinimumSize(new Dimension(70, PREVIOUS_NEXT_HEIGHT));
		zoomOriginalJB.setMaximumSize(new Dimension(70, PREVIOUS_NEXT_HEIGHT));

		modeOfRepresentationJCB = new JComboBox(
				SignalViewerPanel.MODE_OF_REPRESENTATION_TYPES);
		modeOfRepresentationJCB.setSelectedIndex(1);
		modeOfRepresentationJCB.setPreferredSize(new Dimension(140,
				PREVIOUS_NEXT_HEIGHT));
		modeOfRepresentationJCB.setMinimumSize(new Dimension(140,
				PREVIOUS_NEXT_HEIGHT));
		modeOfRepresentationJCB.setMaximumSize(new Dimension(140,
				PREVIOUS_NEXT_HEIGHT));

		exportJB = new JButton("Export averages");
		// Zaji�t�n� konstantn� velikosti tla��tka.
		exportJB.setMinimumSize(new Dimension(100, PREVIOUS_NEXT_HEIGHT));
		exportJB.setPreferredSize(new Dimension(100, PREVIOUS_NEXT_HEIGHT));
		exportJB.setMaximumSize(new Dimension(100, PREVIOUS_NEXT_HEIGHT));

		signalToolBarJTB.add(zoomOutJB);
		signalToolBarJTB.add(zoomInJB);
		signalToolBarJTB.add(zoomOriginalJB);
		final JLabel viewJL = new JLabel("   View: ");
		viewJL.setLabelFor(modeOfRepresentationJCB);
		signalToolBarJTB.add(viewJL);
		signalToolBarJTB.add(modeOfRepresentationJCB);
		signalToolBarJTB.add(exportJB);

		signalToolBarJTB.setFloatable(false);

		secondToolBarJTB.add(signalToolBarJTB);
		secondToolBarJTB.setFloatable(false);

		toolBarPanelJP.add(secondToolBarJTB);
		toolBarPanelJP.add(firstToolBarJTB);

		sendObjectToObservers(TOOL_BAR_READY);
	}

	/**
	 * P�i�azuje <i>ActionListener</i>y jednotliv�m komponent�m.
	 */
	private void addActionListeners() {
		nextJB.addActionListener(new ALnextJB());
		previousJB.addActionListener(new ALpreviousJB());
		epochNumberJS.addChangeListener(new CLepochNumberJS());
		stepJS.addChangeListener(new CLstepJS());
		averageTypeJCB.addActionListener(new ALaverageTypeJCB());
		modeOfRepresentationJCB.addActionListener(new ALmodeOfRepresentationJCB());
		meanPanelsLookJCB.addActionListener(new ALmeanPanelsLookJCB());
		exportJB.addActionListener(new ALexportJB());

		zoomInJB.addActionListener(new ALzoomInJB());
		zoomOutJB.addActionListener(new ALzoomOutJB());
		zoomOriginalJB.addActionListener(new ALzoomOriginalJB());
	}

	/**
	 * Nastavuje pr�v� zobrazen�mu pohledu ��slo aktu�ln� epochy. Vol� metodu pro
	 * rozhodnut� o povolen� �i zak�z�n� tla��tek pro pohyb mezi epochami (<b>nextJB</b>,
	 * <b>previousJB</b>).
	 * 
	 * @param currentEpochNumber
	 *          ��slo aktu�ln� epochy.
	 */
	@Override
	void setCurrentEpochNumber(int currentEpochNumber) {
		epochNumberJS.setValue(currentEpochNumber + Const.ZERO_INDEX_SHIFT);
		enableOrdisableNextAndPrevious();
	}

	/**
	 * Slou�� k p�ed�n� nov�ch dat k zobrazen�. Nov� data p�ed�v� k vykreslen�
	 * jednotliv�m panel�m. Vytv��� pr�m�rovac� panely, pokud nejsou vytvo�eny.
	 * Vol� metodu pro rozhodnut� o povolen� �i zak�z�n� tla��tek pro pohyb mezi
	 * epochami (<b>nextJB</b>, <b>previousJB</b>).
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

		enableOrdisableNextAndPrevious();
	}

	/**
	 * Nastavuje po�et epoch, kter� byly p�ed�ny k pr�m�rov�n�. Upravuje
	 * <i>SpinnerNumberModel</i> atributu <b>epochNumberJS</b>, aby nebylo mo�n�
	 * p�ej�t na neexistuj�c� epochy. Vol� metodu pro rozhodnut� o povolen� �i
	 * zak�z�n� tla��tek pro pohyb mezi epochami (<b>nextJB</b>, <b>previousJB</b>).
	 */
	@Override
	void setEpochCount(int epochCount) {
		epochMaxNumber = epochCount - 1 + Const.ZERO_INDEX_SHIFT;
		int currentEpochNumber = averagingWindow.getAveragingWindowProvider()
				.getCurrentEpochNumber()
				+ Const.ZERO_INDEX_SHIFT;

		// System.out.println("minimum: " + Const.ZERO_INDEX_SHIFT + " <= value: " +
		// currentEpochNumber + " <= maximum: " + epochMaxNumber);

		if (epochMaxNumber > 0) {
			if (currentEpochNumber < Const.ZERO_INDEX_SHIFT) {
				// System.out.println(Const.ZERO_INDEX_SHIFT + ", " + epochMaxNumber);
				epochNumberJS.setModel(new SpinnerNumberModel(Const.ZERO_INDEX_SHIFT,
						Const.ZERO_INDEX_SHIFT, epochMaxNumber, averagingWindow
								.getAveragingWindowProvider().getEpochWalkingStep()));
			} else {
				epochNumberJS.setModel(new SpinnerNumberModel(currentEpochNumber,
						Const.ZERO_INDEX_SHIFT, epochMaxNumber, averagingWindow
								.getAveragingWindowProvider().getEpochWalkingStep()));
			}

			int currentStep = ((Integer) stepJS.getValue()).intValue();
			stepJS.setModel(new SpinnerNumberModel(currentStep, 1, epochCount, 1));
		}

		epochCountJL.setText(EPOCH_COUNT_PREFIX + epochMaxNumber
				+ EPOCH_COUNT_POSTFIX);

		enableOrdisableNextAndPrevious();
	}

	/**
	 * Zakazuje �i povoluje tla��tka <b>nextJB</b> a <b>previousJB</b> podle
	 * ��sla pr�v� zobrazen� epochy. Zabra�uje podte�en� nebo p�ete�en� nad Listem
	 * epoch.
	 */
	private void enableOrdisableNextAndPrevious() {
		int currentEpochNumber = ((Integer) epochNumberJS.getValue()).intValue();
		int step = averagingWindow.getAveragingWindowProvider()
				.getEpochWalkingStep();

		if ((currentEpochNumber) > Const.ZERO_INDEX_SHIFT) {
			previousJB.setEnabled(true);
		} else {
			previousJB.setEnabled(false);
		}

		if ((currentEpochNumber + 2 * (step - 1)) < epochMaxNumber) {
			nextJB.setEnabled(true);
		} else {
			nextJB.setEnabled(false);
		}

		if (averagingWindow.getAveragingWindowProvider().getEpochWalkingStep() > 1) {
			epochCountJL.setText(EPOCH_END_PREFIX + (currentEpochNumber + step - 1)
					+ EPOCH_END_POSTFIX + EPOCH_COUNT_PREFIX + epochMaxNumber
					+ EPOCH_COUNT_POSTFIX);
		} else {
			epochCountJL.setText(EPOCH_COUNT_PREFIX + epochMaxNumber
					+ EPOCH_COUNT_POSTFIX);
		}
	}

	/**
	 * Povoluje/zakazuje ovl�dac� prvky pohledu.
	 * 
	 * @param enabled
	 *          true pro povolen� nebo false pro zak�z�n� ovl�dac�ch prvk�
	 */
	@Override
	void setEnabledOperatingElements(boolean enabled) {
		if (meanPanels != null) {
			for (MeanPanel panel : meanPanels) {
				panel.setEnabledOperatingElements(enabled);
			}
		}

		previousJB.setEnabled(enabled);
		nextJB.setEnabled(enabled);
		epochNumberJS.setEnabled(enabled);
		stepJS.setEnabled(enabled);
		averageTypeJCB.setEnabled(enabled);
		modeOfRepresentationJCB.setEnabled(enabled);
		meanPanelsLookJCB.setEnabled(enabled);
		exportJB.setEnabled(enabled);

		zoomInJB.setEnabled(enabled);
		zoomOutJB.setEnabled(enabled);
		zoomOriginalJB.setEnabled(enabled);
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
	 * Vrac� n�strojovou li�tu pohledu.
	 * 
	 * @return n�strojov� li�ta pohledu.
	 */
	@Override
	Container getToolBar() {
		averageTypeJCB.setSelectedIndex(averagingWindow
				.getAveragingWindowProvider().getAverageType());
		return toolBarPanelJP;
	}

	/**
	 * Vrac� vnit�ek pohledu.
	 * 
	 * @return vnit�ek pohledu.
	 */
	@Override
	Container getInside() {
		return meanPanelsScroll;
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
	 * Metoda je vol�na metodou <b>update</b> t��dy <i>CommunicationProvider</i>
	 * a p�ed�v� tak obdr�en� objekty do t�to metody pro dal�� zpracov�n�.
	 * 
	 * @param arg0
	 *          reference na t��du, kter� pos�l� objekt.
	 * @param arg1
	 *          objekt popisuj�c� zm�nu.
	 */
	@Override
	void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * ActionListener tla��tka <b>nextJB</b>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALnextJB implements ActionListener {
		/**
		 * ��d� <b>averagingWindowProvider</b> o zve�ejn�n� dat n�sleduj�c� epochy.
		 * 
		 * @param event
		 *          nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			averagingWindow.getAveragingWindowProvider().nextEpoch();
		}
	}

	/**
	 * ActionListener tla��tka <b>previousJB</b>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALpreviousJB implements ActionListener {
		/**
		 * ��d� <b>averagingWindowProvider</b> o zve�ejn�n� dat p�edchoz� epochy.
		 * 
		 * @param event
		 *          nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			averagingWindow.getAveragingWindowProvider().previousEpoch();
		}
	}

	/**
	 * ChangeListener atributu <code>epochNumberJS</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class CLepochNumberJS implements ChangeListener {
		/**
		 * Vol� metodu <code>AveragingWindowProvider.jumpToEpoch</code> pro skok
		 * na vybranou epochu �i n-tici epoch.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void stateChanged(ChangeEvent event) {
			averagingWindow.getAveragingWindowProvider().jumpToEpoch(
					((Integer) epochNumberJS.getValue()).intValue()
							- Const.ZERO_INDEX_SHIFT);
		}
	}

	/**
	 * ChangeListener atributu <code>stepJS</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class CLstepJS implements ChangeListener {
		/**
		 * P�i zm�n� po�tu epoch tvo��c�ch n-tici nastavuje prost�ednictv�m
		 * <code>AveragingWindowProvideru</code> novou velikost n-tice epoch.
		 */
		@Override
		public void stateChanged(ChangeEvent arg0) {
			int currentEpochNumber = 0;
			int newStep = ((Integer) stepJS.getValue()).intValue();
			averagingWindow.getAveragingWindowProvider().setEpochWalkingStep(newStep);
			averagingWindow.getAveragingWindowProvider().jumpToEpoch(
					currentEpochNumber);

			epochMaxNumber = ((Integer) ((SpinnerNumberModel) (epochNumberJS
					.getModel())).getMaximum()).intValue();

			if (currentEpochNumber < Const.ZERO_INDEX_SHIFT) {
				epochNumberJS.setModel(new SpinnerNumberModel(Const.ZERO_INDEX_SHIFT,
						Const.ZERO_INDEX_SHIFT, epochMaxNumber, newStep));
			} else {
				epochNumberJS.setModel(new SpinnerNumberModel(currentEpochNumber,
						Const.ZERO_INDEX_SHIFT, epochMaxNumber, newStep));
			}

			if (averagingWindow.getAveragingWindowProvider().getEpochWalkingStep() > 1) {
				epochCountJL.setText(EPOCH_END_PREFIX + newStep + EPOCH_END_POSTFIX
						+ EPOCH_COUNT_PREFIX + epochMaxNumber + EPOCH_COUNT_POSTFIX);
			} else {
				epochCountJL.setText(EPOCH_COUNT_PREFIX + epochMaxNumber
						+ EPOCH_COUNT_POSTFIX);
			}

			enableOrdisableNextAndPrevious();
		}
	}

	/**
	 * ActionListener v�b�rov�ho boxu <code>averageTypeJCB</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALaverageTypeJCB implements ActionListener {
		/**
		 * Nastavuje nov� typ pou��van� pr�m�rovac� metody.
		 * 
		 * @param event
		 *          nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			String selectedType = (String) averageTypeJCB.getSelectedItem();
			String[] averageTypes = Averages.AVERAGE_TYPES;

			for (int i = 0; i < averageTypes.length; i++) {
				if (selectedType.equals(averageTypes[i])) {
					averagingWindow.getAveragingWindowProvider().setAverageType(i);
					return;
				}
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
	 * ActionListener v�b�rov�ho boxu <b>meanPanelsLookJCB</b>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALmeanPanelsLookJCB implements ActionListener {
		/**
		 * Vol� metodu pro vytvo�en� <i>MeanPanel</i>� a ��k� si <i>AveragingWindow</i>
		 * o zasl�n� aktu�ln�ch dat k zobrazen�.
		 * 
		 * @param event
		 *          nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			createMeanPanels();
			averagingWindow.getAveragingWindowProvider().updateEpochDataSet();
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
