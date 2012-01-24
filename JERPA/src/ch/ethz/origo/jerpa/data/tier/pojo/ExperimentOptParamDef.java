package ch.ethz.origo.jerpa.data.tier.pojo;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class ExperimentOptParamDef {
    private int experimentOptParamDefId;

    public int getExperimentOptParamDefId() {
        return experimentOptParamDefId;
    }

    public void setExperimentOptParamDefId(int experimentOptParamDefId) {
        this.experimentOptParamDefId = experimentOptParamDefId;
    }

    private String paramName;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    private String paramDataType;

    public String getParamDataType() {
        return paramDataType;
    }

    public void setParamDataType(String paramDataType) {
        this.paramDataType = paramDataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExperimentOptParamDef that = (ExperimentOptParamDef) o;

        if (experimentOptParamDefId != that.experimentOptParamDefId) return false;
        if (paramDataType != null ? !paramDataType.equals(that.paramDataType) : that.paramDataType != null)
            return false;
        if (paramName != null ? !paramName.equals(that.paramName) : that.paramName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = experimentOptParamDefId;
        result = 31 * result + (paramName != null ? paramName.hashCode() : 0);
        result = 31 * result + (paramDataType != null ? paramDataType.hashCode() : 0);
        return result;
    }

    private Collection<ExperimentOptParamVal> experimentOptParamVals;

    public Collection<ExperimentOptParamVal> getExperimentOptParamVals() {
        return experimentOptParamVals;
    }

    public void setExperimentOptParamVals(Collection<ExperimentOptParamVal> experimentOptParamVals) {
        this.experimentOptParamVals = experimentOptParamVals;
    }
}
