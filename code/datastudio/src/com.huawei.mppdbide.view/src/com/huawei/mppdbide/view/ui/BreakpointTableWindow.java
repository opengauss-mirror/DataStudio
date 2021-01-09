/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.debuger.vo.BreakpointList;
import com.huawei.mppdbide.debuger.vo.BreakpointVo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.ui.debug.DebugCheckTableComposite;
import com.huawei.mppdbide.view.ui.debug.DebugCheckboxEvent;
import com.huawei.mppdbide.view.ui.debug.IDebugSourceData;
import com.huawei.mppdbide.view.ui.debug.IDebugSourceDataHeader;
import com.huawei.mppdbide.view.ui.debug.ListDebugSourceDataAdapter;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class BreakpointTableWindow.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class BreakpointTableWindow extends WindowBase<BreakpointVo> {
    /**
     * description: create UI controls
     *
     * @param parent the ui parent control
     */
    @PostConstruct
    public void createControls(Composite parent) {
        tableComposite = new DebugCheckTableComposite(parent, 0);
        tableComposite.initUi();
        tableComposite.createColumns(new BreakpointHeader());
        tableComposite.setTableHandler(this);
    }

    @Override
    protected IDebugSourceData baseVoToSourceData(BreakpointVo objVo) {
        return new BreakpointSourceData(objVo);
    }

    @Override
    protected List<BreakpointVo> getDataList() {
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

    private static class BreakpointSourceData extends ListDebugSourceDataAdapter {
        public BreakpointSourceData(BreakpointVo breakpointVo) {
            super();
            dataArrays.add(String.valueOf(breakpointVo.getLineNum()));
            dataArrays.add(String.valueOf(breakpointVo.getStatement()));
            dataArrays.add(String.valueOf(breakpointVo.getEnable()));
        }

        @Override
        public boolean isShowOrder() {
            return false;
        }

        @Override
        public boolean isEditable(int titleIndex) {
            if (titleIndex == 0) {
                return false;
            }
            return super.isEditable(titleIndex);
        }
    }

    private static class BreakpointHeader implements IDebugSourceDataHeader {
        @Override
        public List<String> getTitles() {
            return Arrays.asList(TitleDesc.values())
                    .stream()
                    .map(desc -> desc.getDesc())
                    .collect(Collectors.toList());
        }

        @Override
        public List<Integer> getTitleSizeScales() {
            return Arrays.asList(2, 5, 2);
        }
    }

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

    @Override
    public void selectHandler(List<IDebugSourceData> selectItems, DebugCheckboxEvent event) {
        selectItems.stream().filter(item -> {
            if ((DebugCheckboxEvent.ALL.getCode() & event.getCode()) <= 0x03) {
                String isDisable = String.valueOf(event.getCode() == 0x03);
                String enableOrDisable = item.getValue(2).toString();
                return enableOrDisable.equals(isDisable);
            }
            return true;
        }).forEach(item -> {
            int lineNum = Integer.parseInt(item.getValue(0).toString());
            PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            int order = -1;
            if ((DebugCheckboxEvent.ALL.getCode() & event.getCode()) <= 0x03) {
                order = 0;
            } else if ((DebugCheckboxEvent.ALL.getCode() & event.getCode()) <= 0x0C) {
                order = 1;
            } else {
                order = 2;
            }
            plSourceEditor.breakpointResponse(lineNum - 1, order);
        });
    }
}