package ch.ethz.origo.jerpa.application.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ch.ethz.origo.jerpa.application.exception.ProjectOperationException;

/**
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * 
 */
public abstract class SessionManager {

	protected ArrayList<Project> projects;
	protected int currentProjectIndex;

	public SessionManager() {
		projects = new ArrayList<Project>();
		currentProjectIndex = projects.size() - 1;
	}

	public abstract void saveCurrentProject() throws ProjectOperationException;

	public abstract void loadFile(File file) throws ProjectOperationException;

	/**
	 * Load saved project from file ( object - <code>file</code> ) and set up as
	 * current project.
	 * 
	 * @param file
	 *          object which represent file where is project file.
	 * @throws ProjectOperationException
	 */
	public abstract void loadProject(File file) throws ProjectOperationException;
	
	/**
	 * Uzav�e v�echny otev�en� projekty a sma�e jejich do�asn� soubory.
	 */
	public void closeAllProjects() {
		while (projects.size() > 0) {
			try {
				closeProject(0);
			} catch (IOException e) {
				// System.out.println("Projekt nemohl byt uzavren - soubor nemohl byt
				// smazan.");
			}
		}
	}

	/**
	 * Uzav�e projekt se zadan�m indexem a sma�e jeho do�asn� soubor.
	 * 
	 * @param index
	 *          Index projektu, kter� se m� uzav��t.
	 * @throws IOException
	 */
	protected void closeProject(int index) throws IOException {

		if (index < 0 || index >= projects.size()) {
			return;
		}

		Project project = projects.get(index);

		project.closeBuffers();

		projects.remove(index);
		currentProjectIndex = projects.size() - 1;

	}

	/**
	 * Uzav�e aktu�ln� otev�en� projekt a sma�e jeho do�asn� soubor.
	 * 
	 * @return Index nov�ho aktu�ln� otev�en�ho projektu. Nen�-li ��dn� projekt
	 *         otev�en, vrac� -1.
	 * @throws java.io.IOException
	 */
	protected int closeCurrentProject() throws IOException {

		if (currentProjectIndex >= projects.size()) {
			new Exception("Error while closing project: currentProjectIndex: "
					+ currentProjectIndex + ", projects.size(): " + projects.size())
					.printStackTrace();
			currentProjectIndex = projects.size() - 1;
		}

		if (currentProjectIndex < 0) {
			currentProjectIndex = -1;
			return currentProjectIndex;
		}

		getCurrentProject().closeBuffers();

		projects.remove(currentProjectIndex);

		switchProject(0); /*
											 * TODO - historie napredposledy zobrazeneho projektu, pri
											 * uzavreni aktualniho projektu prepnout na ten
											 * napredposledy zobrazeny; zaroven upravit prezentacni
											 * vrstvu, bude-li treba
											 */
		return currentProjectIndex;
	}
	
	/**
	 * Vr�t� projekt zadan� indexem.<br/> Je-li zad�n �patn� index, vrac� null.
	 * 
	 * @param index
	 * @return Projekt se zadan� indexem.
	 */
	public Project getProject(int index) {
		if (index < 0 || index > projects.size()) {
			return null;
		}

		return projects.get(index);
	}

	/**
	 * Vr�t� referenci na aktu�ln� otev�en� (zobrazen�) projekt.
	 * 
	 * @return Reference na aktu�ln� otev�en� projekt nebo <code>null</code>,
	 *         pokud ��dn� projekt nen� otev�en.
	 */
	public Project getCurrentProject() {
		if (currentProjectIndex < 0) {
			return null;
		} else if (currentProjectIndex >= projects.size()) {
			return null;
		} else {
			return projects.get(currentProjectIndex);
		}
	}

	/**
	 * Vrac� index aktu�ln� otev�en�ho projektu.
	 * 
	 * @return Index aktu�ln� otev�en�ho projektu.
	 */
	public int getCurrentProjectIndex() {
		return currentProjectIndex;
	}

	/**
	 * Vr�t� pole s n�zvy projekt�. Indexy v poli koresponduj� s indexy projekt�.
	 * 
	 * @return Pole s n�zvy projekt�.
	 */
	public String[] getProjectsNames() {
		String[] names = new String[projects.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = new String(projects.get(i).getName());
		}
		return names;
	}
	
	/**
	 * P�epne aktu�ln� projekt na projekt zadan� indexem a vrac� index tohoto
	 * projektu.<br/> Je-li index mimo rozsah otev�en�ch projekt�, je jako
	 * aktu�ln� projekt nastaven projekt s indexem 0. Nen�-li otev�en ��dn�
	 * projekt, metoda vrac� hodnotu -1.
	 * 
	 * @param index
	 *          Index projektu.
	 * @return Index aktu�ln� nastaven�ho projektu. -1, nen�-li otev�en ��dn�
	 *         projekt.
	 */
	public int switchProject(int index) {
		if (projects == null || projects.size() == 0) {
			currentProjectIndex = -1;
			return -1;
		}

		if (index < 0 || index >= projects.size()) {
			currentProjectIndex = 0;
		} else {
			currentProjectIndex = index;
		}

		return currentProjectIndex;
	}
	
	/**
	 * Zalo�� nov� pr�zdn� projekt, p�id� ho do seznamu projekt� a nastav� jej
	 * jako aktu�ln�.
	 * 
	 * @param name
	 *          N�zev projektu.
	 */
	protected void createProject(Project project, String name) {
		project.setName(name);
		projects.add(project);
		currentProjectIndex = projects.size() - 1;
	}

}
