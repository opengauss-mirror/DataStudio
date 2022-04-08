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

package org.opengauss.mppdbide.presentation.objectproperties;

import java.util.Comparator;
import java.util.List;

import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGenericGridColumnProvider.
 * 
 * @since 3.0.0
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
