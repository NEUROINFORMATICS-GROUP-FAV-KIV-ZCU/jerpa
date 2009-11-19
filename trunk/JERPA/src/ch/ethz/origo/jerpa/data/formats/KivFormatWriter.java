package ch.ethz.origo.jerpa.data.formats;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import ch.ethz.origo.jerpa.application.Const;
import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.Header;

/**
 * Modul pro z�pis BrainStudio form�tu.
 * 
 * @author Tomas Rondik
 * @author Petr Soukal
 * @author Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * 
 */
public class KivFormatWriter implements DataFormatWriter {
	/**
	 * Defaultn� je bin�rn� z�pis dat.
	 */
	private static String asciiOrBinary = "-binary";
	/**
	 * Implicitn� jm�no souboru uchov�vaj�c�ho data jednoho sign�lu. P�i vytv��en�
	 * tohoto souboru se za tento String p�ipojuje dvoj��sl� s po�adov�m ��slem
	 * sign�lu. (Nap�. Signal5 je p�t� sign�l)
	 */
	private final String NAME_OF_CHANNELS_FILE = "Signal";
	/**
	 * Koncovka souboru sign�lu, ve kter�m jsou hodnoty ulo�eny jako Ascii znaky
	 */
	private final String ASCII_FORMAT_EXTENSION = "txt";

	private static final int MICROSECOND = 1000000;

	@Override
	public void write(Header header, Buffer buffer, File outputFile)
			throws IOException {
		// podle mno�stv� dat v prvn�m �asov�m okam�iku zji��uje po�et sign�l�
		float[] minima = new float[buffer.getNumberOfSignals()]; // fakt jsem to
																															// takhle

		float[] maxima = new float[buffer.getNumberOfSignals()]; // na�el (minima a
		// maxima), �e je to i
		// anglicky

		int[] counts = new int[buffer.getNumberOfSignals()];

		/*
		 * __________________________________________________ Vytvo�en� .xml souboru
		 * nesouc�ho popisn� informace
		 * __________________________________________________
		 */

		// rozhoduje, jestli budou data zaps�na bin�rn� nebo v ascii
		if (asciiOrBinary.equals(Const.KIV_BINARY)) {
			writeBinary(header, buffer, outputFile.getAbsolutePath(), minima, maxima,
					counts);
		} else if (asciiOrBinary.equals(Const.KIV_ASCII)) {
			writeAscii(header, buffer, outputFile.getAbsolutePath(), minima, maxima,
					counts);
		} else {
			// System.out.println("Nastala chyba, ke kter� nem�lo nikdy doj�t! (v
			// asciiOrBinary se vyskytl " + "neo�ek�van� �et�zec: " + asciiOrBinary);
			// // FIXME
			throw new IOException(
					"Nastala chyba, ke kter� nem�lo nikdy doj�t! (v asciiOrBinary se vyskytl "
							+ "neo�ek�van� �et�zec: " + asciiOrBinary + this.getClass()); // FIXME
		}

		final String KODOVANI = "utf-8";
		final String VERZE = "1.0";

		try {
			XMLOutputFactory oFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter stWriter = oFactory.createXMLStreamWriter(
					new FileOutputStream(outputFile), KODOVANI);

			stWriter.writeStartDocument(KODOVANI, VERZE);

			stWriter.writeCharacters("\r\n");
			stWriter.writeStartElement("eeg");
			stWriter.writeCharacters("\r\n  ");
			stWriter.writeStartElement("header");
			stWriter.writeCharacters("\r\n    ");
			stWriter.writeStartElement("subject");
			stWriter.writeCharacters("\r\n      ");
			stWriter.writeStartElement("name");
			stWriter.writeCharacters(header.getSubjectName());
			stWriter.writeEndElement();// name

			stWriter.writeCharacters("\r\n    ");
			stWriter.writeEndElement();// subject

			stWriter.writeCharacters("\r\n    ");
			stWriter.writeStartElement("doctor");
			stWriter.writeCharacters("\r\n      ");
			stWriter.writeStartElement("name");
			stWriter.writeCharacters(header.getDocName());
			stWriter.writeEndElement();// name

			stWriter.writeCharacters("\r\n    ");
			stWriter.writeEndElement();// doctor

			stWriter.writeCharacters("\r\n    ");
			stWriter.writeStartElement("date");
			stWriter.writeCharacters(dateInvestigation(header));
			stWriter.writeEndElement();// date

			stWriter.writeCharacters("\r\n    ");
			stWriter.writeStartElement("time");
			stWriter.writeCharacters(timeInvestigation(header));
			stWriter.writeEndElement();// time

			stWriter.writeCharacters("\r\n    ");
			stWriter.writeStartElement("length");
			stWriter.writeCharacters(header.getLength());
			stWriter.writeEndElement();// length

			stWriter.writeCharacters("\r\n  ");
			stWriter.writeEndElement();// header

			stWriter.writeCharacters("\r\n  ");
			stWriter.writeStartElement("signals");

			for (int i = 0; i < header.getNumberOfChannels(); i++) {
				stWriter.writeCharacters("\r\n    ");
				stWriter.writeEmptyElement("signal");

				if (asciiOrBinary.equals(Const.KIV_ASCII)) {
					stWriter.writeAttribute("filename", NAME_OF_CHANNELS_FILE + (i + 1)
							+ "" + "." + ASCII_FORMAT_EXTENSION);
				} else {
					stWriter.writeAttribute("filename", NAME_OF_CHANNELS_FILE + (i + 1));
				}

				stWriter.writeAttribute("frequency", ""
						+ header.getChannels().get(i).getFrequency());
				stWriter.writeAttribute("period", ""
						+ (header.getChannels().get(i).getPeriod() / MICROSECOND));
				stWriter.writeAttribute("original", ""
						+ header.getChannels().get(i).getOriginal());
				stWriter.writeAttribute("min", "" + minima[i]);
				stWriter.writeAttribute("max", "" + maxima[i]);
				stWriter.writeAttribute("units", ""
						+ header.getChannels().get(i).getUnit());
				stWriter.writeAttribute("name", ""
						+ header.getChannels().get(i).getName());
				stWriter.writeAttribute("count", "" + counts[i]);
			}

			stWriter.writeCharacters("\r\n  ");
			stWriter.writeEndElement();// signals

			stWriter.writeCharacters("\r\n");
			stWriter.writeEndElement();// eeg

			stWriter.close();
			// asciiOrBinary = "-binary"; // nastav� jako defaultn� op�t bin�rn� z�pis
			// dat
		} catch (Exception e) {
			// asciiOrBinary = "-binary"; // nastav� jako defaultn� op�t bin�rn� z�pis
			// dat
			throw new IOException("Chyba p�i z�pisu hlavi�ky do souboru!, " + e
					+ ", " + this.getClass(), e);
		}

	}

