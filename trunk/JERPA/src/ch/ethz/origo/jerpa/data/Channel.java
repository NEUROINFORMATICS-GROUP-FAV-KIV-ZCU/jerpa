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
package ch.ethz.origo.jerpa.data;

/**
 * T��da uchov�vaj�c� informace o jednom kan�lu (sign�lu).
 
 * @author Jiri Kucera (original class from jERP Studio)
 * @author Tomas Rondik (original class from jERP Studio)
 * @author Petr Soukal (original class from jERP Studio)
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/17/09)
 * @since 0.1.0
 * @see Cloneable
 */
public class Channel implements Cloneable {

	private static final int MICROSECONDS = 1000000;
	/**
	 * Name of sensor.
	 */
	private String name;

	// Values.
	private short nV_bit;
	private String edfTransducerType;
	private String unit;
	private String edfPrefiltering;

	/**
	 * Frekvence vzorku signalu v Hz.
	 */
	private float frequency;
	/**
	 * Perioda vzorku signalu v mikrosekundach.
	 */
	private float period;
	/**
	 * Urcuje, ktera z hodnot frequency nebo period je puvodni. Druha hodnota je
	 * dopocitana.
	 */
	private String original;

	public Channel() {
		this.name = "unknown";
		this.nV_bit = 0;
		this.edfTransducerType = "unknown";
		this.unit = "unknown";
		this.edfPrefiltering = "unknown";
		this.frequency = 0;
		this.period = 0;
		this.original = "unknown";
	}

	/**
	 * P�ekryt� metody toString() zd�d�n� od t��dy Object.
	 */
	@Override
	public String toString() {
		// return name + "\t" + nV_bit + "\t" + edfTransducerType + "\t" + unit +
		// "\t" + edfPrefiltering + "\t" + edfPhysicalMinimum + "\t" +
		// edfPhysicalMaximum + "\t" + edfDigitalMinimum + "\t" + edfDigitalMaximum
		// + "\t";
		return name + "\t" + nV_bit + "\t" + edfTransducerType + "\t" + unit + "\t"
				+ edfPrefiltering;
	}

	/**
	 * @return the edfPhysicalDimension
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the edfPrefiltering
	 */
	public String getEdfPrefiltering() {
		return edfPrefiltering;
	}

	/**
	 * @param edfPrefiltering
	 *          the edfPrefiltering to set
	 */
	public void setEdfPrefiltering(String edfPrefiltering) {
		this.edfPrefiltering = edfPrefiltering;
	}

	/**
	 * @return the edfTransducerType
	 */
	public String getEdfTransducerType() {
		return edfTransducerType;
	}

	/**
	 * @param edfTransducerType
	 *          the edfTransducerType to set
	 */
	public void setEdfTransducerType(String edfTransducerType) {
		this.edfTransducerType = edfTransducerType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the nV_bit
	 */
	public short getNV_bit() {
		return nV_bit;
	}

	/**
	 * @param nv_bit
	 *          the nV_bit to set
	 */
	public void setNV_bit(short nv_bit) {
		nV_bit = nv_bit;
	}

	/**
	 * @return the frequency
	 */
	public float getFrequency() {
		return frequency;
	}

	/**
	 * @param frequency -
	 *          the frequency to set.
	 */
	public void setFrequency(float frequency) {
		this.frequency = frequency;
		this.period = MICROSECONDS / frequency;
		this.original = "frequency";
	}

	/**
	 * @return the original
	 */
	public String getOriginal() {
		return original;
	}

	/**
	 * @param original -
	 *          the original to set.
	 */
	public void setOriginal(String original) {
		this.original = original;
	}

	/**
	 * @return the period
	 */
	public float getPeriod() {
		return period;
	}

	/**
	 * @param period -
	 *          the period to set.
	 */
	public void setPeriod(float period) {
		this.period = period;
		this.frequency = MICROSECONDS / period;
		this.original = "period";
	}

	public void setPeriodOnly(float period) {
		this.period = period;
	}

	public void setFrequencyOnly(float frequency) {
		this.frequency = frequency;
	}

	/**
	 * Vytvo�� hlubokou kopii t��dy.
	 * 
	 * @return Hlubok� kopie t��dy.
	 */
	@Override
	public Channel clone() {
		Channel channel;
		try {
			channel = (Channel) super.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null;
		}
		return channel;
	}
}
