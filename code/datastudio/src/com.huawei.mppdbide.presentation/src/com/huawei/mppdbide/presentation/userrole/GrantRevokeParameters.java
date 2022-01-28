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

import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class GrantRevokeParameters.
 * 
 * @since 3.0.0
 */
public class GrantRevokeParameters {
    private List<Object> selectedObjects;
    private String userRolesStr;
    private List<String> withGrantOptionPrivileges;
    private List<String> withoutGrantOptionPrivileges;
    private boolean allPrivilege;
    private boolean allWithGrantOption;
    private List<String> revokePrivileges;
    private List<String> revokeGrantPrivileges;
    private boolean revokeAllPrivilege;
    private boolean revokeAllGrantPrivilege;
    private boolean isGrant;

    /**
     * Gets the selected objects.
     *
     * @return the selected objects
     */
    public List<Object> getSelectedObjects() {
        return selectedObjects;
    }

    /**
     * Sets the selected objects.
     *
     * @param selectedObjects the new selected objects
     */
    public void setSelectedObjects(List<Object> selectedObjects) {
        this.selectedObjects = selectedObjects;
    }

    /**
     * Gets the user roles str.
     *
     * @return the user roles str
     */
    public String getUserRolesStr() {
        return userRolesStr;
    }

    /**
     * Sets the user roles str.
     *
     * @param userRolesStr the new user roles str
     */
    public void setUserRolesStr(String userRolesStr) {
        this.userRolesStr = userRolesStr;
    }

    /**
     * Gets the with grant option privileges.
     *
     * @return the with grant option privileges
     */
    public List<String> getWithGrantOptionPrivileges() {
        return withGrantOptionPrivileges;
    }

    /**
     * Sets the with grant option privileges.
     *
     * @param withGrantOptionPrivileges the new with grant option privileges
     */
    public void setWithGrantOptionPrivileges(List<String> withGrantOptionPrivileges) {
        this.withGrantOptionPrivileges = withGrantOptionPrivileges;
    }

    /**
     * Gets the without grant option privileges.
     *
     * @return the without grant option privileges
     */
    public List<String> getWithoutGrantOptionPrivileges() {
        return withoutGrantOptionPrivileges;
    }

    /**
     * Sets the without grant option privileges.
     *
     * @param withoutGrantOptionPrivileges the new without grant option
     * privileges
     */
    public void setWithoutGrantOptionPrivileges(List<String> withoutGrantOptionPrivileges) {
        this.withoutGrantOptionPrivileges = withoutGrantOptionPrivileges;
    }

    /**
     * Checks if is all privilege.
     *
     * @return true, if is all privilege
     */
    public boolean isAllPrivilege() {
        return allPrivilege;
    }

    /**
     * Sets the all privilege.
     *
     * @param allPrivilege the new all privilege
     */
    public void setAllPrivilege(boolean allPrivilege) {
        this.allPrivilege = allPrivilege;
    }

    /**
     * Checks if is all with grant option.
     *
     * @return true, if is all with grant option
     */
    public boolean isAllWithGrantOption() {
        return allWithGrantOption;
    }

    /**
     * Sets the all with grant option.
     *
     * @param allWithGrantOption the new all with grant option
     */
    public void setAllWithGrantOption(boolean allWithGrantOption) {
        this.allWithGrantOption = allWithGrantOption;
    }

    /**
     * Gets the revoke privileges.
     *
     * @return the revoke privileges
     */
    public List<String> getRevokePrivileges() {
        return revokePrivileges;
    }

    /**
     * Sets the revoke privileges.
     *
     * @param revokePrivileges the new revoke privileges
     */
    public void setRevokePrivileges(List<String> revokePrivileges) {
        this.revokePrivileges = revokePrivileges;
    }

    /**
     * Gets the revoke grant privileges.
     *
     * @return the revoke grant privileges
     */
    public List<String> getRevokeGrantPrivileges() {
        return revokeGrantPrivileges;
    }

    /**
     * Sets the revoke grant privileges.
     *
     * @param revokeGrantPrivileges the new revoke grant privileges
     */
    public void setRevokeGrantPrivileges(List<String> revokeGrantPrivileges) {
        this.revokeGrantPrivileges = revokeGrantPrivileges;
    }

    /**
     * Checks if is revoke all privilege.
     *
     * @return true, if is revoke all privilege
     */
    public boolean isRevokeAllPrivilege() {
        return revokeAllPrivilege;
    }

    /**
     * Sets the revoke all privilege.
     *
     * @param revokeAllPrivilege the new revoke all privilege
     */
    public void setRevokeAllPrivilege(boolean revokeAllPrivilege) {
        this.revokeAllPrivilege = revokeAllPrivilege;
    }

    /**
     * Checks if is revoke all grant privilege.
     *
     * @return true, if is revoke all grant privilege
     */
    public boolean isRevokeAllGrantPrivilege() {
        return revokeAllGrantPrivilege;
    }

    /**
     * Sets the revoke all grant privilege.
     *
     * @param revokeAllGrantPrivilege the new revoke all grant privilege
     */
    public void setRevokeAllGrantPrivilege(boolean revokeAllGrantPrivilege) {
        this.revokeAllGrantPrivilege = revokeAllGrantPrivilege;
    }

    /**
     * Checks if is grant.
     *
     * @return true, if is grant
     */
    public boolean isGrant() {
        return isGrant;
    }

    /**
     * Sets the grant.
     *
     * @param isGrnt the new grant
     */
    public void setGrant(boolean isGrnt) {
        this.isGrant = isGrnt;
    }
}
