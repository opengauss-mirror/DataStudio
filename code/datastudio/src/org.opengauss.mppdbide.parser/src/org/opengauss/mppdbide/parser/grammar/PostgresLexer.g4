lexer grammar PostgresLexer;

@header {
    package org.opengauss.mppdbide.parser.grammar;
}
/* ordinary key words in alphabetical order */

ABORT_P : A B O R T;
ABSOLUTE_P : A B S O L U T E;
ACCESS : A C C E S S;
ACCOUNT : A C C O U N T;
ACTION : A C T I O N;
ADD_P : A D D;
ADMIN : A D M I N;
AFTER : A F T E R;
AGGREGATE :  A G G R E G A T E;
ALL : A L L;
ALSO : A L S O;
ALTER : A L T E R;
ALWAYS : A L W A Y S;
ANALYSE : A N A L Y S E;
ANALYZE : A N A L Y Z E;
AND : A N D;
ANOYBLOCK : A N O Y B L O C K;
ANY : A N Y;
APP : A P P;
ARRAY : A R R A Y;
AS : A S;
ASC : A S C;
ASSERTION : A S S E R T I O N;
ASSIGNMENT : A S S I G N M E N T;
ASYMMETRIC : A S Y M M E T R I C;
AT : A T;
ATTRIBUTE : A T T R I B U T E;
AUTHID : A U T H I D;
AUTHORIZATION : A U T H O R I Z A T I O N;
AUTOEXTEND : A U T O E X T E N D;
AUTOMAPPED : A U T O M A P P E D;
BACKWARD :      B A C K W A R D;
BARRIER : B A R R I E R;
BEFORE : B E F O R E;
BEGIN_P : B E G I N;
BETWEEN : B E T W E E N;
BIGINT : B I G I N T;
BINARY : B I N A R Y;
BINARY_DOUBLE : B I N A R Y UNDERSCORE D O U B L E;
BINARY_INTEGER : B I N A R Y UNDERSCORE I N T E G E R;
BIT : B I T;
BLOB_P : B L O B;
BODY : B O D Y;
BOGUS : B O G U S;
BOOLEAN_P : B O O L E A N;
BOTH : B O T H;
BUCKETS : B U C K E T S;
BY : B Y;
CACHE : C A C H E;
CALL : C A L L;
CALLED : C A L L E D;
CASCADE : C A S C A D E;
CASCADED : C A S C A D E D;
CASE : C A S E;
CAST : C A S T;
CATALOG_P : C A T A L O G;
CHAIN : C H A I N;
CHAR_P : C H A R;
CHARACTER : C H A R A C T E R;
CHARACTERISTICS : C H A R A C T E R I S T I C S;
CHECK : C H E C K;
CHECKPOINT : C H E C K P O I N T;
CLASS : C L A S S;
CLEAN : C L E A N;
CLOB : C L O B;
CLOSE : C L O S E;
CLUSTER : C L U S T E R;
COALESCE : C O A L E S C E;
COLLATE : C O L L A T E;
COLLATION : C O L L A T I O N;
COLUMN : C O L U M N;
COMMENT : C O M M E N T;
COMMENTS : C O M M E N T S;
COMMIT : C O M M I T;
COMMITTED : C O M M I T T E D;
COMPACT : C O M P A C T;
COMPATIBLE_ILLEGAL_CHARS : C O M P A T I B L E UNDERSCORE I L L E G A L UNDERSCORE C H A R S;
COMPRESS : C O M P R E S S;
CONCURRENTLY : C O N C U R R E N T L Y;
CONFIGURATION : C O N F I G U R A T I O N;
CONNECTION : C O N N E C T I O N;
CONSTRAINT : C O N S T R A I N T;
CONSTRAINTS : C O N S T R A I N T S;
CONTENT_P : C O N T E N T;
CONTINUE_P : C O N T I N U E;
CONVERSION_P : C O N V E R S I O N;
COORDINATOR : C O O R D I N A T O R;
COPY : C O P Y;
COST : C O S T;
CREATE : C R E A T E;
CROSS : C R O S S;
CSV : C S V;
CUBE : C U B E;
CURRENT_P : C U R R E N T;
CURRENT_CATALOG : C U R R E N T UNDERSCORE C A T A L O G;
CURRENT_DATE : C U R R E N T UNDERSCORE D A T E;
CURRENT_ROLE : C U R R E N T UNDERSCORE R O L E;
CURRENT_SCHEMA : C U R R E N T UNDERSCORE S C H E M A;
CURRENT_TIME : C U R R E N T UNDERSCORE T I M E;
CURRENT_TIMESTAMP : C U R R E N T UNDERSCORE T I M E S T A M P;
CURRENT_USER : C U R R E N T UNDERSCORE U S E R;
CURSOR : C U R S O R;
CYCLE : C Y C L E;
DATA_P : D A T A;
DATABASE : D A T A B A S E;
DATAFILE : D A T A F I L E;
DATE_P : D A T E;
DATE_FORMAT_P : D A T E UNDERSCORE F O R M A T;
DAY_P : D A Y;
DBCOMPATIBILITY_P : D B C O M P A T I B I L I T Y;
DEALLOCATE : D E A L L O C A T E;
DEC : D E C;
DECIMAL_P : D E C I M A L;
DECLARE : D E C L A R E;
DECODE : D E C O D E;
DEFAULT : D E F A U L T;
DEFAULTS : D E F A U L T S;
DEFERRABLE : D E F E R R A B L E;
DEFERRED : D E F E R R E D;
DEFINER : D E F I N E R;
DELETE_P : D E L E T E;
DELIMITER : D E L I M I T E R;
DELIMITERS : D E L I M I T E R S;
DELTA : D E L T A;
DELTAMERGE : D E L T A M E R G E;
DESC : D E S C;
DETERMINISTIC : D E T E R M I N I S T I C;
DICTIONARY : D I C T I O N A R Y;
DIRECT : D I R E C T;
DISABLE_P : D I S A B L E;
DISCARD : D I S C A R D;
DISTINCT : D I S T I N C T;
DISTRIBUTE : D I S T R I B U T E;
DISTRIBUTION : D I S T R I B U T I O N;
DO : D O;
DOCUMENT_P : D O C U M E N T;
DOMAIN_P : D O M A I N;
DOUBLE_P : D O U B L E;
DROP : D R O P;
EACH : E A C H;
ELSE : E L S E;
ENABLE_P : E N A B L E;
ENCODING : E N C O D I N G;
ENCRYPTED : E N C R Y P T E D;
END_P : E N D;
ENFORCED : E N F O R C E D;
ENUM_P : E N U M;
ESCAPE : E S C A P E;
EOL : E O L;
ESCAPING : E S C A P I N G;
EXCEPT : E X C E P T;
EXCHANGE : E X C H A N G E;
EXCLUDE :     E X C L U D E;
EXCLUDING : E X C L U D I N G;
EXCLUSIVE : E X C L U S I V E;
EXECUTE : E X E C U T E;
EXISTS : E X I S T S;
EXPLAIN : E X P L A I N;
EXTENSION : E X T E N S I O N;
EXTERNAL : E X T E R N A L;
EXTRACT : E X T R A C T;
FALSE_P : F A L S E;
FAMILY : F A M I L Y;
FETCH : F E T C H;
FILEHEADER_P : F I L E H E A D E R;
FIRST_P : F I R S T;
FIXED_P : F I X E D;
FLOAT_P : F L O A T;
FOLLOWING : F O L L O W I N G;
FOR : F O R;
FORCE : F O R C E;
FOREIGN : F O R E I G N;
FORMATTER : F O R M A T T E R;
FORWARD : F O R W A R D;
FREEZE : F R E E Z E;
FROM : F R O M;
FULL : F U L L;
FUNCTION : F U N C T I O N;
FUNCTIONS : F U N C T I O N S;
GLOBAL : G L O B A L;
GRANT : G R A N T;
GRANTED : G R A N T E D;
GREATEST : G R E A T E S T;
GROUP_P : G R O U P;
GROUPING_P : G R O U P I N G;
HANDLER : H A N D L E R;
HAVING : H A V I N G;
HDFSDIRECTORY : H D F S D I R E C T O R Y;
HEADER_P : H E A D E R;
HOLD : H O L D;
HOUR_P : H O U R;
IDENTIFIED : I D E N T I F I E D;
IDENTITY_P : I D E N T I T Y;
IF_P : I F;
IGNORE_EXTRA_DATA : I G N O R E UNDERSCORE E X T R A UNDERSCORE D A T A;
ILIKE : I L I K E;
IMMEDIATE : I M M E D I A T E;
IMMUTABLE : I M M U T A B L E;
IMPLICIT_P : I M P L I C I T;
IN_P : I N;
INCLUDING : I N C L U D I N G;
INCREMENT : I N C R E M E N T;
INDEX : I N D E X;
INDEXES : I N D E X E S;
INHERIT : I N H E R I T;
INHERITS : I N H E R I T S;
INITIAL_P : I N I T I A L;
INITIALLY : I N I T I A L L Y;
INITRANS : I N I T R A N S;
INLINE_P : I N L I N E;
INNER_P : I N N E R;
INOUT : I N O U T;
INPUT_P : I N P U T;
INSENSITIVE : I N S E N S I T I V E;
INSERT : I N S E R T;
INSTEAD : I N S T E A D;
INT_P : I N T;
INTEGER : I N T E G E R;
INTERSECT : I N T E R S E C T;
INTERVAL : I N T E R V A L;
INTO : I N T O;
INVOKER : I N V O K E R;
IS : I S;
ISNULL : I S N U L L;
ISOLATION : I S O L A T I O N;
JOIN : J O I N;
KEY : K E Y;
LABEL : L A B E L;
LANGUAGE : L A N G U A G E;
LARGE_P : L A R G E;
LAST_P : L A S T;
LC_COLLATE_P : L C UNDERSCORE C O L L A T E;
LC_CTYPE_P : L C UNDERSCORE C T Y P E;
LEADING : L E A D I N G;
LEAKPROOF : L E A K P R O O F;
LEAST : L E A S T;
LESS : L E S S;
LEFT : L E F T;
LEVEL : L E V E L;
LIKE : L I K E;
LIMIT : L I M I T;
LISTEN : L I S T E N;
LOAD : L O A D;
LOCAL : L O C A L;
LOCALTIME : L O C A L T I M E;
LOCALTIMESTAMP : L O C A L T I M E S T A M P;
LOCATION : L O C A T I O N;
LOCK_P : L O C K;
LOG_P : L O G;
LOGGING : L O G G I N G;
LOOP : L O O P;
MAPPING : M A P P I N G;
MATCH : M A T C H;
MAXEXTENTS : M A X E X T E N T S;
MAXSIZE : M A X S I Z E;
MAXTRANS : M A X T R A N S;
MAXVALUE : M A X V A L U E;
MERGE : M E R G E;
MINUS_P : M I N U S;
MINUTE_P : M I N U T E;
MINVALUE : M I N V A L U E;
MINEXTENTS : M I N E X T E N T S;
MODE : M O D E;
MODIFY_P : M O D I F Y;
MONTH_P : M O N T H;
MOVE : M O V E;
MOVEMENT : M O V E M E N T;
NAME_P : N A M E;
NAMES : N A M E S;
NATIONAL : N A T I O N A L;
NATURAL : N A T U R A L;
NCHAR : N C H A R;
NEXT : N E X T;
NLSSORT : N L S S O R T;
NO : N O;
NOCOMPRESS : N O C O M P R E S S;
NOCYCLE : N O C Y C L E;
NODE : N O D E;
NOLOGGING : N O L O G G I N G;
NOMAXVALUE : N O M A X V A L U E;
NOMINVALUE : N O M I N V A L U E;
NON : N O N;
NONE : N O N E;
NOT : N O T;
NOTHING : N O T H I N G;
NOTIFY : N O T I F Y;
NOTNULL : N O T N U L L;
NOWAIT : N O W A I T;
NULL_P : N U L L;
NULLIF : N U L L I F;
NULLS_P : N U L L S;
NUMBER_P : N U M B E R;
NUMERIC : N U M E R I C;
NUMSTR : N U M S T R;
NVARCHAR2 : N V A R C H A R '2';
NVL : N V L;
OBJECT_P : O B J E C T;
OF : O F;
OFF : O F F;
OFFSET : O F F S E T;
OIDS : O I D S;
ON : O N;
ONLY : O N L Y;
OPERATOR : O P E R A T O R;
OPTIMIZATION : O P T I M I Z A T I O N;
OPTION : O P T I O N;
OPTIONS : O P T I O N S;
OR : O R;
ORDER : O R D E R;
OUT_P : O U T;
OUTER_P : O U T E R;
OVER : O V E R;
OVERLAPS : O V E R L A P S;
OVERLAY : O V E R L A Y;
OWNED : O W N E D;
OWNER : O W N E R;
PARSER : P A R S E R;
PARTIAL : P A R T I A L;
PARTITION : P A R T I T I O N;
PARTITIONS : P A R T I T I O N S;
PASSING : P A S S I N G;
PASSWORD : P A S S W O R D;
PCTFREE : P C T F R E E;
PER_P : P E R;
PERCENT : P E R C E N T;
PERFORMANCE : P E R F O R M A N C E;
PERM : P E R M;
PLACING : P L A C I N G;
PLANS : P L A N S;
POSITION : P O S I T I O N;
POOL : P O O L;
PRECEDING : P R E C E D I N G;
PRECISION : P R E C I S I O N;
PREFERRED : P R E F E R R E D;
PREFIX : P R E F I X;
PRESERVE : P R E S E R V E;
PREPARE : P R E P A R E;
PREPARED : P R E P A R E D;
PRIMARY : P R I M A R Y;
PRIOR : P R I O R;
PRIVILEGES : P R I V I L E G E S;
PRIVILEGE : P R I V I L E G E;
PROCEDURAL : P R O C E D U R A L;
PROCEDURE : P R O C E D U R E;
PACKAGE : P A C K A G E;
PROFILE : P R O F I L E;
QUERY : Q U E R Y;
QUOTE : Q U O T E;
RANGE : R A N G E;
RAW : R A W;
READ : R E A D;
REAL : R E A L;
REASSIGN : R E A S S I G N;
REBUILD : R E B U I L D;
RECHECK : R E C H E C K;
RECURSIVE : R E C U R S I V E;
REF : R E F;
REFERENCES : R E F E R E N C E S;
REINDEX : R E I N D E X;
REJECT_P : R E J E C T;
RELATIVE_P : R E L A T I V E;
RELEASE : R E L E A S E;
RELOPTIONS : R E L O P T I O N S;
REMOTE_P : R E M O T E;
RENAME : R E N A M E;
REPEATABLE : R E P E A T A B L E;
REPLACE : R E P L A C E;
REPLICA : R E P L I C A;
RESET : R E S E T;
RESIZE : R E S I Z E;
RESOURCE : R E S O U R C E;
RESTART : R E S T A R T;
RESTRICT : R E S T R I C T;
RETURN : R E T U R N;
RETURNING : R E T U R N I N G;
RETURNS : R E T U R N S;
REUSE : R E U S E;
REVOKE : R E V O K E;
RIGHT : R I G H T;
ROLE : R O L E;
ROLLBACK : R O L L B A C K;
ROLLUP : R O L L U P;
ROW : R O W;
ROWS : R O W S;
RULE : R U L E;
SAVEPOINT : S A V E P O I N T;
SCHEMA : S C H E M A;
SCROLL : S C R O L L;
SEARCH : S E A R C H;
SECOND_P : S E C O N D;
SECURITY : S E C U R I T Y;
SELECT : S E L E C T;
SEQUENCE : S E Q U E N C E;
SEQUENCES : S E Q U E N C E S;
SERIALIZABLE : S E R I A L I Z A B L E;
SERVER : S E R V E R;
SESSION : S E S S I O N;
SESSION_USER : S E S S I O N UNDERSCORE U S E R;
SET : S E T;
SETS : S E T S;
SETOF : S E T O F;
SHARE : S H A R E;
SHOW : S H O W;
SIMILAR : S I M I L A R;
SIMPLE : S I M P L E;
SIZE : S I Z E;
SMALLDATETIME : S M A L L D A T E T I M E;
SMALLDATETIME_FORMAT_P : S M A L L D A T E T I M E UNDERSCORE F O R M A T;
SMALLINT : S M A L L I N T;
SNAPSHOT : S N A P S H O T;
SOME : S O M E;
SPACE : S P A C E;
SPLIT : S P L I T;
STABLE : S T A B L E;
STANDALONE_P : S T A N D A L O N E;
START : S T A R T;
STATEMENT : S T A T E M E N T;
STATISTICS : S T A T I S T I C S;
STDIN : S T D I N;
STDOUT : S T D O U T;
STORAGE : S T O R A G E;
STORE_P : S T O R E;
STRICT_P : S T R I C T;
STRIP_P : S T R I P;
SUBSTRING : S U B S T R I N G;
SYMMETRIC : S Y M M E T R I C;
SYSDATE : S Y S D A T E;
SYSID : S Y S I D;
SYSTEM_P : S Y S T E M;
SYS_REFCURSOR : S Y S UNDERSCORE R E F C U R S O R;
TABLE : T A B L E;
TABLES : T A B L E S;
TABLESPACE : T A B L E S P A C E;
TEMP : T E M P;
TEMPLATE : T E M P L A T E;
TEMPORARY : T E M P O R A R Y;
TEXT_P : T E X T;
THAN : T H A N;
THEN : T H E N;
TIME : T I M E;
TIME_FORMAT_P : T I M E UNDERSCORE F O R M A T;
TIMESTAMP : T I M E S T A M P;
TIMESTAMP_FORMAT_P : T I M E S T A M P UNDERSCORE F O R M A T;
TINYINT : T I N Y I N T;
TO : T O;
TRAILING : T R A I L I N G;
TRANSACTION : T R A N S A C T I O N;
TREAT : T R E A T;
TRIGGER : T R I G G E R;
TRIM : T R I M;
TRUE_P : T R U E;
TRUNCATE : T R U N C A T E;
TRUSTED : T R U S T E D;
TYPE_P : T Y P E;
TYPES_P : T Y P E S;
UNBOUNDED : U N B O U N D E D;
UNCOMMITTED : U N C O M M I T T E D;
UNENCRYPTED : U N E N C R Y P T E D;
UNION : U N I O N;
UNIQUE : U N I Q U E;
UNKNOWN : U N K N O W N;
UNLIMITED : U N L I M I T E D;
UNLISTEN : U N L I S T E N;
UNLOCK : U N L O C K;
UNLOGGED : U N L O G G E D;
UNTIL : U N T I L;
UNUSABLE : U N U S A B L E;
UPDATE : U P D A T E;
USER : U S E R;
USING : U S I N G;
VACUUM : V A C U U M;
VALID : V A L I D;
VALIDATE : V A L I D A T E;
VALIDATION : V A L I D A T I O N;
VALIDATOR : V A L I D A T O R;
VALUE_P : V A L U E;
VALUES : V A L U E S;
VARCHAR : V A R C H A R;
VARCHAR2 : V A R C H A R '2';
VARIADIC : V A R I A D I C;
VARRAY : V A R R A Y;
VARYING : V A R Y I N G;
VERBOSE : V E R B O S E;
VERSION_P : V E R S I O N;
VIEW : V I E W;
VOLATILE : V O L A T I L E;
WHEN : W H E N;
WHERE : W H E R E;
WHITESPACE_P : W H I T E S P A C E;
WINDOW : W I N D O W;
WITH : W I T H;
WITHOUT : W I T H O U T;
WORK : W O R K;
WORKLOAD : W O R K L O A D;
WRAPPER : W R A P P E R;
WRITE : W R I T E;
XML_P : X M L;
XMLATTRIBUTES : X M L A T T R I B U T E S;
XMLCONCAT : X M L C O N C A T;
XMLELEMENT : X M L E L E M E N T;
XMLEXISTS : X M L E X I S T S;
XMLFOREST : X M L F O R E S T;
XMLPARSE : X M L P A R S E;
XMLPI : X M L P I;
XMLROOT : X M L R O O T;
XMLSERIALIZE : X M L S E R I A L I Z E;
YEAR_P : Y E A R;
YES_P : Y E S;
ZONE : Z O N E;

