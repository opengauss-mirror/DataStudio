/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShortcutKeyMapper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ShortcutKeyMapper {

    private boolean isCtrlPressed;
    private boolean isAltPressed;
    private boolean isShiftPressed;
    private boolean isFKeyPressed;
    private String charStringPressed;
    private String digitStringPressed;
    private String fvalueKeyPressed;
    private boolean isBackSpacePressed;
    private StringBuilder builder = null;

    /**
     * Checks if is ctrl pressed.
     *
     * @return true, if is ctrl pressed
     */
    public boolean isCtrlPressed() {
        return isCtrlPressed;
    }

    /**
     * Sets the ctrl pressed.
     *
     * @param isCtrlPressd the new ctrl pressed
     */
    public void setCtrlPressed(boolean isCtrlPressd) {
        this.isCtrlPressed = isCtrlPressd;
    }

    /**
     * Checks if is alt pressed.
     *
     * @return true, if is alt pressed
     */
    public boolean isAltPressed() {
        return isAltPressed;
    }

    /**
     * Sets the alt pressed.
     *
     * @param isAltPressd the new alt pressed
     */
    public void setAltPressed(boolean isAltPressd) {
        this.isAltPressed = isAltPressd;
    }

    /**
     * Checks if is shift pressed.
     *
     * @return true, if is shift pressed
     */
    public boolean isShiftPressed() {
        return isShiftPressed;
    }

    /**
     * Sets the shift pressed.
     *
     * @param isShiftPressd the new shift pressed
     */
    public void setShiftPressed(boolean isShiftPressd) {
        this.isShiftPressed = isShiftPressd;
    }

    /**
     * Checks if is f key pressed.
     *
     * @return true, if is f key pressed
     */
    public boolean isFKeyPressed() {
        return isFKeyPressed;
    }

    /**
     * Sets the f key pressed.
     *
     * @param isFKeyPressd the new f key pressed
     */
    public void setFKeyPressed(boolean isFKeyPressd) {
        this.isFKeyPressed = isFKeyPressd;
    }

    /**
     * Gets the char string pressed.
     *
     * @return the char string pressed
     */
    public String getCharStringPressed() {
        return charStringPressed;
    }

    /**
     * Sets the char string pressed.
     *
     * @param charStringPressed the new char string pressed
     */
    public void setCharStringPressed(String charStringPressed) {
        this.charStringPressed = charStringPressed;
    }

    /**
     * Gets the digit string pressed.
     *
     * @return the digit string pressed
     */
    public String getDigitStringPressed() {
        return digitStringPressed;
    }

    /**
     * Sets the digit string pressed.
     *
     * @param digitStringPressed the new digit string pressed
     */
    public void setDigitStringPressed(String digitStringPressed) {
        this.digitStringPressed = digitStringPressed;
    }

    /**
     * Gets the fvalue key pressed.
     *
     * @return the fvalue key pressed
     */
    public String getFvalueKeyPressed() {
        return fvalueKeyPressed;
    }

    /**
     * Sets the fvalue key pressed.
     *
     * @param fvalueKeyPressed the new fvalue key pressed
     */
    public void setFvalueKeyPressed(String fvalueKeyPressed) {
        this.fvalueKeyPressed = fvalueKeyPressed;
    }

    /**
     * Gets the checks if is back space pressed.
     *
     * @return the checks if is back space pressed
     */
    public boolean getIsBackSpacePressed() {
        return isBackSpacePressed;
    }

    /**
     * Sets the checks if is back space pressed.
     *
     * @param isBackSpacePressed the new checks if is back space pressed
     */
    public void setIsBackSpacePressed(boolean isBackSpacePressed) {
        this.isBackSpacePressed = isBackSpacePressed;
    }

    /**
     * Gets the short cut mapper keys.
     *
     * @return the short cut mapper keys
     */
    public String getShortCutMapperKeys() {
        builder = new StringBuilder("");
        if (isBackSpacePressed) {
            builder = new StringBuilder("");
            clear();
        }
        if (isCtrlPressed) {
            builder.append("CTRL+");
        }
        if (isAltPressed) {
            builder.append("ALT+");
        }
        if (isShiftPressed) {
            builder.append("SHIFT+");
        }
        if (isFKeyPressed) {
            builder.append(getFvalueKeyPressed());

        } else if (null != charStringPressed && !charStringPressed.isEmpty()) {
            if (!builder.toString().isEmpty()) {
                builder.append(getCharStringPressed());

            } else {
                setCharStringPressed("");

            }
        } else if (null != digitStringPressed && !digitStringPressed.isEmpty()) {
            if (!builder.toString().isEmpty()) {
                builder.append(getDigitStringPressed());
            } else {
                setDigitStringPressed("");

            }
        }
        return builder.toString();

    }

    /**
     * Clear.
     */
    public void clear() {
        setCtrlPressed(false);
        setIsBackSpacePressed(false);
        setAltPressed(false);
        setShiftPressed(false);
        setFKeyPressed(false);
        setCharStringPressed("");
        setDigitStringPressed("");
    }
}
