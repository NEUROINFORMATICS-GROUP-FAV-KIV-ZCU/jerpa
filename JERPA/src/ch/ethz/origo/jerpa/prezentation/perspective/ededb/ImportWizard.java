package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.tables.ImportFilesTableModel;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.dao.*;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static javax.swing.BorderFactory.createMatteBorder;

/**
 * GUI frame with tools for import new scenarios, experiments and its files.
 */
public class ImportWizard extends JFrame {

    private final static Logger log = Logger.getLogger(ImportWizard.class);

    protected JRadioButton existingRadio, newRadio;
    protected JComboBox expOwnerCombo, expSubjectCombo, weatherCombo, scenarioCombo, groupCombo;
    protected JFormattedTextField expStartTimeField, expEndTimeField, expTemperatureField;
    protected JTextArea weatherNoteArea;
    protected DateFormat timeFormat = new SimpleDateFormat("d.M.yyyy HH:mm:ss");
    protected JButton addWeather, addScenario, addFile, removeFile, addGroup, confirmButton, cancelButton;

    protected DataFileDao dataFileDao = DaoFactory.getDataFileDao();
    protected PersonDao personDao = DaoFactory.getPersonDao();
    protected ExperimentDao experimentDao = DaoFactory.getExperimentDao();
    protected WeatherDao weatherDao = DaoFactory.getWeatherDao();
    protected ScenarioDao scenarioDao = DaoFactory.getScenarioDao();
    protected ResearchGroupDao researchGroupDao = DaoFactory.getResearchGroupDao();
    protected ImportFilesTableModel importTableModel = new ImportFilesTableModel();

    private TooltipComboBoxRenderer tooltipComboBoxRenderer = new TooltipComboBoxRenderer();

    private final static int LETTERS_VISIBLE = 15;
    private final static int LINES_VISIBLE = 6;
    private final static Dimension COMBO_SIZE = new Dimension(350, 30);

    /**
     * Constructor - creates frame and invokes canvas creating method.
     */
    public ImportWizard() {
        super("Import Wizard");

        this.add(createCanvas());

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.pack();
        this.setMinimumSize(new Dimension(400, 300));
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }

    /**
     * Method for creating canvas. All components are positioned using GridBagLayout.
     *
     * @return filled canvas with all modules and components
     */
    private Component createCanvas() {
        JPanel canvas = new JPanel(new GridBagLayout());

        existingRadio = new JRadioButton("Add to existing experiment");
        newRadio = new JRadioButton("Create new experiment");
        ButtonGroup group = new ButtonGroup();
        group.add(existingRadio);
        group.add(newRadio);

        existingRadio.setSelected(true);
        JComboBox experiments = new JComboBox(experimentDao.getAll().toArray());
        experiments.setPreferredSize(COMBO_SIZE);
        experiments.setRenderer(tooltipComboBoxRenderer);

        JPanel chooser = new JPanel(new BorderLayout());

        try {
            JLabel icon = new JLabel();
            icon.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "database_48.png", 32, 32));
            icon.setFocusable(false);
            icon.setText("Import Wizard");
            Font iconFont = new Font(Font.SANS_SERIF, Font.BOLD, 30);
            icon.setFont(iconFont);
            chooser.add(icon, BorderLayout.BEFORE_FIRST_LINE);
        } catch (PerspectiveException e) {
            log.error(e.getMessage(), e);
        }

        chooser.add(existingRadio, BorderLayout.LINE_START);
        chooser.add(newRadio, BorderLayout.CENTER);
        chooser.add(experiments, BorderLayout.LINE_END);

        GridBagConstraints chooserPaneConstraints = new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(chooser, chooserPaneConstraints);


        GridBagConstraints experimentPaneConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createExperimentPane(), experimentPaneConstraints);

        GridBagConstraints weatherPaneConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createWeatherPane(), weatherPaneConstraints);

        GridBagConstraints scenarioPaneConstraints = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createScenarioPane(), scenarioPaneConstraints);

        GridBagConstraints groupPaneConstraints = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createGroupPane(), groupPaneConstraints);

        GridBagConstraints filesPaneConstraints = new GridBagConstraints(1, 2, 1, 2, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createFilesPane(), filesPaneConstraints);

        confirmButton = new JButton("Confirm and save");
        cancelButton = new JButton("Cancel");

        GridBagConstraints confirmButtonConstraints = new GridBagConstraints(0, 4, 1, 1, 0.2, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints cancelButtonConstraints = new GridBagConstraints(1, 4, 1, 1, 0.2, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        canvas.add(confirmButton, confirmButtonConstraints);
        canvas.add(cancelButton, cancelButtonConstraints);

        return new JScrollPane(canvas);
    }

    /**
     * Creates panel with table of files to be imported and buttons for adding or removing a file from such table.
     *
     * @return filled panel
     */
    private JPanel createFilesPane() {
        Border filesBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "New data files", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel filesPane = new JPanel(new GridBagLayout());
        filesPane.setBorder(filesBorder);

        addFile = new JButton("Add");
        removeFile = new JButton("Remove");
        JTable filesTable = new JTable(importTableModel);
        filesTable.setFillsViewportHeight(true);
        filesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        filesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane importFileScroll = new JScrollPane(filesTable);
        importFileScroll.setPreferredSize(new Dimension(350, 150));

        GridBagConstraints filesTableConstraints = new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints addFileConstraints = new GridBagConstraints(0, 1, 1, 1, 0.5, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints removeFileConstraints = new GridBagConstraints(1, 1, 1, 1, 0.5, 0, GridBagConstraints.LINE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        filesPane.add(importFileScroll, filesTableConstraints);
        filesPane.add(addFile, addFileConstraints);
        filesPane.add(removeFile, removeFileConstraints);

        return filesPane;
    }

    /**
     * Creates panel with research group information.
     *
     * @return filled panel
     */
    private JPanel createGroupPane() {
        Border groupBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Research group", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel groupPane = new JPanel(new GridBagLayout());
        groupPane.setBorder(groupBorder);

        groupCombo = new JComboBox(researchGroupDao.getAll().toArray());
        groupCombo.setRenderer(tooltipComboBoxRenderer);
        groupCombo.setPreferredSize(COMBO_SIZE);
        addGroup = new JButton("+");
        addGroup.setToolTipText("Add new research group");

        GridBagConstraints groupComboConstraints = new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints addGroupConstraints = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        groupPane.add(groupCombo, groupComboConstraints);
        groupPane.add(addGroup, addGroupConstraints);

        return groupPane;
    }

    /**
     * Creates panel with scenario information.
     *
     * @return filled panel
     */
    private JPanel createScenarioPane() {
        Border scenarioBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Scenario", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel scenarioPane = new JPanel(new GridBagLayout());
        scenarioPane.setBorder(scenarioBorder);

        scenarioCombo = new JComboBox(scenarioDao.getAll().toArray());
        scenarioCombo.setPreferredSize(COMBO_SIZE);
        scenarioCombo.setRenderer(tooltipComboBoxRenderer);
        addScenario = new JButton("+");
        addScenario.setToolTipText("Add new scenario");

        GridBagConstraints scenarioComboConstraints = new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints addScenarioConstraints = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        scenarioPane.add(scenarioCombo, scenarioComboConstraints);
        scenarioPane.add(addScenario, addScenarioConstraints);

        return scenarioPane;
    }

    /**
     * Creates panel with weather information.
     *
     * @return filled panel
     */
    private JPanel createWeatherPane() {
        Border weatherBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Weather", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel weatherPane = new JPanel(new GridBagLayout());
        weatherPane.setBorder(weatherBorder);

        JLabel weatherNoteLabel = new JLabel("Note");
        addWeather = new JButton("+");
        addWeather.setToolTipText("Add new weather");

        weatherCombo = new JComboBox(weatherDao.getAll().toArray());
        weatherCombo.setPreferredSize(COMBO_SIZE);
        weatherCombo.setRenderer(tooltipComboBoxRenderer);
        weatherNoteArea = new JTextArea(LINES_VISIBLE, LETTERS_VISIBLE);
        JScrollPane weatherNoteScroll = new JScrollPane(weatherNoteArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints weatherComboConstraints = new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints weatherNoteLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints weatherNoteAreaConstraints = new GridBagConstraints(1, 1, 2, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints addWeatherConstraints = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        weatherPane.add(weatherCombo, weatherComboConstraints);
        weatherPane.add(weatherNoteLabel, weatherNoteLabelConstraints);
        weatherPane.add(weatherNoteScroll, weatherNoteAreaConstraints);
        weatherPane.add(addWeather, addWeatherConstraints);

        return weatherPane;

    }

    /**
     * Creates panel with experiment related information.
     *
     * @return filled panel
     */
    private JPanel createExperimentPane() {
        Border experimentBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Experiment", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel experimentPane = new JPanel(new GridBagLayout());
        experimentPane.setBorder(experimentBorder);

        JLabel ownerLabel = new JLabel("Owner");
        JLabel subjectLabel = new JLabel("Subject");
        JLabel startLabel = new JLabel("Start time");
        JLabel endLabel = new JLabel("End time");
        JLabel temperatureLabel = new JLabel("Temperature");

        try {
            MaskFormatter tempMask = new MaskFormatter("***°C");
            tempMask.setValidCharacters("0123456789 ");
            expTemperatureField = new JFormattedTextField(tempMask);
            expTemperatureField.setColumns(LETTERS_VISIBLE);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }

        expOwnerCombo = new JComboBox(personDao.getAll().toArray());
        expSubjectCombo = new JComboBox(personDao.getAll().toArray());
        expOwnerCombo.setPreferredSize(COMBO_SIZE);
        expSubjectCombo.setPreferredSize(COMBO_SIZE);
        expOwnerCombo.setRenderer(tooltipComboBoxRenderer);
        expSubjectCombo.setRenderer(tooltipComboBoxRenderer);
        expStartTimeField = new JFormattedTextField(timeFormat) {
            @Override
            protected void processFocusEvent(final FocusEvent e) {
                if (e.getID() == FocusEvent.FOCUS_LOST) {
                    if (getText() == null || getText().isEmpty()) {
                        setValue(null);
                    }
                }
                super.processFocusEvent(e);
            }
        };
        expEndTimeField = new JFormattedTextField(timeFormat) {
            @Override
            protected void processFocusEvent(final FocusEvent e) {
                if (e.getID() == FocusEvent.FOCUS_LOST) {
                    if (getText() == null || getText().isEmpty()) {
                        setValue(null);
                    }
                }
                super.processFocusEvent(e);
            }
        };

        expStartTimeField.setToolTipText("d.M.yyyy HH:mm:ss");
        expEndTimeField.setToolTipText("d.M.yyyy HH:mm:ss");
        expStartTimeField.setColumns(LETTERS_VISIBLE);
        expEndTimeField.setColumns(LETTERS_VISIBLE);

        GridBagConstraints ownerLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints ownerComboConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints subjectLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints subjectComboConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints startLabelConstraints = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints startFieldConstraints = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints endLabelConstraints = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints endFieldConstraints = new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints temperatureLabelConstraints = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints temperatureFieldConstraints = new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        experimentPane.add(ownerLabel, ownerLabelConstraints);
        experimentPane.add(expOwnerCombo, ownerComboConstraints);
        experimentPane.add(subjectLabel, subjectLabelConstraints);
        experimentPane.add(expSubjectCombo, subjectComboConstraints);
        experimentPane.add(startLabel, startLabelConstraints);
        experimentPane.add(expStartTimeField, startFieldConstraints);
        experimentPane.add(endLabel, endLabelConstraints);
        experimentPane.add(expEndTimeField, endFieldConstraints);
        experimentPane.add(temperatureLabel, temperatureLabelConstraints);
        experimentPane.add(expTemperatureField, temperatureFieldConstraints);

        return experimentPane;
    }


    /**
     * Class for rendering tooltips over items in Combo boxes, giving the possible overflow.
     */
    private class TooltipComboBoxRenderer extends BasicComboBoxRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                if (-1 < index) {
                    list.setToolTipText(value.toString());
                }
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setFont(list.getFont());
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
}
