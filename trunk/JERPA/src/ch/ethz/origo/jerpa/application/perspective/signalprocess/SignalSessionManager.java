/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *  
 *    Copyright (C) 2009 - 2010 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.application.perspective.signalprocess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.SignalProjectLoader;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.SignalProjectWriter;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output.ExportFrameProvider;
import ch.ethz.origo.juigle.application.exception.ProjectOperationException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.project.Project;
import ch.ethz.origo.juigle.application.project.SessionManager;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.2.3 (4/17/2010)
 * @since 0.1.0 (11/18/09)
 * @see SessionManager
 * 
 */
public class SignalSessionManager extends SessionManager {

	private SignalsSegmentation signalsSegmentation;
	private AutomaticArtefactSelection autoSelectionArtefact;
	private BaselineCorrection baselineCorrection;
	private SignalPerspectiveObservable sigPerspObservable;

	public SignalSessionManager() {
		signalsSegmentation = new SignalsSegmentation(this);
		autoSelectionArtefact = new AutomaticArtefactSelection(this);
		baselineCorrection = new BaselineCorrection(this);
		sigPerspObservable = SignalPerspectiveObservable.getInstance();
		new ExportFrameProvider(this);
	}

	@Override
	public void loadFile(File file) throws ProjectOperationException {
		BufferCreator loader;
		Buffer buffer;
		Header header;
		ArrayList<Epoch> epochs;

		try {
			loader = new BufferCreator(file);
			buffer = loader.getBuffer();
			header = loader.getHeader();
			epochs = loader.getEpochs();
		} catch (IOException e) {
			throw new ProjectOperationException("JERPA011", e);
		} catch (CorruptedFileException e) {
			throw new ProjectOperationException("JERPA011", e);
		}

		createProject(new SignalProject(), file.getName());

		getCurrentProject().setBuffer(buffer);
		getCurrentProject().setHeader(header);
		getCurrentProject().setAllEpochsList(epochs);

		loader = null;

		List<Integer> selectedChannels = new ArrayList<Integer>();
		for (int i = 0; i < getCurrentProject().getHeader().getNumberOfChannels(); i++) {
			selectedChannels.add(i);
		}
		getCurrentProject().setSelectedChannels(selectedChannels);

		List<Integer> averagedChannels = new ArrayList<Integer>();
		for (int i = 0; i < getCurrentProject().getHeader().getNumberOfChannels(); i++) {
			averagedChannels.add(i);
		}
		getCurrentProject().setAveragedSignalsIndexes(averagedChannels);

		getCurrentProject().setDataFile(file);

	}

	@Override
	public void loadProject(File file) throws ProjectOperationException {
		SignalProjectLoader loader;
		SignalProject project = null;
		loader = new SignalProjectLoader(file);
		project = (SignalProject) loader.loadProject();
		addProject(project);
	}
	
