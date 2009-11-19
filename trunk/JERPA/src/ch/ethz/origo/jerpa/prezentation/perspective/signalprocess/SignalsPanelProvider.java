package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import noname.JERPAUtils;
import ch.ethz.origo.jerpa.application.Const;
import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.BaselineCorrection;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalsSegmentation;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.AveragingDataManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SingnalPerspectiveObservable;
import ch.ethz.origo.jerpa.data.Artefact;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtilities;

/**
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @see IObserver
 * 
 */
public class SignalsPanelProvider implements IObserver {

	private SignalSessionManager session;
	private SingnalPerspectiveObservable spObservable;
	private DrawingComponent drawingComponent;
	private SignalsSegmentation signalsSegmentation;
	private BaselineCorrection baselineCorrection;
	private SignalsPanel signalsPanel;
	private Icon playIcon;
	private Icon pauseIcon;
	private Icon stopIcon;
	private Icon selectionEpochIcon;
	private Icon unselectionEpochIcon;
	private Icon selectionArtefactIcon;
	private Icon unselectionArtefactIcon;
	private Icon playbackIcon;
	private Icon baselineCorrectionIcon;
	private int selectedFunction;
	private int artefactBegining;
	private int artefactEnd;
	private int baselineCorrectionBegining;
	private int baselineCorrectionEnd;
	private int selectionStart; // bude tato promenna (+ set/gettery) jeste
	// potreba?
	private int selectionEnd; // bude tato promenna (+ set/gettery) jeste potreba?
	private boolean areaSelection = false;
	private boolean changeEpochInterval = false;
	private boolean showPopupMenu = false;
	private Color colorSelection;
	// private Header header;
	// private Buffer buffer;
	private int numberOfDrawableChannels; // Po�et vybran�ch sign�l� (tedy t�ch,
	// kter� mohou, ale nemus� b�t
	// zobrazeny)
	private int numberOfVisibleChannels; // Po�et zobrazen�ch sign�l�
	private int firstVisibleChannel; // Index prvn�ho vykreslovan�ho sign�lu
	// private List<Integer> visibleSignalsIndexes;
	private float paintVolume;

	public SignalsPanelProvider(SignalSessionManager session) {
		this.session = session;
		drawingComponent = new DrawingComponent(this);
		signalsSegmentation = this.session.getSignalsSegmentation();
		baselineCorrection = this.session.getBaselineCorrection();
		signalsPanel = new SignalsPanel(this);
		setFirstVisibleChannel(0);
		spObservable = SingnalPerspectiveObservable.getInstance();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Object state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Object object, int state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Observable o, Object arg) {
		int msg;

		if (arg instanceof java.lang.Integer) {
			msg = ((Integer) arg).intValue();
		} else {
			return;
		}

		switch (msg) {
		case SingnalPerspectiveObservable.MSG_PROJECT_CLOSED:
			setDrawingComponent();
			signalsPanel.verticalScrollBar.setEnabled(false);
			signalsPanel.horizontalScrollBar.setEnabled(false);
			signalsPanel.drawableSignalsCheckBoxes = null;
			signalsPanel.checkBoxesPanel.removeAll();
			setAllWindowControlsEnabled(false);
			showPopupMenu = false;
			signalsPanel.increaseNumberOfChannelsButton.setEnabled(false);
			signalsPanel.decreaseNumberOfChannelsButton.setEnabled(false);
			signalsPanel.playBT.setEnabled(false);
			signalsPanel.stopBT.setEnabled(false);
			break;
		case SingnalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
			setDrawingComponent();
			setNumberOfVisibleChannels(session.getCurrentProject()
					.getSelectedChannels().size());
			setSignalSegmentation();
			setAllWindowControlsEnabled(true);
			setFirstVisibleChannel(0);
			showPopupMenu = true;
			recountChannels();
			averageSelectedEpochs();
			// signalsPanel.invertedSignalsButton.setSelected(appCore.getCurrentProject().isInvertedSignalsView());
			drawingComponent.setInvertedView(session.getCurrentProject()
					.isInvertedSignalsView());
			break;

		case SingnalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_START:
			setAllWindowControlsEnabled(false);
			showPopupMenu = false;
			signalsPanel.setPlayButtonIcon(getPauseIcon());
			selectedFunction = Const.SELECT_NOTHING;
			getDrawingComponent().startDrawing();
			break;

		case SingnalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_RESUME:
			signalsPanel.setPlayButtonIcon(getPauseIcon());
			showPopupMenu = false;
			setAllWindowControlsEnabled(false);
			selectedFunction = Const.SELECT_NOTHING;
			getDrawingComponent().togglePause();
			break;

		case SingnalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_PAUSE:
			signalsPanel.setPlayButtonIcon(getPlayIcon());
			setAllWindowControlsEnabled(true);
			showPopupMenu = true;
			selectedFunction = signalsPanel.getSelectedFunctionIndex();
			getDrawingComponent().togglePause();
			break;

		case SingnalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_STOP:
			getDrawingComponent().stopDrawing();
			showPopupMenu = true;
			signalsPanel.setPlayButtonIcon(getPlayIcon());
			signalsPanel.setHorizontalScrollbarValue(0);
			setAllWindowControlsEnabled(true);
			selectedFunction = signalsPanel.getSelectedFunctionIndex();
			break;

		case SingnalPerspectiveObservable.MSG_CHANNEL_SELECTED:
			recountChannels();
			break;

		case SingnalPerspectiveObservable.MSG_AUTOMATIC_ARTEFACT_SELECTION:
			signalsSegmentation.addArtefacts(session.getAutoSelectionArtefact()
					.getArtefacts());
			drawingComponent.setDrawedArtefacts(signalsSegmentation
					.getArtefactsDraw());
			session.getAutoSelectionArtefact().clearArraysArtefacts();
			session.getCurrentProject().setArtefactsList(
					signalsSegmentation.getArtefacts());
			averageSelectedEpochs();
			break;

		case SingnalPerspectiveObservable.MSG_NEW_BUFFER:
			drawingComponent.setDataSource(((SignalProject) session
					.getCurrentProject()).getBuffer(), ((SignalProject) session
					.getCurrentProject()).getHeader());
			drawingComponent.setDrawedArtefacts(signalsSegmentation
					.getArtefactsDraw());
			drawingComponent.setDrawedEpochs(signalsSegmentation.getEpochsDraw());
			break;

		case SingnalPerspectiveObservable.MSG_INVERTED_SIGNALS_VIEW_CHANGED:
			drawingComponent.setInvertedView(((SignalProject) session
					.getCurrentProject()).isInvertedSignalsView());

		}

	}

