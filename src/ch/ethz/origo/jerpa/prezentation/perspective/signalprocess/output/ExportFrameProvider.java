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

import java.awt.Color;
import java.util.ResourceBundle;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.AveragingDataManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * Programov� rozhran� pro komunikaci mezi aplika�n� vrstvou a ��st� prezenta�n�
 * vrstvy, kter� se zab�v� exportem v�sledk�.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.2.0 (4/17/2010)
 * @since 0.1.0 (3/21/2010)
 * @see IObserver
 * @see ILanguage
 */
public final class ExportFrameProvider implements ILanguage, IObserver {
	/**
	 * Reference na t��du poskytuj�c� reference na projekty.
	 */
	private SignalSessionManager app;

	/**
	 * Reference na prvek prezenta�n� vrstvy, prost�ednictv�m kter�ho p�ed�v�
	 * prezenta�n� vrstv� data k zobrazen�.
	 */
	private ExportFrame exportFrame;
	/**
	 * Index pr�v� vybran� z�lo�ky.
	 */
	private int selectedTabProjectIndex;
	/**
	 * Reference na pr�v� vybran� projekt.
	 */
	private SignalProject selectedTabProject;
	/**
	 * Reference na abstrakci p��stupu k projektu.
	 */
	private AveragingDataManager averagingDataManager;

	private ResourceBundle resourceBundle;

	private String resourceBundlePath;

	/**
	 * Vytv��� komunika�n� rozhran� pro komunikaci mezi aplika�n� a prezenta�n�
	 * vrstvou v ��sti export v�sledk�.
	 * 
	 * @param app
	 *          Reference na t��du poskytuj�c� reference na projekty.
	 */
	public ExportFrameProvider(SignalSessionManager app) {
		this.app = app;
		exportFrame = null;
		selectedTabProjectIndex = app.getCurrentProjectIndex();
		selectedTabProject = app.getCurrentProject();
		SignalPerspectiveObservable.getInstance().attach(this);
		LanguageObservable.getInstance().attach((ILanguage) this);
		this.setLocalizedResourceBundle(LangUtils
				.getPerspectiveLangPathProp(LangUtils.SIGNAL_PERSP_LANG_FILE_KEY));
		averagingDataManager = new AveragingDataManager(selectedTabProject);
	}

	/**
	 * Nastavuje hodnotu atributu <code>selectedTabProjectIndex</code>. V
	 * z�vislosti na tomto nastaven� nastavuje atribut
	 * <code>selectedTabProject</code> na aktu�ln� projekt a na aktu�ln� projekt
	 * sm��uje tak� <code>averagingDataManager</code>.
	 * 
	 * @param selectedTabProjectIndex
	 */
	void setSelectedTabProjectIndex(int selectedTabProjectIndex) {
		this.selectedTabProjectIndex = selectedTabProjectIndex;
		selectedTabProject = (SignalProject) app
				.getProject(this.selectedTabProjectIndex);
		averagingDataManager.setProject(selectedTabProject);
	}

	/**
	 * Aktualizuje data v pr�v� vybran� z�lo�ce ur�en� k exportu.
	 */
	void updateSelectedTabEpochDataSet() {
		if (exportFrame != null) {
			try {
				exportFrame.setSelectedTab(selectedTabProjectIndex);
				exportFrame
						.setSelectedTabSignalViewersCoordinateBasicOrigin(averagingDataManager
								.getLeftEpochBorderInFrames());
				exportFrame.setSelectedTabEpochDataSet(averagingDataManager
						.getEpochDataSet());

			} catch (Exception exception) {
				exportFrame.showException(exception);
				exception.printStackTrace(); // TODO - lad�c� v�pis, do fin�ln� verze
				// odstranit
			}
		}
	}

	/**
	 * Zp�sobuje zobrazen� exportn�ho okna (realizov�no t��dou
	 * <code>ExportFrame</code>).
	 * 
	 * @throws PerspectiveException
	 */
	private void showExportFrame() throws PerspectiveException {
		if (exportFrame != null) {
			exportFrame.setVisible(false);
		}
		exportFrame = new ExportFrame(this);
		exportFrame.setVisible(true);
	}

