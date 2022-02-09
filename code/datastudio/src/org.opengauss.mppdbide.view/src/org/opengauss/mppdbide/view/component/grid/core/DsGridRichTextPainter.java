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

package org.opengauss.mppdbide.view.component.grid.core;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.nebula.widgets.richtext.RichTextPainter;
import org.eclipse.nebula.widgets.richtext.painter.AlignmentStyle;
import org.eclipse.nebula.widgets.richtext.painter.DefaultEntityReplacer;
import org.eclipse.nebula.widgets.richtext.painter.EntityReplacer;
import org.eclipse.nebula.widgets.richtext.painter.LinePainter;
import org.eclipse.nebula.widgets.richtext.painter.ResourceHelper;
import org.eclipse.nebula.widgets.richtext.painter.SpanElement;
import org.eclipse.nebula.widgets.richtext.painter.SpanElement.SpanType;
import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState;
import org.eclipse.nebula.widgets.richtext.painter.TagProcessingState.TextAlignment;
import org.eclipse.nebula.widgets.richtext.painter.instructions.BoldPaintInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.FontMetricsProvider;
import org.eclipse.nebula.widgets.richtext.painter.instructions.ItalicPaintInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.ListInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.NewLineInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.PaintInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.ParagraphInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.ResetFontPaintInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.ResetParagraphInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.ResetSpanStylePaintInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.SpanStylePaintInstruction;
import org.eclipse.nebula.widgets.richtext.painter.instructions.TextPaintInstruction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class DsGridRichTextPainter.
 *
 * @since 3.0.0
 */
public class DsGridRichTextPainter extends RichTextPainter {

    /**
     * The input factory.
     */
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    {
        // as there is no well-formed XML document, we have taken care of
        // entity references here
        inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
    }

    private boolean isWordWrap;
    private final Point preferedSize = new Point(0, 0);
    private EntityReplacer entityReplcer = new DefaultEntityReplacer();

    /**
     * Instantiates a new ds grid rich text painter.
     *
     * @param wordWrap the word wrap
     */
    public DsGridRichTextPainter(boolean wordWrap) {
        this.isWordWrap = wordWrap;
    }

    /**
     * Paint HTML.
     *
     * @param html the html
     * @param gc the gc
     * @param bounds the bounds
     * @param render the render
     */
    @Override
    protected void paintHTML(String html, GC gc, Rectangle bounds, boolean render) {

        final TagProcessingState tagState = new TagProcessingState();
        tagState.setStartingPoint(bounds.x, bounds.y);
        tagState.setRendering(render);

        Deque<SpanElement> spanEleStack = new LinkedList<>();

        ArrayList<LinePainter> lines = new ArrayList<LinePainter>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        // we are ignoring contol character as we only care about html tags
        String cleanHtml = html.replaceAll(CONTROL_CHARACTER_REGEX, "");

        // we need to introduce a fake root tag, because otherwise we will get
        // invalid XML exceptions
        cleanHtml = FAKE_ROOT_TAG_START + cleanHtml + FAKE_ROOT_TAG_END;

        gc.setAntialias(SWT.DEFAULT);
        gc.setTextAntialias(SWT.DEFAULT);

        XMLEventReader xmlParser = null;

        Deque<Integer> listIndentation = new LinkedList<Integer>();

        GC tempGC = new GC(gc.getDevice());
        try {
            xmlParser = inputFactory.createXMLEventReader(new StringReader(cleanHtml));
            getPainter(bounds, tagState, spanEleStack, lines, xmlParser, listIndentation, tempGC);
        } catch (XMLStreamException exception) {
            MPPDBIDELoggerUtility.error("DsGridRichTextPainter: XMLStreamException occurred.", exception);
        } finally {
            tempGC.dispose();
            if (xmlParser != null) {
                try {
                    xmlParser.close();
                } catch (XMLStreamException exception) {
                    MPPDBIDELoggerUtility.error("DsGridRichTextPainter: XMLStreamException occurred.", exception);
                }
            }
        }

        // initialize the state which is able to iterate over the line
        // instructions
        tagState.setLineIterator(lines.iterator());

        preferedSize.x = 0;
        preferedSize.y = 0;

        // perform the painting here
        for (LinePainter linePainter : lines) {
            linePainter.paint(gc, bounds);

            preferedSize.x = Math.max(preferedSize.x, linePainter.getContentWidth());
            preferedSize.y += linePainter.getLineHeight();
        }
        // add paragraphSpace on top and bottom
        preferedSize.y += 2 * tagState.getParagraphCount() * getParagraphSpace();

        preferedSize.y = Math.max(preferedSize.y, bounds.height);

    }

