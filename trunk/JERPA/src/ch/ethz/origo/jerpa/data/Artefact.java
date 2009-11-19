package ch.ethz.origo.jerpa.data;

/**
 * T��da reprezentuj�c� artefakt.
 * 
 * @author Petr - Soukal
 * @author Vaclav Souhrada 
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 */
public class Artefact {
	private int artefactStart;
	private int artefactEnd;

	/**
	 * Vytv��� instanci t��dy.
	 * 
	 * @param artefactStart -
	 *          po��te�n� hodnota intervalu artefaktu.
	 * @param artefactEnd -
	 *          kone�n� hodnota intervalu artefaktu.
	 */
	public Artefact(int artefactStart, int artefactEnd) {
		this.artefactStart = artefactStart;
		this.artefactEnd = artefactEnd;
	}

	/**
	 * Nastavuje po��te�n� hodnotu intervalu artefaktu.
	 * 
	 * @param artefactStart -
	 *          po��te�n� hodnota intervalu artefaktu.
	 */
	public void setArtefactStart(int artefactStart) {
		this.artefactStart = artefactStart;
	}

	/**
	 * Nastavuje kone�nou hodnotu intervalu artefaktu.
	 * 
	 * @param artefactEnd -
	 *          kone�nou hodnota intervalu artefaktu.
	 */
	public void setArtefactEnd(int artefactEnd) {
		this.artefactEnd = artefactEnd;
	}

	/**
	 * @return po��te�n� hodnota intervalu artefaktu.
	 */
	public int getArtefactStart() {
		return artefactStart;
	}

	/**
	 * @return kone�n� hodnota intervalu artefaktu.
	 */
	public int getArtefactEnd() {
		return artefactEnd;
	}
}
