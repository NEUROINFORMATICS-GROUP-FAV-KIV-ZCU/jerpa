package ch.ethz.origo.jerpa.application.perspective.signalprocess.project;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.Averages;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.AveragingDataManager;
import ch.ethz.origo.jerpa.application.project.Project;
import ch.ethz.origo.jerpa.application.project.ProjectMementoCaretaker;
import ch.ethz.origo.jerpa.data.Artefact;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging.SignalViewerPanel;
import ch.ethz.origo.juigle.prezentation.JUIGLEFileChooser;

/**
 * T��da poskytuj�c� podporu projekt�.<br/>
 * <br/>
 * V t�to t��d� jsou ulo�eny stavov� prom�nn� zachycuj�c� aktu�ln� stav
 * projektu. Stavov� prom�nn� mus� b�t ulo�eny/obnoveny podle vzoru v metod�ch
 * <code>createMemento()</code> a <code>setMemento()</code> a ve t��d�
 * ProjectState.<br/>
 * <br/>
 * Aktu�ln� stav projektu je mo�no ulo�it do historie zd�d�nou metodou
 * <code>doCommand()</code> (vol� se p�ed vol�n�m jin�ch metod, kter� modifikuj�
 * stav projektu), pot� je mo�no historii ulo�en�ch stav� proch�zet zd�d�n�mi
 * metodami <code>undo()</code> a <code>redo()</code>.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @author Jiri Kucera (jERP Studio)
 * @version 0.1.0 10/25/2009
 * @since 0.1.0 (06/07/2009)
 * @see Project
 */
public class SignalProject extends Project {

	/*
	 * Stavov� prom�nn� projektu - nezapomenout je p�idat do metod createMemento()
	 * a setMemento() a do t��dy ProjectState.
	 */
	private File projectFile; // neukladat

	private File dataFile; // uklada se, nacita se (zatim jen nazev souboru)
	// private Buffer buffer; // neukladat do XML

	// private List<Buffer> buffers; // TODO - promyslet realizaci undo/redo a
	// mazani starych bufferu
	private Buffer buffer;

	private int currentBuffer; // neukladat

	private Header header; // uklada se, nacita se

	private String name; // uklada se, nacita se

	/**
	 * Indexy kan�l� ur�en�ch k zobrazen�.<br/>
	 * Hodnoty v seznamu odpov�daj� index�m kan�l� v seznamu Header.channels -
	 * TODO - potvrdit
	 */
	private List<Integer> selectedChannels; // uklada se, nacita se

	/**
	 * Indexy pr�m�rovan�ch sign�l� ve vstupn�m souboru. Hodnoty v seznamu
	 * odpov�daj� index�m kan�l� v seznamu Header.channels - TODO - potvrdit
	 */
	private List<Integer> averagedSignalsIndexes; // uklada se, nacita se

	/**
	 * Ud�v�, pro kter� kan�ly plat� hromadn� p�id�v�n�/odeb�r�n� epoch. Index
	 * pole se v�dy shoduje s po�ad�m kan�lu, ke kter�mu pat�� booleovsk� hodnota,
	 * ve vstupn�m souboru. Pole je instanciov�no a iniciov�no, pokud nen�
	 * <b>null</b>, v metod� <i>setHeader</i>, proto�e jeho d�lka odpov�d� po�tu
	 * kan�l� ve vstupn�m souboru - tj. hodnot�, kterou nese instance t��dy
	 * <i>Header</i>, tzn. indexy v poli odpov�daj� index�m kan�l� v seznamu
	 * Header.channels.
	 */
	private boolean[] applyChanges; // uklada se, nacita se

	/**
	 * Vzd�lennost lev�ho okraje epochy od "st�edu" epochy ve milisekund�ch.
	 */
	private int leftEpochBorderMs; // uklada se, nacita se
	private int leftEpochBorderF; // uklada se, nacita se

	/**
	 * Vzd�lennost prav�ho okraje epochy od "st�edu" epochy ve milisekund�ch.
	 */
	private int rightEpochBorderMs; // uklada se, nacita se
	private int rightEpochBorderF; // uklada se, nacita se

	/**
	 * ��slo pr�v� zobrazen� epochy.
	 */
	private int currentEpochNumber; // uklada se, nacita se

	/**
	 * Typ pou�it�ho pr�m�ru.
	 */
	private int averageType; // uklada se, nacita se

	private int epochWalkingStep; // uklada se, nacita se

	// END - STAVOV� PROM�NN� AVERAGING_DATA_MANAGERU
	private List<Epoch> allEpochsList; // uklada se, nacita se

