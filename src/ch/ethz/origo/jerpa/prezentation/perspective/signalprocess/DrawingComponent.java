/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *  
 *    Copyright (C) 2009 - 2010 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess;

//import static java.awt.Container.dbg;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;

import ch.ethz.origo.jerpa.application.exception.InvalidFrameIndexException;
import ch.ethz.origo.jerpa.data.Buffer;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.Const;

/**
 * Komponenta zaji��uj�c� vykreslov�n� EEG sign�lu, epoch, artefakt� apod.
 * 
 * @author Jiri Kucera
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 11/17/2009 
 * @since 0.1.0
 */
public class DrawingComponent extends JComponent implements Runnable {

	/** Only for serialization */
	private static final long serialVersionUID = 8150800790840513596L;

	private static final int NANOSEC_IN_MIKROSEC = 1000; // Po�et nanosekund v
	// mikrosekund�

	private static final int DEFAULT_DRAWED_RANGE = 10000; // V�choz� d�lka
	// vykreslovan�ho
	// �seku v
	// milisekund�ch.

	private static final int NO_DELAYS_PER_YIELD = 16; // Minim�ln� po�et cykl�
	// p�ed t�m, ne� je
	// umo�n�no pokra�ovat
	// jin�m vl�kn�m.

	private Thread animator; // Vl�kno pro v�po�et a vykreslen� sign�l�.

	private volatile boolean running = false; // Ur�uje, zda je spu�t�no anima�n�
	// vl�kno

	private volatile boolean paused = true; // Ur�uje, zda je zapauzov�no anima�n�
	// vl�kno

	private long period; // Perioda sn�mk� v _nanosekund�ch_

	/**
	 * TEAM: zde jsou ulozena data, bude to chtit tohle nahradit nejakym objektem
	 * z aplikacni vrstvy, z ktereho se data budou dolovat
	 */
	private Buffer buffer;
	private Header header;
	private SignalsPanelProvider signalsProvider = null;
	private SignalsPanel signalsPanel; // okno se zobrazov�n�m drawingComponenty

	private int[] drawedEpochs;
	private boolean[] drawedArtefacts;
	private int numberOfSignals; // Po�et sign�l� k vykreslen� (v�etn� sign�l�,
	// kter� nejsou kv�li odzoomov�n� vid�t).

	private volatile long startPos = 0; // Index po��te�n�ho framu (od za��tku
	// souboru).

	private volatile long playbackIndicatorPosition = 0; // Index framu, na
	// kter�m se nach�z�
	// ukazatel p�ehr�v�n�.

	private float vZoom = 10;
	private int skips = 0; // Po�et p�esko�en� vykreslov�n�.

	private int gridStep; // Vzd�lenost mezi linkami �asov� m��ky v milisekund�ch.

	private int drawedFrames; // D�lka vykreslovan�ho useku ve framech.

	private float framesStep; // Vzd�lenost mezi framy v pixelech.

	private Font font;
	private Font gridFont;
	private Graphics graphics;
	private BufferedImage dbImage = null;
	private int[] drawableChannels;
	private float dMax;
	private boolean invertedSignals;
	private boolean cursorExitedComponent = true;
	private int firstVisibleSignal; // Index prvn�ho vykreslen�ho sign�lu.

	private int numberOfVisibleSignals; // Po�et zobrazen�ch sign�l�.

	/**
	 * Vytvo�� instanci objektu typu <code>DrawingComponent</code>.
	 * 
	 * @param signalsWindowProvider
	 *          Objekt typu <code>SignalsWindowProvider</code>
	 */
	public DrawingComponent(final SignalsPanelProvider signalsProvider) {
		this.signalsProvider = signalsProvider;
		buffer = null;
		header = null;

		setBackground(Color.WHITE);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					signalsProvider
							.setPressedPosition(getAbsolutePosition(e.getX()));
					signalsProvider.setStartSelection(e.getX());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					signalsProvider.setReleasedPosition(getAbsolutePosition(e
							.getX()));
				} else if (e.getButton() == MouseEvent.BUTTON3
						&& !cursorExitedComponent) {
					signalsProvider.setPopupmenu(DrawingComponent.this, e.getX(), e
							.getY(), getAbsolutePosition(e.getX()));
				}
				refresh();

