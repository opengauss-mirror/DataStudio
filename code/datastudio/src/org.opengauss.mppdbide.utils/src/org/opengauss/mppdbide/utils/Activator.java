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

package org.opengauss.mppdbide.utils;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * Title: class
 * 
 * Description: The Class Activator.
 *
 * @since 3.0.0
 */
public class Activator implements BundleActivator {

    private static BundleContext context;

    /**
     * Gets the context.
     *
     * @return the context
     */
    public static BundleContext getContext() {
        return context;
    }

    /**
     * Start.
     *
     * @param bundleContext the bundle context
     */
    public void start(BundleContext bundleContext) {
        setContext(bundleContext);
    }

    /**
     * Sets the context.
     *
     * @param bundleContext the new context
     */
    public static void setContext(BundleContext bundleContext) {
        Activator.context = bundleContext;
    }

    /**
     * Stop.
     *
     * @param bundleContext the bundle context
     */
    public void stop(BundleContext bundleContext) {
        removeContext();
    }

    /**
     * Removes the context.
     */
    public static void removeContext() {
        Activator.context = null;
    }

}
