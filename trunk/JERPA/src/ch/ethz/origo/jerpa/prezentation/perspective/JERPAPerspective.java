package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import nezarazeno.PerspectiveException;
import ch.ethz.origo.jerpaui.prezentation.JERPAMenu;
import ch.ethz.origo.jerpaui.prezentation.JERPAMenuItem;
import ch.ethz.origo.jerpaui.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0 07/16/09
 * @since 0.1.0 (05/18/09)
 *
 */
public final class JERPAPerspective extends Perspective {

	/** Only for serialization */
	private static final long serialVersionUID = -8946236261770444906L;
	
	@Override
	public void initPerspectiveMenuPanel() throws PerspectiveException {
		if (menu == null) {
			menu = new JERPAMenu(JERPAMenu.MENU_LOCATION_TOP);
			// initialize menu items
			JERPAMenuItem helpItem = new JERPAMenuItem("Help");
			JERPAMenuItem aboutItem = new JERPAMenuItem("About");
			// initialize actions for menu actions
			Action helpAction = new AbstractAction() { // help item action
				/** Only for serialization */
				private static final long serialVersionUID = -3956658187607985009L;
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Help Menu");		
				}
			};	
			Action aboutAction = new AbstractAction() {
				/** Only for serialization */
				private static final long serialVersionUID = -7856826238481822196L;
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("About Menu");				
				}
			};
			// add actions to menu items
			helpItem.setAction(helpAction);
			aboutItem.setAction(aboutAction);
			// add items to menu
			menu.addItem(helpItem);
			menu.addItem(aboutItem);
			
			menu.setVisible(true);
		}
	}

	@Override
	public String getTitle() {
		return "JERPA Settings";
	}
	
}