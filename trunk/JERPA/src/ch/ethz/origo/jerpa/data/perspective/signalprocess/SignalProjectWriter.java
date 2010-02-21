package ch.ethz.origo.jerpa.data.perspective.signalprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.data.Artefact;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.Channel;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.NioOutputStream;
import ch.ethz.origo.juigle.application.exception.ProjectWriterException;
import ch.ethz.origo.juigle.application.project.IProjectWriter;

/**
 * T��da pro ukl�d�n� projektu do souboru.
 * 
 * @author Jiri Kucera
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (2/21/2010)
 * @since 0.1.0 (2/21/2010)
 * @see IProjectWriter
 */
public class SignalProjectWriter implements IProjectWriter {

	private SignalProject project;
	private String encoding;
	private String cfgFile;
	
	public SignalProjectWriter() {
		encoding = "utf-8";
		cfgFile = "jerpproject.xml";		
	}

	public SignalProjectWriter(SignalProject project) {
		this();
		this.project = project;
	}

	public void saveProject() throws ProjectWriterException {
		NioOutputStream ost;
		Buffer buffer = project.getBuffer();
		String outputDataFilePath = project.getProjectFile().getAbsolutePath()
				+ ".dat";
		File outputDataFile = new File(outputDataFilePath);
		project.setDataFile(outputDataFile);

		try {
			Document doc = makeDocument();
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setAttribute("indent-number", 2);
			Transformer writer = tf.newTransformer();
			writer.setOutputProperty(OutputKeys.INDENT, "yes");
			writer.setOutputProperty("encoding", encoding);
			writer.transform(new DOMSource(doc), new StreamResult(
					new OutputStreamWriter(
							new FileOutputStream(project.getProjectFile()), Charset
									.forName(encoding))));
			ost = new NioOutputStream(outputDataFilePath);
		} catch (TransformerException ex) {
			throw new ProjectWriterException(new CorruptedFileException("JERPA018:"
					+ SignalProjectWriter.class.getName(), ex));
		} catch (ParserConfigurationException ex) {
			throw new ProjectWriterException(new CorruptedFileException("JERPA018:"
					+ SignalProjectWriter.class.getName(), ex));
		} catch (FileNotFoundException e) {
			throw new ProjectWriterException("JERPA017:" + outputDataFilePath, e);
		}

		try {
			buffer.seek(0);

			while (buffer.remaining() > 0) {
				ost.writeFloat(buffer.getFloat());
			}
		} catch (IOException ex) {
			throw new ProjectWriterException("JERPA019", ex);
		} finally {
			try {
				ost.close();
			} catch (IOException e1) {
				throw new ProjectWriterException("JERPA019", e1);
			}
		}
	}

	private Document makeDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		Document doc = impl.createDocument(null, "jerpproject", null);
		Node projectNode = doc.getDocumentElement();

		projectNode.appendChild(getHeaderElement(doc));

		projectNode.appendChild(getChannelsElement(doc));

		projectNode.appendChild(getEpochsElement(doc));

		projectNode.appendChild(getNameElement(doc));

		projectNode.appendChild(getDataFileElement(doc));

		projectNode.appendChild(getAbsolutePathToIndexesElement(doc));

		projectNode.appendChild(getAverageTypeElement(doc));

		projectNode.appendChild(getArtefactsElement(doc));

		projectNode.appendChild(getEpochsGrouping(doc));

		projectNode.appendChild(getInvertedSignalsView(doc));

		projectNode.appendChild(getExportLook(doc));

