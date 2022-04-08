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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSUserRolePropertiesGridDataProvider.
 *
 * @since 3.0.0
 */
public class DSUserRolePropertiesGridDataProvider {

    /**
     * Generate user role property general tab.
     *
     * @param updatedRows the updated rows
     * @param userRole the user role
     * @throws ParseException the parse exception
     */
    public static void generateUserRolePropertyGeneralTab(List<IDSGridEditDataRow> updatedRows, UserRole userRole)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(MPPDBIDEConstants.USER_ROLE_DATE_DISPLAY_FORMAT);
        for (IDSGridEditDataRow row : updatedRows) {
            String displayName = (String) row.getValue(0);
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_CONNECT_LIMIT).equals(displayName)) {
                userRole.setRolConnLimit(Integer.valueOf((String) row.getValue(1)));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_VALID_BEGIN).equals(displayName)) {
                userRole.setRolValidBegin(sdf.parse(String.valueOf(row.getValue(1))));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_VALID_UNTIL).equals(displayName)) {
                userRole.setRolValidUntil(sdf.parse(String.valueOf(row.getValue(1))));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_RESOURCE_POOL).equals(displayName)) {
                userRole.setRolResPool(String.valueOf(row.getValue(1)));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_COMMENT).equals(displayName)) {
                userRole.setComment(
                        "null".equals(String.valueOf(row.getValue(1))) ? "" : String.valueOf(row.getValue(1)));
                continue;
            }
            // for rename
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_NAME).equals(displayName)) {
                userRole.setName("null".equals(String.valueOf(row.getValue(1))) ? "" : String.valueOf(row.getValue(1)));
                continue;
            }
        }
    }

    /**
     * Generate user role property tab privilege.
     *
     * @param updatedRows the updated rows
     * @param userRole the user role
     */
    public static void generateUserRolePropertyTabPrivilege(List<IDSGridEditDataRow> updatedRows, UserRole userRole) {
        for (IDSGridEditDataRow row : updatedRows) {
            String displayName = (String) row.getValue(0);
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_CAN_LOGIN).equals(displayName)) {
                userRole.setRolCanLogin((Boolean) (row.getValue(1)));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_CREATE_ROLE).equals(displayName)) {
                userRole.setRolCreateRole((Boolean) (row.getValue(1)));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_CREATE_DB).equals(displayName)) {
                userRole.setRolCreateDb((Boolean) (row.getValue(1)));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_SYSTEM_ADMIN).equals(displayName)) {
                userRole.setRolSystemAdmin((Boolean) (row.getValue(1)));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_AUDITOR).equals(displayName)) {
                userRole.setRolAuditAdmin((Boolean) (row.getValue(1)));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLR_INHERIT).equals(displayName)) {
                userRole.setRolInherit((Boolean) (row.getValue(1)));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_REPLICATION).equals(displayName)) {
                userRole.setRolReplication((Boolean) (row.getValue(1)));
                continue;
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_IS_LOCK).equals(displayName)) {
                userRole.setIsLock((Boolean) (row.getValue(1)));
                continue;
            }
        }
    }

    /**
     * Generate user role property tab membership.
     *
     * @param updatedRows the updated rows
     * @param userRole the user role
     * @param userRoleName the user role name
     */
    public static void generateUserRolePropertyTabMembership(List<IDSGridEditDataRow> updatedRows, UserRole userRole,
            String userRoleName) {
        for (IDSGridEditDataRow row : updatedRows) {
            String displayName = (String) row.getValue(0);
            if (userRoleName != null && userRoleName.equals(displayName)) {
                Object value = row.getValue(1);
                if (value instanceof String && StringUtils.isNotEmpty(value.toString())) {
                    String parentNamesStr = value.toString().substring(1, value.toString().length() - 1);
                    if (StringUtils.isNotEmpty(parentNamesStr)) {
                        String[] parentNames = parentNamesStr.split(PropertiesConstants.SPLIT_STR);
                        List<UserRole> parents = Arrays.asList(parentNames).stream().map(parentName -> {
                            UserRole parent = new UserRole();
                            parent.setName(parentName);
                            return parent;
                        }).collect(Collectors.toList());
                        userRole.setParents(parents);
                    } else {
                        userRole.setParents(new ArrayList<UserRole>());
                    }
                }
                if (value instanceof String && StringUtils.isEmpty(value.toString())) {
                    userRole.setParents(new ArrayList<UserRole>());
                }
            }
            continue;
        }
    }

}
