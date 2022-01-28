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
 *
 * @since 3.0.0
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
                Color color = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
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
     */
    private static class VariableSourceData extends ListDebugSourceDataAdapter {
        private static final String SYSTEM_PARAMETER_STRING = "__gsdb_sql_cursor_attri_";

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
            if (inParamsList == null) {
                inParamsList = new ArrayList<DefaultParameter>();
            }
            List<DefaultParameter> matchedParam = Stream.concat(inParamsList.stream(), outParamsList.stream())
                    .filter(param -> {
                        if (param.getDefaultParameterName().equals(variableVo.name)
                                || variableVo.name.contains("$")) {
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
            return Arrays.asList(4, 4, 2, 2);
        }
    }

    /**
     * Title: enum variable title desc
     * Description: The Class DebugEditorItem.
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