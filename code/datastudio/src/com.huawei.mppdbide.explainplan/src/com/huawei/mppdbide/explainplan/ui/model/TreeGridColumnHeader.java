/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.ui.model;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface TreeGridColumnHeader.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface TreeGridColumnHeader {

    /**
     * The Constant LABEL_NODE_TYPE.
     */
    public static final String LABEL_NODE_TYPE = "Node Type";

    /**
     * The Constant LABEL_STARTUP_COST.
     */
    public static final String LABEL_STARTUP_COST = "Startup Cost";

    /**
     * The Constant LABEL_TOTAL_COST.
     */
    public static final String LABEL_TOTAL_COST = "Total Cost";

    /**
     * The Constant LABEL_ROWS.
     */
    public static final String LABEL_ROWS = "Rows";

    /**
     * The Constant LABEL_WIDTH.
     */
    public static final String LABEL_WIDTH = "Width";

    /**
     * The Constant LABEL_ACTUAL_STARTUP_TIME.
     */
    public static final String LABEL_ACTUAL_STARTUP_TIME = "Actual Startup Time";

    /**
     * The Constant LABEL_ACTUAL_TOTAL_TIME.
     */
    public static final String LABEL_ACTUAL_TOTAL_TIME = "Actual Total Time";

    /**
     * The Constant LABEL_ACTUAL_ROWS.
     */
    public static final String LABEL_ACTUAL_ROWS = "Actual Rows";

    /**
     * The Constant LABEL_ACTUAL_LOOPS.
     */
    public static final String LABEL_ACTUAL_LOOPS = "Actual Loops";

    /**
     * The Constant LABEL_ADDITIONAL_INFO.
     */
    public static final String LABEL_ADDITIONAL_INFO = "Additional Info";

    /**
     * The Constant PROP_NODE_TYPE.
     */

    public static final String PROP_NODE_TYPE = "nodeType";

    /**
     * The Constant PROP_STARTUP_COST.
     */
    public static final String PROP_STARTUP_COST = "startupCost";

    /**
     * The Constant PROP_TOTAL_COST.
     */
    public static final String PROP_TOTAL_COST = "totalCost";

    /**
     * The Constant PROP_ROWS.
     */
    public static final String PROP_ROWS = "planRows";

    /**
     * The Constant PROP_WIDTH.
     */
    public static final String PROP_WIDTH = "planWidth";

    /**
     * The Constant PROP_ACTUAL_STARTUP_TIME.
     */
    public static final String PROP_ACTUAL_STARTUP_TIME = "actualStartupTime";

    /**
     * The Constant PROP_ACTUAL_TOTAL_TIME.
     */
    public static final String PROP_ACTUAL_TOTAL_TIME = "actualTotalTime";

    /**
     * The Constant PROP_ACTUAL_ROWS.
     */
    public static final String PROP_ACTUAL_ROWS = "actualRows";

    /**
     * The Constant PROP_ACTUAL_LOOPS.
     */
    public static final String PROP_ACTUAL_LOOPS = "actualLoops";

    /**
     * The Constant PROP_ADDITIONAL_INFO.
     */
    public static final String PROP_ADDITIONAL_INFO = "additionalInfo";

    /**
     * The Constant COLUMN_LABEL_HEAVIEST.
     */
    public static final String COLUMN_LABEL_HEAVIEST = "HEAVY";

    /**
     * The Constant COLUMN_LABEL_COSTLIEST.
     */
    public static final String COLUMN_LABEL_COSTLIEST = "COSTLY";

    /**
     * The Constant COLUMN_LABEL_SLOWEST.
     */
    public static final String COLUMN_LABEL_SLOWEST = "SLOW";
}
