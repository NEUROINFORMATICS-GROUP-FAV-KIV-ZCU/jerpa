package ch.ethz.origo.jerpa.data.filters.dwt;

import java.util.Arrays;

import ch.ethz.origo.jerpa.data.filters.IAlgorithmDescriptor;

import noname.Power2Utils;

/**
 * T��da pro rychlou waveletovu transformaci pomoc� Daubechies8 wavelet� (d�lka
 * 16) a mate�sk�ch koeficient�. Po transformaci vznikne pole stejn� d�lky
 * p�vodn�ho sign�lu, proto�e se podvozorkov�v�.
 * 
 * @author Petr Soukal (class author)
 * @author Vaclav Souhrada (class revision)
 * @version 1.0.0 (11/29/09)
 * @since JERPA Version 0.1.0
 * @see IAlgorithmDescriptor
 */
public class FastDaubechies8 implements IAlgorithmDescriptor {
	
	/** Author of this filter */
	private static final String AUTHOR = "Petr Soukal";
	/** Current version of this filter */
	private static final String VERSION = "1.0.0";
	/** Filter's name */
	private static final String NAME = "Fast Daubechies8 WT";
	
	private static final String CATEGORY = "Filter";
	
	// mate�sk� koeficienty
	private static final double[] scale = { 5.44158422430000010E-02,
			3.12871590914000020E-01, 6.75630736296999990E-01,
			5.85354683654000010E-01, -1.58291052559999990E-02,
			-2.84015542961999990E-01, 4.72484573999999990E-04,
			1.28747426619999990E-01, -1.73693010020000010E-02,
			-4.40882539310000000E-02, 1.39810279170000000E-02,
			8.74609404700000050E-03, -4.87035299299999960E-03,
			-3.91740372999999990E-04, 6.75449405999999950E-04,
			-1.17476784000000000E-04 };

	// koeficienty waveletu
	private static final double[] wavelet = { -scale[15], scale[14], -scale[13],
			scale[12], -scale[11], scale[10], -scale[9], scale[8], -scale[7],
			scale[6], -scale[5], scale[4], -scale[3], scale[2], -scale[1], scale[0] };

	// koeficienty waveletu
	private static final double[] iScale = { scale[14], wavelet[14], scale[12],
			wavelet[12], scale[10], wavelet[10], scale[8], wavelet[8], scale[6],
			wavelet[6], scale[4], wavelet[4], scale[2], wavelet[2], scale[0],
			wavelet[0] };

	// koeficienty waveletu
	private static final double[] iWavelet = { scale[15], wavelet[15], scale[13],
			wavelet[13], scale[11], wavelet[11], scale[9], wavelet[9], scale[7],
			wavelet[7], scale[5], wavelet[5], scale[3], wavelet[3], scale[1],
			wavelet[1] };

	/**
	 * Metoda prodlu�uje vstupn� sign�l na d�lku (2^n) pokud v takovou d�lku nem�
	 * a nov� m�sto se vypln� nulami. Pokud m� sign�l d�lku 16 nebo v�t��, tak
	 * spou�t� transformaci. Kone�n� �rove� transformace je odvozena od d�lky
	 * sign�lu.
	 * 
	 * @param inputSignal
	 *          - origin�ln� vstupn� sign�l.
	 * @return sign�l po transformaci.
	 */
	public static double[] transform(double[] inputSignal) {
		double[] signal;

		int newSignalLength = Power2Utils.newNumberOfPowerBase2(inputSignal.length);

		if (newSignalLength != inputSignal.length) {
			signal = Arrays.copyOf(inputSignal, newSignalLength);
			Arrays.fill(signal, inputSignal.length, signal.length, Power2Utils.ZERO);
		} else
			signal = inputSignal.clone();

		for (int last = signal.length; last >= wavelet.length; last /= Power2Utils.CONST_2)
			transform(signal, last);

		return signal;
	}

