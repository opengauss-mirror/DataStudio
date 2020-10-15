/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * Title: class
 * 
 * Description: The Class Activator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class Activator implements BundleActivator {

    private static BundleContext context;
    private static final Object LOCK = new Object();
    
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
        synchronized (LOCK) {
            setContext(bundleContext);
            bundleContext.getBundle().getBundleId();
        }
    }

    /**
     * Sets the context.
     *
     * @param bundleContext the new context
     */
    public static void setContext(BundleContext bundleContext) {
        context = bundleContext;
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
        context = null;
    }
}
