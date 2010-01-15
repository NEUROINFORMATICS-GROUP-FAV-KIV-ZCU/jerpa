package ch.ethz.origo.jerpa.data.filters.dwt;

import java.util.Arrays;

import ch.ethz.origo.jerpa.data.filters.IFilter;

import noname.Power2Utils;

/**
 * T��da pro rychlou waveletovu transformaci pomoc� Daubechies2 wavelet� (d�lka
 * 4) a mate�sk�ch koeficient�. Po transformaci vznikne pole stejn� d�lky
 * p�vodn�ho sign�lu, proto�e se podvozorkov�v�.
 * 
 * @author Petr Soukal (class author)
 * @author Vaclav Souhrada (class revision)
 * @version 1.0.0 (11/29/09)
 * @since JERPA Version 0.1.0
 * @see IFilter
 */
public class FastDaubechies2 implements IFilter {
	
	/** Author of this filter */
	private static final String AUTHOR = "Petr Soukal";
	/** Current version of this filter */
	private static final String VERSION = "1.0.0";
	/** Name of filter */
	private static final String NAME = "Fast Daubechies2 WT";
	
	private static final double sqrt_3 = Math.sqrt(3);
	private static final double denom = 4 * Math.sqrt(2);

	// mate�sk� koeficienty
	private static final double[] scale = { (1 + sqrt_3) / denom,
			(3 + sqrt_3) / denom, (3 - sqrt_3) / denom, (1 - sqrt_3) / denom };

	// wavelet koeficienty
	private static final double[] wavelet = { -scale[3], scale[2], -scale[1],
			scale[0] };

	// mate�sk� koeficienty pro inverzn� trnsformaci
	private static final double[] iScale = { scale[2], wavelet[2], scale[0],
			wavelet[0] };

	// wavelet koeficienty pro inverzn� trnsformaci
	private static final double[] iWavelet = { scale[3], wavelet[3], scale[1],
			wavelet[1] };

	/**
	 * Metoda prodlu�uje vstupn� sign�l na d�lku (2^n) pokud v takovou d�lku nem�
	 * a nov� m�sto se vypln� nulami. Pokud m� sign�l d�lku 4 nebo v�t��, tak
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

		for (int last = signal.length; last >= wavelet.length; last /= Power2Utils.CONST_2) {
			transform(signal, last);
		}

		return signal;
	}

	/**
	 * Metoda prodlu�uje vstupn� coeficienty na d�lku (2^n) pokud v takovou d�lku
	 * nem� a nov� m�sto se vypln� nulami. Pokud m� pole koeficient� d�lku 4 nebo
	 * v�t�� spou�t� inverzn� transformaci.
	 * 
	 * @param inputCoef
	 *          - transformovan� vstupn� koeficienty.
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

		for (int last = wavelet.length; last <= coeficients.length; last *= Power2Utils.CONST_2) {
			invTransform(coeficients, last);
		}

		return coeficients;
	}

	/**
	 * Metoda transformuj�c� sign�l pomoc� nastaven�ch wavelet� a mate�sk�ch
	 * koeficient�. V prvn� polovin� �seku transformovan�ho sign�lu je ukl�d�n�
	 * aproxima�n� slo�ka z�skan� mate�sk�mi koeficienty a v druh� polovin� je
	 * ukl�d�na detailn� slo�ka z�skan� pomoc� wavelet koeficient�.
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

		for (int j = 0; j < last - 3; j = j + Power2Utils.CONST_2) {
			tmp[i] = signal[j] * scale[0] + signal[j + 1] * scale[1] + signal[j + 2]
					* scale[2] + signal[j + 3] * scale[3];

			tmp[i + half] = signal[j] * wavelet[0] + signal[j + 1] * wavelet[1]
					+ signal[j + 2] * wavelet[2] + signal[j + 3] * wavelet[3];
			i++;
		}

		tmp[i] = signal[last - 2] * scale[0] + signal[last - 1] * scale[1]
				+ signal[0] * scale[2] + signal[1] * scale[3];

		tmp[i + half] = signal[last - 2] * wavelet[0] + signal[last - 1]
				* wavelet[1] + signal[0] * wavelet[2] + signal[1] * wavelet[3];

		for (i = 0; i < last; i++) {
			signal[i] = tmp[i];
		}
	}

	/**
	 * Metoda transformuj�c� koeficienty zp�tky na origin�ln� sign�l pomoc�
	 * nastaven�ch wavelet� a mate�sk�ch koeficient�. Skl�d� p�vodn� sign�l z
	 * aproxima�n�ch a detailn�ch slo�ek transformovan�ho sign�lu.
	 * 
	 * @param coef
	 *          - transformovan� sign�l.
	 * @param last
	 *          - d�lka transformovan�ho �seku sign�lu.
	 */
	private static void invTransform(double[] coef, int last) {
		int half = last / Power2Utils.CONST_2;
		int halfPls1 = half + 1;
		double tmp[] = new double[last];
		int j = 0;

		tmp[j++] = coef[half - 1] * iScale[0] + coef[last - 1] * iScale[1]
				+ coef[0] * iScale[2] + coef[half] * iScale[3];

		tmp[j++] = coef[half - 1] * iWavelet[0] + coef[last - 1] * iWavelet[1]
				+ coef[0] * iWavelet[2] + coef[half] * iWavelet[3];

		for (int i = 0; i < half - 1; i++) {
			tmp[j++] = coef[i] * iScale[0] + coef[i + half] * iScale[1] + coef[i + 1]
					* iScale[2] + coef[i + halfPls1] * iScale[3];
			tmp[j++] = coef[i] * iWavelet[0] + coef[i + half] * iWavelet[1]
					+ coef[i + 1] * iWavelet[2] + coef[i + halfPls1] * iWavelet[3];
		}

		for (int i = 0; i < last; i++) {
			coef[i] = tmp[i];
		}
	}

	@Override
	public String getAuthorName() {
		return FastDaubechies2.AUTHOR;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return FastDaubechies2.NAME;
	}

	@Override
	public String getVersion() {
		return FastDaubechies2.VERSION;
	}
	
}