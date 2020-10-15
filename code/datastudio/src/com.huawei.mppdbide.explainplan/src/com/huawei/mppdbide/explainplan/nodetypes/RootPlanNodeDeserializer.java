/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
