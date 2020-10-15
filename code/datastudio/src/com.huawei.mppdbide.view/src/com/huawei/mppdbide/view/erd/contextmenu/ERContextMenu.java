/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.erd.contextmenu;

import java.util.Collections;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.huawei.mppdbide.presentation.erd.AbstractERPresentation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.erd.convertor.ERModelToGraphModelConvertor;
import com.huawei.mppdbide.view.ui.erd.ERPart;
import com.huawei.mppdbide.view.utils.UIElement;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

/**
 * Title: ERContextMenu Description: the popup menu. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author z00518937
 * @version [DataStudio 6.5.1, Nov 5, 2019]
 * @since Nov 5, 2019
 */
public class ERContextMenu extends ContextMenu {
    private IViewer viewer;
    private AbstractERPresentation diagram;

    private Menu viewStyleMenu;
    private CheckMenuItem showIconsItem;
    private CheckMenuItem showDataTypesItem;
    private CheckMenuItem showNullabilityItem;
    private CheckMenuItem showCommentsItem;
    private CheckMenuItem showFullyQualifiedNamesItem;
    private Menu attributeVisibilityMenu;
    private RadioMenuItem attrVisibilityAllItem;
    private RadioMenuItem attrVisibilityAnyKeysItem;
    private RadioMenuItem attrVisibilityPrimaryKeyItem;
    private RadioMenuItem attrVisibilityNoneItem;

    /**
     * Instantiates a new ER context menu.
     *
     * @param viewer the viewer
     * @param diagram the diagram
     */
    public ERContextMenu(IViewer viewer, AbstractERPresentation diagram) {
        this.viewer = viewer;
        this.diagram = diagram;
    }

    /**
     * Inits the ER context menu.
     */
    public void initERContextMenu() {
        viewStyleMenu = new Menu();
        viewStyleMenu.setText(MessageConfigLoader.getProperty(IMessagesConstants.VIEW_STYLES));
        getItems().add(viewStyleMenu);
        attributeVisibilityMenu = new Menu();
        attributeVisibilityMenu.setText(MessageConfigLoader.getProperty(IMessagesConstants.SHOW_ATTRIBUTES));
        getItems().add(attributeVisibilityMenu);

        addShowIconsMenuItem(viewStyleMenu);
        addShowDataTypesMenuItem(viewStyleMenu);
        addShowNullabilityMenuItem(viewStyleMenu);
        addShowCommentsMenuItem(viewStyleMenu);
        addShowFullyQualifiedNamesMenuItem(viewStyleMenu);

        ToggleGroup toggleGroup = new ToggleGroup();
        addAttrVisibilityAllItem(attributeVisibilityMenu, toggleGroup);
        addAttrVisibilityAnyKeysItem(attributeVisibilityMenu, toggleGroup);
        addAttrVisibilityPrimaryKeyItem(attributeVisibilityMenu, toggleGroup);
        addAttrVisibilityNoneItem(attributeVisibilityMenu, toggleGroup);
    }

    /**
     * Gets the viewer.
     *
     * @return the viewer
     */
    public IViewer getViewer() {
        return viewer;
    }

    /**
     * Gets the diagram.
     *
     * @return the diagram
     */
    public AbstractERPresentation getDiagram() {
        return diagram;
    }

    private void addShowIconsMenuItem(Menu menu) {
        showIconsItem = new CheckMenuItem();
        ERViewStyle style = ERViewStyle.ICONS;
        showIconsItem.setText(style.getTitle());
        showIconsItem.setSelected(isCheckedViewStyle(style));
        showIconsItem.setOnAction(actionEvent -> {
            refreshViewStyle(style);
        });
        menu.getItems().add(showIconsItem);
    }

    private void addShowDataTypesMenuItem(Menu menu) {
        showDataTypesItem = new CheckMenuItem();
        ERViewStyle style = ERViewStyle.TYPES;
        showDataTypesItem.setText(style.getTitle());
        showDataTypesItem.setSelected(isCheckedViewStyle(style));
        showDataTypesItem.setOnAction(actionEvent -> {
            refreshViewStyle(style);
        });
        menu.getItems().add(showDataTypesItem);
    }

    private void addShowNullabilityMenuItem(Menu menu) {
        showNullabilityItem = new CheckMenuItem();
        ERViewStyle style = ERViewStyle.NULLABILITY;
        showNullabilityItem.setText(style.getTitle());
        showNullabilityItem.setSelected(isCheckedViewStyle(style));
        showNullabilityItem.setOnAction(actionEvent -> {
            refreshViewStyle(style);
        });
        menu.getItems().add(showNullabilityItem);
    }

    private void addShowCommentsMenuItem(Menu menu) {
        showCommentsItem = new CheckMenuItem();
        ERViewStyle style = ERViewStyle.COMMENTS;
        showCommentsItem.setText(style.getTitle());
        showCommentsItem.setSelected(isCheckedViewStyle(style));
        showCommentsItem.setOnAction(actionEvent -> {
            refreshViewStyle(style);
        });
        menu.getItems().add(showCommentsItem);
    }

    private void addShowFullyQualifiedNamesMenuItem(Menu menu) {
        showFullyQualifiedNamesItem = new CheckMenuItem();
        ERViewStyle style = ERViewStyle.ENTITY_FQN;
        showFullyQualifiedNamesItem.setText(style.getTitle());
        showFullyQualifiedNamesItem.setSelected(isCheckedViewStyle(style));
        showFullyQualifiedNamesItem.setOnAction(actionEvent -> {
            refreshViewStyle(style);
        });
        menu.getItems().add(showFullyQualifiedNamesItem);
    }

