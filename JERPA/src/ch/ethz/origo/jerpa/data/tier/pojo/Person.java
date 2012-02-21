package ch.ethz.origo.jerpa.data.tier.pojo;

import java.sql.Date;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class Person {
    private int personId;
    private boolean changed;
    private boolean added;

    public boolean getAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String surname;

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    private Date dateOfBirth;

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGender(char gender){
        this.gender= "" + gender;
    }

    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String authority;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    private int confirmed;

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    private String authentication;

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    private Date registrationDate;

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
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

        Person person = (Person) o;

        if (confirmed != person.confirmed) return false;
        if (personId != person.personId) return false;
        if (version != person.version) return false;
        if (authentication != null ? !authentication.equals(person.authentication) : person.authentication != null)
            return false;
        if (authority != null ? !authority.equals(person.authority) : person.authority != null) return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(person.dateOfBirth) : person.dateOfBirth != null) return false;
        if (gender != null ? !gender.equals(person.gender) : person.gender != null) return false;
        if (name != null ? !name.equals(person.name) : person.name != null) return false;
        if (note != null ? !note.equals(person.note) : person.note != null) return false;
        if (password != null ? !password.equals(person.password) : person.password != null) return false;
        if (registrationDate != null ? !registrationDate.equals(person.registrationDate) : person.registrationDate != null)
            return false;
        if (surname != null ? !surname.equals(person.surname) : person.surname != null) return false;
        if (username != null ? !username.equals(person.username) : person.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = personId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (authority != null ? authority.hashCode() : 0);
        result = 31 * result + confirmed;
        result = 31 * result + (authentication != null ? authentication.hashCode() : 0);
        result = 31 * result + (registrationDate != null ? registrationDate.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    private Collection<Article> articles;

    public Collection<Article> getArticles() {
        return articles;
    }

    public void setArticles(Collection<Article> articles) {
        this.articles = articles;
    }

    private Collection<ArticlesComments> articleComments;

    public Collection<ArticlesComments> getArticleComments() {
        return articleComments;
    }

    public void setArticleComments(Collection<ArticlesComments> articleComments) {
        this.articleComments = articleComments;
    }

    private Collection<ArticlesSubscribtions> articleSubscribtions;

    public Collection<ArticlesSubscribtions> getArticleSubscribtions() {
        return articleSubscribtions;
    }

    public void setArticleSubscribtions(Collection<ArticlesSubscribtions> articleSubscribtions) {
        this.articleSubscribtions = articleSubscribtions;
    }

    private Collection<Experiment> experimentsAsSubject;

    public Collection<Experiment> getExperimentsAsSubject() {
        return experimentsAsSubject;
    }

    public void setExperimentsAsSubject(Collection<Experiment> experimentsAsSubject) {
        this.experimentsAsSubject = experimentsAsSubject;
    }

    private Collection<Experiment> experimentsAsOwner;

    public Collection<Experiment> getExperimentsAsOwner() {
        return experimentsAsOwner;
    }

    public void setExperimentsAsOwner(Collection<Experiment> experimentsAsOwner) {
        this.experimentsAsOwner = experimentsAsOwner;
    }

    private Collection<GroupPermissionRequest> groupPermissionRequests;

    public Collection<GroupPermissionRequest> getGroupPermissionRequests() {
        return groupPermissionRequests;
    }

    public void setGroupPermissionRequests(Collection<GroupPermissionRequest> groupPermissionRequests) {
        this.groupPermissionRequests = groupPermissionRequests;
    }

    private Collection<HearingImpairmentRel> hearingImpairmentRels;

    public Collection<HearingImpairmentRel> getHearingImpairmentRels() {
        return hearingImpairmentRels;
    }

    public void setHearingImpairmentRels(Collection<HearingImpairmentRel> hearingImpairmentRels) {
        this.hearingImpairmentRels = hearingImpairmentRels;
    }

    private Collection<History> histories;

    public Collection<History> getHistories() {
        return histories;
    }

    public void setHistories(Collection<History> histories) {
        this.histories = histories;
    }

    private ResearchGroup defaultGroup;

    public ResearchGroup getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(ResearchGroup defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    private Collection<PersonOptParamVal> personOptParamVals;

    public Collection<PersonOptParamVal> getPersonOptParamVals() {
        return personOptParamVals;
    }

    public void setPersonOptParamVals(Collection<PersonOptParamVal> personOptParamVals) {
        this.personOptParamVals = personOptParamVals;
    }

    private Collection<ResearchGroup> researchGroups;

    public Collection<ResearchGroup> getResearchGroups() {
        return researchGroups;
    }

    public void setResearchGroups(Collection<ResearchGroup> researchGroups) {
        this.researchGroups = researchGroups;
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

    private Collection<VisualImpairmentRel> visualImpairmentRels;

    public Collection<VisualImpairmentRel> getVisualImpairmentRels() {
        return visualImpairmentRels;
    }

    public void setVisualImpairmentRels(Collection<VisualImpairmentRel> visualImpairmentRels) {
        this.visualImpairmentRels = visualImpairmentRels;
    }

    public String toString(){
        return name + " " + surname + ": " + personId;
    }
}
