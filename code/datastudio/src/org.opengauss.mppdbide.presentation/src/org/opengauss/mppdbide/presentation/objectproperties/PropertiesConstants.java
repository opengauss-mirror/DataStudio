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

/**
 * 
 * Title: interface
 * 
 * Description: The Interface PropertiesConstants.
 * 
 * @since 3.0.0
 */
public interface PropertiesConstants {

    /**
     * The Constant INDEX.
     */
    public static final String INDEX = "Index";

    /**
     * The Constant CONSTRAINTS.
     */
    public static final String CONSTRAINTS = "Constraints";

    /**
     * The Constant COLUMNS.
     */
    public static final String COLUMNS = "Columns";

    /**
     * The Constant GENERAL.
     */
    public static final String GENERAL = "General";

    /**
     * The Constant PARTITION.
     */
    public static final String PARTITION = "Partition";

    /**
     * The Constant KEYS.
     */
    public static final String KEYS = "Keys";

    /**
     * The Constant CHECKS.
     */
    public static final String CHECKS = "Checks";

    /**
     * The Constant DISTRIBUTION.
     */
    public static final String DISTRIBUTION = "Distribution";

    /**
     * The Constant USER_ROLE_PROPERTY_TAB_GENERAL.
     */
    public static final String USER_ROLE_PROPERTY_TAB_GENERAL = "userRolePropertyTabGeneral";

    /**
     * The Constant USER_ROLE_PROPERTY_TAB_PRIVILEGE.
     */
    public static final String USER_ROLE_PROPERTY_TAB_PRIVILEGE = "userRolePropertyTabPrivilege";

    /**
     * The Constant USER_ROLE_PROPERTY_TAB_MEMBERSHIP.
     */
    public static final String USER_ROLE_PROPERTY_TAB_MEMBERSHIP = "userRolePropertyTabMembership";

    /**
     * The Constant USER_ROLE_COMMENT_MAXIMUM_CHARACTERS.
     */
    public static final int USER_ROLE_COMMENT_MAXIMUM_CHARACTERS = 4000;

    /**
     * The Constant COMBO_BOX_MAX_VISIBLE_ITEM.
     */
    public static final int COMBO_BOX_MAX_VISIBLE_ITEM = 18;

    /**
     * The Constant SPLIT_STR.
     */
    public static final String SPLIT_STR = ", ";

    /**
     * The Constant RESOURCE_POOL_COMBO_BOX_DATA_PROVIDER.
     */
    public static final String RESOURCE_POOL_COMBO_BOX_DATA_PROVIDER = "RESOURCE_POOL_COMBO_BOX_DATA_PROVIDER";

    /**
     * The Constant MEMBER_SHIP_COMBO_BOX_DATA_PROVIDER.
     */
    public static final String MEMBER_SHIP_COMBO_BOX_DATA_PROVIDER = "MEMBER_SHIP_COMBO_BOX_DATA_PROVIDER";

    /**
     * The Constant EDIT_PROPERTIES_COMBO_BOX_TABLESPACE.
     */
    public static final String EDIT_PROPERTIES_COMBO_BOX_TABLESPACE = "EDIT_PROPERTIES_COMBO_BOX_TABLESPACE";

    /**
     * The Constant EDIT_PROPERTIES_COMBO_BOX_CONSTRAINT_TYPE.
     */
    public static final String EDIT_PROPERTIES_COMBO_BOX_CONSTRAINT_TYPE = "EDIT_PROPERTIES_COMBO_BOX_CONSTRAINT_TYPE";

    /**
     * The Constant EDIT_PROPERTIES_COMBO_BOX_COLUMNS_LIST.
     */
    public static final String EDIT_PROPERTIES_COMBO_BOX_COLUMNS_LIST = "EDIT_PROPERTIES_COMBO_BOX_COLUMNS_LIST";

    /**
     * The Constant EDIT_PROPERTIES_COMBO_BOX_ON_DELETE.
     */
    public static final String EDIT_PROPERTIES_COMBO_BOX_ON_DELETE = "EDIT_PROPERTIES_COMBO_BOX_ON_DELETE";

    /**
     * The Constant CREATE_PROPERTIES_COMBO_BOX_DISTRIBUTION_TYPE.
     */
    public static final String CREATE_PROPERTIES_COMBO_BOX_DISTRIBUTION_TYPE = "CREATE_PROPERTIES_COMBO_BOX_DISTRIBUTION_TYPE";

    /**
     * The Constant CREATE_PROPERTIES_COMBO_BOX_PARTITION_TYPE.
     */
    public static final String CREATE_PROPERTIES_COMBO_BOX_PARTITION_TYPE = "CREATE_PROPERTIES_COMBO_BOX_PARTITION_TYPE";

    /**
     * The create table compression type.
     */
    String CREATE_TABLE_COMPRESSION_TYPE = "CREATE_TABLE_COMPRESSION_TYPE";

}
