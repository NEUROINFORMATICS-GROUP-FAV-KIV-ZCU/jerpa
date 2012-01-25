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
 *    Copyright (C) 2009 - 2011 
 *    							University of West Bohemia, 
 *                  Department of Computer Science and Engineering, 
 *                  Pilsen, Czech Republic
 */
package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.ethz.origo.jerpa.application.exception.CorruptedFileException;
import ch.ethz.origo.jerpa.data.BufferCreator;
import ch.ethz.origo.jerpa.data.tier.DaoFactory;
import ch.ethz.origo.jerpa.data.tier.dao.DaoException;
import ch.ethz.origo.jerpa.data.tier.dao.DataFileDao;
import ch.ethz.origo.jerpa.data.tier.pojo.DataFile;
import ch.ethz.origo.jerpa.data.tier.pojo.Experiment;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.data.ConfigPropertiesLoader;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.SignalProjectWriter;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.ArtefactSelectionDialog;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.BaselineCorrectionDialog;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.SignalsPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging.AveragingPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.head.ChannelsPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.importdialog.ImportDialogProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.info.SignalInfoProvider;
import ch.ethz.origo.juigle.application.JUIGLEErrorParser;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.exception.ProjectOperationException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.application.observers.JUIGLEObservable;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.prezentation.JUIGLEFileChooser;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import ch.ethz.origo.juigle.prezentation.dialogs.AboutDialog;
import ch.ethz.origo.juigle.prezentation.dialogs.AboutRecord;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.menu.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * Class represented Perspective for application <code>JERPA</code>. This is the
 * main perspective of application JERPA. Contains component for display EEG
 * signals and tools for automatic artifact selection, baseline correction, EEG
 * signals info and a lot of others functions.
 *
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.4.0 (4/3/2011)
 * @see Perspective
 * @see IObserver
 * @since 0.1.0 (05/18/09)
 */
public class SignalPerspective extends Perspective implements IObserver {

    /**
     * Only for serialization
     */
    private static final long serialVersionUID = 3313465073940475745L;
    private static final String RB_RESOURCE_KEY = "perspective.signalprocessing.lang";
    // file menu items
    private JUIGLEMenuItem fileMenu;
    private JUIGLEMenuItem openFileItem;
    private JUIGLEMenuItem saveFileItem;
    private JUIGLEMenuItem saveAsFileItem;
    private JUIGLEMenuItem closeFileItem;
    private JUIGLEMenuItem importItem;
    private JUIGLEMenuItem exitItem;
    // edit menu items
    private JUIGLEMenuItem editMenu;
    private JUIGLEMenuItem baselineCorrItem;
    private JUIGLEMenuItem autoArteSelItem;
    // view menu items
    private JUIGLEMenuItem viewMenu;
    private JUIGLEMenuItem channelItem;
    private JUIGLEMenuItem editInfoWinItem;
    private JUIGLEMenuItem signalsWinItem;
    private JUIGLEMenuItem averagingWinItem;
    // help menu items
    private JUIGLEMenuItem helpItem;
    // private JUIGLEMenuItem keyboardShortcutItem;
    private JUIGLEMenuItem aboutItem;
    // Grid bag constraints for mains panels
    private GridBagConstraints gbcSignalPanelProv;
    private GridBagConstraints gbcAveragingProv;
    private GridBagConstraints gbcSignalInfoProv;
    private GridBagConstraints gbcChannelProv;
    /**
     * Session manager for this perspective
     */
    private SignalSessionManager sessionManager;
    private SignalPerspectiveObservable spObservable;
    private SignalInfoProvider signalInfoProvider;
    private AveragingPanelProvider averagingPanelProvider;
    private SignalsPanelProvider signalPanelProvider;
    private ChannelsPanelProvider channelPanelProvider;
    private ArtefactSelectionDialog artefactSelectionDialog;
    private BaselineCorrectionDialog baselineCorrectionDialog;

    private DataFileDao dataFileDao = DaoFactory.getDataFileDao();

    public static final String ID_PERSPECTIVE = SignalPerspective.class.getName();

