package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.Averages;

/**
 * Panel akc� pro pou�it� ve t��d�ch <code>MeanPanel1_3</code> a
 * <code>MeanPanel2_2</code>.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 * 
 */
@SuppressWarnings("serial")
final class EpochAddingAP extends ActionsPanel {
	/**
	 * Popisek zobrazen� d�v�ryhodnosti pr�m�ru.
	 */
	private static final String TRUSTFUL_PREFIX = "| Trustful: ";
	/**
	 * Ovl�dac� prvek pro p�id�v�n�/odeb�r�n� epochy �i n-tice epoch do/z pr�m�ru
	 * kan�lu.
	 */
	private JCheckBox addThisEpoch;
	/**
	 * Zobrazen� d�v�ryhodnosti pr�m�ru.
	 */
	private JLabel trustful;

	/**
	 * Vytv��� instance panelu akc�.
	 * 
	 * @param meanPanel
	 *          Pr�m�rovac� panel, ve kter�m je panel akc� um�st�n.
	 */
	EpochAddingAP(MeanPanel meanPanel) {
		super(meanPanel);
		layoutInit();
		addThisEpoch = new JCheckBox("Add into average");
		addThisEpoch.addActionListener(new ALaddThisEpochJCB());
		trustful = new JLabel("UNKNOWN");
		trustful.setForeground(Color.BLACK);
		this.add(addThisEpoch);
		this.add(new JLabel(TRUSTFUL_PREFIX));
		this.add(trustful);
	}

	/**
	 * Inicializace layoutu panelu akc�.
	 */
	private void layoutInit() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	/**
	 * Nastavuje zda se m� na panelu zobrazit, �e je aktu�ln� epocha za�azena do
	 * pr�m�ru.
	 * 
	 * @param selected
	 *          <code>true</code>, pokud je aktu�ln� epocha za�azena do
	 *          pr�m�ru, jinak <code>false</code>.
	 */
	@Override
	void setEpochSelected(boolean selected) {
		addThisEpoch.setSelected(selected);
	}

	/**
	 * Povolen�/zakaz�n� ovl�dac�ch prvk� pohled�.
	 * 
	 * @param enabled
	 *          <i>true</i> pro povolen�, <i>false</i> pro zak�z�n� ovl�dac�ch
	 *          prvk�.
	 */
	@Override
	void setEnabledOperatingElements(boolean enabled) {
		addThisEpoch.setEnabled(enabled);
	}

	/**
	 * Nastavuje, jak� informace o d�v�ryhodnosti pr�m�ru kan�lu se m� zobrazit.
	 * 
	 * @param trustful
	 *          N�kter� z konstant t��dy Averages.
	 */
	@Override
	void setTrustFul(int trustful) {
		switch (trustful) {
		case Averages.UNKNOWN:
			this.trustful.setText("UNKNOWN");
			this.trustful.setForeground(Color.BLACK);
			break;
		case Averages.TRUSTFUL:
			this.trustful.setText("TRUSTFUL");
			this.trustful.setForeground(Color.GREEN);
			break;
		case Averages.NOT_TRUSTFUL:
			this.trustful.setText("NOT TRUSTFUL");
			this.trustful.setForeground(Color.RED);
			break;
		}

	}

	/**
	 * ActionListener atributu <i>includeThisEpochJCB</i>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALaddThisEpochJCB implements ActionListener {
		/**
		 * Podle toho, zda do�lo k ozna�en� nebo odzna�en� zahrne pr�v� zobrazenou
		 * epochu do pr�m�ru nebo ji z n�j vyjme (zahrnut� �i vyjmut� neprov�d�
		 * p��mo, ale vol� p��slu�n� metody atributu
		 * <code>averagingWindowProvider</code>).
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			meanPanel.averagingWindowProvider.setEpochInChannelSelected(addThisEpoch
					.isSelected(), meanPanel.channelOrderInInputFile);
		}
	}
}
