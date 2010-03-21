package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging.SignalViewerPanel;
import ch.ethz.origo.juigle.prezentation.SpringUtilities;

/**
 * Panel pro nastaven� vzhledu exportovan�ho dokumentu.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
final class SettingsPanel extends JPanel {
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
		ExportFrameProvider provider = this.exportFrame.getExportFrameProvider();
		canvasColor = provider.getCanvasColor();
		interpolationColor = provider.getInterpolationColor();
		functionalValuesColor = provider.getFunctionalValuesColor();
		axisColor = provider.getAxisColor();
		createInside();
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
		viewersLookPanel.setBorder(BorderFactory
				.createTitledBorder("Viewers settings"));

		JPanel spinnersJP = new JPanel(new SpringLayout());

		JLabel widthLabel = new JLabel("Width: ");
		viewersWidthJS = new JSpinner();
		viewersWidth = provider.getExportViewersWidth();
		viewersWidthJS.setModel(new SpinnerNumberModel(viewersWidth, 0,
				Integer.MAX_VALUE, 1));
		viewersWidthJS.addChangeListener(cl);
		viewersWidthJS.setMinimumSize(dimension);
		viewersWidthJS.setPreferredSize(dimension);
		// viewersWidthJS.setMaximumSize(dimension);

		widthLabel.setLabelFor(viewersWidthJS);
		JLabel heightLabel = new JLabel("Height: ");
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
		JTextArea note = new JTextArea();
		note.setBackground(new Color(236, 233, 216));
		note.setEnabled(false);
		note.setWrapStyleWord(true);
		note.setLineWrap(true);
		note.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		note.setText("Note: toto nastaven� se neprojev� v okam�it�m n�hledu.");
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
		JLabel viewerLabel = new JLabel("Current settings");
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
		JLabel viewerLabel = new JLabel("New settings");
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
		colorSettingsJP
				.setBorder(BorderFactory.createTitledBorder("Colors option"));

		ActionListener al = new ALcolorsJB();

		canvasColorJB = new JButton("Canvas color");
		canvasColorJB.addActionListener(al);
		canvasColorJP = new JPanel();
		canvasColorJP.setPreferredSize(dimension);
		canvasColorJP.setMinimumSize(dimension);
		canvasColorJP.setMaximumSize(dimension);
		canvasColorJP.setBackground(canvasColor);

		interpolationColorJB = new JButton("Interpolation color");
		interpolationColorJB.addActionListener(al);
		interpolationColorJP = new JPanel();
		interpolationColorJP.setPreferredSize(dimension);
		interpolationColorJP.setMinimumSize(dimension);
		interpolationColorJP.setMaximumSize(dimension);
		interpolationColorJP.setBackground(interpolationColor);

		axisColorJB = new JButton("Axis color");
		axisColorJB.addActionListener(al);
		axisColorJP = new JPanel();
		axisColorJP.setPreferredSize(dimension);
		axisColorJP.setMinimumSize(dimension);
		axisColorJP.setMaximumSize(dimension);
		axisColorJP.setBackground(axisColor);

		functionalValuesColorJB = new JButton("Functional values color");
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
		signalViewersPanel.setBorder(BorderFactory
				.createTitledBorder("Instantaneous preview"));

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
			if (source == canvasColorJB) {
				choice = JColorChooser.showDialog(exportFrame, "Canvas color",
						canvasColorJP.getBackground());
				if (choice != null) {
					canvasColor = choice;
					canvasColorJP.setBackground(canvasColor);
					newSettingsViewer.setCanvasColor(canvasColor);
					coloursTest();
				}
			} else if (source == interpolationColorJB) {
				choice = JColorChooser.showDialog(exportFrame, "Interpolation color",
						interpolationColorJP.getBackground());
				if (choice != null) {
					interpolationColor = choice;
					interpolationColorJP.setBackground(interpolationColor);
					newSettingsViewer.setInterpolationColor(interpolationColor);
					coloursTest();
				}
			} else if (source == axisColorJB) {
				choice = JColorChooser.showDialog(exportFrame, "Axis color",
						axisColorJP.getBackground());
				if (choice != null) {
					axisColor = choice;
					axisColorJP.setBackground(axisColor);
					newSettingsViewer.setAxisColor(axisColor);
					invalidate();
					createInside();
					validate();
					repaint();
					coloursTest();
				}
			} else if (source == functionalValuesColorJB) {
				choice = JColorChooser.showDialog(exportFrame,
						"Functional values color", functionalValuesColorJP.getBackground());
				if (choice != null) {
					functionalValuesColor = choice;
					functionalValuesColorJP.setBackground(functionalValuesColor);
					newSettingsViewer.setFunctionalValuesColor(functionalValuesColor);
					coloursTest();
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
	private void coloursTest() {

		if (axisColor.equals(canvasColor) || axisColor.equals(interpolationColor)) {
			JOptionPane.showMessageDialog(exportFrame,
					"Axis color could same as signals color.", "Color mismatch",
					JOptionPane.WARNING_MESSAGE);
		} else if (canvasColor.equals(interpolationColor)) {
			JOptionPane.showMessageDialog(exportFrame,
					"Background color same as signals color.", "Color mismatch",
					JOptionPane.WARNING_MESSAGE);
		}
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
}
