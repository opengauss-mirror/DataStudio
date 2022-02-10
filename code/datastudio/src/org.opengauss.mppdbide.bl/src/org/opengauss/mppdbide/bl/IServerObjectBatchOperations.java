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

package org.opengauss.mppdbide.bl;

import org.opengauss.mppdbide.bl.export.EXPORTTYPE;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IServerObjectBatchOperations.
 * 
 */

public interface IServerObjectBatchOperations {

    /**
     * Gets the drop query.
     *
     * @param isCascade the is cascade
     * @return the drop query
     */
    public String getDropQuery(boolean isCascade);

    /**
     * Gets the object full name.
     *
     * @return the object full name
     */
    public String getObjectFullName();

    /**
     * Gets the object type name.
     *
     * @return the object type name
     */
    public String getObjectTypeName();

    /**
     * Checks if is drop allowed.
     *
     * @return true, if is drop allowed
     */
    public boolean isDropAllowed();

    /**
     * Checks if is export allowed.
     *
     * @param exportType the export type
     * @return true, if is export allowed
     */
    public boolean isExportAllowed(EXPORTTYPE exportType);
}
