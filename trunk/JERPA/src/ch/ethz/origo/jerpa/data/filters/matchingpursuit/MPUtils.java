package ch.ethz.origo.jerpa.data.filters.matchingpursuit;

/**
 * T��da obsahuje funkce pou��van� pro v�po�et metody Matching pursuit.
 * 
 * T��da byla p�evzata z diplomov� pr�ce Ing. Jaroslava Svobody 
 * (<cite>Svoboda Jaroslav.  Metody zpracov�n� evokovan�ch potenci�l�,  Plze�,  2008.  
 * Diplomov� pr�ce  pr�ce  na Kated�e   informatiky a v�po�etn�  
 * techniky Z�pado�esk� univerzity v Plzni. Vedouc� diplomov� pr�ce Ing. Pavel 
 * Mautner, Ph.D.</cite>) a n�sledn� upravena.
 * 
 * @author Tom� �ond�k
 * @version 17. 11. 2008
 * 
 */
public class MPUtils
{
	/**
	 * TODO - koment��
	 */
	private static Complex[] wtbl = null;
	/**
	 * TODO - koment��
	 */
	private static int last_n = 0;
	
	/**
	 * Method generates complex values for synthese of FFT algorithm.
	 * 
	 * @param n
	 *            nuber of input vector points
	 * @return array of generated complex values
	 */
	public static Complex[] make_wtbl(int n)
	{
		if (last_n == n)
			return wtbl;
		
		int n2 = n >> 1;
		wtbl = new Complex[n2 + 1];
		wtbl[0] = new Complex(1, 0);
		
		double t = 0;
		for (int i = 1; i < n2; i++)
		{
			t = 2 * Math.PI * i / n;
			wtbl[i] = new Complex(Math.cos(t), Math.sin(t));
		}
		
		wtbl[n2] = new Complex(-1, 0);
		last_n = n;
		
		return wtbl;
	}
	/***
	 * Vrac� funk�n� hodnotu Gaussova ok�nka (po��t�no podle vztahu: <i>e^(-pi * t^2))</i>v �ase dan�m parametrem <code>t</code>.
	 * @param t �as, pro kter� je spo�tena funk�n� hodnota Gaussova ok�nka
	 * @return funk�n� hodnota Gaussova ok�nka v �ase <code>t</code>
	 */
	public static double gaussianWindow(double t)
	{
		return Math.exp(-Math.PI * Math.pow(t, 2));
	}
	/**
	 * Vypo�ten� skal�rn�ho sou�inu dvou sign�l� p�edan�ch jako parametry metody.
	 * 
	 * @param signal1 diskr�tn� sign�l
	 * @param signal2 diskr�tn� sign�l
	 * @return skal�rn� sou�in sign�l� <code>signal1</code> a <code>signal2</code>
	 */
	public static double inerProduct(double[] signal1, double[] signal2)
	{
		int output = 0;
		if (signal1.length == signal2.length)
		{
			for (int i = 0; i < signal1.length; i++)
				output += signal1[i] * signal2[i];
		}
		return output;
	}
	/**
	 * Metoda vypo��t�v� Fourierovu transformaci algoritmem FFT 
	 
	 * @param signal vstupn� sign�l
	 * @param inverse
	 * <ul>
	 * 	<li> <code>true</code>, pokud m� b�t vypo�tena inverzn� Fourierova transformace
	 * 	<li> <code>false</code> jinak
	 * </ul>
	 * @return transformovan� sign�l
	 */
	public static Complex[] doFFT(double[] signal, boolean inverse)
	{
		Complex[] f = new Complex[signal.length];
		
		for (int i = 0; i < signal.length; i++)
		{
			f[i] = new Complex(signal[i], 0);
		}
		// preparation
		int n = f.length;
		int n2 = n >> 1;
		Complex[] wtbl = make_wtbl(n);
		
		// Bit inversion
		for (int i = 0, j = 0, k;;) // for-cyklus pou�it� kv�li omezen� viditelnosti prom�nn�ch i,j,k
		{
			if (i < j)
			{
				Complex t = f[i];
				f[i] = f[j];
				f[j] = t;
			}
			if (++i >= n)
				break;
			
			for (k = n2; k <= j; k >>= 1)
			{
				j -= k;
			}
			j += k;
		}
		Complex tm = f[0].clone();
		
		// Synthese of frequency spectra
		int k = 1;
		while (k < n)
		{
			int h = 0;
			int k2 = k << 1;
			int d = n / k2;
			
			for (int j = 0; j < k; j++)
			{
				if (inverse)
				{
					for (int i = j; i < n; i += k2)
					{
						int ik = i + k;
						f[ik].timesBy(wtbl[h]);
						tm.set(f[ik]).addTo(f[i]);
						f[ik].negate().addTo(f[i]);
						f[i].set(tm);
					}
				}
				else
				{
					for (int i = j; i < n; i += k2)
					{
						int ik = i + k;
						f[ik].timesBy(wtbl[n2 - h]);
						tm.set(f[ik]).negate().addTo(f[i]);
						f[ik].addTo(f[i]);
						f[i].set(tm);
					}
				}
				h += d;
			}
			k = k2;
		}
		return f;
	}
	/**
	 * Priv�tn� konstruktor. Neexistuje racion�ln� d�vod k vytv��en� instanc� t�to t��dy.
	 */
	private MPUtils(){}
}
