package ch.ethz.origo.jerpa.data.tier.pojo;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class PersonOptParamValPK implements Serializable {
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

        PersonOptParamValPK that = (PersonOptParamValPK) o;

        if (personId != that.personId) return false;
        if (personOptParamDefId != that.personOptParamDefId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = personId;
        result = 31 * result + personOptParamDefId;
        return result;
    }
}
