/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

import java.util.List;
import java.util.Objects;

/**
 * Title: CreateTriggerUiData for use
 * Description: the class CreateTriggerUiData
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [DataStudio for openGauss 2021-04-26]
 * @since 2021-04-26
 */
public class CreateTriggerUiData {
    /**
     * Title: ErrType for use
     * Description: this use to show err msg of ui param
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @version [DataStudio for openGauss 2021-04-26]
     * @since 2021-04-26
     */
    public enum ErrType {
        ERR_SUCCESS("success"),
        ERR_TRIGGERNAME("\u89e6\u53d1\u5668\u540d\u4e0d\u80fd\u4e3a\u7a7a"),
        ERR_FUNCNAME("\u89e6\u53d1\u5668\u51fd\u6570\u4e0d\u80fd\u4e3a\u7a7a"),
        ERR_TABLENAME("\u89e6\u53d1\u8868\u540d\u4e0d\u80fd\u4e3a\u7a7a"),
        ERR_OPERATE("\u64cd\u4f5c\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a"),
        ERR_COLUMN("\u66f4\u65b0\u64cd\u4f5c\u9700\u8981\u52fe\u9009\u9700\u8981\u66f4\u65b0\u7684\u5217\u540d");
        /**
         * The error message
         */
        public final String errMsg;
        ErrType(String errMsg) {
            this.errMsg = errMsg;
        }
    }

    private String triggerName;
    private String period;
    private List<String> operate;
    private List<String> column;
    private String schemaName;
    private String tableName;
    private String level;
    private String oldName;
    private String newName;
    private String condition;
    private String functionName;

    /**
     * Valid input
     *
     * @return ErrType the error type
     */
    public ErrType valid() {
        if ("".equals(triggerName)) {
            return ErrType.ERR_TRIGGERNAME;
        }
        if ("".equals(this.functionName)) {
            return ErrType.ERR_FUNCNAME;
        }
        if ("".equals(tableName)) {
            return ErrType.ERR_TABLENAME;
        }
        if (Objects.isNull(operate) || operate.size() == 0) {
            return ErrType.ERR_OPERATE;
        }
        return ErrType.ERR_SUCCESS;
    }

    /**
     * Gets the trigger define
     *
     * @return String the trigger define
     */
    public String getTriggerDefine() {
        StringBuilder sb = new StringBuilder(128);
        boolean canCondition = true;
        sb.append("CREATE TRIGGER " + triggerName + " ");
        sb.append(this.period);
        if (this.period.equals(TriggerKeyword.INSTEAD_OF.keyword)) {
            canCondition = false;
        }
        sb.append(System.lineSeparator());
        StringBuilder op = new StringBuilder(128);
        for (String event : this.operate) {
            op.append(" OR ").append(event);
            if (event.equals(TriggerKeyword.UPDATE.keyword)) {
                op.append(obtainUpdateColumn());
            }
        }
        sb.append(op.toString().substring(3));
        sb.append(System.lineSeparator());
        sb.append(" ON " + schemaName + "." + tableName);
        sb.append(System.lineSeparator());
        sb.append(" FOR EACH " + level);
        sb.append(System.lineSeparator());
        if (canCondition && condition != null && !"".equals(condition.trim())) {
            sb.append(" WHEN " + condition);
            sb.append(System.lineSeparator());
        }
        sb.append(" EXECUTE PROCEDURE " + functionName + "();");
        return sb.toString();
    }

    /**
     * Obtains the update column
     *
     * @return String the column string
     */
    public String obtainUpdateColumn() {
        if (column.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append(" OF ");
        for (String c : column) {
            sb.append(c).append(", ");
        }
        String obtainStr = sb.toString();
        return obtainStr.substring(0, obtainStr.length() - 2);
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<String> getOperate() {
        return operate;
    }

    public void setOperate(List<String> operate) {
        this.operate = operate;
    }

    public List<String> getColumn() {
        return column;
    }

    public void setColumn(List<String> column) {
        this.column = column;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }
}
