package ch.ethz.origo.jerpa.data.formats;

import java.io.IOException;
import java.util.ArrayList;

import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;

/**
 * Rozhran�, kter� by melo b�t implementov�no ka�d�m programem podporovan�m
 * form�tem. Vynucuje implementaci metody load, se kterou pracuje rozhran� mezi
 * datovou a aplika�n� vrstvou.
 * 
 * @author Ji�� Ku�era (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * 
 */
public interface DataFormatLoader {

	/**
	 * Na�te data z datov�ho souboru do <code>BufferCreator</code>u a vr�t�
	 * hlavi�ku <code>Header</code>. Cestu k datov�mu souboru poskytne
	 * <code>BufferCreator</code>.
	 * 
	 * @param loader
	 * @return Hlavi�ka typu <code>Header</code> s informacemi o na�ten�m
	 *         souboru.
	 * @throws java.io.IOException
	 * @throws cz.zcu.kiv.jerpstudio.data.formats.CorruptedFileException
	 */
	public Header load(BufferCreator loader) throws IOException,
			CorruptedFileException;

	/**
	 * Vrac� ArrayList obsahuj�c� markery.
	 * 
	 * @return ArrayList obsahuj�c� markery.
	 */
	public ArrayList<Epoch> getEpochs();
}
