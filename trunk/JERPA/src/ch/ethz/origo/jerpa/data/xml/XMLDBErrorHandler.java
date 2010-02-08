package ch.ethz.origo.jerpa.data.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (2/7/2010)
 * @since 0.1.0 (2/7/2010)
 * @see ErrorHandler
 */
public class XMLDBErrorHandler implements ErrorHandler {

	@Override
	public void error(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
		
	}

}
