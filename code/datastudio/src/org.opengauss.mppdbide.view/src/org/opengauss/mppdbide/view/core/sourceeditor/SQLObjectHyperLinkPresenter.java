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

package org.opengauss.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.MultipleHyperlinkPresenter;
import org.eclipse.swt.graphics.RGB;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLObjectHyperLinkPresenter.
 *
 * @since 3.0.0
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
