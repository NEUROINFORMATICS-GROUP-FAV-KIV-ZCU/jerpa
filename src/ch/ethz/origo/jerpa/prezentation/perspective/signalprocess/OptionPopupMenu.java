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
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenuItem;

/**
 * T��da vytv��ej�c� popup-menu, kter� umo��uje funkce segmentace sign�l� a
 * ozna�en� artefakt�.
 * 
 * @author Petr Soukal (original class from jERP Studio is
 *         <code>OptionMenu</code>)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (11/17/2009)
 * @see JPopupMenu
 * @see ILanguage
 */
public class OptionPopupMenu extends JPopupMenu implements ILanguage {

	/** Only for serialization */
	private static final long serialVersionUID = -1606263409130420159L;

	private String resourceBundlePath;
	//private ResourceBundle resource;

	private SignalsPanelProvider signalsProvider;
	private JUIGLEMenuItem selectEpoch;
	private JUIGLEMenuItem setPlaybackIndicator;
	private JUIGLEMenuItem unselectEpoch;
	private JUIGLEMenuItem unselectAllEpochs;
	private JUIGLEMenuItem unselectArtefact;
	private JUIGLEMenuItem unselectAllArtefacts;
	private JUIGLEMenuItem unselectAll;
	private long frame;

	/**
	 * Vytv��� objekt dan� t��dy a tla��tka menu.
	 * 
	 * @param signalsWindowProvider
	 *          - objekt t��dy SignalsWindowProvider pro komunikaci s ostatn�mi
	 *          t��dami prezenta�n� vrstvy.
	 * @throws JUIGLELangException 
	 */
	public OptionPopupMenu(SignalsPanelProvider signalsProvider) throws JUIGLELangException {
		this.signalsProvider = signalsProvider;
		// set up localized files
		setLocalizedResourceBundle(LangUtils
				.getPerspectiveLangPathProp(LangUtils.SIGNAL_PERSP_LANG_FILE_KEY));
		// initialize menu items
		selectEpoch = new JUIGLEMenuItem();
		setPlaybackIndicator = new JUIGLEMenuItem();
		unselectEpoch = new JUIGLEMenuItem();
		unselectAllEpochs = new JUIGLEMenuItem();
		unselectArtefact = new JUIGLEMenuItem();
		unselectAllArtefacts = new JUIGLEMenuItem();
		unselectAll = new JUIGLEMenuItem();
		// set up localized files path and keys
		selectEpoch.setResourceBundleKey("sig.viewer.selepoch");
		setPlaybackIndicator.setResourceBundleKey("sig.viewer.playback");
		unselectEpoch.setResourceBundleKey("sig.viewer.unselepoch");
		unselectAllEpochs.setResourceBundleKey("sig.viewer.unselepoch.all");
		unselectArtefact.setResourceBundleKey("sig.viewer.unselart");
		unselectAllArtefacts.setResourceBundleKey("sig.viewer.unselart.all");
		unselectAll.setResourceBundleKey("sig.viewer.unselect.all");
		updateText();
		// add listeners to items
		selectEpoch.addActionListener(new FunctionSelectEpoch());
		setPlaybackIndicator.addActionListener(new FunctionSetPlaybackIndicator());
		unselectEpoch.addActionListener(new FunctionUnselectEpoch());
		unselectAllEpochs.addActionListener(new FunctionUnselectAllEpochs());
		unselectArtefact.addActionListener(new FunctionUnselectArtefact());
		unselectAllArtefacts.addActionListener(new FunctionUnselectAllArtefacts());
		unselectAll.addActionListener(new FunctionUnselectAll());

		this.add(setPlaybackIndicator);
		this.add(selectEpoch);
		this.addSeparator();
		this.add(unselectEpoch);
		this.add(unselectArtefact);
		this.addSeparator();
		this.add(unselectAllEpochs);
		this.add(unselectAllArtefacts);
		this.add(unselectAll);
		LanguageObservable.getInstance().attach(this);
	}

