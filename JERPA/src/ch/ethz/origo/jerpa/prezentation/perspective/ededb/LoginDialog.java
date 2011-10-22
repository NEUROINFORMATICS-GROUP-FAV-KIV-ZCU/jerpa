package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.xml.ws.WebServiceException;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBProperties;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

/**
 * Class creating login dialog for setting up connection to EEG/ERP Database.
 * 
 * @author Petr Miko - miko.petr (at) gmail.com
 */
public class LoginDialog extends KeyAdapter implements ILanguage, ActionListener {

	private ResourceBundle resource;
	private String resourceBundlePath;
	private final EDEDClient session;
	private JFormattedTextField usernameField;
	private JPasswordField passwordField;
	private JFormattedTextField endpointField;
	private JDialog dialog;
	private JTextArea info;
	private JXLabel usernameLabel;
	private JXLabel passwordLabel;
	private JXLabel endpointLabel;
	private JButton okButton, cancelButton;
	private JToggleButton optionsButton;
	private final JXPanel moreLabelPane = new JXPanel(new GridLayout(0, 1));
	private final JXPanel moreFieldPane = new JXPanel(new GridLayout(0, 1));
	private String inputsErrorText;
	private String inputsErrorDesc;
	private String credentialsErrorText;
	private String credentialsErrorDesc;
	private String connectionErrorText;
	private String connectionErrorDesc;
	private final JProgressBar progress = new JProgressBar();
	public static Cursor busyCursor;
	public static Cursor defaultCursor;

	/**
	 * Constructor.
	 * 
	 * @param session EDEDSession from EDEDClient.jar
	 */
	public LoginDialog(EDEDClient session) {

		LanguageObservable.getInstance().attach(this);
		setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

		this.session = session;

		busyCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		defaultCursor = Cursor.getDefaultCursor();

		createDialog();
	}