	/**
	 * Metoda transformuj�c� sign�l pomoc� nastaven�ch wavelet� a mate�sk�ch
	 * koeficient�. V prvn� polovin� �seku transformovan�ho sign�lu je ukl�d�n�
	 * aproxima�n� slo�ka z�skan� mate�sk�mi koeficienty a v druh� polovin� je
	 * ukl�d�na detailn� slo�ka z�skan� pomoc� waveletov�mi koeficienty.
	 * 
	 * 
	 * @param signal
	 *          - transformovan� sign�l.
	 * @param last
	 *          - d�lka transformovan�ho �seku sign�lu.
	 */
	private static void transform(double[] signal, int last) {
		int half = last / Power2Utils.CONST_2;
		double tmp[] = new double[last];
		int i = 0;

		for (int j = 0; j < last - 15; j = j + Power2Utils.CONST_2) {
			tmp[i] = signal[j] * scale[0] + signal[j + 1] * scale[1] + signal[j + 2]
					* scale[2] + signal[j + 3] * scale[3] + signal[j + 4] * scale[4]
					+ signal[j + 5] * scale[5] + signal[j + 6] * scale[6] + signal[j + 7]
					* scale[7] + signal[j + 8] * scale[8] + signal[j + 9] * scale[9]
					+ signal[j + 10] * scale[10] + signal[j + 11] * scale[11]
					+ signal[j + 12] * scale[12] + signal[j + 13] * scale[13]
					+ signal[j + 14] * scale[14] + signal[j + 15] * scale[15];

			tmp[i + half] = signal[j] * wavelet[0] + signal[j + 1] * wavelet[1]
					+ signal[j + 2] * wavelet[2] + signal[j + 3] * wavelet[3]
					+ signal[j + 4] * wavelet[4] + signal[j + 5] * wavelet[5]
					+ signal[j + 6] * wavelet[6] + signal[j + 7] * wavelet[7]
					+ signal[j + 8] * wavelet[8] + signal[j + 9] * wavelet[9]
					+ signal[j + 10] * wavelet[10] + signal[j + 11] * wavelet[11]
					+ signal[j + 12] * wavelet[12] + signal[j + 13] * wavelet[13]
					+ signal[j + 14] * wavelet[14] + signal[j + 15] * wavelet[15];
			i++;
		}

		tmp[i] = signal[last - 14] * scale[0] + signal[last - 13] * scale[1]
				+ signal[last - 12] * scale[2] + signal[last - 11] * scale[3]
				+ signal[last - 10] * scale[4] + signal[last - 9] * scale[5]
				+ signal[last - 8] * scale[6] + signal[last - 7] * scale[7]
				+ signal[last - 6] * scale[8] + signal[last - 5] * scale[9]
				+ signal[last - 4] * scale[10] + signal[last - 3] * scale[11]
				+ signal[last - 2] * scale[12] + signal[last - 1] * scale[13]
				+ signal[0] * scale[14] + signal[1] * scale[15];

		tmp[i + half] = signal[last - 14] * wavelet[0] + signal[last - 13]
				* wavelet[1] + signal[last - 12] * wavelet[2] + signal[last - 11]
				* wavelet[3] + signal[last - 10] * wavelet[4] + signal[last - 9]
				* wavelet[5] + signal[last - 8] * wavelet[6] + signal[last - 7]
				* wavelet[7] + signal[last - 6] * wavelet[8] + signal[last - 5]
				* wavelet[9] + signal[last - 4] * wavelet[10] + signal[last - 3]
				* wavelet[11] + signal[last - 2] * wavelet[12] + signal[last - 1]
				* wavelet[13] + signal[0] * wavelet[14] + signal[1] * wavelet[15];

		i++;

		// --------------------------------------------------------------------------------
		tmp[i] = signal[last - 12] * scale[0] + signal[last - 11] * scale[1]
				+ signal[last - 10] * scale[2] + signal[last - 9] * scale[3]
				+ signal[last - 8] * scale[4] + signal[last - 7] * scale[5]
				+ signal[last - 6] * scale[6] + signal[last - 5] * scale[7]
				+ signal[last - 4] * scale[8] + signal[last - 3] * scale[9]
				+ signal[last - 2] * scale[10] + signal[last - 1] * scale[11]
				+ signal[0] * scale[12] + signal[1] * scale[13] + signal[2] * scale[14]
				+ signal[3] * scale[15];

		tmp[i + half] = signal[last - 12] * wavelet[0] + signal[last - 11]
				* wavelet[1] + signal[last - 10] * wavelet[2] + signal[last - 9]
				* wavelet[3] + signal[last - 8] * wavelet[4] + signal[last - 7]
				* wavelet[5] + signal[last - 6] * wavelet[6] + signal[last - 5]
				* wavelet[7] + signal[last - 4] * wavelet[8] + signal[last - 3]
				* wavelet[9] + signal[last - 2] * wavelet[10] + signal[last - 1]
				* wavelet[11] + signal[0] * wavelet[12] + signal[1] * wavelet[13]
				+ signal[2] * wavelet[14] + signal[3] * wavelet[15];

		i++;

		// --------------------------------------------------------------------------------
		tmp[i] = signal[last - 10] * scale[0] + signal[last - 9] * scale[1]
				+ signal[last - 8] * scale[2] + signal[last - 7] * scale[3]
				+ signal[last - 6] * scale[4] + signal[last - 5] * scale[5]
				+ signal[last - 4] * scale[6] + signal[last - 3] * scale[7]
				+ signal[last - 2] * scale[8] + signal[last - 1] * scale[9] + signal[0]
				* scale[10] + signal[1] * scale[11] + signal[2] * scale[12] + signal[3]
				* scale[13] + signal[4] * scale[14] + signal[5] * scale[15];

		tmp[i + half] = signal[last - 10] * wavelet[0] + signal[last - 9]
				* wavelet[1] + signal[last - 8] * wavelet[2] + signal[last - 7]
				* wavelet[3] + signal[last - 6] * wavelet[4] + signal[last - 5]
				* wavelet[5] + signal[last - 4] * wavelet[6] + signal[last - 3]
				* wavelet[7] + signal[last - 2] * wavelet[8] + signal[last - 1]
				* wavelet[9] + signal[0] * wavelet[10] + signal[1] * wavelet[11]
				+ signal[2] * wavelet[12] + signal[3] * wavelet[13] + signal[4]
				* wavelet[14] + signal[5] * wavelet[15];

		i++;

		// --------------------------------------------------------------------------------
		tmp[i] = signal[last - 8] * scale[0] + signal[last - 7] * scale[1]
				+ signal[last - 6] * scale[2] + signal[last - 5] * scale[3]
				+ signal[last - 4] * scale[4] + signal[last - 3] * scale[5]
				+ signal[last - 2] * scale[6] + signal[last - 1] * scale[7] + signal[0]
				* scale[8] + signal[1] * scale[9] + signal[2] * scale[10] + signal[3]
				* scale[11] + signal[4] * scale[12] + signal[5] * scale[13] + signal[6]
				* scale[14] + signal[7] * scale[15];

		tmp[i + half] = signal[last - 8] * wavelet[0] + signal[last - 7]
				* wavelet[1] + signal[last - 6] * wavelet[2] + signal[last - 5]
				* wavelet[3] + signal[last - 4] * wavelet[4] + signal[last - 3]
				* wavelet[5] + signal[last - 2] * wavelet[6] + signal[last - 1]
				* wavelet[7] + signal[0] * wavelet[8] + signal[1] * wavelet[9]
				+ signal[2] * wavelet[10] + signal[3] * wavelet[11] + signal[4]
				* wavelet[12] + signal[5] * wavelet[13] + signal[6] * wavelet[14]
				+ signal[7] * wavelet[15];

		i++;

		// --------------------------------------------------------------------------------
		tmp[i] = signal[last - 6] * scale[0] + signal[last - 5] * scale[1]
				+ signal[last - 4] * scale[2] + signal[last - 3] * scale[3]
				+ signal[last - 2] * scale[4] + signal[last - 1] * scale[5] + signal[0]
				* scale[6] + signal[1] * scale[7] + signal[2] * scale[8] + signal[3]
				* scale[9] + signal[4] * scale[10] + signal[5] * scale[11] + signal[6]
				* scale[12] + signal[7] * scale[13] + signal[8] * scale[14] + signal[9]
				* scale[15];

		tmp[i + half] = signal[last - 6] * wavelet[0] + signal[last - 5]
				* wavelet[1] + signal[last - 4] * wavelet[2] + signal[last - 3]
				* wavelet[3] + signal[last - 2] * wavelet[4] + signal[last - 1]
				* wavelet[5] + signal[0] * wavelet[6] + signal[1] * wavelet[7]
				+ signal[2] * wavelet[8] + signal[3] * wavelet[9] + signal[4]
				* wavelet[10] + signal[5] * wavelet[11] + signal[6] * wavelet[12]
				+ signal[7] * wavelet[13] + signal[8] * wavelet[14] + signal[9]
				* wavelet[15];

		i++;

		// --------------------------------------------------------------------------------
		tmp[i] = signal[last - 4] * scale[0] + signal[last - 3] * scale[1]
				+ signal[last - 2] * scale[2] + signal[last - 1] * scale[3] + signal[0]
				* scale[4] + signal[1] * scale[5] + signal[2] * scale[6] + signal[3]
				* scale[7] + signal[4] * scale[8] + signal[5] * scale[9] + signal[6]
				* scale[10] + signal[7] * scale[11] + signal[8] * scale[12] + signal[9]
				* scale[13] + signal[10] * scale[14] + signal[11] * scale[15];

		tmp[i + half] = signal[last - 4] * wavelet[0] + signal[last - 3]
				* wavelet[1] + signal[last - 2] * wavelet[2] + signal[last - 1]
				* wavelet[3] + signal[0] * wavelet[4] + signal[1] * wavelet[5]
				+ signal[2] * wavelet[6] + signal[3] * wavelet[7] + signal[4]
				* wavelet[8] + signal[5] * wavelet[9] + signal[6] * wavelet[10]
				+ signal[7] * wavelet[11] + signal[8] * wavelet[12] + signal[9]
				* wavelet[13] + signal[10] * wavelet[14] + signal[11] * wavelet[15];

		i++;

		// --------------------------------------------------------------------------------
		tmp[i] = signal[last - 2] * scale[0] + signal[last - 1] * scale[1]
				+ signal[0] * scale[2] + signal[1] * scale[3] + signal[2] * scale[4]
				+ signal[3] * scale[5] + signal[4] * scale[6] + signal[5] * scale[7]
				+ signal[6] * scale[8] + signal[7] * scale[9] + signal[8] * scale[10]
				+ signal[9] * scale[11] + signal[10] * scale[12] + signal[11]
				* scale[13] + signal[12] * scale[14] + signal[13] * scale[15];

		tmp[i + half] = signal[last - 2] * wavelet[0] + signal[last - 1]
				* wavelet[1] + signal[0] * wavelet[2] + signal[1] * wavelet[3]
				+ signal[2] * wavelet[4] + signal[3] * wavelet[5] + signal[4]
				* wavelet[6] + signal[5] * wavelet[7] + signal[6] * wavelet[8]
				+ signal[7] * wavelet[9] + signal[8] * wavelet[10] + signal[9]
				* wavelet[11] + signal[10] * wavelet[12] + signal[11] * wavelet[13]
				+ signal[12] * wavelet[14] + signal[13] * wavelet[15];

		for (i = 0; i < last; i++) {
			signal[i] = tmp[i];
		}
	}

