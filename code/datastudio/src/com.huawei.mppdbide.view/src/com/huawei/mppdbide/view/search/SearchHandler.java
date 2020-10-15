/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.search;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.bl.search.SearchObjectEnum;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.search.SearchObjCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SearchHandler {

    /**
     * Execute.
     *
     * @param parent the parent
     */
    @Execute
    public void execute(Composite parent) {

        if (getSearchWindow() == null) {
            SearchObjCore core = new SearchObjCore();
            core.addObserver(UIElement.getInstance().getObjectBrowserModel());
            core.setSearchStatus(SearchObjectEnum.SEARCH_INI);
            IWindowDetail windowDetail = new SearchWindowDetails();
            UIElement.getInstance().getSearchObjectWindow(windowDetail, core);
        } else {
            UIElement.getInstance().bringSearchWindowOnTop();
            getSearchWindow();
        }

    }

    private SearchWindow getSearchWindow() {
        Object obj = UIElement.getInstance().getSearchWindowPartObject();

        if (!(obj instanceof SearchWindow)) {
            return null;
        }

        SearchWindow window = (SearchWindow) obj;
        window.loadConnectionDetails();
        window.loadObjectBrowserSelectionDetails();
        return window;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return IHandlerUtilities.isSearchOptionEnable();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SearchWindowDetails.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class SearchWindowDetails implements IWindowDetail {

        @Override
        public String getTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_WINDOW_LBL);
        }

        @Override
        public String getUniqueID() {
            return UIConstants.UI_PART_SEARCHWINDOW_ID;
        }

        @Override
        public String getIcon() {
            return IconUtility.getIconImageUri(IiconPath.ICO_SEARCH, getClass());
        }

        @Override
        public String getShortTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_WINDOW_LBL);
        }

        @Override
        public boolean isCloseable() {
            return true;
        }
    }
}
