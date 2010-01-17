package ch.ethz.origo.jerpa.data.formats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Channel;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.NioInputStream;

/**
 * Modul pro na��t�n� BrainStudio form�tu.
 * 
 * @author Tomas Rondik
 * @author Petr Soukal
 * @author Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * @see DataFormatLoader
 */
public class KivFormatLoader implements DataFormatLoader {

	/**
	 * Hlavi�kov� soubor
	 */
	private Header header;
	/**
	 * Koncovka souboru sign�lu, ve kter�m jsou hodnoty ulo�eny jako Ascii znaky
	 */
	private final String ASCII_FORMAT_EXTENSION = "txt";
	/**
	 * Prom�nn� den m��en�, p�es kterou se bude zapisovat do souboru.
	 */
	private String dayOfAcquisition = null;
	/**
	 * Promena cas mereni, pres kterou se bude zapisovat do souboru.
	 */
	private String timeOfAcquisition = null;
	private static final int MICROSECOND = 1000000;

	public KivFormatLoader() {
		header = new Header();
	}

	/**
	 * Nahr�v� data do hlavi�ky z popisn�ho .xml souboru, jeho� jm�no obsahuje
	 * objekt loader. Zji��uje, jestli jsou nam��en� hodnoty ulo�eny v bin�rn�m
	 * nebo ascii form�tu a podle toho vol� metody pro na�ten�.
	 * 
	 */
	@Override
	public Header load(BufferCreator loader) throws IOException,
			CorruptedFileException {
		List<String> channelFileNames = new ArrayList<String>();

		try {
			XMLInputFactory iFactory = XMLInputFactory.newInstance();
			XMLStreamReader stReader = iFactory.createXMLStreamReader(new FileReader(
					loader.getInputFile()));
			channelFileNames = new ArrayList<String>();
			float globalPeriod = Float.MAX_VALUE;
			long globalCount = 0;
			final char MARK_SUBJECT = 's';
			final char EMPTY_MARK = ' ';
			char mark = EMPTY_MARK;

			while (stReader.hasNext()) {
				stReader.next();

				if (stReader.isStartElement()) {

					if (stReader.getLocalName().equals("signal")) {
						Channel point = new Channel();
						channelFileNames.add(stReader.getAttributeValue(null, "filename")
								.trim());

						float frequency = Float.parseFloat(stReader.getAttributeValue(null,
								"frequency").trim());
						point.setFrequency(frequency);

						float period = Float.parseFloat(stReader.getAttributeValue(null,
								"period").trim());
						point.setPeriod(period * MICROSECOND);

						long count = Long.parseLong(stReader.getAttributeValue(null,
								"count").trim());

						if (globalPeriod >= period) {
							globalPeriod = period;
						}

						if (globalCount <= count) {
							globalCount = count;
						}

						// je�t� tam je min a max ale nev�m p�esn� co to je za hodnoty
						point.setOriginal(stReader.getAttributeValue(null, "original")
								.trim());
						point.setUnit(stReader.getAttributeValue(null, "units").trim());
						point.setName(stReader.getAttributeValue(null, "name").trim());
						// je�t� count ale nev�m jestli bude pot�eba

						header.addChannel(point);
					}

					if (stReader.getLocalName().equals("subject")) {
						mark = MARK_SUBJECT;
					}

					if (stReader.getLocalName().equals("name")) {
						if (mark == MARK_SUBJECT) {
							String subjectName = stReader.getElementText().trim();

							if (subjectName.length() == 0) {
								header.setSubjectName(null);
							} else {
								header.setSubjectName(subjectName);
							}
						} else {
							header.setDocName(stReader.getElementText().trim());
						}

						mark = EMPTY_MARK;
					}

					if (stReader.getLocalName().equals("date")) {
						dayOfAcquisition = stReader.getElementText().trim();
					}

					if (stReader.getLocalName().equals("time")) {
						timeOfAcquisition = stReader.getElementText().trim();
					}

					if (stReader.getLocalName().equals("length")) {
						header.setLength(stReader.getElementText().trim());
					}
				}
			}

			header.setSamplingInterval((int) (globalPeriod * MICROSECOND));
			header.setNumberOfSamples(globalCount);

			if (timeOfAcquisition != null && dayOfAcquisition != null) {
				header.setDateOfAcquisition(transformTimeAndDayToCalendar(
						dayOfAcquisition, timeOfAcquisition));
			}

			/*
			 * ____________________________________________
			 */
			String[] parsedNameOfChannel = channelFileNames.get(0).split("[.]");

			if ((parsedNameOfChannel.length == 2)
					&& (parsedNameOfChannel[parsedNameOfChannel.length - 1]
							.equals(ASCII_FORMAT_EXTENSION))) {
				loadAscii(loader, channelFileNames);
			} // kdy� jsou data v souborech jednotliv�ch sign�l� ulo�ena jako 4 bytov�
				// re�ln� ��sla
			else if (parsedNameOfChannel.length == 1) {
				loadBinary(loader, channelFileNames);
			} // jinak
			else {
				// System.out.println("Nezn�m� form�t datov�ch soubor� sign�l�!");
				throw new CorruptedFileException(
						"Nezn�m� form�t datov�ch soubor� sign�l�!" + this.getClass());
			}
		} catch (XMLStreamException e) {
			throw new CorruptedFileException("XML parsing error.");
		} catch (IOException e) {
			throw e;
		} catch (CorruptedFileException e) {
			throw e;
		} catch (Exception e) {
			throw new CorruptedFileException("Probably not a KIV EEG file.");
		}

		return header;
	}

