package org.opengauss.mppdbide.mock.bl;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

public class MockViewUtils
{
    public enum EXCEPTIONENUM
    {
        YES, NO;
    }

    public static final String  GETNAMESPACEQUERY = "SELECT oid, nspname from pg_namespace where ((oid >= 16384 or nspname LIKE 'public') and nspname  NOT LIKE 'pg_%') and has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";
    public static final String GET_ALL_SYSTEM_NAMESPACE                         = "SELECT oid, nspname from pg_namespace where ((oid < 16384 and nspname NOT LIKE 'public') or nspname LIKE 'pg_%') and has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";
    public static final String  GETVIEWQUERY      = "SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 10";

    private static final String VIEWQUERY_SCHEMA  = "and n.nspname=";
    private static final String VIEWQUERY_OID     = "and c.oid=";

    public static void createNamespace(
            PreparedStatementResultSetHandler preparedstatementHandler,
            int nsid, String nsname)
    {
        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
       // namespaceRS.addRow(new Object[] {1, "pg_catalog"});
       // namespaceRS.addRow(new Object[] {2, "information_schema"});
        namespaceRS.addRow(new Object[] {nsid, nsname});
        preparedstatementHandler.prepareResultSet(
                GETNAMESPACEQUERY, namespaceRS);
        
/*        MockResultSet namespaceRS1 = preparedstatementHandler.createResultSet();
        namespaceRS1.addColumn("oid");
        namespaceRS1.addColumn("nspname");
        namespaceRS1.addRow(new Object[] {nsid, nsname});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.FETCH_ALL_NAMESPACE_LOAD_PRIV, namespaceRS1);*/
        
        MockResultSet namespaceRS11 = preparedstatementHandler.createResultSet();
        namespaceRS11.addColumn("oid");
        namespaceRS11.addColumn("nspname");
        namespaceRS11.addRow(new Object[] {nsid, nsname});
        preparedstatementHandler.prepareResultSet(
                CommonLLTUtils.FETCH_ALL_NAMESPACE_LOAD_ALL, namespaceRS11);
    }

