parser grammar PostgresParser;

options { tokenVocab=PostgresLexer; }

@header {
    package com.huawei.mppdbide.parser.grammar;
}

allStmt : (unit_statement)+ | explainable_statement+;

functionStmt : createFunction+;

explainable_statement : EXPLAIN explain_type? unit_statement ;

explain_type : VERBOSE
                | PERFORMANCE
                | ANALYSE VERBOSE?
                | ANALYZE VERBOSE?
                | T_LEFTBRACKET target_list T_RIGHTBRACKET //target_list is temporarily used for 18.1 version
                ;

unit_statement : (selectStmt
                | insertStmt
                | updateStmt
                | deleteStmt
                | any_stmt) T_SEMICOLON
                ;

selectStmt : select_no_parens
           | select_with_parens
           ;

select_with_parens : T_LEFTBRACKET (select_no_parens | select_with_parens) T_RIGHTBRACKET ;

select_no_parens : with_clause? select_clause sort_clause? ( select_limit opt_for_locking_clause
                                                            | for_locking_clause opt_select_limit
														   )? ;

with_clause : WITH RECURSIVE? cte_list;

cte_list : common_table_expr (T_COMMA common_table_expr)* ;

common_table_expr : name opt_name_list AS T_LEFTBRACKET preparableStmt T_RIGHTBRACKET ;

opt_name_list : T_LEFTBRACKET name_list T_RIGHTBRACKET | ;

name_list : (name T_COMMA)* name ;

preparableStmt : selectStmt
               | insertStmt
               | updateStmt
               | deleteStmt
               ;

insertStmt : opt_with_clause insertInto insert_rest returning_clause?;

insertInto : INSERT INTO insertTable;

insertTable : qualified_name;

returning_clause : RETURNING returningTargetList
                   |
				   ;

returningTargetList : 	target_list;			 
				 
target_list : target_el (T_COMMA target_el)* ; //rule simplified for select alias

target_el : T_PRODUCT
          | a_expr ( AS colLabel
                     | IDENT
				    )?
          ;

insert_rest : insertColumnFull? (selectStmt
            | insertDefaultValues)
            ;
			
insertDefaultValues : DEFAULT VALUES;
			
			
insertColumnFull : (T_LEFTBRACKET insert_column_list T_RIGHTBRACKET);			
			

insert_column_list : (insert_column_item T_COMMA)* insert_column_item ;

insert_column_item : colId opt_indirection ;

updateStmt : opt_with_clause updateTableName updateSetCluase updateFrom_cluase? where_or_current_clause returning_clause ;

updateTableName : UPDATE onlycluase? relation_expr_opt_alias;

updateSetCluase : SET set_clause_list;

updateFrom_cluase : from_clause;

relation_expr_opt_alias : relation_expr AS? colId? ;

relation_expr : qualified_name T_PRODUCT?
              | ONLY ( qualified_name
                       | T_LEFTBRACKET qualified_name T_RIGHTBRACKET
			          )
              ;

opt_with_clause : with_clause
                |
                ;

set_clause_list : (set_clause T_COMMA)* set_clause ;

set_clause : single_set_clause
           | multiple_set_clause
           ;

single_set_clause : set_target T_EQUALS ctext_expr;

multiple_set_clause : multiple_set_clause_columns T_EQUALS ( multiple_set_clause_ctext_row
                                                                              | selectStmt)
                    ;

multiple_set_clause_columns : T_LEFTBRACKET set_target_list T_RIGHTBRACKET;

multiple_set_clause_ctext_row : ctext_row;

where_clause : WHERE a_expr?
             |
             ;
			 
group_clause : GROUP_P BY group_by_list
             |
             ;


group_by_list : group_by_item (T_COMMA group_by_item)* //rule simplified for select alias
              ;

group_by_item : a_expr
              | empty_grouping_set
              | cube_clause
              | rollup_clause
              | grouping_sets_clause
              ;

empty_grouping_set : T_LEFTBRACKET T_RIGHTBRACKET;

cube_clause : CUBE T_LEFTBRACKET expr_list T_RIGHTBRACKET;

rollup_clause : ROLLUP T_LEFTBRACKET expr_list T_RIGHTBRACKET;

grouping_sets_clause : GROUPING_P SETS T_LEFTBRACKET group_by_list T_RIGHTBRACKET;

having_clause : HAVING a_expr
              |
              ;

ctext_expr : a_expr
           | DEFAULT
           ;

ctext_row : T_LEFTBRACKET ctext_expr_list T_RIGHTBRACKET;

opt_distinct : DISTINCT (ON T_LEFTBRACKET expr_list T_RIGHTBRACKET)?
             | ALL
             |
             ;

ctext_expr_list : (ctext_expr T_COMMA)* ctext_expr
                ;

from_clause : FROM from_list
            |
            ;

from_list : table_ref (T_COMMA table_ref)* //rule simplified for select alias
          ;

table_ref : relation_expr ( alias_clause
                            | PARTITION T_LEFTBRACKET name T_RIGHTBRACKET alias_clause?
                            | PARTITION_FOR T_LEFTBRACKET maxValueList T_RIGHTBRACKET alias_clause?
		                  )?
           | func_table ( alias_clause
		                 | AS T_LEFTBRACKET tableFuncElementList T_RIGHTBRACKET
                         | AS? colId T_LEFTBRACKET tableFuncElementList T_RIGHTBRACKET
						 )?
           | select_with_parens alias_clause?
           | table_ref join_type table_ref join_qual?
           | T_LEFTBRACKET table_ref join_type table_ref join_qual? T_RIGHTBRACKET? alias_clause?
           ;

