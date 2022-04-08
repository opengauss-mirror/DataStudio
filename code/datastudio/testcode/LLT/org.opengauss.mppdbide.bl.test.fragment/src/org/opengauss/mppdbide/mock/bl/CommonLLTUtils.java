package org.opengauss.mppdbide.mock.bl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.opengauss.mppdbide.adapter.AbstractConnectionDriver;
import org.opengauss.mppdbide.adapter.IConnectionDriver;
import org.opengauss.mppdbide.adapter.driver.DBMSDriverManager;
import org.opengauss.mppdbide.adapter.driver.Gauss200V1R7Driver;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionManager;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.SynonymMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.SynonymUtil;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.files.DSFileAttributes;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.ISetFilePermission;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.AbstractResultSetHandler;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

public class CommonLLTUtils
{
    private static final String OS_NAME = "os.name";
    private static final String WINDOWS = "Windows";
    private static final String LINUX = "Linux";
    private static ResultSet      rs;

    public static final Timestamp TIMESTAMP = new Timestamp(
            System.currentTimeMillis());

    public enum EXCEPTIONENUM
    {
        YES, NO, EXCEPTION
    }
    public static final String FETCH_ALL_NAMESPACE_LOAD_PRIV = "SELECT oid, nspname from pg_namespace where has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";
    public static final String FETCH_ALL_NAMESPACE_LOAD_ALL = "SELECT oid, nspname from pg_namespace ORDER BY nspname;";
    public static final String FETCH_ALL_TABLESPACE_ALL = "select oid, pg_tablespace_location(oid) as location ,spcname,spcacl,"
            + "spcoptions, spcmaxsize, relative from pg_tablespace order by spcname;";
    public static final String FETCH_ALL_TABLESPACE_BY_ID = "select oid, pg_tablespace_location(oid) as location, spcname,spcacl,spcoptions, spcmaxsize, relative from pg_tablespace  where oid =10;";
    public static final String GET_ALL_SYSNAMESPACE_ALL = "SELECT oid, nspname from pg_namespace where ((oid < 16384 and nspname NOT LIKE 'public') "
    + "or nspname LIKE 'pg_%') ORDER BY nspname;";

    public static final String GET_ALL_USERNAMESPACE_ALL = "SELECT oid, nspname from pg_namespace where ((oid >= 16384 or nspname LIKE 'public') "
            + "and nspname  NOT LIKE 'pg_%') ORDER BY nspname;";
    public static final String GET_ALL_NAMESPACE                         = "SELECT oid, nspname from pg_namespace where ((oid >= 16384 or nspname LIKE 'public') and nspname  NOT LIKE 'pg_%') and has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";
    public static final String GET_ALL_SYSTEM_NAMESPACE                         = "SELECT oid, nspname from pg_namespace where ((oid < 16384 and nspname NOT LIKE 'public') or nspname LIKE 'pg_%') and has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";
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
    public static final String SHALLOWLOADQUERY                          = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname in ('plpgsql','sql','c') and has_function_privilege(pr.oid, 'EXECUTE') and pr.pronamespace= 1 ORDER BY objname";
    public static final String SHALLOWLOADQUERY_1                          = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname in ('plpgsql','sql') and has_function_privilege(pr.oid, 'EXECUTE') and pr.pronamespace= 10 ORDER BY objname";
    
    public static final String GET_PARTITION_COLUMN_INFO                 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
            + " pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
            + " c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod "
            + " as precision, c.attndims as dimentions, "
            + " c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as "
            + " default_value, d.adbin as attDefStr "
            + " from pg_class t "
            + " left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v')) "
            + " left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) "
            + " left join pg_type typ on (c.atttypid = typ.oid) "
            + " where c.attisdropped = 'f' and c.attnum > 0 and t.oid = "
            + 0 + " and t.relkind <> 'i' "
            + " order by c.attnum;";
    
    public static final String CHECK_DEBUG = "SELECT count(1) from pg_proc where proname='pldbg_attach_session';";

    public static final String CHECK_EXPLAIN_PLAN = "EXPLAIN SELECT 1";
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

  //  public static final String GET_ALL_DATATYPE                          = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns ,  typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typ.typnamespace in (select oid from pg_namespace where nspname in ('information_schema', 'pg_catalog')) order by typ.typname";
    public static final String GET_ALL_DATATYPE_DB                       = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns ,  typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typ.typnamespace in (select oid from pg_namespace where nspname in ('information_schema', 'pg_catalog')) order by typ.typname";
   // public static final String GET_ALL_DATATYPE_database                 = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns ,  typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typ.typnamespace in (select oid from pg_namespace where nspname in ('information_schema', 'pg_catalog')) order by typ.typname";

    public static final String FETCH_ALL_TABLEMETADATA                   = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace,tbl.reloptions as reloptions, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r'";
    // overloaded method of refreshTableMetada

    public static final String REFRESH_TABLE_METADATA                    = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype = 'n' and tbl.relnamespace = 1;";

    // Partition table info for a particular table
    public static final String REFRESH_TABLE_METADATA_PARTITION_TABLE    = "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes, tbl.reloptions as reloptions  from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.parttype in ('p', 'v') and tbl.oid = 0 and tbl.relkind <>  'i'"
    		+ " and oid in ("
    		            		+ "select pcrelid from pgxc_class "
    		            		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
    							+ ") select * from x "
    		        		+ "where has_table_privilege(x.oid,'SELECT');";

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
            + 10 + " and has_tablespace_privilege(oid, 'CREATE');";

    public static final String GET_ALL_VIEWS                             = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
            + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, "
            + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value "
            + "from pg_class v "
            + "left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') "
            + "left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) "
            + "left join pg_type typ on (c.atttypid = typ.oid) "
            + "where c.attisdropped = 'f' and c.attnum > 0 " + "and v.oid = "
            + 1 + " order by v.oid, c.attnum";

    public static final String Refresh_TABLEMETADATA                     = "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, "
            + "ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, "
            + "xctbl.nodeoids as nodes ,tbl.reloptions as reloptions "
            + "from pg_class tbl left join (select d.description, d.objoid from pg_description d "
            + "where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl "
            + "on (tbl.oid = xctbl.pcrelid) "
            + "left join pg_tablespace ts on ts.oid = tbl.reltablespace "
            + "where tbl.relkind = 'r' and "
            + "tbl.parttype in ('n','p') and tbl.oid = 1"
			+ " and oid in ("
        		+ "select pcrelid from pgxc_class "
        		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
				+ ") select * from x "
    		+ "where has_table_privilege(x.oid,'SELECT');";

    public static final String RENAME_Refresh_TABLEMETADATA              = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.oid = 1;";
    
    public static final String Refresh_PARTITION_TABLEMETADATA           = "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, "
            + "ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, "
            + "xctbl.nodeoids as nodes ,tbl.reloptions as reloptions "
            + "from pg_class tbl left join (select d.description, d.objoid from pg_description d "
            + "where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl "
            + "on (tbl.oid = xctbl.pcrelid) "
            + "left join pg_tablespace ts on ts.oid = tbl.reltablespace "
            + "where tbl.relkind = 'r' and "
            + "tbl.parttype in ('n','p') and tbl.oid = 0"
			+ " and oid in ("
        		+ "select pcrelid from pgxc_class "
        		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
				+ ") select * from x "
    		+ "where has_table_privilege(x.oid,'SELECT');";
    
    public static final String GET_ALL_FTABLES_IN_SCHEMA                 = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes,tbl.reloptions as reloptions, frgn.ftoptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'f' and has_table_privilege(frgn.ftrelid, 'SELECT') and tbl.relnamespace = 1;";

