package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * Class for creating undecorated dialog showing "working animation"
 *
 * @author Petr Miko
 */
public class Working {

    private static JDialog working;

    /**
     *  MEthod creating JDialog
     */
    public Working() {
        working = new JDialog();

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        
        working.add(progress);
        working.setLocationRelativeTo(null);
        working.setUndecorated(true);
        working.setAlwaysOnTop(true);
        working.pack();
    }

    /**
     * Method setting visibility on true
     */
    public synchronized static void show() {
        if (working == null) {
            new Working();
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                working.setVisible(true);
            }
        });
    }

    /**
     * Method setting visibility on false
     */
    public synchronized static void hide() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                working.setVisible(false);
            }
        });
    }
}