	/**
	 * Creating LoginDialong JDialog with all its fields, labels and buttons
	 * (with actions).
	 */
	private void createDialog() {
		dialog = new JDialog();

		JXPanel canvas = new JXPanel();
		canvas.setLayout(new BoxLayout(canvas, BoxLayout.PAGE_AXIS));
		JXPanel labelPane = new JXPanel(new GridLayout(0, 1));
		JXPanel fieldPane = new JXPanel(new GridLayout(0, 1));
		JXPanel buttonPane = new JXPanel(new FlowLayout());

		updateErrorTexts();

		info = new JTextArea(resource.getString("logindialog.ededb.caution"));
		info.setEditable(false);
		info.setFocusable(false);
		info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		info.setBackground(canvas.getBackground());
		info.setForeground(canvas.getForeground());

		progress.setIndeterminate(true);
		progress.setVisible(false);

		endpointField = new JFormattedTextField();
		usernameField = new JFormattedTextField();
		passwordField = new JPasswordField();

		endpointLabel = new JXLabel(resource.getString("logindialog.ededb.endpoint"));
		usernameLabel = new JXLabel(resource.getString("logindialog.ededb.username"));
		passwordLabel = new JXLabel(resource.getString("logindialog.ededb.password"));
		usernameLabel.setLabelFor(usernameField);
		passwordLabel.setLabelFor(passwordField);

		endpointField.setColumns(10);
		usernameField.setColumns(10);
		passwordField.setColumns(10);

		usernameField.setText(EDEDBProperties.getConfigKey("ededb.username"));
		passwordField.setText(EDEDBProperties.getConfigKey("ededb.password"));
		endpointField.setText(EDEDBProperties.getConfigKey("ededb.endpoint"));

		moreLabelPane.add(endpointLabel);
		labelPane.add(usernameLabel);
		labelPane.add(passwordLabel);

		moreFieldPane.add(endpointField);
		fieldPane.add(usernameField);
		fieldPane.add(passwordField);

		okButton = new JButton(resource.getString("logindialog.ededb.buttons.ok"));
		cancelButton = new JButton(resource.getString("logindialog.ededb.buttons.cancel"));
		optionsButton = new JToggleButton(resource.getString("logindialog.ededb.buttons.more"));

		okButton.addActionListener(this);
		okButton.setActionCommand("ok");

		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");

		optionsButton.addActionListener(this);
		optionsButton.setActionCommand("options");

		endpointField.addKeyListener(this);
		usernameField.addKeyListener(this);
		passwordField.addKeyListener(this);

		if (endpointField.getText().isEmpty()) {
			optionsButton.setSelected(true);

			moreLabelPane.setVisible(true);
			moreFieldPane.setVisible(true);
		}
		else {

			moreLabelPane.setVisible(false);
			moreFieldPane.setVisible(false);
		}

		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		buttonPane.add(optionsButton);

		JXPanel top = new JXPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.LINE_AXIS));
		try {
			top.add(new JLabel(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "login_48.png", 32, 32)));
		}
		catch (PerspectiveException ex) {}

		top.add(info);

		JXPanel center = new JXPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS));
		center.add(labelPane);
		center.add(fieldPane);

		JXPanel more = new JXPanel();
		more.setLayout(new FlowLayout());
		more.add(moreLabelPane);
		more.add(moreFieldPane);

		canvas.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		canvas.add(top);
		canvas.add(center);
		canvas.add(buttonPane);
		canvas.add(more);
		canvas.add(progress);

		dialog.setTitle(resource.getString("logindialog.ededb.title"));
		dialog.setResizable(false);
		dialog.setAlwaysOnTop(true);
		dialog.setModal(true);
		dialog.add(canvas);
		dialog.pack();
		dialog.setVisible(false);
		dialog.setLocationRelativeTo(null);
	}

	/**
	 * Setter of LoginDialog visibility.
	 * 
	 * @param visibility boolean
	 */
	public void setVisible(boolean visibility) {
		if (dialog != null) {
			dialog.setVisible(visibility);
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
				dialog.setTitle(resource.getString("logindialog.ededb.title"));
				info.setText(resource.getString("logindialog.ededb.caution"));
				endpointLabel.setText(resource.getString("logindialog.ededb.endpoint"));
				usernameLabel.setText(resource.getString("logindialog.ededb.username"));
				passwordLabel.setText(resource.getString("logindialog.ededb.password"));
				okButton.setText(resource.getString("logindialog.ededb.buttons.ok"));
				cancelButton.setText(resource.getString("logindialog.ededb.buttons.cancel"));
				optionsButton.setText(resource.getString("logindialog.ededb.buttons.more"));
				LoginDialog.this.updateErrorTexts();
			}
		});

	}

	/**
	 * Init/update texts. Vital for localization.
	 */
	private void updateErrorTexts() {
		inputsErrorText = resource.getString("logindialog.ededb.errors.inputs.text");
		inputsErrorDesc = resource.getString("logindialog.ededb.errors.inputs.desc");
		credentialsErrorText = resource.getString("logindialog.ededb.errors.credentials.text");
		credentialsErrorDesc = resource.getString("logindialog.ededb.errors.credentials.desc");
		connectionErrorText = resource.getString("logindialog.ededb.errors.connection.text");
		connectionErrorDesc = resource.getString("logindialog.ededb.errors.connection.desc");
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if ("ok".equals(event.getActionCommand())) {
			final String tempEndpoint;
			final String tempUsername;
			final String tempPassword;

			if (!LoginDialog.this.endpointField.getText().isEmpty() && !LoginDialog.this.usernameField.getText().isEmpty()
			        && LoginDialog.this.passwordField.getPassword().length > 0) {

				tempEndpoint = endpointField.getText();
				tempUsername = usernameField.getText();
				tempPassword = new String(passwordField.getPassword());

				Thread loginThread = new Thread(new Runnable() {

					@Override
					public void run() {

						dialog.getRootPane().setCursor(busyCursor);

						try {
							session.userLogIn(tempUsername, tempPassword, tempEndpoint);
							EDEDBProperties.setConfigKey("ededb.endpoint", tempEndpoint);
							dialog.setVisible(false);

							EDEDBProperties.setConfigKey("ededb.username", tempUsername);
							EDEDBProperties.setConfigKey("ededb.password", tempPassword);
						}
						catch (WebServiceException ex) {

							if (ex.getCause().getClass() == IOException.class) {
								JUIGLErrorInfoUtils.showErrorDialog(credentialsErrorDesc, credentialsErrorText, ex);
							}
							else if (ex.getCause().getClass() == ConnectException.class) {
								JUIGLErrorInfoUtils.showErrorDialog(connectionErrorDesc, connectionErrorText, ex);
							}
							else {
								JUIGLErrorInfoUtils.showErrorDialog(ex.getMessage(), ex.getLocalizedMessage(), ex);
							}
						}
						catch (ConnectException ex) {
							JUIGLErrorInfoUtils.showErrorDialog(connectionErrorDesc, connectionErrorText, ex);
						}

						okButton.setEnabled(true);
						cancelButton.setEnabled(true);
						dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
						Working.setActivity(false, "working.ededb.connecting");
						progress.setVisible(false);

						dialog.getRootPane().setCursor(defaultCursor);
					}
				});

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						okButton.setEnabled(false);
						cancelButton.setEnabled(false);
						dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
						Working.setActivity(true, "working.ededb.connecting");
						progress.setVisible(true);
					}
				});

				loginThread.start();

			}
			else {
				JOptionPane.showMessageDialog(new JFrame(), LoginDialog.this.inputsErrorText, LoginDialog.this.inputsErrorDesc,
				        JOptionPane.ERROR_MESSAGE);
			}
		}

		else if ("cancel".equals(event.getActionCommand())) {
			dialog.setVisible(false);
		}

		else if ("options".equals(event.getActionCommand())) {
			moreLabelPane.setVisible(LoginDialog.this.optionsButton.isSelected());
			moreFieldPane.setVisible(LoginDialog.this.optionsButton.isSelected());

			dialog.pack();
		}

	}

	@Override
	public void keyReleased(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.VK_ENTER) {
			LoginDialog.this.okButton.doClick();
		}
		else if (keyCode == KeyEvent.VK_ESCAPE) {
			LoginDialog.this.cancelButton.doClick();
		}
	}

}
