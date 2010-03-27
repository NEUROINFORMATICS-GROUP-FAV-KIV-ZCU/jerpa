package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.Averages;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.AveragingDataManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;
import ch.ethz.origo.juigle.prezentation.SpringUtilities;

/**
 * Pohled slou��c� k hromadn�mu za�azov�n�/odeb�r�n� epoch do/z pr�m�r�.
 * Umo��uje za�azovat/odeb�rat epochy podle t�� krit�ri�:
 * <ol>
 * <li>v�echny epochy
 * <li>epochy, jejich� indexy byly na�teny ze souboru
 * <li>epochy v ur�it�m �asov�m intervalu
 * </ol>
 * Funk�nost poskytovan� instancemi t��dy <code>GroupEpochsMeanPanel</code>:
 * pro ka�d� kan�l jsou zobrazeny �ty�i pr�b�hy sign�lu (aktu�ln� pr�m�r, pr�m�r
 * skupiny epoch, pr�m�r po zahrnut� skupiny epoch, pr�m�r po odebr�n� skupiny
 * epoch). Ke ka�d�mu kan�lu zobrazuje jeho z�kladn� informace, kter� je mo�n�
 * z�skat z hlavi�kov�ho souboru (t��da <code>Header</code>).
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 */
@SuppressWarnings("serial")
final class GroupEpochsView extends AveragingPanelView {
	/**
	 * V�ka n�strojov� li�ty v pixelech.
	 */
	private static final int COMPONENT_HEIGHT = 20;
	/**
	 * V�ka panelu zp�sobu pr�ce se skupinou epoch.
	 */
	private static final int RIBBON_HEIGHT = 85;
	/**
	 * Seznam panel� pro pr�m�rov�n� epoch jednoho sign�lu. V poli jsou za sebou
	 * se�azeny podle po�ad� v�skytu ve vstupn�m souboru.
	 */
	private MeanPanel[] meanPanels;
	/**
	 * Komponenta, na kterou jsou umis�ov�ny pr�m�rovac� panely pro jednotliv�
	 * kan�ly.
	 */
	private JScrollPane meanPanelsScroll;
	/**
	 * V�b�r zp�sobu zobrazen� sign�l�.
	 */
	private JComboBox modeOfRepresentationJCB;
	/**
	 * Tla�tko pro proveden� p�ibl�en� sign�lu na v�ech SignalViewerech.
	 */
	private JButton zoomInJB;
	/**
	 * Tla�tko pro proveden� odd�len� sign�lu na v�ech SignalViewerech.
	 */
	private JButton zoomOutJB;
	/**
	 * Tla��tko pro navr�cen� p�ibl�en� �i odd�len� SignalViewer� do p�vodn�ho
	 * zobrazen�.
	 */
	private JButton zoomOriginalJB;
	/**
	 * P�vodn� hodnota, na kterou jsou nastaveny <i>SignalViewer</i>y v
	 * <i>meanPanel</i>u.
	 */
	private float zoomOriginal;
	/**
	 * Krok o kter� prob�h� p�ibl�en� nebo odd�len� sign�lu v <i>SignalViewer</i>ech.
	 */
	private float zoomStep;
	/**
	 * Slou�� k v�b�ru pou�it�ho pr�m�ru.
	 */
	private JComboBox averageTypeJCB;
	/**
	 * Panel, do kter�ho se umis�uje n�strojov� li�ta pohledu.
	 */
	private JPanel toolBarPanelJP;
	/**
	 * Komponenta, na n� se umis�uj� komponenty pro volbu skupiny epoch podle
	 * �asov�ho �seku.
	 */
	private JComponent timeSelection;
	/**
	 * Tla��tko pro aktivaci v�b�ru skupiny epoch podle �asov�ho �seku.
	 */
	private JRadioButton timeSelectionJRB;
	/**
	 * Komponenta, na n� se umis�uj� komponenty pro volbu skupiny epoch index�
	 * na�ten�ch ze souboru.
	 */
	private JComponent indexesSelection;
	/**
	 * Tla��tko pro aktivaci v�b�ru skupiny epoch podle index� na�ten�ch ze
	 * souboru.
	 */
	private JRadioButton indexesSelectionJRB;
	/**
	 * Tla��tko pro otev�en� okna v�b�ru souboru s indexy epoch.
	 */
	private JButton browseJB;
	/**
	 * Komponenta, na n� se umis�uj� komponenty pro volbu v�ech epoch.
	 */
	private JComponent allSelection;
	/**
	 * Tla��tko pro aktivaci v�b�ru v�ech epoch.
	 */
	private JRadioButton allSelectionJRB;
	/**
	 * Komponenta, do ni� se umis�uj� komponenty pro zvolen� zp�sobu pr�ce se
	 * skupinou epoch.
	 */
	private JComponent apply;
	/**
	 * Tla��tko pro v�b�r zp�sobu pr�ce se skupinou epoch - p�id�n� epoch do
	 * pr�m�r�.
	 */
	private JRadioButton addJRB;
	/**
	 * Tla��tko pro v�b�r zp�sobu pr�ce se skupinou epoch - odebr�n� epoch z
	 * pr�m�r�.
	 */
	private JRadioButton removeJRB;
	/**
	 * Tla��tko pro aplikaci zvolen�ho zp�sobu pr�ce (p�id�n�/odebr�n�) na
	 * vybranou skupinu epoch.
	 */
	private JButton applyJB;
	/**
	 * Slou�� k nastaven� po��tku �asov�ho intervalu v�b�ru skupiny epoch v
	 * milisekund�ch.
	 */
	private JSpinner fromMsJS;
	/**
	 * Slou�� k nastaven� po��tku �asov�ho intervalu v�b�ru skupiny epoch v
	 * sekund�ch.
	 */
	private JSpinner fromSecJS;
	/**
	 * Slou�� k nastaven� po��tku �asov�ho intervalu v�b�ru skupiny epoch v
	 * minut�ch.
	 */
	private JSpinner fromMinJS;
	/**
	 * Slou�� k nastaven� konce �asov�ho intervalu v�b�ru skupiny epoch v
	 * milisekund�ch.
	 */
	private JSpinner toMsJS;
	/**
	 * Slou�� k nastaven� konce �asov�ho intervalu v�b�ru skupiny epoch v
	 * sekund�ch.
	 */
	private JSpinner toSecJS;
	/**
	 * Slou�� k nastaven� konce �asov�ho intervalu v�b�ru skupiny epoch v
	 * minut�ch.
	 */
	private JSpinner toMinJS;
	/**
	 * Tla��tko otev�raj�c� exportn� okno (realizovan� t��dou
	 * <code>ExportFrame</code>).
	 */
	private JButton exportJB;
	/**
	 * Textov� pole pro zobrazen� cesty k souboru s indexy epoch.
	 */
	private JTextField pathJTF;

