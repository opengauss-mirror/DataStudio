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

package org.opengauss.mppdbide.view.core.sourceeditor.templates;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.templates.TemplateException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.sourceeditor.templates.persistence.TemplateFactory;
import org.opengauss.mppdbide.view.core.sourceeditor.templates.persistence.TemplateIf;
import org.opengauss.mppdbide.view.core.sourceeditor.templates.persistence.TemplatePersistenceDataIf;

/**
 * 
 * Title: class
 * 
 * Description: The Class TemplateReaderWriter.
 *
 * @since 3.0.0
 */
public class TemplateReaderWriter {

    /**
     * 
     * Title: class
     * 
     * Description: The Class TemplateJsonData.
     */
    private static final class TemplateJsonData {
        @SerializedName("deleted")
        private String deleted;

        @SerializedName("description")
        private String description;

        @SerializedName("enabled")
        private String enabled;

        @SerializedName("name")
        private String name;

        @SerializedName("pattern")
        private String pattern;

        @SerializedName("id")
        private String id;

        /**
         * Instantiates a new template json data.
         *
         * @param template the template
         * @param id2 the id 2
         * @param deleted2 the deleted 2
         * @param enabled2 the enabled 2
         */
        private TemplateJsonData(TemplateIf template, String id2, String deleted2, String enabled2) {
            this.setDeleted(deleted2);
            this.enabled = enabled2;
            this.id = id2;
            this.name = template.getName();
            this.description = template.getDescription();
            this.pattern = template.getPattern();
        }