join_qual : USING T_LEFTBRACKET name_list T_RIGHTBRACKET
          | ON a_expr
          ;

join_type : FULL JOIN
          | LEFT JOIN
          | FULL JOIN
          | RIGHT JOIN
          | JOIN
          | LEFT OUTER_P
          | FULL OUTER_P
          | RIGHT OUTER_P
          | INNER_P JOIN
          | NATURAL JOIN
          | CROSS JOIN
          | LEFT OUTER_P JOIN
          | FULL OUTER_P JOIN
          | RIGHT OUTER_P JOIN
          ;

join_outer : OUTER_P
            |
           ;

tableFuncElementList : (tableFuncElement T_COMMA)* tableFuncElement
                     ;

tableFuncElement : colId typename opt_collate_clause;

opt_collate_clause : COLLATE any_name
                   |
                   ;

alias_clause : AS? colId (T_LEFTBRACKET name_list T_RIGHTBRACKET)?
             ;

maxValueList : (maxValueItem T_COMMA)* maxValueItem
             ;

maxValueItem : a_expr
             | MAXVALUE
             ;

func_table : func_expr;

set_target : colId opt_indirection;

set_target_list : (set_target T_COMMA)* set_target
                ;

deleteStmt : opt_with_clause deleteTableName using_clause? where_or_current_clause returning_clause
           ;
		   
deleteTableName : DELETE_P FROM onlycluase? relation_expr_opt_alias;

onlycluase : 	ONLY;	   

where_or_current_clause : WHERE ( a_expr
                                  | CURRENT_P OF cursor_name
						         )
                        |
                        ;

cursor_name : name;

using_clause : USING from_list
             |
             ;

name : colId;

colId : unreserved_keyword
      | col_name_keyword
      | IDENT
      ;

select_clause : simple_select
              | select_with_parens
              | select_clause ( UNION //added from simple_select to remove left recursion
                               | INTERSECT 
                               | EXCEPT
                               | MINUS_P 
                              ) opt_all select_clause
              ;

simple_select : SELECT opt_distinct target_list? into_clause from_clause where_clause group_clause having_clause window_clause //rule simplified for select alias
              | values_clause
              | TABLE relation_expr
              ;

opt_sort_clause : sort_clause | ;

select_limit : limit_clause offset_clause?
             | offset_clause limit_clause?
             ;

limit_clause : LIMIT select_limit_value (T_COMMA select_offset_value)?
             | FETCH first_or_next opt_select_fetch_first_value row_or_rows ONLY
             ;

first_or_next : FIRST_P
              | NEXT
              ;

opt_select_fetch_first_value : signedIconst
                             | T_LEFTBRACKET a_expr T_RIGHTBRACKET
                             |
                             ;

signedIconst : ICONST
             | T_MINUS ICONST
             | T_PLUS ICONST
             ;

row_or_rows : ROW
            | ROWS
            ;

select_offset_value : a_expr;

select_limit_value : a_expr
                   | ALL
                   ;

offset_clause : OFFSET ( select_offset_value
                        | select_offset_value2 row_or_rows
			            )
              ;

select_offset_value2 : c_expr;

opt_for_locking_clause : for_locking_clause
                       |
                       ;

for_locking_clause : for_locking_items
                   | FOR READ ONLY
                   ;

for_locking_items : for_locking_item+
                  ;

for_locking_item : FOR UPDATE locked_rels_list opt_nowait
                 | FOR SHARE locked_rels_list opt_nowait
                 ;

locked_rels_list : OF qualified_name_list
                 |
                 ;

qualified_name_list : (qualified_name T_COMMA)* qualified_name
                    ;

schema_name : colId               ;

qualified_name : colId indirection?               ;

funct_name : indirection?               ;

opt_nowait : NOWAIT
           |
           ;

opt_select_limit : select_limit
                 |
                 ;

a_expr : c_expr
       | a_expr ( T_TYPECAST typename
				| COLLATE any_name
				| AT TIME ZONE a_expr
				| T_PLUS a_expr
				| T_MINUS a_expr
				| T_PRODUCT a_expr
				| T_DIVIDE a_expr
				| T_MODULO a_expr
				| T_EXPONENT a_expr
				| T_LESSTHAN a_expr
				| T_GREATERTHAN a_expr
				| T_EQUALS a_expr
				| cmpOp a_expr
				| qual_Op a_expr
                | AND a_expr
				| OR a_expr
       		    | NOT? ( LIKE 
       		             | ILIKE 
       		             | SIMILAR TO ) a_expr (ESCAPE a_expr)?
				| ISNULL
				| NOTNULL
       		    | IS ( NOT? ( NULL_P
						      | TRUE_P
							  | FALSE_P
						      | UNKNOWN
						      | DISTINCT FROM a_expr
						      | OF T_LEFTBRACKET type_list T_RIGHTBRACKET
						      | DOCUMENT_P
						     )
					 )
				| NOT? BETWEEN SYMMETRIC b_expr AND b_expr
				| NOT? BETWEEN opt_asymmetric b_expr AND b_expr
				| NOT? IN_P in_expr
				| subquery_Op sub_type ( select_with_parens | T_LEFTBRACKET a_expr T_RIGHTBRACKET)
				)
       | ( T_PLUS
		  | T_MINUS
          | qual_Op
          | NOT )
		  a_expr
       | row OVERLAPS row
       | UNIQUE select_with_parens
       ;

subquery_Op : all_Op
            | OPERATOR T_LEFTBRACKET any_operator T_RIGHTBRACKET
            | NOT? LIKE
            | NOT? ILIKE
            ;

any_operator : all_Op
             | colId T_DOT any_operator
             ;

all_Op : Op
       | cmpOp
       | mathOp
       ;

