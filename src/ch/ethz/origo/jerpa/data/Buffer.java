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
package ch.ethz.origo.jerpa.data;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.Observable;

import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;

/**
 * Rozhran� mezi aplika�n� a datovou vrstvou.<br/>
 * Obstar�v� vyrovn�vac� pam� pro na��t�n� dat z do�asn�ho souboru.
 * 
 * @author Jiri Kucera
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.1 01/17/2010)
 * @since 0.1.0 (11/17/2009)
 */
public class Buffer extends Observable {

	private final static int WORD_LENGTH = Integer.SIZE / 8;
	private File tmpFile = null;
	private NioInputStream ist;
	private int numberOfSignals;
	private long numberOfSamples = 0;
	private boolean frameLast;
	private boolean valueLast;
	private long seekPos;
	private boolean closed;
	private int numberOfParents;

	/**
	 * Konstruktor vytvo�� Buffer nad do�asn�m souborem.<br/>
	 * Do�asn� soubor mus� m�t ulo�ena data v bin�rn�m form�tu:<br/>
	 * A0, B0, C0, A1, B1, C1, A2, B2, C2...,<br/>
	 * kde A, B, C (D, E...) je ozna�en� sign�lu, 0, 1, 2... je ��slo framu.<br/>
	 * A0, B0, C0... jsou hodnoty o d�lce <code>WORD_LENGTH</code> byt�.
	 * 
	 * @param tmpFile
	 *          Do�asn� soubor s ulo�en�mi daty.
	 * @param numberOfSamples
	 *          Po�et vzork� (fram�, sampl�) sign�lu.
	 * @param numberOfSignals
	 *          Po�et sign�l� (kan�l�).
	 * @throws java.io.IOException
	 */
	protected Buffer(File tmpFile, long numberOfSamples, int numberOfSignals)
			throws IOException {
		this.numberOfSignals = numberOfSignals;
		this.tmpFile = tmpFile;
		this.numberOfSamples = numberOfSamples;
		this.ist = new NioInputStream(tmpFile);
		frameLast = false;
		valueLast = false;
		closed = false;
		numberOfParents = 0;
	}

	/**
	 * Vr�t� sn�mek s dan�m indexem.
	 * 
	 * @param frameIndex
	 *          Index sn�mku.
	 * @return Vrac� sn�mek na dan�m indexu v souboru.
	 * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
	 */
	public float[] getFrame(long frameIndex) throws InvalidFrameIndexException {

		if (frameIndex < 0 || frameIndex >= numberOfSamples) {
			throw new InvalidFrameIndexException("JERPA003{" + frameIndex + "}");
		}

		float[] frame = loadFrame(frameIndex);

		frameLast = true;
		valueLast = false;

		return frame;
	}

	/**
	 * Vr�t� hodnotu sign�lu nach�zej�c� se na sn�mku se zadan�m indexem.
	 * 
	 * @param channelIndex
	 *          Index sign�lu.
	 * @param frameIndex
	 *          Index sn�mku.
	 * @return Hodnota sign�lu na dan�m indexu.
	 * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
	 */
	public float getValue(int channelIndex, long frameIndex)
			throws InvalidFrameIndexException {
		if (frameIndex < 0 || frameIndex >= numberOfSamples) {
			throw new InvalidFrameIndexException("Frame index out of data length: "
					+ frameIndex);
		}
		if (channelIndex < 0 || channelIndex >= numberOfSignals) {
			throw new InvalidFrameIndexException("Channel index out of data length: "
					+ channelIndex);
		}

		float value = loadValue(channelIndex, frameIndex);

		frameLast = false;
		valueLast = true;

		return value;
	}

	/**
	 * Vr�t� n�sleduj�c� sn�mek od posledn� vr�cen�ho sn�mku.
	 * 
	 * @return Vrac� n�sleduj�c� sn�mek.
	 * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
	 */
	public float[] getNextFrame() throws InvalidFrameIndexException {
		if (frameLast) {
			return loadNextFrame();
		} else {
			throw new InvalidFrameIndexException("Should read whole frame first.");
		}
	}

	/**
	 * Vr�t� hodnotu (stejn�ho) sign�lu z n�sleduj�c�ho sn�mku od naposledy �ten�
	 * hodnoty.
	 * 
	 * @return N�sleduj�c� hodnota sn�mku pro dan� naposledy �ten� kan�l.
	 * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
	 */
	public float getNextValue() throws InvalidFrameIndexException {
		if (valueLast) {
			return loadNextValue();
		} else {
			throw new InvalidFrameIndexException("Should read single value first.");
		}
	}

	/**
	 * Metoda pro uzav�en� do�asn�ho souboru a jeho smaz�n�.<br/>
	 * Nutno zavolat p�ed zru�en�m instance t��dy Buffer.
	 * 
	 * @throws IOException
	 */
	public void closeBuffer() throws IOException {

		if (numberOfParents-- > 1) {
			return;
		}

		if (closed) {
			return;
		}

		if (!tmpFile.delete()) {
			throw new IOException("Could not delete temporary file: "
					+ tmpFile.getAbsolutePath());
		}

		closed = true;
	}

