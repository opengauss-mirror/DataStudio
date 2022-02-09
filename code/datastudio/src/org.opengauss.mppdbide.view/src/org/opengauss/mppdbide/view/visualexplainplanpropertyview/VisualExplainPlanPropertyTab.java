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

package org.opengauss.mppdbide.view.visualexplainplanpropertyview;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.view.component.IGridUIPreference;
import org.opengauss.mppdbide.view.component.grid.DSGridComponent;
import org.opengauss.mppdbide.view.prefernces.PreferenceWrapper;
import org.opengauss.mppdbide.view.prefernces.UserEncodingOption;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanPropertyTab.
 *
 * @since 3.0.0
 */
public class VisualExplainPlanPropertyTab extends CTabItem {
    private DSGridComponent gridComponent;
    private Composite composite;
    private ViewExplainPlanPropertyGridUIPreference resultGridUIPref;

    /**
     * Instantiates a new visual explain plan property tab.
     *
     * @param parent the parent
     * @param style the style
     * @param composite the composite
     * @param resultsetDisplaydata the resultset displaydata
     * @param viewViewExplainPlanPropertyTabManager the view view explain plan
     * property tab manager
     */
    public VisualExplainPlanPropertyTab(CTabFolder parent, int style, Composite composite,
            IDSGridDataProvider resultsetDisplaydata,
            VisualExplainPlanPropertyTabManager viewViewExplainPlanPropertyTabManager) {
        super(parent, style);
        setControl(composite);
        this.resultGridUIPref = new ViewExplainPlanPropertyGridUIPreference();
        this.gridComponent = new VisualExplainQueryArea(resultGridUIPref, resultsetDisplaydata,
                viewViewExplainPlanPropertyTabManager.getUiPresenter());
        this.composite = composite;
    }

    /**
     * Pre destroy.
     */
    public void preDestroy() {
        resultGridUIPref = null;
        if (gridComponent != null) {
            gridComponent.onPreDestroy();
            gridComponent = null;
        }
    }

    /**
     * Inits the.
     */
    public void init() {
        this.gridComponent.createComponents(this.composite);
    }

    /**
     * Reset data.
     *
     * @param resultsetDisplaydata the resultset displaydata
     */
    public void resetData(IDSGridDataProvider resultsetDisplaydata) {
        this.gridComponent.setDataProvider(resultsetDisplaydata);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ViewExplainPlanPropertyGridUIPreference.
     */
    private static final class ViewExplainPlanPropertyGridUIPreference implements IGridUIPreference {
        private PreferenceStore prefStore;

        private ViewExplainPlanPropertyGridUIPreference() {
            prefStore = PreferenceWrapper.getInstance().getPreferenceStore();
        }

        @Override
        public boolean isEnableGlobalFuzzySearch() {
            return true;
        }

        @Override
        public boolean isAllowColumnReorder() {
            return false;
        }

        @Override
        public boolean isShowQueryArea() {
            return true;
        }

        @Override
        public boolean isShowGlobalSearchPanelOnStart() {
            return true;
        }

        @Override
        public boolean isEnableSort() {
            return false;
        }

        @Override
        public boolean isAllowRowReorder() {
            return false;
        }

        @Override
        public boolean isEnableEdit() {
            return false;
        }

        @Override
        public boolean isAllowRowHide() {
            return false;
        }

        @Override
        public boolean isSupportDataExport() {
            return false;
        }

        @Override
        public boolean isCopyWithColumnHeader() {
            return prefStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_COLUMN_HEADER);
        }

        @Override
        public boolean isCopywithRowHeader() {
            return prefStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_ROW_HEADER);
        }

        @Override
        public boolean isAllowColumnHide() {
            return false;
        }

        @Override
        public int getColumnWidth() {
            return 150;
        }

        @Override
        public ColumnWidthType getColumnWidthStrategy() {
            return ColumnWidthType.DATA_WIDTH;
        }

        @Override
        public boolean isWordWrap() {
            return true;
        }

        @Override
        public boolean isFitToOnePage() {
            return false;
        }

        @Override
        public int getMaxDisplayDataLength() {
            return 2000;
        }

        @Override
        public boolean editTableDataUIPreference() {
            return false;
        }

        @Override
        public String getNULLValueText() {
            return "";
        }

        @Override
        public boolean isNeedAdvancedCopy() {
            return true;
        }

        @Override
        public String getDefaultValueText() {
            return "";
        }

        @Override
        public boolean isAddBatchDropTool() {
            return false;
        }

        @Override
        public boolean isEditQueryResultsFlow() {
            return false;
        }

        @Override
        public boolean isShowStatusBar() {
            return false;
        }

        @Override
        public boolean isIncludeEncoding() {

            return false;
        }

        @Override
        public String getDefaultEncoding() {
            return prefStore.getString(UserEncodingOption.DATA_STUDIO_ENCODING);
        }

        @Override
        public boolean isAddItemSupported() {
            return false;
        }

        @Override
        public boolean isDeleteItemSupported() {
            return false;
        }

        @Override
        public boolean isCancelChangesSupported() {
            return false;
        }

        @Override
        public boolean isRefreshSupported() {
            return false;
        }

    }

    /**
     * Handle focus.
     */
    public void handleFocus() {
        this.gridComponent.focus();
    }

}
