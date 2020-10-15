/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
