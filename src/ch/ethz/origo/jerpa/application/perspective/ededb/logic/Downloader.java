package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.data.tier.DownloadException;
import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.jerpa.data.tier.Storage;
import ch.ethz.origo.jerpa.data.tier.StorageException;
import ch.ethz.origo.jerpa.data.tier.border.DataFile;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

public class Downloader extends Observable implements Observer {

	private static boolean isDownloading = false;
	private static final Logger log = Logger.getLogger(Downloader.class);
	private final EDEDBController controller;
	private final EDEDClient session;
	private final Storage storage;
	private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private final Map<Integer, Boolean> downloading;
	private final static Object lock = new Object();

	public Downloader(EDEDBController controller, EDEDClient session) {
		this.controller = controller;
		this.session = session;
		storage = controller.getStorage();

		downloading = new HashMap<Integer, Boolean>();
		checkOnline();

	}

	private void checkOnline() {
		Thread online = new Thread(new Runnable() {

			@Override
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

	public void download(DataFile fileInfo) {

		boolean overwrite = false;

		try {
			if (overwrite = (storage.getFileState(fileInfo) == FileState.HAS_COPY)) {

				int choice = JOptionPane.showConfirmDialog(null, "You're about to overwrite file " + fileInfo.getFileName() + ". Proceed?",
				        "Overwrite prompt", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

				controller.unselectAllFiles();

				if (choice == JOptionPane.NO_OPTION)
					return;
			}

			FileDownload fileDownload = new FileDownload(session, storage, fileInfo);
			fileDownload.addObserver(this);

			synchronized (downloading) {
				Downloader.isDownloading = true;
				setChanged();
				notifyObservers(DownloadState.DOWNLOADING);

				pool.submit(fileDownload);
				downloading.put(fileInfo.getFileId(), true);
			}

			synchronized (lock) {
				lock.notify();
			}
		}
		catch (HeadlessException e) {
			log.error(e.getMessage(), e);
		}
		catch (StorageException e) {
			log.error(e.getMessage(), e);
			JUIGLErrorInfoUtils.showErrorDialog(e.getMessage(), e.getLocalizedMessage(), e);
		}
	}

	public boolean isDownloading(DataFile fileInfo) {
		return isDownloading(fileInfo.getFileId());
	}

	public boolean isDownloading(int fileId) {
		synchronized (downloading) {
			return (downloading.containsKey(fileId) && downloading.get(fileId));
		}
	}

	public static boolean isDownloading() {
		return Downloader.isDownloading;
	}

	@Override
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
