package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import noname.JERPAUtils;

import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.AveragingDataManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.application.project.SessionManager;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;

/**
 * Komunika�n� rozhran� mezi pr�m�rovac� komponentou (<code>AveragingWindow</code>)
 * a aplika�n� vrstvou.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.2.0 (2/03/2010)
 * @since 0.1.0 (1/31/2010)
 */
public final class AveragingPanelProvider implements IObserver {
	/**
	 * Reference na t��du poskytuj�c� reference na projekty.
	 */
	private SessionManager app;
	/**
	 * T��da pro komunikaci <code>AveragingWindowProvider</code>u se zbytkem
	 * aplikace.
	 */
	private SignalPerspectiveObservable observable;
	/**
	 * Reference na prvek prezenta�n� vrstvy, prost�ednictv�m kter�ho p�ed�v�
	 * prezenta�n� vrstv� data k zobrazen�.
	 */
	private AveragingPanel averagingPanel;
	/**
	 * Reference na abstrakci p��stupu k projektu.
	 */
	private AveragingDataManager averagingDataManager;
	/**
	 * Reference na projekt, se kter�m u�ivatel pr�v� pracuje.
	 */
	private SignalProject project;

	/**
	 * Vytv��� komunika�n� rozhran� pro komunikaci mezi aplika�n� a prezenta�n�
	 * vrstvou v ��sti pr�m�rov�n� epoch.
	 * 
	 * @param app
	 *          Reference na t��du poskytuj�c� reference na projekty.
	 * @param observable
	 *          Reference na t��du pro komunikaci
	 *          <code>AveragingWindowProvider</code>u se zbytkem aplikace.
	 */
	public AveragingPanelProvider(SessionManager session, SignalPerspectiveObservable observable) {
		this.app = session;
		this.observable = observable;
		project = null;
		averagingDataManager = new AveragingDataManager(project);
		averagingPanel = new AveragingPanel(this);
	}

	/**
	 * Metoda zaji��uje p�echod na n�sleduj�c� epochu �i n-tici epoch.
	 */
	void nextEpoch() {
		try {
			averagingPanel.updateEpochDataSet(averagingDataManager.nextEpoch());
			averagingPanel.setCurrentEpochNumber(averagingDataManager
					.getCurrentEpochNumber());
		} catch (Exception exception) {
			averagingPanel.showException(exception);
		}
	}

	/**
	 * Metoda zaji��uje p�echod na p�edchoz� epochu �i n-tici epoch.
	 */
	void previousEpoch() {
		try {
			averagingPanel.updateEpochDataSet(averagingDataManager.previousEpoch());
			averagingPanel.setCurrentEpochNumber(averagingDataManager
					.getCurrentEpochNumber());
		} catch (Exception exception) {
			averagingPanel.showException(exception);
		}
	}

	/**
	 * Metoda zaji��uje p�echod na libovolnou epochu �i n-tici epoch.
	 * 
	 * @param epochNumber
	 *          ��slo epochy nebo prvn� epochy z n-tice epoch.
	 */
	void jumpToEpoch(int epochNumber) {
		try {
			averagingPanel.updateEpochDataSet(averagingDataManager
					.jumpToEpoch(epochNumber));
			averagingPanel.setCurrentEpochNumber(averagingDataManager
					.getCurrentEpochNumber());
		} catch (Exception exception) {
			averagingPanel.showException(exception);
		}
	}

	/**
	 * Nastavuje typ pou��van�ho pr�m�ru.
	 * 
	 * @param averageType
	 *          Pou��van� pr�m�r (n�kter� z konstant t��dy <code>Averages</code>).
	 */
	void setAverageType(int averageType) {
		averagingDataManager.setAverageType(averageType);
		updateEpochDataSet();
		updateGroupEpochsDataSet();
	}

	/**
	 * Vrac� typ pou��van�ho pr�m�ru.
	 * 
	 * @return Pou��van� pr�m�r (n�kter� z konstant t��dy <code>Averages</code>).
	 */
	int getAverageType() {
		return averagingDataManager.getAverageType();
	}

	/**
	 * Nastavuje posledn� pou�it� typ hromadn�ho za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�r�.
	 * 
	 * @param lastUsedGroupEpochsType
	 *          typ hromadn�ho za�azov�n�/odeb�r�n� (konstanty t��dy
	 *          <code>AveragingDataManager</code> za��naj�c�
	 *          <code>GROUP_EPOCHS_</code>).
	 */
	void setLastUsedGroupEpochsType(int lastUsedGroupEpochsType) {
		averagingDataManager.setLastUsedGroupEpochsType(lastUsedGroupEpochsType);
		updateGroupEpochsDataSet();
	}

