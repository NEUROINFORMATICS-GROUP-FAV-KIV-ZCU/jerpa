package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;

/**
 * Dialog pro opravu baseline
 * 
 * @author Petr - Soukal
 * @author Vaclav Souhrada (v dot souhrada at gmail dot com)
 * @version 0.1.1 (2/23/2010)
 * @since 0.1.0 (2/18/2010)
 * @see JDialog
 */
public class BaselineCorrectionDialog extends JDialog {

	/** Only for serialization */
	private static final long serialVersionUID = 5759202735358682431L;
	
	private final long SPINN_UPPER_LIMIT = Long.MAX_VALUE;
	private final long START_SPINN_VALUE = 0, END_SPINN_VALUE = 500,
			SPINN_LOWER_LIMIT = 0, SPINN_STEP = 1;
	private final int DWIDTH = 250, DHEIGHT = 260, BORDER_CONST = 5;
	private final String DESCRIPTION_UNIT = "ms", TOTAL_LENGTH_STRING = "total",
			INTERVAL_STRING = "interval";
	private SignalSessionManager session;

	private ButtonGroup buttonGroup;
	private JRadioButton totalLengthSignalsRB;
	private JRadioButton intervalSignalsRB;
	private JSpinner startInterval;
	private JSpinner endInterval;

	/**
	 * Vytv��� objekt t��dy.
	 * 
	 * @param session
	 *          - current signal session
	 */
	public BaselineCorrectionDialog(final SignalSessionManager session) {
		super();
		this.setTitle("Baseline Correction");
		this.session = session;
		this.add(createInterior());
		this.setSize(new Dimension(DWIDTH, DHEIGHT));
		// this.pack();
		this.setResizable(false);
		this.add(createInterior());
	}

