package jerpa.prezentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.Painter;

import com.jhlabs.image.NoiseFilter;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada@gmail.com)
 * @version 0.1.0
 * @since 0.1
 * 
 */
public class MainFrame extends JXFrame {

	/** Only for serialization */
	private static final long serialVersionUID = -8412972252271601358L;

	private JXPanel jContentPane;
	
	private JXButton maximalizeApp;

	private BufferedImage logoImg;
	private static BufferedImage minimizeImg, closeImg, minimizeOverImg,
			closeOverImg;
	private static BufferedImage maximizeImg, maximizeOverImg, maximize2Img,
			maximize2OverImg;

	public MainFrame() {
		super();
		try {
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initialize() throws IOException {
		initImages();
		initGUI();
	}

	private void initImages() {
		try {
			logoImg = ImageIO.read(ClassLoader
					.getSystemResourceAsStream("jerpa/data/images/icon.gif"));
			minimizeImg = ImageIO.read(ClassLoader
					.getSystemResourceAsStream("jerpa/data/images/minimize.png"));
			maximizeImg = ImageIO.read(ClassLoader
					.getSystemResourceAsStream("jerpa/data/images/maximize.png"));
			closeImg = ImageIO.read(ClassLoader
					.getSystemResourceAsStream("jerpa/data/images/close.png"));
			minimizeOverImg = ImageIO.read(ClassLoader
					.getSystemResourceAsStream("jerpa/data/images/minimize_over.png"));
			maximizeOverImg = ImageIO.read(ClassLoader
					.getSystemResourceAsStream("jerpa/data/images/maximize_over.png"));
			closeOverImg = ImageIO.read(ClassLoader
					.getSystemResourceAsStream("jerpa/data/images/close_over.png"));
			maximize2OverImg = ImageIO.read(ClassLoader
					.getSystemResourceAsStream("jerpa/data/images/maximize2_over.png"));
			maximize2Img = ImageIO.read(ClassLoader
					.getSystemResourceAsStream("jerpa/data/images/maximize2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initGUI() {
		this.setDefaultCloseOperation(JXFrame.EXIT_ON_CLOSE);
		this.setSize(new Dimension(800, 600));
		this.setUndecorated(true);
		this.setContentPane(getContetPane());
		this.setStartPosition(JXFrame.StartPosition.CenterInScreen);
		this.setVisible(true);
	}
	
	private JXPanel getContetPane() {
		if (jContentPane == null) {
			jContentPane = new JXPanel();
			jContentPane.setBackground(Color.lightGray);
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getHeaderPanel(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	private JXPanel getHeaderPanel() {
		final Paint backgroundMenu = createBackgroundTexture(new Color(0, 98, 137),
				new Color(104, 188, 222), 35);
		JXPanel headerPanel = new JXPanel(true);
		Painter<Component> p = new Painter<Component>() {
			public void paint(Graphics2D g, Component c, int width, int height) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.setPaint(backgroundMenu);
				if (getExtendedState() != JXFrame.MAXIMIZED_BOTH) {
					g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight() + 5, 20, 20);
				} else
					g2d.fillRect(0, 0, c.getWidth(), c.getHeight() + 5);
			}
		};
		headerPanel.setBackgroundPainter(p);

		JXButton minimalizeApp = new JXButton(new ImageIcon(minimizeImg));
		minimalizeApp.setRolloverIcon(new ImageIcon(minimizeOverImg));
		minimalizeApp.setBorder(null);
		minimalizeApp.setFocusPainted(false);
		minimalizeApp.setContentAreaFilled(false);
		minimalizeApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setExtendedState(JXFrame.ICONIFIED);
			}
		});
		maximalizeApp = new JXButton(new ImageIcon(maximizeImg));
		maximalizeApp.setRolloverIcon(new ImageIcon(maximizeOverImg));
		maximalizeApp.setBorder(null);
		maximalizeApp.setFocusPainted(false);
		maximalizeApp.setContentAreaFilled(false);
		maximalizeApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getExtendedState() != JXFrame.MAXIMIZED_BOTH) {
					setExtendedState(JXFrame.MAXIMIZED_BOTH);
				} else {
					setExtendedState(JXFrame.NORMAL);
				}
			}
		});
		JXButton closeApp = new JXButton(new ImageIcon(closeImg));
		closeApp.setRolloverIcon(new ImageIcon(closeOverImg));
		closeApp.setBorder(null);
		closeApp.setFocusPainted(false);
		closeApp.setContentAreaFilled(false);
		closeApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.dispose();
			}
		});

		headerPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbcLogoLabel = new GridBagConstraints(0, 0, 1, 1, 0.0,
				0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10,
				0, 0, 0), 0, 0);

		GridBagConstraints gbcNameLabel2 = new GridBagConstraints(1, 0, 1, 1, 0.0,
				0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10,
				0, 0, 5), 0, 0);

		GridBagConstraints gbcMinimalizeButt = new GridBagConstraints(2, 0, 1, 1,
				0.0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				10, 0, 0, 0), 0, 0);

		GridBagConstraints gbcMaximalizeButt = new GridBagConstraints(3, 0, 1, 1,
				0.0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				10, 0, 0, 0), 0, 0);

		GridBagConstraints gbcCloseButt = new GridBagConstraints(4, 0, 1, 1, 0.0,
				0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10,
				0, 0, 0), 0, 0);

		headerPanel.add(new JXLabel(new ImageIcon(logoImg)), gbcLogoLabel);
		headerPanel.add(new JXLabel("JERPA 1.0"), gbcNameLabel2);
		headerPanel.add(minimalizeApp, gbcMinimalizeButt);
		headerPanel.add(maximalizeApp, gbcMaximalizeButt);
		headerPanel.add(closeApp, gbcCloseButt);

		headerPanel.setOpaque(false);
		return headerPanel;
	}

	public Paint createBackgroundTexture(Color color1, Color color2, int size) {
		BufferedImage image = GraphicsUtilities.createTranslucentCompatibleImage(
				size, size);
		Graphics2D g2d = image.createGraphics();
		Paint paint = new GradientPaint(0, 0, color1, 0, size, color2);
		g2d.setPaint(paint);
		g2d.fillRect(0, 0, size, size);
		g2d.dispose();
		NoiseFilter filter = new NoiseFilter();
		filter.setAmount(10);
		filter.setDensity(0.5f);
		filter.setDistribution(NoiseFilter.UNIFORM);
		filter.setMonochrome(true);
		filter.filter(image, image);

		Paint result = new TexturePaint(image, new Rectangle(size, size));
		return result;
	}
	
	@Override
	public synchronized void setExtendedState(int state) {
		super.setExtendedState(state);
		if (state == JXFrame.MAXIMIZED_BOTH) {
			maximalizeApp.setIcon(new ImageIcon(maximize2Img));
			maximalizeApp.setRolloverIcon(new ImageIcon(maximize2OverImg));
		}
	}
}
