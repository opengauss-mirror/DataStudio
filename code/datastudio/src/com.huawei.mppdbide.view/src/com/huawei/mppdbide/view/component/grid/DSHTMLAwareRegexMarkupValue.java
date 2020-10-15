/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import java.io.StringReader;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.RegexMarkupValue;
import org.eclipse.nebula.widgets.richtext.RichTextPainter;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSHTMLAwareRegexMarkupValue.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSHTMLAwareRegexMarkupValue extends RegexMarkupValue {
    private static final String GROUP_INDEX_PLACEHOLDER = "$1";
    private XMLInputFactory factory = XMLInputFactory.newInstance();
    private String markupValue;

    /**
     * Instantiates a new DSHTML aware regex markup value.
     *
     * @param value the value
     * @param markupPrefix the markup prefix
     * @param markupSuffix the markup suffix
     */
    public DSHTMLAwareRegexMarkupValue(String value, String markupPrefix, String markupSuffix) {
        super(value, markupPrefix, markupSuffix);
        this.markupValue = markupPrefix + GROUP_INDEX_PLACEHOLDER + markupSuffix;
    }

    /**
     * Apply markup.
     *
     * @param input the input
     * @return the string
     */
    @Override
    public String applyMarkup(String input) {
        String result = "";
        StringBuffer resultBuf = new StringBuffer();
        if (getOriginalRegexValue() != null && !getOriginalRegexValue().isEmpty()) {
            XMLEventReader parser = null;
            XMLEvent event = null;
            try {
                parser = this.factory.createXMLEventReader(new StringReader(
                        RichTextPainter.FAKE_ROOT_TAG_START + input + RichTextPainter.FAKE_ROOT_TAG_END));
                if (parser != null) {
                    boolean hasNext = parser.hasNext();
                    while (hasNext) {
                        event = parser.nextEvent();

                        applyMarkup1(resultBuf, event);
                        hasNext = parser.hasNext();
                    }
                    // To highlight the search
                    replaceWithMarkUpValue(resultBuf);
                }
            } catch (XMLStreamException exception) {
                // Not expected to come here. Need to ignore.
                MPPDBIDELoggerUtility.error("HTML parser error while printing data in grid", exception);
            } finally {
                if (parser != null) {
                    try {
                        parser.close();
                    } catch (XMLStreamException exception) {
                        MPPDBIDELoggerUtility.error("HTML parser error while closing in grid", exception);
                    }
                }
            }

            result = resultBuf.toString().replace(RichTextPainter.FAKE_ROOT_TAG_START, "")
                    .replace(RichTextPainter.FAKE_ROOT_TAG_END, "");
        } else {
            result = input;
        }
        return result;
    }

    private void applyMarkup1(StringBuffer resultBuf, XMLEvent event) {
        switch (event.getEventType()) {
            case XMLStreamConstants.CHARACTERS: {
                Characters characters = event.asCharacters();
                String text = characters != null ? StringEscapeUtils.escapeHtml(characters.getData()) : "";
                resultBuf.append(text);
                break;
            }
            case XMLStreamConstants.START_DOCUMENT: {
                break;
            }
            case XMLStreamConstants.END_DOCUMENT: {
                break;
            }
            default: {
                resultBuf.append(event.toString());
            }
        }
    }

    private void replaceWithMarkUpValue(StringBuffer resultBuf) {
        String replaceResultBuf = resultBuf.toString();
        replaceResultBuf = replaceResultBuf.replace("<root>", "");
        replaceResultBuf = replaceResultBuf.replace("</root>", "");
        Pattern regexValuePattern = Pattern.compile(getOriginalRegexValue(), Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher regexValueMatcher = regexValuePattern.matcher(replaceResultBuf);

        if (regexValueMatcher.find()) {
            resultBuf.setLength(0);
            replaceResultBuf = replaceResultBuf.replaceAll(getOriginalRegexValue(), this.markupValue);
            resultBuf.append("<root>").append(replaceResultBuf).append("</root>");
        }
    }

}
