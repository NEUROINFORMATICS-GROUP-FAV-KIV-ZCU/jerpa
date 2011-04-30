package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import java.awt.Desktop;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.VerticalLayout;

/**
 * Class for creating side-toolbar for EDEDB.
 *
 * @author Petr Miko
 */
public class Toolbar extends JXPanel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private EDEDBController controller;
    private EDEDClient session;
    private JButton connectButton, disconnectButton, downloadButton, chooseFolderButton,
            openFolderButton, deleteFileButton, visualizeFileButton;
    private JRadioButton ownerButton, subjectButton, allButton;

    /**
     * Creating main panel and setting elements into proper positions.
     *
     * @param controller EDEDBController class of EDEDB
     * @param session EDEDSession class
     */
    public Toolbar(EDEDBController controller, EDEDClient session) {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.session = session;
        this.controller = controller;

        JPanel radioBar = new JPanel();

        this.setLayout(new VerticalLayout());
        radioBar.setLayout(new BoxLayout(radioBar, BoxLayout.LINE_AXIS));

        createButtons();

        radioBar.add(allButton);
        radioBar.add(ownerButton);
        radioBar.add(subjectButton);

        this.add(connectButton);
        this.add(disconnectButton);
        this.add(chooseFolderButton);
        this.add(openFolderButton);
        this.add(radioBar);
        this.add(visualizeFileButton);
        this.add(downloadButton);
        this.add(deleteFileButton);

        allButton.setSelected(true);
        disconnectButton.setVisible(false);
        openFolderButton.setVisible(Desktop.isDesktopSupported());
        downloadButton.setEnabled(false);

        revalidate();
        repaint();
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
        visualizeFileButton = new JButton();
        deleteFileButton = new JButton();
        allButton = new JRadioButton();
        ownerButton = new JRadioButton();
        subjectButton = new JRadioButton();

        updateButtonsText();
        createIcons();

        ButtonGroup group = new ButtonGroup();

        controller.setRights(Rights.ALL);

        group.add(allButton);
        group.add(ownerButton);
        group.add(subjectButton);

        connectButton.setHorizontalAlignment(SwingConstants.LEFT);
        disconnectButton.setHorizontalAlignment(SwingConstants.LEFT);
        downloadButton.setHorizontalAlignment(SwingConstants.LEFT);
        deleteFileButton.setHorizontalAlignment(SwingConstants.LEFT);
        chooseFolderButton.setHorizontalAlignment(SwingConstants.LEFT);
        openFolderButton.setHorizontalAlignment(SwingConstants.LEFT);
        visualizeFileButton.setHorizontalAlignment(SwingConstants.LEFT);

        connectButton.addActionListener(controller.getActionConnect());
        disconnectButton.addActionListener(controller.getActionDisconnect());
        downloadButton.addActionListener(controller.getActionDownloadSelected());
        deleteFileButton.addActionListener(controller.getActionDeleteSelected());
        chooseFolderButton.addActionListener(controller.getActionChooseDownloadFolder());
        openFolderButton.addActionListener(controller.getActionOpenDownloadPath());
        visualizeFileButton.addActionListener(controller.getActionVisualizeSelected());

        allButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                allButton.setSelected(true);
                controller.setRights(Rights.ALL);
            }
        });

        ownerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ownerButton.setSelected(true);
                controller.setRights(Rights.OWNER);
            }
        });

        subjectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                subjectButton.setSelected(true);
                controller.setRights(Rights.SUBJECT);
            }
        });
    }

    /**
     * Setting buttons visibility giving the exact situation.
     */
    public void updateButtonsVisibility() {

        openFolderButton.setVisible(Desktop.isDesktopSupported());


        if (session.isConnected()) {
            connectButton.setVisible(false);
            disconnectButton.setVisible(true);
        } else {
            connectButton.setVisible(true);
            disconnectButton.setVisible(false);
        }

        if (session.isConnected() && !controller.isLock()) {
            downloadButton.setEnabled(true);
        } else {
            downloadButton.setEnabled(false);
        }

        if (!controller.isDownloading() && !controller.isLock()) {
            visualizeFileButton.setEnabled(true);
        } else {
            visualizeFileButton.setEnabled(false);
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
        visualizeFileButton.setText(resource.getString("sidebar.ededb.toolbar.visualise"));
        deleteFileButton.setText(resource.getString("sidebar.ededb.toolbar.deletefile"));
        allButton.setText(resource.getString("sidebar.ededb.toolbar.all"));
        ownerButton.setText(resource.getString("sidebar.ededb.toolbar.owner"));
        subjectButton.setText(resource.getString("sidebar.ededb.toolbar.subject"));
    }

    /**
     * Setter of localization resource budle path
     * @param path path to localization source file.
     */
    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    /**
     * Getter of path to resource bundle.
     * @return path to localization file.
     */
    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    /**
     * Setter of resource budle key.
     * @param string key
     */
    public void setResourceBundleKey(String string) {
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    /**
     * Method invoked by change of LanguageObservable.
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
        visualizeFileButton.setEnabled(active);
        openFolderButton.setEnabled(active);
        downloadButton.setEnabled(active);
        allButton.setEnabled(active);
        ownerButton.setEnabled(active);
        subjectButton.setEnabled(active);

        updateButtonsVisibility();
    }

    /**
     * Method for creating and setting icons to buttons.
     */
    private void createIcons() {
        try {
            visualizeFileButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "ededb_48.png", 32, 32));
            openFolderButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "folder_48.png", 32, 32));
            chooseFolderButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "spanner_48.png", 32, 32));

            connectButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "login_48.png", 32, 32));
            disconnectButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "logout_48.png", 32, 32));
            downloadButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "arrow_down_green_48.png", 32, 32));
            deleteFileButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "cross_48.png", 32, 32));

        } catch (PerspectiveException ex) {
            JUIGLErrorInfoUtils.showErrorDialog(
                    ex.getMessage(),
                    ex.getLocalizedMessage(),
                    ex);
        }
    }
}
