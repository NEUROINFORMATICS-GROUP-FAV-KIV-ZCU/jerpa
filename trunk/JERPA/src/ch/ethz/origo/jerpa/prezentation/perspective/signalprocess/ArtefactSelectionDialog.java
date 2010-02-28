package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import noname.JERPAUtils;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.data.Channel;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;

/**
 * Dialog pro nastaven� automatick�ho ozna�ov�n� artefakt�.
 * 
 * @author Petr - Soukal
 * @author Vaclav Souhrada (v dot souhrada at gmail dot com)
 * @version 0.1.2 (2/24/2010)
 * @since 0.1.0 (2/18/2010)
 * @see JDialog
 * 
 */
public class ArtefactSelectionDialog extends JDialog implements ILanguage {

	/** Only for serialization */
	private static final long serialVersionUID = 358489032609275934L;
	private final short MIN_SPINN_VALUE = -100, MIN_SPINN_LOWER_LIMIT = -2000,
			MIN_SPINN_UPPER_LIMIT = 0, MIN_SPINN_STEP = 1;
	private final short MAX_SPINN_VALUE = 100, MAX_SPINN_LOWER_LIMIT = 0,
			MAX_SPINN_UPPER_LIMIT = 2000, MAX_SPINN_STEP = 1;
	private final short STEP_SPINN_VALUE = 100, STEP_SPINN_LOWER_LIMIT = 0,
			STEP_SPINN_UPPER_LIMIT = 2000, STEP_SPINN_STEP = 1;
	private final short DWIDTH = 360, DHEIGHT = 580;
	private final double GRAD_CONST_WIDTH = 3, AMP_CONST_WIDTH = 3,
			GRAD_CONST_HEIGHT = 2.5, AMP_CONST_HEIGHT = 3;
	private final String DESCRIPTION_UNIT = "\u03bcV";
	private final int BORDER_CONST = 5;

	private SignalSessionManager session;
	private ArrayList<String> allChannels;
	private String[] allChannelsArray;

	private JXPanel mainPanel;
	private JCheckBox gradientCritCHB;
	private JCheckBox amplitudeCritCHB;
	private JXButton applyBT;
	private JXButton stornoBT;
	// gradient criterion
	private JSpinner voltageStepSpinner;
	private JXList gradientListUnselectionChannels;
	private JXList gradientListSelectionChannels;
	private JXButton addGradientChannelBT;
	private JXButton removeGradientChannelBT;
	private JCheckBox gradientIndividualChannels;

	// amplitude criterion
	private JSpinner minAmplitudeSpinner;
	private JSpinner maxAmplitudeSpinner;
	private JXList amplitudeListUnselectionChannels;
	private JList amplitudeListSelectionChannels;
	private JXButton addAmplitudeChannelBT;
	private JXButton removeAmplitudeChannelBT;
	private JCheckBox amplitudeIndividualChannels;
	private List<String> allChannelsInAmpUnselectList;
	private List<String> allChannelsInAmpSelectList;
	private List<String> allChannelsInGradUnselectList;
	private List<String> allChannelsInGradSelectList;
	
	private JXLabel voltageStepDescription;
	private JXLabel unselectionLabel;
	private JXLabel selectionLabel;
	private JXLabel minDescription;
  private JXLabel maxDescription;
  private JXLabel warningLabel;

	private ResourceBundle resource;
	private String resourcePath;
	
	private String tab1 = "";
	private String tab2 = "";
	
	private JTabbedPane tabbedPane;
	
	private JXPanel centerPanel;
	private JXPanel northPanel;
	private JXPanel northPanel2;

	/**
	 * Vytv��� objekt t��dy.
	 * 
	 * @param session
	 *          - current session
	 * @throws JUIGLELangException 
	 */
	public ArtefactSelectionDialog(final SignalSessionManager session) throws JUIGLELangException {
		super();
		this.session = session;
		setLocalizedResourceBundle(LangUtils
				.getPerspectiveLangPathProp(LangUtils.SIGNAL_PERSP_LANG_FILE_KEY));
		allChannelsInAmpUnselectList = new ArrayList<String>();
		allChannelsInAmpSelectList = new ArrayList<String>();
		allChannelsInGradUnselectList = new ArrayList<String>();
		allChannelsInGradSelectList = new ArrayList<String>();
		unselectionLabel = new JXLabel();
		selectionLabel = new JXLabel();
		this.add(createInterior());
		updateText();
		this.setSize(new Dimension(DWIDTH, DHEIGHT));
		this.setResizable(false);
		setChannelsName();
		LanguageObservable.getInstance().attach(this);
	}

