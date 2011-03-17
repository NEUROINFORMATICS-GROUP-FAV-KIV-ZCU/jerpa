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
package ch.ethz.origo.jerpa.application.perspective;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.origo.jerpa.data.ConfigPropertiesLoader;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.juigle.application.IPerspectiveLoader;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.plugin.IPluggable;
import ch.ethz.origo.juigle.plugin.PluginEngine;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * This class loading all perspectives to the application. As first this 
 * class loading integrated perspectives. (This must be added to the configure file). 
 * Next loader loading classes (perspectives) as Plugin Engine.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.3 (5/04/2010)
 * @since 0.1.0 (07/18/09)
 * @see IPerspectiveLoader
 */
public class PerspectiveLoader implements IPerspectiveLoader {
	// TODO MOZNA PRERADIT DO JUIGLE

	private String defaultPerspectiveName;

	private static PerspectiveLoader loader;

	private List<Perspective> perspectives;

	/**
	 * Default constructor - initialize variables
	 * 
	 * @throws PerspectiveException
	 */
	public PerspectiveLoader() throws PerspectiveException {
		this.defaultPerspectiveName = ConfigPropertiesLoader
				.getDefaultPerspective().trim();
		loadPerspectives();
	}

	public static PerspectiveLoader getInstance() throws PerspectiveException {
		if (loader == null) {
			loader = new PerspectiveLoader();
		}
		return loader;
	}

	private void loadPerspectives() throws PerspectiveException {
		perspectives = new ArrayList<Perspective>();
		String[] perspectivesName = ConfigPropertiesLoader.getListOfPerspective();
		ClassLoader loader = PerspectiveLoader.class.getClassLoader();

		for (String name : perspectivesName) {
			try {
				Perspective prsvClass = (Perspective) loader.loadClass(
						ConfigPropertiesLoader.getPerspectivePackagePath() + "." + name)
						.newInstance();
				checkIfPerspectiveIsDefault(prsvClass, name);
				perspectives.add(prsvClass);
			} catch (InstantiationException e) {
				throw new PerspectiveException("JERPA023:" + name, e);
			} catch (IllegalAccessException e) {
				throw new PerspectiveException("JERPA023:" + name, e);
			} catch (ClassNotFoundException e) {
				throw new PerspectiveException("JERPA023:" + name, e);
			}
		}
		// now load perspectives from plugins
		PluginEngine plugEngine = PluginEngine.getInstance();
		for (IPluggable plugin : plugEngine.getAllCorrectPluggables(JERPAUtils.PLUGIN_PERSPECTIVES_KEY)) {
			perspectives.add((Perspective) plugin);
			plugEngine.startPluggable(plugin);
		}

	}

	private void checkIfPerspectiveIsDefault(Perspective perspective,
			String className) {
		if (className.equals(defaultPerspectiveName)) {
			perspective.setPerspectiveAsDefault(true);
		}
	}

	@Override
	public Perspective getDefaultPerspective() {
		for (Perspective per : perspectives) {
			if (per.isDefaultPerspective()) {
				return per;
			}
		}
		return null;
	}

	@Override
	public List<Perspective> getListOfPerspectives() {
		return perspectives;
	}
}