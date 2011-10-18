package ch.ethz.origo.jerpa.application.perspective.ededb.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;

import org.apache.log4j.Logger;

import ch.ethz.origo.jerpa.data.tier.DownloadException;
import ch.ethz.origo.jerpa.data.tier.Storage;
import ch.ethz.origo.jerpa.data.tier.border.DataFile;
import ch.ethz.origo.jerpa.ededclient.sources.EDEDClient;
import ch.ethz.origo.jerpa.prezentation.perspective.ededb.Working;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;

/**
 * Thread extending class for file download.
 * 
 * @author Petr Miko
 */
public class FileDownload extends Observable implements Runnable {

	private final EDEDClient session;
	private final DataFile fileInfo;
	private final Storage storage;
	private ProgressInputStream inStream = null;
	private boolean alive = true;

	private final static Logger log = Logger.getLogger(FileDownload.class);

	public FileDownload(EDEDClient session, Storage storage, DataFile fileInfo) {
		super();

		this.session = session;
		this.fileInfo = fileInfo;
		this.storage = storage;
	}

	/**
	 * Synchronized run method of downloading process.
	 */
	@Override
	public void run() {

		try {

			Working.setActivity(true, "working.ededb.downloading");

			inStream = new ProgressInputStream(session.getService().downloadDataFile(fileInfo.getFileId()).getInputStream(), fileInfo);
			log.info("File " + fileInfo.getFileName() + ": beginning download");

			DownloadProgress downProgress = new DownloadProgress();
			downProgress.start();

			storage.writeDataFile(fileInfo, inStream);

			log.info("File " + fileInfo.getFileName() + ": download finished");
		}
		catch (Exception e) {
			// ignores exceptions when downloader is not supposed to be
			// downloading
			if (Downloader.isDownloading()) {
				DownloadException exception = new DownloadException(e);
				log.error(exception.getMessage(), exception);
				JUIGLErrorInfoUtils.showErrorDialog("Storage exception.", exception.getMessage(), exception);
				notifyObservers(exception);
			}
		}
		finally {
			try {
				if (inStream != null) {
					inStream.close();
				}

				alive = false;
				Working.setDownload(100, fileInfo);
				Working.setActivity(false, "working.ededb.downloading");

				setChanged();
				notifyObservers(fileInfo.getFileId());

			}
			catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}
	}

	private class DownloadProgress extends Thread {

		@Override
		public void run() {

			int progress = 0;

			while ((progress = inStream.progress()) < 100 && alive && Downloader.isDownloading()) {
				Working.setDownload(progress, fileInfo);
			}

		}
	}

	private class ProgressInputStream extends InputStream {

		private InputStream in = null;
		private int bytesRead = 0;
		private final DataFile fileInfo;
		private int progress = 0;
		private int prev = 0;

		public ProgressInputStream(InputStream in, DataFile fileInfo) {
			this.in = in;
			this.fileInfo = fileInfo;
		}

		@Override
		public int available() {
			return (int) (fileInfo.getFileLength() - bytesRead);
		}

		@Override
		public int read() throws IOException {
			if (Downloader.isDownloading()) {
				int b = in.read();
				if (b != -1) {
					bytesRead++;
				}
				return b;
			}
			else
				return -1;
		}

		@Override
		public int read(byte[] b) throws IOException {
			if (Downloader.isDownloading()) {
				int read = in.read(b);
				bytesRead += read;
				return read;
			}
			else
				return -1;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (Downloader.isDownloading()) {
				int read = in.read(b, off, len);
				bytesRead += read;
				return read;
			}
			else
				return -1;
		}

		public int progress() {
			progress = (int) (((++bytesRead * 100) / fileInfo.getFileLength()));
			if (progress - prev > 0) {
				prev = progress;
			}
			return progress;
		}

	}
}
