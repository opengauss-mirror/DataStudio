/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.InvalidDataException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ColumnLayout.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class ColumnLayout extends Layout {

    private static final String RECALCULATE_LAYOUT = "recalculateKey";

    /**
     * The number of extra pixels taken as horizontal trim by the table column.
     * To ensure there are N pixels available for the content of the column,
     * assign N+COLUMN_TRIM for the column width.
     */
    private static int columnTrim;
    static {
        String platform = SWT.getPlatform();
        if ("win32".equals(platform)) {
            columnTrim = 4;
        } else {
            columnTrim = 3;
        }
    }

    private List<ColumnLayoutData> columns = new ArrayList<ColumnLayoutData>();

    /**
     * Adds the column data.
     *
     * @param data the data
     */
    public void addColumnData(ColumnLayoutData data) {
        columns.add(data);
    }

    /**
     * Compute table size.
     *
     * @param table the table
     * @param wHint the w hint
     * @param hHint the h hint
     * @return the point
     */
    private Point computeTableSize(Table table, int wHint, int hHint) {
        Point result = table.computeSize(wHint, hHint);

        int tableWidth = 0;
        int size = columns.size();
        for (int index = 0; index < size; ++index) {
            ColumnLayoutData layoutData = (ColumnLayoutData) columns.get(index);
            if (layoutData instanceof ColumnPixelData) {
                ColumnPixelData col = (ColumnPixelData) layoutData;
                tableWidth += col.width;
                if (col.addTrim) {
                    tableWidth += columnTrim;
                }
            } else if (layoutData instanceof ColumnWeightData) {
                ColumnWeightData col = (ColumnWeightData) layoutData;
                tableWidth += col.minimumWidth;
            } else {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_LAYOUT_ERROR));
            }
        }
        if (result != null && tableWidth > result.x) {
            result.x = tableWidth;
        }

        return result;
    }

    /**
     * Layout table.
     *
     * @param table the table
     * @param width the width
     * @param area the area
     * @param increase the increase
     * @throws InvalidDataException
     */
    private void layoutTable(final Table table, final int width, final Rectangle area, final boolean increase)
            throws InvalidDataException {
        final TableColumn[] tableColumns = table.getColumns();
        final int columnSize = Math.min(columns.size(), tableColumns.length);
        final int[] columnWidths = new int[columnSize];

        final int[] weightIter = new int[columnSize];
        int numberOfWeightCols = 0;

        int fixedColWidth = 0;
        int minWeightColWidth = 0;
        int totalColWeight = 0;

        // First calc space occupied by fixed columns
        for (int index = 0; index < columnSize; index++) {
            ColumnLayoutData col = (ColumnLayoutData) columns.get(index);
            if (col instanceof ColumnPixelData) {
                ColumnPixelData cpd = (ColumnPixelData) col;
                int pixels = cpd.width;
                if (cpd.addTrim) {
                    pixels += columnTrim;
                }
                columnWidths[index] = pixels;
                fixedColWidth += pixels;
            } else if (col instanceof ColumnWeightData) {
                ColumnWeightData cw = (ColumnWeightData) col;
                weightIter[numberOfWeightCols] = index;
                numberOfWeightCols++;
                totalColWeight += cw.weight;
                minWeightColWidth += cw.minimumWidth;
                columnWidths[index] = cw.minimumWidth;
            } else {
                throw new InvalidDataException(IMessagesConstants.COLUMN_LAYOUT_ERROR);
            }
        }

        // For columns that have a weight
        calculateClmWidth(width, columnWidths, weightIter, numberOfWeightCols, fixedColWidth, minWeightColWidth,
                totalColWeight);

        setTableSize(table, area, increase, tableColumns, columnSize, columnWidths);
    }

    /**
     * Sets the table size.
     *
     * @param table the table
     * @param area the area
     * @param increase the increase
     * @param tableColumns the table columns
     * @param columnSize the column size
     * @param columnWidths the column widths
     */
    private void setTableSize(final Table table, final Rectangle area, final boolean increase,
            final TableColumn[] tableColumns, final int columnSize, final int[] columnWidths) {
        if (increase) {
            table.setSize(area.width, area.height);
        }
        for (int indx = 0; indx < columnSize; indx++) {
            tableColumns[indx].setWidth(columnWidths[indx]);
        }
        if (!increase) {
            table.setSize(area.width, area.height);
        }
    }

    /**
     * Calculate clm width.
     *
     * @param width the width
     * @param columnWidths the column widths
     * @param weightIter the weight iter
     * @param numberOfWeightCols the number of weight cols
     * @param fixedColWidth the fixed col width
     * @param minWeightColWidth the min weight col width
     * @param totalColWeight the total col weight
     */
    private void calculateClmWidth(final int width, final int[] columnWidths, final int[] weightIter,
            int numberOfWeightCols, int fixedColWidth, int minWeightColWidth, int totalColWeight) {
        final int restIncludingMinWidths = width - fixedColWidth;
        final int rest = restIncludingMinWidths - minWeightColWidth;
        if (numberOfWeightCols > 0 && rest > 0) {
            distributeToWeightCol(columnWidths, weightIter, numberOfWeightCols, totalColWeight, restIncludingMinWidths,
                    rest);
        }
    }

    /**
     * Distribute to weight col.
     *
     * @param columnWidths the column widths
     * @param weightIter the weight iter
     * @param numberOfWeightCols the number of weight cols
     * @param totalColWeight the total col weight
     * @param restIncludingMinWidths the rest including min widths
     * @param rest the rest
     */
    private void distributeToWeightCol(final int[] columnWidths, final int[] weightIter, int numberOfWeightCols,
            int totalColWeight, final int restIncludingMinWidths, final int rest) {
        // Modify weights to reflect what each column already
        // has due to its min. Otherwise, columns with low
        // minimums get discriminated.
        int totalWantedPixels = 0;
        final int[] wantedPixels = new int[numberOfWeightCols];
        for (int pos = 0; pos < numberOfWeightCols; pos++) {
            ColumnWeightData cw = (ColumnWeightData) columns.get(weightIter[pos]);
            wantedPixels[pos] = totalColWeight == 0 ? 0 : cw.weight * restIncludingMinWidths / totalColWeight;
            totalWantedPixels += wantedPixels[pos];
        }

        // Now distribute the rest to the columns with weight.
        int totalDistributed = 0;
        for (int cnt = 0; cnt < numberOfWeightCols; ++cnt) {
            int pixels = totalWantedPixels == 0 ? 0 : wantedPixels[cnt] * rest / totalWantedPixels;
            totalDistributed += pixels;
            columnWidths[weightIter[cnt]] += pixels;
        }

        // Distribute any remaining pixels to columns with weight.
        int diff = rest - totalDistributed;
        for (int count = 0; diff > 0; count = (count + 1) % numberOfWeightCols) {
            ++columnWidths[weightIter[count]];
            --diff;
        }
    }

    /**
     * Compute size.
     *
     * @param composite the composite
     * @param wHint the w hint
     * @param hHint the h hint
     * @param flushCache the flush cache
     * @return the point
     */
    protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
        return computeTableSize(getTable(composite), wHint, hHint);
    }

    /**
     * Layout.
     *
     * @param composite the composite
     * @param flushCache the flush cache
     */
    protected void layout(Composite composite, boolean flushCache) {
        Rectangle area = composite.getClientArea();
        Table table = getTable(composite);
        int tableWidth = table.getSize().x;
        int trim = computeTrim(area, table, tableWidth);
        int width = Math.max(0, area.width - trim);

        if (width > 1) {
            try {
                layoutTable(table, width, area, tableWidth < area.width);
            } catch (InvalidDataException exception) {
                MPPDBIDELoggerUtility.error(exception.getDBErrorMessage());
                return;
            }
        }

        if (composite.getData(RECALCULATE_LAYOUT) == null) {
            composite.setData(RECALCULATE_LAYOUT, Boolean.FALSE);
            composite.layout();
        }
    }

    /**
     * Compute trim.
     *
     * @param area the area
     * @param table the table
     * @param tableWidth the table width
     * @return the int
     */
    private int computeTrim(Rectangle area, Table table, int tableWidth) {
        Point preferredSize = computeTableSize(table, area.width, area.height);
        int trim;
        if (tableWidth > 1) {
            trim = tableWidth - table.getClientArea().width;
        } else {
            // initially, the table has no extend and no client area - use the
            // border with plus some padding as educated guess
            trim = 2 * table.getBorderWidth() + 1;
        }
        if (null != preferredSize && preferredSize.y > area.height) {
            // Subtract the scrollbar width from the total column width
            // if a vertical scrollbar will be required, but is not currently
            // showing (in which case it is already subtracted above)
            ScrollBar vBar = table.getVerticalBar();
            Point vBarSize = vBar.getSize();
            trim += vBarSize.x;
        }
        return trim;
    }

    /**
     * Gets the table.
     *
     * @param composite the composite
     * @return the table
     */
    private Table getTable(Composite composite) {
        return (Table) composite.getChildren()[0];
    }
}
