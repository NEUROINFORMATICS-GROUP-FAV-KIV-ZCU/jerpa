package ch.ethz.origo.jerpa.data.tier.pojo;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class HardwareUsageRelPK implements Serializable {
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

        HardwareUsageRelPK that = (HardwareUsageRelPK) o;

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
}
