/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.debuger.exception.DebugPositionNotFoundException;
import com.huawei.mppdbide.debuger.service.SourceCodeService;
import com.huawei.mppdbide.debuger.vo.StackVo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;
import com.huawei.mppdbide.view.ui.debug.DebugBaseTableComposite;
import com.huawei.mppdbide.view.ui.debug.DebugCheckboxEvent;
import com.huawei.mppdbide.view.ui.debug.IDebugSourceData;
import com.huawei.mppdbide.view.ui.debug.IDebugSourceDataHeader;
import com.huawei.mppdbide.view.ui.debug.ListDebugSourceDataAdapter;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class StackTableWindow.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class StackTableWindow extends WindowBase<StackVo> {
    /**
     * create UI controls
     *
     * @param parent the parent control
     */
    @PostConstruct
    public void createControls(Composite parent) {
        tableComposite = new DebugBaseTableComposite(parent, 0);
        tableComposite.initUi();
        tableComposite.createColumns(new StackSourceDataHeader());
        tableComposite.setTableHandler(this);
    }

    @Override
    protected IDebugSourceData baseVoToSourceData(StackVo objVo) {
        return new StackSourceData(objVo);
    }

    @Override
    protected List<StackVo> getDataList() {
        try {
            return DebugServiceHelper.getInstance().getDebugService().getStacks();
        } catch (SQLException e) {
            return new ArrayList<StackVo>();
        }
    }

    private static class StackSourceData extends ListDebugSourceDataAdapter {
        /**
         * the input stackVo object
         */
        protected StackVo stackVo;

        public StackSourceData(StackVo stackVo) {
            super();
            this.stackVo = stackVo;
            dataArrays.add(getLevel());
            dataArrays.add(targetName());
            dataArrays.add(getLineNum());
        }

        @Override
        public boolean isShowOrder() {
            return false;
        }

        private String getLevel() {
            return stackVo.level.toString();
        }

        private String targetName() {
            String result = stackVo.targetname;
            if ("NULL".equals(result)) {
                result = "";
            }
            return result;
        }

        private String getLineNum() {
            int codeLine = stackVo.linenumber;
            DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
            SourceCodeService codeService = serviceHelper.getCodeService();
            int showLine = -1;
            try {
                showLine = codeService.codeLine2ShowLine(codeLine);
            } catch (DebugPositionNotFoundException dbgExp) {
                MPPDBIDELoggerUtility.error("get breakpoint line failed!" + dbgExp.getMessage());
            }
            return String.valueOf(++showLine);
        }
    }

    private static class StackSourceDataHeader implements IDebugSourceDataHeader {
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
        INVOKING_LEVEL(IMessagesConstants.DEBUG_STACK_INVOKING_LEVEL),
        FUNCTION_INFO(IMessagesConstants.DEBUG_STACK_FUNCTION_INFO),
        CURRENT_LINE_NUM(IMessagesConstants.DEBUG_STACK_CURRENT_LINE_NUM);

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
        if (event == DebugCheckboxEvent.DOUBLE_CLICK) {
            selectItems.stream().forEach(item -> {
                int lineNum = -1;
                DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
                SourceCodeService codeService = serviceHelper.getCodeService();
                try {
                    lineNum = codeService.getBeginDebugCodeLine();
                } catch (DebugPositionNotFoundException debugExp) {
                    MPPDBIDELoggerUtility.error("receive invalid position:" + debugExp.toString());
                    return;
                }
                PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
                plSourceEditor.highlightStack(lineNum);
            });
        }
    }
}