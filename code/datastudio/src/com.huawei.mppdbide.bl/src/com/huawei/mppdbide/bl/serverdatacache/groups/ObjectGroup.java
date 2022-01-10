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

package com.huawei.mppdbide.bl.serverdatacache.groups;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.StringUtils;

import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.ShowMoreObject;
import com.huawei.mppdbide.bl.util.BLUtils;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: class Description: The Class ObjectGroup. 
 * @param <T> the generic type
 * @since 17 May, 2019
 */

public class ObjectGroup<T extends ServerObject> implements Iterable<T> {

    /**
     * The type.
     */
    protected OBJECTTYPE type;

    /**
     * The display name.
     */
    protected String displayName = "";

    private ConcurrentHashMap<Long, T> objMap;

    private Object parent;

    private final PatriciaTrie<T> trie;

    private int objectToDisplayCount;

    private IBLPreference blPreference;

    /**
     * Instantiates a new object group.
     *
     * @param type the type
     * @param parentObject the parent object
     */
    public ObjectGroup(OBJECTTYPE type, Object parentObject) {
        this.type = type;

        setDisplayName(type);
        objMap = new ConcurrentHashMap<Long, T>(4);
        trie = new PatriciaTrie<T>();
        this.parent = parentObject;
        blPreference = BLPreferenceManager.getInstance().getBLPreference();
        objectToDisplayCount = blPreference.getLazyRenderingObjectCount();
    }

