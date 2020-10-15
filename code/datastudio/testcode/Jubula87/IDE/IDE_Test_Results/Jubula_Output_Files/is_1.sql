

-- Name: isnumber; Type: C Function; Schema: auto;

CREATE OR REPLACE FUNCTION auto.isnumber(text)
 RETURNS integer
 LANGUAGE c
 FENCED
AS '$libdir/pg_plugin/cn_5001#14151163903541260356611697268357137#isNumber.so', $$ISNUMBER$$
/
