package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.application.exception.JERPAGraphicsException;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging.SignalViewerPanel;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.data.EmailErrorReporter;
import ch.ethz.origo.juigle.data.JUIGLEErrorParser;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import ch.ethz.origo.juigle.prezentation.SpringUtilities;

/**
 * Panel pro nastaven� vzhledu exportovan�ho dokumentu.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.2.0 (4/17/2010)
 * @since 0.1.0 (3/21/2010)
 * @see JPanel
 * @see ILanguage
 */
@SuppressWarnings("serial")
final class SettingsPanel extends JPanel implements ILanguage {

	private Logger logger = Logger.getLogger(SettingsPanel.class);

	private static String RBKEY_INTERPOLATION_COLOR = "avg.frame.export.settingpanel.color.interpolation";
	private static String RBKEY_AXIS_COLOR = "avg.frame.export.settingpanel.color.axis";
	private static String RBKEY_FUNCTIONAL_COLOR_VALUES = "avg.frame.export.settingpanel.color.functional";
	/**
	 * Reference na programov� rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	private ExportFrame exportFrame;
	/**
	 * Barva pozad� pr�b�hu sign�lu v exportn�m okn�.
	 */
	private Color canvasColor;
	/**
	 * Tla��tko pro nastaven� barvy pozad� exportovan�ho dokumentu.
	 */
	private JButton canvasColorJB;
	/**
	 * Panel s uk�zkou zvolen� barvy pozad� exportovan�ho dokumentu.
	 */
	private JPanel canvasColorJP;
	/**
	 * Barva interpolace funk�n�ch hodnot sign�lu v exportn�m okn�.
	 */
	private Color interpolationColor;
	/**
	 * Tla��tko pro nastaven� barvy interpolace funk�n�ch hodnot v exportovan�m
	 * dokumentu.
	 */
	private JButton interpolationColorJB;
	/**
	 * Panel s uk�zkou zvolen� barvy interpolace funkn�n�ch hodnot v exportovan�m
	 * dokumentu.
	 */
	private JPanel interpolationColorJP;
	/**
	 * Barva os sou�adn�ho syst�mu v exportn�m okn�.
	 */
	private Color axisColor;
	/**
	 * Tla��tko pro nastaven� barvy os v exportovan�m dokumentu.
	 */
	private JButton axisColorJB;
	/**
	 * Panel s uk�zkou zvolen� barvy os v exportovan�m dokumentu.
	 */
	private JPanel axisColorJP;
	/**
	 * Barva zv�razn�n� funk�n�ch hodnot sign�lu v exportn�m okn�.
	 */
	private Color functionalValuesColor;
	/**
	 * Tla��tko pro nastaven� barvy zv�razn�n� funk�n�ch hodnot sign�lu v
	 * exportovan�m dokumentu.
	 */
	private JButton functionalValuesColorJB;
	/**
	 * Panel s uk�zkou zvolen� barvy zv�razn�n� funk�n�ch hodnot sign�lu v
	 * exportovan�m dokumentu.
	 */
	private JPanel functionalValuesColorJP;
	/**
	 * Zobrazen� aktu�ln�ho nastaven�.
	 */
	private SignalViewerPanel currentSettingsViewer;
	/**
	 * Panel, na kter�m je um�st�n <code>currentSettingsViewer</code>.
	 */
	private JPanel underCurrentSettingsViewer;
	/**
	 * Zobrazen� nov�ho nastaven�.
	 */
	private SignalViewerPanel newSettingsViewer;
	/**
	 * Panel, na kter�m je um�st�n <code>underNewSettingsViewer</code>.
	 */
	private JPanel underNewSettingsViewer;
	/**
	 * Panel, na kter�m je um�st�n n�hled aktu�ln�ho nastaven� v�etn� popisku.
	 */
	private JPanel currentSettingsPanel;
	/**
	 * Panel, na kter�m je um�st�n n�hled nov�ho nastaven� v�etn� popisku.
	 */
	private JPanel newSettingsPanel;
	/**
	 * Panel na kter�m zobrazuj� mo�nosti nastaven�.
	 */
	private JPanel settingsPanel;
	/**
	 * Panel na kter�m se zobrazuj� nastaven� vzhledu zobrazovan�ch sign�l�.
	 */
	private JPanel viewersLookPanel;
	/**
	 * Prvek pro nastaven� ���ky zobrazovan�ch sign�l�.
	 */
	private JSpinner viewersWidthJS;
	/**
	 * Panel pro nastaven� v�ky zobrazovan�ch sign�l�.
	 */
	private JSpinner viewersHeightJS;
	/**
	 * Panel na kter�m se zobrazuj� n�hledy nastaven�.
	 */
	private JPanel signalViewersPanel;