	/**
	 * Hodnoty v seznamu odpov�daj� index�m kan�l� v seznamu Project.allEpochsList
	 * - TODO - potvrdit
	 */
	private List<Integer> averagedEpochsIndexes; // uklada se, nacita se

	/**
	 * Cesta k souboru s indexy pro hromadn� za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�r�.
	 */
	private String absolutePathToIndexes; // uklada se, nacita se

	/**
	 * ��k�, jestli se p�i pr�ci se skupinou epoch jedn� o za�azov�n� nebo
	 * odeb�r�n� z pr�m�r�. Nab�v� jedn� ze dvou n�sleduj�c�ch hodnot:
	 * <ul>
	 * <li><code>AveragingDataManager.GROUP_EPOCHS_ADDING</code>, kdy� jsou epochy
	 * p�id�v�ny<br/>
	 * <li><code>AveragingDataManager.GROUP_EPOCHS_REMOVING</code>, kdy� jsou
	 * epochy odeb�r�ny<br/>
	 * </ul>
	 */
	private int groupEpochWorkMode; // uklada se, nacita se

	/**
	 * Indexy epoch za�azovan�ch/odeb�ran�ch do/z pr�m�r� z�skan� ze souboru.
	 * Hodnoty v seznamu <b>neodpov�daj�</b> index�m kan�l� v seznamu
	 * Project.allEpochsList, ale index�m v Project.averagedEpochsIndexes.
	 */
	private List<Integer> loadedEpochsIndexes; // uklada se, nacita se

	private List<Artefact> artefacts; // uklada se, nacita se

	/**
	 * Popisuje, jestli maj� b�t zobrazovan� sign�ly invertov�ny (tj. osa
	 * funk�n�ch hodnot roste v opa�n�m sm�ru, ne� v kart�zsk�ch sou�adnic�ch v
	 * E2).
	 */
	private boolean invertedSignalsView; // uklada se, nacita se

	/**
	 * Posledn� pou�it� typ hromadn�ho za�azov�n�/odeb�r�n� epoch do/z pr�m�r�.
	 * Nab�v� jedn� z n�sleduj�c�ch hodnot:
	 * <ul>
	 * <li><code>AveragingDataMaanger.GROUP_EPOCHS_TIME</code>, pro
	 * za�azov�n�/odeb�r�n� epoch v �asov�m �seku<br>
	 * <li><code>AveragingDataMaanger.GROUP_EPOCHS_INDEXES</code>, pro
	 * za�azov�n�/odeb�r�n� epoch podle ze souboru na�ten�ch index�<br>
	 * <li><code>AveragingDataMaanger.GROUP_EPOCHS_ALL</code>, pro
	 * za�azov�n�/odeb�r�n� v�ech epoch<br>
	 * </ul>
	 */
	private int lastUsedGroupEpochsType; // uklada se, nacita se

	/**
	 * Milisekunda definuj�c� po��tek �asov�ho intervalu, v n�m� obsa�en� epochy
	 * jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 */
	private int timeSelectionBegin; // uklada se, nacita se

	/**
	 * Milisekunda definuj�c� konec �asov�ho intervalu, v n�m� obsa�en� epochy
	 * jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 */
	private int timeSelectionEnd; // uklada se, nacita se

	/**
	 * P�ibl�en� exportovan�ho pr�b�hu sign�lu v ose funk�n�ch hodnot v exportn�m
	 * okn�.
	 */
	private float zoomY; // uklada se, nacita se

	// BEGIN - prom�nn� vzhledu exportu
	/**
	 * Barva pozad� pr�b�hu sign�lu v exportn�m okn�.
	 */
	private Color canvasColor; // Inicializov�no v exportAttributesInit(). //
	// uklada se, nacita se

	/**
	 * Barva interpolace funk�n�ch hodnot sign�lu v exportn�m okn�.
	 */
	private Color interpolationColor; // Inici. v exportAttributesInit(). //
	// uklada se, nacita se

	/**
	 * Barva os sou�adn�ho syst�mu v exportn�m okn�.
	 */
	private Color axisColor; // Inicializov�no v metod� exportAttributesInit().
	// // uklada se, nacita se

	/**
	 * Barva zv�razn�n� funk�n�ch hodnot sign�lu v exportn�m okn�.
	 */
	private Color functionalValuesColor; // Inic. v exportAttributesInit(). //
	// uklada se, nacita se

