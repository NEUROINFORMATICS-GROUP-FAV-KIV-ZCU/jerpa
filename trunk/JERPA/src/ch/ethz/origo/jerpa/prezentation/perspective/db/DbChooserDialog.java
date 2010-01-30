package ch.ethz.origo.jerpa.prezentation.perspective.db;

import java.awt.Color;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.painter.TextPainter;
import org.jdesktop.swingx.renderer.DefaultListRenderer;

import ch.ethz.origo.juigle.prezentation.dialogs.JUIGLEDialog;

/**
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (1/29/2010)
 * @since 0.1.0 (1/29/2010)
 * @see JUIGLEDialog
 *
 */
public class DbChooserDialog extends JUIGLEDialog {

	/** Only for serialization */
	private static final long serialVersionUID = -1921373739839914535L;
	
	private JXList list;
	private JXPanel contentPanel;

	public DbChooserDialog() {
		super(new JXPanel());
		initialize();
	}

	private void initialize() {
		initContentPanel();
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.pack();
		this.setLocation(getCenterPosition(this.getSize()));		
	}
	
	private void initContentPanel() {
		contentPanel = (JXPanel)content;
		//contentPanel.setBackgroundPainter(new Pa());
		list = new JXList(new Object[] {'v', 'o', 'm', 'a', 'c', 'k', 'a'} );
		list.setRolloverEnabled(true);
		list.setCellRenderer(new DefaultListRenderer());
		list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
		      null, Color.RED)); 
		contentPanel.add(list);
		
	}
	
	public static void main(String[] args) {
		new DbChooserDialog();
	}
}
