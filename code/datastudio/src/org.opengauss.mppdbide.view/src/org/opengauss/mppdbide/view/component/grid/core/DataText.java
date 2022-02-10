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

package org.opengauss.mppdbide.view.component.grid.core;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.presentation.edittabledata.DSResultSetGridDataRow;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.utils.observer.DSEvent;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.utils.observer.IDSGridUIListenable;
import org.opengauss.mppdbide.utils.observer.IDSListener;
import org.opengauss.mppdbide.view.component.DSGridStateMachine;
import org.opengauss.mppdbide.view.component.IGridUIPreference;
import org.opengauss.mppdbide.view.component.grid.DSGridComponent;
import org.opengauss.mppdbide.view.component.grid.GridSearchArea;
import org.opengauss.mppdbide.view.component.grid.GridUIUtils;
import org.opengauss.mppdbide.view.component.grid.TextScrollEventDataLoadListener;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.ui.dialog.TextCellDialog;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.DateFormatUtils;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class DataText.
 *
 * @since 3.0.0
 */
public class DataText {

    /**
     * The Constant FIRST_ROW_LINE.
     */
    public static final int FIRST_ROW_LINE = 2;

    /**
     * The Constant FOUR_MB.
     */
    private TextScrollEventDataLoadListener scrollDataLoadListener;
    private String cellText;
    private int cellTextColumnNumber;
    private int cellTextLineNumber;
    private String[] cloNames;
    private IDSGridColumnProvider colPro;
    private List<IDSGridDataRow> allRows;
    private int maxColumnSize;
    private int[] colWidths;
    private StyledText styledText;
    private StyledText styledSearchText;
    private int replaceTab;

    private int horOffsetBegin;
    private int horOffsetEnd;
    private int lineOffset;
    private int offset = -1;
    private Composite textParent;
    private Composite searchParent;
    private IGridUIPreference uiPref;

    /**
     * The event table.
     */
    protected DSEventTable eventTable;
    private DSGridStateMachine stateMachine;

    /**
     * The data provider.
     */
    protected IDSGridDataProvider dataProvider;
    private boolean searchHideOrShowFlag;
    private StyleRange curLineRange;
    private DSGridComponent gridComponent;
    private MenuItem searchItem;
    private MenuItem copyItem;
    private MenuItem refreshItem;

    private MenuItem searchItemForSearchText;

    private boolean showOrHiderefFlag;
    private String defaultEncod;
    private String selEncod;
    private boolean endLineFlag;
    private boolean refreshFlag;
    private boolean endodingFlag;
    private List<String> styleTextList = new ArrayList<String>();
    private List<Integer> searchMatchLineList;
    private GridSearchArea searchArea;
    private boolean searchStatus;
    private boolean successWorkerFlag;
    private boolean initDataTextFlag;

    private boolean partloaded;
    private int loadedRowCnt;

    private List<DSResultSetGridDataRow> cursorGridRowList;
    private int scrolledRow = 0;

    /**
     * Instantiates a new data text.
     *
     * @param uiPref the ui pref
     * @param dataProvider the data provider
     * @param eventTable the event table
     * @param stateMachine the state machine
     * @param gridComponent the grid component
     */
    public DataText(IGridUIPreference uiPref, IDSGridDataProvider dataProvider, DSEventTable eventTable,
            DSGridStateMachine stateMachine, DSGridComponent gridComponent) {
        this.uiPref = uiPref;
        this.eventTable = eventTable;
        this.stateMachine = stateMachine;
        this.dataProvider = dataProvider;
        this.gridComponent = gridComponent;
        this.replaceTab = uiPref.replaceTab();
    }

    /**
     * Creates the component.
     *
     * @param parent the parent
     */

