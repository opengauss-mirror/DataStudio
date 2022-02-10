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

package org.opengauss.mppdbide.presentation.objectproperties;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import org.opengauss.mppdbide.bl.serverdatacache.UserRoleFieldEnum;
import org.opengauss.mppdbide.bl.serverdatacache.UserRoleManager;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesUserRoleImpl.
 *
 * @since 3.0.0
 */
public class PropertiesUserRoleImpl implements IServerObjectProperties {

    private UserRole userRole;
    private OlapConvertToObjectPropertyData convertToObjectPropertyData;

    /**
     * Instantiates a new properties user role impl.
     *
     * @param obj the obj
     */
    public PropertiesUserRoleImpl(Object obj) {
        this.userRole = (UserRole) obj;
        convertToObjectPropertyData = new OlapConvertToObjectPropertyData();
    }

    @Override
    public String getObjectName() {
        return this.userRole.getName();
    }

    @Override
    public String getHeader() {
        return this.userRole.getName() + '@' + this.userRole.getServer().getName();
    }

    @Override
    public String getUniqueID() {
        return this.userRole.getOid() + '@' + this.userRole.getServer().getName() + "properties";
    }

    @Override
    public Database getDatabase() {
        try {
            return this.userRole.getServer().findOneActiveDb();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Failed to get database: No active database present", exception);
            return null;
        }
    }

    /**
     * Gets the user role.
     *
     * @return the user role
     */
    public UserRole getUserRole() {
        return this.userRole;
    }

    @Override
    public List<IObjectPropertyData> getAllProperties(DBConnection conn) throws MPPDBIDEException {
        List<String> tabNameList = new ArrayList<String>(4);
        List<List<String[]>> propertyList = new ArrayList<List<String[]>>(4);
        Server server = this.userRole.getServer();
        boolean isSysAdmin = UserRoleManager.isSysAdmin(conn);

        UserRoleManager.copyProperties(UserRoleManager.fetchUserRoleDetailInfoByOid(server, conn, this.userRole),
                this.userRole);
        this.userRole.setParents(UserRoleManager.fetchAllParent(server, conn, this.userRole));
        this.userRole.setComment(UserRoleManager.fetchDescriptionOfUserRole(conn, userRole));
        if (isSysAdmin) {
            this.userRole.setIsLock(UserRoleManager.fetchLockStatusOfUserRole(conn, userRole));
        }

        tabNameList.add(PropertiesConstants.USER_ROLE_PROPERTY_TAB_GENERAL);
        tabNameList.add(PropertiesConstants.USER_ROLE_PROPERTY_TAB_PRIVILEGE);
        tabNameList.add(PropertiesConstants.USER_ROLE_PROPERTY_TAB_MEMBERSHIP);

        propertyList.add(getPropertiesOfGeneralTab());
        propertyList.add(getPropertiesOfPrivilegeTab(isSysAdmin));
        propertyList.add(getPropertiesOfMemberShipTab());

        return convertToObjectPropertyData.getObjectPropertyData(tabNameList, propertyList, null, this);
    }

