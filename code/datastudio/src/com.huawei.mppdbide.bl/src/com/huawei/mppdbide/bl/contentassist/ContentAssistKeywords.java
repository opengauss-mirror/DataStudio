/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.contentassist;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.trie.PatriciaTrie;

import com.huawei.mppdbide.adapter.keywordssyntax.KeywordsFactoryProvider;
import com.huawei.mppdbide.adapter.keywordssyntax.KeywordsToTrieConverter;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.keyword.KeywordObject;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectList;

/**
 * 
 * Title: class ContentAssistKeywords
 * 
 * Description: The Class ContentAssistKeywords.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author swx316469
 * @version [DataStudio 8.0.0, 28 Aug, 2019]
 * @since 28 Aug, 2019
 */
public class ContentAssistKeywords {

    private static final Object INSTANCE_LOCK = new Object();

    private OLAPObjectList<KeywordObject> olapKeywords;

    private ObjectList<KeywordObject> defaultKeywords;

    private static volatile ContentAssistKeywords instance = null;

    /**
     * Instantiates a new content assist keywords.
     */
    private ContentAssistKeywords() {
        olapKeywords = new OLAPObjectList<KeywordObject>(OBJECTTYPE.KEYWORDS, this);
        defaultKeywords = new ObjectList<KeywordObject>(OBJECTTYPE.KEYWORDS, this);
    }

    /**
     * Load keywords.
     *
     * @param database the database
     */
    public void loadKeywords(Database database) {
        if (null == database && defaultKeywords.getSize() == 0) {
            fetchKeywords(defaultKeywords, null);
        } else if (null != database && database.getDBType() == DBTYPE.OPENGAUSS && olapKeywords.getSize() == 0) {
            fetchKeywords(olapKeywords, database);
        }

    }

    /**
     * Gets the single instance of ContentAssistKeywords.
     *
     * @param database the database
     * @return single instance of ContentAssistKeywords
     */
    public static ContentAssistKeywords getInstance() {
        if (null == instance) {
            synchronized (INSTANCE_LOCK) {
                if (null == instance) {
                    instance = new ContentAssistKeywords();
                }
            }
        }
        return instance;
    }

    /**
     * Fetch keywords.
     *
     * @param olapLeywords2 the olap leywords 2
     * @param database the database
     */
    private void fetchKeywords(ObjectList<KeywordObject> olapLeywords2, Database database) {
        SQLSyntax syntax = getSQLSyntax(database);
        if (null != syntax) {
            int counter = 1;
            counter = addKeywords(syntax.getReservedkrywords(), counter, olapLeywords2);
            counter = addKeywords(syntax.getUnreservedkrywords(), counter, olapLeywords2);
            addKeywords(syntax.getConstants(), counter, olapLeywords2);
        }

    }

    /**
     * Adds the keywords.
     *
     * @param trie the trie
     * @param counter the counter
     * @param keywords the keywords
     * @return the int
     */
    private int addKeywords(PatriciaTrie<String> trie, int counter, ObjectList<KeywordObject> keywords) {
        for (String keyword : trie.keySet()) {
            KeywordObject keywordObj = new KeywordObject(counter, keyword, OBJECTTYPE.KEYWORDS, false);
            keywords.addItem(keywordObj);
            counter++;
        }
        return counter;
    }

    /**
     * Gets the SQL syntax.
     *
     * @param database the database
     * @return the SQL syntax
     */
    public SQLSyntax getSQLSyntax(Database database) {
        SQLSyntax syntax = new SQLSyntax();
        if (null != database) {
            syntax = database.getSqlSyntax();
        } else {
            syntax = KeywordsToTrieConverter.convertKeywordstoTrie(syntax,
                    KeywordsFactoryProvider.getKeywordsFactory().getKeywords());
        }
        return syntax;

    }

    /**
     * Find matching OLAP keyword.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findMatchingOLAPKeyword(String prefix) {
        SortedMap<String, ServerObject> keyWords = new TreeMap<String, ServerObject>();
        keyWords.putAll(olapKeywords.findMatching(prefix));
        return keyWords;
    }

    /**
     * Find matching default keyword.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findMatchingDefaultKeyword(String prefix) {
        SortedMap<String, ServerObject> keyWords = new TreeMap<String, ServerObject>();
        keyWords.putAll(defaultKeywords.findMatching(prefix));
        return keyWords;
    }

    /**
     * Clear OLAP keywords.
     *
     * @param collection the collection
     */
    public void clearOLAPKeywords(Collection<Database> collection) {
        Iterator<Database> dbItr = collection.iterator();
        Database db = null;
        boolean hasNext = dbItr.hasNext();

        while (hasNext) {
            db = dbItr.next();
            if (db.isConnected() && db.getDBType() == DBTYPE.OPENGAUSS) {
                return;
            }
            hasNext = dbItr.hasNext();
        }
        olapKeywords.clear();
    }

    /**
     * Clear.
     */
    public void clear() {
        olapKeywords.clear();
    }

}