	/**
	 * Metoda slou��c� k p��jmu zpr�v od zbytku aplikace. V jej�m t�le jsou zpr�vy
	 * vyhodnocov�ny a vol�ny odpov�daj�c� metody.
	 * 
	 * @param observable
	 *          Zdroj zpr�vy.
	 * @param object
	 *          Zpr�va.
	 */
	@Override
	public void update(IObservable observable, Object object) {
		if (object instanceof java.lang.Integer) {
			int msg = ((Integer) object).intValue();

			switch (msg) {
			case SignalPerspectiveObservable.MSG_RUN_AVERAGES_EXPORT:
				try {
					showExportFrame();
				} catch (PerspectiveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
				if (exportFrame != null && exportFrame.isVisible())
					try {
						showExportFrame();
					} catch (PerspectiveException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				setSelectedTabProjectIndex(app.getCurrentProjectIndex());
			case SignalPerspectiveObservable.MSG_NEW_AVERAGES_AVAILABLE:
				updateSelectedTabEpochDataSet();
				break;
			}
		}
	}

	/**
	 * Vrac� pole se jm�ny otev�en�ch projekt�.
	 * 
	 * @return Jm�na otev�en�ch projekt�.
	 */
	String[] getProjectsNames() {
		return app.getProjectsNames();
	}

	/**
	 * Vrac� referenci na atribut <code>gui</code>.
	 * 
	 * @return T��da pro komunikaci <code>ExportFrameProvider</code>u se zbytkem
	 *         aplikace.
	 */
	public SignalSessionManager getSignalSessionManager() {
		return app;
	}

	/**
	 * Vrac� referenci na objekt s hlavi�kov�mi informacemi o projektu v aktu�ln�
	 * z�lo�ce.
	 * 
	 * @return Objekt s hlavi�kov�mi informacemi.
	 */
	Header getHeader() {
		return selectedTabProject.getHeader();
	}

	/**
	 * Vrac� hodnotu zoomu v ose funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @return zoom v ose funk�n�ch hodnot.
	 */
	float getZoomY() {
		return selectedTabProject.getZoomY();
	}

	/**
	 * Nastavuje hodnotu zoomu v ose funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @param zoomY
	 *          zoom v ose funk�n�ch hodnot.
	 */
	void setZoomY(float zoomY) {
		selectedTabProject.setZoomY(zoomY);
	}

	/**
	 * Vrac� barvu pozad� pro <b>SignalViewer</b>y v exportn�m okn�.
	 * 
	 * @return barva pozad� <b>SignalViewer</b>u.
	 */
	Color getCanvasColor() {
		return selectedTabProject.getCanvasColor();
	}

	/**
	 * Nastavuje barvu pozad� pro <b>SignalViewer</b>y v exportn�m okn�.
	 * 
	 * @param canvasColor
	 *          barva pozad� <b>SignalViewer</b>u.
	 */
	void setCanvasColor(Color canvasColor) {
		selectedTabProject.setCanvasColor(canvasColor);
		exportFrame.colorSettingWasChanged();
	}

	/**
	 * Vrac� barvu interpolace funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @return barva interpolace v <b>SignalViewer</b>u.
	 */
	Color getInterpolationColor() {
		return selectedTabProject.getInterpolationColor();
	}

	/**
	 * Nastavuje barvu interpolace funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @param interpolationColor
	 *          barva interpolace v <b>SignalViewer</b>u.
	 */
	void setInterpolationColor(Color interpolationColor) {
		selectedTabProject.setInterpolationColor(interpolationColor);
		exportFrame.colorSettingWasChanged();
	}

	/**
	 * Vrac� barvu os sou�adn�ho syst�mu pro <b>SignalViewer</b>y v exportn�m
	 * okn�.
	 * 
	 * @return barva os v <b>SignalViewer</b>u.
	 */
	Color getAxisColor() {
		return selectedTabProject.getAxisColor();
	}

	/**
	 * Nastavuje barvu os sou�adn�ho syst�mu pro <b>SignalViewer</b>y v exportn�m
	 * okn�.
	 * 
	 * @param axisColor
	 *          barva os v <b>SignalViewer</b>u.
	 */
	void setAxisColor(Color axisColor) {
		selectedTabProject.setAxisColor(axisColor);
		exportFrame.colorSettingWasChanged();
	}

	/**
	 * Vrac� barvu zv�razn�n� funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @return barva zv�razn�n� funk�n�ch hodnot v <b>SignalViewer</b>u.
	 */
	Color getFunctionalValuesColor() {
		return selectedTabProject.getFunctionalValuesColor();
	}

	/**
	 * Nastavuje barvu zv�razn�n� funk�n�ch hodnot pro <b>SignalViewer</b>y v
	 * exportn�m okn�.
	 * 
	 * @param functionalValuesColor
	 *          barva zv�razn�n� funk�n�ch hodnot v <b>SignalViewer</b>u.
	 */
	void setFunctionalValuesColor(Color functionalValuesColor) {
		selectedTabProject.setFunctionalValuesColor(functionalValuesColor);
		exportFrame.colorSettingWasChanged();
	}

	/**
	 * Vrac� koment�� exportovan�ch pr�m�r� v exportn�m okn�.
	 * 
	 * @return koment�� exportovan�ch pr�m�r�.
	 */
	String getCommentary() {
		return selectedTabProject.getCommentary();
	}

	/**
	 * Nastavuje koment�� exportovan�ch pr�m�r� v exportn�m okn�.
	 * 
	 * @param commentary
	 *          koment�� exportovan�ch pr�m�r�.
	 */
	void setCommentary(String commentary) {
		selectedTabProject.setCommentary(commentary);
		exportFrame.colorSettingWasChanged();
	}

	/**
	 * Vrac� referenci na atribut <code>averagingDataManager</code>.
	 * 
	 * @return the averagingDataManager
	 */
	AveragingDataManager getAveragingDataManager() {
		return averagingDataManager;
	}

	/**
	 * Vrac� zp�sob zobrazen� sign�l� v <b>SignalViewer</b>ech v exportn�m okn�.
	 * 
	 * @return zp�sob zobrazen� sign�l�.
	 */
	int getModeOfRepresentation() {
		return selectedTabProject.getModeOfRepresentationInExport();
	}

	/**
	 * Vrac� zp�sob zobrazen� sign�l� v <b>SignalViewer</b>ech v exportn�m okn�.
	 * 
	 * @param modeOfRepresentationInExport
	 *          zp�sob zobrazen� sign�l�. N�kter� z konstant t��dy
	 *          <i>SignalViewer</i>, kter� ur�uje zp�sob zobrazen� sign�lu.
	 */
	void setModeOfRepresentation(int modeOfRepresentationInExport) {
		selectedTabProject
				.setModeOfRepresentationInExport(modeOfRepresentationInExport);
		exportFrame.colorSettingWasChanged();
	}

	/**
	 * Vrac� ���ku zobrazen� exportovan�ch pr�m�r�.
	 * 
	 * @return ���ka zobrazen� exportovan�ch pr�m�r�.
	 */
	int getExportViewersWidth() {
		return selectedTabProject.getExportViewersWidth();
	}

	/**
	 * Nastavuje ���ku zobrazen� exportovan�ch pr�m�r�.
	 * 
	 * @param exportViewersWidth
	 *          ���ka zobrazen� exportovan�ch pr�m�r�.
	 */
	void setExportViewersWidth(int exportViewersWidth) {
		selectedTabProject.setExportViewersWidth(exportViewersWidth);
		exportFrame.colorSettingWasChanged();
	}

	/**
	 * Vrac� v�ku zobrazen� exportovan�ch pr�m�r�.
	 * 
	 * @return v�ka zobrazen� exportovan�ch pr�m�r�.
	 */
	int getExportViewersHeight() {
		return selectedTabProject.getExportViewersHeight();
	}

	/**
	 * Nastavuje v�ku zobrazen� exportovan�ch pr�m�r�.
	 * 
	 * @param exportViewersHeight
	 *          v�ka zobrazen� exportovan�ch pr�m�r�.
	 */
	void setExportViewersHeight(int exportViewersHeight) {
		selectedTabProject.setExportViewersHeight(exportViewersHeight);
		exportFrame.colorSettingWasChanged();
	}

	/**
	 * Vrac� booleovsk� pole, kter� pro v�echny kan�ly ve vstupn�m souboru
	 * obsahuje infromaci, zda se maj� jejich pr�m�ry zobrazit v exportovan�m
	 * dokumentu. Jako index pro p��stup k hodnot� pro konkr�tn� kan�l se pou��v�
	 * <code>channelOrderInInputFile</code>. Toto nastaven� m� samoz�ejm� vliv
	 * pouze na kan�ly, kter� se ��astn� pr�m�rov�n� epoch.
	 * 
	 * @return Booleovsk� pole
	 */
	boolean[] getChannelsToExport() {
		return selectedTabProject.getChannelsToExport();
	}

	/**
	 * Nastavuje, zda se m� pr�m�r konkr�tn�ho kan�lu zobrazit v exportovan�m
	 * dokumentu.
	 * 
	 * @param channelOrderInInput
	 *          po�ad� kan�lu ve vstupn�m souboru.
	 * @param exported
	 *          <code>true</code>, pokud se m� pr�m�r kan�lu objevit v
	 *          exportovan�m dokumentu, jinak <code>false</code>.
	 */
	void setChannelToExport(int channelOrderInInput, boolean exported) {
		selectedTabProject.getChannelsToExport()[channelOrderInInput] = exported;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Object state) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourceBundlePath = path;
		this.resourceBundle = ResourceBundle.getBundle(path);

	}

	@Override
	public void setResourceBundleKey(String key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateText() throws JUIGLELangException {
		setLocalizedResourceBundle(resourceBundlePath);
	}

	/**
	 * Return instance of the current resource bundle for Export gui.
	 * 
	 * @return instance of the current resource bundle for Export gui
	 * @version 0.1.0 (4/17/2010)
	 * @since 0.2.0 (4/17/2010)
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

}
