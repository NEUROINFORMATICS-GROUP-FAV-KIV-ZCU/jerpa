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
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.RepaintManager;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;

/**
 * Realizuje jednu str�nku v exportovan�m dokumentu.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
final class DefaultPage extends Page {
	/**
	 * Panel, do kter�ho se umis�uj� exportn� panely (<code>ExportPanel</code>)
	 * pro jednotliv� kan�ly.
	 */
	private JPanel signalsJP;

	/**
	 * Vytv��� novou str�ku exportovan�ho dokumentu.
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
	DefaultPage(ExportFrameProvider provider, List<EpochDataSet> epochsDataSet,
			Container headerInfo, Container footerInfo) {
		super(provider, epochsDataSet, headerInfo, footerInfo);
		layoutInit();
		createInside();
	}

	/**
	 * Inicializuje a nastavuje parametry layoutu str�nky.
	 */
	private void layoutInit() {
		BorderLayout panelToPrintJPLayout = new BorderLayout();
		panelToPrintJPLayout.setVgap(10);
		this.setLayout(panelToPrintJPLayout);
	}

	/**
	 * Umis�uje komponenty na str�nku.
	 */
	private void createInside() {
		this.setBackground(provider.getCanvasColor());
		if (headerInfo != null) {
			this.add(headerInfo, BorderLayout.NORTH);
		}

		signalsJP = new JPanel();

		GridLayout signalsLayout = new GridLayout(epochsDataSet.size(), 1);
		signalsLayout.setVgap(10);
		signalsJP.setLayout(signalsLayout);
		signalsJP.setBackground(provider.getCanvasColor());

		for (EpochDataSet epochDataSet : epochsDataSet) {
			ExportPanel viewerPanel = new DefaultExportPanel(epochDataSet
					.getChannelOrderInInputFile(), provider);
			viewerPanel.setSignalViewersCoordinateBasicOrigin(provider
					.getAveragingDataManager().getLeftEpochBorderInFrames());
			viewerPanel.setEpochDataSet(epochDataSet);
			signalsJP.add(viewerPanel);
		}

		this.add(signalsJP, BorderLayout.CENTER);

		if (footerInfo != null) {
			this.add(footerInfo, BorderLayout.SOUTH);
		}
	}

	/**
	 * Metoda se vol� p�i tisku. B�h�m tisku je vypnuto dvojit� bufferov�n� -
	 * sni�uje v�po�etn� n�ro�nost tisku.
	 * 
	 * @param g
	 *          Grafick� kontext.
	 * @param pageFmt
	 *          Form�t str�nky.
	 * @param pageNumber
	 *          ��slo str�nky.
	 */
	@Override
	public int print(Graphics g, PageFormat pageFmt, int pageNumber)
			throws PrinterException {
		Graphics2D g2 = (Graphics2D) g;
		g2.translate(pageFmt.getImageableX(), pageFmt.getImageableY());
		RepaintManager mgr = RepaintManager.currentManager(this);
		boolean db = mgr.isDoubleBufferingEnabled();
		mgr.setDoubleBufferingEnabled(false);
		this.print(g2);
		mgr.setDoubleBufferingEnabled(db);
		return PAGE_EXISTS;
	}

}