    /**
     * Sets the display name.
     *
     * @param type the new display name
     */
    private void setDisplayName(OBJECTTYPE type) {
        setDisplayNameFirst(type);
        switch (type) {
            case DATATYPE_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.DATATYPE_NAME);
                break;
            }
            case ACCESSMETHOD_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.ACCESS_METHOD_NAME);
                break;
            }
            case FOREIGN_TABLE_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.FOREIGN_TABLE_GROUP);
                break;
            }
            case USER_ROLE_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_GROUP_DISPLAY_NAME);
                break;
            }
            default: {
                break;
            }
        }
        setDisplayNameForCommons(type);
    }

    private void setDisplayNameForCommons(OBJECTTYPE type) {
        switch (type) {
            case VIEW_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.VIEWS_NAME);
                break;
            }
            case SEQUENCE_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.SEQUENCE);
                break;
            }
            case SYNONYM_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.SYNONYM_GROUP_NAME);
                break;
            }
            case TRIGGER_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.TRIGGER_GROUP_NAME);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Sets the display name first.
     *
     * @param type2 the new display name first
     */
    private void setDisplayNameFirst(OBJECTTYPE type2) {
        switch (type2) {
            case TABLESPACE_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.TABLESPACES_NAME);
                break;
            }
            case DATABASE_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.DATABASES_NAME);
                break;
            }
            case USER_NAMESPACE_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.USER_NAMESPACE_NAME);
                break;
            }
            case SYSTEM_NAMESPACE_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.SYSTEM_NAMESPACE_NAME);
                break;
            }
            case FUNCTION_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.FUNCTION_PROCEDURE_NAME);
                break;
            }
            case TABLE_GROUP: {
                this.displayName = MessageConfigLoader.getProperty(IMessagesConstants.TABLES_NAME);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Gets the object group type.
     *
     * @return the object group type
     */
    public OBJECTTYPE getObjectGroupType() {
        return type;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return displayName;
    }

    /**
     * Adds the to group.
     *
     * @param obj the obj
     */
    public void addToGroup(T obj) {
        objMap.put(obj.getOid(), obj);
        addServerObjectToSortList(obj);
    }

    /**
     * Removes the from group.
     *
     * @param oid the oid
     */
    public void removeFromGroup(long oid) {
        if (objMap.containsKey(oid)) {
            T obj = objMap.get(oid);
            objMap.remove(oid);
            removeServerObjectFromSortList(obj);
        }
    }

    /**
     * Contains.
     *
     * @param oid the oid
     * @return true, if successful
     */
    public boolean contains(long oid) {
        if (objMap.containsKey(oid)) {
            return true;
        }
        return false;
    }

    /**
     * Gets the object by id.
     *
     * @param oid the oid
     * @return the object by id
     */
    public T getObjectById(long oid) {
        return objMap.get(oid);
    }

    /**
     * Add a server object.
     *
     * @param object the object
     */
    private void addServerObjectToSortList(T object) {
        synchronized (trie) {
            trie.put(object.getSearchName(), object);
        }
    }

    /**
     * Removes the server object from sort list.
     *
     * @param object the object
     * @return true, if successful
     */
    private boolean removeServerObjectFromSortList(T object) {
        synchronized (trie) {
            trie.remove(object.getSearchName());
        }

        return false;
    }

    /**
     * Gets the matching.
     *
     * @param prefix the prefix
     * @return the matching
     */
    public SortedMap<String, T> getMatching(String prefix) {
        String prefixShort = "";
        if (null != prefix) {
            if (prefix.length() > 3) {
                prefixShort = prefix.substring(0, 2);
            } else {
                prefixShort = prefix;
            }
        }
        SortedMap<String, T> map = getAllObjectWithPrefixCaseInsensitive(prefixShort);
        SortedMap<Long, ServerObject> serverObjMap = new TreeMap<Long, ServerObject>();
        for (ServerObject obj : map.values()) {
            serverObjMap.put(obj.getOid(), obj);
        }
        SortedMap<String, ServerObject> resultMap = new TreeMap<String, ServerObject>();
        for (ServerObject obj : serverObjMap.values()) {
            if (prefix != null
                    && obj.getSearchName().toLowerCase(Locale.ENGLISH).startsWith(prefix.toLowerCase(Locale.ENGLISH))) {
                resultMap.put(obj.getSearchName(), obj);
            }
        }

        return (SortedMap<String, T>) resultMap;
    }

    /**
     * Gets the all object with prefix case insensitive.
     *
     * @param text the text
     * @return the all object with prefix case insensitive
     */
    private SortedMap<String, T> getAllObjectWithPrefixCaseInsensitive(String text) {
        SortedMap<String, T> map = new TreeMap<String, T>();
        ArrayList<String> prefixList = BLUtils.getAllCombinationsOfPrefix(text);
        for (String prefix : prefixList) {
            map.putAll(trie.prefixMap(String.valueOf(prefix)));
        }
        return map;
    }

    /**
     * Gets the.
     *
     * @param name the name
     * @return the t
     */
    public T get(String name) {
        SortedMap<String, T> map = getMatching(name);

        for (Entry<String, T> entry : map.entrySet()) {
            T object = entry.getValue();
            if (name.equals(object.getName())) {
                return object;
            }
        }

        return null;
    }

    /**
     * Gets the matching hyper link.
     *
     * @param prefix the prefix
     * @return the matching hyper link
     */
    public SortedMap<String, T> getMatchingHyperLink(String prefix) {

        SortedMap<String, T> map = getMatching(prefix);
        SortedMap<String, T> retMap = new TreeMap<String, T>();

        for (Entry<String, T> entry : map.entrySet()) {
            T object = entry.getValue();
            if (prefix.equals(object.getName())) {
                retMap.put(entry.getKey(), object);
            }
        }
        return retMap;

    }

    /**
     * Gets the matching tables hyper link.
     *
     * @param prefix the prefix
     * @return the matching tables hyper link
     */
    public SortedMap<String, T> getMatchingTablesHyperLink(String prefix) {
        SortedMap<String, T> map = new TreeMap<String, T>();
        Entry<String, T> entry = trie.select(prefix);
        if (null != entry) {
            String key = entry.getKey();
            String[] keys = key.split(" ");
            String keyToMatch = keys[0];
            if (keyToMatch.equals(prefix)) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;

    }

    /**
     * Gets the sorted server object list.
     *
     * @return the sorted server object list
     */

    public ArrayList<T> getSortedServerObjectList() {
        try {
            ArrayList<T> list = new ArrayList<T>(10);
            list.addAll(trie.values());
            return list;
        } catch (ConcurrentModificationException e) {
            return new ArrayList<T>(10);
        }
    }

    /**
     * Clear.
     */
    public void clear() {
        objMap.clear();
        trie.clear();
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        return trie.size();
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Object getParent() {
        return this.parent;
    }

    /**
     * Iterator.
     *
     * @return the iterator
     */
    @Override
    public Iterator<T> iterator() {
        return trie.values().iterator();
    }

    /**
     * Gets the display label.
     *
     * @return the display label
     */
    public String getDisplayLabel() {
        return getName();
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public Object[] getChildren() {

        List<T> sortedServerObjectList = this.getSortedServerObjectList();

        String text = FilterObject.getInstance().getFilterText();

        if (StringUtils.isEmpty(text)) {
            return addMoreObjectsOption(sortedServerObjectList);
        }

        if (getObjectType()) {
            sortedServerObjectList = sortedServerObjectList.stream().filter(
                    article -> article.getName().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH)))
                    .collect(Collectors.toList());
        }
        return addMoreObjectsOption(sortedServerObjectList);
    }

    /**
     * Add "More Objects.." option for rendering next batch of objects
     * 
     * @param sorted list of all loaded objects
     * @return sublist with "More Objects" option
     */
    private Object[] addMoreObjectsOption(List<T> sortedServerObjectList) {

        if (sortedServerObjectList.size() >= this.objectToDisplayCount) {
            List<T> subList = sortedServerObjectList.subList(0, objectToDisplayCount);
            subList.add((T) new ShowMoreObject(OBJECTTYPE.SHOW_MORE_OBJECTS, this));
            return subList.toArray();
        } else {
            return sortedServerObjectList.toArray();
        }

    }

    /**
     * Increment show object count.
     */
    public void incrementShowObjectCount() {
        this.objectToDisplayCount += blPreference.getLazyRenderingObjectCount();
    }

    /**
     * Gets the object type.
     *
     * @return the object type
     */
    public boolean getObjectType() {
        return this.type == OBJECTTYPE.TABLE_GROUP || this.type == OBJECTTYPE.FUNCTION_GROUP
                || this.type == OBJECTTYPE.VIEW_GROUP || this.type == OBJECTTYPE.SEQUENCE_GROUP
                || this.type == OBJECTTYPE.FOREIGN_TABLE_GROUP || this.type == OBJECTTYPE.INDEX_GROUP
                || this.type == OBJECTTYPE.PARTITION_GROUP || this.type == OBJECTTYPE.SYNONYM_GROUP;
    }

    /**
     * Gets the object browser label.
     *
     * @return the object browser label
     */
    public String getObjectBrowserLabel() {
        int size = getSize();
        return getName() + " (" + size + ") ";
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        final int prime = MPPDBIDEConstants.PRIME_31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getParent() == null) ? 0 : getParent().hashCode());
        return result;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ObjectGroup)) {
            return false;
        }

        ObjectGroup<?> other = (ObjectGroup<?>) obj;
        if (getName() == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }

        if (getParent() == null) {
            if (other.getParent() != null) {
                return false;
            }
        } else if (!getParent().equals(other.getParent())) {
            return false;
        }

        if (this.type != other.getObjectGroupType()) {
            return false;
        }

        return true;
    }

    /**
     * Removes the.
     *
     * @param obj the obj
     */
    public void remove(T obj) {
        removeFromGroup(obj.getOid());
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {

        return null;
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    public INamespace getNamespace() {
        return null;
    }

    /**
     * Gets the children without filter.
     *
     * @return the children without filter
     */
    public Object[] getChildrenWithoutFilter() {
        return this.getSortedServerObjectList().toArray();
    }

}
