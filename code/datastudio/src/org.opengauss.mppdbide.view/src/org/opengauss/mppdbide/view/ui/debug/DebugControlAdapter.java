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

package org.opengauss.mppdbide.view.ui.debug;

import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Title: DebugControlAdapter for use
 * Description:  the control adapter
 *
 * @since 3.0.0
 */
public class DebugControlAdapter extends ControlAdapter {
    /**
     * this is min less column size
     */
    public static final int DEFAULT_SIZE_SHOW = 20;

    private Table table;
    private IDebugSourceDataHeader header;

    public DebugControlAdapter(Table table, IDebugSourceDataHeader header) {
        this.table = table;
        this.header = header;
    }

    @Override
    public void controlResized(ControlEvent e) {
        Point point = table.getSize();
        List<Integer> scales = header.getTitleSizeScales();
        int totalScale = scales.stream().mapToInt(val -> val).sum();
        TableColumn[] columns = table.getColumns();
        IntStream.iterate(0, seed -> seed + 1).limit(scales.size()).forEach(idx -> {
            TableColumn column = columns[idx];
            int columnSize = point.x * scales.get(idx) / totalScale;
            column.setWidth(columnSize < DEFAULT_SIZE_SHOW ? DEFAULT_SIZE_SHOW : columnSize);
        });
    }
}
