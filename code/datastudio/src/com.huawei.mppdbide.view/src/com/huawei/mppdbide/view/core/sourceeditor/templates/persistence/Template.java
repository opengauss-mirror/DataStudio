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
 * 
 * Title: class
 * 
 * Description: The Class Template.
 *
 * @since 3.0.0
 */
public class Template implements TemplateIf {
    private String name;
    private String description;
    private String pattern;

    /**
     * Instantiates a new template.
     *
     * @param template the template
     */
    public Template(TemplateIf template) {
        this(template.getName(), template.getDescription(), template.getPattern());
    }

    /**
     * Instantiates a new template.
     *
     * @param name the name
     * @param description the description
     * @param pattern the pattern
     */
    public Template(String name, String description, String pattern) {
        this.description = description;
        this.name = name;
        this.pattern = pattern;
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    public int hashCode() {
        return name.hashCode() ^ pattern.hashCode();
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the pattern.
     *
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Equals.
     *
     * @param object the object
     * @return true, if successful
     */
    public boolean equals(Object object) {
        if (!(object instanceof Template)) {
            return false;
        }

        Template temp = (Template) object;
        if (temp == this) {
            return true;
        }

        return temp.name.equals(name) && temp.pattern.equals(pattern) && temp.description.equals(description);
    }
}
