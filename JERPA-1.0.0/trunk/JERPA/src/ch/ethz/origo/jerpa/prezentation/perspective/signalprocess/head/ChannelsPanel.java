/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *  
 *    Copyright (C) 2009 - 2010 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.head;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.prezentation.JUIGLEButton;

/**
 * Okno pro zobrazen� elektrod m���c� data. Slou�� pro zobrazen� sign�l�, kter�
 * chce u�ivatel vid�t.
 * 
 * @author Petr Soukal (original class from jERP Studio)
 * @author Vaclav Souhrada
 * @version 0.2.0 (1/31/2010)
 * @since 0.1.0 (11/17/09)
 */
public class ChannelsPanel extends JXPanel implements ILanguage {

	/** Only for serialization */
	private static final long serialVersionUID = 886527601758286651L;

	protected int MAX_ELECTRODE_LENGTH = 10, COLS = 3, ROWS = 0, BORDER = 5;
	protected JXPanel electrodesPanel;
	protected JUIGLEButton showChannels;
	private ChannelsPanelProvider channelsProvider;
	protected JCheckBox[] electrodes;
	private TitledBorder titleBorder;

	private String resourceBundlePath;

	private ResourceBundle resource;

	/**
	 * Vytv��� instance t��dy.
	 * 
	 * @param channelsWindowProvider
	 * @throws JUIGLELangException
	 */
	public ChannelsPanel(ChannelsPanelProvider channelsProvider)
			throws JUIGLELangException {
		super(new BorderLayout());
		// set localized files
		setLocalizedResourceBundle(LangUtils
				.getPerspectiveLangPathProp(LangUtils.SIGNAL_PERSP_LANG_FILE_KEY));
		// set up channel provider
		this.channelsProvider = channelsProvider;
		JScrollPane scrollPane = new JScrollPane();
		JXPanel centerPanel = new JXPanel(new BorderLayout());
		centerPanel.setBackground(Color.BLACK);
		JXPanel buttonPanel = new JXPanel();

		// LineBorder lineBorder = (LineBorder)
		// BorderFactory.createLineBorder(Color.RED);
		titleBorder = BorderFactory.createTitledBorder(resource
				.getString("channel.panel.bordelayout.title"));
		titleBorder.setTitleColor(Color.RED);
		CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
				titleBorder, BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER,
						BORDER));

		electrodesPanel = new JXPanel(new GridLayout(ROWS, COLS));
		electrodesPanel.setOpaque(false);
		electrodesPanel.setBorder(compoundBorder);

		showChannels = new JUIGLEButton(getResourceBundlePath(),
				"channel.panel.butt.1");
		showChannels.addActionListener(new FunctionShowChannelsBT());
		showChannels.setEnabled(false);
		showChannels.updateText();
		this.add(scrollPane);
		buttonPanel.add(showChannels);
		centerPanel.add(electrodesPanel, BorderLayout.CENTER);
		centerPanel.add(buttonPanel, BorderLayout.SOUTH);
		scrollPane.setViewportView(centerPanel);
		this.setVisible(true);
	}

	/**
	 * Vybere ozna�en� elektrody a pred� je pomoc� headWindowProvidera oknu pro
	 * zobrazov�n� a p�ehr�v�n� sign�l�.
	 */
	private class FunctionShowChannelsBT implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			channelsProvider.changeSelectedChannels();
		}
	}

	@Override
	public String getResourceBundlePath() {
		return resourceBundlePath;
	}

	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourceBundlePath = path;
		resource = ResourceBundle.getBundle(path);
	}

	/**
	 * NOT IMPLEMENTED FOR THIS CLASS
	 */
	@Override
	public void setResourceBundleKey(String key) {
		// NOT IMPLEMENTED FOR THIS CLASS
	}

	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					showChannels.updateText();
					titleBorder.setTitle(resource.getString("channel.panel.bordelayout.title"));
				} catch (JUIGLELangException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}
}
