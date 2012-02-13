package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.dao.ExperimentDao;
import ch.ethz.origo.jerpa.data.tier.pojo.*;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import static javax.swing.BorderFactory.*;

/**
 * @author Petr Miko
 *         <p/>
 *         Viewer of experiment information.
 */
public class ExperimentOverview extends JDialog implements ActionListener, ILanguage {

    private static String resourceBundlePath;
    private static ResourceBundle resource;

    private JButton closeButton;
    private TitledBorder metaBorder, generalBorder, timeBorder, peopleBorder, hardwareBorder;
    private JLabel ownerLabel, subjectLabel, startLabel, endLabel, idLabel, nameLabel, tempLabel,
            weatherLabel, weatherNoteLabel, hwTitleLabel, hwTypeLabel, hwDescriptionLabel;

    private Experiment experiment;
    private Scenario scenario;

//    private final static Logger log = Logger.getLogger(ExperimentOverview.class);

    private final int LETTERS_VISIBLE = 20;
    private final int LINES_VISIBLE = 8;

    /**
     * Constructor. Creates the view in accordance to provided experiment id.
     *
     * @param experimentId experiment identifier
     */
    public ExperimentOverview(int experimentId) {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ExperimentDao experimentDao = DaoFactory.getExperimentDao();
        experiment = experimentDao.get(experimentId);
        scenario = experiment.getScenario();
        HibernateUtil.rebind(experiment);
        HibernateUtil.rebind(scenario);

        this.setTitle(experiment.getExperimentId() + " - " + scenario.getTitle());

        JPanel canvas = new JPanel();
        initViewer(canvas);

        updateTitles();

        this.add(canvas);
        this.pack();
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Fills the canvas with panels to be visualized.
     *
     * @param canvas panel for display purposes
     */
    private void initViewer(JPanel canvas) {
        canvas.setLayout(new GridBagLayout());

        GridBagConstraints generalPaneConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createGeneralPane(), generalPaneConstraints);

        GridBagConstraints peoplePaneConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createPeoplePane(), peoplePaneConstraints);

        GridBagConstraints metaPaneConstraints = new GridBagConstraints(0, 1, 1, 2, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createMetaPane(), metaPaneConstraints);

