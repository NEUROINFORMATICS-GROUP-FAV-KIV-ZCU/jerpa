package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import noname.JERPAUtils;

import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.SignalProjectWriter;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.ArtefactSelectionDialog;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.BaselineCorrectionDialog;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.SignalsPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging.AveragingPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.head.ChannelsPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.info.SignalInfoProvider;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.exception.ProjectOperationException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.application.project.Project;
import ch.ethz.origo.juigle.data.JUIGLEErrorParser;
import ch.ethz.origo.juigle.prezentation.JUIGLEFileChooser;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import ch.ethz.origo.juigle.prezentation.dialogs.AboutDialog;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.3.2 (2/23/2010)
 * @since 0.1.0 (05/18/09)
 * @see Perspective
 * @see IObserver
 * 
 */
public class SignalPerspective extends Perspective implements IObserver {

	/** Only for serialization */
	private static final long serialVersionUID = 3313465073940475745L;

	//
	private JUIGLEMenuItem fileMenu;
	private JUIGLEMenuItem openFileItem;
	private JUIGLEMenuItem saveFileItem;
	private JUIGLEMenuItem saveAsFileItem;
	private JUIGLEMenuItem closeFileItem;
	private JUIGLEMenuItem importItem;
	private JUIGLEMenuItem exportItem;
	private JUIGLEMenuItem exitItem;
	//
	private JUIGLEMenuItem editMenu;
	private JUIGLEMenuItem undoItem;
	private JUIGLEMenuItem redoItem;
	private JUIGLEMenuItem baselineCorrItem;
	private JUIGLEMenuItem autoArteSelItem;
	//
	private JUIGLEMenuItem viewMenu;
	private JUIGLEMenuItem channelItem;
	private JUIGLEMenuItem editInfoWinItem;
	private JUIGLEMenuItem signalsWinItem;
	private JUIGLEMenuItem averagingWinItem;
	//
	private JUIGLEMenuItem helpItem;
	private JUIGLEMenuItem keyboardShortcutItem;
	private JUIGLEMenuItem aboutItem;

	private SignalSessionManager sessionManager;

	private SignalPerspectiveObservable spObservable;

	private SignalInfoProvider signalInfoProvider;
	private AveragingPanelProvider averagingPanelProvider;
	private SignalsPanelProvider signalPanelProvider;
	private ChannelsPanelProvider channelPanelProvider;
	
	private ArtefactSelectionDialog artefactSelectionDialog;
	private BaselineCorrectionDialog baselineCorrectionDialog;

	public SignalPerspective() {
		perspectiveObservable.attach(this);
		spObservable = SignalPerspectiveObservable.getInstance();
		sessionManager = new SignalSessionManager();
		resourcePath = LangUtils
				.getPerspectiveLangPathProp("perspective.signalprocessing.lang");
	}

