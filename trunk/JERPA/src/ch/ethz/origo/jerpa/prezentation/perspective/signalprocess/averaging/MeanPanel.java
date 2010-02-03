package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.ObjectBroadcaster;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;
import ch.ethz.origo.jerpa.data.Header;

/**
 * Abstraktn� t��da od kter� jsou odd�d�n� v�echny pr�m�rovac� panely. Obsahuje
 * deklaraci v�ech spole�n�ch atribut� a inicializaci vybran�ch atribut�.
 * Obsahuje konstanty k�duj�c� vzhled jednotliv�ch pr�m�rovac�ch panel� a pole s
 * odpov�daj�c�mi n�zvy.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 */
abstract class MeanPanel extends JXPanel {

	/** Only for serialization */
	private static final long serialVersionUID = -7887751545443546630L;
	/**
	 * Reference na panel akc�.
	 */
	ActionsPanel actionsPanel;
	/**
	 * Komunika�n� rozhran� mezi pr�m�rovac� komponentou (<i>AveragingWindow</i>)
	 * a aplika�n� vrstvou.
	 */
	AveragingPanelProvider averagingWindowProvider;
	/**
	 * Objekt pro pos�l�n� zpr�v mezi jednotliv�mi <code>SignalViewer</code>y.
	 */
	ObjectBroadcaster objectBroadcaster;
	/**
	 * Po�ad� v�skytu sign�lu ve vstupn�m souboru.
	 */
	int channelOrderInInputFile;

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
	 * Konstruktor abstraktn� t��dy, kter� volaj� konstruktory odd�d�n�ch t��d.
	 * 
	 * @param channelOrderInInputFile
	 *          po�ad� kan�lu p�ipadaj�c�ho tomuto panelu ve vstupn�m souboru.
	 * @param averagingWindowProvider
	 *          rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	MeanPanel(int channelOrderInInputFile,
			AveragingPanelProvider averagingWindowProvider) {
		this.channelOrderInInputFile = channelOrderInInputFile;
		this.averagingWindowProvider = averagingWindowProvider;
		this.objectBroadcaster = new ObjectBroadcaster();
		this.actionsPanel = new NullActionsPanel(this);
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
		Header header = averagingWindowProvider.getHeader();
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
	 * Vrac� referenci na atribut <code>objectBroadcaster<code>.
	 * @return reference na atribut <code>objectBroadcaster<code>.
	 */
	ObjectBroadcaster getObjectBroadcaster() {
		return objectBroadcaster;
	}

	/**
	 * Nastavuje panel akc�.
	 * 
	 * @param actionsPanel
	 *          Nov� panel akc�.
	 */
	void setActionsPanel(ActionsPanel actionsPanel) {
		this.actionsPanel = actionsPanel;
	}

	/**
	 * Nastavuje zp�sob zobrazen� sign�lu jednotliv�ch <i>SignalViewer</i>�.
	 * 
	 * @param modeOfViewersRepresentation
	 *          zp�sob zobrazen� sign�lu (pou��vaj� se konstanty t��dy
	 *          <i>SignalViewer</i>).
	 */
	abstract void setModeOfViewersRepresentation(int modeOfViewersRepresentation);

	/**
	 * Metoda slou�� k p�ed�n� nov�ch dat k zobrazen�.
	 * 
	 * @param epochDataSet
	 *          nov� data k zobrazen�.
	 */
	abstract void setEpochDataSet(EpochDataSet epochDataSet);

	/**
	 * P�ibl�en� sign�l� zobrazen�ch <i>SignalViewer</i>y o hodnotu atributu
	 * <b>value</b>. Odd�len� se prov�d� p�ed�n�m parametru se z�pornou hodnotou.
	 * 
	 * @param value
	 *          hodnota, o kolik se m� prov�st p�ibl�en�.
	 */
	abstract void zoomBy(float value);

	/**
	 * P�ibl�en� sign�l� zobrazen�ch <i>SignalViewer</i>y o hodnotu atributu
	 * <b>value</b>. Odd�len� se prov�d� p�ed�n�m parametru se z�pornou hodnotou.
	 * 
	 * @param value
	 *          hodnota, na kterou se m� prov�st p�ibl�en�.
	 */
	abstract void zoomTo(float value);

	/**
	 * Nastavuje, zda maj� b�t zobrazen� sign�ly invertov�ny.
	 * 
	 * @param inverted
	 *          pokud m� b�t sign�l invertov�n, pak <i>true</i>, jinak <i>false</i>.
	 */
	abstract void invertedSignal(boolean inverted);

	/**
	 * Povolen�/zakaz�n� ovl�dac�ch prvk� pohled�.
	 * 
	 * @param enabled
	 *          <i>true</i> pro povolen�, <i>false</i> pro zak�z�n� ovl�dac�ch
	 *          prvk�.
	 */
	abstract void setEnabledOperatingElements(boolean enabled);

	/**
	 * Vrac�, zda jsou ovl�dac� prvky pr�m�rovac�ho panelu povoleny �i zak�z�ny.
	 * 
	 * @return <code>true</code> pokud jsou povoleny, jinak <code>false</code>.
	 */
	abstract boolean isEnabledOperatingElements();

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
