package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonAreaLayout;
import org.jdesktop.swingx.HorizontalLayout;
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
    private EDEDSession session;
    private JButton connectButton, disconnectButton, downloadButton, chooseFolderButton,
            openFolderButton, deleteFileButton, analyseFileButton;
    private JRadioButton ownerButton, subjectButton;

    /**
     * Creating main panel and setting elements into proper positions.
     *
     * @param controller EDEDBController class of EDEDB
     * @param session EDEDSession class
     */
    public Toolbar(EDEDBController controller, EDEDSession session) {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.session = session;
        this.controller = controller;

        JPanel radioBar = new JPanel();

        this.setLayout(new VerticalLayout());
        radioBar.setLayout(new BoxLayout(radioBar, BoxLayout.LINE_AXIS));

        createButtons();

        radioBar.add(ownerButton);
        radioBar.add(subjectButton);

        this.add(connectButton);
        this.add(disconnectButton);
        this.add(chooseFolderButton);
        this.add(openFolderButton);
        this.add(radioBar);
        this.add(analyseFileButton);
        this.add(downloadButton);
        this.add(deleteFileButton);

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
        createIcons();

        ButtonGroup group = new ButtonGroup();

        controller.setRights(Rights.OWNER);

        group.add(ownerButton);
        group.add(subjectButton);
        
        connectButton.setHorizontalAlignment(SwingConstants.LEFT);
        disconnectButton.setHorizontalAlignment(SwingConstants.LEFT);
        downloadButton.setHorizontalAlignment(SwingConstants.LEFT);
        deleteFileButton.setHorizontalAlignment(SwingConstants.LEFT);
        chooseFolderButton.setHorizontalAlignment(SwingConstants.LEFT);
        openFolderButton.setHorizontalAlignment(SwingConstants.LEFT);
        analyseFileButton.setHorizontalAlignment(SwingConstants.LEFT);

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

        if (!controller.isDownloading() && !controller.isLock()) {
            analyseFileButton.setEnabled(true);
        } else {
            analyseFileButton.setEnabled(false);
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

    private void createIcons() {
        try {
            analyseFileButton.setIcon(JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "ededb_48.png", 32, 32));
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