        GridBagConstraints timePaneConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createTimePane(), timePaneConstraints);

        GridBagConstraints hwPaneConstraints = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
        canvas.add(createHwPane(), hwPaneConstraints);

        closeButton = new JButton();
        closeButton.addActionListener(this);
        closeButton.setActionCommand("close");

        GridBagConstraints closeButtonConstraints = new GridBagConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0);
        canvas.add(closeButton, closeButtonConstraints);
    }

    /**
     * Creates panel with HW information.
     * @return panel with HW
     */
    private JPanel createHwPane() {
        hardwareBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Hardware", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel hwPane = new JPanel(new GridBagLayout());
        hwPane.setBorder(hardwareBorder);

        hwTitleLabel = new JLabel();
        hwTypeLabel = new JLabel();
        hwDescriptionLabel = new JLabel();

        int row = 0;
        for(Hardware hw : experiment.getHardwares()){
            HibernateUtil.rebind(hw);

            JTextField hwTitleField = new JTextField(hw.getTitle());
            JTextField hwTypeField = new JTextField(hw.getType());
            JTextField hwDescriptionField = new JTextField(hw.getDescription());

            hwTitleField.setEditable(false);
            hwTypeField.setEditable(false);
            hwDescriptionField.setEditable(false);

            hwTitleField.setColumns(LETTERS_VISIBLE);
            hwTypeField.setColumns(LETTERS_VISIBLE);
            hwDescriptionField.setColumns(LETTERS_VISIBLE);


            GridBagConstraints hwTitleLabelConstraints = new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
            GridBagConstraints hwTitleFieldConstraints = new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
            row++;
            GridBagConstraints hwTypeLabelConstraints = new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
            GridBagConstraints hwTypeFieldConstraints = new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
            row++;
            GridBagConstraints hwDescriptionLabelConstraints = new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
            GridBagConstraints hwDescriptionFieldConstraints = new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

            hwPane.add(hwTitleLabel, hwTitleLabelConstraints);
            hwPane.add(hwTitleField, hwTitleFieldConstraints);
            hwPane.add(hwTypeLabel, hwTypeLabelConstraints);
            hwPane.add(hwTypeField, hwTypeFieldConstraints);
            hwPane.add(hwDescriptionLabel, hwDescriptionLabelConstraints);
            hwPane.add(hwDescriptionField, hwDescriptionFieldConstraints);
        }
        return hwPane;
    }


    /**
     * Creates panel with experiment meta information.
     *
     * @return panel with meta information of experiments'
     */
    private JPanel createMetaPane() {
        metaBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Meta information", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel metaPane = new JPanel(new GridBagLayout());
        metaPane.setBorder(metaBorder);

        Weather weather = experiment.getWeather();
        HibernateUtil.rebind(weather);

        tempLabel = new JLabel();
        weatherLabel = new JLabel();
        weatherNoteLabel = new JLabel();

        JTextField tempField = new JTextField(experiment.getTemperature() + " °C", LETTERS_VISIBLE);
        JTextField weatherField = new JTextField(weather.getTitle(), LETTERS_VISIBLE);
        JTextArea weatherNoteArea = new JTextArea(experiment.getWeathernote(), LINES_VISIBLE, LETTERS_VISIBLE);

        weatherField.setToolTipText(weather.getDescription());
        weatherNoteArea.setLineWrap(true);
        weatherNoteArea.setWrapStyleWord(true);
        tempField.setEditable(false);
        weatherField.setEditable(false);
        weatherNoteArea.setEditable(false);

        JScrollPane weatherNoteScroll = new JScrollPane(weatherNoteArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints tempLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints tempFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints weatherLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints weatherFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints weatherNoteLabelConstraints = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints weatherNoteAreaConstraints = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        metaPane.add(tempLabel, tempLabelConstraints);
        metaPane.add(tempField, tempFieldConstraints);
        metaPane.add(weatherLabel, weatherLabelConstraints);
        metaPane.add(weatherField, weatherFieldConstraints);
        metaPane.add(weatherNoteLabel, weatherNoteLabelConstraints);
        metaPane.add(weatherNoteScroll, weatherNoteAreaConstraints);

        return metaPane;
    }

    /**
     * Creates panel with general information about experiment.
     *
     * @return panel with general information
     */
    private JPanel createGeneralPane() {
        generalBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "General information", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel generalPane = new JPanel(new GridBagLayout());
        generalPane.setBorder(generalBorder);

        idLabel = new JLabel();
        nameLabel = new JLabel();
        JTextField idField = new JTextField("" + experiment.getExperimentId(), LETTERS_VISIBLE);
        JTextField nameField = new JTextField(scenario.getTitle(), LETTERS_VISIBLE);
        GridBagConstraints idLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints idFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints nameLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints nameFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        idField.setEditable(false);
        nameField.setEditable(false);
        generalPane.add(idLabel, idLabelConstraints);
        generalPane.add(idField, idFieldConstraints);
        generalPane.add(nameLabel, nameLabelConstraints);
        generalPane.add(nameField, nameFieldConstraints);

        return generalPane;
    }

    /**
     * Creates panel with experiment's time information.
     *
     * @return panel with times
     */
    private JPanel createTimePane() {
        timeBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "Time", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel timePane = new JPanel(new GridBagLayout());
        timePane.setBorder(timeBorder);

        Date startTime = experiment.getStartTime();
        Date endTime = experiment.getEndTime();

        DateFormat timeFormat = new SimpleDateFormat("d.M.yyyy HH:mm:ss");

        startLabel = new JLabel();
        endLabel = new JLabel();
        JTextField startField = new JTextField(startTime == null ? null : timeFormat.format(startTime), LETTERS_VISIBLE);
        JTextField endField = new JTextField(endTime == null ? null : timeFormat.format(endTime), LETTERS_VISIBLE);

        startField.setEditable(false);
        endField.setEditable(false);

        GridBagConstraints startLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints startFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints endLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints endFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        timePane.add(startLabel, startLabelConstraints);
        timePane.add(startField, startFieldConstraints);
        timePane.add(endLabel, endLabelConstraints);
        timePane.add(endField, endFieldConstraints);

        return timePane;
    }

    /**
     * Creates panel with information about people involved in experiment.
     *
     * @return panel with people names
     */
    private JPanel createPeoplePane() {
        peopleBorder = BorderFactory.createTitledBorder(createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY), "People", TitledBorder.CENTER, TitledBorder.CENTER);
        JPanel peoplePane = new JPanel(new GridBagLayout());
        peoplePane.setBorder(peopleBorder);

        Person owner = experiment.getOwner();
        Person subject = experiment.getSubject();
        HibernateUtil.rebind(owner);
        HibernateUtil.rebind(subject);

        ownerLabel = new JLabel();
        subjectLabel = new JLabel();
        JTextField ownerField = new JTextField(owner.getName() + " " + owner.getSurname(), LETTERS_VISIBLE);
        JTextField subjectField = new JTextField(subject.getName() + " " + subject.getSurname(), LETTERS_VISIBLE);

        ownerField.setEditable(false);
        subjectField.setEditable(false);

        GridBagConstraints ownerLabelConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints ownerFieldConstraints = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        GridBagConstraints subjectLabelConstraints = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 1), 0, 0);
        GridBagConstraints subjectFieldConstraints = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        peoplePane.add(ownerLabel, ownerLabelConstraints);
        peoplePane.add(ownerField, ownerFieldConstraints);
        peoplePane.add(subjectLabel, subjectLabelConstraints);
        peoplePane.add(subjectField, subjectFieldConstraints);
        return peoplePane;
    }

    public void actionPerformed(ActionEvent action) {
        if ("close".equals(action.getActionCommand())) {
            this.dispose();
        }
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
     *
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                updateTitles();
            }
        });

    }

    private void updateTitles() {

        metaBorder.setTitle(resource.getString("expOverview.ededb.section.meta"));
        generalBorder.setTitle(resource.getString("expOverview.ededb.section.general"));
        timeBorder.setTitle(resource.getString("expOverview.ededb.section.time"));
        peopleBorder.setTitle(resource.getString("expOverview.ededb.section.people"));
        hardwareBorder.setTitle("Hardware");

        closeButton.setText(resource.getString("expOverview.ededb.close"));
        ownerLabel.setText(resource.getString("expOverview.ededb.owner"));
        subjectLabel.setText(resource.getString("expOverview.ededb.subject"));
        startLabel.setText(resource.getString("expOverview.ededb.startTime"));
        endLabel.setText(resource.getString("expOverview.ededb.endTime"));
        idLabel.setText(resource.getString("expOverview.ededb.id"));
        nameLabel.setText(resource.getString("expOverview.ededb.name"));
        tempLabel.setText(resource.getString("expOverview.ededb.temperature"));
        weatherLabel.setText(resource.getString("expOverview.ededb.weather"));
        weatherNoteLabel.setText(resource.getString("expOverview.ededb.weatherNote"));

        hwTitleLabel.setText(resource.getString("expOverview.ededb.hwTitle"));
        hwTypeLabel.setText(resource.getString("expOverview.ededb.hwType"));
        hwDescriptionLabel.setText(resource.getString("expOverview.ededb.hwDescription"));
    }
}