    private void getPainter(Rectangle bounds, final TagProcessingState tagState, Deque<SpanElement> spanEleStack,
            ArrayList<LinePainter> lines, XMLEventReader xmlParser, Deque<Integer> listIndentation, GC tempGC)
            throws XMLStreamException {
        boolean isListOpened = false;
        int availablWidth = bounds.width;
        LinePainter currntLine = null;
        while (xmlParser != null && xmlParser.hasNext()) {
            XMLEvent event = xmlParser.nextEvent();
            currntLine = getPainter1(event, currntLine, tagState, lines, tempGC, availablWidth);
            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT: {
                    final StartElement startElement = event.asStartElement();
                    String elementStr = startElement.getName().toString();
                    if (TAG_PARAGRAPH.equals(elementStr)) {
                        AlignmentStyle alignment = handleAlignmentConfiguration(startElement);
                        currntLine = getCurrentLineForTagParagraph(tagState, lines, tempGC, availablWidth, alignment);
                        availablWidth -= alignment.marginLeft;
                    } else if (TAG_UNORDERED_LIST.equals(elementStr) || TAG_ORDERED_LIST.equals(elementStr)) {
                        isListOpened = true;
                        int indentn = calculateListIndentation(tempGC);
                        availablWidth -= indentn;
                        listIndentation.add(indentn);
                        AlignmentStyle alignment = handleAlignmentConfiguration(startElement);
                        availablWidth -= alignment.marginLeft;
                        currntLine = getCurrentLineForTagList(tagState, spanEleStack, lines, tempGC, availablWidth,
                                startElement, elementStr, indentn, alignment);
                    } else if (TAG_LIST_ITEM.equals(elementStr)) {
                        // if a list was opened before, the list tag created a
                        // new line
                        // otherwise we create a new line for the new list item
                        if (!isListOpened) {
                            currntLine = getCurrentLineForTagItemIfListNotOpeaned(tagState, lines, tempGC,
                                    availablWidth);
                        } else {
                            isListOpened = false;
                        }
                        currntLine = addInstructionForTagListItem(tagState, lines, tempGC, availablWidth, currntLine,
                                startElement);
                    } else {
                        currntLine = getCurrentElementForStartLine(tagState, spanEleStack, lines, tempGC, availablWidth,
                                currntLine, startElement, elementStr);
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if (event.asEndElement() != null) {
                        String endElementString = getEndEleString(event);
                        currntLine = getCurrentLineForEndElement1(tagState, spanEleStack, lines, tempGC, availablWidth,
                                currntLine, endElementString);
                        availablWidth = getAvailableWidthForEndElement(bounds, listIndentation, availablWidth,
                                endElementString);
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    private String getEndEleString(XMLEvent event) {
        QName name = event.asEndElement().getName();
        String endElementString = name != null ? name.toString() : "";
        return endElementString;
    }

    private LinePainter addInstructionForTagListItem(final TagProcessingState tagState, ArrayList<LinePainter> lines,
            GC tempGC, int availablWidth, LinePainter currntLine, final StartElement startElement) {
        final AlignmentStyle alignment = handleAlignmentConfiguration(startElement);

        // paint number/bullets
        currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                getPaintInstructionForTagItem(tagState, alignment));
        return currntLine;
    }

    private LinePainter getPainter1(XMLEvent event, LinePainter currntLine, TagProcessingState tagState,
            ArrayList<LinePainter> lines, GC tempGC, int availablWidth) {
        switch (event.getEventType()) {
            case XMLStreamConstants.END_DOCUMENT: {
                break;
            }
            case XMLStreamConstants.CHARACTERS: {
                currntLine = getCurrentLineForCharacters(tagState, lines, tempGC, availablWidth, currntLine, event);
                break;
            }
            case XMLStreamConstants.ENTITY_REFERENCE: {
                currntLine = getCurrentLIneForEntityReference(tagState, lines, tempGC, availablWidth, currntLine,
                        event);
                break;
            }
            case XMLStreamConstants.ATTRIBUTE: {
                break;
            }
        }
        return currntLine;
    }

    private LinePainter getCurrentLineForTagItemIfListNotOpeaned(final TagProcessingState tagState,
            ArrayList<LinePainter> lines, GC tempGC, int availablWidth) {
        LinePainter currntLine;
        currntLine = createNewLine(lines);
        currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                new NewLineInstruction(tagState));
        return currntLine;
    }

    private LinePainter getCurrentLineForTagParagraph(final TagProcessingState tagState, ArrayList<LinePainter> lines,
            GC tempGC, int availablWidth, AlignmentStyle alignment) {
        LinePainter currntLine;
        currntLine = createNewLine(lines);
        currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                new ParagraphInstruction(alignment, getParagraphSpace(), tagState));
        return currntLine;
    }

    private LinePainter getCurrentLineForEndElement1(final TagProcessingState tagState, Deque<SpanElement> spanEleStack,
            ArrayList<LinePainter> lines, GC tempGC, int availablWidth, LinePainter currntLine,
            String endElementString) {
        if (TAG_PARAGRAPH.equals(endElementString)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    new ResetParagraphInstruction(getParagraphSpace(), tagState));

        } else if (validateForTagList(endElementString)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    getPaintInstructionForTagList(tagState));

        } else {
            currntLine = getCurrentLineForEndElement(tagState, spanEleStack, lines, tempGC, availablWidth, currntLine,
                    endElementString);
        }
        return currntLine;
    }

