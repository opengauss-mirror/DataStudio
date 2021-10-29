/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.trie.PatriciaTrie;

import com.huawei.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchPoolManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class SearchPoolManager {

    private PatriciaTrie<TableMetaData> tableTrie;

    private PatriciaTrie<DebugObjects> debugObjectTrie;

    private PatriciaTrie<ViewMetaData> viewTrie;

    private PatriciaTrie<ForeignTable> ftableTrie;

    private PatriciaTrie<PartitionTable> ptableTrie;

    private PatriciaTrie<SequenceMetadata> sequenceTrie;

    private PatriciaTrie<SynonymMetaData> synonymTrie;

    private PatriciaTrie<TriggerMetaData> triggerTrie;

    /**
     * Instantiates a new search pool manager.
     */
    public SearchPoolManager() {
        this.ftableTrie = new PatriciaTrie<ForeignTable>();
        this.ptableTrie = new PatriciaTrie<PartitionTable>();
        this.tableTrie = new PatriciaTrie<TableMetaData>();
        this.debugObjectTrie = new PatriciaTrie<DebugObjects>();
        this.viewTrie = new PatriciaTrie<ViewMetaData>();
        this.sequenceTrie = new PatriciaTrie<SequenceMetadata>();
        this.synonymTrie = new PatriciaTrie<SynonymMetaData>();
        this.triggerTrie = new PatriciaTrie<TriggerMetaData>();
    }

    /**
     * Gets the ftable trie.
     *
     * @return the ftable trie
     */
    public PatriciaTrie<ForeignTable> getFtableTrie() {
        return ftableTrie;
    }

    /**
     * Gets the ptable trie.
     *
     * @return the ptable trie
     */
    public PatriciaTrie<PartitionTable> getPtableTrie() {
        return ptableTrie;
    }

    /**
     * Gets the sequence trie.
     *
     * @return the sequence trie
     */
    public PatriciaTrie<SequenceMetadata> getSequenceTrie() {
        return sequenceTrie;
    }

    /**
     * Gets the synonym trie.
     *
     * @return the synonym trie
     */
    public PatriciaTrie<SynonymMetaData> getSynonymTrie() {
        return synonymTrie;
    }

    /**
     * Gets the debug object trie.
     *
     * @return the debug object trie
     */
    public PatriciaTrie<DebugObjects> getDebugObjectTrie() {
        return debugObjectTrie;
    }

    /**
     * Gets the view trie.
     *
     * @return the view trie
     */
    public PatriciaTrie<ViewMetaData> getViewTrie() {
        return viewTrie;
    }

    /**
     * Gets the table trie.
     *
     * @return the table trie
     */
    public PatriciaTrie<TableMetaData> getTableTrie() {
        return tableTrie;
    }

    /**
     * Gets the trigger trie.
     *
     * @return the trigger trie
     */
    public PatriciaTrie<TriggerMetaData> getTriggerTrie() {
        return triggerTrie;
    }

    /**
     * Clear trie.
     */
    public void clearTrie() {
        getTableTrie().clear();
        getDebugObjectTrie().clear();
        getViewTrie().clear();
        getSequenceTrie().clear();
        getSynonymTrie().clear();
        getPtableTrie().clear();
        getFtableTrie().clear();
        getTriggerTrie().clear();
    }

    /**
     * Addsequence to search pool.
     *
     * @param sequence the sequence
     */
    public void addsequenceToSearchPool(SequenceMetadata sequence) {
        getSequenceTrie().put(sequence.getSearchName(), sequence);
    }

    /**
     * Addsynonym to search pool.
     *
     * @param synonym the synonym
     */
    public void addsynonymToSearchPool(SynonymMetaData synonym) {
        getSynonymTrie().put(synonym.getSearchName(), synonym);
    }

    /**
     * Add TriggerMetaData to search pool.
     *
     * @param TriggerMetaData the trigger metadata
     */
    public void addTriggerToSearchPool(TriggerMetaData trigger) {
        getTriggerTrie().put(trigger.getSearchName(), trigger);
    }

    /**
     * Removes the object from search pool.
     *
     * @param searchKey the search key
     * @param objectType the object type
     */
    public void removeObjectFromSearchPool(String searchKey, OBJECTTYPE objectType) {
        switch (objectType) {
            case FOREIGN_TABLE: {
                if (null == ftableTrie) {
                    return;
                }
                if (ftableTrie.containsKey(searchKey)) {
                    ftableTrie.remove(searchKey);
                }
                break;
            }
            case PARTITION_TABLE: {
                if (null == ptableTrie) {
                    return;
                }
                if (ptableTrie.containsKey(searchKey)) {
                    ptableTrie.remove(searchKey);
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Adds the debug object to search pool.
     *
     * @param debugObject the debug object
     */
    public void addDebugObjectToSearchPool(DebugObjects debugObject) {
        getDebugObjectTrie().put(debugObject.getSearchName(), debugObject);
    }

    /**
     * Removes the sequence from search pool.
     *
     * @param sequence the sequence
     */
    public void removeSequenceFromSearchPool(SequenceMetadata sequence) {
        String searchKey = sequence.getSearchName();
        if (getSequenceTrie().containsKey(searchKey)) {
            getSequenceTrie().remove(searchKey);
        }
    }

    /**
     * Removes the synonym from search pool.
     *
     * @param synonym the synonym
     */
    public void removeSynonymFromSearchPool(SynonymMetaData synonym) {
        String searchKey = synonym.getSearchName();
        if (getSynonymTrie().containsKey(searchKey)) {
            getSynonymTrie().remove(searchKey);
        }
    }

    /**
     * Removes the trigger from search pool.
     *
     * @param trigger the trigger
     */
    public void removeTriggerFromSearchPool(TriggerMetaData trigger) {
        String searchKey = trigger.getSearchName();
        if (getTriggerTrie().containsKey(searchKey)) {
            getTriggerTrie().remove(searchKey);
        }
    }

    /**
     * Adds the table to search pool.
     *
     * @param tableName the table name
     */
    public void addTableToSearchPool(TableMetaData tableName) {

        getTableTrie().put(tableName.getSearchName(), tableName);
    }

    /**
     * Addview to search pool.
     *
     * @param view the view
     */
    public void addviewToSearchPool(ViewMetaData view) {
        getViewTrie().put(view.getSearchName(), view);
    }

    /**
     * Removes the view from search pool.
     *
     * @param view the view
     */
    public void removeViewFromSearchPool(ViewMetaData view) {
        String searchKey = view.getSearchName();
        if (getViewTrie().containsKey(searchKey)) {
            getViewTrie().remove(searchKey);
        }
    }

    /**
     * Removes the table from search pool.
     *
     * @param table the table
     */
    public void removeTableFromSearchPool(TableMetaData table) {
        if (null != table) {
            String searchKey = table.getSearchName();
            if (getTableTrie().containsKey(searchKey)) {
                getTableTrie().remove(searchKey);
            }
        }
    }

    /**
     * Removes the debug object from search pool.
     *
     * @param table the table
     */
    public void removeDebugObjectFromSearchPool(DebugObjects table) {
        if (null != table) {
            String searchKey = table.getSearchName();
            if (getDebugObjectTrie().containsKey(searchKey)) {
                getDebugObjectTrie().remove(searchKey);
            }
        }
    }

    /**
     * Clear object.
     *
     * @param group the group
     */
    public void clearObject(OLAPObjectGroup<? extends ServerObject> group) {
        for (ServerObject obj : group) {
            if (obj instanceof DebugObjects) {
                removeDebugObjectFromSearchPool((DebugObjects) obj);
            } else if (obj instanceof ViewMetaData) {
                removeViewFromSearchPool((ViewMetaData) obj);
            } else if (obj instanceof ForeignTable || obj instanceof PartitionTable) {
                removeObjectFromSearchPool(obj.getSearchName(), obj.getType());
            } else if (obj instanceof SequenceMetadata) {
                removeSequenceFromSearchPool((SequenceMetadata) obj);
            } else if (obj instanceof SynonymMetaData) {
                removeSynonymFromSearchPool((SynonymMetaData) obj);
            } else if (obj instanceof TableMetaData) {
                removeTableFromSearchPool((TableMetaData) obj);
            } else if (obj instanceof TriggerMetaData) {
                removeTriggerFromSearchPool((TriggerMetaData) obj);
            }
        }
    }

}
