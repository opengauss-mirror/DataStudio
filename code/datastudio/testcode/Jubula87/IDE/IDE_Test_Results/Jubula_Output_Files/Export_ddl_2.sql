

-- Name: zschema1; Type: SCHEMA; Schema: -; Owner: -

CREATE SCHEMA zschema1;


SET search_path = zschema1, pg_catalog;


-- Name: table2_zschema2_table; Type: TABLE; Schema: zschema1; Owner: -

CREATE TABLE zschema1.table2_zschema2_table (
    c1 integer,
    c2 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1)
TO GROUP group_version1;


-- Name: table_2_tc10; Type: TABLE; Schema: zschema1; Owner: -

CREATE TABLE zschema1.table_2_tc10 (
    c1 integer,
    c2 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1)
TO GROUP group_version1;


-- Name: tc10; Type: TABLE; Schema: zschema1; Owner: -

CREATE TABLE zschema1.tc10 (
    c1 integer,
    c2 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1)
TO GROUP group_version1;





-- Name: znew2; Type: SCHEMA; Schema: -; Owner: -

CREATE SCHEMA znew2;


SET search_path = znew2, pg_catalog;


-- Name: renametable; Type: TABLE; Schema: znew2; Owner: -

CREATE TABLE znew2.renametable (
    c1 integer,
    c2 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1)
TO GROUP group_version1;