    public void createComponent(Composite parent) {
        this.textParent = parent;
        GridUIUtils.createHorizontalLine(parent);
        parent.setLayout(new GridLayout(1, false));
        styledText = new StyledText(parent, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        styledText.setBlockSelection(false);
        styledText.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
        styledText.setMargins(4, 4, 4, 4);
        styledText.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
        styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
        getEncoding();
        styledText.setMenu(createEditPopup());
        styledText.addCaretListener(event -> onCursorChange(event.caretOffset));
        addListenerMourseClick();
        addListenerKeyClick();
        loadTextData();
    }

    /**
     * set the begin show index line
     *
     * @param index then row of styletext
     */
    public void setTopIndex(int index) {
        scrolledRow = index;
        styledText.setTopIndex(scrolledRow + 2);
    }

    /**
     * return current text mode scrolled row
     *
     * @return int the scrolled row
     */
    public int getTopIndex() {
        return scrolledRow;
    }

    /**
     * update scrolled line info
     */
    public void updateScrolledInfo() {
        scrolledRow = styledText.getTopIndex() - 2;
    }

    private void addListenerKeyClick() {

        styledText.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent event) {

            }

            @Override
            public void keyPressed(KeyEvent event) {
                if (event.stateMask == SWT.SHIFT && event.keyCode == SWT.F9) {
                    styledText.getMenu().setVisible(true);
                }
            }
        });
    }

    private void addListenerMourseClick() {
        styledText.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent event) {

            }

            @Override
            public void mouseDown(MouseEvent event) {

            }

            @Override
            public void mouseUp(MouseEvent event) {
                boolean selFlag = isStrEmpty(styledText.getSelectionText());
                int[] selTextRange = styledText.getSelectionRanges();
                if (!selFlag && selTextRange.length == 2 && searchArea.isSearchButEnable()) {
                    searchItem.setEnabled(true);
                } else {
                    searchItem.setEnabled(false);
                }
                if (isShowOrHiderefFlag()) {
                    if (gridComponent.isRefreshButEnable()) {
                        refreshItem.setEnabled(true);
                    } else {
                        refreshItem.setEnabled(false);
                    }
                }
                if (event.button == 1 && selFlag) {
                    if (checkCursorDataDailog() && validateCellText()) {
                        showCursorDataDailog(cursorGridRowList);
                    } else {
                        showDataTextCellDialog();
                    }
                }
                copyItem.setEnabled(IHandlerUtilities.getExportDataSelectionOptions());
            }
        });
    }

    private boolean checkCursorDataDailog() {
        IDSGridDataRow dataRowObject = null;
        for (int i = 0; i < allRows.size(); i++) {
            dataRowObject = allRows.get(i);
            Object[] row = dataRowObject.getValues();
            if (cellTextColumnNumber <= row.length && row[cellTextColumnNumber] != null
                    && row[cellTextColumnNumber] instanceof List<?>) {
                this.cursorGridRowList = (List<DSResultSetGridDataRow>) row[cellTextColumnNumber];
                return true;
            }
        }
        return false;
    }

    private void showCursorDataDailog(List<DSResultSetGridDataRow> cursorGridRowList) {
        DSCursorResultSetTable cursorResultSetTable = new DSCursorResultSetTable();
        cursorResultSetTable.activateCell(null, cursorGridRowList);
    }

    private void showDataTextCellDialog() {
        Shell shell = Display.getDefault().getActiveShell();
        TextCellDialog dialog = new TextCellDialog(maxColumnSize, shell);
        if (null != cellText) {
            showDataTextDialog(dialog);
        }
    }

    private void showDataTextDialog(TextCellDialog dialog) {
        if (validateCellText()) {
            dialog.setTextValue(cellText);
            dialog.open();
        } else if (validateCellDataLength()) {
            Clipboard cb = new Clipboard(Display.getDefault());
            Object[] objArray = new Object[] {cellText};
            Transfer[] transArray = new Transfer[] {TextTransfer.getInstance()};
            cb.setContents(objArray, transArray);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_TEXT_CELL_INVISIBLE_VALUE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_TEXT_CELL_INVISIBLE_VALUE_BODY));
        }
    }

    private boolean validateCellDataLength() {
        return cellText.length() > MPPDBIDEConstants.TEXT_MODE_CELL_DIALOG_MAXINUM;
    }

    private boolean validateCellText() {
        return isSpecialCell()
                || (cellText.length() > maxColumnSize
                        && cellText.length() <= MPPDBIDEConstants.TEXT_MODE_CELL_DIALOG_MAXINUM)
                || (MPPDBIDEConstants.BLOB
                        .equals(dataProvider.getColumnDataProvider().getColumnDataTypeName(cellTextColumnNumber))
                        && cellText.length() <= maxColumnSize && cellTextLineNumber > 1)
                || (MPPDBIDEConstants.BYTEA
                        .equals(dataProvider.getColumnDataProvider().getColumnDataTypeName(cellTextColumnNumber))
                        && cellText.length() <= maxColumnSize && cellTextLineNumber > 1)
                || cellText.length() <= maxColumnSize && cellTextLineNumber > 1
                        && MPPDBIDEConstants.CURSOR_WATERMARK.equals(cellText);
    }

    /**
     * Reset text data.
     *
     * @param provider the provider
     */

    public void resetTextData(IDSGridDataProvider provider) {
        this.setDataProvider(provider);
        this.getEncoding();
        if (validateForRefresh()) {
            updateStatusBarForDataText();
            setDataTextFocusAndLoadData();
        } else if (validateForEncoding()) {
            setDataTextFocusAndLoadData();
        } else {
            setDataTextFocusAndLoadData();
            scrollDataLoadListener.setDataProvider(provider);
            updateStatusBarForPartLoaded();
        }
    }

    private void updateStatusBarForPartLoaded() {
        if (partloaded) {
            gridComponent.updateGridStatusBarForDataText(this.partloaded, this.loadedRowCnt);
        }
    }

    private void updateStatusBarForDataText() {
        if (!gridComponent.isShowGridOrShowTextSelect()) {
            gridComponent.updateGridStatusBarForDataText(this.partloaded, this.loadedRowCnt);
        }
    }

    private void setDataTextFocusAndLoadData() {
        boolean searchStr = isStrEmpty(gridComponent.getSearchTxtString());
        if (searchStr) {
            this.styledText.setFocus();
        }
        loadTextData();
    }

    /**
     * Configure load on scroll.
     *
     * @param eventTbl the event tbl
     */
    public void configureLoadOnScroll(final DSEventTable eventTbl) {
        preventConcurrentDataLoad(eventTbl);
        this.scrollDataLoadListener = new TextScrollEventDataLoadListener(this.styledText, this.dataProvider, eventTbl,
                this.stateMachine, this);
        // Add a listener to the text scrollbar
        this.styledText.getVerticalBar().addListener(SWT.Selection, scrollDataLoadListener);
        this.styledText.addKeyListener(scrollDataLoadListener);
    }

    private void preventConcurrentDataLoad(DSEventTable eventTbl) {
        eventTbl.hook(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_CHANGED, new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {
                DataText.this.scrollDataLoadListener.resetLoadingStatus();
            }
        });
        eventTbl.hook(IDSGridUIListenable.LISTEN_TYPE_ON_ERROR, new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {
                DataText.this.scrollDataLoadListener.resetLoadingStatus();
            }
        });
    }

    /**
     * Do hide text.
     *
     * @param composite the composite
     */
    public void doHideText(Composite composite) {
        GridUIUtils.toggleCompositeSectionVisibility(composite, true, null, false);
    }

    /**
     * Do show text.
     *
     * @param composite the composite
     */
    public void doShowText(Composite composite) {
        GridUIUtils.toggleCompositeSectionVisibility(composite, false, null, false);
    }

    /**
     * Load text data.
     */
    private void loadTextData() {
        String progressBarLabel;
        if (validateForRefresh()) {
            progressBarLabel = getProgressBarLabel(IMessagesConstants.RESULT_WINDOW_SQL_REFRESH_EXECUTE);
        } else if (validateForEncoding()) {
            progressBarLabel = getProgressBarLabel(IMessagesConstants.RESULT_WINDOW_SQL_ENCODING_EXECUTE);
        } else {
            progressBarLabel = getProgressBarLabel(IMessagesConstants.RESULT_WINDOW_SQL_LOADING_EXECUTE);
        }
        this.setSuccessWorkerFlag(false);
        setScrollStatus();
        ReloadTextDataWorker initJob = new ReloadTextDataWorker(progressBarLabel);
        activateStatusbar();
        initJob.schedule();
    }

    private void setScrollStatus() {
        if (isInitDataTextFlag()) {
            setScrollStatusOnInitOrLoad(true);
        } else {
            gridComponent.updataButStatusOnLoadDataText(false);
            setScrollStatusOnInitOrLoad(true);
        }
    }

    private boolean validateForEncoding() {
        return gridComponent.getDataText().isEncodingFlag();
    }

    private boolean validateForRefresh() {
        return gridComponent.getDataText().isRefreshFlag();
    }

    /**
     * Replace specal char.
     *
     * @param objRow the obj row
     * @param num the num
     * @return the string
     */
    private String replaceSpecalChar(Object[] objRow, int num) {
        if (MPPDBIDEConstants.BLOB.equals(dataProvider.getColumnDataProvider().getColumnDataTypeName(num))) {
            if (objRow[num] != null) {
                return MPPDBIDEConstants.BLOB_WATERMARK;
            }
            return "";
        }
        if (Types.OTHER == (dataProvider.getColumnDataProvider().getColumnDatatype(num))) {
            if (objRow[num] != null && objRow[num] instanceof List) {
                this.cellText = MPPDBIDEConstants.CURSOR_WATERMARK;
                return MPPDBIDEConstants.CURSOR_WATERMARK;
            }
        }
        if (MPPDBIDEConstants.BYTEA.equals(dataProvider.getColumnDataProvider().getColumnDataTypeName(num))) {
            if (objRow[num] != null) {
                return MPPDBIDEConstants.BYTEA_WATERMARK;
            }
            return "";
        }
        if (Types.TIMESTAMP == dataProvider.getColumnDataProvider().getColumnDatatype(num)
                || Types.TIMESTAMP_WITH_TIMEZONE == dataProvider.getColumnDataProvider().getColumnDatatype(num)) {
            if (objRow[num] != null && objRow[num] instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) objRow[num];
                return DateFormatUtils.handleTimeStampValues(timestamp);
            }
        }
        if (Types.TIME == dataProvider.getColumnDataProvider().getColumnDatatype(num)
                || Types.TIME_WITH_TIMEZONE == dataProvider.getColumnDataProvider().getColumnDatatype(num)) {
            if (objRow[num] != null && objRow[num] instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) objRow[num];
                return DateFormatUtils.handleTimeValues(timestamp);
            }
        }
        Object str = null;
        str = getEncodedValue(objRow, num);
        String displayString = this.isStrEmpty(str) ? "" : String.valueOf(str);
        Pattern pattern = Pattern.compile("\t");
        Matcher matcher = pattern.matcher(displayString);
        displayString = replaceOnTab(displayString, matcher);
        Pattern p1 = Pattern.compile(System.lineSeparator());
        Matcher m1 = p1.matcher(displayString);
        displayString = replaceLineSeperator(displayString, m1);
        return displayString;
    }

    private String replaceLineSeperator(String displayStringParam, Matcher m1) {
        String displayString = displayStringParam;
        if (m1.find()) {
            displayString = m1.replaceAll("");
        }
        return displayString;
    }

    private String replaceOnTab(String displayStringParam, Matcher matcher) {
        String displayString = displayStringParam;
        if (matcher.find()) {
            displayString = matcher.replaceAll(this.getTabStr(replaceTab));
        }
        return displayString;
    }

    private Object getEncodedValue(Object[] objRow, int num) {
        Object str;
        if (validateSelectedEncoding()) {
            str = getEncodedValue(objRow[num], defaultEncod);
        } else {
            str = getEncodedValue(objRow[num], selEncod);
        }
        return str;
    }

    private boolean validateSelectedEncoding() {
        return null == selEncod || MPPDBIDEConstants.SPACE_CHAR.equals(selEncod);
    }

    /**
     * Show row.
     *
     * @param dataRow the data row
     * @param rowSB the row SB
     */
    private void showRow(List<IDSGridDataRow> dataRow, StringBuilder rowSB) {
        if (validateDataRow(dataRow)) {
            setRowLength(dataRow, rowSB);
        }
        rowSB.setLength(rowSB.length() - System.lineSeparator().length());
    }

    private void setRowLength(List<IDSGridDataRow> dataRow, StringBuilder rowSB) {
        for (int cnt = 0; cnt < dataRow.size(); cnt++) {
            IDSGridDataRow row = dataRow.get(cnt);

            getTruncatedString(rowSB, row);
            rowSB.append("|");
            rowSB.append(System.lineSeparator());

            // maximal characters validation

            if (validateRowLength(rowSB)) {
                partloaded = true;
                loadedRowCnt = cnt + 1;
                break;
            }
        }
    }

    private boolean validateRowLength(StringBuilder rowSB) {
        return rowSB.length() > MPPDBIDEConstants.TEXT_MODE_LOAD_MAXIMUM;
    }

    private boolean validateDataRow(List<IDSGridDataRow> dataRow) {
        return null != dataRow && dataRow.size() != 0;
    }

    private void getTruncatedString(StringBuilder rowSB, IDSGridDataRow row) {
        Object[] obj = null;
        for (int elmnt = 0; elmnt < cloNames.length; elmnt++) {
            appendSeperator(rowSB, elmnt);
            obj = row.getValues();
            String displayString = replaceSpecalChar(obj, elmnt);

            displayString = truncateString(elmnt, displayString);
            rowSB.append(displayString);

            appendEmptySpace(rowSB, elmnt, displayString);
        }
    }

    private String truncateString(int count, String displayStringParam) {
        String displayString = displayStringParam;
        if (displayString.length() >= colWidths[count] - 1) {
            displayString = GridUIUtils.truncateString(displayString, colWidths[count] - 1);
        }
        return displayString;
    }

    private void appendSeperator(StringBuilder rowSB, int count) {
        if (count > 0) {
            rowSB.append("|");
        }
    }

    private void appendEmptySpace(StringBuilder rowSB, int item, String displayString) {
        for (int ele = colWidths[item] - displayString.length(); ele > 0; ele--) {
            rowSB.append(" ");
        }
    }

    private void getStyledTextList() {

        this.setSearchHideOrShowFlag(false);
        styleTextList.clear();
        for (int ele = 0; ele < styledText.getLineCount(); ele++) {
            styleTextList.add(styledText.getLine(ele));
        }
    }

    /**
     * Gets the selected row position.
     *
     * @return the selected row position
     */
    public int getSelectedRowPosition() {
        return this.styledText.getSelectionCount();
    }

    /**
     * Gets the data provider.
     *
     * @return the data provider
     */
    public IDSGridDataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    public void setDataProvider(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.allRows = dataProvider.getAllFetchedRows();
        this.maxColumnSize = uiPref.getColumnWidth();
        this.allRows = dataProvider.getAllFetchedRows();
        this.colPro = dataProvider.getColumnDataProvider();
        this.cloNames = colPro.getColumnNames();
    }

    /**
     * Do copy.
     */
    public void doCopy() {
        if (this.textParent.isVisible()) {
            this.styledText.copy();
        } else {
            styledSearchText.copy();
        }

    }

    /**
     * Do search.
     *
     * @param searchText the search text
     * @param searchOptions the search options
     */

    public void setSearchStringForScrollBar(String searchText) {
        if (null != this.scrollDataLoadListener) {
            this.scrollDataLoadListener.setSearchString(searchText);
        }
    }

    /**
     * Enable disable refresh button.
     *
     * @param searchText the search text
     */
    public void enableDisableRefreshButton(String searchText) {
        if (searchText.length() > 0) {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE, null));
        } else {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED, null));
        }
    }

    /**
     * Gets the text parent.
     *
     * @return the text parent
     */
    public Composite getTextParent() {
        return textParent;
    }

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(Composite parent) {
        this.textParent = parent;
    }

    /**
     * Gets the search parent.
     *
     * @return the search parent
     */
    public Composite getSearchParent() {
        return searchParent;
    }

    /**
     * Sets the search parent.
     *
     * @param parent the new search parent
     */
    public void setSearchParent(Composite parent) {
        this.searchParent = parent;
    }

    private boolean isStrEmpty(Object searchText) {
        return null == searchText || String.valueOf(searchText).isEmpty();
    }

    /**
     * Search reg.
     *
     * @param searText the sear text
     */
    public void searchReg(String searText) {
        this.getStyledTextList();
        this.changeButStatus(false);
        this.setSearchStatus(true);
        this.setSuccessWorkerFlag(false);
        DataTextSearchNullWorker nullJobWorker = new DataTextSearchNullWorker(
                getProgressBarLabel(IMessagesConstants.RESULT_WINDOW_SQL_SEARCH_EXECUTE));
        activateStatusbar();
        nullJobWorker.schedule();
    }

    private void activateStatusbar() {
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        StatusMessage statMssage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        StatusMessageList.getInstance().push(statMssage);
        if (bttmStatusBar != null) {
            bttmStatusBar.activateStatusbar();
        }
    }

    /**
     * Search contains.
     *
     * @param searchText the search text
     */
    public void searchContains(String searchText) {
        String quotedStr = isStrEmpty(searchText) ? "" : "(?i)(" + Pattern.quote(searchText) + ")";
        search(quotedStr);
    }

    /**
     * Search equals.
     *
     * @param searchText the search text
     */
    public void searchEquals(String searchText) {
        String quotedStr = isStrEmpty(searchText) ? "" : ("(?i)(^" + Pattern.quote(searchText) + "$)");
        search(quotedStr);
    }

    /**
     * Search starts with.
     *
     * @param searchText the search text
     */
    public void searchStartsWith(String searchText) {
        String quotedStr = isStrEmpty(searchText) ? "" : ("(?i)(^" + Pattern.quote(searchText) + ')');
        search(quotedStr);
    }

    /*
     * Cancel search
     */
    private void searchClear() {
        this.setSearchHideOrShowFlag(false);
        this.gridComponent.hideStatusBar();
        this.doHideText(this.searchParent);
        this.doShowText(this.textParent);
        this.gridComponent.showStatusBar();
        if (this.offset != -1) {
            this.setHigtLight(lineOffset + horOffsetBegin, horOffsetEnd - horOffsetBegin - 1, SWT.COLOR_WHITE);
        }
    }

    private void search(String quotedStr) {
        if ("".equals(quotedStr)) {
            searchClear();
            return;
        }
        changeButStatus(false);
        getStyledTextList();
        this.setSearchStatus(true);
        this.setSuccessWorkerFlag(false);
        DataTextSearchWorker job = new DataTextSearchWorker(
                getProgressBarLabel(IMessagesConstants.RESULT_WINDOW_SQL_SEARCH_EXECUTE), quotedStr);
        activateStatusbar();
        job.schedule();
    }

    /**
     * Creates the search component.
     */
    public void createSearchComponent() {
        GridUIUtils.createHorizontalLine(searchParent);
        searchParent.setLayout(new GridLayout(1, false));
        styledSearchText = new StyledText(searchParent, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        styledSearchText.setBlockSelection(false);
        styledSearchText.setCursor(searchParent.getDisplay().getSystemCursor(SWT.CURSOR_IBEAM));
        styledSearchText.setMargins(4, 4, 4, 4);
        styledSearchText.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
        styledSearchText.setLayoutData(new GridData(GridData.FILL_BOTH));
        styledSearchText.setMenu(createEditPopupForSearchText());
        styledSearchText.addCaretListener(event -> onCursorChange(event.caretOffset));
        addListenerMourseClickForSearchText();
        addListenerKeyClickForSearchText();
    }

    private void addListenerKeyClickForSearchText() {

        styledSearchText.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent event) {

            }

            @Override
            public void keyPressed(KeyEvent event) {
                if (event.stateMask == SWT.SHIFT && event.keyCode == SWT.F9) {
                    styledSearchText.getMenu().setVisible(true);
                }
            }
        });
    }

    private void addListenerMourseClickForSearchText() {
        styledSearchText.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent event) {
            }

            @Override
            public void mouseDown(MouseEvent event) {
            }

            @Override
            public void mouseUp(MouseEvent event) {
                boolean selFlag = isStrEmpty(styledSearchText.getSelectionText());
                int[] selTextRange = styledSearchText.getSelectionRanges();
                if (!selFlag && selTextRange.length == 2 && searchArea.isSearchButEnable()) {
                    searchItemForSearchText.setEnabled(true);
                } else {
                    searchItemForSearchText.setEnabled(false);
                }
                if (event.button == 1 && selFlag) {
                    showDataTextCellDialog();
                }
            }
        });
    }

    private Menu createEditPopupForSearchText() {
        Menu popMenu = new Menu(this.searchParent);
        MenuItem copyItemForSearchText = new MenuItem(popMenu, SWT.PUSH);
        copyItemForSearchText.setText(MessageConfigLoader.getProperty(IMessagesConstants.COPY_RESULT_WINDOW_CONTENTS));
        searchItemForSearchText = new MenuItem(popMenu, SWT.PUSH);
        searchItemForSearchText.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_GRID));
        searchItemForSearchText.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (gridComponent.validateSearchOption()) {
                    gridComponent.getTriggerSearch("", true);
                } else {
                    String seleText = styledSearchText.getSelectionText();
                    gridComponent.setTxtSeatrchText(seleText);
                    gridComponent.getTriggerSearch(seleText, true);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {

            }
        });

        copyItemForSearchText.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                doCopy();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {

            }
        });
        return popMenu;
    }

    /**
     * Checks if is search hide or show flag.
     *
     * @return true, if is search hide or show flag
     */
    public boolean isSearchHideOrShowFlag() {
        return searchHideOrShowFlag;
    }

    /**
     * Sets the search hide or show flag.
     *
     * @param searchHideOrShowFlag the new search hide or show flag
     */
    public void setSearchHideOrShowFlag(boolean searchHideOrShowFlag) {
        this.searchHideOrShowFlag = searchHideOrShowFlag;
    }

    private String getTabStr(int spaceNum) {
        StringBuffer buf = new StringBuffer();
        for (int index = 0; index < spaceNum; ++index) {
            buf.append(" ");
        }
        String str = buf.toString();
        return str;
    }

    /**
     * On cursor change.
     *
     * @param off the off
     */
    private void onCursorChange(int off) {
        int columNum = 0;
        int lineNum = 0;
        this.offset = off;
        lineNum = getOffSetAtLine(off);
        int horizontalOffset = off - lineOffset;
        horOffsetBegin = 0;
        horOffsetEnd = 0;
        columNum = getColumnNumber(horizontalOffset);
        setTextCell(lineNum, columNum, lineOffset, horOffsetBegin, horOffsetEnd);
        // Highlight row
        setHighLightRow();

    }

    private void setHighLightRow() {
        if (this.textParent.isVisible()) {
            setHigtLight(lineOffset + horOffsetBegin, horOffsetEnd - horOffsetBegin - 1, SWT.COLOR_GRAY);
        }
    }

    private int getColumnNumber(int horizontalOffset) {
        int columNum = 0;
        for (int index = 0; index < colWidths.length; index++) {
            horOffsetBegin = horOffsetEnd;
            horOffsetEnd += colWidths[index] + 1;
            columNum = index;
            if (horizontalOffset < horOffsetEnd) {
                columNum = index;
                break;
            }
        }
        return columNum;
    }

    private int getOffSetAtLine(int off) {
        int lineNum;
        if (this.textParent.isVisible()) {
            lineNum = styledText.getLineAtOffset(off);
            lineOffset = styledText.getOffsetAtLine(lineNum);
        } else {
            lineNum = styledSearchText.getLineAtOffset(off);
            lineOffset = styledSearchText.getOffsetAtLine(lineNum);
        }
        return lineNum;
    }

    /**
     * Sets the text cell.
     *
     * @param lineCount the line count
     * @param coluCount the colu count
     * @param lineOffsetCount the line offset count
     * @param horOffsetBeginCount the hor offset begin count
     * @param horOffsetEndCount the hor offset end count
     */
    private void setTextCell(int lineCount, int coluCount, int lineOffsetCount, int horOffsetBeginCount,
            int horOffsetEndCount) {
        cellText = getCellText(lineCount, coluCount, lineOffsetCount, horOffsetBeginCount, horOffsetEndCount);
        cellTextColumnNumber = coluCount;
        cellTextLineNumber = lineCount;
    }

    private String getCellText(int lineCount, int coluCount, int lineOffsetCount, int horOffsetBeginCount,
            int horOffsetEndCount) {
        if (lineCount < FIRST_ROW_LINE) {
            return styledText.getText(lineOffsetCount + horOffsetBeginCount,
                    lineOffsetCount + horOffsetEndCount - FIRST_ROW_LINE);
        }

        if (this.textParent.isVisible()) {
            return getCellTextIfLineCountIsLess(lineCount, coluCount);
        } else {
            return getCellTextIfSearchLinesIsLess(lineCount, coluCount);
        }
    }

    private String getCellTextIfSearchLinesIsLess(int lineCount, int coluCount) {
        if (allRows.size() >= searchMatchLineList.size() && searchMatchLineList.size() > lineCount - FIRST_ROW_LINE) {
            return getCellTextForVisibleParent(lineCount, coluCount);
        }
        return null;
    }

    private String getCellTextIfLineCountIsLess(int lineCount, int coluCount) {
        if (allRows.size() > lineCount - FIRST_ROW_LINE) {
            return getCellTextForVisibleParent(lineCount, coluCount);

        }
        return null;
    }

    private String getCellTextForVisibleParent(int lineCount, int coluCount) {
        int fourMb = 4194304; // 4*1024*1024 MB
        IDSGridDataRow dataRow = allRows.get(lineCount - FIRST_ROW_LINE);
        if (MPPDBIDEConstants.BLOB.equals(dataProvider.getColumnDataProvider().getColumnDataTypeName(coluCount))) {
            if (dataRow instanceof IDSGridEditDataRow) {
                IDSGridEditDataRow dataEditRow = (IDSGridEditDataRow) dataRow;
                byte[] byteValue = (byte[]) dataEditRow.getOriginalValue(coluCount);
                if (byteValue != null) {
                    if (byteValue.length > fourMb) {
                        return MessageConfigLoader.getProperty(IMessagesConstants.FILE_SIZE_EXCEEDED_FOUR);
                    }
                    return DSUnstructuredDataConversionHelper.bytesToHex(byteValue);
                }
            }
            return null;
        }
        if (MPPDBIDEConstants.BYTEA.equals(dataProvider.getColumnDataProvider().getColumnDataTypeName(coluCount))) {
            if (dataRow instanceof IDSGridEditDataRow) {
                IDSGridEditDataRow dataEditRow = (IDSGridEditDataRow) dataRow;
                byte[] byteValue = (byte[]) dataEditRow.getOriginalValue(coluCount);
                if (byteValue != null) {
                    if (byteValue.length > fourMb) {
                        return MessageConfigLoader.getProperty(IMessagesConstants.FILE_SIZE_EXCEEDED_FOUR);
                    }
                    return DSUnstructuredDataConversionHelper.bytesToHexFormated(byteValue);
                }
            }
            return null;
        }
        if (dataRow instanceof IDSGridEditDataRow) {
            IDSGridEditDataRow dataEditRow = (IDSGridEditDataRow) dataRow;
            return String.valueOf(getEncodedValue(dataEditRow.getOriginalValue(coluCount), selEncod));
        } else if (dataRow instanceof DSResultSetGridDataRow) {
            Object[] rowObject = dataRow.getValues();
            if (rowObject[coluCount] != null && rowObject[coluCount] instanceof List<?>) {
                List<Object> gridObj = (List<Object>) rowObject[coluCount];
                if (gridObj.get(0) instanceof DSResultSetGridDataRow) {
                    return MPPDBIDEConstants.CURSOR_WATERMARK;
                }
            }
        } else {
            Object[] obj = dataRow.getValues();
            if (obj.length > coluCount) {
                return String.valueOf(getEncodedValue(obj[coluCount], selEncod));
            }
        }
        return null;
    }

    /**
     * Creates the edit popup.
     *
     * @return the menu
     */
    private Menu createEditPopup() {
        Menu popMenu = new Menu(this.textParent);
        copyItem = new MenuItem(popMenu, SWT.PUSH);
        copyItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.COPY_RESULT_WINDOW_CONTENTS));
        if (this.isShowOrHiderefFlag()) {

            refreshItem = new MenuItem(popMenu, SWT.PUSH);
            refreshItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.REFRESH_TABLE_TOOLTIP));
            refreshItem.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    gridComponent.refreshDataGrid();
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent arg0) {

                }
            });

        }
        searchItem = new MenuItem(popMenu, SWT.PUSH);
        searchItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_GRID));
        searchItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (gridComponent.validateSearchOption()) {
                    gridComponent.getTriggerSearch("", true);
                } else {
                    String seleText = styledText.getSelectionText();
                    gridComponent.setTxtSeatrchText(seleText);
                    gridComponent.getTriggerSearch(seleText, true);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {

            }
        });

        copyItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                doCopy();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {

            }
        });
        return popMenu;
    }

    /*
     * Get transcoded value
     */
    private Object getEncodedValue(Object value, String encoding) {
        if (value instanceof byte[]) {
            byte[] byteVal = (byte[]) value;
            try {
                return getValue(encoding, byteVal);
            } catch (UnsupportedEncodingException e) {
                // Ignore. nothing can be done here.
                MPPDBIDELoggerUtility.debug("Encoding failed");
            }
        }
        return value;
    }

    private Object getValue(String encoding, byte[] byteVal) throws UnsupportedEncodingException {
        if (null != encoding && !encoding.isEmpty()) {
            return new String(byteVal, encoding);
        }
        return new String(byteVal, Charset.defaultCharset());
    }

    private void setHigtLight(int start, int length, int style) {
        curLineRange = new StyleRange(start, length, null, Display.getCurrent().getSystemColor(style));
        StyleRange[] styleRange = new StyleRange[] {curLineRange};
        Display.getDefault().syncExec(() -> styledText.setStyleRanges(styleRange));
    }

    /**
     * Update text data.
     */
    public void updateTextData() {
        getEncoding();
        loadTextData();
    }

    /**
     * Sets the end line flag.
     *
     * @param endLineFlag the new end line flag
     */
    public void setEndLineFlag(boolean endLineFlag) {
        this.endLineFlag = endLineFlag;
    }

    /**
     * Gets the end line flag.
     *
     * @return the end line flag
     */
    public boolean getEndLineFlag() {
        return this.endLineFlag;
    }

    /**
     * Checks if is refresh flag.
     *
     * @return true, if is refresh flag
     */
    public boolean isRefreshFlag() {
        return refreshFlag;
    }

    /**
     * Sets the refresh flag.
     *
     * @param refreshFlag the new refresh flag
     */
    public void setRefreshFlag(boolean refreshFlag) {
        this.refreshFlag = refreshFlag;
    }

    /**
     * Checks if is encoding flag.
     *
     * @return true, if is encoding flag
     */
    public boolean isEncodingFlag() {
        return endodingFlag;
    }

    /**
     * Sets the endoding flag.
     *
     * @param endodingFlag the new endoding flag
     */
    public void setEndodingFlag(boolean endodingFlag) {
        this.endodingFlag = endodingFlag;
    }

    private boolean isSpecialCell() {
        if (this.isStrEmpty(cellText)) {
            return false;
        }
        String regEx = System.lineSeparator() + "|\r|\t";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(cellText);
        return matcher.find();
    }

    /**
     * Sets the sear area.
     *
     * @param area the new sear area
     */
    public void setSearArea(GridSearchArea area) {
        this.searchArea = area;
    }

    /**
     * Change but status.
     *
     * @param status the status
     */
    public void changeButStatus(boolean status) {
        searchArea.updataButStatusOnDataTextSearch(status);
    }

    private String getProgressBarLabel(String executeMessage) {
        Database database = this.dataProvider.getDatabse();
        SQLTerminal sqlTerminal = UIElement.getInstance().getSqlTerminalModel();
        String sqlTerminalPartLabel = "";

        database = setDataBaseinTerminal(database, sqlTerminal);

        sqlTerminalPartLabel = getPartLabel(sqlTerminal, sqlTerminalPartLabel);
        return ProgressBarLabelFormatter.getProgressLabelForTextModeLoading(getDatabaseName(database),
                getServerName(database), sqlTerminalPartLabel, executeMessage);
    }

    private String getPartLabel(SQLTerminal sqlTerminal, String sqlTerminalPartLabelParam) {
        String sqlTerminalPartLabel = sqlTerminalPartLabelParam;
        if (sqlTerminal != null) {
            sqlTerminalPartLabel = sqlTerminal.getPartLabel();
        }
        return sqlTerminalPartLabel;
    }

    private String getServerName(Database database) {
        return null == database ? "" : database.getServerName();
    }

    private String getDatabaseName(Database database) {
        return null == database ? "" : database.getName();
    }

    private Database setDataBaseinTerminal(Database databaseParam, SQLTerminal sqlTerminal) {
        Database database = databaseParam;
        if (database == null) {
            if (null != sqlTerminal) {
                database = sqlTerminal.getSelectedDatabase();
            }
        }
        return database;
    }

    /**
     * Checks if is search status.
     *
     * @return true, if is search status
     */
    public boolean isSearchStatus() {
        return searchStatus;
    }

    /**
     * Sets the search status.
     *
     * @param searchStatus the new search status
     */
    public void setSearchStatus(boolean searchStatus) {
        this.searchStatus = searchStatus;
    }

    /**
     * Checks if is show or hideref flag.
     *
     * @return true, if is show or hideref flag
     */
    public boolean isShowOrHiderefFlag() {
        return showOrHiderefFlag;
    }

    /**
     * Sets the show or hideref flag.
     *
     * @param showOrHiderefFlag the new show or hideref flag
     */
    public void setShowOrHiderefFlag(boolean showOrHiderefFlag) {
        this.showOrHiderefFlag = showOrHiderefFlag;
    }

    /**
     * Checks if is success worker flag.
     *
     * @return true, if is success worker flag
     */
    public boolean isSuccessWorkerFlag() {
        return successWorkerFlag;
    }

    /**
     * Sets the success worker flag.
     *
     * @param successWorkerFlag the new success worker flag
     */
    public void setSuccessWorkerFlag(boolean successWorkerFlag) {
        this.successWorkerFlag = successWorkerFlag;
    }

    /**
     * Checks if is inits the data text flag.
     *
     * @return true, if is inits the data text flag
     */
    public boolean isInitDataTextFlag() {
        return initDataTextFlag;
    }

    /**
     * Sets the inits the data text flag.
     *
     * @param initDataTextFlag the new inits the data text flag
     */
    public void setInitDataTextFlag(boolean initDataTextFlag) {
        this.initDataTextFlag = initDataTextFlag;
    }

    /**
     * Checks if is partloaded.
     *
     * @return true, if is partloaded
     */
    public boolean isPartloaded() {
        return partloaded;
    }

    /**
     * Sets the partloaded.
     *
     * @param partloaded the new partloaded
     */
    public void setPartloaded(boolean partloaded) {
        this.partloaded = partloaded;
    }

    /**
     * Gets the loaded row cnt.
     *
     * @return the loaded row cnt
     */
    public int getLoadedRowCnt() {
        return loadedRowCnt;
    }

    /**
     * Sets the loaded row cnt.
     *
     * @param loadedRowCnt the new loaded row cnt
     */
    public void setLoadedRowCnt(int loadedRowCnt) {
        this.loadedRowCnt = loadedRowCnt;
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        colWidths = null;
        colPro = null;
        this.cloNames = null;
        styledSearchText = null;
        searchParent = null;
        textParent = null;
        uiPref = null;
        eventTable = null;
        curLineRange = null;
        searchItem = null;
        copyItem = null;
        refreshItem = null;
        searchArea = null;
        eventTable = null;
        uiPref = null;
        this.allRows = null;
        removeKeyListner();
        styledText = null;
        deleteObserver();
    }

    private void deleteObserver() {
        if (this.stateMachine != null && this.stateMachine.countObservers() > 0) {
            this.stateMachine.deleteObservers();
        }
    }

    private void removeKeyListner() {
        if (this.scrollDataLoadListener != null && styledText != null) {
            scrollDataLoadListener.onPreDestroy();
            this.styledText.removeKeyListener(this.scrollDataLoadListener);
        }
    }

    private void getEncoding() {
        defaultEncod = uiPref.getDefaultEncoding();
        selEncod = gridComponent.getSelectedEncoding();
    }

    /**
     * Creates the initial data model.
     *
     * @return the string
     */
    private String createInitialDataModel() {
        StringBuilder loadDateSB = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        maxColumnSize = uiPref.getColumnWidth();
        allRows = dataProvider.getAllFetchedRows();
        colPro = dataProvider.getColumnDataProvider();
        cloNames = colPro.getColumnNames();
        colWidths = new int[cloNames.length];
        setMaxColumnWidth();
        setColumnSize();

        // Add text header
        addTextHeader(loadDateSB);
        loadDateSB.append("|");
        loadDateSB.append(System.lineSeparator());
        // Add separator
        addSeperator(loadDateSB);
        // Add text line
        loadDateSB.append(System.lineSeparator());
        showRow(allRows, loadDateSB);
        return String.valueOf(loadDateSB);

    }

    private void setMaxColumnWidth() {
        Object[] objRow = null;
        for (int index = 0; index < cloNames.length; index++) {
            colWidths[index] = cloNames[index].length();
            for (IDSGridDataRow row : allRows) {
                objRow = row.getValues();
                String displayString = replaceSpecalChar(objRow, index);
                colWidths[index] = Math.max(colWidths[index], displayString.length());
            }
        }
    }

    private void setColumnSize() {
        for (int cnt = 0; cnt < colWidths.length; cnt++) {
            colWidths[cnt]++;
            if (colWidths[cnt] > maxColumnSize) {
                colWidths[cnt] = maxColumnSize;
            }
        }
    }

    private void addTextHeader(StringBuilder loadDateSB) {
        for (int index = 0; index < cloNames.length; index++) {
            appendSeperator(loadDateSB, index);
            String attrName = cloNames[index];
            attrName = GridUIUtils.truncateString(attrName, colWidths[index] - 1);
            loadDateSB.append(attrName);
            appendEmptySpace(loadDateSB, index, attrName);
        }
    }

    private void addSeperator(StringBuilder loadDateSB) {
        for (int cnt = 0; cnt < cloNames.length; cnt++) {
            appendSeperator(loadDateSB, cnt);
            for (int k = colWidths[cnt]; k > 0; k--) {
                loadDateSB.append("-");
            }
        }
        loadDateSB.append("|");
    }

    private void handleUIstatusOnInitDataTextFail() {
        if (isInitDataTextFlag()) {
            setUIstatusOnInitDataTextCancelOrException();
        } else {
            gridComponent.getToolbar().updataButStatusOnLoadDataTextException();
            if (gridComponent.isShowGridOrShowTextSelect()) {
                setScrollStatusOnInitOrLoad(false);
            } else {
                setScrollStatusOnInitOrLoad(true);
            }
        }
    }

    private void setUIstatusOnInitDataTextCancelOrException() {
        setInitDataTextFlag(true);
        setScrollStatusOnInitOrLoad(false);
        gridComponent.getToolbar().updataButStatusOnInitDataTextCancelOrException();
    }

    private void setScrollStatusOnInitOrLoad(boolean scrollStatus) {
        if (null != scrollDataLoadListener) {
            scrollDataLoadListener.setCurrentInitDataTextSatatu(scrollStatus);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DataTextSearchWorker.
     */
    private final class DataTextSearchWorker extends TerminalWorker {
        private String quotedStr;
        private List<StyleRange> styleList = new ArrayList<StyleRange>();
        private StringBuilder matchTextSB = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        Color colorStyle = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);

        /**
         * Instantiates a new data text search worker.
         *
         * @param message the message
         * @param quotedStr the quoted str
         */
        public DataTextSearchWorker(String message, String quotedStr) {
            super(message, MPPDBIDEConstants.CANCELABLEJOB);
            this.quotedStr = quotedStr;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            searchTextMatch();
            return null;
        }

        @Override
        public void onOutOfMemoryError(OutOfMemoryError exception) {
            MPPDBIDELoggerUtility.error("DataText: OutofMemory error occurred.", exception);
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_SEARCH_OCCURED));
        }

        private void searchTextMatch() throws MPPDBIDEException {
            searchMatchLineList = new ArrayList<Integer>();
            int lineLenth = getLineLength();
            int searchLineCount = 1;
            formMatchTextSB();
            for (int item = 0; item < styleTextList.size() - FIRST_ROW_LINE; item++) {
                Object[] objRow = allRows.get(item).getValues();
                int serchCount = 0;
                int lineMark = 0;
                for (int rowIndex = 0; rowIndex < objRow.length; rowIndex++) {
                    if (null == objRow[rowIndex]) {
                        continue;
                    }
                    String str = replaceSpecalChar(objRow, rowIndex);
                    Pattern pattern = Pattern.compile(quotedStr);
                    Matcher matcher = pattern.matcher(String.valueOf(str));
                    while (matcher.find()) {
                        serchCount++;
                        lineMark++;
                        if (serchCount == 1) {
                            handleMatchTextAndSearchMatchLine(item, lineMark);
                            searchLineCount++;
                        }

                        /*
                         * Get the number of matches before the matching letter
                         */
                        int lineCount = 0;
                        if (rowIndex > 0) {
                            for (int cnt = 0; cnt < rowIndex; cnt++) {
                                lineCount = colWidths[cnt] + lineCount;
                            }
                        }
                        lineCount = lineCount + rowIndex;
                        String displayString = GridUIUtils.truncateString(String.valueOf(str), colWidths[rowIndex] - 1);

                        addCurLineRangeToStyleList(lineLenth, searchLineCount, matcher, lineCount, displayString);

                    }
                }
            }
            Display.getDefault().syncExec(() -> {
                styledSearchText.setText(matchTextSB.toString());
            });
            StyleRange[] styleArray = styleList.toArray(new StyleRange[0] );
            Display.getDefault().asyncExec(() -> styledSearchText.setStyleRanges(styleArray));
        }

        private void addCurLineRangeToStyleList(int lineLenth, int searchLineCount, Matcher matcher, int lineCount,
                String displayString) {
            if (displayString.length() >= matcher.end()) {
                int start = lineCount + matcher.start() + lineLenth * searchLineCount;
                int end = lineCount + matcher.end() + lineLenth * searchLineCount;
                curLineRange = new StyleRange(start, end - start, null, colorStyle);
                styleList.add(curLineRange);
            } else if (displayString.length() > matcher.start() && displayString.length() <= matcher.end()) {
                int start = lineCount + matcher.start() + lineLenth * searchLineCount;
                int end = lineCount + displayString.length() + lineLenth * searchLineCount;
                curLineRange = new StyleRange(start, end - start, null, colorStyle);
                styleList.add(curLineRange);
            }
        }

        private void handleMatchTextAndSearchMatchLine(int index, int lineMark) {
            if (lineMark == 1) {
                if (styleTextList.size() > FIRST_ROW_LINE) {
                    matchTextSB.append(System.lineSeparator() + styleTextList.get(index + FIRST_ROW_LINE));
                    searchMatchLineList.add(index);
                }
            }
        }

        private void formMatchTextSB() {
            if (styleTextList.size() >= FIRST_ROW_LINE) {
                for (int row = 0; row < FIRST_ROW_LINE; row++) {
                    if (row == 0) {
                        matchTextSB.append(styleTextList.get(row));
                    } else {
                        matchTextSB.append(System.lineSeparator() + styleTextList.get(row));
                    }
                }
            }
        }

        private int getLineLength() {
            int lineLenth = System.lineSeparator().length();
            if (styleTextList.size() > 0) {
                lineLenth = styleTextList.get(0).length() + lineLenth;
            }
            return lineLenth;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            doHideText(textParent);
            doShowText(searchParent);
            setSearchHideOrShowFlag(true);
            setSuccessWorkerFlag(true);
            setSearchStatus(false);
            changeButStatus(true);
        }

        @Override
        public void finalCleanupUI() {

            cleanList();
        }

        @Override
        protected void canceling() {
            super.canceling();
            if (!isSuccessWorkerFlag()) {
                changeButStatus(true);
            }
        }

        private void cleanList() {
            styleList.clear();
            styleTextList.clear();
            matchTextSB.delete(0, matchTextSB.length());
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DataTextSearchNullWorker.
     */
    private final class DataTextSearchNullWorker extends TerminalWorker {
        private StringBuilder matchTextSB = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        /**
         * Instantiates a new data text search null worker.
         *
         * @param message the message
         */
        public DataTextSearchNullWorker(String message) {
            super(message, MPPDBIDEConstants.CANCELABLEJOB);
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            searchNullMatch();
            return null;
        }

        @Override
        public void onOutOfMemoryError(OutOfMemoryError exception) {
            MPPDBIDELoggerUtility.error("DataText: OutofMemory error occurred.", exception);
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_SEARCH_OCCURED));

        }

        private void searchNullMatch() {
            searchMatchLineList = new ArrayList<Integer>();
            if (styleTextList.size() >= FIRST_ROW_LINE) {
                for (int rwcnt = 0; rwcnt < FIRST_ROW_LINE; rwcnt++) {
                    if (rwcnt == 0) {
                        matchTextSB.append(styleTextList.get(rwcnt));
                    } else {
                        matchTextSB.append(System.lineSeparator() + styleTextList.get(rwcnt));
                    }
                }
            }
            if (styleTextList.size() > FIRST_ROW_LINE) {
                for (int index = 0; index < styleTextList.size() - FIRST_ROW_LINE; index++) {
                    Object[] objRow = allRows.get(index).getValues();
                    for (int cnt = 0; cnt < objRow.length; cnt++) {
                        if (null == objRow[cnt]) {
                            matchTextSB.append(System.lineSeparator() + styleTextList.get(index + FIRST_ROW_LINE));
                            searchMatchLineList.add(index);
                            break;
                        }
                    }
                }
            }
            Display.getDefault().asyncExec(() -> styledSearchText.setText(matchTextSB.toString()));
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            doHideText(textParent);
            doShowText(searchParent);
            setSearchHideOrShowFlag(true);
            setSuccessWorkerFlag(true);
            changeButStatus(true);
            setSearchStatus(false);
        }

        @Override
        public void finalCleanupUI() {
            cleanList();
        }

        private void cleanList() {
            styleTextList.clear();
            searchMatchLineList.clear();
            matchTextSB.delete(0, matchTextSB.length());
        }

        @Override
        protected void canceling() {
            super.canceling();
            if (!isSuccessWorkerFlag()) {
                changeButStatus(true);
            }
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ReloadTextDataWorker.
     */
    private final class ReloadTextDataWorker extends TerminalWorker {

        /**
         * Instantiates a new reload text data worker.
         *
         * @param execuMessage the execu message
         */
        public ReloadTextDataWorker(String execuMessage) {
            super(execuMessage, MPPDBIDEConstants.CANCELABLEJOB);

        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            return createInitialDataModel();
        }

        @Override
        public void onSuccessUIAction(Object objParam) {
            Object obj = objParam;
            try {
                if (null != styledText) {
                    styledText.setText(String.valueOf(obj));
                    styledText.setTopIndex(scrolledRow + 2);
                    if (isInitDataTextFlag() && styledText.getLineCount() > FIRST_ROW_LINE) {
                        int start = (styledText.getLine(0).length() + System.lineSeparator().length()) * 2;
                        setHigtLight(start, colWidths[0], SWT.COLOR_GRAY);
                    }
                }
                // let gc collect
                obj = null;
            } catch (OutOfMemoryError exception) {
                handleUIstatusOnInitDataTextFail();
                onOutOfMemoryError(exception);
            }
            setSuccessWorkerFlag(true);
            if (isInitDataTextFlag()) {
                setInitDataTextFlag(false);
                setScrollStatusOnInitOrLoad(false);
                if (null != gridComponent && null != gridComponent.getToolbar()) {
                    gridComponent.getToolbar().updataButStatusOnInitDataText();
                }
            } else {
                if (isRefreshFlag()) {
                    setRefreshFlag(false);
                }
                if (isEncodingFlag()) {
                    setEndodingFlag(false);
                }
                if (null != gridComponent && null != gridComponent.getToolbar()) {
                    gridComponent.getToolbar().updataButStatusOnLoadDataText(true);
                }
                setScrollStatusOnInitOrLoad(false);
            }
        }

        @Override
        public void finalCleanupUI() {

        }

        @Override
        protected void canceling() {
            super.canceling();
            if (!isSuccessWorkerFlag()) {
                if (isInitDataTextFlag()) {
                    setUIstatusOnInitDataTextCancelOrException();
                } else {
                    if (null != gridComponent && null != gridComponent.getToolbar()) {
                        gridComponent.getToolbar().updataButStatusOnLoadDataText(true);
                    }
                    setScrollStatusOnInitOrLoad(false);
                }

            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TerminalWorker.
     */
    private abstract class TerminalWorker extends UIWorkerJob {

        /**
         * Instantiates a new terminal worker.
         *
         * @param name the name
         * @param family the family
         */
        public TerminalWorker(String name, Object family) {
            super(name, family);

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            return;
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            return;
        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
            return;
        }

        @Override
        public void onOutOfMemoryError(OutOfMemoryError exception) {
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_SEARCH_OCCURED));
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_SEARCH_ERROR),
                    exception);
        }

        /**
         * Gets the window image.
         *
         * @return the window image
         */
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass());
        }
    }
}
