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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.util.Date;
import java.util.List;

import org.opengauss.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserRole.
 * 
 */

public class UserRole extends BatchDropServerObject implements GaussOLAPDBMSObject {

    private Boolean rolInherit;
    private Boolean rolCreateRole;
    private Boolean rolCreateDb;
    private Boolean rolCanLogin;
    private Boolean rolReplication;
    private Boolean rolAuditAdmin;
    private Boolean rolSystemAdmin;
    private Integer rolConnLimit;
    private Date rolValidBegin;
    private Date rolValidUntil;

    private String rolResPool;
    private Boolean isLock;
    private Boolean isUser;
    private String comment;
    private List<UserRole> parents;
    private List<UserRole> members;
    private Server server;

    private int spinnerPreSize;
    private String cmbResoucePoolName;
    private String userGroupName;
    private String roleCombo;
    private String adminComo;
    private Boolean auditAdmin;
    private String beginTime;
    private String untilTime;
    private char[] passwordInput;
    private Boolean all;
    private Boolean clear;

    private Boolean user;
    private Boolean role;

    /**
     * Gets the user.
     *
     * @return the user
     */
    public Boolean getUser() {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user the new user
     */
    public void setUser(Boolean user) {
        this.user = user;
    }

    /**
     * Gets the role.
     *
     * @return the role
     */
    public Boolean getRole() {
        return role;
    }

    /**
     * Sets the role.
     *
     * @param role the new role
     */
    public void setRole(Boolean role) {
        this.role = role;
    }

    /**
     * Gets the all.
     *
     * @return the all
     */
    public Boolean getAll() {
        return all;
    }

    /**
     * Sets the all.
     *
     * @param all the new all
     */
    public void setAll(Boolean all) {
        this.all = all;
    }

    /**
     * Gets the clear.
     *
     * @return the clear
     */
    public Boolean getClear() {
        return clear;
    }

    /**
     * Sets the clear.
     *
     * @param clear the new clear
     */
    public void setClear(Boolean clear) {
        this.clear = clear;
    }

    private static final String DROP_QUERY = "DROP ROLE IF EXISTS ";

    /**
     * Gets the spinner pre size.
     *
     * @return the spinner pre size
     */
    public int getSpinnerPreSize() {
        return spinnerPreSize;
    }

    /**
     * Sets the spinner pre size.
     *
     * @param spinnerPreSize the new spinner pre size
     */
    public void setSpinnerPreSize(int spinnerPreSize) {
        this.spinnerPreSize = spinnerPreSize;
    }

    /**
     * Gets the cmb resouce pool name.
     *
     * @return the cmb resouce pool name
     */
    public String getCmbResoucePoolName() {
        return cmbResoucePoolName;
    }

    /**
     * Sets the cmb resouce pool name.
     *
     * @param cmbResoucePoolName the new cmb resouce pool name
     */
    public void setCmbResoucePoolName(String cmbResoucePoolName) {
        this.cmbResoucePoolName = cmbResoucePoolName;
    }

    /**
     * Gets the user group name.
     *
     * @return the user group name
     */
    public String getUserGroupName() {
        return userGroupName;
    }

    /**
     * Sets the user group name.
     *
     * @param userGroupName the new user group name
     */
    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    /**
     * Gets the role combo.
     *
     * @return the role combo
     */
    public String getRoleCombo() {
        return roleCombo;
    }

    /**
     * Sets the role combo.
     *
     * @param roleCombo the new role combo
     */
    public void setRoleCombo(String roleCombo) {
        this.roleCombo = roleCombo;
    }

    /**
     * Gets the admin como.
     *
     * @return the admin como
     */
    public String getAdminComo() {
        return adminComo;
    }

    /**
     * Sets the admin como.
     *
     * @param adminComo the new admin como
     */
    public void setAdminComo(String adminComo) {
        this.adminComo = adminComo;
    }

    /**
     * Gets the audit admin.
     *
     * @return the audit admin
     */
    public Boolean getAuditAdmin() {
        return auditAdmin;
    }

    /**
     * Sets the audit admin.
     *
     * @param auditAdmin the new audit admin
     */
    public void setAuditAdmin(Boolean auditAdmin) {
        this.auditAdmin = auditAdmin;
    }

    /**
     * Gets the begin time.
     *
     * @return the begin time
     */
    public String getBeginTime() {
        return beginTime;
    }

    /**
     * Sets the begin time.
     *
     * @param beginTime the new begin time
     */
    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * Gets the until time.
     *
     * @return the until time
     */
    public String getUntilTime() {
        return untilTime;
    }

    /**
     * Sets the until time.
     *
     * @param untilTime the new until time
     */
    public void setUntilTime(String untilTime) {
        this.untilTime = untilTime;
    }

    /**
     * Gets the password input.
     *
     * @return the password input
     */
    public char[] getPasswordInput() {
        return (char[]) this.passwordInput.clone();
    }

    /**
     * Sets the password input.
     *
     * @param passwordInput the new password input
     */
    public void setPasswordInput(char[] passwordInput) {
        this.passwordInput = passwordInput.clone();
    }

    /**
     * Clear pwd.
     */
    public void clearPwd() {
        for (int index = 0; index < passwordInput.length; index++) {
            passwordInput[index] = 0;
        }
    }

    /**
     * Instantiates a new user role.
     */
    public UserRole() {
        super(OBJECTTYPE.USER_ROLE);
    }

    /**
     * Instantiates a new user role.
     *
     * @param type the type
     * @param oid the oid
     * @param rolName the rol name
     * @param rolInherit the rol inherit
     * @param rolCreateRole the rol create role
     * @param rolCreateDb the rol create db
     * @param rolCanLogin the rol can login
     * @param rolReplication the rol replication
     * @param rolAuditAdmin the rol audit admin
     * @param rolSystemAdmin the rol system admin
     * @param rolConnLimit the rol conn limit
     * @param rolValidBegin the rol valid begin
     * @param rolValidUntil the rol valid until
     * @param rolResPool the rol res pool
     * @param server the server
     */
    public UserRole(OBJECTTYPE type, Long oid, String rolName, Boolean rolInherit, Boolean rolCreateRole,
            Boolean rolCreateDb, Boolean rolCanLogin, Boolean rolReplication, Boolean rolAuditAdmin,
            Boolean rolSystemAdmin, Integer rolConnLimit, Date rolValidBegin, Date rolValidUntil, String rolResPool,
            Server server) {
        super(oid, rolName, type, server.getPrivilegeFlag());
        this.rolInherit = rolInherit;
        this.rolCreateRole = rolCreateRole;
        this.rolCreateDb = rolCreateDb;
        this.rolCanLogin = rolCanLogin;
        this.rolReplication = rolReplication;
        this.rolAuditAdmin = rolAuditAdmin;
        this.rolSystemAdmin = rolSystemAdmin;
        this.rolConnLimit = rolConnLimit;
        this.rolValidBegin = rolValidBegin != null ? (Date) rolValidBegin.clone() : null;
        this.rolValidUntil = rolValidUntil != null ? (Date) rolValidUntil.clone() : null;
        this.rolResPool = rolResPool;
        this.server = server;
    }

    /**
     * Instantiates a new user role.
     *
     * @param server the server
     */
    public UserRole(Server server) {
        this(OBJECTTYPE.USER_ROLE, Long.valueOf(0), null, false, false, false, false, false, false, false, 0, null,
                null, null, server);

    }

    @Override
    public UserRoleObjectGroup getParent() {
        return this.server.getUserRoleObjectGroup();
    }

    /**
     * Gets the rol inherit.
     *
     * @return the rol inherit
     */
    public Boolean getRolInherit() {
        return rolInherit;
    }

    /**
     * Sets the rol inherit.
     *
     * @param rolInherit the new rol inherit
     */
    public void setRolInherit(Boolean rolInherit) {
        this.rolInherit = rolInherit;
    }

    /**
     * Gets the rol create role.
     *
     * @return the rol create role
     */
    public Boolean getRolCreateRole() {
        return rolCreateRole;
    }

    /**
     * Sets the rol create role.
     *
     * @param rolCreateRole the new rol create role
     */
    public void setRolCreateRole(Boolean rolCreateRole) {
        this.rolCreateRole = rolCreateRole;
    }

    /**
     * Gets the rol create db.
     *
     * @return the rol create db
     */
    public Boolean getRolCreateDb() {
        return rolCreateDb;
    }

    /**
     * Sets the rol create db.
     *
     * @param rolCreateDb the new rol create db
     */
    public void setRolCreateDb(Boolean rolCreateDb) {
        this.rolCreateDb = rolCreateDb;
    }

    /**
     * Gets the rol can login.
     *
     * @return the rol can login
     */
    public Boolean getRolCanLogin() {
        return rolCanLogin;
    }

    /**
     * Sets the rol can login.
     *
     * @param rolCanLogin the new rol can login
     */
    public void setRolCanLogin(Boolean rolCanLogin) {
        this.rolCanLogin = rolCanLogin;
    }

    /**
     * Gets the rol replication.
     *
     * @return the rol replication
     */
    public Boolean getRolReplication() {
        return rolReplication;
    }

    /**
     * Sets the rol replication.
     *
     * @param rolReplication the new rol replication
     */
    public void setRolReplication(Boolean rolReplication) {
        this.rolReplication = rolReplication;
    }

    /**
     * Gets the rol audit admin.
     *
     * @return the rol audit admin
     */
    public Boolean getRolAuditAdmin() {
        return rolAuditAdmin;
    }

    /**
     * Sets the rol audit admin.
     *
     * @param rolAuditAdmin the new rol audit admin
     */
    public void setRolAuditAdmin(Boolean rolAuditAdmin) {
        this.rolAuditAdmin = rolAuditAdmin;
    }

    /**
     * Gets the rol system admin.
     *
     * @return the rol system admin
     */
    public Boolean getRolSystemAdmin() {
        return rolSystemAdmin;
    }

    /**
     * Sets the rol system admin.
     *
     * @param rolSystemAdmin the new rol system admin
     */
    public void setRolSystemAdmin(Boolean rolSystemAdmin) {
        this.rolSystemAdmin = rolSystemAdmin;
    }

    /**
     * Gets the rol conn limit.
     *
     * @return the rol conn limit
     */
    public Integer getRolConnLimit() {
        return rolConnLimit;
    }

    /**
     * Sets the rol conn limit.
     *
     * @param rolConnLimit the new rol conn limit
     */
    public void setRolConnLimit(Integer rolConnLimit) {
        this.rolConnLimit = rolConnLimit;
    }

    /**
     * Gets the rol valid begin.
     *
     * @return the rol valid begin
     */
    public Date getRolValidBegin() {
        return rolValidBegin != null ? (Date) rolValidBegin.clone() : null;
    }

    /**
     * Sets the rol valid begin.
     *
     * @param rolValidBegin the new rol valid begin
     */
    public void setRolValidBegin(Date rolValidBegin) {
        this.rolValidBegin = rolValidBegin != null ? (Date) rolValidBegin.clone() : null;
    }

    /**
     * Gets the rol valid until.
     *
     * @return the rol valid until
     */
    public Date getRolValidUntil() {
        return rolValidUntil != null ? (Date) rolValidUntil.clone() : null;
    }

    /**
     * Sets the rol valid until.
     *
     * @param rolValidUntil the new rol valid until
     */
    public void setRolValidUntil(Date rolValidUntil) {
        this.rolValidUntil = rolValidUntil != null ? (Date) rolValidUntil.clone() : null;
    }

    /**
     * Gets the rol res pool.
     *
     * @return the rol res pool
     */
    public String getRolResPool() {
        return rolResPool;
    }

    /**
     * Sets the rol res pool.
     *
     * @param rolResPool the new rol res pool
     */
    public void setRolResPool(String rolResPool) {
        this.rolResPool = rolResPool;
    }

    /**
     * Gets the checks if is lock.
     *
     * @return the checks if is lock
     */
    public Boolean getIsLock() {
        return isLock;
    }

    /**
     * Sets the checks if is lock.
     *
     * @param isLock the new checks if is lock
     */
    public void setIsLock(Boolean isLock) {
        this.isLock = isLock;
    }

    /**
     * Gets the comment.
     *
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment.
     *
     * @param comment the new comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the parents.
     *
     * @return the parents
     */
    public List<UserRole> getParents() {
        return parents;
    }

    /**
     * Sets the parents.
     *
     * @param parents the new parents
     */
    public void setParents(List<UserRole> parents) {
        this.parents = parents;
    }

    /**
     * Gets the members.
     *
     * @return the members
     */
    public List<UserRole> getMembers() {
        return members;
    }

    /**
     * Sets the members.
     *
     * @param members the new members
     */
    public void setMembers(List<UserRole> members) {
        this.members = members;
    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    public Server getServer() {
        return server;
    }

    /**
     * Sets the server.
     *
     * @param server the new server
     */
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public String getDropQuery(boolean isCascade) {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        query.append("DROP ");
        query.append(this.getRolCanLogin() ? "USER " : "ROLE ");
        query.append("IF EXISTS ");
        query.append(this.getDisplayName());
        if (isCascade) {
            query.append(this.getRolCanLogin() ? MPPDBIDEConstants.CASCADE : "");
        }
        return query.toString();
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {

        try {
            return getServer().findOneActiveDb();
        } catch (DatabaseOperationException exception) {

            MPPDBIDELoggerUtility.error("No Active DataBase.", exception);
        }
        return null;

    }
    
    /**
     * get is user 
     * 
     * @return is user 
     */
    public Boolean getIsUser() {
        return isUser;
    }
    
    /**
     * set is user 
     * 
     * @param isUser the is user
     */
    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }

}
