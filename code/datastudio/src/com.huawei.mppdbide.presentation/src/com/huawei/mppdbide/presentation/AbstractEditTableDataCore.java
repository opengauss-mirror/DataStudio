/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractEditTableDataCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public abstract class AbstractEditTableDataCore implements IEditTableDataCore {
    private static final String EDIT_TABLE_DATA = "EDIT_TABLE_DATA";

    /**
     * 
     * Title: class
     * 
     * Description: The Class EditTableDataWindowDetails.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    protected class EditTableDataWindowDetails implements IWindowDetail {

        @Override
        public String getTitle() {

            return getWindowTitle();
        }

        @Override
        public String getShortTitle() {

            return getTable().getDisplayName();
        }

        @Override
        public String getUniqueID() {

            return EDIT_TABLE_DATA + getTitle();
        }

        @Override
        public String getIcon() {

            return null;
        }

        @Override
        public boolean isCloseable() {
            return true;
        }

    }
}
