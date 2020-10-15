/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor.templates.persistence;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface TemplatePersistenceDataIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface TemplatePersistenceDataIf {

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId();

    /**
     * Checks if is deleted.
     *
     * @return true, if is deleted
     */
    public boolean isDeleted();

    /**
     * Sets the deleted.
     *
     * @param isDeleted1 the new deleted
     */
    public void setDeleted(boolean isDeleted1);

    /**
     * Gets the template.
     *
     * @return the template
     */
    public TemplateIf getTemplate();

    /**
     * Sets the template.
     *
     * @param template the new template
     */
    public void setTemplate(TemplateIf template);

    /**
     * Checks if is custom.
     *
     * @return true, if is custom
     */
    public boolean isCustom();

    /**
     * Checks if is modified.
     *
     * @return true, if is modified
     */
    public boolean isModified();

    /**
     * Checks if is user added.
     *
     * @return true, if is user added
     */
    public boolean isUserAdded();

    /**
     * Revert.
     */
    public void revert();

    /**
     * Checks if is enabled.
     *
     * @return true, if is enabled
     */
    public boolean isEnabled();

    /**
     * Sets the enabled.
     *
     * @param isEnabled the new enabled
     */
    public void setEnabled(boolean isEnabled);
}
