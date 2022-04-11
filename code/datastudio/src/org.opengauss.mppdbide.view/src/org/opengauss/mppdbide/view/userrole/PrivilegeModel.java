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

package org.opengauss.mppdbide.view.userrole;

import org.eclipse.swt.widgets.Button;

/**
 * 
 * Title: class
 * 
 * Description: The Class PrivilegeModel.
 *
 * @since 3.0.0
 */
public class PrivilegeModel {
    private String privilegeName;
    private Button privilegeButton;
    private Button grantOptionButton;

    /**
     * Instantiates a new privilege model.
     *
     * @param privilegeName the privilege name
     * @param privilegeButton the privilege button
     * @param grantOptionButton the grant option button
     */
    public PrivilegeModel(String privilegeName, Button privilegeButton, Button grantOptionButton) {
        super();
        this.privilegeName = privilegeName;
        this.privilegeButton = privilegeButton;
        this.grantOptionButton = grantOptionButton;
    }

    /**
     * Gets the privilege name.
     *
     * @return the privilege name
     */
    public String getPrivilegeName() {
        return privilegeName;
    }

    /**
     * Sets the privilege name.
     *
     * @param privilegeName the new privilege name
     */
    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    /**
     * Gets the privilege button.
     *
     * @return the privilege button
     */
    public Button getPrivilegeButton() {
        return privilegeButton;
    }

    /**
     * Sets the privilege button.
     *
     * @param privilegeButton the new privilege button
     */
    public void setPrivilegeButton(Button privilegeButton) {
        this.privilegeButton = privilegeButton;
    }

    /**
     * Gets the grant option button.
     *
     * @return the grant option button
     */
    public Button getGrantOptionButton() {
        return grantOptionButton;
    }

    /**
     * Sets the grant option button.
     *
     * @param grantOptionButton the new grant option button
     */
    public void setGrantOptionButton(Button grantOptionButton) {
        this.grantOptionButton = grantOptionButton;
    }

}
