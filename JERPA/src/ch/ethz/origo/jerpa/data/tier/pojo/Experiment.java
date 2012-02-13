package ch.ethz.origo.jerpa.data.tier.pojo;

import ch.ethz.origo.jerpa.data.tier.HibernateUtil;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class Experiment {
    private int experimentId;
    private boolean changed;
    private Set<Hardware> hardwares;

    public Set<Hardware> getHardwares() {
        return hardwares;
    }

    public void setHardwares(Set<Hardware> hardwares) {
        this.hardwares = hardwares;
    }

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    private Date startTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    private Date endTime;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    private short temperature;

    public short getTemperature() {
        return temperature;
    }

    public void setTemperature(short temperature) {
        this.temperature = temperature;
    }

    private String weathernote;

    public String getWeathernote() {
        return weathernote;
    }

    public void setWeathernote(String weathernote) {
        this.weathernote = weathernote;
    }

    private boolean isPrivate;

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Experiment that = (Experiment) o;

        if (experimentId != that.experimentId) return false;
        if (temperature != that.temperature) return false;
        if (version != that.version) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (weathernote != null ? !weathernote.equals(that.weathernote) : that.weathernote != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = experimentId;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (int) temperature;
        result = 31 * result + (weathernote != null ? weathernote.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    private Collection<CoexperimenterRel> coexperimenterRels;

    public Collection<CoexperimenterRel> getCoexperimenterRels() {
        return coexperimenterRels;
    }

    public void setCoexperimenterRels(Collection<CoexperimenterRel> coexperimenterRels) {
        this.coexperimenterRels = coexperimenterRels;
    }

    private Collection<DataFile> dataFiles;

    public Collection<DataFile> getDataFiles() {
        return dataFiles;
    }

    public void setDataFiles(Collection<DataFile> dataFiles) {
        this.dataFiles = dataFiles;
    }

    private Person subject;

    public Person getSubject() {
        return subject;
    }

    public void setSubject(Person subject) {
        this.subject = subject;
    }

    private Person owner;

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    private ResearchGroup researchGroup;

    public ResearchGroup getResearchGroup() {
        return researchGroup;
    }

    public void setResearchGroup(ResearchGroup researchGroup) {
        this.researchGroup = researchGroup;
    }

    private Scenario scenario;

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    private Weather weather;

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    private Collection<ExperimentOptParamVal> experimentOptParamVals;

    public Collection<ExperimentOptParamVal> getExperimentOptParamVals() {
        return experimentOptParamVals;
    }

    public void setExperimentOptParamVals(Collection<ExperimentOptParamVal> experimentOptParamVals) {
        this.experimentOptParamVals = experimentOptParamVals;
    }

    private Collection<History> histories;

    public Collection<History> getHistories() {
        return histories;
    }

    public void setHistories(Collection<History> histories) {
        this.histories = histories;
    }

    public String toString(){
        DateFormat timeFormat = new SimpleDateFormat("d.M.yyyy HH:mm:ss");

        return experimentId + " | " + (startTime == null ? " ? " : timeFormat.format(startTime)) +
                " -> " + (endTime == null ? " ? " : timeFormat.format(endTime)) + " | "
                + scenario.getTitle();
    }
}