cmpOp :
      ;

mathOp : T_PLUS
       | T_MINUS
       | T_PRODUCT
       | T_DIVIDE
       | T_MODULO
       | T_EXPONENT
       | T_LESSTHAN
       | T_GREATERTHAN
       | T_EQUALS
       ;

sub_type : ANY
         | SOME
         | ALL
         ;

in_expr : select_with_parens
        | T_LEFTBRACKET expr_list T_RIGHTBRACKET
        ;



expr_list : (a_expr T_COMMA)* a_expr;

opt_asymmetric : ASYMMETRIC
               |
               ;

b_expr : c_expr
       | b_expr ( T_TYPECAST typename
                  | T_PLUS b_expr
                  | T_MINUS b_expr
                  | T_PRODUCT b_expr
                  | T_DIVIDE b_expr
                  | T_MODULO b_expr
                  | T_EXPONENT b_expr
                  | T_LESSTHAN b_expr
                  | T_GREATERTHAN b_expr
                  | T_EQUALS b_expr
                  | cmpOp b_expr
                  | qual_Op b_expr
                  | qual_Op
				  | IS ( NOT? DISTINCT FROM b_expr
                       | NOT? OF T_LEFTBRACKET type_list T_RIGHTBRACKET
                       | NOT? DOCUMENT_P
	                 )
	             )
       | T_PLUS b_expr
       | T_MINUS b_expr
       | qual_Op b_expr
       ;

type_list : (typename T_COMMA)* typename
          ;

row : ROW T_LEFTBRACKET expr_list? T_RIGHTBRACKET
    | T_LEFTBRACKET expr_list T_COMMA a_expr T_RIGHTBRACKET
    ;

qual_Op : Op
        | OPERATOR T_LEFTBRACKET any_operator T_RIGHTBRACKET
        ;

c_expr : columnref
       | aexprConst
       | T_PARAM opt_indirection
       | c_exprWithParams //rule simplified for select alias
       | case_expr
       | func_expr
       | select_with_parens indirection?
       | EXISTS select_with_parens
       | ARRAY (select_with_parens|array_expr)
       | explicit_row
       | implicit_row
       | GROUPING_P T_LEFTBRACKET expr_list T_RIGHTBRACKET
       ;

c_exprWithParams : T_LEFTBRACKET a_expr T_RIGHTBRACKET? opt_indirection;

implicit_row : T_LEFTBRACKET expr_list T_COMMA a_expr T_RIGHTBRACKET;

array_expr : T_LEFT_SQBRACKET (expr_list | array_expr_list)?  T_RIGHT_SQBRACKET
           ;

array_expr_list : (array_expr T_COMMA)* array_expr
                ;

explicit_row : ROW T_LEFTBRACKET expr_list? T_RIGHTBRACKET
             ;

indirection : indirection_el+;

indirection_el : T_DOT ( attr_name
                         | T_PRODUCT
			           )?
               | T_LEFT_SQBRACKET a_expr? ( T_RIGHT_SQBRACKET? //rule simplified for select alias
											| T_COLON? a_expr? T_RIGHT_SQBRACKET? //rule simplified for select alias
											| T_COMMA? a_expr? T_RIGHT_SQBRACKET? //rule simplified for select alias
			                              )
               ;

attr_name : colLabel;

colLabel : IDENT
         | unreserved_keyword
         | col_name_keyword
         | type_func_name_keyword
         | reserved_keyword
         ;

type_func_name_keyword : AUTHORIZATION
                       | BINARY
                       | COLLATION
                       | COMPACT
                       | CONCURRENTLY
                       | CROSS
                       | CURRENT_SCHEMA
                       | DELTAMERGE
                       | FREEZE
                       | FULL
                       | HDFSDIRECTORY
                       | ILIKE
                       | INNER_P
                       | ISNULL
                       | JOIN
                       | LEFT
                       | LIKE
                       | NATURAL
                       | NOTNULL
                       | OUTER_P
                       | OVER
                       | OVERLAPS
                       | RIGHT
                       | SIMILAR
                       | VERBOSE
                       ;

reserved_keyword: ALL
                | ANALYSE
                | ANALYZE
                | AND
                | ANY
                | ARRAY
                | AS
                | ASC
                | ASYMMETRIC
                | AUTHID
                | BOTH
                | BUCKETS
                | CASE
                | CAST
                | CHECK
                | COLLATE
                | COLUMN
                | CONSTRAINT
                | CREATE
                | CURRENT_CATALOG
                | CURRENT_DATE
                | CURRENT_ROLE
                | CURRENT_TIME
                | CURRENT_TIMESTAMP
                | CURRENT_USER
                | DEFAULT
                | DEFERRABLE
                | DESC
                | DISTINCT
                | DO
                | ELSE
                | END_P
                | EXCEPT
                | FALSE_P
                | FETCH
                | FOR
                | FOREIGN
                | FROM
                | FUNCTION
                | GRANT
                | GROUP_P
                | HAVING
                | IN_P
                | INITIALLY
                | INTERSECT
                | INTO
                | IS
                | LEADING
                | LESS
                | LIMIT
                | LOCALTIME
                | LOCALTIMESTAMP
                | MAXVALUE
                | MINUS_P
                | MODIFY_P
                | NLSSORT
                | NOT
                | NULL_P
                | OFFSET
                | ON
                | ONLY
                | OR
                | ORDER
                | PERFORMANCE
                | PLACING
                | PRIMARY
                | PROCEDURE
                | PACKAGE
                | REFERENCES
                | REJECT_P
                | RETURN
                | RETURNING
                | SELECT
                | SESSION_USER
                | SOME
                | SPLIT
                | SYMMETRIC
                | SYSDATE
                | TABLE
                | THEN
                | TO
                | TRAILING
                | TRUE_P
                | UNION
                | UNIQUE
                | USER
                | USING
                | VARIADIC
                | WHEN
                | WHERE
                | WINDOW
                | WITH
                ;

