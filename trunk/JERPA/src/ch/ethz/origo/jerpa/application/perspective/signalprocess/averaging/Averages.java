package ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.OneWayAnova;
import org.apache.commons.math.stat.inference.OneWayAnovaImpl;

/**
 * T��da poskytuj�c� metody pro v�po�et pr�m�ru. Proto�e nem� ��dn� atributy, je
 * skyt jej� konstruktor a v�echny metody jsou statick�.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 */
public final class Averages {
	/**
	 * Konstanta reprezentuj�c� aritmetick� pr�m�r.
	 */
	public static final int ARITHMETICAL = 0;
	/**
	 * Konstanta reprezentuj�c� pou�it� statictik� metody ANOVA
	 */
	public static final int ANOVA = 1;

	/**
	 * Pole obsahuj�c� typy pr�m�r� v tom po�ad�, v jak�m budou nab�zeny v
	 * roletkov�m menu v toolBaru v okn� AberagingWindow.
	 */
	public static final String[] AVERAGE_TYPES = { "Arithmetical", "ANOVA" };
	/**
	 * Konstanta pro ohodnocen� d�v�ryhodnosti pr�m�ru - nezn�m� d�v�ryhodnost
	 * pr�m�ru.
	 */
	public static final int UNKNOWN = 0;
	/**
	 * Konstanta pro ohodnocen� d�v�ryhodnosti pr�m�ru - d�v�ryhodn� pr�m�r.
	 */
	public static final int TRUSTFUL = 1;
	/**
	 * Konstanta pro ohodnocen� d�v�ryhodnosti pr�m�ru - ned�v�ryhodn� pr�m�r.
	 */
	public static final int NOT_TRUSTFUL = 2;

	/**
	 * Priv�tn� konstruktor. Nelze vytv��et instance t��dy. V�po�et pr�m�r� je
	 * poskytov�n statick�mi metodami.
	 */
	private Averages() {
	}

	/**
	 * Rozcestn�k pro v�b�r po�adovan�ho typu pr�m�ru. P�ed�v� parametry metod�m,
	 * kter� vytv��� po�adovan� typ pr�m�ru z dat p�edan�ch v parametru
	 * <i>checkedEpochValues</i>.
	 * 
	 * @param channelValues
	 *          Funk�n� hodnoty epoch, kter� se budou pr�m�rovat.
	 * @param averagingType
	 *          Po�adovan� typ pr�m�ru.
	 * @return Objekt obsahuj�c� po�adovan� pr�m�r ze sign�l� p�edan�ch jako
	 *         parametry metody.
	 * @throws MathException
	 * @throws IllegalArgumentException
	 */
	public static ChannelEpoch average(List<ChannelEpoch> channelValues,
			int averagingType) throws IllegalArgumentException, MathException // FIXME
																																				// Exporting
																																				// non-public
																																				// type
																																				// through
																																				// public
																																				// API
	{
		ChannelEpoch average = null;

		switch (averagingType) {
		case ARITHMETICAL:
			average = arithmetical(channelValues);
			break;
		default:
			average = anova(channelValues);
		}

		return average;
	}

	/**
	 * Metoda realizuj�c� v�en� aritmetick� pr�m�r.
	 * 
	 * @param channelValues
	 *          Funk�n� hodnoty epoch, kter� se budou pr�m�rovat.
	 * @return Objekt obsahuj�c� po�adovan� pr�m�r ze sign�l� p�edan�ch jako
	 *         parametry metody.
	 */
	private static ChannelEpoch arithmetical(List<ChannelEpoch> channelValues) {
		ChannelEpoch average = new ChannelEpoch();

		if (channelValues.size() > 0) {
			average.setValues(new float[channelValues.get(0).getValues().length]);
			Arrays.fill(average.getValues(), 0);
			int weightSum = 0;

			for (ChannelEpoch dummy : channelValues) {
				for (int i = 0; i < dummy.getValues().length; i++) {
					average.getValues()[i] += dummy.getValues()[i];
				}
				weightSum += dummy.getWeight();
			}

			for (int i = 0; i < average.getValues().length; i++) {
				average.getValues()[i] /= weightSum;
			}
			average.setWeigh(weightSum);
		}

		return average;
	}

	/**
	 * Metoda vol� metodu <code>arithmetical</code> pro vytvo�en� v�en�ho
	 * aritmetick�ho pr�m�ru a d�le metodu <code>OneWayAnova.anovaTest</code>
	 * pro zji�t�n� d�v�ryhodnosti odbr�en�ho v�en�ho aritmetick�ho pr�m�ru.
	 * 
	 * @param channelValues
	 *          Funk�n� hodnoty epoch, kter� se budou pr�m�rovat.
	 * @return Objekt obsahuj�c� po�adovan� pr�m�r ze sign�l� p�edan�ch jako
	 *         parametry metody.
	 * @throws IllegalArgumentException
	 * @throws MathException
	 */
	private static ChannelEpoch anova(List<ChannelEpoch> channelValues)
			throws IllegalArgumentException, MathException {
		ChannelEpoch average = arithmetical(channelValues);
		OneWayAnova anova = new OneWayAnovaImpl();

		List<double[]> channelEpochValues = new ArrayList<double[]>();
		for (ChannelEpoch channelEpoch : channelValues) {
			double[] values = new double[channelEpoch.getValues().length];
			for (int i = 0; i < values.length; i++) {
				values[i] = channelEpoch.getValues()[i];
			}
			channelEpochValues.add(values);
		}

		int trustful = UNKNOWN;
		if (channelValues.size() > 2) {
			boolean positive = anova.anovaTest(channelEpochValues, 0.05);
			trustful = positive ? TRUSTFUL : NOT_TRUSTFUL;

		}
		average.setTrustful(trustful);
		return average;
	}
}
