package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.DownloadException;
import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.jerpa.data.tier.dao.DataFileDao;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Downloader extends Observable implements Observer {

	private static boolean isDownloading = false;
	private static final Logger log = Logger.getLogger(Downloader.class);
	private final EDEDBController controller;
	private final EDEDClient session;
	private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private final Map<Integer, Boolean> downloading;
	private final static Object lock = new Object();
    private DataFileDao dataFileDao = DaoFactory.getDataFileDao();

	public Downloader(EDEDBController controller, EDEDClient session) {
		this.controller = controller;
		this.session = session;
		downloading = new HashMap<Integer, Boolean>();
		checkOnline();

	}

	private void checkOnline() {
		Thread online = new Thread(new Runnable() {

			
			public void run() {
				while (true) {
					synchronized (lock) {
						try {
							if (!downloading.isEmpty())
								lock.wait(1000L);
							else
								lock.wait();

							if (controller.isServiceOffline()) {
								downloading.clear();
								Downloader.isDownloading = false;
								setChanged();
								notifyObservers(DownloadState.NOT_DOWNLOADING);
							}
						}
						catch (InterruptedException e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		});

		online.start();
	}

	public void download(DataFile dataFile) {

		boolean overwrite = false;

		try {
			if (overwrite = (dataFileDao.getFileState(dataFile) == FileState.HAS_COPY)) {

				int choice = JOptionPane.showConfirmDialog(null, "You're about to overwrite file " + dataFile.getFilename() + ". Proceed?",
				        "Overwrite prompt", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

				controller.unselectAllFiles();

				if (choice == JOptionPane.NO_OPTION)
					return;
			}

			FileDownload fileDownload = new FileDownload(session, dataFile);
			fileDownload.addObserver(this);

			synchronized (downloading) {
				Downloader.isDownloading = true;
				setChanged();
				notifyObservers(DownloadState.DOWNLOADING);

				pool.submit(fileDownload);
				downloading.put(dataFile.getDataFileId(), true);
			}

			synchronized (lock) {
				lock.notify();
			}
		}
		catch (HeadlessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public boolean isDownloading(DataFile fileInfo) {
		return isDownloading(fileInfo.getDataFileId());
	}

	public boolean isDownloading(int fileId) {
		synchronized (downloading) {
			return (downloading.containsKey(fileId) && downloading.get(fileId));
		}
	}

	public static boolean isDownloading() {
		return Downloader.isDownloading;
	}

	
	public void update(Observable o, Object arg) {

		if (arg instanceof Integer) {
			Integer id = (Integer) arg;
			synchronized (downloading) {
				downloading.remove(id);
				if (downloading.isEmpty()) {
					Downloader.isDownloading = false;
					setChanged();
					notifyObservers(DownloadState.NOT_DOWNLOADING);

				}
			}
			controller.update();
		}
		else if (arg instanceof DownloadException) {
			controller.setUserLoggedIn(false);
		}
	}
}
