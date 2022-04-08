package org.opengauss.mppdbide.mock.bl;

import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.JDBCMockObjectFactory;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;

public class CommonLLTUtilsHelper
{

    public enum EXCEPTIONENUM
    {
        YES, NO, EXCEPTION
    }
    public static final String FETCH_ALL_NAMESPACE_LOAD_PRIV = "SELECT oid, nspname from pg_namespace where has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";
    public static final String FETCH_ALL_NAMESPACE_LOAD_ALL = "SELECT oid, nspname from pg_namespace ORDER BY nspname;";
    public static final String  GET_ALL_SHALLOWLOADTABLES = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, "
            + " tbl.relpersistence relpersistence from pg_class tbl where tbl.relkind = 'r' "
            + "and tbl.relnamespace not in (select oid from pg_namespace where nspname in ('cstore', 'pg_toast')) "
            + "order by relname";
    public static final String  Shallowload1              = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, tbl.relpersistence relpersistence,ts.spcname as reltablespace "
            + "from pg_class tbl left join pg_tablespace ts on ts.oid = tbl.reltablespace "
            + "where tbl.relkind = 'r' and tbl.relnamespace not in "
            + "(select oid from pg_namespace where nspname in ('cstore', 'pg_toast'))"
            + " order by relname";
    public static final String  SHALLOWLOADQUERY          = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, "
            + "pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, "
            + "pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr "
            + "JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  "
            + "WHERE lng.lanname='plpgsql'  ORDER BY objname";
    public static final String  TRIGGERONLY               = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname='plpgsql'  and UPPER(typ.typname)  = 'TRIGGER'  ORDER BY objname";
    public static final String  TRIGGERONLY1              = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname='plpgsql'  and UPPER(typ.typname) != 'TRIGGER' ";
    public static final String  FUNCTIONONLY              = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname='plpgsql'  and UPPER(typ.typname) != 'TRIGGER'  and pr.pronamespace= 1 ORDER BY objname";
    public static final String  SHALLOWLOADQUERYDEFAULT   = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname='plpgsql'  and pr.pronamespace= 1 ORDER BY objname";

    public static final String  GET_ALL_FUNCTION_QUERY    = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, "
            + "pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows"
            + " FROM pg_proc pr "
            + " JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace"
            + " JOIN pg_language lng ON lng.oid=prolang LEFT OUTER JOIN pg_description des ON des.objoid=pr.oid "
            + " WHERE lng.lanname='plpgsql' and UPPER(typ.typname) != 'TRIGGER' AND pr.pronamespace="
            + 1 + " ORDER BY objname;";

    public static final String  GET_ALL_TABLE_METADATA    = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes "
            + "from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) "
            + "left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r'";

    public static final String  GET_ALL_DATATYPE          = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, "
            + "typ.typlen as typlen, typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, "
            + "typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc "
            + "from pg_type typ left join pg_description des on (typ.oid = des.objoid) ";

    public static final String  GET_ALL_COLUMN_METADATA   = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
            + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, "
            + "c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
            + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
            + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
            + "where c.attisdropped = 'f' and c.attnum > 0 order by t.oid, c.attnum;";

    public static final String  GET_ALL_INDEXES           = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, "
            + "ci.relam  as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, "
            + "i.indimmediate  as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, "
            + "i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) "
            + "LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) WHERE t.relkind = 'r';";

    public static final String  GET_ALL_CONSTRAINTS       = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, "
            + "c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, "
            + "c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, "
            + "c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, "
            + "c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist "
            + "FROM pg_constraint c LEFT JOIN pg_class t on (t.oid = c.conrelid) WHERE t.relkind = 'r'";

    public static final String  GET_ALL_TRIGGER_QUERY     = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, "
            + "pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows"
            + " FROM pg_proc pr "
            + " JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace"
            + " JOIN pg_language lng ON lng.oid=prolang LEFT OUTER JOIN pg_description des ON des.objoid=pr.oid "
            + " WHERE lng.lanname='plpgsql' and UPPER(typ.typname) = 'TRIGGER' and pr.pronamespace="
            + 1 + " ORDER BY objname;";
    
  

    public static final String  GET_FUNCTION_WITH_HEADER  = "select a.headerlines, a.definition, b.xmin, b.cmin from pg_proc b left join "
            + "(select * from PG_GET_FUNCTIONDEF(";

    public static final String  GET_NAMESPACE_QUERY       = "SELECT nspname from pg_namespace where oid = ?";
    public static final String  SYNC_QUERY                = "SELECT FUNC, LINENUMBER,"
            + " TARGETNAME from pldbg_sync_target(?);";
    public static final String  GET_SOURCE_VERSION        = "SELECT * from pldbg_get_funcVer(?);";
    public static final String  REFRESH_OBJECT_QUERY      = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, "
            + "pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows"
            + " FROM pg_proc pr WHERE oid = ?";

    public static final String  GET_VARIABLES             = "SELECT NAME, LINENUMBER,"
            + " VARCLASS, ISCONST, DTYPE, ISVALUENULL, VALUE "
            + "FROM pldbg_get_variables(?);";

    public static final String  GET_VARIABLE              = "SELECT NAME, LINENUMBER,"
            + " VARCLASS, ISCONST, DTYPE, ISVALUENULL, VALUE "
            + "from pldbg_get_varinfo(?,?,?);";

    public static final String GET_ALL_NAMESPACE                         = "SELECT oid, nspname from pg_namespace where ((oid >= 16384 or nspname LIKE 'public') and nspname  NOT LIKE 'pg_%') and has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";
    public static final String GET_ALL_SYSTEM_NAMESPACE                  = "SELECT oid, nspname from pg_namespace where ((oid < 16384 and nspname NOT LIKE 'public') or nspname LIKE 'pg_%') and has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";

    public static final String  GET_NAMESPACE_BY_ID       = "SELECT oid, nspname from pg_namespace WHERE oid=?";

    public static final String  CREATE_EXTENSION          = "CREATE EXTENSION IF NOT EXISTS pldbgapi";

    public static final String  DETACH_SESSION            = "SELECT pldbg_detach_session(?);";

    public static final String  ATTACH_SESSION            = "SELECT pldbg_attach_session(?,?);";

    public static final String  GET_STACK                 = "SELECT pldbg_get_stack(?);";

    public static final String  STEP_INTO                 = "SELECT FUNC, LINENUMBER,"
            + " TARGETNAME from pldbg_step_into(?);";

    public static final String  STEP_OUT                  = "SELECT FUNC, LINENUMBER,"
            + " TARGETNAME from pldbg_step_out(?);";

    public static final String  STEP_OVER                 = "SELECT FUNC, LINENUMBER,"
            + " TARGETNAME from pldbg_step_over(?);";

    public static final String  CONTINUE                  = "SELECT FUNC, LINENUMBER,"
            + " TARGETNAME from pldbg_continue(?);";

    public static final String  DEPOSIT_VALUE             = "SELECT pldbg_deposit_value(?,?,?,?);";

    public static final String  DEBUG_ID                  = "SELECT pg_backend_pid();";

    public static final String  FUNCTION_VERSION          = "SELECT * from pldbg_get_funcVer(?);";

    public static final String  SERVER_SYNC               = "SELECT pldbg_server_in_sync(?);";

    private static final String BREAKPOINT_DELETE         = "SELECT pldbg_drop_breakpoint(?,?,?,?);";

    public static final String  GET_ALL_VIEWS             = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
            + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
            + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
            + "from pg_class v "
            + "left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') "
            + "left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) "
            + "left join pg_type typ on (c.atttypid = typ.oid) "
            + "where c.attisdropped = 'f' and c.attnum > 0 "
            + " and v.relnamespace = " + 1 + " order by v.oid, c.attnum";
    
    public static final String FETCH_SERVER_IP             = "select inet_server_addr();";

    public static void prepareConnectionResultSets(
            JDBCMockObjectFactory objectFactory)
    {
        MockConnection connection = objectFactory.getMockConnection();

        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();
        listnerResultSet.addRow(new Integer[] {12});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_create_listener();", listnerResultSet);

        MockResultSet initializeDebugResult = preparedstatementHandler
                .createResultSet();
        initializeDebugResult.addRow(new String[] {"1231"});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_initialize_debug();", initializeDebugResult);

        MockResultSet attachResult = preparedstatementHandler.createResultSet();
        attachResult.addRow(new Boolean[] {true});

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_attach_session(?,?);", attachResult);

        MockResultSet detachResult = preparedstatementHandler.createResultSet();
        detachResult.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_detach_session(?);", detachResult);

        MockResultSet isDebugResult = preparedstatementHandler
                .createResultSet();
        isDebugResult.addRow(new Boolean[] {true});

        preparedstatementHandler.prepareResultSet("SELECT pldbg_is_debug_on();",
                isDebugResult);

        MockResultSet debugOn = preparedstatementHandler.createResultSet();
        debugOn.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_on();",
                debugOn);

