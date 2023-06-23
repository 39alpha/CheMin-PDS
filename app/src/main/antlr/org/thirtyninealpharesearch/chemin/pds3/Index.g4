grammar Index;

NL: [\r\n];
WS: [ \t]+;
WORD: ~[,.\-0-9 \t\r\n"^]~[. \-0-9\t\r\n"]*;
EQUALS: [ \t]* '=' [ \t]*;
VERSION: 'V' [0-9]+ ('.' [0-9])*;

INUMBER : INTEGER;

fragment INTEGER : '0'..'9'+;
fragment FLOAT   : '0'..'9'+ '.' '0'..'9'*;

index: indexparts end EOF;

indexparts : indexpart*;

indexpart : pdsVersionId
          | recordType
          | recordBytes
          | fileRecords
          | indexTable
          | volumeId
          | dataSetId
          | missionName
          | instrumentHostName
          | instrumentHostId
          | instrumentName
          | instrumentId
          | targetName
          | table;

pdsVersionId : 'PDS_VERSION_ID' EQUALS word WS* NL+;

recordType : 'RECORD_TYPE' EQUALS word WS* NL+;

recordBytes : 'RECORD_BYTES' EQUALS INUMBER WS* NL+;

fileRecords : 'FILE_RECORDS' EQUALS INUMBER WS* NL+;

indexTable : '^INDEX_TABLE' EQUALS quoted WS* NL+;

volumeId : 'VOLUME_ID' EQUALS quoted WS* NL+;

dataSetId : 'DATA_SET_ID' EQUALS identifierList WS* NL+;

missionName : 'MISSION_NAME' EQUALS quoted WS* NL+;

instrumentHostName : 'INSTRUMENT_HOST_NAME' EQUALS quoted WS* NL+;

instrumentHostId : 'INSTRUMENT_HOST_ID' EQUALS quoted WS* NL+;

instrumentName : 'INSTRUMENT_NAME' EQUALS quoted WS* NL+;

instrumentId : 'INSTRUMENT_ID' EQUALS quoted WS* NL+;

targetName : 'TARGET_NAME' EQUALS quoted WS* NL+;

table :
    tableHeader
    tableProperties
    columns
    tableEnd;

tableHeader : 'OBJECT' EQUALS 'INDEX_TABLE' WS* NL+;

tableEnd : 'END_OBJECT' EQUALS 'INDEX_TABLE' WS* NL+;

tableProperties : tableProperty
                | tableProperty tableProperties;

tableProperty : tableInterchangeFormat
              | tableRows
              | tableRowBytes
              | tableColumns
              | tableIndexType;

tableInterchangeFormat : WS* 'INTERCHANGE_FORMAT' EQUALS word WS* NL+;

tableRows : WS* 'ROWS' EQUALS INUMBER WS* NL+;

tableColumns : WS* 'COLUMNS' EQUALS INUMBER WS* NL+;

tableRowBytes : WS* 'ROW_BYTES' EQUALS INUMBER WS* NL+;

tableIndexType : WS* 'INDEX_TYPE' EQUALS word WS* NL+;

columns : column
        | column columns;

column :
    columnHeader
    columnProperties
    columnEnd;

columnHeader : WS* 'OBJECT' EQUALS 'COLUMN' WS* NL+;

columnEnd : WS* 'END_OBJECT' EQUALS 'COLUMN' WS* NL+;

columnProperties : columnProperty
                 | columnProperty columnProperties;

columnProperty : columnNumber
               | columnName
               | columnDataType
               | columnStartByte
               | columnBytes
               | columnUnknownConstant
               | columnDescription;

columnNumber : WS* 'COLUMN_NUMBER' EQUALS INUMBER WS* NL+;

// This is a terrible, horrible hack...
columnName : WS* 'NAME' EQUALS columnNameHack WS* NL+;

columnNameHack : ~(WS|NL)+;

columnDataType : WS* 'DATA_TYPE' EQUALS word WS* NL+;

columnStartByte : WS* 'START_BYTE' EQUALS INUMBER WS* NL+;

columnBytes : WS* 'BYTES' EQUALS INUMBER WS* NL+;

// This is a terrible, horrible hack...
columnUnknownConstant : WS* 'UNKNOWN_CONSTANT' EQUALS notws WS* NL+;

columnDescription : WS* 'DESCRIPTION' EQUALS quoted WS* NL+;

notws : ~WS+;

filename : WS* word '.' word WS*;

quoted : '"' unquoted '"';

unquoted : ~'"'+;

hyphenatedWord : WORD
               | INUMBER
               | VERSION
               | WORD '-' hyphenatedWord
               | INUMBER '-' hyphenatedWord
               | VERSION '-' hyphenatedWord;

word : WORD
     | WORD word
     | WORD INUMBER word*;

words : word
      | word WS+ words;

identifierList : '{' NL* identifierEntries NL* '}'
               | identifierEntry;

identifierEntries : WS* identifierEntry WS*
                  | WS* identifierEntry WS* ',' WS* NL* identifierEntries;

identifierEntry : '"' WS* hyphenatedWord WS* '"';

end : 'END' (WS* NL+)+;
