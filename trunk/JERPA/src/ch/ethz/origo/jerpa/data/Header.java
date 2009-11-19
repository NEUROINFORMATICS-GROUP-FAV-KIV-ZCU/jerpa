package ch.ethz.origo.jerpa.data;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * T��da uchov�vaj�c� informace o na�ten�m souboru.
 * 
 * @author Jiri Kucera (original class from jERP Studio)
 * @author Tomas Rondik (original class from jERP Studio)
 * @author Petr Soukal (original class from jERP Studio)
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/17/09)
 * @since 0.1.0
 * @see Cloneable
 */
public class Header implements Cloneable {

	/**
	 * Informace o jednotlivych kanalech.
	 */
	private List<Channel> channels;
	/**
	 * Pocet vzorku celkem.
	 */
	private long numberOfSamples;
	/**
	 * Slou�� pro uchov�n� data a �asu, kdy do�lo k vytvo�en� zpracov�van�ho
	 * souboru (je z�sk�v�no z dat v souboru)
	 */
	private GregorianCalendar dateOfAcquisition;
	/**
	 * Vzorkovaci interval v mikrosekundach, tzn. pocet mikrosekund mezi dvema
	 * snimky.
	 */
	private float samplingInterval;
	/**
	 * Delka segmentu.
	 */
	private int segmentLength;
	/**
	 * Rodne cislo. (pod personal number ale muze byt i jine cislo... kori)
	 */
	private String personalNumber;
	/**
	 * Jm�no a p��jmen� (v tomto po�ad�) m��en� osoby (pacienta) v jednom �et�zci
	 * odd�len� mezerou
	 */
	private String subjectName;
	/**
	 * Jm�no a p��jmen� l�ka�e v jednom �et�zci
	 */
	private String docName;
	/**
	 * D�lka m��en�
	 */
	private String length;

	/**
	 * Konstruktor tridy Header.java.
	 */
	public Header() {
		numberOfSamples = Integer.MIN_VALUE;
		dateOfAcquisition = new GregorianCalendar();
		samplingInterval = Integer.MIN_VALUE;
		segmentLength = Integer.MIN_VALUE;
		personalNumber = "";
		subjectName = "";
		docName = "";
		length = "";
		channels = null;
	}

	/**
	 * deklaruje pole senzor�
	 * 
	 * @param point
	 */
	public void addChannel(Channel point) {
		if (channels == null) {
			channels = new ArrayList<Channel>();
		}
		channels.add(point);
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

	/**
	 * @return pole senzor�
	 */
	public List<Channel> getChannels() {
		if (channels == null) {
			channels = new ArrayList<Channel>();
		}
		return channels;
	}

	/**
	 * @return the docName
	 */
	public String getDocName() {
		return docName;
	}

	/**
	 * @param docName
	 *          the docName to set
	 */
	public void setDocName(String docName) {
		this.docName = docName;
	}

	/**
	 * @return the numberOfChannels
	 */
	public int getNumberOfChannels() {
		return channels.size();
	}

	/**
	 * @return the numberOfSamples
	 */
	public long getNumberOfSamples() {
		return numberOfSamples;
	}

	/**
	 * @param numberOfSamples
	 *          the numberOfSamples to set
	 */
	public void setNumberOfSamples(long numberOfSamples) {
		this.numberOfSamples = numberOfSamples;
	}

	/**
	 * @return the personalNumber
	 */
	public String getPersonalNumber() {
		return personalNumber;
	}

	/**
	 * @param personalNumber
	 *          the personalNumber to set
	 */
	public void setPersonalNumber(String personalNumber) {
		this.personalNumber = personalNumber;
	}

	/**
	 * Vrac� vzorkovac� interval v mikrosekund�ch, tzn. po�et mikrosekund mezi
	 * dv�ma sn�mky.
	 * 
	 * @return Vzorkovac� interval v mikrosekund�ch, tzn. po�et mikrosekund mezi
	 *         dv�ma sn�mky.
	 */
	public float getSamplingInterval() {
		return samplingInterval;
	}

	/**
	 * Nastavuje vzorkovac� interval v mikrosekund�ch, tzn. po�et mikrosekund mezi
	 * dv�ma sn�mky.
	 * 
	 * @param samplingInterval
	 *          Vzorkovac� interval v mikrosekund�ch, tzn. po�et mikrosekund mezi
	 *          dv�ma sn�mky.
	 */
	public void setSamplingInterval(float samplingInterval) {
		this.samplingInterval = samplingInterval;
	}

	/**
	 * @return the segmentLength
	 */
	public int getSegmentLength() {
		return segmentLength;
	}

	/**
	 * @param segmentLength
	 *          the segmentLength to set
	 */
	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}

	/**
	 * Vrac� hodnotu atributu subjectName
	 * 
	 * @return jm�no a p��jmen� m��en� osoby (pacienta) v jednom �et�zci
	 */
	public String getSubjectName() {
		return subjectName;
	}

