/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.autosave;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum AutoSaveTerminalStatus.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public enum AutoSaveTerminalStatus {

    /**
     * The init.
     */
    INIT,
    /**
     * The loading.
     */
    LOADING,
    /**
     * The load finished.
     */
    LOAD_FINISHED,
    /**
     * The write finished.
     */
    WRITE_FINISHED,
    /**
     * The load failed.
     */
    LOAD_FAILED,
    /**
     * The write failed.
     */
    WRITE_FAILED
}
