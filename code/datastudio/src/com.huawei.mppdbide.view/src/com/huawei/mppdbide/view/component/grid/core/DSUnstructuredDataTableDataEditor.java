/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.utils.Preferencekeys;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: DSUnstructuredDataTableDataEditor
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, 22-Jan-2020]
 * @since 22-Jan-2020
 */

public class DSUnstructuredDataTableDataEditor extends Dialog {
    /**
     * The Constant FOUR_MB.
     */
    private static final int FOUR_MB = 4194304; // 4*1024*2014 MB

    /**
     * The loading.
     */
    private String loading = MessageConfigLoader.getProperty(IMessagesConstants.LOADING_DATA);

    /**
     * The loaded.
     */
    private String loaded = MessageConfigLoader.getProperty(IMessagesConstants.LOADED_DATA);

    /**
     * The size exceeded four.
     */
    private String sizeExceededFour = MessageConfigLoader.getProperty(IMessagesConstants.FILE_SIZE_EXCEEDED_FOUR);

    /**
     * The cell editor.
     */
    protected DSUnstructuredDataCellEditor cellEditor = null;

    /**
     * The editor.
     */
    protected Text editor = null;

    /**
     * The text view.
     */
    private Text textView = null;

    /**
     * The value bytes.
     */
    protected byte[] valueBytes = null;

    /**
     * The clear btn.
     */
    protected Button clearBtn = null;

    /**
     * The openfile.
     */
    private Button openfile = null;

    /**
     * The save file.
     */
    private Button saveFile = null;

    /**
     * The status.
     */
    protected Label status = null;

    /**
     * The folder.
     */
    private TabFolder folder = null;

    /**
     * The image tab.
     */
    private TabItem imageTab = null;

    /**
     * The data tab.
     */
    private TabItem dataTab = null;

    /**
     * The loader.
     */
    private ImageLoader loader = null;

    private Canvas canvas = null;

    private byte[] originalBytes = null;

    /**
     * Instantiates a new DS unstructured data table data editor.
     *
     * @param parentShell the parent shell
     * @param cellEditor the cell editor
     * @param originalCanonicalValue the original canonical value
     */
    public DSUnstructuredDataTableDataEditor(Shell parentShell, DSUnstructuredDataCellEditor cellEditor,
            Object originalCanonicalValue) {
        super(parentShell);
        this.cellEditor = cellEditor;
        this.valueBytes = convertObjectToBytes(originalCanonicalValue);
        this.originalBytes = valueBytes;
    }

    /**
     * Convert object to bytes.
     *
     * @param value the value
     * @return the byte[]
     */
    public byte[] convertObjectToBytes(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        return null;
    }

    /**
     * Gets the editor text.
     *
     * @return the editor text
     */
    protected String getEditorText() {
        return (editor.getText().length() % 2 == 0) ? editor.getText() : editor.getText() + '0';
    }

