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

package com.huawei.mppdbide.presentation.userrole;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.MessageQueue;

/**
 * 
 * Title: class
 * 
 * Description: The Class GrantRevokeCore.
 * 
 * @since 3.0.0
 */
public class GrantRevokeCore {
    private Database database;
    private Namespace namespace;
    private Object[] objectOption;

    /**
     * Instantiates a new grant revoke core.
     *
     * @param isBatch the is batch
     * @param object the object
     */
    public GrantRevokeCore(boolean isBatch, Object object) {
        if (isBatch) {
            initForBatch(object);
        } else {
            initForNotBatch(object);
        }
    }

    /**
     * Inits the for batch.
     *
     * @param object the object
     */
    private void initForBatch(Object object) {
        List<?> objects = (List<?>) object;

        if (!((ServerObject) objects.get(0) instanceof Namespace)) {
            this.namespace = (Namespace) (((ServerObject) objects.get(0)).getParent());
        }

        this.database = ((ServerObject) objects.get(0)).getDatabase();
        objectOption = objects.toArray();
    }

    /**
     * Inits the for not batch.
     *
     * @param object the object
     */
    private void initForNotBatch(Object object) {
        ObjectGroup<ServerObject> objectGroup = (ObjectGroup<ServerObject>) object;
        this.database = objectGroup.getDatabase();
        objectOption = objectGroup.getChildren();
        if (!(objectOption[0] instanceof Namespace)) {
            this.namespace = (Namespace) objectGroup.getParent();
        }
    }

    /**
     * Generate sql.
     *
     * @param params the params
     * @return the list
     */
    public List<String> generateSql(GrantRevokeParameters params) {
        List<String> previewSqls = new ArrayList<>();
        StringBuffer objectStrBuf = new StringBuffer();
        StringBuffer userRoleStrBuf = new StringBuffer();

        List<Object> selectedObjects = params.getSelectedObjects();
        String userRolesStr = params.getUserRolesStr();
        List<String> withGrantOptionPrivileges = params.getWithGrantOptionPrivileges();
        List<String> withoutGrantOptionPrivileges = params.getWithoutGrantOptionPrivileges();
        boolean allPrivilege = params.isAllPrivilege();
        boolean allWithGrantOption = params.isAllWithGrantOption();
        List<String> revokePrivileges = params.getRevokePrivileges();
        List<String> revokeGrantPrivileges = params.getRevokeGrantPrivileges();
        boolean revokeAllPrivilege = params.isRevokeAllPrivilege();
        boolean revokeAllGrantPrivilege = params.isRevokeAllGrantPrivilege();
        boolean isGrant = params.isGrant();

        userRoleStrBuf.append(userRolesStr);
        appendNamespaceForSelectedObj(objectStrBuf, selectedObjects);

        if (isGrant) {
            queryFormationOnSelectionOfGrant(previewSqls, objectStrBuf, userRoleStrBuf, selectedObjects,
                    withGrantOptionPrivileges, withoutGrantOptionPrivileges, allPrivilege, allWithGrantOption);
        } else {
            queryFormationOnSelectionOfRevoke(previewSqls, objectStrBuf, userRoleStrBuf, selectedObjects,
                    revokePrivileges, revokeGrantPrivileges, revokeAllPrivilege, revokeAllGrantPrivilege);
        }
        return previewSqls;
    }

    /**
     * Query formation on selection of revoke.
     *
     * @param previewSqls the preview sqls
     * @param objectStrBuf the object str buf
     * @param userRoleStrBuf the user role str buf
     * @param selectedObjects the selected objects
     * @param revokePrivileges the revoke privileges
     * @param revokeGrantPrivileges the revoke grant privileges
     * @param revokeAllPrivilege the revoke all privilege
     * @param revokeAllGrantPrivilege the revoke all grant privilege
     */
    private void queryFormationOnSelectionOfRevoke(List<String> previewSqls, StringBuffer objectStrBuf,
            StringBuffer userRoleStrBuf, List<Object> selectedObjects, List<String> revokePrivileges,
            List<String> revokeGrantPrivileges, boolean revokeAllPrivilege, boolean revokeAllGrantPrivilege) {
        StringBuffer revokePrivilegesStrBuf = new StringBuffer();
        StringBuffer revokeGrantPrivilegesStrBuf = new StringBuffer();

        appendPriviledgeWithGrant(revokePrivileges, revokePrivilegesStrBuf);
        appendPriviledgeWithGrant(revokeGrantPrivileges, revokeGrantPrivilegesStrBuf);
        String revokeSqlFormat = null;
        revokeSqlFormat = appendRevokeSyntaxForSelectedObj(selectedObjects);

        appendQueryForRevokAllPrivilege(previewSqls, objectStrBuf, userRoleStrBuf, revokeAllPrivilege,
                revokeAllGrantPrivilege, revokePrivilegesStrBuf, revokeGrantPrivilegesStrBuf, revokeSqlFormat);
    }

