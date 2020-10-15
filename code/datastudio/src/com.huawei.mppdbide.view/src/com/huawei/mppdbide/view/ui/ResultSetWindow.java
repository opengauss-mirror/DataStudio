/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

/**
 * Title: ResultSetWindow
 * 
 * Description: the Class ResultSetWindow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, 2019年9月4日]
 * @since 2019年9月4日
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