	/**
	 * Vytvo�� pro ka�d� sign�l jeden soubor (Signal(1) a� Signal(n)) a zap�e do
	 * n�j hodnoty, kter� tomuto sign�lu p��slu��. Data jsou zapisov�na jako znaky
	 * ascii.
	 * 
	 * @param outputFile
	 *          n�zev zapisovan�ho souboru, p��padn� i z celou cestou
	 * @param data
	 *          Objekt spole�n� vnit�n� datov� reprezentace
	 * @param minima
	 *          Nejmen�� nam��en� hodnota z ka�d�ho sign�lu
	 * @param maxima
	 *          Nejv�t�� nam��en� hodnota z ka�d�ho sign�lu
	 * @param count
	 *          Po�et nam��en�ch hodnot ka�d�ho sign�lu
	 * 
	 * @throws IOException
	 */
	private void writeAscii(Header head, Buffer buffer, String outputFile,
			float[] minima, float[] maxima, int[] counts) throws IOException {
		// pole hodnot v�ech sign�l�, pro dopl�ov�n� hodnot
		int[] numberOfDataRecord = new int[head.getNumberOfChannels()];
		double maxFrequency = 0.0;

		for (int i = 0; i < head.getNumberOfChannels(); i++) {
			if (maxFrequency <= head.getChannels().get(i).getFrequency()) {
				maxFrequency = head.getChannels().get(i).getFrequency();
			}
		}

		for (int i = 0; i < numberOfDataRecord.length; i++) {
			numberOfDataRecord[i] = (int) (maxFrequency / head.getChannels().get(i)
					.getFrequency());
		}

		int slashPosition = outputFile.lastIndexOf(File.separator);
		String outputPath = null;
		if (slashPosition != -1) {
			outputPath = outputFile.substring(0, slashPosition + 1);
		}

		int numberOfChannels = minima.length;
		// pole bufferedWriter�, ka�d� zapisuje do jednoho souboru (soubor
		// jednoho sign�lu)
		BufferedWriter[] bufferedWriters = new BufferedWriter[numberOfChannels];
		Arrays.fill(minima, 0);
		Arrays.fill(maxima, 0);
		Arrays.fill(counts, 0);
		try {
			for (int index = 0; index < bufferedWriters.length; index++) {
				if (outputPath == null) {// vytv��� soubory jednotliv�ch sign�l�

					bufferedWriters[index] = new BufferedWriter(new FileWriter(
							NAME_OF_CHANNELS_FILE + (index + 1) + "."
									+ ASCII_FORMAT_EXTENSION));
				} else {
					bufferedWriters[index] = new BufferedWriter(new FileWriter(outputPath
							+ File.separator + NAME_OF_CHANNELS_FILE + (index + 1) + "."
							+ ASCII_FORMAT_EXTENSION));
				}
			}

			int line = 0;
			float[] values;

			while ((values = buffer.getNextFrame()) != null) {
				for (int i = 0; i < values.length; i++) {
					if (line % numberOfDataRecord[i] == 0) {
						counts[i]++;
						// bufferedWriters[index].write(moment[index]); ukl�dat do xml
						// upravit
						if (values[i] < minima[i]) {
							minima[i] = values[i]; // ukl�d� nov� minimum
							// dan�ho sign�lu

						} else if (values[i] > maxima[i]) {
							maxima[i] = values[i]; // ukl�d� nov� maximum
							// dan�ho sign�lu

						}
						bufferedWriters[i].write(String.valueOf(values[i]));
						bufferedWriters[i].newLine();
					}
				}
				line++;
			}
		} catch (InvalidFrameIndexException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			// System.out.println("Chyba p�i vytv��en� nebo z�pisu do souboru!");
			throw new IOException("Chyba p�i vytv��en� nebo z�pisu do souboru!, " + e
					+ ", " + this.getClass(), e);
		} // k uzav�en� otev�en�ch soubor� by m�lo doj�t v�dy
		finally {
			for (int index = 0; index < bufferedWriters.length; index++) {
				try {
					if (bufferedWriters[index] != null) {
						bufferedWriters[index].close();
					}
				} catch (IOException e) {
					// System.out.println("Nepoda�ilo se uzav��t v�echny soubory, data
					// mohou b�t ne�pln�!");
					throw new IOException(
							"Nepoda�ilo se uzav��t v�echny soubory, data mohou b�t ne�pln�!, "
									+ e + ", " + this.getClass(), e);
				}
			}
		}
	}