	/**
	 * Nastavuje pro v�echny epochy v ur�it�m �asov�m intervalu ve v�ech kan�lech,
	 * zda jsou �i nejsou sou��st� pr�m�r�.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epochy pat�� do pr�m�r�, jinak
	 *          <code>false</code>.
	 */
	void setTimeEpochsSelected(boolean selected) {
		averagingDataManager.setTimeEpochsSelected(selected);

		try {
			averagingPanel.updateGroupEpochDataSet(averagingDataManager
					.getGroupEpochsDataSet());
		} catch (Exception exception) {
			averagingPanel.showException(exception);
		} finally {
			observable.setState(SignalPerspectiveObservable.MSG_NEW_AVERAGES_AVAILABLE);
		}
	}

	/**
	 * Nastavuje pro v�echny epochy, jejich� indexy byly z�sk�ny ze souboru, ve
	 * v�ech kan�lech, zda jsou �i nejsou sou��st� pr�m�r�.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epochy pat�� do pr�m�r�, jinak
	 *          <code>false</code>.
	 */
	void setIndexedEpochsSelected(boolean selected) {
		averagingDataManager.setIndexedEpochsSelected(selected);

		try {
			averagingPanel.updateGroupEpochDataSet(averagingDataManager
					.getGroupEpochsDataSet());
		} catch (Exception exception) {
			averagingPanel.showException(exception);
		} finally {
			observable.setState(SignalPerspectiveObservable.MSG_NEW_AVERAGES_AVAILABLE);
		}
	}

	/**
	 * Nastavuje pro v�echny epochy v konkr�tn�m kan�lu, zda jsou �i nejsou
	 * sou��st� pr�m�ru.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epochy pat�� do pr�m�ru, jinak
	 *          <code>false</code>.
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 */
	void setAllEpochsSelected(boolean selected) {
		averagingDataManager.setAllEpochsSelected(selected);

		try {
			averagingPanel.updateGroupEpochDataSet(averagingDataManager
					.getGroupEpochsDataSet());
		} catch (Exception exception) {
			averagingPanel.showException(exception);
		} finally {
			observable.setState(SignalPerspectiveObservable.MSG_NEW_AVERAGES_AVAILABLE);
		}
	}

	/**
	 * Pro aktu�ln� epochu nastavuje, zda je �i nen� sou��st� pr�m�ru kan�lu
	 * dan�ho parametrem <code>channelOrderInInputFile</code>.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud epocha pat�� do pr�m�ru, jinak
	 *          <code>false</code>.
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 */
	void setEpochInChannelSelected(boolean selected, int channelOrderInInputFile) {
		averagingDataManager.setEpochInChannelSelected(selected,
				channelOrderInInputFile);

		try {
			averagingPanel
					.updateEpochDataSet(averagingDataManager.getEpochDataSet());
		} catch (Exception exception) {
			averagingPanel.showException(exception);
		} finally {
			observable.setState(SignalPerspectiveObservable.MSG_NEW_AVERAGES_AVAILABLE);
		}
	}

	/**
	 * Nastavuje, kter� kan�ly se ��astn� hromadn�ho za�azov�n�/odeb�r�n� epoch
	 * do/z pr�m�r�.
	 * 
	 * @param applyChanges
	 *          Kdy� se kan�l ��astn�, tak <code>true</code>, jinak
	 *          <code>false</code>.
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 */
	void setApplyChanges(boolean applyChanges, int channelOrderInInputFile) {
		averagingDataManager.setApplyChanges(applyChanges, channelOrderInInputFile);
	}

	/**
	 * Vrac�, zda se kan�l ��astn� hromadn�ho za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�ru.
	 * 
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu ve vstupn�m souboru.
	 * @return <code>true</code>, pokud se kan�l ��astn�, jinak
	 *         <code>false</code>.
	 */
	boolean getApplyChanges(int channelOrderInInputFile) {
		return averagingDataManager.getApplyChanges(channelOrderInInputFile);
	}

