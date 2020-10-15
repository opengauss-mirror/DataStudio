/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ChangeIndexTablespace.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ChangeIndexTablespace {

    /**
     * 
     * Title: class
     * 
     * Description: The Class ChangeIndexTablespaceInner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class ChangeIndexTablespaceInner extends UserInputDialog {
        private final IndexMetaData idx;
        private final String idxTableSapaceName;
        private ArrayList<Tablespace> tblspaces = new ArrayList<Tablespace>(4);
        private String indexName;
        private String userInput;

        /**
         * Instantiates a new change index tablespace inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param idx the idx
         * @param idxTableSapaceName the idx table sapace name
         */
        private ChangeIndexTablespaceInner(Shell parent, Object serverObject, IndexMetaData idx,
                String idxTableSapaceName) {
            super(parent, serverObject);
            this.idx = idx;
            this.idxTableSapaceName = idxTableSapaceName;
        }

        @Override
        public void performOkOperation() {
            IndexMetaData indexmetadata = (IndexMetaData) getObject();
            indexName = indexmetadata.getName();
            userInput = getUserInput();

            if ("".equals(userInput)) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_TABLESPACE_NEW,
                        indexmetadata.getName()), false);
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_TABLESPACE_FOR, indexName),
                    true);
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(indexName,
                    indexmetadata.getTable().getName(), indexmetadata.getNamespace().getName(),
                    indexmetadata.getDatabase().getName(), indexmetadata.getDatabase().getServerName(),
                    IMessagesConstants.CHANGE_INDEX_TBLSPACE_PROGRESS_NAME);
            ChangeIndexTablespaceWorker worker = new ChangeIndexTablespaceWorker(progressLabel, indexmetadata,
                    userInput, MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CHANGE_INDEX_TBLSPACE),
                    this);
            worker.schedule();

        }

        @Override
        protected Object userInputControl(Composite comp) {
            tblspaces = ((IndexMetaData) getObject()).getTable().getNamespace().getDatabase().getServer()
                    .getTablespaceGroup().getSortedServerObjectList();
            Iterator<Tablespace> itr = tblspaces.iterator();
            boolean hasNext = itr.hasNext();
            Combo tblspaceCombo = new Combo(comp, SWT.READ_ONLY);
            tblspaceCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            tblspaceCombo.forceFocus();

            final ControlDecoration deco = new ControlDecoration(tblspaceCombo, SWT.TOP | SWT.LEFT);

            // use an existing image
            Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

            // set description and image
            deco.setImage(image);

            // always show decoration
            deco.setShowOnlyOnFocus(false);

            Tablespace tblspace = null;
            int index = 0;
            int selectedindex = -1;
            while (hasNext) {
                tblspace = itr.next();
                if (tblspace.getName().equals(idxTableSapaceName)) {
                    selectedindex = index;
                }
                tblspaceCombo.add(tblspace.getName());
                hasNext = itr.hasNext();
                index++;
            }
            tblspaceCombo.select(selectedindex);

            return tblspaceCombo;
        }

        @Override
        protected String getUserInput() {
            int selectedIdx = ((Combo) inputControl).getSelectionIndex();
            if (selectedIdx < 0) {
                return "";
            }
            return tblspaces.get(selectedIdx).getName();
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_TABLESPACE_TITLE);
        }

        @Override
        protected String getHeader() {
            IndexMetaData index = (IndexMetaData) getObject();
            return MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_TABLESPACE_SELECT, index.getName());
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            close();
            String oldTblSpace = idxTableSapaceName != null ? idxTableSapaceName
                    : MessageConfigLoader.getProperty(IMessagesConstants.DFLT_CLM_UI);
            String message = MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_TABLESPACE_TO, oldTblSpace,
                    userInput);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));

            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (null != objectBrowserModel) {

                objectBrowserModel.refreshObject(idx.getParent());
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            if (!checkIsDialogDisposed()) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_TABLESPACE_CONN_ERROR,
                        indexName, MPPDBIDEConstants.LINE_SEPARATOR), false);
                enableButtons();
            }
        }

        /**
         * Check is dialog disposed.
         *
         * @return true, if successful
         */
        private boolean checkIsDialogDisposed() {
            return this.getShell().isDisposed();
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            if (!checkIsDialogDisposed()) {
                String msg = exception.getServerMessage();
                if (null == msg) {
                    msg = exception.getDBErrorMessage();
                }

                printErrorMessage(msg, false);
                enableButtons();
            }

        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {

        }
        
        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_INDEX, this.getClass());
        }
    }

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof IndexMetaData) {
            final IndexMetaData idx = (IndexMetaData) obj;
            final String idxTableSapaceName = idx.getTablespc();
            UserInputDialog changeTablespaceDialog = new ChangeIndexTablespaceInner(shell, idx, idx,
                    idxTableSapaceName);

            changeTablespaceDialog.open();
        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        IndexMetaData index = IHandlerUtilities.getSelectedIndex();
        if (index == null) {
            return false;
        }

        return true;
    }
}
