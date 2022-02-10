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

package org.opengauss.mppdbide.view.component.grid;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.component.DSGridStateMachine;
import org.opengauss.mppdbide.view.component.grid.core.DataGrid;
import org.opengauss.mppdbide.view.component.grid.core.DataText;
import org.opengauss.mppdbide.view.utils.InitListener;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridSearchArea.
 *
 * @since 3.0.0
 */
public class GridSearchArea {

    /**
     * The Constant REGEX_FOR_NULL_VALUE.
     */
    protected static final String REGEX_FOR_NULL_VALUE = "^$";

    private Composite searchArea;
    private Text txtSearchStr;
    private DataGrid grid;
    private DataText text;
    private GridToolbar toolbar;
    private Button btnSearch;
    private Button btnClearSearchTxt;
    private Combo cmbSearchOpt;
    private boolean isExecutionPlanTab = false;

    /**
     * Sets the execution plan tab flag.
     */
    public void setExecutionPlanTabFlag() {
        this.isExecutionPlanTab = true;
    }

    /**
     * Creates the component.
     *
     * @param parent the parent
     * @param stateMachine the state machine
     */
    public void createComponent(Composite parent, DSGridStateMachine stateMachine) {
        this.searchArea = createComposite(parent);
        addItemSearchType(SEARCHOPTIONS.SRCH_CONTAINS);
        addItemSearchText();
        addItemSearchButton();
        addItemClearButton();
    }

    /**
     * Sets the grid.
     *
     * @param grid2 the new grid
     */
    public void setGrid(DataGrid grid2) {
        this.grid = grid2;
    }

    /**
     * Sets the text.
     *
     * @param text2 the new text
     */
    public void setText(DataText text2) {
        this.text = text2;
    }

