package ch.ethz.origo.jerpa.data.filters.matchingpursuit;

import ch.ethz.origo.jerpa.data.filters.IFilter;

/**
 * T��da byla p�evzata z diplomov� pr�ce Ing. Jaroslava Svobody (<cite>Svoboda
 * Jaroslav. Metody zpracov�n� evokovan�ch potenci�l�, Plze�, 2008. Diplomov�
 * pr�ce pr�ce na Kated�e informatiky a v�po�etn� techniky Z�pado�esk�
 * univerzity v Plzni. Vedouc� diplomov� pr�ce Ing. Pavel Mautner, Ph.D.</cite>)
 * a n�sledn� upravena.
 * 
 * @author Tomas Rondik
 * @version 1.0.0 (1/20/09)
 * @since 0.1.0 JERPA version
 * 
 */
public class FourierMP implements IFilter {
	/**
	 * Po�et parametr� identifikuj�c�ch Gabor�v atom.
	 */
	private static final int PARAMS_COUNT = 5;
	/**
	 * M���tko Gaborova Atomu.
	 */
	private static final int SCALE_INDEX = 0;
	/**
	 * Posunut� Gaborova Atomu.
	 */
	private static final int POSITION_INDEX = 1;
	/**
	 * Frekvence Gaborova Atomu.
	 */
	private static final int FREQUENCY_INDEX = 2;
	/**
	 * F�zov� posun Gaborova Atomu.
	 */
	private static final int PHASE_INDEX = 3;
	/**
	 * Modulus (koeficient podobnosti se vstupn�m sign�lem) Gaborova Atomu.
	 */
	private static final int MODULUS_INDEX = 4;
	
	private static final String AUTHOR = "Tomas Rondik";
	
	private static final String VERSION = "1.0";
	
	private static final String NAME = "Matching Pursuit";
	
	/**
	 * Priv�tn� konstruktor. Neexistuje racion�ln� d�vod k vytv��en� instanc� t�to
	 * t��dy.
	 */
	private FourierMP() {
	}

	/**
	 * Slou�� k proveden� algoritmu Matching Pursuit. Algoritmus pou��v� k
	 * aproximaci vstupn�ho sign�lu slovn�k Gaborov�ch funkc�. K urychlen� v�po�tu
	 * byla pou�ita rychl� Fourierova transformace, jej� d�sledkem je po�adavek na
	 * d�lku vstupn�ho sign�lu (viz <b>Parameters</b>).
	 * 
	 * @param signal
	 *          Vstupn� sign�l d�lky <code>2^n</code>, kde <code>n</code> je
	 *          p�irozen� ��slo. N�rok na d�lku vstupn�ho sign�lu je d�n pou�it�m
	 *          rychl� Fourierovy transformace v algoritmu Matching Pursuit.
	 * @param iterations
	 *          Po�et iterac� algoritmu Matching Pursuit.
	 * @return Dvojrozm�rn� pole obsahuj�c� parametry Gaborov�ch atom� uspo��dan�
	 *         takto:
	 *         <ul>
	 *         <li>double[index_atomu][SCALE_INDEX] - m���tko (scale)
	 *         <li>double[index_atomu][POSITION_INDEX] - posunut� (position)
	 *         <li>double[index_atomu][FREQUENCY_INDEX] - frekvence (frequency)
	 *         <li>double[index_atomu][PHASE_INDEX] - f�zov� posun (phase)
	 *         <li>double[index_atomu][MODULUS_INDEX] - koeficient podobnosti se
	 *         vstupn�m sign�lem (modulus)
	 *         </ul>
	 *         Index atomu ud�v� ��slo iterace algoritmu, ve kter�m byl atom
	 *         pou�it.
	 * @throws IllegalArgumentException
	 *           Pokud je zad�n sign�l d�lky, kter� nen� mocninou ��sla dv� nebo
	 *           pokud je po�et iterac� men�� nebo roven nule.
	 */
	public static double[][] doMP(double[] signal, int iterations)
			throws IllegalArgumentException {
		// BEGIN - kontrola vlastnost� parametr� metody
		/*
		 * Kontroluje se, �e d�lka vstupn�ho sign�lu (tj. pole "signal") je rovna
		 * n�kter� mocnin� dvou. D�le se kontroluje, jestli je po�et iterac� kladn�
		 * ��slo.
		 * 
		 * Do prom�nn� "nDouble" se ulo�� v�sledek logaritmu z ��sla "signal.length"
		 * o z�kladu dva jako desetinn� ��slo.
		 */
		double nDouble = (Math.log(signal.length)) / Math.log(2);
		/*
		 * Do prom�nn� "nLong" se ulo�� v�sledek logaritmu z ��sla "signal.length" o
		 * z�kladu dva jako cel� ��slo.
		 */
		long nLong = Math.round((Math.log(signal.length)) / Math.log(2));
		/*
		 * D�lka vstupn�ho sign�lu je mocninou dvou pr�v� tehdy, kdy� "nDoulbe" je
		 * rovno nule. Aby se p�ede�lo porovn�n� doubleov�ho ��sla s nulou, definuje
		 * se tolerance "ALLOWANCE", kter� je jist� dost mal�.
		 */
		final double ALLOWANCE = 1e-32;

		if (iterations <= 0 || (Math.abs(nDouble - nLong)) > ALLOWANCE)
			throw new IllegalArgumentException();
		// END- kontrola vlastnost� parametr� metody

		double[][] atoms = new double[iterations][PARAMS_COUNT];

		double energy1 = 0, energy2 = 0;

		// V�po�et energie vstupn�ho sign�lu.
		for (int i = 0; i < signal.length; i++) {
			energy1 += Math.pow(signal[i], 2);
		}

		for (int i = 0; i < iterations; i++) {
			double[] g = getOptimalGabor(signal);
			atoms[i] = g;
			double sum = 0;

			// V�po�et energie Gaborova atomu v bodech, ve kter�ch je ur�en� vstupn�
			// sign�l.
			for (int j = 0; j < signal.length; j++) {
				sum += Math.pow(evaluate(j, atoms[i]), 2);
			}

			energy2 = 0;
			sum = atoms[i][4] / Math.sqrt(sum);

			// Ode�ten� Gaborova atomu od sign�lu a v�po�et energie takto vznikl�ho
			// sign�lu.
			for (int j = 0; j < signal.length; j++) {
				signal[j] -= evaluate(j, atoms[i]) * sum;
				energy2 += Math.pow(signal[j], 2);
			}
			if (energy2 > energy1)
				return atoms;
		}
		return atoms;
	}