case_expr : CASE case_arg when_clause_list case_default END_P
          | DECODE T_LEFTBRACKET a_expr T_COMMA expr_list T_RIGHTBRACKET
          ;

case_arg : a_expr | ;

when_clause_list : when_clause+;

when_clause : WHEN a_expr THEN a_expr;

case_default : ELSE a_expr | ;

func_expr : func_name T_LEFTBRACKET ( VARIADIC func_arg_expr
									  | func_arg_list
									  | func_arg_list (T_COMMA VARIADIC func_arg_expr|sort_clause)
									 )? T_RIGHTBRACKET over_clause
                                    | ALL func_arg_list opt_sort_clause T_RIGHTBRACKET over_clause
								    | DISTINCT func_arg_list opt_sort_clause T_RIGHTBRACKET over_clause
									| T_PRODUCT T_RIGHTBRACKET over_clause
          | COLLATION FOR T_LEFTBRACKET a_expr T_RIGHTBRACKET
          | CURRENT_DATE
          | CURRENT_TIME (T_LEFTBRACKET ICONST T_RIGHTBRACKET)?
          | CURRENT_TIMESTAMP (T_LEFTBRACKET ICONST T_RIGHTBRACKET)?
          | LOCALTIME (T_LEFTBRACKET ICONST T_RIGHTBRACKET)?
          | LOCALTIMESTAMP (T_LEFTBRACKET ICONST T_RIGHTBRACKET)?
          | SYSDATE
          | CURRENT_ROLE
          | CURRENT_USER
          | SESSION_USER
          | USER
          | CURRENT_CATALOG
          | CURRENT_SCHEMA
          | CAST T_LEFTBRACKET a_expr AS typename T_RIGHTBRACKET
          | EXTRACT T_LEFTBRACKET extract_list T_RIGHTBRACKET
          | OVERLAY T_LEFTBRACKET overlay_list T_RIGHTBRACKET
          | POSITION T_LEFTBRACKET position_list T_RIGHTBRACKET
          | SUBSTRING T_LEFTBRACKET substr_list T_RIGHTBRACKET
          | TREAT T_LEFTBRACKET a_expr AS typename T_RIGHTBRACKET
          | TRIM T_LEFTBRACKET ( BOTH 
								 | LEADING 
								 | TRAILING)? trim_list T_RIGHTBRACKET		  
          | NULLIF T_LEFTBRACKET a_expr T_COMMA a_expr T_RIGHTBRACKET
          | NVL T_LEFTBRACKET a_expr T_COMMA a_expr T_RIGHTBRACKET
          | COALESCE T_LEFTBRACKET expr_list T_RIGHTBRACKET
          | GREATEST T_LEFTBRACKET expr_list T_RIGHTBRACKET
          | LEAST T_LEFTBRACKET expr_list T_RIGHTBRACKET
          | XMLCONCAT T_LEFTBRACKET expr_list T_RIGHTBRACKET
          | XMLELEMENT T_LEFTBRACKET NAME_P colLabel ( T_COMMA ( xml_attributes
												                 | expr_list
													             | xml_attributes T_COMMA expr_list ) )? T_RIGHTBRACKET
          | XMLEXISTS T_LEFTBRACKET c_expr xmlexists_argument T_RIGHTBRACKET
          | XMLFOREST T_LEFTBRACKET xml_attribute_list T_RIGHTBRACKET
          | XMLPARSE T_LEFTBRACKET document_or_content a_expr xml_whitespace_option T_RIGHTBRACKET
          | XMLPI T_LEFTBRACKET NAME_P colLabel (T_COMMA a_expr)? T_RIGHTBRACKET
          | XMLROOT T_LEFTBRACKET a_expr T_COMMA xml_root_version opt_xml_root_standalone T_RIGHTBRACKET
          | XMLSERIALIZE T_LEFTBRACKET document_or_content a_expr AS simpleTypename T_RIGHTBRACKET
          ;

simpleTypename : genericType
               | numeric
               | bit
               | character
               | constDatetime
               | constInterval (T_LEFTBRACKET ICONST T_RIGHTBRACKET)? opt_interval
               ;

opt_interval : YEAR_P
             | MONTH_P
             | DAY_P
             | HOUR_P
             | MINUTE_P
             | interval_second
             | YEAR_P TO MONTH_P
             | DAY_P TO HOUR_P
             | DAY_P TO MINUTE_P
             | DAY_P TO interval_second
             | HOUR_P TO MINUTE_P
             | HOUR_P TO interval_second
             | MINUTE_P TO interval_second
             | YEAR_P T_LEFTBRACKET ICONST T_RIGHTBRACKET
             | MONTH_P T_LEFTBRACKET ICONST T_RIGHTBRACKET
             | DAY_P T_LEFTBRACKET ICONST T_RIGHTBRACKET
             | HOUR_P T_LEFTBRACKET ICONST T_RIGHTBRACKET
             | MINUTE_P T_LEFTBRACKET ICONST T_RIGHTBRACKET
             | YEAR_P T_LEFTBRACKET ICONST T_RIGHTBRACKET TO MONTH_P
             | DAY_P T_LEFTBRACKET ICONST T_RIGHTBRACKET TO HOUR_P
             | DAY_P T_LEFTBRACKET ICONST T_RIGHTBRACKET TO MINUTE_P
             | DAY_P T_LEFTBRACKET ICONST T_RIGHTBRACKET TO interval_second
             | HOUR_P T_LEFTBRACKET ICONST T_RIGHTBRACKET TO MINUTE_P
             | HOUR_P T_LEFTBRACKET ICONST T_RIGHTBRACKET TO interval_second
             | MINUTE_P T_LEFTBRACKET ICONST T_RIGHTBRACKET TO interval_second
             |
             ;

