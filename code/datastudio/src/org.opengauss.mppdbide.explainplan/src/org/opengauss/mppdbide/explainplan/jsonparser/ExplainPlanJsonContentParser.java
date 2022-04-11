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

package org.opengauss.mppdbide.explainplan.jsonparser;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNodeDeserializer;
import org.opengauss.mppdbide.explainplan.nodetypes.RootPlanNode;
import org.opengauss.mppdbide.explainplan.nodetypes.RootPlanNodeDeserializer;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainPlanJsonContentParser.
 *
 * @since 3.0.0
 */
public class ExplainPlanJsonContentParser {
    private String jsonContent;

    /**
     * Instantiates a new explain plan json content parser.
     *
     * @param jsonContent the json content
     */
    public ExplainPlanJsonContentParser(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    /**
     * Parses the file contents.
     *
     * @return the root plan node
     * @throws DatabaseOperationException the database operation exception
     */
    public RootPlanNode parseFileContents() throws DatabaseOperationException {
        RootPlanNode cRootNode = jsonParser();
        if (cRootNode != null && !cRootNode.getChildren().isEmpty()) {
            cRootNode.getChildren().get(0).setParent(null);
            fixParentChildRelationship(cRootNode.getChildren(), null);
            return cRootNode;
        }

        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_JSON_PARSING_FAILED));
        throw new DatabaseOperationException(IMessagesConstants.VIS_EXPLAIN_JSON_PARSING_FAILED);

    }

    private void fixParentChildRelationship(ArrayList<OperationalNode> arrayList, OperationalNode parent) {
        for (OperationalNode c : arrayList) {
            c.setParent(parent);
            fixParentChildRelationship(c.getChildren(), c);
        }

    }

    private RootPlanNode jsonParser() {
        Gson gson = null;
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(RootPlanNode.class, new RootPlanNodeDeserializer());
        gsonBuilder.registerTypeAdapter(OperationalNode.class, new OperationalNodeDeserializer());
        gson = gsonBuilder.create();

        try {
            return gson.fromJson(jsonContent, RootPlanNode.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
