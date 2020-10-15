/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor.templates.persistence;

/**
 * 
 * Title: class
 * 
 * Description: The Class Template.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
