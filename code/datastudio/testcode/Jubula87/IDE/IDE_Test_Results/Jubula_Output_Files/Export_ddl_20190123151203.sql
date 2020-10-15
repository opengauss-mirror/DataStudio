

-- Name: auto1; Type: Function; Schema: auto;

CREATE OR REPLACE FUNCTION auto.auto1()
 RETURNS void
 LANGUAGE plpgsql
 NOT FENCED
AS $$ DECLARE           c integer;            c2  integer; BEGIN  c:=+2;           c2:=c*2;   END $$
/