	/**
	 * Nastavuje zobrazen� popup-menu a jeho um�st�n�.
	 * 
	 * @param visualComponent
	 *          - komponenta, ke kter� se menu v�e.
	 * @param xAxis
	 *          - x-ov� sou�adnice zobrazen� menu.
	 * @param yAxis
	 *          - y-ov� sou�adnice zobrazen� menu.
	 * @param frame
	 *          - m�sto v souboru, p�epo��tan� ze sou�adnic kliku.
	 */
	public void setVisibleMenu(JComponent visualComponent, int xAxis, int yAxis,
			long frame) {
		this.frame = frame;
		this.show(visualComponent, xAxis, yAxis);
	}

	/**
	 * Nastavuje povolen�/zak�z�n� jednotliv�ch tla��tek.
	 * 
	 * @param enabledSelEpoch
	 *          - povolen�/zak�z�n� ozna�en� epochy.
	 * @param enabledUnselEpoch
	 *          - povolen�/zak�z�n� odzna�en� epochy.
	 * @param enabledUnselArtefact
	 *          - povolen�/zak�z�n� odzna�en� artefaktu.
	 * @param enabledUnselAllEpochs
	 *          - povolen�/zak�z�n� odzna�en� v�ech epoch.
	 * @param enabledUnselAllArtefacts
	 *          - povolen�/zak�z�n� odzna�en� v�ech artefakt�.
	 * @param enabledUnselAll
	 *          - povolen�/zak�z�n� odzna�en� v�eho.
	 */
	public void setEnabledItems(boolean enabledSelEpoch,
			boolean enabledUnselEpoch, boolean enabledUnselArtefact,
			boolean enabledUnselAllEpochs, boolean enabledUnselAllArtefacts,
			boolean enabledUnselAll) {
		selectEpoch.setEnabled(enabledSelEpoch);
		unselectEpoch.setEnabled(enabledUnselEpoch);
		unselectArtefact.setEnabled(enabledUnselArtefact);
		unselectAllEpochs.setEnabled(enabledUnselAllEpochs);
		unselectAllArtefacts.setEnabled(enabledUnselAllArtefacts);
		unselectAll.setEnabled(enabledUnselAll);
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka ozna�en� epochy.
	 */
	private class FunctionSelectEpoch implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.selectEpoch(frame);
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka nastaven� ukazatele p�ehr�v�n�.
	 */
	private class FunctionSetPlaybackIndicator implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.getDrawingComponent().setPlaybackIndicatorPosition(frame);
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� epochy.
	 */
	private class FunctionUnselectEpoch implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectEpoch(frame);
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� v�ech epoch.
	 */
	private class FunctionUnselectAllEpochs implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectAllEpochs();
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� artefaktu.
	 */
	private class FunctionUnselectArtefact implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectConcreteArtefact(frame);
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� v�ech artefakt�.
	 */
	private class FunctionUnselectAllArtefacts implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectAllArtefacts();
		}
	}

	/**
	 * Obsluhuje akci p�i stisknut� tla��tka odzna�en� v�eho.
	 */
	private class FunctionUnselectAll implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.unselectAllEpochsAndArtefacts();
		}
	}

	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourceBundlePath = path;
		//this.resource = ResourceBundle.getBundle(path);
	}

	/**
	 * Not Implemented for this class.
	 */
	@Override
	public void setResourceBundleKey(String key) {

	}

	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					setResourceBundleItemsPath();
					selectEpoch.updateText();
					setPlaybackIndicator.updateText();
					unselectEpoch.updateText();
					unselectAllEpochs.updateText();
					unselectArtefact.updateText();
					unselectAllArtefacts.updateText();
					unselectAll.updateText();
				} catch (JUIGLELangException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}
	
	private void setResourceBundleItemsPath() {
		selectEpoch.setLocalizedResourceBundle(getResourceBundlePath());
		setPlaybackIndicator.setLocalizedResourceBundle(getResourceBundlePath());
		unselectEpoch.setLocalizedResourceBundle(getResourceBundlePath());
		unselectAllEpochs.setLocalizedResourceBundle(getResourceBundlePath());
		unselectArtefact.setLocalizedResourceBundle(getResourceBundlePath());
		unselectAllArtefacts.setLocalizedResourceBundle(getResourceBundlePath());
		unselectAll.setLocalizedResourceBundle(getResourceBundlePath());
	}
}
