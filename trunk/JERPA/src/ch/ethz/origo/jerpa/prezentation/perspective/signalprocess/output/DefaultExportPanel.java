package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging.SignalViewerPanel;

/**
 * Z�kladn� exportn� panel.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
final class DefaultExportPanel extends ExportPanel {
	/**
	 * Zobrazova� sign�lu pro zobrazen� pr�m�ru kan�lu.
	 */
	private SignalViewerPanel viewer;
	/**
	 * Komponenta, na n� je atribut <code>viewer</code> um�st�n.
	 */
	private JPanel underViewerJP;

	/**
	 * Vytv��� nov� exportn� panel.
	 * 
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu, ke kter�mu exportn� panel pat��, ve vstupn�m
	 *          souboru.
	 * @param exportFrameProvider
	 *          Reference na programov� rozhran� mezi prezenta�n� a aplika�n�
	 *          vrstvou.
	 */
	DefaultExportPanel(int channelOrderInInputFile,
			ExportFrameProvider exportFrameProvider) {
		super(channelOrderInInputFile, exportFrameProvider);
		this.setLayout(layoutInit());

		underViewerJP = new JPanel();
		underViewerJP.setLayout(new BorderLayout());

		Dimension size = new Dimension(exportFrameProvider.getExportViewersWidth(),
				exportFrameProvider.getExportViewersHeight());
		underViewerJP.setMinimumSize(size);
		underViewerJP.setPreferredSize(size);
		underViewerJP.setMaximumSize(size);

		this.add(underViewerJP, BorderLayout.CENTER);
		viewer = new SignalViewerPanel(underViewerJP);

		viewer.setMouseMarkEnabled(false);
		viewer.setModeOfRepresentation(exportFrameProvider
				.getModeOfRepresentation());
		viewer.setCanvasColor(exportFrameProvider.getCanvasColor());
		viewer.setInterpolationColor(exportFrameProvider.getInterpolationColor());
		viewer.setAxisColor(exportFrameProvider.getAxisColor());
		viewer.setFunctionalValuesColor(exportFrameProvider
				.getFunctionalValuesColor());

		underViewerJP.add(viewer, BorderLayout.CENTER);
		this.add(infoPanelInit(), BorderLayout.WEST);
		this.setBackground(exportFrameProvider.getCanvasColor());
		this.validate();
	}

	/**
	 * Vytvo�en� layoutu a nastaven� jeho parametr� pro exportn� panel.
	 * 
	 * @return Layout exportn�ho panelu.
	 */
	private LayoutManager layoutInit() {
		BorderLayout layout = new BorderLayout();
		layout.setHgap(10);
		return layout;
	}

	/**
	 * Inicializace panelu s informacemi o kan�lu.
	 * 
	 * @return Panel s informacemi o kan�lu.
	 */
	private Container infoPanelInit() {
		JPanel infoJP = new JPanel();

		BoxLayout infoLayout = new BoxLayout(infoJP, BoxLayout.Y_AXIS);
		infoJP.setLayout(infoLayout);

		infoJP.setBackground(exportFrameProvider.getCanvasColor());

		infoJP.add(new JLabel("Channel name: " + channelName));
		infoJP.add(new JLabel("Order in input file: "
				+ (channelOrderInInputFile + Const.ZERO_INDEX_SHIFT)));
		infoJP.add(new JLabel("Frequency: " + channelFrequency + " Hz"));
		infoJP.add(new JLabel("Period: " + channelPeriod + " \u03bcs"));
		infoJP.add(new JLabel("Original: " + channelOriginal));

		return infoJP;
	}

	/**
	 * Nastavuje, zda maj� b�t zobrazen� sign�ly invertov�ny.
	 * 
	 * @param inverted
	 *          pokud m� b�t sign�l invertov�n, pak <i>true</i>, jinak <i>false</i>.
	 */
	@Override
	void invertedSignal(boolean inverted) {
		viewer.setInvertedSignal(inverted);
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
			viewer.setValues(epochDataSet.getAvgWithCurrentEpochValues());
		} else {
			viewer.setValues(epochDataSet.getAvgWithoutCurrentEpochValues());
		}
	}

	/**
	 * Nastavuje po�adovan� rozm�r zobrazen� exportovan�ho sign�lu.
	 * 
	 * @param width
	 *          ���ka zobrazen� exportovan�ho sign�lu.
	 * @param height
	 *          v�ka zobrazen� exportovan�ho sign�lu.
	 */
	@Override
	void setViewerSize(int width, int height) {
		underViewerJP.setMinimumSize(new Dimension(width, height));
		underViewerJP.setPreferredSize(new Dimension(width, height));
		underViewerJP.setMaximumSize(new Dimension(width, height));
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
		viewer.setCoordinateBasicOrigin(coordinateBasicOriginFrame);
	}
}
