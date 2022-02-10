package org.opengauss.mppdbide.mock.adapter;

import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

import org.opengauss.mppdbide.adapter.AbstractConnectionDriver;
import org.opengauss.mppdbide.adapter.driver.DBMSDriverManager;
import org.opengauss.mppdbide.adapter.driver.Gauss200V1R7Driver;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.utils.IMessagesConstants;
//Dependency on BL
/*import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;*/
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

public class CommonLLTUtils
{
    private static ResultSet      rs;

    public static final Timestamp TIMESTAMP = new Timestamp(
            System.currentTimeMillis());

    public enum EXCEPTIONENUM
    {
        YES, NO, EXCEPTION
    }

    public static final String GET_ALL_NAMESPACE                         = "SELECT oid, nspname from pg_namespace where (oid >= 16384 or nspname LIKE 'public') and nspname  NOT LIKE 'pg_%' ORDER BY nspname;";
    public static final String GET_ALL_SHALLOWLOADTABLES                 = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, "
            + " tbl.relpersistence relpersistence from pg_class tbl where tbl.relkind = 'r' "
            + "and tbl.relnamespace not in (select oid from pg_namespace where nspname in ('cstore', 'pg_toast')) "
            + "order by relname";
    public static final String Shallowload1                              = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, tbl.relpersistence relpersistence,ts.spcname as reltablespace "
            + "from pg_class tbl left join pg_tablespace ts on ts.oid = tbl.reltablespace "
            + "where tbl.relkind = 'r' and tbl.relnamespace not in "
            + "(select oid from pg_namespace where nspname in ('cstore', 'pg_toast'))"
            + " order by relname";
    public static final String FUNCTIONONLY                              = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname='plpgsql'  and UPPER(typ.typname) != 'TRIGGER'  and pr.pronamespace= 1 ORDER BY objname";
    public static final String SHALLOWLOADQUERYDEFAULT                   = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname='plpgsql'  and pr.pronamespace= 1 ORDER BY objname";
    public static final String TRIGGERONLY                               = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname='plpgsql'  and UPPER(typ.typname)  = 'TRIGGER'  ORDER BY objname";
    public static final String TRIGGERONLY1                              = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname='plpgsql'  and UPPER(typ.typname) != 'TRIGGER' ";
    public static final String TRIGGERONLY2                              = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname='plpgsql'  and UPPER(typ.typname)  = 'TRIGGER'  and pr.pronamespace= 1 ORDER BY objname";
    public static final String ALLCONSTRAIN                              = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef  FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind = 'r' and t.oid = 1;";
    public static final String ALLCONSTRAIN_1                            = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam  as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate  as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) WHERE t.relkind = 'r' and t.oid = 1";
    public static final String ALLCONSTRAIN_2                            = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam  as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate  as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) WHERE t.relkind = 'r' and t.oid = 1";
    public static final String LAVEL2COLUNM                              = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr, typ.typnamespace as dtns from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0  and t.relnamespace = 1 order by t.oid, c.attnum";
    public static final String SHALLOWLOADQUERY                          = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, "
            + "pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, "
            + "pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr "
            + "JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  "
            + "WHERE lng.lanname in ('plpgsql','sql')  and pr.pronamespace= 1 ORDER BY objname";

    /*
     * public static final String GET_ALL_DATATYPE =
     * "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, "
     * +
     * "typ.typlen as typlen, typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, "
     * +
     * "typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc "
     * +
     * "from pg_type typ left join pg_description des on (typ.oid = des.objoid) "
     * ; public static final String GET_ALL_DATATYPE_DB =
     * "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, "
     * +
     * "typ.typlen as typlen, typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, "
     * +
     * "typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc "
     * +
     * "from pg_type typ left join pg_description des on (typ.oid = des.objoid) "
     * ; public static final String GET_ALL_DATATYPE_database =
     * "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, "
     * +
     * "typ.typlen as typlen, typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, "
     * +
     * "typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc "
     * +
     * "from pg_type typ left join pg_description des on (typ.oid = des.objoid) "
     * ;
     */

    public static final String GET_ALL_DATATYPE                          = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns ,  typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typ.typnamespace in (select oid from pg_namespace where nspname in ('information_schema', 'pg_catalog')) order by typ.typname";
    public static final String GET_ALL_DATATYPE_DB                       = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns ,  typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typ.typnamespace in (select oid from pg_namespace where nspname in ('information_schema', 'pg_catalog')) order by typ.typname";
    public static final String GET_ALL_DATATYPE_database                 = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns ,  typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typ.typnamespace in (select oid from pg_namespace where nspname in ('information_schema', 'pg_catalog')) order by typ.typname";

    public static final String FETCH_ALL_TABLEMETADATA                   = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace,tbl.reloptions as reloptions, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r'";
    // overloaded method of refreshTableMetada

    public static final String REFRESH_TABLE_METADATA                    = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.relnamespace = 1;";

    // Partition table info for a particular table
    public static final String REFRESH_TABLE_METADATA_PARTITION_TABLE    = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes, tbl.reloptions as reloptions  from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.parttype in ('p', 'v') and tbl.oid = 0 and tbl.relkind <>  'i';";

    public static final String GET_ALL_COLUMN_METADATA                   = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r' and t.parttype ='n') left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 1 order by t.oid, c.attnum;";

    public static final String GET_ALL_COLUMN_METADATA_OVERLOADED_METHOD = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 1 order by t.oid, c.attnum;";

    public static final String GET_ALL_INDEXES                           = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, "
            + "ci.relam  as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, "
            + "i.indimmediate  as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, "
            + "i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) "
            + "LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) WHERE t.relkind = 'r';";

    public static final String GET_ALL_INDEXES_BYNAMESPACE               = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, "
            + "i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate "
            + "as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, "
            + "i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef , def.tablespace  "
            + "FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid)"
            + " LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind = 'r' and ci.relnamespace = 1"
            + " and ci.parttype not in ('p','v') and ci.relkind not in ('I')" + ';';

    public static final String GET_ALL_CONSTRAINTS                       = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname ,c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def ,case when (def.tablespace is null or '') and c.contype <> 'c' then 'DEFAULT' else def.tablespace end FROM pg_constraint c LEFT JOIN pg_class t on (t.oid = c.conrelid) LEFT JOIN pg_index ind ON c.conindid = ind.indexrelid LEFT JOIN pg_class ci on (ind.indexrelid = ci.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname) WHERE t.relkind = 'r' and t.oid = 1";

    public static final String GET_ALL_CONSTRAINTS_BYNAMESPACE           = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, "

            + "c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, "
            + "c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, "
            + "c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, "
            + "c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist "
            + "pg_get_constraintdef(c.oid) as const_def "
            + "FROM pg_constraint c LEFT JOIN pg_class t on (t.oid = c.conrelid) WHERE t.relkind = 'r' and c.connamespace=1"
            + " and cl.parttype not in ('p','v')" + " and c.conrelid <> 0"
            + ';';

    public static final String GET_ALL_FUNCTION_QUERY                    = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, "
            + "pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows"
            + " FROM pg_proc pr "
            + " JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace"
            + " JOIN pg_language lng ON lng.oid=prolang LEFT OUTER JOIN pg_description des ON des.objoid=pr.oid "
            + " WHERE lng.lanname in ('plpgsql','sql') and UPPER(typ.typname) != 'TRIGGER' AND pr.pronamespace="
            + 1 + " ORDER BY objname;";

    public static final String GET_ALL_TRIGGER_QUERY                     = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, "
            + "pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows"
            + " FROM pg_proc pr "
            + " JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace"
            + " JOIN pg_language lng ON lng.oid=prolang LEFT OUTER JOIN pg_description des ON des.objoid=pr.oid "
            + " WHERE lng.lanname='plpgsql' and UPPER(typ.typname) = 'TRIGGER' and pr.pronamespace="
            + 1 + " ORDER BY objname;";

    public static final String TBL_SPC_META                              = "select oid, pg_tablespace_location(oid) as location, spcname,spcacl,spcoptions, spcmaxsize, relative from pg_tablespace  where oid ="
            + 10 + ";";

    public static final String GET_ALL_VIEWS                             = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
            + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
            + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
            + "from pg_class v "
            + "left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') "
            + "left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) "
            + "left join pg_type typ on (c.atttypid = typ.oid) "
            + "where c.attisdropped = 'f' and c.attnum > 0 " + "and v.oid = "
            + 1 + " order by v.oid, c.attnum";

    public static final String Refresh_TABLEMETADATA                     = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.oid = 1;";

    public static final String GET_ALL_FTABLES_IN_SCHEMA                 = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes,tbl.reloptions as reloptions, frgn.ftoptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'f' and tbl.relnamespace = 1;";

    public static final String GET_COLUMN_INFO_FOREIGN_TABLES            = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'f') left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 1 order by t.oid, c.attnum;";

    public static final String NOTIFICATION_QUERY_SUCCESSFULL            = "SELECT logintime, client_conninfo from login_audit_messages_pid(true)";

    public static final String NOTIFICATION_QUERY_FAILURE                = "SELECT logintime, client_conninfo from login_audit_messages_pid(false)";

    public static final String GET_ALL_PARTITION_TABLES                  = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes, tbl.reloptions as reloptions , array_to_string(part.partkey,',') as partkey  from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.parttype in ('p', 'v') and tbl.relnamespace = 1 and tbl.relkind <>  'i';";

    public static final String GET_COLUMN_INFO_PARTITION_TABLES          = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v')) left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 1 and t.relkind <> 'i' order by t.oid, c.attnum;";
    // column info for a single partition table
    public static final String GET_COLUMN_INFO_PARTITION_TABLE           = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v')) left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 1 and t.relkind <> 'i' order by c.attnum;";

    public static final String GET_PARTITIONS                            = "select p.oid AS partition_id , p.relname AS partition_name , p.parentid AS table_id  from pg_class c, pg_partition p  where c.relnamespace =  1 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;";

    // partition info for a single table
    public static final String GET_PARTITION                             = "select p.oid AS partition_id , p.relname AS partition_name , p.parentid AS table_id  from pg_class c, pg_partition p  where c.oid =  1 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;";
  
    public static final String GET_PARTITION_CONSTRAINTS                 = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=1 and cl.parttype in ('p','v') and c.conrelid <> 0;";
    // constraint info for a single partition table
    public static final String GET_PARTITION_CONSTRAINT                  = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=1 and cl.parttype in ('p','v') and c.conrelid <> 0;";

    public static final String GET_PARTITION_INDEXES                     = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind = 'r' and ci.parttype in ('p','v')  and ci.relnamespace = 1;";

    // index info for a single partition table
    public static final String GET_PARTITION_INDEX                       = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind = 'r' and ci.parttype in ('p','v') and ci.oid = 1;";

    public static final String GET_REFRESH_DATATYPE                      = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns , typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typnamespace = 1 order by typ.typname";

    public static final String REFRESH_SEQUENCE_METADATA                 = "select oid,relnamespace,relowner,relname from pg_class where relkind='S' and relnamespace= 1";
    public static final String COLUMN_COMMENTS                           = "select pg_description.objsubid,pg_description.description from pg_description where objoid = "
            + 1 + ';';

    public static DBConnection getDBConnection()
    {
        DBConnection connection1 = new DBConnection();

        String url = null;
        Properties props = new Properties();

        try
        {
            initDriver("org.postgresql.Driver");
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        props.setProperty("user", "test");
        props.setProperty("password", "test");
        props.setProperty("allowEncodingChanges", "true");
        String encoding = System.getProperty("file.encoding");
        props.setProperty("characterEncoding", encoding);
        props.setProperty("ApplicationName", "MPP IDE");

        url = "jdbc:postgresql://127.0.0.1:1234/testDB";

        try
        {
            connection1.dbConnect(props, url);
        }
        catch (DatabaseOperationException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (DatabaseCriticalException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return connection1;
    }
    
    public static void initDriver(String strDriver) throws DatabaseOperationException {
        try {
            Class.forName(strDriver);
            MPPDBIDELoggerUtility.info("ADAPTER: Driver Init Successful.");
        } catch (ClassNotFoundException e) {
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_DRIVER_INIT_FAILED, e);
        }
    }

    public static void prepareProxyInfo(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet sequenceset = preparedstatementHandler.createResultSet();
        sequenceset.addColumn("oid");
        sequenceset.addColumn("relnamespace");
        sequenceset.addColumn("relowner");
        sequenceset.addColumn("relname");
        sequenceset.addRow(new Object[] {1, 1, 10, "sequence1"});
        sequenceset.addRow(new Object[] {2, 1, 10, "sequence2"});
        preparedstatementHandler.prepareResultSet(
                "select oid,relnamespace,relowner,relname from pg_class where relkind='S' and relnamespace=1",
                sequenceset);
        MockResultSet sequenceset2 = preparedstatementHandler.createResultSet();
        sequenceset2.addColumn("oid");
        sequenceset2.addColumn("relnamespace");
        sequenceset2.addColumn("relowner");
        sequenceset2.addColumn("relname");
        sequenceset2.addRow(new Object[] {1, 1, 10, "sequence1"});
        preparedstatementHandler.prepareResultSet(
                "select oid,relnamespace,relowner,relname from pg_class where relkind='S' and oid=1",
                sequenceset2);
        // getSchemaNameOne(preparedstatementHandler);
        MockResultSet getDeadlineInfo = preparedstatementHandler
                .createResultSet();
        getDeadlineInfo.addColumn("DEADLINE");

        getDeadlineInfo.addRow(new Object[] {"3.124632"});
        preparedstatementHandler.prepareResultSet(
                "select intervaltonum(gs_password_deadline()) as DEADLINE;",
                getDeadlineInfo);

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
        String query = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, "
                + "ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion,"
                + " i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, "
                + "i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef  "
                + "FROM pg_index i"
                + " LEFT JOIN pg_class t on (t.oid = i.indrelid) "
                + "LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) "
                + "LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) "
                + "LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname)"
                + " WHERE t.relkind = 'r' and t.oid = " + 0 + ';';
        preparedstatementHandler.prepareResultSet(query, indexRS1);

        MockResultSet getVersionResult = preparedstatementHandler
                .createResultSet();
        getVersionResult.addColumn("proxyAPIVer");
        getVersionResult.addColumn("serverVersionStr");

        getVersionResult.addRow(new Object[] {
                "PostgreSQL 9.2beta2 on i686-pc-linux-gnu, compiled by gcc (SUSE Linux) 4.3.4 [gcc-4_3-branch revision 152973], 32-bit",
                "GMDB Tools V2R5C00B201"});
        preparedstatementHandler.prepareResultSet(
                "SELECT * from pldbg_get_proxy_info();", getVersionResult);

        MockResultSet gettblsprs1 = preparedstatementHandler.createResultSet();
        gettblsprs1.addColumn("oid");
        gettblsprs1.addColumn("nspname");
        gettblsprs1.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(
                "SELECT oid from pg_namespace WHERE nspname=?", gettblsprs1);

        MockResultSet getdbsrs1 = preparedstatementHandler.createResultSet();
        getdbsrs1.addColumn("oid");
        getdbsrs1.addColumn("nspoid");
        getdbsrs1.addColumn("schemaname");
        getdbsrs1.addColumn("viewname");
        getdbsrs1.addColumn("viewowner");
        getdbsrs1.addRow(new Object[] {2, 1, "public", "mytestview", "owner1"});
        getdbsrs1.addRow(
                new Object[] {25, 1, "public", "mytestview2", "owner1"});
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                        + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\") and has_table_privilege(c.oid,'SELECT') ",
                getdbsrs1);

        MockResultSet gettblsprs2 = preparedstatementHandler.createResultSet();
        gettblsprs2.addColumn("nodeid");
        gettblsprs2.addColumn("namespaceid");
        gettblsprs2.addColumn("relname");
        gettblsprs2.addRow(new Object[] {1, 1, "veera"});
        preparedstatementHandler.prepareResultSet(
                "select cls.relfilenode as nodeid ,cls.relnamespace as namespaceid from pg_class as cls where cls.relname=?",
                gettblsprs2);

        MockResultSet gettblsprs4 = preparedstatementHandler.createResultSet();
        gettblsprs4.addColumn("distype");
        gettblsprs4.addColumn("attnum");
        gettblsprs4.addColumn("pcrelid");

        gettblsprs4.addRow(new Object[] {1, 1, "veera"});
        preparedstatementHandler.prepareResultSet(
                "select xcls.pclocatortype as distype,xcls.pcattnum as attnum from pgxc_class as xcls where xcls.pcrelid=?",
                gettblsprs4);

        MockResultSet tablespaceMetaData = preparedstatementHandler
                .createResultSet();
        tablespaceMetaData.addColumn("oid");
        tablespaceMetaData.addColumn("spcname");
        tablespaceMetaData.addColumn("spcoptions");
        tablespaceMetaData.addColumn("location");
        tablespaceMetaData.addColumn("spcmaxsize");
        tablespaceMetaData.addColumn("relative");
        tablespaceMetaData.addRow(new Object[] {1, "tblspc",
                new String[] {"filesystem=HDFS", "address = address"},
                "location", "spcmaxsize", false});
        tablespaceMetaData.addRow(
                new Object[] {1, "tblspc", null, "location", "spcmaxsize", false});
        preparedstatementHandler.prepareResultSet(
                "select oid, pg_tablespace_location(oid) as location, spcname,spcacl,spcoptions, spcmaxsize, relative from pg_tablespace  where oid ="
                        + 1,
                tablespaceMetaData);

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

        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("datname");

        getdbsrs.addRow(new Object[] {1, "Gauss"});
        preparedstatementHandler.prepareResultSet(
                "select oid, datname from pg_database where datistemplate='f'",
                getdbsrs);

        MockResultSet server_encoding = preparedstatementHandler
                .createResultSet();
        server_encoding.addRow(new Object[] {"UTF-8"});
        preparedstatementHandler.prepareResultSet("show server_encoding",
                server_encoding);

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
                "select oid, pg_tablespace_location(oid) as location ,spcname,spcacl,spcoptions, spcmaxsize, relative from pg_tablespace order by spcname;",
                gettblsprs);

        MockResultSet getaccessmethodrs = preparedstatementHandler
                .createResultSet();
        getaccessmethodrs.addColumn("oid");
        getaccessmethodrs.addColumn("amname");

        getaccessmethodrs.addRow(new Object[] {1, "accemethod1"});
        preparedstatementHandler.prepareResultSet(
                "SELECT oid, amname from pg_am order by oid;",
                getaccessmethodrs);

        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addRow(new Object[] {1, "PUBLIC"});
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        // namespaceRS.addRow(new Object[] {2, "schema1"});
        // namespaceRS.addRow(new Object[] {3, "schema2"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_NAMESPACE, namespaceRS);

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
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.GET_ALL_VIEWS,
                viewRS);

        MockResultSet shallowLoadTablesRS = preparedstatementHandler
                .createResultSet();
        shallowLoadTablesRS.addColumn("oid");
        shallowLoadTablesRS.addColumn("relname");
        shallowLoadTablesRS.addColumn("relnamespace");
        shallowLoadTablesRS.addColumn("relpersistence");
        shallowLoadTablesRS.addRow(new Object[] {1, "order_7", 1, "p"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_SHALLOWLOADTABLES, shallowLoadTablesRS);
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
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.FUNCTIONONLY,
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
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.Shallowload1,
                Shallowload1RS);
        /*
         * MockResultSet shallowLoadQueryDefaultRS =
         * preparedstatementHandler.createResultSet();
         * shallowLoadQueryDefaultRS.addColumn("oid");
         * shallowLoadQueryDefaultRS.addColumn("objname");
         * shallowLoadQueryDefaultRS.addColumn("namespace");
         * shallowLoadQueryDefaultRS.addColumn("ret");
         * shallowLoadQueryDefaultRS.addColumn("alltype");
         * shallowLoadQueryDefaultRS.addColumn("nargs");
         * shallowLoadQueryDefaultRS.addColumn("argtype");
         * shallowLoadQueryDefaultRS.addColumn("argname");
         * shallowLoadQueryDefaultRS.addColumn("argmod");
         * shallowLoadQueryDefaultRS.addColumn("secdef");
         * shallowLoadQueryDefaultRS.addColumn("vola");
         * shallowLoadQueryDefaultRS.addColumn("isstrict");
         * shallowLoadQueryDefaultRS.addColumn("retset");
         * shallowLoadQueryDefaultRS.addColumn("procost");
         * shallowLoadQueryDefaultRS.addColumn("setrows");
         * shallowLoadQueryDefaultRS.addRow(new Object[]
         * {1,"auto1",1,23,null,0,null,null,null,"f","v","f","f",100,0}); //
         * shallowLoadQueryRS.addRow(new Object[]
         * {17830,"calculateoutput",1,2278
         * ,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
         * preparedstatementHandler
         * .prepareResultSet(CommonLLTUtils.SHALLOWLOADQUERYDEFAULT,
         * shallowLoadQueryDefaultRS);
         */

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
        loadFuncTriggerRS.addColumn("lang");
        loadFuncTriggerRS.addRow(new Object[] {1, "auto1", 1, 23, null, 0, null,
                null, null, "f", "v", "f", "f", 100, 0, "plpgsql"});
        // loadFuncTriggerRS.addRow(new Object[]
        // {17830,"calculateoutput",1,2278,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
        loadFuncTriggerRS.addRow(new Object[] {2, "function2", 1, 2278,
                "{23,23,23}", 2, 23, "{id1,id2,value}", "{i,o,b}", "f", "v",
                "f", "f", 100, 0, "plpgsql"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.TRIGGERONLY,
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
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.TRIGGERONLY1,
                loadTriggerRS);
        MockResultSet loadTriggerRS2 = preparedstatementHandler
                .createResultSet();
        loadTriggerRS2.addColumn("oid");
        loadTriggerRS2.addColumn("objname");
        loadTriggerRS2.addColumn("namespace");
        loadTriggerRS2.addColumn("ret");
        loadTriggerRS2.addColumn("alltype");
        loadTriggerRS2.addColumn("nargs");
        loadTriggerRS2.addColumn("argtype");
        loadTriggerRS2.addColumn("argname");
        loadTriggerRS2.addColumn("argmod");
        loadTriggerRS2.addColumn("secdef");
        loadTriggerRS2.addColumn("vola");
        loadTriggerRS2.addColumn("isstrict");
        loadTriggerRS2.addColumn("retset");
        loadTriggerRS2.addColumn("procost");
        loadTriggerRS2.addColumn("setrows");
        loadTriggerRS2.addRow(new Object[] {1, "auto1", 1, 23, null, 0, null,
                null, null, "f", "v", "f", "f", 100, 0});
        // loadTriggerRS.addRow(new Object[]
        // {17830,"calculateoutput",1,2278,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.TRIGGERONLY2,
                loadTriggerRS);

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
        shallowLoadQueryRS.addColumn("lang");
        shallowLoadQueryRS.addRow(new Object[] {1, "auto1", 1, 23, null, 0,
                null, null, null, "f", "v", "f", "f", 100, 0, "plpgsql"});
        // shallowLoadQueryRS.addRow(new Object[]
        // {17830,"calculateoutput",1,2278,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
        shallowLoadQueryRS.addRow(new Object[] {2, "function2", 1, 2278,
                "{23,23,23}", 2, 23, "{id1,id2,value}", "{i,o,b}", "f", "v",
                "f", "f", 100, 0, "plpgsql"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.SHALLOWLOADQUERY, shallowLoadQueryRS);

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
        shallowLoadQueryDefaultRS.addColumn("lang");
        shallowLoadQueryDefaultRS.addRow(new Object[] {1, "auto1", 1, 23, null,
                1, null, null, null, "f", "v", "f", "f", 100, 0, "plpgsql"});
        // shallowLoadQueryRS.addRow(new Object[]
        // {17830,"calculateoutput",1,2278,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
        shallowLoadQueryRS.addRow(new Object[] {2, "function2", 1, 2278,
                "{23,23,23}", 2, 23, "{id1,id2,value}", "{i,o,b}", "f", "v",
                "f", "f", 100, 0, "plpgsql"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.SHALLOWLOADQUERYDEFAULT,
                shallowLoadQueryDefaultRS);

        /*
         * MockResultSet datatypeRS =
         * preparedstatementHandler.createResultSet();
         * datatypeRS.addColumn("oid"); datatypeRS.addColumn("typname");
         * datatypeRS.addColumn("typnamespace"); datatypeRS.addColumn("typlen");
         * datatypeRS.addColumn("typbyval"); datatypeRS.addColumn("typtype");
         * datatypeRS.addColumn("typcategory");
         * datatypeRS.addColumn("typtypmod");
         * datatypeRS.addColumn("typnotnull"); datatypeRS.addColumn("typarray");
         * datatypeRS.addColumn("desc"); datatypeRS.addColumn("displaycolumns");
         * 
         * datatypeRS.addRow(new Object[] {1, "bigint", 1, 1, true, "type",
         * "category", 1, true, 12, "description","bigint"});
         * datatypeRS.addRow(new Object[] {2, "int8", 1, 1, true, "type",
         * "category", 1, true, 12, "description2","character"});
         * 
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
         * 
         * preparedstatementHandler
         * .prepareResultSet(CommonLLTUtils.GET_ALL_DATATYPE, datatypeRS);
         */

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
        preparedstatementHandler.prepareResultSet(qry, dbrs);

        MockResultSet tablemetadataRS = preparedstatementHandler
                .createResultSet();
        tablemetadataRS.addColumn("oid");
        tablemetadataRS.addColumn("relname");
        tablemetadataRS.addColumn("relnamespace");
        tablemetadataRS.addColumn("reltablespace");
        tablemetadataRS.addColumn("relpersistence");
        tablemetadataRS.addColumn("desc");
        tablemetadataRS.addColumn("nodes");
        tablemetadataRS.addColumn("reloptions");

        tablemetadataRS.addRow(new Object[] {1, "MyTable", 1, 1, true,
                "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.REFRESH_TABLE_METADATA, tablemetadataRS);

        MockResultSet colmetadataRS5 = preparedstatementHandler
                .createResultSet();
        colmetadataRS5.addColumn("tableid");
        colmetadataRS5.addColumn("namespaceid");
        colmetadataRS5.addColumn("columnidx");
        colmetadataRS5.addColumn("name");
        colmetadataRS5.addColumn("datatypeoid");
        colmetadataRS5.addColumn("dtns");
        colmetadataRS5.addColumn("length");
        colmetadataRS5.addColumn("precision");
        colmetadataRS5.addColumn("dimentions");
        colmetadataRS5.addColumn("notnull");
        colmetadataRS5.addColumn("isdefaultvalueavailable");
        colmetadataRS5.addColumn("default_value");
        colmetadataRS5.addColumn("attDefStr");
        colmetadataRS5.addColumn("displayColumns");
        colmetadataRS5.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                false, true, "Default value", "attrString", "bigint"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_COLUMN_METADATA, colmetadataRS5);

        MockResultSet level2colunmRS = preparedstatementHandler
                .createResultSet();
        level2colunmRS.addColumn("tableid");
        level2colunmRS.addColumn("namespaceid");
        level2colunmRS.addColumn("columnidx");
        level2colunmRS.addColumn("name");
        level2colunmRS.addColumn("datatypeoid");
        level2colunmRS.addColumn("dtns");
        level2colunmRS.addColumn("length");
        level2colunmRS.addColumn("precision");
        level2colunmRS.addColumn("dimentions");
        level2colunmRS.addColumn("notnull");
        level2colunmRS.addColumn("isdefaultvalueavailable");
        level2colunmRS.addColumn("default_value");
        level2colunmRS.addColumn("attDefStr");

        level2colunmRS.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 0, 0, 0,
                false, true, "Default value", "attrString"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.LAVEL2COLUNM,
                level2colunmRS);

        MockResultSet allIndexesRS = preparedstatementHandler.createResultSet();
        allIndexesRS.addColumn("oid");
        allIndexesRS.addColumn("tableId");
        allIndexesRS.addColumn("indexname");
        allIndexesRS.addColumn("namespaceid");
        allIndexesRS.addColumn("accessmethodid");
        allIndexesRS.addColumn("isunique");
        allIndexesRS.addColumn("isprimary");
        allIndexesRS.addColumn("isexclusion");
        allIndexesRS.addColumn("isimmediate");
        allIndexesRS.addColumn("isclustered");
        allIndexesRS.addColumn("checkmin");
        allIndexesRS.addColumn("isready");
        allIndexesRS.addColumn("cols");
        allIndexesRS.addColumn("reloptions");
        allIndexesRS.addColumn("indexdef");

        allIndexesRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", ""});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.ALLCONSTRAIN,
                allIndexesRS);

        MockResultSet allIndexes_rs = preparedstatementHandler
                .createResultSet();
        allIndexes_rs.addColumn("oid");
        allIndexes_rs.addColumn("tableId");
        allIndexes_rs.addColumn("indexname");
        allIndexes_rs.addColumn("namespaceid");
        allIndexes_rs.addColumn("accessmethodid");
        allIndexes_rs.addColumn("isunique");
        allIndexes_rs.addColumn("isprimary");
        allIndexes_rs.addColumn("isexclusion");
        allIndexes_rs.addColumn("isimmediate");
        allIndexes_rs.addColumn("isclustered");
        allIndexes_rs.addColumn("checkmin");
        allIndexes_rs.addColumn("isready");
        allIndexes_rs.addColumn("cols");
        allIndexes_rs.addColumn("reloptions");
        allIndexes_rs.addColumn("indexdef");

        allIndexes_rs.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "-1", "", "fillfactor=1"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.ALLCONSTRAIN_1,
                allIndexes_rs);

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
        indexRS.addColumn("indexdef");
        indexRS.addColumn("tablespace");

        indexRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler
                .prepareResultSet(CommonLLTUtils.GET_ALL_INDEXES, indexRS);

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
        constraintRS.addColumn("tablespace");
        constraintRS.addRow(new Object[] {1, 1, 1, "ConstraintName",
                "ConstraintType", false, false, false, 1, 1, "", "", "", "",
                "1", "1", "", "constraint_tablespace"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_CONSTRAINTS, constraintRS);

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
        listnerResultSet.addColumn("lang");
      //Dependency on BL
       /* ObjectParameter retParam = new ObjectParameter();
        retParam.setDatatype("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);*/

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(16), null, "1", "20 21 21", "{arg1, arg2, arg3}",
                "{i,o,b}", false, "volatile", false, false, 10, 10, "plpgsql"});

        /*
         * listnerResultSet.addRow(new Object[] {2, "function2", 1, new
         * Integer(16), null, "1", "20 21 21", "{arg1, arg2, arg3}", "{i,o,b}",
         * false, "volatile", false, false, 10, 10, "plpgsql"});
         * listnerResultSet.addRow(new Object[] {3, "function2", 1, new
         * Integer(16), null, "1", "20 21 21", "{arg1, arg2, arg3}", "{i,o,b}",
         * false, "volatile", false, false, 10, 10, "sql"});
         */
        setResultSet(listnerResultSet);

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
        //Dependency on BL
        /*ObjectParameter tretParam = new ObjectParameter();
        tretParam.setDatatype("int");
        tretParam.setName("a");
        tretParam.setType(PARAMETERTYPE.IN);*/

        triggerResultSet.addRow(new Object[] {new Integer(1), "trigger1", 1,
                new Integer(2279), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10});

        preparedstatementHandler.prepareResultSet(GET_ALL_TRIGGER_QUERY,
                triggerResultSet);

        getViewMockRS(preparedstatementHandler);

        MockResultSet searchpath = preparedstatementHandler.createResultSet();
        searchpath.addColumn("search_path");

        searchpath.addRow(new Object[] {"PUBLIC,abc,pg_catalog"});
        preparedstatementHandler.prepareResultSet("SHOW search_path",
                searchpath);
        MockResultSet refreshtablemetada = preparedstatementHandler
                .createResultSet();
        refreshtablemetada.addColumn("oid");
        refreshtablemetada.addColumn("relname");
        refreshtablemetada.addColumn("relnamespace");
        refreshtablemetada.addColumn("reltablespace");
        refreshtablemetada.addColumn("relpersistence");
        refreshtablemetada.addColumn("desc");
        refreshtablemetada.addColumn("nodes");
        refreshtablemetada.addColumn("reloptions");

        refreshtablemetada.addRow(new Object[] {1, "MyTable", 1, 1, true,
                "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.Refresh_TABLEMETADATA, refreshtablemetada);

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
        colmetadataRS1.addColumn("attDefStr");
        colmetadataRS1.addColumn("displayColumns");

        colmetadataRS1.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                false, true, "Default value", "attrString", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_COLUMN_METADATA_OVERLOADED_METHOD,
                colmetadataRS1);

        MockResultSet fetchalltablemetada = preparedstatementHandler
                .createResultSet();
        fetchalltablemetada.addColumn("oid");
        fetchalltablemetada.addColumn("relname");
        fetchalltablemetada.addColumn("relnamespace");
        fetchalltablemetada.addColumn("reloptions");
        fetchalltablemetada.addColumn("reltablespace");
        fetchalltablemetada.addColumn("relpersistence");
        fetchalltablemetada.addColumn("desc");
        fetchalltablemetada.addColumn("nodes");

        fetchalltablemetada
                .addRow(new Object[] {1, "pg_db_role_setting_mytable", 1, "", 1,
                        true, "description", "1 2"});

        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.FETCH_ALL_TABLEMETADATA, fetchalltablemetada);

        MockResultSet getallftablesinschema = preparedstatementHandler
                .createResultSet();
        getallftablesinschema.addColumn("oid");
        getallftablesinschema.addColumn("relname");
        getallftablesinschema.addColumn("relnamespace");
        getallftablesinschema.addColumn("reltablespace");
        getallftablesinschema.addColumn("relpersistence");
        getallftablesinschema.addColumn("desc");
        getallftablesinschema.addColumn("nodes");
        getallftablesinschema.addColumn("reloptions");
        getallftablesinschema.addColumn("ftoptions");

        getallftablesinschema.addRow(new Object[] {1, "MyTable", 1, 1, true,
                "description", "1 2", "", "gsfs"});

        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_FTABLES_IN_SCHEMA,
                getallftablesinschema);

        MockResultSet getcolumninfoRSftble = preparedstatementHandler
                .createResultSet();
        getcolumninfoRSftble.addColumn("tableid");
        getcolumninfoRSftble.addColumn("namespaceid");
        getcolumninfoRSftble.addColumn("columnidx");
        getcolumninfoRSftble.addColumn("name");
        getcolumninfoRSftble.addColumn("datatypeoid");
        getcolumninfoRSftble.addColumn("dtns");
        getcolumninfoRSftble.addColumn("length");
        getcolumninfoRSftble.addColumn("precision");
        getcolumninfoRSftble.addColumn("dimentions");
        getcolumninfoRSftble.addColumn("notnull");
        getcolumninfoRSftble.addColumn("isdefaultvalueavailable");
        getcolumninfoRSftble.addColumn("default_value");
        getcolumninfoRSftble.addColumn("attDefStr");
        getcolumninfoRSftble.addColumn("displayColumns");
        getcolumninfoRSftble.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200,
                0, 0, false, true, "Default value", "attrString", "bigint"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_COLUMN_INFO_FOREIGN_TABLES,
                getcolumninfoRSftble);

        MockResultSet getAllPartitionatable = preparedstatementHandler
                .createResultSet();
        getAllPartitionatable.addColumn("oid");
        getAllPartitionatable.addColumn("relname");
        getAllPartitionatable.addColumn("relnamespace");
        getAllPartitionatable.addColumn("reltablespace");
        getAllPartitionatable.addColumn("relpersistence");
        getAllPartitionatable.addColumn("desc");
        getAllPartitionatable.addColumn("nodes");
        getAllPartitionatable.addColumn("reloptions");
        getAllPartitionatable.addColumn("partkey");

        getAllPartitionatable.addRow(new Object[] {1, "MyPartTable", 1, 1, true,
                "description", "1 2", "", "1"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_PARTITION_TABLES, getAllPartitionatable);

        MockResultSet getcolumninfoRSptble = preparedstatementHandler
                .createResultSet();
        getcolumninfoRSptble.addColumn("tableid");
        getcolumninfoRSptble.addColumn("namespaceid");
        getcolumninfoRSptble.addColumn("columnidx");
        getcolumninfoRSptble.addColumn("name");
        getcolumninfoRSptble.addColumn("datatypeoid");
        getcolumninfoRSptble.addColumn("dtns");
        getcolumninfoRSptble.addColumn("length");
        getcolumninfoRSptble.addColumn("precision");
        getcolumninfoRSptble.addColumn("dimentions");
        getcolumninfoRSptble.addColumn("notnull");
        getcolumninfoRSptble.addColumn("isdefaultvalueavailable");
        getcolumninfoRSptble.addColumn("default_value");
        getcolumninfoRSptble.addColumn("attDefStr");
        getcolumninfoRSptble.addColumn("displayColumns");
        getcolumninfoRSptble.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200,
                0, 0, false, true, "Default value", "attrString", "bigint"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_COLUMN_INFO_PARTITION_TABLES,
                getcolumninfoRSptble);

        MockResultSet getPartitionsRS = preparedstatementHandler
                .createResultSet();
        getPartitionsRS.addColumn("partition_id");
        getPartitionsRS.addColumn("partition_name");
        getPartitionsRS.addColumn("table_id");

        getPartitionsRS.addRow(new Object[] {1, "part_1", 1});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.GET_PARTITIONS,
                getPartitionsRS);

        /*
         * MockResultSet getPartitionsRS1 = preparedstatementHandler
         * .createResultSet(); getPartitionsRS.addColumn("partition_id");
         * getPartitionsRS.addColumn("partition_name");
         * getPartitionsRS.addColumn("table_id");
         * 
         * getPartitionsRS.addRow(new Object[] {1, "part_1", 1});
         * preparedstatementHandler.prepareResultSet(
         * CommonLLTUtils.GET_PARTITIONS_OVERLOADED, getPartitionsRS);
         */

        MockResultSet indexpartitionRS = preparedstatementHandler
                .createResultSet();
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
        indexRS.addColumn("indexdef");
        indexRS.addColumn("tablespace");
        indexRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_PARTITION_INDEXES, indexpartitionRS);

        MockResultSet partitionConstraintRS = preparedstatementHandler
                .createResultSet();
        partitionConstraintRS.addColumn("constraintid");
        partitionConstraintRS.addColumn("tableid");
        partitionConstraintRS.addColumn("namespaceid");
        partitionConstraintRS.addColumn("constraintname");
        partitionConstraintRS.addColumn("constrainttype");
        partitionConstraintRS.addColumn("deferrable");
        partitionConstraintRS.addColumn("deferred");
        partitionConstraintRS.addColumn("validate");
        partitionConstraintRS.addColumn("indexid");
        partitionConstraintRS.addColumn("fkeytableId");
        partitionConstraintRS.addColumn("updatetype");
        partitionConstraintRS.addColumn("deletetype");
        partitionConstraintRS.addColumn("matchtype");
        partitionConstraintRS.addColumn("expr");
        partitionConstraintRS.addColumn("columnlist");
        partitionConstraintRS.addColumn("fkeycolumnlist");
        partitionConstraintRS.addColumn("const_def");
        partitionConstraintRS.addRow(new Object[] {1, 1, 1, "ConstraintName",
                "ConstraintType", false, false, false, 1, 1, "", "", "", "",
                "{1}", "1", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_PARTITION_CONSTRAINTS,
                partitionConstraintRS);

        MockResultSet refreshDatatypeRS = preparedstatementHandler
                .createResultSet();
        /*
         * refreshDatatypeRS.addColumn("oid");
         * refreshDatatypeRS.addColumn("typname");
         * refreshDatatypeRS.addColumn("typnamespace");
         * refreshDatatypeRS.addColumn("typbyval");
         * refreshDatatypeRS.addColumn("displaycolumns");
         * refreshDatatypeRS.addColumn("typarray");
         * refreshDatatypeRS.addColumn("typcategory");
         * refreshDatatypeRS.addColumn("typtype");
         * refreshDatatypeRS.addColumn("typlen");
         * refreshDatatypeRS.addColumn("typnotnull");
         * refreshDatatypeRS.addColumn("typtypmod");
         * refreshDatatypeRS.addColumn("desc");
         */

        refreshDatatypeRS.addColumn("oid");
        refreshDatatypeRS.addColumn("typname");
        refreshDatatypeRS.addColumn("typnamespace");
        refreshDatatypeRS.addColumn("typlen");
        refreshDatatypeRS.addColumn("displaycolumns");
        refreshDatatypeRS.addColumn("typbyval");
        refreshDatatypeRS.addColumn("typtype");
        refreshDatatypeRS.addColumn("typcategory");
        refreshDatatypeRS.addColumn("typtypmod");
        refreshDatatypeRS.addColumn("typnotnull");
        refreshDatatypeRS.addColumn("typarray");
        refreshDatatypeRS.addColumn("desc");
        refreshDatatypeRS.addRow(
                new Object[] {1, "bigint", 1, 1, "character", true, "type",
                        "category", 1, true, 12, "description", "bigint"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_REFRESH_DATATYPE, refreshDatatypeRS);

        MockResultSet serverEncodingRs = preparedstatementHandler.createResultSet();
        serverEncodingRs.addColumn("server_encoding");
        serverEncodingRs.addRow(new Object[] {"UTF-8"});
        preparedstatementHandler.prepareResultSet("show server_encoding", serverEncodingRs);
    }

    public static void prepareProxyInfoForRs(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet allIndexes_rs_1 = preparedstatementHandler
                .createResultSet();
        allIndexes_rs_1.addColumn("oid");
        allIndexes_rs_1.addColumn("tableId");
        allIndexes_rs_1.addColumn("indexname");
        allIndexes_rs_1.addColumn("namespaceid");
        allIndexes_rs_1.addColumn("accessmethodid");
        allIndexes_rs_1.addColumn("isunique");
        allIndexes_rs_1.addColumn("isprimary");
        allIndexes_rs_1.addColumn("isexclusion");
        allIndexes_rs_1.addColumn("isimmediate");
        allIndexes_rs_1.addColumn("isclustered");
        allIndexes_rs_1.addColumn("checkmin");
        allIndexes_rs_1.addColumn("isready");
        allIndexes_rs_1.addColumn("cols");
        allIndexes_rs_1.addColumn("reloptions");
        allIndexes_rs_1.addColumn("indexdef");

        allIndexes_rs_1.addRow(new Object[] {1, 1, "IndexName", 1, 1, true,
                false, false, false, false, false, false, "0", "", ""});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.ALLCONSTRAIN_2,
                allIndexes_rs_1);
    }

    public static void prepareProxyInfoForDB(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet datatypeRS_1 = preparedstatementHandler.createResultSet();
        datatypeRS_1.addColumn("oid");
        datatypeRS_1.addColumn("typname");
        datatypeRS_1.addColumn("typnamespace");
        datatypeRS_1.addColumn("typlen");
        datatypeRS_1.addColumn("typbyval");
        datatypeRS_1.addColumn("typtype");
        datatypeRS_1.addColumn("typcategory");
        datatypeRS_1.addColumn("typtypmod");
        datatypeRS_1.addColumn("typnotnull");
        datatypeRS_1.addColumn("typarray");
        datatypeRS_1.addColumn("desc");
        datatypeRS_1.addColumn("displaycolumns");

        datatypeRS_1.addRow(new Object[] {1, "bpchar", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {2, "int8", 1, 1, true, "type",
                "category", 1, true, 12, "description2", ""});

        datatypeRS_1.addRow(new Object[] {3, "bit", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {4, "varbit", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {5, "bool", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {6, "box", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {7, "bytea", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {8, "varchar", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {9, "char", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {10, "cidr", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {11, "circle", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {12, "date", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});

        datatypeRS_1.addRow(new Object[] {13, "float8", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {14, "inet", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {15, "int4", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {16, "interval", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {17, "line", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {18, "lseg", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {19, "macaddr", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {20, "money", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {21, "numeric", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {22, "numeric", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {23, "path", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {24, "point", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {25, "polygon", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {26, "float4", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {27, "int2", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});

        datatypeRS_1.addRow(new Object[] {28, "text", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {29, "time", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {30, "timetz", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {31, "timestamp", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {32, "timestamptz", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {33, "tsquery", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {34, "tsvector", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {35, "txid_snapshot", 1, 1, true,
                "type", "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {36, "uuid", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});
        datatypeRS_1.addRow(new Object[] {37, "xml", 1, 1, true, "type",
                "category", 1, true, 12, "description", ""});

        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_DATATYPE_DB, datatypeRS_1);
    }

    public static void getAllNodes(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getnoders = preparedstatementHandler.createResultSet();
        getnoders.addColumn("oid");
        getnoders.addColumn("node_name");
        getnoders.addColumn("node_type");
        getnoders.addColumn("node_port");
        getnoders.addColumn("node_host");
        getnoders.addColumn("nodeis_primary");
        getnoders.addColumn("nodeis_preferred");
        getnoders.addColumn("node_id");

        getnoders.addRow(new Object[] {1, "Node_1", "C", 23456, "1.0.0.0", true,
                true, 12});
        preparedstatementHandler.prepareResultSet(
                "select oid, node_name, node_type, node_port, node_host, nodeis_primary, nodeis_preferred, node_id from pgxc_node;",
                getnoders);
    }

    /*
     * public static void getAllDatatypeExpection(
     * PreparedStatementResultSetHandler preparedstatementHandler) {
     * MockResultSet datatypeRS = preparedstatementHandler.createResultSet();
     * datatypeRS.addColumn("oid"); datatypeRS.addColumn("typname");
     * datatypeRS.addColumn("typnamespace"); datatypeRS.addColumn("typlen");
     * datatypeRS.addColumn("typbyval"); datatypeRS.addColumn("typtype");
     * datatypeRS.addColumn("typcategory"); datatypeRS.addColumn("typtypmod");
     * datatypeRS.addColumn("typnotnull"); datatypeRS.addColumn("typarray");
     * datatypeRS.addColumn("desc"); datatypeRS.addColumn("displaycolumns"); //
     * datatypeRS.addRow(new Object[]{1, "bigint", 1, 1, true, //
     * "type","category", 1, true, 12, "description"}); datatypeRS.addRow(new
     * Object[] {2, "int8", 1, 1, true, "type", "category", 1, true, 12,
     * "description2"});
     * 
     * datatypeRS.addRow(new Object[] {3, "bit", 1, 1, true, "type", "category",
     * 1, true, 12, "description"}); datatypeRS.addRow(new Object[] {4,
     * "varbit", 1, 1, true, "type", "category", 1, true, 12, "description"});
     * datatypeRS.addRow(new Object[] {5, "bool", 1, 1, true, "type",
     * "category", 1, true, 12, "description"}); datatypeRS.addRow(new Object[]
     * {6, "box", 1, 1, true, "type", "category", 1, true, 12, "description"});
     * datatypeRS.addRow(new Object[] {7, "bytea", 1, 1, true, "type",
     * "category", 1, true, 12, "description"}); datatypeRS.addRow(new Object[]
     * {8, "varchar", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {9, "char", 1, 1, true,
     * "type", "category", 1, true, 12, "description"}); datatypeRS.addRow(new
     * Object[] {10, "cidr", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {11, "circle", 1, 1,
     * true, "type", "category", 1, true, 12, "description"});
     * datatypeRS.addRow(new Object[] {12, "date", 1, 1, true, "type",
     * "category", 1, true, 12, "description"});
     * 
     * datatypeRS.addRow(new Object[] {13, "float8", 1, 1, true, "type",
     * "category", 1, true, 12, "description"}); datatypeRS.addRow(new Object[]
     * {14, "inet", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {15, "int4", 1, 1, true,
     * "type", "category", 1, true, 12, "description"}); datatypeRS.addRow(new
     * Object[] {16, "interval", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {17, "line", 1, 1, true,
     * "type", "category", 1, true, 12, "description"}); datatypeRS.addRow(new
     * Object[] {18, "lseg", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {19, "macaddr", 1, 1,
     * true, "type", "category", 1, true, 12, "description"});
     * datatypeRS.addRow(new Object[] {20, "money", 1, 1, true, "type",
     * "category", 1, true, 12, "description"}); datatypeRS.addRow(new Object[]
     * {21, "numeric", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {22, "numeric", 1, 1,
     * true, "type", "category", 1, true, 12, "description"});
     * datatypeRS.addRow(new Object[] {23, "path", 1, 1, true, "type",
     * "category", 1, true, 12, "description"}); datatypeRS.addRow(new Object[]
     * {24, "point", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {25, "polygon", 1, 1,
     * true, "type", "category", 1, true, 12, "description"});
     * datatypeRS.addRow(new Object[] {26, "float4", 1, 1, true, "type",
     * "category", 1, true, 12, "description"}); datatypeRS.addRow(new Object[]
     * {27, "int2", 1, 1, true, "type", "category", 1, true, 12,
     * "description"});
     * 
     * datatypeRS.addRow(new Object[] {28, "text", 1, 1, true, "type",
     * "category", 1, true, 12, "description"}); datatypeRS.addRow(new Object[]
     * {29, "time", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {30, "timetz", 1, 1,
     * true, "type", "category", 1, true, 12, "description"});
     * datatypeRS.addRow(new Object[] {31, "timestamp", 1, 1, true, "type",
     * "category", 1, true, 12, "description"}); datatypeRS.addRow(new Object[]
     * {32, "timestamptz", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {33, "tsquery", 1, 1,
     * true, "type", "category", 1, true, 12, "description"});
     * datatypeRS.addRow(new Object[] {34, "tsvector", 1, 1, true, "type",
     * "category", 1, true, 12, "description"}); datatypeRS.addRow(new Object[]
     * {35, "txid_snapshot", 1, 1, true, "type", "category", 1, true, 12,
     * "description"}); datatypeRS.addRow(new Object[] {36, "uuid", 1, 1, true,
     * "type", "category", 1, true, 12, "description"}); datatypeRS.addRow(new
     * Object[] {37, "xml", 1, 1, true, "type", "category", 1, true, 12,
     * "description"});
     * 
     * preparedstatementHandler
     * .prepareResultSet(CommonLLTUtils.GET_ALL_DATATYPE, datatypeRS); }
     */

    public static void createDataBaseRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("datname");

        getdbsrs.addRow(new Object[] {1, "Gauss"});
        getdbsrs.addRow(new Object[] {2, "tempdb"});

        preparedstatementHandler.prepareResultSet(
                "select oid, datname from pg_database where datistemplate='f'",
                getdbsrs);
    }

    public static void updateDataBaseRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("datname");
        getdbsrs.addRow(new Object[] {"tempdb2"});
        preparedstatementHandler.prepareResultSet(
                "SELECT datname FROM PG_DATABASE WHERE OID=2", getdbsrs);

    }

    public static void dropDataBaseRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("datname");

        getdbsrs.addRow(new Object[] {1, "Gauss"});

        preparedstatementHandler.prepareResultSet(
                "select oid, datname from pg_database where datistemplate='f'",
                getdbsrs);
    }

    public static void getDataBasePtropertiesRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("name");
        getdbsrs.addColumn("encoding");
        getdbsrs.addColumn("allow_conn");
        getdbsrs.addColumn("max_conn_limit");
        getdbsrs.addColumn("default_tablespace");
        getdbsrs.addColumn("collation");
        getdbsrs.addColumn("char_type");

        getdbsrs.addRow(new Object[] {1, "Gauss", "UTF-8", "Yes", "100",
                "Defauly Tablespace", "Collation", "Charset..."});

        preparedstatementHandler.prepareResultSet(
                "SELECT  oid as oid, datname AS name, pg_encoding_to_char(encoding) as encoding, datallowconn as allow_conn, "
                        + "datconnlimit as max_conn_limit, (select spcname from pg_tablespace where oid=dattablespace) as  default_tablespace, datcollate as collation, datctype as char_type from pg_database where oid = 1;",
                getdbsrs);
    }

    public static void getDataBasePtropertiesemptyRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("name");
        getdbsrs.addColumn("encoding");
        getdbsrs.addColumn("allow_conn");
        getdbsrs.addColumn("max_conn_limit");
        getdbsrs.addColumn("default_tablespace");
        getdbsrs.addColumn("collation");
        getdbsrs.addColumn("char_type");

        getdbsrs.addRow(new Object[] {});

        preparedstatementHandler.prepareResultSet(
                "SELECT  oid as oid, datname AS name, pg_encoding_to_char(encoding) as encoding, datallowconn as allow_conn, "
                        + "datconnlimit as max_conn_limit, (select spcname from pg_tablespace where oid=dattablespace) as  default_tablespace, datcollate as collation, datctype as char_type from pg_database where oid = 23;",
                getdbsrs);
    }

    public static void refreshDebugObjectRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, "
                + "pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, "
                + "pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, "
                + "pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows"
                + " FROM pg_proc pr WHERE oid = 2";

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
        listnerResultSet.addColumn("lang");
      //Dependency on BL
        /*ObjectParameter retParam = new ObjectParameter();
        retParam.setDatatype("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);*/

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(16), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10, "plpgsql"});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

    }

    public static void refreshDebugObjectRS2(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, "
                + "pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, "
                + "pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, "
                + "pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, "
                + "lng.lanname lang "
                + "FROM pg_proc pr JOIN pg_language lng ON lng.oid=pr.prolang WHERE lng.lanname in ('plpgsql','sql') and oid = 1";

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
        listnerResultSet.addColumn("lang");
        //Dependency on BL
        /*ObjectParameter retParam = new ObjectParameter();
        retParam.setDatatype("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);*/

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(2279), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10, "plpgsql"});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

    }

    public static void refreshDebugObjectRS3(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {

        String REFRESH_DBG_OBJ_QRY = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_language lng ON lng.oid=pr.prolang WHERE lng.lanname in ('plpgsql','sql') and oid = 1";
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
        listnerResultSet.addColumn("lang");
      //Dependency on BL
        /*ObjectParameter retParam = new ObjectParameter();
        retParam.setDatatype("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);*/

        listnerResultSet.addRow(new Object[] {new Integer(2), null, 1,
                new Integer(2279), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10, "plpgsql"});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

    }

    public static void fetchNamespaceRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "SELECT oid, nspname from pg_namespace WHERE oid=1";

        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();

        listnerResultSet.addColumn("oid");
        listnerResultSet.addColumn("nspname");

        listnerResultSet.addRow(new Object[] {new Integer(1), "PUBLIC"});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

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

        colmetadataRS.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(colmetadata, colmetadataRS);

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
                "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes "
                        + "from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) "
                        + "left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.relnamespace = "
                        + 1 + ';',
                tablemetadataRS);

    }

    public static void runOnSQLTerminalRS(
            PreparedStatementResultSetHandler statementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "select * from tbl1";

        MockResultSet listnerResultSet = statementHandler.createResultSet();

        listnerResultSet.addColumn("id");
        listnerResultSet.addColumn("name");

        listnerResultSet.addRow(new Object[] {new Integer(1), "PUBLIC"});
        listnerResultSet.addRow(new Object[] {new Integer(2), null});

        statementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

    }

    public static void renameRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "";

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

        colmetadataRS.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                colmetadataRS);

    }

    public static void dropRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "";

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

        colmetadataRS.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                colmetadataRS);

    }

    public static void createTableRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
                + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = "
                + 1 + " order by t.oid, c.attnum;";

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

        colmetadataRS.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                colmetadataRS);

    }

    public static void createTableSpaceRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {

        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("oid");
        colmetadataRS.addColumn("spcname");
        colmetadataRS.addColumn("spcoptions");
        colmetadataRS.addColumn("location");
        colmetadataRS.addColumn("spcmaxsize");

        colmetadataRS.addRow(new Object[] {1, "tblspc",
                new String[] {"spcoptions=options"}, "location", "spcmaxsize"});
        preparedstatementHandler.prepareResultSet(TBL_SPC_META, colmetadataRS);

    }

    public static void createTableRS1(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
                new Object[] {1, "MyTable", 12, 1, "t", "description", "1 2"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.REFRESH_TABLE_METADATA, tablemetadataRS);

    }

    public static void createTableRS2(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
                new Object[] {1, "MyTable", 1, 1, "t", "description", "1 2"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.REFRESH_TABLE_METADATA, tablemetadataRS);

    }

    public static void createTableRS3(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
                new Object[] {1, "MyTable", 1, 1, "u", "description", "1 2"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.REFRESH_TABLE_METADATA, tablemetadataRS);

    }

    public static void createSequence(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet tablemetadataRS = preparedstatementHandler
                .createResultSet();
        tablemetadataRS.addColumn("SEQUENCE");
        tablemetadataRS.addColumn("INCREMENT BY");
        tablemetadataRS.addColumn("MINVALUE");
        tablemetadataRS.addColumn("MAXVALUE");
        tablemetadataRS.addColumn("START WITH");
        tablemetadataRS.addColumn("CACHE");
        tablemetadataRS.addColumn("CYCLE");
        // tablemetadataRS.addColumn("OWNED BY");

        tablemetadataRS.addRow(new Object[] {"pg_catalog.Seq_01", 2, 10, 10000,
                10, 1, "CYCLE"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.REFRESH_SEQUENCE_METADATA, tablemetadataRS);

    }

    public static void createSequenceWithOwner(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet tablemetadataRS = preparedstatementHandler
                .createResultSet();
        tablemetadataRS.addColumn("SEQUENCE");
        tablemetadataRS.addColumn("INCREMENT BY");
        tablemetadataRS.addColumn("MINVALUE");
        tablemetadataRS.addColumn("MAXVALUE");
        tablemetadataRS.addColumn("START WITH");
        tablemetadataRS.addColumn("CACHE");
        tablemetadataRS.addColumn("CYCLE");
        tablemetadataRS.addColumn("OWNED BY");

        tablemetadataRS.addRow(new Object[] {"pg_catalog.Seq_01", 2, 10, 10000,
                10, 1, "CYCLE", "pg_catalog.table1.col1"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.REFRESH_SEQUENCE_METADATA, tablemetadataRS);

    }

    public static void fetchColMetadataForTableRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        /*
         * String REFRESH_DBG_OBJ_QRY =
         * "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
         * +
         * "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
         * +
         * "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
         * +
         * "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
         * +
         * "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
         * + "where c.attisdropped = 'f' and c.attnum > 0 and t.oid = " + 1 +
         * " order by t.oid, c.attnum;";
         */
        String REFRESH_DBG_OBJ_QRY = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 1 order by t.oid, c.attnum;";

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

        colmetadataRS.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 200, 0, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                colmetadataRS);

    }

    public static void getCountRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "SELECT count(*) as cnt FROM \"pg_catalog\".\"MyTable\";";

        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("cnt");

        colmetadataRS.addRow(new Object[] {10});
        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                colmetadataRS);

    }

    public static void getTablePtropertiesRS(
            PreparedStatementResultSetHandler preparedstatementHandler,
            String tableType)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("relpersistence");
        getdbsrs.addColumn("owner");
        getdbsrs.addColumn("pages");
        getdbsrs.addColumn("rows_count");
        getdbsrs.addColumn("has_index");
        getdbsrs.addColumn("is_shared");
        getdbsrs.addColumn("check_count");
        getdbsrs.addColumn("has_pkey");
        getdbsrs.addColumn("has_rules");
        getdbsrs.addColumn("has_triggers");
        getdbsrs.addColumn("options");
        getdbsrs.addColumn("hashoid");
        getdbsrs.addColumn("tbl_desc");
        getdbsrs.addColumn("spcname");
        getdbsrs.addRow(new Object[] {tableType, "GaussMPPDB", "12", "1200",
                false, false, false, true, true, false, "1 2 3", true,
                "Tbl Description", "tblspc_prop"});
        getdbsrs.addRow(new Object[]{tableType,"GaussMppDB","12","1200",false, false, false, true, true, false,"orientation= row,compression= no",true,"Tbl Description","tblspc_prop_1" });

        preparedstatementHandler.prepareResultSet(
                "SELECT tbl.relpersistence as relpersistence, case when tbl.reltablespace = 0 then 'DEFAULT' else tblsp.spcname end, auth.rolname as owner, tbl.relpages pages, tbl.reltuples as rows_count, tbl.relhasindex as has_index, tbl.relisshared as is_shared, tbl.relchecks as check_count, tbl.relhaspkey as has_pkey, tbl.relhasrules as has_rules, tbl.relhastriggers as has_triggers, array_to_string(tbl.reloptions, ',') as options,tbl.relhasoids as hashoid, d.description as tbl_desc FROM pg_class tbl LEFT JOIN pg_roles auth on (tbl.relowner = auth.oid) left join pg_description d on (tbl.oid = d.objoid) LEFT JOIN pg_tablespace tblsp ON (tbl.reltablespace = tblsp.oid) WHERE tbl.oid = 1;",
                getdbsrs);
    }
    
    public static void getTablePtropertiesRS_forOptions(
            PreparedStatementResultSetHandler preparedstatementHandler,
            String tableType)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("relpersistence");
        getdbsrs.addColumn("owner");
        getdbsrs.addColumn("pages");
        getdbsrs.addColumn("rows_count");
        getdbsrs.addColumn("has_index");
        getdbsrs.addColumn("is_shared");
        getdbsrs.addColumn("check_count");
        getdbsrs.addColumn("has_pkey");
        getdbsrs.addColumn("has_rules");
        getdbsrs.addColumn("has_triggers");
        getdbsrs.addColumn("options");
        getdbsrs.addColumn("hashoid");
        getdbsrs.addColumn("tbl_desc");
        getdbsrs.addColumn("spcname");
        getdbsrs.addRow(new Object[]{tableType,"GaussMppDB","12","1200",false, false, false, true, true, false,"orientation= row,compression= no,fillfactor=12,MAX_Batchrow=10,Partial_cluster_rows=1,version=1.2",false,"Tbl Description","tblspc_prop_1" });

        preparedstatementHandler.prepareResultSet(
                "SELECT tbl.relpersistence as relpersistence, case when tbl.reltablespace = 0 then 'DEFAULT' else tblsp.spcname end, auth.rolname as owner, tbl.relpages pages, tbl.reltuples as rows_count, tbl.relhasindex as has_index, tbl.relisshared as is_shared, tbl.relchecks as check_count, tbl.relhaspkey as has_pkey, tbl.relhasrules as has_rules, tbl.relhastriggers as has_triggers, array_to_string(tbl.reloptions, ',') as options,tbl.relhasoids as hashoid, d.description as tbl_desc FROM pg_class tbl LEFT JOIN pg_roles auth on (tbl.relowner = auth.oid) left join pg_description d on (tbl.oid = d.objoid) LEFT JOIN pg_tablespace tblsp ON (tbl.reltablespace = tblsp.oid) WHERE tbl.oid = 1;",
                getdbsrs);
    }
    
    public static void getTablePtropertiesRS_TableDoesNotExist(
            PreparedStatementResultSetHandler preparedstatementHandler,
            String tableType)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("relpersistence");
        getdbsrs.addColumn("owner");
        getdbsrs.addColumn("pages");
        getdbsrs.addColumn("rows_count");
        getdbsrs.addColumn("has_index");
        getdbsrs.addColumn("is_shared");
        getdbsrs.addColumn("check_count");
        getdbsrs.addColumn("has_pkey");
        getdbsrs.addColumn("has_rules");
        getdbsrs.addColumn("has_triggers");
        getdbsrs.addColumn("options");
        getdbsrs.addColumn("hashoid");
        getdbsrs.addColumn("tbl_desc");
        getdbsrs.addColumn("spcname");

        preparedstatementHandler.prepareResultSet(
                "SELECT tbl.relpersistence as relpersistence, case when tbl.reltablespace = 0 then 'DEFAULT' else tblsp.spcname end, auth.rolname as owner, tbl.relpages pages, tbl.reltuples as rows_count, tbl.relhasindex as has_index, tbl.relisshared as is_shared, tbl.relchecks as check_count, tbl.relhaspkey as has_pkey, tbl.relhasrules as has_rules, tbl.relhastriggers as has_triggers, array_to_string(tbl.reloptions, ',') as options,tbl.relhasoids as hashoid, d.description as tbl_desc FROM pg_class tbl LEFT JOIN pg_roles auth on (tbl.relowner = auth.oid) left join pg_description d on (tbl.oid = d.objoid) LEFT JOIN pg_tablespace tblsp ON (tbl.reltablespace = tblsp.oid) WHERE tbl.oid = 1;",
                getdbsrs);
    }


    public static void getTablePtropertiesemptyRS(
            PreparedStatementResultSetHandler preparedstatementHandler,
            String tableType)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("relpersistence");
        getdbsrs.addColumn("owner");
        getdbsrs.addColumn("pages");
        getdbsrs.addColumn("rows_count");
        getdbsrs.addColumn("has_index");
        getdbsrs.addColumn("is_shared");
        getdbsrs.addColumn("check_count");
        getdbsrs.addColumn("has_pkey");
        getdbsrs.addColumn("has_rules");
        getdbsrs.addColumn("has_triggers");
        getdbsrs.addColumn("options");
        getdbsrs.addColumn("hashoid");
        getdbsrs.addColumn("tbl_desc");

        getdbsrs.addRow(new Object[] {});

        preparedstatementHandler.prepareResultSet(
                "SELECT tbl.relpersistence as relpersistence, auth.rolname as owner, tbl.relpages pages, "
                        + "tbl.reltuples as rows_count, tbl.relhasindex as has_index, tbl.relisshared as is_shared, "
                        + "tbl.relchecks as check_count, tbl.relhaspkey as has_pkey, tbl.relhasrules as has_rules, "
                        + "tbl.relhastriggers as has_triggers, array_to_string(tbl.reloptions, ',') as options,tbl.relhasoids as hashoid, d.description as tbl_desc "
                        + "FROM pg_class tbl LEFT JOIN pg_roles auth on "
                        + "(tbl.relowner = auth.oid) left join "
                        + "pg_description d on (tbl.oid = d.objoid) WHERE tbl.oid = "
                        + 2 + " ;",
                getdbsrs);
    }

    public static void getTablePtropertiesRS_01(
            PreparedStatementResultSetHandler preparedstatementHandler,
            String tableType)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("relpersistence");
        getdbsrs.addColumn("owner");
        getdbsrs.addColumn("pages");
        getdbsrs.addColumn("rows_count");
        getdbsrs.addColumn("has_index");
        getdbsrs.addColumn("is_shared");
        getdbsrs.addColumn("check_count");
        getdbsrs.addColumn("has_pkey");
        getdbsrs.addColumn("has_rules");
        getdbsrs.addColumn("has_triggers");
        getdbsrs.addColumn("options");
        getdbsrs.addColumn("hashoid");
        getdbsrs.addColumn("tbl_desc");

        getdbsrs.addRow(new Object[] {tableType, "GaussMPPDB", "12", "1200",
                false, false, false, true, true, false, "1=2 3", false,
                "Tbl Description"});

        preparedstatementHandler.prepareResultSet(

                "SELECT tbl.relpersistence as relpersistence, auth.rolname as owner, tbl.relpages pages, tbl.reltuples as rows_count, tbl.relhasindex as has_index, tbl.relisshared as is_shared, tbl.relchecks as check_count, tbl.relhaspkey as has_pkey, tbl.relhasrules as has_rules, tbl.relhastriggers as has_triggers, array_to_string(tbl.reloptions, ',') as options,tbl.relhasoids as hashoid, d.description as tbl_desc FROM pg_class tbl LEFT JOIN pg_roles auth on (tbl.relowner = auth.oid) left join pg_description d on (tbl.oid = d.objoid) WHERE tbl.oid = 1;",
                getdbsrs);
    }

    public static void selectFromTableRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "SELECT * FROM \"pg_catalog\".\"MyTable\" LIMIT 1000 OFFSET 0;";

        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("cnt");

        colmetadataRS.addRow(new Object[] {10});
        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                colmetadataRS);

    }

    public static void getPropertiesConstraint(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet propconstraintRS = preparedstatementHandler
                .createResultSet();
        propconstraintRS.addColumn("constraintid");
        propconstraintRS.addColumn("tableid");
        propconstraintRS.addColumn("namespaceid");
        propconstraintRS.addColumn("constraintname");
        propconstraintRS.addColumn("constrainttype");
        propconstraintRS.addColumn("deferrable");
        propconstraintRS.addColumn("deferred");
        propconstraintRS.addColumn("validate");
        propconstraintRS.addColumn("indexid");
        propconstraintRS.addColumn("fkeytableId");
        propconstraintRS.addColumn("updatetype");
        propconstraintRS.addColumn("deletetype");
        propconstraintRS.addColumn("matchtype");
        propconstraintRS.addColumn("expr");
        propconstraintRS.addColumn("columnlist");
        propconstraintRS.addColumn("fkeycolumnlist");
        propconstraintRS.addColumn("const_def");
        propconstraintRS.addColumn("tablespace");
        propconstraintRS.addRow(new Object[] {1, 1, 1, "ConstraintName",
                "ConstraintType", false, false, false, 1, 1, "", "", "", "",
                "1", "1", "", "constraint_tablespace"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_CONSTRAINTS, propconstraintRS);
    }

    public static void getConstaraintForTableRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred as deferred, c.convalidated as validate, c.conindid as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c where c.conrelid = 1;",
                constraintRS);
        /*
         * preparedstatementHandler.prepareResultSet(
         * "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, "
         * +
         * "c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, "
         * +
         * "c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, "
         * +
         * "c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, "
         * +
         * "c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, "
         * + "pg_get_constraintdef(c.oid) as const_def " +
         * "FROM pg_constraint c where  c.connamespace= " + 1 + ';',
         * constraintRS);
         */
    }

    public static void getConstaraintForTableRSEx(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=1 and cl.parttype not in ('p','v') and c.conrelid <> 0;",
                constraintRS);

    }
    public static void getConstaraintForTableRSExAutoSuggest(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=1 and cl.parttype not in ('p','v') and c.conrelid <> 0;",
                constraintRS);
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=2 and cl.parttype not in ('p','v') and c.conrelid <> 0;",
                constraintRS);
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=3 and cl.parttype not in ('p','v') and c.conrelid <> 0;",
                constraintRS);
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=4 and cl.parttype not in ('p','v') and c.conrelid <> 0;",
                constraintRS);
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=5 and cl.parttype not in ('p','v') and c.conrelid <> 0;",
                constraintRS);
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=6 and cl.parttype not in ('p','v') and c.conrelid <> 0;",
                constraintRS);

    }

    public static void getConstaraintForTableRSEx2(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=1 and cl.parttype not in ('p','v') and c.conrelid <> 0;",
                constraintRS);

    }

    public static void getIndexForTableRS1(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
        indexRS.addColumn("indexdef");

        indexRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", ""});
        preparedstatementHandler.prepareResultSet(
                "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam  as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate  as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions ,def.indexdef FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind = 'r' and t.oid = 1",
                indexRS);

    }

    public static void getIndexForTableRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String qry = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef , def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind = 'r' and t.oid = 1;";
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
        indexRS.addColumn("indexdef");
        indexRS.addColumn("tablespace");

        indexRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler.prepareResultSet(qry, indexRS);

    }

    public static void refreshDbgObj(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "select a.headerlines, a.definition, b.xmin, b.cmin from pg_proc b left join "
                + "(select * from PG_GET_FUNCTIONDEF(" + 1
                + ")) a on (1) where b.oid=" + 1 + ';';

        StringBuilder strSourcecode = new StringBuilder();

        strSourcecode.append("\"Declare").append("\nc INT = 6;")
                .append("\nd INT;BEGIN");
        strSourcecode.append("\nc := c+1;")
                .append("\nc := perform nestedfunc()");
        strSourcecode.append("\nc := c+1;").append("\nc := c+1;");
        strSourcecode.append("\nc := c+1;").append("\nc := c+1;")
                .append("\nc := 100;");
        strSourcecode.append("\nd := c + 200;").append("\nreturn d;")
                .append("\nend;\")");

        MockResultSet indexRS = preparedstatementHandler.createResultSet();
        indexRS.addRow(new Object[] {4, strSourcecode.toString(), 1, 1});

        preparedstatementHandler.prepareResultSet(query, indexRS);
    }

    public static void refreshDbgObj1(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "select a.headerlines, a.definition, b.xmin, b.cmin from pg_proc b left join "
                + "(select * from PG_GET_FUNCTIONDEF(" + 1
                + ")) a on (1) where b.oid=" + 1;

        StringBuilder strSourcecode = new StringBuilder();

        strSourcecode.append("(\"Declare").append("\nc INT = 6;")
                .append("\nd INT;BEGIN");
        strSourcecode.append("\nc := c+1;")
                .append("\nc := perform nestedfunc()");
        strSourcecode.append("\nc := c+1;").append("\nc := c+1;");
        strSourcecode.append("\nc := c+1;").append("\nc := c+1;")
                .append("\nc := 100;");
        strSourcecode.append("\nd := c + 200;").append("\nreturn d;")
                .append("\nend;\")");

        MockResultSet indexRS = preparedstatementHandler.createResultSet();
        indexRS.addRow(new Object[] {4, strSourcecode.toString(), 1, 1});

        preparedstatementHandler.prepareResultSet(query, indexRS);
    }

    public static void executeExecutor(
            PreparedStatementResultSetHandler statementHandler)
    {
        String query = "SELECT pg_catalog.function2(1, 1)";

        MockResultSet indexRS = statementHandler.createResultSet();
        indexRS.addColumn("col1");
        indexRS.addRow(new Object[] {1});

        statementHandler.prepareResultSet(query, indexRS);
    }

    public static void getColMetaDataPreScale(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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

        colmetadataRS.addRow(new Object[] {1, 1, 1, "ColName", 1, 1, 0, 1, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(colmetadata, colmetadataRS);
    }

    public static void getColMetaDataPreScale2(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
    }

    public static void getIndexcCOnstaByNamespace(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
        indexRS.addColumn("indexdef");
        indexRS.addColumn("tablespace");
        indexRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true, false,
                false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_INDEXES_BYNAMESPACE, indexRS);

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
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_CONSTRAINTS_BYNAMESPACE, constraintRS);
    }

    public static void getDeadLineInfoRs(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("DEADLINE");
        getdbsrs.addRow(new Object[] {"2Days"});
        preparedstatementHandler.prepareResultSet(
                "select intervaltonum(gs_password_deadline()) as DEADLINE;",
                getdbsrs);

    }

    public static void getDeadLineInfoRsPasswordExpired(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("DEADLINE");
        getdbsrs.addRow(new Object[] {"-1.00"});
        preparedstatementHandler.prepareResultSet(
                "select intervaltonum(gs_password_deadline()) as DEADLINE;",
                getdbsrs);

    }

    public static void getNotifyInfoRs(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("NOTIFYTIME");
        getdbsrs.addRow(new Object[] {2});
        preparedstatementHandler.prepareResultSet(
                "select * from gs_password_notifytime() as NOTIFYTIME;",
                getdbsrs);

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
        getdbsrs.addRow(
                new Object[] {25, 1, "public", "mytestview2", "owner1"});
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                        + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\") and has_table_privilege(c.oid,'SELECT') ",
                getdbsrs);

        addViewColumn(preparedstatementHandler, 2, 1);
        addViewColumn(preparedstatementHandler, 11, 1);
        addViewColumn(preparedstatementHandler, 22, 1);
        addViewColumn1(preparedstatementHandler);

        MockResultSet viewDdlRs = preparedstatementHandler.createResultSet();
        viewDdlRs.addColumn("definition");
        viewDdlRs.addRow(new Object[] {"select * from something"});
        preparedstatementHandler
                .prepareResultSet("SELECT * FROM pg_get_viewdef(2)", viewDdlRs);

        MockResultSet viewDdlRs2 = preparedstatementHandler.createResultSet();
        viewDdlRs2.addColumn("definition");
        viewDdlRs2.addRow(
                new Object[] {"SELECT * FROM t1 WHERE kind = 'Comedy';"});
        preparedstatementHandler.prepareResultSet(
                "SELECT * FROM pg_get_viewdef(25)", viewDdlRs2);

    }

    public static void getViewMockView1(
            PreparedStatementResultSetHandler preparedstatementHandler,
            String schemaname)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("nspoid");
        getdbsrs.addColumn("schemaname");
        getdbsrs.addColumn("viewname");
        getdbsrs.addColumn("viewowner");
        getdbsrs.addRow(new Object[] {11, 10, "public", schemaname, "owner1"});
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                        + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\") and has_table_privilege(c.oid,'SELECT') ",
                getdbsrs);

        addViewColumn(preparedstatementHandler, 2, 10);
        addViewColumn(preparedstatementHandler, 11, 10);
        addViewColumn(preparedstatementHandler, 22, 10);
    }

    public static void getSchemaNameOne(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        namespaceRS.addRow(new Object[] {10, "Schema_One"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_NAMESPACE, namespaceRS);
    }

    public static void getSchemaTwo(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        /* namespaceRS.addRow(new Object[]{10, "Schema_One"}); */
        namespaceRS.addRow(new Object[] {11, "Schema_Two"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_NAMESPACE, namespaceRS);
    }

    public static void addViewColumn(
            PreparedStatementResultSetHandler preparedstatementHandler, int id,
            int nsid)
    {
        String query = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, c.atttypid as datatypeoid, typ.typnamespace as dtns, "
                + "c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, "
                + "d.adsrc as default_value from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') "
                + "left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0  and v.relnamespace = "
                + nsid + " order by v.oid, c.attnum";

        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("viewid");
        getdbsrs.addColumn("namespaceid");
        getdbsrs.addColumn("columnidx");
        getdbsrs.addColumn("name");
        getdbsrs.addColumn("datatypeoid");
        getdbsrs.addColumn("dtns");
        getdbsrs.addColumn("length");
        getdbsrs.addColumn("precision");
        getdbsrs.addColumn("dimentions");
        getdbsrs.addColumn("notnull");
        getdbsrs.addColumn("isdefaultvalueavailable");
        getdbsrs.addColumn("default_value");

        getdbsrs.addRow(new Object[] {id, nsid, 1, "col1", 2, 1, 64, -1, 0, 'f',
                'f', ""});
        getdbsrs.addRow(new Object[] {id, nsid, 2, "col2", 1, 1, 64, -1, 0, 'f',
                'f', ""});

        preparedstatementHandler.prepareResultSet(query, getdbsrs);
    }

    public static void addViewColumn1(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and v.oid = 2 order by v.oid, c.attnum";

        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("viewid");
        getdbsrs.addColumn("namespaceid");
        getdbsrs.addColumn("columnidx");
        getdbsrs.addColumn("name");
        getdbsrs.addColumn("datatypeoid");
        getdbsrs.addColumn("dtns");
        getdbsrs.addColumn("length");
        getdbsrs.addColumn("precision");
        getdbsrs.addColumn("dimentions");
        getdbsrs.addColumn("notnull");
        getdbsrs.addColumn("isdefaultvalueavailable");
        getdbsrs.addColumn("default_value");

        getdbsrs.addRow(
                new Object[] {2, 1, 1, "col1", 2, 1, 64, -1, 0, 'f', 'f', ""});
        getdbsrs.addRow(
                new Object[] {2, 2, 2, "col2", 1, 1, 64, -1, 0, 'f', 'f', ""});

        preparedstatementHandler.prepareResultSet(query, getdbsrs);
    }

    public static void fetchColumnMetaDataRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, "
                + "c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, "
                + "d.adsrc as default_value from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 1 order by t.oid, c.attnum;";

        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();

        listnerResultSet.addColumn("tableid");
        listnerResultSet.addColumn("namespaceid");
        listnerResultSet.addColumn("columnidx");
        listnerResultSet.addColumn("name");
        listnerResultSet.addColumn("datatypeoid");
        listnerResultSet.addColumn("dtns");
        listnerResultSet.addColumn("length");
        listnerResultSet.addColumn("precision");
        listnerResultSet.addColumn("dimentions");
        listnerResultSet.addColumn("notnull");
        listnerResultSet.addColumn("isdefaultvalueavailable");
        listnerResultSet.addColumn("default_value");

        listnerResultSet.addRow(new Object[] {new Integer(1), new Integer(1),
                null, null, null, null, null, null, null, "t", null, null});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

    }

    public static void prepareproxryfordebug(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {

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
        loadFuncTriggerRS.addColumn("lang");
        loadFuncTriggerRS.addRow(new Object[] {1, "auto1", 1, 23, null, 0, null,
                null, null, "f", "v", "f", "f", 100, 0});
        loadFuncTriggerRS.addRow(new Object[] {2, "function2", 1, 2278,
                "{23,23,23}", 2, 23, "{id1,id2,value}", "{i,o,b}", "f", "v",
                "f", "f", 100, 0, "plpgsql"});
        preparedstatementHandler.prepareResultSet(
                "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang WHERE lng.lanname='plpgsql' and pr.pronamespace= 1 ORDER BY objname",
                loadFuncTriggerRS);
    }

    public static void refreshSourceCodeRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "select a.headerlines, a.definition, b.xmin, b.cmin from pg_proc b left join (select * from PG_GET_FUNCTIONDEF(1)) a on (1) where b.oid=1;";

        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();

        listnerResultSet.addColumn("headerlines");
        listnerResultSet.addColumn("definition");
        listnerResultSet.addColumn("xmin");
        listnerResultSet.addColumn("cmin");

        listnerResultSet.addRow(
                new Object[] {new Integer(1), new Integer(1), null, null});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

    }

    public static void prepareProxyInfoForSearchPath(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        MockResultSet searchpath = preparedstatementHandler.createResultSet();
        searchpath.addColumn("search_path");

        searchpath.addRow(
                new Object[] {"PUBLIC,,pg_catalog,schema1,schema2,schema3"});
        preparedstatementHandler.prepareResultSet("SHOW search_path",
                searchpath);
    }

    /*
     * public static void prepareProxyInfoForGetAllNamespaces(
     * PreparedStatementResultSetHandler preparedstatementHandler){
     * 
     * MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
     * namespaceRS.addColumn("oid"); namespaceRS.addColumn("nspname");
     * namespaceRS.addRow(new Object[] {1, "PUBLIC"}); namespaceRS.addRow(new
     * Object[] {1, "pg_catalog"}); namespaceRS.addRow(new Object[] {2,
     * "information_schema"}); preparedstatementHandler.prepareResultSet(
     * CommonLLTUtils.GET_ALL_NAMESPACE, namespaceRS); }
     */

    public static void addViewTableData(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {

        String query = "select row_number() over () as \"*\", * from pg_catalog.\"Mytable\"";

        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("col1");
        getdbsrs.addColumn("col2");
        getdbsrs.addColumn("col3");
        getdbsrs.addColumn("col4");
        getdbsrs.addColumn("col5");

        getdbsrs.addRow(new Object[] {1, "abc", 2, 3, 4});
        getdbsrs.addRow(new Object[] {100, "cde", 101, 102, 103});
        getdbsrs.addRow(new Object[] {200, "egh", 201, 202, 203});
        getdbsrs.addRow(new Object[] {300, "def", 301, 302, 303});

        preparedstatementHandler.prepareResultSet(query, getdbsrs);

    }

    public static void setResultSet(ResultSet resultSet)
    {
        rs = resultSet;
    }

    public static ResultSet getResultSet()
    {
        return rs;
    }

    public static void getAllFunctionQuery(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet listnerResultSet1 = preparedstatementHandler
                .createResultSet();

        listnerResultSet1.addColumn("oid");
        listnerResultSet1.addColumn("objname");
        listnerResultSet1.addColumn("namespace");
        listnerResultSet1.addColumn("ret");
        listnerResultSet1.addColumn("alltype");
        listnerResultSet1.addColumn("nargs");
        listnerResultSet1.addColumn("argtype");
        listnerResultSet1.addColumn("argname");
        listnerResultSet1.addColumn("argmod");
        listnerResultSet1.addColumn("secdef");
        listnerResultSet1.addColumn("vola");
        listnerResultSet1.addColumn("isstrict");
        listnerResultSet1.addColumn("retset");
        listnerResultSet1.addColumn("procost");
        listnerResultSet1.addColumn("setrows");
        listnerResultSet1.addColumn("lang");
      //Dependency on BL
        /*ObjectParameter retParam = new ObjectParameter();
        retParam.setDatatype("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);*/

        listnerResultSet1.addRow(new Object[] {2, "function2", 1,
                new Integer(16), null, "1", "20 21 21", "{arg1, arg2, arg3}",
                "{i,o,b}", false, "volatile", false, false, 10, 10, "sql"});
        listnerResultSet1.addRow(new Object[] {3, "function2", 1,
                new Integer(16), null, "1", "20 21 21", "{arg1, arg2, arg3}",
                "{i,o,b}", false, "volatile", false, false, 10, 10, "sql"});
        preparedstatementHandler.prepareResultSet(GET_ALL_FUNCTION_QUERY,
                listnerResultSet1);
        setResultSet(listnerResultSet1);

    }

    public static void notificationResultSet(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet notifyResultset = preparedstatementHandler
                .createResultSet();

        notifyResultset.addColumn("Timestamp");
        notifyResultset.addColumn("client_conninfo");
        notifyResultset
                .addRow(new Object[] {TIMESTAMP, "[unknown]@10.18.214.64"});
        notifyResultset
                .addRow(new Object[] {TIMESTAMP, "[unknown]@10.18.213.65"});

        preparedstatementHandler.prepareResultSet(
                NOTIFICATION_QUERY_SUCCESSFULL, notifyResultset);
    }

    public static void notificationResultSetFailure(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet notifyResultset1 = preparedstatementHandler
                .createResultSet();

        notifyResultset1.addColumn("Timestamp");
        notifyResultset1.addColumn("client_conninfo");
        notifyResultset1
                .addRow(new Object[] {TIMESTAMP, "[unknown]@10.18.214.64"});
        notifyResultset1
                .addRow(new Object[] {TIMESTAMP, "[unknown]@10.18.213.65"});

        preparedstatementHandler.prepareResultSet(NOTIFICATION_QUERY_FAILURE,
                notifyResultset1);
    }

    public static void mockGetPartitionOverloaded(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String GET_PARTITIONS_OVERLOADED = "select p.oid AS partition_id , p.relname AS partition_name , p.parentid AS table_id"
                + "  from pg_class c, pg_partition p  where c.oid =  0 and c.parttype = 'p' "
                + " and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;";

        MockResultSet getPartitionsOverloadedRS = preparedstatementHandler
                .createResultSet();
        getPartitionsOverloadedRS.addColumn("partition_id");
        getPartitionsOverloadedRS.addColumn("partition_name");
        getPartitionsOverloadedRS.addColumn("table_id");
        getPartitionsOverloadedRS.addRow(new Object[] {1, "part_1", 1});
        preparedstatementHandler.prepareResultSet(GET_PARTITIONS_OVERLOADED,
                getPartitionsOverloadedRS);

    }

    public static void Dropsequence(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String dropView = "DROP SEQUENCE IF EXISTS pg_catalog.sequence1";

        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("messaage");

        colmetadataRS.addRow(new Object[] {"View droped"});
        preparedstatementHandler.prepareResultSet(dropView, colmetadataRS);
    }

    public static void columnComments(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet columnComnt = preparedstatementHandler.createResultSet();
        columnComnt.addColumn("objsubid");
        columnComnt.addColumn("description");
        columnComnt.addRow(new Object[] {1, "my comment"});
        preparedstatementHandler.prepareResultSet(COLUMN_COMMENTS, columnComnt);
    }

    public static void getPartitionData(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String qry = "select pr.relname , pr.boundaries ,t.spcname from pg_partition pr left join pg_tablespace t on (pr.reltablespace=t.oid) where parttype in('p,v') and pr.parentid =1";
        MockResultSet partinfo = preparedstatementHandler.createResultSet();
        partinfo.addColumn("relname");
        partinfo.addColumn("boundaries");
        partinfo.addColumn("spcname");
        partinfo.addRow(new Object[] {"part_1", "10", "tablespace"});
        preparedstatementHandler.prepareResultSet(qry, partinfo);

    }

    public static void datatypes(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String qry = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns , typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray from pg_type typ where typnamespace = 1 order by typ.typname";
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

        dbrs.addRow(new Object[] {1, "bigint", 1, 1, "character", true, "type",
                "category", 1, true, 12, "bigint"});
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

    /// new queries mocked

    public static void refreshTableinnamespace(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String qry = "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions from pg_class tbl where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.relnamespace = 1 and has_table_privilege(tbl.oid, 'SELECT');";
        MockResultSet refreshtablemetadatainnamspace = preparedstatementHandler
                .createResultSet();
        refreshtablemetadatainnamspace.addColumn("oid");
        refreshtablemetadatainnamspace.addColumn("relname");
        refreshtablemetadatainnamspace.addColumn("relpersistence");
        refreshtablemetadatainnamspace.addColumn("reloptions");
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', ""});
        preparedstatementHandler.prepareResultSet(qry,
                refreshtablemetadatainnamspace);

    }
    public static void refreshTableinnamespaceAutoSuggest(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String qry1 = "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions from pg_class tbl where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.relnamespace = 1 and has_table_privilege(tbl.oid, 'SELECT');";
        String qry2 = "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions from pg_class tbl where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.relnamespace = 2 and has_table_privilege(tbl.oid, 'SELECT');";
        String qry3 = "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions from pg_class tbl where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.relnamespace = 3 and has_table_privilege(tbl.oid, 'SELECT');";
        String qry4 = "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions from pg_class tbl where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.relnamespace = 4 and has_table_privilege(tbl.oid, 'SELECT');";
        String qry5 = "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions from pg_class tbl where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.relnamespace = 5 and has_table_privilege(tbl.oid, 'SELECT');";
        String qry6 = "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions from pg_class tbl where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.relnamespace = 6 and has_table_privilege(tbl.oid, 'SELECT');";
        
        MockResultSet refreshtablemetadatainnamspace = preparedstatementHandler
                .createResultSet();
        refreshtablemetadatainnamspace.addColumn("oid");
        refreshtablemetadatainnamspace.addColumn("relname");
        refreshtablemetadatainnamspace.addColumn("relpersistence");
        refreshtablemetadatainnamspace.addColumn("reloptions");
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', ""});
        preparedstatementHandler.prepareResultSet(qry1,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', ""});
        preparedstatementHandler.prepareResultSet(qry2,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', ""});
        preparedstatementHandler.prepareResultSet(qry3,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', ""});
        preparedstatementHandler.prepareResultSet(qry4,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', ""});
        preparedstatementHandler.prepareResultSet(qry5,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', ""});
        preparedstatementHandler.prepareResultSet(qry6,
                refreshtablemetadatainnamspace);

    }

    public static void fetchViewQuery(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String fetchViewQuery = "SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and c.relnamespace = 1";
        MockResultSet fetchViewRS = preparedstatementHandler.createResultSet();
        fetchViewRS.addColumn("oid");
        fetchViewRS.addColumn("viewname");
        fetchViewRS.addColumn("viewowner");
        fetchViewRS.addRow(new Object[] {1, "myview", "Gauss123"});
        fetchViewRS.addRow(new Object[] {2, "mytestview2", "Gauss1234"});
        preparedstatementHandler.prepareResultSet(fetchViewQuery, fetchViewRS);
    }

    public static void fetchViewQuery_1(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String fetchViewQuery = "SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and c.relnamespace = 1";
        MockResultSet fetchViewRS = preparedstatementHandler.createResultSet();
        fetchViewRS.addColumn("oid");
        fetchViewRS.addColumn("viewname");
        fetchViewRS.addColumn("viewowner");
        fetchViewRS.addRow(new Object[] {1, "myview", "Gauss123"});
        fetchViewRS.addRow(new Object[] {2, "mytestview2", "Gauss1234"});
        preparedstatementHandler.prepareResultSet(fetchViewQuery, fetchViewRS);
    }

    public static void createViewColunmMetadata(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0  and v.relnamespace = 1 order by v.oid, c.attnum";

        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("viewid");
        getdbsrs.addColumn("namespaceid");
        getdbsrs.addColumn("columnidx");
        getdbsrs.addColumn("name");
        getdbsrs.addColumn("datatypeoid");
        getdbsrs.addColumn("dtns");
        getdbsrs.addColumn("length");
        getdbsrs.addColumn("precision");
        getdbsrs.addColumn("dimentions");
        getdbsrs.addColumn("notnull");
        getdbsrs.addColumn("isdefaultvalueavailable");
        getdbsrs.addColumn("default_value");
        getdbsrs.addColumn("attDefStr");
        getdbsrs.addColumn("displayColumns");

        getdbsrs.addRow(new Object[] {1, 1, 1, "col1", 2, 1, 64, -1, 0, 'f',
                'f', "", "", ""});
        getdbsrs.addRow(new Object[] {1, 1, 2, "col2", 1, 1, 64, -1, 0, 'f',
                'f', "", "", ""});
        /*
         * getdbsrs.addRow(new Object[] {2, nsid, 1, "col3", 2, 1, 64, -1, 0,
         * 'f', 'f', ""}); getdbsrs.addRow(new Object[] {2, nsid, 2, "col4", 1,
         * 1, 64, -1, 0, 'f', 'f', ""}); getdbsrs.addRow(new Object[] {3, nsid,
         * 1, "col5", 2, 1, 64, -1, 0, 'f', 'f', ""}); getdbsrs.addRow(new
         * Object[] {3, nsid, 2, "col6", 1, 1, 64, -1, 0, 'f', 'f', ""});
         * getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 1, 1, -1, -1, 0,
         * 'f', 'f', ""}); getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 11,
         * 1, -1, -1, 0, 'f', 'f', ""});
         */
        preparedstatementHandler.prepareResultSet(query, getdbsrs);
    }

    public static void fetchViewColumnInfo(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and v.oid = 2 order by v.oid, c.attnum";

        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("viewid");
        getdbsrs.addColumn("namespaceid");
        getdbsrs.addColumn("columnidx");
        getdbsrs.addColumn("name");
        getdbsrs.addColumn("displaycolumns");
        getdbsrs.addColumn("datatypeoid");
        getdbsrs.addColumn("dtns");
        getdbsrs.addColumn("length");
        getdbsrs.addColumn("precision");
        getdbsrs.addColumn("dimentions");
        getdbsrs.addColumn("notnull");
        getdbsrs.addColumn("isdefaultvalueavailable");
        getdbsrs.addColumn("default_value");
        getdbsrs.addColumn("attDefStr");

        getdbsrs.addRow(new Object[] {1, 1, 1, "col1", "", 2, 1, 64, -1, 0, 'f',
                'f', "", ""});
        getdbsrs.addRow(new Object[] {1, 1, 2, "col2", "", 1, 1, 64, -1, 0, 'f',
                'f', "", ""});
        /*
         * getdbsrs.addRow(new Object[] {2, nsid, 1, "col3", 2, 1, 64, -1, 0,
         * 'f', 'f', ""}); getdbsrs.addRow(new Object[] {2, nsid, 2, "col4", 1,
         * 1, 64, -1, 0, 'f', 'f', ""}); getdbsrs.addRow(new Object[] {3, nsid,
         * 1, "col5", 2, 1, 64, -1, 0, 'f', 'f', ""}); getdbsrs.addRow(new
         * Object[] {3, nsid, 2, "col6", 1, 1, 64, -1, 0, 'f', 'f', ""});
         * getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 1, 1, -1, -1, 0,
         * 'f', 'f', ""}); getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 11,
         * 1, -1, -1, 0, 'f', 'f', ""});
         */
        preparedstatementHandler.prepareResultSet(query, getdbsrs);
    }

    public static void refreshTableForPartitionTable(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet partitionTablemetadataRS = preparedstatementHandler
                .createResultSet();
        partitionTablemetadataRS.addColumn("oid");
        partitionTablemetadataRS.addColumn("relname");
        partitionTablemetadataRS.addColumn("relnamespace");
        partitionTablemetadataRS.addColumn("reltablespace");
        partitionTablemetadataRS.addColumn("relpersistence");
        partitionTablemetadataRS.addColumn("desc");
        partitionTablemetadataRS.addColumn("nodes");
        partitionTablemetadataRS.addColumn("reloptions");

        partitionTablemetadataRS.addRow(new Object[] {1, "MyTable", 1, 1, true,
                "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.REFRESH_TABLE_METADATA_PARTITION_TABLE,
                partitionTablemetadataRS);
        MockResultSet getcolumninfoRSptble_1 = preparedstatementHandler
                .createResultSet();
        getcolumninfoRSptble_1.addColumn("tableid");
        getcolumninfoRSptble_1.addColumn("namespaceid");
        getcolumninfoRSptble_1.addColumn("columnidx");
        getcolumninfoRSptble_1.addColumn("name");
        getcolumninfoRSptble_1.addColumn("datatypeoid");
        getcolumninfoRSptble_1.addColumn("dtns");
        getcolumninfoRSptble_1.addColumn("length");
        getcolumninfoRSptble_1.addColumn("precision");
        getcolumninfoRSptble_1.addColumn("dimentions");
        getcolumninfoRSptble_1.addColumn("notnull");
        getcolumninfoRSptble_1.addColumn("isdefaultvalueavailable");
        getcolumninfoRSptble_1.addColumn("default_value");
        getcolumninfoRSptble_1.addColumn("attDefStr");
        getcolumninfoRSptble_1.addColumn("displayColumns");
        getcolumninfoRSptble_1.addRow(new Object[] {1, 1, 1, "ColName", 1, 1,
                200, 0, 0, false, true, "Default value", "attrString", "bigint"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_COLUMN_INFO_PARTITION_TABLE,
                getcolumninfoRSptble_1);

        MockResultSet partitionConstraintRS_1 = preparedstatementHandler
                .createResultSet();
        partitionConstraintRS_1.addColumn("constraintid");
        partitionConstraintRS_1.addColumn("tableid");
        partitionConstraintRS_1.addColumn("namespaceid");
        partitionConstraintRS_1.addColumn("constraintname");
        partitionConstraintRS_1.addColumn("constrainttype");
        partitionConstraintRS_1.addColumn("deferrable");
        partitionConstraintRS_1.addColumn("deferred");
        partitionConstraintRS_1.addColumn("validate");
        partitionConstraintRS_1.addColumn("indexid");
        partitionConstraintRS_1.addColumn("fkeytableId");
        partitionConstraintRS_1.addColumn("updatetype");
        partitionConstraintRS_1.addColumn("deletetype");
        partitionConstraintRS_1.addColumn("matchtype");
        partitionConstraintRS_1.addColumn("expr");
        partitionConstraintRS_1.addColumn("columnlist");
        partitionConstraintRS_1.addColumn("fkeycolumnlist");
        partitionConstraintRS_1.addColumn("const_def");
        partitionConstraintRS_1.addRow(new Object[] {1, 1, 1, "ConstraintName",
                "ConstraintType", false, false, false, 1, 1, "", "", "", "",
                "1", "1", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_PARTITION_CONSTRAINT,
                partitionConstraintRS_1);

        MockResultSet indexpartitionRS_1 = preparedstatementHandler
                .createResultSet();
        indexpartitionRS_1.addColumn("oid");
        indexpartitionRS_1.addColumn("tableId");
        indexpartitionRS_1.addColumn("indexname");
        indexpartitionRS_1.addColumn("namespaceid");
        indexpartitionRS_1.addColumn("accessmethodid");
        indexpartitionRS_1.addColumn("isunique");
        indexpartitionRS_1.addColumn("isprimary");
        indexpartitionRS_1.addColumn("isexclusion");
        indexpartitionRS_1.addColumn("isimmediate");
        indexpartitionRS_1.addColumn("isclustered");
        indexpartitionRS_1.addColumn("checkmin");
        indexpartitionRS_1.addColumn("isready");
        indexpartitionRS_1.addColumn("cols");
        indexpartitionRS_1.addColumn("reloptions");
        indexpartitionRS_1.addColumn("indexdef");
        indexpartitionRS_1.addColumn("tablespace");
        indexpartitionRS_1.addRow(new Object[] {1, 1, "IndexName", 1, 1, true,
                false, false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_PARTITION_INDEX, indexpartitionRS_1);
        
        MockResultSet getPartitionsRS_1 = preparedstatementHandler
                .createResultSet();
        getPartitionsRS_1.addColumn("partition_id");
        getPartitionsRS_1.addColumn("partition_name");
        getPartitionsRS_1.addColumn("table_id");

        getPartitionsRS_1.addRow(new Object[] {1, "part_1", 1});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.GET_PARTITION,
                getPartitionsRS_1);

    }

    public static void getRsForAutoSuggest(PreparedStatementResultSetHandler preparedstatementHandler )
    {
        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addRow(new Object[] {1, "ns1"});
        namespaceRS.addRow(new Object[] {2, "NS1"});
        namespaceRS.addRow(new Object[] {3, "Ns1"});
        namespaceRS.addRow(new Object[] {4, "ns2"});
        namespaceRS.addRow(new Object[] {5, "NS3"});
        namespaceRS.addRow(new Object[] {6, "\"NS1\""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_NAMESPACE, namespaceRS);
        
        String REFRESH_TABLE = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
                + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = "
                + 1 + " order by t.oid, c.attnum;";

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

        colmetadataRS.addRow(new Object[] {1, 1, 1, "tbl1", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                 colmetadataRS.addRow(new Object[] {2, 1, 1, "tbl2", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                 colmetadataRS.addRow(new Object[] {3, 1, 1, "TBL1", 1, 1, 200, 0, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(REFRESH_TABLE,
                colmetadataRS);
                
String REFRESH_TABLE1 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
                + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = "
                + 2 + " order by t.oid, c.attnum;";

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

        colmetadataRS1.addRow(new Object[] {1, 2, 1, "tbl1", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                 colmetadataRS1.addRow(new Object[] {2, 2, 1, "tbl2", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                 colmetadataRS1.addRow(new Object[] {3, 2, 1, "TBL1", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                colmetadataRS1.addRow(new Object[] {3, 2, 1, "Tbl1", 1, 1, 200, 0, 0,
                false, true, "Default value"});
        preparedstatementHandler.prepareResultSet(REFRESH_TABLE1,
                colmetadataRS1);
                
                                String REFRESH_TABLE2 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
                + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = "
                + 3 + " order by t.oid, c.attnum;";

        MockResultSet colmetadataRS2 = preparedstatementHandler
                .createResultSet();
        colmetadataRS2.addColumn("tableid");
        colmetadataRS2.addColumn("namespaceid");
        colmetadataRS2.addColumn("columnidx");
        colmetadataRS2.addColumn("name");
        colmetadataRS2.addColumn("datatypeoid");
        colmetadataRS2.addColumn("dtns");
        colmetadataRS2.addColumn("length");
        colmetadataRS2.addColumn("precision");
        colmetadataRS2.addColumn("dimentions");
        colmetadataRS2.addColumn("notnull");
        colmetadataRS2.addColumn("isdefaultvalueavailable");
        colmetadataRS2.addColumn("default_value");

        colmetadataRS2.addRow(new Object[] {1, 4, 1, "Tbl1", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                 colmetadataRS2.addRow(new Object[] {2, 4 ,1, "xtbl2", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                 colmetadataRS2.addRow(new Object[] {3, 4, 1, "xtbl1", 1, 1, 200, 0, 0,
                false, true, "Default value"});

        preparedstatementHandler.prepareResultSet(REFRESH_TABLE2,
             colmetadataRS2);

                                    String REFRESH_TABLE3 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
                + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = "
                + 4 + " order by t.oid, c.attnum;";

        MockResultSet colmetadataRS3 = preparedstatementHandler
                .createResultSet();
        colmetadataRS3.addColumn("tableid");
        colmetadataRS3.addColumn("namespaceid");
        colmetadataRS3.addColumn("columnidx");
        colmetadataRS3.addColumn("name");
        colmetadataRS3.addColumn("datatypeoid");
        colmetadataRS3.addColumn("dtns");
        colmetadataRS3.addColumn("length");
        colmetadataRS3.addColumn("precision");
        colmetadataRS3.addColumn("dimentions");
        colmetadataRS3.addColumn("notnull");
        colmetadataRS3.addColumn("isdefaultvalueavailable");
        colmetadataRS3.addColumn("default_value");

         colmetadataRS3.addRow(new Object[] {1, 4, 1, "Tbl1", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                 colmetadataRS3.addRow(new Object[] {2, 4 ,1, "xtbl2", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                 colmetadataRS3.addRow(new Object[] {3, 4, 1, "xtbl1", 1, 1, 200, 0, 0,
                false, true, "Default value"});
                    preparedstatementHandler.prepareResultSet(REFRESH_TABLE3,
                colmetadataRS3);

String REFRESH_TABLE4 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
                + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = "
                + 5 + " order by t.oid, c.attnum;";

        MockResultSet colmetadataRS4 = preparedstatementHandler
                .createResultSet();
        colmetadataRS4.addColumn("tableid");
        colmetadataRS4.addColumn("namespaceid");
        colmetadataRS4.addColumn("columnidx");
        colmetadataRS4.addColumn("name");
        colmetadataRS4.addColumn("datatypeoid");
        colmetadataRS4.addColumn("dtns");
        colmetadataRS4.addColumn("length");
        colmetadataRS4.addColumn("precision");
        colmetadataRS4.addColumn("dimentions");
        colmetadataRS4.addColumn("notnull");
        colmetadataRS4.addColumn("isdefaultvalueavailable");
        colmetadataRS4.addColumn("default_value");
        
        colmetadataRS4.addRow(new Object[] {1, 5, 1, "NS1", 1, 1, 200, 0, 0,
                false, true, "Default value"});

        preparedstatementHandler.prepareResultSet(REFRESH_TABLE4,
                colmetadataRS4);
                
                String REFRESH_TABLE5 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
                + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = "
                + 6 + " order by t.oid, c.attnum;";

        MockResultSet colmetadataRS5 = preparedstatementHandler
                .createResultSet();
        colmetadataRS5.addColumn("tableid");
        colmetadataRS5.addColumn("namespaceid");
        colmetadataRS5.addColumn("columnidx");
        colmetadataRS5.addColumn("name");
        colmetadataRS5.addColumn("datatypeoid");
        colmetadataRS5.addColumn("dtns");
        colmetadataRS5.addColumn("length");
        colmetadataRS5.addColumn("precision");
        colmetadataRS5.addColumn("dimentions");
        colmetadataRS5.addColumn("notnull");
        colmetadataRS5.addColumn("isdefaultvalueavailable");
        colmetadataRS5.addColumn("default_value");
        
        colmetadataRS5.addRow(new Object[] {1, 6, 1, "TBL2", 1, 1, 200, 0, 0,
                false, true, "Default value"});

        preparedstatementHandler.prepareResultSet(REFRESH_TABLE5,
                colmetadataRS5);

    }
    
    public static Gauss200V1R7Driver mockConnection(Driver driver) {
        ArrayList<AbstractConnectionDriver> olapDriverInstance = DBMSDriverManager.getOLAPDriverInstance("");
        
        Gauss200V1R7Driver lGauss200V1R7Driver = getGaussDriver(driver);
        
        olapDriverInstance.add(0,lGauss200V1R7Driver);
        olapDriverInstance.add(1,lGauss200V1R7Driver);
        return lGauss200V1R7Driver;
    }

    public static Gauss200V1R7Driver getGaussDriver(Driver driver) {
        Gauss200V1R7Driver lGauss200V1R7Driver=new Gauss200V1R7Driver("") {  
            @Override
            protected void configureDriverDetails(String dsInstallPath) {
                super.configureDriverDetails(dsInstallPath);
            setDriver(MPPDBIDEConstants.GAUSS200V1R7DRIVER);
            setJDBCDriver(driver);
        }};
        return lGauss200V1R7Driver;
    }
    
}
