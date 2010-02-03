package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.averaging.Averages;

/**
 * Panel akc� pou��van� v pr�m�rovac�m panelu <code>GroupEpochsMeanPanel</code>.
 * 
 * @author Tomas Rondik
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/31/2010)
 * @since 0.1.0 (1/31/2010)
 */
@SuppressWarnings("serial")
final class GroupAddingAP extends ActionsPanel {
	/**
	 * Popisek k zobrazen�, zda aktu�ln� skupina epoch je �i nen� sou��st�
	 * pr�m�ru.
	 */
	private static final String ADDED_PREFIX = "| Added in average: ";
	/**
	 * Popisek zobrazen� d�v�ryhodnosti pr�m�ru.
	 */
	private static final String TRUSTFUL_PREFIX = "| Trustful: ";
	/**
	 * Konstanta popisuj�c�, �e aktu�ln� skupina epoch je za�azena do pr�m�ru.
	 */
	private static final String ADDED = "YES";
	/**
	 * Konstanta popisuj�c�, �e aktu�ln� skupina epoch nen� za�azena do pr�m�ru.
	 */
	private static final String NOT_ADDED = "NO";
	/**
	 * Ovl�dac� prvek pro za�azen�/odebr�n� kan�lu z hromadn�ho
	 * za�azov�n�/odeb�r�n� epoch.
	 */
	private JCheckBox applyChangesJCB;
	/**
	 * Zobrazen�, zda aktu�ln� skupina epoch je �i nen� sou��st� pr�m�ru.
	 */
	private JLabel added;
	/**
	 * Zobrazen� d�v�ryhodnosti pr�m�ru.
	 */
	private JLabel trustful;

	/**
	 * Vytv��� nov� panel akc�.
	 * 
	 * @param meanPanel
	 *          Pr�m�rovac� panel, ve kter�m je panel akc� um�st�n.
	 */
	GroupAddingAP(MeanPanel meanPanel) {
		super(meanPanel);
		layoutInit();
		applyChangesJCB = new JCheckBox("Apply changes");
		applyChangesJCB.addActionListener(new ALapplyChangesJCB());
		added = new JLabel(NOT_ADDED);
		added.setForeground(Color.RED);
		trustful = new JLabel("UNKNOWN");
		trustful.setForeground(Color.BLACK);
		this.add(applyChangesJCB);
		this.add(new JLabel(ADDED_PREFIX));
		this.add(added);
		this.add(new JLabel(TRUSTFUL_PREFIX));
		this.add(trustful);
	}

	/**
	 * Inicializace layoutu pr�m�rovac�ho panelu.
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
		if (selected) {
			added.setText(ADDED);
			added.setForeground(Color.GREEN);
		} else {
			added.setText(NOT_ADDED);
			added.setForeground(Color.RED);
		}
	}

	/**
	 * Nastavuje, zda se kan�l ��astn� hromadn�ho za�azov�n�/odeb�r�n� epoch do/z
	 * pr�m�r�.
	 */
	@Override
	void setApplyChanges(boolean applyChanges) {
		applyChangesJCB.setSelected(applyChanges);
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
		applyChangesJCB.setEnabled(enabled);
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
	 * ActionListener atributu <code>applyChangesJCB</code>.
	 * 
	 * @author Tom� �ond�k
	 */
	private class ALapplyChangesJCB implements ActionListener {
		/**
		 * Nastavuje, zda se kan�l ��astn� hromadn�ho za�azov�n�/odeb�r�n� epoch
		 * do/z pr�m�r�.
		 * 
		 * @param event
		 *          Nastal� ud�lost.
		 */
		@Override
		public void actionPerformed(ActionEvent event) {
			meanPanel.averagingWindowProvider.setApplyChanges(applyChangesJCB
					.isSelected(), meanPanel.channelOrderInInputFile);
		}
	}
}
