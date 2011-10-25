package ch.ethz.origo.jerpa.data.tier.border;

import java.sql.Timestamp;

public class Experiment {

	private int experimentId;
	private int ownerId;
	private int subjectPersonId;
	private int scenarioId;
	private int weatherId;
	private int researchGroupId;
	private Timestamp startTime;
	private Timestamp endTime;
	private int temperature;
	private String weatherNote;
	private int privateFlag;
    private long revision;

	// this is important for table view
	private String title;

	public Experiment() {}

	public int getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public int getSubjectPersonId() {
		return subjectPersonId;
	}

	public void setSubjectPersonId(int subjectPersonId) {
		this.subjectPersonId = subjectPersonId;
	}

	public int getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(int scenarioId) {
		this.scenarioId = scenarioId;
	}

	public int getWeatherId() {
		return weatherId;
	}

	public void setWeatherId(int weatherId) {
		this.weatherId = weatherId;
	}

	public int getResearchGroupId() {
		return researchGroupId;
	}

	public void setResearchGroupId(int researchGroupId) {
		this.researchGroupId = researchGroupId;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public String getWeatherNote() {
		return weatherNote;
	}

	public void setWeatherNote(String weatherNote) {
		this.weatherNote = weatherNote;
	}

	public int getPrivateFlag() {
		return privateFlag;
	}

	public void setPrivateFlag(int privateFlag) {
		this.privateFlag = privateFlag;
	}

	public String getTitle() {
		return title;
	}

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