        /**
         * Gets the description.
         *
         * @return the description
         */
        private String getDescription() {
            return description;
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        private String getName() {
            return name;
        }

        /**
         * Gets the pattern.
         *
         * @return the pattern
         */
        private String getPattern() {
            return pattern;
        }

        /**
         * Gets the id.
         *
         * @return the id
         */
        private String getId() {
            return id;
        }

        /**
         * Gets the enabled.
         *
         * @return the enabled
         */
        private String getEnabled() {
            return enabled;
        }

        /**
         * Gets the deleted.
         *
         * @return the deleted
         */
        private String getDeleted() {
            return deleted;
        }

        /**
         * Sets the deleted.
         *
         * @param deleted the new deleted
         */
        public void setDeleted(String deleted) {
            this.deleted = deleted;
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TemplatesMetadata.
     */
    private static final class TemplatesMetadata {
        @SerializedName("version")
        private String version;

        @SerializedName("encoding")
        private String encoding;

        @SerializedName("templates")
        private ArrayList<TemplateJsonData> templates;

        /**
         * Instantiates a new templates metadata.
         *
         * @param version the version
         * @param encoding the encoding
         */
        private TemplatesMetadata(String version, String encoding) {
            this.setVersion(version);
            this.setEncoding(encoding);
            this.setTemplates(null);
        }

        /**
         * Gets the templates.
         *
         * @return the templates
         */
        public ArrayList<TemplateJsonData> getTemplates() {
            return templates;
        }

        /**
         * Sets the templates.
         *
         * @param templates the new templates
         */
        public void setTemplates(ArrayList<TemplateJsonData> templates) {
            this.templates = templates;
        }

        /**
         * Sets the encoding.
         *
         * @param encoding the new encoding
         */
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        /**
         * Gets the encoding.
         *
         * @return the encoding
         */
        public String getEncoding() {
            return encoding;
        }

        /**
         * Sets the version.
         *
         * @param version the new version
         */
        public void setVersion(String version) {
            this.version = version;
        }

        /**
         * Gets the version.
         *
         * @return the version
         */
        public String getVersion() {
            return version;
        }
    }

    private static final String TEMPLATE_VERSION_NUMBER = "1.0.0";

    /**
     * Instantiates a new template reader writer.
     */
    public TemplateReaderWriter() {
    }

    /**
     * Read.
     *
     * @param reader the reader
     * @return the template persistence data if[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public TemplatePersistenceDataIf[] read(Reader reader) throws IOException {
        try {
            Collection<TemplatePersistenceDataIf> templates = new ArrayList<TemplatePersistenceDataIf>(1);
            Set<String> ids = new HashSet<String>();

            Gson gson = new Gson();
            TemplatesMetadata templateMD = null;
            try {
                templateMD = gson.fromJson(reader, getType());
            } catch (JsonIOException e) {
                throw new IOException(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_JSONEXCEPTION));
            } catch (JsonSyntaxException e) {
                throw new IOException(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_JSONEXCEPTION));
            }

            if (templateMD == null) {
                throw new IOException(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_JSONEXCEPTION));
            }
            addLoggerForVersion(templateMD);
            addValidatedTemplate(templates, ids, templateMD);

            return (TemplatePersistenceDataIf[]) templates.toArray(new TemplatePersistenceDataIf[templates.size()]);

        } catch (TemplateException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void addLoggerForVersion(TemplatesMetadata templateMD) {
        if (MPPDBIDELoggerUtility.isInfoEnabled()) {
            MPPDBIDELoggerUtility
                    .info("Template Version " + templateMD.getVersion() + " Encoding " + templateMD.getEncoding());
        }
    }

    private void addValidatedTemplate(Collection<TemplatePersistenceDataIf> templates, Set<String> ids,
            TemplatesMetadata templateMD) throws IOException, TemplateException {
        ArrayList<TemplateJsonData> templateItems = templateMD.getTemplates();
        int count = templateItems.size();
        TemplateJsonData item = null;
        for (int indx = 0; indx != count; indx++) {
            item = templateItems.get(indx);
            if (item == null) {
                continue;
            } else if (validateItem(item)) {
                throw new IOException(
                        MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_MISSING_REQUIRED_ATTR));
            }

            String id = item.getId();
            if (validateId(ids, id)) {
                throw new IOException(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DUPLICATE_ID));
            }

            boolean deleted = getBooleanValue(item.getDeleted(), false);
            boolean enabled = getBooleanValue(item.getEnabled(), true);

            String name = item.getName();
            String description = item.getDescription();
            description = addDefaultDescription(description);

            String pattern = item.getPattern();

            TemplateIf template = TemplateFactory.getTemplate(name, description, pattern);
            TemplatePersistenceDataIf data = TemplateFactory.getTemplatePersistenceData(template, enabled, id, deleted);

            templates.add(data);
        }
    }

    private String addDefaultDescription(String descriptionParam) {
        String description = descriptionParam;
        if (description == null) {
            description = "";
        }
        return description;
    }

    private boolean validateId(Set<String> ids, String id) {
        return id != null && !("".equals(id)) && ids.contains(id);
    }

    private boolean validateItem(TemplateJsonData item) {
        return item.getName() == null || item.getPattern() == null || item.getDeleted() == null
                || item.getEnabled() == null;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    private Type getType() {
        return new TemplatesMetadataTypeToken().getType();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TemplatesMetadataTypeToken.
     */
    private static final class TemplatesMetadataTypeToken extends TypeToken<TemplatesMetadata> {
    }

    /**
     * Save.
     *
     * @param templates the templates
     * @param writer the writer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void save(TemplatePersistenceDataIf[] templates, Writer writer) throws IOException {
        JsonWriter jsonWriter = null;
        try {
            TemplatesMetadata templatesMD = new TemplatesMetadata(TEMPLATE_VERSION_NUMBER,
                    StandardCharsets.UTF_8.name());

            TemplateJsonData templateJson = null;
            ArrayList<TemplateJsonData> list = new ArrayList<TemplateJsonData>(1);

            for (int cnt = 0; cnt < templates.length; cnt++) {
                TemplatePersistenceDataIf data = templates[cnt];
                TemplateIf template = data.getTemplate();

                String enabled = data.isEnabled() ? Boolean.toString(true) : Boolean.toString(false);
                String deleted = data.isDeleted() ? Boolean.toString(true) : Boolean.toString(false);

                templateJson = new TemplateJsonData(template, data.getId(), deleted, enabled);
                list.add(templateJson);
            }

            templatesMD.setTemplates(list);

            Gson gson = new Gson();
            jsonWriter = new JsonWriter(writer);
            gson.toJson(templatesMD, getType(), jsonWriter);
        } catch (JsonIOException e) {
            throw new IOException(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_JSONEXCEPTION));
        } finally {
            if (jsonWriter != null) {
                try {
                    jsonWriter.close();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("Error while closing JsonWriter", exception);
                }
            }
        }
    }

    /**
     * Gets the boolean value.
     *
     * @param attributeVal the attribute val
     * @param defaultValue the default value
     * @return the boolean value
     * @throws TemplateException the template exception
     */
    private boolean getBooleanValue(String attributeVal, boolean defaultValue) throws TemplateException {
        if (attributeVal == null) {
            return defaultValue;
        } else if (attributeVal.equals(Boolean.toString(true))) {
            return true;
        } else if (attributeVal.equals(Boolean.toString(false))) {
            return false;
        } else {
            throw new TemplateException(
                    MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_ILLEGAL_BOOLEAN_ATTR));
        }
    }
}