	@Override
	public void initPerspectivePanel() throws PerspectiveException {
		super.initPerspectivePanel();
		mainPanel.setLayout(new GridBagLayout());
		try {
			signalInfoProvider = new SignalInfoProvider(sessionManager);
			averagingPanelProvider = new AveragingPanelProvider(sessionManager,
					SignalPerspectiveObservable.getInstance());
			signalPanelProvider = new SignalsPanelProvider(sessionManager);
			channelPanelProvider = new ChannelsPanelProvider(sessionManager);

			GridBagConstraints gbcSignalInfoProv = new GridBagConstraints();
			gbcSignalInfoProv.gridx = 1;
			gbcSignalInfoProv.gridy = 2;
			// gbcSignalInfoProv.fill = GridBagConstraints.HORIZONTAL;
			// gbcSignalInfoProv.gridwidth = GridBagConstraints.REMAINDER;
			// gbcSignalInfoProv.gridheight = GridBagConstraints.REMAINDER;
			// gbcSignalInfoProv.anchor = GridBagConstraints.WEST;

			GridBagConstraints gbcChannelProv = new GridBagConstraints();
			gbcChannelProv.gridx = 0;
			gbcChannelProv.gridy = GridBagConstraints.RELATIVE;
			// gbcChannelProv.fill = GridBagConstraints.BOTH;
			// gbcChannelProv.gridwidth = GridBagConstraints.REMAINDER;
			// gbcChannelProv.gridheight = GridBagConstraints.REMAINDER;
			gbcChannelProv.anchor = GridBagConstraints.WEST;

			GridBagConstraints gbcSignalPanelProv = new GridBagConstraints();
			gbcSignalPanelProv.gridx = 0;
			gbcSignalPanelProv.gridy = 0;
			// gbcSignalPanelProv.ipadx = 400;
			// gbcSignalPanelProv.ipady = 400;
			gbcSignalPanelProv.anchor = GridBagConstraints.NORTH;
			// gbcSignalPanelProv.fill = GridBagConstraints.BOTH;
			// gbcSignalPanelProv.gridwidth = 2;
			// gbcSignalPanelProv.gridheight = 2;
			// gbcSignalPanelProv.weightx = 0.0;
			// gbcSignalPanelProv.weighty = 0.0;
			gbcSignalPanelProv.insets = new Insets(0, 0, 0, 0);

			GridBagConstraints gbcAveragingProv = new GridBagConstraints();
			gbcAveragingProv.gridx = 0;
			gbcAveragingProv.gridy = GridBagConstraints.RELATIVE;
			gbcAveragingProv.fill = GridBagConstraints.BOTH;
			// gbcAveragingProv.gridwidth = 2;
			// gbcAveragingProv.gridheight = GridBagConstraints.REMAINDER;
			// gbcAveragingProv.anchor = GridBagConstraints.NORTH;

			mainPanel.add(signalPanelProvider.getPanel(), gbcSignalPanelProv);
			// mainPanel.add(averagingPanelProvider.getPanel(), gbcAveragingProv);
			// mainPanel.add(channelPanelProvider.getPanel(), gbcChannelProv);
			// mainPanel.add(signalInfoProvider.getPanel(), gbcSignalInfoProv);
		} catch (JUIGLELangException e) {
			throw new PerspectiveException(e);
		}
	}

