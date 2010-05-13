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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.application.exception.InsufficientDataException;
import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;
import ch.ethz.origo.jerpa.prezentation.MainFrame;

/**
 * Panel, na n�m� se zobrazuje vybran� pohled (<i>AveragingWindowView</i>) na
 * pr�m�rov�n� epoch. Pro rozmis�ov�n� komponent pou��v� <i>BorderLayout</i>.
 * Pro zobrazen� n�strojov�ch li�t jednotliv�ch pohled� slou�� atribut
 * <b>toolBar</b> (<i>Container</i>) um�st�n� v NORTH containeru pou�it�ho
 * layoutu. Pro zobrazen� vnit�k� pohled� slou�� atribut <b>inside</b> (<i>Container</i>).
 * Pro p�ep�n�n� mezi jednotliv�mi pohledy slou�� tla��tka (<i>JToggleButton</i>)
 * um�st�n� v SOUTH containeru pou�it�ho layoutu.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 * 
 */
@SuppressWarnings("serial")
final class AveragingPanel extends JXPanel implements Observer {
	/**
	 * Nastavuje pohled realizovan� t��dou <i>EpochByEpochView</i>.
	 */
	private JToggleButton epochByEpochAdditionJTB;

	/**
	 * Nastavuje pohled realizovan� t��dou <i>GroupEpochView</i>.
	 */
	private JToggleButton groupEpochsAdditionJTB;

	/**
	 * Nastavuje pohled realizovan� t��dou <i>AveragesPreView</i>.
	 */
	private JToggleButton averagesPreviewJTB;
	/**
	 * Komunika�n� rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	private AveragingPanelProvider averagingWindowProvider;

	/**
	 * Index pr�v� zobrazen�ho pohledu v poli <b>views</b>.
	 */
	private int currentView;

	/**
	 * Pole obsahuj�c� reference na v�echny dostupn� pohledy.
	 */
	private AveragingPanelView[] views;

	/**
	 * Reference na n�strojovou li�tu pr�v� zobrazovan�ho pohledu.
	 */
	private Container toolBar;

	/**
	 * Reference na vnit�ek pr�v� zobrazovan�ho pohledu.
	 */
	private Container inside;

	/**
	 * Vytv��� novou instanci t��dy. Bu� inicializuje atributy p��mo nebo vol�
	 * metody v jejich� t�le jsou atributy inicializov�ny. Nastavuje pou�it�
	 * layout (zde <i>BorderLayout</i>). Zobrazuje n�strojovou li�tu vybran�ho
	 * pohledu a zakazuje v�echny ovl�dac� prvky tohoto pohledu (povol� se p�i
	 * p�ed�n� dat k zobrazen�).
	 * 
	 * @param averagingWindowProvider
	 */
	AveragingPanel(AveragingPanelProvider averagingWindowProvider) {
		super();
		this.averagingWindowProvider = averagingWindowProvider;
		this.setLayout(new BorderLayout());
		this.setLocation(MainFrame.HEIGHT / 2, 0);
		this.setSize(new Dimension(Const.MAIN_WINDOW_WIDTH / 2,
				Const.MAIN_WINDOW_HEIGHT - 90));
		this.toolBar = null;
		this.inside = null;
		this.add(createViewsPanel(), BorderLayout.SOUTH);

		viewsInit(); // inicializace atributu "views" a jeho napln�n� v�emi
									// dostupn�mi pohledy
		// showView(); // uk�z�n� vybran�ho pohledu
		addActionListeners(); // vol�n� p�i�azen� actionListener� komponent�m

		setEnabledOperatingElements(false);
		/*
		 * Barva CENTER containeru, pokud do n�j nen� p�i�azen vnit�ek pohledu.
		 */
		this.setBackground(Color.WHITE);

		this.setVisible(true);
	}

