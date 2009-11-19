package ch.ethz.origo.jerpa.data;

import java.util.Arrays;

/**
 * T��da reprezentuj�c� epochu nebo marker.
 * 
 * @author Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * @see Epoch
 * @see Comparable
 */
public class Epoch implements Comparable<Epoch> {

	/**
	 * Pozice st�edu epochy ve framech.
	 */
	private long position;

	/**
	 * TODO
	 */
	private boolean[] selected;

	/**
	 * TODO
	 */
	private int[] weights;

	/**
	 * D�lka zna�ky (ve framech).<br/> Nen� vzd�lenost od lev�ho po prav� okraj
	 * epochy(!), ale nejsp�e d�lka trv�n� ud�losti.
	 */
	private int length;

	/**
	 * Typ markeru.
	 */
	private String type;

	/**
	 * ��slo kan�lu, kter�mu zna�ka n�le��. 0 - zna�ka pat�� v�em kan�l�m.
	 */
	private int channelNumber;

	/**
	 * Datum. Tato polo�ka je vyhodnocov�na pouze v p��pad�, �e typ markeru je
	 * "New Segment".
	 */
	private String date;

	/**
	 * Popis markeru.
	 */
	private String description;

	/**
	 * Vytvo�� instanci t��dy.
	 * 
	 * @param numberOfChannels -
	 *          po�et sign�l�.
	 */
	public Epoch(int numberOfChannels) {
		position = -1;
		selected = new boolean[numberOfChannels];
		weights = new int[numberOfChannels];

		initArrays();
	}

	/**
	 * Vytvo�� instanci t��dy.
	 * 
	 * @param numberOfChannels -
	 *          po�et sign�l�.
	 * @param position -
	 *          pozice epochy.
	 */
	public Epoch(int numberOfChannels, long position) {
		this.position = position;
		selected = new boolean[numberOfChannels];
		weights = new int[numberOfChannels];

		initArrays();
	}

	private void initArrays() {
		Arrays.fill(selected, false);
		Arrays.fill(weights, 1);
	}

	public boolean isEpochSelected(int channelOrderInInputFile) {
		return selected[channelOrderInInputFile];
	}

	public void setEpochSelected(boolean selected, int channelOrderInInputFile) {
		this.selected[channelOrderInInputFile] = selected;
	}

	public int getEpochWeight(int channelOrderInInputFile) {
		return weights[channelOrderInInputFile];
	}

	public void setEpochWeight(int weight, int channelOrderInInputFile) {
		weights[channelOrderInInputFile] = weight;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the frame
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * @param position
	 */
	public void setPosition(long position) {
		this.position = position;
	}

	/**
	 * @return the selected
	 */
	public boolean[] getSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *          the selected to set
	 */
	public void setSelected(boolean[] selected) {
		this.selected = selected;
	}

	/**
	 * @return the weights
	 */
	public int[] getWeights() {
		return weights;
	}

	/**
	 * @param weights
	 *          the weights to set
	 */
	public void setWeights(int[] weights) {
		this.weights = weights;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getChannelNumber() {
		return channelNumber;
	}

	public void setChannelNumber(int channelNumber) {
		this.channelNumber = channelNumber;
	}

	/**
	 * Slou�� k �azen� epoch podle um�st�n�.
	 */
	@Override
	public int compareTo(Epoch epoch) {
		if (this.equals(epoch)) {
			return 0;
		}
		return (int) (this.position - epoch.getPosition());
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
