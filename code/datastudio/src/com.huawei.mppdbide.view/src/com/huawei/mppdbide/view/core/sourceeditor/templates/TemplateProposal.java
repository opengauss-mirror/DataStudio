/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor.templates;

import java.text.MessageFormat;
import java.util.Locale;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.templates.persistence.TemplateIf;

/**
 * 
 * Title: class
 * 
 * Description: The Class TemplateProposal.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class TemplateProposal implements ICompletionProposal, ICompletionProposalExtension,
        ICompletionProposalExtension2, ICompletionProposalExtension3 {
    private TemplateIf template;
    private String displayString;
    private Image image;
    private IRegion region;

    private IRegion fSelectedRegion;
    private IInformationControlCreator informationControlCreator;
    private boolean isMatchCase;

    /**
     * Instantiates a new template proposal.
     *
     * @param template2 the template 2
     * @param region the region
     * @param image the image
     * @param isMatchCase the is match case
     */
    public TemplateProposal(TemplateIf template2, IRegion region, Image image, boolean isMatchCase) {
        this.template = template2;
        this.image = image;
        this.region = region;

        this.displayString = null;
        this.isMatchCase = isMatchCase;
    }

    /**
     * Gets the formatted string.
     *
     * @param args the args
     * @return the formatted string
     */
    private String getFormattedString(Object[] args) {
        return MessageFormat.format(" {0} - {1}", args);
    }

    /**
     * Gets the display string.
     *
     * @return the display string
     */
    public String getDisplayString() {
        if (displayString == null) {
            String[] arguments = new String[] {template.getName(), template.getDescription()};
            displayString = getFormattedString(arguments);
        }

        return displayString;
    }

    /**
     * Sets the display string.
     *
     * @param displayString the new display string
     */
    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

    /**
     * Gets the template.
     *
     * @return the template
     */
    protected final TemplateIf getTemplate() {
        return template;
    }

    /**
     * Apply.
     *
     * @param document the document
     */
    @Override
    public void apply(IDocument document) {

    }

    /**
     * Gets the selection.
     *
     * @param document the document
     * @return the selection
     */
    @Override
    public Point getSelection(IDocument document) {
        return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
    }

    /**
     * Gets the additional proposal info.
     *
     * @return the additional proposal info
     */
    @Override
    public String getAdditionalProposalInfo() {
        return template.getPattern();
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    @Override
    public Image getImage() {
        return image;
    }

    /**
     * Gets the context information.
     *
     * @return the context information
     */
    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    /**
     * Sets the information control creator.
     *
     * @param informationControlCreator the new information control creator
     */
    public final void setInformationControlCreator(IInformationControlCreator informationControlCreator) {
        this.informationControlCreator = informationControlCreator;
    }

    /**
     * Gets the information control creator.
     *
     * @return the information control creator
     */
    @Override
    public IInformationControlCreator getInformationControlCreator() {

        return informationControlCreator;
    }

    /**
     * Gets the prefix completion text.
     *
     * @param document the document
     * @param completionOffset the completion offset
     * @return the prefix completion text
     */
    @Override
    public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
        return template.getName();
    }

    /**
     * Gets the prefix completion start.
     *
     * @param document the document
     * @param completionOffset the completion offset
     * @return the prefix completion start
     */
    @Override
    public int getPrefixCompletionStart(IDocument document, int completionOffset) {
        return getReplaceOffset();
    }

    /**
     * Apply.
     *
     * @param viewer the viewer
     * @param trigger the trigger
     * @param stateMask the state mask
     * @param offset the offset
     */
    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
        IDocument document = viewer.getDocument();
        try {
            int start = getReplaceOffset();
            int end = Math.max(getReplaceEndOffset(), offset);

            // insert template string
            document.replace(start, end - start, template.getPattern());
            fSelectedRegion = new Region(getCaretOffSet(template.getPattern()) + start, 0);
        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("TemplateProposal: BadLocationException occurred.", exception);
        }

    }

    /**
     * Selected.
     *
     * @param viewer the viewer
     * @param smartToggle the smart toggle
     */
    @Override
    public void selected(ITextViewer viewer, boolean smartToggle) {

    }

    /**
     * Unselected.
     *
     * @param viewer the viewer
     */
    @Override
    public void unselected(ITextViewer viewer) {
    }

    /**
     * Validate.
     *
     * @param document the document
     * @param offset the offset
     * @param event the event
     * @return true, if successful
     */
    @Override
    public boolean validate(IDocument document, int offset, DocumentEvent event) {
        try {
            int replaceOffset = getReplaceOffset();
            if (offset >= replaceOffset) {
                String content = document.get(replaceOffset, offset - replaceOffset);
                if (!isMatchCase) {
                    return template.getName().toLowerCase(Locale.ENGLISH)
                            .startsWith(content.toLowerCase(Locale.ENGLISH));
                } else {
                    return template.getName().startsWith(content);
                }
            }
        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("TemplateProposal: BadLocationException occurred.", exception);
        }

        return false;
    }

    /**
     * Apply.
     *
     * @param document the document
     * @param trigger the trigger
     * @param offset the offset
     */
    @Override
    public void apply(IDocument document, char trigger, int offset) {
        // not in use
    }

    /**
     * Checks if is valid for.
     *
     * @param document the document
     * @param offset the offset
     * @return true, if is valid for
     */
    @Override
    public boolean isValidFor(IDocument document, int offset) {
        return false;
    }

    /**
     * Gets the trigger characters.
     *
     * @return the trigger characters
     */
    @Override
    public char[] getTriggerCharacters() {
        return new char[0];
    }

    /**
     * Gets the context information position.
     *
     * @return the context information position
     */
    @Override
    public int getContextInformationPosition() {
        return region.getOffset();
    }

    /**
     * Gets the replace offset.
     *
     * @return the replace offset
     */
    private final int getReplaceOffset() {
        return region.getOffset();
    }

    /**
     * Gets the replace end offset.
     *
     * @return the replace end offset
     */
    private final int getReplaceEndOffset() {
        return region.getOffset() + region.getLength();
    }

    /**
     * Gets the caret off set.
     *
     * @param buffer the buffer
     * @return the caret off set
     */
    private int getCaretOffSet(String buffer) {
        return buffer.length();
    }
}
