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

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.ArrayList;

import com.huawei.mppdbide.adapter.gauss.GaussDatatypeUtils;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectParameter.
 * 
 */

public class ObjectParameter {

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum PARAMETERTYPE.
     * 
     */
    public enum PARAMETERTYPE {
        IN, OUT, INOUT
    };

    private String datatype;
    private String name;
    private PARAMETERTYPE type;
    private String value;
    private boolean isSupportedDatatype;

    /**
     * Instantiates a new object parameter.
     */
    public ObjectParameter() {
    }

    /**
     * Instantiates a new object parameter.
     *
     * @param convertToClientType the convert to client type
     * @param supported the supported
     */
    public ObjectParameter(String convertToClientType, boolean supported) {
        this.datatype = convertToClientType;
        this.isSupportedDatatype = supported;
    }

    /**
     * Gets the datatype.
     *
     * @return the datatype
     */
    public String getDataType() {
        return datatype;
    }

    /**
     * Sets the datatype.
     *
     * @param datatype the new datatype
     */
    public void setDataType(String datatype) {
        this.datatype = datatype;
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
    public PARAMETERTYPE getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(PARAMETERTYPE type) {
        this.type = type;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Clear value.
     */
    public void clearValue() {
        value = null;
    }

    /**
     * Gets the checks if is supported datatype.
     *
     * @return the checks if is supported datatype
     */
    public boolean getIsSupportedDatatype() {
        return isSupportedDatatype;
    }

    /**
     * Sets the checks if is supported datatype.
     *
     * @param isSupportedDatatype the new checks if is supported datatype
     */
    public void setIsSupportedDatatype(boolean isSupportedDatatype) {
        this.isSupportedDatatype = isSupportedDatatype;
    }

    /**
     * Gets the variables.
     *
     * @param types the types
     * @param names the names
     * @param modes the modes
     * @param allin the allin
     * @param nsList the ns list
     * @return the variables
     */
    public static ObjectParameter[] getVariables(String types, String names, String modes, boolean allin,
            ArrayList<Namespace> nsList) {
        ArrayList<ObjectParameter> arguments = new ArrayList<ObjectParameter>(MPPDBIDEConstants.VARIABLE_ARRAY_SIZE);
        String[] argModes = null;
        String[] argTypes = null;
        String[] argName = null;

        if (null == types) {
            return arguments.toArray(new ObjectParameter[arguments.size()]);
        }

        if (allin) {
            argTypes = types.split(" ");
        } else {
            if (containsOpenAndClosedParenthesisInOrder(types)) {
                argTypes = types.substring(types.indexOf('{') + 1, types.indexOf('}')).split(",");
            } else {
                argTypes = types.split(",");
            }
        }

        argName = getArgName(names, argTypes);
        argModes = setArgsMode(modes);
        int argLength = argTypes.length;

        addArguments(names, nsList, arguments, argModes, argTypes, argName, argLength);

        return arguments.toArray(new ObjectParameter[arguments.size()]);
    }

    /**
     * Adds the arguments.
     *
     * @param names the names
     * @param nsList the ns list
     * @param arguments the arguments
     * @param argModes the arg modes
     * @param argTypes the arg types
     * @param argName the arg name
     * @param argLength the arg length
     */
    private static void addArguments(String names, ArrayList<Namespace> nsList, ArrayList<ObjectParameter> arguments,
            String[] argModes, String[] argTypes, String[] argName, int argLength) {
        ObjectParameter var = null;
        int typeid = 0;
        String argmode = null;
        for (int argIndex = 0; argIndex < argLength; argIndex++) {
            var = new ObjectParameter();

            if (names != null) {
                var.setName(argName[argIndex]);
            }

            if (null != argModes) {
                argmode = argModes[argIndex];
            } else {
                argmode = "i";
            }

            if ("o".equals(argmode)) {
                var.setType(PARAMETERTYPE.OUT);
            } else if ("b".equals(argmode)) {
                var.setType(PARAMETERTYPE.INOUT);
            } else {
                var.setType(PARAMETERTYPE.IN);
            }

            try {
                typeid = Integer.parseInt(argTypes[argIndex]);
            } catch (NumberFormatException exception) {
                MPPDBIDELoggerUtility.error("Exception occured while adding the arguments", exception);
            }

            var.setDataType(getDataTypeName(typeid, nsList));
            var.setIsSupportedDatatype(GaussDatatypeUtils.isSupported(typeid));

            arguments.add(var);
        }
    }

    /**
     * Sets the args mode.
     *
     * @param modes the modes
     * @param argModes the arg modes
     * @return the string[]
     */
    private static String[] setArgsMode(String modes) {
        String[] argModes = null;
        if (null != modes) {
            if (modes.contains("{") && modes.contains("}")) {
                argModes = modes.substring(modes.indexOf('{') + 1, modes.indexOf('}')).split(",");
            } else {
                argModes = modes.split(",");
            }
        }
        return argModes;
    }

    /**
     * Gets the arg name.
     *
     * @param names the names
     * @param argTypes the arg types
     * @return the arg name
     */
    private static String[] getArgName(String names, String[] argTypes) {
        String[] argName;
        argName = new String[argTypes.length];
        if (names != null) {
            if (containsOpenAndClosedParenthesisInOrder(names)) {
                argName = names.substring(names.indexOf('{') + 1, names.indexOf('}')).split(",");
            } else {
                argName = names.split(",");
            }
        }
        return argName;
    }

    /**
     * Contains open and closed parenthesis in order.
     *
     * @param types the types
     * @return true, if successful
     */
    private static boolean containsOpenAndClosedParenthesisInOrder(String types) {
        return types.contains("{") && types.contains("}") && types.indexOf("}") > types.indexOf("{");
    }

    /**
     * Gets the data type name.
     *
     * @param rettype the rettype
     * @param nsList the ns list
     * @return the data type name
     */
    private static String getDataTypeName(int rettype, ArrayList<Namespace> nsList) {
        String convertedDataType = GaussDatatypeUtils.convertToClientType(rettype);
        if (null == convertedDataType) {
            convertedDataType = TypeMetaDataUtil.getDataTypeFromNamespace(rettype, nsList);
        }

        return convertedDataType;
    }

    /**
     * Update query.
     *
     * @return the object
     */
    public Object updateQuery() {
        switch (this.getType()) {
            case IN: {
                return "IN " + getDataType();
            }
            case INOUT: {
                return "INOUT " + getDataType();
            }
            case OUT: {
                return "OUT " + getDataType();
            }
            default: {
                return getDataType();
            }
        }
    }

    /**
     * Gets the display name with arg name.
     *
     * @return the display name with arg name
     */
    public String getDisplayNameWithArgName() {
        StringBuilder displayNameWithArg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        displayNameWithArg.append(this.getName());
        displayNameWithArg.append(" ");
        displayNameWithArg.append(this.getDataType()).append(" ");
        switch (this.getType()) {
            case OUT: {
                displayNameWithArg.append("OUT");
                break;
            }
            case INOUT: {
                displayNameWithArg.append("INOUT");
                break;
            }
            default: {
                displayNameWithArg.append("IN");
            }
        }

        return displayNameWithArg.toString();
    }

    /**
     * Gets the display name.
     *
     * @param isAutoSuggest the is auto suggest
     * @return the display name
     */
    public String getDisplayName(boolean isAutoSuggest) {
        StringBuilder displayName = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        displayName.append(this.getDataType());
        if (!isAutoSuggest) {
            displayName.append(" ");
            switch (this.getType()) {
                case OUT: {
                    displayName.append("OUT");
                    break;
                }
                case INOUT: {
                    displayName.append("INOUT");
                    break;
                }
                default: {
                    displayName.append("IN");
                }
            }
        }

        return displayName.toString();

    }

}