/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.editor;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ICustomDateCellEditor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface ICustomDateCellEditor {

    /**
     * Sets the canonical value.
     *
     * @param canonicalValue the new canonical value
     */
    void setCanonicalValue(Object canonicalValue);

    /**
     * Gets the canonical value.
     *
     * @return the canonical value
     */
    Object getCanonicalValue();

}
