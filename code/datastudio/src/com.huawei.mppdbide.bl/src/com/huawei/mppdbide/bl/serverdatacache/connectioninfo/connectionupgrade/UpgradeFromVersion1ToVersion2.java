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

package com.huawei.mppdbide.bl.serverdatacache.connectioninfo.connectionupgrade;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class UpgradeFromVersion1ToVersion2.
 * 
 */

public class UpgradeFromVersion1ToVersion2 implements IConnectionProfileUpgrader {

    @Override
    public String upgrade(String jsonString) {
        Gson gson = new Gson();
        IServerConnectionInfo info = null;

        JsonArray jsonArray = gson.fromJson(jsonString, JsonArray.class);
        for (int jsonObjIndex = 0; jsonObjIndex < jsonArray.size(); jsonObjIndex++) {
            if (jsonArray.get(jsonObjIndex).isJsonObject()) {
                JsonObject asJsonObject = jsonArray.get(jsonObjIndex).getAsJsonObject();
                info = new ServerConnectionInfo();

                info.setDBVersion(getValue("databaseVersion", asJsonObject));
                info.setProfileId(getValue("profileId", asJsonObject));
                formatToGeneralInfo(asJsonObject, info);
                formatToSSLInfo(asJsonObject, info);
                formatToAdvancedInfo(asJsonObject, info);
            }
        }

        String json = gson.toJson(info);
        return json;
    }

    private void formatToGeneralInfo(JsonObject object, IServerConnectionInfo info) {

        info.setConectionName(getValue("conectionName", object));
        info.setDatabaseName(getValue("databaseName", object));
        info.setServerIp(getValue("serverIp", object));
        info.setServerPort(getIntegerValue("serverPort", object));
        info.setUsername(getValue("username", object));
        info.setSSLEnabled(getBoolValue("isSSLEnabled", object));
        info.setSavePrdOption(getValue("savePrdOption", object).equals("") ? SavePrdOptions.DO_NOT_SAVE
                : SavePrdOptions.valueOf(getValue("savePrdOption", object)));
        info.setDriverName(getValue("connctionDriverName", object));
        info.setDbType();
    }

    private void formatToSSLInfo(JsonObject object, IServerConnectionInfo info) {
        info.setClientSSLCertificate(getValue("clSSLCertificatePath", object));
        info.setClientSSLKey(getValue("clSSLKeyPath", object));
        info.setRootCertificate(getValue("rootCertFilePathText", object));
        info.setSSLMode(getValue("sslMode", object));
    }

    private void formatToAdvancedInfo(JsonObject object, IServerConnectionInfo info) {
        info.setLoadLimit(getIntegerValue("loadLimit", object));
        info.setPrivilegeBasedObAccess(getBoolValue("privilegeBasedObAcess", object));
    }

    private String getValue(String key, JsonObject obj) {
        if (obj.has(key)) {
            return obj.get(key).getAsString();
        }
        return "";
    }

    private Integer getIntegerValue(String key, JsonObject obj) {
        if (obj.has(key)) {
            return obj.get(key).getAsInt();
        }
        return 0;
    }

    private Boolean getBoolValue(String key, JsonObject obj) {
        if (obj.has(key)) {
            return obj.get(key).getAsBoolean();
        }
        return false;
    }
}
