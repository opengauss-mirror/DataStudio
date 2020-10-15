/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.userrole;

import org.eclipse.swt.widgets.Button;

/**
 * 
 * Title: class
 * 
 * Description: The Class PrivilegeModel.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
