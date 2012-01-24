package ch.ethz.origo.jerpa.data.tier.pojo;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class HearingImpairmentRelPK implements Serializable {
    private int personId;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    private int hearingImpairmentId;

    public int getHearingImpairmentId() {
        return hearingImpairmentId;
    }

    public void setHearingImpairmentId(int hearingImpairmentId) {
        this.hearingImpairmentId = hearingImpairmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HearingImpairmentRelPK that = (HearingImpairmentRelPK) o;

        if (hearingImpairmentId != that.hearingImpairmentId) return false;
        if (personId != that.personId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = personId;
        result = 31 * result + hearingImpairmentId;
        return result;
    }
}
