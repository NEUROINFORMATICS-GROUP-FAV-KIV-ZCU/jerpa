package ch.ethz.origo.jerpa.data.tier.pojo;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class VisualImpairment {
    private int visualImpairmentId;
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

    public int getVisualImpairmentId() {
        return visualImpairmentId;
    }

    public void setVisualImpairmentId(int visualImpairmentId) {
        this.visualImpairmentId = visualImpairmentId;
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

        VisualImpairment that = (VisualImpairment) o;

        if (visualImpairmentId != that.visualImpairmentId) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = visualImpairmentId;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    private Collection<VisualImpairmentRel> visualImpairmentRels;

    public Collection<VisualImpairmentRel> getVisualImpairmentRels() {
        return visualImpairmentRels;
    }

    public void setVisualImpairmentRels(Collection<VisualImpairmentRel> visualImpairmentRels) {
        this.visualImpairmentRels = visualImpairmentRels;
    }
}
