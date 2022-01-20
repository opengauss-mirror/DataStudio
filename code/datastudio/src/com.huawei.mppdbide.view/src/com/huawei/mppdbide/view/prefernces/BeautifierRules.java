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

package com.huawei.mppdbide.view.prefernces;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: BeautifierRules
 * 
 * Description: The Class BeautifierRules.
 *
 * @since 3.0.0
 */
public class BeautifierRules {
    /** 
     * The instance. 
     */
    private static volatile BeautifierRules instance = null;

    /** 
     * The max file size 20 KB. 
     */
    private static int MAX_FILE_SIZE = 20;

    /** 
     * The general. 
     */
    @Expose
    @SerializedName("General")
    private DSFormatterGeneralRules general;

    /** 
     * The control structures. 
     */
    @Expose
    @SerializedName("Control_Structures")
    private DSFormatterControlStructureRules controlStructures;

    /** 
     * The dml. 
     */
    @Expose
    @SerializedName("DML")
    private DSFormatterDMLRules dml;

    /** 
     * The parameter declaration. 
     */
    @Expose
    @SerializedName("Parameter_Declaration")
    private DSFormatterParameterDeclarationRules parameterDeclaration;

    /**
     * Gets the single instance of BeautifierRules.
     *
     * @return single instance of BeautifierRules
     */
    public static BeautifierRules getInstance() {
        if (null == instance) {
            instance = new BeautifierRules();
        }
        return instance;
    }

    /**
     * Gets the general.
     *
     * @return the general
     */
    public DSFormatterGeneralRules getGeneral() {
        return general;
    }

    /**
     * Sets the general.
     *
     * @param general the new general
     */
    public void setGeneral(DSFormatterGeneralRules general) {
        this.general = general;
    }

    /**
     * Gets the control structures.
     *
     * @return the control structures
     */
    public DSFormatterControlStructureRules getControlStructures() {
        return controlStructures;
    }

    /**
     * Sets the control structures.
     *
     * @param controlStructures the new control structures
     */
    public void setControlStructures(DSFormatterControlStructureRules controlStructures) {
        this.controlStructures = controlStructures;
    }

    /**
     * Gets the dml.
     *
     * @return the dml
     */
    public DSFormatterDMLRules getDml() {
        return dml;
    }

    /**
     * Sets the dml.
     *
     * @param dml the new dml
     */
    public void setDml(DSFormatterDMLRules dml) {
        this.dml = dml;
    }

    /**
     * Gets the parameter declaration.
     *
     * @return the parameter declaration
     */
    public DSFormatterParameterDeclarationRules getParameterDeclaration() {
        return parameterDeclaration;
    }

    /**
     * Sets the parameter declaration.
     *
     * @param parameterDeclaration the new parameter declaration
     */
    public void setParameterDeclaration(DSFormatterParameterDeclarationRules parameterDeclaration) {
        this.parameterDeclaration = parameterDeclaration;
    }

