package ch.ethz.origo.jerpa.data.tier.pojo;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class HearingImpairment {
    private int hearingImpairmentId;
    private boolean changed;

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getHearingImpairmentId() {
        return hearingImpairmentId;
    }

    public void setHearingImpairmentId(int hearingImpairmentId) {
        this.hearingImpairmentId = hearingImpairmentId;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HearingImpairment that = (HearingImpairment) o;

        if (hearingImpairmentId != that.hearingImpairmentId) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hearingImpairmentId;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    private Collection<HearingImpairmentRel> hearingImpairmentRels;

    public Collection<HearingImpairmentRel> getHearingImpairmentRels() {
        return hearingImpairmentRels;
    }

    public void setHearingImpairmentRels(Collection<HearingImpairmentRel> hearingImpairmentRels) {
        this.hearingImpairmentRels = hearingImpairmentRels;
    }
}
