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

package org.opengauss.mppdbide.view.component.grid.core;

import java.sql.SQLException;
import java.util.List;
import java.util.Observer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.presentation.edittabledata.DSCursorTableDataGridDataProvider;
import org.opengauss.mppdbide.presentation.edittabledata.DSResultSetGridDataRow;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSResultRowVisitor;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.observer.DSEvent;
import org.opengauss.mppdbide.utils.observer.IDSGridUIListenable;
import org.opengauss.mppdbide.utils.observer.IDSListener;
import org.opengauss.mppdbide.view.component.GridUIPreference;
import org.opengauss.mppdbide.view.component.IGridUIPreference;
import org.opengauss.mppdbide.view.component.grid.DSGridComponent;
import org.opengauss.mppdbide.view.component.grid.GridSelectionLayerPortData;
import org.opengauss.mppdbide.view.component.grid.GridViewPortData;
import org.opengauss.mppdbide.view.ui.terminal.resulttab.GridResultDataCurrentPageExport;
import org.opengauss.mppdbide.view.ui.terminal.resulttab.GridResultDataSelectedCopyToExcel;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/** 
 * Title: DSCursorResultSetTable
 * 
 * @since 3.0.0
 */
public class DSCursorResultSetTable extends AbstractCellEditor {
    @Override
    protected Control activateCell(Composite parent, Object originalCanonicalValue) {
        Shell shell = Display.getDefault().getActiveShell();
        DSCursorResultTableData dialog = null;
        try {
            if (originalCanonicalValue instanceof List) {
                dialog = new DSCursorResultTableData(shell, (List<DSResultSetGridDataRow>) originalCanonicalValue);
                dialog.open();
            }
        } catch (SQLException | DatabaseOperationException | DatabaseCriticalException exe) {
            MPPDBIDELoggerUtility.error("failed to get cursor dailog values", exe);
        }
        super.close();
        return getEditorControl();
    }

    private static class DSCursorResultTableData extends Dialog implements IDSListener {
        private Object[] valueList;
        /**
         *  the grid component
         */
        protected DSGridComponent gridComponent;
        private IGridUIPreference resultGridUIPref;
        private IDSGridDataProvider dataProvider = null;
        private IDSResultRowVisitor visitor = null;
        private int formatIndex;
        List<DSResultSetGridDataRow> cursorGridDataRow;

        public DSCursorResultTableData(Shell parentShell, List<DSResultSetGridDataRow> originalCanonicalValue)
                throws SQLException, DatabaseOperationException, DatabaseCriticalException {
            super(parentShell);
            this.valueList = originalCanonicalValue.get(0).getValues();
            this.cursorGridDataRow = originalCanonicalValue;
            this.resultGridUIPref = new CursorResultsGridUIPref();
            this.gridComponent = new DSGridComponent(resultGridUIPref, getCursorDataProvider());
        }

        private IDSGridDataProvider getCursorDataProvider()
                throws DatabaseOperationException, DatabaseCriticalException, SQLException {
            IDSResultRowVisitor rowVisitor = getVisitor();
            for (int index = 1; index < cursorGridDataRow.size(); index++) {
                rowVisitor.visit(cursorGridDataRow.get(index));
            }
            return this.dataProvider;
        }

        private IDSResultRowVisitor getVisitor() throws DatabaseOperationException, DatabaseCriticalException {
            if (null == this.visitor) {
                DSCursorTableDataGridDataProvider cursorTableDataProvider = null;
                cursorTableDataProvider = new DSCursorTableDataGridDataProvider(valueList);
                this.dataProvider = cursorTableDataProvider;
                this.visitor = cursorTableDataProvider.initByVisitor(valueList);
                cursorTableDataProvider.setEndOfRecords();
            }
            return this.visitor;
        }

        @Override
        protected void configureShell(Shell newShell) {
            newShell.setSize(800, 400);
            super.configureShell(newShell);

            newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.CURSOR_DATA_NODE));
            newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_RESULTSET_WINDOW, this.getClass()));
            shellAlignCenter(newShell);
        }

        private void shellAlignCenter(Shell newShell) {
            Rectangle bounds = newShell.getMonitor().getBounds();
            Rectangle rect = newShell.getBounds();
            int xCordination = bounds.x + (bounds.width - rect.width) / 2;
            int yCordination = bounds.y + (bounds.height - rect.height) / 2;
            newShell.setLocation(xCordination, yCordination);
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite maincomp = (Composite) super.createDialogArea(parent);
            this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA, this);
            this.gridComponent.addListener(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM, this);
            this.gridComponent.addListener(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM, this);

            maincomp = this.gridComponent.cursorPopupComponents(maincomp, true);
            maincomp.setLayout(new GridLayout(1, false));
            GridData maincompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
            maincomp.setLayoutData(maincompGD);
            return maincomp;
        }

        private static class CursorResultsGridUIPref extends GridUIPreference {
            @Override
            public boolean isShowQueryArea() {
                return false;
            }

            @Override
            public boolean isSupportDataExport() {
                return true;
            }

            @Override
            public boolean isEnableSort() {
                return true;
            }

            @Override
            public boolean isShowLoadMoreRecordButton() {
                return false;
            }

            @Override
            public boolean isShowRightClickMenu() {
                return true;
            }

            /**
             * the isAddItemExportAll
             * 
             * @return the false
             */
            public boolean isAddItemExportAll() {
                return false;
            }
        }

        @Override
        public void handleEvent(DSEvent event) {
            handleToolbarEvents(event);

            switch (event.getType()) {
                case IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM: {
                    listenOnResultWindowCopyToExcelXlsxMenu(event);
                    break;
                }
                case IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM: {
                    listenResultWindowCopyToExcelXlsMenu(event);
                    break;
                }
                default: {
                    break;
                }
            }

        }

        private void listenResultWindowCopyToExcelXlsMenu(DSEvent event) {
            formatIndex = 1;
            GridResultDataSelectedCopyToExcel resultCopyTOExcel = new GridResultDataSelectedCopyToExcel(getSelectData(),
                    formatIndex, null, null);
            resultCopyTOExcel.addObserver((Observer) event.getObject());
            resultCopyTOExcel.export();
        }

        private void listenOnResultWindowCopyToExcelXlsxMenu(DSEvent event) {
            formatIndex = 0;
            GridResultDataSelectedCopyToExcel resultCopyTOExcel = new GridResultDataSelectedCopyToExcel(getSelectData(),
                    formatIndex, null, null);
            resultCopyTOExcel.addObserver((Observer) event.getObject());
            resultCopyTOExcel.export();
        }

        private GridSelectionLayerPortData getSelectData() {
            return this.gridComponent.getSelectDataIterator();
        }

        private void handleToolbarEvents(DSEvent event) {
            switch (event.getType()) {

                case IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA: {
                    listenOnExportCurrentPage(event);
                    break;
                }
                default: {
                    break;
                }
            }
        }

        private void listenOnExportCurrentPage(DSEvent event) {
            GridResultDataCurrentPageExport resultDataExporter = new GridResultDataCurrentPageExport(getUIData(), null,
                    null, MPPDBIDEConstants.RETURN_RESULT_COL_VALUE);
            resultDataExporter.addObserver((Observer) event.getObject());

            resultDataExporter.export(true);

        }

        private GridViewPortData getUIData() {
            return this.gridComponent.getUIDataIterator();
        }

    }

    @Override
    public Control createEditorControl(Composite parent) {
        return null;
    }

    @Override
    public Control getEditorControl() {
        return null;
    }

    @Override
    public void setEditorValue(Object value) {

    }

    @Override
    public Object getEditorValue() {
        return null;
    }
}