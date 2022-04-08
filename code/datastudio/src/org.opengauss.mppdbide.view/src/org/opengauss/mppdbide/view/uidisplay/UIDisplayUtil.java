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

package org.opengauss.mppdbide.view.uidisplay;

import org.eclipse.e4.core.contexts.IEclipseContext;

import org.opengauss.mppdbide.eclipse.dependent.EclipseContextDSKeys;
import org.opengauss.mppdbide.eclipse.dependent.EclipseInjections;
import org.opengauss.mppdbide.view.core.ConsoleCoreWindow;
import org.opengauss.mppdbide.view.core.ConsoleMessageWindow;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIDisplayUtil.
 *
 * @since 3.0.0
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
