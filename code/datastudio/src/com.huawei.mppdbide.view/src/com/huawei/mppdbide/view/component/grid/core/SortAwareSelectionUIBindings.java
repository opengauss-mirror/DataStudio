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

package com.huawei.mppdbide.view.component.grid.core;

import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.SWT;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortAwareSelectionUIBindings.
 *
 * @since 3.0.0
 */
public class SortAwareSelectionUIBindings extends AbstractUiBindingConfiguration {

    /**
     * Configure ui bindings.
     *
     * @param uiBindingRegistry the ui binding registry
     */
    @Override
    public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
        /*
         * Unregister 3 times because rowSelectionLayer, columnSelectionLayer
         * and bodySelectionLayer all register UI Bindings
         */
        for (int i = 0; i < 3; i++) {
            uiBindingRegistry.unregisterSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.NONE));
            uiBindingRegistry.unregisterSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD2));
            uiBindingRegistry.unregisterSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD1));
            uiBindingRegistry
                    .unregisterSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD2 | SWT.MOD1));
        }

        uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.NONE),
                new ViewportSelectColumnActionWrapper(false, false));
        uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD2),
                new ViewportSelectColumnActionWrapper(true, false));
        uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD1),
                new ViewportSelectColumnActionWrapper(false, true));
        uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(SWT.MOD2 | SWT.MOD1),
                new ViewportSelectColumnActionWrapper(true, true));
    }

}
