/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: DSFormatterRecordTypeDeclarationRules
 * 
 * Description: The Class DSFormatterRecordTypeDeclarationRules.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
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
