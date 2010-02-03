package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

/**
 * Panel akc�, kter� nenab�z� ��dn� akce. Existuje pouze z toho d�vodu, �e nelze
 * do pr�m�rovac�ch panel� vlo�it <b>null</b> m�sto komponenty. Pokud by
 * nedo�lo k vlo�en� ��dn�ho pr�m�rovac�ho panelu, pak by p�i vol�n� metod
 * pr�m�rovac�ch panel� doch�zelo k <code>NullPointerException</code>.
 * 
 * @author Tom� �ond�k
 */
@SuppressWarnings("serial")
class NullActionsPanel extends ActionsPanel {
	/**
	 * Vytv��� pr�zdn� panel akc�.
	 * 
	 * @param meanPanel
	 *          Pr�m�rovac� panel, ve kter�m je panel akc� um�st�n.
	 */
	NullActionsPanel(MeanPanel meanPanel) {
		super(meanPanel);
	}
}
