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

package org.opengauss.mppdbide.view.autorefresh;

import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.INamespace;
import org.opengauss.mppdbide.presentation.IExecutionContext;
import org.opengauss.mppdbide.presentation.autorefresh.AutoRefreshQueryFormation;
import org.opengauss.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.JSQLParserUtils;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: AutoRefresh
 * 
 * Description:AutoRefreshObject
 * 
 * @since 3.0.0
 */
public abstract class AutoRefreshObject {

    private String tableName;
    private String schemaName;
    private Database db;
    private INamespace namespace;
    private INamespace destnSchema = null;
    private IExecutionContext execContext;
    private boolean isAlterQuery = false;
    private RefreshObjectDetails refObject;

    /**
     * The list of objects.
     */
    HashSet<Object> listOfObjects;
    private int quoteCount = 0;

    /**
     * Instantiates a new auto refresh object.
     *
     * @param listOfObjects the list of objects
     * @param executionContext the execution context
     */
    public AutoRefreshObject(HashSet<Object> listOfObjects, IExecutionContext executionContext) {
        this.listOfObjects = listOfObjects;
        execContext = executionContext;
        this.db = executionContext.getTermConnection().getDatabase();
    }

    /**
     * Execute auto refresh.
     *
     * @param query the query
     * @throws DatabaseCriticalException the database critical exception
     */
    public void executeAutoRefresh(String query) throws DatabaseCriticalException {
        String objType = null;
        query = query.replaceAll("(--.*?(" + MPPDBIDEConstants.LINE_SEPARATOR + "|$)|\\/\\*.*?\\*\\/)", "");
        String modifiedQuery = query.toLowerCase(Locale.ENGLISH).trim().replaceAll(" +", " ");
        String[] spltedQuery = query.split("\\s+");
        try {
            if (modifiedQuery.startsWith(MPPDBIDEConstants.COMMENT_KW)) {
                objType = parseTableRColumnDescriptionQuery(query, spltedQuery);
            } else if (modifiedQuery.startsWith(MPPDBIDEConstants.ALTER_INDEX_KW)
                    || modifiedQuery.startsWith(MPPDBIDEConstants.ALTER_INDEX_IF_EXISTS_KW)) {
                objType = parseAlterIndex(query);
            } else if (modifiedQuery.startsWith(MPPDBIDEConstants.ALTER_KW)
                    && isAlterWithRenameRSetRAlterClause(spltedQuery)) {
                objType = parseRenameSetAlterClauseFromAlterStmt(query, spltedQuery);
            } else if ((modifiedQuery.startsWith(MPPDBIDEConstants.ALTER_KW)
                    && !(isAlterWithRenameRSetRAlterClause(spltedQuery)))
                    || modifiedQuery.startsWith(MPPDBIDEConstants.DROP_KW)) {
                getQualifiedDDLTblNameRViewName(query);
                objType = getObjectTypeForDDLstatements(query);
            } else if (modifiedQuery.startsWith(MPPDBIDEConstants.CREATE_KW)) {
                objType = parseCreateTable(query);
            }
            if (objType != null && tableName != null) {
                // This check avoids repeatedly finding the schema name for
                // alter index
                if (!isAlterQuery) {
                    getSchemaNameFromSplittedQualifiedName();
                    namespace = getNameSpaceObject(schemaName);
                }
                isNameSpaceLoaded(listOfObjects, objType);
            }
        } catch (DatabaseOperationException databaseOperationException) {
            MPPDBIDELoggerUtility.error("Exception occured while executing auto-refresh", databaseOperationException);
        } catch (DatabaseCriticalException databaseCriticalException) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE),
                    databaseCriticalException);
            throw new DatabaseCriticalException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE,
                    databaseCriticalException);
        }

    }

    private String parseCreateTable(String query) throws DatabaseOperationException, DatabaseCriticalException {
        String str = query;
        String nstr = "";
        boolean isTbl = false;
        boolean isIndex = false;
        boolean isOnKW = false;
        str = removeIfExistsKeywords(str);
        // removing the formatting
        str = str.replaceAll(MPPDBIDEConstants.NEW_LINE_SIGN, " ");
        for (int index = 0; index < str.length(); index++) {
            boolean isEven = evenQuotes(str.charAt(index));
            if ((str.charAt(index) == ' ' || str.charAt(index) == '(') && isEven) {
                isTbl = isItTable(nstr, isTbl);
                isIndex = isItIndex(nstr, isIndex);
                isOnKW = isItOnKw(nstr, isOnKW);
                if (isTbl) {
                    if (!nstr.equalsIgnoreCase(MPPDBIDEConstants.TABLE_KW) && !nstr.isEmpty()) {
                        tableName = nstr;
                        break;
                    }
                } else if (isIndex && isOnKW) {
                    if (!nstr.equalsIgnoreCase("on") && !nstr.isEmpty()
                            && !nstr.equalsIgnoreCase(MPPDBIDEConstants.INDEX_KW)) {
                        tableName = nstr;
                        break;
                    }
                }
                nstr = "";
            } else {
                nstr = nstr + str.charAt(index);
            }
        }
        getTableNameFromLastWord(nstr, isIndex, isOnKW);
        return setObjTypeForCreate(query, isTbl, isIndex);
    }

    private void getTableNameFromLastWord(String nstr, boolean isIndex, boolean isOnKW) {
        if (isIndex && isOnKW && tableName == null) {
            tableName = nstr;
        }
    }

    /**
     * Removes the if exists keywords.
     *
     * @param str the str
     * @return the string
     */
    public String removeIfExistsKeywords(String str) {
        boolean ifNotExists = str.toLowerCase(Locale.ENGLISH).contains(MPPDBIDEConstants.IF_NOT_EXISTS_KW);
        boolean ifExists = str.toLowerCase(Locale.ENGLISH).contains(MPPDBIDEConstants.IF_EXISTS_KW);
        if (ifNotExists) {
            str = str.replace(MPPDBIDEConstants.IF_NOT_EXISTS_KW, "");
        }
        if (ifExists) {
            str = str.replace(MPPDBIDEConstants.IF_EXISTS_KW, "");
        }
        return str;
    }

    private boolean evenQuotes(char ch) {
        if (ch == '"') {
            quoteCount++;
        }
        return quoteCount % 2 == 0;
    }

    private String setObjTypeForCreate(String query, boolean isTbl, boolean isIndex)
            throws DatabaseOperationException, DatabaseCriticalException {
        if (isTbl || isIndex) {
            return MPPDBIDEConstants.CREATE_TABLE;
        } else {
            getQualifiedDDLTblNameRViewName(query);
            return getObjectTypeForDDLstatements(query);
        }
    }

    private boolean isItTable(String nstr, boolean isTbl) {
        if (nstr.equalsIgnoreCase(MPPDBIDEConstants.TABLE_KW)) {
            isTbl = true;
        }
        return isTbl;
    }

    private boolean isItIndex(String nstr, boolean isIndx) {
        if (nstr.equalsIgnoreCase(MPPDBIDEConstants.INDEX_KW)) {
            isIndx = true;
        }
        return isIndx;
    }

    private boolean isItOnKw(String nstr, boolean onKWFnd) {
        if (nstr.equalsIgnoreCase("on")) {
            onKWFnd = true;
        }
        return onKWFnd;
    }

    /**
     * Gets the object type for DD lstatements.
     *
     * @param query the query
     * @return the object type for DD lstatements
     */
    public String getObjectTypeForDDLstatements(String query) {
        String objType;
        objType = JSQLParserUtils.setObjectType(query);
        return objType;
    }

    /**
     * Checks if is name space loaded.
     *
     * @param listOfObjects the list of objects
     * @param objType the obj type
     */
    public void isNameSpaceLoaded(HashSet<Object> listOfObjects, String objType) {
        refObject = new RefreshObjectDetails();
        refObject.setObjectName(getTableName(tableName));
        refObject.setNamespace(namespace);
        refObject.setOperationType(objType);
        refObject.setDesctNamespace(destnSchema);
        if (namespace != null && namespace.isLoaded()) {
            AutoRefreshQueryFormation.getObjectToBeRefreshed(refObject, listOfObjects);
            for (Object obj : listOfObjects) {
                RefreshObjectDetails ref = (RefreshObjectDetails) obj;
                String objTypeRef = ref.getOperationType();
                getRefreshObjects(objTypeRef, ref);
            }
        }
    }

    /**
     * gets the TableName
     * 
     * @param table the table
     * @return the table name
     */
    protected String getTableName(String table) {
        String tableName2 = table;
        String regex = "^[a-zA-Z0-9_]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tableName2);
        if (matcher.matches()) {
            tableName2 = getQualifiedTableName(tableName2);
        } else if (tableName2.startsWith("\"") && tableName2.endsWith("\"")) {
            tableName2 = tableName2.replace('"', ' ').trim();
        } else {
            // No need to to do anything
        }
        return tableName2;
    }

    /**
     * Gets the qualified table name.
     *
     * @param tableName2 the table name 2
     * @return the qualified table name
     */
    protected abstract String getQualifiedTableName(String tableName2);

    /**
     * Checks if is alter with rename R set R alter clause.
     *
     * @param spltedQuery the splted query
     * @return true, if is alter with rename R set R alter clause
     */
    public boolean isAlterWithRenameRSetRAlterClause(String[] spltedQuery) {
        if (spltedQuery[3].equalsIgnoreCase(MPPDBIDEConstants.RENAME_KW)
                || spltedQuery[3].equalsIgnoreCase(MPPDBIDEConstants.ALTER_KW)
                || spltedQuery[3].equalsIgnoreCase(MPPDBIDEConstants.SET_KW)
                || (spltedQuery[3].equalsIgnoreCase(MPPDBIDEConstants.ADD_KW)
                        && spltedQuery[4].equalsIgnoreCase(MPPDBIDEConstants.CONSTRAINT_KW))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Parses the rename set alter clause from alter stmt.
     *
     * @param query the query
     * @param spltedQuery the splted query
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String parseRenameSetAlterClauseFromAlterStmt(String query, String[] spltedQuery)
            throws DatabaseOperationException, DatabaseCriticalException {
        String objType = null;
        tableName = spltedQuery[2];
        objType = parseSetSchemaQuery(query, spltedQuery);
        if (objType == null) {
            if (spltedQuery[1].equalsIgnoreCase(MPPDBIDEConstants.TABLE_KW)) {
                objType = MPPDBIDEConstants.ALTER_TABLE;
            } else {
                objType = MPPDBIDEConstants.ALTER_VIEW;
            }
        }
        return objType;
    }

    /**
     * Gets the name space object.
     *
     * @param schemaName the schema name
     * @return the name space object
     * @throws DatabaseOperationException the database operation exception
     */
    public INamespace getNameSpaceObject(String schemaName) throws DatabaseOperationException {
        INamespace iNamespace = null;
        try {
            schemaName = getUnQuotedIdentifier(schemaName);
            iNamespace = db.getNameSpaceByName(schemaName);
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Exception occured while getting namespace object", exception);
        }
        return iNamespace;
    }

    /**
     * Parses the table R column description query.
     *
     * @param query the query
     * @param spltedQuery the splted query
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private String parseTableRColumnDescriptionQuery(String query, String[] spltedQuery)
            throws DatabaseOperationException, DatabaseCriticalException {
        String objType = null;
        if (query.toLowerCase(Locale.ENGLISH).startsWith(MPPDBIDEConstants.COMMENT_KW)
                && (spltedQuery[2].equalsIgnoreCase(MPPDBIDEConstants.TABLE_KW)
                        || spltedQuery[2].equalsIgnoreCase(MPPDBIDEConstants.COLUMN_KW))) {
            tableName = spltedQuery[3];
            objType = MPPDBIDEConstants.ALTER_TABLE;
        }
        return objType;
    }

    /**
     * Parses the set schema query.
     *
     * @param query the query
     * @param spltedQuery the splted query
     * @return the i namespace
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private String parseSetSchemaQuery(String query, String[] spltedQuery)
            throws DatabaseOperationException, DatabaseCriticalException {
        String objType = null;
        String destnSchemaName = null;
        for (int entry = 0; entry < (spltedQuery.length - 1); entry++) {
            if (spltedQuery[entry].toLowerCase(Locale.ENGLISH).equals(MPPDBIDEConstants.SET_KW)
                    && spltedQuery[entry + 1].toLowerCase(Locale.ENGLISH).equals(MPPDBIDEConstants.SCHEMA_KW)) {
                destnSchemaName = spltedQuery[spltedQuery.length - 1];
                destnSchema = getNameSpaceObject(destnSchemaName);
                if (destnSchema != null) {
                    destnSchema = destnSchema.isLoaded() ? destnSchema : null;
                }

                if (spltedQuery[1].equalsIgnoreCase(MPPDBIDEConstants.TABLE_KW)) {
                    objType = MPPDBIDEConstants.SET_SCHEMA_TABLE;
                } else {
                    objType = MPPDBIDEConstants.SET_SCHEMA_VIEW;
                }
            }
        }
        return objType;
    }

    /**
     * Gets the refresh objects.
     *
     * @param objType the obj type
     * @param refObject2 the list of objects
     * @return the refresh objects
     */
    public void getRefreshObjects(String objType, RefreshObjectDetails refObject2) {
        switch (objType) {
            case MPPDBIDEConstants.CREATE_VIEW: {
                getViewObjects(db, refObject2.getNamespace(), refObject2, tableName);
                break;
            }
            case MPPDBIDEConstants.DROP_TABLE:
            case MPPDBIDEConstants.CREATE_TABLE: {
                getTableObjects(db, refObject2.getNamespace(), refObject2);
                break;
            }
            case MPPDBIDEConstants.ALTER_TABLE: {
                getAlterTableObject(tableName, refObject2.getNamespace(), refObject2);
                break;
            }
            case MPPDBIDEConstants.ALTER_VIEW: {
                getAlterViewObject(tableName, refObject2.getNamespace(), refObject2);
                break;
            }
            case MPPDBIDEConstants.DROP_VIEW: {
                getDropViewObjects(db, refObject2.getNamespace(), refObject2);
                break;
            }
            case MPPDBIDEConstants.CREATE_TRIGGER: {
                getTriggerObjects(db, refObject2.getNamespace(), refObject2);
                break;
            }
            default: {
                break;
            }
        }
        // resetting object type to make to every time it gets the latest value.
        objType = "";
    }

    /**
     * Gets the qualified DDL tbl name R view name.
     *
     * @param query the query
     * @return the qualified DDL tbl name R view name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void getQualifiedDDLTblNameRViewName(String query)
            throws DatabaseOperationException, DatabaseCriticalException {
        tableName = JSQLParserUtils.getQualifiedTblRViewName(query);

    }

    /**
     * Gets the schema name from splitted qualified name.
     *
     * @return the schema name from splitted qualified name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void getSchemaNameFromSplittedQualifiedName() throws DatabaseOperationException, DatabaseCriticalException {
        DBConnection dbConnection = execContext.getTermConnection().getConnection();
        String[] splitArray = null;
        if (tableName.contains(".")) {
            splitArray = JSQLParserUtils.getSplitQualifiedName(tableName, true);
            if (splitArray.length > 1) {
                if (tableName.startsWith("\"") || tableName.endsWith("\"") && tableName.contains(".")) {
                    // appending the quotes back to table/schema name
                    schemaName = appendQuotes(splitArray[0]);
                    tableName = appendQuotes(splitArray[1]);
                } else {
                    schemaName = splitArray[0];
                    tableName = splitArray[1];
                }
            } else {
                schemaName = db.getDefaultSchemaName(dbConnection);
            }
        } else {
            schemaName = db.getDefaultSchemaName(dbConnection);
        }
    }

    private String appendQuotes(String name) {
        name = "\"" + name + "\"";
        return name;
    }

    /**
     * Gets the view objects.
     *
     * @param db the db
     * @param namespace the namespace
     * @param refObject2 the list of objects
     * @param tableName the table name
     * @return the view objects
     */
    public abstract void getViewObjects(Database db, INamespace namespace, RefreshObjectDetails refObject2,
            String tableName);

    /**
     * Gets the table objects.
     *
     * @param db the db
     * @param namespace the namespace
     * @param refObject2 the list of objects
     * @return the table objects
     */
    public abstract void getTableObjects(Database db, INamespace namespace, RefreshObjectDetails refObject2);

    /** 
     * gets the AlterTableObject
     * 
     * @param tableName the tableName
     * @param namespace the namespace
     * @param refObject2 the refObject2
     */
    public abstract void getAlterTableObject(String tableName, INamespace namespace, RefreshObjectDetails refObject2);

    /** 
     * gets the AlterViewObject
     * 
     * @param tableName the tableName
     * @param namespace the namespace
     * @param refObject2 the refObject2
     */
    public abstract void getAlterViewObject(String tableName, INamespace namespace, RefreshObjectDetails refObject2);

    /**
     * Gets the un quoted identifier.
     *
     * @param str the str
     * @return the un quoted identifier
     */
    public abstract String getUnQuotedIdentifier(String str);

    /**
     * Gets the table for index.
     *
     * @param ns the ns
     * @param tableName the table name
     * @return the table for index
     */
    private String parseAlterIndex(String query) throws DatabaseOperationException, DatabaseCriticalException {
        isAlterQuery = true;
        String str = removeIfExistsKeywords(query);
        String nstr = "";
        boolean isIndex = false;
        for (int index = 0; index < str.length(); index++) {
            boolean isEven = evenQuotes(str.charAt(index));
            if (str.charAt(index) == ' ' && isEven) {
                isIndex = isItIndex(nstr, isIndex);
                if (isIndex) {
                    if (!nstr.isEmpty() && !nstr.equalsIgnoreCase(MPPDBIDEConstants.INDEX_KW)) {
                        tableName = nstr;
                        break;
                    }
                }
                nstr = "";
            } else {
                nstr = nstr + str.charAt(index);
            }
        }
        getSchemaNameFromSplittedQualifiedName();
        namespace = getNameSpaceObject(schemaName);
        if (namespace != null) {
            tableName = getTableNameForIndex(namespace, tableName); 
        }
        return MPPDBIDEConstants.ALTER_TABLE;
    }

    /**
     * Gets the table name for index.
     *
     * @param namespace the namespace
     * @param tableName the table name
     * @return the table name for index
     */
    protected abstract String getTableNameForIndex(INamespace namespace, String tableName);

    /**
     * Gets the drop view objects.
     *
     * @param db the db
     * @param namespace the namespace
     * @param refObject2 the list of objects
     * @return the drop view objects
     */
    protected abstract void getDropViewObjects(Database db, INamespace namespace, RefreshObjectDetails refObject2);

    /**
     * Gets the create trigger objects
     *
     * @param Database the db
     * @param INamespace the namespace
     * @param RefreshObjectDetails the refresh object detail
     */
    protected abstract void getTriggerObjects(Database db, INamespace namespace, RefreshObjectDetails refObject2);

}
