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
 * Title: Version1JsonValidator
 * 
 * Description:The class Version1JsonValidator
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 23-May-2019]
 * @since 23-May-2019
 */

public class Version1JsonValidator {

    private static Map<String, String> jsonFieldMap;

    /**
     * Validate.
     *
     * @param jsonObject the json object
     * @return true, if successful
     */
    public static boolean validate(JsonObject jsonObject) {
        boolean isValid = true;
        fillValidFields();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        Iterator<Entry<String, JsonElement>> iterator = entrySet.iterator();
        while (iterator.hasNext() && isValid) {
            Entry<String, JsonElement> next = iterator.next();
            isValid = isMemberPresent(next.getKey());
            if (next.getKey().equals("serverIp") && null != next.getValue()) {
                isValid = validateHostAddress(next.getValue().getAsString());
            }
        }
        return isValid;
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
        jsonFieldMap.put("conectionName", "conectionName");
        jsonFieldMap.put("databaseName", "databaseName");
        jsonFieldMap.put("prd", "prd");
        jsonFieldMap.put("isSSLEnabled", "isSSLEnabled");
        jsonFieldMap.put("serverIp", "serverIp");
        jsonFieldMap.put("serverPort", "serverPort");
        jsonFieldMap.put("username", "username");
        jsonFieldMap.put("savePrdOption", "savePrdOption");
        jsonFieldMap.put("connctionDriverName", "connctionDriverName");
        jsonFieldMap.put("ssl", "ssl");
        jsonFieldMap.put("clSSLCertificatePath", "clSSLCertificatePath");
        jsonFieldMap.put("clSSLKeyPath", "clSSLKeyPath");
        jsonFieldMap.put("sslPrd", "sslPrd");
        jsonFieldMap.put("rootCertFilePathText", "rootCertFilePathText");
        jsonFieldMap.put("clSSLPrivateKeyFile", "clSSLPrivateKeyFile");
        jsonFieldMap.put("sslMode", "sslMode");
        jsonFieldMap.put("advanced", "advanced");
        jsonFieldMap.put("schemaExclusionList", "schemaExclusionList");
        jsonFieldMap.put("schemaInclusionList", "schemaInclusionList");
        jsonFieldMap.put("loadLimit", "loadLimit");
        jsonFieldMap.put("privilegeBasedObAcess", "privilegeBasedObAcess");
    }

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

    private static boolean isMemberPresent(String member) {

        return jsonFieldMap.containsKey(member);
    }
}
