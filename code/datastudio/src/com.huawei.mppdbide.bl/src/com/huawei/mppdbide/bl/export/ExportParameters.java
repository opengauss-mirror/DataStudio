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

package com.huawei.mppdbide.bl.export;

import java.io.File;
import java.util.ArrayList;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportParameters.
 * 
 */

public class ExportParameters {

    private String encryptPwd;
    private String path;
    private Database db;
    private EXPORTTYPE type;
    private boolean isTablespaceOption;
    private ArrayList<ServerObject> objList;
    File workingDir;

    /**
     * Instantiates a new export parameters.
     *
     * @param pwd the pwd
     * @param path the path
     * @param db the db
     * @param type the type
     * @param obj the obj
     * @param isTableSpaceOption the is table space option
     * @param workingDir the working dir
     */
    public ExportParameters(String pwd, String path, Database db, EXPORTTYPE type, ServerObject obj,
            boolean isTableSpaceOption, File workingDir) {
        this.encryptPwd = pwd;
        this.path = path;
        this.db = db;
        this.type = type;
        this.isTablespaceOption = isTableSpaceOption;
        this.objList = new ArrayList<ServerObject>(1);
        objList.add(obj);
        this.workingDir = workingDir;
    }

    /**
     * Instantiates a new export parameters.
     *
     * @param pwd the pwd
     * @param path the path
     * @param db the db
     * @param type the type
     * @param obj the obj
     * @param isTableSpaceOption the is table space option
     * @param workingDir the working dir
     */
    public ExportParameters(String pwd, String path, Database db, EXPORTTYPE type, ArrayList<ServerObject> obj,
            boolean isTableSpaceOption, File workingDir) {
        this.encryptPwd = pwd;
        this.path = path;
        this.db = db;
        this.type = type;
        this.isTablespaceOption = isTableSpaceOption;
        this.objList = obj;
        this.workingDir = workingDir;
    }

    /**
     * Sets the server object list.
     *
     * @param objs the new server object list
     */
    public void setServerObjectList(ArrayList<ServerObject> objs) {
        this.objList = objs;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return encryptPwd;
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDb() {
        return db;
    }

    /**
     * Gets the export type.
     *
     * @return the export type
     */
    public EXPORTTYPE getExportType() {
        return type;
    }

    /**
     * Gets the server object.
     *
     * @return the server object
     */
    public ServerObject getServerObject() {
        return objList.get(0);
    }

    /**
     * Gets the server obj list.
     *
     * @return the server obj list
     */
    public ArrayList<ServerObject> getServerObjList() {
        return objList;
    }

    /**
     * Checks if is tablespace option.
     *
     * @return true, if is tablespace option
     */
    public boolean isTablespaceOption() {
        return isTablespaceOption;
    }

    /**
     * Sets the file path.
     *
     * @param path2 the new file path
     */
    public void setFilePath(String path2) {
        this.path = path2;
    }

    /**
     * Gets the working dir.
     *
     * @return the working dir
     */
    public File getWorkingDir() {
        return workingDir;
    }

}
