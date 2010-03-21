package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Z�pat� exportovan�ho dokumentu. Obsahuje u�ivatel�v koment�� a informace o
 * aplikaci.
 * 
 * @author Tomas Rondik (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/21/2010)
 * @since 0.1.0 (3/21/2010)
 */
@SuppressWarnings("serial")
class PageFooter extends JPanel {
	/**
	 * Programov� rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	private ExportFrameProvider provider;

	/**
	 * Vytv��� nov� z�pat� exportovan�ho dokumentu.
	 * 
	 * @param provider
	 *          Programov� rozhran� pro komunikaci s aplika�n� vrstvou.
	 */
	PageFooter(ExportFrameProvider provider) {
		super();
		this.provider = provider;
		createInside();
	}

	/**
	 * Vytv��� "vnit�ek" z�pat� - vkl�d� do n�j u�ivatel�v koment�� (pokud
	 * existuje) a informace o aplikaci.
	 */
	private void createInside() {
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(layout);
		this.setBackground(provider.getCanvasColor());

		JPanel aplicationInfoJP = new JPanel();
		BoxLayout aplicationInfoJPLayout = new BoxLayout(aplicationInfoJP,
				BoxLayout.Y_AXIS);
		aplicationInfoJP.setLayout(aplicationInfoJPLayout);
		aplicationInfoJP.setBackground(provider.getCanvasColor());

		JTextArea commentaryJTA = new JTextArea();
		commentaryJTA.setWrapStyleWord(true);
		commentaryJTA.setEditable(true);
		commentaryJTA.setLineWrap(true);
		commentaryJTA.setBackground(provider.getCanvasColor());
		commentaryJTA.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		commentaryJTA
				.setText(provider.getCommentary()
						+ "\n"
						+ "jERP Studio - created on Department of Computer Science and Engineering / Faculty of Applied Sciences / University of West Bohemia");
		commentaryJTA.setPreferredSize(new Dimension(400, 120));
		commentaryJTA.setMaximumSize(new Dimension(400, 120));
		commentaryJTA.setMinimumSize(new Dimension(400, 120));

		aplicationInfoJP.add(commentaryJTA);
		this.add(aplicationInfoJP);
	}
}
