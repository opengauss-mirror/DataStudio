package org.opengauss.mppdbide.mock.presentation;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

public class MockViewUtils
{
    public enum EXCEPTIONENUM
    {
        YES, NO;
    }

    public static final String  GETNAMESPACEQUERY = "SELECT oid, nspname from pg_namespace where ((oid < 16384 and nspname NOT LIKE 'public') or nspname LIKE 'pg_%') and has_schema_privilege(nspname, 'USAGE') ORDER BY nspname;";
    public static final String  GETVIEWQUERY      = "SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner , c.relkind as relkind FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 10";

    private static final String VIEWQUERY_SCHEMA  = "and n.nspname=";
    private static final String VIEWQUERY_OID     = "and c.oid=";

    public static void createNamespace(
            PreparedStatementResultSetHandler preparedstatementHandler,
            int nsid, String nsname)
    {
        MockResultSet namespaceRS = preparedstatementHandler.createResultSet();
        namespaceRS.addColumn("oid");
        namespaceRS.addColumn("nspname");
        namespaceRS.addRow(new Object[] {1, "pg_catalog"});
        namespaceRS.addRow(new Object[] {2, "information_schema"});
        namespaceRS.addRow(new Object[] {nsid, nsname});
        preparedstatementHandler.prepareResultSet(
                GETNAMESPACEQUERY, namespaceRS);
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
        getdbsrs.addColumn("relkind");
        for(int i=1;i<=input;i++) {
            getdbsrs.addRow(new Object[] {i,"mytestview"+i, "owner1", "v"});
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
        getdbsrs.addColumn("relkind");
        for(int i=1;i<=input;i++) {
            getdbsrs.addRow(new Object[] {i,"mytestview"+i, "owner1", "v"});
            viewDdlRs = preparedstatementHandler.createResultSet();
            viewDdlRs.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner , c.relkind as relkind FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 1", getdbsrs);

        MockResultSet getdbsrs1 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs1;
        getdbsrs1.addColumn("oid");
        getdbsrs1.addColumn("viewname");
        getdbsrs1.addColumn("viewowner");
        getdbsrs1.addColumn("relkind");
        for(int i=1;i<=input;i++) {
            getdbsrs1.addRow(new Object[] {i,"mytestview"+i, "owner1", "v"});
            viewDdlRs1 = preparedstatementHandler.createResultSet();
            viewDdlRs1.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs1);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 2", getdbsrs1);
        

        MockResultSet getdbsrs2 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs2;
        getdbsrs2.addColumn("oid");
        getdbsrs2.addColumn("viewname");
        getdbsrs2.addColumn("viewowner");
        getdbsrs2.addColumn("relkind");
        for(int i=1;i<=input;i++) {
            getdbsrs2.addRow(new Object[] {i,"mytestview"+i, "owner1", "v"});
            viewDdlRs2 = preparedstatementHandler.createResultSet();
            viewDdlRs2.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs2);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner , c.relkind as relkind FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 3", getdbsrs2);
        

        MockResultSet getdbsrs3 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs3;
        getdbsrs3.addColumn("oid");
        getdbsrs3.addColumn("viewname");
        getdbsrs3.addColumn("viewowner");
        getdbsrs3.addColumn("relkind");
        for(int i=1;i<=input;i++) {
            getdbsrs3.addRow(new Object[] {i,"mytestview"+i, "owner1", "v"});
            viewDdlRs3 = preparedstatementHandler.createResultSet();
            viewDdlRs3.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs3);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner , c.relkind as relkind FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 4", getdbsrs3);
        

        MockResultSet getdbsrs4 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs4;
        getdbsrs4.addColumn("oid");
        getdbsrs4.addColumn("viewname");
        getdbsrs4.addColumn("viewowner");
        getdbsrs4.addColumn("relkind");
        for(int i=1;i<=input;i++) {
            getdbsrs4.addRow(new Object[] {i,"mytestview"+i, "owner1", "v"});
            viewDdlRs4 = preparedstatementHandler.createResultSet();
            viewDdlRs4.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs4);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner , c.relkind as relkind FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 5", getdbsrs4);
        
        MockResultSet getdbsrs5 = preparedstatementHandler.createResultSet();
        MockResultSet viewDdlRs5;
        getdbsrs5.addColumn("oid");
        getdbsrs5.addColumn("viewname");
        getdbsrs5.addColumn("viewowner");
        getdbsrs5.addColumn("relkind");
        for(int i=1;i<=input;i++) {
            getdbsrs5.addRow(new Object[] {i,"mytestview"+i, "owner1", "v"});
            viewDdlRs5 = preparedstatementHandler.createResultSet();
            viewDdlRs5.addRow(new Object[] {"select * from something"+i});
            preparedstatementHandler.prepareResultSet("SELECT * FROM pg_get_viewdef(" + i + ')', viewDdlRs5);
        }
        preparedstatementHandler.prepareResultSet("SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner , c.relkind as relkind FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) and has_table_privilege(c.oid,'SELECT') and c.relnamespace = 6", getdbsrs5);
        
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
        getdbsrs.addColumn("relkind");
        getdbsrs.addColumn("definition");
        getdbsrs.addRow(new Object[] {schemaId, nsid, schemaName, "mytestview"+schemaId, "owner1","v",
                "select * from something"});
        preparedstatementHandler.prepareResultSet(GETVIEWQUERY + VIEWQUERY_OID
                + ns.getOid(), getdbsrs);
    }

    public static void createViewColunmMetadata(
            PreparedStatementResultSetHandler preparedstatementHandler,
            int viewid, int nsid)
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

        getdbsrs.addRow(new Object[] {1, nsid, 1, "col1", 2, 1, 64, -1, 0,
                'f', 'f', ""});
        getdbsrs.addRow(new Object[] {1, nsid, 2, "col2", 1, 1, 64, -1, 0,
                'f', 'f', ""});
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
        StringBuilder strbldr = new StringBuilder("CREATE OR REPLACE [ TEMP | TEMPORARY | MATERIALIZED ] VIEW ");
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
        
        
    }
}
