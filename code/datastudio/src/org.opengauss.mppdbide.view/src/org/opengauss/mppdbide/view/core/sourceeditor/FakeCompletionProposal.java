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

package org.opengauss.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * 
 * Title: class
 * 
 * Description: The Class FakeCompletionProposal.
 *
 * @since 3.0.0
 */
public class FakeCompletionProposal
        implements ICompletionProposal, ICompletionProposalExtension, ICompletionProposalExtension4 {

    /**
     * The display string.
     */
    String fDisplayString = null;

    /**
     * The offset.
     */
    int fOffset = 0;

    /**
     * Instantiates a new fake completion proposal.
     *
     * @param dispString the disp string
     * @param offset the offset
     */
    public FakeCompletionProposal(String dispString, int offset) {
        fDisplayString = dispString;
        fOffset = offset;
    }

    /**
     * Gets the selection.
     *
     * @param document the document
     * @return the selection
     */
    public Point getSelection(IDocument document) {
        return new Point(fOffset, 0);
    }

    /**
     * Checks if is valid for.
     *
     * @param document the document
     * @param offset the offset
     * @return true, if is valid for
     */
    public boolean isValidFor(IDocument document, int offset) {
        return false;
    }

    /**
     * Gets the trigger characters.
     *
     * @return the trigger characters
     */
    public char[] getTriggerCharacters() {
        return new char[0];
    }

    /**
     * Gets the display string.
     *
     * @return the display string
     */
    public String getDisplayString() {
        return fDisplayString;
    }

    /**
     * Gets the additional proposal info.
     *
     * @return the additional proposal info
     */
    public String getAdditionalProposalInfo() {
        return null;
    }

    /**
     * Gets the context information position.
     *
     * @return the context information position
     */
    public int getContextInformationPosition() {
        return -1;
    }

    /**
     * Checks if is auto insertable.
     *
     * @return true, if is auto insertable
     */
    @Override
    public boolean isAutoInsertable() {
        return false;
    }

    /**
     * Apply.
     *
     * @param document the document
     */
    public void apply(IDocument document) {
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    public Image getImage() {
        return null;
    }

    /**
     * Apply.
     *
     * @param document the document
     * @param trigger the trigger
     * @param offset the offset
     */
    public void apply(IDocument document, char trigger, int offset) {
        // Ignore
    }

    /**
     * Gets the context information.
     *
     * @return the context information
     */
    public IContextInformation getContextInformation() {
        return null;
    }

}
