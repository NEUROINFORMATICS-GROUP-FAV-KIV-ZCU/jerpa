package ch.ethz.origo.jerpa.data.tier.pojo;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class HardwareUsageRel {
    private int hardwareId;

    public int getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(int hardwareId) {
        this.hardwareId = hardwareId;
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

        HardwareUsageRel that = (HardwareUsageRel) o;

        if (experimentId != that.experimentId) return false;
        if (hardwareId != that.hardwareId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hardwareId;
        result = 31 * result + experimentId;
        return result;
    }

    private Experiment experiment;

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    private Hardware hardware;

    public Hardware getHardware() {
        return hardware;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }
}
