/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.MultipleHyperlinkPresenter;
import org.eclipse.swt.graphics.RGB;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLObjectHyperLinkPresenter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SQLObjectHyperLinkPresenter extends MultipleHyperlinkPresenter {

    /**
     * Instantiates a new SQL object hyper link presenter.
     */
    public SQLObjectHyperLinkPresenter() {
        super(new RGB(0, 0, 255));
    }

    /**
     * Show hyperlinks.
     *
     * @param hyperlinks the hyperlinks
     */
    @Override
    public void showHyperlinks(IHyperlink[] hyperlinks) {
        super.showHyperlinks(hyperlinks);
    }

    /**
     * Can show multiple hyperlinks.
     *
     * @return true, if successful
     */
    @Override
    public boolean canShowMultipleHyperlinks() {
        return true;
    }
}