	/**
	 * Vytv��� hlavn� panel.
	 * 
	 * @return mainPanel.
	 */
	private JXPanel createInterior() {
		mainPanel = new JXPanel(new BorderLayout());
		mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
		mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);

		return mainPanel;
	}

	/**
	 * Vytv��� panel se z�lo�kami.
	 * 
	 * @return centerPanel.
	 */
	private JXPanel createCenterPanel() {
		JXPanel centerPanel = new JXPanel(new BorderLayout());
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(" ", createGradientCriterionPanel());
		tabbedPane.addTab(" ", createAmplitudeCriterionPanel());

		centerPanel.add(tabbedPane);

		return centerPanel;
	}

	/**
	 * Vytvr�n� panel do z�lo�ky pro gradientn� krit�rium.
	 * 
	 * @return gradientCriterionP.
	 */
	private JXPanel createGradientCriterionPanel() {
		JXPanel gradientCriterionP = new JXPanel(new BorderLayout());

		gradientCritCHB = new JCheckBox();
		gradientCritCHB.addActionListener(new FunctionCriterionCheckBoxes());

		voltageStepDescription = new JXLabel();
		voltageStepSpinner = new JSpinner(new SpinnerNumberModel(STEP_SPINN_VALUE,
				STEP_SPINN_LOWER_LIMIT, STEP_SPINN_UPPER_LIMIT, STEP_SPINN_STEP));

		JXPanel northPanel = new JXPanel(new BorderLayout());
		northPanel.add(gradientCritCHB, BorderLayout.NORTH);
		northPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);

		JXPanel centerPanel = new JXPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

		JXPanel voltStepDescriptPanel = new JXPanel();
		voltStepDescriptPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		voltStepDescriptPanel.add(voltageStepDescription);
		voltStepDescriptPanel.setMaximumSize(new Dimension(voltStepDescriptPanel
				.getPreferredSize().width,
				voltStepDescriptPanel.getPreferredSize().height));

		centerPanel.add(voltStepDescriptPanel);
		JPanel voltageStepPanel = new JPanel();
		voltageStepPanel.add(voltageStepSpinner);
		voltageStepPanel.add(new JLabel(DESCRIPTION_UNIT));
		voltageStepPanel.setMaximumSize(new Dimension(voltageStepPanel
				.getPreferredSize().width, voltageStepPanel.getPreferredSize().height));
		voltageStepPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		centerPanel.add(voltageStepPanel);

		gradientCriterionP.add(northPanel, BorderLayout.NORTH);
		gradientCriterionP.add(centerPanel, BorderLayout.CENTER);
		gradientCriterionP.add(createGradientList(), BorderLayout.SOUTH);

		return gradientCriterionP;
	}

	/**
	 * Vytv��� panel se seznamy kan�l� pro m�d jednotliv�ch kan�l� gradientn�ho
	 * krit�ria.
	 * 
	 * @return gradientListP.
	 */
	private JXPanel createGradientList() {
		JXPanel gradientListP = new JXPanel(new BorderLayout());

		gradientListUnselectionChannels = new JXList();
		gradientListUnselectionChannels
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		gradientListUnselectionChannels.setLayoutOrientation(JList.VERTICAL);
		gradientListUnselectionChannels.setVisibleRowCount(-1);

		JScrollPane listUnselChannelsScroller = new JScrollPane(
				gradientListUnselectionChannels);
		listUnselChannelsScroller
				.setPreferredSize(new Dimension((int) (DWIDTH / GRAD_CONST_WIDTH),
						(int) (DHEIGHT / GRAD_CONST_HEIGHT)));

		gradientListSelectionChannels = new JXList();
		gradientListSelectionChannels
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		gradientListSelectionChannels.setLayoutOrientation(JList.VERTICAL);
		gradientListSelectionChannels.setVisibleRowCount(-1);

		JScrollPane listSelChannelsScroller = new JScrollPane(
				gradientListSelectionChannels);
		listSelChannelsScroller
				.setPreferredSize(new Dimension((int) (DWIDTH / GRAD_CONST_WIDTH),
						(int) (DHEIGHT / GRAD_CONST_HEIGHT)));

		removeGradientChannelBT = new JXButton(">>");
		removeGradientChannelBT.setAlignmentX(Component.CENTER_ALIGNMENT);
		removeGradientChannelBT.addActionListener(new FunctionAddGradChannel());
		addGradientChannelBT = new JXButton("<<");
		addGradientChannelBT.setAlignmentX(Component.CENTER_ALIGNMENT);
		addGradientChannelBT.addActionListener(new FunctionRemoveGradChannel());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		JLabel space = new JLabel();
		space.setMaximumSize(new Dimension(
				addGradientChannelBT.getPreferredSize().width,
				(int) (DHEIGHT / GRAD_CONST_HEIGHT) / 3));
		space.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.add(space);
		buttonPanel.add(removeGradientChannelBT);
		buttonPanel.add(addGradientChannelBT);

		gradientIndividualChannels = new JCheckBox();
		gradientIndividualChannels
				.addActionListener(new FunctionGradIndividualChannels());

		northPanel2 = new JXPanel(new BorderLayout());

		gradientListP.add(gradientIndividualChannels, BorderLayout.NORTH);
		JPanel unselectionPanel = new JPanel(new BorderLayout());
		unselectionPanel.add(unselectionLabel,
				BorderLayout.NORTH);
		unselectionPanel.add(listUnselChannelsScroller, BorderLayout.SOUTH);

		JPanel selectionPanel = new JPanel(new BorderLayout());
		selectionPanel.add(selectionLabel, BorderLayout.NORTH);
		selectionPanel.add(listSelChannelsScroller, BorderLayout.SOUTH);

		northPanel2.add(selectionPanel, BorderLayout.WEST);
		northPanel2.add(unselectionPanel, BorderLayout.EAST);
		northPanel2.add(buttonPanel, BorderLayout.CENTER);
		gradientListP.add(northPanel2);
		setGradIndividualChannelsEnabled(false);

		return gradientListP;
	}
	
	/**
	 * Nastavuje viditelnost dialogu a jeho um�st�n� na monitoru.
	 */
	public void setActualLocationAndVisibility() {
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	/**
	 * Vytvr�n� panel do z�lo�ky pro amplitudov� krit�rium.
	 * 
	 * @return amplitudeCriterionP.
	 */
	private JPanel createAmplitudeCriterionPanel() {
		JPanel amplitudeCriterionP = new JPanel(new BorderLayout());

		amplitudeCritCHB = new JCheckBox();
		amplitudeCritCHB.addActionListener(new FunctionCriterionCheckBoxes());

		minDescription = new JXLabel();
		minAmplitudeSpinner = new JSpinner(new SpinnerNumberModel(MIN_SPINN_VALUE,
				MIN_SPINN_LOWER_LIMIT, MIN_SPINN_UPPER_LIMIT, MIN_SPINN_STEP));
		maxDescription = new JXLabel();
		maxAmplitudeSpinner = new JSpinner(new SpinnerNumberModel(MAX_SPINN_VALUE,
				MAX_SPINN_LOWER_LIMIT, MAX_SPINN_UPPER_LIMIT, MAX_SPINN_STEP));
		maxAmplitudeSpinner.setPreferredSize(new Dimension(minAmplitudeSpinner
				.getPreferredSize().width,
				minAmplitudeSpinner.getPreferredSize().height));

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(amplitudeCritCHB, BorderLayout.NORTH);
		northPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);

		centerPanel = new JXPanel(new BorderLayout());
		JPanel valuesPanel = new JPanel();
		valuesPanel.setLayout(new BoxLayout(valuesPanel, BoxLayout.PAGE_AXIS));
		JPanel minDescriptPanel = new JPanel();
		minDescriptPanel.add(minDescription);
		minDescriptPanel.setMaximumSize(new Dimension(minDescriptPanel
				.getPreferredSize().width, minDescriptPanel.getPreferredSize().height));
		minDescriptPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		valuesPanel.add(minDescriptPanel);
		JPanel minAmpPanel = new JPanel();
		minAmpPanel.add(minAmplitudeSpinner);
		minAmpPanel.add(new JLabel(DESCRIPTION_UNIT));
		minAmpPanel.setMaximumSize(new Dimension(
				minAmpPanel.getPreferredSize().width,
				minAmpPanel.getPreferredSize().height));
		minAmpPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		valuesPanel.add(minAmpPanel);
		JPanel maxDescriptPanel = new JPanel();
		maxDescriptPanel.add(maxDescription);
		maxDescriptPanel.setMaximumSize(new Dimension(maxDescriptPanel
				.getPreferredSize().width, maxDescriptPanel.getPreferredSize().height));
		maxDescriptPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		valuesPanel.add(maxDescriptPanel);
		JPanel maxAmpPanel = new JPanel();
		maxAmpPanel.add(maxAmplitudeSpinner);
		maxAmpPanel.add(new JLabel(DESCRIPTION_UNIT));
		maxAmpPanel.setMaximumSize(new Dimension(
				maxAmpPanel.getPreferredSize().width,
				maxAmpPanel.getPreferredSize().height));
		maxAmpPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		valuesPanel.add(maxAmpPanel);
		JPanel warningPanel = new JPanel(new BorderLayout());
		warningPanel.add(new JLabel(JUIGLEGraphicsUtils
				.createImageIcon(JERPAUtils.IMAGE_PATH + "warning.gif")),
				BorderLayout.NORTH);
		warningLabel = new JXLabel();
		warningLabel.setHorizontalAlignment(JLabel.CENTER);
		warningPanel
				.add(warningLabel, BorderLayout.SOUTH);
		JPanel extensiblePanel = new JPanel();
		extensiblePanel.add(warningPanel);

		centerPanel.add(valuesPanel, BorderLayout.WEST);
		centerPanel.add(extensiblePanel, BorderLayout.CENTER);

		amplitudeCriterionP.add(northPanel, BorderLayout.NORTH);
		amplitudeCriterionP.add(centerPanel, BorderLayout.CENTER);
		amplitudeCriterionP.add(createAmplitudeList(), BorderLayout.SOUTH);

		return amplitudeCriterionP;
	}

	/**
	 * Vytv��� panel se seznamy kan�l� pro m�d jednotliv�ch kan�l� amplitudov�ho
	 * krit�ria.
	 * 
	 * @return amplitudeListP.
	 */
	private JPanel createAmplitudeList() {
		JPanel amplitudeListP = new JPanel(new BorderLayout());

		amplitudeListUnselectionChannels = new JXList();
		amplitudeListUnselectionChannels
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		amplitudeListUnselectionChannels.setLayoutOrientation(JList.VERTICAL);
		amplitudeListUnselectionChannels.setVisibleRowCount(-1);

		JScrollPane listUnselChannelsScroller = new JScrollPane(
				amplitudeListUnselectionChannels);
		listUnselChannelsScroller.setPreferredSize(new Dimension(
				(int) (DWIDTH / AMP_CONST_WIDTH), (int) (DHEIGHT / AMP_CONST_HEIGHT)));

		amplitudeListSelectionChannels = new JXList();
		amplitudeListSelectionChannels
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		amplitudeListSelectionChannels.setLayoutOrientation(JList.VERTICAL);
		amplitudeListSelectionChannels.setVisibleRowCount(-1);

		JScrollPane listSelChannelsScroller = new JScrollPane(
				amplitudeListSelectionChannels);
		listSelChannelsScroller.setPreferredSize(new Dimension(
				(int) (DWIDTH / AMP_CONST_WIDTH), (int) (DHEIGHT / AMP_CONST_HEIGHT)));

		removeAmplitudeChannelBT = new JXButton(">>");
		removeAmplitudeChannelBT.setAlignmentX(Component.CENTER_ALIGNMENT);
		removeAmplitudeChannelBT.addActionListener(new FunctionAddAmpChannel());
		addAmplitudeChannelBT = new JXButton("<<");
		addAmplitudeChannelBT.setAlignmentX(Component.CENTER_ALIGNMENT);
		addAmplitudeChannelBT.addActionListener(new FunctionRemoveAmpChannel());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		JLabel space = new JLabel();
		space.setMaximumSize(new Dimension(
				addAmplitudeChannelBT.getPreferredSize().width,
				(int) (DHEIGHT / AMP_CONST_HEIGHT) / 3));
		space.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.add(space);
		buttonPanel.add(removeAmplitudeChannelBT);
		buttonPanel.add(addAmplitudeChannelBT);
		amplitudeIndividualChannels = new JCheckBox();
		amplitudeIndividualChannels
				.addActionListener(new FunctionAmpIndividualChannels());

		amplitudeListP.add(amplitudeIndividualChannels, BorderLayout.NORTH);

		northPanel = new JXPanel(new BorderLayout());

		JPanel unselectionPanel = new JXPanel(new BorderLayout());
		unselectionPanel.add(unselectionLabel,
				BorderLayout.NORTH);
		unselectionPanel.add(listUnselChannelsScroller, BorderLayout.SOUTH);

		JPanel selectionPanel = new JXPanel(new BorderLayout());
		selectionPanel.add(selectionLabel, BorderLayout.NORTH);
		selectionPanel.add(listSelChannelsScroller, BorderLayout.SOUTH);

		northPanel.add(selectionPanel, BorderLayout.WEST);
		northPanel.add(unselectionPanel, BorderLayout.EAST);
		northPanel.add(buttonPanel, BorderLayout.CENTER);
		amplitudeListP.add(northPanel, BorderLayout.CENTER);
		setAmpIndividualChannelsEnabled(false);

		return amplitudeListP;
	}

	/**
	 * Vytv��� panel s tla��tky.
	 * 
	 * @return southPanel.
	 */
	private JPanel createSouthPanel() {
		JPanel southPanel = new JXPanel();
		applyBT = new JXButton();
		applyBT.setEnabled(false);
		applyBT.addActionListener(new FunctionApplyBT());
		stornoBT = new JXButton();
		stornoBT.addActionListener(new FunctionStornoBT());

		southPanel.add(applyBT);
		southPanel.add(stornoBT);

		return southPanel;
	}

	/**
	 * Nastavuje povolen� tla��tek a seznam� s kan�ly modu jednotliv�ch kan�l�
	 * gradientn�ho krit�ria.
	 */
	private void setGradIndividualChannelsEnabled(boolean enabled) {
		gradientListUnselectionChannels.setEnabled(enabled);
		gradientListSelectionChannels.setEnabled(enabled);

		if (enabled) {
			if (allChannelsInGradSelectList.size() > 0) {
				removeGradientChannelBT.setEnabled(enabled);
			}

			if (allChannelsInGradUnselectList.size() > 0) {
				addGradientChannelBT.setEnabled(enabled);
			}
		} else {
			removeGradientChannelBT.setEnabled(enabled);
			addGradientChannelBT.setEnabled(enabled);
		}
	}

	/**
	 * Nastavuje povolen� tla��tek a seznam� s kan�ly modu jednotliv�ch kan�l�
	 * amplitudov�ho krit�ria.
	 */
	private void setAmpIndividualChannelsEnabled(boolean enabled) {
		amplitudeListUnselectionChannels.setEnabled(enabled);
		amplitudeListSelectionChannels.setEnabled(enabled);

		if (enabled) {
			if (allChannelsInAmpSelectList.size() > 0) {
				removeAmplitudeChannelBT.setEnabled(enabled);
			}

			if (allChannelsInAmpUnselectList.size() > 0) {
				addAmplitudeChannelBT.setEnabled(enabled);
			}
		} else {
			removeAmplitudeChannelBT.setEnabled(enabled);
			addAmplitudeChannelBT.setEnabled(enabled);
		}
	}

	/**
	 * Nastavuje viditelnost dialogu a jeho um�st�n� na monitoru.
	 */
	/*public void setActualLocationAndVisibility() {
		mainWindow.setEnabled(false);
		this.setLocationRelativeTo(mainWindow);
		this.setVisible(true);
	}*/

	/**
	 * Nastavuje jm�na v�ech kan�l� v souboru a vkl�d� je do seznamu pro
	 * kontrolovan� kan�ly.
	 */
	public void setChannelsName() {
		List<Channel> channels = session.getCurrentProject().getHeader()
				.getChannels();

		allChannels = new ArrayList<String>();

		for (Channel channel : channels) {
			allChannels.add(channel.getName());
		}

		allChannelsArray = allChannels.toArray(new String[allChannels.size()]);

		gradientListSelectionChannels.setListData(allChannelsArray);
		amplitudeListSelectionChannels.setListData(allChannelsArray);

		allChannelsInGradSelectList = new ArrayList<String>(allChannels);
		allChannelsInAmpSelectList = new ArrayList<String>(allChannels);

	}

	/**
	 * Obsluhuje checkBox modu jednotliv�ch kan�l� pro gradientn� krit�rium.
	 */
	private class FunctionGradIndividualChannels implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (gradientIndividualChannels.isSelected()) {
				setGradIndividualChannelsEnabled(true);
			} else {
				setGradIndividualChannelsEnabled(false);
			}
		}
	}

	/**
	 * Obsluhuje tla��tko pro p�id�n� kan�lu ze seznamu kontrolovan�ch kan�l� do
	 * seznamu nekontrolovan�ch kan�l� gradientn�ho krit�ria.
	 */
	private class FunctionAddGradChannel implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<String> selectChannels = new ArrayList<String>();

			for (int i = 0; i < gradientListSelectionChannels.getSelectedValues().length; i++) {
				selectChannels.add((String) gradientListSelectionChannels
						.getSelectedValues()[i]);
			}

			for (int i = 0; i < allChannelsInGradUnselectList.size(); i++) {
				selectChannels.add(allChannelsInGradUnselectList.get(i));
			}

			allChannelsInGradUnselectList = new ArrayList<String>(allChannels);
			allChannelsInGradSelectList = new ArrayList<String>(allChannels);

			allChannelsInGradUnselectList.retainAll(selectChannels);
			allChannelsInGradSelectList.removeAll(selectChannels);

			gradientListUnselectionChannels.setListData(allChannelsInGradUnselectList
					.toArray(new String[allChannelsInGradUnselectList.size()]));
			gradientListSelectionChannels.setListData(allChannelsInGradSelectList
					.toArray(new String[allChannelsInGradSelectList.size()]));

			if (allChannelsInGradSelectList.size() == 0) {
				removeGradientChannelBT.setEnabled(false);
			}

			if (allChannelsInGradUnselectList.size() != 0) {
				addGradientChannelBT.setEnabled(true);
			}

		}
	}

	/**
	 * Obsluhuje tla��tko pro p�id�n� kan�lu ze seznamu nekontrolovan�ch kan�l� do
	 * seznamu kontrolovan�ch kan�l� gradientn�ho krit�ria.
	 */
	private class FunctionRemoveGradChannel implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<String> selectChannels = new ArrayList<String>();

			for (int i = 0; i < gradientListUnselectionChannels.getSelectedValues().length; i++) {
				selectChannels.add((String) gradientListUnselectionChannels
						.getSelectedValues()[i]);
			}

			for (int i = 0; i < allChannelsInGradSelectList.size(); i++) {
				selectChannels.add(allChannelsInGradSelectList.get(i));
			}

			allChannelsInGradUnselectList = new ArrayList<String>(allChannels);
			allChannelsInGradSelectList = new ArrayList<String>(allChannels);

			allChannelsInGradUnselectList.removeAll(selectChannels);
			allChannelsInGradSelectList.retainAll(selectChannels);

			gradientListUnselectionChannels.setListData(allChannelsInGradUnselectList
					.toArray(new String[allChannelsInGradUnselectList.size()]));
			gradientListSelectionChannels.setListData(allChannelsInGradSelectList
					.toArray(new String[allChannelsInGradSelectList.size()]));

			if (allChannelsInGradSelectList.size() != 0) {
				removeGradientChannelBT.setEnabled(true);
			}

			if (allChannelsInGradUnselectList.size() == 0) {
				addGradientChannelBT.setEnabled(false);
			}
		}
	}

	/**
	 * Obsluhuje checkBox modu jednotliv�ch kan�l� pro amplitudov� krit�rium.
	 */
	private class FunctionAmpIndividualChannels implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (amplitudeIndividualChannels.isSelected()) {
				setAmpIndividualChannelsEnabled(true);
			} else {
				setAmpIndividualChannelsEnabled(false);
			}
		}
	}

	/**
	 * Obsluhuje tla��tko pro p�id�n� kan�lu ze seznamu kontrolovan�ch kan�l� do
	 * seznamu nekontrolovan�ch kan�l� amplitudov�ho krit�ria.
	 */
	private class FunctionAddAmpChannel implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<String> selectChannels = new ArrayList<String>();

			for (int i = 0; i < amplitudeListSelectionChannels.getSelectedValues().length; i++) {
				selectChannels.add((String) amplitudeListSelectionChannels
						.getSelectedValues()[i]);
			}

			for (int i = 0; i < allChannelsInAmpUnselectList.size(); i++) {
				selectChannels.add(allChannelsInAmpUnselectList.get(i));
			}

			allChannelsInAmpUnselectList = new ArrayList<String>(allChannels);
			allChannelsInAmpSelectList = new ArrayList<String>(allChannels);

			allChannelsInAmpUnselectList.retainAll(selectChannels);
			allChannelsInAmpSelectList.removeAll(selectChannels);

			amplitudeListUnselectionChannels.setListData(allChannelsInAmpUnselectList
					.toArray(new String[allChannelsInAmpUnselectList.size()]));
			amplitudeListSelectionChannels.setListData(allChannelsInAmpSelectList
					.toArray(new String[allChannelsInAmpSelectList.size()]));

			if (allChannelsInAmpSelectList.size() == 0) {
				removeAmplitudeChannelBT.setEnabled(false);
			}

			if (allChannelsInAmpUnselectList.size() != 0) {
				addAmplitudeChannelBT.setEnabled(true);
			}
		}
	}

	/**
	 * Obsluhuje tla��tko pro p�id�n� kan�lu ze seznamu nekontrolovan�ch kan�l� do
	 * seznamu kontrolovan�ch kan�l� amplitudov�ho krit�ria.
	 */
	private class FunctionRemoveAmpChannel implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<String> selectChannels = new ArrayList<String>();

			for (int i = 0; i < amplitudeListUnselectionChannels.getSelectedValues().length; i++) {
				selectChannels.add((String) amplitudeListUnselectionChannels
						.getSelectedValues()[i]);
			}

			for (int i = 0; i < allChannelsInAmpSelectList.size(); i++) {
				selectChannels.add(allChannelsInAmpSelectList.get(i));
			}

			allChannelsInAmpUnselectList = new ArrayList<String>(allChannels);
			allChannelsInAmpSelectList = new ArrayList<String>(allChannels);

			allChannelsInAmpUnselectList.removeAll(selectChannels);
			allChannelsInAmpSelectList.retainAll(selectChannels);

			amplitudeListUnselectionChannels.setListData(allChannelsInAmpUnselectList
					.toArray(new String[allChannelsInAmpUnselectList.size()]));
			amplitudeListSelectionChannels.setListData(allChannelsInAmpSelectList
					.toArray(new String[allChannelsInAmpSelectList.size()]));

			if (allChannelsInAmpSelectList.size() != 0) {
				removeAmplitudeChannelBT.setEnabled(true);
			}

			if (allChannelsInAmpUnselectList.size() == 0) {
				addAmplitudeChannelBT.setEnabled(false);
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

			if (gradientCritCHB.isSelected()) {
				Integer voltageStepValue = (Integer) voltageStepSpinner.getValue();

				if (gradientIndividualChannels.isSelected()
						&& allChannelsInGradSelectList.size() != 0) {
					int[] indicesSelectSignals = new int[allChannelsInGradSelectList
							.size()];

					for (int i = 0; i < indicesSelectSignals.length; i++) {
						indicesSelectSignals[i] = allChannels
								.indexOf(allChannelsInGradSelectList.get(i));
					}

					session.playAutomaticGradientArtefactSelection(voltageStepValue
							.intValue(), indicesSelectSignals);

				} else {
					session.playAutomaticGradientArtefactSelection(voltageStepValue
							.intValue(), null);
				}
			}

			if (amplitudeCritCHB.isSelected()) {
				Integer minAmplitudeValue = (Integer) minAmplitudeSpinner.getValue();
				Integer maxAmplitudeValue = (Integer) maxAmplitudeSpinner.getValue();

				if (amplitudeIndividualChannels.isSelected()
						&& allChannelsInAmpSelectList.size() != 0) {
					int[] indicesSelectSignals = new int[allChannelsInAmpSelectList
							.size()];

					for (int i = 0; i < indicesSelectSignals.length; i++) {
						indicesSelectSignals[i] = allChannels
								.indexOf(allChannelsInAmpSelectList.get(i));
					}

					session.playAutomaticAmplitudeArtefactSelection(minAmplitudeValue
							.intValue(), maxAmplitudeValue.intValue(), indicesSelectSignals);

				} else {
					session.playAutomaticAmplitudeArtefactSelection(minAmplitudeValue
							.intValue(), maxAmplitudeValue.intValue(), null);
				}
			}
			session.sendArtefactSelectionMesage();
			ArtefactSelectionDialog.this.setVisible(false);
		}
	}

	/**
	 * Obsluhuje checkBoxy jednotliv�ch krit�rii a podle ozna�en� jednoho z
	 * krit�rii povoluje tla��tko applyBT.
	 */
	private class FunctionCriterionCheckBoxes implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (gradientCritCHB.isSelected() || amplitudeCritCHB.isSelected()) {
				applyBT.setEnabled(true);
			} else {
				applyBT.setEnabled(false);
			}
		}
	}

	/**
	 * Obsluhuje tla��tko pro stornov�n� akce a zav�en� dialogu.
	 */
	private class FunctionStornoBT implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ArtefactSelectionDialog.this.setVisible(false);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateText() throws JUIGLELangException {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setLocalizedResourceBundle(getResourceBundlePath());
				String channelMode = resource.getString("diag.artef.select.mode.indiv.channels");
				setTitle(resource.getString("diag.artef.select.title"));
				tabbedPane.setTitleAt(0, resource.getString("diag.artef.select.grad"));
				tabbedPane.setTitleAt(1, resource.getString("diag.artef.select.amplit"));
				gradientCritCHB.setText(resource.getString("diag.artef.select.grad.check"));
				voltageStepDescription.setText(resource.getString("diag.artef.select.mavssp"));
				gradientIndividualChannels.setText(channelMode);
				unselectionLabel.setText(resource.getString("diag.artef.select.unselection"));
				selectionLabel.setText(resource.getString("diag.artef.select.selection"));
				amplitudeCritCHB.setText(resource.getString("diag.artef.select.amplitude.check"));
				minDescription.setText(resource.getString("diag.artef.select.amplitude.min"));
				maxDescription.setText(resource.getString("diag.artef.select.amplitude.max"));
				warningLabel.setText(resource.getString("diag.artef.select.warning.label"));
				amplitudeIndividualChannels.setText(channelMode);
				applyBT.setText(resource.getString("diag.artef.select.butt.apply"));
				stornoBT.setText(resource.getString("diag.artef.select.butt.storno"));
				centerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
						.createTitledBorder(resource.getString("diag.artef.select.grad.value")), BorderFactory
						.createEmptyBorder(BORDER_CONST, BORDER_CONST, BORDER_CONST,
								BORDER_CONST)));
				northPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
						.createTitledBorder(channelMode), BorderFactory
						.createEmptyBorder(BORDER_CONST, BORDER_CONST, BORDER_CONST,
								BORDER_CONST)));
				northPanel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory
						.createTitledBorder(channelMode), BorderFactory
						.createEmptyBorder(BORDER_CONST, BORDER_CONST, BORDER_CONST,
								BORDER_CONST)));
			}
		});
	}
	
}
