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

package org.opengauss.mppdbide.bl.serverdatacache;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class TablespaceProperties.
 * 
 */

public class TablespaceProperties {

    private String name;
    private String location;
    private String maxsize;
    private String fileOption;
    private String seqPageCost;
    private String randomPageCost;
    private boolean isRelativePath;
    private Server server;

    /**
     * Instantiates a new tablespace properties.
     *
     * @param name the name
     * @param location the location
     * @param maxsize the maxsize
     * @param fileoption the fileoption
     * @param address the address
     * @param cfgpath the cfgpath
     * @param dataStorePath the data store path
     * @param seqPageCost the seq page cost
     * @param randomPageCost the random page cost
     * @param isRelativePath the is relative path
     */
    public TablespaceProperties(String name, String location, String maxsize, String fileoption, String seqPageCost,
            String randomPageCost, boolean isRelativePath) {
        this.name = name;
        this.location = location;
        this.maxsize = maxsize;
        this.fileOption = fileoption;
        this.seqPageCost = seqPageCost;
        this.randomPageCost = randomPageCost;
        this.isRelativePath = isRelativePath;
    }

    /**
     * Instantiates a new tablespace properties.
     *
     * @param name the name
     * @param seqPageCost the seq page cost
     * @param randomPageCost the random page cost
     */
    public TablespaceProperties(String name, String seqPageCost, String randomPageCost) {
        this.name = name;
        this.seqPageCost = seqPageCost;
        this.randomPageCost = randomPageCost;
    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    public Server getServer() {
        return server;
    }

    /**
     * Sets the server.
     *
     * @param server the new server
     */
    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * Gets the location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location.
     *
     * @param location the new location
     */
    public void setLocation(String location) {
        this.location = location;
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
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the maxsize.
     *
     * @return the maxsize
     */
    public String getTsMaxsize() {
        return maxsize;
    }

    /**
     * Sets the maxsize.
     *
     * @param maxsize the new maxsize
     */
    public void setTsMaxsize(String maxsize) {
        this.maxsize = maxsize;
    }

    /**
     * Gets the file option.
     *
     * @return the file option
     */
    public String getFileOption() {
        return fileOption;
    }

    /**
     * Sets the file option.
     *
     * @param fileOption the new file option
     */
    public void setFileOption(String fileOption) {
        this.fileOption = fileOption;
    }

    /**
     * Gets the seq page cost.
     *
     * @return the seq page cost
     */
    public String getSeqPageCost() {
        return seqPageCost;
    }

    /**
     * Sets the seq page cost.
     *
     * @param seqPageCost the new seq page cost
     */
    public void setSeqPageCost(String seqPageCost) {
        this.seqPageCost = seqPageCost;
    }

    /**
     * Gets the random page cost.
     *
     * @return the random page cost
     */
    public String getRandomPageCost() {
        return randomPageCost;
    }

    /**
     * Sets the random page cost.
     *
     * @param randomPageCost the new random page cost
     */
    public void setRandomPageCost(String randomPageCost) {
        this.randomPageCost = randomPageCost;
    }

    /**
     * Builds the query.
     *
     * @return the string
     */
    public String buildQuery() {
        StringBuilder selectQuery = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        setEnableAbsTablespace(selectQuery);
        selectQuery.append("CREATE TABLESPACE ");
        selectQuery.append(ServerObject.getQualifiedObjectName(name));
        setRelativeCluase(selectQuery);
        selectQuery.append(" LOCATION ");
        selectQuery.append(ServerObject.getLiteralName(location));
        setMaxSize(selectQuery);

        setFileSystem(selectQuery);
        if ("".equalsIgnoreCase(randomPageCost) && "".equalsIgnoreCase(seqPageCost)) {
            MPPDBIDELoggerUtility.info("Random Cost and seq cost is null");
        } else if ((!"".equalsIgnoreCase(randomPageCost)) && "".equalsIgnoreCase(seqPageCost)) {
            selectQuery.append(",");
            selectQuery.append(" random_page_cost=");
            selectQuery.append(randomPageCost);
        } else if ((!"".equalsIgnoreCase(seqPageCost)) && "".equalsIgnoreCase(randomPageCost)) {
            selectQuery.append(",");
            selectQuery.append(" seq_page_cost=");
            selectQuery.append(seqPageCost);
        } else if (!("".equalsIgnoreCase(randomPageCost)) && !("".equalsIgnoreCase(seqPageCost))) {
            selectQuery.append(",");
            selectQuery.append(" random_page_cost=");
            selectQuery.append(randomPageCost);
            selectQuery.append(",");
            selectQuery.append(" seq_page_cost=");
            selectQuery.append(seqPageCost);
        }
        if (!"".equalsIgnoreCase(fileOption)) {
            selectQuery.append(");");
        }
        String query = selectQuery.toString();
        return query;
    }

    /**
     * Sets the file system.
     *
     * @param selectQuery the new file system
     */
    private void setFileSystem(StringBuilder selectQuery) {
        if (!"".equalsIgnoreCase(fileOption)) {
            selectQuery.append(" WITH (");
            selectQuery.append(" filesystem =");
            selectQuery.append(fileOption);
        }
    }

    /**
     * Sets the max size.
     *
     * @param selectQuery the new max size
     */
    private void setMaxSize(StringBuilder selectQuery) {
        if (!"".equalsIgnoreCase(maxsize)) {
            selectQuery.append(" MAXSIZE ");
            selectQuery.append("'");
            selectQuery.append(maxsize);
            selectQuery.append("'");
        }
    }

    /**
     * Sets the relative cluase.
     *
     * @param selectQuery the new relative cluase
     */
    private void setRelativeCluase(StringBuilder selectQuery) {
        if (isRelativePath()) {
            selectQuery.append(" RELATIVE ");
        }
    }

    /**
     * Sets the enable abs tablespace.
     *
     * @param selectQuery the new enable abs tablespace
     */
    private void setEnableAbsTablespace(StringBuilder selectQuery) {
        if (null != server && server.isSupportTablespaceRelativePath()) {
            if (isRelativePath()) {
                selectQuery.append("set enable_absolute_tablespace=off ;");
            } else {
                selectQuery.append("set enable_absolute_tablespace=on ;");
            }
        }
    }

    /**
     * Builds the set option qry.
     *
     * @return the string
     */
    public String buildSetOptionQry() {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        query.append("ALTER TABLESPACE ");
        query.append(this.getName());
        if ("".equalsIgnoreCase(this.randomPageCost)) {
            query.append(" RESET (");
            query.append("random_page_cost");
        } else {
            query.append(" SET ( ");
            query.append("random_page_cost=");
            query.append(this.randomPageCost);
        }
        query.append(" );");
        query.append("ALTER TABLESPACE ");
        query.append(this.getName());
        if ("".equalsIgnoreCase(this.seqPageCost)) {
            query.append(" RESET (");
            query.append("seq_page_cost");
        } else {
            query.append(" SET ( ");
            query.append("seq_page_cost=");
            query.append(this.seqPageCost);
        }
        query.append(" );");

        return query.toString();
    }

    /**
     * Checks if is relative path.
     *
     * @return true, if is relative path
     */
    public boolean isRelativePath() {
        return isRelativePath;
    }

    /**
     * Sets the relative path.
     *
     * @param isRelatvPath the new relative path
     */
    public void setRelativePath(boolean isRelatvPath) {
        this.isRelativePath = isRelatvPath;
    }

}