    /**
     * Append query for revok all privilege.
     *
     * @param previewSqls the preview sqls
     * @param objectStrBuf the object str buf
     * @param userRoleStrBuf the user role str buf
     * @param revokeAllPrivilege the revoke all privilege
     * @param revokeAllGrantPrivilege the revoke all grant privilege
     * @param revokePrivilegesStrBuf the revoke privileges str buf
     * @param revokeGrantPrivilegesStrBuf the revoke grant privileges str buf
     * @param revokeSqlFormat the revoke sql format
     */
    private void appendQueryForRevokAllPrivilege(List<String> previewSqls, StringBuffer objectStrBuf,
            StringBuffer userRoleStrBuf, boolean revokeAllPrivilege, boolean revokeAllGrantPrivilege,
            StringBuffer revokePrivilegesStrBuf, StringBuffer revokeGrantPrivilegesStrBuf, String revokeSqlFormat) {
        if (revokeAllPrivilege) {
            previewSqls.add(MessageFormat.format(revokeSqlFormat, "", "ALL",
                    objectStrBuf.substring(0, objectStrBuf.length() - 1), userRoleStrBuf));
        } else if (revokeAllGrantPrivilege) {
            previewSqls.add(MessageFormat.format(revokeSqlFormat, "GRANT OPTION FOR", "ALL",
                    objectStrBuf.substring(0, objectStrBuf.length() - 1), userRoleStrBuf));
        } else {
            if (revokePrivilegesStrBuf.length() > 0) {
                previewSqls.add(MessageFormat.format(revokeSqlFormat, "",
                        revokePrivilegesStrBuf.substring(0, revokePrivilegesStrBuf.length() - 1),
                        objectStrBuf.substring(0, objectStrBuf.length() - 1), userRoleStrBuf));
            }

            if (revokeGrantPrivilegesStrBuf.length() > 0) {
                previewSqls.add(MessageFormat.format(revokeSqlFormat, "GRANT OPTION FOR",
                        revokeGrantPrivilegesStrBuf.substring(0, revokeGrantPrivilegesStrBuf.length() - 1),
                        objectStrBuf.substring(0, objectStrBuf.length() - 1), userRoleStrBuf));
            }
        }
    }

    /**
     * Append revoke syntax for selected obj.
     *
     * @param selectedObjects the selected objects
     * @return the string
     */
    private String appendRevokeSyntaxForSelectedObj(List<Object> selectedObjects) {
        String revokeSqlFormat;
        if (selectedObjects.get(0) instanceof Namespace) {
            revokeSqlFormat = "REVOKE {0} {1} ON SCHEMA {2} FROM {3} CASCADE;";
        } else if (selectedObjects.get(0) instanceof DebugObjects) {
            revokeSqlFormat = "REVOKE {0} {1} ON FUNCTION {2} FROM {3} CASCADE;";
        } else {
            revokeSqlFormat = "REVOKE {0} {1} ON {2} FROM {3} CASCADE;";
        }
        return revokeSqlFormat;
    }

