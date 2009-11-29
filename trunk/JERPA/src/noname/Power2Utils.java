package noname;

/**
 * T��da pro matematick� operace.
 * 
 * @author Petr Soukal
 * @author Vaclav Souhrada
 * @version 1.0.0 (11/29/09) 
 * @since 0.1.0 (11/29/09)
 */
public class Power2Utils {
	// konstanty
	public final static int CONST_2 = 2;
	public final static int ZERO = 0;

	/**
	 * Metoda vypo��t�v� logaritmus o z�kladu 2 z vlo�en�ho ��sla.
	 * 
	 * @param x
	 *          - ��slo ze kter�ho se po��t� logaritmus o z�kladu 2.
	 * @return log2 z x.
	 */
	public static double log2(int x) {
		return (Math.log(x) / Math.log(CONST_2));
	}

	/**
	 * Pokud neni vlo�en� ��slo mocninou z�kladu 2, tak vr�t� prvn� v�t�� ��slo,
	 * kter� je mocninou z�kladu 2.
	 * 
	 * @param x
	 *          - ��slo, u kter�ho se zji��uje zda je z�kladu 2.
	 * @return ��slo x nebo prvn� v�t�� ��slo, kter� je mocninou z�kladu 2.
	 */
	public static int newNumberOfPowerBase2(int x) {
		double number = log2(x);
		int temp = (int) number;

		if (number % temp == 0)
			return x;
		else {
			temp += 1;
			int newNumber = (int) Math.pow(CONST_2, temp);
			return newNumber;
		}
	}
}