	protected void calculatingAveragesTest() {
		List<float[]> framesValues = new ArrayList<float[]>();
		Epoch epoch = new Epoch(session.getCurrentProject().getHeader()
				.getNumberOfChannels());
		try {
			AveragingDataManager.calculateAverages(session.getCurrentProject(),
					framesValues, epoch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Nastavuje jednotliv� p�edn� a zadn� hodnotu intervalu oblasti epoch.
	 */
	void saveEpochInterval() {
		SignalProject project = session.getCurrentProject();

		if (project == null) {
			return;
		}
		if (drawingComponent != null) {
			int startValue = ((Integer) signalsPanel.startEpoch.getValue())
					.intValue();
			int endValue = ((Integer) signalsPanel.endEpoch.getValue()).intValue();

			if (!setLeftEpochBorder(startValue)) {
				signalsPanel.startEpoch.setValue(new Integer(project
						.getLeftEpochBorderMs()));
				changeEpochInterval = true;
			} else {
				project.setLeftEpochBorderMs(startValue);
				project.setLeftEpochBorderF((int) drawingComponent
						.timeToAbsoluteFrame(startValue));
			}

			if (!setRightEpochBorder(endValue)) {
				signalsPanel.endEpoch.setValue(new Integer(project
						.getRightEpochBorderMs()));
				changeEpochInterval = true;
			} else {
				project.setRightEpochBorderMs(endValue);
				project.setRightEpochBorderF((int) drawingComponent
						.timeToAbsoluteFrame(endValue));
			}

			if (changeEpochInterval) {
				spObservable
						.setState(SingnalPerspectiveObservable.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
				changeEpochInterval = false;
			}

		}
	}

	/**
	 * Zakazuje/povoluje tla��tka v okn� t��dy SignalsWindow.
	 * 
	 * @param enabled
	 *          - hodnota zak�z�n�/povolen�.
	 */
	private void setAllWindowControlsEnabled(boolean enabled) {
		signalsPanel.saveEpochIntervalButton.setEnabled(enabled);
		signalsPanel.selectEpochTB.setEnabled(enabled);
		signalsPanel.unselectEpochTB.setEnabled(enabled);
		signalsPanel.selectArtefactTB.setEnabled(enabled);
		signalsPanel.unselectArtefactTB.setEnabled(enabled);
		signalsPanel.selectPlaybackTB.setEnabled(enabled);
		signalsPanel.baselineCorrectionTB.setEnabled(enabled);
		// signalsPanel.invertedSignalsButton.setEnabled(enabled);
		signalsPanel.decreaseVerticalZoomButton.setEnabled(enabled);
		signalsPanel.increaseVerticalZoomButton.setEnabled(enabled);
		signalsPanel.decreaseHorizontalZoomButton.setEnabled(enabled);
		signalsPanel.increaseHorizontalZoomButton.setEnabled(enabled);
	}

	/**
	 * Zv�t�� po�et zobrazen�ch sign�l� o jedna.<br/>
	 * Hodnota mimo povolen� interval bude automaticky opravena na maximum nebo
	 * minimum intervalu.
	 */
	protected synchronized void increaseNumberOfVisibleChannels() {
		setNumberOfVisibleChannels(numberOfVisibleChannels + 1);
	}

	/**
	 * Zmen�� po�et zobrazen�ch sign�l� o jedna. Hodnota mimo povolen� interval
	 * bude automaticky opravena na maximum nebo minimum intervalu.
	 */
	protected synchronized void decreaseNumberOfVisibleChannels() {
		setNumberOfVisibleChannels(numberOfVisibleChannels - 1);
	}

	protected void decreaseVerticalZoom() {
		float zoom = getDrawingComponent().getVerticalZoom();
		float step = zoom / 5f;

		zoom += step;
		getDrawingComponent().setVerticalZoom(zoom);
	}

	protected void increaseVerticalZoom() {
		float zoom = getDrawingComponent().getVerticalZoom();
		float step = zoom / 5f;
		zoom -= step;
		if (zoom <= 1) {
			return;
		}
		getDrawingComponent().setVerticalZoom(zoom);
	}

	protected void increaseHorizontalZoom() {
		long time = getDrawingComponent().frameToTime(
				getDrawingComponent().getDrawedFrames());

		if (time >= 2000) {
			time -= 1000;
		}
		// time = time - (time % 1000);

		getDrawingComponent().setHorizontalZoom(
				(int) getDrawingComponent().timeToAbsoluteFrame(time));
	}

	protected void decreaseHorizontalZoom() {
		long time = getDrawingComponent().frameToTime(
				getDrawingComponent().getDrawedFrames());
		time += 1000;
		getDrawingComponent().setHorizontalZoom(
				(int) getDrawingComponent().timeToAbsoluteFrame(time));
	}

	/**
	 * Nastavuje parametry obejtku t��dy SignalSegmentation podle parametr�
	 * aktu�ln�ho projektu.
	 */
	private synchronized void setSignalSegmentation() {
		SignalProject project = (SignalProject) session.getCurrentProject();
		signalsSegmentation.setSegmentArrays();

		Integer startValue = (Integer) signalsPanel.startEpoch.getValue();
		Integer endValue = (Integer) signalsPanel.endEpoch.getValue();
		// inicializace dat p�i na��t�n� projektu
		signalsSegmentation
				.setEpochs((ArrayList<Epoch>) project.getAllEpochsList());
		signalsSegmentation.setArtefacts((ArrayList<Artefact>) project
				.getArtefactsList());

		if (project.getLeftEpochBorderMs() == Integer.MIN_VALUE) {
			if (!setLeftEpochBorder(startValue.intValue())) {
				signalsPanel.startEpoch.setValue(new Integer(0));
				setLeftEpochBorder(0);
				project.setLeftEpochBorderMs(0);
				project.setLeftEpochBorderF((int) drawingComponent
						.timeToAbsoluteFrame(0));
			} else {
				project.setLeftEpochBorderMs(startValue.intValue());
				project.setLeftEpochBorderF((int) drawingComponent
						.timeToAbsoluteFrame(startValue.intValue()));
			}
		} else {
			setLeftEpochBorder(project.getLeftEpochBorderMs());
			signalsPanel.startEpoch.setValue(project.getLeftEpochBorderMs());
		}

		if (project.getRightEpochBorderMs() == Integer.MIN_VALUE) {
			if (!setRightEpochBorder(endValue.intValue())) {
				signalsPanel.endEpoch.setValue(new Integer(0));
				setRightEpochBorder(0);
				project.setRightEpochBorderMs(0);
				project.setRightEpochBorderF((int) drawingComponent
						.timeToAbsoluteFrame(0));
			} else {
				project.setRightEpochBorderMs(endValue.intValue());
				project.setRightEpochBorderF((int) drawingComponent
						.timeToAbsoluteFrame(endValue.intValue()));
			}
		} else {
			setRightEpochBorder(project.getRightEpochBorderMs());
			signalsPanel.endEpoch.setValue(project.getRightEpochBorderMs());
		}

		getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
		getDrawingComponent().setDrawedArtefacts(
				signalsSegmentation.getArtefactsDraw());

	}

	/**
	 * Nastavuje vykreslovac� komponentu, na kterou se budou vykreslovat vybran�
	 * sign�ly. TODO - tuhle prasarnu predelat - predelano, ale mozna by se dalo
	 * jeste trochu
	 */
	protected synchronized void setDrawingComponent() {
		SignalProject project = session.getCurrentProject();
		if (project == null) {
			drawingComponent.setDataSource(null, null);
			signalsPanel.horizontalScrollBar.setEnabled(false);
		} else {
			drawingComponent.setDataSource(project.getBuffer(), project.getHeader());
			drawingComponent.setDrawableChannels(project.getSelectedChannels());
			resetHorizontalScrollbarMaximum();
			signalsPanel.horizontalScrollBar.setMinimum(0);
			signalsPanel.horizontalScrollBar.setEnabled(true);
			signalsPanel.verticalScrollBar.setEnabled(true);

			signalsPanel.selectEpochTB.setSelected(true);
			setSelectedFunction(signalsPanel.getSelectedFunctionIndex());
			signalsPanel.playBT.setEnabled(true);
			signalsPanel.stopBT.setEnabled(true);
		}

	}

	protected void togglePause() {
		if (getDrawingComponent().isPaused()) {
			if (getDrawingComponent().isRunning()) {
				spObservable
						.setState(SingnalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_RESUME);
			} else {
				spObservable
						.setState(SingnalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_START);
			}
		} else {
			spObservable
					.setState(SingnalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_PAUSE);
		}
	}

	protected void stopPlayback() {
		spObservable
				.setState(SingnalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_STOP);
	}

	public JPanel getPanel() {
		return signalsPanel;
	}

	/**
	 * @return ikonu pro p�ehr�v�n�.
	 */
	protected Icon getPlayIcon() {
		if (playIcon == null) {
			playIcon = JUIGLEGraphicsUtilities.createImageIcon(JERPAUtils.IMAGE_PATH
					+ "play24.gif");
		}
		return playIcon;
	}

	/**
	 * @return ikonu pro pauzu.
	 */

	protected Icon getPauseIcon() {
		if (pauseIcon == null) {
			pauseIcon = JUIGLEGraphicsUtilities.createImageIcon(JERPAUtils.IMAGE_PATH
					+ "pause24.gif");
		}
		return pauseIcon;
	}

	/**
	 * @return ikonu pro zastaven� p�ehr�v�n�.
	 */
	protected Icon getStopIcon() {
		if (stopIcon == null) {
			stopIcon = JUIGLEGraphicsUtilities.createImageIcon(JERPAUtils.IMAGE_PATH
					+ "stop24.gif");
		}
		return stopIcon;
	}

	/**
	 * @return ikonu ozna�ov�n� epoch.
	 */
	protected Icon getSelectionEpochIcon() {
		if (selectionEpochIcon == null) {
			selectionEpochIcon = JUIGLEGraphicsUtilities
					.createImageIcon(JERPAUtils.IMAGE_PATH + "selEpochIcon.gif");
		}
		return selectionEpochIcon;
	}

	/**
	 * @return ikonu odzna�ov�n� epoch.
	 */
	protected Icon getUnselectionEpochIcon() {

		if (unselectionEpochIcon == null) {
			unselectionEpochIcon = JUIGLEGraphicsUtilities
					.createImageIcon(JERPAUtils.IMAGE_PATH + "unselEpochIcon.gif");
		}
		return unselectionEpochIcon;
	}

	/**
	 * @return ikonu ozna�ov�n� artefakt�.
	 */
	protected Icon getSelectionArtefactIcon() {
		if (selectionArtefactIcon == null) {
			selectionArtefactIcon = JUIGLEGraphicsUtilities
					.createImageIcon(JERPAUtils.IMAGE_PATH + "selArtefactIcon.gif");
		}
		return selectionArtefactIcon;
	}

	/**
	 * @return ikonu odzna�ov�n� artefakt�.
	 */
	protected Icon getUnselectionArtefactIcon() {
		if (unselectionArtefactIcon == null) {
			unselectionArtefactIcon = JUIGLEGraphicsUtilities
					.createImageIcon(JERPAUtils.IMAGE_PATH + "unselArtefactIcon.gif");
		}
		return unselectionArtefactIcon;
	}

	/**
	 * @return ikonu nastaven� ukazatele p�ehr�v�n�.
	 */
	protected Icon getPlaybackIcon() {
		if (playbackIcon == null) {
			playbackIcon = JUIGLEGraphicsUtilities
					.createImageIcon(JERPAUtils.IMAGE_PATH + "playbackicon.png");
		}
		return playbackIcon;
	}

	/**
	 * @return ikonu opravy baseline.
	 */
	protected Icon getBaselineCorrectionIcon() {
		if (baselineCorrectionIcon == null) {
			baselineCorrectionIcon = JUIGLEGraphicsUtilities
					.createImageIcon(JERPAUtils.IMAGE_PATH + "baselineCorrectionIcon.gif");
		}
		return baselineCorrectionIcon;
	}

	protected void setVerticalZoom(int vZoom) {
		if (getDrawingComponent() != null) {
			// System.out.println("vzoom: " + vZoom);
			getDrawingComponent().setVerticalZoom(vZoom);
		}
	}

	protected void setHorizontalZoom(int hZoom) {
		if (getDrawingComponent() != null) {
			getDrawingComponent().setHorizontalZoom(hZoom);
			resetHorizontalScrollbarMaximum();
			// drawingComponent.refresh();
		}
	}

	protected void resetHorizontalScrollbarMaximum() {
		signalsPanel.horizontalScrollBar.setMaximum((int) session
				.getCurrentHeader().getNumberOfSamples()
				- drawingComponent.getDrawedFrames() + 20);
	}

	protected void setSelectedFunction(int value) {
		this.selectedFunction = value;
	}

	/**
	 * Nastavuje parametry popup-menu a jeho zobrazen�.
	 * 
	 * @param visualComponent
	 *          - komponenta, ke kter� se menu v�e.
	 * @param xAxis
	 *          - x-ov� sou�adnice zobrazen� menu.
	 * @param yAxis
	 *          - y-ov� sou�adnice zobrazen� menu.
	 * @param frame
	 *          - m�sto v souboru, p�epo��tan� ze sou�adnic kliku.
	 */
	protected void setPopupmenu(JComponent visualComponent, int xAxis, int yAxis,
			long frame) {
		if (showPopupMenu) {
			boolean enabledSelEpoch = signalsSegmentation.getEnabledSelEpoch(frame);
			boolean enabledUnselEpoch = signalsSegmentation
					.getEnabledUnselEpoch(frame);
			boolean enabledUnselArtefact = signalsSegmentation
					.getEnabledUnselArtefact(frame);
			boolean enabledUnselAllEpochs = signalsSegmentation
					.getEnabledUnselAllEpochs();
			boolean enabledUnselAllArtefacts = signalsSegmentation
					.getEnabledUnselAllArtefacts();
			boolean enabledUnselAll;

			if (enabledUnselAllEpochs || enabledUnselAllArtefacts) {
				enabledUnselAll = true;
			} else {
				enabledUnselAll = false;
			}

			signalsPanel.optionMenu.setEnabledItems(enabledSelEpoch,
					enabledUnselEpoch, enabledUnselArtefact, enabledUnselAllEpochs,
					enabledUnselAllArtefacts, enabledUnselAll);

			signalsPanel.optionMenu.setVisibleMenu(visualComponent, xAxis, yAxis,
					frame);
		}
	}

	/**
	 * Pos�l� informace pro ozna�en� epochy objektu t��dy SignalSegmentation.
	 * 
	 * @param frame
	 *          - m�sto ozna�en� epochy.
	 */
	protected void selectEpoch(long frame) {

		signalsSegmentation.selectEpoch((int) frame);
		getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
		averageSelectedEpochs();
	}

	/**
	 * Pos�l� informace pro odzna�en� epochy objektu t��dy SignalSegmentation.
	 * 
	 * @param frame
	 *          - m�sto odzna�en� epochy.
	 */
	protected void unselectEpoch(long frame)

	{

		signalsSegmentation.unselectEpoch((int) frame);
		getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
		averageSelectedEpochs();
	}

	/**
	 * Pos�l� informace pro odzna�en� artefaktu objektu t��dy SignalSegmentation.
	 * 
	 * @param frame
	 *          - m�sto odzna�en� artefaktu.
	 */
	protected void unselectConcreteArtefact(long frame) {

		signalsSegmentation.unselectionConcreteArtefact(frame);
		getDrawingComponent().setDrawedArtefacts(
				signalsSegmentation.getArtefactsDraw());
		session.getCurrentProject().setArtefactsList(
				signalsSegmentation.getArtefacts());
		averageSelectedEpochs();
	}

	/**
	 * Pos�l� informace pro odzna�en� v�ech artefakt� objektu t��dy
	 * SignalSegmentation.
	 */
	protected void unselectAllArtefacts() {

		signalsSegmentation.unselectionAllArtefacts();
		getDrawingComponent().setDrawedArtefacts(
				signalsSegmentation.getArtefactsDraw());
		session.getCurrentProject().setArtefactsList(
				signalsSegmentation.getArtefacts());
		averageSelectedEpochs();
	}

	/**
	 * Pos�l� informace pro odzna�en� v�ech epoch objektu t��dy
	 * SignalSegmentation.
	 */
	protected void unselectAllEpochs() {

		signalsSegmentation.unselectionAllEpochs();
		getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
		session.getCurrentProject().setAllEpochsList(
				signalsSegmentation.getEpochs());
		averageSelectedEpochs();
	}

	/**
	 * Pos�l� informace pro odzna�en� v�eho objektu t��dy SignalSegmentation.
	 */
	protected void unselectAllEpochsAndArtefacts() {
		signalsSegmentation.unselectionAllArtefacts();
		signalsSegmentation.unselectionAllEpochs();
		getDrawingComponent().setDrawedArtefacts(
				signalsSegmentation.getArtefactsDraw());
		session.getCurrentProject().setArtefactsList(
				signalsSegmentation.getArtefacts());
		getDrawingComponent().setDrawedEpochs(signalsSegmentation.getEpochsDraw());
		session.getCurrentProject().setAllEpochsList(
				signalsSegmentation.getEpochs());
		averageSelectedEpochs();
	}

	/**
	 * Provede operaci podle aktu�ln� vybran� funkce p�i stisku tla��tka my�i.
	 * 
	 * @param position
	 *          - pozice kursoru p�i stisku tla��tka my�i.
	 */
	protected void setPressedPosition(long position) {
		int xAxis = (int) position;

		switch (selectedFunction) {
		case Const.SELECT_PLAYBACK:
			drawingComponent.setPlaybackIndicatorPosition(position);
			break;

		case Const.SELECT_EPOCH:// TODO - pridat znacku

			if (signalsSegmentation.selectEpoch(xAxis)) {
				getDrawingComponent().setDrawedEpochs(
						signalsSegmentation.getEpochsDraw());
				averageSelectedEpochs();
			}
			break;

		case Const.UNSELECT_EPOCH:
			if (signalsSegmentation.unselectEpoch(xAxis)) {
				getDrawingComponent().setDrawedEpochs(
						signalsSegmentation.getEpochsDraw());
				averageSelectedEpochs();
			}
			break;

		case Const.SELECT_ARTEFACT:
			areaSelection = true;
			artefactBegining = xAxis;
			colorSelection = Color.RED;
			break;

		case Const.UNSELECT_ARTEFACT:
			areaSelection = true;
			artefactBegining = xAxis;
			colorSelection = Color.WHITE;
			break;

		case Const.BASELINE_CORRECTION:
			areaSelection = true;
			baselineCorrectionBegining = xAxis;
			colorSelection = Color.ORANGE;
			break;
		}
	}

	/**
	 * Provede operaci podle aktu�ln� vybran� funkce p�i uvoln�n� tla��tka my�i.
	 * 
	 * @param position
	 *          - pozice kursoru p�i uvoln�n� tla��tka my�i.
	 */
	protected void setReleasedPosition(long position) {
		int xAxis = (int) position;

		switch (selectedFunction) {
		case Const.SELECT_ARTEFACT:
			if (artefactBegining > xAxis) {
				artefactEnd = artefactBegining;
				artefactBegining = xAxis;
			} else {
				artefactEnd = xAxis;
			}
			signalsSegmentation.selectArtefact(artefactBegining, artefactEnd);
			getDrawingComponent().setDrawedArtefacts(
					signalsSegmentation.getArtefactsDraw());
			session.getCurrentProject().setArtefactsList(
					signalsSegmentation.getArtefacts());
			averageSelectedEpochs();
			areaSelection = false;
			break;

		case Const.UNSELECT_ARTEFACT:
			if (artefactBegining > xAxis) {
				artefactEnd = artefactBegining;
				artefactBegining = xAxis;
			} else {
				artefactEnd = xAxis;
			}
			signalsSegmentation.unselectArtefact(artefactBegining, artefactEnd);
			getDrawingComponent().setDrawedArtefacts(
					signalsSegmentation.getArtefactsDraw());
			session.getCurrentProject().setArtefactsList(
					signalsSegmentation.getArtefacts());
			averageSelectedEpochs();
			areaSelection = false;
			break;

		case Const.BASELINE_CORRECTION:
			if (baselineCorrectionBegining > xAxis) {
				baselineCorrectionEnd = baselineCorrectionBegining;
				baselineCorrectionBegining = xAxis;
			} else {
				baselineCorrectionEnd = xAxis;
			}

			if (baselineCorrectionBegining < 0) {
				baselineCorrectionBegining = 0;
			}

			if (baselineCorrectionEnd >= session.getCurrentHeader()
					.getNumberOfSamples()) {
				baselineCorrectionEnd = (int) session.getCurrentHeader()
						.getNumberOfSamples() - 1;
			}

			baselineCorrection.setTimeStampsForCorrection(drawingComponent
					.frameToTime(baselineCorrectionBegining), drawingComponent
					.frameToTime(baselineCorrectionEnd));
			areaSelection = false;
			spObservable
					.setState(SingnalPerspectiveObservable.MSG_BASELINE_CORRECTION_INTERVAL_SELECTED);

			break;
		}

		selectionEnd = 0;
	}

	/**
	 * Nastavuje levou oblast epochy.
	 * 
	 * @param start
	 *          - po��te�n� hodnotu intervalu epochy.
	 * @return true - pokud lze tuto hodnotu ulo�it.<br>
	 *         false - pokud nelze tuto hodnotu ulo�it.
	 */
	protected boolean setLeftEpochBorder(int start) {
		int leftBorder = (int) drawingComponent.timeToAbsoluteFrame(start);

		if (signalsSegmentation.setLeftEpochBorder(leftBorder)) {
			getDrawingComponent()
					.setDrawedEpochs(signalsSegmentation.getEpochsDraw());
			spObservable
					.setState(SingnalPerspectiveObservable.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Nastavuje pravou oblast epochy.
	 * 
	 * @param end
	 *          - kone�nou hodnotu intervalu epochy.
	 * @return true - pokud lze tuto hodnotu ulo�it.<br>
	 *         false - pokud nelze tuto hodnotu ulo�it.
	 */
	protected boolean setRightEpochBorder(int end) {
		int rightBorder = (int) drawingComponent.timeToAbsoluteFrame(end);

		if (signalsSegmentation.setRightEpochBorder(rightBorder)) {
			getDrawingComponent()
					.setDrawedEpochs(signalsSegmentation.getEpochsDraw());
			spObservable
					.setState(SingnalPerspectiveObservable.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Nastavuje funk�n� hodnoty sign�l� do okna signalWindow k zobrazen�.
	 * 
	 * @param frame
	 *          - m�sto v souboru.
	 */
	protected void setFunctionalValues(long frame) {
		SignalProject project = session.getCurrentProject();
		if (project == null) {
			return;
		}

		if (showPopupMenu) {
			Buffer buffer = session.getCurrentProject().getBuffer();
			// signalsPanel.setFunctionalValues(frame);
			try {
				for (int i = 0; i < project.getSelectedChannels().size(); i++) {
					signalsPanel.drawableSignalsValueLabels[i].setText(""
							+ buffer.getFrame(frame)[i]);
				}
			} catch (InvalidFrameIndexException e) {
				// netreba nic delat - ponechat puvodni hodnotu
			}
		}

	}

	/**
	 * Vymaz�n� funk�n�ch hodnot v okn� signalWindow.
	 */
	protected void clearFunctionalValues() {
		SignalProject project = session.getCurrentProject();
		if (project == null) {
			return;
		}
		if (project.getSelectedChannels() != null) {
			for (int i = 0; i < project.getSelectedChannels().size(); i++) {
				signalsPanel.drawableSignalsValueLabels[i].setText("");
			}
		}
	}

	/**
	 * @return ArrayList index� epoch k pr�m�rov�n�
	 */
	protected ArrayList<Integer> getIndicesEpochsForAveraging() {

		return signalsSegmentation.getIndicesEpochsForAveraging();
	}

	/**
	 * @return List index� vykreslovan�ch kan�l�.
	 */
	protected List<Integer> getSelectedChannels() {
		return session.getCurrentProject().getSelectedChannels();
	}

	/**
	 * @return (int) hodnotu aktu�ln� funkce.
	 */
	protected int getSelectedFunction() {
		return selectedFunction;
	}

	/**
	 * @return (boolean) hodnotu zda se ozna�uje souvisl� oblast. Nap�. p�i
	 *         ozna�ov�n� artefakt�.
	 */
	protected boolean isAreaSelection() {
		return areaSelection;
	}

	/**
	 * Nastavuje po��te�n� hodnotu vykreslovan� oblasti.
	 * 
	 * @param xAxis
	 */
	protected void setStartSelection(int xAxis) {
		selectionStart = xAxis;
	}

	/**
	 * @return po��te�n� hodnotu vykreslovan� oblasti.
	 */
	protected int getStartSelection() {
		return selectionStart;
	}

	/**
	 * Nastavuje kone�nou hodnotu vykreslovan� oblasti.
	 * 
	 * @param xAxis
	 */
	protected void setEndSelection(int xAxis) {
		selectionEnd = xAxis - selectionStart;
	}

	/**
	 * @return kone�nou hodnotu vykreslovan� oblasti.
	 */
	protected int getEndSelection() {
		return selectionEnd;
	}

	/**
	 * @return nastavenou barvu vykreslovan� oblasti.
	 */
	protected Color getColorSelection() {
		return colorSelection;
	}

	/**
	 * P�epo�te hodnoty vykresliteln�ch a zobrazen�ch sign�l� a nastav�
	 * souvisej�c� parametry GUI.
	 */
	private synchronized void recountChannels() {
		try {
			numberOfDrawableChannels = session.getCurrentProject()
					.getSelectedChannels().size();
		} catch (NullPointerException e) {
			return;
		}

		if (numberOfVisibleChannels > numberOfDrawableChannels) {
			numberOfVisibleChannels = numberOfDrawableChannels;
		} else if (numberOfVisibleChannels < 1) {
			numberOfVisibleChannels = 1;
		}

		int maximalFirstVisibleChannel = numberOfDrawableChannels
				- numberOfVisibleChannels;

		if (firstVisibleChannel + numberOfVisibleChannels > numberOfDrawableChannels) {
			firstVisibleChannel = maximalFirstVisibleChannel;
		} else if (firstVisibleChannel < 0) {
			firstVisibleChannel = 0;
		}

		drawingComponent.setDrawableChannels(((SignalProject) session
				.getCurrentProject()).getSelectedChannels());
		drawingComponent.loadNumbersOfSignals();
		setVisibleSignals();
		signalsPanel.verticalScrollBar
				.setEnabled(numberOfVisibleChannels < numberOfDrawableChannels);
		signalsPanel.verticalScrollBar.setMaximum(maximalFirstVisibleChannel);
		signalsPanel.verticalScrollBar.setValue(firstVisibleChannel);

		signalsPanel.setNumberOfSelectedSignalsButtonsEnabled(
				numberOfVisibleChannels > 1,
				numberOfVisibleChannels < numberOfDrawableChannels);
		// System.out.println("---");
	}

	/**
	 * Nastavuje indexy epoch k pr�m�rov�n� a pos�l� o tom zpr�vu.
	 */
	protected void averageSelectedEpochs() {
		session.getCurrentProject().setAveragedEpochsIndexes(
				getIndicesEpochsForAveraging());
		spObservable
				.setState(SingnalPerspectiveObservable.MSG_NEW_INDEXES_FOR_AVERAGING_AVAILABLE);
	}

	/**
	 * Nastavuje vykreslovan� sign�ly.
	 */
	protected void setVisibleSignals() {
		SignalProject project = (SignalProject) session.getCurrentProject();
		Header header = project.getHeader();
		ChannelCheckBoxListener channelCheckBoxListener = new ChannelCheckBoxListener();
		List<Integer> selectedChannels = project.getSelectedChannels();
		signalsPanel.drawableSignalsCheckBoxes = new JCheckBox[selectedChannels
				.size()];
		signalsPanel.drawableSignalsValueLabels = new JLabel[selectedChannels
				.size()];

		for (int i = 0; i < signalsPanel.drawableSignalsCheckBoxes.length; i++) {
			JCheckBox checkBox = new JCheckBox(header.getChannels().get(
					selectedChannels.get(i)).getName());
			JLabel label = new JLabel("", JLabel.RIGHT);

			checkBox.setToolTipText(header.getChannels().get(selectedChannels.get(i))
					.getName());
			checkBox.setForeground(Const.DC_SIGNALS_COLORS[i
					% Const.DC_SIGNALS_COLORS.length]);
			checkBox.setOpaque(false);

			if (project.getAveragedSignalsIndexes() == null) {
				// System.out.println("project.getAveragedSignalsIndexes() == null");
				checkBox.setSelected(true);
			} else {
				checkBox.setSelected(project.getAveragedSignalsIndexes().contains(
						selectedChannels.get(i)));
			}

			checkBox.addActionListener(channelCheckBoxListener);

			label.setBackground(Color.WHITE);
			label.setOpaque(true);
			label.setBorder(BorderFactory.createLineBorder(Const.DC_SIGNALS_COLORS[i
					% Const.DC_SIGNALS_COLORS.length], signalsPanel.LABEL_LINE));
			label.setForeground(Const.DC_SIGNALS_COLORS[i
					% Const.DC_SIGNALS_COLORS.length]);

			signalsPanel.drawableSignalsCheckBoxes[i] = checkBox;
			signalsPanel.drawableSignalsValueLabels[i] = label;
		}

		signalsPanel.repaintVisibleSignals();

		signalsPanel.repaint();
		signalsPanel.validate();
	}

	/**
	 * Nastav� po�et zobrazen�ch sign�l� na zadanou hodnotu.<br/>
	 * Bude-li pou�ita hodnota v�t��, ne� je po�et vykresliteln�ch sign�l�, po�et
	 * zobrazen�ch sign�l� se nastav� na po�et vykresliteln�ch sign�l�.
	 * 
	 * @param count
	 *          Po�et zobrazen�ch sign�l�.
	 */
	private synchronized void setNumberOfVisibleChannels(int count) {
		numberOfVisibleChannels = count;
		recountChannels();
	}

	/**
	 * Nastav� index prvn�ho zobrazen�ho kan�lu.<br/>
	 * Hodnota mimo rozsah se p�epo�te na nejbli��� povolenou hodnotu.
	 * 
	 * @param index
	 *          Index prvn�ho zobrazen�ho kan�lu.
	 */
	protected synchronized void setFirstVisibleChannel(int index) {
		firstVisibleChannel = index;
		recountChannels();
	}

	/**
	 * Vrac� po�et vybran�ch sign�l�, tzn. po�et sign�l�, kter� mohou b�t
	 * vykresleny.
	 * 
	 * @return Po�et sign�l�, kter� mohou b�t vykresleny.
	 */
	protected synchronized int getNumberOfDrawableChannels() {
		return numberOfDrawableChannels;
	}

	/**
	 * Vrac� po�et vykreslovan�ch sign�l�.<br/>
	 * Je v�y men�� nebo roven po�tu sign�l�, kter� mohou b�t vykresleny.
	 * 
	 * @return Po�et vykreslovan�ch sign�l�.
	 */
	protected synchronized int getNumberOfVisibleChannels() {
		return numberOfVisibleChannels;
	}

	/**
	 * Vrac� index prvn�ho vykreslovan�ho sign�lu.
	 * 
	 * @return Index prvn�ho vykreslovan�ho sign�lu.
	 */
	protected synchronized int getFirstVisibleChannel() {
		return firstVisibleChannel;
	}

	protected DrawingComponent getDrawingComponent() {
		return drawingComponent;
	}

	protected float getPaintVolume() {
		return paintVolume;
	}

	protected JPopupMenu getOptionMenu() {
		return signalsPanel.optionMenu;
	}

	/**
	 * Obsluhuje funkci CheckBox� jednotliv�ch kan�l�, kter� se maj� vyu��vat p�i
	 * pr�m�rov�n�.
	 */
	private class ChannelCheckBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SignalProject project = (SignalProject) session.getCurrentProject();
			if (project == null) {
				return;
			}

			ArrayList<Integer> averagedChannels = new ArrayList<Integer>();

			for (int i = 0; i < signalsPanel.drawableSignalsCheckBoxes.length; i++) {

				if (signalsPanel.drawableSignalsCheckBoxes[i].isSelected()) {
					averagedChannels.add(project.getSelectedChannels().get(i));
				}
			}

			project.setAveragedSignalsIndexes(averagedChannels);
			averageSelectedEpochs();
		}
	}

}
