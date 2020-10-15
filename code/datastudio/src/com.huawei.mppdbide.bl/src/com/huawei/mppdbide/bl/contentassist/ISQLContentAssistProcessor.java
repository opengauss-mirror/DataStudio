/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.contentassist;

import java.util.LinkedHashMap;
import java.util.SortedMap;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISQLContentAssistProcessor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface ISQLContentAssistProcessor {

    /**
     * Find prefix matching objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findPrefixMatchingObjects(String prefix);

    /**
     * Find exact matching objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findExactMatchingObjects(String prefix);

    /**
     * Find non loaded objects.
     *
     * @param connection the connection
     * @param prefixs the prefixs
     * @return the sorted map
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    LinkedHashMap<String, ServerObject> findNonLoadedObjects(DBConnection connection, String[] prefixs)
            throws MPPDBIDEException;

}
