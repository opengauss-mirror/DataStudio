/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.Comparator;
import java.util.List;

import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGenericGridColumnProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DSGenericGridColumnProvider implements IDSGridColumnProvider {
    private List<Object[]> serverObjectProperty;
    private int colCount;

    /**
     * Inits the.
     *
     * @param serverObjectProprty the server object proprty
     */
    public void init(List<Object[]> serverObjectProprty) {
        this.serverObjectProperty = serverObjectProprty;
        this.colCount = serverObjectProprty.get(0).length;
    }

    @Override
    public int getColumnCount() {
        return colCount;
    }

    @Override
    public String[] getColumnNames() {
        return (String[]) serverObjectProperty.get(0);
    }

    @Override
    public String getColumnName(int columnIndex) {

        return (String) serverObjectProperty.get(0)[columnIndex];
    }

    @Override
    public String getColumnDesc(int columnIndex) {

        return (String) serverObjectProperty.get(0)[columnIndex];
    }

    @Override
    public int getColumnIndex(String columnLabel) {

        return 0;
    }

    @Override
    public Comparator<Object> getComparator(int columnIndex) {

        return null;
    }

    @Override
    public int getColumnDatatype(int columnIndex) {

        return 0;
    }

    @Override
    public String getColumnDataTypeName(int columnIndex) {

        return "";
    }

    @Override
    public int getPrecision(int columnIndex) {

        return 0;
    }

    @Override
    public int getScale(int columnIndex) {

        return 0;
    }

    @Override
    public int getMaxLength(int columnIndex) {

        return 0;
    }

    @Override
    public String getDefaultValue(int i) {
        return null;
    }

}
