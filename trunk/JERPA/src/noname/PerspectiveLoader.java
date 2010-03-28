package noname;

import java.util.ArrayList;
import java.util.List;

import nezarazeno.IPerspectiveLoader;
import ch.ethz.origo.jerpa.data.ConfigPropertiesLoader;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.plugin.Pluggable;
import ch.ethz.origo.juigle.plugin.PluginEngine;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.2 (3/28/2010)
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
				throw new PerspectiveException(PerspectiveLoader.class.getName(), e);
			} catch (IllegalAccessException e) {
				throw new PerspectiveException(PerspectiveLoader.class.getName(), e);
			} catch (ClassNotFoundException e) {
				throw new PerspectiveException(PerspectiveLoader.class.getName(), e);
			}
		}
		// now load perspectives from plugins
		PluginEngine plugEngine = PluginEngine.getInstance();
		for (Pluggable plugin : plugEngine.getAllCorrectPluggables(JERPAUtils.PLUGIN_PERSPECTIVES_KEY)) {
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