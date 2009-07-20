package noname;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import nezarazeno.IPerspectiveLoader;
import nezarazeno.PerspectiveException;
import ch.ethz.origo.jerpaui.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 07/18/09
 * @since 0.1.0
 */
public class PerspectiveLoader implements IPerspectiveLoader {
	
	private String path;
	
	private String defaultPerspectiveName;
	
	private Collection<Perspective> perspectives;

	/**
	 * Default constructor - initialize variables
	 * @throws PerspectiveException 
	 */
	public PerspectiveLoader() throws PerspectiveException {
		this.path = ConfigPropertiesLoader.getPerspectivesStoragePath();
		this.defaultPerspectiveName = ConfigPropertiesLoader.getDefaultPerspective().trim();
		loadPerspectives();
	}
	
	private void loadPerspectives() throws PerspectiveException {
		perspectives = new ArrayList<Perspective>();
		String[] perspectivesName = ConfigPropertiesLoader.getListOfPerspective();
		File fileDir = new File(path);
		File[] files = fileDir.listFiles(new JERPAFileNameFilter(perspectivesName));
		ClassLoader loader = PerspectiveLoader.class.getClassLoader();
		for (File file : files) {
			try {
				String classNameWithoutExts = file.getName().substring(0, file.getName().lastIndexOf("."));
				Perspective prsvClass = (Perspective) loader.loadClass(ConfigPropertiesLoader.getPerspectivePackagePath() +
						"." + classNameWithoutExts).newInstance();
				checkIfPerspectiveIsDefault(prsvClass, classNameWithoutExts);
				perspectives.add(prsvClass);
			} catch (InstantiationException e) {
				throw new PerspectiveException(PerspectiveLoader.class.getName(), e);
			} catch (IllegalAccessException e) {
				throw new PerspectiveException(PerspectiveLoader.class.getName(), e);
			} catch (ClassNotFoundException e) {
				throw new PerspectiveException(PerspectiveLoader.class.getName(), e);
			}
		}
	}

	private void checkIfPerspectiveIsDefault(Perspective perspective, String className) {
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
	public Collection<Perspective> getListOfPerspectives() {
		return perspectives;
	}
}