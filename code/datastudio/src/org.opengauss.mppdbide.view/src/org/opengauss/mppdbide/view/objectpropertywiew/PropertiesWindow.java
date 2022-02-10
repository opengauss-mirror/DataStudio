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

package org.opengauss.mppdbide.view.objectpropertywiew;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.view.ui.AbstractUIWindow;
import org.opengauss.mppdbide.view.ui.terminal.AbstractResultDisplayUIManager;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesWindow.
 *
 * @since 3.0.0
 */
public class PropertiesWindow extends AbstractUIWindow {
    private ViewObjectPropertiesResultDisplayUIManager uiManager;

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param part the part
     */
    @PostConstruct
    public void createPartControl(Composite parent, MPart part) {
        super.setTab(part);
        this.uiManager = (ViewObjectPropertiesResultDisplayUIManager) part.getObject();
        if (uiManager != null) {
            this.uiManager.setPart(part);
        }
        createResultWindow(parent);
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    public ViewObjectPropertiesResultDisplayUIManager getResultDisplayUIManager() {
        return (ViewObjectPropertiesResultDisplayUIManager) super.getResultDisplayUIManager();
    }

    /**
     * Gets the display UI manager.
     *
     * @return the display UI manager
     */
    @Override
    protected AbstractResultDisplayUIManager getDisplayUIManager() {
        return this.uiManager;
    }

}
