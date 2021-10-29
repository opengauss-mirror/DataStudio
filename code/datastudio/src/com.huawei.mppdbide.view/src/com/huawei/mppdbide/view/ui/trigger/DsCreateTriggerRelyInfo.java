/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.view.handler.trigger.TriggerUtils;

/**
 * Title: class
 * Description: The implements Class CreateTriggerRelyInfo.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 2021-4-30]
 * @since 2021-4-30
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
    public Namespace getNamespace() {
        return this.namespace;
    }
}
