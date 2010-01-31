package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.head;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.prezentation.JUIGLEButton;

/**
 * Okno pro zobrazen� elektrod m���c� data. Slou�� pro zobrazen� sign�l�, kter�
 * chce u�ivatel vid�t.
 * 
 * @author Petr Soukal (original class from jERP Studio)
 * @author Vaclav Souhrada
 * @version 0.1.1 (1/31/2010)
 * @since 0.1.0 (11/17/09)
 */
public class ChannelsPanel extends JXPanel {

	/** Only for serialization */
	private static final long serialVersionUID = 886527601758286651L;

	int MAX_ELECTRODE_LENGTH = 10, COLS = 3, ROWS = 0, BORDER = 5;
	JXPanel electrodesPanel;
	JUIGLEButton showChannels;
	private ChannelsPanelProvider channelsProvider;
	JCheckBox[] electrodes;

	/**
	 * Vytv��� instance t��dy.
	 * 
	 * @param channelsWindowProvider
	 * @throws JUIGLELangException 
	 */
	public ChannelsPanel(ChannelsPanelProvider channelsProvider) throws JUIGLELangException {
		// super("Show signals", true, true, false, true);
		super(new BorderLayout());
		this.channelsProvider = channelsProvider;
		JScrollPane scrollPane = new JScrollPane();
		JXPanel centerPanel = new JXPanel(new BorderLayout());
		centerPanel.setBackground(Color.BLACK);
		JXPanel buttonPanel = new JXPanel();

		// LineBorder lineBorder = (LineBorder)
		// BorderFactory.createLineBorder(Color.RED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder("All Channels");
		titleBorder.setTitleColor(Color.RED);
		CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
				titleBorder, BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER,
						BORDER));

		electrodesPanel = new JXPanel(new GridLayout(ROWS, COLS));
		electrodesPanel.setOpaque(false);
		electrodesPanel.setBorder(compoundBorder);

		showChannels = new JUIGLEButton(LangUtils
				.getPerspectiveLangPathProp(LangUtils.SIGNAL_PERSP_LANG_FILE_KEY),
				"channel.panel.butt.0");
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
}
