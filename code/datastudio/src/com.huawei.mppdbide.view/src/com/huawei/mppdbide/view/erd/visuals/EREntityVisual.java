/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.erd.visuals;

import com.huawei.mppdbide.bl.erd.model.AbstractERAttribute;
import com.huawei.mppdbide.bl.erd.model.AbstractEREntity;
import com.huawei.mppdbide.view.erd.contextmenu.ERAttributeVisibility;
import com.huawei.mppdbide.view.erd.contextmenu.ERViewStyle;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Title: EREntityVisual
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
 */

public class EREntityVisual extends Group {

    /** 
     * The entity visual. 
     */
    protected VBox entityVisual;

    /** 
     * The table header. 
     */
    protected VBox tableHeader;

    /** 
     * The primary key visual. 
     */
    protected VBox primaryKeyVisual;

    /** 
     * The foreign key visual. 
     */
    protected VBox foreignKeyVisual;

    /** 
     * The attribute visual. 
     */
    protected VBox attributeVisual;

    /** 
     * The entity. 
     */
    protected AbstractEREntity entity;

    /** 
     * The column index. 
     */
    protected int columnIndex = 0;

    /**
     * Instantiates a new ER entity visual.
     *
     * @param entity the entity
     */
    public EREntityVisual(AbstractEREntity entity) {
        this.entity = entity;
        tableHeader = new VBox(5);
        entityVisual = new VBox(0);
        primaryKeyVisual = new VBox(1);
        foreignKeyVisual = new VBox(1);
        attributeVisual = new VBox(1);
    }

    /**
     * Inits the ER entity visual.
     *
     * @param entity the entity
     */
    public void initEREntityVisual(AbstractEREntity entity) {
        entityVisual.setStyle(IERVisualStyleConstants.ENTITY_VISUAL_STYLE);
        addTableHeader(tableHeader);
        addAttrList(primaryKeyVisual, foreignKeyVisual, attributeVisual);
        addSpecialComponensts(entityVisual);
        getChildren().addAll(entityVisual);
    }

    /**
     * Adds the table header.
     *
     * @param tableHeader the table header
     */
    protected void addTableHeader(VBox tableHeader) {
        tableHeader.setStyle(IERVisualStyleConstants.TABLE_HEADER_STYLE);
        tableHeader.setMinHeight(25);

        // Get the table name
        boolean useFQN = ERViewStyle.isShowFullyQualifiedNames();
        String tableName = useFQN ? entity.getFullyQualifiedName() : entity.getName();
        Text tableNameText = new Text(tableName);
        tableNameText.setStyle(IERVisualStyleConstants.TABLE_HEADER_STYLE);
        tableNameText.setFont(Font.font("Verdana", 12));

        // Get the table comment
        Text tableCommentText = new Text();
        tableCommentText.setStyle(IERVisualStyleConstants.TABLE_HEADER_STYLE);
        tableCommentText.setFont(Font.font("Verdana", 12));
        boolean showComments = ERViewStyle.isShowComments();
        String comments = entity.getTableComments();
        if (showComments && null != comments) {
            tableCommentText.setText(comments);
        }

        // get the table icon
        ImageView tableIcon = new ImageView(
                new javafx.scene.image.Image(IconUtility.getIconImageUri(IiconPath.ICO_TABLE, getClass())));

        HBox tableNameBox = new HBox(8);
        tableNameBox.setStyle(IERVisualStyleConstants.TABLE_HEADER_STYLE);
        tableNameBox.getChildren().addAll(tableIcon, tableNameText);

        Color color = getColor(entity.isCurrentTable());
        tableHeader.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        tableHeader.getChildren().add(tableNameBox);
        if (showComments && null != comments) {
            tableHeader.getChildren().add(tableCommentText);
        }
    }

    /**
     * Adds the attr list.
     *
     * @param primaryKeyVisual the primary key visual
     * @param foreignKeyVisual the foreign key visual
     * @param attributeVisual the attribute visual
     */
    public void addAttrList(VBox primaryKeyVisual, VBox foreignKeyVisual, VBox attributeVisual) {
        for (AbstractERAttribute attribute : entity.getAttributes()) {
            GridPane gridpane = new GridPane();
            gridpane.setAlignment(Pos.CENTER_LEFT);
            gridpane.setPadding(new Insets(3));
            gridpane.setHgap(15);

            columnIndex = 0;
            if (ERViewStyle.isShowIcons()) {
                addIconColumn(gridpane, attribute);
            }
            addColumnNameColumn(gridpane, attribute);

            if (ERViewStyle.isShowDataTypes()) {
                addDtaTypeColumn(gridpane, attribute);
            }

            if (ERViewStyle.isShowNullability() && entity.isHasNotNullColumns()) {
                addNullAbilityColumn(gridpane, attribute);
            }

            if (ERViewStyle.isShowComments() && entity.isHasColumnComments()) {
                addCommentColumn(gridpane, attribute);
            }

            if (attribute.isInPrimaryKey()) {
                primaryKeyVisual.getChildren().add(gridpane);
            } else if (attribute.isInForeignKey()) {
                foreignKeyVisual.getChildren().add(gridpane);
            } else {
                attributeVisual.getChildren().add(gridpane);
            }
        }
    }

