/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Table;

import com.huawei.mppdbide.debuger.vo.BreakpointList;
import com.huawei.mppdbide.debuger.vo.BreakpointVo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

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
        LINE_NUM(IMessagesConstants.DEBUG_BREAKPOINT_LINE_NUM),
        STATEMENT(IMessagesConstants.DEBUG_BREAKPOINT_STATEMENT),
        ENABLE(IMessagesConstants.DEBUG_BREAKPOINT_ENABLE);

        /**
         * Title Description
         */
        private final String desc;

        TitleDesc(String title) {
            this.desc = title;
        }

        /**
         * Gets the desc
         *
         * @return String the desc
         */
        public String getDesc() {
            return MessageConfigLoader.getProperty(desc);
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
                .map(desc -> desc.getDesc())
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
        List<BreakpointVo> sortedBreakpointList = breakpointList.entrySet()
                .stream()
                .sorted((entry1, entry2) -> {
                    return entry1.getKey().compareTo(entry2.getKey());
                })
                .map(entry -> entry.getValue())
                .collect(Collectors.toList());
        return sortedBreakpointList;
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

    /**
     * Auto column width.
     *
     * @param table the table
     */
    @Override
    protected void autoColWidth(final Table table) {
        table.addControlListener(new ControlAdapter() {
            /**
             * Sent when the size (width, height) of a control changes.
             *
             * @param e an event containing information about the resize
             */
            public void controlResized(final ControlEvent e) {
                table.getColumn(0).setWidth(50);
                int oldTableWidth = 0;
                for (int i = 1; i < table.getColumnCount(); i++) {
                    oldTableWidth += table.getColumn(i).getWidth();
                }
                for (int i = 1; i < table.getColumnCount(); i++) {
                    int oldWidth = table.getColumn(i).getWidth();
                    table.getColumn(i).setWidth((table.getSize().x - table.getColumn(0).getWidth() - oldTableWidth)
                            / (table.getColumnCount() - 1) + oldWidth);
                }
            }
        });
    }
}