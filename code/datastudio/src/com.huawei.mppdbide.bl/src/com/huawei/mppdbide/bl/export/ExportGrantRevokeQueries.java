/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.export;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ExportGrantRevokeQueries
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 09-Sep-2020]
 * @since 09-Sep-2020
 */

public class ExportGrantRevokeQueries implements IExportGrantRevokePriv {
    private String objectName;
    private String subName;
    private String objType;
    private String acls;
    private String owner;
    private String prefix;
    private boolean foundOwnerPrivs = false;

    /**
     * ExportGrantRevokeQueries constructor
     */
    public ExportGrantRevokeQueries(String objectName, String subName, String objType, String prefix) {
        this.objectName = objectName;
        this.subName = subName;
        this.objType = objType;
        this.prefix = prefix;
    }

    /**
     * executeToGetRelAclQuery execute query for getting acl
     * 
     * @param oid object oid
     * @param nsId namespaceid
     * @param conn connetion
     * @param objName name
     * @return long owner oid
     */
    public long executeToGetRelAclQuery(long oid, long nsId, DBConnection conn, String objName) {
        String query = String.format(Locale.ENGLISH,
                "select relacl,relowner from pg_catalog.pg_class  where oid=%d and relname='%s' and relnamespace=%d",
                oid, objName, nsId);
        Statement stmt = null;
        ResultSet rs = null;
        long ownerId = -1;
        try {
            stmt = conn.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                this.acls = rs.getString("relacl");
                ownerId = rs.getInt("relowner");
            }
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Errpr occured while fetching information about role and acl for object");
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Errpr occured while fetching information about role and acl for object");
            }
        }
        return ownerId;
    }

    /**
     * get Acl information for namespace
     * 
     * @param namespaceName name
     * @param conn connection
     * @return oid for owner
     */
    public long executeToGetRelAclQueryForNamespace(String namespaceName, DBConnection conn) {
        String query = String.format(Locale.ENGLISH, "select nspowner,nspacl from pg_namespace where nspname='%s';",
                namespaceName);
        Statement stmt = null;
        ResultSet rs = null;
        long ownerId = 0;
        try {
            stmt = conn.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                this.acls = rs.getString("nspacl");
                ownerId = rs.getInt("nspowner");
            }
        } catch (SQLException exc) {
            MPPDBIDELoggerUtility.error("Errpr occured while fetching information about role and acl for object");
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException exc) {
                MPPDBIDELoggerUtility.error("Errpr occured while fetching information about role and acl for object");
            }
        }
        return ownerId;
    }

    /**
     * getGrantRevokeQueries get queries Here acls will come as String for
     * example {dsuser=arwdDxt/dsuser,avinash=a*rwDx/dsuser,aaa=arwdDxt/dsuser}
     * 
     * @return string queries
     */
    public String getGrantRevokeQueries() {
        if (this.acls == null) {
            return "";
        }
        if (this.acls.length() == 0) {
            return "";
        }
        if (StringUtils.isEmpty(this.owner)) {
            this.owner = null;
        }
        StringBuilder builder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        builder.append(String.format(Locale.ENGLISH, "%sREVOKE ALL", prefix));
        if (this.subName != null && !StringUtils.isEmpty(this.subName)) {
            builder.append(String.format(Locale.ENGLISH, "(%s)", this.subName));
        }
        builder.append(String.format(Locale.ENGLISH, " ON %s %s FROM PUBLIC;", this.objType, this.objectName));
        builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        ArrayList<String> list = parseAcl(this.acls);
        builder = addGrantRevokeQueries(list, builder);
        return builder.toString();
    }

    /**
     * In this method we are checking privCode and replacing it with privilege
     * string according to object type.
     * 
     * @param privParameter privilege parameter
     * @return PrivilegeParameters parameter
     */
    private PrivilegeParameters addPrevilegeMap(PrivilegeParameters privParameterParam) {
        PrivilegeParameters privParameter = privParameterParam;
        switch (this.objType) {
            case "TABLE":
            case "SEQUENCE":
                privParameter = getPrivParameterForTableSeq(privParameter);
                break;
            case "FUNCTION":
            case "FUNCTIONS":
                privParameter = getPrivilegeString('X', PRIV_EXECUTE, privParameter);
                break;
            case "LANGUAGE":
                privParameter = getPrivilegeString('U', PRIV_USAGE, privParameter);
                break;
            case "SCHEMA":
                privParameter = getPrivilegeString('C', PRIV_CREATE, privParameter);
                privParameter = getPrivilegeString('U', PRIV_USAGE, privParameter);
                break;
            case "TYPE":
            case "TYPES":
                privParameter = getPrivilegeString('U', PRIV_USAGE, privParameter);
                break;
            default:
                return privParameter;
        }
        getFinalPrivParameter(privParameter);
        return privParameter;
    }

    private void getFinalPrivParameter(PrivilegeParameters privParameter) {
        if (privParameter.isAllWithGo()) {
            privParameter.setPriviledgeBuff(resetPrivlegeBuff(privParameter.getPriviledgeBuff()));
            privParameter.setPrevilegeWithGrantBuff(resetPrivlegeBuff(privParameter.getPrevilegeWithGrantBuff()));
            privParameter.getPrevilegeWithGrantBuff().append("ALL");
            if (subName != null) {
                privParameter.getPriviledgeBuff().append(String.format(Locale.ENGLISH, "(%s)", subName));
            }
        }
        if (privParameter.isAllWithoutGo()) {
            privParameter.setPrevilegeWithGrantBuff(resetPrivlegeBuff(privParameter.getPrevilegeWithGrantBuff()));
            privParameter.setPriviledgeBuff(resetPrivlegeBuff(privParameter.getPriviledgeBuff()));
            privParameter.getPriviledgeBuff().append("ALL");
            if (subName != null) {
                privParameter.getPrevilegeWithGrantBuff().append(String.format(Locale.ENGLISH, "(%s)", subName));
            }
        }
    }

    private PrivilegeParameters getPrivParameterForTableSeq(PrivilegeParameters privParameterParam) {
        PrivilegeParameters privParameter = privParameterParam;
        getPrivilegeString('r', "SELECT", privParameter);

        if ("SEQUENCE".equals(objType) || "SEQUENCES".equals(objType)) {
            /* sequence only */
            privParameter = getPrivilegeString('U', PRIV_USAGE, privParameter);
        } else {
            /* table only */
            privParameter = getPrivilegeString('a', PRIV_INSERT, privParameter);
            privParameter = getPrivilegeString('x', PRIV_REFERENCES, privParameter);
        }
        if (subName == null && "TABLE".equals(objType)) {
            privParameter = getPrivilegeString('d', PRIV_DELETE, privParameter);
            privParameter = getPrivilegeString('t', PRIV_TRIGGER, privParameter);
            privParameter = getPrivilegeString('D', PRIV_TRUNCATE, privParameter);
        }
        privParameter = getPrivilegeString('w', PRIV_UPDATE, privParameter);
        return privParameter;
    }

    private StringBuffer resetPrivlegeBuff(StringBuffer buff) {
        if (buff != null) {
            if (!buff.toString().trim().equals("")) {
                buff.delete(0, buff.length());
            } else {
                return new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            }
        }
        return buff;
    }

    /**
     * addGrantRevokeQueries add grant revoke queries
     * 
     * @param list acl list
     * @param builder query builder
     */
    public StringBuilder addGrantRevokeQueries(ArrayList<String> list, StringBuilder builder) {
        for (int i = 0; i < list.size(); i++) {
            // loop through aclArray to parse aclString to get grantee,privilege
            // and grantor .Example for aclString is dsuser=arwdDxt/dsuser
            String aclString = list.get(i);
            String grantee = "";

            String privCode = "";
            String privGrantorStr = "";

            if (StringUtils.isEmpty(aclString)) {
                continue;
            }
            // Here we are spliting with '=' to get grantee ..String before '='
            // is grantee
            String[] strArray = aclString.split("=");
            if (strArray.length == 1) {
                grantee = aclString;
            } else if (strArray.length > 1) {
                grantee = strArray[0];
                privGrantorStr = strArray[1];
            } else {
                continue;
            }
            if (grantee.startsWith("\"") && grantee.endsWith("\"")) {
                grantee = grantee.substring(1, grantee.length() - 1);
            }
            // AS per above example grantee=dsuser and
            // privGrantorStr=arwdDxt/dsuser
            // Here are spliting privGrantorStr to get privCode and
            // grantor..privCode=arwdDxt and grantor=dsuser
            String grantor = "";
            String[] strPrivArray = privGrantorStr.split("/");
            if (strPrivArray.length == 1) {
                privCode = aclString;
            }
            if (strPrivArray.length > 1) {
                privCode = strPrivArray[0];
                grantor = strPrivArray[1];
            }
            String priviledge = "";
            String previlegeWithGrant = "";
            PrivilegeParameters privParameter = new PrivilegeParameters();
            privParameter.setPrivString(privCode);
            privParameter = addPrevilegeMap(privParameter);
            priviledge = privParameter.getPriviledgeBuff().toString().trim();
            previlegeWithGrant = privParameter.getPrevilegeWithGrantBuff().toString().trim();
            if (priviledge.endsWith(",")) {
                priviledge = priviledge.substring(0, priviledge.length() - 1);
            }
            if (previlegeWithGrant.endsWith(",")) {
                previlegeWithGrant = previlegeWithGrant.substring(0, previlegeWithGrant.length() - 1);
            }

            getGrantRevokeQueries(builder, grantee, priviledge, previlegeWithGrant, grantor);
        }
        return builder;
    }

    private void getGrantRevokeQueries(StringBuilder builder, String grantee, String priviledge,
            String previlegeWithGrant, String grantorParam) {
        String grantor = grantorParam;
        if (grantor.isEmpty() && owner != null) {
            grantor = owner;
        }
        if (!priviledge.isEmpty() || !previlegeWithGrant.isEmpty()) {
            if (owner != null && grantor.equals(owner) && grantee.equals(owner)) {
                addQueriresOnSameOwnerAndGrantee(builder, grantee, priviledge, previlegeWithGrant);
            } else {
                addQuerirsOnOwnerAndGranteeDiff(builder, grantee, priviledge, previlegeWithGrant);
            }
        }
        if (!foundOwnerPrivs && owner != null) {
            builder.append(String.format(Locale.ENGLISH, "%sREVOKE ALL", prefix));
            if (subName != null) {
                builder.append(String.format(Locale.ENGLISH, "(%s)", subName));
            }
            builder.append(String.format(Locale.ENGLISH, " ON %s %s FROM %s;", objType, this.objectName, owner));
            builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        }
    }

    private void addQuerirsOnOwnerAndGranteeDiff(StringBuilder builder, String grantee, String priviledge,
            String previlegeWithGrant) {
        if (!priviledge.isEmpty()) {
            builder.append(String.format(Locale.ENGLISH, "%sGRANT %s ON %s %s TO ", prefix, priviledge, objType,
                    this.objectName));

            if (grantee.isEmpty()) {
                builder.append("PUBLIC;");
                builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
            } else {
                builder.append(String.format(Locale.ENGLISH, "%s;", grantee));
                builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
            }
        }
        if (!previlegeWithGrant.isEmpty()) {
            builder.append(String.format(Locale.ENGLISH, "%sGRANT %s ON %s %s TO ", prefix, previlegeWithGrant, objType,
                    this.objectName));
            if (grantee.isEmpty()) {
                builder.append("PUBLIC");

            } else {
                builder.append(String.format(Locale.ENGLISH, "%s", grantee));
            }
            builder.append(" WITH GRANT OPTION;");
            builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        }
    }

    private void addQueriresOnSameOwnerAndGrantee(StringBuilder builder, String grantee, String priviledge,
            String previlegeWithGrant) {
        foundOwnerPrivs = true;
        if (!previlegeWithGrant.equals("ALL")) {
            builder.append(String.format(Locale.ENGLISH, "%s REVOKE ALL", prefix));
            if (subName != null) {
                builder.append(String.format(Locale.ENGLISH, "(%s)", subName));
            }
            builder.append(String.format(Locale.ENGLISH, " ON %s %s FROM %s;", objType, this.objectName, grantee));
            builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
            if (!priviledge.isEmpty()) {
                builder.append(String.format(Locale.ENGLISH, "%sGRANT %s ON %s %s TO %s;", prefix, priviledge, objType,
                        this.objectName, grantee));
                builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
            }
            if (!previlegeWithGrant.isEmpty()) {
                builder.append(String.format(Locale.ENGLISH, "%sGRANT %s ON %s %s TO %s WITH GRANT OPTION;", prefix,
                        previlegeWithGrant, objType, this.objectName, grantee));
                builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
            }
        }
    }

    private PrivilegeParameters getPrivilegeString(char chr, String code, PrivilegeParameters privParameter) {
        String privCode = privParameter.getPrivString();
        // Here we are checking if character passed (char) is available in
        // privCode
        // ..if available then we are adding respective Privilege string (code)
        int index = privCode.indexOf(chr);
        if (index != -1) {
            // Here we are checking if if charcter in privCode is followed by *
            // then it should add to GRANT WITH OPTION. So adding it in another
            // StringBuffer
            if (index < (privCode.length() - 1) && privCode.charAt(index + 1) == '*') {
                privParameter.getPrevilegeWithGrantBuff().append(code);
                privParameter.getPrevilegeWithGrantBuff().append(",");
                if (subName != null) {
                    privParameter.getPrevilegeWithGrantBuff().append(String.format(Locale.ENGLISH, "(%s)", subName));
                }
                privParameter.setAllWithoutGo(false);
            } else {
                privParameter.getPriviledgeBuff().append(code);
                privParameter.getPriviledgeBuff().append(",");
                if (subName != null) {
                    privParameter.getPriviledgeBuff().append(String.format(Locale.ENGLISH, "(%s)", subName));
                }
                privParameter.setAllWithGo(false);
            }
        } else {
            privParameter.setAllWithoutGo(false);
            privParameter.setAllWithGo(false);
        }
        return privParameter;
    }

    /**
     * Here we are parsing acls string and changing to list of acls
     * 
     * @param acl example
     * {dsuser=arwdDxt/dsuser,avinash=a*rwDx/dsuser,aaa=arwdDxt/dsuser}
     * @return array of acls
     */
    private ArrayList<String> parseAcl(String aclParam) {
        String acl = aclParam;
        ArrayList<String> aclList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        if (acl == null) {
            return aclList;
        }

        acl = acl.trim();
        int aclLength = acl.length();
        if (aclLength < 2 || !acl.startsWith("{") || !acl.endsWith("}")) {
            return aclList;
        }
        acl = acl.substring(1, aclLength - 1);
        String[] aclArray = acl.split(",");
        for (int i = 0; i < aclArray.length; i++) {
            aclList.add(aclArray[i]);
        }
        return aclList;
    }

    /**
     * getOwnerName get owner name
     * 
     * @param oid owner id
     * @param conn connection
     * @throws DatabaseOperationException
     */
    public void getOwnerName(long oid, DBConnection conn) throws DatabaseOperationException {
        String query = String.format(Locale.ENGLISH, "SELECT rolname FROM pg_catalog.pg_roles WHERE oid =%d", oid);
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.getConnection().createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                this.owner = rs.getString("rolname");
            }
        } catch (SQLException exc) {
            MPPDBIDELoggerUtility.error("Error occured while fetching owner name for object name");
            throw new DatabaseOperationException(IMessagesConstants.ERROR_WHILE_FETCHING_OWNER_NAME, exc);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException exc) {
                MPPDBIDELoggerUtility.error("Error occured while fetching owner name for object name");
            }
        }
    }
}