	/**
	 * Vytv��� reference na v�echny dostupn� pohledy (<i>AveragingWindowView</i>)
	 * a p�i�azuje je do pole <b>views</b>. Pro zm�nu pohledu pak sta�� vybrat
	 * po�adovanou referenci a zobrazit jej� n�strojovou li�tu a vnit�ek (nen�
	 * t�eba vytv��et novou instanci a rozd�lan� pr�ce v jednotliv�ch pohledech
	 * nebude p�i zm�n� pohledu ztracena).
	 */
	private void viewsInit() {
		views = new AveragingPanelView[3];
		views[0] = new GroupEpochsView(this);
		views[1] = new EpochByEpochView(this);
		views[2] = new AveragesPreView(this);

		for (AveragingPanelView view : views) {
			view.setEnabledOperatingElements(false);
		}

		currentView = 0;
	}

	/**
	 * Do atributu <b>view</b> ukl�d� referenci pohled, kter� m� b�t zobrazen.
	 * Vol� metodu <b>addToolBar</b> pro zobrazen� n�strojov� li�ty vybran�ho
	 * pohledu a metodu <b>addInside</b> pro zobrazen� vnit�ku vybran�ho pohledu.
	 */
	private void showView() {
		addToolBar(views[currentView].getToolBar());
		addInside(views[currentView].getInside());

		averagingWindowProvider.updateEpochDataSet();
		averagingWindowProvider.updateGroupEpochsDataSet();
	}

	/**
	 * Vytv��� a vrac� panel pro p�ep�n�n� mezi pohledy (ten je pak zobrazen v
	 * SOUTH containeru).
	 * 
	 * @return panel pro p�ep�n�n� mezi pohledy (<i>Container</i>).
	 */
	private Container createViewsPanel() {
		ButtonGroup toggleGroup = new ButtonGroup(); // aby byl vybr�n jen jeden
																									// pohled
		groupEpochsAdditionJTB = new JToggleButton("Group epochs addition");
		epochByEpochAdditionJTB = new JToggleButton("Epoch by epoch addition");
		averagesPreviewJTB = new JToggleButton("Averages preview");
		toggleGroup.add(groupEpochsAdditionJTB);
		toggleGroup.add(epochByEpochAdditionJTB);
		toggleGroup.add(averagesPreviewJTB);
		groupEpochsAdditionJTB.setSelected(true); // nastaven� v�choz�ho pohledu

		// um�st�n� v�b�ru pohledu do toolBaru
		JToolBar viewsBar = new JToolBar();
		viewsBar.add(groupEpochsAdditionJTB);
		viewsBar.add(epochByEpochAdditionJTB);
		viewsBar.add(averagesPreviewJTB);
		viewsBar.setFloatable(false);

		return viewsBar;
	}

	/**
	 * P�i�azuje poslucha�e ud�lost� jednotliv�m komponent�m.
	 */
	private void addActionListeners() {
		ActionListener ALviewSelection = new ALviewSelection();
		groupEpochsAdditionJTB.addActionListener(ALviewSelection);
		epochByEpochAdditionJTB.addActionListener(ALviewSelection);
		averagesPreviewJTB.addActionListener(ALviewSelection);
	}

	/**
	 * Nastavuje pr�v� zobrazen�mu pohledu ��slo aktu�ln� epochy.
	 * 
	 * @param currentEpochNumber
	 *          ��slo aktu�ln� epochy.
	 */
	void setCurrentEpochNumber(int currentEpochNumber) {
		for (AveragingPanelView view : views) {
			view.setCurrentEpochNumber(currentEpochNumber);
		}
	}

	/**
	 * Pos�l� pohled�m <code>EpochByEpochView</code> a
	 * <code>AveragesPreView</code> nov� data k zobrazen�.
	 * 
	 * @param epochDataSet
	 *          Nov� data k zobrazen�.
	 */
	void updateEpochDataSet(List<EpochDataSet> epochDataSet) {
		for (AveragingPanelView view : views) {
			if (!(view instanceof GroupEpochsView)) {
				view.updateEpochDataSet(epochDataSet);
			}
		}

		if (toolBar == null || inside == null) {
			showView();
		}
	}

