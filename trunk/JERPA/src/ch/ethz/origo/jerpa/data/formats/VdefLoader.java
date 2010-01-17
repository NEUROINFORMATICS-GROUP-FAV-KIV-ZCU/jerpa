package ch.ethz.origo.jerpa.data.formats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Channel;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.NioInputStream;

/**
 * T��da VdefLoader obsahuje v�echny metody pro parsov�n� vstupn�ch soubor� a
 * na��t�n� hodnot z EEG souboru.
 * 
 * @author Tuma Lukas (ok.lukas at seznam.cz)
 * @author Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * @see DataFormatLoader
 * 
 */
public class VdefLoader implements DataFormatLoader {

	private ArrayList<ChannelDataSet> signals;
	private ArrayList<Epoch> markers;
	private HashMap<String, String> headerKeyNames;
	private HashMap<String, String> epochKeyNames;
	private File headerFile;
	private String markerFile;
	private String eegFile;
	private byte[] data;
	private int channelLines;
	private Header header;
	private BufferCreator loader;
	private long numberOfSamples;

	/**
	 * Konstruktor t��dy VdefLoader
	 */
	public VdefLoader() {
		header = new Header();
		this.signals = new ArrayList<ChannelDataSet>();
		this.markers = new ArrayList<Epoch>();
		this.headerKeyNames = new HashMap<String, String>();
		this.epochKeyNames = new HashMap<String, String>();
		// this.headerFile = headerFile;
		channelLines = 0;
		numberOfSamples = 0;
	}

	/**
	 * Nastavuje defaultn� hodnoty pro kl��ov� slova, kter� nebyla v header
	 * souboru zad�na.
	 */
	private void loadDefaultValues() {
		if (!this.headerKeyNames.containsKey("DataFormat")) {
			this.headerKeyNames.put("DataFormat", "ASCII");
		}

		if (!this.headerKeyNames.containsKey("DataOrientation")) {
			this.headerKeyNames.put("DataOrientation", "MULTIPLEXED");
		}

		if (this.headerKeyNames.get("DataFormat").equals("BINARY")) {
			if (!this.headerKeyNames.containsKey("BinaryFormat")) {
				this.headerKeyNames.put("BinaryFormat", "INT_16");
			}

			if (!this.headerKeyNames.containsKey("UseBigEndianOrder")) {
				this.headerKeyNames.put("UseBigEndianOrder", "NO");
			}
		}
	}

	/**
	 * Metoda pro parsov�n� header souboru.
	 */
	private void parseHeader() throws IOException {
		try {
			// File fileHeader = new File(this.headerFile);
			FileReader fr = new FileReader(headerFile);
			BufferedReader in = new BufferedReader(fr);
			String line = "";

			in.readLine();

			while ((line = in.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				} else if (line.charAt(0) == ';') {
					continue;
				} else if (line.trim().equals("[Comment]")) {
					break;
				} else if (line.indexOf("[") != -1 && line.indexOf("]") != -1) {
					continue;
				} else if (line.startsWith("Ch") && (int) line.charAt(2) >= (int) '0'
						&& (int) line.charAt(2) <= (int) '9') {
					this.signals.add(createChannelDataSet(line));
					channelLines++;
				} else {
					String[] pole = line.split("=");
					this.headerKeyNames.put(pole[0], pole[1]);
				}
			}
			fr.close();
			if (this.headerKeyNames.containsKey("MarkerFile")) {
				this.markerFile = this.headerKeyNames.get("MarkerFile");
				if (this.markerFile.indexOf("$b") >= 0) {
					this.markerFile = this.markerFile.replace("$b", this.headerFile
							.getAbsolutePath().substring(0,
									this.headerFile.getAbsolutePath().indexOf(".")));
				}
			}
			this.eegFile = this.headerKeyNames.get("DataFile");
			if (this.eegFile.indexOf("$b") >= 0) {
				this.eegFile = this.eegFile.replace("$b", this.headerFile
						.getAbsolutePath().substring(0,
								this.headerFile.getAbsolutePath().indexOf(".")));
			}
		} catch (IOException e) {
			throw new IOException("Error parsing header file: "
					+ headerFile.getAbsolutePath());
		}
	}

