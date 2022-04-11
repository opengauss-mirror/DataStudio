var newTokenState;
var tokenState;
var tokenReader;
var tokenList=[];
var tokenWord="";
var punctuatorList=["{", "}", "(", ")", "[", "]", ".", ";", ",", "<", ">", "<=",
                    ">=", "==", "!=", "===", "!==", "+", "-", "*", "%", "++", "--",
                    "<<", ">>", ">>>", "&", "|", "^", "!", "~", "&&", "||", "?", ":",
                    "=", "+=", "-=", "*=", "%=", "<<=", ">>=", ">>>=", "&=", "|=", "^="];

var operatorList=["<", ">", "<=",">=", "==", "!="];

function StringReader(statement)
{
	tokenReader=TempReader(statement);
	tokenList.length=0;
	var tokens=[];
	tokenState=dataState;
    while(!tokenReader.eof()){
        var tempC=tokenReader.getChar();
        newTokenState=tokenState(tempC);
        newTokenState && (tokenState=newTokenState);
        tokenReader.next();
    }
	$(tokenList).each(function(i,token){
		if(token[0]=='id')
		{
			tokens.push(token[1]);
		}
	});
	return tokens;
}

function TempReader(str)
{
	var index=0;
	var stream=str+" "; 
	var me={};
	me.getChar=function(){
	    return stream.charAt(index);
	    };
	me.length=function (){
			return stream.length;
		};
	me.stream=function(){
			return stream;
		};
	me.pchar=function(){
			return stream.charAt(index-1);
		};
	me.nchar=function(){
			return stream.charAt(index+1);
		};
	me.eof=function (){
			return index === stream.length;
		};
	me.next=function(){
	    index++;
	};
	me.prev=function (){
	    index--;
	};
	
	return me;
}
	
function checkUnicodeLetter(c){
	return c.match(/[a-z]/i);
}

function checkUnicodeNumber(c){
	return (c.charCodeAt() >= "\u0030".charCodeAt() && c.charCodeAt() <= "\u0039".charCodeAt())
		|| (c.charCodeAt() >= "\u1D7CE".charCodeAt() && c.charCodeAt() <= "\u1D7FF".charCodeAt());
		
}

function emitToken(type){
	tokenList.push([type, tokenWord]);   
	tokenWord="";
}

function arrIndexOf(arr,c)
{
    for(var i=0;i<arr.length;i++)
    {
      if(arr[i]===c)
      {
          return i;
      }
    }
    return -1;
}

function dataState(c){
	if(arrIndexOf(punctuatorList,c) > -1){
		tokenWord=c;
		return punctuatorState;
		
	}else if(checkUnicodeLetter(c) || c==="_" || c==="$" || c==="\\"){
		tokenWord=c;
		return identifierState;
		
	}else if(c==="\""){
		tokenWord=c;
		return doubleStringLiteralState;
	}
}

function punctuatorState(c){
	//if(punctuatorList.indexOf(tokenWord+c) === -1){
	if($.inArray(tokenWord+c,punctuatorList) === -1){
		if($.inArray(tokenWord,operatorList)>-1)
		{
			emitToken("id");
		}else{
			emitToken("pun");
		}
		tokenReader.prev();
		return dataState;
	}else{
		tokenWord += c;
	}
}

function identifierState(c){
	if(checkUnicodeLetter(c) || checkUnicodeNumber(c)){
		tokenWord += c;
		
	}else{
		emitToken("id");
		tokenReader.prev();
		return dataState;
	}
}

function doubleStringLiteralState(c){
	if(c==="\\"){
		tokenWord += c;
		return doubleStringLiteralEscapeSequenceState;
	}else if(c==="\""){
		tokenWord += c;
		emitToken("str");
		return dataState;
	}else{
		tokenWord += c;
	}
}

function doubleStringLiteralEscapeSequenceState(c){
	tokenWord+=c;
	return doubleStringLiteralState;
}