interval_second : SECOND_P (T_LEFTBRACKET ICONST T_RIGHTBRACKET)?
                ;

constInterval : INTERVAL;

character : CHARACTER opt_varying
          | CHAR_P opt_varying
          | NVARCHAR2
          | VARCHAR
          | VARCHAR2
          | NATIONAL CHARACTER opt_varying
          | NATIONAL CHAR_P opt_varying
          | NCHAR opt_varying
          ;

opt_varying : VARYING
            |
            ;

bit : bitWithLength
    | bitWithoutLength
    ;

numeric : INT_P
        | INTEGER opt_type_modifiers
        | SMALLINT
        | TINYINT
        | BIGINT
        | REAL
        | FLOAT_P opt_float
        | BINARY_DOUBLE
        | BINARY_INTEGER
        | DOUBLE_P PRECISION
        | DECIMAL_P opt_type_modifiers
        | NUMBER_P opt_type_modifiers
        | DEC opt_type_modifiers
        | NUMERIC opt_type_modifiers
        | BOOLEAN_P
        ;

opt_float : T_LEFTBRACKET ICONST T_RIGHTBRACKET
          |
          ;

opt_type_modifiers : T_LEFTBRACKET expr_list T_RIGHTBRACKET
                   |
                   ;

genericType : type_function_name attrs? opt_type_modifiers
            ;

attrs : T_DOT attr_name
      | attrs T_DOT attr_name
      ;

type_function_name : IDENT
                   | unreserved_keyword
                   | type_func_name_keyword
                   ;

xml_root_version : VERSION_P a_expr
                 | VERSION_P NO VALUE_P
                 ;

opt_xml_root_standalone : T_COMMA STANDALONE_P YES_P
                        | T_COMMA STANDALONE_P NO
                        | T_COMMA STANDALONE_P NO VALUE_P
                        |
                        ;

document_or_content : DOCUMENT_P
                    | CONTENT_P
                    ;

xml_whitespace_option : WHITESPACE_P
                      | STRIP_P WHITESPACE_P
                      |
                      ;

xml_attribute_list : xml_attribute_el
                   | xml_attribute_list T_COMMA xml_attribute_el
                   ;

xml_attribute_el : a_expr AS colLabel
                 | a_expr
                 ;

xmlexists_argument : PASSING c_expr
                   | PASSING c_expr BY REF
                   | PASSING BY REF c_expr
                   | PASSING BY REF c_expr BY REF
                   ;

xml_attributes : XMLATTRIBUTES T_LEFTBRACKET xml_attribute_list T_RIGHTBRACKET;

trim_list : a_expr FROM expr_list
          | FROM expr_list
          | expr_list
          ;

substr_list : a_expr ( substr_from substr_for?
                      | substr_for substr_from?)
            | expr_list
            |
            ;

substr_from : FROM a_expr;

substr_for : FOR a_expr;

position_list : b_expr IN_P b_expr
              |
              ;

overlay_list : a_expr overlay_placing substr_from substr_for?
             ;

overlay_placing : PLACING a_expr;

extract_list : extract_arg FROM a_expr
             |
             ;

extract_arg : IDENT
            | YEAR_P
            | MONTH_P
            | DAY_P
            | HOUR_P
            | MINUTE_P
            | SECOND_P
            | sconst
            ;

sort_clause : ORDER BY sortby_list;

sortby_list : sortby
            | sortby (T_COMMA sortby)* //rule simplified for select alias
            ;

sortby : a_expr ( USING qual_all_Op opt_nulls_order
                  | opt_asc_desc opt_nulls_order
	             )
       | NLSSORT T_LEFTBRACKET IDENT T_COMMA sconst T_RIGHTBRACKET opt_nulls_order
       ;

func_arg_expr : a_expr
              | param_name (T_COLON_EQUALS | T_PARA_EQUALS) a_expr
              ;

param_name : type_function_name;

func_arg_list : (func_arg_expr T_COMMA)* func_arg_expr
              ;

func_name : type_function_name
          | colId indirection
          ;

over_clause : OVER (window_specification | colId)
            |
            ;

opt_existing_window_name : colId
                         |
                         ;

opt_partition_clause : PARTITION BY expr_list
                     |
                     ;

opt_frame_clause : RANGE frame_extent
                 | ROWS frame_extent
                 |
                 ;

frame_extent :(BETWEEN frame_bound AND)? frame_bound
             ;

frame_bound : UNBOUNDED (PRECEDING | FOLLOWING)
            | CURRENT_P ROW
            | a_expr (PRECEDING | FOLLOWING)
            ;

opt_indirection : indirection_el+
                |
                ;

aexprConst : ICONST
           | FCONST
           | sconst
           | BCONST
           | XCONST
           | func_name (T_LEFTBRACKET func_arg_list T_RIGHTBRACKET)? sconst
           | constInterval (T_LEFTBRACKET ICONST T_RIGHTBRACKET)? sconst opt_interval?
           | TRUE_P
           | FALSE_P
           | NULL_P
           ;

constTypename : numeric
              | constBit
              | constCharacter
              | constDatetime
              ;

constBit : bitWithLength
         | bitWithoutLength
         ;

bitWithLength : BIT opt_varying T_LEFTBRACKET expr_list T_RIGHTBRACKET;

bitWithoutLength : BIT opt_varying;

constCharacter : characterWithLength
               | characterWithoutLength
               ;

