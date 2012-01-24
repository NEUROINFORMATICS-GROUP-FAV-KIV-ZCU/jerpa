package ch.ethz.origo.jerpa.data.tier.pojo;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class CoexperimenterRelPK implements Serializable {
    private int personId;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    private int experimentId;

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoexperimenterRelPK that = (CoexperimenterRelPK) o;

        if (experimentId != that.experimentId) return false;
        if (personId != that.personId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = personId;
        result = 31 * result + experimentId;
        return result;
    }
}
