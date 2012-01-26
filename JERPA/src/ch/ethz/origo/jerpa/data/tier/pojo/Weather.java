package ch.ethz.origo.jerpa.data.tier.pojo;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class Weather {
    private int weatherId;
    private boolean changed;

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

        Weather weather = (Weather) o;

        if (version != weather.version) return false;
        if (weatherId != weather.weatherId) return false;
        if (description != null ? !description.equals(weather.description) : weather.description != null) return false;
        if (title != null ? !title.equals(weather.title) : weather.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = weatherId;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    private Collection<Experiment> experiments;

    public Collection<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(Collection<Experiment> experiments) {
        this.experiments = experiments;
    }
}
