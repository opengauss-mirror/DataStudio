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

package org.opengauss.mppdbide.view.component.grid;

import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommitInputData.
 *
 * @since 3.0.0
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
