package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 *
 * @author Petr Miko
 */
public class Working {

    private static JDialog working;

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

    public synchronized static void hide() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                working.setVisible(false);
            }
        });
    }
}