characterWithLength : character T_LEFTBRACKET ICONST T_RIGHTBRACKET opt_charset;

characterWithoutLength : character opt_charset;

opt_charset : CHARACTER SET colId
            |
            ;

constDatetime : TIMESTAMP (T_LEFTBRACKET ICONST T_RIGHTBRACKET)? opt_timezone
              | TIME (T_LEFTBRACKET ICONST T_RIGHTBRACKET)? opt_timezone
              | DATE_P
              | SMALLDATETIME
              ;

opt_timezone : WITH_TIME ZONE
             | WITHOUT TIME ZONE
             |
             ;

columnref : colId indirection?
          ;

typename : SETOF? simpleTypename ( opt_array_bounds
                                  | ARRAY (T_LEFT_SQBRACKET ICONST T_RIGHT_SQBRACKET)?)
         ;

opt_array_bounds : opt_array_bounds ( T_LEFT_SQBRACKET ICONST? T_RIGHT_SQBRACKET
									  | T_LEFTBRACKET ICONST T_RIGHTBRACKET
				                     )
                 |
                 ;

any_name : colId attrs?
         ;

qual_all_Op : all_Op
            | OPERATOR T_LEFTBRACKET any_operator T_RIGHTBRACKET
            ;

opt_nulls_order : NULLS_FIRST
                | NULLS_LAST
                |
                ;

opt_asc_desc : ASC
             | DESC
             |
             ;

values_clause : valuesClauseSingle
              | valuesClauseMul;
			  
valuesClauseSingle : VALUES ctext_row;	

valuesClauseMul : VALUES (ctext_row T_COMMA)* ctext_row;	

			  

opt_all : ALL
        | DISTINCT
        |
        ;

into_clause : INTO optTempTableName
            |
            ;

optTempTableName : ( TEMPORARY 
                     | TEMP
                     | LOCAL TEMPORARY
                     | LOCAL TEMP
                     | GLOBAL TEMPORARY
                     | GLOBAL TEMP
                     | UNLOGGED ) opt_table qualified_name
                 | TABLE? qualified_name
                 ;

opt_table : TABLE
          |
          ;

window_clause : WINDOW window_definition_list
              |
              ;

window_definition_list : (window_definition T_COMMA)* window_definition
                       ;

window_definition : colId AS window_specification;

window_specification : T_LEFTBRACKET opt_existing_window_name opt_partition_clause opt_sort_clause opt_frame_clause T_RIGHTBRACKET;

sconst : SCONST;

