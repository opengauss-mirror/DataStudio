/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.createfunction;

import java.util.List;
import java.util.stream.Collectors;

import com.huawei.mppdbide.bl.serverdatacache.TypeMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectList;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 26,04,2021]
 * @since 26,04,2021
 */
public class DsCreateFunctionRelyInfo implements CreateFunctionRelyInfo {
    private String schameName;
    private String sourceCode = null;
    private List<String> validTypes;
    private boolean autoCompile = true;

    public DsCreateFunctionRelyInfo(ObjectGroup<?> objGroup) {
        ObjectList<TypeMetaData> types = objGroup.getDatabase().getDefaultDatatype();
        validTypes = types.getList().stream()
                .map(curType -> curType.getName())
                .collect(Collectors.toList());
    }

    public void setSchameName(String schameName) {
        this.schameName = schameName;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public boolean getAutoCompile() {
        return autoCompile;
    }

    @Override
    public String getSchameName() {
        return schameName;
    }

    @Override
    public List<String> getSupportTypes() {
        return validTypes;
    }

    @Override
    public void execute(String sourceCode, boolean autoCompile) {
        MPPDBIDELoggerUtility.info("source code:" + sourceCode);
        this.sourceCode = sourceCode;
        this.autoCompile = autoCompile;
    }

    @Override
    public String getFunctionBodyTemplate(String language) {
        if (CreateFunctionRelyInfo.PROCEDURE.equals(language)) {
            return commonTemplateBody(true);
        } else if ("C".equals(language)) {
            return cLanguageTemplateBody();
        } else if ("SQL".equals(language)) {
            return sqlLanguageTemplateBody();
        } else {
            return commonTemplateBody(false);
        }
    }

    private String cLanguageTemplateBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("AS ");
        sb.append(getLineSeparator());
        sb.append("\t'/*iso file path and name*/',$$/*function name*/$$");
        sb.append(getLineSeparator());
        sb.append(getEscapeForwardSlash());
        return sb.toString();
    }

    private String sqlLanguageTemplateBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("AS $$");
        sb.append(getLineSeparator());
        sb.append("\t/*executable_section*/");
        sb.append(getLineSeparator());
        sb.append(getLineSeparator());
        sb.append("$$");
        sb.append(getLineSeparator());
        sb.append(getEscapeForwardSlash());
        return sb.toString();
    }

    private String commonTemplateBody(boolean isProcedure) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("AS");
        if (!isProcedure) {
            sb.append(" $$");
        }
        sb.append(getLineSeparator());
        sb.append("DECLARE");
        sb.append(getLineSeparator());
        sb.append("\t/*declaration_section*/");
        sb.append(getLineSeparator());
        sb.append(getLineSeparator());

        sb.append("BEGIN");
        sb.append(getLineSeparator());
        sb.append("\t/*executable_section*/");
        sb.append(getLineSeparator());
        sb.append(getLineSeparator());

        sb.append("END;");
        if (!isProcedure) {
            sb.append("$$");
        }
        sb.append(getLineSeparator());
        sb.append(getEscapeForwardSlash());
        return sb.toString();
    }
}
