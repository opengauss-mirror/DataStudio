/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.huawei.mppdbide.adapter.gauss.GaussDatatypeUtils;
import com.huawei.mppdbide.debuger.vo.VariableVo;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;

/**
 * Title: class
 * Description: The Class VariableTableWindowCore.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class VariableTableWindowCore extends TableWindowCore<VariableVo> {
    private static enum TitleDesc {
        VARIABLE("变量"),
        VALUE("值"),
        DATA_TYPE("数据类型");

        /**
         * Title Description
         */
        public final String desc;
        TitleDesc(String title) {
            this.desc = title;
        }
    }

    /**
     * Get the column title.
     *
     * @return List<String> the column title list
     */
    protected List<String> getTitle() {
        return Arrays.asList(TitleDesc.values())
                .stream()
                .map(desc -> desc.desc)
                .collect(Collectors.toList());
    }

    /**
     * Get the column label provider.
     *
     * @param index the index of the column
     * @return ColumnLabelProvider the column label provider for column index
     */
    protected ColumnLabelProvider getProvider(int index) {
        return new CurColumnLabelProvider(TitleDesc.values()[index]);
    }

    /**
     * Get the list element.
     *
     * @return List<VariableVo> the list element
     */
    protected List<VariableVo> getListElement() {
        try {
            return DebugServiceHelper.getInstance().getDebugService().getVariables();
        } catch (SQLException e) {
            return new ArrayList<VariableVo>();
        }
    }

    /**
     * Title: class
     * Description: The Class CreateCurrentColumnLabelProvider.
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @version [openGauss DataStudio 1.0.1, 04,12,2020]
     * @since 04,12,2020
     */
    protected static class CurColumnLabelProvider extends ColumnLabelProvider {
        private TitleDesc desc;

        /**
         * Instantiates a new current column label provider.
         */
        public CurColumnLabelProvider(TitleDesc desc) {
            this.desc = desc;
        }

        /**
         * Gets the text.
         *
         * @param element the element
         * @return String the text
         */
        @Override
        public String getText(Object element) {
            String result = "";
            if (!(element instanceof VariableVo)) {
                return result;
            }
            VariableVo variableVo = (VariableVo) element;
            switch (desc) {
            case VARIABLE:
                result = variableVo.name;
                break;
            case VALUE:
                result = variableVo.value.toString();
                if ("NULL".equals(result)) {
                    result = "";
                }
                break;
            case DATA_TYPE:
                int value = variableVo.dtype.intValue();
                result = GaussDatatypeUtils.getDataTypeHashMap().get(value).getTypename();
                break;
            default:
                break;
            }
            return result;
        }
    }
}