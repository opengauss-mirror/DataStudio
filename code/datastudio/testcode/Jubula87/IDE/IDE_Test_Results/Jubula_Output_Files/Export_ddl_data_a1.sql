

SET search_path = auto;


-- Name: a1; Type: TABLE; Schema: auto; Owner: -

CREATE TABLE a1 (
    c1 character(1),
    c2 integer,
    c3 character(1),
    c4 character varying
)
WITH (orientation=row, compression=no)
DISTRIBUTE BY HASH (c1);


-- Data for Name: a1; Type: TABLE DATA; Schema: auto; Owner: -

INSERT INTO a1 VALUES ('j', 5, 'h', 'u');
INSERT INTO a1 VALUES ('j', 5, 'h', 'u');
INSERT INTO a1 VALUES ('j', 5, 'h', 'u');
INSERT INTO a1 VALUES ('j', 5, 'h', 'u');
INSERT INTO a1 VALUES ('j', 5, 'h', 'u');
INSERT INTO a1 VALUES ('j', 5, 'h', 'u');



