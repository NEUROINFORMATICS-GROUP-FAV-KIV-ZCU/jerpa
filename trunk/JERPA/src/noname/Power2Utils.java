/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *  
 *    Copyright (C) 2009 - 2010 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package noname;

/**
 * Class contains mathematical operations.
 * 
 * @author Petr Soukal
 * @author Vaclav Souhrada
 * @version 1.0.0 (11/29/09) 
 * @since 0.1.0 (11/29/09)
 */
public class Power2Utils {
	
	// Constants
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