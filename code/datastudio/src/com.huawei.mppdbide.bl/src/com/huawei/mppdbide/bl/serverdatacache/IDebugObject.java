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

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.ArrayList;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDebugObject.
 * 
 */

public interface IDebugObject {

    /**
     * Sets the namespace.
     *
     * @param namespace the new namespace
     */
    void setNamespace(INamespace namespace);

    /**
     * Sets the source code.
     *
     * @param srcCode the new source code
     */
    void setSourceCode(ISourceCode srcCode);

    /**
     * Gets the PL source editor elm id.
     *
     * @return the PL source editor elm id
     */
    String getPLSourceEditorElmId();

    /**
     * Gets the object type.
     *
     * @return the object type
     */
    OBJECTTYPE getObjectType();

    /**
     * Gets the PL source editor elm tooltip.
     *
     * @return the PL source editor elm tooltip
     */
    String getPLSourceEditorElmTooltip();

    /**
     * Gets the display name with arg name.
     *
     * @return the display name with arg name
     */
    String getDisplayNameWithArgName();

    /**
     * Gets the window title name.
     *
     * @return the window title name
     */
    String getWindowTitleName();

    /**
     * Gets the oid.
     *
     * @return the oid
     */
    long getOid();

    /**
     * Gets the database.
     *
     * @return the database
     */
    Database getDatabase();

    /**
     * Gets the latest souce code.
     *
     * @return the latest souce code
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    ISourceCode getLatestSouceCode() throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Gets the type.
     *
     * @return the type
     */
    OBJECTTYPE getType();

    /**
     * Gets the source code.
     *
     * @return the source code
     */
    ISourceCode getSourceCode();

    /**
     * Refresh source code.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    void refreshSourceCode() throws DatabaseOperationException, DatabaseCriticalException;

    /**
     * Gets the display name.
     *
     * @param bol the bol
     * @return the display name
     */
    String getDisplayName(boolean bol);

    /**
     * Gets the name space id.
     *
     * @return the name space id
     */
    long getNameSpaceId();

    /**
     * Gets the latest info.
     *
     * @return the latest info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    String getLatestInfo() throws DatabaseCriticalException, DatabaseOperationException;

    /**
     * Checks if is changed.
     *
     * @param latestInfo the latest info
     * @return true, if is changed
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    boolean isChanged(String latestInfo) throws DatabaseCriticalException, DatabaseOperationException;

    /**
     * Gets the display label.
     *
     * @return the display label
     */
    String getDisplayLabel();

    /**
     * Belongs to.
     *
     * @param database the database
     * @param server the server
     * @return true, if successful
     */
    boolean belongsTo(Database database, Server server);

    /**
     * Handle change.
     *
     * @param latestInfo the latest info
     */
    void handleChange(String latestInfo);

    /**
     * Gets the lang.
     *
     * @return the lang
     */
    Object getLang();

    /**
     * Can support debug.
     *
     * @return true, if successful
     */
    boolean canSupportDebug();

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Checks if is debuggable.
     *
     * @return true, if is debuggable
     */
    boolean isDebuggable();

    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    INamespace getNamespace();

    /**
     * Sets the database.
     *
     * @param db the new database
     */
    void setDatabase(Database db);

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    String getDisplayName();

    /**
     * Generate execution template.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    void generateExecutionTemplate() throws DatabaseOperationException;

    /**
     * Gets the execute template.
     *
     * @return the execute template
     */
    String getExecuteTemplate();

    /**
     * Gets the usagehint.
     *
     * @return the usagehint
     */
    String getUsagehint();

    /**
     * Gets the template parameters.
     *
     * @return the template parameters
     */
    ArrayList<ObjectParameter> getTemplateParameters();

    /**
     * Clear template parameter values.
     */
    void clearTemplateParameterValues();

    /**
     * Sets the template parameters.
     *
     * @param templateParameters the new template parameters
     */
    void setTemplateParameters(ArrayList<ObjectParameter> templateParameters);

    /**
     * Sets the execution query.
     *
     * @param string the new execution query
     */
    void setExecutionQuery(String string);

    /**
     * Gets the execution query.
     *
     * @return the execution query
     */
    String getExecutionQuery();

    /**
     * Validate object type.
     *
     * @return true, if successful
     */
    boolean validateObjectType();

    /**
     * Prepare execution query string.
     *
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     */
    String prepareExecutionQueryString() throws DatabaseOperationException;

    /**
     * Refresh self.
     *
     * @return the i debug object
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    IDebugObject refreshSelf() throws MPPDBIDEException;

    /**
     * Checks if is code reloaded.
     *
     * @return true, if is code reloaded
     */
    boolean isCodeReloaded();

    /**
     * Sets the code reloaded.
     *
     * @param isCodeReloaded the new code reloaded
     */
    void setCodeReloaded(boolean isCodeReloaded);

    /**
     * gets out parameters
     * 
     * @return the out param list
     */
    ArrayList<DefaultParameter> getOutParameters();

    /**
     * Sets isCurrentTerminal flag
     * 
     * @param isCurrentTerminal the flag
     */
    void setIsCurrentTerminal(boolean isCurrentTerminal);

    /**
     * gets the current terminal
     * 
     * @return the current terminal
     */
    boolean getCurrentTerminal();

    /**
     * Sets theisEditTerminalInputValues flag
     * 
     * @param isEditTerminalInputValues the flag
     */
    void setIsEditTerminalInputValues(boolean isEditTerminalInputValues);

    /**
     * gets the isEditTerminalInputValues flag
     * 
     * @return true if TerminalInputValues can be editable
     */
    boolean getEditTerminalInputValues();
}