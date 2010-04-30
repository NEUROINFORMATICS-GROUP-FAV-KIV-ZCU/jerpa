package ch.ethz.origo.jerpa.prezentation.perspective.db;

import javax.swing.JButton;
import javax.swing.JDialog;


import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginEvent;
import org.jdesktop.swingx.auth.LoginListener;

import ch.ethz.origo.juigle.prezentation.database.JUIGLELoginPane;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/28/2010)
 * @since 0.1.0 (1/28/2010)
 * @see 
 * @see LoginListener
 *
 */
public class LoginDialog extends JUIGLELoginPane implements LoginListener {

	/** Only for serialization */
	private static final long serialVersionUID = -8148301402485470747L;
	
	private String title;
	
	public LoginDialog(String title) {
		this.title = title;
		initialize();
	}

	private void initialize() {
		JUIGLELoginPane.JXLoginFrame frame = JUIGLELoginPane.showLoginFrame(this);
	  frame.setTitle(title);
	  frame.setAlwaysOnTop(true);
	  frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		LoginDialog ld = new LoginDialog("Nazdar koco");
	}

	@Override
	public void loginCanceled(LoginEvent source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loginFailed(LoginEvent source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loginStarted(LoginEvent source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loginSucceeded(LoginEvent source) {
		// TODO Auto-generated method stub
		
	}

}