	/**
	 * Pos�l� pohledu <code>GroupEpochsView</code> nov� data k zobrazen�.
	 * 
	 * @param groupEpochDataSet
	 *          Nov� data k zobrazen�.
	 */
	void updateGroupEpochDataSet(List<EpochDataSet> groupEpochDataSet) {
		for (AveragingPanelView view : views) {
			if (view instanceof GroupEpochsView) {
				view.updateEpochDataSet(groupEpochDataSet);
			}
		}

		if (toolBar == null || inside == null) {
			showView();
		}
	}

	/**
	 * Zobrazuje p�edanou n�strojovou li�tu vybran�ho pohledu, pokud nen� <b>null</b>.
	 * Pokud je n�jak� n�strojov� li�ta u� zobrazena, pak dojde nejprve k
	 * odstran�n� star� n�strojov� li�ty a a� potom k p�id�n� nov�.
	 * 
	 * @param toolBar
	 *          N�strojov� li�ta vybran�ho pohledu.
	 */
	private void addToolBar(Container toolBar) {
		if (this.toolBar != null) {
			this.remove(this.toolBar);
		}

		if (toolBar != null) {
			this.toolBar = toolBar;
			this.add(this.toolBar, BorderLayout.NORTH);

			this.validate();
			this.repaint(); // fakt je pot�eba kv�li p�ekreslen� toolBar�
		}
	}

	/**
	 * Zobrazuje p�edan� vnit�ek vybran�ho pohledu, pokud nen� <b>null</b>. Pokud
	 * je n�jak� vnit�ek u� zobrazen, pak dojde nejprve k odstran�n� star�ho
	 * vnit�ku a a� potom k p�id�n� nov�ho.
	 * 
	 * @param inside
	 *          Vnit�ek vybran�ho pohledu.
	 */
	private void addInside(Container inside) {
		if (this.inside != null) {
			this.remove(this.inside);
		}

		if (inside != null) {
			this.inside = inside;
			this.add(this.inside, BorderLayout.CENTER);

			this.validate();
			this.repaint(); // jen z opatrnosti (v addToolBar m� ale v�znam)
		}
	}

	/**
	 * Vol� metodu vybran�ho pohledu, kter� zaji��uje vytvo�en� pr�m�rovac�ch
	 * panel�.
	 */
	void createMeanPanels() {
		for (AveragingPanelView view : views) {
			view.createMeanPanels();
		}
	}

	/**
	 * Vol� metodu vybran�ho pohledu, kter� nastavuje po�et epoch.
	 * 
	 * @param epochCount
	 *          po�et epoch p�edan�ch k pr�m�rov�n�.
	 */
	void setEpochCount(int epochCount) {
		for (AveragingPanelView view : views) {
			view.setEpochCount(epochCount);
		}
	}

	/**
	 * Povoluje/zakazuje ovl�dac� prvky a vol� vybran� pohled pro
	 * povolen�/zak�z�n� ovl�dac�ch prvk� pohledu.
	 * 
	 * @param enabled
	 *          <code>true</code> pro povolen� nebo <code>false</code> pro
	 *          zak�z�n� ovl�dac�ch prvk�
	 */
	void setEnabledOperatingElements(boolean enabled) {
		groupEpochsAdditionJTB.setEnabled(enabled);
		epochByEpochAdditionJTB.setEnabled(enabled);
		averagesPreviewJTB.setEnabled(enabled);

		for (AveragingPanelView view : views) {
			view.setEnabledOperatingElements(enabled);
		}
	}

	/**
	 * Vol� metodu aktu�ln�ho pohledu pro slad�n� zobrazovan�ch dat s daty
	 * aktu�ln�ho projektu.
	 */
	void viewsSetupByProject() {
		for (AveragingPanelView view : views) {
			view.viewSetupByProject();
		}
	}

