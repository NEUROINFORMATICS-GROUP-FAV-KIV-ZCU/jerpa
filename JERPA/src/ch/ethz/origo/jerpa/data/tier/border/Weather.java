package ch.ethz.origo.jerpa.data.tier.border;

public class Weather {

	private int weatherId;
	private String description;
	private String title;
    private long revision;

	/**
	 * Getter of weather identifier.
	 * 
	 * @return weather identifier
	 */
	public int getWeatherId() {
		return weatherId;
	}

	/**
	 * Setter of weather id.
	 * 
	 * @param weatherId weather id
	 */
	public void setWeatherId(int weatherId) {
		this.weatherId = weatherId;
	}

	/**
	 * Getter of weather description.
	 * 
	 * @return weather description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter of weather description.
	 * 
	 * @param description weather description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Getter of weather title.
	 * 
	 * @return weather title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter of weather title.
	 * 
	 * @param title weather title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }
}