	/**
	 * Metoda prodlu�uje vstupn� sign�l na d�lku (2^n) pokud v takovou d�lku nem�
	 * a nov� m�sto se vypln� nulami. Pokud m� sign�l d�lku 16 nebo v�t�� spou�t�
	 * inverzn� transformaci.
	 * 
	 * @param inputCoef
	 *          - transformovan� vstupn� sign�l.
	 * @return sign�l po inverzn� transformaci.
	 */
	public static double[] invTransform(double[] inputCoef) {

		double[] coeficients;
		int newSignalLength = Power2Utils.newNumberOfPowerBase2(inputCoef.length);

		if (newSignalLength != inputCoef.length) {
			coeficients = Arrays.copyOf(inputCoef, newSignalLength);
			Arrays.fill(coeficients, inputCoef.length, coeficients.length,
					Power2Utils.ZERO);
		} else
			coeficients = inputCoef.clone();

		for (int last = wavelet.length; last <= coeficients.length; last *= Power2Utils.CONST_2)
			invTransform(coeficients, last);

		return coeficients;
	}

	/**
	 * Metoda transformuj�c� sign�l zp�tky na origin�ln� pomoc� nastaven�ch
	 * wavelet� a mate�sk�ch koeficient�. Skl�d� p�vodn� sign�l z aproxima�n�ch a
	 * detailn�ch slo�ek transformovan�ho sign�lu.
	 * 
	 * @param coef
	 *          - transformovan� sign�l.
	 * @param last
	 *          - d�lka transformovan�ho �seku sign�lu.
	 */
	private static void invTransform(double[] coef, int last) {
		int half = last / Power2Utils.CONST_2;
		int halfPls1 = half + 1;
		int halfPls2 = half + 2;
		int halfPls3 = half + 3;
		int halfPls4 = half + 4;
		int halfPls5 = half + 5;
		int halfPls6 = half + 6;
		int halfPls7 = half + 7;
		int j = 0;

		double tmp[] = new double[last];

		tmp[j++] = coef[half - 1] * iScale[12] + coef[last - 1] * iScale[13]
				+ coef[0] * iScale[14] + coef[half] * iScale[15] + coef[half - 7]
				* iScale[0] + coef[last - 7] * iScale[1] + coef[half - 6] * iScale[2]
				+ coef[last - 6] * iScale[3] + coef[half - 5] * iScale[4]
				+ coef[last - 5] * iScale[5] + coef[half - 4] * iScale[6]
				+ coef[last - 4] * iScale[7] + coef[half - 3] * iScale[8]
				+ coef[last - 3] * iScale[9] + coef[half - 2] * iScale[10]
				+ coef[last - 2] * iScale[11];

		tmp[j++] = coef[half - 1] * iWavelet[12] + coef[last - 1] * iWavelet[13]
				+ coef[0] * iWavelet[14] + coef[half] * iWavelet[15] + coef[half - 7]
				* iWavelet[0] + coef[last - 7] * iWavelet[1] + coef[half - 6]
				* iWavelet[2] + coef[last - 6] * iWavelet[3] + coef[half - 5]
				* iWavelet[4] + coef[last - 5] * iWavelet[5] + coef[half - 4]
				* iWavelet[6] + coef[last - 4] * iWavelet[7] + coef[half - 3]
				* iWavelet[8] + coef[last - 3] * iWavelet[9] + coef[half - 2]
				* iWavelet[10] + coef[last - 2] * iWavelet[11];
		// -----------------------------------------------------------------------------

		tmp[j++] = coef[0] * iScale[12] + coef[half] * iScale[13] + coef[1]
				* iScale[14] + coef[halfPls1] * iScale[15] + coef[half - 6] * iScale[0]
				+ coef[last - 6] * iScale[1] + coef[half - 5] * iScale[2]
				+ coef[last - 5] * iScale[3] + coef[half - 4] * iScale[4]
				+ coef[last - 4] * iScale[5] + coef[half - 3] * iScale[6]
				+ coef[last - 3] * iScale[7] + coef[half - 2] * iScale[8]
				+ coef[last - 2] * iScale[9] + coef[half - 1] * iScale[10]
				+ coef[last - 1] * iScale[11];

		tmp[j++] = coef[0] * iWavelet[12] + coef[half] * iWavelet[13] + coef[1]
				* iWavelet[14] + coef[halfPls1] * iWavelet[15] + coef[half - 6]
				* iWavelet[0] + coef[last - 6] * iWavelet[1] + coef[half - 5]
				* iWavelet[2] + coef[last - 5] * iWavelet[3] + coef[half - 4]
				* iWavelet[4] + coef[last - 4] * iWavelet[5] + coef[half - 3]
				* iWavelet[6] + coef[last - 3] * iWavelet[7] + coef[half - 2]
				* iWavelet[8] + coef[last - 2] * iWavelet[9] + coef[half - 1]
				* iWavelet[10] + coef[last - 1] * iWavelet[11];
		// -----------------------------------------------------------------------------

		tmp[j++] = coef[1] * iScale[12] + coef[halfPls1] * iScale[13] + coef[2]
				* iScale[14] + coef[halfPls2] * iScale[15] + coef[half - 5] * iScale[0]
				+ coef[last - 5] * iScale[1] + coef[half - 4] * iScale[2]
				+ coef[last - 4] * iScale[3] + coef[half - 3] * iScale[4]
				+ coef[last - 3] * iScale[5] + coef[half - 2] * iScale[6]
				+ coef[last - 2] * iScale[7] + coef[half - 1] * iScale[8]
				+ coef[last - 1] * iScale[9] + coef[0] * iScale[10] + coef[half]
				* iScale[11];

		tmp[j++] = coef[1] * iWavelet[12] + coef[halfPls1] * iWavelet[13] + coef[2]
				* iWavelet[14] + coef[halfPls2] * iWavelet[15] + coef[half - 5]
				* iWavelet[0] + coef[last - 5] * iWavelet[1] + coef[half - 4]
				* iWavelet[2] + coef[last - 4] * iWavelet[3] + coef[half - 3]
				* iWavelet[4] + coef[last - 3] * iWavelet[5] + coef[half - 2]
				* iWavelet[6] + coef[last - 2] * iWavelet[7] + coef[half - 1]
				* iWavelet[8] + coef[last - 1] * iWavelet[9] + coef[0] * iWavelet[10]
				+ coef[half] * iWavelet[11];
		// -----------------------------------------------------------------------------

		tmp[j++] = coef[2] * iScale[12] + coef[halfPls2] * iScale[13] + coef[3]
				* iScale[14] + coef[halfPls3] * iScale[15] + coef[half - 4] * iScale[0]
				+ coef[last - 4] * iScale[1] + coef[half - 3] * iScale[2]
				+ coef[last - 3] * iScale[3] + coef[half - 2] * iScale[4]
				+ coef[last - 2] * iScale[5] + coef[half - 1] * iScale[6]
				+ coef[last - 1] * iScale[7] + coef[0] * iScale[8] + coef[half]
				* iScale[9] + coef[1] * iScale[10] + coef[halfPls1] * iScale[11];

		tmp[j++] = coef[2] * iWavelet[12] + coef[halfPls2] * iWavelet[13] + coef[3]
				* iWavelet[14] + coef[halfPls3] * iWavelet[15] + coef[half - 4]
				* iWavelet[0] + coef[last - 4] * iWavelet[1] + coef[half - 3]
				* iWavelet[2] + coef[last - 3] * iWavelet[3] + coef[half - 2]
				* iWavelet[4] + coef[last - 2] * iWavelet[5] + coef[half - 1]
				* iWavelet[6] + coef[last - 1] * iWavelet[7] + coef[0] * iWavelet[8]
				+ coef[half] * iWavelet[9] + coef[1] * iWavelet[10] + coef[halfPls1]
				* iWavelet[11];
		// -----------------------------------------------------------------------------

		tmp[j++] = coef[3] * iScale[12] + coef[halfPls3] * iScale[13] + coef[4]
				* iScale[14] + coef[halfPls4] * iScale[15] + coef[half - 3] * iScale[0]
				+ coef[last - 3] * iScale[1] + coef[half - 2] * iScale[2]
				+ coef[last - 2] * iScale[3] + coef[half - 1] * iScale[4]
				+ coef[last - 1] * iScale[5] + coef[0] * iScale[6] + coef[half]
				* iScale[7] + coef[1] * iScale[8] + coef[halfPls1] * iScale[9]
				+ coef[2] * iScale[10] + coef[halfPls2] * iScale[11];

		tmp[j++] = coef[3] * iWavelet[12] + coef[halfPls3] * iWavelet[13] + coef[4]
				* iWavelet[14] + coef[halfPls4] * iWavelet[15] + coef[half - 3]
				* iWavelet[0] + coef[last - 3] * iWavelet[1] + coef[half - 2]
				* iWavelet[2] + coef[last - 2] * iWavelet[3] + coef[half - 1]
				* iWavelet[4] + coef[last - 1] * iWavelet[5] + coef[0] * iWavelet[6]
				+ coef[half] * iWavelet[7] + coef[1] * iWavelet[8] + coef[halfPls1]
				* iWavelet[9] + coef[2] * iWavelet[10] + coef[halfPls2] * iWavelet[11];
		// -----------------------------------------------------------------------------

		tmp[j++] = coef[4] * iScale[12] + coef[halfPls4] * iScale[13] + coef[5]
				* iScale[14] + coef[halfPls5] * iScale[15] + coef[half - 2] * iScale[0]
				+ coef[last - 2] * iScale[1] + coef[half - 1] * iScale[2]
				+ coef[last - 1] * iScale[3] + coef[0] * iScale[4] + coef[half]
				* iScale[5] + coef[1] * iScale[6] + coef[halfPls1] * iScale[7]
				+ coef[2] * iScale[8] + coef[halfPls2] * iScale[9] + coef[3]
				* iScale[10] + coef[halfPls3] * iScale[11];

		tmp[j++] = coef[4] * iWavelet[12] + coef[halfPls4] * iWavelet[13] + coef[5]
				* iWavelet[14] + coef[halfPls5] * iWavelet[15] + coef[half - 2]
				* iWavelet[0] + coef[last - 2] * iWavelet[1] + coef[half - 1]
				* iWavelet[2] + coef[last - 1] * iWavelet[3] + coef[0] * iWavelet[4]
				+ coef[half] * iWavelet[5] + coef[1] * iWavelet[6] + coef[halfPls1]
				* iWavelet[7] + coef[2] * iWavelet[8] + coef[halfPls2] * iWavelet[9]
				+ coef[3] * iWavelet[10] + coef[halfPls3] * iWavelet[11];
		// -----------------------------------------------------------------------------

		tmp[j++] = coef[5] * iScale[12] + coef[halfPls5] * iScale[13] + coef[6]
				* iScale[14] + coef[halfPls6] * iScale[15] + coef[half - 1] * iScale[0]
				+ coef[last - 1] * iScale[1] + coef[0] * iScale[2] + coef[half]
				* iScale[3] + coef[1] * iScale[4] + coef[halfPls1] * iScale[5]
				+ coef[2] * iScale[6] + coef[halfPls2] * iScale[7] + coef[3]
				* iScale[8] + coef[halfPls3] * iScale[9] + coef[4] * iScale[10]
				+ coef[halfPls4] * iScale[11];

		tmp[j++] = coef[5] * iWavelet[12] + coef[halfPls5] * iWavelet[13] + coef[6]
				* iWavelet[14] + coef[halfPls6] * iWavelet[15] + coef[half - 1]
				* iWavelet[0] + coef[last - 1] * iWavelet[1] + coef[0] * iWavelet[2]
				+ coef[half] * iWavelet[3] + coef[1] * iWavelet[4] + coef[halfPls1]
				* iWavelet[5] + coef[2] * iWavelet[6] + coef[halfPls2] * iWavelet[7]
				+ coef[3] * iWavelet[8] + coef[halfPls3] * iWavelet[9] + coef[4]
				* iWavelet[10] + coef[halfPls4] * iWavelet[11];
		// -----------------------------------------------------------------------------

		for (int i = 0; i < half - 7; i++) {
			tmp[j++] = coef[i] * iScale[0] + coef[i + half] * iScale[1] + coef[i + 1]
					* iScale[2] + coef[i + halfPls1] * iScale[3] + coef[i + 2]
					* iScale[4] + coef[i + halfPls2] * iScale[5] + coef[i + 3]
					* iScale[6] + coef[i + halfPls3] * iScale[7] + coef[i + 4]
					* iScale[8] + coef[i + halfPls4] * iScale[9] + coef[i + 5]
					* iScale[10] + coef[i + halfPls5] * iScale[11] + coef[i + 6]
					* iScale[12] + coef[i + halfPls6] * iScale[13] + coef[i + 7]
					* iScale[14] + coef[i + halfPls7] * iScale[15];

			tmp[j++] = coef[i] * iWavelet[0] + coef[i + half] * iWavelet[1]
					+ coef[i + 1] * iWavelet[2] + coef[i + halfPls1] * iWavelet[3]
					+ coef[i + 2] * iWavelet[4] + coef[i + halfPls2] * iWavelet[5]
					+ coef[i + 3] * iWavelet[6] + coef[i + halfPls3] * iWavelet[7]
					+ coef[i + 4] * iWavelet[8] + coef[i + halfPls4] * iWavelet[9]
					+ coef[i + 5] * iWavelet[10] + coef[i + halfPls5] * iWavelet[11]
					+ coef[i + 6] * iWavelet[12] + coef[i + halfPls6] * iWavelet[13]
					+ coef[i + 7] * iWavelet[14] + coef[i + halfPls7] * iWavelet[15];
		}

		for (int i = 0; i < last; i++) {
			coef[i] = tmp[i];
		}
	}

	@Override
	public String getAuthorName() {
		return FastDaubechies8.AUTHOR;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return FastDaubechies8.NAME;
	}

	@Override
	public String getVersion() {
		return FastDaubechies8.VERSION;
	}

	@Override
	public String getCategory() {
		return FastDaubechies8.CATEGORY;
	}
}
