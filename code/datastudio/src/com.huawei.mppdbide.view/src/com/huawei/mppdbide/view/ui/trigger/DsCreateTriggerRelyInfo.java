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

package com.huawei.mppdbide.view.ui.trigger;

import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.view.handler.trigger.TriggerUtils;

/**
 * Title: class
 * Description: The implements Class CreateTriggerRelyInfo.
 *
 * @since 3.0.0
 */
public class DsCreateTriggerRelyInfo implements CreateTriggerRelyInfo {
    private Namespace namespace;
    private String sourceCode;

    @Override
    public void execute(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }

    @Override
    public List<String> getTableNames() {
        return TriggerUtils.getTableNames(namespace.getTables());
    }

    @Override
    public List<String> getFunctionNames() {
        return TriggerUtils.getFunctionNames(namespace.getDatabase());
    }

    @Override
    public List<CreateTriggerParam> getTableColumns(String tableName) {
        return TriggerUtils.getTableColumns(namespace.getTables(), tableName);
    }

    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public String getNamespaceName() {
        return this.namespace.getName();
    }
}
