package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.application.perspective.ededb.logic.EDEDBController;
import ch.ethz.origo.juigle.application.ILanguage;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
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
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import org.jdesktop.swingx.JXPanel;

/**
 * JXPanel extending class for performing first run interface.
 *
 * @author Petr Miko
 */
public class FirstRun extends JXPanel implements ILanguage {

    private ResourceBundle resource;
    private String resourceBundlePath;
    private EDEDBController controller;

    private JButton choose;
    private JTextArea text;
    private TitledBorder titledBorder;

    /**
     * Contructor creating whole FirstRun JXPanel with all its elements.
     * @param controller EDEDBController
     */
    public FirstRun(final EDEDBController controller) {
        super();

        LanguageObservable.getInstance().attach(this);
        setLocalizedResourceBundle("ch.ethz.origo.jerpa.jerpalang.perspective.ededb.EDEDB");

        this.controller = controller;
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        titledBorder = new TitledBorder(resource.getString("firstrun.ededb.title"));
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        
        JScrollPane textPane = new JScrollPane();

        text = new JTextArea(resource.getString("firstrun.ededb.text"));
        text.setBackground(this.getBackground());
        text.setForeground(this.getForeground());
        text.setEditable(false);
        text.setFocusable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        
        
        textPane.getViewport().add(text);
        
        choose = new JButton(resource.getString("firstrun.ededb.choosedir"));

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
        throw new UnsupportedOperationException("Method is not implemented yet...");
    }

    public void updateText() throws JUIGLELangException {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                titledBorder.setTitle(resource.getString("firstrun.ededb.title"));
                text.setText(resource.getString("firstrun.ededb.text"));
                choose.setText(resource.getString("firstrun.ededb.choosedir"));
            }
        });
    }
}
