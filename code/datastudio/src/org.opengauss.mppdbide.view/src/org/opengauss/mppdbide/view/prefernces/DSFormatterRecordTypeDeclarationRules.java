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

package org.opengauss.mppdbide.view.prefernces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: DSFormatterRecordTypeDeclarationRules
 * 
 * Description: The Class DSFormatterRecordTypeDeclarationRules.
 *
 * @since 3.0.0
 */
public class DSFormatterRecordTypeDeclarationRules {
    /** 
     * The record type align. 
     */
    @Expose
    @SerializedName("Align_datatypes")
    private boolean recordTypeAlign;

    /** 
     * The record type comma after. 
     */
    @Expose
    @SerializedName("Comma_after_datatype")
    private boolean recordTypeCommaAfter;

    /**
     * Checks if is record type align.
     *
     * @return true, if is record type align
     */
    public boolean isRecordTypeAlign() {
        return recordTypeAlign;
    }

    /**
     * Sets the record type align.
     *
     * @param recordTypeAlign the new record type align
     */
    public void setRecordTypeAlign(boolean recordTypeAlign) {
        this.recordTypeAlign = recordTypeAlign;
    }

    /**
     * Checks if is record type comma after.
     *
     * @return true, if is record type comma after
     */
    public boolean isRecordTypeCommaAfter() {
        return recordTypeCommaAfter;
    }

    /**
     * Sets the record type comma after.
     *
     * @param recordTypeCommaAfter the new record type comma after
     */
    public void setRecordTypeCommaAfter(boolean recordTypeCommaAfter) {
        this.recordTypeCommaAfter = recordTypeCommaAfter;
    }
}
