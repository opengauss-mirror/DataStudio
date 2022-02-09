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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.workbench.swt.internal.copy.WorkbenchSWTMessages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.groups.FilterObject;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.data.DSViewDataManager;
import org.opengauss.mppdbide.view.prefernces.IObjectBrowserPreference;
import org.opengauss.mppdbide.view.prefernces.PreferenceWrapper;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: ObjectBrowserFilterTree
 *
 * @since 3.0.0
 */
public class ObjectBrowserFilterTree extends Composite implements Observer {

    /**
     * The filter text.
     */
    protected Text filterText;

    /**
     * The clear button control.
     */
    protected Control clearButtonControl;
    /**
     * The error button control.
     */
    protected Control errButtonControl;

    /**
     * The tree viewer.
     */
    protected TreeViewer treeViewer;

    /**
     * The filter composite.
     */
    protected Composite filterComposite;

    /**
     * The initial text.
     */
    protected String initialText = "";

    /**
     * The refresh job.
     */
    private Job refreshJob;

    /**
     * The parent.
     */
    protected Composite parent;

    /**
     * The show filter controls.
     */
    protected boolean showFilterControls;

    /**
     * The tree composite.
     */
    protected Composite treeComposite;

    /**
     * The Constant CLEAR_ICON.
     */
    private static final String CLEAR_ICON = "org.eclipse.ui.internal.dialogs.CLEAR_ICON"; // $NON-NLS-1$

    /**
     * The Constant DISABLED_CLEAR_ICON.
     */
    private static final String DISABLED_CLEAR_ICON = "org.eclipse.ui.internal.dialogs.DCLEAR_ICON";

