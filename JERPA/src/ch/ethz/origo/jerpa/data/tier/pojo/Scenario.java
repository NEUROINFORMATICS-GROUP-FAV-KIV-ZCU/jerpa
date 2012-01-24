package ch.ethz.origo.jerpa.data.tier.pojo;

import java.sql.Clob;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class Scenario {
    private int scenarioId;

    public int getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(int scenarioId) {
        this.scenarioId = scenarioId;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private short scenarioLength;

    public short getScenarioLength() {
        return scenarioLength;
    }

    public void setScenarioLength(short scenarioLength) {
        this.scenarioLength = scenarioLength;
    }

    private Clob scenarioXml;

    public Clob getScenarioXml() {
        return scenarioXml;
    }

    public void setScenarioXml(Clob scenarioXml) {
        this.scenarioXml = scenarioXml;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private boolean isPrivate;

    public boolean isIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    private String scenarioName;

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    private String mimetype;

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
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

        Scenario scenario = (Scenario) o;

        if (scenarioId != scenario.scenarioId) return false;
        if (scenarioLength != scenario.scenarioLength) return false;
        if (version != scenario.version) return false;
        if (description != null ? !description.equals(scenario.description) : scenario.description != null)
            return false;
        if (mimetype != null ? !mimetype.equals(scenario.mimetype) : scenario.mimetype != null) return false;
        if (scenarioName != null ? !scenarioName.equals(scenario.scenarioName) : scenario.scenarioName != null)
            return false;
        if (scenarioXml != null ? !scenarioXml.equals(scenario.scenarioXml) : scenario.scenarioXml != null)
            return false;
        if (title != null ? !title.equals(scenario.title) : scenario.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = scenarioId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (int) scenarioLength;
        result = 31 * result + (scenarioXml != null ? scenarioXml.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (scenarioName != null ? scenarioName.hashCode() : 0);
        result = 31 * result + (mimetype != null ? mimetype.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    private Collection<Experiment> experiments;

    public Collection<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(Collection<Experiment> experiments) {
        this.experiments = experiments;
    }

    private Collection<History> histories;

    public Collection<History> getHistories() {
        return histories;
    }

    public void setHistories(Collection<History> histories) {
        this.histories = histories;
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
}