    public static void createViewMetadata(
            PreparedStatementResultSetHandler preparedstatementHandler,
            int nsid,int input)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs;
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("viewname");
        getdbsrs.addColumn("viewowner");
        for(int i=1;i<=input;i++) {
            getdbsrs.addRow(new Object[] {i,"mytestview"+i, "owner1"});
            viewDdlRs = preparedstatementHandler.createResultSet();
            viewDdlRs.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs);
        }
        preparedstatementHandler.prepareResultSet(GETVIEWQUERY, getdbsrs);
        
    }
    public static void createViewMetadataAutoSuggest(
            PreparedStatementResultSetHandler preparedstatementHandler,
            int nsid,int input)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs;
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("viewname");
        getdbsrs.addColumn("viewowner");
        for(int i=1;i<=input;i++) {
            getdbsrs.addRow(new Object[] {i,"mytestview"+i, "owner1"});
            viewDdlRs = preparedstatementHandler.createResultSet();
            viewDdlRs.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 1", getdbsrs);

        MockResultSet getdbsrs1 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs1;
        getdbsrs1.addColumn("oid");
        getdbsrs1.addColumn("viewname");
        getdbsrs1.addColumn("viewowner");
        for(int i=1;i<=input;i++) {
            getdbsrs1.addRow(new Object[] {i,"mytestview"+i, "owner1"});
            viewDdlRs1 = preparedstatementHandler.createResultSet();
            viewDdlRs1.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs1);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 2", getdbsrs1);
        

        MockResultSet getdbsrs2 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs2;
        getdbsrs2.addColumn("oid");
        getdbsrs2.addColumn("viewname");
        getdbsrs2.addColumn("viewowner");
        for(int i=1;i<=input;i++) {
            getdbsrs2.addRow(new Object[] {i,"mytestview"+i, "owner1"});
            viewDdlRs2 = preparedstatementHandler.createResultSet();
            viewDdlRs2.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs2);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 3", getdbsrs2);
        

        MockResultSet getdbsrs3 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs3;
        getdbsrs3.addColumn("oid");
        getdbsrs3.addColumn("viewname");
        getdbsrs3.addColumn("viewowner");
        for(int i=1;i<=input;i++) {
            getdbsrs3.addRow(new Object[] {i,"mytestview"+i, "owner1"});
            viewDdlRs3 = preparedstatementHandler.createResultSet();
            viewDdlRs3.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs3);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 4", getdbsrs3);
        

        MockResultSet getdbsrs4 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs4;
        getdbsrs4.addColumn("oid");
        getdbsrs4.addColumn("viewname");
        getdbsrs4.addColumn("viewowner");
        for(int i=1;i<=input;i++) {
            getdbsrs4.addRow(new Object[] {i,"mytestview"+i, "owner1"});
            viewDdlRs4 = preparedstatementHandler.createResultSet();
            viewDdlRs4.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs4);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 5", getdbsrs4);
        
        MockResultSet getdbsrs5 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs5;
        getdbsrs5.addColumn("oid");
        getdbsrs5.addColumn("viewname");
        getdbsrs5.addColumn("viewowner");
        for(int i=1;i<=input;i++) {
            getdbsrs5.addRow(new Object[] {i,"mytestview"+i, "owner1"});
            viewDdlRs5 = preparedstatementHandler.createResultSet();
            viewDdlRs5.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs5);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 6", getdbsrs5);
        
    }
    
    public static void createViewMetadata_SchemaName(
            PreparedStatementResultSetHandler preparedstatementHandler,int schemaId,
            int nsid, String schemaName)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("nspoid");
        getdbsrs.addColumn("schemaname");
        getdbsrs.addColumn("viewname");
        getdbsrs.addColumn("viewowner");
        getdbsrs.addColumn("definition");
        getdbsrs.addRow(new Object[] {schemaId, nsid, schemaName, "mytestview"+schemaId, "owner1",
                "select * from something"});
        preparedstatementHandler.prepareResultSet(GETVIEWQUERY
                + VIEWQUERY_SCHEMA + "'" + schemaName + "'", getdbsrs);
    }

    public static void createViewMetadata_Oid(
            PreparedStatementResultSetHandler preparedstatementHandler,int schemaId,
            int nsid, String schemaName, Namespace ns)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("nspoid");
        getdbsrs.addColumn("schemaname");
        getdbsrs.addColumn("viewname");
        getdbsrs.addColumn("viewowner");
        getdbsrs.addColumn("definition");
        getdbsrs.addRow(new Object[] {schemaId, nsid, schemaName, "mytestview"+schemaId, "owner1",
                "select * from something"});
        preparedstatementHandler.prepareResultSet(GETVIEWQUERY + VIEWQUERY_OID
                + ns.getOid(), getdbsrs);
    }

    public static void createViewColunmMetadata(
            PreparedStatementResultSetHandler preparedstatementHandler,
            int viewid, int nsid)
    {
        String query = "WITH tbl AS ( select oid as viewid,relnamespace as namespaceid from pg_class where relnamespace =1 and relkind = 'v'), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select viewid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr)) select t.viewid as viewid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.viewid = c.tableoid) LEFT JOIN attrdef d ON(t.viewid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.viewid ,c.columnidx;";
        
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
        getdbsrs.addColumn("displaycolumns");

        getdbsrs.addRow(new Object[] {1, nsid, 1, "col1", 2, 1, 64, -1, 0,
                'f', 'f', "", ""});
        getdbsrs.addRow(new Object[] {1, nsid, 2, "col2", 1, 1, 64, -1, 0,
                'f', 'f', "",""});
       /* getdbsrs.addRow(new Object[] {2, nsid, 1, "col3", 2, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {2, nsid, 2, "col4", 1, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {3, nsid, 1, "col5", 2, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 1, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 1, 1, -1, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 11, 1, -1, -1, 0,
                'f', 'f', ""});*/
        preparedstatementHandler.prepareResultSet(query, getdbsrs);
        
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
        
        String fecthForeignTableColumns=" WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = 1and relkind = 'f' and parttype ='n'), attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr))select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx; ";
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
        
        String str4="select d.adsrc as default_value, d.adbin as attDefStr  from pg_attrdef d  where d.adrelid=2 and d.adnum=1";
        preparedstatementHandler.prepareResultSet(str4, fetchViewRS3);
        
        String srt5="select d.adsrc as default_value, d.adbin as attDefStr  from pg_attrdef d  where d.adrelid=2 and d.adnum=2";
        preparedstatementHandler.prepareResultSet(srt5, fetchViewRS3);
    }
    
    public static void createViewColunmMetadataAutoSuggest(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String query1 = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0  and v.relnamespace = 1 order by v.oid, c.attnum";
        String query2 = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0  and v.relnamespace = 2 order by v.oid, c.attnum";
        String query3 = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0  and v.relnamespace = 3 order by v.oid, c.attnum";
        String query4 = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0  and v.relnamespace = 4 order by v.oid, c.attnum";
        String query5 = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0  and v.relnamespace = 5 order by v.oid, c.attnum";
        String query6 = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, d.adbin as attDefStr from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0  and v.relnamespace = 6 order by v.oid, c.attnum";
        
        
        
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("viewid");
        getdbsrs.addColumn("namespaceid");
        getdbsrs.addColumn("columnidx");
        getdbsrs.addColumn("name");
        getdbsrs.addColumn("displayColumns");
        getdbsrs.addColumn("datatypeoid");
        getdbsrs.addColumn("dtns");
        getdbsrs.addColumn("length");
        getdbsrs.addColumn("precision");
        getdbsrs.addColumn("dimentions");
        getdbsrs.addColumn("notnull");
        getdbsrs.addColumn("isdefaultvalueavailable");
        getdbsrs.addColumn("default_value");
        getdbsrs.addColumn("attDefStr");

        getdbsrs.addRow(new Object[] {1, 1, 1, "col1", "",2, 1, 64, -1, 0,
                'f', 'f', "", ""});
        getdbsrs.addRow(new Object[] {1, 1, 2, "col2","", 1, 1, 64, -1, 0,
                'f', 'f', "", ""});
       /* getdbsrs.addRow(new Object[] {2, nsid, 1, "col3", 2, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {2, nsid, 2, "col4", 1, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {3, nsid, 1, "col5", 2, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 1, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 1, 1, -1, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {3, nsid, 2, "col6", 11, 1, -1, -1, 0,
                'f', 'f', ""});*/
        preparedstatementHandler.prepareResultSet(query1, getdbsrs);
        preparedstatementHandler.prepareResultSet(query2, getdbsrs);
        preparedstatementHandler.prepareResultSet(query3, getdbsrs);
        preparedstatementHandler.prepareResultSet(query4, getdbsrs);
        preparedstatementHandler.prepareResultSet(query5, getdbsrs);
        preparedstatementHandler.prepareResultSet(query6, getdbsrs);
    }

    public static void createView(
            PreparedStatementResultSetHandler preparedstatementHandler,
            String newSchemaName, Namespace ns, String oldName)
    {
        String createView = "ALTER VIEW " + ns.getQualifiedObjectName() + '.'
                + oldName + " SET schema " + newSchemaName;

        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("messaage");

        colmetadataRS.addRow(new Object[] {"View Altered"});
        preparedstatementHandler.prepareResultSet(createView, colmetadataRS);
    }

    public static void DropView(
            PreparedStatementResultSetHandler preparedstatementHandler,
            String viewName)
    {
        String dropView = "DROP VIEW " + viewName;

        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("messaage");

        colmetadataRS.addRow(new Object[] {"View droped"});
        preparedstatementHandler.prepareResultSet(dropView, colmetadataRS);
    }

    public static void setNamespace(
            PreparedStatementResultSetHandler preparedstatementHandler,int nsid,
            String viewName)
    {
        String str="ALTER VIEW schema2.mytestview1 SET schema newSchema2";
        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("messaage");

        colmetadataRS.addRow(new Object[] {"namespace set"});
        preparedstatementHandler.prepareResultSet(str,
                colmetadataRS);
    }

    public static void setDefault(
            PreparedStatementResultSetHandler preparedstatementHandler,int nsid,
            String viewName)
    {
        MockResultSet getdbsrs = preparedstatementHandler.createResultSet();
        getdbsrs.addColumn("oid");
        getdbsrs.addColumn("nspoid");
        getdbsrs.addColumn("schemaname");
        getdbsrs.addColumn("viewname");
        getdbsrs.addColumn("viewowner");
        getdbsrs.addColumn("definition");
        getdbsrs.addRow(new Object[] {2, nsid, "newschema", viewName, "owner1",
                "select * from something"});

        preparedstatementHandler.prepareResultSet("", getdbsrs);
    }

    public static void renameView(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {

        String GETRENAMEVIEWQUERY = "ALTER VIEW \"newSchema\".mytestview1 RENAME TO mytestview11";
        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("messaage");

        colmetadataRS.addRow(new Object[] {"View renamed"});
        preparedstatementHandler.prepareResultSet(GETRENAMEVIEWQUERY,
                colmetadataRS);

    }

    public static void renameViewWithException(
            PreparedStatementResultSetHandler preparedstatementHandler,
            Namespace ns, String oldView, String newView)
    {

        String GETRENAMEVIEWQUERY = "ALTER VIEW" + ns.getName() + "." + oldView
                + " RENAME TO " + newView;
        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("messaage");

        colmetadataRS.addRow(new Object[] {"View renamed"});
        preparedstatementHandler.prepareResultSet(GETRENAMEVIEWQUERY,
                colmetadataRS);
        preparedstatementHandler
                .prepareThrowsSQLException("Invalid valid query");

    }
    public static void DropViewWithException(
            PreparedStatementResultSetHandler preparedstatementHandler,
            String viewName)
    {
        
        String dropView = "DROP VIEW " + viewName;

        MockResultSet colmetadataRS = preparedstatementHandler
                .createResultSet();
        colmetadataRS.addColumn("messaage");

        colmetadataRS.addRow(new Object[] {"View droped"});
        preparedstatementHandler.prepareResultSet(dropView, colmetadataRS);
        preparedstatementHandler
        .prepareThrowsSQLException("Invalid valid query");
    }

    public static void refreshNameSpace(PreparedStatementResultSetHandler preparedstatementHandler)
    {
        String str="select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as precision, c.attndims as dimentions, c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value from pg_class v left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) left join pg_type typ on (c.atttypid = typ.oid) where c.attisdropped = 'f' and c.attnum > 0 and v.oid = 1 order by v.oid, c.attnum";
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

        getdbsrs.addRow(new Object[] {1, 10, 1, "col1", 2, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {1, 10, 2, "col2", 1, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {2, 10, 1, "col1", 2, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {2, 10, 2, "col2", 1, 1, 64, -1, 0,
                'f', 'f', ""});
        preparedstatementHandler.prepareResultSet(str, getdbsrs);
        
    }
    public static String getMockedCreateViewTemplate(Namespace ns)
    {
        StringBuilder strbldr = new StringBuilder("CREATE (OR REPLACE) [ TEMP | TEMPORARY | MATERIALIZED ] VIEW ");
        strbldr.append(ns.getQualifiedObjectName())
                .append(".")
                .append("<VIEW NAME>")
                .append(" [ ( column_name [, ...] ) ] ")
                .append(System.lineSeparator())
                .append("[ WITH ( {view_option_name [= view_option_value]} [, ... ] ) ]")
                .append(System.lineSeparator())
                .append("\tAS ")
                .append(System.lineSeparator())
                .append("<SQL QUERY>")
                .append(";");
        return  strbldr.toString();
    }

    public static void refreshNameSpaceid(
            PreparedStatementResultSetHandler preparedstatementHandler)
    {
        // TODO Auto-generated method stub
        
    }
}