	/**
	 * Vytvo�� pro ka�d� sign�l jeden soubor (Signal(1) a� Signal(n)) a zap�e do
	 * n�j hodnoty, kter� tomuto sign�lu p��slu��. Data jsou zapisov�na jako 4
	 * bytov� re�ln� ��sla.
	 * 
	 * @param outputFile
	 *          n�zev zapisovan�ho souboru, p��padn� i z celou cestou
	 * @param data
	 *          Objekt spole�n� vnit�n� datov� reprezentace
	 * @param minima
	 *          Nejmen�� nam��en� hodnota z ka�d�ho sign�lu
	 * @param maxima
	 *          Nejv�t�� nam��en� hodnota z ka�d�ho sign�lu
	 * @param count
	 *          Po�et nam��en�ch hodnot ka�d�ho sign�lu
	 * @throws IOException
	 * 
	 * @throws IOException
	 */
	private void writeBinary(Header head, Buffer buffer, String outputFile,
			float[] minima, float[] maxima, int[] counts) throws IOException {
		// pole hodnot v�ech sign�l�, pro dopl�ov�n� hodnot

		int[] numberOfDataRecord = new int[head.getNumberOfChannels()];
		double maxFrequency = 0.0;

		for (int i = 0; i < head.getNumberOfChannels(); i++) {
			if (maxFrequency <= head.getChannels().get(i).getFrequency()) {
				maxFrequency = head.getChannels().get(i).getFrequency();
			}
		}

		for (int i = 0; i < numberOfDataRecord.length; i++) {
			numberOfDataRecord[i] = (int) (maxFrequency / head.getChannels().get(i)
					.getFrequency());
		}

		int slashPosition = outputFile.lastIndexOf(File.separator);
		String outputPath = null;
		if (slashPosition != -1) {
			outputPath = outputFile.substring(0, slashPosition + 1);
		}

		int numberOfChannels = minima.length;
		// pole bufferedWriter�, ka�d� zapisuje do jednoho souboru (soubor
		// jednoho sign�lu)
		DataOutputStream[] dataOut = new DataOutputStream[numberOfChannels];
		Arrays.fill(minima, 0);
		Arrays.fill(maxima, 0);
		Arrays.fill(counts, 0);

		try {
			for (int index = 0; index < dataOut.length; index++) {
				if (outputPath == null) {// vytv��� soubory jednotliv�ch sign�l�

					dataOut[index] = new DataOutputStream(new FileOutputStream(
							NAME_OF_CHANNELS_FILE + (index + 1)));
				} else {
					dataOut[index] = new DataOutputStream(new FileOutputStream(outputPath
							+ File.separator + NAME_OF_CHANNELS_FILE + (index + 1)));
				}
			}

			int line = 0;
			float[] values;

			while ((values = buffer.getNextFrame()) != null) {
				for (int i = 0; i < values.length; i++) {
					if (line % numberOfDataRecord[i] == 0) {
						counts[i]++;
						// bufferedWriters[index].write(moment[index]); ukl�dat do xml
						// upravit
						if (values[i] < minima[i]) {
							minima[i] = values[i]; // ukl�d� nov� minimum
							// dan�ho sign�lu

						} else if (values[i] > maxima[i]) {
							maxima[i] = values[i]; // ukl�d� nov� maximum
							// dan�ho sign�lu

						}
						dataOut[i].writeFloat(values[i]);
					}
				}
				line++;
			}
		} catch (Exception e) {
			// System.out.println("Chyba p�i vytv��en� nebo z�pisu do souboru!");
			throw new IOException("Chyba p�i vytv��en� nebo z�pisu do souboru!, " + e
					+ ", " + this.getClass(), e);
		} finally {
			for (int index = 0; index < dataOut.length; index++) {
				try {
					if (dataOut[index] != null) {
						dataOut[index].close();
					}
				} catch (IOException e) {
					// System.out.println("Nepoda�ilo se uzav��t v�echny soubory, data
					// mohou b�t ne�pln�!");
					throw new IOException(
							"Nepoda�ilo se uzav��t v�echny soubory, data mohou b�t ne�pln�!, "
									+ e + ", " + this.getClass(), e);
				}
			}
		}
	}

	/**
	 * Metoda ma na starosti prevedeni z GregorianCalendar na datum m��en�.
	 * 
	 * @param Header
	 *          header
	 * @return dateInvestigation
	 */
	private String dateInvestigation(Header header) {
		String dateInvestigation = "";
		GregorianCalendar calendar = header.getDateOfAcquisition();

		dateInvestigation += calendar.get(Calendar.DAY_OF_MONTH) + "."
				+ (calendar.get(Calendar.MONTH) + 1) + "."
				+ calendar.get(Calendar.YEAR);

		return dateInvestigation;
	}

	/**
	 * Metoda ma na starosti prevedeni z GregorianCalendar na �as m��en�.
	 * 
	 * @param Header
	 *          header
	 * @return timeInvestigation
	 */
	private String timeInvestigation(Header header) {
		String timeInvestigation = "";
		GregorianCalendar calendar = header.getDateOfAcquisition();

		timeInvestigation += calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);

		return timeInvestigation;
	}

}
