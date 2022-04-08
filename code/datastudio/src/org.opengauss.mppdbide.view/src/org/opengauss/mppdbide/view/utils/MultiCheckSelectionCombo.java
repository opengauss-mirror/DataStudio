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

package org.opengauss.mppdbide.view.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Title: class
 * 
 * Description: The Class MultiCheckSelectionCombo.
 *
 * @since 3.0.0
 */
public class MultiCheckSelectionCombo extends Composite {

    private List<Option> options = new ArrayList<Option>();
    private Button[] buttons;
    private List<ModifyListener> modifyListeners = new ArrayList<ModifyListener>();
    private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
    private List<VerifyListener> verifyListeners = new ArrayList<VerifyListener>();

    private Button display;

    /**
     * 
     * Title: class
     * 
     * Description: The Class Option.
     */
    private static class Option {
        String text;
        boolean selection = false;

        Option(String text) {
            if (text == null) {
                throw new IllegalArgumentException();
            }
            this.text = text;
        }

        Option(String text, boolean selection) {
            if (text == null) {
                throw new IllegalArgumentException();
            }
            this.text = text;
            this.selection = selection;
        }
    }

    /**
     * Instantiates a new multi check selection combo.
     *
     * @param parent the parent
     * @param style the style
     */
    public MultiCheckSelectionCombo(Composite parent, int style) {
        super(parent, style);
        display = new Button(this, style);
        init();
    }

    /**
     * Instantiates a new multi check selection combo.
     *
     * @param parent the parent
     * @param style the style
     * @param defaultText the default text
     */
    public MultiCheckSelectionCombo(Composite parent, int style, String defaultText) {
        super(parent, style);
        if (defaultText == null) {
            throw new IllegalArgumentException("Default Text cannot be null");
        }
        init();
    }

    /**
     * Sets the layout data.
     *
     * @param data the new layout data
     */
    public void setLayoutData(GridData data) {
    }

    /**
     * Sets the data.
     *
     * @param key the key
     * @param value the value
     */
    public void setData(String key, Object value) {
        display.setData(key, value);
    }

