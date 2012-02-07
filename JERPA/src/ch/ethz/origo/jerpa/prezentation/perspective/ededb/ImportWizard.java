package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.dao.*;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import static javax.swing.BorderFactory.createMatteBorder;

/**
 * GUI frame with tools for import new scenarios, experiments and its files.
 */
public class ImportWizard extends JFrame implements ILanguage {

    private final static Logger log = Logger.getLogger(ImportWizard.class);

    private JLabel weatherNoteLabel, ownerLabel, subjectLabel, startLabel, endLabel, temperatureLabel;
    private TitledBorder filesBorder, groupBorder, scenarioBorder, experimentBorder, weatherBorder;
    protected JRadioButton existingRadio, newRadio;
    protected JComboBox expOwnerCombo, expSubjectCombo, weatherCombo, scenarioCombo, groupCombo, experimentsCombo;
    protected JFormattedTextField expStartTimeField, expEndTimeField, expTemperatureField;
    protected JTextArea weatherNoteArea;
    protected DateFormat timeFormat = new SimpleDateFormat("d.M.yyyy HH:mm:ss");
    protected JButton addWeather, addScenario, addFile, removeFile, addGroup, confirmButton, cancelButton;
    protected JProgressBar progressBar = new JProgressBar();

    protected DataFileDao dataFileDao = DaoFactory.getDataFileDao();
    protected PersonDao personDao = DaoFactory.getPersonDao();
    protected ExperimentDao experimentDao = DaoFactory.getExperimentDao();
    protected WeatherDao weatherDao = DaoFactory.getWeatherDao();
    protected ScenarioDao scenarioDao = DaoFactory.getScenarioDao();
    protected ResearchGroupDao researchGroupDao = DaoFactory.getResearchGroupDao();

    protected ImportFilesTable importTable = new ImportFilesTable();

    private TooltipComboBoxRenderer tooltipComboBoxRenderer = new TooltipComboBoxRenderer();

    private final static int LETTERS_VISIBLE = 15;
    private final static int LINES_VISIBLE = 6;
    private final static Dimension COMBO_SIZE = new Dimension(350, 30);

    private static String resourceBundlePath;
    protected static ResourceBundle resource;

    /**
     * Constructor - creates frame and invokes canvas creating method.
     */
    public ImportWizard() {
        super("Import Wizard");

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.add(createCanvas());
        updateTitles();

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
        existingRadio = new JRadioButton();
        newRadio = new JRadioButton();
        ButtonGroup group = new ButtonGroup();
        group.add(existingRadio);
        group.add(newRadio);

        existingRadio.setSelected(true);
        experimentsCombo = new JComboBox(experimentDao.getAll().toArray());
        experimentsCombo.setPreferredSize(COMBO_SIZE);
        experimentsCombo.setRenderer(tooltipComboBoxRenderer);

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
        chooser.add(experimentsCombo, BorderLayout.LINE_END);

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

        GridBagConstraints progressBarConstraints = new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(progressBar, progressBarConstraints);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);

        GridBagConstraints filesPaneConstraints = new GridBagConstraints(1, 2, 1, 3, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createFilesPane(), filesPaneConstraints);

        confirmButton = new JButton();
        cancelButton = new JButton();

        GridBagConstraints confirmButtonConstraints = new GridBagConstraints(0, 5, 1, 1, 0.2, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints cancelButtonConstraints = new GridBagConstraints(1, 5, 1, 1, 0.2, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

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
        filesBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "New data files", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel filesPane = new JPanel(new GridBagLayout());
        filesPane.setBorder(filesBorder);

        addFile = new JButton();
        removeFile = new JButton();
        importTable.setFillsViewportHeight(true);
        importTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        importTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane importFileScroll = new JScrollPane(importTable);
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
        groupBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Research group", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel groupPane = new JPanel(new GridBagLayout());
        groupPane.setBorder(groupBorder);

        groupCombo = new JComboBox(researchGroupDao.getAll().toArray());
        groupCombo.setRenderer(tooltipComboBoxRenderer);
        groupCombo.setPreferredSize(COMBO_SIZE);
        addGroup = new JButton("+");

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
        scenarioBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Scenario", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel scenarioPane = new JPanel(new GridBagLayout());
        scenarioPane.setBorder(scenarioBorder);

        scenarioCombo = new JComboBox(scenarioDao.getAll().toArray());
        scenarioCombo.setPreferredSize(COMBO_SIZE);
        scenarioCombo.setRenderer(tooltipComboBoxRenderer);
        addScenario = new JButton("+");

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
        weatherBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Weather", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel weatherPane = new JPanel(new GridBagLayout());
        weatherPane.setBorder(weatherBorder);

        weatherNoteLabel = new JLabel();
        addWeather = new JButton("+");

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
        experimentBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Experiment", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel experimentPane = new JPanel(new GridBagLayout());
        experimentPane.setBorder(experimentBorder);

        ownerLabel = new JLabel();
        subjectLabel = new JLabel();
        startLabel = new JLabel();
        endLabel = new JLabel();
        temperatureLabel = new JLabel();

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

    private void updateTitles() {

        existingRadio.setText(resource.getString("importWizard.ededb.existingRadio"));
        newRadio.setText(resource.getString("importWizard.ededb.newRadio"));
        progressBar.setString(resource.getString("importWizard.ededb.progress"));
        confirmButton.setText(resource.getString("importWizard.ededb.ok"));
        cancelButton.setText(resource.getString("importWizard.ededb.cancel"));

        experimentBorder.setTitle(resource.getString("importWizard.border.experiment"));
        groupBorder.setTitle(resource.getString("importWizard.border.group"));
        scenarioBorder.setTitle(resource.getString("importWizard.border.scenario"));
        weatherBorder.setTitle(resource.getString("importWizard.border.weather"));
        filesBorder.setTitle(resource.getString("importWizard.border.files"));

        addFile.setText(resource.getString("importWizard.ededb.addFile"));
        removeFile.setText(resource.getString("importWizard.ededb.removeFile"));

        weatherNoteLabel.setText(resource.getString("importWizard.ededb.label.weatherNote"));

        ownerLabel.setText(resource.getString("importWizard.ededb.label.owner"));
        subjectLabel.setText(resource.getString("importWizard.ededb.label.subject"));
        startLabel.setText(resource.getString("importWizard.ededb.label.startTime"));
        endLabel.setText(resource.getString("importWizard.ededb.label.endTime"));
        temperatureLabel.setText(resource.getString("importWizard.ededb.label.temperature"));

        addWeather.setToolTipText(resource.getString("importWizard.ededb.add.weather"));
        addGroup.setToolTipText(resource.getString("importWizard.ededb.add.group"));
        addScenario.setToolTipText(resource.getString("importWizard.ededb.add.scenario"));

        this.repaint();
    }

    /**
     * Setter of localization resource bundle path
     *
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
        resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     *
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource bundle key.
     *
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
     *
     * @throws ch.ethz.origo.juigle.application.exception.JUIGLELangException
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                updateTitles();
            }
        });

    }

}
