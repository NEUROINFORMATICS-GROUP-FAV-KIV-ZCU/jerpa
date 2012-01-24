package ch.ethz.origo.jerpa.data.tier.pojo;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class ExperimentOptParamVal {
    private String paramValue;

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

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

        ExperimentOptParamVal that = (ExperimentOptParamVal) o;

        if (experimentId != that.experimentId) return false;
        if (experimentOptParamDefId != that.experimentOptParamDefId) return false;
        if (paramValue != null ? !paramValue.equals(that.paramValue) : that.paramValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = paramValue != null ? paramValue.hashCode() : 0;
        result = 31 * result + experimentId;
        result = 31 * result + experimentOptParamDefId;
        return result;
    }

    private Experiment experiment;

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    private ExperimentOptParamDef experimentOptParamDef;

    public ExperimentOptParamDef getExperimentOptParamDef() {
        return experimentOptParamDef;
    }

    public void setExperimentOptParamDef(ExperimentOptParamDef experimentOptParamDef) {
        this.experimentOptParamDef = experimentOptParamDef;
    }
}