	/**
	 * Koment�� exportovan�ch pr�m�r� v exportn�m okn�.
	 */
	private String commentary; // Inicializov�no v exportAttributesInit(). //
	// uklada se, nacita se

	/**
	 * Zp�sob zobrazen� sign�l� v <b>SignalViewer</b>ech v exportn�m okn�. Nab�v�
	 * jedn� ze t�� n�sleduj�c�ch hodnot: <br/>
	 * <code>SignalViewer.FUNCTIONAL_VALUES</code> pro zobrazen� funk�n�ch hodnot<br/>
	 * <code>SignalViewer.INTERPOLATION</code> pro zobrazen� interpolace funk�n�ch
	 * hodnot<br/>
	 * <code>SignalViewer.FUNCTIONAL_VALUES_AND_INTERPOLATION</code> pro zobrazen�
	 * interpolace funk�n�ch hodnot se zv�razn�n�m funk�n�ch hodnot<br/>
	 */
	private int modeOfRepresentationInExport; // uklada se, nacita se

	/**
	 * ���ka zobrazen� exportovan�ch pr�m�r�.
	 */
	private int exportViewersWidth; // uklada se, nacita se

	/**
	 * V�ka zobrazen� exportovan�ch pr�m�r�.
	 */
	private int exportViewersHeight; // uklada se, nacita se
	/**
	 * ��k�, jestli se kan�l m� nebo nem� objevit v exportovan�m dokumentu.
	 */
	private boolean[] channelsToExport; // NEUKL�DAT

	public SignalProject() {
		super();
		averagingDataManagerInit();
		exportAttributesInit();
		buffer = null;
		// buffers = new ArrayList<Buffer>();
		currentBuffer = -1;
		header = null;
		averagedSignalsIndexes = new ArrayList<Integer>();
		projectFile = null;
		absolutePathToIndexes = null;
		dataFile = null;
		averagedEpochsIndexes = new ArrayList<Integer>();
		leftEpochBorderMs = Integer.MIN_VALUE;
		rightEpochBorderMs = Integer.MIN_VALUE;
		leftEpochBorderF = Integer.MIN_VALUE;
		rightEpochBorderF = Integer.MIN_VALUE;

		allEpochsList = new ArrayList<Epoch>();
		// selectedChannels = new ArrayList<Integer>(); neinicializovat, proto�e se
		// pak p�i na��t�n� nepozn� jestli je pr�zdn� nebo se jedn� o nov� projekt
		artefacts = new ArrayList<Artefact>();
		invertedSignalsView = false;
	}

	/**
	 * Inicializace atribut� nezbytn�ch pro pr�m�rov�n� epoch.
	 */
	private void averagingDataManagerInit() {
		applyChanges = null;
		averagedSignalsIndexes = null;
		currentEpochNumber = 0;
		averageType = Averages.ARITHMETICAL;
		epochWalkingStep = 1;
		lastUsedGroupEpochsType = AveragingDataManager.GROUP_EPOCHS_ALL;
		absolutePathToIndexes = null;
		loadedEpochsIndexes = new ArrayList<Integer>();
		groupEpochWorkMode = AveragingDataManager.GROUP_EPOCHS_ADDING;
		timeSelectionBegin = 0;
		timeSelectionEnd = 1;
		zoomY = SignalViewerPanel.ZOOM_Y_ORIGINAL;
	}

	/**
	 * Inicializace atribut� vzhledu exportovan�ch pr�m�r� a dal��ch atribut�
	 * ovliv�uj�c�ch vzhled v�sledn�ho exportu.
	 */
	private void exportAttributesInit() {
		canvasColor = Color.WHITE;
		interpolationColor = Color.DARK_GRAY;
		functionalValuesColor = Color.RED;
		axisColor = Color.BLUE;
		commentary = new String("");
		modeOfRepresentationInExport = SignalViewerPanel.INTERPOLATION;
		exportViewersWidth = 200;
		exportViewersHeight = 100;
		channelsToExport = null;
	}

