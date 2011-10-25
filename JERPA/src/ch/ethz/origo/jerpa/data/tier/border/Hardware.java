package ch.ethz.origo.jerpa.data.tier.border;

/**
 * Class for gathering few important information about hardware.
 *
 * @author: Petr Miko (miko.petr at gmail.com)
 */
public class Hardware {

    private String description;
    private int hardwareId;
    private long revision;
    private String title;
    private String type;

    /**
     * Getter of HW description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter of HW description.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter of HW identifier.
     *
     * @return HW identifier
     */
    public int getHardwareId() {
        return hardwareId;
    }

    /**
     * Setter of HW identifier.
     *
     * @param hardwareId
     */
    public void setHardwareId(int hardwareId) {
        this.hardwareId = hardwareId;
    }

    /**
     * Getter of revision number (oracle scn).
     * @return revision number
     */
    public long getRevision() {
        return revision;
    }

    /**
     * Setter of revision number (oracle scn).
     *
     * @param revision revision number
     */
    public void setRevision(long revision) {
        this.revision = revision;
    }

    /**
     * Getter of HW title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter of HW title.
     *
     * @param title HW title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter of HW type.
     *
     * @return HW type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter of HW type.
     *
     * @param type HW type
     */
    public void setType(String type) {
        this.type = type;
    }
}