var assistantKeywordsPurple=unescape("ABORT ABSOLUTE ACCESS ACCOUNT ACTION ADD ADMIN AFTER AGGREGATE ALIAS ALSO ALTER ALWAYS APP ATTRIBUTE AUTOEXTEND AUTOMAPPED AVG BACKWARD BARRIER BEFORE BEGIN BLOB BY CACHE CALL CALLED CASCADED CASCADE CATALOG CHAIN CHARACTERISTICS CHECKPOINT CLASS CLEAN CLOB CLOSE CLUSTER COMMENT COMMENTS COMMIT COMMITTED COMPRESS CONFIGURATION CONNECTION CONSTRAINTS CONTENT CONTINUE CONVERSION COORDINATOR COPY COST CSV CURRENT CURSOR CYCLE DATABASE DATAFILE DATA DAY DEALLOCATE DECLARE DEFAULTS DEFERRED DEFINER DELETE DELIMITER DELIMITERS DELTA DETERMINISTIC DICTIONARY DIRECT DISABLE DISCARD DISTRIBUTE DISTRIBUTION DOCUMENT DOMAIN DOUBLE DROP EACH ENABLE ENCODING ENCRYPTED ENFORCED ENUM EOL ESCAPE ESCAPING EXCHANGE EXCLUDE EXCLUDING EXCLUSIVE EXECUTE EXPLAIN EXTENSION EXTERNAL FAMILY FILEHEADER FIRST FIXED FOLLOWING FORCE FORMATTER FORWARD FUNCTIONS GLOBAL GRANTED HANDLER HEADER HOLD HOUR IDENTIFIED IDENTITY IF IMMEDIATE IMMUTABLE IMPLICIT INCLUDING INCREMENT INDEXES INDEX INHERIT INHERITS INITIAL INITRANS INLINE INPUT INSENSITIVE INSERT INSTEAD INVOKER ISOLATION KEY LABEL LANGUAGE LARGE LAST LC_COLLATE LC_CTYPE LEAKPROOF LENGTH LEVEL LISTEN LOAD LOCAL LOCATION LOCK LOGGING LOGIN LOG LOOP MAPPING MATCH MAX MAXEXTENTS MAXSIZE MAXTRANS MAXVALUE MERGE MESSAGE_TEXT MIN MINEXTENTS MINUTE MINVALUE MODE MONTH MOVE MOVEMENT NAME NAMES NEXT NOCOMPRESS NOCYCLE NODE NOLOGGING NOLOGIN NOMAXVALUE NOMINVALUE NOTHING NOTIFY NOWAIT NO NULLS NUMSTR NVARCHAR2 OBJECT OFF OF OIDS OPERATOR OPTIMIZATION OPTIONS OPTION OWNED OWNER PARSER PARTIAL PARTITION PARTITIONS PASSING PASSWORD PCTFREE PER PERCENT PLANS POOL PRECEDING PREFERRED PREFIX PREPARE PREPARED PRESERVE PRIOR PRIVILEGES PRIVILEGE PROCEDURAL PROFILE QUERY QUOTE RANGE RAW READ REASSIGN REBUILD RECHECK RECURSIVE REF REINDEX RELATIVE RELEASE RELOPTIONS REMOTE RENAME REPEATABLE REPLACE REPLICA RESET RESIZE RESOURCE RESTART RESTRICT RETURNED_SQLSTATE RETURNS REUSE REVOKE ROLE ROLLBACK ROWS ROW_COUNT RULE SAVEPOINT SCHEMA SCROLL SEARCH SECOND SECURITY SEQUENCE SEQUENCES SERIALIZABLE SERVER SESSION SET SHARE SHOW SIMPLE SIZE SNAPSHOT SQLSTATE STABLE STANDALONE START STATEMENT STATISTICS STDIN STDOUT STORAGE STORE STRICT STRIP SUPERUSER SYS_REFCURSOR SYSID SYSTEM TABLESPACE TABLES TEMPLATE TEMPORARY TEMP TEXT THAN TRANSACTION TRUNCATE TRUSTED TYPE TYPES UNBOUNDED UNCOMMITTED UNENCRYPTED UNKNOWN UNLIMITED UNLISTEN UNLOCK UNLOGGED UNTIL UNUSABLE UPDATE VACUUM VALID VALIDATE VALIDATION VALIDATOR VALUE VERSION VIEW VOLATILE WHITESPACE WITHOUT WORK WORKLOAD WRAPPER WRITE XMLATTRIBUTES XMLCONCAT XMLELEMENT XMLEXISTS XMLFOREST XMLPARSE XMLPI XMLROOT XMLSERIALIZE XML YEAR YES ZONE".replace(/\+/g, ' ')).split(/\s+/);//#C60086
    