	/**
	 * Nastavuje hodnotu atributu subjectName
	 * 
	 * @param subjectName
	 *          jm�no a p��jmen� m��en� osoby (pacienta) v jednom �et�zci
	 */
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	/**
	 * Vrac� objekt atributu calendar
	 * 
	 * @return objekt t��dy java.util.Calendar
	 */
	public GregorianCalendar getDateOfAcquisition() {
		return dateOfAcquisition;
	}

	/**
	 * Nastavuje atribut calendar
	 * 
	 * @param date
	 */
	public void setDateOfAcquisition(GregorianCalendar date) {
		// Debug.print("Gregorian Calendar: " + gregorianCalendar +
		// this.getCalendar());
		this.dateOfAcquisition = date;
	}

	/**
	 * Vrac� �et�zec �asu d�lky m��en�
	 * 
	 * @return �et�zec �asu d�lky m��en�
	 */
	public String getLength() {
		if (length == null || length.equals("")) {
			long l = (long) (numberOfSamples * (double) samplingInterval) / 1000;
			String hours = String.valueOf((int) (l / 3600 / 1000));
			String minutes = String.valueOf((int) (l % (3600 * 1000)) / 60000);
			String seconds = String
					.valueOf((int) ((l % (3600 * 1000) % 60000) / 1000));
			String millis = String
					.valueOf((int) ((l % (3600 * 1000) % 60000) % 1000));

			while (hours.length() < 2) {
				hours = "0" + hours;
			}

			while (minutes.length() < 2) {
				minutes = "0" + minutes;
			}

			while (seconds.length() < 2) {
				seconds = "0" + seconds;
			}

			while (millis.length() < 3) {
				millis = "0" + millis;
			}
			length = hours + ":" + minutes + ":" + seconds + "." + millis;
		}
		return length;
	}

	/**
	 * Nastavuje atribut �asu d�lky m��en�.
	 * 
	 * @param length
	 *          Nastavuje �et�zec vyjad�uj�c� d�lku m��en�.
	 */
	public void setLength(String length) {
		this.length = length;
	}

	/**
	 * Metoda equals porovn� objet s jin�m zadan�m, vrac� true, pokud jsou shodn�
	 * shodn� jsou v t�to implementaci, pokud se shoduj� ve v�ech sv�ch atributech
	 * 
	 * @param obj
	 *          porovn�van� objekt
	 * @return true v p��pad� shody, jinak false
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Header)) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		Header h = (Header) obj;

		return docName == null ? h.docName == null
				: docName.equals(h.docName) && dateOfAcquisition == null ? h.dateOfAcquisition == null
						: dateOfAcquisition.equals(h.dateOfAcquisition) && channels == null ? h.channels == null
								: channels.equals(h.channels) && length == null ? h.length == null
										: length.equals(h.length)
												&& numberOfSamples == h.numberOfSamples
												&& personalNumber == null ? h.personalNumber == null
												: personalNumber.equals(h.personalNumber)
														&& samplingInterval == h.samplingInterval
														&& segmentLength == h.segmentLength
														&& subjectName == null ? h.subjectName == null
														: subjectName.equals(h.subjectName);
	}

	/**
	 * Vypo��t� nejvhodn�j�� hashovac� funkci pro danou instanci
	 * 
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + (docName == null ? 0 : docName.hashCode());
		result = 37 * result
				+ (dateOfAcquisition == null ? 0 : dateOfAcquisition.hashCode());
		result = 37 * result + (channels == null ? 0 : channels.hashCode());
		result = 37 * result + (length == null ? 0 : length.hashCode());
		result = 37 * result + (int) (numberOfSamples ^ (numberOfSamples >>> 32));
		result = 37 * result
				+ (personalNumber == null ? 0 : personalNumber.hashCode());
		result = 37 * result + Float.floatToIntBits(samplingInterval);
		result = 37 * result + segmentLength;
		result = 37 * result + (subjectName == null ? 0 : subjectName.hashCode());
		return result;
	}

	/**
	 * Vytvo�� hlubokou kopii t��dy.
	 * 
	 * @return Hlubok� kopie t��dy.
	 */
	@Override
	public Header clone() {
		Header header;
		try {
			header = (Header) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}

		header.dateOfAcquisition = (GregorianCalendar) dateOfAcquisition.clone();

		List<Channel> newChannels = new ArrayList<Channel>();
		for (Channel ch : channels) {
			newChannels.add(ch.clone());
		}

		header.channels = newChannels;

		return header;
	}

	@Override
	public String toString() {

		String str = "size: " + (channels == null ? "null" : channels.size())
				+ ", number of samples: " + numberOfSamples + ", milis from Epoch: "
				+ dateOfAcquisition.getTimeInMillis() + ", sampling interval: "
				+ samplingInterval + ", segment length: " + segmentLength
				+ ", personal number: " + personalNumber + ", subject name: "
				+ subjectName + ", doc name: " + docName + ", length: " + length;

		return str;
	}
}
