/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl;

import com.huawei.mppdbide.bl.export.EXPORTTYPE;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IServerObjectBatchOperations.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
