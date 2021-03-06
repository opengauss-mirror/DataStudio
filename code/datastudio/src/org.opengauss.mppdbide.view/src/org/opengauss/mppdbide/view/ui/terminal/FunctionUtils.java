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

package org.opengauss.mppdbide.view.ui.terminal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.INamespace;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.HandlerUtilities;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.utils.common.UICommonUtil;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class Description: The Class FunctionUtils.
 *
 * @since 3.0.0
 */
public class FunctionUtils {

    /**
     * Refresh func without schema.
     *
     * @param parser the parser
     * @param objectBrowserModel the object browser model
     * @param plSourceEditor the pl source editor
     * @param dbConnection the db connection
     * @return the refresh object details
     */
    public static RefreshObjectDetails refreshFuncWithoutSchema(FunctionProcNameParser parser,
            ObjectBrowser objectBrowserModel, PLSourceEditor plSourceEditor,
            TerminalExecutionConnectionInfra dbConnection) {
        String schemaName = null;
        RefreshObjectDetails refDetails = new RefreshObjectDetails();
        if (null != parser.getSchemaName() && null != parser.getFuncName() && !parser.getFuncName().equals("")) {
            schemaName = parser.getSchemaName();
        } else {
            try {
                schemaName = plSourceEditor.getDatabase().getDefaultSchemaName(dbConnection.getConnection());
            } catch (MPPDBIDEException exception) {
                MPPDBIDELoggerUtility.error("Refresh failed.", exception);
                return refDetails;
            }
        }
        schemaName = UICommonUtil.getUnQuotedIdentifierOLAP(schemaName);
        try {
            INamespace namespace = (INamespace) plSourceEditor.getDebugObject().getDatabase()
                    .getNameSpaceByName(schemaName);
            String objName = null;
            if (parser.getObjectName() != null) {
                objName = parser.getObjectName();  
            } else {
                objName = plSourceEditor.getDebugObject().getName();
            }
            namespace.refreshDebugObjectGroup();

            ServerObject obj = namespace.getFunctions().get(objName);
            obj = getServerObject(plSourceEditor, obj);
            obj.setName(getObjectName(objName));
            refDetails.setObjToBeRefreshed(obj);
            refDetails.setObjectName(objName);
            refDetails.setParent(namespace.getFunctions());
            refDetails.setNamespace(namespace);
            refDetails.setOperationType("CREATE_FUNC_PROC");
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("Refresh failed.", exception);
            return refDetails;
        }

        return refDetails;
    }

    private static String getObjectName(String objName) {
        String[] objNames = objName.split("\\.");
        return objNames[objNames.length - 1];
    }
    
    private static ServerObject getServerObject(PLSourceEditor plSourceEditor, ServerObject objParam) {
        ServerObject obj = objParam;
        if (obj == null) {
            obj = (ServerObject) plSourceEditor.getDebugObject();
        }
        return obj;
    }

