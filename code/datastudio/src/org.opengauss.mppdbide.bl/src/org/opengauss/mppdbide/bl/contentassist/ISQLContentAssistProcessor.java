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

package org.opengauss.mppdbide.bl.contentassist;

import java.util.LinkedHashMap;
import java.util.SortedMap;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISQLContentAssistProcessor.
 * 
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