	/**
	 * Vytvo�� objekt t��dy ChannelDataSet a ulo�� ho do seznamu.
	 * 
	 * @param line
	 *          �et�zec, ze kter�ho se z�skaj� informace o kan�lu.
	 */
	private ChannelDataSet createChannelDataSet(String line) {
		if (line.indexOf("\u00C2") != -1) {
			line = line.split("\u00C2")[0] + line.split("\u00C2")[1];
		}
		ChannelDataSet ch = new ChannelDataSet();
		String[] pole = line.split(",");
		ch.name = pole[0].split("=")[1];
		ch.reference = pole[1];
		ch.resolution = Float.valueOf(pole[2]).floatValue();
		ch.unit = pole[3];
		return ch;
	}

	/**
	 * Metoda pro parsov�n� marker souboru.
	 */
	private void parseMarker() throws IOException {
		try {
			File fileMarker = new File(this.markerFile);
			if (!fileMarker.exists()) {
				this.markerFile = this.headerFile.getAbsolutePath().substring(0,
						this.headerFile.getAbsolutePath().lastIndexOf("\\") + 1)
						+ this.markerFile;
				fileMarker = new File(this.markerFile);
			}
			FileReader fr = new FileReader(fileMarker);
			BufferedReader in = new BufferedReader(fr);
			String line = "";

			in.readLine();

			while ((line = in.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				} else if (line.charAt(0) == ';') {
					continue;
				} else if (line.trim().equals("[Comment]")) {
					break;
				} else if (line.indexOf("[") != -1 && line.indexOf("]") != -1) {
					continue;
				} else if (line.startsWith("Mk") && (int) line.charAt(2) >= (int) '0'
						&& (int) line.charAt(2) <= (int) '9') {
					createMarker(line);
				} else {
					String[] pole = line.split("=");
					this.epochKeyNames.put(pole[0], pole[1]);
				}
			}
			fr.close();
		} catch (IOException e) {
			throw new IOException("Error parsing marker file: " + markerFile);
		}
	}

	/**
	 * Vytvo�� objekt t��dy Marker a ulo�� ho do seznamu.
	 * 
	 * @param line
	 *          �et�zec, ze kter�ho se z�skaj� informace o markeru.
	 */
	private void createMarker(String line) {
		String[] pole = line.split(",");
		Epoch m = new Epoch(signals.size());
		// m.setName(pole[0].split("=")[0]);
		m.setType(pole[0].split("=")[1]);
		m.setDescription(pole[1]);
		m.setPosition(Integer.parseInt(pole[2]));
		m.setLength(Byte.parseByte(pole[3]));
		m.setChannelNumber(Byte.parseByte(pole[4]));
		this.markers.add(m);
	}

