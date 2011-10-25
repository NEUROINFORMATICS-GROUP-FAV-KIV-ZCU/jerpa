package ch.ethz.origo.jerpa.data.tier.border;

public class ResearchGroup {

	private int researchGroupId;
	private int ownerId;
	private String title;
	private String description;
    private long revision;

	/**
	 * Getter of research group identifier.
	 * 
	 * @return research group identifier
	 */
	public int getResearchGroupId() {
		return researchGroupId;
	}

	/**
	 * Setter of research group identifier.
	 * 
	 * @param researchGroupId research group identifier
	 */
	public void setResearchGroupId(int researchGroupId) {
		this.researchGroupId = researchGroupId;
	}

	/**
	 * Getter if owner person identifier.
	 * 
	 * @return person identifier
	 */
	public int getOwnerId() {
		return ownerId;
	}

	/**
	 * Setter of owner person identifier.
	 * 
	 * @param ownerId person identifier
	 */
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * Getter of research group title.
	 * 
	 * @return research group title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter of research group title.
	 * 
	 * @param title research group title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Getter of research group description.
	 * 
	 * @return String of research group description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter of research group description.
	 * 
	 * @param description String of research group description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }
}