	/**
	 * Na�te z do�asn�ho souboru sn�mek zadan� indexem.
	 * 
	 * @param frameIndex
	 *          index sn�mku.
	 * @return sn�mek na�ten� z do�asn�ho souboru.
	 */
	private float[] loadFrame(long frameIndex) {
		seekPos = frameIndex * WORD_LENGTH * numberOfSignals;

		float[] frame = new float[numberOfSignals];

		if (seekPos < 0
				|| seekPos >= numberOfSamples * WORD_LENGTH * numberOfSignals) {
			for (int i = 0; i < numberOfSignals; i++) {
				frame[i] = 0;
			}
			return frame;
		}

		try {
			ist.seek((int) seekPos);
		} catch (IOException e) {
			for (int i = 0; i < numberOfSignals; i++) {
				frame[i] = 0;
			}
			return frame;
		} catch (IllegalArgumentException e) {
			for (int i = 0; i < numberOfSignals; i++) {
				frame[i] = 0;
			}
			return frame;

		}

		try {
			for (int i = 0; i < numberOfSignals; i++) {
				frame[i] = ist.readFloat();
			}
		} catch (IOException e) {
			for (int i = 0; i < numberOfSignals; i++) {
				frame[i] = 0;
			}
		} catch (BufferUnderflowException e) {
			for (int i = 0; i < numberOfSignals; i++) {
				frame[i] = 0;
			}
		}

		return frame;
	}

	/**
	 * Na�te z do�asn�ho souboru sn�mek n�sleduj�c� za posledn� �ten�m sn�mkem.
	 * 
	 * @return Vrac� sn�mek na�ten� z do�asn�ho souboru.
	 * @throws IOException
	 */
	private float[] loadNextFrame() {
		float[] frame = new float[numberOfSignals];

		try {
			for (int i = 0; i < numberOfSignals; i++) {
				frame[i] = ist.readFloat();
			}
		} catch (IOException e) {
			for (int i = 0; i < numberOfSignals; i++) {
				frame[i] = 0;
			}
		}

		return frame;
	}

	/**
	 * Na�te z do�asn�ho souboru hodnotu n�sleduj�c� za posledn� �tenou hodnotou.
	 * 
	 * @return
	 */
	private float loadNextValue() {
		seekPos += WORD_LENGTH * numberOfSignals;

		float value;

		if (seekPos < 0
				|| seekPos >= WORD_LENGTH * numberOfSamples * numberOfSignals) {
			return 0;
		}

		try {
			ist.seek((int) seekPos);
		} catch (IOException e) {
			return 0;
		} catch (IllegalArgumentException e) {
			return 0;
		}

		try {
			value = ist.readFloat();
		} catch (EOFException e) {
			value = 0;
		} catch (IOException e) {
			value = 0;
		} catch (BufferUnderflowException e) {
			value = 0;
		}

		return value;
	}

	/**
	 * Na�te z do�asn�ho souboru hodnotu sign�lu na sn�mku zadan�m indexem.
	 * 
	 * @param channelIndex
	 *          Index sign�lu, jeho� hodnota m� b�t �tena.
	 * @param frameIndex
	 *          Index sn�mku, jeho� hodnota m� b�t pro zadan� sign�l �tena.
	 * @return
	 */
	private float loadValue(int channelIndex, long frameIndex) {
		seekPos = WORD_LENGTH * (frameIndex * numberOfSignals + channelIndex);

		float value;

		if (seekPos < 0
				|| seekPos >= WORD_LENGTH * numberOfSamples * numberOfSignals) {
			return 0;
		}

		try {
			ist.seek((int) seekPos);
		} catch (IOException e) {
			return 0;
		} catch (IllegalArgumentException e) {
			return 0;
		}

		try {
			value = ist.readFloat();
		} catch (EOFException e) {
			value = 0;
		} catch (IOException e) {
			value = 0;
		} catch (BufferUnderflowException e) {
			value = 0;
		}

		return value;
	}

	/**
	 * Vrac� po�et sign�l� v�etn� synchroniza�n�ch.
	 * 
	 * @return Po�et sign�l�
	 */
	public int getNumberOfSignals() {
		return numberOfSignals;
	}

	/**
	 * Metoda pro p��m� seekov�n� v datov�m �lo�i�ti. Nastav� pozici v datov�m
	 * �lo�i�ti na zadanou hodnotu<br/>
	 * Slou�� pouze pro usnadn�n� kop�rov�n� raw dat bez specifikace jejich
	 * v�znamu.
	 * 
	 * @param seekPos
	 *          Pozice v datov�m �lo�i�ti.
	 * @throws java.io.IOException
	 */
	public void seek(int seekPos) throws IOException {
		ist.seek(seekPos);
	}

	/**
	 * Vr�t� dal�� hodnotu typu <code>float</code> z datov�ho �lo�i�t� a posune
	 * ukazatel od st�vaj�c� pozice o �ty�i bajty vp�ed.<br/>
	 * Slou�� pouze pro usnadn�n� kop�rov�n� raw dat bez specifikace jejich
	 * v�znamu.
	 * 
	 * @return Hodnota nach�zej�c� se na aktu�ln� pozici ukazatele.
	 * @throws java.io.IOException
	 */
	public float getFloat() throws IOException {
		return ist.readFloat();
	}

	/**
	 * Vr�t� po�et zb�vaj�c�ch bajt� od aktu�ln� pozice ukazatele po konec
	 * datov�ho �lo�i�t�. Slou�� pouze pro usnadn�n� kop�rov�n� raw dat bez
	 * specifikace jejich v�znamu.
	 * 
	 * @return Po�et zb�vaj�c�ch bajt�.
	 */
	public int remaining() {
		return ist.getRemaining();
	}

	public void addParent() {
		numberOfParents++;
	}

	// public void removeParent() {
	// numberOfParents--;
	// }

	public int getNumberOfParents() {
		return numberOfParents;
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	protected void finalize() {
		tmpFile.delete();
		closed = true;
	}
	
	public long getNumOfSamples() {
		return numberOfSamples;
	}
}
// FIXME osetrit ve vsech metodach presazeni konce nebo zacatku souboru (kalwi -
// osobne to udelam)
