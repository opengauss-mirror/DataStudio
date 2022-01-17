/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.search;

import com.huawei.mppdbide.bl.search.SearchNameMatchEnum;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchObjInfo.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class SearchObjInfo {

    /**
     * The Constant TABLES_VIEWS_FAST_LOAD.
     */
    public static final String TABLES_VIEWS_FAST_LOAD = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ns.nspname  nsname,"
            + "tbl.relowner relowner,tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions ";

    /**
     * The Constant TABLES_VIEWS_FAST_LOAD_WHERE.
     */
    public static final String TABLES_VIEWS_FAST_LOAD_WHERE = "from pg_class tbl left join pg_namespace ns on tbl.relnamespace = ns.oid "
            + "left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) ";

    /**
     * The Constant NO_PRIVILEGE.
     */
    public static final String NO_PRIVILEGE = ")";

    /**
     * The Constant REL_TYPE_TABLES.
     */
    public static final String REL_TYPE_TABLES = " (tbl.relkind='r' or tbl.relkind='f'";

    /**
     * The Constant NODEGROUP_PRIVILEGE.
     */
    public static final String NODEGROUP_PRIVILEGE = " and tbl.oid in (with x as (select pcrelid from pgxc_class "
            + "where has_nodegroup_privilege(pgroup, 'USAGE'))"
            + " select * from x where has_table_privilege(x.pcrelid, 'SELECT')))";

    /**
     * The Constant TABLE_PRIVILEGE.
     */
    public static final String TABLE_PRIVILEGE = " and has_table_privilege(tbl.oid, 'SELECT'))";

    /**
     * The Constant REL_TYPE_VIEWS.
     */
    public static final String REL_TYPE_VIEWS = " (tbl.relkind='v'";

    /**
     * The Constant VIEWS_PRIVILEGE.
     */
    public static final String VIEWS_PRIVILEGE = " and has_table_privilege(tbl.oid, 'SELECT'))";

    /**
     * The Constant REL_TYPE_SEQUENCE.
     */
    public static final String REL_TYPE_SEQUENCE = " (tbl.relkind='S'";

    /**
     * The Constant SEQUENCE_PRIVILEGE.
     */
    public static final String SEQUENCE_PRIVILEGE = " and has_sequence_privilege('\"'|| ns.nspname||'\"'||'.'||'\"'||tbl.relname||'\"', 'USAGE'))";

    /**
     * The Constant FUNCTION_PROCEDURE_FAST_LOAD.
     */
    public static final String FUNCTION_PROCEDURE_FAST_LOAD = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, "
            + "pr.proallargtypes alltype, " + "pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, "
            + "pr.proargmodes argmod, pr.proretset retset, lng.lanname lang,ns.nspname nsname ";

    /**
     * The Constant FUNCTION_PROCEDURE_FAST_LOAD_WHERE.
     */
    public static final String FUNCTION_PROCEDURE_FAST_LOAD_WHERE = "FROM pg_proc pr JOIN pg_language lng ON lng.oid=prolang "
            + "left join  pg_namespace ns ON ns.oid=pr.pronamespace " + "WHERE lng.lanname in ('plpgsql','sql','c') ";

    /**
     * The Constant FUNCTION_PROCEDURE_FAST_LOAD_PRIVILEGE.
     */
    public static final String FUNCTION_PROCEDURE_FAST_LOAD_PRIVILEGE = " and has_function_privilege(pr.oid, 'EXECUTE')";

    /**
     * The Constant SYNONYM_FAST_LOAD_PRIVILAGE
     */
    public static final String SYNONYM_FAST_LOAD_PRIVILAGE = "select syn.synname,syn.oid,syn.synnamespace,"
            + "syn.synobjschema,syn.synowner ";

    /**
     * The Constant TRIGGER_FAST_LOAD_PRIVILAGE
     */
    public static final String TRIGGER_FAST_LOAD_PRIVILAGE = "select t.oid as oid, t.tgrelid as tableoid, t.tgname, ns.nspname as nsname, c.relnamespace "
    		+ "as relnamespace, t.tgfoid as functionoid,t.tgtype as tgtype, t.tgenabled as tgenable, pg_get_triggerdef(t.oid) as ddlmsg ";

    /**
     * TRIGGER_FAST_LOAD_PRIVILAGE_WHERE
     */
    public static final String TRIGGER_FAST_LOAD_PRIVILAGE_WHERE = "from pg_trigger t left join pg_class c on t.tgrelid = c.oid inner join "
    		+ "pg_namespace ns on c.relnamespace=ns.oid and c.relnamespace =";
    
    /**
     * The Constant SYNONYM_FROM_CLASS
     */
    public static final String SYNONYM_FROM_CLASS = " from pg_synonym syn";

    private int selectedserver;
    private int selectedDB;
    private int selectedNamespace;
    private String searchText;
    private SearchNameMatchEnum nameMatch;
    private boolean matchCase;
    private boolean tableSelected;
    private boolean viewsSelected;
    private boolean funProcSelected;
    private boolean sequenceSelected;
    private boolean synonymSelected;
    private boolean triggerSelected;

    /**
     * Gets the selectedserver.
     *
     * @return the selectedserver
     */
    public int getSelectedserver() {
        return selectedserver;
    }

    /**
     * Sets the selectedserver.
     *
     * @param selectedserver the new selectedserver
     */
    public void setSelectedserver(int selectedserver) {
        this.selectedserver = selectedserver;
    }

    /**
     * Gets the selected DB.
     *
     * @return the selected DB
     */
    public int getSelectedDB() {
        return selectedDB;
    }

    /**
     * Sets the selected DB.
     *
     * @param selectedDB the new selected DB
     */
    public void setSelectedDB(int selectedDB) {
        this.selectedDB = selectedDB;
    }

    /**
     * Gets the selected namespace.
     *
     * @return the selected namespace
     */
    public int getSelectedNamespace() {
        return selectedNamespace;
    }

    /**
     * Sets the selected namespace.
     *
     * @param selectedNamespace the new selected namespace
     */
    public void setSelectedNamespace(int selectedNamespace) {
        this.selectedNamespace = selectedNamespace;
    }

    /**
     * Gets the search text.
     *
     * @return the search text
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Sets the search text.
     *
     * @param searchText the new search text
     */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * Gets the name match index.
     *
     * @return the name match index
     */
    public SearchNameMatchEnum getNameMatchIndex() {
        return this.nameMatch;
    }

    /**
     * Sets the name match.
     *
     * @param nameMatchIndex the new name match
     */
    public void setNameMatch(int nameMatchIndex) {
        switch (nameMatchIndex) {
            case 0: {
                this.nameMatch = SearchNameMatchEnum.CONTAINS;
                break;
            }
            case 1: {
                this.nameMatch = SearchNameMatchEnum.STARTS_WITH;
                break;
            }
            case 2: {
                this.nameMatch = SearchNameMatchEnum.EXACT_WORD;
                break;
            }
            case 3: {
                this.nameMatch = SearchNameMatchEnum.REGULAR_EXPRESSION;
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Checks if is match case.
     *
     * @return true, if is match case
     */
    public boolean isMatchCase() {
        return matchCase;
    }

    /**
     * Sets the match case.
     *
     * @param matchCase the new match case
     */
    public void setMatchCase(boolean matchCase) {
        this.matchCase = matchCase;
    }

    /**
     * Checks if is table selected.
     *
     * @return true, if is table selected
     */
    public boolean isTableSelected() {
        return tableSelected;
    }

    /**
     * Sets the table selected.
     *
     * @param tableSelected the new table selected
     */
    public void setTableSelected(boolean tableSelected) {
        this.tableSelected = tableSelected;
    }

    /**
     * Checks if is views selected.
     *
     * @return true, if is views selected
     */
    public boolean isViewsSelected() {
        return viewsSelected;
    }

    /**
     * Sets the views selected.
     *
     * @param viewsSelected the new views selected
     */
    public void setViewsSelected(boolean viewsSelected) {
        this.viewsSelected = viewsSelected;
    }

    /**
     * Checks if is fun proc selected.
     *
     * @return true, if is fun proc selected
     */
    public boolean isFunProcSelected() {
        return funProcSelected;
    }

    /**
     * Sets the fun proc selected.
     *
     * @param funProcSelected the new fun proc selected
     */
    public void setFunProcSelected(boolean funProcSelected) {
        this.funProcSelected = funProcSelected;
    }

    /**
     * Checks if is sequence selected.
     *
     * @return true, if is sequence selected
     */
    public boolean isSequenceSelected() {
        return sequenceSelected;
    }

    /**
     * Sets the sequence selected.
     *
     * @param sequenceSelected the new sequence selected
     */
    public void setSequenceSelected(boolean sequenceSelected) {
        this.sequenceSelected = sequenceSelected;
    }

    /**
     * Checks if is synonym selected.
     *
     * @return true, if is synonym selected
     */
    public boolean isSynonymSelected() {
        return synonymSelected;
    }

    public boolean isTriggerSelected() {
        return triggerSelected;
    }

    /**
     * Sets the synonym selected.
     *
     * @param synonymSelected the new synonym selected
     */
    public void setSynonymSelected(boolean synonymSelected) {
        this.synonymSelected = synonymSelected;
    }
    
    public void setTriggerSelected(boolean triggerSelected) {
        this.triggerSelected = triggerSelected;
    }

    private String getRegularExpression() {
        StringBuilder builder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (!isMatchCase()) {
            builder.append(",'i'");
        }
        builder.append(") ");
        return builder.toString();
    }

    /**
     * Form search query for fun proc.
     *
     * @param nsoid the nsoid
     * @param searchIndex the search index
     * @param privilegeFlag the privilege flag
     * @return the string
     */
    public String formSearchQueryForFunProc(long nsoid, SearchNameMatchEnum searchIndex, boolean privilegeFlag) {
        StringBuilder builder = new StringBuilder(FUNCTION_PROCEDURE_FAST_LOAD);
        if (searchIndex == SearchNameMatchEnum.REGULAR_EXPRESSION) {
            builder.append(", regexp_matches(objname, ?");
            builder.append(getRegularExpression());
        }
        builder.append(FUNCTION_PROCEDURE_FAST_LOAD_WHERE);
        builder.append(" and pr.pronamespace=");
        builder.append(nsoid);
        if (privilegeFlag) {
            builder.append(FUNCTION_PROCEDURE_FAST_LOAD_PRIVILEGE);
        }
        if (searchIndex != SearchNameMatchEnum.REGULAR_EXPRESSION) {
            builder.append(" and objname ");
            builder.append(getExpressionForNameMatch(searchIndex));
        }
        return builder.toString();
    }

    /**
     * Form search query for syn.
     *
     * @param nsoid the nsoid
     * @param searchIndex the search index
     * @return the string
     */
    public String formSearchQueryForSyn(long nsoid, SearchNameMatchEnum searchIndex, boolean privilegeFlag) {
        StringBuilder builder = new StringBuilder(SYNONYM_FAST_LOAD_PRIVILAGE);
        if (searchIndex == SearchNameMatchEnum.REGULAR_EXPRESSION) {
            builder.append(", regexp_matches(synname, ?");
            builder.append(getRegularExpression());
        }
        builder.append(SYNONYM_FROM_CLASS);
        builder.append(" where syn.synnamespace=");
        builder.append(nsoid);
        if (searchIndex != SearchNameMatchEnum.REGULAR_EXPRESSION) {
            builder.append(" and synname ");
            builder.append(getExpressionForNameMatch(searchIndex));
        }
        return builder.toString();

    }

    /**
     * Form search query for tablesn views.
     *
     * @param nsoid the nsoid
     * @param searchIndex the search index
     * @param privilegeFlag the privilege flag
     * @param nodeGroupPrivilegeFlag the node group privilege flag
     * @return the string
     */
    public String formSearchQueryForTablesnViews(long nsoid, SearchNameMatchEnum searchIndex, boolean privilegeFlag,
            boolean nodeGroupPrivilegeFlag) {
        boolean orFlag = false;
        StringBuilder builder = new StringBuilder(TABLES_VIEWS_FAST_LOAD);
        if (searchIndex == SearchNameMatchEnum.REGULAR_EXPRESSION) {
            builder.append(", regexp_matches(relname,?) ");
        }
        builder.append(TABLES_VIEWS_FAST_LOAD_WHERE);

        builder.append("where tbl.relnamespace =");
        builder.append(nsoid);
        builder.append(" and (");
        if (isTableSelected()) {
            orFlag = true;
            builder.append(REL_TYPE_TABLES);
            builder.append(
                    privilegeFlag ? (nodeGroupPrivilegeFlag ? NODEGROUP_PRIVILEGE : TABLE_PRIVILEGE) : NO_PRIVILEGE);
        }
        if (isViewsSelected()) {
            if (orFlag) {
                builder.append(" or");
            } else {
                orFlag = true;
            }
            builder.append(REL_TYPE_VIEWS);
            builder.append(privilegeFlag ? VIEWS_PRIVILEGE : NO_PRIVILEGE);
        }
        if (isSequenceSelected()) {
            if (orFlag) {
                builder.append(" or");
            } else {
                orFlag = true;
            }
            builder.append(REL_TYPE_SEQUENCE);
            builder.append(privilegeFlag ? SEQUENCE_PRIVILEGE : NO_PRIVILEGE);
        }
        builder.append(") and tbl.parttype in ('n', 'p', 'v') ");

        if (searchIndex != SearchNameMatchEnum.REGULAR_EXPRESSION) {
            builder.append(" and relname ");
            builder.append(getExpressionForNameMatch(searchIndex));
        }
        return builder.toString();
    }

    /**
     * Form search query for triggers.
     *
     * @param nsoid
     * @param searchIndex
     * @param privilegeFlag
     * @param nodeGroupPrivilegeFlag
     * @return
     */
	public String formSearchQueryForTriggers(long nsoid, SearchNameMatchEnum searchIndex, boolean privilegeFlag,
            boolean nodeGroupPrivilegeFlag) {
        StringBuilder builder = new StringBuilder(TRIGGER_FAST_LOAD_PRIVILAGE);
        if (searchIndex == SearchNameMatchEnum.REGULAR_EXPRESSION) {
            builder.append(", regexp_matches(tgname, ?) ");
        }
                
        builder.append(TRIGGER_FAST_LOAD_PRIVILAGE_WHERE);
        builder.append(nsoid);
        if (searchIndex != SearchNameMatchEnum.REGULAR_EXPRESSION) {
            builder.append(" and tgname ");
            builder.append(getExpressionForNameMatch(searchIndex));
        }
       
        return builder.toString();
    }

    private String getExpressionForNameMatch(SearchNameMatchEnum searchIndex) {
        StringBuilder builder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        if (isMatchCase()) {
            builder.append(" LIKE ");
        } else {
            builder.append(" ILIKE ");
        }
        builder.append("? ;");

        return builder.toString();
    }
}
