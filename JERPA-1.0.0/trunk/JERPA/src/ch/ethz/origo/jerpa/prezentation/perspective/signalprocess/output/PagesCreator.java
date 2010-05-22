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

import java.awt.Container;
import java.awt.print.PageFormat;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.EpochDataSet;

/**
 * T��da m� jedinou static metodu <code>createPagesList</code> pro vytvo�en�
 * v�ech str�nek exportovan�ho dokumentu.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
final class PagesCreator {
	/**
	 * Nen� mo�n� vytv��et instance t��dy. T��da nem� ��dn� atributy a ve�kerou
	 * funk�nost poskytuje skrz metodu <code>createPagesList</code>.
	 */
	private PagesCreator() {
	}

	/**
	 * Z p�edan�ch parametr� vytv��� v�echny str�nky exportovan�ho dokumentu.
	 * 
	 * @param provider
	 *          Reference na programov� rozhran� pro komunikaci s aplika�n�
	 *          vrstvou.
	 * @param epochsDataSet
	 *          Data pr�m�r� pro v�echny pr�m�rovan� kan�ly.
	 * @param header
	 *          Objekt s hlavi�kov�mi informacemi o vstupn�m souboru.
	 * @param footer
	 *          Objekt s koment��em u�ivatele a informacemi o aplikaci.
	 * @param pageFormat
	 *          Form�t str�nek exportovan�ho dokumentu.
	 * @return Str�nky exportovan�ho dokumentu.
	 */
	static List<Page> createPagesList(ExportFrameProvider provider,
			List<EpochDataSet> epochsDataSet, Container header, Container footer,
			PageFormat pageFormat) {
		List<Page> pages = new ArrayList<Page>();

		List<EpochDataSet> selected = new ArrayList<EpochDataSet>();

		for (EpochDataSet dataSet : epochsDataSet) {
			if (dataSet.getChannelOrderInInputFile() != -1) {
				if (provider.getChannelsToExport()[dataSet.getChannelOrderInInputFile()]) {
					selected.add(dataSet);
				}
			} else {
				selected = epochsDataSet;
				break;
			}
		}
		int remaingPanelsCount = selected.size();
		int panelsToFirstPage = (int) ((pageFormat.getImageableHeight() - header
				.getPreferredSize().getHeight()) / provider.getExportViewersHeight());
		int panelsToLastPage = (int) ((pageFormat.getImageableHeight() - footer
				.getPreferredSize().getHeight()) / provider.getExportViewersHeight());
		int panelsToOtherPages = (int) (pageFormat.getImageableHeight() / provider
				.getExportViewersHeight());

		if (panelsToFirstPage >= remaingPanelsCount) {
			pages.add(new DefaultPage(provider, selected, header, footer));
		} else if (panelsToFirstPage + panelsToLastPage >= remaingPanelsCount) {
			pages.add(new DefaultPage(provider, selected
					.subList(0, panelsToFirstPage), header, null));
			pages.add(new DefaultPage(provider, selected.subList(panelsToFirstPage,
					selected.size()), null, footer));
		} else {
			int panelIndex = panelsToFirstPage;
			Page page;

			pages.add(new DefaultPage(provider, selected.subList(0, panelIndex),
					header, null));

			for (; panelIndex < selected.size() - panelsToLastPage; panelIndex += panelsToOtherPages) {
				page = new DefaultPage(provider, selected.subList(panelIndex,
						panelIndex + panelsToOtherPages), null, null);
				pages.add(page);
			}

			pages.add(new DefaultPage(provider, selected.subList(panelIndex, selected
					.size()), null, footer));
		}
		return pages;
	}
}
