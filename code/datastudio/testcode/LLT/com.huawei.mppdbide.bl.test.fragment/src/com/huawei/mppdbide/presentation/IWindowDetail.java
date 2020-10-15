package com.huawei.mppdbide.presentation;

/**
 * This interface details the 
 * @author aWX421478
 *
 */
public interface IWindowDetail
{
    /**
     * This method returns the title of the window being displayed 
     * @return - String - The title of this window
     */
    String getTitle();
    
    /**
     * This method returns ths short title of the window
     * @return
     */
    String getShortTitle();
            
    /**
     * This method returns the unique id assigned for this window
     * @return String - The id of the window
     */
    String getUniqueID();
    
    /**
     * returns the icon used for this window
     * @return
     */
    String getIcon();
}