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

package org.opengauss.mppdbide.view.core.sourceeditor.templates.persistence;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface TemplatePersistenceDataIf.
 *
 * @since 3.0.0
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
