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

package org.opengauss.mppdbide.gauss.sqlparser.stmt.node;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.gauss.format.processor.listener.IFormarProcessorListener;

/**
 * 
 * Title: TParseTreeNode
 *
 * @since 3.0.0
 */
public abstract class TParseTreeNode {

    private List<String> nextComments = null;

    private List<String> preText = null;

    private List<String> postText = null;

    private IFormarProcessorListener formatListener = null;

    private TParseTreeNode nextNode = null;

    private TParseTreeNode currentObject = null;

    private TParseTreeNode autoStartBean = null;

    private Boolean checkPreviousNewLine = Boolean.FALSE;

    /**
     * Sets the previous object.
     *
     * @param currOjb the new previous object
     */
    public void setPreviousObject(TParseTreeNode currOjb) {
        if (null == currOjb) {
            return;
        }

        if (null != currentObject) {
            currentObject.setNextNode(currOjb);
        } else {
            autoStartBean = currOjb;
        }

        currentObject = currOjb;
    }

    /**
     * Gets the next comments.
     *
     * @return the next comments
     */
    public List<String> getNextComments() {
        return nextComments;
    }

    /**
     * Sets the next comments.
     *
     * @param nextComments the new next comments
     */
    public void setNextComments(List<String> nextComments) {
        this.nextComments = nextComments;
    }

    /**
     * Adds the comment.
     *
     * @param nodeText the node text
     */
    public void addComment(String nodeText) {
        if (null == nextComments) {
            nextComments = new ArrayList<String>(15);
        }
        nextComments.add(nodeText);
    }

    /**
     * Gets the next node.
     *
     * @return the next node
     */
    public TParseTreeNode getNextNode() {
        return nextNode;
    }

    /**
     * Sets the next node.
     *
     * @param nextNode the new next node
     */
    public void setNextNode(TParseTreeNode nextNode) {
        this.nextNode = nextNode;
    }

    /**
     * Adds the pre text.
     *
     * @param nodeText the node text
     */
    public void addPreText(String nodeText) {
        if (null == preText) {
            preText = new ArrayList<String>(15);
        }
        preText.add(nodeText);
    }

    /**
     * Adds the post text.
     *
     * @param nodeText the node text
     */
    public void addPostText(String nodeText) {
        if (null == postText) {
            postText = new ArrayList<String>(15);
        }
        postText.add(nodeText);
    }

    /**
     * Gets the pre text.
     *
     * @return the pre text
     */
    public List<String> getPreText() {
        return preText;
    }

    /**
     * Sets the pre text.
     *
     * @param preText the new pre text
     */
    public void setPreText(List<String> preText) {
        this.preText = preText;
    }

    /**
     * Gets the post text.
     *
     * @return the post text
     */
    public List<String> getPostText() {
        return postText;
    }

    /**
     * Sets the post text.
     *
     * @param postText the new post text
     */
    public void setPostText(List<String> postText) {
        this.postText = postText;
    }

    /**
     * Gets the start node.
     *
     * @return the start node
     */
    public abstract TParseTreeNode getStartNode();

    /**
     * Gets the format listener.
     *
     * @return the format listener
     */
    public IFormarProcessorListener getFormatListener() {
        return formatListener;
    }

    /**
     * Sets the format listener.
     *
     * @param formatListener the new format listener
     */
    public void setFormatListener(IFormarProcessorListener formatListener) {
        this.formatListener = formatListener;
    }

    /**
     * Gets the auto start bean.
     *
     * @return the auto start bean
     */
    public TParseTreeNode getAutoStartBean() {
        return autoStartBean;
    }

    /**
     * Gets the check previous new line.
     *
     * @return the check previous new line
     */
    public Boolean getCheckPreviousNewLine() {
        return checkPreviousNewLine;
    }

    /**
     * Sets the check previous new line.
     *
     * @param checkPreviousNewLine the new check previous new line
     */
    public void setCheckPreviousNewLine(Boolean checkPreviousNewLine) {
        this.checkPreviousNewLine = checkPreviousNewLine;
    }
}
