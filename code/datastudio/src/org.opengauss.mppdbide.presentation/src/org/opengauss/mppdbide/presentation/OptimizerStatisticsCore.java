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

package org.opengauss.mppdbide.presentation;

import org.opengauss.mppdbide.bl.serverdatacache.Database;

/**
 * 
 * Title: class
 * 
 * Description: The Class OptimizerStatisticsCore.
 * 
 * @since 3.0.0
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
