package ch.ethz.origo.jerpa.data.tier.pojo;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class PersonOptParamVal {
    private String paramValue;

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    private int personId;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    private int personOptParamDefId;

    public int getPersonOptParamDefId() {
        return personOptParamDefId;
    }

    public void setPersonOptParamDefId(int personOptParamDefId) {
        this.personOptParamDefId = personOptParamDefId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonOptParamVal that = (PersonOptParamVal) o;

        if (personId != that.personId) return false;
        if (personOptParamDefId != that.personOptParamDefId) return false;
        if (paramValue != null ? !paramValue.equals(that.paramValue) : that.paramValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = paramValue != null ? paramValue.hashCode() : 0;
        result = 31 * result + personId;
        result = 31 * result + personOptParamDefId;
        return result;
    }

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private PersonOptParamDef personOptParamDef;

    public PersonOptParamDef getPersonOptParamDef() {
        return personOptParamDef;
    }

    public void setPersonOptParamDef(PersonOptParamDef personOptParamDef) {
        this.personOptParamDef = personOptParamDef;
    }
}