var assistantKeywordsBlue=unescape("BETWEEN BIGINT BINARY_DOUBLE BINARY_INTEGER BIT BOOLEAN CHAR CHARACTER COALESCE DECIMAL DECODE DEC EXTRACT EXISTS FLOAT8 FLOAT GREATEST INOUT INTERVAL INTEGER INT4 INT LEAST NATIONAL NCHAR NONE NULLIF NUMBER NUMERIC NVL OUT OVERLAY POSITION PRECISION REAL ROW SETOF SMALLDATETIME SMALLINT SUBSTRING TIMESTAMP TIME TINYINT TREAT TRIM VALUES VARCHAR2 VARCHAR".replace(/\+/g, ' ')).split(/\s+/);//#4166ED
    
    
var assistantKeywordsDeepGreen=unescape("ABS ADA ALLOCATE ALL ANALYSE ANALYZE AND ANY ARE ARRAY ASC ASENSITIVE ASYMMETRIC AS ATOMIC AUTHID AUTHORIZATION BINARY BITVAR BIT_LENGTH BOTH BUCKETS BREADTH CARDINALITY CASE CAST CATALOG_NAME CHARACTER_LENGTH CHARACTER_SET_CATALOG CHARACTER_SET_NAME CHARACTER_SET_SCHEMA CHAR_LENGTH CHECK CHECKED CLASS_ORIGIN COBOL COLLATE COLLATION COLLATION_CATALOG COLLATION_NAME COLLATION_SCHEMA COLUMN COLUMN_NAME COMMAND_FUNCTION COMMAND_FUNCTION_CODE COMPLETION CONCURRENTLY CONDITION CONDITION_NUMBER CONNECT CONNECTION_NAME CONSTRAINT CONSTRAINT_CATALOG CONSTRAINT_NAME CONSTRAINT_SCHEMA CONSTRUCTOR CONTAINS CONVERT CORRESPONDING COUNT CREATE CROSS CUBE CURRENT_CATALOG CURRENT_DATE CURRENT_PATH CURRENT_ROLE CURRENT_SCHEMA CURRENT_TIME CURRENT_TIMESTAMP CURRENT_USER CURSOR_NAME DATETIME_INTERVAL_CODE DATETIME_INTERVAL_PRECISION DATE DBCOMPATIBILITY DEFAULT DEFERRABLE DEFINED DEPTH DEREF DESC DESCRIBE DESCRIPTOR DESTROY DESTRUCTOR DIAGNOSTICS DISCONNECT DISPATCH DISTINCT DO DYNAMIC DYNAMIC_FUNCTION DYNAMIC_FUNCTION_CODE ELSE END END-EXEC EQUALS EVERY EXCEPT EXCEPTION EXEC EXISTING FALSE FETCH FINAL FOREIGN FORTRAN FOR FOUND FREEZE FREE FROM FULL FUNCTION GENERAL GENERATED GET GOTO GO GRANT GROUP GROUPING HAVING HIERARCHY HOST IGNORE ILIKE IMPLEMENTATION INDICATOR INFIX INITIALIZE INITIALLY INNER INSTANCE INSTANTIABLE INTERSECT INTO IN ISNULL IS ITERATE JOIN KEY_MEMBER KEY_TYPE LATERAL LEADING LEFT LESS LIKE LIMIT LOCALTIME LOCALTIMESTAMP LOCATOR LOWER MAP MATCHED MESSAGE_LENGTH MESSAGE_OCTET_LENGTH METHOD MINUS MOD MODIFIES MODIFY MODULE MORE MUMPS NATURAL NCLOB NEW NLSSORT NOT NOTNULL NULL NULLABLE OCTET_LENGTH OFFSET OLD ON ONLY OPEN OPERATION ORDER ORDINALITY OUTER OUTPUT OVER OVERLAPS OVERRIDING PAD PARAMETER PARAMETERS PARAMETER_MODE PARAMETER_NAME PARAMETER_ORDINAL_POSITION PARAMETER_SPECIFIC_CATALOG PARAMETER_SPECIFIC_NAME PARAMETER_SPECIFIC_SCHEMA PASCAL PATH PERFORMANCE PLACING PLI POSTFIX PREORDER PRIMARY PROCEDURE PUBLIC READS REFERENCES REFERENCING REJECT RESULT RETURN RETURNED_LENGTH RETURNED_OCTET_LENGTH RETURNING RIGHT ROLLUP ROUTINE ROUTINE_CATALOG ROUTINE_NAME ROUTINE_SCHEMA SCALE SCHEMA_NAME SCOPE SECTION SELECT SELF SENSITIVE SERVER_NAME SESSION_USER SETS SIMILAR SOME SOURCE SPACE SPECIFIC SPECIFICTYPE SPECIFIC_NAME SPLIT SQL SQLCODE SQLERROR SQLEXCEPTION SQLWARNING STATE STATIC STRUCTURE SUBCLASS_ORIGIN SUBLIST SUM SYMMETRIC SYSDATE SYSTEM_USER TABLE_NAME TABLE TERMINATE THEN TIMEZONE_HOUR TIMEZONE_MINUTE TO TRAILING TRANSACTIONS_COMMITTED TRANSACTIONS_ROLLED_BACK TRANSACTION_ACTIVE TRANSFORM TRANSFORMS TRANSLATE TRANSLATION TRIGGER TRIGGER_CATALOG TRIGGER_NAME TRIGGER_SCHEMA TRUE UESCAPE UNDER UNION UNIQUE UNNAMED UNNEST UPPER USAGE USER USER_DEFINED_TYPE_CATALOG USER_DEFINED_TYPE_NAME USER_DEFINED_TYPE_SCHEMA USING VARIABLE VARIADIC VARYING VERBOSE WHEN WHENEVER WHERE WINDOW WITH".replace(/\+/g, ' ')).split(/\s+/);//#237E18
    
