package ch.ethz.origo.jerpa.prezentation.perspective.ededb;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * Frame for visualizing blob from db.
 *
 * @author Petr Miko
 */
public class BlobViewer {

    private final static Logger log = Logger.getLogger(BlobViewer.class);

    private final Dimension PREFERRED_SIZE = new Dimension(800, 600);
    private final int BUFFER_SIZE = 512;

    private Blob fileContent;
    private String filename;
    private long fileLength;
    private String mimeType;

    private Thread open;


    public BlobViewer(final Blob fileContent, String filename, final long fileLength, final String mimeType) {

        this.fileContent = fileContent;
        this.filename = filename;
        this.fileLength = fileLength;
        this.mimeType = mimeType;

        if (fileContent == null) {
            JOptionPane.showMessageDialog(null, "No file to be visualized!");
        } else {
            open = new Thread(new Runnable() {
                public void run() {
                    openText();
                }
            });

            open.start();

        }
    }

    private void openText() {
        try {
            JDialog dialog = new JDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.addWindowListener(new WindowCloseListener());
            dialog.setTitle(filename);
            dialog.setLayout(new BorderLayout());

            byte[] buffer = new byte[BUFFER_SIZE];

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(PREFERRED_SIZE);

            JProgressBar loading = new JProgressBar();
            loading.setStringPainted(true);

            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.add(loading, BorderLayout.SOUTH);
            dialog.validate();
            dialog.pack();
            dialog.setVisible(true);

            Working.setActivity(true, "working.ededb.open");
            int position = 1;
            while(position < fileLength && !open.isInterrupted()){
                buffer = fileContent.getBytes(position, BUFFER_SIZE);
                textArea.append(new String(buffer));
                position += BUFFER_SIZE;
                if(position > fileLength)
                    position = (int) fileLength;

                updateProgressBar(loading, position);
            }
            loading.setVisible(false);
            Working.setActivity(false, "working.ededb.open");
        } catch (SQLException exception) {
            JUIGLErrorInfoUtils.showErrorDialog("Text reading failed.", exception.getMessage(), exception);
        }
    }

    private void updateProgressBar(JProgressBar loading, int total) {
        int progress = (int) ((total * 100.0) / fileLength);

        loading.setString(progress + "%");

        loading.setValue(progress);
    }

    private class WindowCloseListener extends WindowAdapter {
        @Override
        public void windowClosing
                (WindowEvent e) {

            synchronized (open) {
                if (open != null && open.isAlive() && !open.isInterrupted()) {
                    try {
                        open.interrupt();
                        open.join();
                    } catch (InterruptedException e1) {
                        log.error(e1);
                    }
                }
            }
        }
    }
}
