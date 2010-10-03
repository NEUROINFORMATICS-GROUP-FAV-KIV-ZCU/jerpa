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
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Rozes�la� zpr�v p�i pou�it� n�vrhov�ho vzoru Observer/Observable.
 * Zjednodu�uje pos�l�n� objekt� mezi v�ce t��dami, kter� mohou b�t z�rove�
 * vys�la�i (potomci t��dy <i>Observable</i>) a p�ij�ma�i (implementuj�
 * rozhran� <i>Observer</i>). Odd�d�n� t��dy jsou p�ekryty tak, aby p�i
 * pos�l�n� objekt� mezi dv�ma instancemi t�to t��dy nedo�lo k zacyklen�.
 * Obdr�enoSv� poslucha�e si registruje bu� p�i sv�m vzniku, kde jsou poslucha�i
 * p�ed�ni jako parametry konstruktoru nebo metodou <i>addObserver</i>. Jako
 * poslucha�e si jej registruj� t��dy, kter� cht�j� t�mto poslucha��m pos�lat
 * objekty.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 * 
 */
public final class ObjectBroadcaster extends Observable implements Observer {
	/*
	 * Jde v podstat� o novou implementaci t��dy Observable tak, aby nemohlo doj�t
	 * k zacyklen� p�i pos�l�n� objekt� mezi dv�ma "ObjectBroadcastery". Jsou
	 * p�ekryty v�echny metody vyjma metod "clearChanged", "hasChanged",
	 * "setChanged". Ty nav�c p�i p�ekryt� metody "notifyObservers" ztr�cej� na
	 * v�znamu. Observery jsou ukl�d�ny do Listu "myObservers". Pro pos�l�n�
	 * objekt� observer�m se pou��v� priv�tn� metoda "sendObjectToObservers",
	 * kter� vol� p��mo metodu "update" jednotliv�ch observer�.
	 */

	/**
	 * Seznam poslucha��, kter�m jsou objekty p�epos�l�v�ny.
	 */
	private List<Observer> myObservers;

	/**
	 * Vytv��� instanci t��dy bez registrovan�ch poslucha��.
	 */
	public ObjectBroadcaster() {
		super();
		myObservers = new ArrayList<Observer>();
	}

	/**
	 * Vytv��� instanci t��dy. Poslucha�i jsou p�ed�n� v kolekci jako parametr.
	 * 
	 * @param observers
	 *          poslucha�i.
	 */
	public ObjectBroadcaster(Collection<Observer> observers) {
		super();
		myObservers = new ArrayList<Observer>();

		for (Observer observer : observers) {
			myObservers.add(observer);
		}
	}

	/**
	 * Vytv��� instanci t��dy. Jako parametr je p�ed�n libovoln� po�et poslucha��.
	 * 
	 * @param observers
	 *          libovoln� po�et poslucha��.
	 */
	public ObjectBroadcaster(Observer... observers) {
		super();
		myObservers = new ArrayList<Observer>();

		for (Observer observer : observers) {
			myObservers.add(observer);
		}
	}

	/**
	 * P�id�v� nov�ho poslucha�e do seznamu poslucha��.
	 * 
	 * @param observer
	 *          nov� poslucha�.
	 */
	@Override
	public synchronized void addObserver(Observer observer) {
		myObservers.add(observer);
	}

	/**
	 * P�id�v� v�echny poslucha�e p�edan� v argumentu do seznamu poslucha��.
	 * 
	 * @param observers
	 *          kolekce obsahuj�c� nov� poslucha�e.
	 */
	public synchronized void addObserver(Collection<Observer> observers) {
		for (Observer observer : observers) {
			myObservers.add(observer);
		}
	}

	/**
	 * P�id�v� v�echny poslucha�e p�edan� v argumentu do seznamu poslucha��.
	 * 
	 * @param observers
	 *          libovoln� po�et nov�ch poslucha��.
	 */
	public synchronized void addObserver(Observer... observers) {
		for (Observer observer : observers) {
			myObservers.add(observer);
		}
	}

	/**
	 * Odstra�uje v�echny poslucha�e ze seznamu poslucha��.
	 */
	@Override
	public synchronized void deleteObservers() {
		myObservers = new ArrayList<Observer>();
	}

	/**
	 * Odstra�uje poslucha�e p�edan�ho v parametru ze seznamu poslucha��.
	 * 
	 * @param observer
	 *          poslucha�, kter� bude odstran�n ze seznamu poslucha��.
	 */
	@Override
	public synchronized void deleteObserver(Observer observer) {
		myObservers.remove(observer);
	}

	/**
	 * Vrac� po�et poslucha�� v seznamu poslucha��.
	 */
	@Override
	public int countObservers() {
		return myObservers.size();
	}

	/**
	 * P�ekryt� metody odd�d�n� od t��dy <i>Observable</i>. Intern� pouze vol�
	 * priv�tn� metodu <b>sendObjectToObservers</b> (<code>sendObjectToObservers(this, null);</code>).
	 */
	@Override
	public void notifyObservers() {
		sendObjectToObservers(this, null);
	}

	/**
	 * P�ekryt� metody odd�d�n� od t��dy <i>Observable</i>. Intern� pouze vol�
	 * priv�tn� metodu <b>sendObjectToObservers</b> (<code>sendObjectToObservers(this, object);</code>).
	 * 
	 * @param object
	 *          objekt pos�lan� poslucha��m.
	 */
	@Override
	public void notifyObservers(Object object) {
		sendObjectToObservers(this, object);
	}

	/**
	 * Realizuje pos�l�n� objekt� poslucha��m.
	 * 
	 * @param observable
	 *          reference na vys�la�.
	 * @param object
	 *          objekt pos�lan� poslucha��m.
	 */
	private void sendObjectToObservers(Observable observable, Object object) {
		for (Observer observer : myObservers) {
			if (observer instanceof Observable) // kdy� je poslucha� z�rove� vys�la�em
			{
				if ((((Observable) observer).equals(observable))) // kdy� by mohlo doj�t
																													// k zacyklen� v
																													// pos�l�n� zpr�v
				{
					continue;
				}
			}

			/*
			 * Vol�n� metody "setChanged" nen� nutn�, proto�e se vol� p��mo metoda
			 * "update".
			 */
			observer.update(this, object);
		}
	}

	/**
	 * Implementace metody rozhran� <i>Observer</i>. Ve sv�m t�le vol� priv�tn�
	 * metodu pro pos�l�n� objekt� poslucha��m (<code>sendObjectToObservers(observable, object);</code>).
	 * 
	 * @param observable
	 *          reference na vys�la�.
	 * @param object
	 *          objekt pos�lan� poslucha��m.
	 */
	@Override
	public void update(Observable observable, Object object) {
		sendObjectToObservers(observable, object);
	}
}
