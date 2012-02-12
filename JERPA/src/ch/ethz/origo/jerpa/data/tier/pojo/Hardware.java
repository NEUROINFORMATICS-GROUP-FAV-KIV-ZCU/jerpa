package ch.ethz.origo.jerpa.data.tier.pojo;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class Hardware {
    private int hardwareId;
    private boolean changed;

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(int hardwareId) {
        this.hardwareId = hardwareId;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private long version;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hardware hardware = (Hardware) o;

        if (hardwareId != hardware.hardwareId) return false;
        if (version != hardware.version) return false;
        if (description != null ? !description.equals(hardware.description) : hardware.description != null)
            return false;
        if (title != null ? !title.equals(hardware.title) : hardware.title != null) return false;
        if (type != null ? !type.equals(hardware.type) : hardware.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hardwareId;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    private Set<Experiment> experiments;

    public Set<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(Set<Experiment> experiments) {
        this.experiments = experiments;
    }

    public String toString(){
        return type + " | " + title + " | " + description;
    }
}
