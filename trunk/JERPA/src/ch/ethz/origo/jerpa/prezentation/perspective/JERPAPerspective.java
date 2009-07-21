package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;

import nezarazeno.PerspectiveException;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.Painter;

import ch.ethz.origo.jerpaui.prezentation.GraphicsUtilities;
import ch.ethz.origo.jerpaui.prezentation.JERPAFrame;
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
			menu.setFloatable(false);
			menu.setRollover(true);
			// initialize menu items
			JERPAMenuItem helpItem = new JERPAMenuItem("Help");
			JERPAMenuItem aboutItem = new JERPAMenuItem("About JERPA");
			JERPAMenuItem pluginItem = new JERPAMenuItem("Plugins");
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
			try {
				helpItem
						.setIcon(ImageIO
								.read(ClassLoader
										.getSystemResourceAsStream("ch/ethz/origo/jerpa/data/images/icon.gif")));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// add actions to menu items
			helpItem.setAction(helpAction);
			aboutItem.setAction(aboutAction);
			// add items to menu
			menu.addItem(helpItem);
			menu.addItem(aboutItem);
			menu.addItem(pluginItem);

			// menu.setVisible(true);
			if (menuPanel == null) {
				menuPanel = new JXPanel();
				menuPanel.setOpaque(false);

				final Paint menuBackground = GraphicsUtilities.createBackgroundTexture(
						Color.BLUE, Color.LIGHT_GRAY, 300);
				Painter<Component> p = new Painter<Component>() {

					@Override
					public void paint(Graphics2D g, Component c, int width, int height) {
						Graphics2D g2d = g;
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
						g2d.setPaint(menuBackground);

						if (JERPAFrame.getFrameState() != JXFrame.MAXIMIZED_BOTH) {
							g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
						} else {
							g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
						}
					}
				};
				menuPanel.setBackgroundPainter(p);
				menuPanel.add(menu);
			}
		}
	}

	@Override
	public String getTitle() {
		return "JERPA Settings";
	}

}