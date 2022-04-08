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

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler.Save;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MemoryCleaner;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.saveif.ISaveablePart;
import org.opengauss.mppdbide.view.ui.terminal.AbstractResultDisplayUIManager;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class Description: The Class AbstractUIWindow.
 *
 * @since 3.0.0
 */
public abstract class AbstractUIWindow implements ISaveablePart {

    /**
     * The parent composite.
     */
    protected Composite parentComposite;

    /** 
     * The tab. 
     */
    protected MPart tab;

    /**
     * The dirty handler.
     */
    @Inject
    protected MDirtyable dirtyHandler;

    /**
     * Gets the tab.
     *
     * @return the tab
     */
    public MPart getTab() {
        return tab;
    }

    /**
     * Sets the tab.
     *
     * @param tab the new tab
     */
    public void setTab(MPart tab) {
        this.tab = tab;
    }

    /**
     * Gets the display UI manager.
     *
     * @return the display UI manager
     */
    protected abstract AbstractResultDisplayUIManager getDisplayUIManager();

    /**
     * Creates the result window.
     *
     * @param parent the parent
     */
    protected void createResultWindow(Composite parent) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        Composite currComposite = new Composite(parent, SWT.NONE);
        currComposite.setBounds(0, 0, 464, 320);
        GridLayout layout = getGridLayout();
        currComposite.setLayout(layout);
        currComposite.setData(gridData);

        SashForm sashForm = new SashForm(currComposite, SWT.NONE);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sashForm.setOrientation(SWT.VERTICAL);
        this.parentComposite = sashForm;

        AbstractResultDisplayUIManager displayUIManager = getDisplayUIManager();
        if (null != displayUIManager) {
            displayUIManager.showResult(this.parentComposite);
            displayUIManager.setDirtyHandler(dirtyHandler);
        }
    }

    private GridLayout getGridLayout() {
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        return layout;
    }

    /**
     * Destroy source viewer.
     */
    @PreDestroy
    public void destroySourceViewer() {
        if (getDisplayUIManager() != null) {
            getDisplayUIManager().setDisposed();
        }
    }

    /**
     * Destroy.
     *
     * @param part the part
     */
    @PreDestroy
    public void destroy(@Active MPart part) {
        UIElement.getInstance().removePartFromStack(part.getElementId());
        MemoryCleaner.cleanUpMemory();
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    public AbstractResultDisplayUIManager getResultDisplayUIManager() {
        return getDisplayUIManager();
    }

    /**
     * Reset data.
     */
    public void resetData() {
        getDisplayUIManager().resetDataResult();

    }

    /**
     * Prompt user to save.
     *
     * @return the save
     */
    @Override
    public Save promptUserToSave() {
        getTab().getParent().setSelectedElement(getTab());
        String title = MessageConfigLoader.getProperty(IMessagesConstants.DISCARD_CHANGES_TITLE);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.DISCARD_TERMINAL_DATA_BODY);
        String cancel = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC);
        String discardChanges = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_DISCARD);
        int userChoice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                IconUtility.getIconImage(IiconPath.ICO_EDIT_EDIT, this.getClass()), title, message,
                new String[] {discardChanges, cancel}, 1);
        if (0 == userChoice) {
            return Save.NO;
        }
        return Save.CANCEL;
    }

}