    private int getAvailableWidthForEndElement(Rectangle bounds, Deque<Integer> listIndentation, int availablWidthParam,
            String endElementString) {
        int availablWidth = availablWidthParam;
        if (TAG_PARAGRAPH.equals(endElementString)) {
            availablWidth = bounds.width;
        } else if (validateForTagList(endElementString)) {
            if (!listIndentation.isEmpty()) {
                availablWidth += listIndentation.removeLast();
            }
        }
        return availablWidth;
    }

    private LinePainter getCurrentElementForStartLine(final TagProcessingState tagState,
            Deque<SpanElement> spanEleStack, ArrayList<LinePainter> lines, GC tempGC, int availablWidth,
            LinePainter currntLine, final StartElement startElement, String elementStr) {
        if (TAG_BR.equals(elementStr)) {
            currntLine = getCurrentLineForTagItemIfListNotOpeaned(tagState, lines, tempGC, availablWidth);
        } else if (TAG_SPAN.equals(elementStr)) {
            PaintInstruction styleInstruction = handleStyleConfiguration(startElement, spanEleStack, tagState);
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState, styleInstruction);
        } else if (TAG_STRONG.equals(elementStr)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    new BoldPaintInstruction(tagState));
        } else if (TAG_EM.equals(elementStr)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    new ItalicPaintInstruction(tagState));
        } else if (TAG_UNDERLINE.equals(elementStr)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState, new PaintInstruction() {

                @Override
                public void paint(GC gc, Rectangle area) {
                    tagState.setUnderlineActive(true);
                }
            });
        } else if (TAG_STRIKETHROUGH.equals(elementStr)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    getPaintInstructionForTagStrikeThrough1(tagState));
        }
        return currntLine;
    }

    private LinePainter getCurrentLineForTagList(final TagProcessingState tagState, Deque<SpanElement> spanEleStack,
            ArrayList<LinePainter> lines, GC tempGC, int availablWidth, final StartElement startElement,
            String elementStr, int indentn, AlignmentStyle alignment) {
        LinePainter currntLine;
        boolean isOrderedList = TAG_ORDERED_LIST.equals(elementStr);

        currntLine = createNewLine(lines);
        currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                new ListInstruction(indentn, isOrderedList, alignment, getParagraphSpace(), tagState));

        // inspect font attributes here
        PaintInstruction styleInstruction = handleStyleConfiguration(startElement, spanEleStack, tagState);
        currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState, styleInstruction);
        return currntLine;
    }

    private PaintInstruction getPaintInstructionForTagItem(final TagProcessingState tagState,
            final AlignmentStyle alignment) {
        return new PaintInstruction() {

            @Override
            public void paint(GC gc, Rectangle area) {
                tagState.resetX();

                String bullet = getBulletCharacter(tagState.getListDepth()) + "\u00a0";
                if (tagState.isOrderedList()) {
                    bullet = "" + tagState.getCurrentListNumber() + ". ";
                }
                int extend = gc.textExtent(bullet).x;
                gc.drawText(bullet, tagState.getPointer().x - extend, tagState.getPointer().y,
                        tagState.hasPreviousBgColor());

                tagState.setTextAlignment(alignment.alignment);
                tagState.calculateX(area.width);
            }
        };
    }

    private LinePainter getCurrentLineForEndElement(final TagProcessingState tagState, Deque<SpanElement> spanEleStack,
            ArrayList<LinePainter> lines, GC tempGC, int availablWidth, LinePainter currntLine,
            String endElementString) {
        if (TAG_SPAN.equals(endElementString)) {
            SpanElement spanElement = spanEleStack.pollLast();
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    new ResetSpanStylePaintInstruction(tagState, spanElement));
        } else if (TAG_STRONG.equals(endElementString) || TAG_EM.equals(endElementString)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    new ResetFontPaintInstruction(tagState));
        } else if (TAG_UNDERLINE.equals(endElementString)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    getPaintInstructionForTagUnderline(tagState));
        } else if (TAG_STRIKETHROUGH.equals(endElementString)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    getPaintInstructionForTagStrikeThrough(tagState));
        } else if (TAG_LIST_ITEM.equals(endElementString)) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    getPaintInstructionForTagItem(tagState));
        }
        return currntLine;
    }

    private PaintInstruction getPaintInstructionForTagUnderline(final TagProcessingState tagState) {
        return new PaintInstruction() {

            @Override
            public void paint(GC gc, Rectangle area) {
                tagState.setUnderlineActive(false);
            }
        };
    }

    private PaintInstruction getPaintInstructionForTagStrikeThrough(final TagProcessingState tagState) {
        return new PaintInstruction() {

            @Override
            public void paint(GC gc, Rectangle area) {
                tagState.setStrikethroughActive(false);
            }
        };
    }

    private PaintInstruction getPaintInstructionForTagStrikeThrough1(final TagProcessingState tagState) {
        return new PaintInstruction() {

            @Override
            public void paint(GC gc, Rectangle area) {
                tagState.setStrikethroughActive(true);
            }
        };
    }

    private PaintInstruction getPaintInstructionForTagItem(final TagProcessingState tagState) {
        return new PaintInstruction() {

            @Override
            public void paint(GC gc, Rectangle area) {
                tagState.increaseCurrentListNumber();
                tagState.setTextAlignment(TextAlignment.LEFT);
            }
        };
    }

    private boolean validateForTagList(String endElementString) {
        return TAG_ORDERED_LIST.equals(endElementString) || TAG_UNORDERED_LIST.equals(endElementString);
    }

    private PaintInstruction getPaintInstructionForTagList(final TagProcessingState tagState) {
        return new PaintInstruction() {

            @Override
            public void paint(GC gc, Rectangle area) {
                tagState.resetListConfiguration();

                // if the last list layer was
                // finished, increase the line
                // height like in paragraph
                if (tagState.getListDepth() == 0) {
                    tagState.setMarginLeft(0);
                    tagState.increaseY(tagState.getCurrentLineHeight());
                    tagState.increaseY(getParagraphSpace());
                }

                tagState.resetX();
                tagState.setTextAlignment(TextAlignment.LEFT);
            }
        };
    }

    private LinePainter getCurrentLineForCharacters(final TagProcessingState tagState, ArrayList<LinePainter> lines,
            GC tempGC, int availablWidth, LinePainter currntLine, XMLEvent event) {
        Characters charactrs = event.asCharacters();
        String text = charactrs != null ? charactrs.getData() : "";
        if (text != null) {
            currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                    new TextPaintInstruction(tagState, text));
        }
        return currntLine;
    }

    private LinePainter getCurrentLIneForEntityReference(final TagProcessingState tagState,
            ArrayList<LinePainter> lines, GC tempGC, int availablWidth, LinePainter currntLine, XMLEvent event) {
        String value = entityReplcer.getEntityReferenceValue((EntityReference) event);
        currntLine = addInstruction(tempGC, availablWidth, lines, currntLine, tagState,
                new TextPaintInstruction(tagState, value));
        return currntLine;
    }

    private LinePainter addInstruction(GC gc, int availableWidth, Collection<LinePainter> lines,
            LinePainter currntLineParam, final TagProcessingState tagProgState, PaintInstruction instructionParam) {
        LinePainter currntLine = currntLineParam;
        PaintInstruction instruction = instructionParam;
        if (instruction instanceof FontMetricsProvider) {
            // applying the font to the temp GC
            ((FontMetricsProvider) instruction).getFontMetrics(gc);
        }

        // if currentLine is null at this point, there is no spanning p tag and
        // we create a new line
        // for convenience to support also simple text
        currntLine = createNewLine(lines, currntLine, tagProgState);

        LinePainter lineToUse = currntLine;
        TextPaintInstruction txtInstr = null;
        if (instruction instanceof TextPaintInstruction) {
            lineToUse = addTextPaintInstruction(gc, availableWidth, lines, tagProgState, currntLine, instruction,
                    lineToUse);
        } else {
            lineToUse.addInstruction(instruction);
        }

        return lineToUse;
    }

    private LinePainter addTextPaintInstruction(GC gc, int availableWidth, Collection<LinePainter> lines,
            final TagProcessingState tagProgState, LinePainter currntLine, PaintInstruction instructionParam,
            LinePainter lineToUseParam) {
        LinePainter lineToUse = lineToUseParam;
        PaintInstruction instruction = instructionParam;
        TextPaintInstruction txtInstr = (TextPaintInstruction) instruction;
        int txtLength = txtInstr.getTextLength(gc);
        int trimmedTxtLength = txtInstr.getTrimmedTextLength(gc);

        if ((currntLine.getContentWidth() + txtLength) > availableWidth) {

            if (this.isWordWrap) {
                // if word wrapping is enabled, split the text and create
                // new lines by making several
                // TextPaintInstructions with substrings

                Deque<String> wordsToProcess = new LinkedList<>(Arrays.asList(txtInstr.getText().split("\\s+")));
                String subStr = "";
                int subStrLength = 0;
                while (!wordsToProcess.isEmpty()) {
                    String word = wordsToProcess.removeFirst();
                    int wordLength = gc.textExtent(word).x;
                    subStrLength += wordLength;
                    if ((lineToUse.getContentWidth() + subStrLength) > availableWidth) {
                        boolean newLine = true;
                        if (!subStr.trim().isEmpty()) {
                            // right side trimmed
                            subStr = ResourceHelper.rtrim(subStr);

                            txtInstr = new TextPaintInstruction(tagProgState, subStr);
                            txtLength = txtInstr.getTextLength(gc);
                            trimmedTxtLength = txtInstr.getTrimmedTextLength(gc);

                            increaseWindth(lineToUse, txtInstr, txtLength, trimmedTxtLength);

                            subStr = word;
                            subStrLength = wordLength;
                        } else if (lineToUse.getContentWidth() == 0) {
                            // no content already but text width greater
                            // than available width
                            // 0.2 - modifed text to show ...
                            // 0.2 - add trim to avoid empty lines
                            // because of spaces
                            subStr = word;
                            subStrLength = wordLength;
                            newLine = false;
                        }

                        lineToUse = addOnNewLine(lines, tagProgState, lineToUse, newLine);
                    } else {
                        subStr += word;
                    }
                }

                if (!subStr.trim().isEmpty()) {
                    txtInstr = new TextPaintInstruction(tagProgState, subStr);
                    txtLength = txtInstr.getTextLength(gc);
                    trimmedTxtLength = txtInstr.getTrimmedTextLength(gc);
                    instruction = txtInstr;
                }
            }

        }

        increateContentWidth(instruction, lineToUse, txtLength, trimmedTxtLength);
        return lineToUse;
    }

    private void increateContentWidth(PaintInstruction instruction, LinePainter lineToUse, int txtLength,
            int trimmedTxtLength) {
        lineToUse.addInstruction(instruction);
        lineToUse.increaseContentWidth(txtLength);
        lineToUse.increaseTrimmedContentWidth(trimmedTxtLength);
    }

    private void increaseWindth(LinePainter lineToUse, TextPaintInstruction txtInstr, int txtLength,
            int trimmedTxtLength) {
        lineToUse.addInstruction(txtInstr);

        lineToUse.increaseContentWidth(txtLength);
        lineToUse.increaseTrimmedContentWidth(trimmedTxtLength);
    }

    private LinePainter addOnNewLine(Collection<LinePainter> lines, final TagProcessingState tagProgState,
            LinePainter lineToUseParam, boolean newLine) {
        LinePainter lineToUse = lineToUseParam;
        if (newLine) {
            lineToUse = createNewLine(lines);
            lineToUse.addInstruction(new NewLineInstruction(tagProgState));
        }
        return lineToUse;
    }

    private LinePainter createNewLine(Collection<LinePainter> lines, LinePainter currntLineParam,
            final TagProcessingState tagProgState) {
        LinePainter currntLine = currntLineParam;
        if (currntLine == null) {
            currntLine = createNewLine(lines);
            currntLine.addInstruction(new PaintInstruction() {
                @Override
                public void paint(GC gc, Rectangle area) {
                    tagProgState.activateNextLine();
                    tagProgState.increaseY(getParagraphSpace());
                    tagProgState.increaseParagraphCount();
                }
            });
        }
        return currntLine;
    }

    private LinePainter createNewLine(Collection<LinePainter> lines) {
        LinePainter currntLine = new LinePainter();
        lines.add(currntLine);
        return currntLine;
    }

    private AlignmentStyle handleAlignmentConfiguration(StartElement element) {
        AlignmentStyle resultStyle = new AlignmentStyle();
        for (Iterator<?> attributes = element.getAttributes(); attributes.hasNext();) {
            Attribute attribute = (Attribute) attributes.next();
            if (attribute != null && ATTRIBUTE_STYLE.equals(attribute.getName().toString())) {
                Map<String, String> styleProperties = getStyleProperties(attribute.getValue());
                for (Map.Entry<String, String> entry : styleProperties.entrySet()) {
                    if (ATTRIBUTE_PARAGRAPH_MARGIN_LEFT.equals(entry.getKey())) {
                        String pixlValue = entry.getValue().replace("px", "");
                        try {
                            int pixel = Integer.parseInt(pixlValue.trim());
                            resultStyle.marginLeft = pixel;
                        } catch (NumberFormatException exception) {
                            MPPDBIDELoggerUtility.error("Failed to parser integer", exception);
                            // if the value is not a valid number value
                            // we simply ignore it
                        }
                    } else if (ATTRIBUTE_PARAGRAPH_TEXT_ALIGN.equals(entry.getKey())) {
                        try {
                            TextAlignment alignment = TextAlignment
                                    .valueOf(entry.getValue().toUpperCase(Locale.ENGLISH));
                            resultStyle.alignment = alignment;
                        } catch (IllegalArgumentException exception) {
                            MPPDBIDELoggerUtility.error("Failed to align text in paragraph", exception);
                            // if the value is not a valid txt-aligment
                            // we simply ignore it
                        }
                    }
                }
            }
        }
        return resultStyle;
    }

    private PaintInstruction handleStyleConfiguration(StartElement element, Deque<SpanElement> spanStack,
            TagProcessingState state) {
        // creating the span element with reset info on tag close
        SpanElement spanElemt = new SpanElement();
        // creating span style paint instruction that should be performed on
        // painting
        SpanStylePaintInstruction styleInstruction = new SpanStylePaintInstruction(state);

        // inspect the attributes here
        for (Iterator<?> attributes = element.getAttributes(); attributes.hasNext();) {
            Attribute attribt = (Attribute) attributes.next();
            if (attribt != null && ATTRIBUTE_STYLE.equals(attribt.getName().toString())) {
                Map<String, String> styleProperties = getStyleProperties(attribt.getValue());
                for (Map.Entry<String, String> entry : styleProperties.entrySet()) {
                    if (ATTRIBUTE_STYLE_COLOR.equals(entry.getKey())) {
                        // update span element to know what to reset on tag
                        // close
                        spanElemt.types.add(SpanType.COLOR);
                        // update the style paint instruction here
                        styleInstruction.setForegroundColor(ResourceHelper.getColor(entry.getValue()));
                    } else if (ATTRIBUTE_STYLE_BACKGROUND_COLOR.equals(entry.getKey())) {
                        // update span element to know what to reset on tag
                        // close
                        spanElemt.types.add(SpanType.BG_COLOR);
                        // update the style paint instruction
                        styleInstruction.setBackgroundColor(ResourceHelper.getColor(entry.getValue()));
                    } else if (ATTRIBUTE_STYLE_FONT_SIZE.equals(entry.getKey())) {
                        // update span element to know what to reset on tag
                        // close
                        spanElemt.types.add(SpanType.FONT);
                        // update the style paint instruction
                        String pixlValue = entry.getValue().replace("px", "");
                        try {
                            int pixel = Integer.parseInt(pixlValue.trim());
                            // size in pixels specified in HTML
                            // so we have to convert it to point
                            int xCoordinate = Display.getDefault().getDPI().x;
                            if (xCoordinate != 0) {
                                int pointSize = 72 * pixel / xCoordinate;
                                styleInstruction.setFontSize(pointSize);
                            }
                        } catch (NumberFormatException exception) {
                            MPPDBIDELoggerUtility.error("Error while parsing invalid number string", exception);
                            // ignore error.
                        }
                    } else if (ATTRIBUTE_STYLE_FONT_FAMILY.equals(entry.getKey())) {
                        // update span element to know what to reset on tag
                        // close
                        spanElemt.types.add(SpanType.FONT);
                        // update style paint instruction
                        styleInstruction.setFontType(entry.getValue().split(",")[0]);
                    }
                }
            }
        }

        spanStack.add(spanElemt);
        return styleInstruction;
    }

    private Map<String, String> getStyleProperties(String styleString) {
        Map<String, String> resultMap = new HashMap<>();

        String[] configs = styleString.split(";");
        for (String config : configs) {
            String[] keyValuePair = config.split(":");
            if (keyValuePair.length > 1) {
                resultMap.put(keyValuePair[0].trim(), keyValuePair[1].trim());
            }
        }

        return resultMap;
    }

    /**
     * Gets the preferred size.
     *
     * @return the preferred size
     */
    public Point getPreferredSize() {
        return preferedSize;
    }

}
