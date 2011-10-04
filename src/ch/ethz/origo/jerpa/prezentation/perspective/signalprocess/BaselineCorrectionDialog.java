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
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

/**
 * Dialog pro opravu baseline
 * 
 * @author Petr - Soukal
 * @author Vaclav Souhrada (v dot souhrada at gmail dot com)
 * @version 0.1.3 (2/28/2010)
 * @since 0.1.0 (2/18/2010)
 * @see JDialog
 */
public class BaselineCorrectionDialog extends JDialog implements ILanguage {

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
	
	private JLabel startIntervalLabel;
	private JLabel endIntervalLabel;
	
	private JXButton applyBT;
	private JXButton stornoBT;
	
	private ResourceBundle resource;
	
	private String resourcePath;
	
	private JXPanel valuesPanel;

	/**
	 * Vytv��� objekt t��dy.
	 * 
	 * @param session
	 *          - current signal session
	 * @throws JUIGLELangException 
	 */
	public BaselineCorrectionDialog(final SignalSessionManager session) throws JUIGLELangException {
		super();
		this.session = session;
		this.setLocalizedResourceBundle(LangUtils
				.getPerspectiveLangPathProp(LangUtils.SIGNAL_PERSP_LANG_FILE_KEY));
		this.add(createInterior());
		updateText();
		this.setSize(new Dimension(DWIDTH, DHEIGHT));
		// this.pack();
		this.setResizable(false);
		this.setModal(true);
		LanguageObservable.getInstance().attach(this);
	}

	/**
	 * Vytv��� hlavn� panel.
	 * 
	 * @return mainPanel.
	 */
	private JXPanel createInterior() {
		JXPanel mainPanel = new JXPanel(new BorderLayout());
		mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
		mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);

		return mainPanel;
	}

	private JXPanel createCenterPanel() {
		JXPanel centerPanel = new JXPanel(new BorderLayout());
		buttonGroup = new ButtonGroup();
		FunctionRadioButtons radioAction = new FunctionRadioButtons();

		totalLengthSignalsRB = new JRadioButton();
		totalLengthSignalsRB.setActionCommand(TOTAL_LENGTH_STRING);
		totalLengthSignalsRB.addActionListener(radioAction);
		intervalSignalsRB = new JRadioButton();
		intervalSignalsRB.setActionCommand(INTERVAL_STRING);
		intervalSignalsRB.addActionListener(radioAction);
		buttonGroup.add(totalLengthSignalsRB);
		buttonGroup.add(intervalSignalsRB);

		centerPanel.add(totalLengthSignalsRB, BorderLayout.NORTH);
		centerPanel.add(intervalSignalsRB, BorderLayout.CENTER);
		centerPanel.add(createValuesPanel(), BorderLayout.SOUTH);

		return centerPanel;
	}

	private JXPanel createValuesPanel() {
		valuesPanel = new JXPanel();
		valuesPanel.setLayout(new BoxLayout(valuesPanel, BoxLayout.PAGE_AXIS));

		startIntervalLabel = new JLabel();
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

		endIntervalLabel = new JLabel();
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
		applyBT = new JXButton();
		applyBT.addActionListener(new FunctionApplyBT());
		stornoBT = new JXButton();
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
			BaselineCorrectionDialog.this.setVisible(false);
		}
	}

	@Override
	public String getResourceBundlePath() {
		return resourcePath;
	}

	@Override
	public void setLocalizedResourceBundle(String path) {
		this.resourcePath = path;
		this.resource = ResourceBundle.getBundle(resourcePath);
		
	}

	@Override
	public void setResourceBundleKey(String key) {
		// NOT USED FOR THIS CLASS
	}

	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setLocalizedResourceBundle(getResourceBundlePath());
				setTitle(resource.getString("diag.bsln.correct.title"));
				totalLengthSignalsRB.setText(resource.getString("diag.bsln.correct.length"));
				intervalSignalsRB.setText(resource.getString("diag.bsln.correct.interv"));
				valuesPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
						.createTitledBorder(resource.getString("diag.bsln.correct.interv.value")), BorderFactory
						.createEmptyBorder(BORDER_CONST, BORDER_CONST, BORDER_CONST,
								BORDER_CONST)));
				startIntervalLabel.setText(resource.getString("diag.bsln.correct.interv.start"));
				endIntervalLabel.setText(resource.getString("diag.bsln.correct.interv.end"));
				applyBT.setText(resource.getString("diag.bsln.correct.butt.apply"));
				stornoBT.setText(resource.getString("diag.bsln.correct.butt.storno"));
			}
		});		
	}

}
