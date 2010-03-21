package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import javax.swing.JPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;
import ch.ethz.origo.jerpa.data.Header;

/**
 * Abstraktn� t��da, od n� mus� b�t odd�d�ny v�echny exportn� panely. Exportn�
 * panel zobrazuje z�kladn� informace o kan�lu + zobrazuje (pomoc�
 * <code>SignalViewer</code>u) pr�b�h pr�m�ru pro tento kan�l.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
abstract class ExportPanel extends JPanel {
	/**
	 * Po�ad� v�skytu sign�lu ve vstupn�m souboru.
	 */
	int channelOrderInInputFile;
	/**
	 * Reference na programov� rozhran� mezi prezenta�n� a aplika�n� vrstvou.
	 */
	ExportFrameProvider exportFrameProvider;

	// BEGIN informace vytahovan� z hlavi�kov�ho souboru (Header)
	/**
	 * Jm�no zobrazovan�ho kan�lu.
	 */
	String channelName;
	/**
	 * Origin�ln� nebo p�epo�tem z�skan� frekvence zobrazovan�ho kan�lu.
	 */
	float channelFrequency;
	/**
	 * Origin�ln� nebo p�epo�tem z�skan� perioda zobrazovan�ho kan�lu.
	 */
	float channelPeriod;
	/**
	 * Uv�d�, kter� z dvojice [frekvence, perioda] je z�sk�na ze vstupn�ho (tj.
	 * druh� je z�skan� p�epo�tem).
	 */
	String channelOriginal;

	// END informace vytahovan� z hlavi�kov�ho souboru (Header)
	/**
	 * Konstruktor abstraktn� t��dy.
	 */
	ExportPanel(int channelOrderInInputFile,
			ExportFrameProvider exportFrameProvider) {
		this.channelOrderInInputFile = channelOrderInInputFile;
		this.exportFrameProvider = exportFrameProvider;
		channelInfoExtraction();
	}

	/**
	 * Z�sk�v� informace o tomuto panelu p�ipadaj�c�m kan�lu. Pomoc� atributu
	 * <b>averagingWindowProvider</b> z�sk�v� hlavi�kov� soubor (<i>Header</i>),
	 * ze kter�ho informace z�sk�v�. Odd�d�n� t��dy u� k informac�m o kan�lu
	 * p�istupuj� p�es atributy rodi�ovsk� t��dy (tj. nemusej� se z�sk�n�m
	 * informac� zab�vat).
	 */
	private void channelInfoExtraction() {
		Header header = exportFrameProvider.getHeader();
		if (header != null) {
			channelName = header.getChannels().get(channelOrderInInputFile).getName();
			channelFrequency = header.getChannels().get(channelOrderInInputFile)
					.getFrequency();
			channelPeriod = header.getChannels().get(channelOrderInInputFile)
					.getPeriod();
			channelOriginal = header.getChannels().get(channelOrderInInputFile)
					.getOriginal();
		} else {
			channelName = "Unknown channel name";
			channelFrequency = 0;
			channelPeriod = 0;
			channelOriginal = "Unknown original value";
		}
	}

	/**
	 * Nastavuje, zda maj� b�t zobrazen� sign�ly invertov�ny.
	 * 
	 * @param inverted
	 *          pokud m� b�t sign�l invertov�n, pak <i>true</i>, jinak <i>false</i>.
	 */
	abstract void invertedSignal(boolean inverted);

	/**
	 * Metoda slou�� k p�ed�n� nov�ch dat k zobrazen�.
	 * 
	 * @param epochDataSet
	 *          nov� data k zobrazen�.
	 */
	abstract void setEpochDataSet(EpochDataSet epochDataSet);

	/**
	 * Nastavuje po�adovan� rozm�r zobrazen� exportovan�ho sign�lu.
	 * 
	 * @param width
	 *          ���ka zobrazen� exportovan�ho sign�lu.
	 * @param height
	 *          v�ka zobrazen� exportovan�ho sign�lu.
	 */
	abstract void setViewerSize(int width, int height);

	/**
	 * Nastavuje po��tek sou�adn� soustavy v zobrazova��ch sign�lu (instance t��dy
	 * SignalViewer).
	 * 
	 * @param coordinateBasicOriginFrame
	 *          pozice po��tku soustavy sou�adnic ve framech.
	 */
	abstract void setSignalViewersCoordinateBasicOrigin(
			int coordinateBasicOriginFrame);
}
