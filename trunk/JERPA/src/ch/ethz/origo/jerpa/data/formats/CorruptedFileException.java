package ch.ethz.origo.jerpa.data.formats;

/**
 * V�jimka signalizuj�c� po�kozenou logickou strukturu na��tan�ho souboru.
 * 
 * @author Ji�� Ku�era
 */
public class CorruptedFileException extends Exception {
	public CorruptedFileException(String message) {
		super(message);
	}

	/**
	 * Vyhodi vyjimku kdyz je preruseno cteni vstupu
	 * 
	 * @param message
	 *          doplnujici chybova hlaska
	 * @param e
	 *          vyjimka samotna
	 */
	public CorruptedFileException(String message, Exception e) {
		super(message, e);
	}
}