	@Override
	public ProjectMementoCaretaker createMemento() {
		// Aby fungovalo undo/redo, ukl�dat hlubok� kopie objekt�, nikoliv pouze
		// reference na n�.
		SignalProject project = new SignalProject();

		project.absolutePathToIndexes = absolutePathToIndexes;
		project.allEpochsList = new ArrayList<Epoch>();
		for (Epoch e : allEpochsList) {
			project.allEpochsList.add(e);
		}
		project.applyChanges = applyChanges.clone();
		project.artefacts = new ArrayList<Artefact>();
		for (Artefact a : artefacts) {
			project.artefacts.add(a);
		}
		project.averageType = averageType;
		project.averagedEpochsIndexes = new ArrayList<Integer>();
		for (Integer i : averagedEpochsIndexes) {
			project.averagedEpochsIndexes.add(i);
		}
		project.averagedSignalsIndexes = new ArrayList<Integer>();
		for (Integer i : averagedSignalsIndexes) {
			project.averagedSignalsIndexes.add(i);
		}
		project.axisColor = new Color(axisColor.getRGB());
		project.canvasColor = new Color(canvasColor.getRGB());
		project.commentary = commentary;
		project.currentBuffer = currentBuffer;
		project.currentEpochNumber = currentEpochNumber;
		project.dataFile = dataFile.getAbsoluteFile();
		project.epochWalkingStep = epochWalkingStep;
		project.exportViewersHeight = exportViewersHeight;
		project.exportViewersWidth = exportViewersWidth;
		project.functionalValuesColor = new Color(functionalValuesColor.getRGB());
		project.groupEpochWorkMode = groupEpochWorkMode;
		project.header = header.clone();
		project.interpolationColor = new Color(interpolationColor.getRGB());
		project.invertedSignalsView = invertedSignalsView;
		project.lastUsedGroupEpochsType = lastUsedGroupEpochsType;
		project.leftEpochBorderF = leftEpochBorderF;
		project.leftEpochBorderMs = leftEpochBorderMs;
		project.loadedEpochsIndexes = new ArrayList<Integer>();
		for (Integer i : loadedEpochsIndexes) {
			project.loadedEpochsIndexes.add(i);
		}
		project.modeOfRepresentationInExport = modeOfRepresentationInExport;
		project.name = name;
		try {
			project.projectFile = projectFile.getAbsoluteFile();
		} catch (NullPointerException e) {
			project.projectFile = null;
		}
		project.rightEpochBorderF = rightEpochBorderF;
		project.rightEpochBorderMs = rightEpochBorderMs;
		project.selectedChannels = new ArrayList<Integer>();
		for (Integer i : selectedChannels) {
			project.selectedChannels.add(i);
		}
		project.timeSelectionBegin = timeSelectionBegin;
		project.timeSelectionEnd = timeSelectionEnd;
		project.zoomY = zoomY;

		project.setBuffer(buffer);

		return new ProjectMementoCaretaker(project);
	}

	@Override
	public void openFile() {

	}

	@Override
	public void saveFile() {

	}

	@Override
	public void setMemento(ProjectMementoCaretaker memento) {
		SignalProject project = (SignalProject) memento.getState();
		absolutePathToIndexes = project.absolutePathToIndexes;
		allEpochsList = new ArrayList<Epoch>();
		for (Epoch e : project.allEpochsList) {
			allEpochsList.add(e);
		}
		applyChanges = project.applyChanges.clone();
		artefacts = new ArrayList<Artefact>();
		for (Artefact a : project.artefacts) {
			artefacts.add(a);
		}
		averageType = project.averageType;
		averagedEpochsIndexes = new ArrayList<Integer>();
		for (Integer i : project.averagedEpochsIndexes) {
			averagedEpochsIndexes.add(i);
		}
		averagedSignalsIndexes = new ArrayList<Integer>();
		for (Integer i : project.averagedSignalsIndexes) {
			averagedSignalsIndexes.add(i);
		}
		axisColor = new Color(project.axisColor.getRGB());
		canvasColor = new Color(project.canvasColor.getRGB());
		commentary = project.commentary;
		currentBuffer = project.currentBuffer;
		currentEpochNumber = project.currentEpochNumber;
		dataFile = project.dataFile.getAbsoluteFile();
		epochWalkingStep = project.epochWalkingStep;
		exportViewersHeight = project.exportViewersHeight;
		exportViewersWidth = project.exportViewersWidth;
		functionalValuesColor = new Color(project.functionalValuesColor.getRGB());
		groupEpochWorkMode = project.groupEpochWorkMode;
		header = project.header.clone();
		interpolationColor = new Color(project.interpolationColor.getRGB());
		invertedSignalsView = project.invertedSignalsView;
		lastUsedGroupEpochsType = project.lastUsedGroupEpochsType;
		leftEpochBorderF = project.leftEpochBorderF;
		leftEpochBorderMs = project.leftEpochBorderMs;
		loadedEpochsIndexes = new ArrayList<Integer>();
		for (Integer i : project.loadedEpochsIndexes) {
			loadedEpochsIndexes.add(i);
		}
		modeOfRepresentationInExport = project.modeOfRepresentationInExport;
		name = project.name;
		try {
			projectFile = project.projectFile.getAbsoluteFile();
		} catch (NullPointerException e) {
			projectFile = null;
		}
		rightEpochBorderF = project.rightEpochBorderF;
		rightEpochBorderMs = project.rightEpochBorderMs;
		selectedChannels = new ArrayList<Integer>();
		for (Integer i : project.selectedChannels) {
			selectedChannels.add(i);
		}
		timeSelectionBegin = project.timeSelectionBegin;
		timeSelectionEnd = project.timeSelectionEnd;
		zoomY = project.zoomY;

		setBuffer(project.buffer);
		try {
			project.getBuffer().closeBuffer();
		} catch (IOException e) { /* silent */
		}
	}

