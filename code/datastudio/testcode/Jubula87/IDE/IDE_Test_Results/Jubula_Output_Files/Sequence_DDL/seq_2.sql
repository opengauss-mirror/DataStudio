

SET search_path = auto, pg_catalog;

-- Name: seq_1; Type: SEQUENCE; Schema: auto; Owner: -

CREATE SEQUENCE seq_1
    START WITH 2
    INCREMENT BY 2
    NO MINVALUE
    MAXVALUE 15
    CACHE 1;


-- Name: seq_1; Type: SEQUENCE SET; Schema: auto; Owner: -

SELECT pg_catalog.setval('seq_1', 2, false);