    /**
     * Check if the ERDViewStyle is checked.
     * 
     * @Author: z00518937
     * @Date: Sep 18, 2019
     * @Title: isChecked
     * @param style the style
     * @return true, if the ERDViewStyle is checked.
     */
    private boolean isCheckedViewStyle(ERViewStyle style) {
        return ERViewStyle.isContainViewStyle(ERViewStyle.getDefaultStyles(), style);
    }

    private void refreshViewStyle(ERViewStyle style) {
        ERViewStyle.setAttributeStyle(style, !isCheckedViewStyle(style));
        if (ERViewStyle.getAttributeStyles().size() == 0) {
            ERViewStyle.setAttributeStyle(ERViewStyle.ICONS, true);
            showIconsItem.setSelected(true);
        }

        List<MPart> list = UIElement.getInstance().getAllOpenERPart();
        for (MPart mPart : list) {
            if (mPart.getObject() instanceof ERPart) {
                ERPart erPart = (ERPart) mPart.getObject();

                AbstractERPresentation erDiagram = erPart.getErContextMenu().getDiagram();
                Graph graph = ERModelToGraphModelConvertor.getGraphModel(erDiagram);
                erPart.getErContextMenu().getViewer().getContents().setAll(Collections.singletonList(graph));
                erPart.getErContextMenu().setItemsSelectStatu();
            }
        }
    }

    private void setItemsSelectStatu() {
        if (ERViewStyle.isShowIcons()) {
            showIconsItem.setSelected(true);
        } else {
            showIconsItem.setSelected(false);
        }

        if (ERViewStyle.isShowDataTypes()) {
            showDataTypesItem.setSelected(true);
        } else {
            showDataTypesItem.setSelected(false);
        }

        if (ERViewStyle.isShowNullability()) {
            showNullabilityItem.setSelected(true);
        } else {
            showNullabilityItem.setSelected(false);
        }

        if (ERViewStyle.isShowComments()) {
            showCommentsItem.setSelected(true);
        } else {
            showCommentsItem.setSelected(false);
        }

        if (ERViewStyle.isShowFullyQualifiedNames()) {
            showFullyQualifiedNamesItem.setSelected(true);
        } else {
            showFullyQualifiedNamesItem.setSelected(false);
        }
    }

    private void addAttrVisibilityAllItem(Menu menu, ToggleGroup toggleGroup) {
        attrVisibilityAllItem = new RadioMenuItem();
        ERAttributeVisibility visibility = ERAttributeVisibility.ALL;
        attrVisibilityAllItem.setText(visibility.getTitle());
        attrVisibilityAllItem.setSelected(isCheckedAttributeVisibility(visibility));
        attrVisibilityAllItem.setOnAction(actionEvent -> {
            refreshAttributeVisibility(visibility);
        });
        attrVisibilityAllItem.setToggleGroup(toggleGroup);
        menu.getItems().add(attrVisibilityAllItem);
    }

    private void addAttrVisibilityAnyKeysItem(Menu menu, ToggleGroup toggleGroup) {
        attrVisibilityAnyKeysItem = new RadioMenuItem();
        ERAttributeVisibility visibility = ERAttributeVisibility.ANY_KEYS;
        attrVisibilityAnyKeysItem.setText(visibility.getTitle());
        attrVisibilityAnyKeysItem.setSelected(isCheckedAttributeVisibility(visibility));
        attrVisibilityAnyKeysItem.setOnAction(actionEvent -> {
            refreshAttributeVisibility(visibility);
        });
        attrVisibilityAnyKeysItem.setToggleGroup(toggleGroup);
        menu.getItems().add(attrVisibilityAnyKeysItem);
    }

    private void addAttrVisibilityPrimaryKeyItem(Menu menu, ToggleGroup toggleGroup) {
        attrVisibilityPrimaryKeyItem = new RadioMenuItem();
        ERAttributeVisibility visibility = ERAttributeVisibility.PRIMARY_KEY;
        attrVisibilityPrimaryKeyItem.setText(visibility.getTitle());
        attrVisibilityPrimaryKeyItem.setSelected(isCheckedAttributeVisibility(visibility));
        attrVisibilityPrimaryKeyItem.setOnAction(actionEvent -> {
            refreshAttributeVisibility(visibility);
        });
        attrVisibilityPrimaryKeyItem.setToggleGroup(toggleGroup);
        menu.getItems().add(attrVisibilityPrimaryKeyItem);
    }

    private void addAttrVisibilityNoneItem(Menu menu, ToggleGroup toggleGroup) {
        attrVisibilityNoneItem = new RadioMenuItem();
        ERAttributeVisibility visibility = ERAttributeVisibility.NONE;
        attrVisibilityNoneItem.setText(visibility.getTitle());
        attrVisibilityNoneItem.setSelected(isCheckedAttributeVisibility(visibility));
        attrVisibilityNoneItem.setOnAction(actionEvent -> {
            refreshAttributeVisibility(visibility);
        });
        attrVisibilityNoneItem.setToggleGroup(toggleGroup);
        menu.getItems().add(attrVisibilityNoneItem);
    }

    private boolean isCheckedAttributeVisibility(ERAttributeVisibility visibility) {
        return visibility == ERAttributeVisibility.getDefaultVisibility();
    }

    private void refreshAttributeVisibility(ERAttributeVisibility visibility) {
        ERAttributeVisibility.setDefaultVisiblity(visibility);

        AbstractERPresentation digram = this.getDiagram();
        Graph graph = ERModelToGraphModelConvertor.getGraphModel(digram);
        this.getViewer().getContents().setAll(Collections.singletonList(graph));
    }
}