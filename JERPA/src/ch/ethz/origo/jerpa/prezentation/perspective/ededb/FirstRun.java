package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import org.jdesktop.swingx.JXPanel;

/**
 *
 * @author Petr Miko
 */
class FirstRun extends JXPanel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private Controller controller;

    public FirstRun(final Controller controller) {
        super();

        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.controller = controller;
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        TitledBorder titledBorder = new TitledBorder(resource.getString("firstrun.ededb.title"));
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        
        JScrollPane textPane = new JScrollPane();

        JTextArea text = new JTextArea(resource.getString("firstrun.ededb.text"));
        text.setBackground(this.getBackground());
        text.setForeground(this.getForeground());
        text.setEditable(false);
        text.setFocusable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        
        
        textPane.getViewport().add(text);
        
        JButton choose = new JButton(resource.getString("firstrun.ededb.choosedir"));

        choose.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnValue = fileChooser.showOpenDialog(new JPanel());

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selected = fileChooser.getSelectedFile();
                    
                    controller.setDownloadPath(selected.getAbsolutePath());
                    controller.setFirstRun(false);
                    controller.initGraphics();
                    controller.update();
                }
            }
        });

        this.add(textPane);
        this.add(choose);
        this.setBorder(titledBorder);

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
