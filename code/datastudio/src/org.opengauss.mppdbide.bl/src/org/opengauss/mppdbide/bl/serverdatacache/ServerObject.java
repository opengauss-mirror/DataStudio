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

import org.opengauss.mppdbide.adapter.gauss.HandleGaussStringEscaping;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.SQLKeywords;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ServerObject.
 * 
 */

public abstract class ServerObject {

    private long oid;
    private String name;
    private OBJECTTYPE type;
    private boolean isValid = true;
    private static final String isObjectName = "^([a-z_][a-z|0-9|_|$]*)|([a-z_][a-z|0-9|_|,| |.|$]*)$";
    
    /** 
     * The is loaded. 
     */
    protected boolean isLoaded;

    /**
     * The privilege flag.
     */
    protected boolean privilegeFlag;

    /**
     * Sets the privilege flag.
     *
     * @param privilegeFlag the new privilege flag
     */
    public void setPrivilegeFlag(boolean privilegeFlag) {
        this.privilegeFlag = privilegeFlag;
    }

    /**
     * Gets the privilege flag.
     *
     * @return the privilege flag
     */
    public boolean getPrivilegeFlag() {
        return this.privilegeFlag;
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        final int primeNumber = 31;
        int result = 1;
        result = getRamdomNumber(primeNumber, result) + ((name == null) ? 0 : name.hashCode());

        result = getRamdomNumber(primeNumber, result) + (int) (oid ^ (oid >>> 32));

        result = getRamdomNumber(primeNumber, result) + ((getParent() == null) ? 0 : getParent().hashCode());
        return result;
    }

    /**
     * Gets the window title name.
     *
     * @return the window title name
     */
    public String getWindowTitleName() {
        return name;
    }

