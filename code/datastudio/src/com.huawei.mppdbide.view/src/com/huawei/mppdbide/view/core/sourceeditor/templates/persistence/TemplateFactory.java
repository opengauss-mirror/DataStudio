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

/**
 * Title: TemplateFactory
 * 
 * Description: A factory for creating Template objects.
 * 
 * @since 3.0.0
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