	/**
	 * Vytv��� hlavn� panel.
	 * 
	 * @return mainPanel.
	 */
	private JPanel createInterior() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
		mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);

		return mainPanel;
	}

	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel(new BorderLayout());
		buttonGroup = new ButtonGroup();
		FunctionRadioButtons radioAction = new FunctionRadioButtons();

		totalLengthSignalsRB = new JRadioButton("Signals total length");
		totalLengthSignalsRB.setActionCommand(TOTAL_LENGTH_STRING);
		totalLengthSignalsRB.addActionListener(radioAction);
		intervalSignalsRB = new JRadioButton("Signals interval");
		intervalSignalsRB.setActionCommand(INTERVAL_STRING);
		intervalSignalsRB.addActionListener(radioAction);
		buttonGroup.add(totalLengthSignalsRB);
		buttonGroup.add(intervalSignalsRB);

		centerPanel.add(totalLengthSignalsRB, BorderLayout.NORTH);
		centerPanel.add(intervalSignalsRB, BorderLayout.CENTER);
		centerPanel.add(createValuesPanel(), BorderLayout.SOUTH);

		return centerPanel;
	}

	private JPanel createValuesPanel() {
		JPanel valuesPanel = new JPanel();
		valuesPanel.setLayout(new BoxLayout(valuesPanel, BoxLayout.PAGE_AXIS));
		valuesPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Signals Interval Values"), BorderFactory
				.createEmptyBorder(BORDER_CONST, BORDER_CONST, BORDER_CONST,
						BORDER_CONST)));

		JLabel startIntervalLabel = new JLabel("Start interval:");
		JPanel startIntervalLabelP = new JPanel();
		startIntervalLabelP.add(startIntervalLabel);
		startIntervalLabelP.setMaximumSize(new Dimension(startIntervalLabelP
				.getPreferredSize().width,
				startIntervalLabelP.getPreferredSize().height));
		startIntervalLabelP.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel startIntervalPanel = new JPanel();
		startInterval = new JSpinner(new SpinnerNumberModel(START_SPINN_VALUE,
				SPINN_LOWER_LIMIT, SPINN_UPPER_LIMIT, SPINN_STEP));
		startInterval.setPreferredSize(new Dimension(DWIDTH / 2, startInterval
				.getPreferredSize().height));
		startIntervalPanel.add(startInterval);
		startIntervalPanel.add(new JLabel(DESCRIPTION_UNIT));
		startIntervalPanel
				.setMaximumSize(new Dimension(
						startIntervalPanel.getPreferredSize().width, startIntervalPanel
								.getPreferredSize().height));
		startIntervalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel endIntervalLabel = new JLabel("End interval:");
		JPanel endIntervalLabelP = new JPanel();
		endIntervalLabelP.add(endIntervalLabel);
		endIntervalLabelP
				.setMaximumSize(new Dimension(
						endIntervalLabelP.getPreferredSize().width, endIntervalLabelP
								.getPreferredSize().height));
		endIntervalLabelP.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel endIntervalPanel = new JPanel();
		endInterval = new JSpinner(new SpinnerNumberModel(END_SPINN_VALUE,
				SPINN_LOWER_LIMIT, SPINN_UPPER_LIMIT, SPINN_STEP));
		endInterval.setPreferredSize(new Dimension(DWIDTH / 2, endInterval
				.getPreferredSize().height));
		endIntervalPanel.add(endInterval);
		endIntervalPanel.add(new JLabel(DESCRIPTION_UNIT));
		endIntervalPanel.setMaximumSize(new Dimension(endIntervalPanel
				.getPreferredSize().width, endIntervalPanel.getPreferredSize().height));
		endIntervalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		valuesPanel.add(startIntervalLabelP);
		valuesPanel.add(startIntervalPanel);
		valuesPanel.add(endIntervalLabelP);
		valuesPanel.add(endIntervalPanel);

		totalLengthSignalsRB.doClick();

		return valuesPanel;
	}

	/**
	 * Vytv��� panel s tla��tky.
	 * 
	 * @return southPanel.
	 */
	private JPanel createSouthPanel() {
		JPanel southPanel = new JPanel();
		JButton applyBT = new JButton("Apply");
		applyBT.addActionListener(new FunctionApplyBT());
		JButton stornoBT = new JButton("Storno");
		stornoBT.addActionListener(new FunctionStornoBT());

		southPanel.add(applyBT);
		southPanel.add(stornoBT);

		return southPanel;
	}

	/**
	 * Nastavuje viditelnost dialogu a jeho um�st�n� na monitoru.
	 */
	public void setActualLocationAndVisibility() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void setSpinnersValues(long startTimeStamp, long endTimeStamp) {
		startInterval.setValue(new Double(startTimeStamp));
		endInterval.setValue(new Double(endTimeStamp));
		intervalSignalsRB.doClick();
	}

	/**
	 * Obsluhuje tla��tko pro vykon�n� automatick�ho ozna�ov�n� artefakt� vybran�m
	 * krit�riem.
	 */
	private class FunctionRadioButtons implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getActionCommand().equals(TOTAL_LENGTH_STRING)) {
				startInterval.setEnabled(false);
				endInterval.setEnabled(false);
			} else {
				startInterval.setEnabled(true);
				endInterval.setEnabled(true);
			}
		}
	}

	/**
	 * Obsluhuje tla��tko pro vykon�n� automatick�ho ozna�ov�n� artefakt� vybran�m
	 * krit�riem.
	 */
	private class FunctionApplyBT implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (intervalSignalsRB.isSelected()) {
				Double startIntervalValue = (Double) startInterval.getValue();
				Double endIntervalValue = (Double) endInterval.getValue();

				if (startIntervalValue.longValue() >= endIntervalValue.longValue()) {
					JOptionPane.showMessageDialog(null,
							"End-interval value must be greater than Start-interval value.",
							"Values Error", JOptionPane.ERROR_MESSAGE);
				} else {
					session.applyBaselineCorrection(startIntervalValue
							.longValue(), endIntervalValue.longValue());
//					mainWindow.setEnabled(true);
					BaselineCorrectionDialog.this.setVisible(false);
				}
			} else {
				session.applyBaselineCorrection();
//				mainWindow.setEnabled(true);
				BaselineCorrectionDialog.this.setVisible(false);
			}
		}
	}

	/**
	 * Obsluhuje tla��tko pro stornov�n� akce a zav�en� dialogu.
	 */
	private class FunctionStornoBT implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
//			mainWindow.setEnabled(true);
			BaselineCorrectionDialog.this.setVisible(false);
		}
	}
}