unreserved_keyword : ABORT_P
            | ABSOLUTE_P
            | ACCESS
            | ACCOUNT
            | ACTION
            | ADD_P
            | ADMIN
            | AFTER
            | AGGREGATE
            | ALSO
            | ALTER
            | ALWAYS
            | APP
            | ASSERTION
            | ASSIGNMENT
            | AT
            | ATTRIBUTE
            | AUTOEXTEND
            | AUTOMAPPED
            | BACKWARD
            | BARRIER
            | BEFORE
            | BEGIN_NON_ANOYBLOCK
            | BEGIN_P
            | BLOB_P
            | BY
            | CACHE
            | CALL
            | CALLED
            | CASCADE
            | CASCADED
            | CATALOG_P
            | CHAIN
            | CHARACTERISTICS
            | CHECKPOINT
            | CLASS
            | CLEAN
            | CLOB
            | CLOSE
            | CLUSTER
            | COMMENT
            | COMMENTS
            | COMMIT
            | COMMITTED
            | COMPATIBLE_ILLEGAL_CHARS
            | COMPRESS
            | CONFIGURATION
            | CONNECTION
            | CONSTRAINTS
            | CONTENT_P
            | CONTINUE_P
            | CONVERSION_P
            | COORDINATOR
            | COPY
            | COST
            | CSV
            | CUBE
            | CURRENT_P
            | CURSOR
            | CYCLE
            | DATA_P
            | DATABASE
            | DATAFILE
            | DAY_P
            | DATE_FORMAT_P
            | DBCOMPATIBILITY_P
            | DEALLOCATE
            | DECLARE
            | DEFAULTS
            | DEFERRED
            | DEFINER
            | DELETE_P
            | DELIMITER
            | DELIMITERS
            | DELTA
            | DETERMINISTIC
            | DICTIONARY
            | DIRECT
            | DISABLE_P
            | DISCARD
            | DISTRIBUTE
            | DISTRIBUTION
            | DOCUMENT_P
            | DOMAIN_P
            | DOUBLE_P
            | DROP
            | EACH
            | ENABLE_P
            | ENCODING
            | ENCRYPTED
            | ENFORCED
            | ENUM_P
            | EOL
            | ESCAPE
            | ESCAPING
            | EXCHANGE
            | EXCLUDE
            | EXCLUDING
            | EXCLUSIVE
            | EXECUTE
            | EXPLAIN
            | EXTENSION
            | EXTERNAL
            | FAMILY
            | FILEHEADER_P
            | FIRST_P
            | FIXED_P
            | FOLLOWING
            | FORCE
            | FORMATTER
            | FORWARD
            | FUNCTIONS
            | GLOBAL
            | GRANTED
            | HANDLER
            | HEADER_P
            | HOLD
            | HOUR_P
            | IDENTIFIED
            | IDENTITY_P
            | IF_P
            | IGNORE_EXTRA_DATA
            | IMMEDIATE
            | IMMUTABLE
            | IMPLICIT_P
            | INCLUDING
            | INCREMENT
            | INDEX
            | INDEXES
            | INHERIT
            | INHERITS
            | INITIAL_P
            | INITRANS
            | INLINE_P
            | INPUT_P
            | INSENSITIVE
            | INSERT
            | INSTEAD
            | INVOKER
            | ISOLATION
            | KEY
            | LABEL
            | LANGUAGE
            | LARGE_P
            | LAST_P
            | LC_COLLATE_P
            | LC_CTYPE_P
            | LEAKPROOF
            | LEVEL
            | LISTEN
            | LOAD
            | LOCAL
            | LOCATION
            | LOCK_P
            | LOG_P
            | LOGGING
            | LOOP
            | MAPPING
            | MATCH
            | MAXEXTENTS
            | MAXSIZE
            | MAXTRANS
            | MERGE
            | MINEXTENTS
            | MINUTE_P
            | MINVALUE
            | MODE
            | MONTH_P
            | MOVE
            | MOVEMENT
            | NAME_P
            | NAMES
            | NEXT
            | NO
            | NOCOMPRESS
            | NOCYCLE
            | NODE
            | NOLOGGING
            | NOMAXVALUE
            | NOMINVALUE
            | NOTHING
            | NOTIFY
            | NOWAIT
            | NULLS_P
            | NUMSTR
            | OBJECT_P
            | OF
            | OFF
            | OIDS
            | OPERATOR
            | OPTIMIZATION
            | OPTION
            | OPTIONS
            | OWNED
            | OWNER
            | PARSER
            | PARTIAL
            | PARTITION
            | PARTITIONS
            | PASSING
            | PASSWORD
            | PCTFREE
            | PER_P
            | PERCENT
            | PERM
            | PLANS
            | POOL
            | PRECEDING
            | PREFERRED
            | PREFIX
            | PREPARE
            | PREPARED
            | PRESERVE
            | PRIOR
            | PRIVILEGE
            | PRIVILEGES
            | PROCEDURAL
            | PROFILE
            | QUERY
            | QUOTE
            | RANGE
            | RAW  T_LEFTBRACKET ICONST T_RIGHTBRACKET
            | RAW
            | READ
            | REASSIGN
            | REBUILD
            | RECHECK
            | RECURSIVE
            | REF
            | REINDEX
            | RELATIVE_P
            | RELEASE
            | RELOPTIONS
            | REMOTE_P
            | RENAME
            | REPEATABLE
            | REPLACE
            | REPLICA
            | RESET
            | RESIZE
            | RESOURCE
            | RESTART
            | RESTRICT
            | RETURNS
            | REUSE
            | REVOKE
            | ROLE
            | ROLLBACK
            | ROLLUP
            | ROWS
            | RULE
            | SAVEPOINT
            | SCHEMA
            | SCROLL
            | SEARCH
            | SECOND_P
            | SECURITY
            | SEQUENCE
            | SEQUENCES
            | SERIALIZABLE
            | SERVER
            | SESSION
            | SET
            | SETS
            | SHARE
            | SHOW
            | SIMPLE
            | SIZE
            | SMALLDATETIME_FORMAT_P
            | SNAPSHOT
            | SPACE
            | STABLE
            | STANDALONE_P
            | START
            | STATEMENT
            | STATISTICS
            | STDIN
            | STDOUT
            | STORAGE
            | STORE_P
            | STRICT_P
            | STRIP_P
            | SYS_REFCURSOR
            | SYSID
            | SYSTEM_P
            | TABLES
            | TABLESPACE
            | TEMP
            | TEMPLATE
            | TEMPORARY
            | TEXT_P
            | THAN
            | TIME_FORMAT_P
            | TIMESTAMP_FORMAT_P
            | TRANSACTION
            | TRIGGER
            | TRUNCATE
            | TRUSTED
            | TYPE_P
            | TYPES_P
            | UNBOUNDED
            | UNCOMMITTED
            | UNENCRYPTED
            | UNKNOWN
            | UNLIMITED
            | UNLISTEN
            | UNLOCK
            | UNLOGGED
            | UNTIL
            | UNUSABLE
            | UPDATE
            | VACUUM
            | VALID
            | VALIDATE
            | VALIDATION
            | VALIDATOR
            | VALUE_P
            | VARYING
            | VERSION_P
            | VIEW
            | VOLATILE
            | WHITESPACE_P
            | WITHOUT
            | WORK
            | WORKLOAD
            | WRAPPER
            | WRITE
            | XML_P
            | YEAR_P
            | YES_P
            | ZONE
             ;

col_name_keyword: BETWEEN
            | BIGINT
            | BINARY_DOUBLE
            | BINARY_INTEGER
            | BIT
            | BOOLEAN_P
            | CHAR_P
            | CHARACTER
            | COALESCE
            | DATE_P
            | DEC
            | DECIMAL_P
            | DECODE
            | EXISTS
            | EXTRACT
            | FLOAT_P
            | GREATEST
            | GROUPING_P
            | INOUT
            | INT_P
            | INTEGER
            | INTERVAL
            | LEAST
            | NATIONAL
            | NCHAR
            | NONE
            | NULLIF
            | NUMBER_P
            | NUMERIC
            | NVARCHAR2
            | NVL
            | OUT_P
            | OVERLAY
            | POSITION
            | PRECISION
            | REAL
            | ROW
            | SETOF
            | SMALLDATETIME
            | SMALLINT
            | SUBSTRING
            | TIME
            | TIMESTAMP
            | TINYINT
            | TREAT
            | TRIM
            | VALUES
            | VARCHAR
            | VARCHAR2
            | XMLATTRIBUTES
            | XMLCONCAT
            | XMLELEMENT
            | XMLEXISTS
            | XMLFOREST
            | XMLPARSE
            | XMLPI
            | XMLROOT
            | XMLSERIALIZE
            ;

