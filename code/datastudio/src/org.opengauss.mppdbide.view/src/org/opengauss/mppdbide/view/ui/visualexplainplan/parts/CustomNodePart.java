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

package org.opengauss.mppdbide.view.ui.visualexplainplan.parts;

import java.text.DecimalFormat;

import org.eclipse.gef.zest.fx.parts.NodePart;

import org.opengauss.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import org.opengauss.mppdbide.presentation.visualexplainplan.VEPNodeAttributeId;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.visualexplainplan.configuration.VisualExplainPlanViewConfiguration;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Title: CustomNodePart
 * 
 * Description:CustomNodePart
 * 
 * @since 3.0.0
 */
public class CustomNodePart extends NodePart {

    /**
     * Do create visual.
     *
     * @return the group
     */
    @Override
    protected Group doCreateVisual() {

        VBox vbox1;
        HBox hbox1;
        Text headerTextLeft;
        Text headerTextRight;
        ImageView icon;
        UIModelAnalysedPlanNode node = null;

        Object object = getContent().attributesProperty().get(VEPNodeAttributeId.NODE_PROPERTY);
        if (null != object && object instanceof UIModelAnalysedPlanNode) {
            node = (UIModelAnalysedPlanNode) object;
        }

        if (null == node) {
            return null;
        }
        Color color = VisualExplainPlanViewConfiguration.getInstance().getColor(node.getNodeType());

        icon = new ImageView(new javafx.scene.image.Image(
                IconUtility.getIconImageUri(IconUtility.VIS_EXPLAIN_DEFAULT_NODE_IMAGE, getClass())));

        vbox1 = new VBox(2);
        vbox1.setStyle("-fx-border-color: black; -fx-border-width: 1");
        vbox1.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        hbox1 = new HBox(10);
        hbox1.setStyle("-fx-vgap : 2");

        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(getTwoSpace()).append(node.getNodeTitle());
        headerTextLeft = new Text(sb.toString());
        headerTextLeft.setStyle("-fx-alignment:left;");
        headerTextLeft.setFont(Font.font("Verdana", 11));

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(getTwoSpace() + getTwoSpace());
        if (node.getSelfTime() < 1) {
            sb.append("<1ms");
        } else {
            sb.append(new DecimalFormat("##.##").format(node.getSelfTime()) + "ms");
        }
        sb.append(" (").append(new DecimalFormat("##.#").format(node.getTotalTimeContributionPercentage())).append("%")
                .append(")");
        sb.append(getTwoSpace());

        headerTextRight = new Text(sb.toString());
        headerTextRight.setStyle("-fx-alignment:right;");
        headerTextRight.setFont(Font.font("Verdana", 11));

        hbox1.getChildren().addAll(icon, headerTextLeft, headerTextRight);

        return addSpecialComponensts(vbox1, hbox1, node);
    }

    private Group addSpecialComponensts(VBox vbox1, HBox hbox1, UIModelAnalysedPlanNode node) {
        VBox vbox2;
        Separator separatorLine;
        Text heaviestText = null;
        Text costliestText = null;
        Text slowestText = null;
        boolean isNodeSpl = false;

        if (node.isCostliest() || node.isHeaviest() || node.isSlowest()) {
            isNodeSpl = true;
        }

        if (isNodeSpl) {
            separatorLine = new Separator();
            separatorLine.setStyle("-fx-border-style: solid; -fx-border-width: 1 0 0 0; -fx-border-color: black;");

            vbox2 = new VBox(1);

            if (node.isHeaviest()) {
                heaviestText = new Text(
                        ' ' + MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ANALYSIS_HEAVIEST));
                applyFontAndColor(heaviestText);
                vbox2.getChildren().add(heaviestText);
            }

            if (node.isCostliest()) {
                costliestText = new Text(
                        ' ' + MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ANALYSIS_COSTLIEST));
                applyFontAndColor(costliestText);
                vbox2.getChildren().add(costliestText);
            }

            if (node.isSlowest()) {
                slowestText = new Text(
                        ' ' + MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ANALYSIS_SLOWEST));
                applyFontAndColor(slowestText);
                vbox2.getChildren().add(slowestText);
            }

            vbox1.getChildren().addAll(hbox1, separatorLine, vbox2);
        } else {
            vbox1.getChildren().add(hbox1);
        }

        Group grp = new Group(vbox1);
        return grp;
    }

    private String getTwoSpace() {
        return "  ";
    }

    private void applyFontAndColor(Text inputText) {
        Font font = Font.font("Verdana", 11);
        inputText.setFont(font);
        inputText.setFill(Color.RED);
    }
}
