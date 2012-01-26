package ch.ethz.origo.jerpa.data.tier.pojo;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class GroupPermissionRequest {
    private int requestId;
    private boolean changed;

    public boolean getChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    private String requestedPermission;

    public String getRequestedPermission() {
        return requestedPermission;
    }

    public void setRequestedPermission(String requestedPermission) {
        this.requestedPermission = requestedPermission;
    }

    private long granted;

    public long getGranted() {
        return granted;
    }

    public void setGranted(long granted) {
        this.granted = granted;
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

        GroupPermissionRequest that = (GroupPermissionRequest) o;

        if (granted != that.granted) return false;
        if (requestId != that.requestId) return false;
        if (version != that.version) return false;
        if (requestedPermission != null ? !requestedPermission.equals(that.requestedPermission) : that.requestedPermission != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = requestId;
        result = 31 * result + (requestedPermission != null ? requestedPermission.hashCode() : 0);
        result = 31 * result + (int) (granted ^ (granted >>> 32));
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private ResearchGroup researchGroup;

    public ResearchGroup getResearchGroup() {
        return researchGroup;
    }

    public void setResearchGroup(ResearchGroup researchGroup) {
        this.researchGroup = researchGroup;
    }
}