		return doc;
	}

	private Element getExportLook(Document doc) {
		Element element = doc.createElement("exportlook");

		element.setAttribute("representationmode", String.valueOf(project
				.getModeOfRepresentationInExport()));
		element.setAttribute("exportviewerswidth", String.valueOf(project
				.getExportViewersWidth()));
		element.setAttribute("exportviewersheight", String.valueOf(project
				.getExportViewersHeight()));
		element.setAttribute("zoomy", String.valueOf(project.getZoomY()));

		Element subElement;

		subElement = doc.createElement("commentary");
		subElement.appendChild(doc.createTextNode(project.getCommentary()));
		element.appendChild(subElement);

		subElement = doc.createElement("canvascolor");
		subElement.setAttribute("red", String.valueOf(project.getCanvasColor()
				.getRed()));
		subElement.setAttribute("green", String.valueOf(project.getCanvasColor()
				.getGreen()));
		subElement.setAttribute("blue", String.valueOf(project.getCanvasColor()
				.getBlue()));
		element.appendChild(subElement);

		subElement = doc.createElement("interpolationcolor");
		subElement.setAttribute("red", String.valueOf(project
				.getInterpolationColor().getRed()));
		subElement.setAttribute("green", String.valueOf(project
				.getInterpolationColor().getGreen()));
		subElement.setAttribute("blue", String.valueOf(project
				.getInterpolationColor().getBlue()));
		element.appendChild(subElement);

		subElement = doc.createElement("axiscolor");
		subElement.setAttribute("red", String.valueOf(project.getAxisColor()
				.getRed()));
		subElement.setAttribute("green", String.valueOf(project.getAxisColor()
				.getGreen()));
		subElement.setAttribute("blue", String.valueOf(project.getAxisColor()
				.getBlue()));
		element.appendChild(subElement);

		subElement = doc.createElement("functionalvaluescolor");
		subElement.setAttribute("red", String.valueOf(project
				.getFunctionalValuesColor().getRed()));
		subElement.setAttribute("green", String.valueOf(project
				.getFunctionalValuesColor().getGreen()));
		subElement.setAttribute("blue", String.valueOf(project
				.getFunctionalValuesColor().getBlue()));
		element.appendChild(subElement);

		return element;
	}

	private Element getArtefactsElement(Document doc) {
		Element element = doc.createElement("artefacts");

		List<Artefact> artefacts = project.getArtefactsList();

		for (int i = 0; i < artefacts.size(); i++) {
			Element artefactElement = doc.createElement("artefact");
			Artefact artefact = artefacts.get(i);

			artefactElement.setAttribute("index", String.valueOf(i));
			artefactElement.setAttribute("start", String.valueOf(artefact
					.getArtefactStart()));
			artefactElement.setAttribute("end", String.valueOf(artefact
					.getArtefactEnd()));

			element.appendChild(artefactElement);
		}

		return element;
	}

	private Element getAverageTypeElement(Document doc) {
		Element element = doc.createElement("averagetype");

		element.appendChild(doc.createTextNode(String.valueOf(project
				.getAverageType())));

		return element;
	}

	private Element getNameElement(Document doc) {
		Element element = doc.createElement("name");

		element.appendChild(doc.createTextNode(project.getName()));

		return element;
	}

	private Element getEpochsElement(Document doc) {
		Element element;

		element = doc.createElement("epochs");
		element.setAttribute("leftborderms", String.valueOf(project
				.getLeftEpochBorderMs()));
		element.setAttribute("rightborderms", String.valueOf(project
				.getRightEpochBorderMs()));
		element.setAttribute("leftborderf", String.valueOf(project
				.getLeftEpochBorderF()));
		element.setAttribute("rightborderf", String.valueOf(project
				.getRightEpochBorderF()));
		element.setAttribute("current", String.valueOf(project
				.getCurrentEpochNumber()));
		element.setAttribute("walkingstep", String.valueOf(project
				.getEpochWalkingStep()));
		element.setAttribute("timeselectionbegin", String.valueOf(project
				.getTimeSelectionBegin()));
		element.setAttribute("timeselectionend", String.valueOf(project
				.getTimeSelectionEnd()));

		int count = project.getAllEpochsList().size();
		element.setAttribute("count", String.valueOf(count));

		for (int i = 0; i < count; i++) {
			Element epochElement = doc.createElement("epoch");
			Epoch epoch = project.getAllEpochsList().get(i);

			epochElement.setAttribute("index", String.valueOf(i));
			epochElement.setAttribute("date", epoch.getDate());
			epochElement.setAttribute("description", epoch.getDescription());
			epochElement.setAttribute("type", epoch.getType());
			epochElement.setAttribute("channelnumber", String.valueOf(epoch
					.getChannelNumber()));
			epochElement.setAttribute("length", String.valueOf(epoch.getLength()));
			epochElement
					.setAttribute("position", String.valueOf(epoch.getPosition()));
			epochElement.setAttribute("propertiescount", String.valueOf(epoch
					.getWeights().length));

			boolean averaged = project.getAveragedEpochsIndexes().contains(i);
			boolean loaded = false;

			epochElement.setAttribute("averaged", String.valueOf(averaged));

			if (averaged) {
				loaded = project.getLoadedEpochsIndexes().contains(
						project.getAveragedEpochsIndexes().indexOf(i));
			}

			epochElement.setAttribute("loaded", String.valueOf(loaded));

			for (int j = 0; j < epoch.getWeights().length; j++) {
				Element channelPropertiesElement = doc
						.createElement("perchannelproperties");

				channelPropertiesElement.setAttribute("index", String.valueOf(j));
				channelPropertiesElement.setAttribute("weight", String.valueOf(epoch
						.getWeights()[j]));
				channelPropertiesElement.setAttribute("selected", String.valueOf(epoch
						.getSelected()[j]));

				epochElement.appendChild(channelPropertiesElement);
			}

			element.appendChild(epochElement);
		}

		return element;
	}

	private Element getDataFileElement(Document doc) {
		Element element = doc.createElement("datafile");

		if (project.getDataFile() == null) {
			new UnsupportedOperationException(
					"Saving non-external data file project not supported yet.")
					.printStackTrace();
		} else {
			element.appendChild(doc.createTextNode(project.getDataFile().getName()));
		}

		return element;
	}

	private Element getAbsolutePathToIndexesElement(Document doc) {
		Element element = doc.createElement("indexespath");

		if (project.getAbsolutePathToIndexes() == null) {

		} else {
			element.appendChild(doc
					.createTextNode(project.getAbsolutePathToIndexes()));
		}

		return element;
	}

	private Element getEpochsGrouping(Document doc) {
		Element element = doc.createElement("epochsgrouping");

		element.setAttribute("groupepochworkmode", String.valueOf(project
				.getGroupEpochWorkMode()));
		element.setAttribute("lastusedgroupepochstype", String.valueOf(project
				.getLastUsedGroupEpochsType()));

		// element.appendChild(doc.createTextNode(String.valueOf(project.getGroupEpochWorkMode())));

		return element;
	}

	private Element getInvertedSignalsView(Document doc) {
		Element element = doc.createElement("invertedview");

		element.appendChild(doc.createTextNode(String.valueOf(project
				.isInvertedSignalsView())));

		return element;
	}

	private Element getChannelsElement(Document doc) {
		List<Channel> channels = project.getHeader().getChannels();
		Element channelsElement = doc.createElement("channels");

		List<Integer> selectedChannels = project.getSelectedChannels();
		List<Integer> averagedSignals = project.getAveragedSignalsIndexes();
		boolean[] applyChanges = project.getApplyChanges();

		channelsElement.setAttribute("count", String.valueOf(channels.size()));

		for (int i = 0; i < channels.size(); i++) {
			Element channelElement = doc.createElement("channel");
			Channel channel = channels.get(i);

			channelElement.setAttribute("selected", String.valueOf(selectedChannels
					.contains(i)));
			channelElement.setAttribute("averaged", String.valueOf(averagedSignals
					.contains(i)));
			channelElement.setAttribute("applychanges", String
					.valueOf(applyChanges[i]));

			channelElement.setAttribute("index", String.valueOf(i));
			channelElement.setAttribute("name", channel.getName());
			channelElement.setAttribute("original", channel.getOriginal());
			channelElement.setAttribute("frequency", String.valueOf(channel
					.getFrequency()));
			channelElement
					.setAttribute("period", String.valueOf(channel.getPeriod()));
			channelElement.setAttribute("unit", channel.getUnit());
			channelElement.setAttribute("prefiltering", channel.getEdfPrefiltering());
			channelElement.setAttribute("transducertype", channel
					.getEdfTransducerType());
			channelElement.setAttribute("nvbit", String.valueOf(channel.getNV_bit()));

			channelsElement.appendChild(channelElement);
		}

		return channelsElement;
	}

	private Element getHeaderElement(Document doc) {
		Header header = project.getHeader();
		Element headerElement = doc.createElement("header");

		Element element;

		element = doc.createElement("numberofchannels");
		element.appendChild(doc.createTextNode(String.valueOf(header
				.getNumberOfChannels())));
		headerElement.appendChild(element);

		element = doc.createElement("calendar");
		element.appendChild(doc.createTextNode(String.valueOf(header
				.getDateOfAcquisition().getTimeInMillis())));
		headerElement.appendChild(element);

		element = doc.createElement("docname");
		element
				.appendChild(doc.createTextNode(String.valueOf(header.getDocName())));
		headerElement.appendChild(element);

		element = doc.createElement("length");
		element.appendChild(doc.createTextNode(String.valueOf(header.getLength())));
		headerElement.appendChild(element);

		element = doc.createElement("numberofsamples");
		element.appendChild(doc.createTextNode(String.valueOf(header
				.getNumberOfSamples())));
		headerElement.appendChild(element);

		element = doc.createElement("personalnumber");
		element.appendChild(doc.createTextNode(String.valueOf(header
				.getPersonalNumber())));
		headerElement.appendChild(element);

		element = doc.createElement("samplinginterval");
		element.appendChild(doc.createTextNode(String.valueOf(header
				.getSamplingInterval())));
		headerElement.appendChild(element);

		element = doc.createElement("segmentlength");
		element.appendChild(doc.createTextNode(String.valueOf(header
				.getSegmentLength())));
		headerElement.appendChild(element);

		element = doc.createElement("subjectname");
		element.appendChild(doc.createTextNode(String.valueOf(header
				.getSubjectName())));
		headerElement.appendChild(element);

		return headerElement;
	}
}