	/**
	 * Vytv��� pohled pro hromadn� za�azov�n�/odeb�r�n� epoch do/z pr�m�r�
	 * vybran�ch kan�l�.
	 * 
	 * @param averagingWindow
	 */
	GroupEpochsView(AveragingPanel averagingWindow) {
		super(averagingWindow);
		zoomStep = 0.03f;
		zoomOriginal = SignalViewerPanel.ZOOM_Y_ORIGINAL;
		meanPanels = null;
		toolBarInit();
	}

	/**
	 * Inicializace n�strojov� li�ty pohledu. Na konci metody po vytvo�en�
	 * n�strojov� li�ty pos�l� zpr�vu <code>TOOL_BAR_READY</code>.
	 */
	private void toolBarInit() {
		toolBarPanelJP = new JPanel(new BorderLayout());

		JToolBar signalToolBarJTB = new JToolBar();
		zoomInJB = new JButton();
		zoomInJB.setToolTipText("Zoom in");
		zoomInJB.setIcon(averagingWindow.getAveragingWindowProvider().getIcon(
				"magvp.png"));
		zoomInJB.setPreferredSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomInJB.setMinimumSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomInJB.setMaximumSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomInJB.addActionListener(new ALzoomInJB());

		zoomOutJB = new JButton();
		zoomOutJB.setToolTipText("Zoom out");
		zoomOutJB.setIcon(averagingWindow.getAveragingWindowProvider().getIcon(
				"magvm.png"));
		zoomOutJB.setPreferredSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomOutJB.setMinimumSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomOutJB.setMaximumSize(new Dimension(30, COMPONENT_HEIGHT));
		zoomOutJB.addActionListener(new ALzoomOutJB());

		zoomOriginalJB = new JButton("Zoom original");
		zoomOriginalJB.setPreferredSize(new Dimension(70, COMPONENT_HEIGHT));
		zoomOriginalJB.setMinimumSize(new Dimension(70, COMPONENT_HEIGHT));
		zoomOriginalJB.setMaximumSize(new Dimension(70, COMPONENT_HEIGHT));
		zoomOriginalJB.addActionListener(new ALzoomOriginalJB());

		modeOfRepresentationJCB = new JComboBox(
				SignalViewerPanel.MODE_OF_REPRESENTATION_TYPES);
		modeOfRepresentationJCB.setSelectedIndex(1);
		modeOfRepresentationJCB.addActionListener(new ALmodeOfRepresentationJCB());
		modeOfRepresentationJCB.setPreferredSize(new Dimension(140,
				COMPONENT_HEIGHT));
		modeOfRepresentationJCB
				.setMinimumSize(new Dimension(140, COMPONENT_HEIGHT));
		modeOfRepresentationJCB
				.setMaximumSize(new Dimension(140, COMPONENT_HEIGHT));

		averageTypeJCB = new JComboBox(Averages.AVERAGE_TYPES);
		averageTypeJCB.setSelectedIndex(averagingWindow
				.getAveragingWindowProvider().getAverageType());
		averageTypeJCB.setPreferredSize(new Dimension(100, COMPONENT_HEIGHT));
		averageTypeJCB.setMinimumSize(new Dimension(100, COMPONENT_HEIGHT));
		averageTypeJCB.setMaximumSize(new Dimension(100, COMPONENT_HEIGHT));
		averageTypeJCB.addActionListener(new ALaverageTypeJCB());

		exportJB = new JButton("Export averages");
		// Zaji�t�n� konstantn� velikosti tla��tka.
		exportJB.setMinimumSize(new Dimension(100, COMPONENT_HEIGHT));
		exportJB.setPreferredSize(new Dimension(100, COMPONENT_HEIGHT));
		exportJB.setMaximumSize(new Dimension(100, COMPONENT_HEIGHT));
		exportJB.addActionListener(new ALexportJB());

		signalToolBarJTB.add(zoomOutJB);
		signalToolBarJTB.add(zoomInJB);
		signalToolBarJTB.add(zoomOriginalJB);
		final JLabel viewJL = new JLabel("   View: ");
		viewJL.setLabelFor(modeOfRepresentationJCB);
		signalToolBarJTB.add(viewJL);
		signalToolBarJTB.add(modeOfRepresentationJCB);
		final JLabel averageTypeJL = new JLabel(" Average type: ");
		averageTypeJL.setLabelFor(averageTypeJCB);
		signalToolBarJTB.add(averageTypeJL);
		signalToolBarJTB.add(averageTypeJCB);
		signalToolBarJTB.add(exportJB);

		signalToolBarJTB.setFloatable(false);

		toolBarPanelJP.add(signalToolBarJTB);

		initTimeSelection();
		initIndexesSelection();
		initAllSelection();
		initApply();

		JPanel ribbonJP = new JPanel(); // :-)
		GroupLayout layout = new GroupLayout(ribbonJP);
		ribbonJP.setLayout(layout);

		layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(
				timeSelection).addComponent(indexesSelection)
				.addComponent(allSelection).addComponent(apply));

