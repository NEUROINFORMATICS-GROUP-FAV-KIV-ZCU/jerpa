package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;

/**
 * Popup menu komponenty <i>SignalViewer</i>. Umo��uje nastavovat hodnoty
 * n�kter�ch jejich atribut�.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 */
@SuppressWarnings("serial")
public final class SignalViewerPopupMenu extends JPopupMenu {
	/**
	 * <i>SignalViewer</i>, jeho� popup menu t��da realizuje.
	 */
	private SignalViewerPanel signalViewer;
	/**
	 * Polo�ka menu, kter� umo��uje nastaven� zp�sobu zobrazen� sign�lu.
	 */
	private JMenu viewTypeJM;
	/**
	 * Zobrazen� sign�lu jako funk�n�ch hodnot bez interpolace pr�b�hu sign�lu.
	 */
	private JRadioButton functionalValuesJRB;
	/**
	 * Zobrazen� sign�lu jako interpolace pr�b�hu sign�lu bez zv�razn�n� funk�n�ch
	 * hodnot.
	 */
	private JRadioButton interpolationJRB;
	/**
	 * Zobrazen� sign�lu jako interpolace pr�b�hu sign�lu se zv�razn�n�m funk�n�ch
	 * hodnot.
	 */
	private JRadioButton functionalValuesAndInterpolationJRB;
	/**
	 * Otev�r� mo�nost p�ibl�en�/odd�len� sign�lu.
	 */
	private JMenu zoomJM;

	/**
	 * Vytv��� nov� popup menu sv�zan� se <i>SignalViewer</i>em p�edan�m jako
	 * parametr <b>signalViewer</b>.
	 * 
	 * @param signalViewer
	 *          <i>SignalViewer</i>, k n�mu� se bude popup menu vztahovat.
	 */
	public SignalViewerPopupMenu(SignalViewerPanel signalViewer) {
		super();
		this.signalViewer = signalViewer;
		createInside();
		addActionListeners();
	}

	/**
	 * Vytv��� vnit�ek popup menu.
	 */
	private void createInside() {
		viewTypeJM = new JMenu("View Type");
		functionalValuesJRB = new JRadioButton("Functional Values");
		interpolationJRB = new JRadioButton("Interpolation");
		functionalValuesAndInterpolationJRB = new JRadioButton(
				"Functional Values And Interpolation");

		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(functionalValuesJRB);
		radioGroup.add(interpolationJRB);
		radioGroup.add(functionalValuesAndInterpolationJRB);

		viewTypeJM.add(functionalValuesJRB);
		viewTypeJM.add(interpolationJRB);
		viewTypeJM.add(functionalValuesAndInterpolationJRB);

		zoomJM = new JMenu("Zoom");

		this.add(viewTypeJM);
		this.add(zoomJM);

		setSelectedViewType();
	}

	/**
	 * Podle zp�sobu zobrazen� sign�lu v <i>SignalViewer</i>u natavuje, kter�
	 * zp�sob zobrazen� m� b�t ozna�en�.
	 */
	public void setSelectedViewType() {
		switch (signalViewer.getModeOfRepresentation()) {
		case SignalViewerPanel.FUNCTIONAL_VALUES:
			functionalValuesJRB.setSelected(true);
			break;
		case SignalViewerPanel.INTERPOLATION:
			interpolationJRB.setSelected(true);
			break;
		default:
			functionalValuesAndInterpolationJRB.setSelected(true);
		}
	}

	/**
	 * P�i�azen� actionListener� polo�k�m popup menu.
	 */
	private void addActionListeners() {
		functionalValuesJRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				signalViewer.setModeOfRepresentation(SignalViewerPanel.FUNCTIONAL_VALUES);
				setVisible(false);
			}
		});

		interpolationJRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				signalViewer.setModeOfRepresentation(SignalViewerPanel.INTERPOLATION);
				setVisible(false);
			}
		});

		functionalValuesAndInterpolationJRB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				signalViewer
						.setModeOfRepresentation(SignalViewerPanel.FUNCTIONAL_VALUES_AND_INTERPOLATION);
				setVisible(false);
			}
		});
	}
}
