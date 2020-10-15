/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.connectioninfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Title: Version2jsonValidator
 * 
 * Description:The class Version2jsonValidator
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 23-May-2019]
 * @since 23-May-2019
 */

public class Version2jsonValidator {

    private static Map<String, String> jsonFieldMap;
    private static Map<String, String> genJsonFieldMap;
    private static Map<String, String> sslJsonFieldMap;
    private static Map<String, String> advJsonFieldMap;

    /**
     * Validate.
     *
     * @param jsonObject the json object
     * @return true, if successful
     */
    public static boolean validate(JsonObject jsonObject) {
        boolean isValid = false;

        isValid = validateVersionAndID(jsonObject);
        if (isValid) {
            isValid = validateGeneralInfo(jsonObject);
        }
        if (isValid) {
            isValid = validateSSLInfo(jsonObject);
        }
        if (isValid) {
            isValid = validateAdvInfo(jsonObject);
        }

        return isValid;
    }

    /**
     * Validate SSL info.
     *
     * @param jsonObject the json object
     * @return true, if successful
     */
    private static boolean validateSSLInfo(JsonObject jsonObject) {
        boolean isValidJson = true;
        fillValidSSLFields();
        if (isMemberPresent("ssl", jsonFieldMap)) {
            JsonObject genJsonObject = jsonObject.get("ssl").getAsJsonObject();
            Set<Entry<String, JsonElement>> entrySet = genJsonObject.entrySet();
            Iterator<Entry<String, JsonElement>> iterator = entrySet.iterator();
            while (iterator.hasNext() && isValidJson) {
                Entry<String, JsonElement> next = iterator.next();
                isValidJson = isMemberPresent(next.getKey(), sslJsonFieldMap);
            }
        }

        return isValidJson;

    }

    /**
     * Validate adv info.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private static boolean validateAdvInfo(JsonObject obj) {

        boolean isValidJson = true;
        fillValidAdvFields();
        if (isMemberPresent("advanced", jsonFieldMap)) {
            JsonObject genJsonObject = obj.get("advanced").getAsJsonObject();
            Set<Entry<String, JsonElement>> entrySet = genJsonObject.entrySet();
            Iterator<Entry<String, JsonElement>> iterator = entrySet.iterator();
            while (iterator.hasNext() && isValidJson) {
                Entry<String, JsonElement> next = iterator.next();
                isValidJson = isMemberPresent(next.getKey(), advJsonFieldMap);

            }
        }

        return isValidJson;

    }

    /**
     * Validate version and ID.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private static boolean validateVersionAndID(JsonObject obj) {
        boolean isValidJson = true;
        fillValidFields();
        Set<Entry<String, JsonElement>> entrySet = obj.entrySet();
        Iterator<Entry<String, JsonElement>> iterator = entrySet.iterator();
        while (iterator.hasNext() && isValidJson) {
            Entry<String, JsonElement> next = iterator.next();
            isValidJson = isMemberPresent(next.getKey(), jsonFieldMap);
        }

        return isValidJson;
    }

    /**
     * Validate general info.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private static boolean validateGeneralInfo(JsonObject obj) {
        boolean isValidJson = true;
        fillValidGenFields();
        if (isMemberPresent("general", jsonFieldMap)) {
            JsonObject genJsonObject = obj.get("general").getAsJsonObject();
            Set<Entry<String, JsonElement>> entrySet = genJsonObject.entrySet();
            Iterator<Entry<String, JsonElement>> iterator = entrySet.iterator();
            while (iterator.hasNext() && isValidJson) {
                Entry<String, JsonElement> next = iterator.next();
                isValidJson = isMemberPresent(next.getKey(), genJsonFieldMap);
                if (next.getKey().equals("conectionName") && null != next.getValue()) {
                    isValidJson = validateHostAddress(next.getValue().getAsString());
                }
            }
        }

        return isValidJson;
    }

    /**
     * Validate host address.
     *
     * @param string the string
     * @return true, if successful
     */
    private static boolean validateHostAddress(String string) {

        Pattern pattern = Pattern.compile(ServerConnectionInfoJsonValidator.IP_ADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(string);
        if (string.matches(ServerConnectionInfoJsonValidator.IS_IP_ADDRESS)) {
            if (!(matcher.matches())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fill valid fields.
     */
    private static void fillValidFields() {
        jsonFieldMap = new HashMap<>();
        jsonFieldMap.put("profileId", "profileId");
        jsonFieldMap.put("databaseVersion", "databaseVersion");
        jsonFieldMap.put("dbType", "dbType");
        jsonFieldMap.put("version", "version");
        jsonFieldMap.put("general", "general");
        jsonFieldMap.put("ssl", "ssl");
        jsonFieldMap.put("advanced", "advanced");

    }

    /**
     * Fill valid gen fields.
     */
    private static void fillValidGenFields() {
        genJsonFieldMap = new HashMap<>();
        genJsonFieldMap.put("conectionName", "conectionName");
        genJsonFieldMap.put("databaseName", "databaseName");
        genJsonFieldMap.put("prd", "prd");
        genJsonFieldMap.put("isSSLEnabled", "isSSLEnabled");
        genJsonFieldMap.put("serverIp", "serverIp");
        genJsonFieldMap.put("serverPort", "serverPort");
        genJsonFieldMap.put("username", "username");
        genJsonFieldMap.put("savePrdOption", "savePrdOption");
        genJsonFieldMap.put("connctionDriverName", "connctionDriverName");
    }

    /**
     * Fill valid SSL fields.
     */
    private static void fillValidSSLFields() {
        sslJsonFieldMap = new HashMap<>();
        sslJsonFieldMap.put("clSSLCertificatePath", "clSSLCertificatePath");
        sslJsonFieldMap.put("clSSLKeyPath", "clSSLKeyPath");
        sslJsonFieldMap.put("sslPrd", "sslPrd");
        sslJsonFieldMap.put("rootCertFilePathText", "rootCertFilePathText");
        sslJsonFieldMap.put("sslMode", "sslMode");
        sslJsonFieldMap.put("clSSLPrivateKeyFile", "clSSLPrivateKeyFile");

    }

    /**
     * Fill valid adv fields.
     */
    private static void fillValidAdvFields() {
        advJsonFieldMap = new HashMap<>();
        advJsonFieldMap.put("schemaExclusionList", "schemaExclusionList");
        advJsonFieldMap.put("schemaInclusionList", "schemaInclusionList");
        advJsonFieldMap.put("canLoadChildObj", "canLoadChildObj");
        advJsonFieldMap.put("loadLimit", "loadLimit");
        advJsonFieldMap.put("privilegeBasedObAcess", "privilegeBasedObAcess");
    }

    /**
     * Checks if is member present.
     *
     * @param member the member
     * @param fieldMap the field map
     * @return true, if is member present
     */
    private static boolean isMemberPresent(String member, Map<String, String> fieldMap) {

        return fieldMap.containsKey(member);
    }
}
