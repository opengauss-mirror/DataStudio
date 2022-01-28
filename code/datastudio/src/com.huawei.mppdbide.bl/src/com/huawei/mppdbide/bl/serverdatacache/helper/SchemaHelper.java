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

package com.huawei.mppdbide.bl.serverdatacache.helper;

import java.util.Set;
import java.util.SortedMap;

import com.huawei.mppdbide.bl.contentassist.ContentAssistUtilIf;
import com.huawei.mppdbide.bl.contentassist.ContentAssistUtilOLAP;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * 
 * Title: class
 * 
 * Description: The Class SchemaHelper.
 * 
 */

public class SchemaHelper {

    /**
     * Check for invalid namespaces in include list.
     *
     * @param db the db
     * @return the string
     */
    public static String checkForInvalidNamespacesInIncludeList(Database db) {
        StringBuffer buff = new StringBuffer();
        Set<String> includeExcludeList = db.getSchemInclusionList();
        matchIncludeExcludeSchemas(buff, includeExcludeList, db);
        return buff.length() == 0 ? null : buff.substring(0, buff.length() - 1);
    }

    /**
     * Check for invalid namespaces in exclude list.
     *
     * @param db the db
     * @return the string
     */
    public static String checkForInvalidNamespacesInExcludeList(Database db) {
        StringBuffer buff = new StringBuffer();
        Set<String> includeExcludeList = db.getSchemExclusionList();
        matchIncludeExcludeSchemas(buff, includeExcludeList, db);
        return buff.length() == 0 ? null : buff.substring(0, buff.length() - 1);
    }

    private static void matchIncludeExcludeSchemas(StringBuffer buff, Set<String> includeExcludeList, Database db) {
        SortedMap<String, ServerObject> matchedList = null;
        ContentAssistUtilIf contentAssistUtil;
        for (String namespace : includeExcludeList) {
            contentAssistUtil = new ContentAssistUtilOLAP(db);
            matchedList = contentAssistUtil.findExactMatchingNamespaces(namespace);
            if (matchedList.isEmpty()) {
                buff.append(namespace);
                buff.append(",");
            }
        }
    }
}