function assistantHighlight(idVal) {
  var pucl = document.getElementById(idVal);
  if (pucl == null) return;
  var temp = pucl.innerHTML;
  
  var htmlReg = new RegExp("\<.*?\>", "i");
  var arrA = new Array();
  for (var i = 0; true; i++) {
      var m = htmlReg.exec(temp);
      if (m) {
          arrA[i] = m;
      }
      else {
          break;
      }
      temp = temp.replace(m, "{[(" + i + ")]}");
  }
  var temps=temp.split("\n");
  var realStr="";
  for (var i = 0; i < temps.length; i++) {
      var tempStr=" "+temps[i]+" ";
      if($.trim(tempStr)=="")
      {
        continue;
      }
      if(tempStr.indexOf(' --')==0)
      {
        realStr=realStr+"<span style='color:#408080;'>"+tempStr+"</span>\n";
        continue;
      }
      for (var w = 0; w < assistantKeywordsPurple.length; w++) {
          var r = new RegExp(" (" + assistantKeywordsPurple[w] + ") ", "ig");
          tempStr = tempStr.replace(r, " <b style='color:#C60086;'>$1</b> ");
      }
      for (var w = 0; w < assistantKeywordsBlue.length; w++) {
          var r = new RegExp(" (" + assistantKeywordsBlue[w] + ") ", "ig");
          tempStr= tempStr.replace(r, " <b style='color:#4166ED;'>$1</b> ");
      }
      for (var w = 0; w < assistantKeywordsDeepGreen.length; w++) {
          var r = new RegExp(" (" + assistantKeywordsDeepGreen[w] + ") ", "ig");
          tempStr = tempStr.replace(r, " <b style='color:#237E18;'>$1</b> ");
      }
      realStr=realStr+tempStr+"\n";
  }
  
  for (var i = 0; i < arrA.length; i++) {
      realStr = realStr.replace("{[(" + i + ")]}", arrA[i]);
  }
  
  temps=realStr.split("\n");
  
  realStr="";
  for (var i = 0; i < temps.length; i++) {
      var tempStr=temps[i];      
      if($.trim(tempStr)=="")
      {
        continue;
      }
      if(tempStr.indexOf('--')==0)
      {
        realStr=realStr+tempStr+"\r\n";
        continue;
      }
      for (var w = 0; w < assistantKeywordsPurple.length; w++) {
          if(tempStr.toLowerCase()==assistantKeywordsPurple[w].toLowerCase())
          {
            tempStr = "<b style='color:#C60086;'>"+assistantKeywordsPurple[w]+"</b>"+tempStr.substr(assistantKeywordsPurple[w].length);
            break;
          }
      }
      for (var w = 0; w < assistantKeywordsBlue.length; w++) {
          if(tempStr.toLowerCase()==assistantKeywordsBlue[w].toLowerCase())
          {
            tempStr = "<b style='color:#4166ED;'>"+assistantKeywordsBlue[w]+"</b>"+tempStr.substr(assistantKeywordsBlue[w].length);
            break;
          }
      }
      for (var w = 0; w < assistantKeywordsDeepGreen.length; w++) {
          if(tempStr.toLowerCase()==assistantKeywordsDeepGreen[w].toLowerCase())
          {
            tempStr = "<b style='color:#237E18;'>"+assistantKeywordsDeepGreen[w]+"</b>"+tempStr.substr(assistantKeywordsDeepGreen[w].length);
            break;
          }
      }
      realStr=realStr+tempStr+"\r\n";
  }
  pucl.innerHTML = "<pre>"+realStr.replace(/ , /g,",").replace(/' /g,"'").replace(/ ';/g,"';").replace(/ ; /g,";")+"</pre>";
}