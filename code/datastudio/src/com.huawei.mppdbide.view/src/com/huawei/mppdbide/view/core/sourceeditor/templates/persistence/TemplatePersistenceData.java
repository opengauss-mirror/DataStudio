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

package com.huawei.mppdbide.view.core.sourceeditor.templates.persistence;

import org.eclipse.core.runtime.Assert;

/**
 * 
 * Title: class
 * 
 * Description: The Class TemplatePersistenceData.
 *
 * @since 3.0.0
 */
public class TemplatePersistenceData implements TemplatePersistenceDataIf {
    private final TemplateIf originalTemplate;
    private final String id;
    private final boolean originalIsEnabled;

    private TemplateIf customTemplate = null;
    private boolean isDeleted = false;
    private boolean customIsEnabled = true;

    /**
     * Instantiates a new template persistence data.
     *
     * @param template the template
     * @param enabled the enabled
     */
    public TemplatePersistenceData(TemplateIf template, boolean enabled) {
        this(template, enabled, null);
    }

    /**
     * Creates a new instance. If <code>id</code> is not <code>null</code>, the
     * instance is represents a template that is contributed and can be
     * identified via its id.
     *
     * @param template the template which is stored by the new instance
     * @param enabled whether the template is enabled
     * @param id the id of the template, or <code>null</code> if a user-added
     * instance should be created
     */
    private TemplatePersistenceData(TemplateIf template, boolean enabled, String id) {
        this(template, enabled, id, false);
    }

    /**
     * Instantiates a new template persistence data.
     *
     * @param template the template
     * @param enabled the enabled
     * @param id2 the id 2
     * @param deleted the deleted
     */
    public TemplatePersistenceData(TemplateIf template, boolean enabled, String id2, boolean deleted) {
        Assert.isNotNull(template);
        this.originalTemplate = template;
        this.customTemplate = template;
        this.originalIsEnabled = enabled;
        this.customIsEnabled = enabled;
        this.id = id2;
        this.isDeleted = deleted;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Checks if is deleted.
     *
     * @return true, if is deleted
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Sets the deleted.
     *
     * @param isDeleted1 the new deleted
     */
    public void setDeleted(boolean isDeleted1) {
        this.isDeleted = isDeleted1;
    }

    /**
     * Gets the template.
     *
     * @return the template
     */
    public TemplateIf getTemplate() {
        return customTemplate;
    }

    /**
     * Sets the template.
     *
     * @param template the new template
     */
    public void setTemplate(TemplateIf template) {
        this.customTemplate = template;
    }

    /**
     * Checks if is custom.
     *
     * @return true, if is custom
     */
    public boolean isCustom() {
        return id == null || isDeleted || originalIsEnabled != customIsEnabled
                || !originalTemplate.equals(customTemplate);
    }

    /**
     * Checks if is modified.
     *
     * @return true, if is modified
     */
    public boolean isModified() {
        return isCustom() && !isUserAdded();
    }

    /**
     * Checks if is user added.
     *
     * @return true, if is user added
     */
    public boolean isUserAdded() {
        return id == null;
    }

    /**
     * Revert.
     */
    public void revert() {
        customTemplate = originalTemplate;
        customIsEnabled = originalIsEnabled;
        isDeleted = false;
    }

    /**
     * Checks if is enabled.
     *
     * @return true, if is enabled
     */
    public boolean isEnabled() {
        return customIsEnabled;
    }

    /**
     * Sets the enabled.
     *
     * @param isEnabled the new enabled
     */
    public void setEnabled(boolean isEnabled) {
        customIsEnabled = isEnabled;
    }
}
