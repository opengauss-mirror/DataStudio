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

package org.opengauss.mppdbide.view.ui;

/**
 * Title: ResultSetWindow
 * 
 * Description: the Class ResultSetWindow.
 *
 * @since 3.0.0
 */
public class ResultSetWindow {
    private static boolean isOpenNewTAb = false;
    private static boolean isDiscardAllModified = false;
    private static boolean isCancelForAllModified = false;

    /**
     * Checks if is cancel for all modified.
     *
     * @return true, if is cancel for all modified
     */
    public static boolean isCancelForAllModified() {
        return isCancelForAllModified;
    }

    /**
     * Sets the cancel for all modified.
     *
     * @param isCancelForAllModified the new cancel for all modified
     */
    public static void setCancelForAllModified(boolean isCancelForAllModified) {
        ResultSetWindow.isCancelForAllModified = isCancelForAllModified;
    }

    /**
     * Checks if is discard all modified.
     *
     * @return true, if is discard all modified
     */
    public static boolean isDiscardAllModified() {
        return isDiscardAllModified;
    }

    /**
     * Sets the discard all modified.
     *
     * @param isDiscardAlll the new discard all modified
     */
    public static void setDiscardAllModified(boolean isDiscardAlll) {
        ResultSetWindow.isDiscardAllModified = isDiscardAlll;
    }

    /**
     * Checks if is open new T ab.
     *
     * @return true, if is open new T ab
     */
    public static boolean isOpenNewTAb() {
        return isOpenNewTAb;
    }

    /**
     * Sets the open new T ab.
     *
     * @param isOpenNewTAb the new open new T ab
     */
    public static void setOpenNewTAb(boolean isOpenNewTAb) {
        ResultSetWindow.isOpenNewTAb = isOpenNewTAb;
    }
}