	/**
	 * Ukl�d� data ze soubor� (kde jsou data ulo�ena jako 4 bytov� re�ln� ��sla)
	 * v�ech sign�l� do do�asn�ho souboru.
	 * 
	 * @param ChannelFileNames
	 *          Jm�na soubor� s hodnotami z jednotliv�ch kan�l�
	 * @throws IOException
	 */
	private void loadBinary(BufferCreator loader, List<String> ChannelFileNames)
			throws IOException {
		// pole hodnot v�ech sign�l�, pro dopl�ov�n� hodnot
		int[] numberOfDataRecord = new int[header.getNumberOfChannels()];
		double maxFrequency = 0.0;
		String inputFile = loader.getInputFile().getAbsolutePath();

		for (int i = 0; i < header.getNumberOfChannels(); i++) {
			if (maxFrequency <= header.getChannels().get(i).getFrequency()) {
				maxFrequency = header.getChannels().get(i).getFrequency();
			}
		}

		for (int i = 0; i < numberOfDataRecord.length; i++) {
			numberOfDataRecord[i] = (int) (maxFrequency / header.getChannels().get(i)
					.getFrequency());
		}

		int slashPosition = inputFile.lastIndexOf(File.separator);
		String inputPath = null;
		if (slashPosition != -1) {
			inputPath = inputFile.substring(0, slashPosition + 1);
		}

		// pole vstupn�ch proud� od jednotliv�ch soubor�
		NioInputStream[] dataIn = new NioInputStream[header.getNumberOfChannels()];
		// inicializace pole vstupn�ch proud�
		for (int index = 0; index < dataIn.length; index++) {
			if (inputPath == null) {
				dataIn[index] = new NioInputStream(ChannelFileNames.get(index),
						ByteOrder.LITTLE_ENDIAN); // getName();

			} else {
				dataIn[index] = new NioInputStream(inputPath
						+ ChannelFileNames.get(index), ByteOrder.LITTLE_ENDIAN);
			}
		}

		float[] previousValues = new float[dataIn.length];
		// proch�z� do nejv�t��ho po�tu sn�mk�
		for (int i = 0; i < header.getNumberOfSamples(); i++) {
			// pole hodnot v�ech sign�l� v jednom �asov�m okam�iku
			float[] values = new float[dataIn.length];
			for (int index = 0; index < dataIn.length; index++) {
				if (i % numberOfDataRecord[index] == 0) {
					values[index] = dataIn[index].readFloat();
				} else {
					values[index] = previousValues[index];
				}

			}

			previousValues = values;
			// vkl�d� pole hodnot jednoho �asov�ho okam�iku do
			// spole�n�ho datov�ho form�tu
			try {
				loader.saveFrame(values);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < dataIn.length; i++) {
			// dataIn[i].close();
		}
	}

	/**
	 * Ukl�d� data ze soubor� (kde jsou data ulo�ena jako ascii znaky) v�ech
	 * sign�l� do do�asn�ho souboru.
	 * 
	 * @param ChannelFileNames
	 *          Jm�na soubor� s hodnotami z jednotliv�ch kan�l�
	 * @throws Exception
	 */
	private void loadAscii(BufferCreator loader, List<String> ChannelFileNames)
			throws IOException {
		// pole hodnot v�ech sign�l�, pro dopl�ov�n� hodnot
		int[] numberOfDataRecord = new int[header.getNumberOfChannels()];
		double maxFrequency = 0.0;
		String inputFile = loader.getInputFile().getAbsolutePath();

		for (int i = 0; i < header.getNumberOfChannels(); i++) {
			if (maxFrequency <= header.getChannels().get(i).getFrequency()) {
				maxFrequency = header.getChannels().get(i).getFrequency();
			}
		}

		for (int i = 0; i < numberOfDataRecord.length; i++) {
			numberOfDataRecord[i] = (int) (maxFrequency / header.getChannels().get(i)
					.getFrequency());
		}

		int slashPosition = inputFile.lastIndexOf(File.separator);
		String inputPath = null;
		if (slashPosition != -1) {
			inputPath = inputFile.substring(0, slashPosition + 1);
		}

		// pole �te�ek
		BufferedReader[] bufferedReaders = new BufferedReader[header
				.getNumberOfChannels()];

		try {
			for (int index = 0; index < bufferedReaders.length; index++) {
				if (inputPath == null) {
					bufferedReaders[index] = new BufferedReader(new FileReader(
							ChannelFileNames.get(index)));
				} else {
					bufferedReaders[index] = new BufferedReader(new FileReader(inputPath
							+ ChannelFileNames.get(index)));
				}
			}

			String value;
			Float floatValue;
			float[] previousValues = new float[bufferedReaders.length];

			// proch�z� do nejv�t��ho po�tu sn�mk�
			for (int i = 0; i < header.getNumberOfSamples(); i++) {
				// pole hodnot v�ech sign�l� v jednom �asov�m okam�iku
				float[] values = new float[bufferedReaders.length];
				for (int index = 0; index < bufferedReaders.length; index++) {
					if (i % numberOfDataRecord[index] == 0) {
						value = bufferedReaders[index].readLine();
						floatValue = Float.parseFloat(value);
						values[index] = floatValue.floatValue();
					} else {
						values[index] = previousValues[index];
					}

				}

				previousValues = values;
				// vkl�d� pole hodnot jednoho �asov�ho okam�iku do
				// spole�n�ho datov�ho form�tu

				try {
					loader.saveFrame(values);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			for (int i = 0; i < bufferedReaders.length; i++) {
				bufferedReaders[i].close();
			}
		} catch (IOException e) {
			throw new IOException("Chyba uvnit� KIVForm�tu v metod� loadAscii(...), "
					+ e + ", " + this.getClass(), e); // FIXME

		}
	}

	/**
	 * Metoda ma na starosti prevedeni stringu casu a stringu dne mereni na
	 * GregorianCalendar.
	 * 
	 * @param String
	 *          date
	 * @param String
	 *          time
	 * @return gregorianCalendar
	 */
	private GregorianCalendar transformTimeAndDayToCalendar(String date,
			String time) {

		String[] datesArray = date.split("[:. ]");
		String[] timesArray = time.split("[:.]");

		GregorianCalendar gregorianCalendar = new GregorianCalendar(Integer
				.valueOf(datesArray[2]), Integer.valueOf(datesArray[1]) - 1, Integer
				.valueOf(datesArray[0]), Integer.valueOf(timesArray[0]), Integer
				.valueOf(timesArray[1]), Integer.valueOf(timesArray[2]));

		return gregorianCalendar;
	}

	@Override
	public ArrayList<Epoch> getEpochs() {
		return new ArrayList<Epoch>();
	}
}
