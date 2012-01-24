package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Petr Miko
 *         <p/>
 *         DAO for DataFile type manipulation.
 */
public class DataFileDao extends GenericDao<DataFile, Integer> {

    public DataFileDao() {
        super(DataFile.class);
    }

    public void writeFileContent(DataFile file, InputStream inStream) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Blob blob = Hibernate.getLobCreator(session).createBlob(inStream, file.getFileLength());
        file.setDataFileId(file.getDataFileId());
        file.setFileContent(blob);
        session.saveOrUpdate(file);
        session.getTransaction().commit();
    }

    public List<DataFile> getAllFromExperiments(List<Experiment> experiments) {
        List<DataFile> files = new ArrayList<DataFile>();

        for (Experiment experiment : experiments) {
            files.addAll(experiment.getDataFiles());
        }
        return files;
    }

    public synchronized FileState getFileState(DataFile file) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.refresh(file);
            Blob blob = file.getFileContent();
            if (blob == null) {
                return FileState.NO_COPY;
            } else {
                long fileSize = blob.length();
                if (fileSize == 0) {
                    return FileState.NO_COPY;
                } else if (fileSize != file.getFileLength()) {
                    return FileState.CORRUPTED;
                } else {
                    return FileState.HAS_COPY;
                }
            }
        } catch (SQLException e) {
            if (transaction.isActive())
                transaction.rollback();
            e.printStackTrace();
            return FileState.DOWNLOADING;
        } finally {
            if (transaction.isActive())
                transaction.commit();
        }
//        return FileState.NO_COPY;
    }

    public File getFile(DataFile file) throws DaoException {

        File fileContent = new File(file.getFilename());
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = new FileOutputStream(fileContent);
            inputStream = file.getFileContent().getBinaryStream();

            byte[] buffer = new byte[512];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
                outputStream.flush();
            }

            inputStream.close();
            outputStream.close();
        } catch (SQLException e) {
            throw new DaoException(e);
        } catch (IOException e) {
            throw new DaoException(e);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //irrelevant
                }

            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    //irrelevant
                }
        }
        return fileContent;
    }

    public void removeFile(DataFile dataFile) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        dataFile.setFileContent(null);
        session.update(dataFile);
        transaction.commit();
    }
}
