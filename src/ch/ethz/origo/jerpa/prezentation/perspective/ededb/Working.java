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
     * Method setting visibility according to input integer
     */
    public synchronized static void setVisible(boolean visibility) {
        if (working == null) {
            new Working();
        }

        final boolean tmp = visibility;
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                working.setVisible(tmp);
                working.repaint();
            }
        });
    }
}