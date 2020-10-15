/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor.templates.persistence;

/**
 * Title: TemplateFactory
 * 
 * Description: A factory for creating Template objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-May-2019]
 * @since 21-May-2019
 */

public final class TemplateFactory {

    /**
     * Gets the template.
     *
     * @param template the template
     * @return the template
     */
    public static TemplateIf getTemplate(TemplateIf template) {
        return new Template(template);
    }

    /**
     * Gets the template.
     *
     * @param name the name
     * @param description the description
     * @param pattern the pattern
     * @return the template
     */
    public static TemplateIf getTemplate(String name, String description, String pattern) {
        return new Template(name, description, pattern);
    }

    /**
     * Gets the template persistence data.
     *
     * @param template the template
     * @param enabled the enabled
     * @return the template persistence data
     */
    public static TemplatePersistenceDataIf getTemplatePersistenceData(TemplateIf template, boolean enabled) {
        return new TemplatePersistenceData(template, enabled);
    }

    /**
     * Gets the template persistence data.
     *
     * @param template the template
     * @param enabled the enabled
     * @param id2 the id 2
     * @param deleted the deleted
     * @return the template persistence data
     */
    public static TemplatePersistenceDataIf getTemplatePersistenceData(TemplateIf template, boolean enabled, String id2,
            boolean deleted) {
        return new TemplatePersistenceData(template, enabled, id2, deleted);
    }

}
