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
 *    Copyright (C) 2009 - 2011 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.application.perspective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import ch.ethz.origo.jerpa.data.ConfigPropertiesLoader;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.juigle.application.IPerspectiveLoader;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.plugin.IPluggable;
import ch.ethz.origo.juigle.plugin.PluginEngine;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * This class loading all perspectives to the application. As first this class
 * loading integrated perspectives. (This must be added to the configure file).
 * Next loader loading classes (perspectives) as Plugin Engine.
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.2.0 (4/3/2011)
 * @since 0.1.0 (07/18/09)
 * @see IPerspectiveLoader
 */
public class PerspectiveLoader implements IPerspectiveLoader {
	// FIXME Since JUIGLE 2.0 will be this class part of JUIGLE library

	private String defaultPerspectiveName;

	private static PerspectiveLoader loader;

	private Map<String, Perspective> mapOfPerspectives;

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
		mapOfPerspectives = new HashMap<String, Perspective>();
		String[] perspectivesName = ConfigPropertiesLoader.getListOfPerspective();
		ClassLoader loader = PerspectiveLoader.class.getClassLoader();

		for (String name : perspectivesName) {
			try {
				Perspective prsvClass = (Perspective) loader.loadClass(
						ConfigPropertiesLoader.getPerspectivePackagePath() + "." + name)
						.newInstance();
				checkIfPerspectiveIsDefault(prsvClass, name);
				mapOfPerspectives.put(prsvClass.getID(), prsvClass);
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
		List<IPluggable> listOfPluggable = plugEngine
				.getAllCorrectPluggables(JERPAUtils.PLUGIN_PERSPECTIVES_KEY);
		if (listOfPluggable != null && !listOfPluggable.isEmpty()) {
			for (IPluggable plugin : listOfPluggable) {
				Perspective perspective = (Perspective) plugin;
				mapOfPerspectives.put(perspective.getID(), perspective);
				plugEngine.startPluggable(plugin);
			}
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
		for (Perspective per : getListOfPerspectives()) {
			if (per.isDefaultPerspective()) {
				return per;
			}
		}

		return null;
	}

	@Override
	public List<Perspective> getListOfPerspectives() {
		List<Perspective> perspectives = new ArrayList<Perspective>();
		for (Entry<String, Perspective> entry : mapOfPerspectives.entrySet()) {
			perspectives.add(entry.getValue());
		}

		return perspectives;
	}

	@Override
	public Perspective getPerspective(String id) {
		if (mapOfPerspectives != null && !mapOfPerspectives.isEmpty()) {
			return mapOfPerspectives.get(id);
		}

		return null;
	}
}