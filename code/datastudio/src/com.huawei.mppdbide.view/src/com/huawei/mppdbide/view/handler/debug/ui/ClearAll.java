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

package com.huawei.mppdbide.view.handler.debug.ui;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.huawei.mppdbide.view.ui.WindowBase;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class ClearAll.
 *
 * @since 3.0.0
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