	/**
	 * Prost�ednictv�m <code>SingnalPerspectiveObservable</code> (atribut <code>gui</code>)
	 * zpr�vu zbytku aplikace, �e je t�eba spustit exportn� okno (realizovan�
	 * t��dou <code>ExportFrame</code>).
	 */
	void runAveragesExport() {
		observable.setState(SignalPerspectiveObservable.MSG_RUN_AVERAGES_EXPORT);
	}

	/**
	 * Vol� metody pro nahr�n� index� epoch ze souboru, kter� je p�ed�n jako
	 * parametr.
	 * 
	 * @param file
	 *          Soubor s indexy epoch (je z�sk�v�n <code>JFileChooser</code>em
	 *          v prezenta�n� vrstv�).
	 */
	void loadEpochsIndexes(File file) {
		try {
			averagingDataManager
					.setLastUsedGroupEpochsType(AveragingDataManager.GROUP_EPOCHS_INDEXES);
			averagingDataManager.loadEpochsIndexes(file);
			averagingPanel.updateGroupEpochDataSet(averagingDataManager
					.getGroupEpochsDataSet());
		} catch (Exception exception) {
			exception.printStackTrace();
			averagingPanel.showException(exception);
		}
	}

	/**
	 * Vrac� hodnotu atributu <code>Project.currentEpochNumber</code>.
	 * 
	 * @return ��slo pr�v� zobrazovan� epochy.
	 */
	int getCurrentEpochNumber() {
		return averagingDataManager.getCurrentEpochNumber();
	}

	/**
	 * Nastavuje, zda maj� b�t zobrazen� sign�ly invertov�ny.
	 * 
	 * @param inverted
	 *          pokud m� b�t sign�l invertov�n, pak <i>true</i>, jinak <i>false</i>.
	 */
	void setInvertedSignalsView(boolean invertedSignals) {
		project.setInvertedSignalsView(invertedSignals);
		observable.setState(SignalPerspectiveObservable.MSG_INVERTED_SIGNALS_VIEW_CHANGED);
	}

	/**
	 * Vrac�, zda jsou pr�v� zobrazovan� sign�ly zobrazov�ny jako invertovan�.
	 * 
	 * @return pokud jsou sign�ly invertov�ny, pak <i>true</i>, jinak <i>false</i>.
	 */
	boolean getInvertedSignalsView() {
		return project.isInvertedSignalsView();
	}

	/**
	 * Metoda se vol�, pokud dojde k jak�koliv zm�n� dat souvisej�c�ch s
	 * pr�m�rov�n�m.
	 */
	private void averagingDataChange() {
		try {
			if (averagingDataManager.getAveragedEpochsIndexes().size() > 0) {
				averagingPanel.setEnabledOperatingElements(true);
			} else {
				/*
				 * Kdy� nen� dostupn� ��dn� epocha ur�en� k pr�m�rov�n�, pak nem� smysl,
				 * aby u�ivatel mohl prov�d�t n�jak� akce s pr�m�rovac�m oknem.
				 * 
				 * Metoda "setEnabledOperatingElements" t��dy "AveragingWindow" mus� b�t
				 * vol�na zde, aby neznehodnotila vol�n� metody
				 * "enableOrdisableNextAndPrevious()" t��dy "AveragingWindow".
				 */
				averagingPanel.setEnabledOperatingElements(false);
			}

			/*
			 * V prom�nn� Project.currentEpochNumber m�e b�t ulo�ena �patn� hodnota
			 * (nap�. po odebr�n� epochy v SignalsWindow, kdy� byla pr�v� tato
			 * posledn� epocha zobrazena v AveragingWindow - v pohledu
			 * EpochByEpochView. Potom dojde k tomu, �e project.currentEpochNumber
			 * ukazuje na neexistuj�c� epochu. Metoda
			 * AveragingDataManager.setCurrentEpochNumber koriguje tato nesmysln�
			 * nastaven�. Tj. je-li v projektu ulo�ena nesmysln� hodnota, dojde k
			 * jej�mu opraven�.
			 */
			averagingDataManager.setCurrentEpochNumber(averagingDataManager
					.getCurrentEpochNumber());
			averagingPanel.setEpochCount(averagingDataManager
					.getAveragedEpochsIndexes().size());
			averagingPanel.createMeanPanels();
			averagingPanel
					.setSignalViewersCoordinateBasicOrigin(averagingDataManager
							.getLeftEpochBorderInFrames());
			averagingPanel
					.updateEpochDataSet(averagingDataManager.getEpochDataSet());
			averagingPanel.updateGroupEpochDataSet(averagingDataManager
					.getGroupEpochsDataSet());
			averagingPanel.setCurrentEpochNumber(averagingDataManager
					.getCurrentEpochNumber());
			observable.setState(SignalPerspectiveObservable.MSG_NEW_AVERAGES_AVAILABLE);
		} catch (Exception exception) {
			exception.printStackTrace();
			averagingPanel.showException(exception);
		}
	}