    private static void displayNewFunctionCreatedMsg(PLSourceEditor plSourceEditor, IDebugObject object) {
        String createFuncPkg = IMessagesConstants.CREATE_FUNCTION_PROCEDURE;
        String createNewObject = IMessagesConstants.NEW_OBJECT_WILL_BE_OPENED;

        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, plSourceEditor.getClass()),
                MessageConfigLoader.getProperty(createFuncPkg), MessageConfigLoader.getProperty(createNewObject),
                new String[] {MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK)}, 0);
    }

    /**
     * Open debug objects.
     *
     * @param grp the grp
     * @param schemaName the schema name
     * @param dbConnection the db connection
     * @param plSourceEdtor the pl source edtor
     * @param parser the parser
     * @return the i debug object
     * @throws DatabaseCriticalException the database critical exception
     */
    public static IDebugObject openDebugObjects(String grp, String schemaName,
            TerminalExecutionConnectionInfra dbConnection, PLSourceEditor plSourceEdtor, FunctionProcNameParser parser)
            throws DatabaseCriticalException {
        String functionOidQuery = "select oid from pg_proc where  proname = ? and pronamespace = (select oid from pg_namespace where nspname = ?)";
        DebugObjects object = null;
        ResultSet functionResultSet = null;
        DBConnection conn = dbConnection.getConnection();
        try {
            functionResultSet = conn.execSelectForSearch2Parmeters(functionOidQuery,
                    grp.contains("\"") ? String.valueOf(grp).replace("\"", "")
                            : String.valueOf(grp).toLowerCase(Locale.ENGLISH),
                    schemaName.toLowerCase(Locale.ENGLISH).replace("\"", ""));
            boolean hasNext;
            hasNext = functionResultSet.next();
            while (hasNext) {
                object = (DebugObjects) ((INamespace) plSourceEdtor.getDebugObject().getDatabase()
                        .getNameSpaceByName(schemaName.toLowerCase(Locale.ENGLISH).replace("\"", ""))).getFunctions()
                                .getObjectById(functionResultSet.getInt("oid"));
                if (object != null && isReturnTypeMatched(object, parser.getRetType())
                        && isParametersMatched(object, parser.getArgs())) {
                    break;
                }
                hasNext = functionResultSet.next();
            }
            if (object == null) {
                return plSourceEdtor.getDebugObject();
            }
            validateOidAndDisplaySourceCode(plSourceEdtor, object);
            return object;
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Query new function oid failed.", exception);
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Query new function oid failed.", exception);
        } finally {
            if (null != conn) {
                conn.closeResultSet(functionResultSet);
            }
        }
        return plSourceEdtor.getDebugObject();
    }

    private static boolean isParametersMatched(DebugObjects dbgObj, List<String[]> args) {
        boolean isMatched = false;
        ObjectParameter[] parameters = dbgObj.getObjectParameters();
        if (parameters.length != args.size()) {
            return false;
        }
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getDataType().equals(convertToServerSupported(args.get(i)[2]))) {
                isMatched = true;
            } else {
                return false;
            }
        }
        return isMatched;
    }

    private static boolean isReturnTypeMatched(DebugObjects dbgObj, String retType) {
        if (dbgObj.getReturnType().getDataType().equals(convertToServerSupported(retType))) {
            return true;
        }
        return false;
    }

    /**
     * Convert to server supported.
     *
     * @param datatypeName the datatype name
     * @return the string
     */
    public static String convertToServerSupported(String datatypeName) {
        if (datatypeName == null) {
            return "void";
        }
        switch (datatypeName) {
            case "bitvarying": {
                return "varbit";
            }
            case "bitvarying[]": {
                return "_varbit";
            }
            case "boolean": {
                return "bool";
            }
            case "character": {
                return "char";
            }
            case "character[]": {
                return "char[]";
            }
            case "charactervarying": {
                return "varchar";
            }
            case "charactervarying[]": {
                return "varchar[]";
            }
            case "timestampwithouttimezone": {
                return "timestamp";
            }
            case "doubleprecision": {
                return "double precision";
            }
            case "timewithouttimezone": {
                return "time";
            }
            case "timewith timezone": {
                return "time with time zone";
            }
            case "timestampwith timezone": {
                return "timestamp with time zone";
            }
            default: {
                return datatypeName;
            }
        }
    }

    private static void validateOidAndDisplaySourceCode(PLSourceEditor plSourceEditor, DebugObjects object)
            throws DatabaseOperationException, DatabaseCriticalException {
        if (object.getOid() != plSourceEditor.getDebugObject().getOid()) {
            displayNewFunctionCreatedMsg(plSourceEditor, object);
        }
        HandlerUtilities.displaySourceCodeInEditor(object, true);
    }

    /**
     * Open debug objects without schema.
     *
     * @param grp the grp
     * @param dbConnection the db connection
     * @param plSrcEdtr the pl src edtr
     * @param parser the parser
     * @return the i debug object
     * @throws DatabaseCriticalException the database critical exception
     */
    public static IDebugObject openDebugObjectsWithoutSchema(String grp, TerminalExecutionConnectionInfra dbConnection,
            PLSourceEditor plSrcEdtr, FunctionProcNameParser parser) throws DatabaseCriticalException {
        String functionOidQuery = "select oid from pg_proc where  proname = ? and pronamespace = (select oid from pg_namespace where nspname = ?)";
        DebugObjects obj = null;
        ResultSet functionRSet = null;
        DBConnection conn = dbConnection.getConnection();
        String defaultSchema = null;
        try {
            defaultSchema = plSrcEdtr.getDatabase().getDefaultSchemaName(conn);
            functionRSet = conn.execSelectForSearch2Parmeters(functionOidQuery,
                    grp.contains("\"") ? String.valueOf(grp).replace("\"", "")
                            : String.valueOf(grp).toLowerCase(Locale.ENGLISH),
                    defaultSchema);
            boolean hasNext;
            hasNext = functionRSet.next();
            while (hasNext) {
                obj = (DebugObjects) ((INamespace) plSrcEdtr.getDebugObject().getDatabase()
                        .getNameSpaceByName(defaultSchema)).getFunctions().getObjectById(functionRSet.getInt("oid"));
                if (isReturnTypeMatched(obj, parser.getRetType()) && isParametersMatched(obj, parser.getArgs())) {
                    break;
                }
                hasNext = functionRSet.next();
            }
            if (obj == null) {
                return plSrcEdtr.getDebugObject();
            }
            validateOidAndDisplaySourceCode(plSrcEdtr, obj);
            return obj;
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Query new function oid failed.", exception);
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Query new function oid failed.", exception);
        } finally {
            if (null != conn) {
                conn.closeResultSet(functionRSet);
            }
        }
        return plSrcEdtr.getDebugObject();
    }
}