    @Override
    protected void configureShell(Shell newShell) {
        newShell.setSize(800, 500);
        super.configureShell(newShell);

        newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_NODE));
        newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_EDIT_TABLE, this.getClass()));
        shellAlignCenter(newShell);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Shell align center.
     *
     * @param newShell the new shell
     */
    private void shellAlignCenter(Shell newShell) {
        Rectangle bounds = newShell.getMonitor().getBounds();
        Rectangle rect = newShell.getBounds();
        int xCordination = bounds.x + (bounds.width - rect.width) / 2;
        int yCordination = bounds.y + (bounds.height - rect.height) / 2;
        newShell.setLocation(xCordination, yCordination);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite maincomp = (Composite) super.createDialogArea(parent);
        maincomp.setLayout(new GridLayout(1, false));
        GridData maincompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        maincomp.setLayoutData(maincompGD);

        Composite buttonComposite = new Composite(maincomp, SWT.NONE);
        buttonComposite.setLayout(new GridLayout(2, false));
        GridData buttonCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        buttonComposite.setLayoutData(buttonCompositeGD);
        openFileButton(buttonComposite);
        saveFileButton(buttonComposite);

        folder = new TabFolder(maincomp, SWT.NONE);
        folder.setLayout(new GridLayout(1, false));
        GridData folderGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        folder.setLayoutData(folderGD);
        createDataTab(folder);
        createImageTab(folder);
        createTextTab(folder);

        addFolderItemListener();

        createStatus(maincomp);
        return maincomp;
    }

    private void addFolderItemListener() {
        folder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                byte[] editorBytes = null;
                if (MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_IMAGE_TAB)
                        .equals(folder.getSelection()[0].getText())) {
                    disposeCanvas();
                    editorBytes = folderSelectionGetEditorBytes(editorBytes);
                    if (editorBytes != null) {
                        imageTab.setControl(createImageComposite(folder, editorBytes));
                    } else {
                        imageTab.setControl(new Composite(folder, SWT.NONE));
                    }
                }
                if (MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_TEXT)
                        .equals(folder.getSelection()[0].getText())) {
                    editorBytes = folderSelectionGetEditorBytes(editorBytes);
                    try {
                        textView.setText(
                                (editorBytes != null) ? new String(editorBytes, MPPDBIDEConstants.FILEENCODING_UTF)
                                        : "");
                    } catch (UnsupportedEncodingException exception) {
                        MPPDBIDELoggerUtility.error("unsupported encoding format", exception);
                    }
                }
            }

            private byte[] folderSelectionGetEditorBytes(byte[] editorBytes) {
                try {
                    editorBytes = getBytesFromEditorText(getEditorText());
                } catch (StringIndexOutOfBoundsException excep) {
                    folder.setSelection(dataTab);
                    setStatusText(status,
                            MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_DATA_INVALID_NUMBER_DIGITS),
                            false);
                }
                return editorBytes;
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    /**
     * Creates the text tab.
     *
     * @param folder the folder
     */
    private void createTextTab(TabFolder folder) {
        TabItem textTab = new TabItem(folder, SWT.NONE);
        textTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_TEXT));
        Composite textViewComposite = new Composite(folder, SWT.NONE);
        textViewComposite.setLayout(new GridLayout(1, false));
        GridData textViewCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        textViewComposite.setLayoutData(textViewCompositeGD);
        createTextView(textViewComposite);
        textTab.setControl(textViewComposite);
    }

    /**
     * Creates the image tab.
     *
     * @param folder the folder
     */
    private void createImageTab(TabFolder folder) {
        imageTab = new TabItem(folder, SWT.NONE);
        imageTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_IMAGE_TAB));
    }

    /**
     * Creates the data tab.
     *
     * @param folder the folder
     */
    private void createDataTab(TabFolder folder) {
        dataTab = new TabItem(folder, SWT.NONE);
        dataTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_DATA_TAB));
        Composite editorTextComposite = new Composite(folder, SWT.NONE);
        editorTextComposite.setLayout(new GridLayout(1, false));
        GridData editorTextCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        editorTextComposite.setLayoutData(editorTextCompositeGD);
        createEditor(editorTextComposite);
        dataTab.setControl(editorTextComposite);
    }

    /**
     * Creates the scrolled image.
     *
     * @param folder the folder
     * @return the scrolled composite
     */
    private Composite createImageComposite(TabFolder folder, byte[] bytes) {
        Composite mainComposite = new Composite(folder, SWT.NONE);
        mainComposite.setLayout(new GridLayout(1, false));
        GridData mainCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        mainComposite.setLayoutData(mainCompositeGD);

        createScrollableImage(bytes, mainComposite);
        createImageProperties(mainComposite);

        return mainComposite;
    }

    private void createImageProperties(Composite mainComposite) {
        Composite imagePropertiesComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout imagePropertiesCompositeGL = new GridLayout(1, false);
        imagePropertiesCompositeGL.marginHeight = 0;
        imagePropertiesCompositeGL.marginWidth = 0;
        imagePropertiesComposite.setLayout(imagePropertiesCompositeGL);
        GridData imagePropertiesCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        imagePropertiesComposite.setLayoutData(imagePropertiesCompositeGD);

        Label imageProps = new Label(imagePropertiesComposite, SWT.RIGHT | SWT.WRAP);
        GridData imagePropsGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        imageProps.setLayoutData(imagePropsGD);
        if (loader != null) {
            imageProps.setText(loader.data[0].width + " x " + loader.data[0].height);
        }
    }

    private void createScrollableImage(byte[] bytes, Composite mainComposite) {
        ScrolledComposite mainSc = new ScrolledComposite(mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        Composite parentComposite = new Composite(mainSc, SWT.NONE);
        mainSc.setContent(parentComposite);
        parentComposite.setLayout(new GridLayout(1, false));
        GridData parentCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        parentComposite.setLayoutData(parentCompositeGD);
        if (loader != null) {
            loader = null;
        }
        if (bytes != null && bytes.length <= FOUR_MB && !sizeExceededFour.equals(editor.getText())) {
            loadImage(parentComposite, bytes);
        } else {
            loadTextExceededFour(parentComposite, bytes);
        }
        mainSc.setExpandHorizontal(true);
        mainSc.setExpandVertical(true);
        if (loader != null) {
            mainSc.setMinSize(loader.data[0].width, loader.data[0].height);
        } else {
            mainSc.setMinSize(parentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
        mainSc.pack();

        addImageScrolledCompositeListener(mainSc);
    }

    /**
     * Adds the image scrolled composite listener for mouse wheel scroll.
     *
     * @param mainSc the main sc
     */
    private void addImageScrolledCompositeListener(ScrolledComposite mainSc) {
        mainSc.addListener(SWT.Activate, new Listener() {
            @Override
            public void handleEvent(Event e) {
                mainSc.setFocus();
            }
        });
    }

    /**
     * Load text exceeded four.
     *
     * @param parentComposite the parent composite
     */
    private void loadTextExceededFour(Composite parentComposite, byte[] bytes) {
        Label textLabel = new Label(parentComposite, SWT.NONE);
        GridData textLabelGD = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        textLabel.setLayoutData(textLabelGD);
        // valueBytes length can be 0 for EMPTY_BLOB()
        textLabel.setText((bytes != null && bytes.length != 0) ? sizeExceededFour : "");
    }

    /**
     * Load image.
     *
     * @param parentComposite the parent composite
     */
    private void loadImage(Composite parentComposite, byte[] bytes) {
        try {
            InputStream inputStreamReader = new BufferedInputStream(new ByteArrayInputStream(bytes));
            loader = new ImageLoader();
            loader.load(inputStreamReader);
            /*
             * Check for number of frames and based on it render in label or
             * canvas
             */
            if (loader.data.length > 1) {
                loadImageInCanvas(parentComposite, bytes);
            } else {
                loadImageInLabel(parentComposite);
            }
        } catch (SWTException excep) {
            loader = null;
            loadTextNotSupported(parentComposite);
        }
    }

    /**
     * Load text not supported.
     *
     * @param parentComposite the parent composite
     */
    private void loadTextNotSupported(Composite parentComposite) {
        Label textLabel = new Label(parentComposite, SWT.NONE);
        GridData textLabelGD = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        textLabel.setLayoutData(textLabelGD);
        textLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_IMAGE_NOT_SUPPORTED));
    }

    /**
     * Load image in label.
     *
     * @param parentComposite the parent composite
     */
    private void loadImageInLabel(Composite parentComposite) {
        Label renderedImage = new Label(parentComposite, SWT.NONE);
        GridData renderedImageGD = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        renderedImage.setLayoutData(renderedImageGD);
        renderedImage.setImage(getFirstFrameImageFromLoader());
    }

    /**
     * Gets the first frame image from loader.
     *
     * @return the first frame image from loader
     */
    private Image getFirstFrameImageFromLoader() {
        ImageData firstFrameData = loader.data[0];
        Image firstFrameImage = new Image(Display.getCurrent(), firstFrameData);
        return firstFrameImage;
    }

    /**
     * Load image in canvas.
     *
     * @param parentComposite the parent composite
     */
    private void loadImageInCanvas(Composite parentComposite, byte[] bytes) {
        Animation canvasAnimation = new Animation(parentComposite, bytes);
        canvas = canvasAnimation.getCanvas();
    }

    /**
     * Dispose canvas.
     */
    private void disposeCanvas() {
        if (canvas != null && !canvas.isDisposed()) {
            canvas.dispose();
        }
        canvas = null;
    }

    /**
     * Open file button.
     *
     * @param maincomp the maincomp
     */
    private void openFileButton(Composite maincomp) {
        openfile = new Button(maincomp, SWT.PUSH);
        openfile.setImage(IconUtility.getIconImage(IiconPath.ICO_OPEN_SQL, this.getClass()));
        openfile.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.MENU_OPEN));

        openfile.addSelectionListener(addOpenFileSelectionListener());
    }

    /**
     * Adds the open file selection listener.
     *
     * @return the selection listener
     */
    private SelectionListener addOpenFileSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String fileName = openFileDialogAndGetFilename();
                if (fileName == null) {
                    return;
                }
                
                File file = new File(fileName);
                double fileSizeLimit = PreferenceWrapper.getInstance().getPreferenceStore()
                        .getInt(Preferencekeys.FILE_LIMIT_FOR_BYTEA);

                double fileSizeInMB = FileUtils.sizeOf(file) / (double) (1024 * 1024);
                if (fileSizeLimit != 0 && fileSizeInMB > fileSizeLimit) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_HEADER),
                            MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE));
                    return;
                }
                editor.setEnabled(true);
                editor.setText("");
                setStatusText(status, loading, true);
                readDataANdShowPreview(file);
            }

            private String openFileDialogAndGetFilename() {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
                String[] filterExt = {"*.*", "*.txt;*.rtf", "*.xml", "*.htm;*.html", "*.jpg;*.bmp;*.png;*.gif",
                    "*.wmf;*.emf"};
                String[] filterNames = {"All Files (*.*)", "Text (*.txt,*.rtf)", "XML (*.xml)", "HTML (*.htm,*.html)",
                    "Images (*.jpg,*.bmp,*.png,*.gif)", "Images (*.wmf,*.emf)"};
                dialog.setFilterExtensions(filterExt);
                dialog.setFilterNames(filterNames);

                String fileName = dialog.open();
                return fileName;
            }

            private void readDataANdShowPreview(File file) {
                Display.getDefault().asyncExec(new Runnable() {

                    /**
                     * Run.
                     */
                    public void run() {
                        try {
                            valueBytes = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
                            folder.setSelection(dataTab);
                            disposeCanvas();
                            if (valueBytes.length > FOUR_MB) {
                                editor.setText(sizeExceededFour);
                                editor.setEnabled(false);
                                status.setText("");
                            } else {
                                editor.setText(loadHexInEditorText());
                                setStatusText(status, loaded + " (" + valueBytes.length + " Bytes)", true);
                            }
                            clearBtn.setEnabled(true);
                        } catch (IOException excep) {
                            MPPDBIDELoggerUtility.error("Exception occured while reading file", excep);
                            status.setText("");
                        }
                    }
                });
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    /**
     * Save file button.
     *
     * @param maincomp the maincomp
     */
    private void saveFileButton(Composite maincomp) {
        saveFile = new Button(maincomp, SWT.PUSH);
        saveFile.setImage(IconUtility.getIconImage(IiconPath.ICO_SAVE_SQL, this.getClass()));
        saveFile.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.MENU_SAVE));

        saveFile.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String editorTxt = getEditorText();
                if (!sizeExceededFour.equals(editorTxt)) {
                    try {
                        valueBytes = getBytesFromEditorText(editorTxt);
                    } catch (StringIndexOutOfBoundsException excep) {
                        setStatusText(status,
                                MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_DATA_INVALID_NUMBER_DIGITS),
                                false);
                        return;
                    }
                }
                if (valueBytes == null) {
                    return;
                }
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
                String[] filterExt = {"*.*", "*.txt;*.rtf", "*.xml", "*.htm;*.html", "*.jpg;*.bmp;*.png;*.gif",
                    "*.wmf;*.emf"};
                String[] filterNames = {"All Files (*.*)", "Text (*.txt,*.rtf)", "XML (*.xml)", "HTML (*.htm,*.html)",
                    "Images (*.jpg,*.bmp,*.png,*.gif)", "Images (*.wmf,*.emf)"};
                dialog.setFilterExtensions(filterExt);
                dialog.setFilterNames(filterNames);

                handleFileOperation(dialog);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    private void handleFileOperation(FileDialog dialog) {
        String fileName = dialog.open();
        if (fileName == null) {
            return;
        }
        File file = null;
        boolean isFileCreated = false;
        boolean isFileDeleted = false;
        try {
            file = new File(fileName);
            if (!file.exists()) {
                isFileCreated = file.createNewFile();
            }
            if (isFileCreated) {
                MPPDBIDELoggerUtility.info("file created successfully");
            }
            Files.write(Paths.get(file.getCanonicalPath()), valueBytes, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException excep) {
            if (file.exists()) {
                isFileDeleted = file.delete();
            }
            if (isFileDeleted) {
                MPPDBIDELoggerUtility.info("file deleted successfully");
            }
            MPPDBIDELoggerUtility.error("Exception occured while saving the file data", excep);
        }
    }

    /**
     * Creates the editor.
     *
     * @param maincomp the maincomp
     */
    private void createEditor(Composite maincomp) {
        editor = new Text(maincomp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData editorGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        editor.setLayoutData(editorGD);

        if (valueBytes == null) {
            editor.setText("");
        } else if (valueBytes.length > FOUR_MB) {
            editor.setText(sizeExceededFour);
            editor.setEnabled(false);
        } else {
            // convert the bytes to HEX and Display
            Display.getDefault().asyncExec(new Runnable() {
                /**
                 * Run.
                 */
                public void run() {
                    setStatusText(status, loading, true);
                    editor.setText(loadHexInEditorText());
                    setStatusText(status, loaded + " (" + valueBytes.length + " Bytes)", true);
                }
            });
        }

        addEditorTextKeyListener();
    }

    /**
     * Creates the text view.
     *
     * @param maincomp the maincomp
     */
    private void createTextView(Composite maincomp) {
        textView = new Text(maincomp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData editorGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        textView.setLayoutData(editorGD);
        textView.setEditable(false);
    }

    /**
     * Load hex in editor text.
     *
     * @return the string
     */
    protected String loadHexInEditorText() {
        return DSUnstructuredDataConversionHelper.bytesToHex(valueBytes);
    }

    /**
     * Adds the editor text key listener.
     */
    protected void addEditorTextKeyListener() {
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                status.setText("");
                keyEvent.doit = false;
                if (String.valueOf(keyEvent.character).matches("[0-9A-Fa-f]+") || keyEvent.character == '\b'
                        || (keyEvent.stateMask == SWT.CTRL && keyEvent.keyCode == 'v')) {
                    if (!cellEditor.isCellEditable()) {
                        clearBtn.setEnabled(true);
                    }
                    keyEvent.doit = true;
                } else if (keyEvent.keyCode == SWT.ARROW_UP || keyEvent.keyCode == SWT.ARROW_DOWN
                        || keyEvent.keyCode == SWT.ARROW_LEFT || keyEvent.keyCode == SWT.ARROW_RIGHT) {
                    keyEvent.doit = true;

                } else if ((keyEvent.stateMask == SWT.CTRL && keyEvent.keyCode == 'c')) {
                    keyEvent.doit = true;
                } else if (keyEvent.stateMask == SWT.CTRL && keyEvent.keyCode == 'a') {
                    editor.selectAll();
                    editor.selectAll();
                    keyEvent.doit = false;
                    keyEvent.doit = false;
                } else {
                    keyEvent.doit = false;
                }
            }
        });
    }

    /**
     * Creates the status.
     *
     * @param maincomp the maincomp
     */
    private void createStatus(Composite maincomp) {
        status = new Label(maincomp, SWT.RIGHT | SWT.WRAP);
        GridData statusGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        statusGD.heightHint = 20;
        statusGD.horizontalAlignment = SWT.FILL;
        status.setLayoutData(statusGD);
    }

    /**
     * Sets the status text.
     *
     * @param status the status
     * @param message the message
     * @param type the type
     */
    private void setStatusText(Label status, String message, boolean type) {
        status.setText(message);
        if (type) {
            status.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
        } else {
            status.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        String clearLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.CLEAR_CONSOLE) + "     ";
        String okLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
        final String cancelLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        clearBtn = createButton(parent, UIConstants.CLEAR_ID, clearLbl, false);
        createButton(parent, UIConstants.OK_ID, okLbl, true);
        createButton(parent, UIConstants.CANCEL_ID, cancelLabel, false);

        configureEditorAndButton();
    }

    /**
     * Configure editor and button.
     */
    private void configureEditorAndButton() {
        openfile.setEnabled(!cellEditor.isCellEditable());
        editor.setEditable(!cellEditor.isCellEditable());
        clearBtn.setEnabled(!cellEditor.isCellEditable() && valueBytes != null);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == UIConstants.OK_ID) {
            String editorText = getEditorText();
            if (!sizeExceededFour.equals(editorText)) {
                try {
                    valueBytes = getBytesFromEditorText(editorText);
                } catch (StringIndexOutOfBoundsException excep) {
                    setStatusText(status,
                            MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_DATA_INVALID_NUMBER_DIGITS),
                            false);
                    return;
                }
            }
            if (valueBytes == null) {
                cellEditor.setEditorValue(null);
            } else if (Arrays.equals(originalBytes, valueBytes)) {
                close();
                return;
            } else {
                cellEditor.setEditorValue(valueBytes);
            }
            cellEditor.commit(MoveDirectionEnum.NONE, true);
            close();
        } else if (buttonId == UIConstants.CLEAR_ID) {
            valueBytes = null;
            editor.setText("");
            editor.setEnabled(true);
            status.setText("");
            folder.setSelection(dataTab);
        } else {
            close();
        }
    }

    /**
     * Gets the bytes from editor text.
     *
     * @param editorTxt the editor txt
     * @return the bytes from editor text
     */
    protected byte[] getBytesFromEditorText(String editorTxt) {
        return DSUnstructuredDataConversionHelper.hexStringToByteArray(editorTxt);
    }
}