	/**
	 * Vrac� velikost n-tice epoch.
	 * 
	 * @return Po�et epoch v n-tici.
	 */
	int getEpochWalkingStep() {
		return averagingDataManager.getEpochWalkingStep();
	}

	/**
	 * Nastavuje velikost n-tice epoch.
	 * 
	 * @param step
	 *          Po�et epoch v n-tici.
	 */
	void setEpochWalkingStep(int step) {
		averagingDataManager.setEpochWalkingStep(step);
	}

	/**
	 * Metoda slou��c� k p��jmu zpr�v od zbytku aplikace. V jej�m t�le jsou zpr�vy
	 * vyhodnocov�ny a vol�ny odpov�daj�c� metody.
	 * 
	 * @param observable
	 *          Zdroj zpr�vy.
	 * @param object
	 *          Zpr�va.
	 */
	@Override
	public void update(IObservable observable, Object object) {
		if (object instanceof Integer) {
			int msg = ((Integer) object).intValue();

			switch (msg) {
			case SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
				project = (SignalProject)app.getCurrentProject();
				averagingDataManager.setProject(project);
				averagingPanel.viewsSetupByProject();
			case SignalPerspectiveObservable.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE:
			case SignalPerspectiveObservable.MSG_NEW_BUFFER:
				averagingDataChange();
				break;
			case SignalPerspectiveObservable.MSG_INVERTED_SIGNALS_VIEW_CHANGED:
				averagingPanel.setInvertedSignalsView(project.isInvertedSignalsView());
				break;
			}
		}
	}

	/**
	 * Metoda z�sk� z atributu <code>averagingDataManager</code> data pro
	 * zobrazen� v pohledech <code>EpochByEpochView</code> a
	 * <code>AveragesPreView</code> a p�ed� je do prezenta�n� vrstvy.
	 */
	void updateEpochDataSet() {
		if (averagingPanel != null) {
			try {
				averagingPanel.updateEpochDataSet(averagingDataManager
						.getEpochDataSet());
			} catch (Exception exception) {
				averagingPanel.showException(exception);
			}
		}
	}

	/**
	 * Metoda z�sk� z atributu <code>averagingDataManager</code> data pro
	 * zobrazen� v pohledu <code>GroupEpochsView</code> a p�ed� je do
	 * prezenta�n� vrstvy.
	 */
	void updateGroupEpochsDataSet() {
		if (averagingPanel != null) {
			try {
				averagingPanel.updateGroupEpochDataSet(averagingDataManager
						.getGroupEpochsDataSet());
			} catch (Exception exception) {
				averagingPanel.showException(exception);
			}
		}
	}

	/**
	 * Vrac� referenci na atribut <code>Project.averagedSignalsIndexes</code>.
	 * 
	 * @return Seznam index� kan�l�, kter� se pr�m�ruj�.
	 */
	List<Integer> getAvaragedSignalsIndexes() {
		return averagingDataManager.getAveragedSignalsIndexes();
	}

	/**
	 * Vrac� referenci na atribut <code>Project.header</code>.
	 * 
	 * @return Objekt s hlavi�kov�mi informacemi o vstupn�m souboru.
	 */
	Header getHeader() {
		return project.getHeader();
	}

	/**
	 * Vrac� referenci na atribut <code>averagingWindow</code>.
	 * 
	 * @return Reference na prvek prezenta�n� vrstvy, prost�ednictv�m kter�ho se
	 *         p�ed�vaj� data k zobrazen� prezenta�n� vrstv�.
	 */
	public JPanel getWindow() {
		return averagingPanel;
	}

	/**
	 * Vrac� posledn� pou�it� typ hromadn�ho za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�r�.
	 * 
	 * @return the lastUsedGroupEpochsType typ hromadn�ho za�azov�n�/odeb�r�n�
	 *         (konstanty t��dy <code>AveragingDataManager</code> za��naj�c�
	 *         <code>GROUP_EPOCHS_</code>).
	 */
	int getLastUsedGroupEpochsType() {
		return averagingDataManager.getLastUsedGroupEpochsType();
	}

