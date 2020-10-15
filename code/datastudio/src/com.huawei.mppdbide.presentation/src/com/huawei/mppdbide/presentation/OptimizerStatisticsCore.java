/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

import com.huawei.mppdbide.bl.serverdatacache.Database;

/**
 * 
 * Title: class
 * 
 * Description: The Class OptimizerStatisticsCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class OptimizerStatisticsCore {
    private Database connectedDatabase;
    private TerminalExecutionConnectionInfra termConnection;
    private ViewOptimizerStatsWindowDetails window;

    /**
     * Instantiates a new optimizer statistics core.
     *
     * @param db the db
     */
    public OptimizerStatisticsCore(Database db) {
        this.connectedDatabase = db;
    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    public TerminalExecutionConnectionInfra getTermConnection() {
        if (null == this.termConnection) {
            this.termConnection = new TerminalExecutionConnectionInfra();
            this.termConnection.setDatabase(connectedDatabase);
        }
        return this.termConnection;
    }

    /**
     * Gets the window details.
     *
     * @return the window details
     */
    public IWindowDetail getWindowDetails() {
        if (null == this.window) {
            this.window = new ViewOptimizerStatsWindowDetails();
        }
        return this.window;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ViewOptimizerStatsWindowDetails.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class ViewOptimizerStatsWindowDetails implements IWindowDetail {

        @Override
        public String getTitle() {
            return "Optimizer Stats";
        }

        @Override
        public String getUniqueID() {
            return "Optimizer_stats";
        }

        @Override
        public String getIcon() {
            return null;
        }

        @Override
        public String getShortTitle() {
            return "Optimizer Stats";
        }

        @Override
        public boolean isCloseable() {
            return true;
        }
    }

}
