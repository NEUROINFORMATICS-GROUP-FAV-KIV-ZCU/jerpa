package ch.ethz.origo.jerpa.data.perspective.signalprocess;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.data.Artefact;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Channel;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.NioInputStream;
import ch.ethz.origo.juigle.application.exception.ProjectOperationException;
import ch.ethz.origo.juigle.application.project.IProjectLoader;

/**
 * Class for setting project from file.
 * 
 * @author Jiri Kucera
 * @author Vaclav Souhrada
 * @version 0.1.2 (3/21/2010)
 * @since 0.1.0 (01/17/2010)
 */
public class SignalProjectLoader implements IProjectLoader {

	private File file;
	private Header header;
	private Buffer buffer;
	private SignalProject project;
	private List<Channel> channels;
	private int numberOfChannels;
	private List<Integer> averagedEpochsIndexes;
	private List<Integer> loadedEpochsIndexes;

	/**
	 * Konstruktor vytvo�� instanci t��dy nad so
	 * 
	 * @param file
	 */
	public SignalProjectLoader(File file) {
		this.file = file;
		header = new Header();
		project = new SignalProject();
		numberOfChannels = -1;
	}

	public SignalProject loadProject() throws ProjectOperationException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(file);

			extractProject(doc);
			loadData();
		} catch (SAXException ex) {
			throw new ProjectOperationException(new CorruptedFileException("SAXException at "
					+ SignalProjectLoader.class.getName()));
		} catch (ParserConfigurationException ex) {
			throw new ProjectOperationException(new CorruptedFileException("ParserConfigurationException at "
					+ SignalProjectLoader.class.getName()));
		} catch (NullPointerException e) {
			throw new ProjectOperationException(new CorruptedFileException("Error parsing meta-file."));
		} catch (NumberFormatException e) {
			throw new ProjectOperationException(new CorruptedFileException(e.getMessage()));
		} catch (CorruptedFileException e) {
			throw new ProjectOperationException("JERPA011", e);
		} catch (IOException e) {
			throw new ProjectOperationException("JERPA011", e);
		}

		if (project == null) {
			throw new ProjectOperationException("Project null.");
		} else {
			return project;
		}
	}

	/**
	 * Na�te data sign�l� z datov�ho souboru.
	 * 
	 * @throws java.io.IOException
	 * @throws cz.zcu.kiv.jerpstudio.data.formats.CorruptedFileException
	 */
	private void loadData() throws IOException, CorruptedFileException {
		long bytesToRead = header.getNumberOfChannels()
				* header.getNumberOfSamples() * 4;

		if (!project.getDataFile().isFile()) {
			throw new CorruptedFileException(project.getDataFile().getAbsolutePath()
					+ " not a file.");
		}

		if (project.getDataFile().length() != bytesToRead) {
			throw new CorruptedFileException("Wrong data file size: "
					+ project.getDataFile().length() + ", expected: " + bytesToRead + ".");
		}

		if (header == null) {
			new Exception("Header not set (null).");
			System.exit(1);
		}

		BufferCreator bc = new BufferCreator(header);
		NioInputStream ist = new NioInputStream(project.getDataFile());

		ist.seek(0);

		int readBytes = 0;
		while (ist.getRemaining() >= 4) {
			float value = ist.readFloat();
			bc.saveFloat(value);
			readBytes += 4;
		}

		buffer = bc.getBuffer();

		project.setBuffer(buffer);

		if (readBytes != bytesToRead) {
			buffer.closeBuffer();
			throw new CorruptedFileException("Wrong number of bytes read: "
					+ readBytes + ", expected: " + bytesToRead + ".");
		}

	}

	private String extractTextSubnode(Node rootNode) {
		String result = "";
		NodeList nl = rootNode.getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
				result = nl.item(i).getNodeValue().trim();
			}
		}

		return result;
	}

	private void extractProject(Document doc) throws CorruptedFileException {
		NodeList nl = doc.getFirstChild().getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("header")) {
				extractHeader(node);
			}

			if (nodeName.equals("channels")) {
				extractChannels(node);
			}

			if (nodeName.equals("epochs")) {
				extractEpochs(node);
			}

			if (nodeName.equals("name")) {
				extractName(node);
			}

			if (nodeName.equals("datafile")) {
				extractDataFile(node);
			}

			if (nodeName.equals("indexespath")) {
				extractIndexesPath(node);
			}

			if (nodeName.equals("epochsgrouping")) {
				extractEpochsGrouping(node);
			}

			if (nodeName.equals("averagetype")) {
				extractAverageType(node);
			}

			if (nodeName.equals("artefacts")) {
				extractArtefacts(node);
			}

			if (nodeName.equals("invertedview")) {
				extractInvertedView(node);
			}

			if (nodeName.equals("exportlook")) {
				extractExportLook(node);
			}
		}

		project.setHeader(header);
		project.setBuffer(null); // TODO

	}

	private void extractHeader(Node rootNode) throws CorruptedFileException {
		NodeList nl = rootNode.getChildNodes();

		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("numberofchannels")) {
				int value = Integer.parseInt(node.getChildNodes().item(0)
						.getNodeValue().trim());
				if (numberOfChannels == -1 || numberOfChannels == value) {
					numberOfChannels = value;
				} else {
					throw new CorruptedFileException("Wrong number of channels: " + value
							+ ", expected: " + numberOfChannels + ".");
				}
			}

			if (nodeName.equals("calendar")) {
				long value = Long.parseLong(node.getChildNodes().item(0).getNodeValue()
						.trim());
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTimeInMillis(value);
				header.setDateOfAcquisition(calendar);
			}

			if (nodeName.equals("docname")) {
				header.setDocName(extractTextSubnode(node));
			}

			if (nodeName.equals("length")) {
				header.setLength(extractTextSubnode(node));
			}

			if (nodeName.equals("numberofsamples")) {
				header.setNumberOfSamples(Integer.parseInt(extractTextSubnode(node)));
			}

			if (nodeName.equals("personalnumber")) {
				header.setPersonalNumber(extractTextSubnode(node));
			}

			if (nodeName.equals("samplinginterval")) {
				header.setSamplingInterval(Float.parseFloat(extractTextSubnode(node)));
			}

			if (nodeName.equals("segmentlength")) {
				header.setSegmentLength(Integer.parseInt(extractTextSubnode(node)));
			}

			if (nodeName.equals("subjectname")) {
				header.setSubjectName(extractTextSubnode(node));
			}

		}
		// System.out.println(header.toString());
	}

	private void extractChannels(Node rootNode) throws CorruptedFileException {
		NodeList nl = rootNode.getChildNodes();
		NamedNodeMap attributes = rootNode.getAttributes();

		List<Integer> selectedChannels = new ArrayList<Integer>();

		List<Integer> averagedSignals = new ArrayList<Integer>();

		int count = Integer.parseInt(attributes.getNamedItem("count")
				.getNodeValue().trim());

		if (numberOfChannels == -1 || numberOfChannels == count) {
			numberOfChannels = count;
		} else {
			throw new CorruptedFileException("Wrong number of channels: " + count
					+ ", expected: " + numberOfChannels + ".");
		}

		channels = new ArrayList<Channel>(numberOfChannels);
		boolean[] applyChanges = new boolean[numberOfChannels];

		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("channel")) {
				Channel channel = new Channel();
				attributes = node.getAttributes();
				int index = Integer.parseInt(attributes.getNamedItem("index")
						.getNodeValue().trim());

				if (channels.size() != index) {
					throw new CorruptedFileException("Wrong channel index: "
							+ Integer.parseInt(attributes.getNamedItem("index")
									.getNodeValue().trim()) + ", expected: " + channels.size()
							+ ".");
				}

				channel.setEdfPrefiltering(attributes.getNamedItem("prefiltering")
						.getNodeValue().trim());

				channel.setEdfTransducerType(attributes.getNamedItem("transducertype")
						.getNodeValue().trim());

				channel.setFrequencyOnly(Float.parseFloat(attributes.getNamedItem(
						"frequency").getNodeValue().trim()));

				channel.setNV_bit(Short.parseShort(attributes.getNamedItem("nvbit")
						.getNodeValue().trim()));

				channel.setName(attributes.getNamedItem("name").getNodeValue().trim());

				channel.setOriginal(attributes.getNamedItem("original").getNodeValue()
						.trim());

				channel.setPeriodOnly(Float.parseFloat(attributes
						.getNamedItem("period").getNodeValue().trim()));

				channel.setUnit(attributes.getNamedItem("unit").getNodeValue().trim());

				if (Boolean.valueOf(attributes.getNamedItem("selected").getNodeValue()
						.trim())) {
					selectedChannels.add(index);
				}

				if (Boolean.valueOf(attributes.getNamedItem("averaged").getNodeValue()
						.trim())) {
					averagedSignals.add(index);
				}

				applyChanges[index] = Boolean.valueOf(attributes.getNamedItem(
						"applychanges").getNodeValue().trim());

				channels.add(channel);

			}

		}

		project.setSelectedChannels(selectedChannels);
		project.setAveragedSignalsIndexes(averagedSignals);
		project.setApplyChanges(applyChanges);

		// for (Channel ch : channels) {
		// System.out.println(ch.toString());
		// }

		header.setChannels(channels);
	}

	private void extractEpochs(Node rootNode) throws CorruptedFileException {
		NodeList nl = rootNode.getChildNodes();
		NamedNodeMap attributes = rootNode.getAttributes();

		int epochsCount = Integer.parseInt(attributes.getNamedItem("count")
				.getNodeValue().trim());

		project.setLeftEpochBorderMs(Integer.parseInt(attributes.getNamedItem(
				"leftborderms").getNodeValue().trim()));
		project.setRightEpochBorderMs(Integer.parseInt(attributes.getNamedItem(
				"rightborderms").getNodeValue().trim()));
		project.setLeftEpochBorderF(Integer.parseInt(attributes.getNamedItem(
				"leftborderf").getNodeValue().trim()));
		project.setRightEpochBorderF(Integer.parseInt(attributes.getNamedItem(
				"rightborderf").getNodeValue().trim()));
		project.setCurrentEpochNumber(Integer.parseInt(attributes.getNamedItem(
				"current").getNodeValue().trim()));
		project.setEpochWalkingStep(Integer.parseInt(attributes.getNamedItem(
				"walkingstep").getNodeValue().trim()));
		project.setTimeSelectionBegin(Integer.parseInt(attributes.getNamedItem(
				"timeselectionbegin").getNodeValue().trim()));
		project.setTimeSelectionEnd(Integer.parseInt(attributes.getNamedItem(
				"timeselectionend").getNodeValue().trim()));

		List<Epoch> epochs = new ArrayList<Epoch>();
		averagedEpochsIndexes = new ArrayList<Integer>();
		loadedEpochsIndexes = new ArrayList<Integer>();

		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("epoch")) {
				Epoch epoch = extractEpoch(node);

				epochs.add(epoch);
			}

		}

		project.setAveragedEpochsIndexes(averagedEpochsIndexes);
		project.setLoadedEpochsIndexes(loadedEpochsIndexes);
		project.setAllEpochsList(epochs);

	}

	private Epoch extractEpoch(Node rootNode) throws CorruptedFileException {
		Epoch epoch = new Epoch(numberOfChannels);

		NodeList nl = rootNode.getChildNodes();
		NamedNodeMap attributes = rootNode.getAttributes();

		epoch.setChannelNumber(Integer.parseInt(attributes.getNamedItem(
				"channelnumber").getNodeValue().trim()));
		epoch.setDate(attributes.getNamedItem("date").getNodeValue().trim());
		epoch.setDescription(attributes.getNamedItem("description").getNodeValue()
				.trim());
		epoch.setLength(Integer.parseInt(attributes.getNamedItem("length")
				.getNodeValue().trim()));
		epoch.setPosition(Integer.parseInt(attributes.getNamedItem("position")
				.getNodeValue().trim()));
		epoch.setType(attributes.getNamedItem("type").getNodeValue().trim());

		if (Boolean.valueOf(attributes.getNamedItem("averaged").getNodeValue()
				.trim())) {
			averagedEpochsIndexes.add(Integer.valueOf(attributes
					.getNamedItem("index").getNodeValue().trim()));
		}

		if (Boolean
				.valueOf(attributes.getNamedItem("loaded").getNodeValue().trim())) {
			int index = Integer.valueOf(attributes.getNamedItem("index")
					.getNodeValue().trim());
			int value = Integer.valueOf(averagedEpochsIndexes.indexOf(index));
			if (value < 0) {
				throw new CorruptedFileException(
						"Loaded epoch index not found in averagedEpochsIndexes: " + index);
			}
			loadedEpochsIndexes.add(value);
		}

		int propertiesCount = Integer.parseInt(attributes.getNamedItem(
				"propertiescount").getNodeValue().trim());

		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("perchannelproperties")) {
				attributes = node.getAttributes();

				int index = Integer.parseInt(attributes.getNamedItem("index")
						.getNodeValue().trim());
				epoch.setEpochSelected(Boolean.parseBoolean(attributes.getNamedItem(
						"selected").getNodeValue().trim()), index);
				epoch.setEpochWeight(Integer.parseInt(attributes.getNamedItem("weight")
						.getNodeValue().trim()), index);

			}
		}

		return epoch;
	}

	private void extractExportLook(Node rootNode) {
		NodeList nl = rootNode.getChildNodes();
		NamedNodeMap attributes = rootNode.getAttributes();

		project.setModeOfRepresentationInExport(Integer.parseInt(attributes
				.getNamedItem("representationmode").getNodeValue().trim()));
		project.setExportViewersWidth(Integer.parseInt(attributes.getNamedItem(
				"exportviewerswidth").getNodeValue().trim()));
		project.setExportViewersHeight(Integer.parseInt(attributes.getNamedItem(
				"exportviewersheight").getNodeValue().trim()));
		project.setZoomY(Float.parseFloat(attributes.getNamedItem("zoomy")
				.getNodeValue().trim()));

		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("canvascolor")) {
				project.setCanvasColor(extractAndGetColor(node));
			}

			if (nodeName.equals("interpolationcolor")) {
				project.setInterpolationColor(extractAndGetColor(node));
			}

			if (nodeName.equals("axiscolor")) {
				project.setAxisColor(extractAndGetColor(node));
			}

			if (nodeName.equals("functionalvaluescolor")) {
				project.setFunctionalValuesColor(extractAndGetColor(node));
			}

			if (nodeName.equals("commentary")) {
				project.setCommentary(extractTextSubnode(node));
			}

		}
	}

	private Color extractAndGetColor(Node rootNode) {
		int r, g, b;

		r = Integer.parseInt(rootNode.getAttributes().getNamedItem("red")
				.getNodeValue().trim());
		g = Integer.parseInt(rootNode.getAttributes().getNamedItem("green")
				.getNodeValue().trim());
		b = Integer.parseInt(rootNode.getAttributes().getNamedItem("blue")
				.getNodeValue().trim());

		return new Color(r, g, b);
	}

	private void extractName(Node rootNode) {
		project.setName(extractTextSubnode(rootNode));
	}

	private void extractIndexesPath(Node rootNode) {
		project.setAbsolutePathToIndexes(extractTextSubnode(rootNode));
	}

	private void extractEpochsGrouping(Node rootNode) {
		NamedNodeMap attributes = rootNode.getAttributes();

		project.setGroupEpochWorkMode(Integer.parseInt(attributes.getNamedItem(
				"groupepochworkmode").getNodeValue().trim()));
		project.setLastUsedGroupEpochsType(Integer.parseInt(attributes
				.getNamedItem("lastusedgroupepochstype").getNodeValue().trim()));
	}

	private void extractDataFile(Node rootNode) {
		String filePath = file.getParentFile() + File.separator
				+ extractTextSubnode(rootNode);
		project.setDataFile(new File(filePath));
		// System.out.println("DF: " + project.getDataFile().getAbsolutePath());
	}

	private void extractInvertedView(Node rootNode) {
		project.setInvertedSignalsView(Boolean
				.parseBoolean(extractTextSubnode(rootNode)));
	}

	private void extractAverageType(Node rootNode) {
		project.setAverageType(Integer.parseInt(extractTextSubnode(rootNode)));
	}

	private void extractArtefacts(Node rootNode) {
		NodeList nl = rootNode.getChildNodes();

		List<Artefact> artefacts = new ArrayList<Artefact>();

		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			String nodeName = node.getNodeName();

			if (nodeName.equals("artefact")) {
				int start = Integer.parseInt(node.getAttributes().getNamedItem("start")
						.getNodeValue().trim());
				int end = Integer.parseInt(node.getAttributes().getNamedItem("end")
						.getNodeValue().trim());
				Artefact artefact = new Artefact(start, end);

				artefacts.add(artefact);
			}
		}

		project.setArtefactsList(artefacts);

	}
}
