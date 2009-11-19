package ch.ethz.origo.jerpa.data.formats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;

import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Channel;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;

/**
 * T��da slou�� k testov�n�. Generuje voliteln� po�et funkc� sinus s volitelnou
 * frekvenc� fram� a d�lkou periody funkce. D�le vytvo�� plnohodnotnou hlavi�ku,
 * jej� hodnoty jsou tvo�eny konstantami t��dy. <b>Tuto t��du pou�ije loader pro
 * na�ten� fiktivn�ho vstupn�ho souboru pr�v� tehdy, pokud je mu p�ed�n
 * libovoln� soubor s p��ponou <i>generator</i><b>.
 * 
 * @author Tomas Rondik (jERP Studio)
 * Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * 
 */
public class GeneratorLoader implements DataFormatLoader {

	/**
	 * Instance t��dy Header nesouc� hlavi�kov� informace.
	 */
	private Header header;
	// ZACATEK - �daje hlavi�ky souboru
	/*
	 * U atribut� a konstant, kter� nemaj� na zobrazen� pr�b�hu generovan�ho
	 * sign�lu ��dn� vliv je toto v�slovn� uvedeno.
	 */
	/**
	 * Informace o jednotlivych kanalech.
	 */
	private ArrayList<Channel> channels;
	/**
	 * Po�et sign�l�.
	 */
	private static final int NUMBER_OF_CHANNELS = 5;
	/**
	 * Pocet vzorku celkem.
	 */
	private static final long NUMBER_OF_SAMPLES = 32768;
	/**
	 * Zv�t�en� sign�lu v funk�n�ch hodnot.
	 */
	private static final int VALUES_EXPANSION = 100;

	/**
	 * Rok po��zen� z�znamu.
	 */
	private static final int YEAR = 0;
	/**
	 * M�s�c po��zen� z�znamu.
	 */
	private static final int MONTH = 0;
	/**
	 * Den po��zen� z�znamu.
	 */
	private static final int DAY = 0;
	/**
	 * Hodina po��zen� z�znamu.
	 */
	private static final int HOUR = 0;
	/**
	 * Minuta po��zen� z�znamu.
	 */
	private static final int MINUTE = 0;
	/**
	 * Sekunda po��zen� z�znamu.
	 */
	private static final int SECOND = 0;
	// KONEC - �daje o �ase, kdy byl z�znam po��zen
	/**
	 * Po�et sn�mk� za sekundu.
	 */
	private static final int FREQUENCY = 1000;
	/**
	 * D�lka periody funkce v sekund�ch.
	 */
	private static final int FUNCTION_PERIOD = 10;
	/**
	 * Delka segmentu.
	 */
	/*
	 * Nem� na zobrazen� pr�b�hu generovan�ho sign�lu ��dn� vliv
	 */
	private static final int SEGMENT_LENGTH = 0;
	/**
	 * ��slo, kter� p�esn� identifikuje m��enou osobu (n�j�ast�ji rodn� ��slo).
	 */
	/*
	 * Nem� na zobrazen� pr�b�hu generovan�ho sign�lu ��dn� vliv
	 */
	private static final String PERSONAL_NUMBER = "112233/4455";
	/**
	 * Jm�no a p��jmen� (v tomto po�ad�) m��en� osoby (pacienta) v jednom �et�zci
	 * odd�len� mezerou
	 */
	/*
	 * Nem� na zobrazen� pr�b�hu generovan�ho sign�lu ��dn� vliv
	 */
	private static final String SUBJECT_NAME = "Jan Nov�k";
	/**
	 * Jm�no a p��jmen� l�ka�e v jednom �et�zci
	 */
	/*
	 * Nem� na zobrazen� pr�b�hu generovan�ho sign�lu ��dn� vliv
	 */
	private static final String DOC_NAME = "Josef Trnka";
	/**
	 * D�lka m��en�. Form�t �asu t�to konstanty nen� p�esn� specifikov�n.
	 */
	/*
	 * Nem� na zobrazen� pr�b�hu generovan�ho sign�lu ��dn� vliv.
	 */
	private static final String LENGTH = ""; // 

	// KONEC - �daje hlavi�ky souboru

	private Random generator;

	/**
	 * Konstruktor.
	 */
	public GeneratorLoader() {
		header = new Header();
		generator = new Random();
	}

	/**
	 * Metoda volan� instanc� t��dy loader pro na�ten� vstupn�ho souboru. Jej�
	 * implementace je vynucen� implementov�n�m rozhran� DataFormatLoader.
	 */
	@Override
	public Header load(BufferCreator loader) throws IOException {
		try {
			// ZACATEK - vytvo�en� informac� o kan�lech
			channels = new ArrayList<Channel>();

			for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
				Channel channel = new Channel();
				channel.setFrequency(FREQUENCY);
				channel.setName("channel " + (i + 1));

				channels.add(channel);
			}

			// header.setCalendar(new GregorianCalendar(YEAR, MONTH, DAY, HOUR,
			// MINUTE, SECOND));
			header.setDateOfAcquisition(new GregorianCalendar());
			header.setDocName(DOC_NAME);
			header.setChannels(channels);
			header.setNumberOfSamples(NUMBER_OF_SAMPLES);
			header.setPersonalNumber(PERSONAL_NUMBER);
			header.setSamplingInterval(1000000 / FREQUENCY); // FREQUENCY je v
																												// sekund�ch, sampling
																												// interval je v
																												// milisekund�ch

			header.setSegmentLength(SEGMENT_LENGTH);
			header.setSubjectName(SUBJECT_NAME);

			// KONEC - vytvo�en� informac� o kan�lech

			// ZACATEK - generov�n� funk�n�ch hodnot sign�l�
			float[] values = new float[channels.size()];
			double angle = (2 * Math.PI) / FUNCTION_PERIOD;
			double actualAngle = 0;

			for (int i = 0; i < NUMBER_OF_SAMPLES; i++) {
				for (int j = 1; j < values.length - 2; j++) {
					values[j] = (float) (Math.sin(actualAngle)
							* Math.sin(actualAngle / 10) * Math.sin(actualAngle / 100) * VALUES_EXPANSION);
				}

				values[0] = (float) (Math.sin(actualAngle) * Math.sin(actualAngle / 10) * VALUES_EXPANSION);
				values[values.length - 2] = (float) (Math.sin(actualAngle)
						* Math.sin(actualAngle / (generator.nextInt(10) + 5))
						* Math.sin(actualAngle / (generator.nextInt(100) + 50)) * VALUES_EXPANSION);
				values[values.length - 1] = (float) actualAngle / 50;

				// System.out.println(Arrays.toString(values));
				loader.saveFrame(values);
				actualAngle += angle;
			}
			// KONEC - generov�n� funk�n�ch hodnot sign�l�

			return header;
		} catch (Exception e) {
			// TODO - tohle je spatne
			e.printStackTrace();
			throw new IOException("Chyba v metod� load t��dy Generator.java");
		}
	}

	@Override
	public ArrayList<Epoch> getEpochs() {
		ArrayList<Epoch> markers = new ArrayList<Epoch>();

		for (long pos = generator.nextInt(3000) + 3000; pos < NUMBER_OF_SAMPLES; pos += generator
				.nextInt(3000) + 3000) {
			Epoch marker = new Epoch(NUMBER_OF_CHANNELS);
			marker.setChannelNumber(0);
			marker.setDescription("Random marker");
			marker.setLength(1);
			marker.setType("Some type");
			marker.setPosition(pos);
			markers.add(marker);
		}

		return markers;
	}
}
