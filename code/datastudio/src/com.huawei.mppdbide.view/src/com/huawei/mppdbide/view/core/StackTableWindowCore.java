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

import com.huawei.mppdbide.debuger.vo.StackVo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;

/**
 * Title: class
 * Description: The Class StackTableWindowCore.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class StackTableWindowCore extends TableWindowCore<StackVo> {
    private static enum TitleDesc {
        INVOKING_LEVEL(IMessagesConstants.DEBUG_STACK_INVOKING_LEVEL),
        FUNCTION_INFO(IMessagesConstants.DEBUG_STACK_FUNCTION_INFO);

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
     * @return List<StackVo> the list element
     */
    @Override
    protected List<StackVo> getListElement() {
        try {
            return DebugServiceHelper.getInstance().getDebugService().getStacks();
        } catch (SQLException e) {
            return new ArrayList<StackVo>();
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
            if (!(element instanceof StackVo)) {
                return result;
            }
            StackVo variableVo = (StackVo) element;
            switch (desc) {
            case INVOKING_LEVEL:
                result = variableVo.level.toString();
                break;
            case FUNCTION_INFO:
                result = variableVo.targetname;
                if ("NULL".equals(result)) {
                    result = "";
                }
                break;
            default:
                break;
            }
            return result;
        }
    }
}