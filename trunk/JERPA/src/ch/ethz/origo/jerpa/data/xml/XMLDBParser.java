package ch.ethz.origo.jerpa.data.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (2/7/2010)
 * @since 0.1.0 (2/7/2010)
 * 
 */
public class XMLDBParser {

	public void parser(File file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			builder.setErrorHandler(new XMLDBErrorHandler());
			Document doc = builder.parse(file);
			parseAndCreateDatabase(doc);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void parseAndCreateDatabase(Document doc) {
				
	}
}
