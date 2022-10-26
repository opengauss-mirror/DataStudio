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

package org.opengauss.mppdbide.debuger.vo.dbe;

import java.util.Map;
import java.util.Map.Entry;

import org.opengauss.mppdbide.adapter.gauss.Datatype;
import org.opengauss.mppdbide.adapter.gauss.GaussDatatypeUtils;
import org.opengauss.mppdbide.debuger.annotation.DumpFiled;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: the VariablesVo class
 *
 * @since 3.0.0
 */
public class VariablesVo {
    /**
     * varname of function
     */
    @DumpFiled
    public String varname;

    /**
     * vartype of function
     */
    @DumpFiled
    public String vartype;

    /**
     * value of function
     */
    @DumpFiled
    public String value;

    /**
     * package_name of function
     */
    @DumpFiled
    public String package_name;

    /**
     * isconst of function
     */
    @DumpFiled
    public Boolean isconst;

    /**
     * dtype of function
     */
    public Long dtype;

    /**
     * get Dtype
     *
     * @return the return value
     */
    public Long getDtype() {
        String type = DebugConstants.getDataType(this.vartype);
        if (type == null) {
            MPPDBIDELoggerUtility.error("not supported type!");
            return 0L;
        }
        Map<Integer, Datatype> map = GaussDatatypeUtils.getDataTypeHashMap();
        for (Entry<Integer, Datatype> entry : map.entrySet()) {
            if (type.equalsIgnoreCase(entry.getValue().getTypename())) {
                this.dtype = Long.valueOf(entry.getKey());
                break;
            }
        }
        return dtype;
    }

    /**
     * get varname
     *
     * @return the return value
     */
    public String getVarname() {
        return varname;
    }

    /**
     * get type
     *
     * @return the return value
     */
    public String getVartype() {
        return vartype;
    }

    /**
     * get value
     *
     * @return the return value
     */
    public String getValue() {
        return value;
    }

    /**
     * get package name
     *
     * @return the return value
     */
    public String getPackageName() {
        return package_name;
    }

    /**
     * get isConst
     *
     * @return the return value
     */
    public Boolean getIsConst() {
        return isconst;
    }
}
