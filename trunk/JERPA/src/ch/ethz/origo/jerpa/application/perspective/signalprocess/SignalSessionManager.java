package ch.ethz.origo.jerpa.application.perspective.signalprocess;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.application.exception.ProjectOperationException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.application.project.SessionManager;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.formats.CorruptedFileException;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void loadProject(File file) throws ProjectOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveCurrentProject() throws ProjectOperationException {
		// TODO Auto-generated method stub

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
