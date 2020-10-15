/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminalautosave;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface SQLTerminalAutoSaveIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface SQLTerminalAutoSaveIf {

    /**
     * Checks if is file terminal flag.
     *
     * @return true, if is file terminal flag
     */
    public boolean isFileTerminalFlag();

    /**
     * Sets the file terminal flag.
     *
     * @param fileTerminalFlag the new file terminal flag
     */
    public void setFileTerminalFlag(boolean fileTerminalFlag);
}