    private Composite createComposite(Composite parent) {
        Composite newSearchComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(4, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        newSearchComposite.setLayout(layout);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(newSearchComposite);

        return newSearchComposite;
    }

    /**
     * Adds the item search type.
     *
     * @param defaultSearchOption the default search option
     */
    public void addItemSearchType(SEARCHOPTIONS defaultSearchOption) {
        this.cmbSearchOpt = new Combo(this.searchArea, SWT.READ_ONLY);
        String[] items = new String[] {SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName(),
            SEARCHOPTIONS.SRCH_EQUALS.getDisplayName(), SEARCHOPTIONS.SRCH_STARTS_WITH.getDisplayName(),
            SEARCHOPTIONS.SRCH_NULL.getDisplayName()};

        this.cmbSearchOpt.setItems(items);
        this.cmbSearchOpt.setText(SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName());
        this.cmbSearchOpt.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (cmbSearchOpt.getText().equals(SEARCHOPTIONS.SRCH_NULL.getDisplayName())) {
                    txtSearchStr.setText("");
                    txtSearchStr.setEnabled(false);
                } else {
                    txtSearchStr.setEnabled(true);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    /**
     * Adds the item search text.
     */
    public void addItemSearchText() {
        this.txtSearchStr = new Text(this.searchArea, SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL | SWT.NONE);
        this.txtSearchStr.setMessage(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_ITEM_TEXT));

        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        this.txtSearchStr.setLayoutData(gridData);
        this.txtSearchStr.addListener(SWT.MenuDetect, new InitListener());
        this.txtSearchStr.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.keyCode == SWT.CR || keyEvent.keyCode == SWT.KEYPAD_CR) {
                    triggerSearch(true);
                }
            }
        });
    }

    /**
     * Adds the item search button.
     */
    public void addItemSearchButton() {
        btnSearch = new Button(this.searchArea, SWT.PUSH);
        btnSearch.setImage(IconUtility.getIconImage(IiconPath.SEARCH_TEXT, getClass()));
        btnSearch.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_GRID));
        btnSearch.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                triggerSearch(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Ignore
            }
        });
    }

    /**
     * Trigger search.
     *
     * @param doSearch the do search
     */
    protected void triggerSearch(boolean doSearch) {
        String searchStr = this.txtSearchStr.getText();
        matcherSearch(searchStr, doSearch);
    }

    /**
     * Trigger search.
     *
     * @param valueStr the value str
     * @param doSearch the do search
     */
    protected void triggerSearch(String valueStr, boolean doSearch) {
        matcherSearch(valueStr, doSearch);
    }

    /**
     * Matcher search.
     *
     * @param targetValueStrParam the target value str param
     * @param doSearch the do search
     */
    protected void matcherSearch(String targetValueStrParam, boolean doSearch) {
        String targetValueStr = targetValueStrParam;
        String optStr = this.cmbSearchOpt.getText();
        SEARCHOPTIONS searchOpt = null;

        if (optStr.equals(SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName())) {
            searchOpt = SEARCHOPTIONS.SRCH_CONTAINS;
        } else if (optStr.equals(SEARCHOPTIONS.SRCH_EQUALS.getDisplayName())) {
            searchOpt = SEARCHOPTIONS.SRCH_EQUALS;
        } else if (optStr.equals(SEARCHOPTIONS.SRCH_STARTS_WITH.getDisplayName())) {
            searchOpt = SEARCHOPTIONS.SRCH_STARTS_WITH;
        } else if (optStr.equals(SEARCHOPTIONS.SRCH_REGEX.getDisplayName())) {
            searchOpt = SEARCHOPTIONS.SRCH_REGEX;
        } else {
            if (doSearch) {
                targetValueStr = REGEX_FOR_NULL_VALUE;
            }

            searchOpt = SEARCHOPTIONS.SRCH_NULL;
        }
        if (null != this.text) {
            if (this.toolbar.isShowGridOrShowTextSelect()) {
                this.grid.doSearch(targetValueStr, searchOpt);
            } else {
                doSearch(targetValueStr, searchOpt);
            }
        } else {
            this.grid.doSearch(targetValueStr, searchOpt);
        }
    }

    /**
     * Do search.
     *
     * @param searchText the search text
     * @param searchOptions the search options
     */
    public void doSearch(String searchText, SEARCHOPTIONS searchOptions) {
        search(searchText, searchOptions);
        // Scroll bar is unavailable in search mode
        this.text.setSearchStringForScrollBar(searchText);
        // In search mode, the refresh button is grayed out
        this.text.enableDisableRefreshButton(searchText);
    }

    private void search(String searchText, SEARCHOPTIONS searchOptions) {
        switch (searchOptions) {
            case SRCH_CONTAINS: {
                this.text.searchContains(searchText);
                break;
            }
            case SRCH_EQUALS: {
                this.text.searchEquals(searchText);
                break;
            }
            case SRCH_STARTS_WITH: {
                this.text.searchStartsWith(searchText);
                break;
            }
            case SRCH_NULL: {
                this.text.searchReg(searchText);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Adds the item clear button.
     */
    public void addItemClearButton() {
        btnClearSearchTxt = new Button(this.searchArea, SWT.PUSH);
        btnClearSearchTxt.setImage(IconUtility.getIconImage(IiconPath.CLEAR_SEARCH_TEXT, getClass()));
        btnClearSearchTxt.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.CLEAR_SEARCH_GRID));
        btnClearSearchTxt.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!txtSearchStr.getEnabled()) {
                    txtSearchStr.setEnabled(true);
                }
                GridSearchArea.this.txtSearchStr.setText("");
                cmbSearchOpt.setText(SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName());
                triggerSearch(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Ignore
            }
        });

    }

    /**
     * Do hide search area.
     */
    public void doHideSearchArea() {
        GridUIUtils.toggleCompositeSectionVisibility(this.searchArea, true, null, this.isExecutionPlanTab);
    }

    /**
     * Do show search area.
     */
    public void doShowSearchArea() {
        GridUIUtils.toggleCompositeSectionVisibility(this.searchArea, false, this.txtSearchStr,
                this.isExecutionPlanTab);
    }

    /**
     * Checks if is search area visible.
     *
     * @return true, if is search area visible
     */
    public boolean isSearchAreaVisible() {
        return this.searchArea.isVisible();
    }

    /**
     * Sets the focus.
     */
    public void setFocus() {
        this.txtSearchStr.setFocus();
    }

    /**
     * Pre destroy.
     */
    public void preDestroy() {
        this.grid = null;
    }

    /**
     * Gets the txt search str.
     *
     * @return the txt search str
     */
    public Text getTxtSearchStr() {
        return txtSearchStr;
    }

    /**
     * Sets the txt search str.
     *
     * @param txtSearchStr the new txt search str
     */
    public void setTxtSearchStr(Text txtSearchStr) {
        this.txtSearchStr = txtSearchStr;
    }

    /**
     * Sets the toolbar.
     *
     * @param toolbar the new toolbar
     */
    public void setToolbar(GridToolbar toolbar) {
        this.toolbar = toolbar;
    }

    /**
     * Gets the cmb search opt.
     *
     * @return the cmb search opt
     */
    public Combo getCmbSearchOpt() {
        return cmbSearchOpt;
    }

    /**
     * Sets the cmb search opt.
     *
     * @param cmbSearchOpt the new cmb search opt
     */
    public void setCmbSearchOpt(Combo cmbSearchOpt) {
        this.cmbSearchOpt = cmbSearchOpt;
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        this.searchArea = null;
        this.cmbSearchOpt = null;
        this.grid = null;
        this.txtSearchStr = null;
        this.btnClearSearchTxt = null;
        this.btnSearch = null;
    }

    /**
     * Updata but status on data text search.
     *
     * @param status the status
     */
    public void updataButStatusOnDataTextSearch(boolean status) {
        this.cmbSearchOpt.setEnabled(status);
        this.txtSearchStr.setEnabled(status);
        this.btnClearSearchTxt.setEnabled(status);
        this.btnSearch.setEnabled(status);
    }

    /**
     * Checks if is search but enable.
     *
     * @return true, if is search but enable
     */
    public boolean isSearchButEnable() {
        if (null != btnSearch && btnSearch.isEnabled()) {
            return true;
        }
        return false;
    }
}
