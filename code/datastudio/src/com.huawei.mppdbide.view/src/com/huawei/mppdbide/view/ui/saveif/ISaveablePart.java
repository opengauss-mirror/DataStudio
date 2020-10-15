/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.saveif;

import org.eclipse.e4.ui.workbench.modeling.ISaveHandler.Save;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISaveablePart.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface ISaveablePart {

    /**
     * Prompt user to save.
     *
     * @return the save
     */
    Save promptUserToSave();

    /**
     * Save part.
     */
    default void savePart() {

    }
}
