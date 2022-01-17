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

package com.huawei.mppdbide.view.userrole;

import java.awt.Rectangle;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Title: class
 * 
 * Description: The Class MultipleSelectionComboComponent.
 *
 * @since 3.0.0
 */
public class MultipleSelectionComboComponent extends Composite {

    /**
     * The text.
     */
    Text text = null;

    /**
     * The items.
     */
    String[] items = null;

    /**
     * The selection.
     */
    int[] selection = null;

    /**
     * The float shell.
     */
    Shell floatShell = null;

    /**
     * The list.
     */
    List list = null;

    /**
     * Instantiates a new multiple selection combo component.
     *
     * @param parent the parent
     * @param items the items
     * @param selection the selection
     * @param style the style
     */
    public MultipleSelectionComboComponent(Composite parent, String[] items, int[] selection, int style) {
        super(parent, style);
        this.selection = Arrays.copyOf(selection, selection.length);
        this.items = Arrays.copyOf(items, items.length);
        init();
    }

    /**
     * Inits the.
     */
    private void init() {
        setLayout(new GridLayout());
        text = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
        text.setLayoutData(new GridData(GridData.FILL_BOTH));

        text.addMouseListener(new MouseAdapter() {

            /**
             * Mouse down.
             *
             * @param event the event
             */
            public void mouseDown(MouseEvent event) {
                super.mouseDown(event);
                initFloatShell();
            }
        });
    }

    /**
     * Inits the float shell.
     */
    private void initFloatShell() {
        Point point = text.getParent().toDisplay(text.getLocation());
        Point size = text.getSize();
        Rectangle shellRect = new Rectangle(point.x, point.y + size.y, size.x, 0);
        floatShell = new Shell(MultipleSelectionComboComponent.this.getShell(), SWT.NO_TRIM);

        GridLayout gl = new GridLayout();
        gl.marginBottom = 2;
        gl.marginTop = 2;
        gl.marginRight = 2;
        gl.marginLeft = 2;
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        floatShell.setLayout(gl);

        list = new List(floatShell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        for (String value : items) {
            list.add(value);
        }
        GridData gd = new GridData(GridData.FILL_BOTH);
        list.setLayoutData(gd);
        floatShell.setSize(shellRect.width, 180);
        floatShell.setLocation(shellRect.x, shellRect.y);

        list.addMouseListener(addListMouseListener());

        floatShell.addShellListener(addFloatShellListener());

        floatShell.open();

        list.setSelection(selection);

    }

    private ShellAdapter addFloatShellListener() {
        return new ShellAdapter() {

            /**
             * Shell deactivated.
             *
             * @param arg0 the arg 0
             */
            public void shellDeactivated(ShellEvent arg0) {
                if (floatShell != null && !floatShell.isDisposed()) {
                    selection = list.getSelectionIndices();
                    displayText();
                    floatShell.dispose();
                }
            }
        };
    }

    private MouseAdapter addListMouseListener() {
        return new MouseAdapter() {

            /**
             * Mouse up.
             *
             * @param event the event
             */
            public void mouseUp(MouseEvent event) {
                super.mouseUp(event);
                selection = list.getSelectionIndices();
                if ((event.stateMask & SWT.CTRL) == 0) {
                    floatShell.dispose();
                    displayText();
                }
            }
        };
    }

    /**
     * Display text.
     */
    private void displayText() {
        if (selection != null && selection.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < selection.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(items[selection[i]]);
            }
            text.setText(sb.toString());
        } else {
            text.setText("");
        }
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        if (selection != null && selection.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (int index = 0; index < selection.length; index++) {
                if (index > 0) {
                    sb.append(",");
                }
                sb.append(items[selection[index]]);
            }
            return sb.toString();
        } else {
            return null;
        }
    }

}
