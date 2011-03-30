package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.Controller;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import org.jdesktop.swingx.JXPanel;

/**
 * Class for creating side-toolbar for EDEDB.
 *
 * @author Petr Miko
 */
public class Toolbar extends JXPanel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private Controller controller;
    private EDEDSession session;
    private JButton connectButton, disconnectButton, downloadButton, chooseFolderButton,
            openFolderButton, deleteFileButton, analyseFileButton;
    private JRadioButton ownerButton, subjectButton;

    /**
     * Creating main panel and setting elements into proper positions.
     *
     * @param controller Controller class of EDEDB
     * @param session EDEDSession class
     */
    public Toolbar(Controller controller, EDEDSession session) {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.session = session;
        this.controller = controller;

        JPanel buttonBar = new JPanel();
        JPanel radioBar = new JPanel();

        this.setLayout(new BorderLayout());
        buttonBar.setLayout(new GridBagLayout());
        GridBagConstraints butBarConstrains = new GridBagConstraints();
        radioBar.setLayout(new BoxLayout(radioBar, BoxLayout.PAGE_AXIS));

        createButtons();

        butBarConstrains.fill = GridBagConstraints.HORIZONTAL;
        butBarConstrains.gridx = 0;
        butBarConstrains.gridy = 0;
        butBarConstrains.gridwidth = 2;
        buttonBar.add(connectButton, butBarConstrains);

        butBarConstrains.gridx = 0;
        butBarConstrains.gridy = 0;
        buttonBar.add(disconnectButton, butBarConstrains);


        butBarConstrains.gridx = 0;
        butBarConstrains.gridy = 1;
        buttonBar.add(openFolderButton, butBarConstrains);

        butBarConstrains.gridx = 0;
        butBarConstrains.gridy = 2;
        buttonBar.add(chooseFolderButton, butBarConstrains);

        butBarConstrains.gridx = 0;
        butBarConstrains.gridy = 3;
        buttonBar.add(analyseFileButton, butBarConstrains);

        butBarConstrains.gridx = 0;
        butBarConstrains.gridy = 4;
        butBarConstrains.gridwidth = 1;
        buttonBar.add(downloadButton, butBarConstrains);

        butBarConstrains.gridx = 1;
        butBarConstrains.gridy = 4;
        butBarConstrains.gridwidth = 1;
        buttonBar.add(deleteFileButton, butBarConstrains);

        radioBar.add(ownerButton);
        radioBar.add(subjectButton);

        this.add(buttonBar, BorderLayout.NORTH);
        this.add(radioBar, BorderLayout.CENTER);

        ownerButton.setSelected(true);
    }

    /**
     * Buttons initialization and setting their actions
     */
    private void createButtons() {

        connectButton = new JButton();
        disconnectButton = new JButton();
        downloadButton = new JButton();
        openFolderButton = new JButton();
        chooseFolderButton = new JButton();
        analyseFileButton = new JButton();
        deleteFileButton = new JButton();
        ownerButton = new JRadioButton();
        subjectButton = new JRadioButton();

        updateButtonsText();

        ButtonGroup group = new ButtonGroup();

        controller.setRights(Rights.OWNER);

        group.add(ownerButton);
        group.add(subjectButton);

        connectButton.addActionListener(controller.getActionConnect());
        disconnectButton.addActionListener(controller.getActionDisconnect());
        downloadButton.addActionListener(controller.getActionDownloadSelected());
        deleteFileButton.addActionListener(controller.getActionDeleteSelected());
        chooseFolderButton.addActionListener(controller.getActionChooseDownloadFolder());
        openFolderButton.addActionListener(controller.getActionOpenDownloadPath());
        analyseFileButton.addActionListener(controller.getActionAnalyseSelected());

        disconnectButton.setVisible(false);

        ownerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ownerButton.setSelected(true);
                controller.setRights(Rights.OWNER);
                controller.update();
            }
        });

        subjectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                subjectButton.setSelected(true);
                controller.setRights(Rights.SUBJECT);
                controller.update();
            }
        });
    }

    /**
     * Setting buttons visibility giving the exact situation.
     */
    public void updateButtonsVisibility() {

        if (session.isConnected()) {
            connectButton.setVisible(false);
            disconnectButton.setVisible(true);
        } else {
            connectButton.setVisible(true);
            disconnectButton.setVisible(false);
        }

        if (controller.isOnlineTab() && !controller.isLock()) {
            downloadButton.setEnabled(true);
            ownerButton.setEnabled(true);
            subjectButton.setEnabled(true);
        } else {
            downloadButton.setEnabled(false);
            ownerButton.setEnabled(false);
            subjectButton.setEnabled(false);
        }
    }

    /**
     * Update text method. Vital for localization.
     */
    public void updateButtonsText() {
        connectButton.setText(resource.getString("sidebar.ededb.toolbar.connect"));
        disconnectButton.setText(resource.getString("sidebar.ededb.toolbar.disconnect"));
        downloadButton.setText(resource.getString("sidebar.ededb.toolbar.download"));
        openFolderButton.setText(resource.getString("sidebar.ededb.toolbar.opendir"));
        chooseFolderButton.setText(resource.getString("sidebar.ededb.toolbar.choosedir"));
        analyseFileButton.setText(resource.getString("sidebar.ededb.toolbar.analysefile"));
        deleteFileButton.setText(resource.getString("sidebar.ededb.toolbar.deletefile"));
        ownerButton.setText(resource.getString("sidebar.ededb.toolbar.owner"));
        subjectButton.setText(resource.getString("sidebar.ededb.toolbar.subject"));
    }

    /**
     * Method setting localization resource budle path.
     * @param path Resource bundle path
     */
    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of resource budle path.
     * @return resource budle path
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * ILanguage updating method. Vital for localization.
     * @throws JUIGLELangException
     */
    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                updateButtonsText();
            }
        });

    }

    /**
     * Method setting buttons on/off.
     * @param active boolean setting buttons' activeness
     */
    public void setButtonsEnabled(boolean active) {
        connectButton.setEnabled(active);
        disconnectButton.setEnabled(active);
        chooseFolderButton.setEnabled(active);
        deleteFileButton.setEnabled(active);
        analyseFileButton.setEnabled(active);
        openFolderButton.setEnabled(active);
        downloadButton.setEnabled(active);
        ownerButton.setEnabled(active);
        subjectButton.setEnabled(active);

        updateButtonsVisibility();
    }
}