    /**
     * Query formation on selection of grant.
     *
     * @param previewSqls the preview sqls
     * @param objectStrBuf the object str buf
     * @param userRoleStrBuf the user role str buf
     * @param selectedObjects the selected objects
     * @param withGrantOptionPrivileges the with grant option privileges
     * @param withoutGrantOptionPrivileges the without grant option privileges
     * @param allPrivilege the all privilege
     * @param allWithGrantOption the all with grant option
     */
    private void queryFormationOnSelectionOfGrant(List<String> previewSqls, StringBuffer objectStrBuf,
            StringBuffer userRoleStrBuf, List<Object> selectedObjects, List<String> withGrantOptionPrivileges,
            List<String> withoutGrantOptionPrivileges, boolean allPrivilege, boolean allWithGrantOption) {
        StringBuffer withGrantOptionPrivilegeStrBuf = new StringBuffer();
        StringBuffer withoutGrantOptionPrivilegeStrBuf = new StringBuffer();

        appendPriviledgeWithGrant(withGrantOptionPrivileges, withGrantOptionPrivilegeStrBuf);
        appendPriviledgeWithGrant(withoutGrantOptionPrivileges, withoutGrantOptionPrivilegeStrBuf);

        String grantSqlFormat = null;
        grantSqlFormat = appendGrantSyntaxForSelectedObj(selectedObjects);

        appendQueryForAllPrivilegeWithGrantOption(previewSqls, objectStrBuf, userRoleStrBuf, allPrivilege,
                allWithGrantOption, grantSqlFormat);

        appendQueryForAllPrivilegeWithoutGrant(previewSqls, objectStrBuf, userRoleStrBuf, allPrivilege,
                allWithGrantOption, withGrantOptionPrivilegeStrBuf, grantSqlFormat);

        appendQueryForNotAllPrivilege(previewSqls, objectStrBuf, userRoleStrBuf, allPrivilege,
                withGrantOptionPrivilegeStrBuf, withoutGrantOptionPrivilegeStrBuf, grantSqlFormat);
    }

    /**
     * Append query for not all privilege.
     *
     * @param previewSqls the preview sqls
     * @param objectStrBuf the object str buf
     * @param userRoleStrBuf the user role str buf
     * @param allPrivilege the all privilege
     * @param withGrantOptionPrivilegeStrBuf the with grant option privilege str
     * buf
     * @param withoutGrantOptionPrivilegeStrBuf the without grant option
     * privilege str buf
     * @param grantSqlFormat the grant sql format
     */
    private void appendQueryForNotAllPrivilege(List<String> previewSqls, StringBuffer objectStrBuf,
            StringBuffer userRoleStrBuf, boolean allPrivilege, StringBuffer withGrantOptionPrivilegeStrBuf,
            StringBuffer withoutGrantOptionPrivilegeStrBuf, String grantSqlFormat) {
        if (!allPrivilege) {
            if (withGrantOptionPrivilegeStrBuf.length() > 0) {
                previewSqls.add(MessageFormat.format(grantSqlFormat,
                        withGrantOptionPrivilegeStrBuf.substring(0, withGrantOptionPrivilegeStrBuf.length() - 1),
                        objectStrBuf.substring(0, objectStrBuf.length() - 1), userRoleStrBuf, "WITH GRANT OPTION"));
            }

            if (withoutGrantOptionPrivilegeStrBuf.length() > 0) {
                previewSqls.add(MessageFormat.format(grantSqlFormat,
                        withoutGrantOptionPrivilegeStrBuf.substring(0, withoutGrantOptionPrivilegeStrBuf.length() - 1),
                        objectStrBuf.substring(0, objectStrBuf.length() - 1), userRoleStrBuf, ""));
            }
        }
    }

    /**
     * Append query for all privilege without grant.
     *
     * @param previewSqls the preview sqls
     * @param objectStrBuf the object str buf
     * @param userRoleStrBuf the user role str buf
     * @param allPrivilege the all privilege
     * @param allWithGrantOption the all with grant option
     * @param withGrantOptionPrivilegeStrBuf the with grant option privilege str
     * buf
     * @param grantSqlFormat the grant sql format
     */
    private void appendQueryForAllPrivilegeWithoutGrant(List<String> previewSqls, StringBuffer objectStrBuf,
            StringBuffer userRoleStrBuf, boolean allPrivilege, boolean allWithGrantOption,
            StringBuffer withGrantOptionPrivilegeStrBuf, String grantSqlFormat) {
        if (allPrivilege && !allWithGrantOption) {
            previewSqls.add(MessageFormat.format(grantSqlFormat, "ALL",
                    objectStrBuf.substring(0, objectStrBuf.length() - 1), userRoleStrBuf, ""));

            if (withGrantOptionPrivilegeStrBuf.length() > 0) {
                previewSqls.add(MessageFormat.format(grantSqlFormat,
                        withGrantOptionPrivilegeStrBuf.substring(0, withGrantOptionPrivilegeStrBuf.length() - 1),
                        objectStrBuf.substring(0, objectStrBuf.length() - 1), userRoleStrBuf, "WITH GRANT OPTION"));
            }
        }
    }