	public Buffer getBuffer() {
		if (buffer == null || buffer.isClosed()) {
			return null;
		}

		return buffer;
	}

	public void setBuffer(Buffer buffer) {
		if (this.buffer != buffer) {
			buffer.addParent();
			try {
				this.buffer.closeBuffer();
			} catch (Exception e) { /* silent */
			}
		}
		this.buffer = buffer;
	}

	@Override
	public void closeBuffers() {
		while (!undoStack.empty()) {
			try {
				((SignalProject) undoStack.pop().getState()).getBuffer().closeBuffer();
			} catch (Exception e) { /* silent */
			}
		}
		while (!redoStack.empty()) {
			try {
				((SignalProject) redoStack.pop().getState()).getBuffer().closeBuffer();
			} catch (Exception e) { /* silent */
			}
		}
		try {
			buffer.closeBuffer();
		} catch (Exception e) { /* silent */
		}
	}

	@Override
	public void doCommand() {
		if (cmdLocked) {
			return;
		}
		for (ProjectMementoCaretaker m : redoStack) {
			try {
				((SignalProject) m.getState()).getBuffer().closeBuffer();
			} catch (IOException e) { /* silent */
			}
		}
		super.doCommand();
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;

		this.applyChanges = new boolean[this.header.getNumberOfChannels()];
		Arrays.fill(applyChanges, true);
		channelsToExport = new boolean[header.getChannels().size()];
		Arrays.fill(channelsToExport, true);

	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	public List<Integer> getSelectedChannels() {
		return selectedChannels;
	}

	public void setSelectedChannels(List<Integer> selectedChannels) {
		this.selectedChannels = selectedChannels;
	}

	/**
	 * @return the averagedSignalsIndexes
	 */
	public List<Integer> getAveragedSignalsIndexes() {
		return averagedSignalsIndexes;
	}

	public void setAveragedSignalsIndexes(List<Integer> indexes) {
		averagedSignalsIndexes = indexes;
	}

	public boolean[] getChannelsToExport() {
		return channelsToExport;
	}

	/**
	 * @return the averageType
	 */
	public int getAverageType() {
		return averageType;
	}

	/**
	 * @param averageType
	 *          the averageType to set
	 */
	public void setAverageType(int averageType) {
		this.averageType = averageType;
	}

	/**
	 * @return the currentEpochNumber
	 */
	public int getCurrentEpochNumber() {
		return currentEpochNumber;
	}

	/**
	 * @param currentEpochNumber
	 *          the currentEpochNumber to set
	 */
	public void setCurrentEpochNumber(int currentEpochNumber) {
		this.currentEpochNumber = currentEpochNumber;
	}

	/**
	 * @return the leftEpochBorder
	 */
	public int getLeftEpochBorderMs() {
		return leftEpochBorderMs;
	}

	/**
	 * @param leftEpochBorderMs
	 */
	public void setLeftEpochBorderMs(int leftEpochBorderMs) {
		this.leftEpochBorderMs = leftEpochBorderMs;
	}

	/**
	 * @return the rightEpochBorder
	 */
	public int getRightEpochBorderMs() {
		return rightEpochBorderMs;
	}

	/**
	 * @param rightEpochBorderMs
	 */
	public void setRightEpochBorderMs(int rightEpochBorderMs) {
		this.rightEpochBorderMs = rightEpochBorderMs;
	}

	public File getProjectFile() {
		return projectFile;
	}

	public void setProjectFile(File projectFile) {
		this.projectFile = projectFile;
	}

	public File getDataFile() {
		return dataFile;
	}

	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}

	/**
	 * @return the listAllEpochs
	 */
	public List<Epoch> getAllEpochsList() {
		return allEpochsList;
	}

