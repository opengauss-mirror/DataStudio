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

package com.huawei.mppdbide.view.ui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Title: SwtTableDataToolTipListener
 * 
 * @since 3.0.0
 */
public class SwtTableDataToolTipListener implements Listener {
    private Table table;

    /**
     * Instantiates a new swt table data tool tip listener.
     *
     * @param table the table
     */
    public SwtTableDataToolTipListener(Table table) {
        this.table = table;
        this.table.addListener(SWT.Dispose, this);
        this.table.addListener(SWT.MouseMove, this);
        this.table.addListener(SWT.MouseHover, this);
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(Event event) {
        switch (event.type) {
            case SWT.Dispose:
            case SWT.MouseMove: {
                table.setToolTipText("");
                break;
            }
            case SWT.MouseHover: {
                Point pt = new Point(event.x, event.y);
                TableItem item = table.getItem(pt);
                if (item != null) {
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        Rectangle rect = item.getBounds(i);
                        if (rect.contains(pt)) {
                            table.setToolTipText(item.getText(i));
                        }
                    }
                } else {
                    table.setToolTipText("");
                }
                break;
            }
            default: {
                break;
            }
        }
    }
}
