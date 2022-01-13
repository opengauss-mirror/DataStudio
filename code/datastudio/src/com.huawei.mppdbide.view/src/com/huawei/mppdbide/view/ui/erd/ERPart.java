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

package com.huawei.mppdbide.view.ui.erd;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.SetMultimap;
import com.huawei.mppdbide.presentation.erd.ERDiagramPresentation;
import com.huawei.mppdbide.presentation.erd.EREntityPresentation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

import javafx.scene.Node;

/**
 * The Class ERPart.
 *
 * @ClassName: ERPart
 * @Description: The Class ERPart.
 *
 * @since 3.0.0
 */
public class ERPart extends ERAbstractCore {
    private Composite toolbarComposite;

    /**
     * Post construct.
     *
     * @param availableComp the available comp
     * @param part the part
     * @return the composite
     */
    @PostConstruct
    public Composite postConstruct(Composite availableComp, MPart part) {
        Object obj = part.getObject();
        if (obj instanceof EREntityPresentation) {
            this.setPresenter((EREntityPresentation) obj);
        } else {
            this.setPresenter((ERDiagramPresentation) obj);
        }
        createComponent(availableComp, part);
        return availableComp;
    }

    private void createComponent(Composite parent, MPart part) {

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;

        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;

        Composite currComposite = new Composite(parent, SWT.NONE);
        currComposite.setData(gridData);
        currComposite.setLayout(layout);

        createToolBar(currComposite, part);
        SashForm sashForm = new SashForm(currComposite, SWT.NONE);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sashForm.setOrientation(SWT.VERTICAL);
        super.createPartControl(sashForm);
    }

    /**
     * Creates the tool bar.
     *
     * @param parent the parent
     * @param part the part
     */
    private void createToolBar(Composite parent, MPart part) {
        Composite composite = new Composite(parent, SWT.None);
        this.toolbarComposite = composite;
        GridData gdToolbarComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        toolbarComposite.setLayoutData(gdToolbarComposite);

        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        this.toolbarComposite.setLayout(layout);
        this.toolbarComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        createDescription(part);
    }

    /**
     * create the description of primaryKey and foreignKey icon and the header
     * color
     */
    private void createDescription(MPart part) {
        Composite executeComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdExecuteComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        executeComposite.setLayoutData(gdExecuteComposite);
        GridLayout executelayout = new GridLayout(10, false);
        executelayout.marginHeight = 0;
        executelayout.horizontalSpacing = 6;
        executeComposite.setLayout(executelayout);
        Label primaryKeyIcon = new Label(executeComposite, SWT.NONE);
        primaryKeyIcon.setImage(IconUtility.getIconImage(IiconPath.ER_PRIMARY_KEY, getClass()));
        Label primaryDesc = new Label(executeComposite, SWT.NONE);
        primaryDesc.setText(MessageConfigLoader.getProperty(IMessagesConstants.ER_DESC_PRIMARYKEY));

        Label foreignKeyIcon = new Label(executeComposite, SWT.NONE);
        foreignKeyIcon.setImage(IconUtility.getIconImage(IiconPath.ER_FOREIGN_KEY, getClass()));
        Label foreignDesc = new Label(executeComposite, SWT.NONE);
        foreignDesc.setText(MessageConfigLoader.getProperty(IMessagesConstants.ER_DESC_FOREIGNKEY));

        if (part.getObject() instanceof EREntityPresentation) {

            Label currentColorIcon = new Label(executeComposite, SWT.NONE);
            currentColorIcon.setImage(IconUtility.getIconImage(IiconPath.ER_CURRENT_COLOR_ICON, getClass()));
            Label currentColorDesc = new Label(executeComposite, SWT.NONE);
            currentColorDesc.setText(MessageConfigLoader.getProperty(IMessagesConstants.ER_DESC_CURRENT_COLOR));

            Label relatedColorIcon = new Label(executeComposite, SWT.NONE);
            relatedColorIcon.setImage(IconUtility.getIconImage(IiconPath.ER_RELATED_COLOR_ICON, getClass()));
            Label relatedColorDesc = new Label(executeComposite, SWT.NONE);
            relatedColorDesc.setText(MessageConfigLoader.getProperty(IMessagesConstants.ER_DESC_RELATED_COLOR));
        }
    }

    /**
     * Sets the adaptable.
     *
     * @param adaptable the new adaptable
     */
    public void setAdaptable(IAdaptable adaptable) {

    }

    /**
     * Do refresh visual.
     *
     * @param visual the visual
     */
    @Override
    protected void doRefreshVisual(Node visual) {

    }

    /**
     * Do get content anchorages.
     *
     * @return the sets the multimap
     */
    @Override
    protected SetMultimap doGetContentAnchorages() {
        return null;
    }

    /**
     * Do get content children.
     *
     * @return the list
     */
    @Override
    protected List doGetContentChildren() {
        return null;
    }

    /**
     * Do create visual.
     *
     * @return the node
     */
    @Override
    protected Node doCreateVisual() {
        return null;
    }
}
