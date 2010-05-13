/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *  
 *    Copyright (C) 2009 - 2010 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.application.perspective.signalprocess;

import java.util.ArrayList;
import java.util.Collections;

import ch.ethz.origo.jerpa.data.Artefact;
import ch.ethz.origo.jerpa.data.Epoch;
import ch.ethz.origo.jerpa.data.Header;

/**
 * T��da pro uchov�v�n� dat o v�b�ru epoch a artefakt�.
 * 
 * @author Petr - Soukal (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * 
 */
public class SignalsSegmentation {
	
	private int EPOCH_CENTER = 1, EPOCH_SPACE = -1;
	private int LEFT_ARTEFACT = -1, RIGHT_ARTEFACT = 1;
	
	private SignalSessionManager appCore;
	private Header header;

	private int startEpoch, endEpoch;// interval kolik framu vzit p�ed st�edem a
	// za st�edem epochy
	private ArrayList<Epoch> epochs;
	private ArrayList<Artefact> artefacts;
	private int[] epochsDraw;
	private boolean[] artefactsDraw;

	/**
	 * Vytv��� objekt t��dy a instancuje appCore.
	 * 
	 * @param appCore
	 *          - instance objektu hlavn� ��d�c� t��dy aplika�n� vrstvy.
	 */
	public SignalsSegmentation(SignalSessionManager appCore) {
		this.appCore = appCore;
	}

