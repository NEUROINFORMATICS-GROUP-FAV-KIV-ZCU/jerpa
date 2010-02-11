package ch.ethz.origo.jerpa.data.algorithms.filters.mp;

/**
 * Instance t��dy slou�� k reprezentaci komplexn�ch ��sel a umo��uj� prov�d�t 
 * s nimi z�kladn� operace. 
 * 
 * T��da byla p�evzata z diplomov� pr�ce Ing. Jaroslava Svobody 
 * (<cite>Svoboda Jaroslav.  Metody zpracov�n� evokovan�ch potenci�l�,  Plze�,  2008.  
 * Diplomov� pr�ce  pr�ce  na Kated�e   informatiky a v�po�etn�  
 * techniky Z�pado�esk� univerzity v Plzni. Vedouc� diplomov� pr�ce Ing. Pavel 
 * Mautner, Ph.D.</cite>) a n�sledn� upravena.
 *
 * @author Tom� �ond�k
 * @version 17. 11. 2008 
 */
public class Complex
{
	/**
	 * Re�ln� ��st komplexn�ho ��sla.
	 */
	private double re;
	/**
	 * Imagin�rn� ��st komplexn�ho ��sla.
	 */
	private double im;
	/**
	 * Povolen� rozd�l od nulov� hodnoty. Hodnoty (<code>0 +- ZERO_ALLOWANCE</code>) jsou pova�ov�ny za rovn� nule.
	 */
	public static final double ZERO_ALLOWANCE = 1.0e-7F;
	
	/**
	 * Vytvo�en� instance reprezentuj�c� komplexn� ��slo.
	 * 
	 * @param re re�ln� ��st komplexn�ho ��sla
	 * @param im imagin�rn� ��st komplexn�ho ��sla
	 */
	public Complex(double re, double im)
	{
		this.re = re;
		this.im = im;
	}
	
	/**
	 * Nastavuje re�lnou ��st komplexn�ho ��sla na hodnotu re�ln� ��sti komplexn�ho ��sla p�edan�ho jako parametr 
	 * metody. Nastavuje imagin�rn� ��st komplexn�ho ��sla na hodnotu imagin�rn� ��sti komplexn�ho ��sla p�edan�ho 
	 * jako parametr metody.
	 * 
	 * @param complex komplexn� ��slo podle n�j� se nastavuj� hodnoty re�ln� a imagin�rn� ��sti
	 * @return instance t��dy <code>Complex</code> s p��slu�n� nastavenou re�lnou i imagin�rn� ��st�
	 */
	public Complex set(Complex complex)
	{
		this.re = complex.re;
		this.im = complex.im;
		return this;
	}

	/**
	 * Zji��uje, jestli je komplexn� ��slo nulov�, tj. jestli jsou jeho re�ln� a imagin�rn� ��st rovny nule. Od 
	 * ��st� ��sla p�itom nen� po�adov�no, aby byly p�esn� rovny nule - sta��, aby byly v absolutn� hodnot� ost�e 
	 * men�� ne� konstanta <code>ZERO_ALLOWANCE</code>.
	 * 
	 * @return <b>true</b>, pokud je komplexn� ��slo nulov�, jinak <b>false</b>.
	 */
	public boolean isZero()
	{
		return (Math.abs(this.re) < ZERO_ALLOWANCE && Math.abs(this.im) < ZERO_ALLOWANCE);
	}

	/**
	 * Se�te komplexn� ��slo s komplexn�m ��slem p�edan�m jako parametr. Re�lnou ��st ��sla nav�� o re�lnou ��st 
	 * p�edan�ho ��sla, imagin�rn� ��st ��sla nav�� o imagin�rn� ��st p�edan�ho ��sla. Vrac� komplexn� ��slo, kter� je sou�tem 
	 * obou komplexn�ch ��sel.
	 * 
	 * @param complex komplexn� ��slo jeho� hodnoty budou p�i��t�ny
	 * @return komplexn� ��slo, kter� je v�sledkem sou�tu
	 */
	public Complex addTo(Complex complex)
	{
		this.re += complex.re;
		this.im += complex.im;
		return this;
	}

	/**
	 * Vyn�sob� komplexn� ��slo s komplexn�m ��slem p�edan�m jako parametr. Vrac� komplexn� ��slo, kter� je sou�inem 
	 * obou komplexn�ch ��sel.
	 * 
	 * @param complex komplexn� ��slo, kter� bude pou�ito jako sou�initel
	 * @return komplexn� ��slo, kter� je v�sledkem sou�inu
	 */
	public Complex timesBy(Complex complex)
	{
		double tmp = this.re * complex.re - this.im * complex.im;
		this.im = this.im * complex.re + this.re * complex.im;
		this.re = (double) tmp;
		return this;
	}

	/**
	 * Negace re�ln� a imagin�rn� ��sti komplexn�ho ��sla.
	 * @return negovan� instance komplexn�ho ��sla
	 */
	public Complex negate()
	{
		this.re = -this.re;
		this.im = -this.im;
		return this;
	}

	/**
	 * Vyd�len� re�ln� a imagin�rn� ��sti komplexn�ho ��sla hodnotou p�edanou jako parametr metody.
	 * @param n d�litel
	 * @return vyd�len� instance komplexn�ho ��sla
	 */
	public Complex div(int n)
	{
		this.re /= n;
		this.im /= n;
		return this;
	}

	/**
	 * Metoda vrac� velikost komplexn�ho ��sla.
	 * @return velikost komplexn�ho ��sla
	 */
	public double size()
	{
		return (double) Math.sqrt(this.re * this.re + this.im * this.im);
	}

	/**
	 * Vrac� re�lnou ��st komplexn�ho ��sla.
	 * @return re�ln� ��st
	 */
	public double getRe()
	{
		return re;
	}

	/**
	 * Vrac� imagin�rn� ��st komplexn�ho ��sla.
	 * @return imagin�rn� ��st
	 */
	public double getIm()
	{
		return im;
	}
	
	/**
	 * Metoda vytv��� kopii instance. P�ekr�v� metodu ze t��dy <code>Object</code>.
	 * @return kopie instance
	 */
	@Override
	public Complex clone()
	{
		return new Complex(re, im);
	}
	
	/**
	 * Vrac� textovou reprezentaci komplexn�ho ��sla. P�ekr�v� metodu ze t��dy <code>Object</code>.
	 * @return textov� reprezentace komplexn�ho ��sla
	 */
	@Override
	public String toString()
	{
		return re + " " + im + "i";
	}
}