    public static final String GET_COLUMN_INFO_FOREIGN_TABLES            = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'f' and t.parttype ='n') left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 1 order by t.oid, c.attnum;";

    public static final String NOTIFICATION_QUERY_SUCCESSFULL            = "SELECT logintime, client_conninfo from login_audit_messages_pid(true)";

    public static final String NOTIFICATION_QUERY_FAILURE                = "SELECT logintime, client_conninfo from login_audit_messages_pid(false)";

    public static final String GET_ALL_PARTITION_TABLES                  = "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes, tbl.reloptions as reloptions , array_to_string(part.partkey,',') as partkey  from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on(tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.parttype in ('p', 'v') and tbl.relnamespace = 1 and tbl.relkind <>  'i'"
    		+ " and oid in ("
    		+ "select pcrelid from pgxc_class "
    		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
    		+ ") select * from x "
    		+ "where has_table_privilege(x.oid,'SELECT');";

    public static final String GET_COLUMN_INFO_PARTITION_TABLES          = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v')) left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 1 and t.relkind <> 'i' order by t.oid, c.attnum;";
    // column info for a single partition table
    public static final String GET_COLUMN_INFO_PARTITION_TABLE           = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v')) left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and t.oid = 1 and t.relkind <> 'i' order by c.attnum;";

    public static final String GET_PARTITIONS                            = "select p.oid AS partition_id , p.relname AS partition_name, p.partstrategy as partition_type, p.parentid AS table_id  from pg_class c, pg_partition p  where c.oid =  0 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;";

    // partition info for a single table
    public static final String GET_PARTITION                             = "select p.oid AS partition_id , p.relname AS partition_name , p.parentid AS table_id  from pg_class c, pg_partition p  where c.oid =  1 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;";
  
    public static final String GET_PARTITION_CONSTRAINTS                 = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=0 and cl.parttype in ('p','v') and c.conrelid <> 0;";
    // constraint info for a single partition table
    public static final String GET_PARTITION_CONSTRAINT                  = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=1 and cl.parttype in ('p','v') and c.conrelid <> 0;";

    public static final String GET_PARTITION_INDEXES                     = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind in ('r', 'f', 'I') and ci.parttype in ('p','n') and ci.relnamespace = 1 and tableid= 0;";

    // index info for a single partition table
    public static final String GET_PARTITION_INDEX                       = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind in ('r', 'f') and ci.parttype in ('p','v') and ci.oid = 1;";

    public static final String GET_REFRESH_DATATYPE                      = "select typ.oid as oid, typ.typname as typname, typ.typnamespace as typnamespace, typ.typlen as typlen, pg_catalog.format_type(oid,typ.typtypmod) as displaycolumns , typ.typbyval as typbyval, typ.typtype as typtype, typ.typcategory as typcategory, typ.typtypmod as typtypmod, typ.typnotnull as typnotnull, typ.typarray as typarray, des.description as desc from pg_type typ left join pg_description des on (typ.oid = des.objoid) where typnamespace = 1 order by typ.typname";

    public static final String REFRESH_SEQUENCE_METADATA                 = "select oid,relnamespace,relowner,relname from pg_class where relkind='S' and relnamespace= 1";
    public static final String COLUMN_COMMENTS                           = "select pg_description.objsubid,pg_description.description from pg_description where objoid = "
            + 1 + ';';
    
    public static final String GEL_CONSTRAINTS_AT_LOAD_LEVEL             = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=1 and cl.parttype in ('p','v') and c.conrelid <> 0;";
    
    public static final String GET_PARTITION_INDEXES_AT_LOAD_LEVEL       = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as namespaceid, ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, i.indisexclusion as isexclusion, i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, i.indisready as isready, i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace FROM pg_index i LEFT JOIN pg_class t on (t.oid = i.indrelid) LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) WHERE t.relkind in ('r', 'f') and ci.parttype in ('p','v')  and ci.relnamespace = 1;";
    
    
    public static final String GET_PARTITIONS_AT_LOAD_LEVEL              = "select p.oid AS partition_id , p.relname AS partition_name, p.partstrategy as partition_type, p.parentid AS table_id  from pg_class c, pg_partition p  where c.relnamespace =  1 and c.parttype = 'p'  and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;";
    
    public static final String SET_DEFAULT_DS_ENCODING                          = "UTF-8";
    
    public static final String FETCH_SERVER_IP                           = "select inet_server_addr();";
    
    public static final String FETCH_ALL_TABLE_IN_NAMESPACE  = "with x as ("
            + "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r' and tbl.relnamespace = 1"
            + " and oid in ("
            + "select pcrelid from pgxc_class "
            + "where has_nodegroup_privilege(pgroup, 'USAGE'))"
            + ") select * from x "
            + "where has_table_privilege(x.oid,'SELECT');";
    
    private static final String REFRESH_JOB_STATEMENT = "SELECT job,log_user,"
            + "priv_user,dbname,last_date,this_date,next_date,broken,interval,"
            + "failures,what FROM USER_JOBS WHERE log_user = ? and job = ?";
    
    public static void initDriver(String strDriver) throws DatabaseOperationException {
        try {
            Class.forName(strDriver);
            MPPDBIDELoggerUtility.info("ADAPTER: Driver Init Successful.");
        } catch (ClassNotFoundException e) {
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_DRIVER_INIT_FAILED, e);
        }
    }
    
    public static DBConnection getDBConnection()
    {
        DBConnection connection1 = new DBConnection();

        String url = null;
        Properties props = new Properties();

        props.setProperty("user", "test");
        props.setProperty("password", "test");
        props.setProperty("allowEncodingChanges", "true");
      //  String encoding = System.getProperty("file.encoding");
        props.setProperty("characterEncoding", SET_DEFAULT_DS_ENCODING);
        props.setProperty("ApplicationName", "MPP IDE");

        url = "jdbc:postgresql://127.0.0.1:1234/testDB";

        try
        {
            connection1.setConnection(DBConnectionForTestSupport.dbConnect(props, url));
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

    public static void prepareProxyInfo(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        mockCheckDebugSupport(preparedstatementHandler);
        mockCheckExplainPlanSupport(preparedstatementHandler);
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
        getdbsrs1.addColumn("relkind");
        getdbsrs1.addRow(new Object[] {2, 1, "public", "mytestview", "owner1", "v"});
        getdbsrs1.addRow(
                new Object[] {25, 1, "public", "mytestview2", "owner1"});
        preparedstatementHandler.prepareResultSet(
        		"with x as (SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner , c.relkind as relkind FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and c.relnamespace = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                getdbsrs1);
        
        MockResultSet getdbsrs2 = preparedstatementHandler.createResultSet();
        getdbsrs2.addColumn("oid");
        getdbsrs2.addColumn("nspoid");
        getdbsrs2.addColumn("schemaname");
        getdbsrs2.addColumn("viewname");
        getdbsrs2.addColumn("viewowner");
        getdbsrs2.addRow(new Object[] {2, 1, "public", "mytestview", "owner1"});
        getdbsrs2.addRow(
                new Object[] {25, 1, "public", "mytestview2", "owner1"});
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                        + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'm'::\"char\") ",
                        getdbsrs2);

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

        MockResultSet getdbTblspc = preparedstatementHandler.createResultSet();
        getdbTblspc.addColumn("spcname");

        getdbTblspc.addRow(new Object[] {"pg_default"});
        preparedstatementHandler.prepareResultSet(
                "SELECT tbs.spcname from pg_tablespace tbs, pg_database db where tbs.oid = db.dattablespace and db.datname = 'Gauss'",
                getdbTblspc);        
        
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
                FETCH_ALL_TABLESPACE_ALL,
                gettblsprs_all);

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
        namespaceRS.addRow(new Object[] {3, "PUBLIC"});
       // namespaceRS.addRow(new Object[] {1, "pg_catalog"});
       // namespaceRS.addRow(new Object[] {2, "information_schema"});
        // namespaceRS.addRow(new Object[] {2, "schema1"});
        // namespaceRS.addRow(new Object[] {3, "schema2"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_NAMESPACE, namespaceRS);
        
        MockResultSet namespaceRS_all = preparedstatementHandler.createResultSet();
        namespaceRS_all.addColumn("oid");
        namespaceRS_all.addColumn("nspname");
        namespaceRS_all.addRow(new Object[] {3, "PUBLIC"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_USERNAMESPACE_ALL, namespaceRS_all);
        
        MockResultSet namespaceRS_all1 = preparedstatementHandler.createResultSet();
        namespaceRS_all1.addColumn("oid");
        namespaceRS_all1.addColumn("nspname");
        namespaceRS_all1.addRow(new Object[] {3, "PUBLIC"});
        namespaceRS_all1.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS_all1.addRow(new Object[] {2, "information_schema"});
       namespaceRS_all1.addRow(new Object[] {10, "newSchema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS_all1);
        
        MockResultSet namespaceRS_all11 = preparedstatementHandler.createResultSet();
        namespaceRS_all11.addColumn("oid");
        namespaceRS_all11.addColumn("nspname");
        namespaceRS_all11.addRow(new Object[] {3, "PUBLIC"});
        namespaceRS_all11.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS_all11.addRow(new Object[] {2, "information_schema"});
     //   namespaceRS_all11.addRow(new Object[] {10, "newSchema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.FETCH_ALL_NAMESPACE_LOAD_ALL, namespaceRS_all11);
        
        MockResultSet systemNamespaceRS = preparedstatementHandler.createResultSet();
        systemNamespaceRS.addColumn("oid");
        systemNamespaceRS.addColumn("nspname");
       // namespaceRS.addRow(new Object[] {1, "PUBLIC"});
        systemNamespaceRS.addRow(new Object[] {1, "pg_catalog"});
        systemNamespaceRS.addRow(new Object[] {2, "information_schema"});
        // namespaceRS.addRow(new Object[] {2, "schema1"});
        // namespaceRS.addRow(new Object[] {3, "schema2"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_SYSTEM_NAMESPACE, systemNamespaceRS);
        
        MockResultSet systemNamespaceRS_all = preparedstatementHandler.createResultSet();
        systemNamespaceRS_all.addColumn("oid");
        systemNamespaceRS_all.addColumn("nspname");
        systemNamespaceRS_all.addRow(new Object[] {1, "pg_catalog"});
        systemNamespaceRS_all.addRow(new Object[] {2, "information_schema"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_SYSNAMESPACE_ALL, systemNamespaceRS_all);

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
        
        MockResultSet viewRS_all = preparedstatementHandler.createResultSet();
        viewRS_all.addColumn("viewid");
        viewRS_all.addColumn("namespaceid");
        viewRS_all.addColumn("columnidx");
        viewRS_all.addColumn("name");
        viewRS_all.addColumn("datatypeoid");
        viewRS_all.addColumn("dtns");
        viewRS_all.addColumn("length");
        viewRS_all.addColumn("precision");
        viewRS_all.addColumn("dimentions");
        viewRS_all.addColumn("notnull");
        viewRS_all.addColumn("isdefaultvalueavailable");
        viewRS_all.addColumn("default_value");
        viewRS_all.addRow(
                new Object[] {2, 1, 1, "col1", 3, 1, 0, 0, 0, 'f', 'f', ""});
        viewRS_all.addRow(
                new Object[] {2, 1, 1, "col1", 8, 1, 0, 0, 0, 'f', 'f', ""});
        viewRS_all.addRow(
                new Object[] {2, 1, 1, "col1", 5, 1, 0, 0, 0, 'f', 'f', ""});
       // preparedstatementHandler.prepareResultSet(CommonLLTUtils.GET_ALL_VIEWS_ALL,
        //        viewRS_all);

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
        
        MockResultSet refreshtablemetadatainnamspace = preparedstatementHandler
                .createResultSet();
        refreshtablemetadatainnamspace.addColumn("oid");
        refreshtablemetadatainnamspace.addColumn("relname");
        refreshtablemetadatainnamspace.addColumn("relpersistence");
        refreshtablemetadatainnamspace.addColumn("reloptions");
        refreshtablemetadatainnamspace.addColumn("parttype");
        refreshtablemetadatainnamspace.addColumn("partkey");
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.FETCH_ALL_TABLE_IN_NAMESPACE,
                refreshtablemetadatainnamspace);
        
        

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
        
        MockResultSet shallowLoadQueryRS1 = preparedstatementHandler
                .createResultSet();
        shallowLoadQueryRS1.addColumn("oid");
        shallowLoadQueryRS1.addColumn("objname");
        shallowLoadQueryRS1.addColumn("namespace");
        shallowLoadQueryRS1.addColumn("ret");
        shallowLoadQueryRS1.addColumn("alltype");
        shallowLoadQueryRS1.addColumn("nargs");
        shallowLoadQueryRS1.addColumn("argtype");
        shallowLoadQueryRS1.addColumn("argname");
        shallowLoadQueryRS1.addColumn("argmod");
        shallowLoadQueryRS1.addColumn("secdef");
        shallowLoadQueryRS1.addColumn("vola");
        shallowLoadQueryRS1.addColumn("isstrict");
        shallowLoadQueryRS1.addColumn("retset");
        shallowLoadQueryRS1.addColumn("procost");
        shallowLoadQueryRS1.addColumn("setrows");
        shallowLoadQueryRS1.addColumn("lang");
        shallowLoadQueryRS1.addRow(new Object[] {1, "auto1", 10, 23, null, 0,
                null, null, null, "f", "v", "f", "f", 100, 0, "plpgsql"});
        // shallowLoadQueryRS.addRow(new Object[]
        // {17830,"calculateoutput",1,2278,"{23,23}",1,23,"{id,value}","{i,o}","f","v","f","f",100,0});
        shallowLoadQueryRS1.addRow(new Object[] {2, "function2", 10, 2278,
                "{23,23,23}", 2, 23, "{id1,id2,value}", "{i,o,b}", "f", "v",
                "f", "f", 100, 0, "plpgsql"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.SHALLOWLOADQUERY_1, shallowLoadQueryRS1);

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
        shallowLoadQueryRS1.addRow(new Object[] {2, "function2", 1, 2278,
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
        dbrs.addRow(new Object[] {1, "char", 1, 1, "character", true, "type",
                "category", 1, true, 12, "description", "char"});
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

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

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
        
        MockResultSet renameRefreshtablemetada = preparedstatementHandler
                .createResultSet();
        renameRefreshtablemetada.addColumn("oid");
        renameRefreshtablemetada.addColumn("relname");
        renameRefreshtablemetada.addColumn("relnamespace");
        renameRefreshtablemetada.addColumn("reltablespace");
        renameRefreshtablemetada.addColumn("relpersistence");
        renameRefreshtablemetada.addColumn("desc");
        renameRefreshtablemetada.addColumn("nodes");
        renameRefreshtablemetada.addColumn("reloptions");
        
        renameRefreshtablemetada.addRow(new Object[] {1, "MyTable", 1, 1, true,
            "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.RENAME_Refresh_TABLEMETADATA, renameRefreshtablemetada);
        
        MockResultSet renameRefreshPartitiontablemetada = preparedstatementHandler
                .createResultSet();
        renameRefreshPartitiontablemetada.addColumn("oid");
        renameRefreshPartitiontablemetada.addColumn("relname");
        renameRefreshPartitiontablemetada.addColumn("relnamespace");
        renameRefreshPartitiontablemetada.addColumn("reltablespace");
        renameRefreshPartitiontablemetada.addColumn("relpersistence");
        renameRefreshPartitiontablemetada.addColumn("desc");
        renameRefreshPartitiontablemetada.addColumn("nodes");
        renameRefreshPartitiontablemetada.addColumn("reloptions");
        
        renameRefreshPartitiontablemetada.addRow(new Object[] {1, "MyTable", 1, 1, true,
            "description", "1 2", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.Refresh_PARTITION_TABLEMETADATA, renameRefreshPartitiontablemetada);

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
        getallftablesinschema.addColumn("parttype");
        getallftablesinschema.addColumn("partkey");

        getallftablesinschema.addRow(new Object[] {1, "MyTable", 1, 1, true,
                "description", "1 2", "", "gsfs", "n", "abc"});

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

    public static void mockCheckDebugSupport(PreparedStatementResultSetHandler preparedstatementHandler) {
        MockResultSet debugCheckTest = preparedstatementHandler.createResultSet();
        debugCheckTest.addColumn("count");
        debugCheckTest.addRow(new Object[] {0});
        preparedstatementHandler.prepareResultSet(
                CHECK_DEBUG, debugCheckTest);
    }
    
    public static void mockCheckDebugSupportValid(PreparedStatementResultSetHandler preparedstatementHandler) {
        MockResultSet debugCheckTest = preparedstatementHandler.createResultSet();
        debugCheckTest.addColumn("count");
        debugCheckTest.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(
                CHECK_DEBUG, debugCheckTest);
    }

    public static void mockCheckExplainPlanSupport(PreparedStatementResultSetHandler preparedstatementHandler) {
        MockResultSet debugCheckTest = preparedstatementHandler.createResultSet();
        debugCheckTest.addColumn("QUERY PLAN");
        debugCheckTest.addRow(new Object[] {"Result  (cost=0.00..0.01 rows=1 width=0)"});
        preparedstatementHandler.prepareResultSet(
                CHECK_EXPLAIN_PLAN, debugCheckTest);
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

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(16), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10, "plpgsql"});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

    }
    
    public static void createServerObjectsNoPrivilege(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname, pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_type typ ON typ.oid=prorettype JOIN pg_namespace typns ON typns.oid=typ.typnamespace JOIN pg_language lng ON lng.oid=prolang  WHERE lng.lanname in ('plpgsql','sql') and pr.pronamespace= 1 ORDER BY objname";

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

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(16), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10, "plpgsql"});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);
        
        String GET_VIEWS = "SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and c.relnamespace = 1;";
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("nspoid");
        getdbsrs.addColumn("schemaname");
        getdbsrs.addColumn("viewname");
        getdbsrs.addColumn("viewowner");
        getdbsrs.addRow(new Object[] {11, 10, "public", "public", "owner1"});
        preparedstatementHandler.prepareResultSet(GET_VIEWS,
                getdbsrs);
    }

    public static void refreshDebugObjectRS2(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_language lng ON lng.oid=pr.prolang WHERE lng.lanname in ('plpgsql','sql','c')  and oid = 1 and has_function_privilege(pr.oid, 'EXECUTE')";

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

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(2), "function2", 1,
                new Integer(2279), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10, "plpgsql"});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

    }

    public static void refreshDebugObjectRS3(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {

        String REFRESH_DBG_OBJ_QRY = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, lng.lanname lang FROM pg_proc pr JOIN pg_language lng ON lng.oid=pr.prolang WHERE lng.lanname in ('plpgsql','sql','c')  and oid = 1 and has_function_privilege(pr.oid, 'EXECUTE')";
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

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

        listnerResultSet.addRow(new Object[] {new Integer(2), null, 1,
                new Integer(2279), null, null, null, null, "mode", false,
                "volatile", false, false, 10, 10, "plpgsql"});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);

    }

    public static void fetchNamespaceRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "SELECT oid, nspname from pg_namespace WHERE oid=3";

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
        
        String FETCH_CLM_QUERY="WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = 1and relkind = 'r' and parttype ='n'), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr))select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";

        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("tableid");
        colmetadataRS.addColumn("namespaceid");
        colmetadataRS.addColumn("columnidx");
        colmetadataRS.addColumn("name");
        colmetadataRS.addColumn("displayColumns");
        colmetadataRS.addColumn("datatypeoid");
        colmetadataRS.addColumn("dtns");
        colmetadataRS.addColumn("length");
        colmetadataRS.addColumn("precision");
        colmetadataRS.addColumn("dimentions");
        colmetadataRS.addColumn("notnull");
        colmetadataRS.addColumn("isdefaultvalueavailable");
        colmetadataRS.addColumn("default_value");
        colmetadataRS.addColumn("attDefStr");

        colmetadataRS.addRow(new Object[] {1, 1, 1, "ColName", "", 1, 1, 200, 0, 0,
                false, true, "Default value", ""});
        preparedstatementHandler.prepareResultSet(FETCH_CLM_QUERY,
                colmetadataRS);
        
        
        String fetchTableClmQuery = "select oid as tableid from pg_class where relnamespace=1 and relkind = 'r' and parttype ='n'";
        MockResultSet fetchViewRS = preparedstatementHandler.createResultSet();
        fetchViewRS.addColumn("tableid");
        fetchViewRS.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(fetchTableClmQuery, fetchViewRS);
        
        String fecthForeignTableColumns="WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = 1and relkind = 'f' and parttype ='n'), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr))select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";
        MockResultSet fecthForeignTableColumnsRS = preparedstatementHandler.createResultSet();
        fecthForeignTableColumnsRS.addColumn("tableid");
        fecthForeignTableColumnsRS.addColumn("namespaceid");
        fecthForeignTableColumnsRS.addColumn("datatypeoid");
        fecthForeignTableColumnsRS.addColumn("dtns");
        fecthForeignTableColumnsRS.addColumn("columnidx");
        fecthForeignTableColumnsRS.addColumn("name");
        fecthForeignTableColumnsRS.addColumn("dimentions");
        fecthForeignTableColumnsRS.addColumn("notnull");
        fecthForeignTableColumnsRS.addColumn("isdefaultvalueavailable");
        fecthForeignTableColumnsRS.addColumn("attDefStr");
        fecthForeignTableColumnsRS.addColumn("default_value");
        fecthForeignTableColumnsRS.addColumn("displayColumns");
        fecthForeignTableColumnsRS.addColumn("precision");
        fecthForeignTableColumnsRS.addColumn("length");
        fecthForeignTableColumnsRS.addRow(new Object[] {1,1,1,1,1,"col1",1,false,false,null,"default","col1",1,0});
        preparedstatementHandler.prepareResultSet(fecthForeignTableColumns, fecthForeignTableColumnsRS);
        
        String partTableColumns="WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = 1and relkind <> 'i' and parttype in ('p', 'v')), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select tableid from tbl) and d.adnum in( select columnidx from attr))select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";
        preparedstatementHandler.prepareResultSet(partTableColumns, fecthForeignTableColumnsRS);
        
        String str="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=1 and c.attisdropped = 'f' and c.attnum > 0;";
    
        MockResultSet fetchViewRS1 = preparedstatementHandler.createResultSet();
        fetchViewRS1.addColumn("columnidx");
        fetchViewRS1.addColumn("name");
        fetchViewRS1.addColumn("displayColumns");
        fetchViewRS1.addColumn("datatypeoid");
        fetchViewRS1.addColumn("length");
        fetchViewRS1.addColumn("precision");
        fetchViewRS1.addColumn("dimentions");
        fetchViewRS1.addColumn("notnull");
        fetchViewRS1.addColumn("isdefaultvalueavailable");

        

        fetchViewRS1.addRow(new Object[] {1, "ColName", "",1, 200, 0, 0, false,true});
        preparedstatementHandler.prepareResultSet(str, fetchViewRS1);
        
        String str5="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=2 and c.attisdropped = 'f' and c.attnum > 0;";
        preparedstatementHandler.prepareResultSet(str5, fetchViewRS1);
        
        String str1="select typ.typnamespace as dtns from pg_type typ  where typ.oid=2";
        MockResultSet fetchViewRS2 = preparedstatementHandler.createResultSet();
        fetchViewRS2.addColumn("dtns");
        fetchViewRS2.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(str1, fetchViewRS2);
        
        String str2="select typ.typnamespace as dtns from pg_type typ  where typ.oid=1";
        preparedstatementHandler.prepareResultSet(str2, fetchViewRS2);
        
        
        String str3="select d.adsrc as default_value, d.adbin as attDefStr  from pg_attrdef d  where d.adrelid=1 and d.adnum=1";
        MockResultSet fetchViewRS3 = preparedstatementHandler.createResultSet();
        fetchViewRS3.addColumn("default_value");
        fetchViewRS3.addColumn("attDefStr");
        fetchViewRS3.addRow(new Object[] {"Default value",""});
        preparedstatementHandler.prepareResultSet(str3, fetchViewRS3);

    }
    
    public static void tableColumnsQueryNew(PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
        colmetadataRS.addColumn("relative");
        colmetadataRS.addRow(new Object[] {1, "tblspc",
                new String[] {"spcoptions=options"}, "location", "spcmaxsize", false});
        preparedstatementHandler.prepareResultSet(TBL_SPC_META, colmetadataRS);
        
        MockResultSet colmetadataRS_all = preparedstatementHandler
                .createResultSet();
        colmetadataRS_all.addColumn("oid");
        colmetadataRS_all.addColumn("spcname");
        colmetadataRS_all.addColumn("spcoptions");
        colmetadataRS_all.addColumn("location");
        colmetadataRS_all.addColumn("spcmaxsize");
        colmetadataRS_all.addColumn("relative");

        colmetadataRS_all.addRow(new Object[] {1, "tblspc",
                new String[] {"spcoptions=options"}, "location", "spcmaxsize", false});
        preparedstatementHandler.prepareResultSet(FETCH_ALL_TABLESPACE_ALL, colmetadataRS_all);
        
        MockResultSet colmetadataRS_byId = preparedstatementHandler
                .createResultSet();
        colmetadataRS_byId.addColumn("oid");
        colmetadataRS_byId.addColumn("spcname");
        colmetadataRS_byId.addColumn("spcoptions");
        colmetadataRS_byId.addColumn("location");
        colmetadataRS_byId.addColumn("spcmaxsize");
        colmetadataRS_byId.addColumn("relative");
        colmetadataRS_byId.addRow(new Object[] {1, "tblspc",
                new String[] {"spcoptions=options"}, "location", "spcmaxsize", false});
        preparedstatementHandler.prepareResultSet(FETCH_ALL_TABLESPACE_BY_ID, colmetadataRS_byId);

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
        String FETCH_CLM_QUERY="WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = 1and relkind = 'r' and parttype ='n'), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr))select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";
        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("tableid");
        colmetadataRS.addColumn("namespaceid");
        colmetadataRS.addColumn("columnidx");
        colmetadataRS.addColumn("name");
        colmetadataRS.addColumn("displayColumns");
        colmetadataRS.addColumn("datatypeoid");
        colmetadataRS.addColumn("dtns");
        colmetadataRS.addColumn("length");
        colmetadataRS.addColumn("precision");
        colmetadataRS.addColumn("dimentions");
        colmetadataRS.addColumn("notnull");
        colmetadataRS.addColumn("isdefaultvalueavailable");
        colmetadataRS.addColumn("default_value");
        colmetadataRS.addColumn("attDefStr");
        
        
        

        colmetadataRS.addRow(new Object[] {1, 1, 1, "ColName","", 1, 1, 200, 0, 0,
                false, true, "Default value",""});
        preparedstatementHandler.prepareResultSet(FETCH_CLM_QUERY,
                colmetadataRS);
        
        String fetchViewQuery ="WITH tbl AS ( select oid as viewid,relnamespace as namespaceid from pg_class where relnamespace =1 and relkind = 'v'), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select viewid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr)) select t.viewid as viewid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.viewid = c.tableoid) LEFT JOIN attrdef d ON(t.viewid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.viewid ,c.columnidx;";
        MockResultSet fetchViewQueryRS = preparedstatementHandler.createResultSet();
        fetchViewQueryRS.addColumn("viewid");
        fetchViewQueryRS.addColumn("datatypeoid");
        fetchViewQueryRS.addColumn("dtns");
        fetchViewQueryRS.addColumn("name");
        fetchViewQueryRS.addColumn("dimentions");
        fetchViewQueryRS.addColumn("notnull");
        fetchViewQueryRS.addColumn("default_value");
        fetchViewQueryRS.addColumn("displayColumns");
        fetchViewQueryRS.addColumn("length");
        fetchViewQueryRS.addColumn("precision");
        fetchViewQueryRS.addColumn("columnidx");
        fetchViewQueryRS.addRow(new Object[] {1,1,1,"col1",1,false,"defaultvalue","col1",1,0,1});
        preparedstatementHandler.prepareResultSet(fetchViewQuery, fetchViewQueryRS);
        
       String FOREIGN_TABLE_COLM_QRY = "WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = 1and relkind = 'f' and parttype ='n'), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr))select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";
        MockResultSet fecthForeignTableColumnsRS = preparedstatementHandler.createResultSet();
        fecthForeignTableColumnsRS.addColumn("tableid");
        fecthForeignTableColumnsRS.addColumn("namespaceid");
        fecthForeignTableColumnsRS.addColumn("datatypeoid");
        fecthForeignTableColumnsRS.addColumn("dtns");
        fecthForeignTableColumnsRS.addColumn("columnidx");
        fecthForeignTableColumnsRS.addColumn("name");
        fecthForeignTableColumnsRS.addColumn("dimentions");
        fecthForeignTableColumnsRS.addColumn("notnull");
        fecthForeignTableColumnsRS.addColumn("isdefaultvalueavailable");
        fecthForeignTableColumnsRS.addColumn("attDefStr");
        fecthForeignTableColumnsRS.addColumn("default_value");
        fecthForeignTableColumnsRS.addColumn("displayColumns");
        fecthForeignTableColumnsRS.addColumn("precision");
        fecthForeignTableColumnsRS.addColumn("length");
        fecthForeignTableColumnsRS.addRow(new Object[] {1,1,1,1,1,"\"ColName\"",1,false,false,null,"default","col1",1,0});
        preparedstatementHandler.prepareResultSet(FOREIGN_TABLE_COLM_QRY, fecthForeignTableColumnsRS);
        String fetchTableClmQuery = "select oid as tableid from pg_class where relnamespace=1 and relkind = 'r' and parttype ='n'";
        MockResultSet fetchViewRS = preparedstatementHandler.createResultSet();
        fetchViewRS.addColumn("tableid");
        fetchViewRS.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(fetchTableClmQuery, fetchViewRS);
        
        String fecthForeignTableColumns=" WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = 1and relkind = 'f' and parttype ='n'), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr))select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx; ";
        preparedstatementHandler.prepareResultSet(fecthForeignTableColumns, fecthForeignTableColumnsRS);
        
        String partTableColumns=" WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = 1and relkind <> 'i' and parttype in ('p', 'v')), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select tableid from tbl) and d.adnum in( select columnidx from attr))select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";
        preparedstatementHandler.prepareResultSet(partTableColumns, fecthForeignTableColumnsRS);
        
        String str="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=1 and c.attisdropped = 'f' and c.attnum > 0;";
    
        MockResultSet fetchViewRS1 = preparedstatementHandler.createResultSet();
        fetchViewRS1.addColumn("columnidx");
        fetchViewRS1.addColumn("name");
        fetchViewRS1.addColumn("displayColumns");
        fetchViewRS1.addColumn("datatypeoid");
        fetchViewRS1.addColumn("length");
        fetchViewRS1.addColumn("precision");
        fetchViewRS1.addColumn("dimentions");
        fetchViewRS1.addColumn("notnull");
        fetchViewRS1.addColumn("isdefaultvalueavailable");

        

        fetchViewRS1.addRow(new Object[] {1, "ColName", "",1, 200, 0, 0, false,true});
        preparedstatementHandler.prepareResultSet(str, fetchViewRS1);
        
        String str5="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=2 and c.attisdropped = 'f' and c.attnum > 0;";
        preparedstatementHandler.prepareResultSet(str5, fetchViewRS1);
        
        String str1="select typ.typnamespace as dtns from pg_type typ  where typ.oid=2";
        MockResultSet fetchViewRS2 = preparedstatementHandler.createResultSet();
        fetchViewRS2.addColumn("dtns");
        fetchViewRS2.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(str1, fetchViewRS2);
        
        String str2="select typ.typnamespace as dtns from pg_type typ  where typ.oid=1";
        preparedstatementHandler.prepareResultSet(str2, fetchViewRS2);
        
        
        String str3="select d.adsrc as default_value, d.adbin as attDefStr  from pg_attrdef d  where d.adrelid=1 and d.adnum=1";
        MockResultSet fetchViewRS3 = preparedstatementHandler.createResultSet();
        fetchViewRS3.addColumn("default_value");
        fetchViewRS3.addColumn("attDefStr");
        fetchViewRS3.addRow(new Object[] {"Default value",""});
        preparedstatementHandler.prepareResultSet(str3, fetchViewRS3);

    }

    public static void getCountRS(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "SELECT count(*) as cnt FROM pg_catalog.\"MyTable\";";

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
        String REFRESH_DBG_OBJ_QRY = "SELECT * FROM pg_catalog.\"MyTable\" LIMIT 1000 OFFSET 0;";

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
        String view="select oid as viewid, relnamespace as namespaceid from pg_class where relnamespace=1 and relkind = 'v'";
        MockResultSet fecthviewColumnsRS = preparedstatementHandler.createResultSet();
        fecthviewColumnsRS.addColumn("viewid");
        fecthviewColumnsRS.addColumn("namespaceid");
        fecthviewColumnsRS.addRow(new Object[] {1,1});
        preparedstatementHandler.prepareResultSet(view, fecthviewColumnsRS);
        
        String fetchTableClmQuery = "select oid as tableid from pg_class where relnamespace=1 and relkind = 'r' and parttype ='n'";
        MockResultSet fetchViewRS = preparedstatementHandler.createResultSet();
        fetchViewRS.addColumn("tableid");
        fetchViewRS.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(fetchTableClmQuery, fetchViewRS);
        
        String fecthForeignTableColumns=" select oid as tableid, relnamespace as namespaceid from pg_class where relnamespace=1 and relkind = 'f' and parttype ='n'; ";
        MockResultSet fecthForeignTableColumnsRS = preparedstatementHandler.createResultSet();
        fecthForeignTableColumnsRS.addColumn("tableid");
        fecthForeignTableColumnsRS.addColumn("namespaceid");
        fecthForeignTableColumnsRS.addRow(new Object[] {1,1});
        preparedstatementHandler.prepareResultSet(fecthForeignTableColumns, fecthForeignTableColumnsRS);
        
        String partTableColumns=" select oid as tableid, relnamespace as namespaceid from pg_class where relnamespace=1 and relkind <> 'i' and parttype in ('p', 'v');";
        preparedstatementHandler.prepareResultSet(partTableColumns, fecthForeignTableColumnsRS);
        
        String str="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=1 and c.attisdropped = 'f' and c.attnum > 0;";
    
        MockResultSet fetchViewRS1 = preparedstatementHandler.createResultSet();
        fetchViewRS1.addColumn("columnidx");
        fetchViewRS1.addColumn("name");
        fetchViewRS1.addColumn("displayColumns");
        fetchViewRS1.addColumn("datatypeoid");
        fetchViewRS1.addColumn("length");
        fetchViewRS1.addColumn("precision");
        fetchViewRS1.addColumn("dimentions");
        fetchViewRS1.addColumn("notnull");
        fetchViewRS1.addColumn("isdefaultvalueavailable");

        

        fetchViewRS1.addRow(new Object[] {1, "ColName", "",1, 200, 0, 0, false,true});
        preparedstatementHandler.prepareResultSet(str, fetchViewRS1);
        
        String str5="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=2 and c.attisdropped = 'f' and c.attnum > 0;";
        preparedstatementHandler.prepareResultSet(str5, fetchViewRS1);
        
        String str1="select typ.typnamespace as dtns from pg_type typ  where typ.oid=2";
        MockResultSet fetchViewRS2 = preparedstatementHandler.createResultSet();
        fetchViewRS2.addColumn("dtns");
        fetchViewRS2.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(str1, fetchViewRS2);
        
        String str2="select typ.typnamespace as dtns from pg_type typ  where typ.oid=1";
        preparedstatementHandler.prepareResultSet(str2, fetchViewRS2);
        
        
        String str3="select d.adsrc as default_value, d.adbin as attDefStr  from pg_attrdef d  where d.adrelid=1 and d.adnum=1";
        MockResultSet fetchViewRS3 = preparedstatementHandler.createResultSet();
        fetchViewRS3.addColumn("default_value");
        fetchViewRS3.addColumn("attDefStr");
        fetchViewRS3.addRow(new Object[] {"Default value",""});
        preparedstatementHandler.prepareResultSet(str3, fetchViewRS3);
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

    public static void fetchTriggerQuery(PreparedStatementResultSetHandler preparedstatementHandler) {
        String fetchTriggerQuery = "select t.oid as oid, t.tgrelid as tableoid, t.tgname as name, t.tgfoid as functionoid, t.tgtype as tgtype, t.tgenabled as tgenable, pg_get_triggerdef(t.oid) as ddlmsg from pg_trigger t, pg_class c where t.tgrelid = c.oid and c.relnamespace=?";
        MockResultSet fetchTriggerRS = preparedstatementHandler.createResultSet();
        fetchTriggerRS.addColumn("oid");
        fetchTriggerRS.addColumn("tableoid");
        fetchTriggerRS.addColumn("name");
        fetchTriggerRS.addColumn("functionoid");
        fetchTriggerRS.addColumn("tgtype");
        fetchTriggerRS.addColumn("tgenable");
        fetchTriggerRS.addColumn("ddlmsg");
        fetchTriggerRS.addRow(new Object[] {1, 1, "trigger1", 1, 1, true, ""});
        preparedstatementHandler.prepareResultSet(fetchTriggerQuery, fetchTriggerRS);
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
        		"with x as (SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and c.relnamespace = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                getdbsrs);
        
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
                        + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'm'::\"char\") ",
                        getdbsrs1);

        addViewColumn(preparedstatementHandler, 2, 1);
        addViewColumn(preparedstatementHandler, 11, 1);
        addViewColumn(preparedstatementHandler, 22, 1);
        addViewColumn1(preparedstatementHandler);

        MockResultSet viewDdlRs = preparedstatementHandler.createResultSet();
        viewDdlRs.addColumn("definition");
        viewDdlRs.addRow(new Object[] {"select * from something"});
        preparedstatementHandler
                .prepareResultSet("SELECT * FROM pg_get_viewdef('pg_catalog.mytestview2')", viewDdlRs);

        MockResultSet viewDdlRs2 = preparedstatementHandler.createResultSet();
        viewDdlRs2.addColumn("definition");
        viewDdlRs2.addRow(
                new Object[] {"SELECT * FROM t1 WHERE kind = 'Comedy';"});
        preparedstatementHandler.prepareResultSet(
                "SELECT * FROM pg_get_viewdef(25)", viewDdlRs2);
        addViewColumn(preparedstatementHandler, 1, 1);

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
                "with x as (SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and c.relnamespace = 1) select * from x where has_table_privilege(x.oid,'SELECT');",
                getdbsrs);

        MockResultSet getdbsrs1 = preparedstatementHandler.createResultSet();
        getdbsrs1.addColumn("oid");
        getdbsrs1.addColumn("nspoid");
        getdbsrs1.addColumn("schemaname");
        getdbsrs1.addColumn("viewname");
        getdbsrs1.addColumn("viewowner");
        getdbsrs1.addRow(new Object[] {11, 10, "public", schemaname, "owner1"});
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                        + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'v'::\"char\") ",
                        getdbsrs1);
        
        MockResultSet getdbsrs2 = preparedstatementHandler.createResultSet();
        getdbsrs2.addColumn("oid");
        getdbsrs2.addColumn("nspoid");
        getdbsrs2.addColumn("schemaname");
        getdbsrs2.addColumn("viewname");
        getdbsrs2.addColumn("viewowner");
        getdbsrs2.addRow(new Object[] {11, 10, "public", schemaname, "owner1"});
        preparedstatementHandler.prepareResultSet(
                "SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and c.relnamespace = 1;",
                        getdbsrs2);
        addViewColumn(preparedstatementHandler, 2, 1);
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
        //namespaceRS.addRow(new Object[] {1, "pg_catalog"});
       // namespaceRS.addRow(new Object[] {2, "information_schema"});
        namespaceRS.addRow(new Object[] {10, "Schema_One"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_NAMESPACE, namespaceRS);
        
        MockResultSet namespaceRS_all = preparedstatementHandler.createResultSet();
        namespaceRS_all.addColumn("oid");
        namespaceRS_all.addColumn("nspname");
        namespaceRS_all.addRow(new Object[] {10, "Schema_One"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_USERNAMESPACE_ALL, namespaceRS_all);
        
        
    }

   /* public static void getSchemaTwo(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
         namespaceRS.addRow(new Object[]{10, "Schema_One"}); 
        namespaceRS.addRow(new Object[] {11, "Schema_Two"});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_ALL_NAMESPACE, namespaceRS);
    }*/

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
        
        String view="select oid as viewid, relnamespace as namespaceid from pg_class where relnamespace=1 and relkind = 'v'";
        MockResultSet fecthViewColumnsRS = preparedstatementHandler.createResultSet();
        fecthViewColumnsRS.addColumn("viewid");
        fecthViewColumnsRS.addColumn("namespaceid");
        fecthViewColumnsRS.addRow(new Object[] {1,1});
        preparedstatementHandler.prepareResultSet(view, fecthViewColumnsRS);
        
        String fetchTableClmQuery = "select oid as tableid from pg_class where relnamespace=1 and relkind = 'r' and parttype ='n'";
        MockResultSet fetchViewRS = preparedstatementHandler.createResultSet();
        fetchViewRS.addColumn("tableid");
        fetchViewRS.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(fetchTableClmQuery, fetchViewRS);
        
        String fecthForeignTableColumns=" select oid as tableid, relnamespace as namespaceid from pg_class where relnamespace=1 and relkind = 'f' and parttype ='n'; ";
        MockResultSet fecthForeignTableColumnsRS = preparedstatementHandler.createResultSet();
        fecthForeignTableColumnsRS.addColumn("tableid");
        fecthForeignTableColumnsRS.addColumn("namespaceid");
        fecthForeignTableColumnsRS.addRow(new Object[] {1,1});
        preparedstatementHandler.prepareResultSet(fecthForeignTableColumns, fecthForeignTableColumnsRS);
        
        String partTableColumns=" select oid as tableid, relnamespace as namespaceid from pg_class where relnamespace=1 and relkind <> 'i' and parttype in ('p', 'v');";
        preparedstatementHandler.prepareResultSet(partTableColumns, fecthForeignTableColumnsRS);
        
        String str="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=1 and c.attisdropped = 'f' and c.attnum > 0;";
    
        MockResultSet fetchViewRS1 = preparedstatementHandler.createResultSet();
        fetchViewRS1.addColumn("columnidx");
        fetchViewRS1.addColumn("name");
        fetchViewRS1.addColumn("displayColumns");
        fetchViewRS1.addColumn("datatypeoid");
        fetchViewRS1.addColumn("length");
        fetchViewRS1.addColumn("precision");
        fetchViewRS1.addColumn("dimentions");
        fetchViewRS1.addColumn("notnull");
        fetchViewRS1.addColumn("isdefaultvalueavailable");

        

        fetchViewRS1.addRow(new Object[] {1, "ColName", "",1, 200, 0, 0, false,true});
        preparedstatementHandler.prepareResultSet(str, fetchViewRS1);
        
        String str5="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=2 and c.attisdropped = 'f' and c.attnum > 0;";
        preparedstatementHandler.prepareResultSet(str5, fetchViewRS1);
        
        String str1="select typ.typnamespace as dtns from pg_type typ  where typ.oid=2";
        MockResultSet fetchViewRS2 = preparedstatementHandler.createResultSet();
        fetchViewRS2.addColumn("dtns");
        fetchViewRS2.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(str1, fetchViewRS2);
        
        String str2="select typ.typnamespace as dtns from pg_type typ  where typ.oid=1";
        preparedstatementHandler.prepareResultSet(str2, fetchViewRS2);
        
        
        String str3="select d.adsrc as default_value, d.adbin as attDefStr  from pg_attrdef d  where d.adrelid=1 and d.adnum=1";
        MockResultSet fetchViewRS3 = preparedstatementHandler.createResultSet();
        fetchViewRS3.addColumn("default_value");
        fetchViewRS3.addColumn("attDefStr");
        fetchViewRS3.addRow(new Object[] {"Default value",""});
        preparedstatementHandler.prepareResultSet(str3, fetchViewRS3);
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
        String REFRESH_DBG_OBJ_QRY = "select headerlines, definition from PG_GET_FUNCTIONDEF(1);";

        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();

        listnerResultSet.addColumn("headerlines");
        listnerResultSet.addColumn("definition");
        listnerResultSet.addColumn("xmin");
        listnerResultSet.addColumn("cmin");

        listnerResultSet.addRow(
                new Object[] {new Integer(1), new Integer(1)});

        preparedstatementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);
        
        String query2 = "select xmin1, cmin1 from pldbg_get_funcVer(" + 1 + ")";
        MockResultSet versionRS = preparedstatementHandler.createResultSet();
        versionRS.addRow(new Object[] {null, null});
        preparedstatementHandler.prepareResultSet(query2, versionRS);

    }
    
    public static void getOwnerId(
            StatementResultSetHandler statementHandler)
    {
        String REFRESH_DBG_OBJ_QRY = "select relacl,relowner from pg_catalog.pg_class  where oid=1 and relname='test'";

        MockResultSet listnerResultSet = statementHandler
                .createResultSet();

        listnerResultSet.addColumn("relacl");
        listnerResultSet.addColumn("relowner");

        listnerResultSet.addRow(
                new Object[] {new String("{dsuser=ar*w*dDxt/dsuser}"),new Long(1)});

        statementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);
        
        
        String qry="SELECT rolname FROM pg_catalog.pg_roles WHERE oid =1";
        MockResultSet listnerResultSet1 = statementHandler
                .createResultSet();

        listnerResultSet1.addColumn("rolname");

        listnerResultSet1.addRow(
                new Object[] {new String("dsuser")});

        statementHandler.prepareResultSet(qry,
                listnerResultSet1);
        
    }
    
    public static void getTableDDL(
            PreparedStatementResultSetHandler preparedstatementHandler) {
        String qry="SELECT * FROM pg_get_tabledef('pg_catalog.test')";
        MockResultSet listnerResultSet1 = preparedstatementHandler
                .createResultSet();

        listnerResultSet1.addColumn("rolname");

        listnerResultSet1.addRow(
                new Object[] {new String("SET search_path = dsuser;\r\n" + 
                        "CREATE  TABLE \"TempTable\" (\r\n" + 
                        "    id integer,\r\n" + 
                        "    c1 integer,\r\n" + 
                        "    c2 integer\r\n" + 
                        ")\r\n" + 
                        "WITH (orientation=row, compression=no)\r\n" + 
                        "DISTRIBUTE BY HASH(id)\r\n" + 
                        "TO GROUP group_version1; ")});

        preparedstatementHandler.prepareResultSet(qry,
                listnerResultSet1);
        
        String tablespaceQuery="select spcname from pg_class as class left join pg_tablespace as tablespace on class.reltablespace=tablespace.oid where class.oid=1";
        MockResultSet rs = preparedstatementHandler
                 .createResultSet();
 
         rs.addColumn("spcname");
 
         rs.addRow(
                 new Object[] {new String("test")});
 
         preparedstatementHandler.prepareResultSet(tablespaceQuery,
                 rs);
    }
    public static void getViewDDL(
            PreparedStatementResultSetHandler preparedstatementHandler) {
        String qry="SELECT * FROM pg_get_viewdef('schema.test')";
        MockResultSet listnerResultSet1 = preparedstatementHandler
                .createResultSet();

        listnerResultSet1.addColumn("rolname");

        listnerResultSet1.addRow(
                new Object[] {new String("\r\n" + 
                        "-- Name: abc_view111; Type: View; Schema: dsuser;\r\n" + 
                        "\r\n" + 
                        "CREATE OR REPLACE VIEW schema.test\r\n" + 
                        "    AS \r\n" + 
                        "SELECT  * FROM xyz abc;")});

        preparedstatementHandler.prepareResultSet(qry,
                listnerResultSet1);
    }
    
    public static void getSequenceDDL(PreparedStatementResultSetHandler preparedstatementHandler) {
        String qry = "SELECT sequence_name,start_value, increment_by, CASE WHEN increment_by > 0 AND max_value = 2147483647 THEN NULL      WHEN increment_by < 0 AND max_value = -1 THEN NULL  ELSE max_value END AS max_value, CASE WHEN increment_by > 0 AND min_value = 1 THEN NULL WHEN increment_by < 0 AND min_value = -2147483648 THEN NULL  ELSE min_value END AS min_value, cache_value, is_cycled FROM schema.test;";
       
        MockResultSet listnerResultSet1 = preparedstatementHandler
                .createResultSet();

        listnerResultSet1.addColumn("sequence_name");
        listnerResultSet1.addColumn("start_value");
        listnerResultSet1.addColumn("increment_by");
        listnerResultSet1.addColumn("max_value");
        listnerResultSet1.addColumn("min_value");
        listnerResultSet1.addColumn("cache_value");
        listnerResultSet1.addColumn("is_cycled");

        listnerResultSet1.addRow(
                new Object[] {"seq","2","2","12","2","2",false});

        preparedstatementHandler.prepareResultSet(qry,
                listnerResultSet1);
        
        String qury1="select d.refobjid as tableid,d.refobjsubid as clmidx FROM pg_class c  LEFT JOIN pg_depend d ON (c.relkind = 'S' AND d.classid = c.tableoid AND d.objid = c.oid AND d.objsubid = 0 AND d.refclassid = c.tableoid AND d.deptype = 'a') where c.oid=1";
        MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();

        listnerResultSet.addColumn("tableid");
        listnerResultSet.addColumn("clmidx");

        listnerResultSet.addRow(
                new Object[] {1,2});

        preparedstatementHandler.prepareResultSet(qury1,
                listnerResultSet);
        
        
       String qry1="select relname, attname  from pg_class as c  LEFT JOIN pg_catalog.pg_attribute as att ON (c.oid = att.attrelid) where attrelid = 1  and attnum = 2";
       MockResultSet listnerResultSet2 = preparedstatementHandler
               .createResultSet();

       listnerResultSet2.addColumn("relname");
       listnerResultSet2.addColumn("attname");

       listnerResultSet2.addRow(
               new Object[] {"test","clm"});

       preparedstatementHandler.prepareResultSet(qry1,
               listnerResultSet2);
    }
    
    public static void getSequenceDDLData(PreparedStatementResultSetHandler preparedstatementHandler) {
        String qry = "SELECT pg_catalog.nextval('schema.test')";
        MockResultSet listnerResultSet1 = preparedstatementHandler.createResultSet();
        listnerResultSet1.addColumn("nextval");
        listnerResultSet1.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(qry, listnerResultSet1);
    }
    
    public static void getNamespaceDDL(
            PreparedStatementResultSetHandler preparedstatementHandler, AbstractResultSetHandler statementHandler) {
        String qry=" SELECT description FROM pg_catalog.pg_description where objoid=1";
        MockResultSet listnerResultSet1 = preparedstatementHandler
                .createResultSet();

        listnerResultSet1.addColumn("description");

        listnerResultSet1.addRow(
                new Object[] {new String("Added comments")});

        preparedstatementHandler.prepareResultSet(qry,
                listnerResultSet1);
        

        String REFRESH_DBG_OBJ_QRY = "select nspowner,nspacl from pg_namespace where nspname='schema';";

        MockResultSet listnerResultSet = statementHandler
                .createResultSet();

        listnerResultSet.addColumn("nspacl");
        listnerResultSet.addColumn("nspowner");

        listnerResultSet.addRow(
                new Object[] {new String("{=arwdDxt/dsuser}"),new Long(1)});

        statementHandler.prepareResultSet(REFRESH_DBG_OBJ_QRY,
                listnerResultSet);
        
        
        String qry1="SELECT rolname FROM pg_catalog.pg_roles WHERE oid =1";
        MockResultSet listnerResultSet11 = statementHandler
                .createResultSet();

        listnerResultSet11.addColumn("rolname");

        listnerResultSet11.addRow(
                new Object[] {new String("dsuser")});

        statementHandler.prepareResultSet(qry1,
                listnerResultSet11);
        
    
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

        ObjectParameter retParam = new ObjectParameter();
        retParam.setDataType("int");
        retParam.setName("a");
        retParam.setType(PARAMETERTYPE.IN);

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
        String GET_PARTITIONS_OVERLOADED = "select p.oid AS partition_id , p.relname AS partition_name, p.partstrategy as partition_type, p.parentid AS table_id"
                + "  from pg_class c, pg_partition p  where c.oid =  0 and c.parttype = 'p' "
                + " and p.parentid = c.oid  and p.parttype = 'p'  order by p.boundaries;";

        MockResultSet getPartitionsOverloadedRS = preparedstatementHandler
                .createResultSet();
        getPartitionsOverloadedRS.addColumn("partition_id");
        getPartitionsOverloadedRS.addColumn("partition_name");
        getPartitionsOverloadedRS.addColumn("partition_type");
        getPartitionsOverloadedRS.addColumn("table_id");
        getPartitionsOverloadedRS.addRow(new Object[] {1, "part_1", "r", 1});
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
        String qry = "with x as ("
        		+ "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r' and tbl.relnamespace = 1"
        		+ " and oid in ("
        		+ "select pcrelid from pgxc_class "
        		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
        		+ ") select * from x "
        		+ "where has_table_privilege(x.oid,'SELECT');";
        
        MockResultSet refreshtablemetadatainnamspace = preparedstatementHandler
                .createResultSet();
        refreshtablemetadatainnamspace.addColumn("oid");
        refreshtablemetadatainnamspace.addColumn("relname");
        refreshtablemetadatainnamspace.addColumn("relpersistence");
        refreshtablemetadatainnamspace.addColumn("reloptions");
        refreshtablemetadatainnamspace.addColumn("parttype");
        refreshtablemetadatainnamspace.addColumn("partkey");
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(qry,
                refreshtablemetadatainnamspace);
        
        String qry1 = "with x as (select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r' and tbl.relnamespace = 1 and oid in (select pcrelid from pgxc_class where has_nodegroup_privilege(pgroup, 'USAGE'))) select * from x where has_table_privilege(x.oid,'SELECT');";
        MockResultSet refreshtablemetadatainnamspace1 = preparedstatementHandler
                .createResultSet();
        refreshtablemetadatainnamspace1.addColumn("oid");
        refreshtablemetadatainnamspace1.addColumn("relname");
        refreshtablemetadatainnamspace1.addColumn("relpersistence");
        refreshtablemetadatainnamspace1.addColumn("reloptions");
        refreshtablemetadatainnamspace1.addColumn("parttype");
        refreshtablemetadatainnamspace1.addColumn("partkey");
        refreshtablemetadatainnamspace1
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(qry1,
                refreshtablemetadatainnamspace1);

    }
    public static void refreshTableinnamespaceAutoSuggest(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String qry1 = "with x as ("
        		+ "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r' and tbl.relnamespace = 1"
        		+ " and oid in ("
        		+ "select pcrelid from pgxc_class "
        		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
        		+ ") select * from x "
        		+ "where has_table_privilege(x.oid,'SELECT');";
        String qry2 = "with x as ("
        		+ "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r' and tbl.relnamespace = 2"
        		+ " and oid in ("
        		+ "select pcrelid from pgxc_class "
        		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
        		+ ") select * from x "
        		+ "where has_table_privilege(x.oid,'SELECT');";
        String qry3 = "with x as ("
        		+ "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r' and tbl.relnamespace = 3"
        		+ " and oid in ("
        		+ "select pcrelid from pgxc_class "
        		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
        		+ ") select * from x "
        		+ "where has_table_privilege(x.oid,'SELECT');";
        String qry4 = "with x as ("
        		+ "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r' and tbl.relnamespace = 4"
        		+ " and oid in ("
        		+ "select pcrelid from pgxc_class "
        		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
        		+ ") select * from x "
        		+ "where has_table_privilege(x.oid,'SELECT');";
        String qry5 = "with x as ("
        		+ "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r' and tbl.relnamespace = 5"
        		+ " and oid in ("
        		+ "select pcrelid from pgxc_class "
        		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
        		+ ") select * from x "
        		+ "where has_table_privilege(x.oid,'SELECT');";
        String qry6 = "with x as ("
        		+ "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,tbl.reloptions as reloptions, tbl.parttype as parttype, array_to_string(part.partkey,',') as partkey from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') where tbl.relkind = 'r' and tbl.relnamespace = 6"
        		+ " and oid in ("
        		+ "select pcrelid from pgxc_class "
        		+ "where has_nodegroup_privilege(pgroup, 'USAGE'))"
        		+ ") select * from x "
        		+ "where has_table_privilege(x.oid,'SELECT');";
        
        MockResultSet refreshtablemetadatainnamspace = preparedstatementHandler
                .createResultSet();
        refreshtablemetadatainnamspace.addColumn("oid");
        refreshtablemetadatainnamspace.addColumn("relname");
        refreshtablemetadatainnamspace.addColumn("relpersistence");
        refreshtablemetadatainnamspace.addColumn("reloptions");
        refreshtablemetadatainnamspace.addColumn("parttype");
        refreshtablemetadatainnamspace.addColumn("partkey");
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(qry1,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(qry2,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(qry3,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(qry4,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(qry5,
                refreshtablemetadatainnamspace);
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(qry6,
                refreshtablemetadatainnamspace);

    }

    public static void fetchViewQuery(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String fetchViewQuery = "with x as (SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner , c.relkind as relkind FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and c.relnamespace = 1) select * from x where has_table_privilege(x.oid,'SELECT')";
        MockResultSet fetchViewRS = preparedstatementHandler.createResultSet();
        fetchViewRS.addColumn("oid");
        fetchViewRS.addColumn("viewname");
        fetchViewRS.addColumn("viewowner");
        fetchViewRS.addColumn("relkind");
        fetchViewRS.addRow(new Object[] {1, "myview", "Gauss123", "v"});
        fetchViewRS.addRow(new Object[] {2, "mytestview2", "Gauss1234", "v"});
        fetchViewRS.addRow(new Object[] {11, "public", "owner1", "v"});
        preparedstatementHandler.prepareResultSet(fetchViewQuery, fetchViewRS);
    }

    public static void fetchViewQuery_1(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String fetchViewQuery = "with x as (SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and c.relnamespace = 1) select * from x where has_table_privilege(x.oid,'SELECT')";
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
                "1", "1", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_PARTITION_CONSTRAINT,
                partitionConstraintRS);

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
    {/*
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
        
        String REFRESH_TABLE = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx,"
                + " c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision,"
                + " c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value,"
                + " d.adbin as attDefStr "
                + "from pg_class t "
                + "left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r' and t.parttype ='n') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) "
                + "left join pg_type typ on (c.atttypid = typ.oid) "
                + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 1"
                + " order by t.oid, c.attnum;";

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
                
String REFRESH_TABLE1 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx,"
        + " c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
        + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision,"
        + " c.attndims as dimentions, "
        + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value,"
        + " d.adbin as attDefStr "
        + "from pg_class t "
        + "left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r' and t.parttype ='n') "
        + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) "
        + "left join pg_type typ on (c.atttypid = typ.oid) "
        + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 2"
        + " order by t.oid, c.attnum;";

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
                
                                String REFRESH_TABLE2 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx,"
                                        + " c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
                                        + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision,"
                                        + " c.attndims as dimentions, "
                                        + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value,"
                                        + " d.adbin as attDefStr "
                                        + "from pg_class t "
                                        + "left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r' and t.parttype ='n') "
                                        + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) "
                                        + "left join pg_type typ on (c.atttypid = typ.oid) "
                                        + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 3"
                                        + " order by t.oid, c.attnum;";

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

                                    String REFRESH_TABLE3 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx,"
                                            + " c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
                                            + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision,"
                                            + " c.attndims as dimentions, "
                                            + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value,"
                                            + " d.adbin as attDefStr "
                                            + "from pg_class t "
                                            + "left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r' and t.parttype ='n') "
                                            + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) "
                                            + "left join pg_type typ on (c.atttypid = typ.oid) "
                                            + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 4"
                                            + " order by t.oid, c.attnum;";

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

String REFRESH_TABLE4 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx,"
        + " c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
        + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision,"
        + " c.attndims as dimentions, "
        + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value,"
        + " d.adbin as attDefStr "
        + "from pg_class t "
        + "left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r' and t.parttype ='n') "
        + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) "
        + "left join pg_type typ on (c.atttypid = typ.oid) "
        + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 5"
        + " order by t.oid, c.attnum;";

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
                
                String REFRESH_TABLE5 = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx,"
                        + " c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
                        + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision,"
                        + " c.attndims as dimentions, "
                        + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value,"
                        + " d.adbin as attDefStr "
                        + "from pg_class t "
                        + "left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r' and t.parttype ='n') "
                        + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) "
                        + "left join pg_type typ on (c.atttypid = typ.oid) "
                        + "where c.attisdropped = 'f' and c.attnum > 0 and t.relnamespace = 6"
                        + " order by t.oid, c.attnum;";

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

    */}

    public static void preparePartitionColumnInfoResultSet(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {

       /* MockResultSet listnerResultSet = preparedstatementHandler
                .createResultSet();

        listnerResultSet.addColumn("tableid");
        listnerResultSet.addColumn("namespaceid");
        listnerResultSet.addColumn("columnidx");
        listnerResultSet.addColumn("name");
        listnerResultSet.addColumn("displaycolumns");
        listnerResultSet.addColumn("datatypeoid");
        listnerResultSet.addColumn("dtns");
        listnerResultSet.addColumn("length");
        listnerResultSet.addColumn("precision");
        listnerResultSet.addColumn("dimentions");
        listnerResultSet.addColumn("notnull");
        listnerResultSet.addColumn("isdefaultvalueavailable");
        listnerResultSet.addColumn("default_value");
        listnerResultSet.addColumn("attdefstr");

        listnerResultSet.addRow(new Object[] {1, 1, 1, "column", null, null,
                1, null, null, null, null, null, true, 1,});

        preparedstatementHandler.prepareResultSet(GET_PARTITION_COLUMN_INFO,
                listnerResultSet);*/
        
        String partTableColumns="WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace =  1and relkind <> 'i' and parttype in ('p', 'v')), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select tableid from tbl) and d.adnum in( select columnidx from attr))select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";
        MockResultSet fecthForeignTableColumnsRS = preparedstatementHandler.createResultSet();
        fecthForeignTableColumnsRS.addColumn("tableid");
        fecthForeignTableColumnsRS.addColumn("datatypeoid");
        fecthForeignTableColumnsRS.addColumn("columnidx");
        fecthForeignTableColumnsRS.addColumn("name");
        fecthForeignTableColumnsRS.addColumn("displaycolumns");
        fecthForeignTableColumnsRS.addColumn("datatypeoid");
        fecthForeignTableColumnsRS.addColumn("dtns");
        fecthForeignTableColumnsRS.addColumn("length");
        fecthForeignTableColumnsRS.addColumn("precision");
        fecthForeignTableColumnsRS.addColumn("dimentions");
        fecthForeignTableColumnsRS.addColumn("notnull");
        fecthForeignTableColumnsRS.addColumn("isdefaultvalueavailable");
        fecthForeignTableColumnsRS.addColumn("default_value");
        fecthForeignTableColumnsRS.addColumn("attdefstr");
        fecthForeignTableColumnsRS.addRow(new Object[] {1, 1, 1, "column", null, null,
            1, null, null, null, null, null, true, 1,});
        preparedstatementHandler.prepareResultSet(partTableColumns, fecthForeignTableColumnsRS);
        
        
        
       

        String str="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=1 and c.attisdropped = 'f' and c.attnum > 0;";
    
        MockResultSet fetchViewRS1 = preparedstatementHandler.createResultSet();
        fetchViewRS1.addColumn("columnidx");
        fetchViewRS1.addColumn("name");
        fetchViewRS1.addColumn("displayColumns");
        fetchViewRS1.addColumn("datatypeoid");
        fetchViewRS1.addColumn("length");
        fetchViewRS1.addColumn("precision");
        fetchViewRS1.addColumn("dimentions");
        fetchViewRS1.addColumn("notnull");
        fetchViewRS1.addColumn("isdefaultvalueavailable");

        

        fetchViewRS1.addRow(new Object[] {1, "ColName", "",1, 200, 0, 0, false,true});
        preparedstatementHandler.prepareResultSet(str, fetchViewRS1);
        
        String str5="select c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable from  pg_attribute c where c.attrelid=2 and c.attisdropped = 'f' and c.attnum > 0;";
        preparedstatementHandler.prepareResultSet(str5, fetchViewRS1);
        
        String str1="select typ.typnamespace as dtns from pg_type typ  where typ.oid=2";
        MockResultSet fetchViewRS2 = preparedstatementHandler.createResultSet();
        fetchViewRS2.addColumn("dtns");
        fetchViewRS2.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(str1, fetchViewRS2);
        
        String str2="select typ.typnamespace as dtns from pg_type typ  where typ.oid=1";
        preparedstatementHandler.prepareResultSet(str2, fetchViewRS2);
        
        
        String str3="select d.adsrc as default_value, d.adbin as attDefStr  from pg_attrdef d  where d.adrelid=0 and d.adnum=1";
        MockResultSet fetchViewRS3 = preparedstatementHandler.createResultSet();
        fetchViewRS3.addColumn("default_value");
        fetchViewRS3.addColumn("attDefStr");
        fetchViewRS3.addRow(new Object[] {"Default value",""});
        preparedstatementHandler.prepareResultSet(str3, fetchViewRS3);
        String str4="select d.adsrc as default_value, d.adbin as attDefStr  from pg_attrdef d  where d.adrelid=1 and d.adnum=1";
        preparedstatementHandler.prepareResultSet(str4, fetchViewRS3);


    }

    public static void preparePartitionConstrainstResultSet(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
                "1", "1", ""});
        preparedstatementHandler.prepareResultSet(
                GET_PARTITION_CONSTRAINTS, partitionConstraintRS);

    }
    
    public static void prepareShowDDLResultSet(PreparedStatementResultSetHandler preparedstatementHandler) {
        MockResultSet showDDLset = preparedstatementHandler.createResultSet();
        showDDLset.addColumn("count(*)");
        showDDLset.addRow(new Object[] {1});
        preparedstatementHandler.prepareResultSet(
                "select count(*) from pg_catalog.pg_proc where proname='pg_get_tabledef';", showDDLset);
        
    }
    
    public static void prepareShowTableDDLResultSet(PreparedStatementResultSetHandler preparedstatementHandler) {
        MockResultSet showDDLset = preparedstatementHandler.createResultSet();
        showDDLset.addColumn("pg_get_tabledef");
        showDDLset.addRow(new Object[] {"SET search_path = admin_user;\r\n" + 
                "CREATE  TABLE sdfsd (\r\n" + 
                "    df character(1)\r\n" + 
                ")\r\n" + 
                "WITH (orientation=row, compression=no)\r\n" + 
                "DISTRIBUTE BY HASH(df)\r\n" + 
                "TO GROUP group_version1;"});
        preparedstatementHandler.prepareResultSet(
                "SELECT * FROM pg_get_tabledef('pg_catalog.TempTable')", showDDLset);
    }
    
    public static void preparePartitionIndexestResultSet(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet indexpartitionRS = preparedstatementHandler
                .createResultSet();
        indexpartitionRS.addColumn("oid");
        indexpartitionRS.addColumn("tableId");
        indexpartitionRS.addColumn("indexname");
        indexpartitionRS.addColumn("namespaceid");
        indexpartitionRS.addColumn("accessmethodid");
        indexpartitionRS.addColumn("isunique");
        indexpartitionRS.addColumn("isprimary");
        indexpartitionRS.addColumn("isexclusion");
        indexpartitionRS.addColumn("isimmediate");
        indexpartitionRS.addColumn("isclustered");
        indexpartitionRS.addColumn("checkmin");
        indexpartitionRS.addColumn("isready");
        indexpartitionRS.addColumn("cols");
        indexpartitionRS.addColumn("reloptions");
        indexpartitionRS.addColumn("indexdef");
        indexpartitionRS.addColumn("tablespace");
        indexpartitionRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true,
                false, false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_PARTITION_INDEXES, indexpartitionRS);
    }
    
    public static void preparePartitionstResultSet(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getPartitionsRS = preparedstatementHandler
                .createResultSet();
        getPartitionsRS.addColumn("partition_id");
        getPartitionsRS.addColumn("partition_name");
        getPartitionsRS.addColumn("partition_type");
        getPartitionsRS.addColumn("table_id");

        getPartitionsRS.addRow(new Object[] {1, "part_1", "r", 1});
        preparedstatementHandler.prepareResultSet(  CommonLLTUtils.GET_PARTITIONS ,
                getPartitionsRS);
    }
    
    public static void preparePartitionConstrainstLoadLevel(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
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
                "1", "1", ""});
        preparedstatementHandler.prepareResultSet(
                GEL_CONSTRAINTS_AT_LOAD_LEVEL, partitionConstraintRS);

    }
    
    public static void preparePartitionIndexLoadLevel(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet indexpartitionRS = preparedstatementHandler
                .createResultSet();
        indexpartitionRS.addColumn("oid");
        indexpartitionRS.addColumn("tableId");
        indexpartitionRS.addColumn("indexname");
        indexpartitionRS.addColumn("namespaceid");
        indexpartitionRS.addColumn("accessmethodid");
        indexpartitionRS.addColumn("isunique");
        indexpartitionRS.addColumn("isprimary");
        indexpartitionRS.addColumn("isexclusion");
        indexpartitionRS.addColumn("isimmediate");
        indexpartitionRS.addColumn("isclustered");
        indexpartitionRS.addColumn("checkmin");
        indexpartitionRS.addColumn("isready");
        indexpartitionRS.addColumn("cols");
        indexpartitionRS.addColumn("reloptions");
        indexpartitionRS.addColumn("indexdef");
        indexpartitionRS.addColumn("tablespace");
        indexpartitionRS.addRow(new Object[] {1, 1, "IndexName", 1, 1, true,
                false, false, false, false, false, false, "1", "", "", ""});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.GET_PARTITION_INDEXES_AT_LOAD_LEVEL,
                indexpartitionRS);

    }
    
    public static void preparePartitionstLoadLevel(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        MockResultSet getPartitionsRS = preparedstatementHandler
                .createResultSet();
        getPartitionsRS.addColumn("partition_id");
        getPartitionsRS.addColumn("partition_name");
        getPartitionsRS.addColumn("partition_type");
        getPartitionsRS.addColumn("table_id");

        getPartitionsRS.addRow(new Object[] {1, "part_1", "r", 1});
        preparedstatementHandler.prepareResultSet(CommonLLTUtils.GET_PARTITIONS_AT_LOAD_LEVEL,
                getPartitionsRS);
    }

    public static void mockServerEncoding(PreparedStatementResultSetHandler preparedStatementResultSetHandler)
    {
        MockResultSet server_encoding = preparedStatementResultSetHandler
                .createResultSet();
        server_encoding.addRow(new Object[] {"UTF-8"});
        preparedStatementResultSetHandler.prepareResultSet("show server_encoding",
                server_encoding);
    }
    
    public static void refreshDBMSJob(PreparedStatementResultSetHandler preparedStatementResultSetHandler) {
        String query = REFRESH_JOB_STATEMENT;
        MockResultSet functionParamRs = preparedStatementResultSetHandler.createResultSet();
        functionParamRs.addColumn("job");
        functionParamRs.addColumn("log_user");
        functionParamRs.addColumn("priv_user");
        functionParamRs.addColumn("dbname");
        functionParamRs.addColumn("last_date");
        functionParamRs.addColumn("this_date");
        functionParamRs.addColumn("next_date");
        functionParamRs.addColumn("broken");
        functionParamRs.addColumn("interval");
        functionParamRs.addColumn("failures");
        functionParamRs.addColumn("what");
        functionParamRs.addRow(new Object[] {1, "DSUSER", "DSUSER", "DSUSER", "2020-2-11 00:00:00.0", null,
            "2020-2-11 00:00:00.0", "N", "sysdate", 0, "function();"});
        preparedStatementResultSetHandler.prepareResultSet(query, functionParamRs);
    }
    
    public static void fetchAllSynonyms(PreparedStatementResultSetHandler preparedstatementHandler) {
        String fetchAllsynonys = SynonymUtil.FETCH_SYNONYM_STATEMENT;
        MockResultSet fetchAllsynonysRS = preparedstatementHandler.createResultSet();
        fetchAllsynonysRS.addColumn("synonym_name");
        fetchAllsynonysRS.addColumn("owner");
        fetchAllsynonysRS.addColumn("schema_name");
        fetchAllsynonysRS.addColumn("table_name");
        fetchAllsynonysRS.addRow(new Object[] {1, "syn1", "user", "user", "tbl1"});
        preparedstatementHandler.prepareResultSet(fetchAllsynonys, fetchAllsynonysRS);
        
        String refresh = SynonymMetaData.REFRESH_SYNONYM_STATEMENT;
        MockResultSet fetchAllsynonysRefresh = preparedstatementHandler.createResultSet();
        fetchAllsynonysRefresh.addColumn("synonym_name");
        fetchAllsynonysRefresh.addColumn("owner");
        fetchAllsynonysRefresh.addColumn("schema_name");
        fetchAllsynonysRefresh.addColumn("table_name");
        fetchAllsynonysRefresh.addRow(new Object[] {"syn1", "user", "user", "tbl1"});
        preparedstatementHandler.prepareResultSet(refresh, fetchAllsynonysRefresh);
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
    
    public static void runLinuxFilePermissionInstance() throws Exception {
        Object filePermissionFactory = FilePermissionFactory.class.newInstance();
        Field f1 = filePermissionFactory.getClass().getDeclaredField("INSTANCE");
        f1.setAccessible(true);
        if (isLinux()) {
            f1.set(filePermissionFactory, new SetFilePermissionLinux());
        } else {
            f1.set(filePermissionFactory, new SetFilePermissionWindows());
        }
    }
    
    public static void setConnectionManagerConnectionDriver(IConnectionDriver connectionDriver,Database db) throws Exception {
        Object connectionManagerInstance = db.getConnectionManager();
        Field f1 = connectionManagerInstance.getClass().getDeclaredField("connectionDriver");
        f1.setAccessible(true);
        f1.set(connectionManagerInstance, connectionDriver);        
    }
    
    public static boolean isWindows() {
        if ((System.getProperty(OS_NAME)).contains(WINDOWS)) {
            return true;
        }
        return false;
    }

    public static boolean isLinux() {
        if ((System.getProperty(OS_NAME)).contains(LINUX)) {
            return true;
        }
        return false;
    }
    
    private static class SetFilePermissionLinux implements ISetFilePermission {

        @Override
        public Path createFileWithPermission(String path, boolean isDir, Set<AclEntryPermission> userChosenPermissions,
                boolean setDefaultOnNull) throws DatabaseOperationException {
            // To Auto-generated method stub
            Path newPath = Paths.get(path);

            // Check if file already exists
            boolean fileExists = Files.exists(newPath);

            try {
                if (isDir) {
                    // If create log directory and logs folder does not exist,
                    // create it with security permissions.

                    /* default Directory permission 700 */
                    Set<PosixFilePermission> permsDir = new HashSet<PosixFilePermission>();
                    permsDir.add(PosixFilePermission.OWNER_READ);
                    permsDir.add(PosixFilePermission.OWNER_WRITE);
                    permsDir.add(PosixFilePermission.OWNER_EXECUTE);
                    if (!fileExists) {

                        newPath = Files.createDirectory(newPath);
                        Files.setPosixFilePermissions(newPath, permsDir);

                    }
                } else {
                    // Create file with security permissions.

                    /* default File permission 644 */

                    Set<PosixFilePermission> permsFile = new HashSet<PosixFilePermission>();
                    permsFile.add(PosixFilePermission.OWNER_READ);
                    permsFile.add(PosixFilePermission.OWNER_WRITE);
                    permsFile.add(PosixFilePermission.OWNER_EXECUTE);
                    if (!fileExists) {
                        newPath = Files.createFile(newPath);
                    }
                    Files.setPosixFilePermissions(newPath, permsFile);
                }

            } catch (IOException exception) {
                try {
                    Files.deleteIfExists(newPath);
                } catch (IOException e1) {
                    MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
                }
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.FILE_PERMISSION_ERROR),
                        exception);
                throw new DatabaseOperationException(IMessagesConstants.FILE_PERMISSION_ERROR, exception);
            }
            return newPath;

        }
    }
    
    private static class SetFilePermissionWindows implements ISetFilePermission {
        @Override
        public Path createFileWithPermission(String path, boolean isDir, Set<AclEntryPermission> userChosenPermissions,
                boolean setDefaultOnNull) throws DatabaseOperationException {
            Path newPath = Paths.get(path);

            // Check if file already exists
            boolean fileExists = Files.exists(newPath);

            // Create file attribute with security permission
            FileAttribute<List<AclEntry>> fileAttributes = new DSFileAttributes(userChosenPermissions);
            try {
                if (isDir) {
                    // If create log directory and logs folder does not exist,
                    // create it with security permissions.
                    if (!fileExists) {
                        if (setDefaultOnNull) {
                            try {
                                newPath = Files.createDirectory(newPath);
                            } catch (IOException exception) {
                                MPPDBIDELoggerUtility.error(
                                        MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FOLDER_FAIL_ERR),
                                        exception);
                            }
                        } else {
                            newPath = Files.createDirectory(newPath);
                        }
                    }
                } else {
                    // Create file with security permissions.
                    if (!fileExists) {
                        if (setDefaultOnNull) {
                            try {
                                Files.createFile(newPath);
                            } catch (IOException exception) {
                                MPPDBIDELoggerUtility.error(
                                        MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FILE_FAIL_ERR),
                                        exception);
                            }
                        } else {
                            newPath = Files.createFile(newPath);
                        }
                    }
                }

            } catch (IOException exception) {
                try {
                    Files.deleteIfExists(newPath);
                } catch (IOException e1) {
                    MPPDBIDELoggerUtility.error("Error while deleting file in exception.", e1);
                    throw new DatabaseOperationException("Error while deleting file in exception.");
                }
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.FILE_PERMISSION_ERROR),
                        exception);
                throw new DatabaseOperationException(IMessagesConstants.FILE_PERMISSION_ERROR, exception);
            }
            return newPath;
        }
    }
    
    public static String getPackagePathSecureUtil(SecureUtil sec) {
        String packagePath = null;
        try {
            Field packagePathFeild = sec.getClass().getDeclaredField("packagePath");
            packagePathFeild.setAccessible(true);
            packagePath = packagePathFeild.get(sec).toString();
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return packagePath;
    }
    
    public static  void mockResultsetForNewlyCreatedTable(PreparedStatementResultSetHandler preparedstatementHandler) {
        String newlycreatedTableQuery = "select tbl.relname relname,tbl.parttype parttype,tbl.relnamespace relnamespace,tbl.oid oid,ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, xctbl.nodeoids as nodes ,tbl.reloptions as reloptions from pg_class tbl left join (select d.description, d.objoid from pg_description d where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.relname = 'MyTable' and tbl.relnamespace= 1";
        MockResultSet refreshtablemetadatainnamspace = preparedstatementHandler.createResultSet();
        refreshtablemetadatainnamspace.addColumn("oid");
        refreshtablemetadatainnamspace.addColumn("relname");
        refreshtablemetadatainnamspace.addColumn("relpersistence");
        refreshtablemetadatainnamspace.addColumn("reloptions");
        refreshtablemetadatainnamspace.addColumn("parttype");
        refreshtablemetadatainnamspace.addColumn("partkey");
        refreshtablemetadatainnamspace
                .addRow(new Object[] {1, "MyTable", 'p', "", 'n', "abc"});
        preparedstatementHandler.prepareResultSet(newlycreatedTableQuery, refreshtablemetadatainnamspace);
        CommonLLTUtils.getIndexcCOnstaByNamespace(preparedstatementHandler);
        CommonLLTUtils.getIndexForTableRS(preparedstatementHandler);
        CommonLLTUtils.getConstaraintForTableRS(preparedstatementHandler);
    }
    

}