	private TitledBorder settingsColorBorder;
	private TitledBorder viewersLookPanelBorder;
	private TitledBorder signalViewersPanelBorder;

	private JLabel viewerLabel;
	private JLabel widthLabel;
	private JLabel heightLabel;
	private JLabel viewerLabelSettings;

	private JTextArea note;

	private ResourceBundle resourceBundle;

	private ExportFrameProvider provider;

	/**
	 * ���ka zobrazovan�ch sign�l�.
	 */
	private int viewersWidth;
	/**
	 * V�ka zobrazovan�ch sign�l�.
	 */
	private int viewersHeight;

	/**
	 * Vytv��� nov� panel nastaven� exportovan�ho dokumentu.
	 * 
	 * @param exportFrame
	 *          Programov� rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	SettingsPanel(ExportFrame exportFrame) {
		this.exportFrame = exportFrame;
		provider = this.exportFrame.getExportFrameProvider();
		canvasColor = provider.getCanvasColor();
		interpolationColor = provider.getInterpolationColor();
		functionalValuesColor = provider.getFunctionalValuesColor();
		axisColor = provider.getAxisColor();
		LanguageObservable.getInstance().attach(this);
		this.resourceBundle = provider.getResourceBundle();
		createInside();
		updateText();
	}

	/**
	 * Inicializace a nastaven� parametr� layoutu.
	 */
	private void layoutInit() {
		BorderLayout layout = new BorderLayout();
		layout.setHgap(10);
		this.setLayout(layout);
	}

