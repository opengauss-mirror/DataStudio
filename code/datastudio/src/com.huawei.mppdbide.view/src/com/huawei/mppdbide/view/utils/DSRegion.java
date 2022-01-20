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

package com.huawei.mppdbide.view.utils;

import org.eclipse.jface.text.IRegion;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSRegion.
 *
 * @since 3.0.0
 */
public class DSRegion implements IRegion {
    private boolean forwardFlag;
    private String partitionType;
    private int fOffset;
    private int fLength;

    /**
     * Instantiates a new DS region.
     *
     * @param offset the offset
     * @param length the length
     */
    public DSRegion(int offset, int length) {
        fOffset = offset;
        fLength = length;
    }

    /**
     * Gets the forward.
     *
     * @return the forward
     */
    public boolean getForward() {
        return forwardFlag;
    }

    /**
     * Sets the forward.
     *
     * @param isForward the new forward
     */
    public void setForward(boolean isForward) {
        this.forwardFlag = isForward;
    }

    /**
     * Gets the partition type.
     *
     * @return the partition type
     */
    public String getPartitionType() {
        return partitionType;
    }

    /**
     * Sets the partition type.
     *
     * @param partitionType the new partition type
     */
    public void setPartitionType(String partitionType) {
        this.partitionType = partitionType;
    }

    /**
     * Gets the length.
     *
     * @return the length
     */
    public int getLength() {
        return fLength;
    }

    /**
     * Gets the offset.
     *
     * @return the offset
     */
    public int getOffset() {
        return fOffset;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    public boolean equals(Object obj) {
        if (obj instanceof IRegion) {
            IRegion region = (IRegion) obj;
            return region.getOffset() == fOffset && region.getLength() == fLength;
        }
        return false;
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    public int hashCode() {
        return (fOffset << 24) | (fLength << 16);
    }

    /**
     * To string.
     *
     * @return the string
     */
    public String toString() {
        return "offset: " + fOffset + ", length: " + fLength;
    }

}