    /**
     * Default constructor. Initializes required objects.
     */
    public SignalPerspective() {
        perspectiveObservable.attach(this);
        spObservable = SignalPerspectiveObservable.getInstance();
        spObservable.attach(this);
        sessionManager = new SignalSessionManager();
        resourcePath = LangUtils.getPerspectiveLangPathProp(SignalPerspective.RB_RESOURCE_KEY);
    }

    @Override
    public void initPerspectivePanel() throws PerspectiveException {
        super.initPerspectivePanel();
        mainPanel.setLayout(new GridBagLayout());
        try {
            signalInfoProvider = new SignalInfoProvider(sessionManager);
            averagingPanelProvider = new AveragingPanelProvider(sessionManager);
            signalPanelProvider = new SignalsPanelProvider(sessionManager);
            channelPanelProvider = new ChannelsPanelProvider(sessionManager);

            gbcSignalInfoProv = new GridBagConstraints();
            gbcSignalInfoProv.gridx = 0;
            gbcSignalInfoProv.gridy = 1;
            gbcSignalInfoProv.anchor = GridBagConstraints.CENTER;
            gbcSignalInfoProv.fill = GridBagConstraints.BOTH;
            gbcSignalInfoProv.weightx = 0.4;
            gbcSignalInfoProv.weighty = 0.5;

            gbcChannelProv = new GridBagConstraints();
            gbcChannelProv.gridx = 0;
            gbcChannelProv.gridy = 0;
            gbcChannelProv.anchor = GridBagConstraints.CENTER;
            gbcChannelProv.fill = GridBagConstraints.BOTH;
            gbcChannelProv.weightx = 0.4;
            gbcChannelProv.weighty = 0.5;

            gbcSignalPanelProv = new GridBagConstraints();
            gbcSignalPanelProv.gridx = 1;
            gbcSignalPanelProv.gridy = 0;
            gbcSignalPanelProv.anchor = GridBagConstraints.CENTER;
            gbcSignalPanelProv.fill = GridBagConstraints.BOTH;
            gbcSignalPanelProv.weightx = 0.6;
            gbcSignalPanelProv.weighty = 0.5;
            gbcSignalPanelProv.insets = new Insets(0, 0, 0, 0);

            gbcAveragingProv = new GridBagConstraints();
            gbcAveragingProv.gridx = 1;
            gbcAveragingProv.gridy = 1;
            gbcAveragingProv.anchor = GridBagConstraints.CENTER;
            gbcAveragingProv.fill = GridBagConstraints.BOTH;
            gbcAveragingProv.weightx = 0.6;
            gbcAveragingProv.weighty = 0.5;

            mainPanel.add(signalInfoProvider.getPanel(), gbcSignalInfoProv);
            mainPanel.add(channelPanelProvider.getPanel(), gbcChannelProv);
            mainPanel.add(signalPanelProvider.getPanel(), gbcSignalPanelProv);
            mainPanel.add(averagingPanelProvider.getPanel(), gbcAveragingProv);
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
            // menu.addMenuSeparator();
            initAndAddToolbarItems();
            menu.addMenuSeparator();
            // menuTitledPanel.add(menu);
            menuTaskPane.add(menu);
        } catch (JUIGLEMenuException e1) {
            throw new PerspectiveException(e1);
        }
    }

