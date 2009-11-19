package ch.ethz.origo.jerpa.data.formats;

import java.io.File;
import java.io.IOException;

import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.Header;

/**
 * Rozhran� modul� pro ukl�d�n� datov�ch soubor�.
 * 
 * @author Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 */
public interface DataFormatWriter {
	/**
	 * Zap�e data do souboru.
	 * 
	 * @param header
	 *          Hlavi�ka s informacemi o souboru.
	 * @param buffer
	 *          T��da s daty.
	 * @param outputFile
	 *          V�stupn� soubor.
	 * @throws java.io.IOException
	 */
	public void write(Header header, Buffer buffer, File outputFile)
			throws IOException;

}
