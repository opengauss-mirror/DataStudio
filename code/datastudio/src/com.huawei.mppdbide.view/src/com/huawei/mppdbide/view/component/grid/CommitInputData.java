/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommitInputData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CommitInputData {

    private List<String> uniqueKeys;
    private boolean isAtomic;
    private boolean isRemember;

    /**
     * Instantiates a new commit input data.
     */
    public CommitInputData() {

    }

    /**
     * Gets the unique keys.
     *
     * @return the unique keys
     */
    public List<String> getUniqueKeys() {
        return uniqueKeys;
    }

    /**
     * Checks if is atomic.
     *
     * @return true, if is atomic
     */
    public boolean isAtomic() {
        return isAtomic;
    }

    /**
     * Sets the unique keys.
     *
     * @param uniqueKeys the new unique keys
     */
    public void setUniqueKeys(List<String> uniqueKeys) {
        this.uniqueKeys = uniqueKeys;
    }

    /**
     * Sets the atomic.
     *
     * @param isAtmic the new atomic
     */
    public void setAtomic(boolean isAtmic) {
        this.isAtomic = isAtmic;
    }

    /**
     * Sets the remember.
     *
     * @param isRmember the new remember
     */
    public void setRemember(boolean isRmember) {
        this.isRemember = isRmember;
    }

    /**
     * Gets the remember.
     *
     * @return the remember
     */
    public boolean getRemember() {
        return this.isRemember;
    }
}
