/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */


package com.huawei.mppdbide.view.ui.debug;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

/**
 * Title: ListStructuredContentProvider for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2020-12-31
 */
public class ListStructuredContentProvider implements IStructuredContentProvider {
    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            return ((List<?>) inputElement).toArray();
        }
        return new Object[0];
    }
}
