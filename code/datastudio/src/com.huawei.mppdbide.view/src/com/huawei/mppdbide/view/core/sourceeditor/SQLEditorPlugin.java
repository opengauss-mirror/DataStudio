/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLEditorPlugin.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SQLEditorPlugin extends AbstractUIPlugin {
    private static volatile SQLEditorPlugin plugin = null;
    private static volatile SQLSyntaxColorProvider colorProvider = null;
    private SQLCodeScanner codeScanner = null;

    /**
     * The Constant SQL_PARTITIONING.
     */
    public static final String SQL_PARTITIONING = "__sql_partitioning"; // $NON-NLS-1$

    private SQLPartitionScanner fPartitionScanner;
    private static final Object INSTANCE_LOCK = new Object();

    /**
     * Gets the partition scanner.
     *
     * @return the partition scanner
     */
    public SQLPartitionScanner getPartitionScanner() {
        if (fPartitionScanner == null) {
            fPartitionScanner = new SQLPartitionScanner();
        }
        return fPartitionScanner;
    }

    /**
     * Gets the default.
     *
     * @return the default
     */
    public static SQLEditorPlugin getDefault() {
        if (null == plugin) {
            synchronized (INSTANCE_LOCK) {
                if (null == plugin) {
                    plugin = new SQLEditorPlugin();
                }
            }
        }
        return plugin;
    }

    /**
     * Gets the color provider.
     *
     * @return the color provider
     */
    public SQLSyntaxColorProvider getColorProvider() {
        if (null == colorProvider) {
            synchronized (INSTANCE_LOCK) {
                if (null == colorProvider) {
                    colorProvider = new SQLSyntaxColorProvider();
                }
            }
        }
        return colorProvider;
    }

    /**
     * Gets the SQL code scanner.
     *
     * @param sqlSyntax the sql syntax
     * @return the SQL code scanner
     */
    public SQLCodeScanner getSQLCodeScanner(SQLSyntax sqlSyntax) {
        if (null == codeScanner && sqlSyntax != null) {
            codeScanner = new SQLCodeScanner(sqlSyntax);
        }

        return codeScanner;
    }

    /**
     * Sets the SQL code scanner.
     *
     * @param sqlSyntax the new SQL code scanner
     */
    public void setSQLCodeScanner(SQLSyntax sqlSyntax) {
        codeScanner = getSQLCodeScanner(sqlSyntax);

        if (codeScanner != null) {
            codeScanner = null;
        }
    }
}
