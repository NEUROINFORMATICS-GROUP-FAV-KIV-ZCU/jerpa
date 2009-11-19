package ch.ethz.origo.jerpa.application.perspective.signalprocess;

import java.util.ArrayList;

import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.data.Artefact;
import ch.ethz.origo.jerpa.data.Buffer;

/**
 * T��da pro automatick� ozna�ov�n� artefakt�.
 * 
 * @author Petr - Soukal (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 */
public class AutomaticArtefactSelection {
	private SignalSessionManager appCore;
	private int channelsCount;
	private long numberOfSamples;
	private Buffer buffer;
	private boolean[] artefactsArray;
	private ArrayList<Artefact> artefacts;

	/**
	 * Vytv��� objekt t��dy a nastavuje appCore.
	 * 
	 * @param appCore -
	 *          j�dro aplikace udr�uj�c� vztah mezi aplika�n� a prezenta�n�
	 *          vrstvou.
	 */
	public AutomaticArtefactSelection(SignalSessionManager appCore) {
		this.appCore = appCore;
		artefacts = new ArrayList<Artefact>();
	}

	/**
	 * Nastavuje data aktu�ln�ho projektu.
	 */
	public void setCurrentData() {
		channelsCount = appCore.getCurrentProject().getHeader().getChannels()
				.size();
		numberOfSamples = appCore.getCurrentProject().getHeader()
				.getNumberOfSamples();
		buffer = appCore.getCurrentProject().getBuffer();
		artefactsArray = new boolean[(int) numberOfSamples];
	}

	/**
	 * Ozna�uje artefakty v souboru podle nastaven�ch hodnot gradientn�m
	 * krit�riem.
	 * 
	 * @param maxAllowedVoltageStep -
	 *          krok maxim�ln�ho nap�t�, kter� ur�uje interval artefaktu.
	 * @param indicesSelectSignals -
	 *          pole testovan�ch kan�l�.
	 */
	public void gradientCriterion(int maxAllowedVoltageStep,
			int[] indicesSelectSignals) {
		try {
			if (indicesSelectSignals == null) {
				for (int i = 0; i < numberOfSamples - 1; i++) {
					for (int j = 0; j < channelsCount; j++) {
						if ((Math.abs(buffer.getValue(j, i) - buffer.getValue(j, i + 1))) > maxAllowedVoltageStep) {
							artefactsArray[i] = true;
							artefactsArray[++i] = true;
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < numberOfSamples - 1; i++) {
					for (int j = 0; j < indicesSelectSignals.length; j++) {
						if ((Math.abs(buffer.getValue(indicesSelectSignals[j], i)
								- buffer.getValue(indicesSelectSignals[j], i + 1))) > maxAllowedVoltageStep) {
							artefactsArray[i] = true;
							artefactsArray[++i] = true;
							break;
						}
					}
				}
			}

			createArrayListArtefacts();
		} catch (InvalidFrameIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Ozna�uje artefakty v souboru podle nastaven�ch hodnot amplitudov�ho
	 * krit�riem.
	 * 
	 * @param minAllowedAmplitude -
	 *          minim�ln� nap�t�, kter� ur�uje spodn� mez intervalu artefaktu.
	 * @param maxAllowedAmplitude -
	 *          maxim�ln� nap�t�, kter� ur�uje horn� mez intervalu artefaktu.
	 * @param indicesSelectSignals -
	 *          pole testovan�ch kan�l�.
	 */
	public void amplitudeCriterion(int minAllowedAmplitude,
			int maxAllowedAmplitude, int[] indicesSelectSignals) {

		try {
			if (indicesSelectSignals == null) {
				for (int i = 0; i < numberOfSamples - 1; i++) {
					for (int j = 0; j < channelsCount; j++) {
						if (buffer.getValue(j, i) > maxAllowedAmplitude
								|| buffer.getValue(j, i) < minAllowedAmplitude) {
							artefactsArray[i] = true;
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < numberOfSamples - 1; i++) {
					for (int j = 0; j < indicesSelectSignals.length; j++) {
						if (buffer.getValue(indicesSelectSignals[j], i) > maxAllowedAmplitude
								|| buffer.getValue(indicesSelectSignals[j], i) < minAllowedAmplitude) {
							artefactsArray[i] = true;
							break;
						}
					}
				}
			}

			createArrayListArtefacts();
		} catch (InvalidFrameIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Prohled�v� pole artefakt� a vytv��� nov� objekty artefakt�, kter� pot�
	 * vkl�d� do arrayListu artefakt�, pro dal�� zpracov�n�.
	 */
	public void createArrayListArtefacts() {
		boolean foundArtefact = false;
		int start = 0;

		for (int i = 0; i < artefactsArray.length; i++) {
			if (!foundArtefact && artefactsArray[i]) {
				start = i;
				foundArtefact = true;

			} else if (foundArtefact && !artefactsArray[i]) {
				artefacts.add(new Artefact(start, i - 1));
				foundArtefact = false;
			}
		}

		if (foundArtefact) {
			artefacts.add(new Artefact(start, (int) numberOfSamples - 1));
		}
	}

	/**
	 * Vy�ist� arrayList a pole artefakt�.
	 */
	public void clearArraysArtefacts() {
		artefacts = new ArrayList<Artefact>();
		artefactsArray = new boolean[(int) numberOfSamples];
	}

	/**
	 * @return artefacts - arrayList artefakt�.
	 */
	public ArrayList<Artefact> getArtefacts() {
		return artefacts;
	}
}
