package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * Class of GUI fields containing login username and download folder
 * information.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class LoginInfo extends JXPanel implements Observer, ILanguage {

	private static final long serialVersionUID = 8282126523750543895L;
	private ResourceBundle resource;
	private String resourceBundlePath;
	private final JTextArea usernameText;
	private final EDEDClient session;
	private final EDEDBController controller;

	private final TitledBorder usernameBorder;

	/**
	 * Constructor creating JXPanel for information fields.
	 * 
	 * @param controller EDEDB EDEDBController
	 * @param session EDEDSession
	 */
	public LoginInfo(EDEDBController controller, EDEDClient session) {
		super();

		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.session = session;
		this.controller = controller;

		usernameText = createArea();
		usernameBorder = new TitledBorder(resource.getString("sidebar.ededb.info.title.username"));
		usernameBorder.setTitleJustification(TitledBorder.CENTER);
		usernameText.setBorder(usernameBorder);
		usernameText.setText(resource.getString("sidebar.ededb.info.text.notloggedin"));

		this.add(usernameText);
	}

	/**
	 * Method creating JTextArea with certain parameters.
	 * 
	 * @return
	 */
	private JTextArea createArea() {
		JTextArea area = new JTextArea();

		area.setFocusable(false);
		area.setBackground(getBackground());
		area.setForeground(getForeground());

		area.setWrapStyleWord(true);

		return area;
	}

	/**
	 * Shown information update method.
	 */
	public void updateLoginInfo() {
		if (session.isConnected()) {
			usernameText.setText(session.getUsername());
		}
		else {
			usernameText.setText(resource.getString("sidebar.ededb.info.text.notloggedin"));
		}
	}

	/**
	 * Setter of localization resource bundle path
	 * 
	 * @param path path to localization source file.
	 */
	@Override
	public void setLocalizedResourceBundle(String path) {
		resourceBundlePath = path;
		resource = ResourceBundle.getBundle(path);
	}

	/**
	 * Getter of path to resource bundle.
	 * 
	 * @return path to localization file.
	 */
	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	/**
	 * Setter of resource bundle key.
	 * 
	 * @param string key
	 */
	@Override
	public void setResourceBundleKey(String string) {
		throw new UnsupportedOperationException("Method is not implemented yet...");
	}

	/**
	 * Method invoked by change of LanguageObservable.
	 * 
	 * @throws JUIGLELangException
	 */
	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				usernameBorder.setTitle(resource.getString("sidebar.ededb.info.title.username"));

				updateLoginInfo();
			}
		});
	}

	@Override
	public void update(Observable o, Object arg) {

		updateLoginInfo();
		repaint();

	}
}