	/**
	 * Rozhoduje o tom zda se m�e epocha vlo�it na dan� m�sto, pokud ano tak
	 * epochu vlo��.
	 * 
	 * @param xAxis
	 *          - parametr ud�vaj�c� hodnotu framu p�i kliknut� na
	 *          drawingComponentu, kam by se m�la epocha vlo�it.
	 * @return true - pokud lze epochu na dann� m�sto vlo�it.<br/>
	 *         false - pokud nelze epochu na dann� m�sto vlo�it. *
	 */
	public boolean selectEpoch(int xAxis) {
		if (header == null) {
			return false;
		}

		int frameCenterEpoch = xAxis;

		if (frameCenterEpoch >= startEpoch
				&& frameCenterEpoch < header.getNumberOfSamples() - endEpoch) {
			if (epochsDraw[frameCenterEpoch] == 0
					&& epochsDraw[frameCenterEpoch + endEpoch] == 0
					&& epochsDraw[frameCenterEpoch - startEpoch] == 0) {
				epochsDraw[frameCenterEpoch] = EPOCH_CENTER;
				epochs.add(new Epoch(header.getNumberOfChannels(), frameCenterEpoch));

				for (int i = frameCenterEpoch - startEpoch; i < frameCenterEpoch; i++) {
					epochsDraw[i] = EPOCH_SPACE;
				}

				for (int i = frameCenterEpoch + 1; i <= frameCenterEpoch + endEpoch; i++) {
					epochsDraw[i] = EPOCH_SPACE;
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * Ma�e epochu z dann�ho m�sta.
	 * 
	 * @param xAxis
	 *          - parametr ud�vaj�c� hodnotu framu p�i kliknut� na
	 *          drawingComponentu, kde by se m�la epocha smazat.
	 * @return true - pokud se epocha na dan�m m�st� nach�z� a byla smaz�na.<br/>
	 *         false - pokud se epocha na dan�m m�st� nenach�z� a proto nebyla
	 *         smaz�na.
	 */
	public boolean unselectEpoch(int xAxis) {
		if (header == null) {
			return false;
		}

		if (epochsDraw[xAxis] != 0) {
			int indexCenterEpoch = searchingIndexCenterEpoch(xAxis);

			Collections.sort(epochs);
			int foundIndex = Collections.binarySearch(epochs, new Epoch(header
					.getNumberOfChannels(), indexCenterEpoch));
			epochs.remove(foundIndex);

			for (int i = indexCenterEpoch - startEpoch; i <= (indexCenterEpoch + endEpoch); i++) {
				epochsDraw[i] = 0;
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Hled� st�ed epochy p�ekr�vaj�c� dan� frame.
	 * 
	 * @param xAxis
	 *          - parametr ud�vaj�c� hodnotu framu p�i kliknut� na
	 *          drawingComponentu.
	 * @return index - index st�edu epochy, kter� zasahuje do dan�ho framu.
	 */
	private int searchingIndexCenterEpoch(int xAxis) {
		int index;
		int terminal = xAxis + startEpoch;

		if (terminal >= header.getNumberOfSamples()) {
			terminal = (int) header.getNumberOfSamples() - 1;
		}

		for (index = xAxis; index <= terminal; index++) {
			if (epochsDraw[index] == EPOCH_CENTER) {
				return index;
			}
		}

		for (index = xAxis - endEpoch; index < xAxis; index++) {
			if (epochsDraw[index] == EPOCH_CENTER) {
				break;
			}
		}

		return index;
	}

	/**
	 * Vkl�d� hodnotu true do pole pro vykreslen� artefaktu v intervalu
	 * startArtefact, endArtefact.
	 * 
	 * @param startArtefact
	 *          - po��te�n� hodnota artefaktu.
	 * @param endArtefact
	 *          - kone�n� hodnota artefaktu.
	 */
	public void selectArtefact(int startArtefact, int endArtefact) {
		if (header == null) {
			return;
		}

		if (startArtefact < 0) {
			startArtefact = 0;
		}
		if (endArtefact >= header.getNumberOfSamples()) {
			endArtefact = (int) (header.getNumberOfSamples() - 1);
		}

		for (int i = startArtefact; i <= endArtefact; i++) {
			artefactsDraw[i] = true;
		}

		insertArtefactInArrayList(startArtefact, endArtefact);
	}

	/**
	 * Vkl�d� hodnotu false do pole pro vykreslen� artefaktu v intervalu start,
	 * end.
	 * 
	 * @param start
	 *          - po��te�n� hodnota maz�n� artefaktu.
	 * @param end
	 *          - kone�n� hodnota maz�n� artefaktu.
	 */
	public void unselectArtefact(int start, int end) {
		if (header == null) {
			return;
		}

		if (start < 0) {
			start = 0;
		}
		if (end >= header.getNumberOfSamples()) {
			end = (int) (header.getNumberOfSamples() - 1);
		}

		for (int i = start; i <= end; i++) {
			artefactsDraw[i] = false;
		}

		deleteArtefactFromArrayList(start, end);
	}

	/**
	 * Vkl�d� artefakty do ArrayListu pro ukl�d�n� dat v projektu.
	 * 
	 * @param startArtefact
	 *          - po��te�n� hodnota maz�n� artefaktu.
	 * @param endArtefact
	 *          - kone�n� hodnota maz�n� artefaktu.
	 */
	private void insertArtefactInArrayList(int startArtefact, int endArtefact) {
		if (artefacts.size() > 0) {
			int index = 0;

			for (; index < artefacts.size(); index++)// vkl�d�n� a �azen� artefaktu
			{
				if (startArtefact <= artefacts.get(index).getArtefactStart()) {
					artefacts.add(index, new Artefact(startArtefact, endArtefact));
					break;
				}
			}

			if (index == artefacts.size())// pokud nebyl artefakt za�azen
			{
				artefacts.add(new Artefact(startArtefact, endArtefact));
			}

			for (int j = index + 1; j < artefacts.size(); j++) {
				if (startArtefact <= artefacts.get(j).getArtefactStart()// smaz�n� v�ech
						// artefakt� v
						// dan�m
						// intervalu
						&& endArtefact >= artefacts.get(j).getArtefactEnd()) {
					artefacts.remove(j);
					j--;
				} else {
					break;
				}
			}

			if (index > 0)// spojen� aktu�ln�ho artefaktu a lev�ho artefaktu
			{
				if (artefacts.get(index - 1).getArtefactEnd() + 1 >= startArtefact
						&& artefacts.get(index - 1).getArtefactEnd() + 1 <= endArtefact) {
					artefacts.get(index).setArtefactStart(
							artefacts.get(index - 1).getArtefactStart());
					artefacts.remove(index - 1);
					index--;
				} else if (artefacts.get(index - 1).getArtefactEnd() + 1 >= startArtefact
						&& artefacts.get(index - 1).getArtefactEnd() + 1 >= endArtefact) {
					artefacts.remove(index);
					return;
				}
			}

			if (index < artefacts.size() - 1)// spojen� aktu�ln�ho artefaktu a
			// prav�ho artefaktu
			{
				if (artefacts.get(index + 1).getArtefactStart() - 1 <= endArtefact) {
					artefacts.get(index).setArtefactEnd(
							artefacts.get(index + 1).getArtefactEnd());
					artefacts.remove(index + 1);
				}
			}
		} else// vlo�en� prvn�ho artefaktu
		{
			artefacts.add(new Artefact(startArtefact, endArtefact));
		}

	}

	/**
	 * Ma�e, nebo zkracuje artefakty z ArrayListu pro ukl�d�n� dat v projektu.
	 * 
	 * @param startArtefact
	 *          - po��te�n� hodnota maz�n� artefaktu.
	 * @param endArtefact
	 *          - kone�n� hodnota maz�n� artefaktu.
	 */
	private void deleteArtefactFromArrayList(int start, int end) {
		if (artefacts.size() > 0) {
			for (int i = 0; i < artefacts.size(); i++) {
				if (start <= artefacts.get(i).getArtefactStart()// smaz�n� v�ech
						// artefakt� v dan�m
						// intervalu
						&& end >= artefacts.get(i).getArtefactEnd()) {
					artefacts.remove(i);
					i--;
				}
			}

			int index = 0;
			int spaceArtefact = 0;

			for (; index < artefacts.size(); index++)// hled�n� prav�ho nebo lev�ho
			// artefaktu, ovliv�uj�c� dan�
			// interval
			{
				if (start > artefacts.get(index).getArtefactStart()
						&& start <= artefacts.get(index).getArtefactEnd()) {
					spaceArtefact = LEFT_ARTEFACT;
					break;
				} else if (start <= artefacts.get(index).getArtefactStart()
						&& end >= artefacts.get(index).getArtefactStart()) {
					spaceArtefact = RIGHT_ARTEFACT;
					break;
				}
			}

			if (spaceArtefact == LEFT_ARTEFACT) {
				if (end < artefacts.get(index).getArtefactEnd())// maz�n� st�edu
				// artefaktu a rozd�len�
				// na dva
				{
					Artefact artefact1 = new Artefact(artefacts.get(index)
							.getArtefactStart(), start - 1);
					Artefact artefact2 = new Artefact(end + 1, artefacts.get(index)
							.getArtefactEnd());

					artefacts.remove(index);
					artefacts.add(index, artefact2);
					artefacts.add(index, artefact1);
					index++;
				} else// zkr�cen� artefaktu na konci
				{
					artefacts.get(index).setArtefactEnd(start - 1);
				}

				if (index < artefacts.size() - 1)// zda existuje prav� artefakt
				{
					if (end >= artefacts.get(index + 1).getArtefactStart())// zda je
					// ovlivn�n
					// dan�m
					// intervalem
					{
						artefacts.get(index + 1).setArtefactStart(end + 1);// zkr�cen�
						// artefaktu na
						// za��tku
					}

				}
			} else if (spaceArtefact == RIGHT_ARTEFACT) {
				artefacts.get(index).setArtefactStart(end + 1);// zkr�cen� artefaktu na
				// za��tku
			}
		}
	}

	/**
	 * Vytv��� ArrayListy a nastavuje pole podle �daj� o velikosti m��en�ch dat ze
	 * souboru.
	 */
	public void setSegmentArrays() {
		if ((header = appCore.getCurrentHeader()) == null) {
			return;
		}

		startEpoch = 0;
		endEpoch = 0;
		epochs = new ArrayList<Epoch>();
		artefacts = new ArrayList<Artefact>();
		epochsDraw = new int[(int) (header.getNumberOfSamples())];
		artefactsDraw = new boolean[(int) (header.getNumberOfSamples())];
	}

	/**
	 * Metoda nastavuj�c� p�edn� rozmez� epoch.
	 * 
	 * @param start
	 *          - p�edn� rozmez� epochy.
	 * @return true - pokud lze nastavit rozmez� epoch.<br/>
	 *         false - pokud rozmez� nejde na dan� paramtr nastavit.
	 */
	public boolean setLeftEpochBorder(int start) {
		// �sek zji��ov�n�, zda se v�bec m�e rozmez� epoch zv�t�it aby se
		// nep�ekr�vali
		boolean collision = false;

		if (epochs.size() != 0) {
			Collections.sort(epochs);

			if (epochs.get(0).getPosition() < start) {
				collision = true;
			}

			for (int i = 0; i < epochs.size() - 1; i++) {
				if ((epochs.get(i + 1).getPosition() - epochs.get(i).getPosition()) <= (start + endEpoch)) {
					collision = true;
					break;
				}
			}
		}

		if (!collision) {
			if (start < startEpoch) {
				for (Epoch centerEp : epochs) {
					for (int i = (int) centerEp.getPosition() - startEpoch; i < centerEp
							.getPosition()
							- start; i++) {
						epochsDraw[i] = 0;
					}
				}
			} else if (start > startEpoch) {
				for (Epoch centerEp : epochs) {
					for (int i = (int) centerEp.getPosition() - start; i < centerEp
							.getPosition()
							- startEpoch; i++) {
						epochsDraw[i] = -1;
					}
				}
			}

			startEpoch = start;

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Metoda nastavuj�c� zadn� rozmez� epoch.
	 * 
	 * @param end
	 *          - zadn� rozmez� epochy.
	 * @return true - pokud lze nastavit rozmez� epoch.<br/>
	 *         false - pokud rozmez� nejde na dan� paramtr nastavit.
	 */
	public boolean setRightEpochBorder(int end) {
		// �sek zji��ov�n�, zda se v�bec m�e rozmez� epoch zv�t�it aby se
		// nep�ekr�vali
		boolean collision = false;

		if (epochs.size() != 0) {
			Collections.sort(epochs);

			if (epochs.get(epochs.size() - 1).getPosition() + end >= header
					.getNumberOfSamples()) {
				collision = true;
			}

			for (int i = 0; i < epochs.size() - 1; i++) {
				if ((epochs.get(i + 1).getPosition() - epochs.get(i).getPosition()) <= (startEpoch + end)) {
					collision = true;
					break;
				}
			}
		}

		if (!collision) {
			if (end < endEpoch) {
				for (Epoch centerEp : epochs) {
					for (int i = (int) centerEp.getPosition() + end + 1; i <= centerEp
							.getPosition()
							+ endEpoch; i++) {
						epochsDraw[i] = 0;
					}
				}
			} else if (end > endEpoch) {
				for (Epoch centerEp : epochs) {
					for (int i = (int) centerEp.getPosition() + endEpoch + 1; i <= centerEp
							.getPosition()
							+ end; i++) {
						epochsDraw[i] = -1;
					}
				}
			}
			endEpoch = end;

			return true;
		} else {
			return false;
		}

	}

	/**
	 * P�id�v� artefakty vyhledan� automatick�m ozna�ov�n�m artefakt�.
	 * 
	 * @param newArtefacts
	 *          - arrayList nov� nalezen�ch artefakt�.
	 */
	public void addArtefacts(ArrayList<Artefact> newArtefacts) {
		for (Artefact art : newArtefacts) {
			selectArtefact(art.getArtefactStart(), art.getArtefactEnd());
		}
	}

	/**
	 * Zji��uje zda je mo�n� povolit tla��tko pro ozna�ov�n� epoch v popup-menu.
	 * 
	 * @param frame
	 *          - frame na kter� se kliklo v drawingComponent�.
	 * @return true - pokud se m�e epocha ozna�it.<br>
	 *         false - pokud se epocha nem�e ozna�it.
	 */
	public boolean getEnabledSelEpoch(long frame) {
		if ((frame - startEpoch) >= 0 && (frame + endEpoch) < epochsDraw.length) {
			if (epochsDraw[(int) frame + endEpoch] == 0
					&& epochsDraw[(int) frame - startEpoch] == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Zji��uje zda je mo�n� povolit tla��tko pro odzna�ov�n� epoch v popup-menu.
	 * 
	 * @param frame
	 *          - frame na kter� se kliklo v drawingComponent�.
	 * @return true - pokud se na dan�m m�st� vyskytuje epocha<br>
	 *         false - pokud se epocha na dan�m m�st� nevyskytuje.
	 */
	public boolean getEnabledUnselEpoch(long frame) {
		if (epochsDraw[(int) frame] != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Zji��uje zda je mo�n� povolit tla��tko pro odzna�ov�n� artefaktu v
	 * popup-menu.
	 * 
	 * @param frame
	 *          - frame na kter� se kliklo v drawingComponent�.
	 * @return true - pokud se na dan�m m�st� vyskytuje artefakt.<br>
	 *         false - pokud se na dan�m m�st� artefakt nevyskytuje.
	 */
	public boolean getEnabledUnselArtefact(long frame) {
		if (artefactsDraw[(int) frame]) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Zji��uje zda je mo�n� povolit tla��tko pro ozdna�ov�n� v�ech epoch.
	 * 
	 * @return true - pokud je ozna�en� aspo� jedna epocha.<br>
	 *         false - pokud neni ozna�en� ��dn� epocha.
	 */
	public boolean getEnabledUnselAllEpochs() {
		if (epochs.size() != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Zji��uje zda je mo�n� povolit tla��tko pro ozdna�ov�n� v�ech artefakt�.
	 * 
	 * @return true - pokud je ozna�en� aspo� jeden artefakt.<br>
	 *         false - pokud neni ozna�en� ��dn� artefakt.
	 */
	public boolean getEnabledUnselAllArtefacts() {
		if (artefacts.size() != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Sma�e artefakt v cel�m rozsahu, kter� se vyskytuje na dan�m m�st�.
	 * 
	 * @param frame
	 *          - frame na kter� se kliklo v drawingComponent�.
	 */
	public void unselectionConcreteArtefact(long frame) {
		for (int i = 0; i < artefacts.size(); i++) {
			if (frame >= artefacts.get(i).getArtefactStart()
					&& frame <= artefacts.get(i).getArtefactEnd()) {
				artefacts.remove(i);
				break;
			}
		}

		for (int i = (int) frame; i < artefactsDraw.length; i++) {
			if (artefactsDraw[i]) {
				artefactsDraw[i] = false;
			} else {
				break;
			}
		}

		if (frame > 0) {
			for (int i = (int) frame - 1; i >= 0; i--) {
				if (artefactsDraw[i]) {
					artefactsDraw[i] = false;
				} else {
					break;
				}
			}
		}
	}

	/**
	 * Sma�e v�echny artefakty.
	 */
	public void unselectionAllArtefacts() {
		artefacts = new ArrayList<Artefact>();
		artefactsDraw = new boolean[(int) (header.getNumberOfSamples())];
	}

	/**
	 * Sma�e v�echny epochy.
	 */
	public void unselectionAllEpochs() {
		epochs = new ArrayList<Epoch>();
		epochsDraw = new int[(int) (header.getNumberOfSamples())];
	}

	/**
	 * @return pole epoch pro vykreslen�.
	 */
	public int[] getEpochsDraw() {
		return epochsDraw;
	}

	/**
	 * @return pole artefakt� pro vykreslen�.
	 */
	public boolean[] getArtefactsDraw() {
		return artefactsDraw;
	}

	/**
	 * Nastavuje ArrayList artefakt� a podle t�chto artefakt� se ozna�uj� oblasti
	 * v poli pro vykreslen�.
	 * 
	 * @param artefacts
	 *          - ArrayList objekt� t��dy Artefact.
	 */
	public void setArtefacts(ArrayList<Artefact> artefacts) {
		this.artefacts = artefacts;

		for (Artefact art : artefacts) {
			for (int i = art.getArtefactStart(); i <= art.getArtefactEnd(); i++) {
				artefactsDraw[i] = true;
			}
		}
	}

	/**
	 * @return ArrayList artefakt�.
	 */
	public ArrayList<Artefact> getArtefacts() {
		return artefacts;
	}

	/**
	 * Nastavuje ArrayList epoch a podle t�chto epoch se ozna�uj� oblasti v poli
	 * pro vykreslen�.
	 * 
	 * @param epochs
	 *          - ArrayList objekt� t��dy Epoch.
	 */
	public void setEpochs(ArrayList<Epoch> epochs) {
		this.epochs = epochs;

		for (Epoch epoch : epochs) {
			epochsDraw[(int) epoch.getPosition()] = EPOCH_CENTER;
		}
	}

	/**
	 * @return arrayList v�ech epoch.
	 */
	public ArrayList<Epoch> getEpochs() {
		return epochs;
	}

	/**
	 * @return ArrayList st�ed� epoch pro pr�m�rov�n�.
	 */
	public ArrayList<Integer> getIndicesEpochsForAveraging() {
		Collections.sort(epochs);
		ArrayList<Integer> indicesEpochForAveraging = new ArrayList<Integer>();
		boolean foundArtefact;

		for (int i = 0; i < epochs.size(); i++) {
			foundArtefact = false;

			for (int j = (int) epochs.get(i).getPosition() - startEpoch; j <= epochs
					.get(i).getPosition()
					+ endEpoch; j++) {
				if (artefactsDraw[j]) {
					foundArtefact = true;
					break;
				}
			}

			if (!foundArtefact) {
				indicesEpochForAveraging.add(i);
			}
		}

		return indicesEpochForAveraging;
	}
}