    /**
     * Write beautifier to disk.
     *
     * @param filePath the file path
     */
    public void writeBeautifierToDisk(String filePath) {

        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                generateFormatterWriteErrorDialog();
                MPPDBIDELoggerUtility.error("Error while creating the formatter file");
            }
        } else {
            if (!isOverwrite(filePath)) {
                return;
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(this);
        try {
            Files.write(path, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_EXPORT),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_SUCCES_DAILOG_TITLE));
        } catch (IOException exception) {
            generateFormatterWriteErrorDialog();
            MPPDBIDELoggerUtility.error("Error while writing formatter file to the disk");
        }
    }

    private boolean isOverwrite(String path) {
        return UIConstants.OK_ID == MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_FILE_OVERWRITE),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_NAME_EXIT, path));
    }

    private void generateFormatterWriteErrorDialog() {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FILE_CREATION_ERROR_HEADER),
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FILE_CREATION_ERROR));
    }

    private void generateFormatterReadErrorDialog() {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FILE_READ_ERROR_HEADER),
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FILE_READ_ERROR));
    }

    private void generateInvalidFormatterDialog() {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_INVALID_FILE_SELECTED),
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_SELECT_VALID_JSON));
    }

    /**
     * Read beautifier from disk.
     *
     * @param filePath the file path
     * @return the beautifier rules
     */
    public BeautifierRules readBeautifierFromDisk(String filePath) {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        if (isFileSizeExceeded(path)) {
            return null;
        }
        BeautifierRules obj = null;
        if (Files.exists(path)) {
            byte[] readAllBytes = null;
            try {
                readAllBytes = Files.readAllBytes(path);
            } catch (IOException e) {
                generateFormatterReadErrorDialog();
                MPPDBIDELoggerUtility.error("Error while reading the formatter file");
                return null;
            }
            String json = new String(readAllBytes, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            try {
                obj = gson.fromJson(json, this.getClass());
                if (obj.getGeneral() == null || obj.getControlStructures() == null || obj.getDml() == null
                        || obj.getParameterDeclaration() == null) {
                    generateInvalidFormatterDialog();
                    return null;
                }
            } catch (JsonSyntaxException excep) {
                generateInvalidFormatterDialog();
                MPPDBIDELoggerUtility.error("[Invalid json] " + excep.getCause().getMessage());
                return null;
            }
        } else {
            generateFileDoesntExistDialog();
            return null;
        }
        String errorFields = validateJsonValues(obj);
        if (!errorFields.isEmpty()) {
            generateErrorResideInFileDialog(errorFields);
            return null;
        }
        return obj;
    }

    private boolean isFileSizeExceeded(Path path) {
        double fileSizeInKB = FileUtils.sizeOf(path.toFile()) / (double) (1024);
        if (fileSizeInKB > MAX_FILE_SIZE) {
            generateFormatterReadErrorDialog();
            MPPDBIDELoggerUtility.error("Error while reading the formatter file. File size exceeded 20KB");
            return true;
        }
        return false;
    }

    private void generateFileDoesntExistDialog() {
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_IMPORT),
                MessageConfigLoader.getProperty(IMessagesConstants.MSG_SELECT_FILE_DOES_NOT_EXIST));
    }

    private void generateErrorResideInFileDialog(String errorFields) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_ERROR_RESIDE_IN_FILE), errorFields);
    }

    private String validateJsonValues(BeautifierRules br) {
        StringBuilder errorMsg = new StringBuilder();
        validateGeneralTabValues(br, errorMsg);
        validateDMLTabValues(br, errorMsg);
        validateParameterTabValues(br, errorMsg);
        return errorMsg.toString();
    }

    private void validateParameterTabValues(BeautifierRules br, StringBuilder errorMsg) {
        int parameterFormat = br.getParameterDeclaration().getParameterFormat();
        if (parameterFormat < 0 || parameterFormat > 2) {
            appendErrorMessage(errorMsg, MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_PARAMATER),
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));
        }
    }

    private void validateDMLTabValues(BeautifierRules br, StringBuilder errorMsg) {
        String formatterDml = MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_DML);
        int dmlSelectFormat = br.getDml().getDmlSelectFormat();
        if (dmlSelectFormat < 0 || dmlSelectFormat > 2) {
            appendErrorMessage(errorMsg, formatterDml,
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_SELECT) + " "
                            + MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));
        }
        int dmlInsertFormat = br.getDml().getDmlInsertFormat();
        if (dmlInsertFormat < 0 || dmlInsertFormat > 2) {
            appendErrorMessage(errorMsg, formatterDml,
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_INSERT) + " "
                            + MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));
        }
        int dmlUpdateFormat = br.getDml().getDmlUpdateFormat();
        if (dmlUpdateFormat < 0 || dmlUpdateFormat > 2) {
            appendErrorMessage(errorMsg, formatterDml,
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_UPDATE) + " "
                            + MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));
        }
        int dmlOthersFormat = br.getGeneral().getGeneralItemListFormat();
        if (dmlOthersFormat < 0 || dmlOthersFormat > 2) {
            appendErrorMessage(errorMsg, formatterDml,
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_OTHERS) + " "
                            + MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));
        }
    }

    private void validateGeneralTabValues(BeautifierRules br, StringBuilder errorMsg) {
        String formatterGeneral = MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_GENERAL);
        if (br.getGeneral().validateGeneralIndent()) {
            appendErrorMessage(errorMsg, formatterGeneral,
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_INDENT));
        }
        if (br.getGeneral().validateGeneralRightMargin()) {
            appendErrorMessage(errorMsg, formatterGeneral,
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_RIGHT_MARGIN));
        }
        if (br.getGeneral().validateGeneralTabCharacterSize()) {
            appendErrorMessage(errorMsg, formatterGeneral,
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_TAB_CHAR_SIZE));
        }
    }

    private StringBuilder appendErrorMessage(StringBuilder sb, String tab, String feild) {
        return sb.append(tab).append(" - ").append(feild).append(MPPDBIDEConstants.LINE_SEPARATOR);
    }
}
