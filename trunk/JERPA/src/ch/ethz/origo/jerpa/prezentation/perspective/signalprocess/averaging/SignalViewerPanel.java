package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Vykresluje funk�n� hodnoty jedn� epochy jednoho sign�lu. Umo��uje �etn�
 * nastaven�, d�ky �emu� je pou��v�n jak p�i pr�ci s pr�m�ry, tak p�i jejich
 * exportu. Dok�e zv�raz�ovat pozici my�i a pomoc� sv� priv�tn� t��dy
 * <code>CommunicationProvider</code> odes�lat informace o pozici my�i
 * registrovan�m poslucha��m.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 11/18/09 
 * @since 0.1.0 (11/18/09)
 */
@SuppressWarnings("serial")
public final class SignalViewerPanel extends JPanel implements MouseListener,
		MouseMotionListener {
	/**
	 * Priv�tn� t��da slou��c� pro komunikaci s ostatn�mi
	 * <code>SignalViewer</code>y, pop�. jin�mi poslucha�i, kter�m jsou
	 * odes�lan� informace u�ite�n�.
	 * 
	 * @author Tom� �ond�k
	 */
	private class CommunicationProvider extends Observable implements Observer {
		/**
		 * Metoda pro pos�l�n� zpr�v registrovan�m poslucha��m.
		 * 
		 * @param object
		 *          Zpr�va.
		 */
		public void sendObjectToObservers(Object object) {
			this.setChanged();
			this.notifyObservers(object);
		}

		/**
		 * Metoda pro p��jem zpr�v od ostatn�ch <code>SignalViewer</code>�, pop�.
		 * jin�ch vys�la��, kter� si <code>SignalViewer</code> zaregistrovali.
		 */
		@Override
		public void update(Observable arg0, Object arg1) {
			/*
			 * Pokud je zpr�va informac� o um�st�n� my�i.
			 */
			if (arg1 instanceof Point) {
				mouseMoved = true;
				Point value = (Point) arg1;
				mousePositionX = value.x;
				mousePositionY = value.y;
				markMousePosition();
				repaint();
			}
			// Pokud je zpr�va komunika�n� konstantou.
			else if (arg1 instanceof Integer) {
				int value = ((Integer) arg1).intValue();

				if (value == SHOW_MOUSE_MARK) {
					mouseOverSignalViewer = true;
				} else if (value == HIDE_MOUSE_MARK) {
					repaint();
					mouseOverSignalViewer = false;
				}
			}
		}
	}

	/**
	 * Reference na objekt priv�tn� t��dy umo��uj�c� komunikaci pomoc� n�vrhov�ho
	 * vzoru Observer s ostatn�mi <code>SignalViewer</code>y.
	 */
	private CommunicationProvider communicationProvider;
	/**
	 * Reference na vyskakovac� menu <code>SignalViewer</code>u.
	 */
	private SignalViewerPopupMenu popupMenu;
	/**
	 * Okraje oblasti, ve kter� se vykresluje pr�b�h sign�lu.
	 */
	public static final Insets signalMargin = new Insets(0, 0, 0, 0);
	/**
	 * Pole n�zv� konstant zp�sobu zobrazen� pr�b�hu sign�lu.
	 */
	public static final String[] MODE_OF_REPRESENTATION_TYPES = {
			"Functional values", "Interpolation", "Values and interpolation" };
	/**
	 * Konstanta popisuje zobrazen� funk�n�ch hodnot bez interpolace.
	 */
	public static final int FUNCTIONAL_VALUES = 0;
	/**
	 * Konstanta popisuje zobrazen� interpolace funk�n�ch hodnot bez zv�razn�n�
	 * funk�n�ch hodnot.
	 */
	public static final int INTERPOLATION = 1;
	/**
	 * Konstanta popisuje zobrazen� funk�n�ch hodnot s jejich interpolac�.
	 */
	public static final int FUNCTIONAL_VALUES_AND_INTERPOLATION = 2;
	/**
	 * Komunika�n� konstanta - m� se zv�raz�ovat pozice my�i.
	 */
	public static final int SHOW_MOUSE_MARK = 1;
	/**
	 * Komunika�n� konstanta - nem� se zv�raz�ovat pozice my�i.
	 */
	public static final int HIDE_MOUSE_MARK = 0;
	/**
	 * Defaultn� hodnota p�ibl�en�/odd�len� zobrazovan�ho sign�lu.
	 */
	public static final float ZOOM_Y_ORIGINAL = 0.2f;
	/**
	 * Textov� informace, kter� se objev� v <code>SignalViewer</code>u, pokud
	 * nejsou zn�ma data, kter� se maj� vykreslit.
	 */
	private static final String UNKNOWN_FUNCTIONAL_VALUES = "Values for this channel are not known";
	/**
	 * Velikost chybov�ho textu.
	 */
	private static final int ERROR_TEXT_SIZE = 12;
	/**
	 * Vzd�lennost chybov�ho textu od okraj� <i>SignalViewer<i>u v obou os�ch.
	 */
	private static final int ERROR_TEXT_DISTANCE = 8;
	/**
	 * Zp�sob zobrazen� funk�n�ch hodnot. Nab�v� n�kter� z hodnot konstant
	 * popisuj�c�ch zobrazen� funk�n�ch hodnot (nap�. konstanty <i>INTERPOLATION</i>
	 * a <i>FUNCTIONAL_VALUES</i>).
	 */
	private int modeOfRepresentation;
	/**
	 * ���ka panelu zobrazuj�c�ho sign�l.
	 */
	private float canvasWidth;
	/**
	 * V�ka panelu zobrazuj�c�ho sign�l.
	 */
	private float canvasHeight;
	/**
	 * Pozice po��tku soustavy sou�adnic ud�van� v po�tu fram� od po��tku
	 * vykreslov�n� sign�lu.
	 */
	private int coordinateBasicOriginFrame;
	/**
	 * Hodnoty jednoho sign�lu v pr�v� zobrazovan� epo�e - funk�n� hodnoty, kter�
	 * jsou zobrazov�ny.
	 */
	private float[] values;
	/**
	 * Barva pozad� zobrazovac� komponenty.
	 */
	private Color canvasColor;
	/**
	 * Barva interpolace sign�lu.
	 */
	private Color interpolationColor;
	/**
	 * Barva os.
	 */
	private Color axisColor;
	/**
	 * Barva zobrazen� funk�n�ch hodnot.
	 */
	private Color functionalValuesColor;
	/**
	 * Barva zv�razn�n� pozice my�i.
	 */
	private Color mousePositionColor;
	/**
	 * Komponenta, na kterou je SignalViewer um�st�n.
	 */
	private JComponent background;
	/**
	 * Kresl�c� komponenta.
	 */
	private Graphics2D g2;
	/**
	 * P�ibl�en� sign�lu v ose funk�n�ch hodnot.
	 */
	private float zoomY;
	/**
	 * Booleovsk� hodnota ��kaj�c�, zda se m� vykreslovat osa �asu.
	 */
	private boolean showXAxis;
	/**
	 * Booleovsk� hodnota ��kaj�c�, zda se m� vykreslovat osa funk�n�ch hodnot.
	 */
	private boolean showYAxis;
	/**
	 * Booleovsk� hodnota ��kaj�c�, zda maj� b�t zobrazeny hodnoty na �asov� ose.
	 */
	private boolean showXValues;
	/**
	 * Booleovsk� hodnota ��kaj�c�, zda maj� b�t zobrazeny hodnoty na ose
	 * funk�n�ch hodnot.
	 */
	private boolean showYValues;
	/**
	 * ��k�, jestli se kurzor my�i nach�z� nad <code>SignalViewer</code>em.
	 */
	private boolean mouseOverSignalViewer;
	/**
	 * ��k�, jestli se pozice kurzoru my�i od jej�ho posledn�ho sejmut� zm�nila.
	 * Sni�uje v�po�etn� n�ro�nost zv�razn�n� pozice my�i.
	 */
	private boolean mouseMoved;
	/**
	 * Hodnota p�edchoz� pozice my�i p�i zobrazen� interpolace sign�lu.
	 */
	private float oldMousePositionValue;
	/**
	 * P�edchoz� pozice v atributu <code>values</code>, ze kter� byla
	 * vypo��t�v�na aproximovan� funk�n� hodnota sign�lu v m�st� pozice my�i.
	 */
	private int oldValuesIndex;
	/**
	 * P�edchoz� pozice my�i v ose X.
	 */
	private int oldMousePositionX;
	/**
	 * Aktu�ln� pozice my�i v ose X.
	 */
	private int mousePositionX;
	/**
	 * Aktu�ln� pozice my�i v ose Y.
	 */
	private int mousePositionY;
	/**
	 * ��k�, jestli se m� sign�l zobrazovat invertovan� (tj. zda se m� oto�it sm�r
	 * r�stu osy funk�n�ch hodnot).
	 */
	private boolean invertedSignal;
	/**
	 * ��k�, jestli je povoleno ozna�ov�n� pozice my�i v <i>SignalViewer</i>u.
	 */
	private boolean mouseMarkEnabled;

	/**
	 * Vytv��� nov� zobrazova� sign�lu.
	 * 
	 * @param background
	 *          Komponenta, na kterou je SignalViewer um�st�n.
	 */
	public SignalViewerPanel(JComponent background) {
		super();

		this.background = background;

		canvasColor = Color.WHITE;
		interpolationColor = Color.DARK_GRAY;
		functionalValuesColor = Color.RED;
		axisColor = Color.BLUE;
		mousePositionColor = Color.MAGENTA;

		zoomY = ZOOM_Y_ORIGINAL;
		showXAxis = true;
		showXValues = true;
		showYAxis = true;
		showYValues = true;
		invertedSignal = false;
		modeOfRepresentation = INTERPOLATION;

		popupMenu = new SignalViewerPopupMenu(this);

		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		communicationProvider = new CommunicationProvider();
		mouseOverSignalViewer = false;
		mouseMoved = false;
		mouseMarkEnabled = true;
		oldMousePositionValue = 0f;
		oldValuesIndex = 0;
		oldMousePositionX = 0;
		coordinateBasicOriginFrame = 0;
	}

	/**
	 * P�ekryt� metody <i>paintComponent</i>. Realizuje vykreslov�n� funk�n�ch
	 * hodnot jednoho sign�lu v z�vislosti na �ase. Jako zdroj funk�n�ch hodnot
	 * slou�� atribut <i>values</i>. Pokud nebyly <i>values</i> inicializov�ny,
	 * zobraz� m�sto sign�lu text obsa�en� v konstant�
	 * <i>UNKNOWN_FUNCTIONAL_VALUES</i>. Sign�l vykresluje tak, aby krajn� body
	 * �asov� osy (tj. obecn� 0 a t_max) odpov�daly krajn�m bod�m SignalViewru
	 * (tj. re�ln� 0 a canvasWidth).
	 */
	@Override
	protected void paintComponent(Graphics g) {
		/*
		 * BEGIN - Z�sk�n� floatov�ch hodnot aktu�ln� ���ky a v�ky komponenty
		 * <i>SignalViewer</i>.
		 * 
		 * Pro�: metody <i>getWidth</i> a <i>getHeight</i> t��dy JComponent
		 * vracej� typ <i>int</i>. Pro z�sk�n� p�esn� hodnoty ���ky a v�ky (tj.
		 * re�ln�ho ��sla) je t�eba nejprve z�skat instanci t��idy <i>Dimension</i>
		 * a z n� teprv z�skat parametry ���ky a v�ky jako hodnoty typu <i>double</i>.
		 */
		Dimension size = background.getSize();
		canvasWidth = (float) size.getWidth();
		canvasHeight = (float) size.getHeight();
		/*
		 * END - Z�sk�n� floatov�ch hodnot aktu�ln� ���ky a v�ky komponenty
		 * <i>SignalViewer</i>.
		 */

		g2 = (Graphics2D) g;

		g2.setPaint(canvasColor); // nastaven� barvy pozad� (pl�tna), na kter� se
															// sign�l vykresluje
		g2.fill(new Rectangle2D.Double(0, 0, canvasWidth, canvasHeight));

		if (values != null && values.length > 0) // funk�n� hodnoty vykreslovan�
																							// k�ivky jsou zn�m�
		{
			axisPainting(); // vol�n� metody pro vykreslen� os

			switch (modeOfRepresentation) {
			case INTERPOLATION:
				paintInterpolation();
				break;
			case FUNCTIONAL_VALUES:
				paintFunctionalValues();
				break;
			default:
				paintInterpolation();
				paintFunctionalValues(); // funk�n� hodnoty jsou vykreslov�ny a� po
																	// interpolaci, aby nedo�lo
				// k jejich zakryt� interpola�n� k�ivkou
			}

			if (mouseOverSignalViewer) {
				markMousePosition();
			}

		} else // funk�n� hodnoty vykreslovan� k�ivky nejsou zn�m�
		{
			g2.setColor(interpolationColor);
			g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, ERROR_TEXT_SIZE));
			g2.drawString(UNKNOWN_FUNCTIONAL_VALUES, ERROR_TEXT_SIZE
					+ ERROR_TEXT_DISTANCE, ERROR_TEXT_SIZE + ERROR_TEXT_DISTANCE);
		}
	}

	/**
	 * Metoda slou�� k vykreslen� interpola�n� k�ivky mezi funk�n�mi hodnotami
	 * sign�lu.
	 */
	private void paintInterpolation() {
		g2.setPaint(interpolationColor);

		float step = (canvasWidth - signalMargin.left - signalMargin.right)
				/ (values.length - 1);
		float x0 = signalMargin.left;
		float x1 = step + signalMargin.left;
		float y0 = 0;
		float y1;

		if (invertedSignal) {
			y1 = (values[0] * zoomY + (canvasHeight / 2));
		} else {
			y1 = (-values[0] * zoomY + (canvasHeight / 2));
		}

		for (int i = 1; i < values.length; i++) {

			y0 = y1;

			if (invertedSignal) {
				y1 = (values[i] * zoomY + (canvasHeight / 2));
			} else {
				y1 = (-values[i] * zoomY + (canvasHeight / 2));
			}

			g2.draw(new Line2D.Float(x0, y0, x1, y1));

			x0 = x1;
			x1 += step;
		}
	}

	/**
	 * Metoda slou�� k vykreslen� zv�razn�n� pozice funk�n�ch hodnot.
	 */
	private void paintFunctionalValues() {
		g2.setPaint(functionalValuesColor);

		float step = (canvasWidth - signalMargin.left - signalMargin.right)
				/ (values.length - 1);
		float x0 = signalMargin.left;
		float y0 = 0;

		for (int i = 0; i < values.length; i++) {

			if (invertedSignal) {
				y0 = (values[i] * zoomY + (canvasHeight / 2));
			} else {
				y0 = (-values[i] * zoomY + (canvasHeight / 2));
			}

			if (modeOfRepresentation == FUNCTIONAL_VALUES) {
				g2.fill(new Rectangle2D.Float(x0 - 2, y0 - 2, 4, 4));
			} else {
				g2.draw(new Rectangle2D.Float(x0 - 2, y0 - 2, 4, 4));
			}

			x0 += step;

		}
	}

	/**
	 * Metoda slou�� k zv�razn�n� pozice my�i a vyps�n� aproximovan� funk�n�
	 * hodnoty v m�st� zv�razn�n�.
	 */
	private void interpolationMouseMark() {
		if (mouseMoved
				&& (mousePositionX >= signalMargin.left && mousePositionX <= (canvasWidth - signalMargin.right))) {
			mouseMoved = false;
			float step = (canvasWidth - signalMargin.left - signalMargin.right)
					/ (values.length - 1);
			oldValuesIndex = (int) ((mousePositionX - signalMargin.left) / step);

			
			if (oldValuesIndex >= values.length - 1) {
				oldValuesIndex = values.length - 2;
			}

			oldMousePositionValue = values[oldValuesIndex];

			float x0 = mousePositionX + signalMargin.left
					- ((mousePositionX - signalMargin.left) % step);
			float x1 = x0 + step;
			float increment = (((mousePositionX - x0) / (x1 - x0)) * Math
					.abs(values[oldValuesIndex] - values[oldValuesIndex + 1]));

			if (values[oldValuesIndex] <= values[oldValuesIndex + 1]) {
				oldMousePositionValue += increment;
			} else {
				oldMousePositionValue -= increment;
			}

			oldMousePositionX = mousePositionX;

			this.repaint();
		}

		g2.setColor(mousePositionColor);
		g2.draw(new Line2D.Float(oldMousePositionX, 0, oldMousePositionX,
				canvasHeight));
		g2.drawString(" " + String.valueOf(oldMousePositionValue),
				oldMousePositionX, 20);
	}

	/**
	 * Metoda slou�� k vykreslen� zv�razn�n� pozice my�i pouze ve funk�n�ch
	 * hodnot�ch sign�lu. Z�rove� k tomuto zv�razn�n� zobrazuje skute�n� funk�n�
	 * hodnoty.
	 */
	private void functionalValuesMouseMark() {
		float step = (canvasWidth - signalMargin.left - signalMargin.right)
				/ (values.length - 1);

		if (mouseMoved
				&& (mousePositionX >= signalMargin.left && mousePositionX < (canvasWidth - signalMargin.right))) {
			mouseMoved = false;
			oldValuesIndex = Math.round((mousePositionX - signalMargin.left) / step);
			this.repaint();
		}

		float mouseMarkPositionX = oldValuesIndex * step;
		g2.setColor(mousePositionColor);
		g2.draw(new Line2D.Float(mouseMarkPositionX + signalMargin.left, 0,
				mouseMarkPositionX + signalMargin.left, canvasHeight));
		g2.drawString(" " + String.valueOf(values[oldValuesIndex]),
				mouseMarkPositionX + signalMargin.left, 20);
	}

	/**
	 * Metoda rozhoduje o tom, jak�m zp�sobem bude zv�razn�na pozice my�i.
	 */
	private void markMousePosition() {
		if (mouseMarkEnabled && values != null && values.length > 0 && g2 != null) {
			switch (modeOfRepresentation) {
			case FUNCTIONAL_VALUES:
				functionalValuesMouseMark();
				break;
			default:
				interpolationMouseMark();
			}

		}
	}

	/**
	 * Kresl� osy. Pro jejich vykreslen� pou��v� barvu definovanou v atributu
	 * <i>axisColor</i>. Nenastavuje zp�t p�vodn� barvu, kter� byla nastavena pro
	 * parametr <i>g2</i>.
	 * 
	 * @param g2
	 *          Grafick� kontext.
	 * @param canvasWidth
	 *          ���ka, kterou zab�r� <i>SignalViewer</i>.
	 * @param canvasHeight
	 *          V�ka, kterou zab�r� <i>SignalViewer</i>.
	 */
	private void axisPainting() {
		float step = (canvasWidth - signalMargin.left - signalMargin.right)
				/ (values.length - 1);
		float[] dash = { 3f, 3f };
		g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 1.0f, dash, 0f));
		g2.setPaint(axisColor);

		if (showXAxis) {
			g2.draw(new Line2D.Float(0, canvasHeight / 2, canvasWidth,
					canvasHeight / 2));
		}

		if (showYAxis) {
			g2.draw(new Line2D.Float(step * coordinateBasicOriginFrame, 0, step
					* coordinateBasicOriginFrame, canvasHeight));
		}

		g2.setStroke(new BasicStroke());
	}

	/**
	 * Metoda slou�� k registraci poslucha�� vnit�n� t��dy
	 * <code>CommunicationProvider</code>.
	 * 
	 * @param observer
	 *          Poslucha�.
	 */
	public void registerObserver(Observer observer) {
		communicationProvider.addObserver(observer);
	}

	/**
	 * Nastavuje hodnoty funkce. Po nastaven� nov�ch hodnot zavol� seter metodu
	 * repaint() a sign�l se vykresl�. Pro vykreslen� sign�lu tedy nen� t�eba
	 * volat dal�� metodu.
	 * 
	 * @param values
	 *          pole funk�n�ch hodnot sign�lu v pr�v� zobrazovan� epo�e
	 */
	public void setValues(float[] values) {
		this.values = values;
		this.repaint();

	}

	/**
	 * Vrac� hodnotu ���ky zobrazovac� komponenty. Pou��v� se pro p�izp�soben�
	 * velikosti okoln�ch komponent.
	 * 
	 * @return ���ka zobrazovac� komponenty.
	 */
	public float getCanvasWidth() {
		return canvasWidth;
	}

	/**
	 * Vrac� hodnotu v�ky zobrazovac� komponenty. Pou��v� se pro p�izp�soben�
	 * velikosti okoln�ch komponent.
	 * 
	 * @return v�ka zobrazovac� komponenty.
	 */
	public float getCanvasHeight() {
		return canvasHeight;
	}

	/**
	 * Vrac� hodnotu atributu <code>axisColor</code>.
	 * 
	 * @return Barva os.
	 */
	public Color getAxisColor() {
		return axisColor;
	}

	/**
	 * Nastavuje hodnotu atributu <code>axisColor</code>.
	 * 
	 * @param axisColor
	 *          Nov� barva os.
	 */
	public void setAxisColor(Color axisColor) {
		this.axisColor = axisColor;
		repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>canvasColor</code>.
	 * 
	 * @return Barva pozad�, na kter� jsou funk�n� hodnoty vykreslov�ny.
	 */
	public Color getCanvasColor() {
		return canvasColor;
	}

	/**
	 * Nastavuje hodnotu atributu <code>canvasColor</code>.
	 * 
	 * @param canvasColor
	 *          Nov� barva pozad�, na kter� se vykresluje pr�b�h sign�lu.
	 */
	public void setCanvasColor(Color canvasColor) {
		this.canvasColor = canvasColor;
		repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>interpolationColor</code>.
	 * 
	 * @return Barva k�ivky interpolace funk�n�ch hodnot.
	 */
	public Color getInterpolationColor() {
		return interpolationColor;
	}

	/**
	 * Nastavuje hodnotu atributu <code>interpolationColor</code>.
	 * 
	 * @param interpolationColor
	 *          Nov� barva k�ivky interpolace funk�n�ch hodnot.
	 */
	public void setInterpolationColor(Color interpolationColor) {
		this.interpolationColor = interpolationColor;
		repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>showXAxis</code>.
	 * 
	 * @return Pokud se zobrazuje osa defini�n�ho oboru, pak <code>true</code>,
	 *         jinak <code>false</code>.
	 */
	public boolean isShowXAxis() {
		return showXAxis;
	}

	/**
	 * Nastavuje hodnotu atributu <code>showXAxis</code>.
	 * 
	 * @param showXAxis
	 *          <code>true</code>, pokud m� b�t zobrazena osa defini�n�ho
	 *          oboru, jinak <code>false</code>.
	 */
	public void setShowXAxis(boolean showXAxis) {
		this.showXAxis = showXAxis;
		repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>showXValues</code>.
	 * 
	 * @return Pokud se zobrazuje popis osy defini�n�ho oboru, pak
	 *         <code>true</code>, jinak <code>false</code>.
	 */
	public boolean isShowXValues() {
		return showXValues;
	}

	/**
	 * Nastavuje hodnotu atributu <code>showXValues</code>.
	 * 
	 * @param showXValues
	 *          <code>true</code>, pokud m� b�t zobrazen popis osy defini�n�ho
	 *          oboru, jinak <code>false</code>.
	 */
	public void setShowXValues(boolean showXValues) {
		this.showXValues = showXValues;
		repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>showYAxis</code>.
	 * 
	 * @return Pokud se zobrazuje osa funk�n�ch hodnot, pak <code>true</code>,
	 *         jinak <code>false</code>.
	 */
	public boolean isShowYAxis() {
		return showYAxis;
	}

	/**
	 * Nastavuje hodnotu atributu <code>showYAxis</code>.
	 * 
	 * @param showYAxis
	 *          <code>true</code>, pokud m� b�t zobrazena osa funk�n�ch hodnot,
	 *          jinak <code>false</code>.
	 */
	public void setShowYAxis(boolean showYAxis) {
		this.showYAxis = showYAxis;
		repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>showYValues</code>.
	 * 
	 * @return Pokud se zobrazuje popis osy funk�n�ch hodnot, pak
	 *         <code>true</code>, jinak <code>false</code>.
	 */
	public boolean isShowYValues() {
		return showYValues;
	}

	/**
	 * Nastavuje hodnotu atributu <code>showYValues</code>.
	 * 
	 * @param showYValues
	 *          <code>true</code>, pokud m� b�t zobrazen popis osy funk�n�ch
	 *          hodnot, jinak <code>false</code>.
	 */
	public void setShowYValues(boolean showYValues) {
		this.showYValues = showYValues;
		repaint();
	}

	// TODO - koment��
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	// TODO - koment��
	@Override
	public void mouseMoved(MouseEvent event) {
		mouseMoved = true;
		Point point = event.getPoint();
		mousePositionX = point.x;
		mousePositionY = point.y;

		if (mouseMarkEnabled && values != null) {
			communicationProvider.sendObjectToObservers(new Point(mousePositionX,
					mousePositionY));
		}

		markMousePosition();
	}

	/**
	 * Vrac� hodnotu atributu <code>mouseOverSignalViewer</code>.
	 * 
	 * @return Pokud je kurzor my�i um�st�n nad <code>SignalViewer</code>em,
	 *         pak <code>true</code>, jinak <code>false</code>.
	 */
	public boolean isMouseOverSignalViewer() {
		return mouseOverSignalViewer;
	}

	/**
	 * Nastavuje hodnotu atributu <code>mouseOverSignalViewer</code>.
	 * 
	 * @param mouseOverSignalViewer
	 *          <code>true</code>, pokud se kurzor my�i nach�z� nad
	 *          <code>SignalViewer</code>em, jinak <code>false</code>.
	 */
	public void setMouseOverSignalViewer(boolean mouseOverSignalViewer) {
		this.mouseOverSignalViewer = mouseOverSignalViewer;
	}

	/**
	 * Vrac� hodnotu atributu <code>mousePositionX</code>.
	 * 
	 * @return Pozice my�i v ose X.
	 */
	public int getMousePositionX() {
		return mousePositionX;
	}

	/**
	 * Nastavuje hodnotu atributu <code>mousePositionX</code>.
	 * 
	 * @param mousePositionX
	 *          Nov� pozice my�i v ose X.
	 */
	public void setMousePositionX(int mousePositionX) {
		this.mousePositionX = mousePositionX;
	}

	/**
	 * Vrac� hodnotu atributu <code>mousePositionY</code>.
	 * 
	 * @return Pozice my�i v ose Y.
	 */
	public int getMousePositionY() {
		return mousePositionY;
	}

	/**
	 * Nastavuje hodnotu atributu <code>mousePositionY</code>.
	 * 
	 * @param mousePositionY
	 *          the mousePositionY to set
	 */
	public void setMousePositionY(int mousePositionY) {
		this.mousePositionY = mousePositionY;
	}

	// TODO - koment��
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	// TODO - koment��
	@Override
	public void mouseEntered(MouseEvent arg0) {
		mouseOverSignalViewer = true;
		mouseMoved = true;
		communicationProvider.sendObjectToObservers(SHOW_MOUSE_MARK);
	}

	// TODO - koment��
	@Override
	public void mouseExited(MouseEvent arg0) {
		mouseOverSignalViewer = false;
		this.repaint();
		communicationProvider.sendObjectToObservers(HIDE_MOUSE_MARK);
	}

	// TODO - koment��
	@Override
	public void mousePressed(MouseEvent arg0) {
		checkPopup(arg0);
	}

	// TODO - koment��
	@Override
	public void mouseReleased(MouseEvent arg0) {
		checkPopup(arg0);
	}

	// TODO - koment��
	private void checkPopup(MouseEvent event) {
		if (event.isPopupTrigger()) {
			popupMenu.show(this, event.getX(), event.getY());
		}
	}

	/**
	 * Vrac� referenci na atribut <code>communicationProvider</code>.
	 * 
	 * @return Reference na objekt pro komunikaci s ostatn�mi
	 *         <code>SignalViewer</code>y.
	 */
	public CommunicationProvider getCommunicationProvider() // FIXME - exporting
																													// non-public type
																													// through public API
	{
		return communicationProvider;
	}

	/**
	 * Vrac� hodnotu atributu <code>mousePositionColor</code>.
	 * 
	 * @return Barva zv�razn�n� pozice my�i.
	 */
	public Color getMousePositionColor() {
		return mousePositionColor;
	}

	/**
	 * Nastavuje hodnotu atributu <code>mousePositionColor</code>.
	 * 
	 * @param mousePositionColor
	 *          Nov� barva zv�razn�n� pozice my�i.
	 */
	public void setMousePositionColor(Color mousePositionColor) {
		this.mousePositionColor = mousePositionColor;
		repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>modeOfRepresentation</code>.
	 * 
	 * @return Zp�sob vykreslov�n� pr�b�hu sign�lu.
	 */
	public int getModeOfRepresentation() {
		return modeOfRepresentation;
	}

	/**
	 * Nastavuje hodnotu atributu <code>modeOfRepresentation</code>.
	 * 
	 * @param modeOfRepresentation
	 *          Nov� zp�sob zobrazov�n� pr�b�hu sign�lu.
	 */
	public void setModeOfRepresentation(int modeOfRepresentation) {
		this.modeOfRepresentation = modeOfRepresentation;
		popupMenu.setSelectedViewType();
		this.repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>functionalValuesColor</code>.
	 * 
	 * @return Barva zv�razn�n� funk�n�ch hodnot.
	 */
	public Color getFunctionalValuesColor() {
		return functionalValuesColor;
	}

	/**
	 * Nastavuje hodnotu atributu <code>functionalValuesColor</code>.
	 * 
	 * @param functionalValuesColor
	 *          Nov� barva pro zv�razn�n� funk�n�ch hodnot.
	 */
	public void setFunctionalValuesColor(Color functionalValuesColor) {
		this.functionalValuesColor = functionalValuesColor;
		repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>coordinateBasicOriginFrame</code>.
	 * 
	 * @return po�et fram� mezi po��tek soustavy sou�adnic a prvn� funk�n�
	 *         hodnotou.
	 */
	public int getCoordinateBasicOrigin() {
		return coordinateBasicOriginFrame;
	}

	/**
	 * Nastavuje hodnotu atributu <code>coordinateBasicOriginFrame</code>.
	 * 
	 * @param coordinateBasicOriginFrame
	 *          Nov� pozice po��tku soustavy sou�adnic.
	 */
	public void setCoordinateBasicOrigin(int coordinateBasicOriginFrame) {
		this.coordinateBasicOriginFrame = coordinateBasicOriginFrame;
		this.repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>zoomY</code>.
	 * 
	 * @return aktu�ln� p�ibl�en�/odd�len� v ose Y
	 */
	public float getZoomY() {
		return zoomY;
	}

	/**
	 * Nastavuje hodnotu atributu <code>zoomY</code>.
	 * 
	 * @param zoomY
	 *          Nov� hodnota p�ibl�en�/odd�len� sign�lu.
	 */
	public void setZoomY(float zoomY) {
		this.zoomY = zoomY;
		this.repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>invertedSignal</code>.
	 * 
	 * @return <code>true</code>, pokud je sign�l zobrazov�n invertovan�, jinak
	 *         <code>false</code>.
	 */
	public boolean isInvertedSignal() {
		return invertedSignal;
	}

	/**
	 * Nastavuje hodnotu atributu <code>invertedSignal</code>.
	 * 
	 * @param invertedSignal
	 *          <code>true</code>, pokud m� b�t sign�l zobrazovan� invertovan�,
	 *          jinak <code>false</code>.
	 */
	public void setInvertedSignal(boolean invertedSignal) {
		this.invertedSignal = invertedSignal;
		this.repaint();
	}

	/**
	 * Vrac� hodnotu atributu <code>mouseMarkEnabled</code>.
	 * 
	 * @return <code>true</code> pokud je povoleno zv�raz�ov�n� pozice my�i,
	 *         jinak <code>false</code>.
	 */
	public boolean isMouseMarkEnabled() {
		return mouseMarkEnabled;
	}

	/**
	 * Nastavuje hodnotu atributu <code>mouseMarkEnabled</code>.
	 * 
	 * @param mouseMarkEnabled
	 *          <code>true</code>, pokud m� b�t povoleno zv�razn�n� pozice
	 *          my�i, jinak <code>false</code>.
	 */
	public void setMouseMarkEnabled(boolean mouseMarkEnabled) {
		this.mouseMarkEnabled = mouseMarkEnabled;
	}
}
