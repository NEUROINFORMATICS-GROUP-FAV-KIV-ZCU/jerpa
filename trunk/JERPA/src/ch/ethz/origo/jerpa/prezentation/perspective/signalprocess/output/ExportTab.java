package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import java.awt.BorderLayout;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;

/**
 * Z�lo�ka konkr�tn�ho projektu, na kter� se zobrazuje exportovan� dokument.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
final class ExportTab extends JPanel {
	/**
	 * Index projektu v sezamu <code>SessionManager.projects</code>.
	 */
	private int projectIndex;
	/**
	 * Form�t str�nek exportovan�ho dokumentu.
	 */
	private PageFormat pageFormat;
	/**
	 * Reference na okno, ve kter�m je z�lo�ka um�st�na, pro z�sk�n� reference na
	 * programov� rozhran� s aplika�� vrstvou, pop�. pro vol�n� metod tohoto okna.
	 */
	private ExportFrame exportFrame;
	/**
	 * Data exportovan�ho dokumentu, kter� jsou pr�v� zobrazov�na.
	 */
	private List<EpochDataSet> epochsDataSet;
	/**
	 * Seznam str�nek exportovan�ho dokumentu.
	 */
	private List<Page> pages;
	/**
	 * Objekt pro p�enos str�nek do tisku. Jedn� se o implementaci rohran�
	 * <code>Pageable</code> obsa�enou v Jav�.
	 */
	private Book book;
	/**
	 * Komponenta, kter� umo��uje scrollov�n� zobrazen� exportovan�ho dokumentu.
	 */
	private JScrollPane preview;
	/**
	 * Komponenta, na n� je zobrazov�n exportovan� dokument. Pou��v� se jako zdroj
	 * dat p�i exportu do form�t� JPEG a PNG.
	 */
	private JPanel previewPanel;
	/**
	 * Panel, na kter�m je um�st�n <code>preivewPanel</code>.
	 */
	private JPanel underPreviewJP;

	/**
	 * Vytv��� z�lo�ku jednoho otev�en�ho projektu pro export pr�m�r�.
	 * 
	 * @param projectIndex
	 *          Index projektu v sezamu <code>SessionManager.projects</code>.
	 * @param exportFrame
	 *          Reference na okno, ve kter�m je z�lo�ka um�st�na, pro z�sk�n�
	 *          reference na programov� rozhran� s aplika�� vrstvou, pop�. pro
	 *          vol�n� metod tohoto okna.
	 * @param pageFormat
	 *          Form�t str�nek exportovan�ho dokumentu.
	 */
	ExportTab(int projectIndex, ExportFrame exportFrame, PageFormat pageFormat) {
		this.exportFrame = exportFrame;
		this.pageFormat = pageFormat;
		book = null;
		epochsDataSet = null;
		underPreviewJP = null;
		layoutInit();
	}

	/**
	 * Inicializace a nastaven� parametr� layoutu z�lo�ky.
	 */
	private void layoutInit() {
		this.setLayout(new BorderLayout());
	}

	/**
	 * Um�s�uje str�nky exportovan�ho dokumentu na panel <code>previewPanel</code>.
	 * Z�rove� inicializuje a pln� atribut <code>Book</code> tisknuteln�mi
	 * str�nkami exportovan�ho dokumentu.
	 */
	private void initPreview() {
		underPreviewJP = new JPanel();
		underPreviewJP.setBackground(Preview.BACKGROUND_COLOR);
		PageHeader pageHeader = new PageHeader(exportFrame.getExportFrameProvider());
		PageFooter pageFooter = new PageFooter(exportFrame.getExportFrameProvider());

		if (epochsDataSet != null) {
			pages = PagesCreator.createPagesList(
					exportFrame.getExportFrameProvider(), epochsDataSet, pageHeader,
					pageFooter, pageFormat);
		}

		previewPanel = new Preview(pages, Preview.PRINT);
		underPreviewJP.add(previewPanel);
		preview = new JScrollPane(underPreviewJP);

		book = new Book();

		for (Page page : pages) {
			book.append((Printable) page, pageFormat);
		}
	}

	/**
	 * Nastavuje dat k zobrazen� v exportovan�m dokumentu.
	 * 
	 * @param epochsDataSet
	 *          Nov� data k zobrazen�.
	 */
	void setEpochDataSet(List<EpochDataSet> epochsDataSet) {
		this.epochsDataSet = epochsDataSet;
		createInside();
	}

	/**
	 * Vytv��� vnit�ek z�lo�ky.
	 */
	private void createInside() {
		if (preview != null) {
			this.remove(preview);
		}

		initPreview();

		this.add(preview, BorderLayout.CENTER);

		this.validate();
		this.repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>projectIndex</code>.
	 * 
	 * @return Index projektu v sezamu <code>SessionManager.projects</code>.
	 */
	public int getProjectIndex() {
		return projectIndex;
	}

	/**
	 * Nastavuje po��tek soustavy sou�adnic v exportovan�m dokumentu. T�lo metody
	 * je v sou�asn� podob� aplikace pr�zdn�, proto�e pro libovolnou zm�nu v
	 * exportovan�m dokumentu je vytv��en nov� exportovan� dokument a nastaven�
	 * po��tku soustavy sou�adnic takovou zm�nou je.
	 * 
	 * @param coordinateBasicOriginFrame
	 *          Pozice po��tku soustavy sou�adnic.
	 */
	void setSignalViewersCoordinateBasicOrigin(int coordinateBasicOriginFrame) {
		// preview.setSignalViewersCoordinateBasicOrigin(coordinateBasicOriginFrame);
	}

	/**
	 * Metoda reaguje na zm�ny v nastaven� zobrazen� exportovan�ho dokumentu.
	 */
	void colorSettingWasChanged() {
		createInside();
	}

	/**
	 * Vrac� referenci na atribut <code>previewPanel</code>.
	 * 
	 * @return Panel obsahuj�c� zobrazen� exportovan�ho dokumentu.
	 */
	JPanel getPreview() {
		return previewPanel;
	}

	/**
	 * Vrac� referenci na atribut <code>book</code>.
	 * 
	 * @return Soubor str�nek k tisku.
	 */
	Book getBook() {
		return book;
	}
}
