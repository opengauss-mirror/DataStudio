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
import com.huawei.mppdbide.explainplan.plannode.factory.GetNodeFromFactory;

/**
 * 
 * Title: class
 * 
 * Description: The Class OperationalNodeDeserializer.
 *
 * @since 3.0.0
 */
public class OperationalNodeDeserializer implements JsonDeserializer<OperationalNode> {

    @Override
    public OperationalNode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();
        OperationalNode node = null;

        String nodeType = jsonObject.get("Node Type").getAsString();

        Class concreteClass = GetNodeFromFactory.getClass(nodeType);
        Object obj = new Gson().fromJson(json, concreteClass);

        if (obj != null && obj instanceof OperationalNode) {
            node = (OperationalNode) obj;

            JsonElement json2 = jsonObject.getAsJsonObject().get("Plans");
            if (null != json2) {
                JsonArray array = json2.getAsJsonArray();
                int size = array.size();
                OperationalNode childnode = null;
                for (int i = 0; i < size; i++) {
                    childnode = context.deserialize(array.get(i), OperationalNode.class);
                    node.addChildNode(childnode);
                }
            }
        }

        return node;
    }

}
