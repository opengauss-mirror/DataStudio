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

import com.huawei.mppdbide.adapter.gauss.GaussDatatypeUtils;
import com.huawei.mppdbide.debuger.vo.VariableVo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;
import com.huawei.mppdbide.view.ui.debug.DebugBaseTableComposite;
import com.huawei.mppdbide.view.ui.debug.DebugCheckboxEvent;
import com.huawei.mppdbide.view.ui.debug.IDebugSourceData;
import com.huawei.mppdbide.view.ui.debug.IDebugSourceDataHeader;
import com.huawei.mppdbide.view.ui.debug.ListDebugSourceDataAdapter;

/**
 * Title: class
 * Description: The Class VariableTableWindow.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class VariableTableWindow extends WindowBase<VariableVo> {
    /**
     * description: create UI controls
     *
     * @param parent the parent control
     */
    @PostConstruct
    public void createControls(Composite parent) {
        tableComposite = new DebugBaseTableComposite(parent, 0);
        tableComposite.initUi();
        tableComposite.createColumns(new VariableSourceDataHeader());
        tableComposite.setTableHandler(this);
    }

    @Override
    protected IDebugSourceData baseVoToSourceData(VariableVo objVo) {
        return new VariableSourceData(objVo);
    }

    @Override
    protected List<VariableVo> getDataList() {
        try {
            return DebugServiceHelper.getInstance().getDebugService().getVariables();
        } catch (SQLException e) {
            return new ArrayList<VariableVo>();
        }
    }

    /**
     * Title: show the variable table source data
     * Description: The Class DebugEditorItem.
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author z00588921
     * @version [openGauss DataStudio 1.0.1, 04,01,2021]
     * @since 04,01,2021
     */
    private static class VariableSourceData extends ListDebugSourceDataAdapter {
        private VariableVo variableVo;

        public VariableSourceData(VariableVo vo) {
            super();
            this.variableVo = vo;
            dataArrays.add(vo.name);
            dataArrays.add(getVariableValue());
            dataArrays.add(getType());
        }

        @Override
        public boolean isShowOrder() {
            return false;
        }

        private String getVariableValue() {
            String value = variableVo.value.toString();
            if ("NULL".equals(value)) {
                value = "<NULL>";
            }
            return value;
        }

        private String getType() {
            int typeCode = variableVo.dtype.intValue();
            String type = GaussDatatypeUtils.getDataTypeHashMap().get(typeCode).getTypename();
            return type;
        }
    }

    /**
     * Title: show the variable table header
     * Description: The Class DebugEditorItem.
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author z00588921
     * @version [openGauss DataStudio 1.0.1, 04,01,2021]
     * @since 04,01,2021
     */
    private static class VariableSourceDataHeader implements IDebugSourceDataHeader {
        @Override
        public List<String> getTitles() {
            return Arrays.asList(TitleDesc.values())
                    .stream()
                    .map(desc -> desc.getDesc())
                    .collect(Collectors.toList());
        }

        @Override
        public List<Integer> getTitleSizeScales() {
            return Arrays.asList(4, 4, 2);
        }
    }

    /**
     * Title: enum variable title desc
     * Description: The Class DebugEditorItem.
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author z00588921
     * @version [openGauss DataStudio 1.0.1, 04,01,2021]
     * @since 04,01,2021
     */
    private static enum TitleDesc {
        VARIABLE(IMessagesConstants.DEBUG_VARIABLE_VARIABLE),
        VALUE(IMessagesConstants.DEBUG_VARIABLE_VALUE),
        DATA_TYPE(IMessagesConstants.DEBUG_VARIABLE_DATA_TYPE);
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
    }
}