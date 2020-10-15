/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.uidisplay;

import org.eclipse.e4.core.contexts.IEclipseContext;

import com.huawei.mppdbide.eclipse.dependent.EclipseContextDSKeys;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIDisplayUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class UIDisplayUtil {
    /**
     * Gets the debug console.
     *
     * @return the debug console
     */
    public static ConsoleMessageWindow getDebugConsole() {
        IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
        if (eclipseContext.containsKey(EclipseContextDSKeys.DEBUG_CONSOLE_WINDOW)) {
            return (ConsoleMessageWindow) eclipseContext.get(EclipseContextDSKeys.DEBUG_CONSOLE_WINDOW);
        }

        // when not found send to global.
        return ConsoleCoreWindow.getInstance();
    }
}
