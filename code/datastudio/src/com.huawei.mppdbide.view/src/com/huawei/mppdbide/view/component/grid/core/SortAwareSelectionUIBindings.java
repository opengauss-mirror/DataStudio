/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