/*
 * The grammar thinks these are keywords, but they are not in the kwlist.h
 * list and so can never be entered directly.  The filter in parser.c
 * creates these tokens when required.
 */
NULLS_FIRST : NULLS_P T_whitespace+ FIRST_P;
NULLS_LAST  : NULLS_P T_whitespace+ LAST_P;
WITH_TIME : WITH T_whitespace+ TIME;
PARTITION_FOR : PARTITION T_whitespace+ FOR;
BEGIN_NON_ANOYBLOCK : BEGIN_P T_whitespace+ NON T_whitespace+ ANOYBLOCK;

T_TYPECAST : '::';
T_PARAM : '$' T_integer;
T_COLON_EQUALS : ':=';
T_PARA_EQUALS : '=>';

/* making lexer case insensitive */
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');
fragment UNDERSCORE: '_';

/* common symbols used in parser grammar */
T_LEFTBRACKET : '(';
T_RIGHTBRACKET : ')';
T_LEFT_SQBRACKET : '[';
T_RIGHT_SQBRACKET : ']';
T_COMMA : ',';
T_EQUALS : '=';
T_COLON : ':';
T_PLUS : '+';
T_MINUS : '-';
T_PRODUCT : '*';
T_DIVIDE : '/';
T_MODULO : '%';
T_EXPONENT : '^';
T_LESSTHAN : '<';
T_GREATERTHAN : '>';
T_DOT : '.';
T_SEMICOLON : ';';

