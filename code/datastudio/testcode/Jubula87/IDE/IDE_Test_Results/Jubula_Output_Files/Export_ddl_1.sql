

-- Name: auto; Type: SCHEMA; Schema: -; Owner: -

CREATE SCHEMA auto;


SET search_path = auto;

-- Name: auto1(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto1() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$;


-- Name: auto11(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto11() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$;


-- Name: auto2(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto2() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$;


-- Name: auto22(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto22() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$;


-- Name: auto3(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto3() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE 
BEGIN
dbms_output.put_line(hello);
END$$;


-- Name: auto4(timestamp without time zone); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto4(timestamp without time zone) RETURNS interval
    LANGUAGE sql STABLE STRICT COST 1
    AS $_$select pg_catalog.age((cast(current_date as timestamp without time zone)), $1)$_$;


-- Name: auto44(timestamp without time zone); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto44(timestamp without time zone) RETURNS interval
    LANGUAGE sql STABLE STRICT COST 1
    AS $_$select pg_catalog.age((cast(current_date as timestamp without time zone)), $1)$_$;


-- Name: auto45(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto45() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$;


-- Name: auto47(timestamp without time zone); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto47(timestamp without time zone) RETURNS interval
    LANGUAGE sql STABLE STRICT COST 1
    AS $_$select pg_catalog.age((cast(current_date as timestamp without time zone)), $1)$_$;


-- Name: auto_1(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto_1() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$;


-- Name: auto_2(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto_2() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$;


-- Name: auto_3(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto_3() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$;


-- Name: auto_4(timestamp without time zone); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto_4(timestamp without time zone) RETURNS interval
    LANGUAGE sql STABLE STRICT
    AS $_$select pg_catalog.age((cast(current_date as timestamp without time zone)), $1)$_$;


-- Name: auto_search_1(); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto_search_1() RETURNS void
    LANGUAGE plpgsql
    AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$;


-- Name: auto_search_2(timestamp without time zone); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION auto_search_2(timestamp without time zone) RETURNS interval
    LANGUAGE sql STABLE STRICT
    AS $_$select pg_catalog.age((cast(current_date as timestamp without time zone)), $1)$_$;


-- Name: isnumber(text); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION isnumber(text) RETURNS integer
    LANGUAGE c FENCED
    AS '$libdir/pg_plugin/cn_5001#14151163903541260770367099003282421#isNumber.so', 'ISNUMBER';


-- Name: isnumber111(text); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION isnumber111(text) RETURNS integer
    LANGUAGE c FENCED
    AS '$libdir/pg_plugin/cn_5001#14151163903541260166690720048143308#isNumber.so', 'ISNUMBER';


-- Name: isnumber4(text); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION isnumber4(text) RETURNS integer
    LANGUAGE c FENCED
    AS '$libdir/pg_plugin/cn_5001#14151163903541261065871629224099247#isNumber.so', 'ISNUMBER';


-- Name: isnumber_1(text); Type: FUNCTION; Schema: auto; Owner: -

CREATE FUNCTION isnumber_1(text) RETURNS integer
    LANGUAGE c FENCED
    AS '$libdir/pg_plugin/cn_5001#14151163903541260770311559887582387#isNumber.so', 'ISNUMBER';


-- Name: @$sam&ple; Type: SEQUENCE; Schema: auto; Owner: -

CREATE SEQUENCE "@$sam&ple"
    START WITH 2
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 12
    CACHE 1;



-- Name: a1; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE a1 (
    c1 character(1),
    c2 integer,
    c3 character(1),
    c4 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1);


-- Name: batch_exprt_1; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE batch_exprt_1 (
    c1 integer,
    c2 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1);


-- Name: batch_exprt_2; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE batch_exprt_2 (
    c1 integer,
    c2 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1);


-- Name: batch_exprt_3; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE batch_exprt_3 (
    c1 integer,
    c2 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1);


-- Name: cmt_rlbck; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE cmt_rlbck (
    col1 integer,
    col2 character varying,
    col3 numeric,
    col4 character(1)
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (col1);


-- Name: seq_1; Type: SEQUENCE; Schema: auto; Owner: -

CREATE SEQUENCE seq_1
    START WITH 2
    INCREMENT BY 2
    NO MINVALUE
    MAXVALUE 15
    CACHE 1;


-- Name: t1; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE t1 (
    c1 integer,
    c2 money
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1);


-- Name: test; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE test (
    c1 character varying,
    c2 character(1)
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1);


-- Name: test_12; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE test_12 (
    c1 character varying(20) DEFAULT upper('fgfh'::text),
    c2 integer
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1);


-- Name: test_part; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE test_part (
    col1 integer
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (col1)
PARTITION BY RANGE (col1)
(
    PARTITION ptn VALUES LESS THAN (10) TABLESPACE pg_default
);


-- Name: test_tr6; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE test_tr6 (
    col1 integer,
    col2 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (col1);


-- Name: test_user; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE test_user (
    c1 integer,
    c2 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1);



