/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.huawei.mppdbide.adapter.gauss.GaussDatatypeUtils;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.debuger.vo.VariableVo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;
import com.huawei.mppdbide.view.ui.debug.DebugBaseTableComposite;
import com.huawei.mppdbide.view.ui.debug.DebugCheckboxEvent;
import com.huawei.mppdbide.view.ui.debug.IDebugSourceData;
import com.huawei.mppdbide.view.ui.debug.IDebugSourceDataHeader;
import com.huawei.mppdbide.view.ui.debug.ListDebugSourceDataAdapter;
import com.huawei.mppdbide.view.utils.UIElement;

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
     * description: the max number of the variables
     */
    private static final int VARIABLES_MAX_NUM = 20;

    /**
     * description: the variable values list of last frame
     */
    private static ArrayList<String> variableValues = new ArrayList<String>();

    /**
     * description: clear variable values
     */
    public static void clearVariableValues () {
        variableValues.clear();
    }

    /**
     * description: initialize variable values
     */
    public static void initializeVariableValues () {
        for (int i = 0; i < VARIABLES_MAX_NUM; i++) {
            variableValues.add("<NULL>");
        }
    }

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
        Table table = tableComposite.getTableViewer().getTable();
        initializeVariableValues();
        table.addPaintListener(new PaintListener() {
            /**
             * Sent when a paint event occurs for the control.
             *
             * @param event an event containing information about the paint
             */
            @Override
            public void paintControl(PaintEvent event) {
                Color color = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
                if (table.getItems().length > 0) {
                    for (int i = 0; i < table.getItems().length; i++) {
                        String newValue = table.getItems()[i].getText(1);
                        String oldValue = variableValues.get(i);
                        if (!newValue.equals(oldValue)) {
                            table.getItems()[i].setBackground(color);
                            variableValues.set(i, newValue);
                        }
                    }
                }
            }
        });
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
        private final String SYSTEM_PARAMETER_STRING = "__gsdb_sql_cursor_attri_";

        private VariableVo variableVo;

        public VariableSourceData(VariableVo vo) {
            super();
            this.variableVo = vo;
            dataArrays.add(vo.name);
            dataArrays.add(getVariableValue());
            dataArrays.add(getType());
            dataArrays.add(getParamType());
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

        private String getParamType() {
            PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            IDebugObject debugObject = plSourceEditor.getDebugObject();
            ArrayList<DefaultParameter> inParamsList = debugObject
                    .getDatabase()
                    .getServer()
                    .getDefaulParametertMap()
                    .get(debugObject.getOid());
            ArrayList<DefaultParameter> outParamsList = debugObject.getOutParameters();
            List<DefaultParameter> matchedParam = Stream.concat(inParamsList.stream(), outParamsList.stream())
                    .filter(param -> {
                        if (param.getDefaultParameterName().equals(variableVo.name)) {
                            return true;
                        } else {
                            return false;
                        }
                    }).collect(Collectors.toList());
            String paramType = "";
            if (matchedParam.size() != 0) {
                paramType = matchedParam.get(0).getDefaultParameterMode().toString().toLowerCase(Locale.ENGLISH);
            } else if (variableVo.name.contains(SYSTEM_PARAMETER_STRING)) {
                paramType = "system";
            } else {
                paramType = "temp";
            }
            return paramType;
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
            return Arrays.asList(1, 1, 1, 1);
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
        DATA_TYPE(IMessagesConstants.DEBUG_VARIABLE_DATA_TYPE),
        PARAM_TYPE(IMessagesConstants.DEBUG_VARIABLE_PARAM_TYPE);

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