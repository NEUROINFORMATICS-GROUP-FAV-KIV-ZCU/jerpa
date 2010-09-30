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
package ch.ethz.origo.jerpa.prezentation.perspective.db;

import java.awt.Color;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
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
