package ch.ethz.origo.jerpa.data.tier.pojo;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class ExperimentOptParamValPK implements Serializable {
    private int experimentId;

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    private int experimentOptParamDefId;

    public int getExperimentOptParamDefId() {
        return experimentOptParamDefId;
    }

    public void setExperimentOptParamDefId(int experimentOptParamDefId) {
        this.experimentOptParamDefId = experimentOptParamDefId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExperimentOptParamValPK that = (ExperimentOptParamValPK) o;

        if (experimentId != that.experimentId) return false;
        if (experimentOptParamDefId != that.experimentOptParamDefId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = experimentId;
        result = 31 * result + experimentOptParamDefId;
        return result;
    }
}
