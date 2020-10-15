/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
