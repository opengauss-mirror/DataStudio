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

package org.opengauss.mppdbide.view.prefernces;

/**
 * 
 * Title: class
 * 
 * Description: The Class KeyBinding.
 *
 * @since 3.0.0
 */
public class KeyBinding {

    private String commandId;
    private String commandName;
    private String command;
    private String description;
    private String defaultKey;
    private String newKey;

    /**
     * Instantiates a new key binding.
     */
    public KeyBinding() {

    }

    /**
     * Gets the command.
     *
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command.
     *
     * @param command the new command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the default key.
     *
     * @return the default key
     */
    public String getDefaultKey() {
        return defaultKey;
    }

    /**
     * Sets the default key.
     *
     * @param defaultKey the new default key
     */
    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
    }

    /**
     * Gets the command name.
     *
     * @return the command name
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Sets the command name.
     *
     * @param commandName the new command name
     */
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    /**
     * Gets the new key.
     *
     * @return the new key
     */
    public String getNewKey() {
        return newKey;
    }

    /**
     * Sets the new key.
     *
     * @param newKey the new new key
     */
    public void setNewKey(String newKey) {
        this.newKey = newKey;
    }

    /**
     * Gets the command id.
     *
     * @return the command id
     */
    public String getCommandId() {
        return commandId;
    }

    /**
     * Sets the command id.
     *
     * @param commandId the new command id
     */
    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

}
