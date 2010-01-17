package ch.ethz.origo.jerpa.data.formats;

import java.io.IOException;
import java.util.ArrayList;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;

/**
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.0.1 (11/25/09)
 * @since 0.1.0 (11/25/09)
 * @see DataFormatLoader
 */
public class DatabaseFormatLoader implements DataFormatLoader {

	@Override
	public ArrayList<Epoch> getEpochs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Header load(BufferCreator loader) throws IOException,
			CorruptedFileException {
		// TODO Auto-generated method stub
		return null;
	}

}
