package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.info;

import java.util.Observable;

import javax.swing.JPanel;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalProject;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SingnalPerspectiveObservable;
import ch.ethz.origo.jerpa.data.Header;
import ch.ethz.origo.juigle.application.observers.IObserver;

/**
 * 
 * 
 * @author Vaclav Souhrada
 * @version 0.1.0 (11/18/09)
 * @since 0.1.0 (11/18/09)
 * @see IObserver
 */
public class SignalInfoProvider implements IObserver {

	private SignalSessionManager session;
	private SignalInfoPanel infoPanel;
	private GlobalInfoTableDataModel globalInfoDataModel;
	private ChannelsInfoTableDataModel channelsInfoDataModel;
	
	public SignalInfoProvider(SignalSessionManager session) {
		this.session = session;
		this.globalInfoDataModel = new GlobalInfoTableDataModel(session
				.getCurrentHeader());
		this.channelsInfoDataModel = new ChannelsInfoTableDataModel(session
				.getCurrentHeader());
		this.infoPanel = new SignalInfoPanel(this);
	}
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Object state) {

	}

	@Override
	public void update(Object object, int state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Observable o, Object arg) {
		int msg;
		if (arg instanceof java.lang.Integer) {
			msg = ((Integer) arg).intValue();
		} else {
			return;
		}

		switch (msg) {
		case SingnalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
			Header header = ((SignalProject)session.getCurrentProject()).getHeader();
			globalInfoDataModel.setHeader(header);
			channelsInfoDataModel.setHeader(header);
			infoPanel.setItemsEnabled(true);
			infoPanel.refresh();
			break;

		case SingnalPerspectiveObservable.MSG_PROJECT_CLOSED:
			globalInfoDataModel.setHeader(null);
			channelsInfoDataModel.setHeader(null);
			infoPanel.setItemsEnabled(false);
			infoPanel.refresh();
		}

	}

	public JPanel getPanel() {
		return infoPanel;
	}

	protected GlobalInfoTableDataModel getGlobalInfoDataModel() {
		return globalInfoDataModel;
	}

	protected ChannelsInfoTableDataModel getChannelsInfoDataModel() {
		return channelsInfoDataModel;
	}

}