        MockResultSet debugOff = preparedstatementHandler.createResultSet();
        debugOff.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_off();",
                debugOff);

        MockResultSet tablespaceMetaData = preparedstatementHandler
                .createResultSet();
        tablespaceMetaData.addColumn("oid");
        tablespaceMetaData.addColumn("spcname");
        tablespaceMetaData.addColumn("spcoptions");
        tablespaceMetaData.addColumn("location");
        tablespaceMetaData.addColumn("spcmaxsize");
        tablespaceMetaData.addColumn("relative");
        tablespaceMetaData.addRow(new Object[] {1, "tblspc",
                new String[] {"spcoptions=options"}, "location", "spcmaxsize", false});
        preparedstatementHandler.prepareResultSet(
                "select oid, pg_tablespace_location(oid) as location, spcname,spcacl,spcoptions, spcmaxsize, relative from pg_tablespace  where oid ="
                        + 1,
                tablespaceMetaData);

        MockResultSet setDebugPositionRS = preparedstatementHandler
                .createResultSet();
        setDebugPositionRS.addRow(new String[] {"(6,5)"});
        preparedstatementHandler.prepareResultSet(
                "SELECT FUNC, LINENUMBER,"
                        + " TARGETNAME from pldbg_sync_target(?);",
                setDebugPositionRS);

        prepareProxyInfo(preparedstatementHandler);
        prepareValidateVersion(preparedstatementHandler);

        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addRow(new Object[] {3, "PUBLIC"});
      //  namespaceRS.addRow(new Object[] {1, "pg_catalog"});
       // namespaceRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_NAMESPACE, namespaceRS);
        MockResultSet namespaceSysRS = preparedstatementHandler.createResultSet();
        namespaceSysRS.addColumn("oid");
        namespaceSysRS.addColumn("nspname");
       // namespaceSysRS.addRow(new Object[] {1, "PUBLIC"});
        namespaceSysRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceSysRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_SYSTEM_NAMESPACE, namespaceSysRS);
        
        MockResultSet namespaceRS1 = preparedstatementHandler.createResultSet();
        namespaceRS1.addColumn("oid");
        namespaceRS1.addColumn("nspname");
        namespaceRS1.addRow(new Object[] {3, "PUBLIC"});
        namespaceRS1.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS1.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS1);
        
        MockResultSet namespaceRS11 = preparedstatementHandler.createResultSet();
        namespaceRS11.addColumn("oid");
        namespaceRS11.addColumn("nspname");
        namespaceRS11.addRow(new Object[] {3, "PUBLIC"});
        namespaceRS11.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS11.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_ALL, namespaceRS11);
        
        MockResultSet refreshnamespaceRS = preparedstatementHandler
                .createResultSet();
        refreshnamespaceRS.addColumn("oid");
        refreshnamespaceRS.addColumn("nspname");
        refreshnamespaceRS.addRow(new Object[] {1, "PUBLIC"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_NAMESPACE_BY_ID, refreshnamespaceRS);

    }

    public static void prepareConnectionResultSetsNoNSP(
            JDBCMockObjectFactory objectFactory)
    {
        MockConnection connection = objectFactory.getMockConnection();

        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();
        listnerResultSet.addRow(new Integer[] {12});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_create_listener();", listnerResultSet);

        MockResultSet initializeDebugResult = preparedstatementHandler
                .createResultSet();
        initializeDebugResult.addRow(new String[] {"1231"});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_initialize_debug();", initializeDebugResult);

        MockResultSet attachResult = preparedstatementHandler.createResultSet();
        attachResult.addRow(new Boolean[] {true});

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_attach_session(?,?);", attachResult);

        MockResultSet detachResult = preparedstatementHandler.createResultSet();
        detachResult.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_detach_session(?);", detachResult);

        MockResultSet isDebugResult = preparedstatementHandler
                .createResultSet();
        isDebugResult.addRow(new Boolean[] {true});

        preparedstatementHandler.prepareResultSet("SELECT pldbg_is_debug_on();",
                isDebugResult);

        MockResultSet debugOn = preparedstatementHandler.createResultSet();
        debugOn.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_on();",
                debugOn);

        MockResultSet debugOff = preparedstatementHandler.createResultSet();
        debugOff.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_off();",
                debugOff);

        MockResultSet setDebugPositionRS = preparedstatementHandler
                .createResultSet();
        setDebugPositionRS.addRow(new String[] {"(6,5)"});
        preparedstatementHandler.prepareResultSet(
                "SELECT FUNC, LINENUMBER,"
                        + " TARGETNAME from pldbg_sync_target(?);",
                setDebugPositionRS);

        prepareProxyInfo(preparedstatementHandler);
        prepareValidateVersion(preparedstatementHandler);

        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        // namespaceRS.addRow(new Object[]{1, "PUBLIC"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_NAMESPACE, namespaceRS);
        
        MockResultSet namespaceRS1 = preparedstatementHandler.createResultSet();
        namespaceRS1.addColumn("oid");
        namespaceRS1.addColumn("nspname");
        namespaceRS1.addRow(new Object[] {3, "PUBLIC"});
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS1);
        
        MockResultSet namespaceRS11 = preparedstatementHandler.createResultSet();
        namespaceRS11.addColumn("oid");
        namespaceRS11.addColumn("nspname");
        namespaceRS11.addRow(new Object[] {3, "PUBLIC"});
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_ALL, namespaceRS11);

        MockResultSet refreshnamespaceRS = preparedstatementHandler
                .createResultSet();
        refreshnamespaceRS.addColumn("oid");
        refreshnamespaceRS.addColumn("nspname");
        // refreshnamespaceRS.addRow(new Object[]{1, "PUBLIC"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_NAMESPACE_BY_ID, refreshnamespaceRS);

    }

    public static void prepareFunctionResultSet(MockConnection connection)
    {
        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet listnerResultSet = connection
                .getPreparedStatementResultSetHandler().createResultSet();

        listnerResultSet.addColumn("oid");
        listnerResultSet.addColumn("objname");
        listnerResultSet.addColumn("namespace");
        listnerResultSet.addColumn("ret");
        listnerResultSet.addColumn("alltype");
        listnerResultSet.addColumn("nargs");
        listnerResultSet.addColumn("argtype");
        listnerResultSet.addColumn("argname");
        listnerResultSet.addColumn("argmod");
        listnerResultSet.addColumn("secdef");
        listnerResultSet.addColumn("vola");
        listnerResultSet.addColumn("isstrict");
        listnerResultSet.addColumn("retset");
        listnerResultSet.addColumn("procost");
        listnerResultSet.addColumn("setrows");

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(16), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(GET_ALL_FUNCTION_QUERY,
                listnerResultSet);

        MockResultSet nameSpaceResultSet = preparedstatementHandler
                .createResultSet();
        nameSpaceResultSet.addColumn("nspname");
        nameSpaceResultSet.addRow(new Object[] {"PUBLIC"});
        preparedstatementHandler.prepareResultSet(GET_NAMESPACE_QUERY,
                nameSpaceResultSet);
    }

    public static void prepareFunctionResultSetNoNSP(MockConnection connection)
    {
        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet listnerResultSet = connection
                .getPreparedStatementResultSetHandler().createResultSet();

        listnerResultSet.addColumn("oid");
        listnerResultSet.addColumn("objname");
        listnerResultSet.addColumn("namespace");
        listnerResultSet.addColumn("ret");
        listnerResultSet.addColumn("alltype");
        listnerResultSet.addColumn("nargs");
        listnerResultSet.addColumn("argtype");
        listnerResultSet.addColumn("argname");
        listnerResultSet.addColumn("argmod");
        listnerResultSet.addColumn("secdef");
        listnerResultSet.addColumn("vola");
        listnerResultSet.addColumn("isstrict");
        listnerResultSet.addColumn("retset");
        listnerResultSet.addColumn("procost");
        listnerResultSet.addColumn("setrows");

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(16), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(GET_ALL_FUNCTION_QUERY,
                listnerResultSet);

        MockResultSet nameSpaceResultSet = preparedstatementHandler
                .createResultSet();
        nameSpaceResultSet.addColumn("nspname");
        // nameSpaceResultSet.addRow(new Object[] {"PUBLIC"});
        preparedstatementHandler.prepareResultSet(GET_NAMESPACE_QUERY,
                nameSpaceResultSet);
    }

    public static void prepareFunctionWithParamResultSet(
            MockConnection connection)
    {
        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet listnerResultSet = connection
                .getPreparedStatementResultSetHandler().createResultSet();

        listnerResultSet.addColumn("oid");
        listnerResultSet.addColumn("objname");
        listnerResultSet.addColumn("namespace");
        listnerResultSet.addColumn("ret");
        listnerResultSet.addColumn("alltype");
        listnerResultSet.addColumn("nargs");
        listnerResultSet.addColumn("argtype");
        listnerResultSet.addColumn("argname");
        listnerResultSet.addColumn("argmod");
        listnerResultSet.addColumn("secdef");
        listnerResultSet.addColumn("vola");
        listnerResultSet.addColumn("isstrict");
        listnerResultSet.addColumn("retset");
        listnerResultSet.addColumn("procost");
        listnerResultSet.addColumn("setrows");

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(1), "{23,23,23}", null, "23", "{arg1,arg2, arg3}",
                "{i,o,b}", false, "volatile", false, false, 0, 0});

        preparedstatementHandler.prepareResultSet(GET_ALL_FUNCTION_QUERY,
                listnerResultSet);

        MockResultSet nameSpaceResultSet = preparedstatementHandler
                .createResultSet();
        nameSpaceResultSet.addColumn("nspname");
        nameSpaceResultSet.addRow(new Object[] {"PUBLIC"});
        preparedstatementHandler.prepareResultSet(GET_NAMESPACE_QUERY,
                nameSpaceResultSet);
    }

    public static void prepareFailureFunctionResultSet(
            MockConnection connection)
    {
        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet listnerResultSet = connection
                .getPreparedStatementResultSetHandler().createResultSet();

        listnerResultSet.addColumn("oid");
        listnerResultSet.addColumn("objname");
        listnerResultSet.addColumn("namespace");
        listnerResultSet.addColumn("ret");
        listnerResultSet.addColumn("alltype");
        listnerResultSet.addColumn("nargs");
        listnerResultSet.addColumn("argtype");
        listnerResultSet.addColumn("argname");
        listnerResultSet.addColumn("argmod");
        listnerResultSet.addColumn("secdef");
        listnerResultSet.addColumn("vola");
        listnerResultSet.addColumn("isstrict");
        listnerResultSet.addColumn("retset");
        listnerResultSet.addColumn("procost1");
        listnerResultSet.addColumn("setrows");

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(16), null, null, null, null, "mode", false,
                "volatile", false, false, 10});

        preparedstatementHandler.prepareResultSet(GET_ALL_FUNCTION_QUERY,
                listnerResultSet);

        MockResultSet nameSpaceResultSet = preparedstatementHandler
                .createResultSet();
        nameSpaceResultSet.addColumn("nspname");
        nameSpaceResultSet.addRow(new Object[] {"public"});
        preparedstatementHandler.prepareResultSet(GET_NAMESPACE_QUERY,
                nameSpaceResultSet);
    }

    public static void prepareVersionCheckInitial(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet sourceVersionResultSet = preparedstatementHandler
                .createResultSet();
        sourceVersionResultSet.addColumn("xmin");
        sourceVersionResultSet.addColumn("cmin");
        sourceVersionResultSet.addRow(new Object[] {1, 1});
        preparedstatementHandler.prepareResultSet(GET_SOURCE_VERSION,
                sourceVersionResultSet);
    }

    public static void prepareVersionCheckModified(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet sourceVersionResultSet = preparedstatementHandler
                .createResultSet();
        sourceVersionResultSet.addColumn("xmin");
        sourceVersionResultSet.addColumn("cmin");
        sourceVersionResultSet.addRow(new Object[] {1, 1});
        preparedstatementHandler.prepareResultSet(GET_SOURCE_VERSION,
                sourceVersionResultSet);
    }

    public static void prepareTriggerResultSet(MockConnection connection)
    {
        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet listnerResultSet = connection
                .getPreparedStatementResultSetHandler().createResultSet();

        listnerResultSet.addColumn("oid");
        listnerResultSet.addColumn("objname");
        listnerResultSet.addColumn("namespace");
        listnerResultSet.addColumn("ret");
        listnerResultSet.addColumn("alltype");
        listnerResultSet.addColumn("nargs");
        listnerResultSet.addColumn("argtype");
        listnerResultSet.addColumn("argname");
        listnerResultSet.addColumn("argmod");
        listnerResultSet.addColumn("secdef");
        listnerResultSet.addColumn("vola");
        listnerResultSet.addColumn("isstrict");
        listnerResultSet.addColumn("retset");
        listnerResultSet.addColumn("procost");
        listnerResultSet.addColumn("setrows");

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(1), "trigger1", 1,
                new Integer(2279), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(GET_ALL_TRIGGER_QUERY,
                listnerResultSet);
        MockResultSet nameSpaceResultSet = preparedstatementHandler
                .createResultSet();
        nameSpaceResultSet.addColumn("nspname");
        nameSpaceResultSet.addRow(new Object[] {"PUBLIC"});
        preparedstatementHandler.prepareResultSet(GET_NAMESPACE_QUERY,
                nameSpaceResultSet);

    }

    public static void prepareTriggerResultSetNoNSP(MockConnection connection)
    {
        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet listnerResultSet = connection
                .getPreparedStatementResultSetHandler().createResultSet();

        listnerResultSet.addColumn("oid");
        listnerResultSet.addColumn("objname");
        listnerResultSet.addColumn("namespace");
        listnerResultSet.addColumn("ret");
        listnerResultSet.addColumn("alltype");
        listnerResultSet.addColumn("nargs");
        listnerResultSet.addColumn("argtype");
        listnerResultSet.addColumn("argname");
        listnerResultSet.addColumn("argmod");
        listnerResultSet.addColumn("secdef");
        listnerResultSet.addColumn("vola");
        listnerResultSet.addColumn("isstrict");
        listnerResultSet.addColumn("retset");
        listnerResultSet.addColumn("procost");
        listnerResultSet.addColumn("setrows");

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(1), "trigger1", 1,
                new Integer(2279), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(GET_ALL_TRIGGER_QUERY,
                listnerResultSet);
        MockResultSet nameSpaceResultSet = preparedstatementHandler
                .createResultSet();
        nameSpaceResultSet.addColumn("nspname");
        // nameSpaceResultSet.addRow(new Object[] {"PUBLIC"});
        preparedstatementHandler.prepareResultSet(GET_NAMESPACE_QUERY,
                nameSpaceResultSet);

    }

    public static void prepareSourceCodeResultSet(MockConnection connection)
    {
        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet sourceCodeResult = preparedstatementHandler
                .createResultSet();
        sourceCodeResult.addRow(
                new String[] {"Declare\na int:=0;\nbegin\na:=a+1;\nend;"});
        preparedstatementHandler.prepareResultSet(
                "SELECT prosrc FROM pg_proc WHERE oid = ?;", sourceCodeResult);

    }

    public static void prepareRefreshObjectResult(MockConnection connection)
    {
        PreparedStatementResultSetHandler preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        MockResultSet refreshObjectResult = preparedstatementHandler
                .createResultSet();

        refreshObjectResult.addColumn("oid");
        refreshObjectResult.addColumn("objname");
        refreshObjectResult.addColumn("namespace");
        refreshObjectResult.addColumn("ret");
        refreshObjectResult.addColumn("alltype");
        refreshObjectResult.addColumn("nargs");
        refreshObjectResult.addColumn("argtype");
        refreshObjectResult.addColumn("argname");
        refreshObjectResult.addColumn("argmod");
        refreshObjectResult.addColumn("secdef");
        refreshObjectResult.addColumn("vola");
        refreshObjectResult.addColumn("isstrict");
        refreshObjectResult.addColumn("retset");
        refreshObjectResult.addColumn("procost");
        refreshObjectResult.addColumn("setrows");

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        refreshObjectResult.addRow(new Object[] {new Integer(1), "trigger1", 1,
                new Integer(2279), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(REFRESH_OBJECT_QUERY,
                refreshObjectResult);
    }

    public static void createBreakpoint(String isDisabledStr,
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet addBPResult = preparedstatementHandler.createResultSet();
        addBPResult.addRow(new String[] {"(2,5," + isDisabledStr + ":2:o)"});
        preparedstatementHandler.prepareResultSet(
                "SELECT FUNC, LINENUMBER, TARGETNAME from pldbg_set_breakpoint(?,?,?,?,?);",
                addBPResult);
    }

    public static void getServerBreakPoints(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet breakPointsFromServer = preparedstatementHandler
                .createResultSet();
        breakPointsFromServer.addRow(new String[] {"(6,5,f)"});
        // breakPointsFromServer.addRow(new String[] { "(7,6,f)" });
        preparedstatementHandler.prepareResultSet(
                "SELECT FUNC, LINENUMBER,"
                        + " TARGETNAME from pldbg_get_breakpoints(?,?);",
                breakPointsFromServer);
    }

    public static void debugPositionRelated(
            PreparedStatementResultSetHandler preparedstatementHandler,
            StatementResultSetHandler statementHandler)
    {
        MockResultSet debugOnRS = preparedstatementHandler.createResultSet();
        debugOnRS.addRow(new Boolean[] {true});
        preparedstatementHandler.prepareResultSet("SELECT pldbg_debug_on();",
                debugOnRS);

        MockResultSet setDebugPositionRS = preparedstatementHandler
                .createResultSet();
        setDebugPositionRS.addRow(new String[] {"(2,5)"});
        preparedstatementHandler.prepareResultSet(
                "SELECT FUNC, LINENUMBER,"
                        + " TARGETNAME from pldbg_sync_target(?);",
                setDebugPositionRS);

        MockResultSet continueExecutionRS = preparedstatementHandler
                .createResultSet();
        continueExecutionRS.addRow(new String[] {"(0,-1)"});
        preparedstatementHandler.prepareResultSet(
                "SELECT FUNC, LINENUMBER,"
                        + " TARGETNAME from pldbg_continue(?);",
                continueExecutionRS);

        /*
         * MockResultSet stepInExecutionRS = preparedstatementHandler
         * .createResultSet(); stepInExecutionRS.addRow(new String[] { "(6,7)"
         * }); stepInExecutionRS.addRow(new String[] { "(6,8)" });
         * preparedstatementHandler .prepareResultSet("SELECT FUNC, LINENUMBER,"
         * + " TARGETNAME from pldbg_step_into(?);", stepInExecutionRS);
         */

        MockResultSet getSourceResult = preparedstatementHandler
                .createResultSet();
        getSourceResult.addRow(new String[] {"\nDeclare\nc INT = 6;\nd INT;"
                + "BEGIN\nc := c+1;\nc := perform nestedfunc()\nc := c+1;"
                + "\nc := c+1;\nc := c+1;\nc := c+1;\nc := 100;\nd := c + 200;"
                + "\nreturn d;\nend;"});

        preparedstatementHandler.prepareResultSet(
                "SELECT prosrc FROM pg_proc " + "WHERE oid = ?;",
                getSourceResult);

        MockResultSet getFrameResult = preparedstatementHandler
                .createResultSet();
        getFrameResult.addRow(new String[] {"(0,func,2,5)"});

        preparedstatementHandler.prepareResultSet("SELECT pldbg_get_stack(?);",
                getFrameResult);

        MockResultSet getFrameVarResult = preparedstatementHandler
                .createResultSet();

        getFrameVarResult.addColumn("name");
        getFrameVarResult.addColumn("varClass");
        getFrameVarResult.addColumn("lineNumber");
        getFrameVarResult.addColumn("isUnique");
        getFrameVarResult.addColumn("isConst");
        getFrameVarResult.addColumn("isNotNull");
        getFrameVarResult.addColumn("isValueNull");
        getFrameVarResult.addColumn("dtype");
        getFrameVarResult.addColumn("value");

        /*
         * getFrameVarResult.addRow(new String[] { "(c,L,3,t,f,f,f,23,6)" });
         * getFrameVarResult.addRow(new String[] { "(d,L,4,t,f,f,t,23,NULL)" });
         */
        getFrameVarResult.addRow(
                new String[] {"c", "L", "3", "t", "f", "f", "f", "23", "6"});
        getFrameVarResult.addRow(
                new String[] {"d", "L", "4", "t", "f", "f", "t", "23", "NULL"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.GET_VARIABLES,
                getFrameVarResult);

        MockResultSet getSelectFrameResult = preparedstatementHandler
                .createResultSet();
        getSelectFrameResult.addRow(new String[] {"(2,5,func)"});

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_select_frame(?,?);", getSelectFrameResult);
    }

    public static void prepareValidateVersion(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet validateVersionResult = preparedstatementHandler
                .createResultSet();
        validateVersionResult.addRow(new Boolean[] {true});

        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_validate_version(?);", validateVersionResult);
    }

    public static void prepareProxyInfo(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {

        CommonLLTUtils.mockCheckDebugSupport(preparedstatementHandler);
        CommonLLTUtils.mockCheckExplainPlanSupport(preparedstatementHandler);
        MockResultSet getDeadlineInfo = preparedstatementHandler
                .createResultSet();
        getDeadlineInfo.addColumn("DEADLINE");

        getDeadlineInfo.addRow(new Object[] {"3.124632"});
        preparedstatementHandler.prepareResultSet(
                "select intervaltonum(gs_password_deadline()) as DEADLINE;",
                getDeadlineInfo);

        MockResultSet getNamespaceInfo = preparedstatementHandler
                .createResultSet();
        getNamespaceInfo.addColumn("oid");
        getNamespaceInfo.addColumn("nspname");

        getNamespaceInfo.addRow(new Object[] {new Integer(1), "PUBLIC"});
        preparedstatementHandler.prepareResultSet(
                "SELECT oid, nspname from pg_namespace WHERE oid=1",
                getNamespaceInfo);

        MockResultSet viewRS = preparedstatementHandler.createResultSet();
        viewRS.addColumn("viewid");
        viewRS.addColumn("namespaceid");
        viewRS.addColumn("columnidx");
        viewRS.addColumn("name");
        viewRS.addColumn("datatypeoid");
        viewRS.addColumn("dtns");
        viewRS.addColumn("length");
        viewRS.addColumn("precision");
        viewRS.addColumn("dimentions");
        viewRS.addColumn("notnull");
        viewRS.addColumn("isdefaultvalueavailable");
        viewRS.addColumn("default_value");
        viewRS.addRow(
                new Object[] {2, 1, 1, "col1", 3, 1, 0, 0, 0, 'f', 'f', ""});
        viewRS.addRow(
                new Object[] {2, 1, 1, "col1", 8, 1, 0, 0, 0, 'f', 'f', ""});
        viewRS.addRow(
                new Object[] {2, 1, 1, "col1", 5, 1, 0, 0, 0, 'f', 'f', ""});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.GET_ALL_VIEWS,
                viewRS);
        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("tableid");
        colmetadataRS.addColumn("namespaceid");
        colmetadataRS.addColumn("columnidx");
        colmetadataRS.addColumn("name");
        colmetadataRS.addColumn("datatypeoid");
        colmetadataRS.addColumn("dtns");
        colmetadataRS.addColumn("length");
        colmetadataRS.addColumn("precision");
        colmetadataRS.addColumn("dimentions");
        colmetadataRS.addColumn("notnull");
        colmetadataRS.addColumn("isdefaultvalueavailable");
        colmetadataRS.addColumn("default_value");

        String colmetadata = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
                + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = "
                + 1 + " order by t.oid, c.attnum;";

        colmetadataRS.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 0, -1, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(colmetadata, colmetadataRS);

        MockResultSet constraintRS = preparedstatementHandler.createResultSet();
        constraintRS.addColumn("constraintid");
        constraintRS.addColumn("tableid");
        constraintRS.addColumn("namespaceid");
        constraintRS.addColumn("constraintname");
        constraintRS.addColumn("constrainttype");
        constraintRS.addColumn("deferrable");
        constraintRS.addColumn("deferred");
        constraintRS.addColumn("validate");
        constraintRS.addColumn("indexid");
        constraintRS.addColumn("fkeytableId");
        constraintRS.addColumn("updatetype");
        constraintRS.addColumn("deletetype");
        constraintRS.addColumn("matchtype");
        constraintRS.addColumn("expr");
        constraintRS.addColumn("columnlist");
        constraintRS.addColumn("fkeycolumnlist");
        constraintRS.addColumn("const_def");

        constraintRS.addRow(new Object[] {1, 1, 1, "ConstraintName",
                "ConstraintType", false, false, false, 1, 1, "", "", "", "",
                "1", "1", ""});
        // preparedstatementHandler.prepareResultSet("SELECT c.oid as
        // constraintid, c.conrelid as tableid, c.connamespace as namespaceid,
        // c.conname as constraintname, c.contype as constrainttype,
        // c.condeferrable as deferrable, c.condeferred as deferred,
        // c.convalidated as validate, c.conindid as indexid, c.confrelid as
        // fkeytableId, c.confupdtype as updatetype, c.confdeltype as
        // deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey
        // as columnlist, c.confkey as fkeycolumnlist,
        // pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c where
        // c.conrelid = 1;",constraintRS);
        // SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace
        // as namespaceid, c.conname as constraintname, c.contype as
        // constrainttype, c.condeferrable as deferrable, c.condeferred as
        // deferred, c.convalidated as validate, c.conindid as indexid,
        // c.confrelid as fkeytableId, c.confupdtype as updatetype,
        // c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc
        // as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist,
        // pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c where
        // c.conrelid = 24900;
        preparedstatementHandler
                .prepareResultSet(
                        "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, "
                                + "c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, "
                                + "c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, "
                                + "c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, "
                                + "c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, "
                                + "pg_get_constraintdef(c.oid) as const_def "
                                + "FROM pg_constraint c where  c.connamespace="
                                + 1 + " and c.conrelid <> 0" + ';',
                        constraintRS);

        MockResultSet getVersionResult = preparedstatementHandler
                .createResultSet();
        getVersionResult.addColumn("proxyAPIVer");
        getVersionResult.addColumn("serverVersionStr");

        getVersionResult.addRow(new Object[] {
                "PostgreSQL 9.2beta2 on i686-pc-linux-gnu, compiled by gcc (SUSE Linux) 4.3.4 [gcc-4_3-branch revision 152973], 32-bit",
                "GMDB Tools V2R5C00B201"});
        preparedstatementHandler.prepareResultSet(
                "SELECT * from pldbg_get_proxy_info();", getVersionResult);

        MockResultSet getServerVersionResult = preparedstatementHandler
                .createResultSet();
        getServerVersionResult.addColumn("VERSION");

        getServerVersionResult.addRow(new Object[] {
                "GaussDBV100R003C00B530 (2013-01-28 16:14:03) on x86_64-unknown-linux-gnu, compiled by gcc (SUSE Linux) 4.3.4 [gcc-4_3-branch revision 152973], 64-bit"});
        preparedstatementHandler.prepareResultSet("SELECT * from version();",
                getServerVersionResult);

        MockResultSet getServerVersionResultSet = preparedstatementHandler
                .createResultSet();
        getServerVersionResultSet.addColumn("VERSION");

        getServerVersionResultSet.addRow(new Object[] {
                "Gauss200 OLAP V100R005C10 build 7123 compiled at 2016-11-11 16:18:35 on x86_64-unknown-linux-gnu, compiled by g++ (SUSE Linux) 4.3.4 [gcc-4_3-branch revision 152973], 64-bit"});
        preparedstatementHandler.prepareResultSet("SELECT * from version();",
                getServerVersionResultSet);
        MockResultSet getdboidrs = preparedstatementHandler.createResultSet();
        getdboidrs.addColumn("oid");

        getdboidrs.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(
                "SELECT oid from pg_database where datname = 'Gauss'",
                getdboidrs);
        // newly added

        MockResultSet getSourceCode = preparedstatementHandler
                .createResultSet();
        String query = "select a.headerlines, a.definition, b.xmin, b.cmin from pg_proc b left join "
                + "(select * from PG_GET_FUNCTIONDEF(" + 1
                + ")) a on (1) where b.oid=" + 1;

        getSourceCode.addColumn("Code");
        getSourceCode.addColumn("VersionNumber1");
        getSourceCode.addColumn("VersionNumber2");
        getSourceCode.addRow(new Object[] {4,
                "\"Declare\na int:=0;\nbegin\na:=a+1;\nend;\")", 1, 1});

        preparedstatementHandler.prepareResultSet(query, getSourceCode);

        MockResultSet getListner = preparedstatementHandler.createResultSet();
        getListner.addColumn("pldbg_create_listener");

        getListner.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(
                "SELECT pldbg_create_listener();", getListner);

        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("datname");

        getdbsrs.addRow(new Object[] {1, "Gauss"});
        preparedstatementHandler.prepareResultSet(
                "select oid, datname from pg_database where datistemplate='f'",
                getdbsrs);

        MockResultSet getnoders = preparedstatementHandler.createResultSet();
        getnoders.addColumn("oid");
        getnoders.addColumn("node_name");
        getnoders.addColumn("node_type");
        getnoders.addColumn("node_port");
        getnoders.addColumn("node_host");
        getnoders.addColumn("nodeis_primary");
        getnoders.addColumn("nodeis_preferred");
        getnoders.addColumn("node_id");

        getnoders.addRow(new Object[] {1, "Node_1", "node_type", 23456,
                "1.0.0.0", true, true, 12});
        preparedstatementHandler.prepareResultSet(
                "select oid, node_name, node_type, node_port, node_host, nodeis_primary, nodeis_preferred, node_id from pgxc_node;",
                getnoders);

        MockResultSet getnodegrprs = preparedstatementHandler.createResultSet();
        getnodegrprs.addColumn("oid");
        getnodegrprs.addColumn("group_name");
        getnodegrprs.addColumn("group_members");

        getnodegrprs.addRow(new Object[] {1, "group_1", "23 25"});
        preparedstatementHandler.prepareResultSet(
                "select oid, group_name, group_members from pgxc_group order by group_name;",
                getnodegrprs);

        MockResultSet gettblsprs = preparedstatementHandler.createResultSet();
        gettblsprs.addColumn("oid");
        gettblsprs.addColumn("location");
        gettblsprs.addColumn("spcname");
        gettblsprs.addColumn("spcacl");
        gettblsprs.addColumn("spcoptions");
        gettblsprs.addColumn("spcmaxsize");
        gettblsprs.addColumn("relative");
        gettblsprs.addRow(
                new Object[] {1, "/home/dsdev/shalini", "tablespace1", null,
                        new String[] {"filesystem=general",
                                "random_page_cost=2", "seq_page_cost=2",
                                "address =address"},
                        "10K", false});
        preparedstatementHandler.prepareResultSet(
                "select oid, pg_tablespace_location(oid) as location ,spcname,spcacl,spcoptions, spcmaxsize, relative from pg_tablespace where has_tablespace_privilege(spcname, 'CREATE') order by spcname;",
                gettblsprs);
        
        MockResultSet gettblsprs_all = preparedstatementHandler.createResultSet();
        gettblsprs_all.addColumn("oid");
        gettblsprs_all.addColumn("location");
        gettblsprs_all.addColumn("spcname");
        gettblsprs_all.addColumn("spcacl");
        gettblsprs_all.addColumn("spcoptions");
        gettblsprs_all.addColumn("spcmaxsize");
        gettblsprs_all.addColumn("relative");
        gettblsprs_all.addRow(
                new Object[] {1, "/home/dsdev/shalini", "tablespace1", null,
                        new String[] {"filesystem=general",
                                "random_page_cost=2", "seq_page_cost=2",
                                "address =address"},
                        "10K", false});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.FETCH_ALL_TABLESPACE_ALL,
                gettblsprs_all);

        MockResultSet getaccessmethodrs = preparedstatementHandler
                .createResultSet();
        getaccessmethodrs.addColumn("oid");
        getaccessmethodrs.addColumn("amname");

        getaccessmethodrs.addRow(new Object[] {1, "accemethod1"});
        preparedstatementHandler.prepareResultSet(
                "SELECT oid, amname from pg_am order by oid;",
                getaccessmethodrs);

        MockResultSet server_encoding = preparedstatementHandler
                .createResultSet();
        server_encoding.addRow(new Object[] {"UTF-8"});
        preparedstatementHandler.prepareResultSet("show server_encoding",
                server_encoding);

        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addRow(new Object[] {3, "PUBLIC"});
       // namespaceRS.addRow(new Object[] {1, "pg_catalog"});
       // namespaceRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_NAMESPACE, namespaceRS);

        MockResultSet namespaceSysRS = preparedstatementHandler.createResultSet();
        namespaceSysRS.addColumn("oid");
        namespaceSysRS.addColumn("nspname");
       // namespaceSysRS.addRow(new Object[] {1, "PUBLIC"});
        namespaceSysRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceSysRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_SYSTEM_NAMESPACE, namespaceSysRS);
        
        MockResultSet namespaceRS1 = preparedstatementHandler.createResultSet();
        namespaceRS1.addColumn("oid");
        namespaceRS1.addColumn("nspname");
        namespaceRS1.addRow(new Object[] {1, "Public"});
     //   namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS1);
        
        MockResultSet namespaceRS11 = preparedstatementHandler.createResultSet();
        namespaceRS11.addColumn("oid");
        namespaceRS11.addColumn("nspname");
        namespaceRS11.addRow(new Object[] {1, "Public"});
      //  namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.FETCH_ALL_NAMESPACE_LOAD_ALL, namespaceRS11);
        
        MockResultSet datatypeRS = preparedstatementHandler.createResultSet();
        datatypeRS.addColumn("oid");
        datatypeRS.addColumn("typname");
        datatypeRS.addColumn("typnamespace");
        datatypeRS.addColumn("typlen");
        datatypeRS.addColumn("typbyval");
        datatypeRS.addColumn("typtype");
        datatypeRS.addColumn("typcategory");
        datatypeRS.addColumn("typtypmod");
        datatypeRS.addColumn("typnotnull");
        datatypeRS.addColumn("typarray");
        datatypeRS.addColumn("desc");

        datatypeRS.addRow(new Object[] {1, "bigint", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {2, "int8", 1, 1, true, "type",
                "category", 1, true, 12, "description2"});

        datatypeRS.addRow(new Object[] {3, "bit", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {4, "varbit", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {5, "bool", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {6, "box", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {7, "bytea", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {8, "varchar", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {9, "char", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {10, "cidr", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {11, "circle", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {12, "date", 1, 1, true, "type",
                "category", 1, true, 12, "description"});

        datatypeRS.addRow(new Object[] {13, "float8", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {14, "inet", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {15, "int4", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {16, "interval", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {17, "line", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {18, "lseg", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {19, "macaddr", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {20, "money", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {21, "numeric", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {22, "numeric", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {23, "path", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {24, "point", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {25, "polygon", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {26, "float4", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {27, "int2", 1, 1, true, "type",
                "category", 1, true, 12, "description"});

        datatypeRS.addRow(new Object[] {28, "text", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {29, "time", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {30, "timetz", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {31, "timestamp", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {32, "timestamptz", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {33, "tsquery", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {34, "tsvector", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {35, "txid_snapshot", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {36, "uuid", 1, 1, true, "type",
                "category", 1, true, 12, "description"});
        datatypeRS.addRow(new Object[] {37, "xml", 1, 1, true, "type",
                "category", 1, true, 12, "description"});

        preparedstatementHandler
                .prepareResultSet(CommonLLTUtilsHelper.GET_ALL_DATATYPE, datatypeRS);

        MockResultSet tablemetadataRS = preparedstatementHandler
                .createResultSet();
        tablemetadataRS.addColumn("oid");
        tablemetadataRS.addColumn("relname");
        tablemetadataRS.addColumn("relnamespace");
        tablemetadataRS.addColumn("reltablespace");
        tablemetadataRS.addColumn("relpersistence");
        tablemetadataRS.addColumn("desc");
        tablemetadataRS.addColumn("nodes");

        tablemetadataRS.addRow(
                new Object[] {1, "MyTable", 1, 1, true, "description", "1 2"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_TABLE_METADATA, tablemetadataRS);

        MockResultSet colmetadataRS1 = preparedstatementHandler
                .createResultSet();
        colmetadataRS1.addColumn("tableid");
        colmetadataRS1.addColumn("namespaceid");
        colmetadataRS1.addColumn("columnidx");
        colmetadataRS1.addColumn("name");
        colmetadataRS1.addColumn("datatypeoid");
        colmetadataRS1.addColumn("dtns");
        colmetadataRS1.addColumn("length");
        colmetadataRS1.addColumn("precision");
        colmetadataRS1.addColumn("dimentions");
        colmetadataRS1.addColumn("notnull");
        colmetadataRS1.addColumn("isdefaultvalueavailable");
        colmetadataRS1.addColumn("default_value");

        colmetadataRS1.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_COLUMN_METADATA, colmetadataRS1);

        MockResultSet indexRS = preparedstatementHandler.createResultSet();
        indexRS.addColumn("oid");
        indexRS.addColumn("tableId");
        indexRS.addColumn("indexname");
        indexRS.addColumn("namespaceid");
        indexRS.addColumn("accessmethodid");
        indexRS.addColumn("isunique");
        indexRS.addColumn("isprimary");
        indexRS.addColumn("isexclusion");
        indexRS.addColumn("isimmediate");
        indexRS.addColumn("isclustered");
        indexRS.addColumn("checkmin");
        indexRS.addColumn("isready");
        indexRS.addColumn("cols");
        indexRS.addColumn("reloptions");

        indexRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", ""});
        preparedstatementHandler
                .prepareResultSet(CommonLLTUtilsHelper.GET_ALL_INDEXES, indexRS);

        MockResultSet constraintRS1 = preparedstatementHandler
                .createResultSet();
        constraintRS1.addColumn("constraintid");
        constraintRS1.addColumn("tableid");
        constraintRS1.addColumn("namespaceid");
        constraintRS1.addColumn("constraintname");
        constraintRS1.addColumn("constrainttype");
        constraintRS1.addColumn("deferrable");
        constraintRS1.addColumn("deferred");
        constraintRS1.addColumn("validate");
        constraintRS1.addColumn("indexid");
        constraintRS1.addColumn("fkeytableId");
        constraintRS1.addColumn("updatetype");
        constraintRS1.addColumn("deletetype");
        constraintRS1.addColumn("matchtype");
        constraintRS1.addColumn("expr");
        constraintRS1.addColumn("columnlist");
        constraintRS1.addColumn("fkeycolumnlist");

        constraintRS1.addRow(
                new Object[] {1, 1, 1, "ConstraintName", "ConstraintType",
                        false, false, false, 1, 1, "", "", "", "", "1", "1"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_CONSTRAINTS, constraintRS1);

        MockResultSet indexRS1 = preparedstatementHandler.createResultSet();
        indexRS1.addColumn("oid");
        indexRS1.addColumn("tableId");
        indexRS1.addColumn("indexname");
        indexRS1.addColumn("namespaceid");
        indexRS1.addColumn("accessmethodid");
        indexRS1.addColumn("isunique");
        indexRS1.addColumn("isprimary");
        indexRS1.addColumn("isexclusion");
        indexRS1.addColumn("isimmediate");
        indexRS1.addColumn("isclustered");
        indexRS1.addColumn("checkmin");
        indexRS1.addColumn("isready");
        indexRS1.addColumn("cols");
        indexRS1.addColumn("reloptions");
        indexRS1.addColumn("indexdef");
        indexRS1.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", ""});
        String GET_ALL_INDEXES_BYNAMESPACE_1 = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, "
                + "ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion,"
                + " i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, "
                + "i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef  "
                + "FROM pg_index i"
                + " LEFT JOIN pg_class t on (t.oid = i.indrelid) "
                + "LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) "
                + "LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) "
                + "LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname)"
                + " WHERE t.relkind = 'r' and ci.relnamespace = " + 1 + ';';
        preparedstatementHandler.prepareResultSet(GET_ALL_INDEXES_BYNAMESPACE_1,
                indexRS1);

        MockResultSet shallowLoadTablesRS = preparedstatementHandler
                .createResultSet();
        shallowLoadTablesRS.addColumn("oid");
        shallowLoadTablesRS.addColumn("relname");
        shallowLoadTablesRS.addColumn("relnamespace");
        shallowLoadTablesRS.addColumn("relpersistence");
        shallowLoadTablesRS.addRow(new Object[] {1, "order_7", 1, "p"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_SHALLOWLOADTABLES, shallowLoadTablesRS);

        MockResultSet loadFuncTriggerRS = preparedstatementHandler
                .createResultSet();
        loadFuncTriggerRS.addColumn("oid");
        loadFuncTriggerRS.addColumn("objname");
        loadFuncTriggerRS.addColumn("namespace");
        loadFuncTriggerRS.addColumn("ret");
        loadFuncTriggerRS.addColumn("alltype");
        loadFuncTriggerRS.addColumn("nargs");
        loadFuncTriggerRS.addColumn("argtype");
        loadFuncTriggerRS.addColumn("argname");
        loadFuncTriggerRS.addColumn("argmod");
        loadFuncTriggerRS.addColumn("secdef");
        loadFuncTriggerRS.addColumn("vola");
        loadFuncTriggerRS.addColumn("isstrict");
        loadFuncTriggerRS.addColumn("retset");
        loadFuncTriggerRS.addColumn("procost");
        loadFuncTriggerRS.addColumn("setrows");
        loadFuncTriggerRS.addRow(new Object[] {1, "auto1", 1, 23, null, 0, null,
                null, null, "f", "v", "f", "f", 100, 0});
        // loadFuncTriggerRS.addRow(new Object[]
        // {17830,"calculateoutput",1,2278,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.TRIGGERONLY,
                loadFuncTriggerRS);
        MockResultSet loadTriggerRS = preparedstatementHandler
                .createResultSet();
        loadTriggerRS.addColumn("oid");
        loadTriggerRS.addColumn("objname");
        loadTriggerRS.addColumn("namespace");
        loadTriggerRS.addColumn("ret");
        loadTriggerRS.addColumn("alltype");
        loadTriggerRS.addColumn("nargs");
        loadTriggerRS.addColumn("argtype");
        loadTriggerRS.addColumn("argname");
        loadTriggerRS.addColumn("argmod");
        loadTriggerRS.addColumn("secdef");
        loadTriggerRS.addColumn("vola");
        loadTriggerRS.addColumn("isstrict");
        loadTriggerRS.addColumn("retset");
        loadTriggerRS.addColumn("procost");
        loadTriggerRS.addColumn("setrows");
        loadTriggerRS.addRow(new Object[] {1, "auto1", 1, 23, null, 0, null,
                null, null, "f", "v", "f", "f", 100, 0});
        // loadTriggerRS.addRow(new Object[]
        // {17830,"calculateoutput",1,2278,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.TRIGGERONLY1,
                loadTriggerRS);
        MockResultSet functionRS = preparedstatementHandler.createResultSet();
        functionRS.addColumn("oid");
        functionRS.addColumn("objname");
        functionRS.addColumn("namespace");
        functionRS.addColumn("ret");
        functionRS.addColumn("alltype");
        functionRS.addColumn("nargs");
        functionRS.addColumn("argtype");
        functionRS.addColumn("argname");
        functionRS.addColumn("argmod");
        functionRS.addColumn("secdef");
        functionRS.addColumn("vola");
        functionRS.addColumn("isstrict");
        functionRS.addColumn("retset");
        functionRS.addColumn("procost");
        functionRS.addColumn("setrows");

        // 11389,pgxc_prepared_xact,11,25,<NULL>,0,,<NULL>,<NULL>,f,v,f,t,100,1000
        functionRS.addRow(new Object[] {1, "pgxc_prepared_xact", 1, 1, null, 0,
                " ", null, null, "f", "v", "f", "t", 1, 1});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.FUNCTIONONLY,
                functionRS);
        MockResultSet Shallowload1RS = preparedstatementHandler
                .createResultSet();
        Shallowload1RS.addColumn("oid");
        Shallowload1RS.addColumn("relname");
        Shallowload1RS.addColumn("relnamespace");
        Shallowload1RS.addColumn("relpersistence");
        Shallowload1RS.addColumn("reltablespace");
        // 736937,AAA,2200,p
        // 11389,pgxc_prepared_xact,11,25,<NULL>,0,,<NULL>,<NULL>,f,v,f,t,100,1000
        Shallowload1RS
                .addRow(new Object[] {1, "Table1", 1, "p", "tablespace1"});
        Shallowload1RS
                .addRow(new Object[] {1, "Table2", 1, "t", "tablespace2"});
        Shallowload1RS
                .addRow(new Object[] {1, "Table3", 1, "u", "tablespace3"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtilsHelper.Shallowload1,
                Shallowload1RS);
        MockResultSet shallowLoadQueryRS = preparedstatementHandler
                .createResultSet();
        shallowLoadQueryRS.addColumn("oid");
        shallowLoadQueryRS.addColumn("objname");
        shallowLoadQueryRS.addColumn("namespace");
        shallowLoadQueryRS.addColumn("ret");
        shallowLoadQueryRS.addColumn("alltype");
        shallowLoadQueryRS.addColumn("nargs");
        shallowLoadQueryRS.addColumn("argtype");
        shallowLoadQueryRS.addColumn("argname");
        shallowLoadQueryRS.addColumn("argmod");
        shallowLoadQueryRS.addColumn("secdef");
        shallowLoadQueryRS.addColumn("vola");
        shallowLoadQueryRS.addColumn("isstrict");
        shallowLoadQueryRS.addColumn("retset");
        shallowLoadQueryRS.addColumn("procost");
        shallowLoadQueryRS.addColumn("setrows");
        shallowLoadQueryRS.addRow(new Object[] {1, "auto1", 1, 23, null, 0,
                null, null, null, "f", "v", "f", "f", 100, 0});
        // shallowLoadQueryRS.addRow(new Object[]
        // {17830,"calculateoutput",1,2278,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.SHALLOWLOADQUERY, shallowLoadQueryRS);

        MockResultSet shallowLoadQueryDefaultRS = preparedstatementHandler
                .createResultSet();
        shallowLoadQueryDefaultRS.addColumn("oid");
        shallowLoadQueryDefaultRS.addColumn("objname");
        shallowLoadQueryDefaultRS.addColumn("namespace");
        shallowLoadQueryDefaultRS.addColumn("ret");
        shallowLoadQueryDefaultRS.addColumn("alltype");
        shallowLoadQueryDefaultRS.addColumn("nargs");
        shallowLoadQueryDefaultRS.addColumn("argtype");
        shallowLoadQueryDefaultRS.addColumn("argname");
        shallowLoadQueryDefaultRS.addColumn("argmod");
        shallowLoadQueryDefaultRS.addColumn("secdef");
        shallowLoadQueryDefaultRS.addColumn("vola");
        shallowLoadQueryDefaultRS.addColumn("isstrict");
        shallowLoadQueryDefaultRS.addColumn("retset");
        shallowLoadQueryDefaultRS.addColumn("procost");
        shallowLoadQueryDefaultRS.addColumn("setrows");
        shallowLoadQueryDefaultRS.addRow(new Object[] {1, "auto1", 1, 23, null,
                0, null, null, null, "f", "v", "f", "f", 100, 0});
        // shallowLoadQueryRS.addRow(new Object[]
        // {17830,"calculateoutput",1,2278,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.SHALLOWLOADQUERYDEFAULT,
                shallowLoadQueryDefaultRS);

        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();

        listnerResultSet.addColumn("oid");
        listnerResultSet.addColumn("objname");
        listnerResultSet.addColumn("namespace");
        listnerResultSet.addColumn("ret");
        listnerResultSet.addColumn("alltype");
        listnerResultSet.addColumn("nargs");
        listnerResultSet.addColumn("argtype");
        listnerResultSet.addColumn("argname");
        listnerResultSet.addColumn("argmod");
        listnerResultSet.addColumn("secdef");
        listnerResultSet.addColumn("vola");
        listnerResultSet.addColumn("isstrict");
        listnerResultSet.addColumn("retset");
        listnerResultSet.addColumn("procost");
        listnerResultSet.addColumn("setrows");

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(16), null, "1", "20 21 21", "{arg1, arg2, arg3}",
                "{i,o,b}", false, "volatile", false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(GET_ALL_FUNCTION_QUERY,
                listnerResultSet);

        MockResultSet triggerResultSet = preparedstatementHandler
                .createResultSet();

        triggerResultSet.addColumn("oid");
        triggerResultSet.addColumn("objname");
        triggerResultSet.addColumn("namespace");
        triggerResultSet.addColumn("ret");
        triggerResultSet.addColumn("alltype");
        triggerResultSet.addColumn("nargs");
        triggerResultSet.addColumn("argtype");
        triggerResultSet.addColumn("argname");
        triggerResultSet.addColumn("argmod");
        triggerResultSet.addColumn("secdef");
        triggerResultSet.addColumn("vola");
        triggerResultSet.addColumn("isstrict");
        triggerResultSet.addColumn("retset");
        triggerResultSet.addColumn("procost");
        triggerResultSet.addColumn("setrows");

        ObjectParameter tretParam = new ObjectParameter();
        tretParam.setDataType("int");
        tretParam.setName("a");
        tretParam.setType(PARAMETERTYPE.IN);

        triggerResultSet.addRow(new Object[] {new Integer(1), "trigger1", 1,
                new Integer(2279), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(GET_ALL_TRIGGER_QUERY,
                triggerResultSet);
        getViewMockRS(preparedstatementHandler);

        MockResultSet getdbTblspc = preparedstatementHandler.createResultSet();
        getdbTblspc.addColumn("spcname");

        getdbTblspc.addRow(new Object[] {"pg_default"});
        preparedstatementHandler.prepareResultSet(
                "SELECT tbs.spcname from pg_tablespace tbs, pg_database db where tbs.oid = db.dattablespace and db.datname = 'Gauss'",
                getdbTblspc);   
        
        MockResultSet searchpath = preparedstatementHandler.createResultSet();
        searchpath.addColumn("search_path");

        searchpath.addRow(new Object[] {"public,abc,pg_catalog"});
        preparedstatementHandler.prepareResultSet("SHOW search_path",
                searchpath);
        
      //For serverIp
        MockResultSet serverResultset= preparedstatementHandler.createResultSet();
        serverResultset.addColumn("inet_server_addr");
        serverResultset.addRow( new Object[]{"127.0.0.1"});
        preparedstatementHandler.prepareResultSet(FETCH_SERVER_IP,
                serverResultset);
        
        MockResultSet getUserRoleRs = preparedstatementHandler.createResultSet();
        getUserRoleRs.addColumn("rolname");
        getUserRoleRs.addColumn("rolcanlogin");
        getUserRoleRs.addColumn("oid");
        getUserRoleRs.addRow(new Object[] {"chris", true, 16408});
        getUserRoleRs.addRow(new Object[] {"tom", false, 16410});
        preparedstatementHandler.prepareResultSet(
                "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles WHERE rolsuper = false;", getUserRoleRs);
        
        MockResultSet serverEncodingRs = preparedstatementHandler.createResultSet();
        serverEncodingRs.addColumn("server_encoding");
        serverEncodingRs.addRow(new Object[] {"UTF-8"});
        preparedstatementHandler.prepareResultSet("show server_encoding", serverEncodingRs);
    }

    public static void getViewMockRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("nspoid");
        getdbsrs.addColumn("schemaname");
        getdbsrs.addColumn("viewname");
        getdbsrs.addColumn("viewowner");
        getdbsrs.addRow(new Object[] {2, 1, "public", "mytestview", "owner1"});
        preparedstatementHandler.prepareResultSet(
                "with x as (SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                        + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'm'::\"char\") ",
                getdbsrs);
        
        MockResultSet getdbsrs1 = preparedstatementHandler.createResultSet();
        getdbsrs1.addColumn("oid");
        getdbsrs1.addColumn("nspoid");
        getdbsrs1.addColumn("schemaname");
        getdbsrs1.addColumn("viewname");
        getdbsrs1.addColumn("viewowner");
        getdbsrs1.addRow(new Object[] {2, 1, "public", "mytestview", "owner1"});
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                        + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'v'::\"char\") ",
                        getdbsrs1);
    }

  /*  public static void prepareProxyInfoForGetAllNamespaces(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {

        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtilsHelper.GET_ALL_NAMESPACE, namespaceRS);
    }*/

    public static void datatypes(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String qry = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns ,  typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typ.typnamespace in (select oid from pg_namespace where nspname in ('information_schema', 'pg_catalog')) order by typ.typname";
        MockResultSet dbrs = preparedstatementHandler.createResultSet();
        dbrs.addColumn("oid");
        dbrs.addColumn("typname");
        dbrs.addColumn("typnamespace");
        dbrs.addColumn("typlen");
        dbrs.addColumn("displaycolumns");
        dbrs.addColumn("typbyval");
        dbrs.addColumn("typtype");
        dbrs.addColumn("typcategory");
        dbrs.addColumn("typtypmod");
        dbrs.addColumn("typnotnull");
        dbrs.addColumn("typarray");
        dbrs.addColumn("desc");

        dbrs.addRow(new Object[] {1, "bigint", 1, 1, "character", true, "type",
                "category", 1, true, 12, "description", "bigint"});
        dbrs.addRow(new Object[] {1, "char", 1, 1, "character", true, "type",
                "category", 1, true, 12, "description", "bigint"});
        /*
         * dbrs.addRow(new Object[] {2, "int8", 1, 1, true, "type", "category",
         * 1, true, 12, "description2"});
         */

        /*
         * datatypeRS.addRow(new Object[] {3, "bit", 1, 1, true, "type",
         * "category", 1, true, 12, "description","bit"}); datatypeRS.addRow(new
         * Object[] {4, "varbit", 1, 1, true, "type", "category", 1, true, 12,
         * "description","varbit"}); datatypeRS.addRow(new Object[] {5, "bool",
         * 1, 1, true, "type", "category", 1, true, 12, "description","bool"});
         * datatypeRS.addRow(new Object[] {6, "box", 1, 1, true, "type",
         * "category", 1, true, 12, "description","box"}); datatypeRS.addRow(new
         * Object[] {7, "bytea", 1, 1, true, "type", "category", 1, true, 12,
         * "description","bytea"}); datatypeRS.addRow(new Object[] {8,
         * "varchar", 1, 1, true, "type", "category", 1, true, 12,
         * "description","varchar"}); datatypeRS.addRow(new Object[] {9, "char",
         * 1, 1, true, "type", "category", 1, true, 12, "description","char"});
         * datatypeRS.addRow(new Object[] {10, "cidr", 1, 1, true, "type",
         * "category", 1, true, 12, "description","cidr"});
         * datatypeRS.addRow(new Object[] {11, "circle", 1, 1, true, "type",
         * "category", 1, true, 12, "description","circle"});
         * datatypeRS.addRow(new Object[] {12, "date", 1, 1, true, "type",
         * "category", 1, true, 12, "description","date"});
         * 
         * datatypeRS.addRow(new Object[] {13, "float8", 1, 1, true, "type",
         * "category", 1, true, 12, "description","float"});
         * datatypeRS.addRow(new Object[] {14, "inet", 1, 1, true, "type",
         * "category", 1, true, 12, "description",""}); datatypeRS.addRow(new
         * Object[] {15, "int4", 1, 1, true, "type", "category", 1, true, 12,
         * "description",""}); datatypeRS.addRow(new Object[] {16, "interval",
         * 1, 1, true, "type", "category", 1, true, 12, "description",""});
         * datatypeRS.addRow(new Object[] {17, "line", 1, 1, true, "type",
         * "category", 1, true, 12, "description",""}); datatypeRS.addRow(new
         * Object[] {18, "lseg", 1, 1, true, "type", "category", 1, true, 12,
         * "description",""}); datatypeRS.addRow(new Object[] {19, "macaddr", 1,
         * 1, true, "type", "category", 1, true, 12, "description",""});
         * datatypeRS.addRow(new Object[] {20, "money", 1, 1, true, "type",
         * "category", 1, true, 12, "description",""}); datatypeRS.addRow(new
         * Object[] {21, "numeric", 1, 1, true, "type", "category", 1, true, 12,
         * "description",""}); datatypeRS.addRow(new Object[] {22, "numeric", 1,
         * 1, true, "type", "category", 1, true, 12, "description",""});
         * datatypeRS.addRow(new Object[] {23, "path", 1, 1, true, "type",
         * "category", 1, true, 12, "description",""}); datatypeRS.addRow(new
         * Object[] {24, "point", 1, 1, true, "type", "category", 1, true, 12,
         * "description",""}); datatypeRS.addRow(new Object[] {25, "polygon", 1,
         * 1, true, "type", "category", 1, true, 12, "description",""});
         * datatypeRS.addRow(new Object[] {26, "float4", 1, 1, true, "type",
         * "category", 1, true, 12, "description",""}); datatypeRS.addRow(new
         * Object[] {27, "int2", 1, 1, true, "type", "category", 1, true, 12,
         * "description",""});
         * 
         * datatypeRS.addRow(new Object[] {28, "text", 1, 1, true, "type",
         * "category", 1, true, 12, "description",""}); datatypeRS.addRow(new
         * Object[] {29, "time", 1, 1, true, "type", "category", 1, true, 12,
         * "description",""}); datatypeRS.addRow(new Object[] {30, "timetz", 1,
         * 1, true, "type", "category", 1, true, 12, "description",""});
         * datatypeRS.addRow(new Object[] {31, "timestamp", 1, 1, true, "type",
         * "category", 1, true, 12, "description",""}); datatypeRS.addRow(new
         * Object[] {32, "timestamptz", 1, 1, true, "type", "category", 1, true,
         * 12, "description",""}); datatypeRS.addRow(new Object[] {33,
         * "tsquery", 1, 1, true, "type", "category", 1, true, 12,
         * "description",""}); datatypeRS.addRow(new Object[] {34, "tsvector",
         * 1, 1, true, "type", "category", 1, true, 12, "description",""});
         * datatypeRS.addRow(new Object[] {35, "txid_snapshot", 1, 1, true,
         * "type", "category", 1, true, 12, "description",""});
         * datatypeRS.addRow(new Object[] {36, "uuid", 1, 1, true, "type",
         * "category", 1, true, 12, "description",""}); datatypeRS.addRow(new
         * Object[] {37, "xml", 1, 1, true, "type", "category", 1, true, 12,
         * "description",""});
         */
        preparedstatementHandler.prepareResultSet(qry, dbrs);
    }

  
   
}