	/**
	 * Pou��v� se pro zobrazen� vyj�mky, kter� vznikla n�kde v aplika�n� logice
	 * pr�m�rovac�ho okna. Zobraz� vyskakovac� okno (statick� metoda
	 * <b>showMessageDialog</b> t��dy <i>JOptionPane</i>) s v�pisem chyby, ke
	 * kter� do�lo. Pro zn�m� vyj�mky ukazuje anglicky spr�vn� nadpis a podle
	 * z�va�nosti vyj�mky odpov�daj�c� ikonku. Pro nezn�mou vyj�mku ukazuje nadpis
	 * dan� ne�pln�m n�zvem t��dy, jej� instanc� vyj�mka je a ikonku
	 * ERROR_MESSAGE.
	 * 
	 * @param exception
	 */
	void showException(Exception exception) {
		if (exception instanceof InsufficientDataException) {
			JOptionPane.showMessageDialog(this, exception.getMessage(),
					"Insufficient data", JOptionPane.WARNING_MESSAGE);
		} else if (exception instanceof InvalidFrameIndexException) {
			JOptionPane.showMessageDialog(this, exception.getMessage(),
					"Nonexistends epoch", JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Unknown problem:\n"
					+ exception.getMessage(), exception.getClass().getSimpleName(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Vol� metodu atribut <code>averagingWindowProvider</code>, kter� slou��
	 * ke spu�t�n� exportn�ho okna (<code>ExportFrame</code>).
	 */
	void runAveragesExport() {
		averagingWindowProvider.runAveragesExport();
	}

	/**
	 * Vrac� referenci na komunika�n� rozhran� s aplika�� vrstvou (<i>AveragingWindowProvider</i>).
	 * 
	 * @return reference na atribut <b>averagingWindowProvider</b>.
	 */
	AveragingPanelProvider getAveragingWindowProvider() {
		return averagingWindowProvider;
	}

	/**
	 * Nastavuje invertovan� zobrazen� sign�l�.
	 * 
	 * @param inverted
	 *          <code>true</code> pro invertovan� zobrazen�, pro norm�ln�
	 *          zobrazen� <code>false</code>.
	 */
	void setInvertedSignalsView(boolean inverted) {
		for (AveragingPanelView view : views) {
			view.setInvertedSignalsView(inverted);
		}
	}

	/**
	 * Nastavuje pozici po��tku sou�adnic v <code>SignalViewer</code>ech
	 * pr�m�rovac�ch panel�.
	 * 
	 * @param coordinateBasicOriginFrame
	 *          Pozice po��tku soustavy sou�adnic.
	 */
	void setSignalViewersCoordinateBasicOrigin(int coordinateBasicOriginFrame) {
		for (AveragingPanelView view : views) {
			view.setSignalViewersCoordinateBasicOrigin(coordinateBasicOriginFrame);
		}
	}

	/**
	 * P��jem informac� od ostatn�ch komponent GUI.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof Integer) {
			if (views[currentView] != null) {
				switch (((Integer) arg1).intValue()) {
				case AveragingPanelView.TOOL_BAR_READY:
					addToolBar(views[currentView].getToolBar());
					break;
				case AveragingPanelView.INSIDE_READY:
					addInside(views[currentView].getInside());
					averagingWindowProvider.updateEpochDataSet();
					averagingWindowProvider.updateGroupEpochsDataSet();
				}
			}
		}
	}

	/**
	 * Action listener tla��tek pro v�b�r pohledu. Kdy� je n�kter� tla��tko
	 * vybran� (stisknut�), pak vol� metodu <code>showView</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALviewSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (groupEpochsAdditionJTB.isSelected()) {
				currentView = 0;
			} else if (epochByEpochAdditionJTB.isSelected()) {
				currentView = 1;
			} else {
				currentView = 2;
			}

			showView();
		}
	}
}
