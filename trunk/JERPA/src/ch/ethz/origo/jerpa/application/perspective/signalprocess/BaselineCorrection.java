package ch.ethz.origo.jerpa.application.perspective.signalprocess;

import java.io.IOException;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Header;

/**
 * T��da pro opravu baseliny.
 * 
 * @author Petr - Soukal (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 */
public class BaselineCorrection {
	private SignalSessionManager appCore;
	private Buffer buffer;
	private Header header;
	private int channelsCount;
	private long numberOfSamples;
	private long[] signalsAverages;
	private long startTimeStamp;
	private long endTimeStamp;

	/**
	 * Vytv��� objekt t��dy a instancuje appCore.
	 * 
	 * @param appCore -
	 *          instance objektu hlavn� ��d�c� t��dy aplika�n� vrstvy.
	 */
	public BaselineCorrection(SignalSessionManager appCore) {
		this.appCore = appCore;
	}

	/**
	 * Nastavuje pot�ebn� parametry p�i oprav� baseline.
	 */
	public void setCurrentData() {
		channelsCount = appCore.getCurrentProject().getHeader().getChannels()
				.size();
		numberOfSamples = appCore.getCurrentProject().getHeader()
				.getNumberOfSamples();
		buffer = appCore.getCurrentProject().getBuffer();
		header = appCore.getCurrentProject().getHeader();
		signalsAverages = new long[channelsCount];
	}

	/**
	 * Vrac� absolutn� index framu na zadan�m �ase.
	 * 
	 * @param time
	 *          �as v milisekund�ch.
	 * @return Absolutn� index framu - od za��tku souboru.
	 */
	public synchronized int getAbsoluteFrame(long time) {
		return (int) (time * 1000 / header.getSamplingInterval());
	}

	/**
	 * Opravuje baseline v rozsahu cel� delky sign�l�.
	 */
	public void correction() {
		try {
			for (long i = 0; i < numberOfSamples; i++) {
				for (int j = 0; j < signalsAverages.length; j++) {
					signalsAverages[j] += (long) buffer.getValue(j, i);
				}
			}

			for (int i = 0; i < signalsAverages.length; i++) {
				signalsAverages[i] /= numberOfSamples;
			}

			BufferCreator bufferCreator = new BufferCreator(appCore
					.getCurrentHeader());
			float[] actualFrame;

			for (long i = 0; i < numberOfSamples; i++) {
				actualFrame = buffer.getFrame(i);

				for (int j = 0; j < actualFrame.length; j++) {
					actualFrame[j] -= signalsAverages[j];
				}

				bufferCreator.saveFrame(actualFrame);
			}

			appCore.getCurrentProject().setBuffer(bufferCreator.getBuffer());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CorruptedFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidFrameIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Opravuje baseline v rozsahu nastavovan�ho intervalu.
	 * 
	 * @param startTimeStamp -
	 *          po��te�n� hodnota intervalu.
	 * @param endTimeStamp -
	 *          kone�n� hodnota intervalu.
	 */
	public void correction(long startTimeStamp, long endTimeStamp) {
		int startIndex = getAbsoluteFrame(startTimeStamp);
		int endIndex = getAbsoluteFrame(endTimeStamp);

		if (startIndex >= numberOfSamples) {
			startIndex = (int) numberOfSamples - 1;
		}

		if (endIndex >= numberOfSamples) {
			endIndex = (int) numberOfSamples - 1;
		}

		int divider = endIndex - startIndex;

		try {
			for (long i = startIndex; i <= endIndex; i++) {
				for (int j = 0; j < signalsAverages.length; j++) {
					signalsAverages[j] += (long) buffer.getValue(j, i);
				}
			}

			for (int i = 0; i < signalsAverages.length; i++) {
				signalsAverages[i] /= divider;
			}

			BufferCreator bufferCreator = new BufferCreator(appCore
					.getCurrentHeader());
			float[] actualFrame;

			for (long i = 0; i < startIndex; i++) {
				bufferCreator.saveFrame(buffer.getFrame(i));
			}

			for (long i = startIndex; i <= endIndex; i++) {
				actualFrame = buffer.getFrame(i);

				for (int j = 0; j < actualFrame.length; j++) {
					actualFrame[j] -= signalsAverages[j];
				}

				bufferCreator.saveFrame(actualFrame);
			}

			for (long i = endIndex + 1; i < numberOfSamples; i++) {
				bufferCreator.saveFrame(buffer.getFrame(i));
			}

			appCore.getCurrentProject().setBuffer(bufferCreator.getBuffer());
		} catch (IOException e1) {
			// TODO !!! EXCEPTIONY UPRAVIT
			e1.printStackTrace();
		} catch (CorruptedFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidFrameIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Nastavuje hodnoty intervalu pro opravu baseline.
	 * 
	 * @param startTimeStamp -
	 *          po��te�n� hodnota intervalu.
	 * @param endTimeStamp -
	 *          kone�n� hodnota intervalu.
	 */
	public void setTimeStampsForCorrection(long startTimeStamp, long endTimeStamp) {
		this.startTimeStamp = startTimeStamp;
		this.endTimeStamp = endTimeStamp;
	}

	/**
	 * @return po��te�n� hodnota intervalu.
	 */
	public long getStartTimeStamp() {
		return startTimeStamp;
	}

	/**
	 * @return kone�n� hodnota intervalu.
	 */
	public long getEndTimeStamp() {
		return endTimeStamp;
	}
}
