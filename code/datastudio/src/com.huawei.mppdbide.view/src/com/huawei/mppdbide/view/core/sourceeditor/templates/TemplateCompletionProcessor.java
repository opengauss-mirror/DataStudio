/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.FakeCompletionProposal;
import com.huawei.mppdbide.view.core.sourceeditor.SQLContentAssist;
import com.huawei.mppdbide.view.core.sourceeditor.templates.persistence.TemplateIf;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class TemplateCompletionProcessor.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class TemplateCompletionProcessor implements IContentAssistProcessor {
    private IExecTimer timer;
    private SQLContentAssist assistant = null;
    private static final char NEW_LINE_CHAR = '\n';

    /**
     * Instantiates a new template completion processor.
     *
     * @param assitant the assitant
     */
    public TemplateCompletionProcessor(SQLContentAssist assitant) {
        this.assistant = assitant;
    }

    /**
     * Compute completion proposals.
     *
     * @param viewer the viewer
     * @param offset the offset
     * @return the i completion proposal[]
     */
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        timer = new ExecTimer("Code Template Load Time");
        timer.start();

        assistant.setCurrentPrefix(new String[0]);

        String fullContent = viewer.getDocument().get();

        int beginOffset = 0;

        if (offset > UIConstants.NAME_MAX_LEN * 2) {
            beginOffset = offset - UIConstants.NAME_MAX_LEN * 2;
        }

        String fullPretext = fullContent.substring(beginOffset, offset);
        String prefix = findString(fullPretext);
        if (prefix == null) {
            prefix = "";
        }

        this.assistant.setEmptyMessage(MessageConfigLoader.getProperty(IMessagesConstants.NO_PROPOSAL));

        final List<TemplateProposal> templateProposals = new ArrayList<>();

        TemplateStore ts = TemplateStoreManager.getInstance().getTemplateStore();
        boolean isMatchCase = TemplateStoreManager.getInstance().isMatchCase();

        TemplateIf[] templates = ts.getMatchedTemplates(prefix, isMatchCase);

        /* No matching template found, then show No proposal. */
        if (null == templates) {
            return new ICompletionProposal[] {
                new FakeCompletionProposal(MessageConfigLoader.getProperty(IMessagesConstants.NO_PROPOSAL), offset)};
        }

        for (TemplateIf template : templates) {
            templateProposals
                    .add(new TemplateProposal(template, new Region(offset - prefix.length(), 0), null, isMatchCase));
        }

        Collections.sort(templateProposals, new Comparator<TemplateProposal>() {
            @Override
            public int compare(TemplateProposal o1, TemplateProposal o2) {
                return o1.getDisplayString().compareTo(o2.getDisplayString());
            }
        });

        try {
            timer.stopAndLog();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Exception while getting elapsed time", exception);
        }

        return templateProposals.toArray(new ICompletionProposal[templateProposals.size()]);
    }

    /**
     * Compute context information.
     *
     * @param viewer the viewer
     * @param offset the offset
     * @return the i context information[]
     */
    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {

        return new IContextInformation[0];
    }

    /**
     * Gets the completion proposal auto activation characters.
     *
     * @return the completion proposal auto activation characters
     */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {

        return new char[0];
    }

    /**
     * Gets the context information auto activation characters.
     *
     * @return the context information auto activation characters
     */
    @Override
    public char[] getContextInformationAutoActivationCharacters() {

        return new char[0];
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    @Override
    public String getErrorMessage() {

        return null;
    }

    /**
     * Gets the context information validator.
     *
     * @return the context information validator
     */
    @Override
    public IContextInformationValidator getContextInformationValidator() {

        return null;
    }

    /**
     * Find string.
     *
     * @param pretext the pretext
     * @return the string
     */
    private String findString(String pretext) {
        if (pretext.trim().isEmpty()) {
            return null;
        }
        int pos = pretext.length() - 1;
        int ch = '\0';

        while (pos >= 0) {
            ch = pretext.charAt(pos);

            if (NEW_LINE_CHAR == ch) {
                break;
            } else if (Character.isWhitespace(ch)) {
                break;
            }

            --pos;
        }

        String retStr = null;
        retStr = pretext.substring(pos + 1);
        retStr = retStr.trim();

        return retStr;
    }
}
