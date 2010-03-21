package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import java.awt.Container;
import java.awt.print.Printable;
import java.util.List;

import javax.swing.JPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;

/**
 * Abstraktn� t��da definuj�c� jednu str�nku exportovan�ho dokumentu. V
 * exportovan�m dokumentu se mohou vyskytnou t�i druhy str�nek:
 * <ol>
 * <li>Prvn� str�nka - obsahuje hlavi�kov� informace o vstupn�m souboru a tolik
 * pr�m�r�, kolik se na n� vejde.
 * <li>Vnit�� str�nka - obsahuje tolik pr�m�r�, kolik se na n� vejde.
 * <li>Posledn� str�nka - obsahuje u�ivatl�v koment��, informace o aplikaci a
 * tolik pr�m�r�, kolik se na n� vejde. Tato abstraktn� t��da definuje str�nku
 * takov�m zp�sobem, aby instance t��dy, kter� je jej�m potomkem (a tedy v
 * exportovan�m dokumentu pou�itelnou str�nkou) mohla b�t kteroukoliv z
 * uveden�ch str�nek.
 * </ol>
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
abstract class Page extends JPanel implements Printable {
	/**
	 * Data pot�ebn� pro export pr�m�r� pro v�echny kan�ly (tj. nejen pro kan�ly
	 * na t�to str�nce).
	 */
	List<EpochDataSet> epochsDataSet;
	/**
	 * Rozhran� pro komunikaci mezi prezenta�n� a aplika�n� vrstvou.
	 */
	ExportFrameProvider provider;
	/**
	 * Kontejner obsahuj�c� hlavi�kov� informace o vstupn�m souboru.
	 */
	Container headerInfo;
	/**
	 * Kontejner obsahuj�c� koment�� vytvo�en�ch pr�m�r� a informace o aplikaci.
	 */
	Container footerInfo;

	/**
	 * Konstruktor abstraktn� t��dy.
	 * 
	 * @param provider
	 *          Rozhran� pro komunikaci mezi prezenta�n� a aplika�n� vrstvou.
	 * @param epochsDataSet
	 *          Data pot�ebn� pro export pr�m�r� pro v�echny kan�ly.
	 * @param headerInfo
	 *          Kontejner obsahuj�c� hlavi�kov� informace o vstupn�m souboru.
	 *          Pokud m� b�t str�nka vnit�n� �i posledn� str�nkou, pak je tento
	 *          parametr <b>null</b>.
	 * @param footerInfo
	 *          Kontejner obsahuj�c� koment�� vytvo�en�ch pr�m�r� a informace o
	 *          aplikaci. Pokud m� b�t str�nka prvn� �i vnit�n� str�nkou, pak je
	 *          tento parametr <b>null</b>.
	 */
	Page(ExportFrameProvider provider, List<EpochDataSet> epochsDataSet,
			Container headerInfo, Container footerInfo) {
		super();
		this.provider = provider;
		this.epochsDataSet = epochsDataSet;
		this.headerInfo = headerInfo;
		this.footerInfo = footerInfo;
	}
}
