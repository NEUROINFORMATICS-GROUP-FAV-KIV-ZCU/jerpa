package ch.ethz.origo.jerpa.data.tier.pojo;

import java.sql.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class History {
    private int historyId;
    private boolean changed;

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    private Date dateOfDownload;

    public Date getDateOfDownload() {
        return dateOfDownload;
    }

    public void setDateOfDownload(Date dateOfDownload) {
        this.dateOfDownload = dateOfDownload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        History history = (History) o;

        if (historyId != history.historyId) return false;
        if (dateOfDownload != null ? !dateOfDownload.equals(history.dateOfDownload) : history.dateOfDownload != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = historyId;
        result = 31 * result + (dateOfDownload != null ? dateOfDownload.hashCode() : 0);
        return result;
    }

    private DataFile dataFile;

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    private Experiment experiment;

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private Scenario scenario;

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }
}
