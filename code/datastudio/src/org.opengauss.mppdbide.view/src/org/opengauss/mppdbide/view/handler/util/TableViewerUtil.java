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

package org.opengauss.mppdbide.view.handler.util;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class TableViewerUtil.
 *
 * @since 3.0.0
 */
public abstract class TableViewerUtil {

    /**
     * Creates the columns.
     *
     * @param cols the cols
     * @param viewer the viewer
     */
    public static void createColumns(String[] cols, TableViewer viewer) {
        ColumnViewerToolTipSupport.enableFor(viewer);

        int index = 0;

        if (null == cols || cols.length == 0) {
            return;
        }

        int cnt = 0;
        int colSize = cols.length;
        TableViewerColumn viewerColumn = null;
        TableColumn column = null;
        ColumnLabelProvider columnLabelProvider = null;

        final Point point = new Point(5, 5);

        for (; cnt < colSize; cnt++) {

            // : Has to get Kalyan's approval and suppress
            final int columnIndex = index;
            viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
            column = viewerColumn.getColumn();
            column.setText(cols[cnt]);
            column.setResizable(true);

            columnLabelProvider = getColumnLabelProvider(point, columnIndex);

            viewerColumn.setLabelProvider(columnLabelProvider);
            column.pack();
            index++;
        }

    }

    private static ColumnLabelProvider getColumnLabelProvider(final Point point, final int columnIndex) {
        return new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof String[]) {
                    String[] str = (String[]) element;
                    return str[columnIndex];
                }

                return "";
            }

            @Override
            public String getToolTipText(Object element) {
                if (element instanceof String[]) {
                    String[] row = (String[]) element;
                    return getFirst10Lines(row[columnIndex]);
                }

                return "";
            }

            @Override
            public Point getToolTipShift(Object object) {
                return point;
            }

            @Override
            public int getToolTipTimeDisplayed(Object object) {
                return MPPDBIDEConstants.SHOWTOOLTIPFOR;
            }

            @Override
            public int getToolTipDisplayDelayTime(Object object) {
                return MPPDBIDEConstants.SHOWTOOLTIPAFTER;
            }

        };
    }

    /**
     * Dispose current columns.
     *
     * @param viewer the viewer
     */
    public static void disposeCurrentColumns(TableViewer viewer) {
        TableColumn[] columns = viewer.getTable().getColumns();

        int index = 0;
        int colSize = columns.length;
        for (; index < colSize; index++) {
            if (null != columns[index]) {
                columns[index].dispose();
            }
        }
    }

    /**
     * Gets the first 10 lines.
     *
     * @param code the code
     * @return the first 10 lines
     */
    static String getFirst10Lines(String code) {
        StringBuilder toolTiptext = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (null != code) {
            String[] totalLines = code.split(MPPDBIDEConstants.LINE_SEPARATOR);

            int length = totalLines.length;
            if (length > 10) {
                int cnt = 0;
                int index = 0;
                String line = null;

                for (; index < length; index++) {
                    line = totalLines[index];
                    if (line.length() > 80) {
                        line = line.substring(0, 80) + "...";
                    }

                    toolTiptext.append(line).append(MPPDBIDEConstants.LINE_SEPARATOR);
                    if (cnt >= 9) {
                        break;
                    } else {
                        cnt++;
                    }
                }
                toolTiptext.append("...");
            } else {
                int mcnt = 0;
                String line = null;

                for (; mcnt < length; mcnt++) {
                    line = totalLines[mcnt];
                    if (line.length() > 80) {
                        line = line.substring(0, 80) + "...";
                    }
                    toolTiptext.append(line).append(MPPDBIDEConstants.LINE_SEPARATOR);
                }
            }
        }

        return toolTiptext.toString();
    }

}
