/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDateDisplayConverter;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.huawei.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat;
import com.huawei.mppdbide.explainplan.ui.model.TreeGridColumnHeader;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataRow;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnGroupProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.utils.ConvertTimeStampValues;
import com.huawei.mppdbide.utils.ConvertTimeValues;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.ui.table.UIUtils;
import com.huawei.mppdbide.view.utils.DateFormatUtils;
import com.huawei.mppdbide.view.utils.MaxSizeHelper;
import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGridToolTipProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSGridToolTipProvider extends NatTableContentTooltip {
    private ListDataProvider<IDSGridDataRow> gridBodyDataProvider;
    private static final String BIT_ONE = "1";
    private static final String BIT_ZERO = "0";
    private static final String BIT_DATATYPE = "bit";
    private boolean isEditSupported;
    private IDataGridContext dataGridContext;
    private static final int VARCHAR_DEF_SIZE = 2147483647;
    private static final int CHAR_OR_BIT_DEF_SIZE = 1;

    /**
     * Instantiates a new DS grid tool tip provider.
     *
     * @param gridTable the grid table
     * @param tooltipRegions the tooltip regions
     * @param dataGridContext the data grid context
     * @param bodyDataProvider the body data provider
     * @param isEditSupported the is edit supported
     */
    public DSGridToolTipProvider(NatTable gridTable, String[] tooltipRegions, IDataGridContext dataGridContext,
            ListDataProvider<IDSGridDataRow> bodyDataProvider, boolean isEditSupported) {
        super(gridTable, tooltipRegions);
        this.dataGridContext = dataGridContext;
        this.gridBodyDataProvider = bodyDataProvider;
        this.isEditSupported = isEditSupported;
    }

    /**
     * Gets the text.
     *
     * @param event the event
     * @return the text
     */
    @Override
    protected String getText(Event event) {
        LabelStack labels = this.natTable.getRegionLabelsByXY(event.x, event.y);

        int col = this.natTable.getColumnPositionByX(event.x);
        int rowPos = this.natTable.getRowPositionByY(event.y);

        ILayerCell cell = this.natTable.getCellByPosition(col, rowPos);
        LabelStack cellLabels = this.natTable.getConfigLabelsByPosition(col, rowPos);
        if (cell == null) {
            return "";
        }

        if (null == labels) {
            return "";
        }

        IDSGridDataProvider dataProvider = this.dataGridContext.getDataProvider();

        if (dataProvider instanceof ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat
                && labels.hasLabel(GridRegion.BODY)) {
            return getExplainAnalyzePlanTooltip(cell, cellLabels);
        } else if (labels.hasLabel(GridRegion.BODY)) {
            return getTextForLabelHavingBody(cell, dataProvider);
        } else if (labels.hasLabel(GridRegion.ROW_HEADER)) {
            int row = this.natTable.getRowIndexByPosition(rowPos);
            IDSGridEditDataRow editedRow = getCurrRow(row);
            if (null != editedRow && editedRow.getExecutionStatus() == EditTableRecordExecutionStatus.FAILED) {
                return editedRow.getCommitStatusMessage();
            }
        } else if (labels.hasLabel(GridRegion.COLUMN_GROUP_HEADER)) {
            return getTextForLabelHavingColumnGroupHeader(cell, dataProvider);
        } else if (labels.hasLabel(GridRegion.COLUMN_HEADER)) {
            return getColumnHeaderToolTip(cell);
        }

        return "";
    }

    private String getTextForLabelHavingColumnGroupHeader(ILayerCell cell, IDSGridDataProvider dataProvider) {
        IDSGridColumnGroupProvider columnGroupProvider = dataProvider.getColumnGroupProvider();
        String columnGroupName = "";
        if (columnGroupProvider != null) {
            columnGroupName = columnGroupProvider
                    .getColumnGroupName(columnGroupProvider.getColumnGroupIndex(cell.getColumnIndex()));
        }
        return columnGroupName;
    }

    private String getTextForLabelHavingBody(ILayerCell cell, IDSGridDataProvider dataProvider) {
        // Pass empty configregistry to avoid search markup html tags.
        String convertedDataType = CellDisplayConversionUtils.convertDataType(cell, getToolTipConfigRegistry());
        String columnDataType = dataProvider.getColumnDataProvider().getColumnDataTypeName(cell.getColumnIndex());

        if (MPPDBIDEConstants.BLOB.equalsIgnoreCase(columnDataType) && !convertedDataType.isEmpty()) {
            return MPPDBIDEConstants.BLOB_WATERMARK;
        }
        Object cellValue = cell.getDataValue();
        int dataType = dataProvider.getColumnDataProvider().getColumnDatatype(cell.getColumnIndex());
        if (Types.TIMESTAMP == dataType || Types.TIMESTAMP_WITH_TIMEZONE == dataType || Types.TIME == dataType
                || Types.TIME_WITH_TIMEZONE == dataType) {
            return handleTimeStampValues(cell, dataProvider, cellValue);
        }

        if (cellValue != null && cellValue instanceof List) {
            List<Object> cellValueObject = (List<Object>) cellValue;
            if (cellValueObject.get(0) instanceof DSResultSetGridDataRow) {
                return MessageConfigLoader.getProperty(IMessagesConstants.CURSOR_TOOLTIP_TEXT);
            }
        }
        if (MPPDBIDEConstants.RETURN_VOID.equals(cellValue)) {
            return MessageConfigLoader.getProperty(IMessagesConstants.VOID_VALUE_TOOLTIP_TEXT);
        }
        if (cell.getRowPosition() > 0 && !dataProvider.getAllFetchedRows().isEmpty()
                && cell.getRowPosition() - 1 < dataProvider.getAllFetchedRows().size()
                && dataProvider.getAllFetchedRows().get(cell.getRowPosition() - 1) != null) {
            Object[] row = dataProvider.getAllFetchedRows().get(cell.getRowPosition() - 1).getValues();
            if (cell.getColumnPosition() > 1 && cell.getColumnPosition() - 2 < row.length
                    && "IN".equals(row[cell.getColumnPosition() - 2])) {
                return row[cell.getColumnPosition() - 1].toString();
            }
        }
        return getToolTipForLabelAsBody(convertedDataType, columnDataType);
    }

    private String handleTimeStampValues(ILayerCell cell, IDSGridDataProvider dataProvider, Object cellValue) {
        if (Types.TIMESTAMP == dataProvider.getColumnDataProvider().getColumnDatatype(cell.getColumnIndex())
                || Types.TIMESTAMP_WITH_TIMEZONE == dataProvider.getColumnDataProvider()
                        .getColumnDatatype(cell.getColumnIndex())) {
            if (cellValue != null && cellValue instanceof ConvertTimeStampValues) {
                return cellValue.toString();
            }
        }
        if (Types.TIME == dataProvider.getColumnDataProvider().getColumnDatatype(cell.getColumnIndex())
                || Types.TIME_WITH_TIMEZONE == dataProvider.getColumnDataProvider()
                        .getColumnDatatype(cell.getColumnIndex())) {
            if (cellValue != null && cellValue instanceof ConvertTimeValues) {
                return cellValue.toString();
            }
        }
        return "";
    }

    private String getToolTipForLabelAsBody(String convertedDataType, String columnDataType) {
        if (MPPDBIDEConstants.BLOB.equalsIgnoreCase(columnDataType) && !convertedDataType.isEmpty()) {
            return MPPDBIDEConstants.BLOB_WATERMARK;
        } else if (MPPDBIDEConstants.BYTEA.equalsIgnoreCase(columnDataType) && !convertedDataType.isEmpty()) {
            return MPPDBIDEConstants.BYTEA_WATERMARK;
        } else {
            return getConvertedColumnData(convertedDataType, columnDataType);
        }
    }

    private String getExplainAnalyzePlanTooltip(ILayerCell cell, LabelStack cellLabels) {
        String tooltip = "";
        if (cellLabels != null && cellLabels.hasLabel(TreeGridColumnHeader.COLUMN_LABEL_HEAVIEST)) {
            tooltip = MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ANALYSIS_HEAVIEST);
            return tooltip;
        }
        if (cellLabels != null && cellLabels.hasLabel(TreeGridColumnHeader.COLUMN_LABEL_COSTLIEST)) {
            tooltip = MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ANALYSIS_COSTLIEST);
            return tooltip;
        }
        if (cellLabels != null && cellLabels.hasLabel(TreeGridColumnHeader.COLUMN_LABEL_SLOWEST)) {
            tooltip = MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ANALYSIS_SLOWEST);
            return tooltip;
        }
        Object dataValue = cell.getDataValue();
        if (dataValue == null) {
            return "";
        }
        return dataValue.toString();
    }

    private String getColumnHeaderToolTip(ILayerCell cell) {
        IDSGridDataProvider dataProvider = this.dataGridContext.getDataProvider();
        IDSGridColumnProvider columnDataProvider = dataProvider.getColumnDataProvider();
        String toolTipText = getTooltipText(cell, columnDataProvider);
        if (isEditSupported) {
            String colDatatypeName = columnDataProvider

                    .getColumnDataTypeName(cell.getColumnIndex());

            int colPrecisionVal = columnDataProvider.getPrecision(cell.getColumnIndex());
            if (dataProvider instanceof IDSEditGridDataProvider
                    && ((IDSEditGridDataProvider) dataProvider).isDistributionColumn(cell.getColumnIndex())) {
                return toolTipText + MessageConfigLoader.getProperty(IMessagesConstants.TOOLTIP_DISTRIBUTION_COL);
            } else if (dataProvider instanceof DSObjectPropertiesGridDataProvider) {
                return toolTipText;
            } else if (!GridUIUtils.isDatatypeEditSupported(colDatatypeName, colPrecisionVal)) {
                return toolTipText + MessageConfigLoader.getProperty(IMessagesConstants.TOOLTIP_READONLY_COL);
            }
        }

        return toolTipText;
    }

    private String getTooltipText(ILayerCell cell, IDSGridColumnProvider columnDataProvider) {
        String toolTipText = null;
        String datatype = UIUtils
                .convertToDisplayDatatype(columnDataProvider.getColumnDataTypeName(cell.getColumnIndex()));
        if (datatype.isEmpty()) {
            toolTipText = columnDataProvider.getColumnName(cell.getColumnIndex());
        } else {
            toolTipText = columnDataProvider.getColumnName(cell.getColumnIndex()) + ": " + datatype;
        }
        int precision = columnDataProvider.getPrecision(cell.getColumnIndex());
        int scale = columnDataProvider.getScale(cell.getColumnIndex());
        int maxLength = columnDataProvider.getMaxLength(cell.getColumnIndex());
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(toolTipText);
        if ("VARCHAR".equals(UIUtils.getDatatypeFamilyForTooltip(datatype)) && maxLength > 0
                && maxLength != VARCHAR_DEF_SIZE) {
            sb.append('(').append(maxLength).append(')');
        } else if (isCharOrBitStringTooltip(datatype, maxLength)) {
            sb.append('(').append(maxLength).append(')');
        } else if ("NUMERIC".equals(UIUtils.getDatatypeFamilyForTooltip(datatype)) && precision > 0) {
            sb.append('(').append(precision);
            if (scale > 0) {
                sb.append(',').append(scale);
            }
            sb.append(')');
        }

        String columnComment = columnDataProvider.getColumnComment(cell.getColumnIndex());
        if (StringUtils.isNotEmpty(columnComment)) {
            sb.append(MPPDBIDEConstants.NEW_LINE_SIGN).append(columnComment);
        }

        toolTipText = sb.toString();
        return toolTipText;
    }

    private boolean isCharOrBitStringTooltip(String datatype, int maxLength) {
        return ("CHAR".equals(UIUtils.getDatatypeFamilyForTooltip(datatype))
                || "BITSTRING".equals(UIUtils.getDatatypeFamilyForTooltip(datatype))) && maxLength > 0
                && maxLength != CHAR_OR_BIT_DEF_SIZE;
    }

    private String getConvertedColumnData(String convertedDataType, String columnDataType) {
        if (BIT_DATATYPE.equals(columnDataType)) {
            if (Boolean.toString(true).equalsIgnoreCase(convertedDataType)) {
                return BIT_ONE;
            } else if (Boolean.toString(false).equalsIgnoreCase(convertedDataType)) {
                return BIT_ZERO;
            }
        }
        return MaxSizeHelper.trimTextToLimitEndWithDots(convertedDataType);
    }

    private ConfigRegistry getToolTipConfigRegistry() {
        ConfigRegistry config = new ConfigRegistry();

        config.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
                new DefaultDateDisplayConverter(ITableGridStyleLabelFactory.COMMON_GRID_TIME_FORMAT),
                DisplayMode.NORMAL, ITableGridStyleLabelFactory.COL_LABEL_TIME_DATATYPE);

        config.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
                new DefaultDateDisplayConverter(ITableGridStyleLabelFactory.COMMON_GRID_DATE_FORMAT),
                DisplayMode.NORMAL, ITableGridStyleLabelFactory.COL_LABEL_DATE_DATATYPE);

        config.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
                new DefaultDateDisplayConverter(ITableGridStyleLabelFactory.COMMON_GRID_DATE_FORMAT),
                DisplayMode.NORMAL, ITableGridStyleLabelFactory.COL_LABEL_TIME_WITHTIMEZONE);

        return config;
    }

    private IDSGridEditDataRow getCurrRow(int rowIdx) {
        if (dataGridContext.getDataProvider() instanceof IDSEditGridDataProvider) {
            IDSGridDataRow currRow = this.gridBodyDataProvider.getRowObject(rowIdx);
            if (currRow instanceof IDSGridEditDataRow) {
                return (IDSGridEditDataRow) currRow;
            }
        }

        return null;
    }

    /**
     * Should create tool tip.
     *
     * @param event the event
     * @return true, if successful
     */
    @Override
    protected boolean shouldCreateToolTip(Event event) {
        if (super.shouldCreateToolTip(event)) {
            return true;
        }

        LabelStack labels = this.natTable.getRegionLabelsByXY(event.x, event.y);

        if (null != labels && labels.hasLabel(GridRegion.ROW_HEADER)) {
            if (dataGridContext.getDataProvider() instanceof IDSEditGridDataProvider) {
                int rowPos = this.natTable.getRowPositionByY(event.y);
                int row = this.natTable.getRowIndexByPosition(rowPos);

                if (row < 0) {
                    // This condition is not supposed to be failed.
                    return false;
                }

                IDSGridEditDataRow editedRow = getCurrRow(row);
                if (editedRow != null && editedRow.getExecutionStatus() == EditTableRecordExecutionStatus.FAILED) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Sets the body data provider.
     *
     * @param bodyDataProvider the new body data provider
     */
    public void setBodyDataProvider(ListDataProvider<IDSGridDataRow> bodyDataProvider) {
        this.gridBodyDataProvider = bodyDataProvider;
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        this.dataGridContext = null;
        this.gridBodyDataProvider = null;
        this.natTable = null;
    }

}
