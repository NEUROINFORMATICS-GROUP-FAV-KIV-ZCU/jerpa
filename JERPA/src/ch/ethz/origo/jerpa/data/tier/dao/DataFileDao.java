package ch.ethz.origo.jerpa.data.tier.dao;

import ch.ethz.origo.jerpa.data.tier.FileState;
import ch.ethz.origo.jerpa.data.tier.HibernateUtil;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
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

    private final static Logger log = Logger.getLogger(DataFileDao.class);

    public DataFileDao() {
        super(DataFile.class);
    }

    /**
     * Method for saving binary stream into DataFile's blob.
     *
     * @param file     data file
     * @param inStream binary input stream
     */
    public void writeFileContent(DataFile file, InputStream inStream) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Blob blob = Hibernate.getLobCreator(session).createBlob(inStream, file.getFileLength());
        file.setDataFileId(file.getDataFileId());
        file.setFileContent(blob);
        session.saveOrUpdate(file);
        session.getTransaction().commit();
    }

    /**
     * Getter of all data files from specified experiments.
     *
     * @param experiments specified data files source experiments
     * @return data files
     */
    public List<DataFile> getAllFromExperiments(List<Experiment> experiments) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<DataFile> files = new ArrayList<DataFile>();

        for (Experiment experiment : experiments) {
            session.refresh(experiment);
            files.addAll(experiment.getDataFiles());
        }
        transaction.commit();
        return files;
    }

    /**
     * Getter of current data file state.
     *
     * @param file data file
     * @return FileState value
     */
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
                if (fileSize != file.getFileLength()) {
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
    }

    /**
     * Getter of BLOB in File form. Returns temp File.
     *
     * @param file data file
     * @return temp file with contents of data file blob
     * @throws DaoException issue with DAO
     */
    public File getFile(DataFile file) throws DaoException {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        File fileContent = null;
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            fileContent = File.createTempFile("JERPA-tmp-", file.getFilename());
            session.refresh(file);
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
            transaction.commit();
        } catch (SQLException e) {
            transaction.rollback();
            throw new DaoException(e);
        } catch (IOException e) {
            transaction.rollback();
            throw new DaoException(e);
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e);
                }

            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error(e);
                }
        }
        return fileContent;
    }

    /**
     * Method for removing blob from data file.
     *
     * @param dataFile specified data file
     */
    public void removeFile(DataFile dataFile) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        dataFile.setFileContent(null);
        session.update(dataFile);
        transaction.commit();
    }

    public void createDataFile(Experiment exp, File file) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        DataFile dataFile = new DataFile();
        dataFile.setChanged(true);
        dataFile.setExperiment(exp);
        dataFile.setFileLength(file.length());
        dataFile.setFilename(file.getName());
        dataFile.setMimetype(new MimetypesFileTypeMap().getContentType(file));

        InputStream inStream = null;
        try {

            inStream = new FileInputStream(file);
            dataFile.setFileContent(Hibernate.getLobCreator(session).createBlob(inStream, file.length()));


        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }

        try {
            session.save(dataFile);
            transaction.commit();
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            transaction.rollback();
        }
    }

    /**
     * Method for overwriting blob inside existing data file.
     *
     * @param dataFile data file
     * @param file     java.io.File
     */
    public void overwriteDataFile(DataFile dataFile, File file) {

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
            dataFile.setFileContent(Hibernate.getLobCreator(session).createBlob(inStream, file.length()));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }

        try {
            session.save(dataFile);
            transaction.commit();
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
            transaction.rollback();
        }
    }
}