	/**
	 * Metoda na�te bin�rn� EEG soubor do pole bajt�.
	 */
	private void loadBinaryEEG() {
		File fileEEG = new File(this.eegFile);
		if (!fileEEG.exists()) {
			this.eegFile = this.headerFile.getAbsolutePath().substring(0,
					this.headerFile.getAbsolutePath().lastIndexOf("\\") + 1)
					+ this.eegFile;
			fileEEG = new File(this.eegFile);
		}
		long length = fileEEG.length();
		byte[] bytess = new byte[(int) length];
		// InputStream input;
		NioInputStream input;

		try {
			input = new NioInputStream(this.eegFile);
			if (length > Integer.MAX_VALUE) {
				this.data = new byte[0];
				return;
			}
			int offset = 0;
			int numRead = 0;
			while (offset < bytess.length
					&& (numRead = input.read(bytess, offset, bytess.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytess.length) {
				throw new IOException("Could not completely read file "
						+ fileEEG.getName());
			}
			// input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.data = bytess;
	}

	/**
	 * Metoda ur�uje kam se budou jednotliv� hodnoty ukl�dat a vol� metody pro
	 * p�evod bajt� na hodnoty.
	 */
	private void loadBinaryValues() {
		int numberOfBytes;
		if (this.headerKeyNames.get("BinaryFormat").equals("IEEE_FLOAT_32")) {
			numberOfBytes = 4;
		} else {
			numberOfBytes = 2;
		}

		int numberOfValues = this.data.length
				/ (Integer.parseInt(this.headerKeyNames.get("NumberOfChannels")) * numberOfBytes);
		for (int i = 0; i < this.signals.size(); i++) {
			this.signals.get(i).values = new float[numberOfValues];
		}

		if (this.headerKeyNames.get("DataOrientation").equals("MULTIPLEXED")) {
			int row = 0;
			float value = 0;
			for (int i = 0; i < this.data.length; i += numberOfBytes) {
				if (numberOfBytes == 2) {
					byte b1 = this.data[i];
					byte b2 = this.data[i + 1];
					if (this.headerKeyNames.get("BinaryFormat").equals("INT_16")) {
						if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
							value = valueINT16(b1, b2);
						} else {
							value = valueINT16(b2, b1);
						}
					} else {
						if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
							value = valueUINT16(b1, b2);
						} else {
							value = valueUINT16(b2, b1);
						}
					}
				} else {
					byte b1 = this.data[i];
					byte b2 = this.data[i + 1];
					byte b3 = this.data[i + 2];
					byte b4 = this.data[i + 3];
					value = valueFLOAT32(b4, b3, b2, b1);
				}
				value *= signals.get(row).resolution;

				this.signals.get(row).addValue(value);
				if (row == Integer
						.parseInt(this.headerKeyNames.get("NumberOfChannels")) - 1) {
					row = 0;
				} else {
					row++;
				}
			}
		} else {
			int index = 0;
			float value = 0;
			for (int x = 0; x < this.signals.size(); x++) {
				for (int i = 0; i < numberOfValues; i++) {
					if (numberOfBytes == 2) {
						byte b1 = this.data[index++];
						byte b2 = this.data[index++];
						if (this.headerKeyNames.get("BinaryFormat").equals("INT_16")) {
							if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
								value = valueINT16(b1, b2);
							} else {
								value = valueINT16(b2, b1);
							}
						} else {
							if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
								value = valueUINT16(b1, b2);
							} else {
								value = valueUINT16(b2, b1);
							}
						}
					} else {
						byte b1 = this.data[index++];
						byte b2 = this.data[index++];
						byte b3 = this.data[index++];
						byte b4 = this.data[index++];
						value = valueFLOAT32(b4, b3, b2, b1);
						// if (this.headerKeyNames.get("UseBigEndianOrder").equals("YES")) {
						// value = valueFLOAT32(b1, b2, b3, b4);
						// } else {
						// value = valueFLOAT32(b2, b1, b4, b3);
						// }
					}

					value *= signals.get(x).resolution;
					this.signals.get(x).addValue(value);
				}
			}
		}
	}

	/**
	 * Z�sk� UINT_16 hodnotu z dvou bajt�.
	 * 
	 * @param b1
	 *          Prvn� bajt hodnoty.
	 * @param b2
	 *          Druh� bajt hodnoty.
	 * @return Vrac� hodnotu typu float.
	 */
	private float valueUINT16(byte b1, byte b2) {
		return (b1 < 0) ? ((b1 + 256) << 8) | b2 & 0x00ff : b1 << 8 | b2 & 0x00ff;
	}

	/**
	 * Z�sk� INT_16 hodnotu z dvou bajt�.
	 * 
	 * @param b1
	 *          Prvn� bajt hodnoty.
	 * @param b2
	 *          Druh� bajt hodnoty.
	 * @return Vrac� hodnotu typu float.
	 */
	private float valueINT16(byte b1, byte b2) {
		return b1 << 8 | b2 & 0x00ff;
	}

	/**
	 * Z�sk� FLOAT_32 hodnotu ze �ty� bajt�.
	 * 
	 * @param b1
	 *          Prvn� bajt hodnoty.
	 * @param b2
	 *          Druh� bajt hodnoty.
	 * @param b3
	 *          T�et� bajt hodnoty.
	 * @param b4
	 *          �tvrt� bajt hodnoty.
	 * @return Vrac� hodnotu typu float.
	 */
	private float valueFLOAT32(byte b1, byte b2, byte b3, byte b4) {
		return Float.intBitsToFloat(b1 << 24 | (b2 & 0xff) << 16 | (b3 & 0xff) << 8
				| (b4 & 0xff));
	}

	/**
	 * Vytvo�� pole float hodnot p�edstavuj�c� jednotliv� frame a ode�le ho metod�
	 * saveFrame.
	 */
	private void sendFrames() throws IOException {
		for (int x = 0; x < this.signals.get(0).values.length; x++) {
			float[] array = new float[this.signals.size()];
			for (int y = 0; y < array.length; y++) {
				array[y] = this.signals.get(y).values[x];
			}
			loader.saveFrame(array);
			numberOfSamples++;
		}
		header.setNumberOfSamples(numberOfSamples);
	}

	/**
	 * Metoda na�te ASCII EEG soubor.
	 */
	private void loadAsciiEEG() {
		// NOT IMPLEMENTED YET
	}

	/**
	 * T��da reprezentuje jednotliv� kan�l. Obsahuje informace o kan�lu a v�echny
	 * jeho nam��en� hodnoty.
	 * 
	 * @author T�ma Luk� (ok.lukas@seznamm.cz)
	 */
	public class ChannelDataSet {

		private String name;
		private String reference;
		private float resolution;
		private String unit;
		private float[] values;
		private int size;

		/**
		 * Konstruktor t��dy ChannelDataSet.
		 */
		private ChannelDataSet() {
			this.name = "";
			this.reference = "";
			this.resolution = 0;
			this.unit = "";
			this.size = 0;
		}

		/**
		 * P�id� hodnotu do pole na dan� index.
		 * 
		 * @param value
		 *          P�id�van� hodnota
		 */
		private void addValue(float value) {
			this.values[size] = value;
			this.size++;
		}
	}

	@Override
	public Header load(BufferCreator loader) throws IOException,
			CorruptedFileException {
		this.loader = loader;
		headerFile = loader.getInputFile();

		parseHeader();
		loadDefaultValues();
		if (this.markerFile != null) {
			parseMarker();
		}
		if (this.headerKeyNames.get("DataFormat").equals("BINARY")) {
			// System.out.println("loading binary EEG");
			loadBinaryEEG();

			// System.out.println("decoding binary Values");
			loadBinaryValues();
		} else {
			loadAsciiEEG();
		}
		// System.out.println("Sending frames");
		sendFrames();

		String dataType = headerKeyNames.get("DataType");

		if (dataType == null || dataType.equals("TIMEDOMAIN")) {
			header.setSamplingInterval(Float.parseFloat(headerKeyNames
					.get("SamplingInterval")));
		} else if (headerKeyNames.get("DataFormat").equals("FREQUENCYDOMAIN")) {
			header.setSamplingInterval(1f / Float.parseFloat(headerKeyNames
					.get("SamplingInterval")));
		}

		setChannelsInfo();

		return header;
	}

	private void setChannelsInfo() throws CorruptedFileException {

		int numberOfChannels = Integer.parseInt(headerKeyNames
				.get("NumberOfChannels"));

		if (numberOfChannels < 0) {
			throw new CorruptedFileException("Number of channels not specified.");
		}

		if (numberOfChannels != channelLines) {
			throw new CorruptedFileException(
					"Number of channels not equal to channel line entries");
		}

		List<Channel> channels = new ArrayList<Channel>();

		for (int i = 0; i < numberOfChannels; i++) {
			Channel channel = new Channel();

			channel.setName(signals.get(i).name);
			channel.setUnit(signals.get(i).unit);

			String dataType = headerKeyNames.get("DataType");

			if (dataType == null || dataType.equals("TIMEDOMAIN")) {
				channel.setPeriod(Float.parseFloat(headerKeyNames
						.get("SamplingInterval")));
			} else if (headerKeyNames.get("DataFormat").equals("FREQUENCYDOMAIN")) {
				channel.setFrequency(1f / Float.parseFloat(headerKeyNames
						.get("SamplingInterval")));
			}

			channels.add(channel);
		}

		header.setChannels(channels);

	}

	@Override
	public ArrayList<Epoch> getEpochs() {
		return markers;
	}
}
