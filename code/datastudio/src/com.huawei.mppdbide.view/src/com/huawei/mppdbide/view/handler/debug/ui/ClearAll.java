/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug.ui;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.huawei.mppdbide.view.ui.WindowBase;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class ClearAll.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class ClearAll implements Runnable {
    private String[] partIdArray = {"com.huawei.mppdbide.part.id.stack",
            "com.huawei.mppdbide.part.id.variable",
            "com.huawei.mppdbide.part.id.breakpoint"};

    /**
     * Instantiates a new clear all.
     */
    public ClearAll () {
    }

    /**
     * Run.
     */
    @Override
    public void run() {
        for (int i = 0; i < partIdArray.length; i++) {
            String partId = partIdArray[i];
            MPart part = UIElement.getInstance().getPartService().findPart(partId);
            if (!(part.getObject() instanceof WindowBase<?>)) {
                return;
            }
            WindowBase<?> windowBase = (WindowBase<?>) part.getObject();
            windowBase.clear();
        }
    }
}