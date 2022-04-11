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

package org.opengauss.mppdbide.bl.serverdatacache.groups;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.trie.PatriciaTrie;

import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectList.
 * 
 * @param <T> the generic type
 */

public class ObjectList<T extends ServerObject> {

    /**
     * The type.
     */
    protected OBJECTTYPE type;

    /**
     * The name.
     */
    protected String name;
    private final ArrayList<T> list;
    private Object parent;
    private final PatriciaTrie<T> trie;

    /**
     * Instantiates a new object list.
     *
     * @param type the type
     * @param parentObject the parent object
     */
    public ObjectList(OBJECTTYPE type, Object parentObject) {
        this.type = type;

        switch (type) {
            case COLUMN_GROUP: {
                this.name = MessageConfigLoader.getProperty(IMessagesConstants.COLUMNS_NAME);
                break;
            }
            case CONSTRAINT_GROUP: {
                this.name = MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINTS_NAME);
                break;
            }
            case INDEX_GROUP: {
                this.name = MessageConfigLoader.getProperty(IMessagesConstants.INDEXES_NAME);
                break;
            }
            case VIEW_COLUMN_GROUP: {
                this.name = MessageConfigLoader.getProperty(IMessagesConstants.VIEW_COLUMNS_NAME);
                break;
            }
            case PARTITION_GROUP: {
                this.name = MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_GROUP_NAME);
                break;
            }
            default: {
                break;
            }
        }

        list = new ArrayList<T>(4);
        trie = new PatriciaTrie<T>();
        this.parent = parentObject;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public OBJECTTYPE getType() {
        return type;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list.
     *
     * @return the list
     */
    public ArrayList<T> getList() {
        return list;
    }

    /**
     * Find matching.
     *
     * @param prefixOne the prefix
     * @return the sorted map
     */
    public SortedMap<String, T> findMatching(String prefixOne) {
        String prefixShort = "";
        if (null != prefixOne) {
            if (prefixOne.length() > 3) {
                prefixShort = prefixOne.substring(0, 2);
            } else {
                prefixShort = prefixOne;
            }
        }
        SortedMap<String, T> map = getAllObjectWithPrefixCaseInsensitive(prefixShort);
        SortedMap<Long, ServerObject> serverObjMap = new TreeMap<Long, ServerObject>();
        for (ServerObject obj : map.values()) {
            serverObjMap.put(obj.getOid(), obj);
        }
        SortedMap<String, ServerObject> resultMap = new TreeMap<String, ServerObject>();
        for (ServerObject obj : serverObjMap.values()) {
            if (prefixOne != null && obj.getSearchName().toLowerCase(Locale.ENGLISH)
                    .startsWith(prefixOne.toLowerCase(Locale.ENGLISH))) {
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
     * Move item.
     *
     * @param index the index
     * @param isUp the is up
     */
    public void moveItem(int index, boolean isUp) {
        T item = this.list.get(index);
        int newIndex = isUp ? (index - 1) : (index + 1);

        this.list.remove(index);
        this.list.add(newIndex, item);
    }

    /**
     * Gets the item.
     *
     * @param index the index
     * @return the item
     */
    public T getItem(int index) {
        return this.list.get(index);
    }

    /**
     * Adds the item.
     *
     * @param item the item
     */
    public void addItem(T item) {
        this.list.add(item);
        this.trie.put(item.getSearchName(), item);
    }

    /**
     * Adds the item at index.
     *
     * @param item the item
     * @param index the index
     */
    public void addItemAtIndex(T item, int index) {
        if (this.list.size() < index) {
            this.list.add(item);
        } else {
            this.list.add(index, item);
        }
        this.trie.put(item.getSearchName(), item);
    }

    /**
     * Removes the item by idx.
     *
     * @param index the index
     */
    public void removeItemByIdx(int index) {

        T item = getItem(index);
        this.trie.remove(item.getSearchName());
        this.list.remove(index);
    }

    /**
     * Removes the.
     *
     * @param item the item
     */
    public void remove(T item) {
        if (item != null) {
            this.trie.remove(item.getSearchName());
            this.list.remove(item);
        }
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    public int getSize() {
        return this.list.size();
    }

    /**
     * Clear.
     */
    public void clear() {
        this.trie.clear();
        this.list.clear();
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
     * Gets the.
     *
     * @param objName the obj name
     * @return the t
     */
    public T get(String objName) {
        Entry<String, T> entry = trie.select(objName);
        return null == entry ? null : entry.getValue();
    }

    /**
     * Gets the object by id.
     *
     * @param oid the oid
     * @return the object by id
     */
    public T getObjectById(long oid) {
        for (T item : list) {
            if (item.getOid() == oid) {
                return item;
            }
        }

        return null;
    }

    @Override
    public int hashCode() {
        final int primeNumber = 31;
        int result = 1;
        result = getHashCodeForCurrentObject(primeNumber, result);
        result = getHashCodeForParentObject(primeNumber, result);
        return result;
    }

    /**
     * Gets the hash code for parent object.
     *
     * @param primeNumber the prime number
     * @param result the result
     * @return the hash code for parent object
     */
    private int getHashCodeForParentObject(final int primeNumber, int result) {
        return getRamdomNumber(primeNumber, result) + (validateObjectForNull(getParent()) ? 0 : getParent().hashCode());
    }

    /**
     * Gets the ramdom number.
     *
     * @param primeNumber the prime number
     * @param result the result
     * @return the ramdom number
     */
    private int getRamdomNumber(final int primeNumber, int result) {
        return primeNumber * result;
    }

    /**
     * Gets the hash code for current object.
     *
     * @param primeNumber the prime number
     * @param result the result
     * @return the hash code for current object
     */
    private int getHashCodeForCurrentObject(final int primeNumber, int result) {
        return getRamdomNumber(primeNumber, result) + (validateObjectForNull(getName()) ? 0 : getName().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (validateObjectForNull(obj)) {
            return false;
        }

        if (!(obj instanceof ObjectList)) {
            return false;
        }

        ObjectList<?> other = (ObjectList<?>) obj;
        if (validateObjectForNull(getName())) {
            if (!validateObjectForNull(other.getName())) {
                return false;
            }
        } else if (!getName().equals(other.getName())) {
            return false;
        }

        if (validateObjectForNull(getParent())) {
            if (!validateObjectForNull(other.getParent())) {
                return false;
            }
        } else if (!getParent().equals(other.getParent())) {
            return false;
        }

        return true;
    }

    /**
     * Validate object for null.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private boolean validateObjectForNull(Object obj) {
        return obj == null;
    }

}
