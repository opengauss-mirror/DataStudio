/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.huawei.mppdbide.debuger.vo.BreakpointList;
import com.huawei.mppdbide.debuger.vo.BreakpointVo;

/**
 * Title: class
 * Description: The Class BreakpointTableWindowCore.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class BreakpointTableWindowCore extends TableWindowCore<BreakpointVo> {
    private static enum TitleDesc {
        LINE_NUM("行号"),
        STATEMENT("语句"),
        ENABLE("语句");

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
    @Override
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
    @Override
    protected ColumnLabelProvider getProvider(int index) {
        return new CurColumnLabelProvider(TitleDesc.values()[index]);
    }

    /**
     * Get the list element.
     *
     * @return List<BreakpointVo> the list element
     */
    @Override
    protected List<BreakpointVo> getListElement() {
        Map<Integer, BreakpointVo> breakpointList = BreakpointList.getInstance();
        List<BreakpointVo> breakpointVoList = new ArrayList<BreakpointVo>();
        for (Map.Entry<Integer, BreakpointVo> entry : breakpointList.entrySet()) {
            breakpointVoList.add(entry.getValue());
        }
        return breakpointVoList;
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
            if (!(element instanceof BreakpointVo)) {
                return result;
            }
            BreakpointVo variableVo = (BreakpointVo) element;
            switch (desc) {
            case LINE_NUM:
                result = String.valueOf(variableVo.getLineNum());
                break;
            case STATEMENT:
                result = String.valueOf(variableVo.getStatement());
                break;
            case ENABLE:
                result = String.valueOf(variableVo.getEnable());
                break;
            default:
                break;
            }
            return result;
        }
    }
}