	/**
	 * @return the indicesEpochsForAveraging
	 */
	public List<Integer> getAveragedEpochsIndexes() {
		return averagedEpochsIndexes;
	}

	/**
	 * @param epochs
	 */
	public void setAllEpochsList(List<Epoch> epochs) {
		this.allEpochsList = epochs;
	}

	/**
	 * @param averagedEpochsIndexes
	 */
	public void setAveragedEpochsIndexes(List<Integer> averagedEpochsIndexes) {
		this.averagedEpochsIndexes = averagedEpochsIndexes;
	}

	/**
	 * @param artefacts
	 *          the artefacts to set
	 */
	public void setArtefactsList(List<Artefact> artefacts) {
		this.artefacts = artefacts;
	}

	/**
	 * @return artefacts
	 */
	public List<Artefact> getArtefactsList() {
		return artefacts;
	}

	/**
	 * @return the epochWalkingStep
	 */
	public int getEpochWalkingStep() {
		return epochWalkingStep;
	}

	/**
	 * @param epochWalkingStep
	 *          the epochWalkingStep to set
	 */
	public void setEpochWalkingStep(int epochWalkingStep) {
		this.epochWalkingStep = epochWalkingStep;
	}

	/**
	 * Vrac� referenci na atribut <i>applyChanges</i>.
	 * 
	 * @return pole booleovsk�ch hodnot, kde hodnota na indexu ud�v�, zda se
	 *         kan�l, jeho� po�ad� ve vstupn�m souboru odpov�d� tomuto indexu,
	 *         ��astn� hromadn�ho za�azov�n�/odeb�r�n� epoch do/z pr�m�ru.
	 */
	public boolean[] getApplyChanges() {
		return applyChanges;
	}

	/**
	 * Nastavuje hodnotu atributu <i>applyChanges</i>.
	 * 
	 * @param applyChanges
	 *          pole booleovsk�ch hodnot, kde hodnota na indexu ud�v�, zda se
	 *          kan�l, jeho� po�ad� ve vstupn�m souboru odpov�d� tomuto indexu,
	 *          ��astn� hromadn�ho za�azov�n�/odeb�r�n� epoch do/z pr�m�ru.
	 */
	public void setApplyChanges(boolean[] applyChanges) {
		this.applyChanges = applyChanges;
	}

	public List<Integer> getLoadedEpochsIndexes() {
		return loadedEpochsIndexes;
	}

	public void setLoadedEpochsIndexes(List<Integer> loadedEpochsIndexes) {
		// for (Integer dummy : loadedEpochsIndexes) {
		// System.out.print(dummy + ", ");
		// }
		this.loadedEpochsIndexes = loadedEpochsIndexes;
	}

	/**
	 * ��k�, jestli jsou zobrazen� sign�ly invertovan� nebo ne.
	 * 
	 * @return <b>true</b> pro invertovan� zobrazen�, <b>false</b> pro norm�ln�
	 *         zobrazen�.
	 */
	public boolean isInvertedSignalsView() {
		return invertedSignalsView;
	}

	/**
	 * Nastavuje, zda maj� b�t sign�ly zobrazeny invertovan�.
	 * 
	 * @param invertedSignalsView
	 *          <b>true</b> pro invertovan� zobrazen�, <b>false</b> pro norm�ln�
	 *          zobrazen�.
	 */
	public void setInvertedSignalsView(boolean invertedSignalsView) {
		this.invertedSignalsView = invertedSignalsView;
	}

	/**
	 * Vrac� posledn� pou�it� typ hromadn�ho za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�r�.
	 * 
	 * @return the lastUsedGroupEpochsType typ hromadn�ho za�azov�n�/odeb�r�n�
	 *         (konstanty t��dy <code>AveragingDataManager</code>.
	 */
	public int getLastUsedGroupEpochsType() {
		return lastUsedGroupEpochsType;
	}

	/**
	 * Nastavuje posledn� pou�it� typ hromadn�ho za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�r�.
	 * 
	 * @param lastUsedGroupEpochsType
	 *          typ hromadn�ho za�azov�n�/odeb�r�n� (konstanty t��dy
	 *          <code>AveragingDataManager</code>.
	 */
	public void setLastUsedGroupEpochsType(int lastUsedGroupEpochsType) {
		this.lastUsedGroupEpochsType = lastUsedGroupEpochsType;
	}