	/**
	 * Metoda vy��sluje funk�n� hodnotu Gaussova ok�nka v �ase v z�vislosti na
	 * hodnot�ch m���tka, posunut�, frekvence a f�zov�ho posunu Gaborova atomu.
	 * 
	 * @param numberOfSample
	 *          �as ve kter�m bude hodnota vy��slena
	 * @param atom
	 *          pole s koeficienty Gaborova atomu:
	 *          <ul>
	 *          <li>double[SCALE_INDEX] - m���tko (scale)
	 *          <li>double[POSITION_INDEX] - posunut� (position)
	 *          <li>double[FREQUENCY_INDEX] - frekvence (frequency)
	 *          <li>double[PHASE_INDEX] - f�zov� posun (phase)
	 *          <li>double[MODULUS_INDEX] - koeficient podobnosti se vstupn�m
	 *          sign�lem (modulus), ten ale nen� pou�it
	 *          </ul>
	 * 
	 * @return hodnota Gaborova atomu v po�adovan�m bod�
	 */
	private static double evaluate(double numberOfSample, double[] atom) {
		return MPUtils.gaussianWindow((numberOfSample - atom[POSITION_INDEX])
				/ atom[SCALE_INDEX])
				* Math.cos(numberOfSample * atom[FREQUENCY_INDEX] + atom[PHASE_INDEX]);
	}

	/**
	 * Metoda hled� optim�ln� Gabor�v atom pro vstupn� sign�l Fourierovou
	 * transformac�.
	 * 
	 * @param signal
	 *          Vstupn� sign�l pro pr�v� prob�haj�c� iteraci algoritmu Matching
	 *          Pursuit.
	 * @return Optim�ln� Gabor�v atom.
	 */
	private static double[] getOptimalGabor(double[] signal) {
		// GaborsAtom gaborAtom = null;
		double[] atom = new double[PARAMS_COUNT];
		double scale, position, positionDiferencial = 0;
		int n = signal.length;
		double a1 = 0, a = 0, b = 0, b1 = 0, frequency = 0, phase = 0, coef = 0, product = 0;
		double[] x = new double[n];
		double[] y = new double[n];
		Complex[] X = new Complex[n];
		Complex[] Y = new Complex[n];
		double FP = 0, FQ = 0, PP = 0, QQ = 0, PQ = 0;
		scale = n;
		while (scale > 1) {
			positionDiferencial = scale / 2;
			for (position = 0; position <= n; position += positionDiferencial) {
				for (int j = 0; j < n; j++) {
					double g = MPUtils.gaussianWindow((j - position) / scale);
					x[j] = signal[j] * g;
					y[j] = g * g;
				}
				X = MPUtils.doFFT(x, false);
				Y = MPUtils.doFFT(y, false);
				for (int k = 0; k < n / 2; k++) {
					// computing product
					FP = X[k].getRe();
					FQ = -X[k].getIm();
					double c = Y[0].getRe();
					if (k <= n / 2 - 1) {
						PP = (c + Y[2 * k].getRe()) / 2;
						QQ = (c - Y[2 * k].getRe()) / 2;
						PQ = -Y[2 * k].getIm() / 2;
					} else {
						PP = (c + Y[2 * k - n].getRe()) / 2;
						QQ = (c - Y[2 * k - n].getRe()) / 2;
						PQ = -Y[2 * k - n].getIm() / 2;
					}
					frequency = 2 * Math.PI * k / n;
					a = FP;
					b = FQ;
					a1 = a * QQ - b * PQ;
					// calculation of parameter a2
					b1 = b * PP - a * PQ;
					// if frequency of atom is 0
					if (frequency == 0) {
						phase = 0;
						product = a / Math.sqrt(PP);
					}
					// if a1 parameter is 0
					else if (a1 < Complex.ZERO_ALLOWANCE) {
						phase = Math.PI / 2;
						product = -b / Math.sqrt(QQ);
					}
					// in all other cases
					else {
						phase = Math.atan(-b1 / a1);
						product = (a * a1 + b * b1)
								/ Math.sqrt(Math.pow(a1, 2) * PP + Math.pow(b1, 2) * QQ + 2
										* a1 * b1 * PQ);
					}
					// if this atom has better characteristics than previous
					if (product >= coef && product != Double.POSITIVE_INFINITY) {
						coef = product;
						atom[SCALE_INDEX] = scale;
						atom[POSITION_INDEX] = position;
						atom[FREQUENCY_INDEX] = frequency;
						atom[PHASE_INDEX] = phase;
						atom[MODULUS_INDEX] = coef;
					}
				}
			}
			scale /= 2;
		}
		return atom;
	}

	@Override
	public String getAuthorName() {
		return FourierMP.AUTHOR;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return FourierMP.NAME;
	}

	@Override
	public String getVersion() {
		return FourierMP.VERSION;
	}
	
}
