grammar Structure;

NL : [\r\n];
WS : [ \t]+;
POINT : '.';
HYPHEN : '-';
WORD : ~[,.\-0-9 \t\r\n("^]~[. \-0-9\t\r\n("]*;
EQUALS : [ \t]* '=' [ \t]*;

INUMBER : INTEGER;

fragment INTEGER : '0'..'9'+;

structure : objects EOF;

objects : object+;

object :
    objectHeader
    objectName
    objectDataType
    objectUnit
    objectBytes
    objectFormat
    objectDescription?
    objectEnd;

objectHeader : WS* 'OBJECT' EQUALS field WS* NL+;

objectName : WS* 'NAME' EQUALS quoted WS* NL+;

objectDataType : WS* 'DATA_TYPE' EQUALS type WS* NL+;

objectUnit : WS* 'UNIT' EQUALS quoted WS* NL+;

objectBytes : WS* 'BYTES' EQUALS INUMBER WS* NL+;

objectFormat : WS* 'FORMAT' EQUALS quoted WS* NL+;

objectDescription : WS* 'DESCRIPTION' EQUALS quoted WS* NL+;

objectEnd : WS* 'END_OBJECT' EQUALS field WS* NL+;

field : 'FIELD';

quoted : '"' unquoted '"';

unquoted : ~'"'*;

type : 'ASCII_ANYURI'
     | 'ASCII_BIBCODE'
     | 'ASCII_BOOLEAN'
     | 'ASCII_DOI'
     | 'ASCII_DATE_DOY'
     | 'ASCII_DATE_TIME_DOY'
     | 'ASCII_DATE_TIME_DOY_UTC'
     | 'ASCII_DATE_TIME_YMD'
     | 'ASCII_DATE_TIME_YMD_UTC'
     | 'ASCII_DATE_YMD'
     | 'ASCII_DIRECTORY_PATH_NAME'
     | 'ASCII_FILE_NAME'
     | 'ASCII_FILE_SPECIFICATION_NAME'
     | 'ASCII_INTEGER'
     | 'ASCII_LID'
     | 'ASCII_LIDVID'
     | 'ASCII_LIDVID_LID'
     | 'ASCII_MD5_CHECKSUM'
     | 'ASCII_NONNEGATIVE_INTEGER'
     | 'ASCII_NUMERIC_BASE16'
     | 'ASCII_NUMERIC_BASE2'
     | 'ASCII_NUMERIC_BASE8'
     | 'ASCII_REAL'
     | 'ASCII_STRING'
     | 'ASCII_TIME'
     | 'ASCII_VID'
     | 'UTF8_STRING';
