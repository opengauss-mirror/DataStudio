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

package com.huawei.mppdbide.explainplan.nodetypes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class LLVMDNDetails.
 *
 * @since 3.0.0
 */
public class LLVMDNDetails {
    @SerializedName("DN Name")
    private String dnName;

    @SerializedName("LLVM")
    private String llvm;

    /**
     * Gets the dn name.
     *
     * @return the dn name
     */
    public String getDnName() {
        return dnName;
    }

    /**
     * Gets the llvm.
     *
     * @return the llvm
     */
    public String getLlvm() {
        return llvm;
    }

    /**
     * Property details.
     *
     * @return the list
     */
    public List<Object> propertyDetails() {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);

        colInfo.add(getLlvm());
        return colInfo;
    }

    /**
     * Fill column property header.
     *
     * @return the DN intra node details column
     */
    public static DNIntraNodeDetailsColumn fillColumnPropertyHeader() {
        DNIntraNodeDetailsColumn dnLlvmDetails = new DNIntraNodeDetailsColumn();

        dnLlvmDetails.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_LLVM_COLUMNGRP));

        ArrayList<String> colnames = new ArrayList<String>(5);
        colnames.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_LLVM_COLUMN));

        dnLlvmDetails.setColCount(1);
        dnLlvmDetails.setColnames(colnames);

        return dnLlvmDetails;
    }

}