    /**
     * Gets the properties of general tab.
     *
     * @return the properties of general tab
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public List<String[]> getPropertiesOfGeneralTab() throws MPPDBIDEException {
        List<String[]> props = new ArrayList<String[]>();

        props.add(new String[] {MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_PROPERTY),
            MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_VALUE)});

        props.add(
                new ServerProperty(getPropertyName(UserRoleFieldEnum.OID), Long.toString(userRole.getOid())).getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.NAME), userRole.getName()).getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_CONN_LIMIT), userRole.getRolConnLimit())
                .getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_VALID_BEGIN),
                userRole.getRolValidBegin() == null ? "" : userRole.getRolValidBegin().toString()).getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_VALID_UNTIL),
                userRole.getRolValidUntil() == null ? "" : userRole.getRolValidUntil().toString()).getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_RES_POOL), userRole.getRolResPool())
                .getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.COMMENT),
                userRole.getComment() == null ? "" : userRole.getComment()).getProp());

        return props;
    }

    /**
     * Gets the properties of privilege tab.
     *
     * @param isSysAdmin the is sys admin
     * @return the properties of privilege tab
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public List<String[]> getPropertiesOfPrivilegeTab(boolean isSysAdmin) throws MPPDBIDEException {
        List<String[]> props = new ArrayList<String[]>();

        props.add(new String[] {MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_PROPERTY),
            MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_IS_ENABLE)});

        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_CAN_LOGIN),
                userRole.getRolCanLogin().toString()).getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_CREATE_ROLE),
                userRole.getRolCreateRole().toString()).getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_CREATE_DB),
                userRole.getRolCreateDb().toString()).getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_SYSTEM_ADMIN),
                userRole.getRolSystemAdmin().toString()).getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_AUDIT_ADMIN),
                userRole.getRolAuditAdmin().toString()).getProp());
        props.add(
                new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_INHERIT), userRole.getRolInherit().toString())
                        .getProp());
        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.ROL_REPLICATION),
                userRole.getRolReplication().toString()).getProp());

        if (isSysAdmin) {
            props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.IS_LOCK), userRole.getIsLock().toString())
                    .getProp());
        }

        return props;
    }

    /**
     * Gets the properties of member ship tab.
     *
     * @return the properties of member ship tab
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public List<String[]> getPropertiesOfMemberShipTab() throws MPPDBIDEException {
        List<String[]> props = new ArrayList<String[]>();

        props.add(
                new String[] {MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_USER_ROLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_TAB_COLUMN_TITLE_BELONG_TO)});

        StringBuilder parents = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        for (UserRole obj : userRole.getParents()) {
            parents.append(obj.getName() + PropertiesConstants.SPLIT_STR);
        }
        if (parents.length() > 0) {
            parents.delete(parents.length() - PropertiesConstants.SPLIT_STR.length(), parents.length());
        }
        parents.insert(0, '[');
        parents.insert(parents.length(), ']');

        props.add(new ServerProperty(getPropertyName(UserRoleFieldEnum.PARENTS), parents.toString()).getProp());

        return props;
    }

    /**
     * Gets the property name.
     *
     * @param field the field
     * @return the property name
     */
    private String getPropertyName(UserRoleFieldEnum field) {
        String propertyName = null;
        propertyName = getPropertyNameFirst(field);
        switch (field) {
            case ROL_REPLICATION: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_REPLICATION);
                break;
            }
            case ROL_CAN_LOGIN: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_CAN_LOGIN);
                break;
            }
            case ROL_SYSTEM_ADMIN: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_SYSTEM_ADMIN);
                break;
            }
            case ROL_RES_POOL: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_RESOURCE_POOL);
                break;
            }
            case COMMENT: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_COMMENT);
                break;
            }
            case ROL_AUDIT_ADMIN: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_AUDITOR);
                break;
            }
            case IS_LOCK: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_IS_LOCK);
                break;
            }
            case PARENTS: {
                propertyName = this.userRole.getName();
                break;
            }
            default: {
                break;
            }
        }
        return propertyName;
    }

    /**
     * Gets the property name first.
     *
     * @param field the field
     * @return the property name first
     */
    private String getPropertyNameFirst(UserRoleFieldEnum field) {
        String propertyName = null;
        switch (field) {
            case OID: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.OID_MSG);
                break;
            }
            case NAME: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_NAME);
                break;
            }
            case ROL_CONN_LIMIT: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_CONNECT_LIMIT);
                break;
            }
            case ROL_VALID_BEGIN: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_VALID_BEGIN);
                break;
            }
            case ROL_VALID_UNTIL: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_VALID_UNTIL);
                break;
            }
            case ROL_CREATE_ROLE: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_CREATE_ROLE);
                break;
            }
            case ROL_CREATE_DB: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_CREATE_DB);
                break;
            }
            case ROL_INHERIT: {
                propertyName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLR_INHERIT);
                break;
            }
            default: {
                break;
            }
        }
        return propertyName;
    }
}
