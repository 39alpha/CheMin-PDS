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
    objectEnd;

objectHeader : WS* 'OBJECT' EQUALS field WS* NL+;

objectName : WS* 'NAME' EQUALS quoted WS* NL+;

objectDataType : WS* 'DATA_TYPE' EQUALS type WS* NL+;

objectUnit : WS* 'UNIT' EQUALS quoted WS* NL+;

objectBytes : WS* 'BYTES' EQUALS INUMBER WS* NL+;

objectFormat : WS* 'FORMAT' EQUALS quoted WS* NL+;

objectEnd : WS* 'END_OBJECT' EQUALS field WS* NL+;

field : 'FIELD';

quoted : '"' unquoted '"';

unquoted : ~'"'*;

type : 'ASCII_REAL';
