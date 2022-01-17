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

package com.huawei.mppdbide.bl.search;

import java.util.ArrayList;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TriggerMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TriggerObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchNamespace.
 * 
 */

public class SearchNamespace extends Namespace {

    private DebugObjectGroup searchFunctions;
    private TableObjectGroup searchTables;
    private ViewObjectGroup searchViews;
    private ForeignTableGroup searchForeigntables;
    private SequenceObjectGroup searchsequence;
    private SynonymObjectGroup searchSynonym;
    private TriggerObjectGroup searchTrigger;

    /**
     * Instantiates a new search namespace.
     *
     * @param oid the oid
     * @param name the name
     * @param parentDb the parent db
     */
    public SearchNamespace(long oid, String name, Database parentDb) {
        super(oid, name, parentDb);

        searchFunctions = new DebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP, this);
        searchTables = new TableObjectGroup(OBJECTTYPE.TABLE_GROUP, this);
        searchViews = new ViewObjectGroup(this);
        searchForeigntables = new ForeignTableGroup(OBJECTTYPE.FOREIGN_TABLE_GROUP, this);
        searchsequence = new SequenceObjectGroup(OBJECTTYPE.SEQUENCE_GROUP, this);
        searchSynonym = new SynonymObjectGroup(OBJECTTYPE.SYNONYM_GROUP, this);
        searchTrigger = new TriggerObjectGroup(this);
    }

    @Override
    public DebugObjectGroup getFunctions() {
        return this.searchFunctions;
    }

    @Override
    public TableObjectGroup getTables() {
        return this.searchTables;
    }

    /**
     * Gets the tables group.
     *
     * @return the tables group
     */
    public TableObjectGroup getTablesGroup() {
        return this.searchTables;
    }

    @Override
    public ViewObjectGroup getViewGroup() {
        return this.searchViews;
    }

    @Override
    public SequenceObjectGroup getSequenceGroup() {

        return this.searchsequence;
    }

    @Override
    public SynonymObjectGroup getSynonymGroup() {
        return this.searchSynonym;
    }
    
    @Override
    public TriggerObjectGroup getTriggerObjectGroup() {
    	return this.searchTrigger;
	}

    @Override
    public ForeignTableGroup getForeignTablesGroup() {
        return this.searchForeigntables;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {

        return super.hashCode();
    }

    @Override
    public Object[] getChildren() {

        @SuppressWarnings("rawtypes")

        ArrayList<OLAPObjectGroup> objectGroup = new ArrayList<OLAPObjectGroup>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        if (searchFunctions.getSize() > 0) {
            objectGroup.add(this.getFunctions());
        }
        if (searchTables.getSize() > 0) {
            objectGroup.add(this.getTables());
        }
        if (searchViews.getSize() > 0) {
            objectGroup.add(this.getViewGroup());
        }
        if (searchForeigntables.getSize() > 0) {
            objectGroup.add(this.getForeignTablesGroup());
        }
        if (searchsequence.getSize() > 0) {
            objectGroup.add(this.getSequenceGroup());
        }
        if (searchSynonym.getSize() > 0) {
            objectGroup.add(this.getSynonymGroup());
        }
        if (searchTrigger.getSize() > 0) {
            objectGroup.add(this.getTriggerObjectGroup());
        }

        return objectGroup.toArray();

    }

    @Override
    public void addView(ViewMetaData view) {
        this.getViewGroup().addToGroup(view);
        this.getDatabase().getSearchPoolManager().addviewToSearchPool(view);
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        int size = this.getFunctions().getSize() + this.getTables().getSize() + this.getViewGroup().getSize()
                + this.getForeignTablesGroup().getSize() + this.getSequenceGroup().getSize()
                + this.getSynonymGroup().getSize();
        return size;

    }

    @Override
    public void addSequence(SequenceMetadata sequence) {
        this.getSequenceGroup().addToGroup(sequence);
    }

    @Override
    public void clearAllObjects() {
        if (null != searchFunctions) {
            searchFunctions.clear();
        }
        if (null != searchTables) {
            searchTables.clear();
        }
        if (null != searchViews) {
            searchViews.clear();
        }
        if (null != searchForeigntables) {
            searchForeigntables.clear();
        }
        if (null != searchsequence) {
            searchsequence.clear();
        }
        if (null != searchSynonym) {
            searchSynonym.clear();
        }

    }

    /**
     * Adds the to foreign group.
     *
     * @param forTable the for table
     */
    public void addToForeignGroup(TableMetaData forTable) {
        getForeignTablesGroup().addToGroup(forTable);
        getDatabase().getSearchPoolManager().addTableToSearchPool(forTable);
    }

    /**
     * Adds the to sequence group.
     *
     * @param seq the seq
     */
    public void addToSequenceGroup(SequenceMetadata seq) {
        getSequenceGroup().addToGroup(seq);
        getDatabase().getSearchPoolManager().addsequenceToSearchPool(seq);
    }

    /**
     * Adds the to synonym group.
     *
     * @param synonym the syn
     */
    public void addToSynonymGroup(SynonymMetaData syn) {
        getSynonymGroup().addToGroup(syn);
        getDatabase().getSearchPoolManager().addsynonymToSearchPool(syn);
    }

    /**
     * Adds the to trigger group.
     *
     * @param triggerMetaData the trigger metadata
     */
    public void addToTrigerGroup(TriggerMetaData triggerMetaData) {
        getTriggerObjectGroup().addToGroup(triggerMetaData);
        getDatabase().getSearchPoolManager().addTriggerToSearchPool(triggerMetaData);
    }

    /**
     * Adds the view to group.
     *
     * @param view the view
     */
    public void addViewToGroup(ViewMetaData view) {
        addView(view);
        getDatabase().getSearchPoolManager().addviewToSearchPool(view);
    }
}
