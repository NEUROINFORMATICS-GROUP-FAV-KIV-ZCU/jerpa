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
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.Container;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;

/**
 * Abstraktn� t��da, od n� jsou odd�d�ny v�echny t��dy realizuj�c� pohled na
 * pr�m�rov�n� epoch. Poskytuje komunika�n� konstanty, komunika�n� t��du
 * <b>communicationProvider</b> pro komunikaci pohled� s ostatn�mi komponentami
 * pomoc� n�vrhov�ho vzoru Observer/Observable. Implementuje metody shodn� pro
 * v�echny pohledy a definuje metody, jejich� implementace je pro pr�ci s
 * pohledy nutn�.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 */
abstract class AveragingPanelView {
	/**
	 * Komunika�n� konstanta ��kaj�c�, �e n�strojov� li�ta pohledu je p�ipraven� k
	 * um�st�n�.
	 */
	static final int TOOL_BAR_READY = 0;
	/**
	 * Komunika�n� konstanta ��kaj�c�, �e vnit�ek pohledu je p�ipraven k um�st�n�.
	 */
	static final int INSIDE_READY = 1;

	/**
	 * T��da pro komunikaci s ostatn�mi komponentami pomoc� n�vrhov�ho vzoru
	 * Observer/Observable.
	 * 
	 * @author Tom� �ond�k
	 */
	private class CommunicationProvider extends Observable implements Observer {
		/**
		 * Metoda pro pos�l�n� objekt� poslucha��m. Ve sv�m t�le vol� metody
		 * <i>setChanged</i> a <i>notifyObservers</i>.
		 * 
		 * @param object
		 *          objekt, kter� m� b�t poslucha��m posl�n.
		 */
		void sendObjectToObservers(Object object) {
			this.setChanged();
			this.notifyObservers(object);
		}

		/**
		 * Implementace metody rozhran� <i>Observer</i>.
		 * 
		 * @param arg0
		 *          reference na t��du, kter� pos�l� objekt.
		 * @param arg1
		 *          objekt popisuj�c� zm�nu.
		 */
		@Override
		public void update(Observable arg0, Object arg1) {
			update(arg0, arg1);
		}

	}

	/**
	 * Reference na okno, ve kter�m jsou pohledy um�st�ny.
	 */
	AveragingPanel averagingWindow;
	/**
	 * Komunika�n� rozhran� pro komunikaci s ostatn�mi komponentami.
	 */
	private CommunicationProvider communicationProvider;

	/**
	 * Konstruktor abstraktn� t��dy, kter� volaj� konstruktory odd�d�n�ch t��d.
	 * Inicializuje spole�n� atributy.
	 * 
	 * @param averagingWindow
	 *          okno, ve kter�m jsou pohledy um�st�ny.
	 */
	AveragingPanelView(AveragingPanel averagingWindow) {
		this.averagingWindow = averagingWindow;
		this.communicationProvider = new CommunicationProvider();
		this.communicationProvider.addObserver(this.averagingWindow);
	}

	/**
	 * Vrac� referenci na rozhran� pro komunikaci s ostatn�mi komponentami.
	 * 
	 * @return rozhran� pro komunikaci (<b>communicationProvider</b>).
	 */
	CommunicationProvider getCommunicationProvider() {
		return communicationProvider;
	}

	/**
	 * Slou�� pro pos�l�n� objekt� ostatn�m komponent�m. Pou��v�
	 * <b>communicationProvider</b>.
	 * 
	 * @param object
	 *          objekt pos�lan� ostatn�m komponent�m.
	 */
	void sendObjectToObservers(Object object) {
		communicationProvider.sendObjectToObservers(object);
	}

	/**
	 * Metoda je vol�na metodou <b>update</b> t��dy <i>CommunicationProvider</i>
	 * a p�ed�v� tak obdr�en� objekty do t�to metody pro dal�� zpracov�n�.
	 * 
	 * @param arg0
	 *          reference na t��du, kter� pos�l� objekt.
	 * @param arg1
	 *          objekt popisuj�c� zm�nu.
	 */
	abstract void update(Observable arg0, Object arg1);

	/**
	 * Povolen�/zakaz�n� ovl�dac�ch prvk� pohled�.
	 * 
	 * @param enabled
	 *          <i>true</i> pro povolen�, <i>false</i> pro zak�z�n� ovl�dac�ch
	 *          prvk�.
	 */
	abstract void setEnabledOperatingElements(boolean enabled);

	/**
	 * Metoda slou�� k p�ed�n� nov�ch dat k zobrazen�.
	 * 
	 * @param epochDataSet
	 *          nov� data k zobrazen�.
	 */
	abstract void updateEpochDataSet(List<EpochDataSet> epochDataSet);

	/**
	 * Vrac� n�strojovou li�tu pohledu.
	 * 
	 * @return n�strojov� li�ta.
	 */
	abstract Container getToolBar();

	/**
	 * Vrac� vnit�ek pohledu.
	 * 
	 * @return vnit�ek pohledu.
	 */
	abstract Container getInside();

	/**
	 * Vytv��� pr�m�rovac� panely a umis�uje je do layoutu vnit�ku pohledu.
	 */
	abstract void createMeanPanels();

	/**
	 * Nastavuje po�et epoch, kter� byly p�ed�ny k pr�m�rov�n�.
	 * 
	 * @param epochCount
	 *          po�et epoch p�edan�ch k pr�m�rov�n�.
	 */
	abstract void setEpochCount(int epochCount);

	/**
	 * Nastavuje ��slo pr�v� zobrazen� epochy.
	 * 
	 * @param currentEpochNumber
	 *          ��slo pr�v� zobrazen� epochy.
	 */
	abstract void setCurrentEpochNumber(int currentEpochNumber);

	/**
	 * Nastavuje, zda se maj� sign�ly zobrazovat invertovan�.
	 * 
	 * @param inverted
	 *          <b>true</b> pro invertovan� zobrazen�, <b>false</b> pro norm�ln�
	 *          zobrazen�.
	 */
	abstract void setInvertedSignalsView(boolean inverted);

	/**
	 * Nastavuje zobrazen� pohledu v z�vislosti na atributech pr�v� zobrazovan�ho
	 * projektu.
	 */
	abstract void viewSetupByProject();

	/**
	 * Nastavuje po��tek sou�adn� soustavy v zobrazova��ch sign�lu (instance t��dy
	 * SignalViewer).
	 * 
	 * @param coordinateBasicOriginFrame
	 *          pozice po��tku soustavy sou�adnic ve framech.
	 */
	abstract void setSignalViewersCoordinateBasicOrigin(
			int coordinateBasicOriginFrame);
}
