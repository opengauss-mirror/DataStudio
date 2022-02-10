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

package org.opengauss.mppdbide.gauss.format.option;

import org.opengauss.mppdbide.gauss.format.consts.FormatItemsType;

/**
 * Title: OptionsProcessData
 *
 * @since 3.0.0
 */
public class OptionsProcessData implements Cloneable {

    private int offSet = 0;

    private int parentOffSet = 0;

    private int maxKeywordLength = 0;

    private int preIndentOffSet = 0;

    private boolean putStmtNewLine = true;

    private FormatItemsType formatItemsType = FormatItemsType.UNKNOWN;

    private OptionsProcessData parentData = null;

    private int lastFormatType = 0;

    /**
     * return offset value.
     *
     * @return the off set
     */
    public int getOffSet() {
        return offSet;
    }

    /**
     * set offset value.
     *
     * @param offSet the new off set
     */
    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    /**
     * add offset value.
     *
     * @param addedOffSet the added off set
     */
    public void addOffSet(int addedOffSet) {
        this.offSet += addedOffSet;
    }

    /**
     * return parent offset value.
     *
     * @return the parent off set
     */
    public int getParentOffSet() {
        return parentOffSet;
    }

    /**
     * set parent offset value.
     *
     * @param parentOffSet the new parent off set
     */
    public void setParentOffSet(int parentOffSet) {
        this.parentOffSet = parentOffSet;
    }

    /**
     * return max keyword length.
     *
     * @return the max keyword length
     */
    public int getMaxKeywordLength() {
        return maxKeywordLength;
    }

    /**
     * set max keyword length.
     *
     * @param maxKeywordLength the new max keyword length
     */
    public void setMaxKeywordLength(int maxKeywordLength) {
        this.maxKeywordLength = maxKeywordLength;
    }

    /**
     * return pre indend offset value.
     *
     * @return the pre indent off set
     */
    public int getPreIndentOffSet() {
        return preIndentOffSet;
    }

    /**
     * set pre indend offset value.
     *
     * @param preIndentOffSet the new pre indent off set
     */
    public void setPreIndentOffSet(int preIndentOffSet) {
        this.preIndentOffSet = preIndentOffSet;
    }

    /**
     * return ParentData object.
     *
     * @return the parent data
     */
    public OptionsProcessData getParentData() {
        return parentData;
    }

    /**
     * set ParentData object.
     *
     * @param parentData the new parent data
     */
    public void setParentData(OptionsProcessData parentData) {
        this.parentData = parentData;
    }

    /**
     * return formatItemsType.
     *
     * @return the format items type
     */
    public FormatItemsType getFormatItemsType() {
        return formatItemsType;
    }

    /**
     * set formatItemsType.
     *
     * @param formatItemsType the new format items type
     */
    public void setFormatItemsType(FormatItemsType formatItemsType) {
        this.formatItemsType = formatItemsType;
    }

    /**
     * return putStmtNewLine value.
     *
     * @return true, if is put stmt new line
     */
    public boolean isPutStmtNewLine() {
        return putStmtNewLine;
    }

    /**
     * set putStmtNewLine value.
     *
     * @param putStmtNewLine the new put stmt new line
     */
    public void setPutStmtNewLine(boolean putStmtNewLine) {
        this.putStmtNewLine = putStmtNewLine;
    }

    /**
     * Gets the last format type.
     *
     * @return the last format type
     */
    public int getLastFormatType() {
        return lastFormatType;
    }

    /**
     * Sets the last format type.
     *
     * @param lastFormatType the new last format type
     */
    public void setLastFormatType(int lastFormatType) {
        this.lastFormatType = lastFormatType;
    }

    /**
     * clone the data.
     *
     * @return the options process data
     */
    public OptionsProcessData clone() {

        OptionsProcessData optData = new OptionsProcessData();
        optData.setOffSet(offSet);
        optData.setParentOffSet(parentOffSet);
        optData.setMaxKeywordLength(maxKeywordLength);
        optData.setFormatItemsType(formatItemsType);
        optData.setPutStmtNewLine(putStmtNewLine);
        return optData;
    }

}