    /**
     * Adds the button listener.
     */
    public void addButtonListener() {
        display.addListener(SWT.MouseDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                showFloatShell(display);
            }
        });
    }

    /**
     * Inits the.
     */
    private void init() {
        GridLayout layout = new GridLayout();
        layout.marginBottom = 0;
        layout.marginTop = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout(layout);
    }

    /**
     * Sets the image.
     *
     * @param iconImage the new image
     */
    public void setImage(Image iconImage) {
        display.setImage(iconImage);
    }

    /**
     * Checks if is first option marked.
     *
     * @return true, if is first option marked
     */
    public boolean isFirstOptionMarked() {
        String[] selections = getSelections();
        if (selections.length == 0) {
            return false;
        }
        return true;
    }

    /**
     * Show float shell.
     *
     * @param display2 the display 2
     */
    private void showFloatShell(final Button display2) {
        Point point = display2.getParent().toDisplay(display2.getLocation());
        Point size = display2.getSize();
        Rectangle shellRect = new Rectangle(point.x, point.y + size.y, size.x, 0);
        final Shell shell = new Shell(MultiCheckSelectionCombo.this.getShell(), SWT.BORDER | SWT.NO_TRIM | SWT.ON_TOP);
        GridLayout shellGL = new GridLayout(1, false);
        shellGL.marginWidth = 0;
        shellGL.marginHeight = 0;
        shell.setLayout(shellGL);
        final ScrolledComposite sc = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        sc.setLayoutData(gd);

        Composite comp = new Composite(sc, SWT.NONE);
        comp.setLayout(new GridLayout(1, true));

        GridData compGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        comp.setLayoutData(compGD);

        addButtons(comp);
        sc.setContent(comp);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        setShellSize(shell, sc);
        shell.setLocation(shellRect.x, shellRect.y);

        shell.addListener(SWT.Deactivate, shellSelectionListener(display2, shell));
        shell.open();
    }

    /**
     * Shell selection listener.
     *
     * @param display2 the display 2
     * @param shell the shell
     * @return the listener
     */
    private Listener shellSelectionListener(final Button display2, final Shell shell) {
        return new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (shell != null && !shell.isDisposed()) {
                    shell.setVisible(false);
                    for (SelectionListener l : selectionListeners) {
                        l.widgetDefaultSelected(new SelectionEvent(event));
                    }
                    for (VerifyListener listener : verifyListeners) {
                        VerifyEvent verifyEvent = new VerifyEvent(event);
                        verifyEvent.doit = false;
                        listener.verifyText(verifyEvent);
                    }

                    displayText(display2);
                    for (ModifyListener listener : modifyListeners) {
                        listener.modifyText(new ModifyEvent(event));
                    }
                }
            }
        };
    }

    /**
     * Adds the buttons.
     *
     * @param c the c
     */
    private void addButtons(Composite comp) {
        buttons = new Button[options.size()];
        for (int i = 0; i < options.size(); i++) {
            final Button button = new Button(comp, SWT.CHECK);
            final Option option = options.get(i);
            button.setText(option.text);
            button.setSelection(option.selection);
            button.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    option.selection = button.getSelection();
                    for (SelectionListener l : selectionListeners) {
                        l.widgetSelected(new SelectionEvent(event));
                    }
                }
            });
            button.pack();
            buttons[i] = button;
        }
    }

    /**
     * Sets the shell size.
     *
     * @param shell the new shell size
     */
    private void setShellSize(final Shell shell, ScrolledComposite sc) {
        if (options.size() > 1) {
            shell.setSize(350, 300);
        } else {
            shell.setSize(sc.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
    }

    /**
     * Display text.
     *
     * @param display2 the display 2
     */
    private void displayText(Button display2) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (Option o : options) {
            if (o.selection) {
                sb.append(first ? o.text : ", " + o.text);
                first = false;
            }
        }
        display2.pack();
        this.pack();
    }

    /**
     * Adds the.
     *
     * @param string the string
     */
    public void add(String string) {
        options.add(new Option(string));
    }

    /**
     * Adds the.
     *
     * @param string the string
     * @param selection the selection
     */
    public void add(String string, boolean selection) {
        options.add(new Option(string, selection));
    }

    /**
     * Adds the modify listener.
     *
     * @param listener the listener
     */
    public void addModifyListener(ModifyListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        modifyListeners.add(listener);
    }

    /**
     * Adds the selection listener.
     *
     * @param listener the listener
     */
    public void addSelectionListener(SelectionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        selectionListeners.add(listener);
    }

    /**
     * Compute size.
     *
     * @param wHint the w hint
     * @param hHint the h hint
     * @param changed the changed
     * @return the point
     */
    public Point computeSize(int wHint, int hHint, boolean changed) {
        return display.computeSize(wHint, hHint);
    }

    /**
     * Gets the selections.
     *
     * @return the selections
     */
    public String[] getSelections() {
        ArrayDeque<String> selections = new ArrayDeque<String>();
        for (int index = 0; index < options.size(); index++) {
            Option option = options.get(index);
            if (option.selection) {
                selections.add(option.text);
            }
        }
        return selections.toArray(new String[selections.size()]);
    }

    /**
     * Removes the selection listener.
     *
     * @param listener the listener
     */
    public void removeSelectionListener(SelectionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        selectionListeners.remove(listener);
    }

    /**
     * Removes the modify listener.
     *
     * @param listener the listener
     */
    public void removeModifyListener(ModifyListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        modifyListeners.remove(listener);
    }

    /**
     * Sets the font.
     *
     * @param font the new font
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        display.setFont(font);
    }

    /**
     * Sets the items.
     *
     * @param items the new items
     */
    public void setItems(String[] items) {
        options = new ArrayList<Option>(items.length);
        for (String str : items) {
            add(str);
        }
    }

    /**
     * Sets the enabled.
     *
     * @param flag the new enabled
     */
    @Override
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
        display.setEnabled(flag);
    }
}