	@Override
	public void initPerspectiveMenuPanel() throws PerspectiveException {
		if (menuTaskPane == null) {
			// menuTitledPanel = new JXTitledPanel();
			// menuTitledPanel.setOpaque(false);
			menuTaskPane = new JXTaskPane();
			menuTaskPane.setOpaque(false);

			// initalize menu
			menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP,
					resourcePath);
			menu.setFloatable(false);
			menu.setRollover(true);
			// initialize and add menu items
			initAndAddMenuItems();
		}
	}

	/**
	 * 
	 * @throws PerspectiveException
	 * @since 0.1.0
	 */
	private void initAndAddMenuItems() throws PerspectiveException {
		try {
			// add items to menu
			menu.addItem(initAndGetFileMenuItem());
			menu.addItem(initAndGetEditMenuItem());
			menu.addItem(initAndGetViewMenuItem());
			menu.addItem(initAndGetHelpMenuItem());
			menu.addMenuSeparator();
			menu.addHeaderHideButton(true);
			menu.addFooterHideButton(true);
			// menuTitledPanel.add(menu);
			menuTaskPane.add(menu);
		} catch (JUIGLEMenuException e1) {
			throw new PerspectiveException(e1);
		}
	}

	/*
	 * @Override public String getResourceBundlePath() { return
	 * SignalPerspective.resourcePath; }
	 */
	public Icon getIcon() {
		return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH
				+ "icon.gif", "aaaaaaaaaaaaaa");
	}

	@Override
	public void updateText() {
		super.updateText();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				menu.updateText();
			}
		});
	}

	/**
	 * Create inside panel
	 * 
	 * @return instance of inside panel
	 * @version 0.1.0 (2/16/2010)
	 * @since 0.3.0 (2/16/2010)
	 */
	private void getInterior() throws PerspectiveException {

	}

	/**
	 * 
	 * @return
	 * @version 0.1.1
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetFileMenuItem() {
		fileMenu = new JUIGLEMenuItem(getLocalizedString("menu.file"));
		// initialize subItems of file menu
		openFileItem = new JUIGLEMenuItem();
		saveFileItem = new JUIGLEMenuItem();
		saveAsFileItem = new JUIGLEMenuItem();
		closeFileItem = new JUIGLEMenuItem();
		importItem = new JUIGLEMenuItem();
		exportItem = new JUIGLEMenuItem();
		exitItem = new JUIGLEMenuItem();
		// set Resource bundles
		fileMenu.setResourceBundleKey("menu.file");
		openFileItem.setResourceBundleKey("menu.open");
		saveFileItem.setResourceBundleKey("menu.save");
		saveAsFileItem.setResourceBundleKey("menu.saveAs");
		closeFileItem.setResourceBundleKey("menu.close.project");
		importItem.setResourceBundleKey("menu.import");
		exportItem.setResourceBundleKey("menu.export");
		exitItem.setResourceBundleKey("menu.exit");
		// set actions to menu items
		setFileMenuActions();
		// add key accelerators to items
		openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				KeyEvent.CTRL_MASK));
		saveFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.CTRL_MASK));
		// add subitems to file menu
		fileMenu.addSubItem(openFileItem);
		fileMenu.addSubItem(saveFileItem);
		fileMenu.addSubItem(saveAsFileItem);
		fileMenu.addSubItem(closeFileItem);
		fileMenu.addSubItem(importItem);
		fileMenu.addSubItem(exportItem);
		fileMenu.addSubItem(exitItem);

		return fileMenu;
	}

	/**
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetEditMenuItem() {
		editMenu = new JUIGLEMenuItem(getLocalizedString("menu.edit"));
		undoItem = new JUIGLEMenuItem();
		redoItem = new JUIGLEMenuItem();
		baselineCorrItem = new JUIGLEMenuItem();
		autoArteSelItem = new JUIGLEMenuItem();
		//
		editMenu.setResourceBundleKey("menu.edit");
		undoItem.setResourceBundleKey("menu.edit.undo");
		redoItem.setResourceBundleKey("menu.edit.redo");
		baselineCorrItem.setResourceBundleKey("menu.edit.baselinecorrection");
		autoArteSelItem.setResourceBundleKey("menu.edit.autoselarte");
		//
		setEditMenuActions();
		//
		editMenu.addSubItem(undoItem);
		editMenu.addSubItem(redoItem);
		editMenu.addSubItem(baselineCorrItem);
		editMenu.addSubItem(autoArteSelItem);

		return editMenu;
	}

	/**
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetViewMenuItem() {
		viewMenu = new JUIGLEMenuItem(getLocalizedString("menu.view"));
		channelItem = new JUIGLEMenuItem();
		signalsWinItem = new JUIGLEMenuItem();
		editInfoWinItem = new JUIGLEMenuItem();
		averagingWinItem = new JUIGLEMenuItem();
		//
		viewMenu.setResourceBundleKey("menu.view");
		channelItem.setResourceBundleKey("menu.view.channelwin");
		signalsWinItem.setResourceBundleKey("menu.view.signalwin");
		editInfoWinItem.setResourceBundleKey("menu.view.infowin");
		averagingWinItem.setResourceBundleKey("menu.view.averagewin");
		//
		setViewMenuActions();
		//
		viewMenu.addSubItem(channelItem);
		viewMenu.addSubItem(editInfoWinItem);
		viewMenu.addSubItem(signalsWinItem);
		viewMenu.addSubItem(averagingWinItem);

		return viewMenu;
	}

	/**
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetHelpMenuItem() {
		helpItem = new JUIGLEMenuItem(getLocalizedString("menu.help"));
		keyboardShortcutItem = new JUIGLEMenuItem();
		aboutItem = new JUIGLEMenuItem();
		//
		keyboardShortcutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
				KeyEvent.CTRL_MASK));
		//
		helpItem.setResourceBundleKey("menu.help");
		keyboardShortcutItem.setResourceBundleKey("menu.help.keyboard.shortcuts");
		aboutItem.setResourceBundleKey("menu.help.about.signalprocessing");
		//
		setHelpMenuActions();
		helpItem.addSubItem(keyboardShortcutItem);
		helpItem.addSubItem(aboutItem);
		return helpItem;
	}

	/**
	 * 
	 * @since 0.1.1
	 */
	private void setFileMenuActions() {
		Action saveAction = new AbstractAction() {
			/** Only for serialization */
			private static final long serialVersionUID = -1644285485867277600L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					sessionManager.saveFile(new SignalProjectWriter());
				} catch (ProjectOperationException e1) {
					JUIGLErrorInfoUtils.showErrorDialog("Project Error",
							JUIGLEErrorParser.getJUIGLEErrorMessage(e1.getMessage()), e1,
							java.util.logging.Level.WARNING);
				}
			}
		};
		Action saveAsAct = new AbstractAction() {
			/** Only for serialization */
			private static final long serialVersionUID = 5259515978643788611L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					sessionManager.saveAsFile();
				} catch (ProjectOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		openFileItem.setAction(getOpenFileAction());
		saveFileItem.setAction(saveAction);
		saveAsFileItem.setAction(saveAsAct);

	}

	/**
	 * @since 0.1.0
	 */
	private void setEditMenuActions() {
		Action autoArtefactSelAct = new AbstractAction() {

			/** Only for serialization */
			private static final long serialVersionUID = -1644285485867277600L;

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		};
		autoArteSelItem.setAction(autoArtefactSelAct);
	}

	/**
	 * @since 0.1.0
	 */
	private void setViewMenuActions() {

	}

	/**
	 * @since 0.1.0
	 */
	private void setHelpMenuActions() {
		Action about = new AbstractAction() {
			/**  */
			private static final long serialVersionUID = -1644285485867277600L;

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new AboutDialog();
					}
				});
			}
		};
		aboutItem.setAction(about);
	}

	@Override
	public void update() {
		// not implemented
	}

	@Override
	public void update(Object state) {
		makeUpdate(state);
	}

	/**
	 * 
	 * @param obj
	 * @version 0.1.0
	 * @since 0.2.0
	 */
	private void makeUpdate(Object obj) {
	}
	
	

	/**
	 * 
	 * @throws JUIGLEMenuException
	 * @version 0.1.0 (2/14/2010)
	 * @since 0.2.1 (2/14/2010)
	 */
	private void addOpenedFunctionsToMenu() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					menu.addMenuSeparator();
					JUIGLEMenuItem signalPanelItem = new JUIGLEMenuItem();
					JUIGLEMenuItem infoPanelItem = new JUIGLEMenuItem();
					JUIGLEMenuItem channelPanelItem = new JUIGLEMenuItem();
					JUIGLEMenuItem averagePanelItem = new JUIGLEMenuItem();
					signalPanelItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
							JERPAUtils.IMAGE_PATH + "icon.gif", 24, 24));
					infoPanelItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
							JERPAUtils.IMAGE_PATH + "info-48.png", 24, 24));
					averagePanelItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
							JERPAUtils.IMAGE_PATH + "averages32.png", 24, 24));
					menu.addItem(signalPanelItem);
					menu.addItem(infoPanelItem);
					menu.addItem(averagePanelItem);
				} catch (JUIGLEMenuException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PerspectiveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				menu.revalidate();
				menu.repaint();
			}
		});
	}

	@Override
	public void update(IObservable o, Object state) {
		makeUpdate(o, state);
	}

	private Action getOpenFileAction() {
		Action open = new AbstractAction() {
			private static final long serialVersionUID = -6603743681967057946L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JUIGLEFileChooser fileChooser = new JUIGLEFileChooser();
				fileChooser.setDialogTitle("Open file");
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"jERPA project file (*.erpa)", "erpa"));
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"European Data Format (*.edf, *.rec)", "edf", "rec"));
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"Pseudo signal generator (*.generator)", "generator"));
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"BrainStudio Format (*.xml)", "xml"));
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"Brain Vision Data Exchange Header File (*.vhdr)", "vhdr"));
				fileChooser
						.setFileFilter(new FileNameExtensionFilter(
								"All supported files (*.edf, *.erpa, *.generator, *.rec, *.vhdr, *.xml)",
								"edf", "esp", "generator", "rec", "vhdr", "xml"));

				fileChooser.setAcceptAllFileFilterUsed(false);

				if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						sessionManager.loadFile(file);
						addOpenedFunctionsToMenu();
						spObservable
								.setState(SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED);

					} catch (ProjectOperationException e1) {
						// FIXME upravit na vypis do GUI JERPA011
						e1.printStackTrace();
					}
				}
			}
		};
		return open;
	}
	
	public void createNewArtefactDialog() {
		artefactSelectionDialog = new ArtefactSelectionDialog(sessionManager);
		baselineCorrectionDialog = new BaselineCorrectionDialog(sessionManager);
	}
	
	private void checkUndoableControls() {
		Project project = sessionManager.getCurrentProject();
//		undoButton.setEnabled(project.canUndo());
		undoItem.setEnabled(project.canUndo());
//		mainWindow.redoButton.setEnabled(project.canRedo());
		redoItem.setEnabled(project.canRedo());
	}

	/**
	 * 
	 * @param object
	 * @param state
	 * @version 0.1.0
	 * @since 0.2.0
	 */
	private void makeUpdate(IObservable o, Object state) {
		int currState;

		if (state instanceof java.lang.Integer) {
			currState = ((Integer) state).intValue();
		} else {
			return;
		}

		if (o instanceof SignalPerspectiveObservable) {
			switch (currState) {
			case SignalPerspectiveObservable.MSG_PROJECT_CLOSED:
				saveFileItem.setEnabled(false);
				saveAsFileItem.setEnabled(false);
				closeFileItem.setEnabled(false);
//FIXME				mainWindow.saveButton.setEnabled(false);
				autoArteSelItem.setEnabled(false);
				baselineCorrItem.setEnabled(false);
//FIXME				mainWindow.invertSignalsButton.setEnabled(false);
				break;

			case SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
				checkUndoableControls();
//FIXME addOpenedProjectsToMenu(sessionManager.getProjectsNames(), 0);
				sessionManager.getAutoSelectionArtefact().setCurrentData();
				sessionManager.getBaselineCorrection().setCurrentData();
				saveAsFileItem.setEnabled(true);
			//FIXME				mainWindow.saveButton.setEnabled(true);
				saveFileItem.setEnabled(true);
				closeFileItem.setEnabled(true);
			  autoArteSelItem.setEnabled(true);
				baselineCorrItem.setEnabled(true);
				createNewArtefactDialog();
			//FIXME				mainWindow.invertSignalsButton.setEnabled(true);
			//FIXME				mainWindow.invertSignalsButton.setSelected(sessionManager.getCurrentProject()
				//		.isInvertedSignalsView());
				break;

			case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_RESUME:
			case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_START:
			//FIXME				mainWindow.openButton.setEnabled(false);
				openFileItem.setEnabled(false);
				autoArteSelItem.setEnabled(false);
				baselineCorrItem.setEnabled(false);
			//FIXME			mainWindow.undoButton.setEnabled(false);
				undoItem.setEnabled(false);
			//FIXME			mainWindow.redoButton.setEnabled(false);
				redoItem.setEnabled(false);
			//FIXME		mainWindow.invertSignalsButton.setEnabled(false);
				break;

			case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_PAUSE:
			case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_STOP:
			//FIXME			mainWindow.openButton.setEnabled(true);
				openFileItem.setEnabled(true);
				autoArteSelItem.setEnabled(true);
				baselineCorrItem.setEnabled(true);
			//FIXME		mainWindow.undoButton.setEnabled(true);
				undoItem.setEnabled(true);
			//FIXME		mainWindow.redoButton.setEnabled(true);
				redoItem.setEnabled(true);
			//FIXME		mainWindow.invertSignalsButton.setEnabled(true);
				break;

			case SignalPerspectiveObservable.MSG_UNDOABLE_COMMAND_INVOKED:
				checkUndoableControls();
				break;

			case SignalPerspectiveObservable.MSG_NEW_BUFFER:
				sessionManager.getAutoSelectionArtefact().setCurrentData();
				sessionManager.getBaselineCorrection().setCurrentData();
				break;

			case SignalPerspectiveObservable.MSG_BASELINE_CORRECTION_INTERVAL_SELECTED:
				baselineCorrectionDialog.setSpinnersValues(
						sessionManager.getBaselineCorrection().getStartTimeStamp(), sessionManager.getBaselineCorrection()
								.getEndTimeStamp());
				baselineCorrectionDialog.setActualLocationAndVisibility();
				break;

			case SignalPerspectiveObservable.MSG_SHOW_IMPORT_DIALOG:
//				mainWindow.setEnabled(false);
				break;

			case SignalPerspectiveObservable.MSG_MODAL_DIALOG_CLOSED:
//				mainWindow.setEnabled(true);
				break;

			case SignalPerspectiveObservable.MSG_INVERTED_SIGNALS_VIEW_CHANGED:
			//FIXME		mainWindow.invertSignalsButton.setSelected(sessionManager.getCurrentProject()
					//	.isInvertedSignalsView());
				break;
			}
		} else if (o instanceof LanguageObservable) {
			currState = (Integer) state;
			if (currState == LanguageObservable.MSG_LANGUAGE_CHANGED) {
				updateText();
			}
		}
	}

}