    public Icon getPerspectiveIcon() {
        return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH
                + "icon.gif");
    }

    @Override
    public void updateText() {
        super.updateText();
        /*
         * SwingUtilities.invokeLater(new Runnable() {
         * 
         * @Override public void run() {
         * 
         * } });
         */
    }

    @Override
    public String getID() {
        return ID_PERSPECTIVE;
    }

    /**
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
        exitItem = new JUIGLEMenuItem();
        // set Resource bundles
        fileMenu.setResourceBundleKey("menu.file");
        openFileItem.setResourceBundleKey("menu.open");
        saveFileItem.setResourceBundleKey("menu.save");
        saveAsFileItem.setResourceBundleKey("menu.saveAs");
        closeFileItem.setResourceBundleKey("menu.close.project");
        importItem.setResourceBundleKey("menu.import");
        exitItem.setResourceBundleKey("menu.exit");
        // add menu separator
        closeFileItem.addSeparator();
        importItem.addSeparator();
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
        fileMenu.addSubItem(exitItem);

        return fileMenu;
    }

    /**
     * @return
     * @since 0.1.0
     */
    private JUIGLEMenuItem initAndGetEditMenuItem() {
        editMenu = new JUIGLEMenuItem(getLocalizedString("menu.edit"));
        baselineCorrItem = new JUIGLEMenuItem();
        autoArteSelItem = new JUIGLEMenuItem();
        //
        editMenu.setResourceBundleKey("menu.edit");
        baselineCorrItem.setResourceBundleKey("menu.edit.baselinecorrection");
        autoArteSelItem.setResourceBundleKey("menu.edit.autoselarte");
        //
        setEditMenuActions();
        //
        editMenu.addSubItem(baselineCorrItem);
        editMenu.addSubItem(autoArteSelItem);

        return editMenu;
    }

    /**
     * @return
     * @version 0.1.1 (3/20/2010)
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
        // set actions
        signalsWinItem.setAction(getSignalViewAction());
        averagingWinItem.setAction(getAveragePanelViewAction());
        editInfoWinItem.setAction(getSignalInfoPanelViewAction());
        channelItem.setAction(getChannelPanelViewAction());
        //
        viewMenu.addSubItem(channelItem);
        viewMenu.addSubItem(editInfoWinItem);
        viewMenu.addSubItem(signalsWinItem);
        viewMenu.addSubItem(averagingWinItem);

        return viewMenu;
    }

    /**
     * Get items of menu Help.
     * <p/>
     * TODO : enable keyboard shortcut item
     *
     * @return
     * @since 0.1.0
     */
    private JUIGLEMenuItem initAndGetHelpMenuItem() {
        helpItem = new JUIGLEMenuItem(getLocalizedString("menu.help"));
        // keyboardShortcutItem = new JUIGLEMenuItem();
        aboutItem = new JUIGLEMenuItem();
        //
        /*
        * keyboardShortcutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
        * KeyEvent.CTRL_MASK));
        */
        //
        helpItem.setResourceBundleKey("menu.help");
        /*
         * keyboardShortcutItem.setResourceBundleKey("menu.help.keyboard.shortcuts");
         */
        aboutItem.setResourceBundleKey("menu.help.about.signalprocessing");
        //
        setHelpMenuActions();
        // helpItem.addSubItem(keyboardShortcutItem);
        helpItem.addSubItem(aboutItem);
        return helpItem;
    }

    /**
     * @version 0.1.3 (3/24/2010)
     * @since 0.1.1
     */
    private void setFileMenuActions() {
        Action importAct = new AbstractAction() {

            /** Only for serialization */
            private static final long serialVersionUID = 5259515978643788611L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    new ImportDialogProvider(sessionManager);
                } catch (PerspectiveException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        // init exit app action
        Action exitAct = new AbstractAction() {

            private static final long serialVersionUID = -1644285485867277600L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                JUIGLEObservable.getInstance().setState(
                        JUIGLEObservable.MSG_APPLICATION_CLOSING);
            }
        };
        Action closeFileAct = new AbstractAction() {

            private static final long serialVersionUID = -1644285485867277600L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                sessionManager.closeFile();
            }
        };
        openFileItem.setAction(getOpenFileAction());
        saveFileItem.setAction(getSaveAction());
        saveAsFileItem.setAction(getSaveAsAction());
        importItem.setAction(importAct);
        exitItem.setAction(exitAct);
        closeFileItem.setAction(closeFileAct);
    }

    /**
     * @return
     * @version 0.1.0 (3/21/2010)
     * @since 0.3.5 (3/21/2010)
     */
    private Action getSaveAction() {
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
        return saveAction;
    }

    /**
     * @return
     * @version 0.1.0 (3/21/2010)
     * @since 0.3.5 (3/21/2010)
     */
    private Action getSaveAsAction() {
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
        return saveAsAct;
    }

    /**
     * @version 0.1.2 (4/14/2010)
     * @since 0.1.0
     */
    private void setEditMenuActions() {
        Action autoArtefactSelAct = new AbstractAction() {

            /** Only for serialization */
            private static final long serialVersionUID = -1644285485867277600L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                artefactSelectionDialog.setActualLocationAndVisibility();
            }
        };
        Action baselineDialogAct = new AbstractAction() {

            /** Only for serialization */
            private static final long serialVersionUID = -1644285485867277600L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                baselineCorrectionDialog.setActualLocationAndVisibility();
            }
        };
        /*
         * Action undo = new AbstractAction() { private static final long
         * serialVersionUID = 5757513024618899851L;
         * 
         * @Override public void actionPerformed(ActionEvent e) {
         * sessionManager.undo(); } }; Action redo = new AbstractAction() { private
         * static final long serialVersionUID = -3728905182946743343L;
         * 
         * @Override public void actionPerformed(ActionEvent e) {
         * sessionManager.redo(); } };
         */

        autoArteSelItem.setAction(autoArtefactSelAct);
        baselineCorrItem.setAction(baselineDialogAct);
        /*
         * undoItem.setAction(undo); redoItem.setAction(redo);
         */
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
                        AboutDialog ad;
                        try {
                            ad = new AboutDialog(LangUtils.getPerspectiveLangPathProp(SignalPerspective.RB_RESOURCE_KEY), JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH + "signaly.gif"), true);
                            String[] authors = ConfigPropertiesLoader.getListOfAuthors();
                            String[] contributions = ConfigPropertiesLoader.getListOfContributions();
                            AboutRecord ar = new AboutRecord();
                            for (String auth : authors) {
                                ar.addAuthor(auth);
                            }
                            for (String contri : contributions) {
                                ar.addContribution(contri);
                            }
                            ad.setAboutRecord(ar);
                            ad.setVisible(true);
                        } catch (JUIGLELangException e) {
                            SignalPerspective.logger.error(e.getMessage(), e);
                        }

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
     * @param obj
     * @version 0.1.0
     * @since 0.2.0
     */
    private void makeUpdate(Object obj) {
    }

    /**
     * @version 0.1.0 (3/21/2010)
     * @since 0.3.5 (3/21/2010)
     */
    private void initAndAddToolbarItems() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JUIGLEMenuItem openTBItem = new JUIGLEMenuItem();
                JUIGLEMenuItem saveTBItem = new JUIGLEMenuItem();
                // set actions
                openTBItem.setAction(getOpenFileAction());
                saveTBItem.setAction(getSaveAction());
                // set tooltip
                openTBItem.setToolTipText(getLocalizedString("toolbar.open.tooltip"));
                openTBItem.setToolTipResourceBundleKey("toolbar.open.tooltip");
                saveTBItem.setToolTipText(getLocalizedString("toolbar.save.tooltip"));
                saveTBItem.setToolTipResourceBundleKey("toolbar.save.tooltip");
                // set icons
                try {
                    openTBItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
                            JERPAUtils.IMAGE_PATH + "folder_48.png", 32, 32));
                    saveTBItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
                            JERPAUtils.IMAGE_PATH + "save_48.png", 32, 32));
                } catch (PerspectiveException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                menu.addItem(openTBItem);
                menu.addItem(saveTBItem);
                menu.revalidate();
                menu.repaint();
            }
        });
    }

    /**
     * @throws JUIGLEMenuException
     * @version 0.1.1 (3/20/2010)
     * @since 0.2.1 (2/14/2010)
     */
    private void addOpenedFunctionsToMenu() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    JUIGLEMenuItem signalPanelItem = new JUIGLEMenuItem();
                    JUIGLEMenuItem infoPanelItem = new JUIGLEMenuItem();
                    JUIGLEMenuItem channelPanelItem = new JUIGLEMenuItem();
                    JUIGLEMenuItem averagePanelItem = new JUIGLEMenuItem();
                    // set actions
                    signalPanelItem.setAction(getSignalViewAction());
                    averagePanelItem.setAction(getAveragePanelViewAction());
                    infoPanelItem.setAction(getSignalInfoPanelViewAction());
                    // tooltip
                    signalPanelItem.setToolTipText(getLocalizedString("toolbar.view.signalwin"));
                    signalPanelItem.setToolTipResourceBundleKey("toolbar.view.signalwin");
                    infoPanelItem.setToolTipText(getLocalizedString("toolbar.view.infowin"));
                    infoPanelItem.setToolTipResourceBundleKey("toolbar.view.infowin");
                    averagePanelItem.setToolTipText(getLocalizedString("toolbar.view.averagewin"));
                    averagePanelItem.setToolTipResourceBundleKey("toolbar.view.averagewin");
                    // init images
                    signalPanelItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
                            JERPAUtils.IMAGE_PATH + "icon.gif", 24, 24));
                    infoPanelItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
                            JERPAUtils.IMAGE_PATH + "info-48.png", 24, 24));
                    averagePanelItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
                            JERPAUtils.IMAGE_PATH + "averages32.png", 24, 24));
                    menu.addMenuSeparator();
                    menu.addItem(signalPanelItem);
                    menu.addItem(infoPanelItem);
                    menu.addItem(averagePanelItem);
                } catch (PerspectiveException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JUIGLEMenuException e) {
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

    private Action getSignalViewAction() {
        Action signPanelShow = new AbstractAction() {

            private static final long serialVersionUID = 5222085948094114535L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                openComponentInDialog(signalPanelProvider.getPanel(),
                        gbcSignalPanelProv);
            }
        };
        return signPanelShow;
    }

    private Action getAveragePanelViewAction() {
        Action averagePanelShow = new AbstractAction() {

            private static final long serialVersionUID = 5222085948094114535L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                openComponentInDialog(averagingPanelProvider.getPanel(),
                        gbcAveragingProv);
            }
        };
        return averagePanelShow;
    }

    private Action getChannelPanelViewAction() {
        Action channelPanelShow = new AbstractAction() {

            private static final long serialVersionUID = 5222085948094114535L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                openComponentInDialog(channelPanelProvider.getPanel(), gbcChannelProv);
            }
        };
        return channelPanelShow;
    }

    private Action getSignalInfoPanelViewAction() {
        Action signalInfoPanelShow = new AbstractAction() {

            private static final long serialVersionUID = 5222085948094114535L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                openComponentInDialog(signalInfoProvider.getPanel(), gbcSignalInfoProv);
            }
        };
        return signalInfoPanelShow;
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
                fileChooser.setFileFilter(new FileNameExtensionFilter(
                        "All supported files (*.edf, *.erpa, *.generator, *.rec, *.vhdr, *.xml)",
                        "edf", "esp", "generator", "rec", "vhdr", "xml"));

                fileChooser.setAcceptAllFileFilterUsed(false);

                if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    openFile(file);
                }
            }
        };
        return open;
    }

    public boolean openFile(File file) {
        try {
            sessionManager.loadFile(file);
            addOpenedFunctionsToMenu();
            spObservable.setState(SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED);
        } catch (ProjectOperationException ex) {
            JUIGLErrorInfoUtils.showErrorDialog(
                    ex.getMessage(),
                    ex.getLocalizedMessage(),
                    ex);
            return false;
        }

        return true;
    }

    /**
     * Files from db are first copied in same directory, then they are opened using FileSystem approach.
     * After the visualization is done, files are removed from file system.
     * @param file data file to be visualized
     * @return success true/false
     * @throws DaoException issue with DAO
     */
    public boolean openFileFromDB(DataFile file) throws DaoException {
        List<Experiment> exp = new ArrayList<Experiment>();
        exp.add(file.getExperiment());
        List<DataFile> files = dataFileDao.getAllFromExperiments(exp);
        List<File> locFiles = new ArrayList<File>();

        for (DataFile tmpFile : files) {
            File tempFile = dataFileDao.getFile(tmpFile);
            File newFile = new File(tmpFile.getFilename());
            tempFile.renameTo(newFile);
            locFiles.add(newFile);
        }

        boolean result = openFile(dataFileDao.getFile(file));

        for(File tmpFile : locFiles){
            tmpFile.delete();
        }

        return result;
    }

    public void createNewArtefactDialog() {
        try {
            artefactSelectionDialog = new ArtefactSelectionDialog(sessionManager);
            baselineCorrectionDialog = new BaselineCorrectionDialog(sessionManager);
        } catch (JUIGLELangException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Open component in a new <code>JXDialog</code>.
     *
     * @param component   which will be added to the dialog
     * @param gbcPosition {@link GridBagConstraints} GridBagConstraints position
     * @version 0.1.0 (3/20/2010)
     * @since 0.3.4 (3/20/2010)
     */
    private void openComponentInDialog(final JComponent component,
                                       final GridBagConstraints gbcPosition) {
        JXDialog dialog = new JXDialog(component);
        dialog.setMinimumSize(new Dimension(800, 600));
        dialog.setAlwaysOnTop(true);
        // dialog.pack();
        dialog.setLocation(JUIGLEGraphicsUtils.getCenterPosition(dialog));
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                addSignalPanelToContentPane(component, gbcPosition);
            }

            public void windowClosed(WindowEvent e) {
                addSignalPanelToContentPane(component, gbcPosition);
            }

            private void addSignalPanelToContentPane(Component component,
                                                     GridBagConstraints gbcPosition) {
                mainPanel.add(component, gbcPosition);
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });
    }

    /**
     * @param object
     * @param state
     * @version 0.1.1 (4/17/2010)
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
                    // FIXME mainWindow.saveButton.setEnabled(false);
                    autoArteSelItem.setEnabled(false);
                    baselineCorrItem.setEnabled(false);
                    // FIXME mainWindow.invertSignalsButton.setEnabled(false);
                    break;

                case SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
                    // FIXME addOpenedProjectsToMenu(sessionManager.getProjectsNames(), 0);
                    sessionManager.getAutoSelectionArtefact().setCurrentData();
                    sessionManager.getBaselineCorrection().setCurrentData();
                    saveAsFileItem.setEnabled(true);
                    // FIXME mainWindow.saveButton.setEnabled(true);
                    saveFileItem.setEnabled(true);
                    closeFileItem.setEnabled(true);
                    autoArteSelItem.setEnabled(true);
                    baselineCorrItem.setEnabled(true);
                    createNewArtefactDialog();
                    // FIXME mainWindow.invertSignalsButton.setEnabled(true);
                    // FIXME
                    // mainWindow.invertSignalsButton.setSelected(sessionManager.getCurrentProject()
                    // .isInvertedSignalsView());
                    break;

                case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_RESUME:
                case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_START:
                    // FIXME mainWindow.openButton.setEnabled(false);
                    openFileItem.setEnabled(false);
                    autoArteSelItem.setEnabled(false);
                    baselineCorrItem.setEnabled(false);
                    // FIXME mainWindow.undoButton.setEnabled(false);
                    // FIXME mainWindow.redoButton.setEnabled(false);
                    // FIXME mainWindow.invertSignalsButton.setEnabled(false);
                    break;

                case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_PAUSE:
                case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_STOP:
                    // FIXME mainWindow.openButton.setEnabled(true);
                    openFileItem.setEnabled(true);
                    autoArteSelItem.setEnabled(true);
                    baselineCorrItem.setEnabled(true);
                    // FIXME mainWindow.undoButton.setEnabled(true);
                    // FIXME mainWindow.redoButton.setEnabled(true);
                    // FIXME mainWindow.invertSignalsButton.setEnabled(true);
                    break;

                case SignalPerspectiveObservable.MSG_UNDOABLE_COMMAND_INVOKED:
                    /* Do nothing yet - not implemented */
                    break;

                case SignalPerspectiveObservable.MSG_NEW_BUFFER:
                    sessionManager.getAutoSelectionArtefact().setCurrentData();
                    sessionManager.getBaselineCorrection().setCurrentData();
                    break;

                case SignalPerspectiveObservable.MSG_BASELINE_CORRECTION_INTERVAL_SELECTED:
                    baselineCorrectionDialog.setSpinnersValues(sessionManager.getBaselineCorrection().getStartTimeStamp(), sessionManager.getBaselineCorrection().getEndTimeStamp());
                    baselineCorrectionDialog.setActualLocationAndVisibility();
                    break;

                case SignalPerspectiveObservable.MSG_SHOW_IMPORT_DIALOG:
                    // mainWindow.setEnabled(false);
                    // FIXME not used yet
                    break;

                case SignalPerspectiveObservable.MSG_MODAL_DIALOG_CLOSED:
                    // mainWindow.setEnabled(true);
                    // FIXME not used yet
                    break;

                case SignalPerspectiveObservable.MSG_INVERTED_SIGNALS_VIEW_CHANGED:
                    // FIXME
                    // mainWindow.invertSignalsButton.setSelected(sessionManager.getCurrentProject()
                    // .isInvertedSignalsView());
                    break;
                case SignalPerspectiveObservable.MSG_SEND_CURRENT_PROJECT:
                    spObservable.sendObjectToObservers(sessionManager.getCurrentProject());
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