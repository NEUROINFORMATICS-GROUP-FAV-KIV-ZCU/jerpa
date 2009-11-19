package ch.ethz.origo.jerpa.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.origo.jerpa.application.Const;

/**
 * Nahr�v� ze vstupn�ho souboru indexy epoch, kter� maj� b�t zahrnuty/odstran�ny
 * do/z pr�m�r�.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 */
public class IndexesLoader {
	/**
	 * Odd�lova� index� epoch ve vstupn�m souboru.
	 */
	private String indexesSeparator;

	/**
	 * Vytv��� instanci t��dy. Do atributu <i>indexesSeparator</i> nastavuje
	 * nej�ast�ji pou��van� separ�tory:
	 * <code>indexesSeparator = new String("[,; ]");</code>.
	 */
	public IndexesLoader() {
		indexesSeparator = new String("[,; ]"); // nej�ast�ji pou��van� separ�tory
	}

	/**
	 * Na��t� indexy epoch pro hromadn� zahrnov�n�/odstra�ov�n� do/z pr�m�r� ze
	 * vstupn�ho souboru.
	 * 
	 * @param file
	 *          Soubor s indexy epoch.
	 * @return List index� epoch, kter� se maj� zahrnout/odstranit do/z pr�m�r�.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<Integer> loadIndexes(File file) throws FileNotFoundException,
			IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<Integer> indexes = new ArrayList<Integer>();

		String line = reader.readLine();

		while (line != null) {
			String[] parsed = line.split(indexesSeparator);

			for (String string : parsed) {
				/*
				 * Pro p��pad, �e by jako odd�lova� index� byla pou�ita n�jak� n-tice
				 * znak�. Nap�. ", ".
				 */
				if (string.length() > 0) {
					indexes.add(Integer.valueOf(string) - Const.ZERO_INDEX_SHIFT);
				}
			}

			line = reader.readLine();
		}

		reader.close();
		return indexes;
	}

	/**
	 * Vrac� referenci na atribut <i>indexesSeparator</i>.
	 * 
	 * @return Obecn� n-tice znak�, kter� bude pou�ita pro parsov�n� souboru s
	 *         indexy.
	 */
	public String getIndexesSeparator() {
		return indexesSeparator;
	}

	/**
	 * Nastavuje referenci na atribut <i>indexesSeparator</i>.
	 * 
	 * @param indexesSeparator
	 *          Obecn� n-tice znak�, kter� bude pou�ita pro parsov�n� souboru s
	 *          indexy.
	 */
	public void setIndexesSeparator(String indexesSeparator) {
		if (indexesSeparator != null)
			this.indexesSeparator = indexesSeparator;
	}
}
