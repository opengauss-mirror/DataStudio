/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussDatatypeUtils;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects.DebugObjectsUtils.GenerateExecutionTemplateParameter;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.IMessagesConstantsOne;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class DebugObjects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DebugObjects extends BatchDropServerObject implements ObjectChange, GaussOLAPDBMSObject, IDebugObject {
    private boolean isDebugable;
    private String executeTemplate;
    private String executionQuery;
    private OBJECTTYPE objectType;

    private ISourceCode sourceCode;
    private ObjectParameter[] parameters;
    private ArrayList<ObjectParameter> templateParameters;
    private ArrayList<DefaultParameter> outParameters;
    private ObjectParameter returnType;
    private Database db;
    private String usageHint;
    private Namespace namespace;
    private String lang;

    private boolean isSourceLoaded;
    private boolean isCodeReloaded;

    private boolean isCurrentTerminal = false;
    private boolean isEditTerminalInputValues = false;

    /**
     * Instantiates a new debug objects.
     */
    public DebugObjects() {
        super(OBJECTTYPE.PLSQLFUNCTION);
        this.isSourceLoaded = false;
    }

    /**
     * Gets the usagehint.
     *
     * @return the usagehint
     */
    public String getUsagehint() {
        return usageHint;
    }

    /**
     * Sets the usagehint.
     *
     * @param usagehint the new usagehint
     */
    public void setUsagehint(String usagehint) {
        this.usageHint = usagehint;
    }

    /**
     * Instantiates a new debug objects.
     *
     * @param id the id
     * @param name the name
     * @param type the type
     * @param db the db
     */
    public DebugObjects(long id, String name, OBJECTTYPE type, Database db) {
        super(id, name, type, db != null ? db.getPrivilegeFlag() : true);
        this.objectType = type;
        this.sourceCode = new SourceCode();
        this.db = db;
    }

    /**
     * Sets the database.
     *
     * @param db1 the new database
     */
    public void setDatabase(Database db1) {
        this.db = db1;
    }

    /**
     * Is the current object debuggable.
     *
     * @return true, if is debuggable
     */
    public boolean isDebuggable() {
        return isDebugable;
    }

    /**
     * Sets the checks if is debuggable.
     *
     * @param isDebuggable the new checks if is debuggable
     */
    public void setIsDebuggable(boolean isDebuggable) {
        this.isDebugable = isDebuggable;
    }

    /**
     * Get type of current debug object.
     *
     * @return the object type
     */
    public OBJECTTYPE getObjectType() {
        return objectType;
    }

    /**
     * Get Execution template for this debug object.
     *
     * @return the execute template
     */
    public String getExecuteTemplate() {
        return executeTemplate;
    }

    /**
     * Get Execution query summary.
     *
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     */
    public String prepareExecutionQueryString() throws DatabaseOperationException {
        return DebugObjectsUtils.prepareExecutionQueryString(this);
    }

    /**
     * Sets the execute template.
     *
     * @param executeTemplate the new execute template
     */
    public void setExecuteTemplate(String executeTemplate) {
        this.executeTemplate = executeTemplate;
    }

    /**
     * Set source code object for current debug object.
     *
     * @param sourceCode the new source code
     */
    public void setSourceCode(ISourceCode sourceCode) {
        this.sourceCode = sourceCode;
        isSourceLoaded = true;
    }

    /**
     * Get source code object for current debug objects.
     *
     * @return the source code
     */
    public ISourceCode getSourceCode() {
        return this.sourceCode;
    }

    @Override
    public String getSearchName() {
        return getDisplayNameWithArgName() + " - " + getNamespace().getName() + " - " + getTypeLabel();
    }

    /**
     * Sets the object parameters.
     *
     * @param parmeters the new object parameters
     */
    public void setObjectParameters(ObjectParameter[] parmeters) {
        this.parameters = parmeters;
    }

    /**
     * Gets the object parameters.
     *
     * @return the object parameters
     */
    public ObjectParameter[] getObjectParameters() {
        return parameters;
    }

    /**
     * Gets the template parameters.
     *
     * @return the templateParameters
     */
    public ArrayList<ObjectParameter> getTemplateParameters() {
        return templateParameters;
    }

    /**
     * sets out parameter list
     * 
     * @param outParams the out param list
     */
    public void setOutParameters(ArrayList<DefaultParameter> outParams) {
        this.outParameters = outParams;
    }

    /**
     * gets out parameter list
     */
    public ArrayList<DefaultParameter> getOutParameters() {
        return outParameters;
    }

    /**
     * Sets the template parameters.
     *
     * @param templateParameters the templateParameters to set
     */
    public void setTemplateParameters(ArrayList<ObjectParameter> templateParameters) {
        this.templateParameters = templateParameters;
    }

    /**
     * Clear template parameter values.
     */
    public void clearTemplateParameterValues() {
        if (templateParameters != null) {
            int size = templateParameters.size();
            clearParameterValue(size);
        }
    }

    private void clearParameterValue(int size) {
        for (int count = 0; count < size; count++) {
            templateParameters.get(count).clearValue();
        }
    }

    /**
     * Sets the object returns.
     *
     * @param retType the new object returns
     */
    public void setObjectReturns(ObjectParameter retType) {
        this.returnType = retType;
    }

    /**
     * Gets the display name.
     *
     * @param isAutoSuggest the is auto suggest
     * @return the display name
     */
    public String getDisplayName(boolean isAutoSuggest) {
        return DebugObjectsUtils.getDisplayName(isAutoSuggest, getObjectParameters(), getReturnType(), getName(),
                super.getDisplayName());
    }

    /**
     * Gets the grant revoke name.
     *
     * @return the grant revoke name
     */
    public String getGrantRevokeName() {
        StringBuilder grantRevokeName = new StringBuilder(getQualifiedObjectName());
        grantRevokeName.append("(");

        if (null != parameters) {
            int paramLen = parameters.length;
            appendGrantRevokeName(grantRevokeName, paramLen);
        }

        grantRevokeName.append(")");
        return grantRevokeName.toString();
    }

    private void appendGrantRevokeName(StringBuilder grantRevokeName, int paramLen) {
        boolean isFirstParam = true;
        for (int index = 0; index < paramLen; index++) {
            appendComma(isFirstParam, grantRevokeName);
            grantRevokeName.append(parameters[index].getType()).append(" ").append(parameters[index].getDataType());
            isFirstParam = false;
        }
    }

    private void appendComma(boolean isFirstParam, StringBuilder grantRevokeName) {
        if (!isFirstParam) {
            grantRevokeName.append(", ");
        }
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return getNamespace().getDisplayName() + '.' + super.getDisplayName();
    }

    /**
     * Gets the display name with arg name.
     *
     * @return the display name with arg name
     */
    public String getDisplayNameWithArgName() {
        return getDisplayName(false);
    }

    @Override
    public String getWindowTitleName() {
        return getDisplayNameWithArgName();
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return db;
    }

    /**
     * Gets the execution query.
     *
     * @return the execution query
     */
    public String getExecutionQuery() {
        return executionQuery;
    }

    /**
     * Sets the execution query.
     *
     * @param executionQuery the new execution query
     */
    public void setExecutionQuery(String executionQuery) {
        this.executionQuery = executionQuery;
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    public Namespace getNamespace() {
        return this.namespace;
    }

    /**
     * Sets the namespace.
     *
     * @param ns the new namespace
     */
    public void setNamespace(INamespace ns) {
        this.namespace = (Namespace) ns;
        this.privilegeFlag = this.namespace.getPrivilegeFlag();
    }

    /**
     * Gets the name space id.
     *
     * @return the name space id
     */
    public long getNameSpaceId() {
        return this.namespace.getOid();
    }

    /**
     * Gets the name space name.
     *
     * @return the name space name
     */
    public String getDbgNameSpaceName() {
        return this.namespace.getName();
    }

    /**
     * Gets the return type.
     *
     * @return the return type
     */
    public ObjectParameter getReturnType() {
        return returnType;
    }

    /**
     * Refresh source code.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshSourceCode() throws DatabaseOperationException, DatabaseCriticalException {
        String funcDefinitionQuery = String.format(Locale.ENGLISH,
                "select headerlines, definition from PG_GET_FUNCTIONDEF(%d);", getOid());

        addSourceCode();

        if (this.getConnectionManager() != null) {
            DBConnection objBrowserConn = this.getConnectionManager().getObjBrowserConn();
            handleNullBrowserConnection(objBrowserConn);
            ResultSet funcDefinitionQueryRS = objBrowserConn.execSelectAndReturnRs(funcDefinitionQuery);
            handleFuncVersionQryResultSet(objBrowserConn, funcDefinitionQueryRS);
        }
    }

    private void addSourceCode() {
        if (null == this.sourceCode) {
            this.sourceCode = new SourceCode();
        }
    }

    private void handleFuncVersionQryResultSet(DBConnection objBrowserConn, ResultSet funcDefinitionQueryRS)
            throws DatabaseOperationException {
        ResultSet funcVersionQueryRS = execFunVersionQuery(objBrowserConn);
        addFuncDefQuery(funcDefinitionQueryRS, funcVersionQueryRS);
    }

    private ResultSet execFunVersionQuery(DBConnection objBrowserConn) {
        String funcVersionQuery = String.format(Locale.ENGLISH, "select xmin1, cmin1 from pldbg_get_funcVer( %d)",
                getOid());
        ResultSet funcVersionQueryRS = null;
        try {
            funcVersionQueryRS = objBrowserConn.execSelectAndReturnRs(funcVersionQuery);
        } catch (DatabaseCriticalException | DatabaseOperationException exp) {
            MPPDBIDELoggerUtility.error("DEBUG not enabled");
        }
        return funcVersionQueryRS;
    }

    private void addFuncDefQuery(ResultSet funcDefinitionQueryRS, ResultSet funcVersionQueryRS)
            throws DatabaseOperationException {
        try {
            handleFuncDefQueryRS(funcDefinitionQueryRS, funcVersionQueryRS);
        } catch (SQLException e) {
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            this.getConnectionManager().closeRSOnObjBrowserConn(funcDefinitionQueryRS);
            this.getConnectionManager().closeRSOnObjBrowserConn(funcVersionQueryRS);
        }
    }

    private void handleNullBrowserConnection(DBConnection objBrowserConn) throws DatabaseCriticalException {
        if (objBrowserConn == null) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_FUNCTION_REFRESH));
            throw new DatabaseCriticalException(IMessagesConstants.ERR_WHILE_FUNCTION_REFRESH);
        }
    }

    private void handleFuncDefQueryRS(ResultSet funcDefinitionQueryRS, ResultSet funcVersionQueryRS)
            throws SQLException, DatabaseOperationException {
        handleFuncDefErrorCase(funcDefinitionQueryRS, funcVersionQueryRS);
        if (this.sourceCode instanceof SourceCode) {
            SourceCode srcCde = ((SourceCode) this.sourceCode);
            srcCde.updateCode(funcDefinitionQueryRS);
            srcCde.updateVersionNumber(funcVersionQueryRS);
            this.isSourceLoaded = true;
        }
    }

    private void handleFuncDefErrorCase(ResultSet funcDefinitionQueryRS, ResultSet funcVersionQueryRS)
            throws SQLException, DatabaseOperationException {
        validateFunctionDefQuery(funcDefinitionQueryRS);
        validateFunVersionQuery(funcVersionQueryRS);
    }

    private void validateFunVersionQuery(ResultSet funcVersionQueryRS) throws SQLException, DatabaseOperationException {
        if (validateFuncVersionQueryRS(funcVersionQueryRS)) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE));
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE);
        }
    }

    private void validateFunctionDefQuery(ResultSet funcDefinitionQueryRS)
            throws SQLException, DatabaseOperationException {
        if (!funcDefinitionQueryRS.next()) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE));
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE);
        }
    }

    private boolean validateFuncVersionQueryRS(ResultSet funcVersionQueryRS) throws SQLException {
        return null != funcVersionQueryRS && !funcVersionQueryRS.next();
    }

    /**
     * Generate drop query.
     *
     * @return the string
     */
    public String generateDropQuery() {
        boolean cascade = false;
        if ("trigger".equalsIgnoreCase(returnType.getDataType())) {
            cascade = true;
        }
        return DebugObjectsUtils.getDropQuery(DebugObjectsUtils.getDropFuncStatement(false), cascade, namespace,
                parameters, getName());
    }

    /**
     * 
     * Title: interface
     * 
     * Description: The Interface DebugObjectsUtils.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public static interface DebugObjectsUtils {

        static final String DROP_FUNC = "DROP FUNCTION ";
        static final String IF_EXISTS = "IF EXISTS ";

        /**
         * Convert to object.
         *
         * @param rs the rs
         * @param database the database
         * @return the debug objects
         * @throws DatabaseOperationException the database operation exception
         */
        public static DebugObjects convertToObject(ResultSet rs, Database database) throws DatabaseOperationException {
            DebugObjects dbgObj = null;
            int procid = 0;
            String procname = null;
            int rettype = 0;
            String argtmodes = null;
            String argtypes = null;
            String argnames = null;
            String lang = null;
            boolean allin = false;
            OBJECTTYPE objtyp = null;
            int nargs = 0;
            int namespaceOid = 0;

            try {
                procid = rs.getInt("oid");
                procname = rs.getString("objname");
                rettype = rs.getInt("ret");
                argtypes = rs.getString("alltype");
                namespaceOid = rs.getInt("namespace");
                boolean retset = rs.getBoolean("retset");
                if (null == argtypes || retset) {
                    argtypes = null;
                    allin = true;

                    nargs = rs.getInt("nargs");

                    if (nargs != 0) {
                        argtypes = rs.getString("argtype");
                    }
                }

                argnames = rs.getString("argname");
                argtmodes = rs.getString("argmod");

                lang = rs.getString("lang");

                if (null == procname) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
                    throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
                }

            } catch (SQLException exp) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID), exp);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
            }
            objtyp = setDebugObjectType(lang);

            dbgObj = getNewDebugObject(database, procid, procname, rettype, argtmodes, argtypes, argnames, lang, allin,
                    objtyp, namespaceOid);
            return dbgObj;
        }

        /**
         * Gets the new debug object.
         *
         * @param database the database
         * @param procid the procid
         * @param procname the procname
         * @param rettype the rettype
         * @param argtmodes the argtmodes
         * @param argtypes the argtypes
         * @param argnames the argnames
         * @param lang the lang
         * @param allin the allin
         * @param objtyp the objtyp
         * @param namespaceOid the namespace oid
         * @return the new debug object
         * @throws DatabaseOperationException the database operation exception
         */
        public static DebugObjects getNewDebugObject(Database database, int procid, String procname, int rettype,
                String argtmodes, String argtypes, String argnames, String lang, boolean allin, OBJECTTYPE objtyp,
                int namespaceOid) throws DatabaseOperationException {
            DebugObjects dbgObj;
            Namespace ns = null;
            ArrayList<Namespace> nsList = new ArrayList<>();
            addPgCatalogNameSpaceToList(database, nsList);

            addInformationSchemaToList(database, nsList);

            dbgObj = setDebugObjParameters(database, procid, procname, argtmodes, argtypes, argnames, allin, objtyp,
                    nsList);

            setPlSqlReturnType(dbgObj, rettype, nsList);
            setIsDebuggable(dbgObj, lang);

            ns = database.getNameSpaceById(namespaceOid);
            dbgObj.setNamespace(ns);
            dbgObj.setLanguage(lang);

            ns.addDebugObjectToSearchPool(dbgObj);
            return dbgObj;
        }

        /**
         * Sets the pl sql return type.
         *
         * @param dbgObj the dbg obj
         * @param rettype the rettype
         * @param nsList the ns list
         */
        public static void setPlSqlReturnType(DebugObjects dbgObj, int rettype, ArrayList<Namespace> nsList) {
            ObjectParameter proret;
            proret = new ObjectParameter(getDataTypeName(rettype, nsList), GaussDatatypeUtils.isSupported(rettype));

            dbgObj.setObjectReturns(proret);
        }

        /**
         * Sets the debug obj parameters.
         *
         * @param database the database
         * @param procid the procid
         * @param procname the procname
         * @param argtmodes the argtmodes
         * @param argtypes the argtypes
         * @param argnames the argnames
         * @param allin the allin
         * @param objtyp the objtyp
         * @param nsList the ns list
         * @return the debug objects
         */
        public static DebugObjects setDebugObjParameters(Database database, int procid, String procname,
                String argtmodes, String argtypes, String argnames, boolean allin, OBJECTTYPE objtyp,
                ArrayList<Namespace> nsList) {
            DebugObjects dbgObj;
            ObjectParameter[] args;
            args = ObjectParameter.getVariables(argtypes, argnames, argtmodes, allin, nsList);
            dbgObj = new DebugObjects(procid, procname, objtyp, database);
            dbgObj.setObjectParameters(args);
            return dbgObj;
        }

        /**
         * Sets the debug object type.
         *
         * @param lang the lang
         * @return the objecttype
         */
        public static OBJECTTYPE setDebugObjectType(String lang) {
            OBJECTTYPE objtyp = null;
            if (IMessagesConstantsOne.PLPGSQL.equalsIgnoreCase(lang)) {
                objtyp = OBJECTTYPE.PLSQLFUNCTION;
            } else if (IMessagesConstantsOne.SQL.equalsIgnoreCase(lang)) {
                objtyp = OBJECTTYPE.SQLFUNCTION;
            } else if (IMessagesConstantsOne.C_FUN.equalsIgnoreCase(lang)) {
                objtyp = OBJECTTYPE.CFUNCTION;
            }
            return objtyp;
        }

        /**
         * Sets the is debuggable.
         *
         * @param dbgObj the dbg obj
         * @param lang the lang
         */
        public static void setIsDebuggable(DebugObjects dbgObj, String lang) {
            if (IMessagesConstantsOne.PLPGSQL.equalsIgnoreCase(lang)) {
                dbgObj.setIsDebuggable(true);

            } else {
                dbgObj.setIsDebuggable(false);
            }
        }

        /**
         * Adds the information schema to list.
         *
         * @param database the database
         * @param nsList the ns list
         */
        public static void addInformationSchemaToList(Database database, ArrayList<Namespace> nsList) {
            Namespace ns;
            try {
                INamespace nameSpaceByName = database.getNameSpaceByName("information_schema");
                if (nameSpaceByName instanceof Namespace) {
                    ns = (Namespace) nameSpaceByName;
                    nsList.add(ns);
                }
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("DebugObjects: converting objects failed.", exception);
            }
        }

        /**
         * Adds the pg catalog name space to list.
         *
         * @param database the database
         * @param nsList the ns list
         */
        public static void addPgCatalogNameSpaceToList(Database database, ArrayList<Namespace> nsList) {
            Namespace ns;
            try {
                INamespace nameSpaceByName = database.getNameSpaceByName("pg_catalog");
                if (nameSpaceByName instanceof Namespace) {
                    ns = (Namespace) nameSpaceByName;
                    nsList.add(ns);
                }
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("DebugObjects: converting objects failed.", exception);
            }
        }

        /**
         * Prepare execution query string.
         *
         * @param debugObjects the debug objects
         * @return the string
         * @throws DatabaseOperationException the database operation exception
         */
        public static String prepareExecutionQueryString(DebugObjects debugObjects) throws DatabaseOperationException {
            debugObjects.getDatabase().getExecutor().getQueryExectuionString(debugObjects);

            return debugObjects.getExecutionQuery();
        }

        /**
         * Gets the display name.
         *
         * @param isAutoSuggest the is auto suggest
         * @param parameters the parameters
         * @param returnType the return type
         * @param dbgObjName the dbg obj name
         * @param parentName the parent name
         * @return the display name
         */
        public static String getDisplayName(boolean isAutoSuggest, ObjectParameter[] parameters,
                ObjectParameter returnType, String dbgObjName, String parentName) {
            boolean isFirstParam = true;
            String name = isAutoSuggest ? parentName : dbgObjName;
            StringBuilder displayName = new StringBuilder(name);

            displayName.append("(");

            if (null != parameters) {

                int paramLen = parameters.length;
                for (int index = 0; index < paramLen; index++) {
                    if (!isFirstParam) {
                        displayName.append(", ");
                    }

                    displayName.append(parameters[index].getDisplayName(isAutoSuggest));

                    isFirstParam = false;
                }
            }

            displayName.append(")");
            if (!isAutoSuggest) {
                if (null != returnType) {
                    displayName.append(" - ").append(returnType.getDataType());
                }
            }

            return displayName.toString();
        }

        static class GenerateExecutionTemplateParameter {
            private ArrayList<ObjectParameter> templateParams;
            private ArrayList<String> output;
            private ArrayList<DefaultParameter> outParams;

            public GenerateExecutionTemplateParameter(ArrayList<ObjectParameter> templateParams,
                    ArrayList<String> output, ArrayList<DefaultParameter> outParams) {
                this.templateParams = templateParams;
                this.output = output;
                this.outParams = outParams;
            }

            public ArrayList<ObjectParameter> getTemplateParams() {
                return templateParams;
            }

            public void setTemplateParams(ArrayList<ObjectParameter> templateParams) {
                this.templateParams = templateParams;
            }

            public ArrayList<String> getOutput() {
                return output;
            }

            public void setOutput(ArrayList<String> output) {
                this.output = output;
            }

            public ArrayList<DefaultParameter> getOutParams() {
                return outParams;
            }

            public void setOutParams(ArrayList<DefaultParameter> outParams) {
                this.outParams = outParams;
            }
        }

        /**
         * Generate execution template.
         *
         * @param namespace the namespace
         * @param dbgObjName the dbg obj name
         * @param params the params
         * @param parameterObject the parameter object
         * @throws DatabaseOperationException the database operation exception
         */
        public static void generateExecutionTemplate(Namespace namespace, String dbgObjName, ObjectParameter[] params,
                GenerateExecutionTemplateParameter parameterObject) throws DatabaseOperationException {
            boolean hasout = false;
            StringBuilder template = new StringBuilder("SELECT");
            StringBuilder usagehint = new StringBuilder("");

            template.append(" ");
            if (namespace != null) {
                template.append("\"" + namespace.getName() + "\"").append(".");
            }
            template.append(ServerObject.getQualifiedObjectName(dbgObjName));
            template.append(MPPDBIDEConstants.LINE_SEPARATOR).append("(");
            template.append(MPPDBIDEConstants.LINE_SEPARATOR);

            hasout = createTemplateFromParams(params, parameterObject.getTemplateParams(), template,
                    parameterObject.getOutParams());

            if (hasout) {
                usagehint.append(MessageConfigLoader.getProperty(IMessagesConstants.EXEC_DEBUG_DIALOG_OUT_PARA_MSG)
                        + MPPDBIDEConstants.LINE_SEPARATOR);
            }

            parameterObject.getOutput().add(template.toString());
            parameterObject.getOutput().add(usagehint.toString());
        }

        /**
         * creates the TemplateFromParams
         * 
         * @param params the params
         * @param templateParams the templateParams
         * @param template the template
         * @param outParams the outParams
         * @return return true, if is success
         * @throws DatabaseOperationException the DatabaseOperationException
         */
        public static boolean createTemplateFromParams(ObjectParameter[] params,
                ArrayList<ObjectParameter> templateParams, StringBuilder template,
                ArrayList<DefaultParameter> outParams) throws DatabaseOperationException {
            boolean isFirstParam = true;
            boolean hasout = false;
            if (null != params) {

                ObjectParameter param = null;
                int paramSize = params.length;

                for (int paramIndex = 0; paramIndex < paramSize; paramIndex++) {
                    param = params[paramIndex];

                    switch (param.getType()) {
                        case IN:
                        case INOUT: {
                            appendInoutParameter(templateParams, template, isFirstParam, param, paramSize, paramIndex);
                            break;
                        }
                        case OUT: {
                            DefaultParameter defualtParams = changeIntoDefault(param);
                            outParams.add(defualtParams);
                            hasout = true;
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    isFirstParam = false;
                }
            }
            template.append(MPPDBIDEConstants.LINE_SEPARATOR);
            template.append(")");
            template.append(MPPDBIDEConstants.LINE_SEPARATOR);
            return hasout;
        }

        /**
         * changes object parameter into default parameter
         * 
         * @param param the object parameter
         * @return the default parameter
         */
        static DefaultParameter changeIntoDefault(ObjectParameter param) {
            DefaultParameter defaultParam = new DefaultParameter(param.getName(), param.getDataType(), param.getValue(),
                    param.getType());
            return defaultParam;
        }

        /**
         * Append inout parameter.
         *
         * @param templateParams the template params
         * @param template the template
         * @param isFirstParam the is first param
         * @param param the param
         * @param paramSize the param size
         * @param paramIndex the param index
         * @throws DatabaseOperationException the database operation exception
         */
        public static void appendInoutParameter(ArrayList<ObjectParameter> templateParams, StringBuilder template,
                boolean isFirstParam, ObjectParameter param, int paramSize, int paramIndex)
                throws DatabaseOperationException {
            String argType;
            String argName;
            if (!isFirstParam) {
                template.append(MPPDBIDEConstants.LINE_SEPARATOR);
            }

            argType = param.getDataType();
            argName = param.getName();
            if (null != argType) {
                if ("refcursor".equals(argType)) {
                    MPPDBIDELoggerUtility.error(MessageConfigLoader
                            .getProperty(IMessagesConstants.ERR_BL_REFCUR_EXECUTION_TEMPLATE_FAILURE));
                    throw new DatabaseOperationException(IMessagesConstants.ERR_BL_REFCUR_EXECUTION_TEMPLATE_FAILURE);
                } else {
                    template.append("\t");
                    template.append(argType);
                    if (paramIndex < paramSize - 1) {
                        template.append(" ,");
                    }
                    template.append("  --");
                    template.append(argName);
                    template.append(" ");
                    template.append(argType);
                }
            }

            templateParams.add(param);
        }

        /**
         * Gets the data type name.
         *
         * @param rettype the rettype
         * @param nsList the ns list
         * @return the data type name
         */
        static String getDataTypeName(int rettype, ArrayList<Namespace> nsList) {
            String convertedDataType = GaussDatatypeUtils.convertToClientType(rettype);
            if (null == convertedDataType) {
                convertedDataType = TypeMetaDataUtil.getDataTypeFromNamespace(rettype, nsList);
            }

            return convertedDataType;
        }

        /**
         * Gets the drop func statement.
         *
         * @param ifExists the if exists
         * @return the drop func statement
         */
        public static String getDropFuncStatement(boolean ifExists) {
            return DROP_FUNC + (ifExists ? IF_EXISTS : "");
        }

        /**
         * Gets the drop query.
         *
         * @param dropStatement the drop statement
         * @param isCascade the is cascade
         * @param namespace the namespace
         * @param parameters the parameters
         * @param dbgObjName the dbg obj name
         * @return the drop query
         */
        public static String getDropQuery(String dropStatement, boolean isCascade, Namespace namespace,
                ObjectParameter[] parameters, String dbgObjName) {
            StringBuilder qry = new StringBuilder(dropStatement);
            if (namespace != null) {
                qry.append(namespace.getQualifiedObjectName()).append(".");
            }

            qry.append(ServerObject.getQualifiedObjectName(dbgObjName)).append("(");

            if (null != parameters) {

                for (int index = 0; index < parameters.length; index++) {
                    qry.append(parameters[index].updateQuery());

                    if (index < (parameters.length - 1)) {
                        qry.append(",");
                    }
                }
            }

            qry.append(")");

            if (isCascade) {
                qry.append(MPPDBIDEConstants.CASCADE);
            }

            return qry.toString();
        }

        /**
         * Refresh db object.
         *
         * @param debugObjects the debug objects
         * @return the i debug object
         * @throws MPPDBIDEException the MPPDBIDE exception
         */
        public static IDebugObject refreshDbObject(DebugObjects debugObjects) throws MPPDBIDEException {
            return debugObjects.getParent().refreshDbObject(debugObjects);
        }

    }

    /**
     * Sets the language.
     *
     * @param language the new language
     */
    private void setLanguage(String language) {
        this.lang = language;
    }

    /**
     * Gets the lang.
     *
     * @return the lang
     */
    public String getLang() {
        return lang;
    }

    /**
     * Generate execution template.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    public void generateExecutionTemplate() throws DatabaseOperationException {
        ArrayList<ObjectParameter> templateParams = new ArrayList<ObjectParameter>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        ArrayList<DefaultParameter> outParams = new ArrayList<DefaultParameter>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        ArrayList<String> output = new ArrayList<String>(2);
        DebugObjectsUtils.generateExecutionTemplate(namespace, getName(), getObjectParameters(),
                new GenerateExecutionTemplateParameter(templateParams, output, outParams));
        setTemplateParameters(templateParams);
        setOutParameters(outParams);
        setExecuteTemplate(output.get(0));
        setUsagehint(output.get(1));
    }

    /**
     * Gets the latest souce code.
     *
     * @return the latest souce code
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public ISourceCode getLatestSouceCode() throws DatabaseOperationException, DatabaseCriticalException {
        if (!this.isSourceLoaded) {
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_FETCH_SRC_CODE, true);
            try {
                refreshSourceCode();
            } finally {
                MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_FETCH_SRC_CODE, false);
            }
        }
        return getSourceCode();
    }

    /**
     * Belongs to.
     *
     * @param database the database
     * @param server the server
     * @return true, if successful
     */
    public boolean belongsTo(Database database, Server server) {
        // un-attached debug obj
        if (this.getNamespace() == null) {
            return false;
        }
        return this.getNamespace().belongsTo(database, server);
    }

    /**
     * Can support debug.
     *
     * @return canSupportDebug
     */
    public boolean canSupportDebug() {
        if (IMessagesConstantsOne.PLPGSQL.equalsIgnoreCase(getLang())) {
            return true;
        }

        return false;
    }

    @Override
    public String getObjectBrowserLabel() {
        return getDisplayName(false);
    }

    @Override
    public Namespace getParent() {
        return this.namespace;
    }

    /**
     * Gets the PL source editor elm id.
     *
     * @return the PL source editor elm id
     */
    public String getPLSourceEditorElmId() {
        String sourceEditorElmId = String.valueOf(this.getOid())
                .concat(this.getDatabase().getName() + '@' + this.getDatabase().getServerName() + this.objectType);
        return sourceEditorElmId;
    }

    /**
     * Gets the PL source editor elm tooltip.
     *
     * @return the PL source editor elm tooltip
     */
    public String getPLSourceEditorElmTooltip() {
        String sourceEditorElmTooltip = this.getDatabase().getServerName() + " : " + this.getDatabase().getName()
                + " : " + this.getNamespace().getName() + " : " + this.getDisplayNameWithArgName();
        return sourceEditorElmTooltip;
    }

    @Override
    public String getDropQuery(boolean isCascade) {
        return DebugObjectsUtils.getDropQuery(DebugObjectsUtils.getDropFuncStatement(true), isCascade, namespace,
                parameters, getName());
    }

    @Override
    public boolean isExportAllowed(EXPORTTYPE exportType) {
        if (exportType == EXPORTTYPE.SQL_DDL) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isChanged(String latCode) throws DatabaseCriticalException, DatabaseOperationException {
        return ((SourceCode) this.sourceCode).isChanged(latCode);
    }

    @Override
    public String getLatestInfo() throws DatabaseCriticalException, DatabaseOperationException {
        SourceCode latestSourceCode = new SourceCode();
        String funcDefinitionQuery = String.format(Locale.ENGLISH,
                "select headerlines, definition from PG_GET_FUNCTIONDEF(%d);", getOid());

        ConnectionManager connectionManager = this.getConnectionManager();
        if (connectionManager != null) {
            handleFuncVersionQuery(latestSourceCode, funcDefinitionQuery, connectionManager);
        }
        return latestSourceCode.getCode();
    }

    private void handleFuncVersionQuery(SourceCode latestSourceCode, String funcDefinitionQuery,
            ConnectionManager connectionManager)
            throws DatabaseCriticalException, DatabaseOperationException {
        ResultSet funcDefinitionQueryRS = connectionManager.execSelectAndReturnRsOnObjBrowserConn(funcDefinitionQuery);
        ResultSet funcVersionQueryRS = executeVersionQuery(connectionManager);
        addLatestSrcCode(latestSourceCode, connectionManager, funcDefinitionQueryRS, funcVersionQueryRS);
    }

    private ResultSet executeVersionQuery(ConnectionManager connectionManager) {
        String funcVersionQuery = String.format(Locale.ENGLISH, "select xmin1, cmin1 from pldbg_get_funcVer(%d);",
                getOid());
        ResultSet funcVersionQueryRS = null;
        try {
            funcVersionQueryRS = connectionManager.execSelectAndReturnRsOnObjBrowserConn(funcVersionQuery);
        } catch (DatabaseCriticalException | DatabaseOperationException exp) {
            MPPDBIDELoggerUtility.error("DEBUG not enabled");
        }
        return funcVersionQueryRS;
    }

    private void addLatestSrcCode(SourceCode latestSourceCode, ConnectionManager connectionManager,
            ResultSet funcDefinitionQueryRS, ResultSet funcVersionQueryRS) throws DatabaseOperationException {
        try {
            handleLatestSourceCode(latestSourceCode, funcDefinitionQueryRS, funcVersionQueryRS);
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            connectionManager.closeRSOnObjBrowserConn(funcDefinitionQueryRS);
            connectionManager.closeRSOnObjBrowserConn(funcVersionQueryRS);
        }
    }

    private void handleLatestSourceCode(SourceCode latestSourceCode, ResultSet funcDefinitionQueryRS,
            ResultSet funcVersionQueryRS) throws SQLException, DatabaseOperationException {
        handleFuncDefErrorCase(funcDefinitionQueryRS, funcVersionQueryRS);
        latestSourceCode.updateCode(funcDefinitionQueryRS);
        latestSourceCode.updateVersionNumber(funcVersionQueryRS);
    }

    @Override
    public void handleChange(String obj) {
        this.sourceCode.setCode(obj);
    }

    @Override
    public IDebugObject refreshSelf() throws MPPDBIDEException {
        return DebugObjectsUtils.refreshDbObject(this);
    }

    @Override
    public boolean validateObjectType() {
        return true;
    }

    @Override
    public boolean isCodeReloaded() {
        return this.isCodeReloaded;
    }

    @Override
    public void setCodeReloaded(boolean codeReloaded) {
        this.isCodeReloaded = codeReloaded;

    }

    /**
     * Sets the setIsCurrentTerminal flag.
     * 
     * @param setIsCurrentTerminal the flag
     */
    public void setIsCurrentTerminal(boolean isCurrentTerminal) {
        this.isCurrentTerminal = isCurrentTerminal;
    }

    /**
     * Checks if is current terminal.
     * 
     * @return true, if is current terminal
     */
    public boolean getCurrentTerminal() {
        return this.isCurrentTerminal;
    }

    /**
     * Sets the isEditTerminalInputValues flag.
     * 
     * @param isEditTerminalInputValues the flag
     */
    public void setIsEditTerminalInputValues(boolean isEditTerminalInputValues) {
        this.isEditTerminalInputValues = isEditTerminalInputValues;
    }

    /**
     * Checks if is TerminalInputValues can be editable.
     * 
     * @return true, if is TerminalInputValues can be editable.
     */
    public boolean getEditTerminalInputValues() {
        return this.isEditTerminalInputValues;
    }

}
