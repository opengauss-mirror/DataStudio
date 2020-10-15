

-- Name: c; Type: Function; Schema: zte;

CREATE OR REPLACE FUNCTION zte.c(c1 integer)
 RETURNS integer
 LANGUAGE plpgsql
 NOT FENCED
AS $$  DECLARE	c integer; c2  integer; BEGIN c:=c1+2; c2:=c*2; RETURN c2; END $$
/
