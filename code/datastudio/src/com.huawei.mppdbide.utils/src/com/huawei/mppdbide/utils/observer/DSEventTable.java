/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.observer;

import java.util.ArrayList;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSEventTable.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSEventTable {
    private ArrayList<Integer> types;
    private ArrayList<IDSListener> listener;
    private static final int INITIAL_SIZE = 5;

    /**
     * Instantiates a new DS event table.
     */
    public DSEventTable() {
        this.types = new ArrayList<Integer>(INITIAL_SIZE);
        this.listener = new ArrayList<IDSListener>(INITIAL_SIZE);
    }

    /**
     * Hook.
     *
     * @param eventType the event type
     * @param newListener the new listener
     */
    public void hook(int eventType, IDSListener newListener) {
        types.add(eventType);
        listener.add(newListener);
    }

    /**
     * Unhook.
     *
     * @param eventType the event type
     * @param listner the listner
     */
    public void unhook(int eventType, IDSListener listner) {
        int size = types.size();
        for (int i = 0; i < size; i++) {
            if (this.types.get(i).equals(eventType) && this.listener.get(i).equals(listner)) {
                this.types.remove(i);
                this.listener.remove(i);
                i--;
            }
        }
    }

    /**
     * Send event.
     *
     * @param event the event
     */
    public void sendEvent(DSEvent event) {
        int size = types.size();
        for (int i = 0; i < size; i++) {
            if (this.types.get(i).equals(event.getType())) {
                IDSListener listnr = this.listener.get(i);
                listnr.handleEvent(event);
            }
        }
    }

    /**
     * Unhookall.
     */
    public void unhookall() {
        int size = types.size();
        while (size > 0) {
            this.types.remove(0);
            this.listener.remove(0);
            size = types.size();
        }
    }

}
