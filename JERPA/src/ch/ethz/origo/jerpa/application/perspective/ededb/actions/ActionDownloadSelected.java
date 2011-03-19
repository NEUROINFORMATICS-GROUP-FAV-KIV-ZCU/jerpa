package ch.ethz.origo.jerpa.application.perspective.ededb.actions;

import ch.ethz.origo.jerpa.ededclient.sources.EDEDSession;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Controller;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Petr Miko
 */
public class ActionDownloadSelected extends AbstractAction {

    private Controller controller;
    private EDEDSession session;

    public ActionDownloadSelected(Controller controller, EDEDSession session) {
        super();
        this.controller = controller;
        this.session = session;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            if (session.isConnected())
            {
                /*if (session.isServerAvailable()) {
                    if (session.getDownloadFolder() != null) {
                        List<DataRowModel> filesToDownload = tables.getFileDataToDownload();

                        if (filesToDownload.size() == 0) {
                            feature.infoMsg("In order to download files you must select some first.", "Download");
                        }

                        for (DataRowModel file : filesToDownload) {

                            if (file.isDownloaded()) {

                                int retValue = JOptionPane.showConfirmDialog(null, "File \"" + file.getFileInfo().getFilename() +
                                        "\" already exists in your download folder. Overwrite?", "Download - Overwrite?",
                                        JOptionPane.YES_NO_OPTION);

                                if (retValue == JOptionPane.NO_OPTION) {
                                    file.setToDownload(false);
                                    tables.repaint();
                                    continue;
                                }

                            }

                            FileDownload fileDownload = new FileDownload(feature, session, tables, file);
                            fileDownload.run();

                            file.setToDownload(false);
                            tables.repaint();
                        }
                    } else {
                        feature.errorMsg("In order to download you must select the download folder.", "Download error");
                        (new ActionChooseDownloadFolder(main, session)).actionPerformed(null);
                    }

                } else {
                    feature.errorMsg("You must be logged in to download.", "Download error");
                }
                 }
        } catch (ConnectException ex) {
            Logger.getLogger(ActionDownloadSelected.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WebServiceException ex) {
            Logger.getLogger(ActionDownloadSelected.class.getName()).log(Level.SEVERE, null, ex);
        }*/
            }
    }
}
