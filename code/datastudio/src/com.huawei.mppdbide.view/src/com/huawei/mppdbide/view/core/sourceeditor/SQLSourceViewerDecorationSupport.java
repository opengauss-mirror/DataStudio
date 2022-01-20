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

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.huawei.mppdbide.view.utils.DSDefaultCharacterPairMatcherUtil;
import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLSourceViewerDecorationSupport.
 *
 * @since 3.0.0
 */
public class SQLSourceViewerDecorationSupport extends SourceViewerDecorationSupport {

    /**
     * Instantiates a new SQL source viewer decoration support.
     *
     * @param sourceViewer the source viewer
     * @param overviewRuler the overview ruler
     * @param annotationAccess the annotation access
     * @param sharedTextColors the shared text colors
     */
    public SQLSourceViewerDecorationSupport(ISourceViewer sourceViewer, IOverviewRuler overviewRuler,
            IAnnotationAccess annotationAccess, ISharedTextColors sharedTextColors) {
        super(sourceViewer, overviewRuler, annotationAccess, sharedTextColors);

    }

    /**
     * Install decorations.
     */
    public void installDecorations() {
        // Added for highlight the punctuation marks
        ICharacterPairMatcher matcherQuotes = new DSDefaultCharacterPairMatcher(
                DSDefaultCharacterPairMatcherUtil.getMatchPunctuations(),
                DSDefaultCharacterPairMatcherUtil.SQL_PARTITIONING, true);
        setCharacterPairMatcher(matcherQuotes);
        setMatchingCharacterPainterPreferenceKeys(DSDefaultCharacterPairMatcherUtil.EDITOR_MATCHING_BRACKETS,
                DSDefaultCharacterPairMatcherUtil.EDITOR_MATCHING_BRACKETS_COLOR);

        // Enable bracket highlighting in the preference store
        IPreferenceStore prefStore = UserPreference.getInstance().getPrefernceStore();
        prefStore.setDefault(DSDefaultCharacterPairMatcherUtil.EDITOR_MATCHING_BRACKETS, true);
        prefStore.setDefault(DSDefaultCharacterPairMatcherUtil.EDITOR_MATCHING_BRACKETS_COLOR, "255,0,0");

        install(prefStore);
    }

}
