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

package org.opengauss.mppdbide.view.ui.trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: CreateTriggerUiData for use
 * Description: the class CreateTriggerUiData
 *
 * @since 3.0.0
 */
public class CreateTriggerUiData {
    /**
     * Title: ErrType for use
     * Description: this use to show err msg of ui param
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

    private CreateTriggerDataModel dataModel;

    public CreateTriggerUiData(CreateTriggerDataModel dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * Valid input
     *
     * @return ErrType the error type
     */
    public ErrType valid() {
        if ("".equals(dataModel.getTriggerName())) {
            return ErrType.ERR_TRIGGERNAME;
        }
        if ("".equals(dataModel.getTriggerFunc())) {
            return ErrType.ERR_FUNCNAME;
        }

        if ("".equals(dataModel.getTriggerTableName())) {
            return ErrType.ERR_TABLENAME;
        }

        List<String> operate = getOperate();
        if (operate.size() == 0) {
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
        sb.append("CREATE TRIGGER ");
        if (dataModel.getTriggerNameCase()) {
            sb.append("\"" + dataModel.getTriggerName() + "\" ");
        } else {
            sb.append(dataModel.getTriggerName() + " ");
        }
        String period = getPeriod();
        sb.append(period);
        if (period.equals(TriggerKeyword.INSTEAD_OF.keyword)) {
            canCondition = false;
        }
        sb.append(System.lineSeparator());
        StringBuilder op = new StringBuilder(128);
        List<String> operate = getOperate();
        for (String event : operate) {
            op.append(" OR ").append(event);
            if (event.equals(TriggerKeyword.UPDATE.keyword)) {
                op.append(obtainUpdateColumn());
            }
        }
        sb.append(op.toString().substring(3));
        sb.append(System.lineSeparator());
        sb.append(" ON " +
            dataModel.getTriggerNamespaceName()
            + "." + dataModel.getTriggerTableName());
        sb.append(System.lineSeparator());
        String level = getLevel();
        sb.append(" FOR EACH " + level);
        sb.append(System.lineSeparator());

        String condition = dataModel.getWhenCodition();
        if (canCondition && condition != null && !"".equals(condition.trim())) {
            sb.append(" WHEN " + condition);
            sb.append(System.lineSeparator());
        }
        sb.append(" EXECUTE PROCEDURE " + dataModel.getTriggerFunc() + "();");
        return sb.toString();
    }

    /**
     * Obtains the update column
     *
     * @return String the column string
     */
    public String obtainUpdateColumn() {
        List<String> column = dataModel.getUpdateColumn();
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

    /**
     * Gets level
     *
     * @return String the trigger level
     */
    public String getLevel() {
        TriggerKeyword[] keys = {
            TriggerKeyword.ROW,
            TriggerKeyword.STATEMENT
        };
        return keys[dataModel.isStatementLevel() ? 1 : 0].keyword;
    }

    /**
     * Gets period
     *
     * @return String the trigger period
     */
    public String getPeriod() {
        TriggerKeyword[] keys = {
            TriggerKeyword.BEFORE,
            TriggerKeyword.AFTER,
            TriggerKeyword.INSTEAD_OF};
        return keys[dataModel.getTriggerStage()].keyword;
    }

    /**
     * Gets operate
     *
     * @return List<String> the operation list
     */
    public List<String> getOperate() {
        TriggerKeyword[] keys = new TriggerKeyword[] {
            TriggerKeyword.INSERT,
            TriggerKeyword.DELETE,
            TriggerKeyword.TRUNCATE,
            TriggerKeyword.UPDATE};
        int bitPos = 0;
        List<String> results = new ArrayList<>(keys.length);
        for (int i = 0; i < keys.length ; i++) {
            if ((dataModel.getSelectOptration() & (1 <<bitPos)) != 0) {
                results.add(keys[i].keyword);
            }
            bitPos += 1;
        }
        return results;
    }
}