		layout.setVerticalGroup(layout.createParallelGroup().addComponent(
				timeSelection).addComponent(indexesSelection)
				.addComponent(allSelection).addComponent(apply));

		layout.linkSize(SwingConstants.VERTICAL, indexesSelection, allSelection);

		ButtonGroup group = new ButtonGroup();
		group.add(timeSelectionJRB);
		group.add(indexesSelectionJRB);
		group.add(allSelectionJRB);

		toolBarPanelJP.add(signalToolBarJTB, BorderLayout.NORTH);
		toolBarPanelJP.add(ribbonJP, BorderLayout.CENTER);

		sendObjectToObservers(TOOL_BAR_READY);
	}

	/**
	 * Inicializace ��sti n�strojov� li�ty, kter� poskytuje funk�nost spojenou s
	 * v�b�rem epoch podle �asov�ho intervalu.
	 */
	private void initTimeSelection() {
		timeSelection = new JPanel();
		timeSelection.setMinimumSize(new Dimension(260, RIBBON_HEIGHT));
		timeSelection.setPreferredSize(new Dimension(260, RIBBON_HEIGHT));
		timeSelection.setMaximumSize(new Dimension(260, RIBBON_HEIGHT));

		timeSelection.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEtchedBorder(), BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		GroupLayout timeLayout = new GroupLayout(timeSelection);
		timeSelection.setLayout(timeLayout);
		timeSelectionJRB = new JRadioButton("Time");
		timeSelectionJRB.addActionListener(new ALselectionJRB());
		JLabel fromJL = new JLabel("From: ");
		JLabel toJL = new JLabel("To: ");

		final int spinnersHeight = 20;
		final int secondSpinnersWidth = 35;
		final int otherSpinnersWidth = 42;

		ChangeListener clFromAndToJS = new CLfromAndToJS();

		fromMsJS = new JSpinner();
		fromMsJS.addChangeListener(clFromAndToJS);
		fromMsJS.setMinimumSize(new Dimension(otherSpinnersWidth, spinnersHeight));
		fromMsJS
				.setPreferredSize(new Dimension(otherSpinnersWidth, spinnersHeight));
		fromMsJS.setMaximumSize(new Dimension(otherSpinnersWidth, spinnersHeight));

		fromSecJS = new JSpinner();
		fromSecJS.addChangeListener(clFromAndToJS);
		fromSecJS
				.setMinimumSize(new Dimension(secondSpinnersWidth, spinnersHeight));
		fromSecJS.setPreferredSize(new Dimension(secondSpinnersWidth,
				spinnersHeight));
		fromSecJS
				.setMaximumSize(new Dimension(secondSpinnersWidth, spinnersHeight));

		fromMinJS = new JSpinner();
		fromMinJS.addChangeListener(clFromAndToJS);
		fromMinJS.setMinimumSize(new Dimension(otherSpinnersWidth, spinnersHeight));
		fromMinJS
				.setPreferredSize(new Dimension(otherSpinnersWidth, spinnersHeight));
		fromMinJS.setMaximumSize(new Dimension(otherSpinnersWidth, spinnersHeight));

		toMsJS = new JSpinner();
		toMsJS.addChangeListener(clFromAndToJS);
		toMsJS.setMinimumSize(new Dimension(otherSpinnersWidth, spinnersHeight));
		toMsJS.setPreferredSize(new Dimension(otherSpinnersWidth, spinnersHeight));
		toMsJS.setMaximumSize(new Dimension(otherSpinnersWidth, spinnersHeight));

		toSecJS = new JSpinner();
		toSecJS.addChangeListener(clFromAndToJS);
		toSecJS.setMinimumSize(new Dimension(secondSpinnersWidth, spinnersHeight));
		toSecJS
				.setPreferredSize(new Dimension(secondSpinnersWidth, spinnersHeight));
		toSecJS.setMaximumSize(new Dimension(secondSpinnersWidth, spinnersHeight));

		toMinJS = new JSpinner();
		toMinJS.addChangeListener(clFromAndToJS);
		toMinJS.setMinimumSize(new Dimension(otherSpinnersWidth, spinnersHeight));
		toMinJS.setPreferredSize(new Dimension(otherSpinnersWidth, spinnersHeight));
		toMinJS.setMaximumSize(new Dimension(otherSpinnersWidth, spinnersHeight));

		fromJL.setLabelFor(fromMinJS);
		toJL.setLabelFor(toMinJS);

		JPanel fromJP = new JPanel(new SpringLayout());

		fromJP.add(fromJL);
		fromJP.add(fromMinJS);
		fromJP.add(new JLabel("[m]"));
		fromJP.add(fromSecJS);
		fromJP.add(new JLabel("[s]"));
		fromJP.add(fromMsJS);
		fromJP.add(new JLabel("[ms]"));

		fromJP.add(toJL);
		fromJP.add(toMinJS);
		fromJP.add(new JLabel("[m]"));
		fromJP.add(toSecJS);
		fromJP.add(new JLabel("[s]"));
		fromJP.add(toMsJS);
		fromJP.add(new JLabel("[ms]"));

		// Lay out the panel.
		SpringUtilities.makeCompactGrid(fromJP, 2, 7, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		timeLayout.setHorizontalGroup(timeLayout.createParallelGroup()
				.addComponent(timeSelectionJRB).addComponent(fromJP));

		timeLayout.setVerticalGroup(timeLayout.createSequentialGroup()
				.addComponent(timeSelectionJRB).addComponent(fromJP));
	}

	/**
	 * Inicializace ��sti n�strojov� li�ty, kter� poskytuje funk�nost spojenou s
	 * v�b�rem epoch podle index� na�ten�ch ze souboru.
	 */
	private void initIndexesSelection() {
		indexesSelection = new JPanel();

		indexesSelection.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEtchedBorder(), BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		GroupLayout indexesLayout = new GroupLayout(indexesSelection);
		indexesLayout.setAutoCreateContainerGaps(true);
		indexesSelection.setLayout(indexesLayout);

		indexesSelectionJRB = new JRadioButton("Indexes");
		indexesSelectionJRB.addActionListener(new ALselectionJRB());
		JLabel pathJL = new JLabel("Path: ");
		pathJL.setLabelFor(pathJTF);
		browseJB = new JButton("Browse");
		browseJB.addActionListener(new ALbrowseJB());
		pathJTF = new JTextField();
		pathJTF.addKeyListener(new KLpathJTF());
		BorderLayout pathLayout = new BorderLayout();
		JPanel pathJP = new JPanel(pathLayout);
		pathJP.add(pathJL, BorderLayout.WEST);
		pathJP.add(pathJTF, BorderLayout.CENTER);
		pathJP.add(browseJB, BorderLayout.EAST);

		indexesLayout.setHorizontalGroup(indexesLayout.createParallelGroup()
				.addComponent(indexesSelectionJRB).addGroup(
						indexesLayout.createSequentialGroup().addComponent(pathJL)
								.addComponent(pathJTF, browseJB.getMinimumSize().height,
										browseJB.getPreferredSize().height, Short.MAX_VALUE)
								.addComponent(browseJB))

		);

		indexesLayout
				.setVerticalGroup(indexesLayout.createSequentialGroup().addComponent(
						indexesSelectionJRB).addGroup(
						indexesLayout.createParallelGroup().addComponent(pathJL)
								.addComponent(pathJTF, browseJB.getMinimumSize().height,
										browseJB.getPreferredSize().height,
										browseJB.getPreferredSize().height).addComponent(browseJB)));
	}

	/**
	 * Inicializace ��sti n�strojov� li�ty, kter� slou�� pro pr�ci se v�emi
	 * pr�m�rovateln�mi epochami.
	 */
	private void initAllSelection() {
		allSelection = new JPanel(new FlowLayout(FlowLayout.LEFT));

		allSelection.setMinimumSize(new Dimension(90, RIBBON_HEIGHT));
		allSelection.setPreferredSize(new Dimension(90, RIBBON_HEIGHT));
		allSelection.setMaximumSize(new Dimension(90, RIBBON_HEIGHT));

		allSelection.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEtchedBorder(), BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		allSelectionJRB = new JRadioButton("All epochs");
		allSelectionJRB.addActionListener(new ALselectionJRB());
		allSelection.add(allSelectionJRB);
	}

	/**
	 * Inicializace ��sti n�strojov� li�ty, kter� slou�� pro zahrnut�/odebr�n�
	 * skupiny epoch do/z pr�m�r� vybran�ch kan�l�.
	 */
	private void initApply() {
		apply = new JPanel();

		apply.setMinimumSize(new Dimension(140, RIBBON_HEIGHT));
		apply.setPreferredSize(new Dimension(140, RIBBON_HEIGHT));
		apply.setMaximumSize(new Dimension(140, RIBBON_HEIGHT));

		apply.setBackground(Color.LIGHT_GRAY);
		apply.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEtchedBorder(), BorderFactory.createEmptyBorder(0, 0, 0, 0)));

		GroupLayout applyLayout = new GroupLayout(apply);
		applyLayout.setAutoCreateContainerGaps(true);
		apply.setLayout(applyLayout);
		addJRB = new JRadioButton("Add");
		addJRB.addActionListener(new ALaddJRB());
		addJRB.setBackground(apply.getBackground());
		removeJRB = new JRadioButton("Remove");
		removeJRB.addActionListener(new ALremoveJRB());
		removeJRB.setBackground(apply.getBackground());
		applyJB = new JButton("Apply");
		applyJB.setBackground(apply.getBackground());
		applyJB.addActionListener(new ALapplyJB());

		ButtonGroup group = new ButtonGroup();
		group.add(addJRB);
		group.add(removeJRB);

		applyLayout.setHorizontalGroup(applyLayout.createParallelGroup().addGroup(
				applyLayout.createSequentialGroup().addComponent(addJRB).addComponent(
						removeJRB)).addComponent(applyJB));

		applyLayout.setVerticalGroup(applyLayout.createSequentialGroup().addGroup(
				applyLayout.createParallelGroup().addComponent(addJRB).addComponent(
						removeJRB)).addComponent(applyJB));
	}

	/**
	 * Vytv��� pr�m�rovac� panely a umis�uje je do layoutu vnit�ku pohledu.
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
				meanPanels[i] = new GroupEpochsMeanPanel(averagingWindow
						.getAveragingWindowProvider().getAvaragedSignalsIndexes().get(i),
						averagingWindow.getAveragingWindowProvider());

				meanPanels[i].setActionsPanel(new GroupAddingAP(meanPanels[i]));
				meanPanels[i].actionsPanel.setApplyChanges(averagingWindow
						.getAveragingWindowProvider().getApplyChanges(
								averagingWindow.getAveragingWindowProvider()
										.getAvaragedSignalsIndexes().get(i)));
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
	 * Nastavuje ��slo pr�v� zobrazen� epochy.
	 * 
	 * @param currentEpochNumber
	 *          ��slo pr�v� zobrazen� epochy.
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
		if (meanPanels != null) {
			for (MeanPanel panel : meanPanels) {
				panel.setEnabledOperatingElements(enabled);
			}
		}

		averageTypeJCB.setEnabled(enabled);
		exportJB.setEnabled(enabled);
		modeOfRepresentationJCB.setEnabled(enabled);
		zoomInJB.setEnabled(enabled);
		zoomOutJB.setEnabled(enabled);
		zoomOriginalJB.setEnabled(enabled);
		timeSelectionJRB.setEnabled(enabled);
		indexesSelectionJRB.setEnabled(enabled);

		allSelectionJRB.setEnabled(enabled);
		addJRB.setEnabled(enabled);
		removeJRB.setEnabled(enabled);
		applyJB.setEnabled(enabled);

		if (!enabled) // jejich povolov�n� je z�visl� na akc�ch u�ivatele, zak�z�n�
									// je ale t�eba i bez jeho akc�
		{
			timeSelectionEnabled(false);
			indexesSelectionEnabled(false);
			allSelectionEnabled(false);
		}
	}

	/**
	 * Nastavuje zobrazen� pohledu v z�vislosti na atributech pr�v� zobrazovan�ho
	 * projektu.
	 */
	@Override
	void viewSetupByProject() {
		int groupEpochsType = averagingWindow.getAveragingWindowProvider()
				.getLastUsedGroupEpochsType();

		switch (groupEpochsType) {
		case AveragingDataManager.GROUP_EPOCHS_TIME:
			timeSelectionJRB.setSelected(true);
			timeSelectionEnabled(true);
			indexesSelectionEnabled(false);
			allSelectionEnabled(false);
			break;
		case AveragingDataManager.GROUP_EPOCHS_INDEXES:
			indexesSelectionJRB.setSelected(true);
			timeSelectionEnabled(false);
			indexesSelectionEnabled(true);
			allSelectionEnabled(false);
			break;
		default:
			allSelectionJRB.setSelected(true);
			timeSelectionEnabled(false);
			indexesSelectionEnabled(false);
			allSelectionEnabled(true);
		}

		pathJTF.setText(averagingWindow.getAveragingWindowProvider()
				.getAbsolutePathToIndexes());

		switch (averagingWindow.getAveragingWindowProvider()
				.getGroupEpochWorkMode()) {
		case AveragingDataManager.GROUP_EPOCHS_REMOVING:
			removeJRB.setSelected(true);
			break;
		default:
			addJRB.setSelected(true);
		}

		fromMsJS.setModel(new SpinnerNumberModel(0, 0, 999, 1)); // min = 0, max =
																															// 999, step = 1
		fromSecJS.setModel(new SpinnerNumberModel(0, 0, 59, 1)); // min = 0, max =
																															// 59, step = 1
		fromMinJS.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)); // min
																																						// = 0,
																																						// max
																																						// =
																																						// Infinity,
																																						// step
																																						// = 1

		toMsJS.setModel(new SpinnerNumberModel(0, 0, 999, 1)); // min = 0, max =
																														// 999, step = 1
		toSecJS.setModel(new SpinnerNumberModel(0, 0, 59, 1)); // min = 0, max =
																														// 59, step = 1
		toMinJS.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)); // min
																																					// = 0,
																																					// max
																																					// =
																																					// Infinity,
																																					// step
																																					// = 1

		synchronizeTimeSelectionWithProject();
	}

	/**
	 * Synchronizuje po��tek a konec �asov�ho intervalu s projektem.
	 */
	private void synchronizeTimeSelectionWithProject() {
		int milisecondsBegin = averagingWindow.getAveragingWindowProvider()
				.getTimeSelectionBegin();
		int milisecondsEnd = averagingWindow.getAveragingWindowProvider()
				.getTimeSelectionEnd();

		int minutesBegin = milisecondsBegin / Const.MS_IN_MIN;
		milisecondsBegin -= minutesBegin * Const.MS_IN_MIN;
		int secondsBegin = milisecondsBegin / Const.MS_IN_SEC;
		milisecondsBegin -= secondsBegin * Const.MS_IN_SEC;

		int minutesEnd = milisecondsEnd / Const.MS_IN_MIN;
		milisecondsEnd -= minutesEnd * Const.MS_IN_MIN;
		int secondsEnd = milisecondsEnd / Const.MS_IN_SEC;
		milisecondsEnd -= secondsEnd * Const.MS_IN_SEC;

		fromMsJS.setValue(milisecondsBegin);
		fromSecJS.setValue(secondsBegin);
		fromMinJS.setValue(minutesBegin);

		toMsJS.setValue(milisecondsEnd);
		toSecJS.setValue(secondsEnd);
		toMinJS.setValue(minutesEnd);

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
	 * Povoluje/zakazuje ��st n�strojov� li�ty pro v�b�r epoch podle �asov�ho
	 * intervalu.
	 * 
	 * @param enabled
	 *          <code>true</code>, pokud maj� b�t ovl�dac� prvky povoleny,jinak
	 *          <code>false</code>.
	 */
	private void timeSelectionEnabled(boolean enabled) {
		fromMsJS.setEnabled(enabled);
		fromSecJS.setEnabled(enabled);
		fromMinJS.setEnabled(enabled);
		toMsJS.setEnabled(enabled);
		toSecJS.setEnabled(enabled);
		toMinJS.setEnabled(enabled);
	}

	/**
	 * Povoluje/zakazuje ��st n�strojov� li�ty pro v�b�r epoch podle index�
	 * na�ten�ch ze souboru.
	 * 
	 * @param enabled
	 *          <code>true</code>, pokud maj� b�t ovl�dac� prvky povoleny,jinak
	 *          <code>false</code>.
	 */
	private void indexesSelectionEnabled(boolean enabled) {
		pathJTF.setEnabled(enabled);
		browseJB.setEnabled(enabled);
	}

	/**
	 * Povoluje/zakazuje ��st n�strojov� li�ty pro v�b�r v�ech epoch. T�lo metody
	 * je pr�zdn�, proto�e v sou�asn� podob� aplikace neexistuj� ��dn� volby pro
	 * tento druh v�b�ru. Metoda je ale vol�na, jako by volby obsahovala. To v�e z
	 * d�vodu jednoduch� roz�i�itelnosti.
	 * 
	 * @param enabled
	 *          <code>true</code>, pokud maj� b�t ovl�dac� prvky povoleny,jinak
	 *          <code>false</code>.
	 */
	private void allSelectionEnabled(boolean enabled) {
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
	 * Nastavuje po�et epoch, kter� byly p�ed�ny k pr�m�rov�n�.
	 * 
	 * @param epochCount
	 *          po�et epoch p�edan�ch k pr�m�rov�n�.
	 */
	@Override
	void setEpochCount(int epochCount) {
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
	 * Vrac� n�strojovou li�tu pohledu.
	 * 
	 * @return n�strojov� li�ta.
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
	}

	private class ALbrowseJB implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			final JFileChooser fc = new JFileChooser();

			fc.setDialogTitle("Load file");
			fc.addChoosableFileFilter(new FileNameExtensionFilter(
					"Text documents (*.txt)", "txt"));

			int returnVal = fc.showOpenDialog(averagingWindow);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				pathJTF.setText(file.getAbsolutePath());
				averagingWindow.getAveragingWindowProvider().loadEpochsIndexes(file);
			}
		}
	}

	/**
	 * ActionListener atribut� <code>timeSelectionJRB</code>,
	 * <code>indexesSelectionJRB</code>, <code>allSelectionJRB</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALselectionJRB implements ActionListener {
		/**
		 * V z�vislosti na atributu, od kter�ho ud�lost poch�z�, povoluje a zakazuje
		 * ��sti n�strojov� li�ty.
		 * 
		 * @param event
		 *          Nastal� ud�lost
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source.equals(timeSelectionJRB)) {
				timeSelectionEnabled(true);
				indexesSelectionEnabled(false);
				allSelectionEnabled(false);
				averagingWindow.getAveragingWindowProvider()
						.setLastUsedGroupEpochsType(AveragingDataManager.GROUP_EPOCHS_TIME);
			} else if (source.equals(indexesSelectionJRB)) {
				timeSelectionEnabled(false);
				indexesSelectionEnabled(true);
				allSelectionEnabled(false);
				averagingWindow.getAveragingWindowProvider()
						.setLastUsedGroupEpochsType(
								AveragingDataManager.GROUP_EPOCHS_INDEXES);
			} else if (source.equals(allSelectionJRB)) {
				timeSelectionEnabled(false);
				indexesSelectionEnabled(false);
				allSelectionEnabled(true);
				averagingWindow.getAveragingWindowProvider()
						.setLastUsedGroupEpochsType(AveragingDataManager.GROUP_EPOCHS_ALL);
			}
		}
	}

	/**
	 * ActionListener atributu <code>applyJB</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALapplyJB implements ActionListener {
		/**
		 * Podle zp�sobu v�b�ru skupiny epoch za�ad�/odebere tuto skupino do/z
		 * pr�m�r� vybran�ch kan�l�.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			if (timeSelectionJRB.isSelected()) {
				averagingWindow.getAveragingWindowProvider().setTimeEpochsSelected(
						addJRB.isSelected());
			} else if (indexesSelectionJRB.isSelected()) {
				averagingWindow.getAveragingWindowProvider().setIndexedEpochsSelected(
						addJRB.isSelected());
			} else {
				averagingWindow.getAveragingWindowProvider().setAllEpochsSelected(
						addJRB.isSelected());
			}
		}
	}

	/**
	 * KeyListener atributu <code>pathJTF</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class KLpathJTF extends KeyAdapter {
		/**
		 * Pokud je stisknuta kl�vesa ENTER, je na�ten soubor, s indexy epoch, ke
		 * kter�mu je v ok�nku path v GUI uvedena cesta.
		 * 
		 * @param key
		 *          Ud�lost stisknut� kl�vesy.
		 */
		@Override
		public void keyPressed(KeyEvent key) {
			if (key.getKeyCode() == KeyEvent.VK_ENTER) {
				File file = new File(pathJTF.getText());
				pathJTF.setText(file.getAbsolutePath());
				averagingWindow.getAveragingWindowProvider().loadEpochsIndexes(file);
			}
		}
	}

	/**
	 * ActionListener v�b�rov�ho boxu <b>averageTypeJCB</b>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALaverageTypeJCB implements ActionListener {
		/**
		 * Slou�� k v�b�ru pou�it�ho typu pr�m�ru.
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
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			averagingWindow.runAveragesExport();
		}
	}

	/**
	 * ActionListener atributu <code>addJRB</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALaddJRB implements ActionListener {
		/**
		 * Nastavuje zp�sob pr�ce se skupinou epoch na za�azov�n� epoch do pr�m�ru
		 * vybran�ch kan�l�.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			averagingWindow.getAveragingWindowProvider().setGroupEpochWorkMode(
					AveragingDataManager.GROUP_EPOCHS_ADDING);
		}
	}

	/**
	 * ActionListener atributu <code>removeJRB</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALremoveJRB implements ActionListener {
		/**
		 * Nastavuje zp�sob pr�ce se skupinou epoch na odeb�r�n� epoch z pr�m�ru
		 * vybran�ch kan�l�.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			averagingWindow.getAveragingWindowProvider().setGroupEpochWorkMode(
					AveragingDataManager.GROUP_EPOCHS_REMOVING);
		}
	}

	/**
	 * Z�sk�v� z atribut� <code>fromMsJS</code>, <code>fromSecJS</code>,
	 * <code>fromMinJS</code> po��tek �asov�ho intervalu pro v�b�r skupiny
	 * epoch.
	 * 
	 * @return Vzd�lennost po��tku �asov�ho intervalu pro v�b�r skupiny epoch v
	 *         milisekund�ch od po��tku.
	 */
	private int getMilisecondsFrom() {
		int miliseconds = 0;

		miliseconds += ((Integer) fromMsJS.getValue()).intValue();
		miliseconds += Const.MS_IN_SEC
				* ((Integer) fromSecJS.getValue()).intValue();
		miliseconds += Const.MS_IN_MIN
				* ((Integer) fromMinJS.getValue()).intValue();

		return miliseconds;
	}

	/**
	 * Z�sk�v� z atribut� <code>toMsJS</code>, <code>toSecJS</code>,
	 * <code>toMinJS</code> konec �asov�ho intervalu pro v�b�r skupiny epoch.
	 * 
	 * @return Vzd�lennost konce �asov�ho intervalu pro v�b�r skupiny epoch v
	 *         milisekund�ch od po��tku.
	 */
	private int getMilisecondsTo() {
		int miliseconds = 0;

		miliseconds += ((Integer) toMsJS.getValue()).intValue();
		miliseconds += Const.MS_IN_SEC * ((Integer) toSecJS.getValue()).intValue();
		miliseconds += Const.MS_IN_MIN * ((Integer) toMinJS.getValue()).intValue();

		return miliseconds;
	}

	/**
	 * ChangeListener atributu <code></code>
	 * 
	 * @author Tom� �ond�k
	 */
	private class CLfromAndToJS implements ChangeListener {
		/**
		 * Zm�n�-li se nastaven� po��tku �i konce �asov�ho intervalu, pak
		 * zkontroluje, zda je po��tek intervalu ost�e men�� ne� jeho konec a pokud
		 * ano, pak nastav� nov� po��tek a konec �asov�ho intervalu. V opa�n�m
		 * p��pad� upozorn� u�ivatele na nesmysln� nastaven� a nastaven� takov�ch
		 * hodnot nepovol�.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void stateChanged(ChangeEvent event) {
			int milisecondsFrom = getMilisecondsFrom();
			int milisecondsTo = getMilisecondsTo();

			if (milisecondsFrom < milisecondsTo) {
				averagingWindow.getAveragingWindowProvider().setTimeSelectionBegin(
						milisecondsFrom);
				averagingWindow.getAveragingWindowProvider().setTimeSelectionEnd(
						milisecondsTo);
			} else {
				synchronizeTimeSelectionWithProject();
				JOptionPane.showMessageDialog(averagingWindow,
						"The begin of interval must be less than his end.",
						"Time selection bounds", JOptionPane.WARNING_MESSAGE);
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
}
