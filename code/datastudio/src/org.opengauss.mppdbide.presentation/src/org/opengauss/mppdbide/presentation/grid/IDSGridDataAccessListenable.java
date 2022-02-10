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

package org.opengauss.mppdbide.presentation.grid;

import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.observer.IDSListenable;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSGridDataAccessListenable.
 *
 * @since 3.0.0
 */
public interface IDSGridDataAccessListenable extends IDSListenable {
    int LISTEN_TYPE_POST_FETCH = 1;
    int LISTEN_TYPE_ENDOF_RS = 2;
    int LISTEN_TYPE_ABOUTTO_REEXECUTE_QUERY = 3;

    /** 
     * visits the InputValues
     * 
     * @param dp the dp
     * @param index the index
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     */
    void visitInputValues(DefaultParameter dp, int index) throws DatabaseOperationException, DatabaseCriticalException;

}
