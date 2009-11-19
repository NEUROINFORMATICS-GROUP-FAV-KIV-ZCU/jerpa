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

/**
 * Okno pro zobrazen� elektrod m���c� data. Slou�� pro zobrazen� sign�l�, kter�
 * chce u�ivatel vid�t.
 * 
 * @author Petr Soukal (original class from jERP Studio)
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/17/09)
 * @since 0.1.0
 */
public class ChannelsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	int MAX_ELECTRODE_LENGTH = 10, COLS = 3, ROWS = 0, BORDER = 5;
	JPanel electrodesPanel;
	JButton showChannels;
	private ChannelsPanelProvider channelsProvider;
	JCheckBox[] electrodes;

	/**
	 * Vytv��� instance t��dy.
	 * 
	 * @param channelsWindowProvider
	 */
	public ChannelsPanel(ChannelsPanelProvider channelsProvider) {
		// super("Show signals", true, true, false, true);
		super(new BorderLayout());
		this.channelsProvider = channelsProvider;
		JScrollPane scrollPane = new JScrollPane();
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(Color.BLACK);
		JPanel buttonPanel = new JPanel();

		// LineBorder lineBorder = (LineBorder)
		// BorderFactory.createLineBorder(Color.RED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder("All Channels");
		titleBorder.setTitleColor(Color.RED);
		CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
				titleBorder, BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER,
						BORDER));

		electrodesPanel = new JPanel(new GridLayout(ROWS, COLS));
		electrodesPanel.setOpaque(false);
		electrodesPanel.setBorder(compoundBorder);

		showChannels = new JButton("Show selected channels");
		showChannels.addActionListener(new FunctionShowChannelsBT());
		showChannels.setEnabled(false);
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