LESS_LESS       : '<<';
GREATER_GREATER : '>>';

DOT_DOT         : '..' ;
SCONST          : XQString
                | XNString
                | XEString
                | XDOLQString
                ;
BCONST          : XBString;
XCONST          : XHString;
IDENT           : '`'? XDQString '`'?
                | XUIString
                | IDENT_START IDENT_CONT*
                | OLTP_IDENT
                | IDENT_START_SPC T_space*
				| IDENT_START_SPC IDENT_CONT*
                ;
PARAM           : '$' T_integer ;
FCONST          : T_real | T_decimal;
ICONST          : T_integer;

fragment OP_SIMPLE :
    ( '<'
    | '>'
    | '='
    | '/' { _input.LA(1) != '*' }?
    | '*'
    )+
    ;

fragment OP_MINUS : '-' { _input.LA(1) != '-' }? ;
fragment OP_PLUS  : '+' ;

fragment OP_PLUSMINUS : ( OP_PLUS | OP_MINUS ) ;

fragment OP_COMPLEX   : [~!@#%&|`?^]+ ;

fragment OP_WITH_COMPLEX
    :
    ( OP_PLUSMINUS
    | OP_SIMPLE
    )*
    OP_COMPLEX
    ( OP_PLUSMINUS
    | OP_SIMPLE
    | OP_COMPLEX
    )*
    ;

fragment OP_WITHOUT_COMPLEX
    : ( OP_SIMPLE | OP_PLUSMINUS )* OP_SIMPLE
    ;

Op  :
    ( OP_WITHOUT_COMPLEX
    | OP_WITH_COMPLEX
    | OP_PLUSMINUS
    )
    ;

/** Line comment */
T_comment       : '--' ( ~[\n\r] )* -> channel(HIDDEN);
/** C-style comments */

T_ccomment      : '/*' ( T_ccomment | . )*? '*/' -> channel(HIDDEN);

T_space         : [ \t\n\r\f]  -> channel(HIDDEN);
T_newline       : [\n\r]  -> channel(HIDDEN);
T_whitespace    : ( T_space+ | T_comment )  -> channel(HIDDEN);

/** Numbers */
fragment T_real          : ( T_integer | T_decimal ) [Ee] [-+]? DIGIT+ ;
fragment T_decimal       : ( ( DIGIT* '.' DIGIT+ ) | ( DIGIT+ '.' DIGIT* ) ) ;
fragment T_integer       : DIGIT+ ;

/*
 * SQL requires at least one newline in the whitespace separating
 * string literals that are to be concatenated.  Silly, but who are we
 * to argue?  Note that {whitespace_with_newline} should not have * after
 * it, whereas {whitespace} should generally have a * after it...
 */

T_special_whitespace      : ( T_space+ | T_comment T_newline)  -> channel(HIDDEN);
T_horiz_whitespace        : (T_horiz_space | T_comment)  -> channel(HIDDEN);
T_whitespace_with_newline : T_horiz_whitespace* T_newline T_special_whitespace*  -> channel(HIDDEN);

T_horiz_space : [ \t\f]  -> channel(HIDDEN);

/*
 * To ensure that {quotecontinue} can be scanned without having to back up
 * if the full pattern isn't matched, we include trailing whitespace in
 * {quotestop}.  This matches all cases where {quotecontinue} fails to match,
 * except for {quote} followed by whitespace and just one "-" (not two,
 * which would start a {comment}).  To cover that we have {quotefail}.
 * The actions for {quotestop} and {quotefail} must throw back characters
 * beyond the quote proper.
 */
fragment T_quote         : '\'' ;
fragment T_nonquote      : ~'\''+ ;
fragment T_xeinside      : ~[\\']+ ;
fragment T_quotecontinue : T_quote T_whitespace_with_newline T_quote ;
fragment T_xqdouble      : T_quote T_quote ;
fragment OLTP_IDENT      : '`' (T_dquote | T_nondquote)+ '`';

/** Quoted string */
fragment XQString : T_quote ( T_xqdouble | T_quotecontinue | T_nonquote )* T_quote ;
/** Binary string number */
fragment XBString : B T_quote ( T_quotecontinue | [01]+ )* T_quote ;
/** Hexadecimal number */
fragment XHString : X T_quote ( T_quotecontinue | HDIGIT+ )* T_quote ;
/** National character */
fragment XNString : N XQString ;
/* Quoted string that allows backslash escapes */
fragment XEString : E T_quote ( T_xqdouble
                                 | T_quotecontinue
                                 | '\\' ( 'x' ( HDIGIT HDIGIT? ) // hex
                                        | ( 'u' HDIGIT HDIGIT HDIGIT HDIGIT
                                          | 'U' HDIGIT HDIGIT HDIGIT HDIGIT HDIGIT HDIGIT HDIGIT HDIGIT
                                          ) // unicode
                                        | NOT_ODIGIT // escape
                                        | ( ODIGIT ODIGIT ODIGIT | ODIGIT ODIGIT | ODIGIT ) // octets
                                        ) // escapes
                                 | T_xeinside )* T_quote ;

fragment DIGIT         : [0-9] ;
fragment ODIGIT        : [0-7] ;
fragment NOT_ODIGIT    : ~[0-7] ;
fragment HDIGIT        : [0-9A-Fa-f] ;

/* $foo$ style quotes ("dollar quoting")
 * The quoted string starts with $foo$ where "foo" is an optional string
 * in the form of an identifier, except that it may not contain "$",
 * and extends to the first occurrence of an identical string.
 * There is *no* processing of the quoted text.
 *
 * {dolqfailed} is an error rule to avoid scanner backup when {dolqdelim}
 * fails to match its trailing "$".
 */

fragment XDOLQString : '$$' .*? '$$'
                     | '$BODY$' .*? '$BODY$' // this is a hack until the $$ string is implemented
                     | '$function$' .*? '$function$'
                     ;

//support unicode characters
fragment IDENT_START_SPC   : [`~{}@#$%/\\^&|:-] ;
fragment IDENT_START   : [A-Za-z] | ~[\u0000-\u007F\uD800-\uDBFF] | [\uD800-\uDBFF] [\uDC00-\uDFFF];
fragment IDENT_CONT    : [A-Za-z0-9$_] | ~[\u0000-\u007F\uD800-\uDBFF] | [\uD800-\uDBFF] [\uDC00-\uDFFF] ;

/* Double quote
 * Allows embedded spaces and other special characters into identifiers.
 */
 /*
  * Gauss when has two double quotes inside a double quoted string, it takes it as one double quote.
  * i.e "Hello""World" = "Hello"World"
  * so the simple XDQString has been replaced with a new rule.
  */
/*fragment XDQString    : T_dquote ( T_xddouble | T_nondquote )*? T_dquote ;*/
fragment XDQString    : T_dquote ( '""' | T_nondquote )* T_dquote
                           {
                             String s = getText();
                             s = s.substring(1, s.length() - 1); // strip the leading and trailing quotes
                             s = s.replace("\"\"", "\""); // replace all double quotes with single quotes
                             setText(s);
                           }
                           ;

fragment T_dquote     : '"' ;
fragment T_nondquote  : ~'"'+ ;
fragment T_xddouble   : T_dquote T_dquote ;

/* Unicode escapes */
fragment T_uescape    : U E S C A P E T_whitespace* T_quote T_nondquote*? T_quote ;

fragment XUIString    : U '&' XDQString ( T_whitespace* T_uescape )? ;

PSQL_COMMAND : '\\' ( ~[\n\r] )*;

ErrorChar : . ;