any_stmt : colLabel+ ;
createFunction : CREATE (OR REPLACE)+ function_type function_name function_aguments? func_returns? function_language? function_properties_list? func_as_is? function_body? function_properties_list? function_end?;
func_as_is : (AS|IS);
function_type : (FUNCTION|PROCEDURE|package_type);
package_type : PACKAGE BODY?;
function_name : (schema_name) (funct_name);
function_aguments : T_LEFTBRACKET (function_agrs_list)* T_RIGHTBRACKET;
function_agrs_list :one_funtion_argument (T_COMMA one_funtion_argument)* ;
one_funtion_argument :  (func_arg_name)? (func_arg_mode)? typename (func_default_expr)?;
func_arg_name : colId;
func_arg_mode : (IN_P|OUT_P|INOUT_P);
func_default_expr : (DEFAULT | T_COLON_EQUALS | T_EQUALS) a_expr;
func_returns : RETURNS ( TABLE  returns_table_expr_list | typename (DETERMINISTIC)?);
returns_table_expr_list : returns_table_expr_item (T_COMMA returns_table_expr_item)*;
returns_table_expr_item : colId typename;
function_language : LANGUAGE IDENT  (WINDOW)?;
function_body : LESS_LESS (colId|':='|'+'|';')* GREATER_GREATER ;
function_properties_list : function_properties_item (T_COMMA function_properties_item)*;
function_end : END_P SEMICOLON T_DIVIDE;

function_properties_item : (IMMUTABLE | STABLE | VOLATILE)?
            |((NOT)? LEAKPROOF)?
            |(CALLED ON NULL INPUT | RETURNS NULL ON NULL INPUT | STRICT_P)?
            |((EXTERNAL)?  SECURITY (INVOKER|DEFINER))?
            |(COST IDENT)?
            |(ROWS IDENT)?
            |(SET colId (TO | T_EQUALS)? ctext_expr | FROM CURRENT_P)?
            |(AUTHID (DEFINER|CURRENT_USER))?
            | (FENCED);
/* pl_block : decl_sect BEGIN proc_sect exception_sect END opt_label;
 decl_sect : opt_block_label| opt_block_label decl_start | opt_block_label decl_start decl_stmts;
 opt_block_label :
            | LESS_LESS any_identifier GREATER_GREATER;
 any_identifier : pl_unreserved_keyword;

 decl_start : K_DECLARE;

 decl_stmts : decl_stmts decl_stmt | decl_stmt;
 decl_stmt :decl_statement
            |K_DECLARE
            |LESS_LESS any_identifier GREATER_GREATER
            ;
decl_statement : decl_varname decl_const decl_datatype decl_collate decl_notnull decl_defval
            |decl_varname K_ALIAS K_FOR decl_aliasitem T_SEMICOLON
            |decl_varname opt_scrollable K_CURSOR decl_cursor_args decl_is_for decl_cursor_query
            ;
decl_varname :colId
            ;
decl_const : K_CONSTANT
            |
            ;
decl_datatype : typename
                ;

 pl_unreserved_keyword :  K_ABSOLUTE
                        | K_ALIAS
                        | K_ARRAY
                        | K_ASSERT
                        | K_BACKWARD
                        | K_CLOSE
                        | K_COLLATE
                        | K_COLUMN
                        | K_COLUMN_NAME
                        | K_CONSTANT
                        | K_CONSTRAINT
                        | K_CONSTRAINT_NAME
                        | K_CONTINUE
                        | K_CURRENT
                        | K_CURSOR
                        | K_DATATYPE
                        | K_DEBUG
                        | K_DEFAULT
                        | K_DETAIL
                        | K_DIAGNOSTICS
                        | K_DUMP
                        | K_ELSIF
                        | K_ERRCODE
                        | K_ERROR
                        | K_EXCEPTION
                        | K_EXIT
                        | K_FETCH
                        | K_FIRST
                        | K_FORWARD
                        | K_GET
                        | K_HINT
                        | K_IMPORT
                        | K_INFO
                        | K_INSERT
                        | K_IS
                        | K_LAST
                        | K_LOG
                        | K_MESSAGE
                        | K_MESSAGE_TEXT
                        | K_MOVE
                        | K_NEXT
                        | K_NO
                        | K_NOTICE
                        | K_OPEN
                        | K_OPTION
                        | K_PERFORM
                        | K_PG_CONTEXT
                        | K_PG_DATATYPE_NAME
                        | K_PG_EXCEPTION_CONTEXT
                        | K_PG_EXCEPTION_DETAIL
                        | K_PG_EXCEPTION_HINT
                        | K_PRINT_STRICT_PARAMS
                        | K_PRIOR
                        | K_QUERY
                        | K_RAISE
                        | K_RELATIVE
                        | K_RESULT_OID
                        | K_RETURN
                        | K_RETURNED_SQLSTATE
                        | K_REVERSE
                        | K_ROW_COUNT
                        | K_ROWTYPE
                        | K_SCHEMA
                        | K_SCHEMA_NAME
                        | K_SCROLL
                        | K_SLICE
                        | K_SQLSTATE
                        | K_STACKED
                        | K_TABLE
                        | K_TABLE_NAME
                        | K_TYPE
                        | K_USE_COLUMN
                        | K_USE_VARIABLE
                        | K_VARIABLE_CONFLICT
                        | K_WARNING
                        ;*/

/* AS function_body  function_properties_list
                ;
function_aguments : T_LEFTBRACKET (function_agrs_list)* T_RIGHTBRACKET;
function_agrs_list :one_funtion_argument (T_COMMA one_funtion_argument)* ;
one_funtion_argument :  func_arg_name (func_arg_mode)? typename (func_default_expr)?;



*/