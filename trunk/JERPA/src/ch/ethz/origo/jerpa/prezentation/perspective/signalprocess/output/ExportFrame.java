package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.Sides;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.application.exception.InsufficientDataException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.juigle.application.JUIGLEErrorParser;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

/**
 * T��da realizuj�c� okno exportu v�sledk�.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.2.0 (4/17/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
final class ExportFrame extends JFrame {

	private Logger logger = Logger.getLogger(ExportFrame.class);
	
	private static final String RBKEY_FRAME_TITLE = "avg.frame.export.title";
	/**
	 * Reference na programov� rozhran� mezi prezenta�n� a aplika�n� vrstvou.
	 */
	private ExportFrameProvider exportFrameProvider;
	/**
	 * Tla��tko pro vyti�t�n� exportovan�ho dokumentu.
	 */
	private JButton printJB;
	/**
	 * Tla��tko pro ulo�en� exportovan�ho dokumentu do form�tu JPEG nebo PNG.
	 */
	private JButton saveAsJB;
	/**
	 * Tla��tko pro otev�en� dialogov�ho okna s v�b�rem kan�l�, jejich� pr�m�ry se
	 * maj� exportovat.
	 */
	private JButton channelsEditJB;
	/**
	 * Tla��tko pro otev�en� dialogov�ho okna umo��uj�c�ho nastavit vzhled a
	 * velikost <code>SignalViewer</code>� zobrazuj�c�ch pr�b�h pr�m�r� v
	 * exportovan�m dokumentu.
	 */
	private JButton imageSettingsJB;
	/**
	 * Tla��tko pro otev�en� dialogov�ho okna umo��uj�c�ho p�idat k exportovan�m
	 * pr�m�r�m koment��.
	 */
	private JButton commentaryEditJB;
	/**
	 * Kontejner z�lo�ek pro p�ep�n�n� mezi pr�m�ry otev�en�ch projekt�.
	 */
	private JTabbedPane tabsJTP;
	/**
	 * Soubor po�adavk� na tisk�rnu, na kter� by se m�l exportovan� dokument
	 * tisknout.
	 */
	private PrintRequestAttributeSet printRequest;
	

	/**
	 * Vytv��� nov� okno exportu v�sledk�.
	 * 
	 * @param exportFrameProvider
	 *          Reference na programov� rozhran� mezi prezenta�n� a aplika�n�
	 *          vrstvou.
	 * @throws PerspectiveException
	 */
	ExportFrame(ExportFrameProvider exportFrameProvider)
			throws PerspectiveException {
		this.setIconImage(JUIGLEGraphicsUtils.getImage(JERPAUtils.IMAGE_PATH
				+ "icon.gif"));
		this.exportFrameProvider = exportFrameProvider;
		try {
			this.setTitle(getLocalizedText(ExportFrame.RBKEY_FRAME_TITLE));
		} catch (Exception e) {
			showErrorResourceBundleDialog(ExportFrame.RBKEY_FRAME_TITLE, e);
		}
		printerAttributesInit();
		this.setLayout(layoutInit());
		this.getContentPane().add(createInside(), BorderLayout.CENTER);
		this.getContentPane().add(createToolBar(), BorderLayout.NORTH);
		this.setJMenuBar(createMenuBar());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(500, 400);
		this.setResizable(true);
	}

	/**
	 * Inicializace atributu <code>printRequest</code>.
	 */
	private void printerAttributesInit() {
		printRequest = new HashPrintRequestAttributeSet();
		printRequest.add(MediaSizeName.ISO_A4);
		printRequest.add(Sides.ONE_SIDED);
		printRequest.add(new PrinterResolution(300, 300, PrinterResolution.DPI));
		printRequest.add(new MediaPrintableArea(10, 10, 270, 270,
				MediaPrintableArea.MM));
	}

	/**
	 * Metoda vytv��� menu exportn�ho okna.
	 * 
	 * @return Menu.
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileJM = new JMenu("File");
		JMenu editJM = new JMenu("Edit");
		// JMenu helpJM = new JMenu("Help");

		JMenuItem printJMI = new JMenuItem("Print...");
		JMenuItem saveAsJMI = new JMenuItem("Save As...");
		JMenuItem exitJMI = new JMenuItem("Exit");

		saveAsJMI.addActionListener(new ALsaveAsJMI());
		printJMI.addActionListener(new ALprintJMI());
		exitJMI.addActionListener(new ALexitJMI());

		fileJM.add(saveAsJMI);
		fileJM.add(new JSeparator());
		fileJM.add(printJMI);
		fileJM.add(new JSeparator());
		fileJM.add(exitJMI);

		JMenuItem resultsCommentaryJMI = new JMenuItem("Edit commentary");
		JMenuItem printedChannelsJMI = new JMenuItem("Select channels to export");

		resultsCommentaryJMI.addActionListener(new ALresultsCommentaryJMI());
		printedChannelsJMI.addActionListener(new ALprintedChannelsJMI());

		editJM.add(resultsCommentaryJMI);
		editJM.add(printedChannelsJMI);

		menuBar.add(fileJM);
		menuBar.add(editJM);
		// menuBar.add(helpJM);

		return menuBar;
	}

	/**
	 * Vytvo�en� a nastaven� layoutu exportn�ho okna.
	 * 
	 * @return Layout exportn�ho okna.
	 */
	private LayoutManager layoutInit() {
		return new BorderLayout();
	}

	/**
	 * Metoda nastavuje, zda je okno viditeln� �i nikoliv.
	 * 
	 * @param visible
	 *          <code>true</code>, pokud m� b�t okno viditeln�, jinak
	 *          <code>false</code>.
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		exportFrameProvider.updateSelectedTabEpochDataSet();
	}

	/**
	 * Vytv��� z�lo�ky otev�en�ch projekt�, umis�uje je do kontejneru a vrac� k
	 * um�st�n� do exportn�ho okna.
	 * 
	 * @return Z�lo�ky projekt�.
	 */
	private Container createInside() {
		tabsJTP = new JTabbedPane();

		String[] projectNames = exportFrameProvider.getProjectsNames();

		for (int i = 0; i < projectNames.length; i++) {
			PrinterJob pj = PrinterJob.getPrinterJob();
			ExportTab exportTab = new ExportTab(i, this, pj
					.getPageFormat(printRequest));
			tabsJTP.add(projectNames[i], exportTab);
		}

		tabsJTP.addChangeListener(new CLtabsJTP());
		return tabsJTP;
	}

	/**
	 * Vytv��� n�strojovou li�tu pro �pravy vzhledu a export v�sledn�ch pr�m�r�.
	 * 
	 * @return N�strojov� li�ta.
	 * @throws PerspectiveException
	 */
	private Container createToolBar() throws PerspectiveException {
		JToolBar toolBarJTB = new JToolBar();
		saveAsJB = new JButton();
		saveAsJB.setToolTipText("Save As");
		saveAsJB.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH
				+ "save_48.png", 32, 32));
		printJB = new JButton();
		printJB.setToolTipText("Print");
		printJB.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH
				+ "Print_32.png"));

		channelsEditJB = new JButton("Select channels to export");
		imageSettingsJB = new JButton("Image settings");
		commentaryEditJB = new JButton("Edit commentary");

		saveAsJB.addActionListener(new ALsaveAsJMI());
		printJB.addActionListener(new ALprintJMI());
		channelsEditJB.addActionListener(new ALprintedChannelsJMI());
		imageSettingsJB.addActionListener(new ALimageSettingsJB());
		commentaryEditJB.addActionListener(new ALresultsCommentaryJMI());

		toolBarJTB.add(saveAsJB);
		toolBarJTB.add(printJB);
		toolBarJTB.add(new JToolBar.Separator());
		toolBarJTB.add(channelsEditJB);
		toolBarJTB.add(imageSettingsJB);
		toolBarJTB.add(commentaryEditJB);

		toolBarJTB.setFloatable(false);

		return toolBarJTB;
	}

	/**
	 * Slou�� k zobrazen� dialogu pro zad�n� koment��e.
	 */
	private void viewCommentaryDialog() {
		JTextArea commentaryJTA = new JTextArea();
		commentaryJTA.setWrapStyleWord(true);
		commentaryJTA.setEditable(true);
		commentaryJTA.setLineWrap(true);
		commentaryJTA.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		commentaryJTA.setBackground(exportFrameProvider.getCanvasColor());
		commentaryJTA.setText(exportFrameProvider.getCommentary());
		commentaryJTA.setPreferredSize(new Dimension(400, 100));
		commentaryJTA.setMaximumSize(new Dimension(400, 100));
		commentaryJTA.setMinimumSize(new Dimension(400, 100));
		Object[] options = { "Set commentary", "Cancel" };
		final int n = JOptionPane.showOptionDialog(this, commentaryJTA,
				"Edit commentary", JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[1]);

		if (n == 0) {
			exportFrameProvider.setCommentary(commentaryJTA.getText());
			exportFrameProvider.updateSelectedTabEpochDataSet();
		}
	}

	/**
	 * Slou�� k zobrazen� dialogu pro v�b�r kan�l�, jejich� pr�m�ry se maj�
	 * objevit v exportovan�m dokumentu.
	 */
	private void viewChannelsSelectionDialog() {
		ChannelsSelection selection = new ChannelsSelection(exportFrameProvider);
		Object[] options = { "OK", "Cancel" };
		final int n = JOptionPane.showOptionDialog(this, selection,
				"Exported channels selection", JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[1]);

		if (n == 0) {
			JCheckBox[] boxes = selection.getChannels();
			int[] channelsOrderInInputFile = selection.getChannelsOrderInInputFile();
			for (int i = 0; i < boxes.length; i++) {
				exportFrameProvider.setChannelToExport(channelsOrderInInputFile[i],
						boxes[i].isSelected());
			}
			exportFrameProvider.updateSelectedTabEpochDataSet();
		}
	}

	/**
	 * Nastavuje jako aktivn� z�lo�ku, s jej�m� projektem u�ivatel pr�v� pracuje.
	 * 
	 * @param tabIndex
	 *          Index aktu�ln�ho projektu.
	 */
	void setSelectedTab(int tabIndex) {
		tabsJTP.setSelectedIndex(tabIndex);
	}

	/**
	 * Nastavuje data k zobrazen� pr�v� vybran� z�lo�ce.
	 * 
	 * @param epochsDataSet
	 *          Data k zobrazen�.
	 */
	void setSelectedTabEpochDataSet(List<EpochDataSet> epochsDataSet) {
		((ExportTab) (tabsJTP.getSelectedComponent()))
				.setEpochDataSet(epochsDataSet);
	}

	/**
	 * Metoda slou�� pro zobrazen� v�jimek nastal�ch p�i b�hu programu. Pro zn�m�
	 * a o�ek�van� v�jimky jsou nastaveny odpov�daj�c� parametry zobrazen�
	 * v�jimky. Nezn�m� v�jimky se zobraz� jako chybov� hl�en� nezn�m�ho typu.
	 * 
	 * @param exception
	 *          V�jimka.
	 */
	void showException(Exception exception) {
		if (exception instanceof InsufficientDataException) {
			JOptionPane.showMessageDialog(this, exception.getMessage(),
					"Insufficient data", JOptionPane.WARNING_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Unknown problem:\n"
					+ exception.getMessage(), exception.getClass().getSimpleName(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Nastavuje po��tek soustavy sou�adnic zobrazova�� pr�m�ru (
	 * <code>SignalViewer</code>�) v aktu�ln� z�lo�ce.
	 * 
	 * @param coordinateBasicOriginFrame
	 *          Pozice po��tku soustavy sou�adnic.
	 */
	void setSelectedTabSignalViewersCoordinateBasicOrigin(
			int coordinateBasicOriginFrame) {
		((ExportTab) (tabsJTP.getSelectedComponent()))
				.setSignalViewersCoordinateBasicOrigin(coordinateBasicOriginFrame);
	}

	/**
	 * Upozorn�n� aktu�ln� z�lo�ky na zm�nu nastaven� barev exportovan�ho
	 * dokumentu.
	 */
	void colorSettingWasChanged() {
		((ExportTab) (tabsJTP.getSelectedComponent())).colorSettingWasChanged();
	}

	/**
	 * Metoda vrac� referenci na programov� rozhran� pro komunikaci mezi
	 * prezenta�n� a aplika�n� vrstvou.
	 * 
	 * @return Rozhran� pro komunikaci mezi prezenta�n� a aplika�n� vrstvou.
	 */
	ExportFrameProvider getExportFrameProvider() {
		return exportFrameProvider;
	}

	/**
	 * ActionListener tla��tek pro tisk exportovan�ho dokumentu.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALprintJMI implements ActionListener {
		/**
		 * Metoda realizuje tisk exportovan�ho dokumentu na aktu�ln� z�lo�ce.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			PrinterJob pj = PrinterJob.getPrinterJob();
			ExportTab tab = ((ExportTab) (tabsJTP.getSelectedComponent()));
			pj.setPageable(tab.getBook());
			try {
				if (pj.printDialog()) {
					pj.pageDialog(pj.getPageFormat(printRequest));
					pj.print();
				}
			} catch (PrinterException e) {
				showException(e);
			}
		}
	}

	/**
	 * ActionListener tla��tek pro zobrazen� dialogov�ho okna umo��uj�c�ho p�id�n�
	 * koment��e k exportovan�mu dokumentu.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALresultsCommentaryJMI implements ActionListener {
		/**
		 * Vol� metodu <code>viewCommentaryDialog</code>.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			viewCommentaryDialog();
		}
	}

	/**
	 * ActionListener tla��tek pro zobrazen� dialogov�ho okna umo��uj�c�ho v�b�r
	 * kan�l�, jejich� pr�m�ry maj� b�t sou��st� exportovan�ho dokumentu.
	 * 
	 * @author Tom� �ond�k
	 * 
	 */
	private class ALprintedChannelsJMI implements ActionListener {
		/**
		 * Vol� metodu <code>viewChannelsSelectionDialog</code>.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			viewChannelsSelectionDialog();
		}
	}

	/**
	 * ActionListener pro tla��tko <i>Exit</i> v menu okna.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALexitJMI implements ActionListener {
		/**
		 * Vol� metodu <code>JFrame.dispose</code>.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			dispose();
		}
	}

	/**
	 * Metoda realizuje ukl�d�n� exportovan�ho dokumentu ve form�tech JPEG a PNG.
	 */
	private void saveAveragesAsImage() {
		final JFileChooser fc = new JFileChooser();

		fc.setDialogTitle("Save As");
		fc.setFileFilter(new FileNameExtensionFilter("JPG image (*.jpg)", "jpg"));
		fc
				.setFileFilter(new FileNameExtensionFilter("JPEG image (*.jpeg)",
						"jpeg"));
		fc.setFileFilter(new FileNameExtensionFilter("PNG image (*.png)", "png"));

		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			File outputFile = new File(file.getAbsolutePath() + "."
					+ ((FileNameExtensionFilter) fc.getFileFilter()).getExtensions()[0]);
			JComponent output = ((ExportTab) (tabsJTP.getSelectedComponent()))
					.getPreview();
			BufferedImage image = new BufferedImage(output.getWidth(), output
					.getHeight(), BufferedImage.TYPE_INT_BGR);
			Graphics2D g2 = image.createGraphics();
			output.paint(g2);
			g2.dispose();
			try {
				ImageIO.write(image, ((FileNameExtensionFilter) fc.getFileFilter())
						.getExtensions()[0], outputFile);
			} catch (IOException exception) {
				showException(exception);
			}
		}
	}

	/**
	 * ActionListener tla��tek pro ukl�d�n� exportovan�ho dokumentu ve form�tech
	 * JPEG a PNG.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALsaveAsJMI implements ActionListener {
		/**
		 * Vol� metodu <code>saveAveragesAsImage</code>.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			saveAveragesAsImage();
		}
	}

	/**
	 * ChangeListener kontejneru z�lo�ek
	 * 
	 * @author Tom� �ond�k
	 */
	private class CLtabsJTP implements ChangeListener {
		/**
		 * P�i zm�n� z�lo�ky je nutn� obnovit data na n� zobrazovan�. Metoda proto
		 * nejprve nastavuje zvolenou z�lo�ku jako hlavn� a pak ��d�
		 * <code>ExportFrameProvider</code> o zasl�n� nov�ch dat k zobrazen�.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void stateChanged(ChangeEvent event) {
			exportFrameProvider
					.setSelectedTabProjectIndex(tabsJTP.getSelectedIndex());
			exportFrameProvider.updateSelectedTabEpochDataSet();
		}
	}

	/**
	 * Metoda zobrazuje dialogov� okno pro zm�nu vzhledu exportovan�ho dokumentu.
	 */
	private void imageSettings() {
		Object[] selectionValues = { "Save", "Cancel" };
		SettingsPanel colorPanel;
		colorPanel = new SettingsPanel(this);
		int choice = JOptionPane.showOptionDialog(this, (Object) colorPanel,
				"Image settings", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, selectionValues, selectionValues[1]);

		if (choice == 0) // bylo stisknuto "Save"
		{
			exportFrameProvider.setExportViewersWidth(colorPanel.getViewersWidth());
			exportFrameProvider.setExportViewersHeight(colorPanel.getViewersHeight());
			exportFrameProvider.setAxisColor(colorPanel.getAxisColor());
			exportFrameProvider.setCanvasColor(colorPanel.getCanvasColor());
			exportFrameProvider.setFunctionalValuesColor(colorPanel
					.getFunctionalValuesColor());
			exportFrameProvider.setInterpolationColor(colorPanel
					.getInterpolationColor());
		}
	}

	/**
	 * ActionListener tla��tek otev�raj�c�ch dialog s nastaven�m vzhledu
	 * exportovan�ho dokumentu.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALimageSettingsJB implements ActionListener {
		/**
		 * Vol� metodu <code>imageSettings</code>.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			imageSettings();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 * @version 0.1.0 (4/17/2010)
	 * @since 0.2.0 (4/17/2010)
	 */
	private String getLocalizedText(String key) throws Exception {
		return exportFrameProvider.getResourceBundle().getString(key);
	}
	
	private void showErrorResourceBundleDialog(String key, Exception exp) {
		JUIGLELangException e = new JUIGLELangException("JG003:" + key + ":"
				+ exportFrameProvider.getResourceBundlePath(), exp);
		String errorMSG = JUIGLEErrorParser.getJUIGLEErrorMessage(e.getMessage());
		// display error GUI
		JUIGLErrorInfoUtils.showErrorDialog("Error dialog", errorMSG, e,
				Level.WARNING);
		logger.warn(errorMSG, e);
		e.printStackTrace();
	}
	
}