	/**
	 * Vrac� absolutn� cestu k souboru, ze kter�ho byly nahr�ny indexy epoch pro
	 * hromadn� za�azov�n�/odeb�r�n� epoch do/z pr�m�r�.
	 * 
	 * @return absolutn� cestu k souboru s indexy.
	 */
	public String getAbsolutePathToIndexes() {
		return absolutePathToIndexes;
	}

	/**
	 * Nastavuje absolutn� cestu k souboru, ze kter�ho byly nahr�ny indexy epoch
	 * pro hromadn� za�azov�n�/odeb�r�n� epoch do/z pr�m�r�.
	 * 
	 * @param absolutePathToIndexes
	 *          absolutn� cestu k souboru s indexy.
	 */
	public void setAbsolutePathToIndexes(String absolutePathToIndexes) {
		this.absolutePathToIndexes = absolutePathToIndexes;
	}

	/**
	 * Vrac� zp�sob pr�ce se skupinou epoch v pr�m�rovac� komponent�.
	 * 
	 * @return n�kter� z konstant t��dy <code>AveragingDataManager</code>.
	 */
	public int getGroupEpochWorkMode() {
		return groupEpochWorkMode;
	}

	/**
	 * Nastavuje zp�sob pr�ce se skupinou epoch v pr�m�rovac� komponent�.
	 * 
	 * @param groupEpochWorkMode
	 *          n�kter� z konstant t��dy <code>AveragingDataManager</code>.
	 */
	public void setGroupEpochWorkMode(int groupEpochWorkMode) {
		this.groupEpochWorkMode = groupEpochWorkMode;
	}

	/**
	 * Milisekunda definuj�c� po��tek �asov�ho intervalu v n�m� obsa�en� epochy
	 * jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @return milisekunda za��naj�c� �asov� interval
	 */
	public int getTimeSelectionBegin() {
		return timeSelectionBegin;
	}

	/**
	 * Milisekunda definuj�c� po��tek �asov�ho intervalu v n�m� obsa�en� epochy
	 * jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @param timeSelectionBegin
	 *          milisekunda za��naj�c� �asov� interval
	 */
	public void setTimeSelectionBegin(int timeSelectionBegin) {
		this.timeSelectionBegin = timeSelectionBegin;
	}

	/**
	 * Vrac� milisekundu definuj�c� konec �asov�ho intervalu v n�m� obsa�en�
	 * epochy jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @return milisekunda ukon�uj�c� �asov� interval
	 */
	public int getTimeSelectionEnd() {
		return timeSelectionEnd;
	}

	/**
	 * Nastavuje milisekundu definuj�c� konec �asov�ho intervalu v n�m� obsa�en�
	 * epochy jsou ur�eny pro za�azov�n�/odeb�r�n� do/z pr�m�r�.
	 * 
	 * @param timeSelectionEnd
	 *          milisekunda ukon�uj�c� �asov� interval
	 */
	public void setTimeSelectionEnd(int timeSelectionEnd) {
		this.timeSelectionEnd = timeSelectionEnd;
	}

	/**
	 * @return the currentBuffer
	 */
	public int getCurrentBuffer() {
		return currentBuffer;
	}

	/**
	 * @param currentBuffer
	 *          the currentBuffer to set
	 */
	public void setCurrentBuffer(int currentBuffer) {
		this.currentBuffer = currentBuffer;
	}

	/**
	 * Vrac� hodnotu zoomu v ose funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @return zoom v ose funk�n�ch hodnot.
	 */
	public float getZoomY() {
		return zoomY;
	}

	/**
	 * Nastavuje hodnotu zoomu v ose funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @param zoomY
	 *          zoom v ose funk�n�ch hodnot.
	 */
	public void setZoomY(float zoomY) {
		this.zoomY = zoomY;
	}

	/**
	 * Vrac� barvu pozad� pro <b>SignalViewer</b>y v exportn�m okn�.
	 * 
	 * @return barva pozad� <b>SignalViewer</b>u.
	 */
	public Color getCanvasColor() {
		return canvasColor;
	}

	/**
	 * Nastavuje barvu pozad� pro <b>SignalViewer</b>y v exportn�m okn�.
	 * 
	 * @param canvasColor
	 *          barva pozad� <b>SignalViewer</b>u.
	 */
	public void setCanvasColor(Color canvasColor) {
		this.canvasColor = canvasColor;
	}

	/**
	 * Vrac� barvu interpolace funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @return barva interpolace v <b>SignalViewer</b>u.
	 */
	public Color getInterpolationColor() {
		return interpolationColor;
	}