	/**
	 * Vrac� absolutn� cestu k souboru, ze kter�ho byly naposled na�teny indexy
	 * epcoh.
	 * 
	 * @return Absolutn� cesta k souboru.
	 */
	String getAbsolutePathToIndexes() {
		return averagingDataManager.getAbsolutePathToIndexes();
	}

	/**
	 * Vrac� zp�sob pr�ce se skupinou epoch v pr�m�rovac� komponent�.
	 * 
	 * @return n�kter� z konstant t��dy <code>AveragingDataManager</code>.
	 */
	int getGroupEpochWorkMode() {
		return averagingDataManager.getGroupEpochWorkMode();
	}

	/**
	 * Nastavuje zp�sob pr�ce se skupinou epoch v pr�m�rovac� komponent�.
	 * 
	 * @param groupEpochWorkMode
	 *          n�kter� z konstant t��dy <code>AveragingDataManager</code>.
	 */
	void setGroupEpochWorkMode(int groupEpochWorkMode) {
		averagingDataManager.setGroupEpochWorkMode(groupEpochWorkMode);
	}

	/**
	 * Milisekunda definuj�c� po��tek �asov�ho intervalu v n�m� obsa�en� epochy
	 * jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @return milisekunda za��naj�c� �asov� interval
	 */
	int getTimeSelectionBegin() {
		return averagingDataManager.getTimeSelectionBegin();
	}

	/**
	 * Milisekunda definuj�c� po��tek �asov�ho intervalu v n�m� obsa�en� epochy
	 * jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @param timeSelectionBegin
	 *          milisekunda za��naj�c� �asov� interval
	 */
	void setTimeSelectionBegin(int timeSelectionBegin) {
		averagingDataManager.setTimeSelectionBegin(timeSelectionBegin);

		try {
			averagingPanel.updateGroupEpochDataSet(averagingDataManager
					.getGroupEpochsDataSet());
		} catch (Exception exception) {
			averagingPanel.showException(exception);
		}
	}

	/**
	 * Vrac� milisekundu definuj�c� konec �asov�ho intervalu v n�m� obsa�en�
	 * epochy jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @return milisekunda ukon�uj�c� �asov� interval
	 */
	int getTimeSelectionEnd() {
		return averagingDataManager.getTimeSelectionEnd();
	}

	/**
	 * Nastavuje milisekundu definuj�c� konec �asov�ho intervalu v n�m� obsa�en�
	 * epochy jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @param timeSelectionEnd
	 *          milisekunda ukon�uj�c� �asov� interval
	 */
	void setTimeSelectionEnd(int timeSelectionEnd) {
		averagingDataManager.setTimeSelectionEnd(timeSelectionEnd);

		try {
			averagingPanel.updateGroupEpochDataSet(averagingDataManager
					.getGroupEpochsDataSet());
		} catch (Exception exception) {
			averagingPanel.showException(exception);
		}
	}

	/**
	 * Vrac� hodnotu atributu <code>Project.leftEpochBorderMS</code> p�epo�tenou
	 * na framy.
	 * 
	 * @return Po�et fram� tvo��c�ch lev� okraj epochy.
	 */
	int getLeftEpochBorderInFrames() {
		return averagingDataManager.getLeftEpochBorderInFrames();
	}

	/**
	 * Vrac� hodnotu atributu <code>Project.rightEpochBorderMS</code>
	 * p�epo�tenou na framy.
	 * 
	 * @return Po�et fram� tvo��c�ch prav� okraj epochy.
	 */
	int getRightEpochBorderInFrames() {
		return averagingDataManager.getRightEpochBorderInFrames();
	}

	/**
	 * Vrac� objekt t��dy <code>Icon</icon> pro p�i�azen� ikony 
	 * tla��tk�m v GUI.
	 * @param name N�zev souboru s ikonou.
	 * @return Objekt t��dy <code>Icon</icon>.
	 */
	ImageIcon getIcon(String name) {
		if (name == null) {
			return null;
		}
		ImageIcon icon = (ImageIcon) JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH
				+ name, name);
		return icon;
	}
	
	/**
	 * Return averaging panel
	 * 
	 * @return averaging panel
	 * @version 0.1.0
	 * @since 0.2.0
	 */
	public JXPanel getPanel() {
		return averagingPanel;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Object state) {
		// TODO Auto-generated method stub
		
	}
	
}
