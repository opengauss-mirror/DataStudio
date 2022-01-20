/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.view.core.edittabledata;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ITableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.IEditTableDataCore;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.component.grid.CommitInputData;
import com.huawei.mppdbide.view.component.grid.UserPromptMessageForUnqCons;
import com.huawei.mppdbide.view.prefernces.EditTableOptionProviderForPreferences;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.ui.terminal.ColumnsListDialog;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableDataResultDisplayUIManager.
 *
 * @since 3.0.0
 */
public class EditTableDataResultDisplayUIManager extends AbstractEditTableDataResultDisplayUIManager {

    /**
     * Instantiates a new edits the table data result display UI manager.
     *
     * @param core1 the core 1
     */
    public EditTableDataResultDisplayUIManager(IEditTableDataCore core1) {
        super(core1);

    }

    /**
     * Gets the progress label.
     *
     * @return the progress label
     */
    protected String getProgressLabel() {
        String progressLabel = null;
        if (core.getTable() instanceof TableMetaData) {
            TableMetaData tableMetaData = (TableMetaData) core.getTable();
            progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(tableMetaData.getName(),
                    tableMetaData.getNamespace().getName(), tableMetaData.getDatabaseName(),
                    tableMetaData.getServerName(), IMessagesConstants.EDIT_TABLE_COMMIT_PROGRESS_NAME);
        }
        return progressLabel;
    }

    /**
     * Gets the save conditional input.
     *
     * @param dataProvider the data provider
     * @return the save conditional input
     */
    protected CommitInputData getSaveConditionalInput(IDSEditGridDataProvider dataProvider) {
        if (null != super.rememberedUserOptions) {
            return super.rememberedUserOptions;
        }

        CommitInputData saveUserOptions = null;
        if (core.getTable() instanceof TableMetaData) {
            saveUserOptions = getSaveOptions(dataProvider);
        }
        if (null == saveUserOptions) {
            return null;
        } else if (saveUserOptions.getRemember()) {
            super.rememberedUserOptions = saveUserOptions;
            this.editTableGridComponent.savedUserOption();
            // Send event -> remembered user option.
        }
        return saveUserOptions;
    }

    /**
     * Gets the save options.
     *
     * @param dataProvider the data provider
     * @return the save options
     */
    public static CommitInputData getSaveOptions(IDSEditGridDataProvider dataProvider) {
        final int optionSelectedZero = 0;
        final int optionSelectedOne = 1;
        Shell activeShell = Display.getDefault().getActiveShell();
        CommitInputData commitData = new CommitInputData();

        List<String> selectedColnames = null;
        selectedColnames = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        if (dataProvider.isDistributionColumnsRequired()) {
            boolean uniqueConsPresent = ((IDSEditGridDataProvider) dataProvider).isUniqueKeyPresent();
            TableMetaData tableMetaData = (TableMetaData) ((IDSEditGridDataProvider) dataProvider).getTable();
            if (!uniqueConsPresent) {
                UserPromptMessageForUnqCons promptMsg = getPromptMsgDialog(dataProvider, activeShell, tableMetaData);
                int optionSelected = promptMsg.open();

                switch (optionSelected) {
                    case optionSelectedZero: {
                        handleOnOptionSelectedZero(dataProvider, selectedColnames, tableMetaData);
                        break;
                    }
                    case optionSelectedOne: {
                        ColumnsListDialog colListDlg = new ColumnsListDialog(activeShell, tableMetaData,
                                dataProvider.getTableName(), dataProvider.getColumnNames(),
                                dataProvider.getColumnDataTypeNames());
                        colListDlg.open();
                        if (colListDlg.getOkPressed()) {
                            selectedColnames = colListDlg.getUserSelectedColumnNames();
                        } else {
                            return null;
                        }
                        break;
                    }
                    default: {
                        return null;
                    }
                }
                commitData.setRemember(promptMsg.isUserSelectionOfRemember());
            }

        }
        boolean isAtomic = !PreferenceWrapper.getInstance().getPreferenceStore()
                .getBoolean(EditTableOptionProviderForPreferences.EDITTABLE_COMMIT_ON_FAILURE);
        commitData.setAtomic(isAtomic);
        commitData.setUniqueKeys(selectedColnames);
        return commitData;
    }

    private static UserPromptMessageForUnqCons getPromptMsgDialog(IDSEditGridDataProvider dataProvider,
            Shell activeShell, TableMetaData table) {
        UserPromptMessageForUnqCons promptMsg = new UserPromptMessageForUnqCons(activeShell,
                MessageConfigLoader.getProperty(IMessagesConstants.EDIT_DATA_WIZARD_TITLE),
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, EditTableDataResultDisplayUIManager.class),
                MessageConfigLoader.getProperty(IMessagesConstants.NO_PHYSICAL_UNQ_KEY_MSG)
                        + (table != null ? table.getDisplayName() : dataProvider.getTableName())
                        + MPPDBIDEConstants.LINE_SEPARATOR
                        + MessageConfigLoader.getProperty(IMessagesConstants.DEFINE_UNQ_KEY_MSG),
                MessageDialog.WARNING, null, 1);
        return promptMsg;
    }

    private static void handleOnOptionSelectedZero(IDSEditGridDataProvider dataProvider, List<String> selectedColnames,
            ITableMetaData table) {
        if (table != null) {
            if (table instanceof TableMetaData) {
                int columnMetadaListSize = ((TableMetaData) table).getColumnMetaDataList().size();
                for (int cnt = 0; cnt < columnMetadaListSize; cnt++) {
                    selectedColnames.add(((TableMetaData) table).getColumnMetaDataList().get(cnt).getName());
                }
            }

        } else {
            selectedColnames.addAll(dataProvider.getColumnNames());
        }
    }
}
