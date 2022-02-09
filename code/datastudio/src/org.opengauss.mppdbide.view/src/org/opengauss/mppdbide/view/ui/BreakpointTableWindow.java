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

package org.opengauss.mppdbide.view.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.debuger.vo.BreakpointList;
import org.opengauss.mppdbide.debuger.vo.BreakpointVo;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import org.opengauss.mppdbide.view.ui.debug.DebugCheckTableComposite;
import org.opengauss.mppdbide.view.ui.debug.DebugCheckboxEvent;
import org.opengauss.mppdbide.view.ui.debug.IDebugSourceData;
import org.opengauss.mppdbide.view.ui.debug.IDebugSourceDataHeader;
import org.opengauss.mppdbide.view.ui.debug.ListDebugSourceDataAdapter;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class BreakpointTableWindow.
 *
 * @since 3.0.0
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