/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.connectioninfo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Title: ServerConnectionInfoJsonValidator
 * 
 * Description:The Class ServerConnectionInfoJsonValidator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 23-May-2019]
 * @since 23-May-2019
 */

public final class ServerConnectionInfoJsonValidator {

    /**
     * The Constant IP_ADDRESS_PATTERN.
     */
    public static final String IP_ADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    /**
     * The Constant IS_IP_ADDRESS.
     */
    public static final String IS_IP_ADDRESS = "^\\d+\\.\\d+\\.\\d+\\.\\d+$";

    /**
     * Instantiates a new server connection info json validator.
     */
    private ServerConnectionInfoJsonValidator() {
    }

    /**
     * Validate json.
     *
     * @param jsonString the json string
     * @return true, if successful
     */
    public static boolean validateJson(String jsonString) {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);
        for (int jsonIndex = 0; jsonIndex < jsonArray.size(); jsonIndex++) {
            if (jsonArray.get(jsonIndex).isJsonObject()) {
                JsonObject asJsonObject = jsonArray.get(jsonIndex).getAsJsonObject();
                String jsonElement = asJsonObject.has("version") ? asJsonObject.get("version").getAsString() : "";
                if (jsonElement.equals("1.00")) {
                    return Version1JsonValidator.validate(asJsonObject);
                } else if (jsonElement.equals("2.00")) {
                    return Version2jsonValidator.validate(asJsonObject);
                }

            }
        }

        return false;
    }

}