	public void saveAsFile() throws ProjectOperationException {
		Project project = getCurrentProject();
		if (project == null) {
			throw new ProjectOperationException("JG012", new Throwable("UNDEFINED VALUE"));
		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save project");
		fileChooser.setFileFilter(new FileNameExtensionFilter(
				"jERPStudio Project file (*." + Const.PROJECT_FILE_EXTENSION + ")",
				Const.PROJECT_FILE_EXTENSION));

		fileChooser.setAcceptAllFileFilterUsed(false);

		if (fileChooser.showSaveDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (!file.getName().endsWith("." + Const.PROJECT_FILE_EXTENSION)) {
				file = new File(file.getAbsolutePath() + "."
						+ Const.PROJECT_FILE_EXTENSION);
			}
			project.setProjectFile(file);
			saveCurrentProject(new SignalProjectWriter());
		}
	}

	/**
	 * P�id� projekt do seznamu projekt� a nastav� jej jako aktu�ln�.
	 * 
	 * @param project
	 *          Projekt pro p�id�n� do seznamu projekt�.
	 * @return Index p�idan�ho projektu, nebo -1, pokud <code>project</code> je
	 *         <code>null</code>.
	 */
	public int addProject(Project project) {
		if (project == null) {
			return -1;
		}

		projects.add(project);
		currentProjectIndex = projects.size() - 1;
		return currentProjectIndex;
	}

	/**
	 * @return the signalsSegmentation
	 */
	public SignalsSegmentation getSignalsSegmentation() {
		return signalsSegmentation;
	}

	/**
	 * @return the autoSelectionArtefact
	 */
	public AutomaticArtefactSelection getAutoSelectionArtefact() {
		return autoSelectionArtefact;
	}

	/**
	 * @return the baselineCorrection
	 */
	public BaselineCorrection getBaselineCorrection() {
		return baselineCorrection;
	}

	@Override
	public SignalProject getCurrentProject() {
		return (SignalProject) super.getCurrentProject();
	}

	/**
	 * P�ipoj� na konec zadan�ho projektu epochu.
	 * 
	 * @param project
	 *          Projekt, ke kter�mu bude epocha p�ipojena.
	 * @param frames
	 *          Seznam fram� s daty kan�l�. Index v seznamu je indexem framu,
	 *          index v poli je indexem kan�lu.
	 * @param epoch
	 *          Informace o p�id�van� epo�e.
	 * @throws ProjectOperationException
	 * @throws InvalidFrameIndexException
	 */
	public static void appendEpochToProject(SignalProject project,
			List<float[]> frames, Epoch epoch) throws ProjectOperationException {
		if (project.getBuffer() == null) {
			throw new ProjectOperationException("JERPA004");
			// TODO JERPA004 EXCEPTION
		}
		Header newHeader;
		newHeader = project.getHeader().clone();
		if (newHeader == null) {
			return;
		}
		newHeader
				.setNumberOfSamples(newHeader.getNumberOfSamples() + frames.size());

		BufferCreator bc = null;
		try {
			bc = new BufferCreator(newHeader);
			for (int i = 0; i < project.getHeader().getNumberOfSamples(); i++) {
				bc.saveFrame(project.getBuffer().getFrame(i));
			}

			for (int i = 0; i < frames.size(); i++) {
				bc.saveFrame(frames.get(i));
			}

			epoch.setPosition(epoch.getPosition()
					+ project.getHeader().getNumberOfSamples());

			project.getBuffer().closeBuffer();
			project.getAllEpochsList().add(epoch);
			project.setHeader(newHeader);
			project.setBuffer(bc.getBuffer());
		} catch (NullPointerException e) {
			throw new ProjectOperationException(e);
		} catch (IOException e) {
			throw new ProjectOperationException("Error while appending averages", e);
		} catch (CorruptedFileException ex) {
			throw new ProjectOperationException(ex);
		} catch (InvalidFrameIndexException e) {
			throw new ProjectOperationException("Iternal error while appending average", e);
		}
	}

	/**
	 * Vr�t� objekt s metainformacemi <code>Header</code> p�in�le��c� aktu�ln�
	 * otev�en�mu projektu.<br/>
	 * Nen�-li otev�en ��dn� projekt, vrac� null.
	 * 
	 * @return Objekt s metainformacemi pat��c� aktu�ln� otev�en�mu projektu.
	 */
	public Header getCurrentHeader() {
		SignalProject project;

		if ((project = (SignalProject) getCurrentProject()) == null) {
			return null;
		} else {
			return project.getHeader();
		}
	}

	public IObservable getSignalPerspectiveObservable() {
		return sigPerspObservable;
	}
	
	/**
	 * Metoda volaj�c� metodu automatick�ho ozna�ov�n� artefakt� pomoc�
	 * gradientn�ho krit�ria.
	 * 
	 * @param maxAllowedVoltageStep -
	 *          krok maxim�ln�ho nap�t�, kter� ur�uje interval artefaktu.
	 * @param indicesSelectSignals -
	 *          pole testovan�ch kan�l�.
	 */
	public void playAutomaticGradientArtefactSelection(int maxAllowedVoltageStep,
			int[] indicesSelectSignals) {
		autoSelectionArtefact.gradientCriterion(maxAllowedVoltageStep,
				indicesSelectSignals);
	}

	/**
	 * Metoda volaj�c� metodu automatick�ho ozna�ov�n� artefakt� pomoc� krit�ria
	 * amplitudy.
	 * 
	 * @param minAllowedAmplitude -
	 *          minim�ln� nap�t�, kter� ur�uje spodn� mez intervalu artefaktu.
	 * @param maxAllowedAmplitude -
	 *          maxim�ln� nap�t�, kter� ur�uje horn� mez intervalu artefaktu.
	 * @param indicesSelectSignals -
	 *          pole testovan�ch kan�l�.
	 */
	public void playAutomaticAmplitudeArtefactSelection(int minAllowedAmplitude,
			int maxAllowedAmplitude, int[] indicesSelectSignals) {
		autoSelectionArtefact.amplitudeCriterion(minAllowedAmplitude,
				maxAllowedAmplitude, indicesSelectSignals);
	}

	/**
	 * Pos�l� zpr�vu o automatick�m ozna�en� artefakt�.
	 */
	public void sendArtefactSelectionMesage() {
		SignalPerspectiveObservable.getInstance().setState(SignalPerspectiveObservable.MSG_AUTOMATIC_ARTEFACT_SELECTION);
	}

	/**
	 * Metoda volaj�c� metodu pro opravu baseliny v zadan�m intervalu a n�sledn� o
	 * tom pos�l� zpr�vu.
	 */
	public void applyBaselineCorrection(long startTimeStamp, long endTimeStamp) {
		baselineCorrection.correction(startTimeStamp, endTimeStamp);
		SignalPerspectiveObservable.getInstance().setState(SignalPerspectiveObservable.MSG_NEW_BUFFER);
	}

	/**
	 * Metoda volaj�c� metodu pro opravu baseliny v cel� d�lce sign�l� a n�sledn�
	 * o tom pos�l� zpr�vu.
	 */
	public void applyBaselineCorrection() {
		baselineCorrection.correction();
		SignalPerspectiveObservable.getInstance().setState(SignalPerspectiveObservable.MSG_NEW_BUFFER);
	}

}