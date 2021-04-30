/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Title: CreateTriggerControlAdapter for Trigger Dialog
 * Description:  the control adapter
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2021-4-30
 */
public class CreateTriggerControlAdapter extends ControlAdapter {
    /**
     * this is min less column size
     */
    public static final int DEFAULT_SIZE_SHOW = 20;

    private Table table;
    private CreateTriggerParamsTitle titles;

    public CreateTriggerControlAdapter(Table table, CreateTriggerParamsTitle header) {
        this.table = table;
        this.titles = header;
    }

    @Override
    public void controlResized(ControlEvent e) {
        Point point = table.getSize();
        List<Integer> scales = titles.getScales();
        int totalScale = scales.stream().mapToInt(val -> val).sum();
        TableColumn[] columns = table.getColumns();
        IntStream.iterate(0, seed -> seed + 1).limit(scales.size()).forEach(idx -> {
            TableColumn column = columns[idx];
            int columnSize = point.x * scales.get(idx) / totalScale;
            column.setWidth(columnSize < DEFAULT_SIZE_SHOW ? DEFAULT_SIZE_SHOW : columnSize);
        });
    }
}
