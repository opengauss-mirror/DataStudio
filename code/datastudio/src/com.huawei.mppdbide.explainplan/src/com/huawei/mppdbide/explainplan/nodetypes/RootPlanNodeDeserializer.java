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

package com.huawei.mppdbide.explainplan.nodetypes;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * 
 * Title: class
 * 
 * Description: The Class RootPlanNodeDeserializer.
 *
 * @since 3.0.0
 */
public class RootPlanNodeDeserializer implements JsonDeserializer<RootPlanNode> {

    @Override
    public RootPlanNode deserialize(final JsonElement json, final Type typeOfT,
            final JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();
        if (array.size() <= 0) {
            return null;
        }

        JsonObject obj = array.get(0).getAsJsonObject();

        JsonElement json1 = obj.get("Plan");

        RootPlanNode root = new Gson().fromJson(obj, RootPlanNode.class);

        OperationalNode childnode = context.deserialize(json1, OperationalNode.class);

        root.addChildNode(childnode);

        return root;
    }
}
