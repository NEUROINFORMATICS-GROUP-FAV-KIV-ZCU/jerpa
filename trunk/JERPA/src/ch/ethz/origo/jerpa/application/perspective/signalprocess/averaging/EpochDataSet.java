package ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging;

/**
 * Objekt slou�� pro p�ed�v�n� dat z aplika�n� do prezenta�n� vrstvy. Obsahuje
 * data jedn� epochy pro jeden kan�l. Pod pojmem jedna epocha se d�le rozum� bu�
 * jedna jedin� epocha nebo pr�m�r z n�kolika epoch (z hlediska implementace
 * t�to t��dy je to jedno - pr�m�r n�kolika epoch m� stejn� po�et funk�n�ch
 * hodnot jako jedna epocha).
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * @see Cloneable
 */
public final class EpochDataSet implements Cloneable {
	/**
	 * Funk�n� hodnoty celkov�ho pr�m�ru sign�lu po zahrnut� aktu�ln� epochy do
	 * pr�m�ru.
	 */
	private float[] avgWithCurrentEpochValues;
	/**
	 * Funk�n� hodnoty celkov�ho pr�m�ru sign�lu bez zahrnut� aktu�ln� epochy do
	 * pr�m�ru.
	 */
	private float[] avgWithoutCurrentEpochValues;
	/**
	 * Funk�n� hodnoty pr�b�hu sign�lu v aktu�ln� epo�e.
	 */
	private float[] currentEpochValues;
	/**
	 * Funk�n� hodnoty aktu�ln�ho pr�m�ru.
	 */
	private float[] currentAvgValues;
	/**
	 * ��k�, zda je tato epocha pro tento kan�l zahrnuta do pr�m�ru.
	 */
	private boolean checked;
	/**
	 * Po�ad� kan�lu ve vstupn�m souboru.
	 */
	private int channelOrderInInputFile;
	/**
	 * ��k�, jestli je pr�m�r, jeho� funk�n� hodnoty jsou ulo�eny v atributu
	 * <code>currentAvgValues</code>, podle statistick�ho t-testu ANOVA
	 * d�veryhodn�. Nab�v� jedn� z n�sleduj�c�ch konstant:
	 * <ul>
	 * <li><code>Averages.UNKNOWN</code>, kdy� nen� zn�mo, zda je pr�m�r
	 * d�v�ryhodn�.
	 * <li><code>Averages.TRUSTFUL</code>, kdy� je pr�m�r d�v�ryhodn�.
	 * <li><code>Averages.NOT_TRUSTFUL</code>, kdy� je pr�m�r ned�v�ryhodn�.
	 * </ul>
	 */
	private int trustful;

	/**
	 * Vytv��� instanci t��dy pro p�epravu dat z aplikan�� vrstvy do prezenta�n�
	 * vrstvy.
	 * 
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu, ke kter�mu instance pat��, ve vstupn�m souboru.
	 * @param checked
	 *          ��k�, zda je tato epocha pro tento kan�l zahrnuta do pr�m�ru.
	 * @param trustful
	 *          ��k�, jestli je pr�m�r d�v�ryhodn� (viz dokumenta�n� koment��
	 *          atributu <code>trustful</code>.
	 * @param currentEpochValues
	 *          Funk�n� hodnoty pr�b�hu sign�lu v aktu�ln� epo�e.
	 * @param avgWithCurrentEpochValues
	 *          Funk�n� hodnoty celkov�ho pr�m�ru sign�lu po zahrnut� aktu�ln�
	 *          epochy do pr�m�ru.
	 * @param avgWithoutCurrentEpochValues
	 *          Funk�n� hodnoty celkov�ho pr�m�ru sign�lu bez zahrnut� aktu�ln�
	 *          epochy do pr�m�ru.
	 * @param currentAvgValues
	 *          Funk�n� hodnoty aktu�ln�ho pr�m�ru.
	 */
	public EpochDataSet(int channelOrderInInputFile, boolean checked,
			int trustful, float[] currentEpochValues,
			float[] avgWithCurrentEpochValues, float[] avgWithoutCurrentEpochValues,
			float[] currentAvgValues) {
		this.channelOrderInInputFile = channelOrderInInputFile;
		this.checked = checked;
		this.currentEpochValues = currentEpochValues;
		this.avgWithCurrentEpochValues = avgWithCurrentEpochValues;
		this.avgWithoutCurrentEpochValues = avgWithoutCurrentEpochValues;
		this.currentAvgValues = currentAvgValues;
		this.trustful = trustful;
	}

	/**
	 * Soukrom� konstruktor pro vytvo�en� instance p�i klonov�n�.
	 */
	private EpochDataSet() {
	}

	/**
	 * Vrac� hodnotu atributu <code>checked</code>.
	 * 
	 * @return <code>true</code>, pokud je epocha pro tento kan�l za�azena do
	 *         pr�m�ru, jinak <code>false</code>.
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * Nastavuje hodnotu atributu <code>checked</code>.
	 * 
	 * @param checked
	 *          <code>true</code>, pokud je epocha pro tento kan�l za�azena do
	 *          pr�m�ru, jinak <code>false</code>.
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * Vrac� hodnotu atributu <code>channelOrderInInputFile</code>.
	 * 
	 * @return Po�ad� kan�lu, ke kter�mu instance t��dy pat��, ve vstupn�m
	 *         souboru.
	 */
	public int getChannelOrderInInputFile() {
		return channelOrderInInputFile;
	}

