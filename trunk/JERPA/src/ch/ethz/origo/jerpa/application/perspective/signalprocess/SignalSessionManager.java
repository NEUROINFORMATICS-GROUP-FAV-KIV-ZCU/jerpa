package ch.ethz.origo.jerpa.application.perspective.signalprocess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.application.exception.ProjectOperationException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.application.project.Project;
import ch.ethz.origo.jerpa.application.project.SessionManager;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.SignalProjectLoader;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.SignalProjectWriter;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * 
 */
public class SignalSessionManager extends SessionManager {

	private SignalsSegmentation signalsSegmentation;
	private AutomaticArtefactSelection autoSelectionArtefact;
	private BaselineCorrection baselineCorrection;

	public SignalSessionManager() {
		signalsSegmentation = new SignalsSegmentation(this);
		autoSelectionArtefact = new AutomaticArtefactSelection(this);
		baselineCorrection = new BaselineCorrection(this);
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
		try {
			project = (SignalProject)loader.loadProject();
		} catch (IOException e) {
			throw new ProjectOperationException("JERPA011", e);
		} catch (CorruptedFileException e) {
			throw new ProjectOperationException("JERPA011", e);
		}
		addProject(project);
	}

	@Override
	public void saveCurrentProject() throws ProjectOperationException {
		Project project = getCurrentProject();
		if (project == null) {
			return;
		}
		try {
			new SignalProjectWriter(getCurrentProject()).saveProject();
		} catch (CorruptedFileException e) {
			throw new ProjectOperationException("JERPA012", e);
		} catch (IOException e) {
			throw new ProjectOperationException("JERPA012", e);
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
			throw new ProjectOperationException(e);
		} catch (CorruptedFileException ex) {
			throw new ProjectOperationException(ex);
		} catch (InvalidFrameIndexException e) {
			throw new ProjectOperationException(e);
		}
	}
	
	/**
	 * Vr�t� objekt s metainformacemi <code>Header</code> p�in�le��c� aktu�ln�
	 * otev�en�mu projektu.<br/> Nen�-li otev�en ��dn� projekt, vrac� null.
	 * 
	 * @return Objekt s metainformacemi pat��c� aktu�ln� otev�en�mu projektu.
	 */
	public Header getCurrentHeader() {
		SignalProject project;

		if ((project = (SignalProject)getCurrentProject()) == null) {
			return null;
		} else {
			return project.getHeader();
		}
	}

}
