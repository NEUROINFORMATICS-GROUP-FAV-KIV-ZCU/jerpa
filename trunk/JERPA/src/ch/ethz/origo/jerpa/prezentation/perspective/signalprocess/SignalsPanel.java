package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;

import ch.ethz.origo.jerpa.application.Const;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;

/**
 * Okno pro zobrazen� nam��en�ch dat a jejich p�ehr�v�n�. D�le tak� slou�� k
 * vyb�r�n� epoch na zobrazen�ch sign�lech pro pr�m�rov�n� dat.
 * 
 * @author Petr Soukal
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 11/17/2009
 * @since 0.1.0
 */
public class SignalsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final int CH_BOX_PANEL_WIDTH = 150;
	private final int CH_BOXES_WIDTH = 80;
	static final int LABEL_LINE = 1;
	private final int SPINN_VALUE_START = 100, SPINN_VALUE_END = 400,
			SPINN_LOWER_LIMIT = 0, SPINN_UPPER_LIMIT = Integer.MAX_VALUE,
			SPINN_STEP = 1;
	private final int SPINNER_WIDTH = 70;
	private DrawingComponent drawingComponent;
	protected JPanel checkBoxesPanel; // checkBoxy zobrazen�ch sign�l�

	protected JCheckBox[] drawableSignalsCheckBoxes;
	protected JLabel[] drawableSignalsValueLabels;
	private JPanel drawingPanel;
	private JToolBar upperPanel;
	private JToolBar bottomPanel;
	private JPanel centerPanel;
	private JPanel leftPanel;
	protected JButton playBT;
	protected JButton stopBT;
	protected JButton saveEpochIntervalButton;
	protected JScrollBar horizontalScrollBar;
	protected JScrollBar verticalScrollBar;
	protected JSpinner startEpoch;
	protected JSpinner endEpoch;
	private SignalsPanelProvider signalsProvider;
	private Icon playIcon;
	protected JToggleButton selectEpochTB;
	protected JToggleButton unselectEpochTB;
	protected JToggleButton selectArtefactTB;
	protected JToggleButton unselectArtefactTB;
	protected JToggleButton selectPlaybackTB;
	protected JToggleButton baselineCorrectionTB;
	private ButtonGroup buttonGroup;
	private GridBagConstraints centerPanelConstraints;
	private GridBagLayout centerPanelLayout;
	// private int countVisibleSignals;
	protected JButton decreaseNumberOfChannelsButton;
	protected JButton increaseNumberOfChannelsButton;
	protected JButton increaseVerticalZoomButton;
	protected JButton decreaseVerticalZoomButton;
	protected JButton increaseHorizontalZoomButton;
	protected JButton decreaseHorizontalZoomButton;
	// JToggleButton invertedSignalsButton;
	protected OptionPopupMenu optionMenu;

	public SignalsPanel(SignalsPanelProvider signalsProvider) throws JUIGLELangException {
		this.signalsProvider = signalsProvider;
		playIcon = signalsProvider.getPlayIcon();
		optionMenu = new OptionPopupMenu(this.signalsProvider);
		drawingComponent = signalsProvider.getDrawingComponent();
		drawingComponent.setSignalsWindow(this);
		setLayout(new BorderLayout());

		add(createUpperPanel(), BorderLayout.PAGE_START);
		add(createCheckBoxPanel(), BorderLayout.LINE_START);
		add(createCenterPanel(), BorderLayout.CENTER);
		add(createBottomPanel(), BorderLayout.PAGE_END);

		this.setSize(new Dimension(Const.MAIN_WINDOW_WIDTH / 2,
				Const.MAIN_WINDOW_HEIGHT - 90));
		this.setVisible(true);
		selectEpochTB.setEnabled(false);
		unselectEpochTB.setEnabled(false);
		selectArtefactTB.setEnabled(false);
		unselectArtefactTB.setEnabled(false);
		selectPlaybackTB.setEnabled(false);
		baselineCorrectionTB.setEnabled(false);
		// invertedSignalsButton.setEnabled(false);
		decreaseVerticalZoomButton.setEnabled(false);
		increaseVerticalZoomButton.setEnabled(false);
		decreaseHorizontalZoomButton.setEnabled(false);
		increaseHorizontalZoomButton.setEnabled(false);
		setNumberOfSelectedSignalsButtonsEnabled(false, false);
		// repaintVisibleSignals();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				if (drawingComponent != null) {
					repaintVisibleSignals();
				}
			}
		});
	}

	/**
	 * Vytv��� panel s n�stroji pro pr�ci ozna�ov�n� epoch.
	 */
	private JToolBar createUpperPanel() {
		if (upperPanel == null) {
			upperPanel = new JToolBar();

			// upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));
			saveEpochIntervalButton = new JButton("Save epoch configuration");
			saveEpochIntervalButton
					.addActionListener(new FunctionSaveEpochInterval());
			int BUTT_HEIGHT = (int) saveEpochIntervalButton.getPreferredSize()
					.getHeight();

			JLabel startEpochLB = new JLabel("Left border [ms]:");
			startEpochLB.setPreferredSize(new Dimension(startEpochLB
					.getPreferredSize().width, BUTT_HEIGHT));
			startEpochLB.setMinimumSize(new Dimension(
					startEpochLB.getPreferredSize().width, BUTT_HEIGHT));
			startEpochLB.setMaximumSize(new Dimension(
					startEpochLB.getPreferredSize().width, BUTT_HEIGHT));
			JLabel endEpochLB = new JLabel(" Right border [ms]:");
			endEpochLB.setPreferredSize(new Dimension(
					endEpochLB.getPreferredSize().width, BUTT_HEIGHT));
			endEpochLB.setMinimumSize(new Dimension(
					endEpochLB.getPreferredSize().width, BUTT_HEIGHT));
			endEpochLB.setMaximumSize(new Dimension(
					endEpochLB.getPreferredSize().width, BUTT_HEIGHT));
			startEpoch = new JSpinner(new SpinnerNumberModel(SPINN_VALUE_START,
					SPINN_LOWER_LIMIT, SPINN_UPPER_LIMIT, SPINN_STEP));
			startEpoch.setPreferredSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
			startEpoch.setMinimumSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
			startEpoch.setMaximumSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
			endEpoch = new JSpinner(new SpinnerNumberModel(SPINN_VALUE_END,
					SPINN_LOWER_LIMIT, SPINN_UPPER_LIMIT, SPINN_STEP));
			endEpoch.setPreferredSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
			endEpoch.setMinimumSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
			endEpoch.setMaximumSize(new Dimension(SPINNER_WIDTH, BUTT_HEIGHT));
			FunctionSelectedFunction selectFunction = new FunctionSelectedFunction();
			buttonGroup = new ButtonGroup();
			selectEpochTB = new JToggleButton(signalsProvider.getSelectionEpochIcon());
			selectEpochTB.addActionListener(selectFunction);
			selectEpochTB.setToolTipText("Epoch selection");
			selectEpochTB.setActionCommand("" + Const.SELECT_EPOCH);
			unselectEpochTB = new JToggleButton(signalsProvider
					.getUnselectionEpochIcon());
			unselectEpochTB.addActionListener(selectFunction);
			unselectEpochTB.setToolTipText("Epoch unselection");
			unselectEpochTB.setActionCommand("" + Const.UNSELECT_EPOCH);
			selectArtefactTB = new JToggleButton(signalsProvider
					.getSelectionArtefactIcon());
			selectArtefactTB.addActionListener(selectFunction);
			selectArtefactTB.setToolTipText("Artefact selection");
			selectArtefactTB.setActionCommand("" + Const.SELECT_ARTEFACT);
			unselectArtefactTB = new JToggleButton(signalsProvider
					.getUnselectionArtefactIcon());
			unselectArtefactTB.addActionListener(selectFunction);
			unselectArtefactTB.setToolTipText("Artefact unselection");
			unselectArtefactTB.setActionCommand("" + Const.UNSELECT_ARTEFACT);
			selectPlaybackTB = new JToggleButton(signalsProvider.getPlaybackIcon());
			selectPlaybackTB.addActionListener(selectFunction);
			selectPlaybackTB.setToolTipText("Playback");
			selectPlaybackTB.setActionCommand("" + Const.SELECT_PLAYBACK);
			baselineCorrectionTB = new JToggleButton(signalsProvider
					.getBaselineCorrectionIcon());
			baselineCorrectionTB.addActionListener(selectFunction);
			baselineCorrectionTB.setToolTipText("Baseline Correction");
			baselineCorrectionTB.setActionCommand("" + Const.BASELINE_CORRECTION);
			buttonGroup.add(selectPlaybackTB);
			buttonGroup.add(selectEpochTB);
			buttonGroup.add(unselectEpochTB);
			buttonGroup.add(selectArtefactTB);
			buttonGroup.add(unselectArtefactTB);
			buttonGroup.add(baselineCorrectionTB);

			upperPanel.setMargin(new Insets(50, 5, 5, 5));

			upperPanel.add(startEpochLB);
			upperPanel.add(startEpoch);
			upperPanel.add(endEpochLB);
			upperPanel.add(endEpoch);
			upperPanel.add(saveEpochIntervalButton);
			upperPanel.addSeparator();
			upperPanel.add(selectPlaybackTB);
			upperPanel.add(selectEpochTB);
			upperPanel.add(unselectEpochTB);
			upperPanel.add(selectArtefactTB);
			upperPanel.add(unselectArtefactTB);
			upperPanel.add(baselineCorrectionTB);
			upperPanel.addSeparator();

		}

		return upperPanel;
	}

	private JPanel createCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanelLayout = new GridBagLayout();
			centerPanel.setLayout(centerPanelLayout);
			centerPanelConstraints = new GridBagConstraints();

			addComponentToCenterPanel(createCheckBoxPanel(), 0, 0, 1, 1, 0, 1,
					GridBagConstraints.VERTICAL, GridBagConstraints.LINE_START, 20, 0);
			addComponentToCenterPanel(createDrawingPanel(), 1, 0, 1, 1, 1, 1,
					GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0);
			addComponentToCenterPanel(createVerticalScrollBar(), 3, 0, 1, 1, 0, 0,
					GridBagConstraints.VERTICAL, GridBagConstraints.LINE_END, 0, 0);
			addComponentToCenterPanel(createHorizontalScrollBar(), 1, 1, 1, 1, 1, 0,
					GridBagConstraints.HORIZONTAL, GridBagConstraints.PAGE_END, 0, 0);
		}

		return centerPanel;
	}

	private void addComponentToCenterPanel(Component c, int gridx, int gridy,
			int gridwidth, int gridheight, double weightx, double weighty, int fill,
			int anchor, int ipadx, int ipady) {
		centerPanelConstraints.gridx = gridx;
		centerPanelConstraints.gridy = gridy;
		centerPanelConstraints.gridwidth = gridwidth;
		centerPanelConstraints.gridheight = gridheight;
		centerPanelConstraints.weightx = weightx;
		centerPanelConstraints.weighty = weighty;
		centerPanelConstraints.fill = fill;
		centerPanelConstraints.anchor = anchor;
		centerPanelConstraints.ipadx = ipadx;
		centerPanelConstraints.ipady = ipady;
		centerPanelLayout.setConstraints(c, centerPanelConstraints);
		centerPanel.add(c);
	}

	private JToolBar createBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JToolBar();
			// bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

			playBT = new JButton(playIcon);
			playBT.setEnabled(false);
			playBT.addActionListener(new FunctionPlayBT());
			playBT.setPreferredSize(new Dimension(playIcon.getIconWidth(), playIcon
					.getIconHeight()));
			playBT.setMaximumSize(new Dimension(playIcon.getIconWidth(), playIcon
					.getIconHeight()));

			stopBT = new JButton(signalsProvider.getStopIcon());
			stopBT.addActionListener(new FunctionStopBT());
			stopBT.setEnabled(false);
			stopBT.setPreferredSize(new Dimension(playIcon.getIconWidth(), playIcon
					.getIconHeight()));
			stopBT.setMaximumSize(new Dimension(playIcon.getIconWidth(), playIcon
					.getIconHeight()));

			increaseNumberOfChannelsButton = new JButton(JUIGLEGraphicsUtils
					.createImageIcon(JERPAUtils.IMAGE_PATH + "chp.gif"));
			increaseNumberOfChannelsButton.setEnabled(false);
			increaseNumberOfChannelsButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					signalsProvider.increaseNumberOfVisibleChannels();
				}
			});

			decreaseNumberOfChannelsButton = new JButton(JUIGLEGraphicsUtils
					.createImageIcon(JERPAUtils.IMAGE_PATH + "chm.gif"));
			decreaseNumberOfChannelsButton.setEnabled(false);
			decreaseNumberOfChannelsButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					signalsProvider.decreaseNumberOfVisibleChannels();
				}
			});

			decreaseVerticalZoomButton = new JButton(JUIGLEGraphicsUtils
					.createImageIcon(JERPAUtils.IMAGE_PATH + "magvm.png"));
			decreaseVerticalZoomButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					signalsProvider.decreaseVerticalZoom();
				}
			});

			increaseVerticalZoomButton = new JButton(JUIGLEGraphicsUtils
					.createImageIcon(JERPAUtils.IMAGE_PATH + "magvp.png"));
			increaseVerticalZoomButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					signalsProvider.increaseVerticalZoom();
				}
			});

			decreaseHorizontalZoomButton = new JButton(JUIGLEGraphicsUtils
					.createImageIcon(JERPAUtils.IMAGE_PATH + "maghm.png"));
			decreaseHorizontalZoomButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					signalsProvider.decreaseHorizontalZoom();
				}
			});

			increaseHorizontalZoomButton = new JButton(JUIGLEGraphicsUtils
					.createImageIcon(JERPAUtils.IMAGE_PATH + "maghp.png"));
			increaseHorizontalZoomButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					signalsProvider.increaseHorizontalZoom();
				}
			});

			JButton test = new JButton("GC");
			test.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					signalsProvider.calculatingAveragesTest();
				}
			});

			bottomPanel.add(playBT);
			bottomPanel.add(stopBT);
			bottomPanel.addSeparator();
			bottomPanel.add(decreaseNumberOfChannelsButton);
			bottomPanel.add(increaseNumberOfChannelsButton);
			bottomPanel.addSeparator();
			bottomPanel.add(decreaseVerticalZoomButton);
			bottomPanel.add(increaseVerticalZoomButton);
			bottomPanel.addSeparator();
			bottomPanel.add(decreaseHorizontalZoomButton);
			bottomPanel.add(increaseHorizontalZoomButton);

			bottomPanel.setRollover(true);
		}

		return bottomPanel;
	}

	private JPanel createCheckBoxPanel() {
		if (leftPanel == null) {
			leftPanel = new JPanel(new BorderLayout());
			checkBoxesPanel = new JPanel(null);
			checkBoxesPanel.setBackground(Const.SW_COLOR_CHECKBOX_PANEL);
			checkBoxesPanel.setPreferredSize(new Dimension(CH_BOX_PANEL_WIDTH, this
					.getHeight()));
			checkBoxesPanel.setMinimumSize(new Dimension(CH_BOX_PANEL_WIDTH, this
					.getHeight()));
			JPanel buttonsPanel = new JPanel();
			buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
			leftPanel.add(checkBoxesPanel, BorderLayout.CENTER);
			leftPanel.add(buttonsPanel, BorderLayout.SOUTH);
		}
		return leftPanel;
	}

	private JPanel createDrawingPanel() {
		if (drawingPanel == null) {
			drawingPanel = new JPanel();
			drawingPanel.setBackground(Const.SW_COLOR_DC_PANEL);
			drawingPanel.setLayout(new BorderLayout());
			drawingPanel.add(signalsProvider.getDrawingComponent(),
					BorderLayout.CENTER);
			drawingPanel.updateUI();
		}

		return drawingPanel;
	}

	private JScrollBar createVerticalScrollBar() {
		if (verticalScrollBar == null) {
			verticalScrollBar = new JScrollBar(JScrollBar.VERTICAL);
			verticalScrollBar.addAdjustmentListener(new FunctionVerticalScrollBar());
			verticalScrollBar.setMinimum(0);
			verticalScrollBar.setEnabled(false);
		}

		return verticalScrollBar;
	}

	private JScrollBar createHorizontalScrollBar() {
		if (horizontalScrollBar == null) {
			horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
			horizontalScrollBar.addAdjustmentListener(new FunctionScrollBar());
			horizontalScrollBar.setEnabled(false);
		}

		return horizontalScrollBar;
	}

	public void setPlayButtonIcon(Icon icon) {
		playBT.setIcon(icon);
	}

	protected void repaintVisibleSignals() {
		if (drawableSignalsCheckBoxes == null) {
			return;
		}
		checkBoxesPanel.removeAll();
		float paintVolume = checkBoxesPanel.getHeight()
				/ (float) signalsProvider.getNumberOfVisibleChannels();
		int index = signalsProvider.getFirstVisibleChannel();

		int checkBoxShift = (int) (drawableSignalsCheckBoxes[0].getPreferredSize()
				.getHeight() / 2);

		for (int i = 0; i < signalsProvider.getNumberOfVisibleChannels(); i++) {
			drawableSignalsCheckBoxes[index].setBounds(new Rectangle(0, (int) (i
					* paintVolume - checkBoxShift + paintVolume / 2f), CH_BOXES_WIDTH,
					drawableSignalsCheckBoxes[index].getPreferredSize().height));
			drawableSignalsValueLabels[index].setBounds(new Rectangle(CH_BOXES_WIDTH,
					(int) (i * paintVolume - checkBoxShift + paintVolume / 2f),
					CH_BOX_PANEL_WIDTH / 2, drawableSignalsCheckBoxes[index]
							.getPreferredSize().height));

			checkBoxesPanel.add(drawableSignalsCheckBoxes[index]);
			checkBoxesPanel.add(drawableSignalsValueLabels[index]);

			index++;
		}

		repaint();
		validate();
	}

	public void setNumberOfSelectedSignalsButtonsEnabled(
			boolean decreaseButtonEnabled, boolean increaseButtonEnabled) {
		increaseNumberOfChannelsButton.setEnabled(increaseButtonEnabled);
		decreaseNumberOfChannelsButton.setEnabled(decreaseButtonEnabled);
	}

	public int getSelectedFunctionIndex() {

		switch (Integer.valueOf(buttonGroup.getSelection().getActionCommand())
				.intValue()) {
		case Const.SELECT_EPOCH:
			return Const.SELECT_EPOCH;
		case Const.UNSELECT_EPOCH:
			return Const.UNSELECT_EPOCH;
		case Const.SELECT_ARTEFACT:
			return Const.SELECT_ARTEFACT;
		case Const.UNSELECT_ARTEFACT:
			return Const.UNSELECT_ARTEFACT;
		case Const.SELECT_PLAYBACK:
			return Const.SELECT_PLAYBACK;
		case Const.BASELINE_CORRECTION:
			return Const.BASELINE_CORRECTION;
		default:
			return Const.SELECT_NOTHING;
		}
	}

	public void setHorizontalScrollbarValue(int value) {
		horizontalScrollBar.setValue(value);
	}

	/**
	 * Obsluha feedScrollBaru P�i p�esouv�n� v z�znamu pomoc� scrollBaru p�ed�v�
	 * drawingComponente hodnotu scrollBaru.
	 */
	private class FunctionScrollBar implements AdjustmentListener {

		@Override
		public void adjustmentValueChanged(AdjustmentEvent arg0) {
			drawingComponent.setFirstFrame(horizontalScrollBar.getValue());
		}
	}

	/**
	 * Obsluha tla��tka SaveConfiguration Ukl�d� nastavene hodnoty do prom�nn�ch
	 * start- a end- EpochValue a nastav� je v drawingComponente.
	 */
	private class FunctionSaveEpochInterval implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.saveEpochInterval();
		}
	}

	/**
	 * Obsluha toggleButton� v�b�ru funkce Slou�� k v�b�ru funkce, kterou chce
	 * u�ivatel vyu��t.
	 */
	private class FunctionSelectedFunction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (Integer.valueOf(e.getActionCommand()).intValue()) {
			case Const.SELECT_EPOCH:
				signalsProvider.setSelectedFunction(Const.SELECT_EPOCH);
				break;

			case Const.UNSELECT_EPOCH:
				signalsProvider.setSelectedFunction(Const.UNSELECT_EPOCH);
				break;

			case Const.SELECT_ARTEFACT:
				signalsProvider.setSelectedFunction(Const.SELECT_ARTEFACT);
				break;

			case Const.UNSELECT_ARTEFACT:
				signalsProvider.setSelectedFunction(Const.UNSELECT_ARTEFACT);
				break;

			case Const.SELECT_PLAYBACK:
				signalsProvider.setSelectedFunction(Const.SELECT_PLAYBACK);
				break;

			case Const.BASELINE_CORRECTION:
				signalsProvider.setSelectedFunction(Const.BASELINE_CORRECTION);
				break;

			}
		}
	}

	/**
	 * Obsluha tla��tka playBT P�� stisknut� se z�znam spust�, p�i stisknut� v
	 * b��c�m re�imu se data zastav�.
	 */
	private class FunctionPlayBT implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.togglePause();
		}
	}

	/**
	 * Obsluha tla��tka stopBT Slou�� k ukon�en� p�ehr�v�n�. P�esune p�ehr�v�n� na
	 * za��tek.
	 */
	private class FunctionStopBT implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			signalsProvider.stopPlayback();
		}
	}

	private class FunctionVerticalScrollBar implements AdjustmentListener {

		@Override
		public void adjustmentValueChanged(AdjustmentEvent arg0) {
			signalsProvider.setFirstVisibleChannel(verticalScrollBar.getValue());
		}
	}
}