    /**
     * Sets the attribute text style.
     *
     * @param text the new attribute text style
     */
    protected void setAttributeTextStyle(Text text) {
        text.setStyle(IERVisualStyleConstants.ATTRIBUTE_TEXT_STYLE);
        text.setFont(Font.font("Verdana", 13));
    }

    /**
     * add the icon of the column including data type and isKey.
     *
     * @param gridpane the gridpane
     * @param attribute the attribute
     */
    private void addIconColumn(GridPane gridpane, AbstractERAttribute attribute) {
        ColumnConstraints iconColumn = new ColumnConstraints();
        gridpane.getColumnConstraints().add(iconColumn);
        HBox iconBox = new HBox(5);
        iconBox.setAlignment(Pos.CENTER);

        ImageView primaryKeyIcon = new ImageView(
                new javafx.scene.image.Image(IconUtility.getIconImageUri(IiconPath.ER_PRIMARY_KEY, getClass())));
        ImageView foreignKeyIcon = new ImageView(
                new javafx.scene.image.Image(IconUtility.getIconImageUri(IiconPath.ER_FOREIGN_KEY, getClass())));
        ImageView dataTypeIcon = new ImageView(new javafx.scene.image.Image(
                IconUtility.getIconImageUri(IconUtility.getObjectImage(attribute), getClass())));
        if (attribute.isInPrimaryKey()) {
            iconBox.getChildren().add(primaryKeyIcon);
        }
        if (attribute.isInForeignKey()) {
            iconBox.getChildren().add(foreignKeyIcon);
        }
        iconBox.getChildren().add(dataTypeIcon);
        gridpane.add(iconBox, columnIndex++, 0);
    }

    private void addColumnNameColumn(GridPane gridpane, AbstractERAttribute attribute) {
        ColumnConstraints columnNameColumn = new ColumnConstraints();
        columnNameColumn.setHgrow(Priority.ALWAYS);
        gridpane.getColumnConstraints().add(columnNameColumn);
        Text columnNameText = new Text(attribute.getName());
        setAttributeTextStyle(columnNameText);
        gridpane.add(columnNameText, columnIndex++, 0);
    }

    /**
     * Adds the dta type column.
     *
     * @param gridpane the gridpane
     * @param attribute the attribute
     */
    public void addDtaTypeColumn(GridPane gridpane, AbstractERAttribute attribute) {
        ColumnConstraints dataTypeColumn = new ColumnConstraints();
        gridpane.getColumnConstraints().add(dataTypeColumn);
        String dataTypeString = attribute.getDataTypes();
        Text dataTypeText = new Text(dataTypeString);
        setAttributeTextStyle(dataTypeText);
        gridpane.add(dataTypeText, columnIndex++, 0);
    }

    /**
     * Adds the null ability column.
     *
     * @param gridpane the gridpane
     * @param attribute the attribute
     */
    protected void addNullAbilityColumn(GridPane gridpane, AbstractERAttribute attribute) {
        ColumnConstraints nullAbilityColumn = new ColumnConstraints(70);
        gridpane.getColumnConstraints().add(nullAbilityColumn);
        Text nullAbilityText = new Text(attribute.getNullability());
        setAttributeTextStyle(nullAbilityText);
        gridpane.add(nullAbilityText, columnIndex++, 0);
    }

    /**
     * Adds the comment column.
     *
     * @param gridpane the gridpane
     * @param attribute the attribute
     */
    protected void addCommentColumn(GridPane gridpane, AbstractERAttribute attribute) {
        ColumnConstraints commentColumn = new ColumnConstraints(250);
        gridpane.getColumnConstraints().add(commentColumn);
        Text commentText = new Text();
        if (attribute.getComments() != null) {
            commentText.setText("-" + attribute.getComments());
        }
        setAttributeTextStyle(commentText);
        commentText.setWrappingWidth(250);
        gridpane.add(commentText, columnIndex++, 0);
    }

    /**
     * Adds the special componensts.
     *
     * @param entityVisual the entity visual
     */
    public void addSpecialComponensts(VBox entityVisual) {
        // Construct Separator Line
        Separator keyLine;
        Separator attrLine;
        keyLine = new Separator();
        keyLine.setStyle(IERVisualStyleConstants.LINE_STYLE);
        attrLine = new Separator();
        attrLine.setStyle(IERVisualStyleConstants.LINE_STYLE);

        if (ERAttributeVisibility.isNone()) {
            entityVisual.getChildren().addAll(tableHeader);
        } else if (ERAttributeVisibility.isPrimaryKey()) {
            entityVisual.getChildren().addAll(tableHeader, keyLine, primaryKeyVisual);
        } else if (ERAttributeVisibility.isAnyKey()) {
            entityVisual.getChildren().addAll(tableHeader, keyLine, primaryKeyVisual, attrLine, foreignKeyVisual);
        } else {
            entityVisual.getChildren().addAll(tableHeader, keyLine, primaryKeyVisual, attrLine, foreignKeyVisual,
                    attributeVisual);
        }
    }

    /**
     * Gets the color.
     *
     * @param isCurrentTable the is current table
     * @return the color
     */
    public Color getColor(boolean isCurrentTable) {
        if (isCurrentTable) {
            return Color.rgb(167, 220, 224);
        } else {
            return Color.rgb(250, 218, 141);
        }
    }
}