	/**
	 * Nastavuje hodnotu atributu <code>channelOrderInInputFile</code>.
	 * 
	 * @param channelOrderInInputFile
	 *          Po�ad� kan�lu, ke kter�mu instance t��dy pat��, ve vstupn�m
	 *          souboru.
	 */
	public void setChannelOrderInInputFile(int channelOrderInInputFile) {
		this.channelOrderInInputFile = channelOrderInInputFile;
	}

	/**
	 * Vrac� hodnotu atributu <code>avgWithCurrentEpochValues</code>.
	 * 
	 * @return Funk�n� hodnoty celkov�ho pr�m�ru sign�lu po zahrnut� aktu�ln�
	 *         epochy do pr�m�ru.
	 */
	public float[] getAvgWithCurrentEpochValues() {
		return avgWithCurrentEpochValues;
	}

	/**
	 * Nastavuje hodnotu atributu <code>avgWithCurrentEpochValues</code>.
	 * 
	 * @param avgWithCurrentEpochValues
	 *          Funk�n� hodnoty celkov�ho pr�m�ru sign�lu po zahrnut� aktu�ln�
	 *          epochy do pr�m�ru.
	 */
	public void setAvgWithCurrentEpochValues(float[] avgWithCurrentEpochValues) {
		this.avgWithCurrentEpochValues = avgWithCurrentEpochValues;
	}

	/**
	 * Vrac� hodnotu atributu <code>avgWithoutCurrentEpochValues</code>.
	 * 
	 * @return Funk�n� hodnoty celkov�ho pr�m�ru sign�lu bez zahrnut� aktu�ln�
	 *         epochy do pr�m�ru.
	 */
	public float[] getAvgWithoutCurrentEpochValues() {
		return avgWithoutCurrentEpochValues;
	}

	/**
	 * Nastavuje hodnotu atributu <code>avgWithoutCurrentEpochValues</code>.
	 * 
	 * @param avgWithoutCurrentEpochValues
	 *          Funk�n� hodnoty celkov�ho pr�m�ru sign�lu bez zahrnut� aktu�ln�
	 *          epochy do pr�m�ru.
	 */
	public void setAvgWithoutCurrentEpochValues(
			float[] avgWithoutCurrentEpochValues) {
		this.avgWithoutCurrentEpochValues = avgWithoutCurrentEpochValues;
	}

	/**
	 * Vrac� hodnotu atributu <code>currentEpochValues</code>.
	 * 
	 * @return Funk�n� hodnoty pr�b�hu sign�lu v aktu�ln� epo�e.
	 */
	public float[] getCurrentEpochValues() {
		return currentEpochValues;
	}

	/**
	 * Nastavuje hodnotu atributu <code>currentEpochValues</code>.
	 * 
	 * @param currentEpochValues
	 *          Funk�n� hodnoty pr�b�hu sign�lu v aktu�ln� epo�e.
	 */
	public void setCurrentEpochValues(float[] currentEpochValues) {
		this.currentEpochValues = currentEpochValues;
	}

	/**
	 * Vrac� hodnotu atributu <code>currentEpochValues</code>.
	 * 
	 * @return Funk�n� hodnoty pr�b�hu sign�lu v aktu�ln� epo�e.
	 */
	public float[] getCurrentAvgValues() {
		return currentAvgValues;
	}

	/**
	 * Nastavuje hodnotu atributu <code>currentEpochValues</code>.
	 * 
	 * @param currentAvgValues
	 *          Funk�n� hodnoty pr�b�hu sign�lu v aktu�ln� epo�e.
	 */
	public void setCurrentAvgValues(float[] currentAvgValues) {
		this.currentAvgValues = currentAvgValues;
	}

	/**
	 * Vytvo�� hlubokou kopii objektu.
	 * 
	 * @return Hlubok� kopie objektu.
	 */
	@Override
	public EpochDataSet clone() {
		EpochDataSet newEpochDataSet = new EpochDataSet();

		newEpochDataSet.avgWithCurrentEpochValues = avgWithCurrentEpochValues
				.clone();

		newEpochDataSet.avgWithoutCurrentEpochValues = avgWithoutCurrentEpochValues
				.clone();

		newEpochDataSet.currentEpochValues = currentEpochValues;

		newEpochDataSet.checked = checked;

		newEpochDataSet.channelOrderInInputFile = channelOrderInInputFile;

		return newEpochDataSet;
	}

	/**
	 * Vrac� hodnotu atributu <code>trustful</code>.
	 * 
	 * @return D�veryhodnot pr�m�ru (viz dokumenta�n� koment�� atributu
	 *         <code>trustful</code>.
	 */
	public int getTrustful() {
		return trustful;
	}

	/**
	 * Nastavuje hodnotu atributu <code>trustful</code>.
	 * 
	 * @param trustful
	 *          D�veryhodnot pr�m�ru (viz dokumenta�n� koment�� atributu
	 *          <code>trustful</code>.
	 */
	public void setTrustful(int trustful) {
		this.trustful = trustful;
	}
}