				if (paused && !cursorExitedComponent) {
					paintTimeCursor(e);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				cursorExitedComponent = false;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				cursorExitedComponent = true;

				if (paused && !signalsProvider.getOptionMenu().isShowing()) {
					signalsProvider.clearFunctionalValues();
					refresh();
				}
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if (header != null) {
					if (paused && !signalsProvider.getOptionMenu().isShowing()) {
						refresh();
						paintTimeCursor(e);
					}
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (header != null) {

					if (paused) {
						signalsProvider.setEndSelection(e.getX());
						refresh();
						paintTimeCursor(e);
					}
				}
			}
		});

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				gridStep = getGridRange();
				framesStep = getWidth() / (float) drawedFrames;
				dMax = (float) getHeight() / numberOfVisibleSignals;
				refresh();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				refresh();
			}
		});

		font = new Font("SansSerif", Font.BOLD, 24);
		gridFont = new Font(Const.DC_GRID_FONT_FAMILY, Const.DC_GRID_FONT_STYLE,
				Const.DC_GRID_FONT_SIZE);
		invertedSignals = false;
	}

	/**
	 * Nastav� zdroj dat a metainformac�.
	 * 
	 * @param buffer
	 *          Zdroj dat.
	 * @param header
	 *          Zdroj metainformac�.
	 */
	public void setDataSource(Buffer buffer, Header header) {
		// TODO - predelat tak, aby to vyhovovalo chybe pri nacitani projektu
		if (buffer == null || header == null) {
			this.header = null;
			this.buffer = null;
			drawBackground();
			paintScreen(this.getGraphics());
			return;
		}

		this.header = header;
		period = (long) (header.getSamplingInterval() * NANOSEC_IN_MIKROSEC);
		this.buffer = buffer;
		startPos = 0;
		playbackIndicatorPosition = 0;
		if (header.getNumberOfSamples() < (int) timeToAbsoluteFrame(DEFAULT_DRAWED_RANGE)) {
			drawedFrames = (int) timeToAbsoluteFrame(header.getNumberOfSamples());
		} else {
			drawedFrames = (int) timeToAbsoluteFrame(DEFAULT_DRAWED_RANGE);
		}
		framesStep = this.getWidth() / (float) drawedFrames;
		drawedEpochs = new int[(int) (header.getNumberOfSamples())];
		drawedArtefacts = new boolean[(int) (header.getNumberOfSamples())];
		gridStep = getGridRange();
		dMax = (float) getHeight() / numberOfVisibleSignals;
	}

	/**
	 * Znovu vyrendeju a p�ekresl� zobrazen� sign�l.
	 */
	private synchronized void refresh() {
		try {
			render();
		} catch (Exception e) {
			e.printStackTrace();
		}
		paintScreen(this.getGraphics());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#addNotify()
	 */
	@Override
	public void addNotify() {
		super.addNotify(); // creates the peer

		// initializeCanvas();

		paused = true;
	}

	/**
	 * TODO
	 * 
	 * @param e
	 */
	private void paintTimeCursor(MouseEvent e) {
		if (dbImage != null) {
			graphics.setColor(Color.MAGENTA);
			graphics.drawLine(e.getX(), 0, e.getX(), getHeight());
			repaint();
			validate();
			signalsProvider.setFunctionalValues(getAbsolutePosition(e.getX()));
		}
	}

	/**
	 * Nastav� seznam index� kan�l�, kter� jsou dostupn� k vykreslen�.
	 * 
	 * @param channels
	 *          Seznam index� kan�l�, kter� mohou b�t vykresleny.
	 */
	public synchronized void setDrawableChannels(List<Integer> channels) {
		Integer[] vc = channels.toArray(new Integer[channels.size()]);
		drawableChannels = new int[channels.size()];
		for (int i = 0; i < vc.length; i++) {
			drawableChannels[i] = vc[i];
		}
		numberOfSignals = drawableChannels.length;
		gridStep = getGridRange();
		framesStep = getWidth() / (float) drawedFrames;

		dMax = (float) getHeight() / numberOfVisibleSignals;
		refresh();
	}

	/**
	 * Spust� p�ehr�v�n� sign�lu od po��tku.
	 */
	public synchronized void startDrawing() {
		if (animator == null || !running) {
			// System.out.println("new thread");
			animator = new Thread(this);
			animator.start();
		} else {
			// System.out.println("else");
			startPos = 0;
			resumeDrawing();
		}
		notify();

	}

	/**
	 * Znovu spust� p�ehr�v�n� sign�lu po pozastaven�.
	 */
	public synchronized void resumeDrawing() {
		paused = false;
		notify();
	}

	/**
	 * Pozastav� p�ehr�v�n� sign�lu.
	 */
	public synchronized void pauseDrawing() {
		paused = true;
		notify();
	}

	/**
	 * Pozastav� nebo znova spust� p�ehr�v�n� sign�lu.
	 */
	public synchronized void togglePause() {
		if (header == null) {
			return;
		}

		if (startPos + drawedFrames > header.getNumberOfSamples()) {
			paused = true;
		} else {
			paused = !paused;
		}
		notify();
	}

	/**
	 * Zastav� p�ehr�v�n� sign�lu a vr�t� ukazatel p�ehr�v�n� na po��tek.
	 */
	public synchronized void stopDrawing() {
		running = false;
		paused = true;
		notify();
		startPos = 0;
		playbackIndicatorPosition = 0;
		refresh();
	}

	/**
	 * Nastav� po��tek vykreslovan�ho �seku.
	 * 
	 * @param value
	 *          Prvn� sn�mek vykreslovan�ho �seku.
	 */
	public synchronized void setFirstFrame(int value) {
		notify();
		startPos = value;
		refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;

		beforeTime = System.nanoTime();

		running = true;

		paused = false;

		while (running) {

			update();
			signalsPanel.setHorizontalScrollbarValue((int) startPos);

			try {
				render();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			paintScreen(this.getGraphics());

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;

			if (sleepTime > 0) { // some time left in this cycle

				try {
					Thread.sleep(sleepTime / 1000000L); // nano -> ms

				} catch (InterruptedException ex) {
				}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else { // sleepTime <= 0; the frame took longer than the period

				excess -= sleepTime; // store excess time value

				overSleepTime = 0L;

				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield(); // give another thread a chance to run

					noDelays = 0;
				}
			}

			try {
				if (paused) {
					synchronized (this) {
						while (paused && running) {
							wait();
						}
					}
				}
			} catch (InterruptedException e) {
				// nic
			}

			beforeTime = System.nanoTime();

			// Trva-li vykreslovani prilis dlouho, updatuje se stav bez
			// vykresleni tolikrat, aby se pocet updatu blizil cilovemu fps
			int tmpSkips = 0;
			while (excess > period) {
				excess -= period;
				update(); // update state but don't render

				tmpSkips++;
			}
			skips = tmpSkips;
		}
	}

	/**
	 * Update posuvu p�ehr�v�n� sign�lu.
	 */
	private void update() {
		/*
		 * V teto metode se vykonava akce, ktera se ma stat pri dalsim updatu
		 * (nikoliv pri dalsim vykresleni), tzn. nastaveni prvniho framu
		 * vykreslovaneho useku o jednicku vys, asi zde ale nebude nutno nic menit
		 * krome zdroje dat
		 * 
		 * Pozor na to, samotne vykreslovani je uplne oddeleno od updatovani, tzn.
		 * updatovani se provadi tolikrat za sekundu, jako je vzorkovaci frekvence
		 * signalu, vykreslovani se pak provadi tolikrat, kolikrat to stihne pocitac
		 * vykreslit ;-). Takze ve vykreslovani se pak neni vubec treba zabyvat
		 * synchronizaci vykreslovani vuci casu, to je ucel teto komponenty.
		 */
		if (header == null) {
			return;
		}

		if (!paused) {
			if (++playbackIndicatorPosition >= header.getNumberOfSamples()) {
				paused = true;
				return;
			}

			long center = drawedFrames / 2 + startPos;

			if (playbackIndicatorPosition == center) {
				if (startPos + drawedFrames < header.getNumberOfSamples()) {
					startPos++;
				}
			} else if (playbackIndicatorPosition > startPos
					&& playbackIndicatorPosition < center) {
				// nic
			} else if (playbackIndicatorPosition < startPos) {
				startPos = playbackIndicatorPosition;
			} else if (playbackIndicatorPosition > center
					&& playbackIndicatorPosition < startPos + drawedFrames) {
				if (startPos + drawedFrames < header.getNumberOfSamples()) {
					startPos += 2;
				}
			} else if (playbackIndicatorPosition >= startPos + drawedFrames) {
				startPos = playbackIndicatorPosition - drawedFrames + 2;
			}
		}
	}

	/**
	 * Vykreslen� dat.
	 * 
	 * @throws InvalidFrameIndexException
	 * @throws IOException
	 */
	private synchronized void render() throws InvalidFrameIndexException {

		if (buffer == null || header == null) {
			return;
		}

		try {
			dbImage = (BufferedImage) createImage(getWidth(), getHeight());
			gridStep = getGridRange();
			framesStep = this.getWidth() / (float) drawedFrames;
			dMax = (float) getHeight() / numberOfVisibleSignals;
		} catch (IllegalArgumentException e) {
			return;
		}

		if (dbImage != null) {
			graphics = dbImage.getGraphics();
		} else {
			return;
		}

		// V�maz pozad�
		drawBackground();

		// vykreslen� zna�ek epoch
		drawEpochs();

		// vykreslen� sou�adnic
		drawGrid();

		// vykreslen� sign�l�
		drawSignals();

		// vykreslen� ukazatele
		drawPlaybackIndicator();

		// vyps�n� frame skips (debugovac� informace)
		graphics.setColor(Color.GRAY);
		graphics.setFont(font);
		graphics.drawString("frame skips: " + skips, 20, 25);
	}

	/**
	 * Vykreslen� (resp. smaz�n�) pozad�.
	 */
	private void drawBackground() {
		graphics.setColor(Const.DC_BACKGROUND_COLOR);
		graphics.fillRect(0, 0, dbImage.getWidth(), dbImage.getHeight());
	}

	/**
	 * Vykreslen� �asov� m��ky.
	 */
	private void drawGrid() {
		long firstGridLine = frameToTime(startPos);
		long lastGridLine = frameToTime(startPos + drawedFrames);

		graphics.setFont(gridFont);

		if (firstGridLine % gridStep != 0) {
			firstGridLine = firstGridLine - (firstGridLine % gridStep) + gridStep;
		}

		for (long i = firstGridLine; i <= lastGridLine; i += gridStep) {
			int x = getXCoordinate(timeToAbsoluteFrame(i));
			graphics.setColor(Const.DC_GRID_COLOR);
			graphics.drawLine(x, 0, x, getHeight());
			graphics.setColor(Const.DC_GRID_FONT_COLOR);
			graphics.drawString(timeToStr(i), x + 2, getHeight() - 2);
		}
	}

	/**
	 * Vykreslen� sign�l�.
	 * 
	 * @throws cz.zcu.kiv.jerpstudio.data.InvalidFrameIndexException
	 */
	private void drawSignals() throws InvalidFrameIndexException {
		float x0;
		float y0;
		float x1;
		float y1;
		float value;

		// float sRange = Math.abs(sMax + sMax);

		for (int signal = 0; signal < numberOfVisibleSignals
				&& signal < numberOfSignals; signal++) {
			x0 = 0;
			x1 = framesStep;

			value = buffer.getValue(drawableChannels[signal + firstVisibleSignal],
					startPos);

			y1 = dMax * (signal + 0.5f)
					+ (invertedSignals ? value / vZoom : -value / vZoom);

			graphics.setColor(Const.DC_GRID_COLOR);
			for (int j = 0; j < getWidth(); j += 10) {
				graphics.drawLine(j, (int) (signal * dMax + dMax / 2), j + 5,
						(int) (signal * dMax + dMax / 2));
			}

			graphics.setColor(Const.DC_SIGNALS_COLORS[(signal + firstVisibleSignal)
					% Const.DC_SIGNALS_COLORS.length]);
			for (int i = 0; i < drawedFrames
					&& startPos + i < header.getNumberOfSamples(); i++) {

				y0 = y1;

				value = buffer.getNextValue();

				y1 = dMax * (signal + 0.5f)
						+ (invertedSignals ? value / vZoom : -value / vZoom);

				// graphics.drawLine((int) x0, (int) y0, (int) x1, (int) y1);
				graphics.drawLine(getXCoordinateR(i), (int) y0, getXCoordinateR(i + 1),
						(int) y1);
				x0 = x1;
				x1 += framesStep;
			}
		}
	}

	/**
	 * Vykreslen� ukazatele p�ehr�v�n�.
	 */
	private void drawPlaybackIndicator() {
		graphics.setColor(Color.MAGENTA);
		graphics.setColor(Const.DC_PLAYBACK_POINTER_COLOR);
		graphics.drawLine(getXCoordinate(playbackIndicatorPosition), 0,
				getXCoordinate(playbackIndicatorPosition), this.getHeight());
	}

	/**
	 * Vykreslen� epoch.
	 */
	private void drawEpochs() {
		float x0;
		x0 = 0;

		for (int i = 0; i < drawedFrames
				&& startPos + i < header.getNumberOfSamples(); i++) {
			if (drawedArtefacts[(int) startPos + i]) {
				graphics.setColor(Color.RED);
				// graphics.drawLine((int) x0, 0, (int) x0, getHeight());
				((Graphics2D) graphics).fill(new Rectangle2D.Double(x0, 0, framesStep,
						getHeight()));
			}
			x0 += framesStep;
		}

		x0 = 0;
		for (int i = 0; i < drawedFrames
				&& startPos + i < header.getNumberOfSamples(); i++) {
			if (drawedEpochs[(int) startPos + i] == -1) {
				graphics.setColor(Color.GREEN);
				((Graphics2D) graphics).fill(new Rectangle2D.Double(x0, 0, framesStep,
						getHeight()));
			} else if (drawedEpochs[(int) startPos + i] == 1) {
				graphics.setColor(Color.GREEN);
				((Graphics2D) graphics).fill(new Rectangle2D.Double(x0, 0, framesStep,
						getHeight()));
				graphics.setColor(Color.BLACK);
				graphics.drawLine((int) x0, 0, (int) x0, getHeight());
			}
			x0 += framesStep;
		}

		if (signalsProvider.isAreaSelection()) {
			graphics.setColor(signalsProvider.getColorSelection());

			if (signalsProvider.getEndSelection() > 0) {
				((Graphics2D) graphics).fill(new Rectangle2D.Double(
						signalsProvider.getStartSelection(), 0, signalsProvider
								.getEndSelection(), getHeight()));
			} else {
				((Graphics2D) graphics).fill(new Rectangle2D.Double(
						signalsProvider.getStartSelection()
								+ signalsProvider.getEndSelection(), 0,
						-signalsProvider.getEndSelection(), getHeight()));
			}
		}
	}

	/**
	 * P�ekresl� vyrenderovan� image.
	 */
	private void paintScreen(Graphics g) {
		if (dbImage != null) {
			g.drawImage(dbImage, 0, 0, null);
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintScreen(g);
	}

	/**
	 * Nastav� rodi�ovsk� okno.
	 * 
	 * @param winSignals
	 */
	public void setSignalsWindow(SignalsPanel winSignals) {
		this.signalsPanel = winSignals;
	}

	/**
	 * Zjist�, zda je p�ehr�v�n� sign�lu pozastaveno.
	 * 
	 * @return Stav pozastaven� p�ehr�v�n�.
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Zjist�, zda je spu�t�no p�ehr�v�n� sign�lu.<br/>
	 * Vrac� <code>true</code> i v p��pad�, �e je p�ehr�v�n� pozastaveno.
	 * 
	 * @return <code>true</code>, pokud je p�ehr�vac� smy�ka spu�t�na.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Vr�t� d�lku vykreslovan�ho �seku jako po�et sn�mk�..
	 * 
	 * @return Po�et vykreslovan�ch sn�mk�.
	 */
	public int getDrawedFrames() {
		return drawedFrames;
	}

	/**
	 * Vr�t� d�lku vykreslovan�ho �seku v milisekund�ch.
	 * 
	 * @return D�lka vykreslovan�ho �seku v milisekund�ch.
	 */
	public long getDrawedLength() {
		return frameToTime(drawedFrames);
	}

	/**
	 * TODO
	 * 
	 * @param epochsDraw
	 */
	public void setDrawedEpochs(int[] epochsDraw) {
		this.drawedEpochs = epochsDraw;
		refresh();
	}

	/**
	 * TODO
	 * 
	 * @param drawedArtefacts
	 */
	public void setDrawedArtefacts(boolean[] drawedArtefacts) {
		this.drawedArtefacts = drawedArtefacts;
		refresh();
	}

	/**
	 * Vrac� absolutn� index framu nach�zej�c�ho se na zadan� sou�adnici.
	 * 
	 * @param xCoordinate
	 *          Sou�adnice bodu na X ose ve vykreslovac� komponent�.
	 * @return Absolutn� index framu - ��slovan� od po��tku souboru.
	 */
	public long getAbsolutePosition(int xCoordinate) {
		long position;
		position = (long) (startPos + xCoordinate / (float) getWidth()
				* drawedFrames);
		return position;
	}

	/**
	 * Vrac� relativn� index framu nach�zej�c�ho se na zadan� sou�adnici.
	 * 
	 * @param xCoordinate
	 *          Sou�adnice bodu na X ose ve vykreslovac� komponent�.
	 * @return Relativn� index framu - ��slovan� od prvn�ho framu zobrazen�ho v
	 *         komponent�.
	 */
	public long getRelativePosition(int xCoordinate) {
		long position;
		position = (long) (xCoordinate / (float) getWidth() * drawedFrames);
		return position;
	}

	/**
	 * Vrac� X sou�adnici n�le��c� absolutn� zadan�mu framu.
	 * 
	 * @param absolutePosition
	 * @return X sou�adnice framu, kter� byl zad�n absolutn�.
	 */
	public int getXCoordinate(long absolutePosition) {
		int x;
		x = (int) (getWidth() * ((absolutePosition - startPos) / (float) drawedFrames));
		return x;
	}

	/**
	 * Vrac� X sou�adnici n�le��c� relativn� zadan�mu framu (po��tkem je prvn�
	 * vykreslen� frame).
	 * 
	 * @param relativePosition
	 * @return X sou�adnice framu, kter� byl zad�n relativn�.
	 */
	public int getXCoordinateR(long relativePosition) {
		int x;
		x = (int) (getWidth() * (relativePosition / (float) drawedFrames));
		return x;
	}

	/**
	 * Nastav� vertik�ln� zoom.
	 * 
	 * @param value
	 *          Hodnota vertik�ln�ho zoomu.
	 */
	public synchronized void setVerticalZoom(float value) {
		vZoom = value;
		refresh();
	}

	/**
	 * Vr�t� hodnotu vertik�ln�ho zoomu.
	 * 
	 * @return Hodnota vertik�ln�ho zoomu.
	 */
	public synchronized float getVerticalZoom() {
		return vZoom;
	}

	/**
	 * Nastav� hodnotu horizont�ln�ho zoomu (resp. po�et vykreslovan�ch sn�mk�).
	 * 
	 * @param value
	 *          Po�et vykreslovan�ch sn�mk�.
	 */
	public synchronized void setHorizontalZoom(int value) {
		long center = drawedFrames / 2 + startPos;
		if (value < header.getNumberOfSamples()) {
			drawedFrames = value;
		} else {
			drawedFrames = (int) header.getNumberOfSamples();
		}
		startPos = center - drawedFrames / 2;
		if (startPos < 0) {
			startPos = 0;
		} else if (startPos + drawedFrames >= header.getNumberOfSamples()) {
			startPos = header.getNumberOfSamples() - drawedFrames;
		}
		signalsPanel.setHorizontalScrollbarValue((int) startPos);
		signalsProvider.resetHorizontalScrollbarMaximum();
		gridStep = getGridRange();
		framesStep = this.getWidth() / (float) drawedFrames;
		dMax = (float) getHeight() / numberOfVisibleSignals;
		refresh();
	}

	/**
	 * Vrac� optim�ln� m��kovou vzd�lenost v milisekund�ch.
	 * 
	 * @return Vzd�lenost mezi linkami m��ky v milisekund�ch.
	 */
	private int getGridRange() {
		int numberOfFrames = -1;
		int lastOptimalWidthDiff = Integer.MAX_VALUE;
		int currentOptimalWidthDiff;

		if (header == null) {
			return Const.DC_GRID_RANGE_TIMES[0];
		}

		for (int i = 0; i < Const.DC_GRID_RANGE_TIMES.length; i++) {
			numberOfFrames = (int) (Const.DC_GRID_RANGE_TIMES[i] * 1000 / header
					.getSamplingInterval());
			currentOptimalWidthDiff = Math.abs(Const.DC_OPTIMAL_GRID_RANGE
					- getXCoordinateR(numberOfFrames));
			if (currentOptimalWidthDiff > lastOptimalWidthDiff) {
				return Const.DC_GRID_RANGE_TIMES[i - 1];
			} else {
				lastOptimalWidthDiff = currentOptimalWidthDiff;
			}
		}

		return Const.DC_GRID_RANGE_TIMES[Const.DC_GRID_RANGE_TIMES.length - 1];
	}

	/**
	 * Vrac� �as na zadan�m framu vyj�d�en� jako �et�zec ve form�tu m:ss:lll (m -
	 * minuty, s - sekundy, l - milisekundy).
	 * 
	 * @param frame
	 *          Absolutn� index framu (od za��tku souboru).
	 * @return �et�zec s �asem.
	 */
	public String frameToTimeStr(long frame) {
		long time = (long) (frame * header.getSamplingInterval() / 1000);

		return timeToStr(time);
	}

	/**
	 * P�evede zadan� �as zadan� v milisekund�ch na �et�zec ve form�tu m:ss:lll (m
	 * - minuty, s - sekundy, l - milisekundy).
	 * 
	 * @param time
	 *          �as zadan� v milisekund�ch.
	 * @return �et�zec s �asem
	 */
	public String timeToStr(long time) {
		int minutes = (int) time / 60000;

		String seconds = String.valueOf((int) (time % 60000) / 1000);
		if (seconds.length() < 2) {
			seconds = "0" + seconds;
		}

		String millis = String.valueOf(time % 1000);
		while (millis.length() < 3) {
			millis = "0" + millis;
		}

		return String.valueOf(minutes) + ":" + seconds + ":" + millis;

	}

	/**
	 * Vrac� �as v milisekund�ch na zadan�m framu.
	 * 
	 * @param frame
	 *          Absolutn� index framu (od za��tku souboru).
	 * @return �as v milisekund�ch.
	 */
	public long frameToTime(long frame) {
		return (long) (frame * header.getSamplingInterval() / 1000);
	}

	/**
	 * Vrac� absolutn� index framu na zadan�m �ase.
	 * 
	 * @param time
	 *          �as v milisekund�ch.
	 * @return Absolutn� index framu - od za��tku souboru.
	 */
	public synchronized long timeToAbsoluteFrame(long time) {
		return (long) (time * 1000 / header.getSamplingInterval());
	}

	/**
	 * Vrac� relativn� index framu na zadan�m �ase.
	 * 
	 * @param time
	 *          �as v milisekund�ch.
	 * @return Relativn� index framu - od prvn�ho vykreslen�ho framu.
	 */
	public synchronized long timeToRelativeFrame(long time) {
		return timeToAbsoluteFrame(time) - startPos;
	}

	/**
	 * Nastavuje, zda maj� b�t sign�ly vykresleny invertovan�.
	 * 
	 * @param inverted
	 *          <code>true</code> - sign�ly budou vykresleny invertovan�.
	 */
	public void setInvertedView(boolean inverted) {
		invertedSignals = inverted;
		refresh();
	}

	/**
	 * Zji��uje, zda jsou sign�ly vykresleny invertovan�.
	 * 
	 * @return <code>true</code> - sign�ly jsou vykresleny invertovan�.
	 */
	public boolean isInvertedView() {
		return invertedSignals;
	}

	/**
	 * TODO
	 */
	protected synchronized void loadNumbersOfSignals() {
		numberOfVisibleSignals = signalsProvider.getNumberOfVisibleChannels();
		firstVisibleSignal = signalsProvider.getFirstVisibleChannel();

		gridStep = getGridRange();
		framesStep = this.getWidth() / (float) drawedFrames;
		dMax = (float) getHeight() / numberOfVisibleSignals;
		refresh();
	}

	/**
	 * Nastav� ukazatel p�ehr�v�n� na pozici zadanou indexem vzorku.
	 * 
	 * @param position
	 *          Index vzorku, na n�j� se m� nastavit pozice ukazatele.
	 */
	public synchronized void setPlaybackIndicatorPosition(long position) {
		playbackIndicatorPosition = position;
		refresh();
	}
}
