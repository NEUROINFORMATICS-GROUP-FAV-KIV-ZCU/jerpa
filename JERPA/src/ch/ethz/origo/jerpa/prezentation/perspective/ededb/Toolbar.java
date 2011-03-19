package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.ededclient.generated.Rights;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import org.jdesktop.swingx.JXPanel;

/**
 * @author Petr Miko
 */
public class Toolbar extends JXPanel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    
    private Controller controller;
    private EDEDSession session;
    
    private JButton connectButton, disconnectButton, downloadButton, chooseFolder;
    private JRadioButton ownerButton, subjectButton;
    
    public Toolbar(Controller controller, EDEDSession session) {
        super();

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
        buttonBar.add(connectButton, butBarConstrains);

        butBarConstrains.gridx = 0;
        butBarConstrains.gridy = 0;
        buttonBar.add(disconnectButton, butBarConstrains);

        butBarConstrains.gridx = 1;
        butBarConstrains.gridy = 0;
        buttonBar.add(downloadButton, butBarConstrains);

        butBarConstrains.gridx = 0;
        butBarConstrains.gridy = 1;
        butBarConstrains.gridwidth = 2;
        buttonBar.add(chooseFolder, butBarConstrains);

        radioBar.add(ownerButton);
        radioBar.add(subjectButton);

        this.add(buttonBar, BorderLayout.NORTH);
        this.add(radioBar, BorderLayout.CENTER);

        ownerButton.setSelected(true);
    }

    private void createButtons() {

        connectButton = new JButton(resource.getString("sidebar.ededb.toolbar.connect"));
        disconnectButton = new JButton(resource.getString("sidebar.ededb.toolbar.disconnect"));
        downloadButton = new JButton(resource.getString("sidebar.ededb.toolbar.download"));
        chooseFolder = new JButton(resource.getString("sidebar.ededb.toolbar.choosedir"));
        ownerButton = new JRadioButton(resource.getString("sidebar.ededb.toolbar.owner"));
        subjectButton = new JRadioButton(resource.getString("sidebar.ededb.toolbar.subject"));

        ButtonGroup group = new ButtonGroup();
        
        controller.setRights(Rights.OWNER);

        group.add(ownerButton);
        group.add(subjectButton);

        connectButton.addActionListener(controller.getActionConnect());
        disconnectButton.addActionListener(controller.getActionDisconnect());
        downloadButton.addActionListener(controller.getActionDownloadSelected());
        chooseFolder.addActionListener(controller.getActionChooseDownloadFolder());
        
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

    public void updateButtonsVisibility() {

        if (session.isConnected()) {
            connectButton.setVisible(false);
            disconnectButton.setVisible(true);
        } else {
            connectButton.setVisible(true);
            disconnectButton.setVisible(false);
        }
    }

    public void setLocalizedResourceBundle(String path) {
        this.resourceBundlePath = path;
        resource = ResourceBundle.getBundle(path);
    }

    public String getResourceBundlePath() {
        return resourceBundlePath;
    }

    public void setResourceBundleKey(String string) {
        //not implemented
    }

    public void updateText() throws JUIGLELangException {
        //not implemented
    }
}
