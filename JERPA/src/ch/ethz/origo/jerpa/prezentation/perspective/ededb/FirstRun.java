package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * JXPanel extending class for performing first run interface.
 *
 * @author Petr Miko
 */
public class FirstRun extends JXPanel implements ILanguage {

	private static final long serialVersionUID = 7981615796094888373L;
	private ResourceBundle resource;
	private String resourceBundlePath;

	private final JButton choose;
	private final JTextArea text;
	private final TitledBorder titledBorder;

	/**
	 * Constructor method creating whole FirstRun JXPanel with all its elements.
	 *
	 * @param controller EDEDBController
	 */
	public FirstRun(final EDEDBController controller) {
		super();

		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		titledBorder = new TitledBorder(resource.getString("firstrun.ededb.title"));
		titledBorder.setTitleJustification(TitledBorder.CENTER);

		JScrollPane textPane = new JScrollPane();

		text = new JTextArea(resource.getString("firstrun.ededb.text"));
		text.setBackground(this.getBackground());
		text.setForeground(this.getForeground());
		text.setEditable(false);
		text.setFocusable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);

		textPane.getViewport().add(text);

		choose = new JButton(resource.getString("firstrun.ededb.choosedir"));

		choose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnValue = fileChooser.showOpenDialog(new JPanel());

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selected = fileChooser.getSelectedFile();

					controller.setDownloadPath(selected.getAbsolutePath());
					controller.setFirstRun(false);
					controller.initGraphics();
					controller.update();
				}
			}
		});

		this.add(textPane);
		this.add(choose);
		this.setBorder(titledBorder);

	}

	/**
	 * Setter of localization resource bundle path
	 *
	 * @param path path to localization source file.
	 */
	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourceBundlePath = path;
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
				titledBorder.setTitle(resource.getString("firstrun.ededb.title"));
				text.setText(resource.getString("firstrun.ededb.text"));
				choose.setText(resource.getString("firstrun.ededb.choosedir"));
			}
		});
	}
}
