package noname;

import java.io.InputStream;

import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEFrame;
import ch.ethz.origo.juigle.prezentation.JUIGLEMainMenu;

public class TestMainFrame extends JUIGLEFrame {
	
	/** Only for serialization */
	private static final long serialVersionUID = -1903614339597274647L;

	public TestMainFrame() {
		super();
		StringBuffer titleBuff = new StringBuffer();
		titleBuff.append(ConfigPropertiesLoader.getApplicationTitle());
		titleBuff.append(" ");
		titleBuff.append(ConfigPropertiesLoader.getAppMajorVersion());
		titleBuff.append(".");
		titleBuff.append(ConfigPropertiesLoader.getAppMinorVersion());
		titleBuff.append(".");
		titleBuff.append(ConfigPropertiesLoader.getAppRevisionVersion());
		this.setTitle(titleBuff.toString());
		this.setLogo(ClassLoader
				.getSystemResourceAsStream("ch/ethz/origo/jerpa/data/images/Jerpa_icon.png"));
		try {
			this.addMainMenu(getMainMenu());
			this.setPerspectives(PerspectiveLoader.getInstance());
		} catch (PerspectiveException e) {
			e.printStackTrace();
		}
		this.setVisible(true);	
		
	}

	
	public TestMainFrame(String title, InputStream logoImg) {
		super(title, logoImg);
	}
	

	private JUIGLEMainMenu getMainMenu() throws PerspectiveException {
		JUIGLEMainMenu mainMenu = new JUIGLEMainMenu();
		mainMenu.setLocalizedResourceBundle(LangUtils.MAIN_FILE_PATH);
		mainMenu.addHomePageItem(null, "http://jerpa.origo.ethz.ch/");
		return mainMenu;
	}
	

}