    /**
     * Append query for all privilege with grant option.
     *
     * @param previewSqls the preview sqls
     * @param objectStrBuf the object str buf
     * @param userRoleStrBuf the user role str buf
     * @param allPrivilege the all privilege
     * @param allWithGrantOption the all with grant option
     * @param grantSqlFormat the grant sql format
     */
    private void appendQueryForAllPrivilegeWithGrantOption(List<String> previewSqls, StringBuffer objectStrBuf,
            StringBuffer userRoleStrBuf, boolean allPrivilege, boolean allWithGrantOption, String grantSqlFormat) {
        if (allPrivilege && allWithGrantOption) {
            previewSqls.add(MessageFormat.format(grantSqlFormat, "ALL",
                    objectStrBuf.substring(0, objectStrBuf.length() - 1), userRoleStrBuf, "WITH GRANT OPTION"));
        }
    }

    /**
     * Append grant syntax for selected obj.
     *
     * @param selectedObjects the selected objects
     * @return the string
     */
    private String appendGrantSyntaxForSelectedObj(List<Object> selectedObjects) {
        String grantSqlFormat;
        if (selectedObjects.get(0) instanceof Namespace) {
            grantSqlFormat = "GRANT {0} ON SCHEMA {1} TO {2} {3};";
        } else if (selectedObjects.get(0) instanceof DebugObjects) {
            grantSqlFormat = "GRANT {0} ON FUNCTION {1} TO {2} {3};";
        } else {
            grantSqlFormat = "GRANT {0} ON {1} TO {2} {3};";
        }
        return grantSqlFormat;
    }

    /**
     * Append priviledge with grant.
     *
     * @param withGrantOptionPrivileges the with grant option privileges
     * @param withGrantOptionPrivilegeStrBuf the with grant option privilege str
     * buf
     */
    private void appendPriviledgeWithGrant(List<String> withGrantOptionPrivileges,
            StringBuffer withGrantOptionPrivilegeStrBuf) {
        for (String privilegeName : withGrantOptionPrivileges) {
            if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeName)) {
                continue;
            }
            withGrantOptionPrivilegeStrBuf.append(privilegeName).append(",");
        }
    }

    /**
     * Append namespace for selected obj.
     *
     * @param objectStrBuf the object str buf
     * @param selectedObjects the selected objects
     */
    private void appendNamespaceForSelectedObj(StringBuffer objectStrBuf, List<Object> selectedObjects) {
        for (Object object : selectedObjects) {
            ServerObject serverObject = (ServerObject) object;
            if (selectedObjects.get(0) instanceof Namespace) {
                objectStrBuf.append(serverObject.getQualifiedObjectName()).append(",");
            } else if (selectedObjects.get(0) instanceof DebugObjects) {
                objectStrBuf.append(namespace.getQualifiedObjectName()).append(".")
                        .append(((DebugObjects) serverObject).getGrantRevokeName()).append(",");
            } else {
                objectStrBuf.append(namespace.getQualifiedObjectName()).append(".")
                        .append(serverObject.getQualifiedObjectName()).append(",");
            }
        }
    }

    /**
     * Modify object privilege.
     *
     * @param conn the conn
     * @param sqls the sqls
     * @param messageQueue the message queue
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void modifyObjectPrivilege(DBConnection conn, List<String> sqls, MessageQueue messageQueue)
            throws DatabaseOperationException, DatabaseCriticalException {
        try {
            conn.execNonSelect("START TRANSACTION;");
            for (String sql : sqls) {
                conn.execQueryWithMsgQueue(sql, messageQueue);
            }
            conn.execNonSelect("COMMIT;");
            MPPDBIDELoggerUtility.debug("Commit object privilege modification");
        } catch (Exception e) {
            conn.execNonSelect("ROLLBACK;");
            MPPDBIDELoggerUtility.debug("Rollback object privilege modification");
            throw e;
        }

    }

    /**
     * Gets the user role option.
     *
     * @return the user role option
     */
    public List<String> getUserRoleOption() {
        return Arrays.asList(database.getServer().getUserRoleObjectGroup().getChildren()).stream()
                .map(object -> ((ServerObject) object).getQualifiedObjectName() != null
                        ? ((ServerObject) object).getQualifiedObjectName()
                        : "")
                .collect(Collectors.toList());
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Gets the object option.
     *
     * @return the object option
     */
    public Object[] getObjectOption() {
        return objectOption != null ? Arrays.copyOf(objectOption, objectOption.length) : new Object[0];
    }

}