	/**
	 * Nastavuje barvu interpolace funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @param interpolationColor
	 *          barva interpolace v <b>SignalViewer</b>u.
	 */
	public void setInterpolationColor(Color interpolationColor) {
		this.interpolationColor = interpolationColor;
	}

	/**
	 * Vrac� barvu os sou�adn�ho syst�mu pro <b>SignalViewer</b>y v exportn�m
	 * okn�.
	 * 
	 * @return barva os v <b>SignalViewer</b>u.
	 */
	public Color getAxisColor() {
		return axisColor;
	}

	/**
	 * Nastavuje barvu os sou�adn�ho syst�mu pro <b>SignalViewer</b>y v exportn�m
	 * okn�.
	 * 
	 * @param axisColor
	 *          barva os v <b>SignalViewer</b>u.
	 */
	public void setAxisColor(Color axisColor) {
		this.axisColor = axisColor;
	}

	/**
	 * Vrac� barvu zv�razn�n� funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @return barva zv�razn�n� funk�n�ch hodnot v <b>SignalViewer</b>u.
	 */
	public Color getFunctionalValuesColor() {
		return functionalValuesColor;
	}

	/**
	 * Nastavuje barvu zv�razn�n� funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @param functionalValuesColor
	 *          barva zv�razn�n� funk�n�ch hodnot v <b>SignalViewer</b>u.
	 */
	public void setFunctionalValuesColor(Color functionalValuesColor) {
		this.functionalValuesColor = functionalValuesColor;
	}

	/**
	 * Vrac� koment�� exportovan�ch pr�m�r� v exportn�m okn�.
	 * 
	 * @return koment�� exportovan�ch pr�m�r�.
	 */
	public String getCommentary() {
		return commentary;
	}

	/**
	 * Nastavuje koment�� exportovan�ch pr�m�r� v exportn�m okn�.
	 * 
	 * @param commentary
	 *          koment�� exportovan�ch pr�m�r�.
	 */
	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	/**
	 * 
	 * Vrac� zp�sob zobrazen� sign�l� v <b>SignalViewer</b>ech v exportn�m okn�.
	 * 
	 * @return zp�sob zobrazen� sign�l�.
	 */
	public int getModeOfRepresentationInExport() {
		return modeOfRepresentationInExport;
	}

	/**
	 * Vrac� zp�sob zobrazen� sign�l� v <b>SignalViewer</b>ech v exportn�m okn�.
	 * 
	 * @param modeOfRepresentationInExport
	 *          zp�sob zobrazen� sign�l�. N�kter� z konstant t��dy
	 *          <i>SignalViewer</i>, kter� ur�uje zp�sob zobrazen� sign�lu.
	 */
	public void setModeOfRepresentationInExport(int modeOfRepresentationInExport) {
		this.modeOfRepresentationInExport = modeOfRepresentationInExport;
	}

	/**
	 * Vrac� ���ku zobrazen� exportovan�ch pr�m�r�.
	 * 
	 * @return ���ka zobrazen� exportovan�ch pr�m�r�.
	 */
	public int getExportViewersWidth() {
		return exportViewersWidth;
	}

	/**
	 * Nastavuje ���ku zobrazen� exportovan�ch pr�m�r�.
	 * 
	 * @param exportViewersWidth
	 *          ���ka zobrazen� exportovan�ch pr�m�r�.
	 */
	public void setExportViewersWidth(int exportViewersWidth) {
		this.exportViewersWidth = exportViewersWidth;
	}

	/**
	 * Vrac� v�ku zobrazen� exportovan�ch pr�m�r�.
	 * 
	 * @return v�ka zobrazen� exportovan�ch pr�m�r�.
	 */
	public int getExportViewersHeight() {
		return exportViewersHeight;
	}

	/**
	 * Nastavuje v�ku zobrazen� exportovan�ch pr�m�r�.
	 * 
	 * @param exportViewersHeight
	 *          v�ka zobrazen� exportovan�ch pr�m�r�.
	 */
	public void setExportViewersHeight(int exportViewersHeight) {
		this.exportViewersHeight = exportViewersHeight;
	}

	public int getLeftEpochBorderF() {
		return leftEpochBorderF;
	}

	public void setLeftEpochBorderF(int leftEpochBorderF) {
		this.leftEpochBorderF = leftEpochBorderF;
	}

	public int getRightEpochBorderF() {
		return rightEpochBorderF;
	}

	public void setRightEpochBorderF(int rightEpochBorderF) {
		this.rightEpochBorderF = rightEpochBorderF;
	}

}
