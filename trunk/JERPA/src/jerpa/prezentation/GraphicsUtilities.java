package jerpa.prezentation;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class GraphicsUtilities {

	private static final GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	public static BufferedImage createTranslucentCompatibleImage(int width, int height) {
		return configuration.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}

}
