/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLDocumentPartitioner.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SQLDocumentPartitioner extends FastPartitioner {

    /**
     * Instantiates a new SQL document partitioner.
     *
     * @param scanner the scanner
     * @param legalContentTypes the legal content types
     */
    public SQLDocumentPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) {
        super(scanner, legalContentTypes);
    }

    /**
     * Gets the content types.
     *
     * @return the content types
     */
    public static String[] getContentTypes() {
        return new String[] {SQLPartitionScanner.SQL_COMMENT, SQLPartitionScanner.SQL_MULTILINE_COMMENT,
            SQLPartitionScanner.SQL_STRING, SQLPartitionScanner.SQL_DOUBLE_QUOTES_IDENTIFIER};
    }

    /**
     * Connect document.
     *
     * @param doc the doc
     */
    public static void connectDocument(IDocument doc, double fileSize) {
        IDocumentExtension3 ext3Doc = (IDocumentExtension3) doc;

        SQLPartitionScanner scanner = new SQLPartitionScanner();
        IDocumentPartitioner sqlPartitioner = new SQLDocumentPartitioner(scanner, getContentTypes());
        sqlPartitioner.connect(doc);
        if (fileSize <= MPPDBIDEConstants.FILE_LIMIT_FOR_SYNTAX_COLOR) {
            ext3Doc.setDocumentPartitioner(SQLPartitionScanner.SQL_PARTITIONING, sqlPartitioner);
        } 
    }

    /**
     *  clearScanner : to clear mem leak
     */
    public void clearScanner() {
        if (super.fScanner instanceof SQLPartitionScanner) {
            SQLPartitionScanner scanner = (SQLPartitionScanner) super.fScanner;
            scanner.preDestroy();
        }
    }
}
