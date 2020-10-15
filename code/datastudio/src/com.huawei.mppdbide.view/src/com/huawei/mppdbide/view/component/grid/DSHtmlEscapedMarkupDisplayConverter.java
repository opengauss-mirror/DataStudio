/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.MarkupDisplayConverter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.utils.MaxSizeHelper;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSHtmlEscapedMarkupDisplayConverter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSHtmlEscapedMarkupDisplayConverter extends MarkupDisplayConverter {
    private IGridUIPreference uiPref;
    private IDataGridContext dataGridContext;

    /**
     * Instantiates a new DS html escaped markup display converter.
     *
     * @param uiPref the ui pref
     * @param dataGridContext the data grid context
     */
    public DSHtmlEscapedMarkupDisplayConverter(IGridUIPreference uiPref, IDataGridContext dataGridContext) {
        this.uiPref = uiPref;
        this.dataGridContext = dataGridContext;
    }

    /**
     * Canonical to display value.
     *
     * @param cell the cell
     * @param configRegistry the config registry
     * @param canonicalValueParam the canonical value param
     * @return the object
     */
    @Override
    public Object canonicalToDisplayValue(ILayerCell cell, IConfigRegistry configRegistry, Object canonicalValueParam) {
        Object canonicalValue = canonicalValueParam;
        IDSGridDataProvider dataProvider = dataGridContext.getDataProvider();

        if (Types.OTHER == dataProvider.getColumnDataProvider()
                .getColumnDatatype(dataProvider.getColumnDataProvider().getColumnCount() - 1)
                && canonicalValueParam != null && canonicalValue instanceof List) {
            return MPPDBIDEConstants.CURSOR_WATERMARK;

        }

        if (MPPDBIDEConstants.BLOB
                .equalsIgnoreCase(dataProvider.getColumnDataProvider().getColumnDataTypeName(cell.getColumnIndex()))
                && canonicalValueParam != null) {
            return MPPDBIDEConstants.BLOB_WATERMARK;
        }
        if (MPPDBIDEConstants.BYTEA
                .equalsIgnoreCase(dataProvider.getColumnDataProvider().getColumnDataTypeName(cell.getColumnIndex()))
                && canonicalValueParam != null) {
            return MPPDBIDEConstants.BYTEA_WATERMARK;
        }

        if (dataProvider instanceof DSObjectPropertiesGridDataProvider && canonicalValue == null) {
            return this.uiPref.getNULLValueText();
        }

        else if (null == canonicalValue) {
            return handleNullCanonicalValue(cell, dataProvider);
        } else {
            // If we convert the Bigdecimal value to String while adding to
            // Dataprovider, it consumes more memory and leads to performance
            // issue. So, now we are converting to String while display instead
            // while setting to Dataprovider
            if ("NUMERIC".equalsIgnoreCase(
                    dataProvider.getColumnDataProvider().getColumnDataTypeName(cell.getColumnIndex()))) {
                canonicalValue = ((BigDecimal) canonicalValue).toPlainString();
            }
            String value = StringEscapeUtils.escapeHtml(String.valueOf(canonicalValue));
            value = value.replaceAll("[\\s\\x00]+$", "");
            return super.canonicalToDisplayValue(cell, configRegistry, MaxSizeHelper.trimTextToLimitEndWithDots(value));
        }
    }

    private Object handleNullCanonicalValue(ILayerCell cell, IDSGridDataProvider dataProvider) {
        if (cell.getRowPosition() > 0 && !dataProvider.getAllFetchedRows().isEmpty() 
                && cell.getRowPosition() - 1 < dataProvider.getAllFetchedRows().size() 
                && dataProvider.getAllFetchedRows().get(cell.getRowPosition() - 1) != null) {
            Object[] row = dataProvider.getAllFetchedRows().get(cell.getRowPosition() - 1).getValues();
            if (cell.getColumnPosition() > 1 && ("IN").equals(row[cell.getColumnPosition() - 2])) {
                return row[cell.getColumnPosition() - 1];
            }
        } else if (dataProvider.getDatabse() != null && dataProvider.getDatabse().getDBType() == DBTYPE.OPENGAUSS) {
            List<ColumnMetaData> columnMetaDataList = dataGridContext.getColumnMetaDataList();
            if (columnMetaDataList != null && columnMetaDataList.size() > 0
                    && columnMetaDataList.get(cell.getColumnIndex()).getHasDefVal()) {
                return this.uiPref.getDefaultValueText();
            }
        } else if (!StringUtils.isEmpty(dataProvider.getColumnDataProvider().getDefaultValue(cell.getColumnIndex()))) {
            return this.uiPref.getDefaultValueText();
        }
        return this.uiPref.getNULLValueText();
    }

    /**
     * Display to canonical value.
     *
     * @param cell the cell
     * @param configRegistry the config registry
     * @param displayValue the display value
     * @return the object
     */
    @Override
    public Object displayToCanonicalValue(ILayerCell cell, IConfigRegistry configRegistry, Object displayValue) {
        if ("".equals(displayValue)) {
            return "";
        }

        if (this.uiPref.getNULLValueText().equals(displayValue)) {
            return null;
        } else {
            String value = StringEscapeUtils.unescapeHtml(String.valueOf(displayValue));
            return super.displayToCanonicalValue(cell, configRegistry, value);
        }
    }

}
