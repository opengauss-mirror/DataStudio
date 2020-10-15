/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean;

import java.util.Locale;

import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.sqlparser.SQLDDLTypeEnum;
import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;

/**
 * Title: DMLParamScriptBlockInfo Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
 */
public class DMLParamScriptBlockInfo extends ScriptBlockInfoImpl {

    private boolean plBodyStart = false;

    private boolean endStmtFound = false;

    private String startKeywork = null;

    private boolean langKeywordFound = false;

    private boolean langKeywordFoundBeforeStart = false;

    private SQLDDLTypeEnum ddlType = null;

    /**
     * Instantiates a new DML param script block info.
     *
     * @param parent the parent
     * @param token the token
     * @param abstractRuleHandler the abstract rule handler
     */
    public DMLParamScriptBlockInfo(ScriptBlockInfo parent, IToken token, RuleHandlerIf abstractRuleHandler) {
        super(parent, token, abstractRuleHandler);
    }

    /**
     * Checks if is end stmt found.
     *
     * @return true, if is end stmt found
     */
    public boolean isEndStmtFound() {
        return endStmtFound;
    }

    /**
     * Sets the end stmt found.
     *
     * @param endStmtFound the new end stmt found
     */
    public void setEndStmtFound(boolean endStmtFound) {
        this.endStmtFound = endStmtFound;
    }

    /**
     * Gets the start keywork.
     *
     * @return the start keywork
     */
    public String getStartKeywork() {
        return startKeywork;
    }

    /**
     * Sets the start keywork.
     *
     * @param startKeywork the new start keywork
     */
    public void setStartKeywork(String startKeywork) {
        this.startKeywork = startKeywork;
    }

    /**
     * Checks if is lang keyword found.
     *
     * @return true, if is lang keyword found
     */
    public boolean isLangKeywordFound() {
        return langKeywordFound;
    }

    /**
     * Sets the lang keyword found.
     *
     * @param langKeywordFound the new lang keyword found
     */
    public void setLangKeywordFound(boolean langKeywordFound) {
        this.langKeywordFound = langKeywordFound;
    }

    /**
     * Checks if is lang keyword found before start.
     *
     * @return true, if is lang keyword found before start
     */
    public boolean isLangKeywordFoundBeforeStart() {
        return langKeywordFoundBeforeStart;
    }

    /**
     * Sets the lang keyword found before start.
     *
     * @param langKeywordFoundBeforeStart the new lang keyword found before
     * start
     */
    public void setLangKeywordFoundBeforeStart(boolean langKeywordFoundBeforeStart) {
        this.langKeywordFoundBeforeStart = langKeywordFoundBeforeStart;
    }

    /**
     * Checks if is pl body start.
     *
     * @return true, if is pl body start
     */
    public boolean isPlBodyStart() {
        return plBodyStart;
    }

    /**
     * Sets the pl body start.
     *
     * @param plBodyStart the new pl body start
     */
    public void setPlBodyStart(boolean plBodyStart) {
        this.plBodyStart = plBodyStart;
    }

    /**
     * Gets the ddl type.
     *
     * @return the ddl type
     */
    public SQLDDLTypeEnum getDdlType() {
        return ddlType;
    }

    /**
     * Sets the ddl type.
     *
     * @param ddlType the new ddl type
     */
    public void setDdlType(SQLDDLTypeEnum ddlType) {
        this.ddlType = ddlType;
    }

    /**
     * Sets the DDL type.
     *
     * @param latestkeyword the new DDL type
     */
    public void setDDLType(String latestkeyword) {
        switch (latestkeyword.toLowerCase(Locale.ENGLISH)) {
            case SQLFoldingConstants.SQL_PROCEDURE: {
                setDdlType(SQLDDLTypeEnum.PROCEDURE);
                break;
            }
            case SQLFoldingConstants.SQL_FUNCTION: {
                setDdlType(SQLDDLTypeEnum.FUNCTION);
                break;
            }
            case SQLFoldingConstants.SQL_TABLE: {
                setDdlType(SQLDDLTypeEnum.TABLE);
                break;
            }
            case SQLFoldingConstants.SQL_VIEW: {
                setDdlType(SQLDDLTypeEnum.VIEW);
                break;
            }
            case SQLFoldingConstants.SQL_TRIGGER: {
                setDdlType(SQLDDLTypeEnum.TRIGGER);
                break;
            }
            case SQLFoldingConstants.SQL_PACKAGE: {
                setDdlType(SQLDDLTypeEnum.PACKAGE);
                break;
            }
            default:
        }
    }
    
    /**
     * Sets the DDL type.
     *
     * @param latestkeyword the new DDL type
     */
    public void setDDLType(int tokenType) {
        if (SQLTokenConstants.T_SQL_DDL_CREATE_PROC == tokenType) {
            setDdlType(SQLDDLTypeEnum.PROCEDURE);
        } else if (SQLTokenConstants.T_SQL_DDL_CREATE_FUNC == tokenType) {
            setDdlType(SQLDDLTypeEnum.FUNCTION);
        }
    }

}