    /**
     * Get image descriptors for the clear button.
     */
    static {
        ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID,
                "$nl$/icons/full/etool16/clear_co.png");
        if (descriptor != null) {
            JFaceResources.getImageRegistry().put(CLEAR_ICON, descriptor);
        }
        descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID,
                "$nl$/icons/full/dtool16/clear_co.png");
        if (descriptor != null) {
            JFaceResources.getImageRegistry().put(DISABLED_CLEAR_ICON, descriptor);
        }

    }

    /**
     * Instantiates a new object browser filter tree.
     *
     * @param parent the parent
     * @param treeStyle the tree style
     */
    public ObjectBrowserFilterTree(Composite parent, int treeStyle) {
        super(parent, SWT.NONE);
        this.parent = parent;
        init(treeStyle);
        ObjectBrowserFilterUtility.getInstance().addObserver(this);
    }

    /**
     * Instantiates a new object browser filter tree.
     *
     * @param parent the parent
     * @param treeStyle the tree style
     * @param useNewLook the use new look
     */
    @Deprecated
    public ObjectBrowserFilterTree(Composite parent, int treeStyle, boolean useNewLook) {
        this(parent, treeStyle);
    }

    /**
     * Inits the.
     *
     * @param treeStyle the tree style
     */
    protected void init(int treeStyle) {
        showFilterControls = true;
        createControl(parent, treeStyle);
        createRefreshJob();
        setInitialText(WorkbenchSWTMessages.FilteredTree_FilterMessage);
        setFont(parent.getFont());
    }

    /**
     * Creates the control.
     *
     * @param parent the parent
     * @param treeStyle the tree style
     */
    protected void createControl(Composite parent, int treeStyle) {
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        setLayout(layout);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        if (showFilterControls) {
            if (useNativeSearchField(parent)) {
                filterComposite = new Composite(this, SWT.NONE);
            } else {
                filterComposite = new Composite(this, SWT.BORDER);
                filterComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            }
            GridLayout filterLayout = new GridLayout(3, false);
            filterLayout.marginHeight = 0;
            filterLayout.marginWidth = 0;
            filterComposite.setLayout(filterLayout);
            filterComposite.setFont(parent.getFont());

            createFilterControls(filterComposite);
            filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        }

        treeComposite = new Composite(this, SWT.NONE);
        GridLayout treeCompositeLayout = new GridLayout();
        treeCompositeLayout.marginHeight = 0;
        treeCompositeLayout.marginWidth = 0;
        treeComposite.setLayout(treeCompositeLayout);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        treeComposite.setLayoutData(data);
        createTreeControl(treeComposite, treeStyle);
    }

    /**
     * The use native search field.
     */
    private static volatile Boolean useNativeSearchField;

    /**
     * Use native search field.
     *
     * @param composite the composite
     * @return true, if successful
     */
    private static boolean useNativeSearchField(Composite composite) {
        if (useNativeSearchField == null) {
            useNativeSearchField = Boolean.FALSE;
            Text testText = null;
            try {
                testText = new Text(composite, SWT.SEARCH | SWT.ICON_CANCEL);
                useNativeSearchField = Boolean.valueOf((testText.getStyle() & SWT.ICON_CANCEL) != 0);
            } finally {
                if (testText != null) {
                    testText.dispose();
                }
            }

        }
        return useNativeSearchField.booleanValue();
    }

    /**
     * Creates the filter controls.
     *
     * @param parent the parent
     * @return the composite
     */
    protected Composite createFilterControls(Composite parent) {
        createFilterText(parent);
        createErrorBtn(parent);
        createClearText(parent);
        if (clearButtonControl != null) {
            // initially there is no text to clear
            clearButtonControl.setVisible(false);
        }
        if (errButtonControl != null) {
            // initially there is no text to clear
            errButtonControl.setVisible(false);
        }
        return parent;
    }

    private void createErrorBtn(Composite parent2) {
        Label errorBtn = new Label(parent2, SWT.NONE);
        errorBtn.setImage(IconUtility.getIconImage(IiconPath.ICO_CONSTRAINTS, this.getClass()));
        errButtonControl = errorBtn;
    }

    /**
     * Creates the tree control.
     *
     * @param parent the parent
     * @param style the style
     * @return the control
     */
    protected Control createTreeControl(Composite parent, int style) {
        treeViewer = doCreateTreeViewer(parent, style);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        treeViewer.getControl().setLayoutData(data);
        treeViewer.getControl().addDisposeListener(new OBDisposeListener());
        return treeViewer.getControl();
    }

    private static final class ClearButtonAccessibleControlAdaptor extends AccessibleControlAdapter {
        @Override
        public void getRole(AccessibleControlEvent event) {
            event.detail = ACC.ROLE_PUSHBUTTON;
        }
    }

    private static final class ClearButtonAccessibleAdaptor extends AccessibleAdapter {
        @Override
        public void getName(AccessibleEvent event) {
            event.result = WorkbenchSWTMessages.FilteredTree_AccessibleListenerClearButton;
        }
    }

    private final class FilterTextAccessibleAdaptor extends AccessibleAdapter {
        String initialText = null;
        Text filterText = null;

        FilterTextAccessibleAdaptor(Text filterText, String initialText) {
            this.filterText = filterText;
            this.initialText = initialText;

        }

        @Override
        public void getName(AccessibleEvent event) {
            String filterTextString = filterText.getText();
            if (filterTextString.length() == 0 || filterTextString.equals(initialText)) {
                event.result = initialText;
            } else {
                event.result = NLS.bind(WorkbenchSWTMessages.FilteredTree_AccessibleListenerFiltered,
                        new String[] {filterTextString, String.valueOf(getFilteredItemsCount())});
            }
        }
    }

    private class OBDisposeListener implements DisposeListener {
        @Override
        public void widgetDisposed(DisposeEvent e) {
            refreshJob.cancel();

        }

    }

    /**
     * Do create tree viewer.
     *
     * @param parent the parent
     * @param style the style
     * @return the tree viewer
     */
    protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
        return new TreeViewer(parent, style);
    }

    /**
     * errorBtnVisible
     * 
     * @param isVisible is btn visible
     */
    public void errorBtnVisible(boolean isVisible) {
        if (errButtonControl != null) {
            errButtonControl.setToolTipText(ObjectBrowserFilterUtility.getInstance().getErrorTooltip());
            errButtonControl.setVisible(isVisible);
        }
    }

    /**
     * Creates the refresh job.
     */
    private void createRefreshJob() {
        errorBtnVisible(false);
        refreshJob = doCreateRefreshJob();
        refreshJob.setSystem(true);
    }

    /**
     * Do create refresh job.
     *
     * @return the basic UI job
     */
    @SuppressWarnings("restriction")
    protected BasicUIJob doCreateRefreshJob() {
        return new BasicUIJob("Refresh Filter", parent.getDisplay()) {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ExecTimer timer = new ExecTimer("Objectbrowser search");
                timer.start();
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(""));
                if (treeViewer.getControl().isDisposed()) {
                    return Status.CANCEL_STATUS;
                }

                String text = getFilterString();
                FilterObject.getInstance().setFilterText(text);
                if (text == null) {
                    return Status.OK_STATUS;
                }
                boolean initial = initialText != null && initialText.equals(text);
                Control redrawFalseControl = treeComposite != null ? treeComposite : treeViewer.getControl();
                boolean cancel = false;
                try {
                    redrawFalseControl.setRedraw(false);

                    if (text.length() > 0 && !initial || text.isEmpty()) {
                        cancel = callRecursiveExpand(monitor);
                        if (cancel) {
                            errorBtnVisible(true);
                            return Status.CANCEL_STATUS;
                        }
                    } else {
                        updateToolbar(false);
                    }
                } catch (SWTException swtException) {
                    MPPDBIDELoggerUtility.error("SWT Exception while rendering the search", swtException);
                } finally {
                    handleFinallyBlock(redrawFalseControl, cancel);
                }
                postFilteringOperation(timer);
                return Status.OK_STATUS;
            }

        };
    }

    /**
     * Post refresh job.
     *
     * @param redrawFalseControl the redraw false control
     */
    public void postRefreshJob(Control redrawFalseControl) {
        // done updating the tree - set redraw back to true
        TreeItem[] items = getViewer().getTree().getItems();
        if (items.length > 0 && getViewer().getTree().getSelectionCount() == 0) {
            treeViewer.getTree().setTopItem(items[0]);
        }
        redrawFalseControl.setRedraw(true);
    }

    /**
     * Call recursive expand.
     *
     * @param monitor the monitor
     * @return true, if successful
     */
    public boolean callRecursiveExpand(IProgressMonitor monitor) {
        TreeItem[] items = getViewer().getTree().getItems();
        int treeHeight = getViewer().getTree().getBounds().height;
        int numVisibleItems = treeHeight / getViewer().getTree().getItemHeight();
        long timout = PreferenceWrapper.getInstance().getPreferenceStore()
                .getInt(IObjectBrowserPreference.OB_FILTER_TIMEOUT_PREFERENCE_KEY);
        long stopTime = timout * 1000 + System.currentTimeMillis();
        boolean cancel = false;
        getServerList(items);
        if (items.length > 0 && recursiveExpand(items, monitor, stopTime, new int[] {numVisibleItems})) {
            cancel = true;
        }
        updateToolbar(true);
        return cancel;
    }

    private void getServerList(TreeItem[] items) {
        for (int index = 0; index < items.length; index++) {
            TreeItem item = items[index];
            Object data = item.getData();
            if (data instanceof Server) {
                Server server = (Server) data;
                if (server.isAleastOneDbConnected()) {
                    ObjectBrowserFilterUtility.getInstance().addFilteredServer(server.getName());
                }
            }
        }
    }

    /**
     * Recursive expand.
     *
     * @param items the items
     * @param monitor the monitor
     * @param cancelTime the cancel time
     * @param numItemsLeft the num items left
     * @return true, if successful
     */
    private boolean recursiveExpand(TreeItem[] items, IProgressMonitor monitor, long cancelTime, int[] numItemsLeft) {
        boolean canceled = false;
        for (int index = 0; !canceled && index < items.length; index++) {
            TreeItem item = items[index];
            boolean visible = numItemsLeft[0]-- >= 0;
            if (monitor.isCanceled() || (!visible && System.currentTimeMillis() > cancelTime)) {
                canceled = true;
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                                MessageConfigLoader
                                        .getProperty(IMessagesConstants.OBJECT_BROWSER_FILTER_TIMEOUT_ERROR_TITLE),
                                MessageConfigLoader
                                        .getProperty(IMessagesConstants.OBJECT_BROWSER_FILTER_TIMEOUT_ERROR));
                    }
                });
            } else {
                Object itemData = item.getData();
                if (itemData != null) {
                    callRefreshView(item, itemData);
                    TreeItem[] children = item.getItems();
                    if (items.length > 0) {
                        canceled = recursiveExpand(children, monitor, cancelTime, numItemsLeft);
                    }
                }
            }
        }
        return canceled;
    }

    /**
     * Call refresh view.
     *
     * @param item the item
     * @param itemData the item data
     */
    public void callRefreshView(TreeItem item, Object itemData) {
        if (!item.isDisposed() && itemData instanceof ObjectGroup) {
            if ("LAZY".equalsIgnoreCase(DSViewDataManager.getInstance().getTreeRenderPolicy())) {
                ObjectGroup objectGroup = ((ObjectGroup) itemData);
                if (item.getExpanded()) {
                    if (objectGroup.getObjectType()) {
                        treeViewer.refresh(((ObjectGroup) itemData).getParent());
                    }
                } else {
                    treeViewer.setChildCount(itemData, ((ObjectGroup<?>) itemData).getChildren().length);
                }
            } else {
                ObjectGroup objectGroup = ((ObjectGroup) itemData);
                if (objectGroup.getObjectType()) {
                    treeViewer.refresh(objectGroup);
                }
                getServerName(objectGroup);
            }
        }
    }

    private void getServerName(ObjectGroup objectGroup) {
        if (objectGroup instanceof TablespaceObjectGroup) {
            TablespaceObjectGroup group = (TablespaceObjectGroup) objectGroup;
            ObjectBrowserFilterUtility.getInstance().removeRefreshedServerFromList(group.getServer().getName());
        }
      
    }

    /**
     * Update toolbar.
     *
     * @param visible the visible
     */
    protected void updateToolbar(boolean visible) {
        if (clearButtonControl != null) {
            clearButtonControl.setVisible(visible);
        }

    }

    /**
     * Adds the context menu to filter text.
     *
     * @param filterText the filter text
     */
    public static void addContextMenuToFilterText(final Text filterText) {
        Menu menu = new Menu(filterText);

        final MenuItem cutItem = new MenuItem(menu, SWT.PUSH);
        cutItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_CUT));
        addCutItemSelectionListener(filterText, cutItem);
        final MenuItem copyItem = new MenuItem(menu, SWT.PUSH);
        copyItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.RIGHT_CLICK_COPY_RESULT_WINDOW_CONTENTS));
        addCopyItemSelectionListener(filterText, copyItem);
        final MenuItem pasteItem = new MenuItem(menu, SWT.PUSH);
        pasteItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_PASTE));
        addPasteItemSelectionListener(filterText, pasteItem);
        final MenuItem selectItem = new MenuItem(menu, SWT.PUSH);
        selectItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_SELECT_ALL_BUTTON));
        addSelectItemSelectionListener(filterText, selectItem);
        menu.addMenuListener(addMenuListener(filterText, cutItem, copyItem, selectItem));
        filterText.setMenu(menu);
    }

    private static MenuListener addMenuListener(final Text filterText, final MenuItem cutItem, final MenuItem copyItem,
            final MenuItem selectItem) {
        return new MenuListener() {

            @Override
            public void menuShown(MenuEvent event) {
                if (!(filterText.getSelectionText().isEmpty())) {

                    cutItem.setEnabled(true);
                    copyItem.setEnabled(true);

                    selectItem.setEnabled(true);
                } else {
                    if (filterText.getSelectionText().isEmpty()) {
                        cutItem.setEnabled(false);
                        copyItem.setEnabled(false);
                    }
                    selectItem.setEnabled(true);
                }

            }

            @Override
            public void menuHidden(MenuEvent event) {
            }
        };
    }

    private static void addSelectItemSelectionListener(final Text filterText, final MenuItem selectItem) {
        selectItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                filterText.selectAll();
            }
        });
    }

    private static void addPasteItemSelectionListener(final Text filterText, final MenuItem pasteItem) {
        pasteItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                filterText.paste();
            }
        });
    }

    private static void addCopyItemSelectionListener(final Text filterText, final MenuItem copyItem) {
        copyItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                filterText.copy();
            }
        });
    }

    private static void addCutItemSelectionListener(final Text filterText, final MenuItem cutItem) {
        cutItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                filterText.cut();
            }
        });
    }

    /**
     * Creates the filter text.
     *
     * @param parent the parent
     */
    protected void createFilterText(Composite parent) {
        filterText = doCreateFilterText(parent);
        filterText.setTextLimit(63);
        filterText.setEnabled(false);
        filterText.getParent().getMenu();
        addContextMenuToFilterText(filterText);
        addAccessListenerForFilterText();

        filterText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                if (filterText.getText().equals(initialText)) {
                    setFilterText("");
                    textChanged();
                }
            }
        });
        filterText.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent event) {
                if (event.detail == SWT.TRAVERSE_RETURN) {
                    event.doit = false;
                    if (filterText.getText().equals("")) {
                        setFilterText("");
                        textChanged();
                    } else {
                        if (getViewer().getTree().getItemCount() == 0) {
                            Display.getCurrent().beep();
                        } else {
                            textChanged();
                        }
                    }
                }
            }
        });
        if ((filterText.getStyle() & SWT.ICON_CANCEL) != 0) {
            filterText.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetDefaultSelected(SelectionEvent event) {
                    if (event.detail == SWT.ICON_CANCEL) {
                        clearText();
                    }
                }
            });
        }

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        if ((filterText.getStyle() & SWT.ICON_CANCEL) != 0) {
            gridData.horizontalSpan = 2;
        }
        filterText.setLayoutData(gridData);
    }

    /**
     * Adds the access listener for filter text.
     */
    public void addAccessListenerForFilterText() {
        filterText.getAccessible().addAccessibleListener(new FilterTextAccessibleAdaptor(filterText, initialText));
    }

    /**
     * Return the number of filtered items.
     *
     * @return int
     */
    private int getFilteredItemsCount() {
        int total = 0;
        TreeItem[] items = getViewer().getTree().getItems();
        for (int index = 0; index < items.length; index++) {
            total += itemCount(items[index]);
        }
        return total;
    }

    /**
     * Return the count of treeItem and it's children to infinite depth.
     *
     * @param treeItem the tree item
     * @return int
     */
    private int itemCount(TreeItem treeItem) {
        int count = 1;
        TreeItem[] children = treeItem.getItems();
        for (int index = 0; index < children.length; index++) {
            count += itemCount(children[index]);

        }
        return count;
    }

    /**
     * Do create filter text.
     *
     * @param parent the parent
     * @return the text
     */
    protected Text doCreateFilterText(Composite parent) {
        if (useNativeSearchField(parent)) {
            return new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
        }
        return new Text(parent, SWT.SINGLE);
    }

    /**
     * Text changed.
     */
    protected void textChanged() {
        // cancel currently running job first, to prevent unnecessary redraw
        refreshJob.cancel();
        refreshJob.schedule(getRefreshJobDelay());
    }

    /**
     * Gets the refresh job delay.
     *
     * @return the refresh job delay
     */
    protected long getRefreshJobDelay() {
        return 200;
    }

    /**
     * Sets the background.
     *
     * @param background the new background
     */
    @Override
    public void setBackground(Color background) {
        super.setBackground(background);
        if (filterComposite != null && (useNativeSearchField(filterComposite))) {
            filterComposite.setBackground(background);
        }
    }

    /**
     * Creates the clear text.
     *
     * @param parent the parent
     */
    private void createClearText(Composite parent) {
        // only create the button if the text widget doesn't support one
        // natively
        if ((filterText.getStyle() & SWT.ICON_CANCEL) == 0) {
            final Image inactiveImage = JFaceResources.getImageRegistry().getDescriptor(DISABLED_CLEAR_ICON)
                    .createImage();
            final Image activeImage = JFaceResources.getImageRegistry().getDescriptor(CLEAR_ICON).createImage();
            final Image pressedImage = new Image(getDisplay(), activeImage, SWT.IMAGE_GRAY);

            final Label clearButton = new Label(parent, SWT.NONE);
            clearButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            clearButton.setImage(inactiveImage);
            clearButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            clearButton.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.FILTER_CLEAR_TOOLTIP));
            clearButton.addMouseListener(
                    new MouseListenerToClearButton(activeImage, clearButton, pressedImage, inactiveImage));
            clearButton.addMouseTrackListener(
                    new MouseTrackListenerToClearButton(inactiveImage, activeImage, clearButton));
            clearButton.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent event) {
                    inactiveImage.dispose();
                    activeImage.dispose();
                    pressedImage.dispose();
                }
            });
            clearButton.getAccessible().addAccessibleListener(new ClearButtonAccessibleAdaptor());
            clearButton.getAccessible().addAccessibleControlListener(new ClearButtonAccessibleControlAdaptor());
            this.clearButtonControl = clearButton;
        }
    }

    /**
     * Clear text.
     */
    protected void clearText() {
        if (filterText.getText().isEmpty()) {
            return;
        }
        setFilterText("");
        textChanged();
    }

    /**
     * Sets the filter text.
     *
     * @param string the new filter text
     */
    protected void setFilterText(String string) {
        if (filterText != null) {
            filterText.setText(string);
            selectAll();
        }
    }

    /**
     * Gets the filter text.
     *
     * @return the filter text
     */
    public Text getFilterText() {
        return filterText;
    }

    /**
     * Gets the viewer.
     *
     * @return the viewer
     */
    public TreeViewer getViewer() {
        return treeViewer;
    }

    /**
     * Gets the filter string.
     *
     * @return the filter string
     */
    protected String getFilterString() {
        return filterText != null ? filterText.getText() : null;
    }

    /**
     * Sets the initial text.
     *
     * @param text the new initial text
     */
    public void setInitialText(String text) {
        initialText = text;
        if (filterText != null) {
            filterText.setMessage(text);
            if (filterText.isFocusControl()) {
                setFilterText(initialText);
                textChanged();
            } else {
                getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (!filterText.isDisposed() && filterText.isFocusControl()) {
                            setFilterText(initialText);
                            textChanged();
                        }
                    }
                });
            }
        } else {
            setFilterText(initialText);
            textChanged();
        }
    }

    /**
     * Select all.
     */
    protected void selectAll() {
        if (filterText != null) {
            filterText.selectAll();
        }
    }

    /**
     * Gets the initial text.
     *
     * @return the initial text
     */
    protected String getInitialText() {
        return initialText;
    }

    private void handleFinallyBlock(Control redrawFalseControl, boolean cancel) {
        postRefreshJob(redrawFalseControl);
    }

    private void postFilteringOperation(ExecTimer timer) {
        try {
            timer.stopAndLog();
            errorBtnVisible(false);
        } catch (DatabaseOperationException databaseOperationException) {
            MPPDBIDELoggerUtility.error("Exception occured while filtering the Object Browser",
                    databaseOperationException);
        }
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_FILTER_FINISH_MSG)));
    }

    /**
     * Title: MouseTrackListenerToClearButton
     */

    private static final class MouseTrackListenerToClearButton implements MouseTrackListener {
        private final Image inactiveImage;
        private final Image activeImage;
        private final Label clearButton;

        /**
         * <The default constructor>
         */
        private MouseTrackListenerToClearButton(Image inactiveImage, Image activeImage, Label clearButton) {
            this.inactiveImage = inactiveImage;
            this.activeImage = activeImage;
            this.clearButton = clearButton;
        }

        @Override
        public void mouseEnter(MouseEvent event) {
            clearButton.setImage(activeImage);
        }

        @Override
        public void mouseExit(MouseEvent event) {
            clearButton.setImage(inactiveImage);
        }

        @Override
        public void mouseHover(MouseEvent event) {
        }
    }

    /**
     * Title: MouseListenerToClearButton
     */

    private final class MouseListenerToClearButton extends MouseAdapter {
        private final Image activeImage;
        private final Label clearButton;
        private final Image pressedImage;
        private final Image inactiveImage;
        private MouseMoveListener fMoveListener;

        /**
         * <The default constructor>
         */
        private MouseListenerToClearButton(Image activeImage, Label clearButton, Image pressedImage,
                Image inactiveImage) {
            this.activeImage = activeImage;
            this.clearButton = clearButton;
            this.pressedImage = pressedImage;
            this.inactiveImage = inactiveImage;
        }

        @Override
        public void mouseDown(MouseEvent evente) {
            clearButton.setImage(pressedImage);
            fMoveListener = new MouseMoveListener() {
                private boolean fMouseInButton = true;

                @Override
                public void mouseMove(MouseEvent event) {
                    boolean mouseInButton = isMouseInButton(event);
                    if (mouseInButton != fMouseInButton) {
                        fMouseInButton = mouseInButton;
                        clearButton.setImage(mouseInButton ? pressedImage : inactiveImage);
                    }
                }
            };
            clearButton.addMouseMoveListener(fMoveListener);
        }

        @Override
        public void mouseUp(MouseEvent event) {
            if (fMoveListener != null) {
                clearButton.removeMouseMoveListener(fMoveListener);
                fMoveListener = null;
                boolean mouseInButton = isMouseInButton(event);
                clearButton.setImage(mouseInButton ? activeImage : inactiveImage);
                if (mouseInButton) {
                    clearText();
                    filterText.setFocus();
                }
            }
        }

        private boolean isMouseInButton(MouseEvent event) {
            Point buttonSize = clearButton.getSize();
            return 0 <= event.x && event.x < buttonSize.x && 0 <= event.y && event.y < buttonSize.y;
        }
    }

    @Override
    public void update(Observable obj, Object arg) {
        if (ObjectBrowserFilterUtility.getInstance().isAllServerRefreshed()) {
            errorBtnVisible(false);
        } else {
            errorBtnVisible(true);
        }
    }

    /**
     * Title: Abstract class BasicUIJob
     * Description: Abstract class BasicUIJob
     */
    public abstract class BasicUIJob extends Job {
        private Display cachedDisplay;

        /**
         * Create a new instance of the receiver with the supplied name. The display
         * used will be the one from the workbench if this is available. UIJobs with
         * this constructor will determine their display at runtime.
         *
         * @param String the job name
         * @param Display the display
         */
        public BasicUIJob(String name, Display display) {
            super(name);
            this.cachedDisplay = display;
        }

        @Override
        public final IStatus run(final IProgressMonitor monitor) {
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            Display asyncDisplay = (cachedDisplay == null) ? getDisplay()
                    : cachedDisplay;
            if (asyncDisplay == null || asyncDisplay.isDisposed()) {
                return Status.CANCEL_STATUS;
            }
            asyncDisplay.asyncExec(() -> {
                IStatus result = null;
                try {
                    // As we are in the UI Thread we can
                    // always know what to tell the job.
                    setThread(Thread.currentThread());
                    if (monitor.isCanceled()) {
                        result = Status.CANCEL_STATUS;
                    } else {
                        result = runInUIThread(monitor);
                    }
                } finally {
                    done(result);
                }
            });
            return Job.ASYNC_FINISH;
        }

        /**
         * Run the job in the UI Thread.
         *
         * @param IProgressMonitor the monitor
         * @return IStatus the status
         */
        public abstract IStatus runInUIThread(IProgressMonitor monitor);

        /**
         * Returns the display for use by the receiver when running in an asyncExec.
         * If it is not set then the display set in the workbench is used. If the
         * display is null the job will not be run.
         *
         * @return Display or <code>null</code>.
         */
        public Display getDisplay() {
            return (cachedDisplay != null) ? cachedDisplay : Display.getCurrent();
        }
    }
}