	/**
	 * Inicializace panlu zobrazuj�c�ho n�hledy nastaven�.
	 */
	private void initViewersLookPanel() {
		final int componentsWidth = 40;
		final int componentsHeight = 15;
		final Dimension dimension = new Dimension(componentsWidth, componentsHeight);

		viewersLookPanel = new JPanel();
		BoxLayout viewersLookLayout = new BoxLayout(viewersLookPanel,
				BoxLayout.Y_AXIS);
		ChangeListener cl = new CLviewersSpinnerJS();
		ExportFrameProvider provider = exportFrame.getExportFrameProvider();
		viewersLookPanel.setLayout(viewersLookLayout);
		viewersLookPanelBorder = BorderFactory.createTitledBorder("");
		viewersLookPanel.setBorder(viewersLookPanelBorder);

		JPanel spinnersJP = new JPanel(new SpringLayout());

		widthLabel = new JLabel();
		viewersWidthJS = new JSpinner();
		viewersWidth = provider.getExportViewersWidth();
		viewersWidthJS.setModel(new SpinnerNumberModel(viewersWidth, 0,
				Integer.MAX_VALUE, 1));
		viewersWidthJS.addChangeListener(cl);
		viewersWidthJS.setMinimumSize(dimension);
		viewersWidthJS.setPreferredSize(dimension);
		// viewersWidthJS.setMaximumSize(dimension);

		widthLabel.setLabelFor(viewersWidthJS);
		heightLabel = new JLabel();
		viewersHeightJS = new JSpinner();
		viewersHeight = provider.getExportViewersHeight();
		viewersHeightJS.setModel(new SpinnerNumberModel(viewersHeight, 0,
				Integer.MAX_VALUE, 1));
		viewersHeightJS.addChangeListener(cl);
		viewersHeightJS.setMinimumSize(dimension);
		viewersHeightJS.setPreferredSize(dimension);
		// viewersHeightJS.setMaximumSize(dimension);
		heightLabel.setLabelFor(viewersHeightJS);

		spinnersJP.add(widthLabel);
		spinnersJP.add(viewersWidthJS);
		spinnersJP.add(heightLabel);
		spinnersJP.add(viewersHeightJS);

		SpringUtilities.makeCompactGrid(spinnersJP, 2, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		viewersLookPanel.add(spinnersJP);
		note = new JTextArea();
		note.setBackground(new Color(236, 233, 216));
		note.setEnabled(false);
		note.setWrapStyleWord(true);
		note.setLineWrap(true);
		note.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		note.setPreferredSize(dimension);
		viewersLookPanel.add(note);

	}

	/**
	 * Vytv��� vnit�ek panelu pro nastaven� vzhledu exportovan�ho dokumentu
	 */
	private void createInside() {
		layoutInit();

		if (settingsPanel != null || signalViewersPanel != null) {
			this.remove(settingsPanel);
			this.remove(signalViewersPanel);
		}

		initSettingsPanel();
		initSignalViewersPanel();
		this.add(settingsPanel, BorderLayout.WEST);
		this.add(signalViewersPanel, BorderLayout.CENTER);
	}

	/**
	 * Generuje n�hodn� fuk�n� hodnoty pro zobrazen� v n�hledech nastaven�.
	 * 
	 * @return Pole n�hodn�ch funk�n�ch hodnot.
	 */
	private float[] getRandomFunctionalValues() {
		ExportFrameProvider provider = exportFrame.getExportFrameProvider();
		int valuesCount = provider.getAveragingDataManager()
				.getLeftEpochBorderInFrames()
				+ provider.getAveragingDataManager().getRightEpochBorderInFrames();
		float[] values = new float[valuesCount];

		final double mul = 145; // konstanta rozp�t� generovan�ch hodnot

		for (int i = 0; i < values.length; i++) {
			float value = (float) (mul * Math.random());

			if (Math.random() < 0.5) {
				value = -value;
			}

			values[i] = value;
		}

		return values;
	}

	/**
	 * Inicializace n�hledu aktu�ln�ho nastaven�.
	 * 
	 * @param values
	 *          Funk�n� hodnoty pro zobrazen� k�ivky v n�hledu.
	 */
	private void initCurrentSettingsViewerPanel(float[] values) {
		currentSettingsPanel = new JPanel(new BorderLayout());
		underCurrentSettingsViewer = new JPanel(new BorderLayout());
		Dimension underDimension = new Dimension(200, 100);
		underCurrentSettingsViewer.setMinimumSize(underDimension);
		underCurrentSettingsViewer.setPreferredSize(underDimension);
		underCurrentSettingsViewer.setMaximumSize(underDimension);
		currentSettingsPanel.add(underCurrentSettingsViewer, BorderLayout.CENTER);
		currentSettingsViewer = new SignalViewerPanel(underCurrentSettingsViewer);
		underCurrentSettingsViewer.add(currentSettingsViewer, BorderLayout.CENTER);
		currentSettingsViewer.setCanvasColor(canvasColor);
		currentSettingsViewer.setInterpolationColor(interpolationColor);
		currentSettingsViewer.setFunctionalValuesColor(functionalValuesColor);
		currentSettingsViewer.setAxisColor(axisColor);
		currentSettingsViewer.setValues(values);
		currentSettingsViewer.setMouseMarkEnabled(false);
		currentSettingsViewer.setCoordinateBasicOrigin(exportFrame
				.getExportFrameProvider().getAveragingDataManager()
				.getLeftEpochBorderInFrames());
		currentSettingsViewer.setModeOfRepresentation(exportFrame
				.getExportFrameProvider().getModeOfRepresentation());
		viewerLabel = new JLabel();
		viewerLabel.setLabelFor(currentSettingsViewer);

		currentSettingsPanel.add(viewerLabel, BorderLayout.NORTH);
	}

	/**
	 * Inicializace n�hledu p�ipravovan�ho nastaven�.
	 * 
	 * @param values
	 *          Funk�n� hodnoty pro zobrazen� k�ivky v n�hledu.
	 */
	private void initNewSettingsViewerPanel(float[] values) {
		newSettingsPanel = new JPanel(new BorderLayout());
		underNewSettingsViewer = new JPanel(new BorderLayout());
		Dimension underDimension = new Dimension(exportFrame
				.getExportFrameProvider().getExportViewersWidth(), exportFrame
				.getExportFrameProvider().getExportViewersHeight());
		underNewSettingsViewer.setMinimumSize(underDimension);
		underNewSettingsViewer.setPreferredSize(underDimension);
		underNewSettingsViewer.setMaximumSize(underDimension);
		newSettingsPanel.add(underNewSettingsViewer, BorderLayout.CENTER);
		newSettingsViewer = new SignalViewerPanel(underNewSettingsViewer);
		underNewSettingsViewer.add(newSettingsViewer, BorderLayout.CENTER);
		newSettingsViewer.setCanvasColor(canvasColor);
		newSettingsViewer.setInterpolationColor(interpolationColor);
		newSettingsViewer.setFunctionalValuesColor(functionalValuesColor);
		newSettingsViewer.setAxisColor(axisColor);
		newSettingsViewer.setValues(values);
		newSettingsViewer.setMouseMarkEnabled(false);
		newSettingsViewer.setCoordinateBasicOrigin(exportFrame
				.getExportFrameProvider().getAveragingDataManager()
				.getLeftEpochBorderInFrames());
		newSettingsViewer.setModeOfRepresentation(exportFrame
				.getExportFrameProvider().getModeOfRepresentation());
		viewerLabelSettings = new JLabel();
		viewerLabel.setLabelFor(newSettingsViewer);

		newSettingsPanel.add(viewerLabel, BorderLayout.NORTH);
	}

	/**
	 * Vrac� panel pro nastavov�n� barev exportovan�ho dokumentu.
	 * 
	 * @return Panel pro nastavov�n� barev exportovan�ho dokumentu.
	 */
	private JPanel getColorSettingsPanel() {
		final int componentsWidth = 40;
		final int componentsHeight = 15;
		final Dimension dimension = new Dimension(componentsWidth, componentsHeight);
		JPanel colorSettingsJP = new JPanel(new SpringLayout());
		settingsColorBorder = BorderFactory.createTitledBorder("");
		colorSettingsJP.setBorder(settingsColorBorder);

		ActionListener al = new ALcolorsJB();

		canvasColorJB = new JButton();
		canvasColorJB.addActionListener(al);
		canvasColorJP = new JPanel();
		canvasColorJP.setPreferredSize(dimension);
		canvasColorJP.setMinimumSize(dimension);
		canvasColorJP.setMaximumSize(dimension);
		canvasColorJP.setBackground(canvasColor);

		interpolationColorJB = new JButton();
		interpolationColorJB.addActionListener(al);
		interpolationColorJP = new JPanel();
		interpolationColorJP.setPreferredSize(dimension);
		interpolationColorJP.setMinimumSize(dimension);
		interpolationColorJP.setMaximumSize(dimension);
		interpolationColorJP.setBackground(interpolationColor);

		axisColorJB = new JButton();
		axisColorJB.addActionListener(al);
		axisColorJP = new JPanel();
		axisColorJP.setPreferredSize(dimension);
		axisColorJP.setMinimumSize(dimension);
		axisColorJP.setMaximumSize(dimension);
		axisColorJP.setBackground(axisColor);

		functionalValuesColorJB = new JButton();
		functionalValuesColorJB.addActionListener(al);
		functionalValuesColorJP = new JPanel();
		functionalValuesColorJP.setPreferredSize(dimension);
		functionalValuesColorJP.setMinimumSize(dimension);
		functionalValuesColorJP.setMaximumSize(dimension);
		functionalValuesColorJP.setBackground(functionalValuesColor);

		colorSettingsJP.add(canvasColorJB);
		colorSettingsJP.add(canvasColorJP);
		colorSettingsJP.add(interpolationColorJB);
		colorSettingsJP.add(interpolationColorJP);
		colorSettingsJP.add(axisColorJB);
		colorSettingsJP.add(axisColorJP);
		colorSettingsJP.add(functionalValuesColorJB);
		colorSettingsJP.add(functionalValuesColorJP);

		SpringUtilities.makeCompactGrid(colorSettingsJP, 4, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		return colorSettingsJP;
	}

	/**
	 * Inicializace panelu, na n�j� se umis�uj� ostatn� panely nastaven�.
	 */
	private void initSettingsPanel() {
		settingsPanel = new JPanel(new GridLayout(2, 1));
		BoxLayout settingsLayout = new BoxLayout(settingsPanel, BoxLayout.Y_AXIS);
		settingsPanel.setLayout(settingsLayout);
		settingsPanel.add(getColorSettingsPanel());
		initViewersLookPanel();
		settingsPanel.add(viewersLookPanel);
	}

	/**
	 * Inicializace panelu pro pro um�st�n� n�hled� nastaven�.
	 */
	private void initSignalViewersPanel() {
		float[] functionalValues = getRandomFunctionalValues();
		initCurrentSettingsViewerPanel(functionalValues);
		initNewSettingsViewerPanel(functionalValues);
		GridLayout viewersLayout = new GridLayout(2, 1);
		viewersLayout.setVgap(10);

		signalViewersPanel = new JPanel(viewersLayout);
		signalViewersPanelBorder = BorderFactory.createTitledBorder("");
		signalViewersPanel.setBorder(signalViewersPanelBorder);

		signalViewersPanel.add(currentSettingsPanel);
		signalViewersPanel.add(newSettingsPanel);
	}

	/**
	 * ActionListener atribut� <code>canvasColorJB</code>,
	 * <code>interpolationColorJB</code>, <code>axisColorJB</code> a
	 * <code>functionalValuesColorJB</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALcolorsJB implements ActionListener {
		/**
		 * Nastaven� zvolen�ch barev.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			Color choice;
			Color old;
			if (source == canvasColorJB) {
				try {
					choice = JColorChooser.showDialog(exportFrame,
							getLocalizedText("avg.frame.export.settingpanel.canvas"),
							canvasColorJP.getBackground());
					if (choice != null) {
						old = canvasColor;
						canvasColor = choice;
						if (coloursTest()) {
							canvasColorJP.setBackground(canvasColor);
							newSettingsViewer.setCanvasColor(canvasColor);
						} else {
							canvasColor = old;
						}
					}
				} catch (Exception e) {
					showErrorResourceBundleDialog("avg.frame.export.settingpanel.canvas",
							e);
					logger.warn("ResourceBundle key not found...", e);
					e.printStackTrace();
				}
			} else if (source == interpolationColorJB) {
				try {
					choice = JColorChooser.showDialog(exportFrame,
							getLocalizedText(SettingsPanel.RBKEY_INTERPOLATION_COLOR),
							interpolationColorJP.getBackground());
					if (choice != null) {
						old = interpolationColor;
						interpolationColor = choice;
						if (coloursTest()) {
							interpolationColorJP.setBackground(interpolationColor);
							newSettingsViewer.setInterpolationColor(interpolationColor);
						} else {
							interpolationColor = old;
						}
					}
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							SettingsPanel.RBKEY_INTERPOLATION_COLOR, e);
					logger.warn("ResourceBundle key not found...", e);
					e.printStackTrace();
				}
			} else if (source == axisColorJB) {
				try {
					choice = JColorChooser.showDialog(exportFrame,
							getLocalizedText(SettingsPanel.RBKEY_AXIS_COLOR), axisColorJP
									.getBackground());
					if (choice != null) {
						old = axisColor;
						axisColor = choice;
						if (coloursTest()) {
							axisColorJP.setBackground(axisColor);
							newSettingsViewer.setAxisColor(axisColor);
							invalidate();
							createInside();
							validate();
							repaint();
						} else {
							axisColor = old;
						}
					}
				} catch (Exception e) {
					showErrorResourceBundleDialog(SettingsPanel.RBKEY_AXIS_COLOR, e);
					logger.warn("ResourceBundle key not found...", e);
					e.printStackTrace();
				}
			} else if (source == functionalValuesColorJB) {
				try {
					choice = JColorChooser.showDialog(exportFrame,
							getLocalizedText(SettingsPanel.RBKEY_FUNCTIONAL_COLOR_VALUES),
							functionalValuesColorJP.getBackground());
					if (choice != null) {
						old = functionalValuesColor;
						functionalValuesColor = choice;
						if (coloursTest()) {
							functionalValuesColorJP.setBackground(functionalValuesColor);
							newSettingsViewer.setFunctionalValuesColor(functionalValuesColor);
						} else {
							functionalValuesColor = old;
						}

					}
				} catch (Exception e1) {
					showErrorResourceBundleDialog(
							SettingsPanel.RBKEY_FUNCTIONAL_COLOR_VALUES, e1);
					logger.warn("ResourceBundle key not found...", e1);
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Vrac� referenci na atribut <code>canvasColor</code>.
	 * 
	 * @return Barva pozad� exportovan�ho dokumentu.
	 */
	public Color getCanvasColor() {
		return canvasColor;
	}

	/**
	 * Vrac� referenci na atribut <code>interpolationColor</code>.
	 * 
	 * @return Barva k�ivky interpolace funk�n�ch hodnot.
	 */
	public Color getInterpolationColor() {
		return interpolationColor;
	}

	/**
	 * Vrac� referenci na atribut <code>axisColor</code>.
	 * 
	 * @return Barva os.
	 */
	public Color getAxisColor() {
		return axisColor;
	}

	/**
	 * Vrac� referenci na atribut <code>functionalValuesColor</code>.
	 * 
	 * @return Barva zv�razn�n� funk�n�ch hodnot.
	 */
	public Color getFunctionalValuesColor() {
		return functionalValuesColor;
	}

	/**
	 * Vrac� hodnotu atributu <code>viewersWidth</code>.
	 * 
	 * @return ���ka zobrazen� sign�l�.
	 */
	public int getViewersWidth() {
		return viewersWidth;
	}

	/**
	 * Vrac� hodnotu atributu <code>viewersHeight</code>.
	 * 
	 * @return V�ka zobrazen� sign�l�.
	 */
	public int getViewersHeight() {
		return viewersHeight;
	}

	/**
	 * Test na kontrast zvolen�ch barev.
	 */
	private boolean coloursTest() {
		JERPAGraphicsException e = null;

		if (axisColor.equals(canvasColor) || axisColor.equals(interpolationColor)) {
			e = new JERPAGraphicsException("JERPA021");
		} else if (canvasColor.equals(interpolationColor)) {
			e = new JERPAGraphicsException("JERPA022");
		}
		if (e != null) {
			String errorMSG = JUIGLEErrorParser.getErrorMessage(e.getMessage(),
					LangUtils.JERPA_ERROR_LIST_PATH);
			// display error GUI
			JUIGLErrorInfoUtils.showErrorDialog("ERROR Dialog - Color Mismatch",
					errorMSG, e, Level.WARNING);
			logger.warn(errorMSG, e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ChangeListener atribut� <code>viewersWidthJS</code> a
	 * <code>viewersHeightJS</code>.
	 * 
	 * @author Tom� �ond�k
	 * 
	 */
	private class CLviewersSpinnerJS implements ChangeListener {
		/**
		 * Nastaven� v�ky a ���ky zobrazen�ch sign�l�.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void stateChanged(ChangeEvent event) {
			Object source = event.getSource();

			if (source.equals(viewersWidthJS)) {
				viewersWidth = ((Integer) (viewersWidthJS.getValue())).intValue();
			} else if (source.equals(viewersHeightJS)) {
				viewersHeight = ((Integer) (viewersHeightJS.getValue())).intValue();
			}
		}
	}

	@Override
	public String getResourceBundlePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocalizedResourceBundle(String path) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setResourceBundleKey(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateText() {
		this.resourceBundle = provider.getResourceBundle();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					settingsColorBorder
							.setTitle(getLocalizedText("avg.frame.export.settingpanel.color"));
				} catch (Exception e) {
					showErrorResourceBundleDialog("avg.frame.export.settingpanel.color",
							e);
				}
				try {
					canvasColorJB.setText(getLocalizedText("avg.frame.export.settingpanel.canvas"));
				} catch (Exception e) {
					showErrorResourceBundleDialog("avg.frame.export.settingpanel.canvas",
							e);
				}
				try {
					viewersLookPanelBorder
							.setTitle(getLocalizedText("avg.frame.export.settingpanel.viewers"));
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							"avg.frame.export.settingpanel.viewers", e);
				}
				try {
					viewerLabel
							.setText(getLocalizedText("avg.frame.export.settingpanel.settings"));
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							"avg.frame.export.settingpanel.settings", e);
				}
				try {
					note.setText(getLocalizedText("avg.frame.export.settingpanel.note"));
				} catch (Exception e) {
					showErrorResourceBundleDialog("avg.frame.export.settingpanel.note", e);
				}
				try {
					widthLabel
							.setText(getLocalizedText("avg.frame.export.settingpanel.label.width"));
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							"avg.frame.export.settingpanel.label.width", e);
				}
				try {
					heightLabel
							.setText(getLocalizedText("avg.frame.export.settingpanel.label.height"));
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							"avg.frame.export.settingpanel.label.height", e);
				}
				try {
					viewerLabelSettings
							.setText(getLocalizedText("avg.frame.export.settingpanel.label.settings"));
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							"avg.frame.export.settingpanel.label.settings", e);
				}
				try {
					interpolationColorJB
							.setText(getLocalizedText("avg.frame.export.settingpanel.color.interpolation"));
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							"avg.frame.export.settingpanel.color.interpolation", e);
				}
				try {
					axisColorJB
							.setText(getLocalizedText("avg.frame.export.settingpanel.color.axis"));
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							"avg.frame.export.settingpanel.color.axis", e);
				}
				try {
					functionalValuesColorJB
							.setText(getLocalizedText("avg.frame.export.settingpanel.color.functional"));
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							"avg.frame.export.settingpanel.color.functional", e);
				}
				try {
					signalViewersPanelBorder
							.setTitle(getLocalizedText("avg.frame.export.settingpanel.instantaneous"));
				} catch (Exception e) {
					showErrorResourceBundleDialog(
							"avg.frame.export.settingpanel.instantaneous", e);
				}
			}
		});
	}

	private String getLocalizedText(String key) throws Exception {
		return resourceBundle.getString(key);
	}

	private void showErrorResourceBundleDialog(String key, Exception exp) {
		JUIGLELangException e = new JUIGLELangException("JG003:" + key + ":"
				+ provider.getResourceBundlePath(), exp);
		String errorMSG = JUIGLEErrorParser.getJUIGLEErrorMessage(e.getMessage());
		// display error GUI
		JUIGLErrorInfoUtils.showErrorDialog("Error dialog", errorMSG, e,
				Level.WARNING);
		logger.warn(errorMSG, e);
		e.printStackTrace();
	}

}