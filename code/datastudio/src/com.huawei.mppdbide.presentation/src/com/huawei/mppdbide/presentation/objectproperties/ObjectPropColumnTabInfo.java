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

package com.huawei.mppdbide.presentation.objectproperties;

import com.huawei.mppdbide.bl.serverdatacache.TypeMetaData;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectPropColumnTabInfo.
 *
 * @since 3.0.0
 */
public class ObjectPropColumnTabInfo {

    private TypeMetaData datatype;
    private String datatypeSchema;
    private int precision;
    private int scale;

    /**
     * Gets the datatype.
     *
     * @return the datatype
     */
    public TypeMetaData getColDatatype() {
        return datatype;
    }

    /**
     * Sets the datatype.
     *
     * @param typeMetaData the new datatype
     */
    public void setColDataType(TypeMetaData typeMetaData) {
        this.datatype = typeMetaData;
    }

    /**
     * Gets the datatype schema.
     *
     * @return the datatype schema
     */
    public String getDataTypeSchema() {
        return datatypeSchema;
    }

    /**
     * Sets the datatype schema.
     *
     * @param datatypeSchema the new datatype schema
     */
    public void setDataTypeSchema(String datatypeSchema) {
        this.datatypeSchema = datatypeSchema;
    }

    /**
     * Gets the precision.
     *
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Sets the precision.
     *
     * @param precision the new precision
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * Gets the scale.
     *
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * Sets the scale.
     *
     * @param scale the new scale
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        StringBuilder formattedString = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        formattedString.append(getColDatatype().getName());
        if (getPrecision() > 0) {
            formattedString.append('(');
            formattedString.append(getPrecision());
            if (getScale() > 0) {
                formattedString.append(',');
                formattedString.append(getScale());
            }
            formattedString.append(')');
        }

        return formattedString.toString();

    }

}
