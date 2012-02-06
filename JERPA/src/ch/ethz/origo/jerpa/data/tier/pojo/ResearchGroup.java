package ch.ethz.origo.jerpa.data.tier.pojo;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class ResearchGroup {
    private int researchGroupId;
    private boolean changed;

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getResearchGroupId() {
        return researchGroupId;
    }

    public void setResearchGroupId(int researchGroupId) {
        this.researchGroupId = researchGroupId;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

        ResearchGroup that = (ResearchGroup) o;

        if (researchGroupId != that.researchGroupId) return false;
        if (version != that.version) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = researchGroupId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    private Collection<Article> articleses;

    public Collection<Article> getArticleses() {
        return articleses;
    }

    public void setArticleses(Collection<Article> articleses) {
        this.articleses = articleses;
    }

    private Collection<Experiment> experiments;

    public Collection<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(Collection<Experiment> experiments) {
        this.experiments = experiments;
    }

    private Collection<GroupPermissionRequest> groupPermissionRequests;

    public Collection<GroupPermissionRequest> getGroupPermissionRequests() {
        return groupPermissionRequests;
    }

    public void setGroupPermissionRequests(Collection<GroupPermissionRequest> groupPermissionRequests) {
        this.groupPermissionRequests = groupPermissionRequests;
    }

    private Collection<Person> peopleWithThisAsDefault;

    public Collection<Person> getPeopleWithThisAsDefault() {
        return peopleWithThisAsDefault;
    }

    public void setPeopleWithThisAsDefault(Collection<Person> peopleWithThisAsDefault) {
        this.peopleWithThisAsDefault = peopleWithThisAsDefault;
    }

    private Person owner;

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    private Collection<ResearchGroupMembership> researchGroupMemberships;

    public Collection<ResearchGroupMembership> getResearchGroupMemberships() {
        return researchGroupMemberships;
    }

    public void setResearchGroupMemberships(Collection<ResearchGroupMembership> researchGroupMemberships) {
        this.researchGroupMemberships = researchGroupMemberships;
    }

    private Collection<Scenario> scenarios;

    public Collection<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(Collection<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    public String toString(){
        return getTitle() + " (" + researchGroupId + ")";
    }
}