    /**
     * Gets the ramdom number.
     *
     * @param primeNumber the prime number
     * @param result the result
     * @return the ramdom number
     */
    private int getRamdomNumber(final int primeNumber, int result) {
        return primeNumber * result;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (validateObjectForNull(obj)) {
            return false;
        }

        if (!(obj instanceof ServerObject)) {
            return false;
        }

        ServerObject other = (ServerObject) obj;
        if (validateObjectForNull(name)) {
            if (!validateObjectForNull(other.name)) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (oid != other.oid) {
            return false;
        }

        if (type != other.type) {
            return false;
        }

        if (validateObjectForNull(getParent())) {
            if (!validateObjectForNull(other.getParent())) {
                return false;
            }
        } else if (!getParent().equals(other.getParent())) {
            return false;
        }

        return true;
    }

    /**
     * Validate object for null.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private boolean validateObjectForNull(Object obj) {
        return obj == null;
    }

    /**
     * Checks if is valid.
     *
     * @return true, if is valid
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Sets the valid.
     *
     * @param isVald the new valid
     */
    public void setValid(boolean isVald) {
        this.isValid = isVald;
    }

    /**
     * Instantiates a new server object.
     *
     * @param type the type
     */
    public ServerObject(OBJECTTYPE type) {
        this.type = type;
    }

    /**
     * Instantiates a new server object.
     *
     * @param oid the oid
     * @param name the name
     * @param type the type
     * @param privilegeFlag the privilege flag
     */
    public ServerObject(long oid, String name, OBJECTTYPE type, boolean privilegeFlag) {
        this.type = type;
        this.oid = oid;
        this.name = name;
        this.privilegeFlag = privilegeFlag;
    }

    /**
     * Sets the oid.
     *
     * @param oid the new oid
     */
    public void setOid(long oid) {
        this.oid = oid;
    }

    /**
     * Gets the oid.
     *
     * @return the oid
     */
    public long getOid() {
        return oid;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public OBJECTTYPE getType() {
        return type;
    }

    /**
     * Gets the type label.
     *
     * @return the type label
     */
    public String getTypeLabel() {

        return DBTypeLabelUtil.getTypeLabel(type);

    }

    /**
     * Gets the search name.
     *
     * @return the search name
     */
    public String getSearchName() {
        return getQualifiedSimpleObjectName(this.name);
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return getQualifiedObjectName();
    }

    /**
     * Gets the qualified object name.
     *
     * @return the qualified object name
     */
    public String getQualifiedObjectName() {
        return getQualifiedObjectName(this.name);
    }

    /**
     * Gets the qualified object name.
     *
     * @param objectName the object name
     * @return the qualified object name
     */
    public static String getQualifiedObjectName(String objectName) {
        if (objectName == null) {
            return null;
        }
        if (isKeywordName(objectName)) {
            return addQualifiedNameForKeyword(objectName);
        }

        return getQualifiedSimpleObjectName(objectName);

    }

    /**
     * Gets the qualified simple object name.
     *
     * @param objectName the object name
     * @return the qualified simple object name
     */
    public static String getQualifiedSimpleObjectName(String objectName) {
        if (isQualifiedSimpleObjectName(objectName)) {
            return objectName;
        }
        return HandleGaussStringEscaping.escapeIdentifier(objectName).toString();
    }

    /**
     * Gets the qualified object name handle quotes.
     *
     * @param objectName the object name
     * @return the qualified object name handle quotes
     */
    public static String getQualifiedObjectNameHandleQuotes(String objectName) {
        if (ServerObject.isQualifiedSimpleObjectNameHandleQuotes(objectName)) {
            return objectName;
        }
        return HandleGaussStringEscaping.escapeIdentifier(objectName).toString();
    }

    /**
     * Gets the literal name.
     *
     * @param objectName the object name
     * @return the literal name
     */
    public static String getLiteralName(String objectName) {
        return HandleGaussStringEscaping.escapeLiteral(null, objectName).toString();
    }

    /**
     * Checks if is qualified simple object name.
     *
     * @param objectName the object name
     * @return true, if is qualified simple object name
     */
    public static boolean isQualifiedSimpleObjectName(String objectName) {
        if (null != objectName && !objectName.isEmpty() && objectName.matches(isObjectName)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if is qualified simple object name handle quotes.
     *
     * @param objectName the object name
     * @return true, if is qualified simple object name handle quotes
     */
    public static boolean isQualifiedSimpleObjectNameHandleQuotes(String objectName) {
        if (null != objectName && !objectName.isEmpty() && ((objectName.matches("^[A-Z|a-z_][A-Z|a-z|0-9|_|$]*$"))
                || (objectName.startsWith("\"") && objectName.endsWith("\"")))) {
            return true;
        }

        return false;
    }

    /**
     * Gets the qualified object name split.
     *
     * @param objectName the object name
     * @return the qualified object name split
     */
    public String getQualifiedObjectNameSplit(String[] objectName) {
        StringBuilder returnString = new StringBuilder("");
        if (null == objectName) {
            return null;
        } else if (objectName.length == 1) {
            returnString.append(ServerObject.getQualifiedObjectNameHandleQuotes(objectName[0]));
        } else {
            for (int index = 0; index < objectName.length; index++) {
                returnString.append(ServerObject.getQualifiedObjectNameHandleQuotes(objectName[index]));
                if (index < objectName.length - 1) {
                    returnString.append(".");
                }
            }
        }
        return returnString.toString();
    }

    /**
     * Checks if is qualified partition value.
     *
     * @param objectName the object name
     * @return the string
     */
    public static String isQualifiedPartitionValue(String objectName) {
        if (null == objectName) {
            return "";
        }
        if (!objectName.isEmpty() && objectName.matches("[A-Za-z0-9_\\-\\.]*")) {
            return '\'' + objectName + '\'';
        }
        return HandleGaussStringEscaping.escapeIdentifier(objectName).toString();
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return null;
    }

    /**
     * Gets the display label.
     *
     * @return the display label
     */
    public String getDisplayLabel() {

        String objName = getName();
        return null == objName || objName.length() < 1
                ? MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_LABEL_MSG)
                : objName;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public Object[] getChildren() {
        return new Object[0];
    }

    /**
     * Checks if is loading in progress.
     *
     * @return true, if is loading in progress
     */
    public boolean isLoadingInProgress() {
        return false;
    }

    /**
     * Gets the object browser label.
     *
     * @return the object browser label
     */
    public String getObjectBrowserLabel() {
        return getDisplayLabel();
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Object getParent() {
        return null;
    }

    /**
     * Checks if is loaded.
     *
     * @return true, if is loaded
     */
    public boolean isLoaded() {
        return true;
    }

    /**
     * Checks if is keyword name.
     *
     * @param objectName the object name
     * @return true, if is keyword name
     */
    public static boolean isKeywordName(String objectName) {

        return SQLKeywords.getKeywords().containsKey(objectName);

    }

    /**
     * Adds the qualified name for keyword.
     *
     * @param objectName the object name
     * @return the string
     */
    public static String addQualifiedNameForKeyword(String objectName) {
        return "\"" + objectName + "\"";
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    public ServerObject getNamespace() {
        return null;

    }

    /**
     * Gets the auto suggestion name.
     *
     * @param isAutoSuggest the is auto suggest
     * @return the auto suggestion name
     */
    public String getAutoSuggestionName(boolean isAutoSuggest) {
        return getQualifiedObjectName();
    }

    /**
     * Gets the connection manager.
     *
     * @return the connection manager
     */
    public ConnectionManager getConnectionManager() {
        if (getDatabase() != null) {
            return getDatabase().getConnectionManager();
        }
        return null;
    }

    /**
     * Sets the loaded.
     *
     * @param isLoaded the new loaded
     */
    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public boolean getLoaded() {
        return this.isLoaded;
    }
}
