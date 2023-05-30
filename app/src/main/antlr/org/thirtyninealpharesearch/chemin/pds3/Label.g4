grammar Label;

NL: [\r\n];
WS: [ \t]+;
WORD: ~[,.\-0-9 \t\r\n("^]~[. \-0-9\t\r\n("]*;
EQUALS: [ \t]* '=' [ \t]*;
VERSION: 'V' [0-9]+ ('.' [0-9])*;

INUMBER : INTEGER;

fragment INTEGER : '0'..'9'+;
fragment FLOAT   : '0'..'9'+ '.' '0'..'9'*;

label: labelparts end EOF;

labelparts : labelpart*;

labelpart : pdsVersionId
          | labelRevisionNote
          | recordType
          | recordBytes
          | fileRecords
          | objectLink
          | dataSetId
          | productId
          | productVersionId
          | releaseId
          | sourceProductId
          | productType
          | instrumentHostId
          | instrumentHostName
          | instrumentId
          | targetName
          | mslCalibrationStandardName
          | missionPhaseName
          | productCreationTime
          | startTime
          | stopTime
          | spacecraftClockStartCount
          | spacecraftClockStopCount
          | object;

pdsVersionId : 'PDS_VERSION_ID' EQUALS word WS* NL+;

labelRevisionNote : 'LABEL_REVISION_NOTE' EQUALS quoted WS* NL+;

recordType : 'RECORD_TYPE' EQUALS word WS* NL+;

recordBytes : 'RECORD_BYTES' EQUALS INUMBER WS* NL+;

fileRecords : 'FILE_RECORDS' EQUALS INUMBER WS* NL+;

objectLink : '^' WORD EQUALS fileTuple WS* NL+;

dataSetId : 'DATA_SET_ID' EQUALS '"' hyphenatedWord '"' WS* NL+;

productId : 'PRODUCT_ID' EQUALS '"' WS* word WS* '"' WS* NL+;

productVersionId : 'PRODUCT_VERSION_ID' EQUALS '"' VERSION '"' WS* NL+;

releaseId : 'RELEASE_ID' EQUALS '"' INUMBER '"' WS* NL+;

sourceProductId : 'SOURCE_PRODUCT_ID' EQUALS identifierList WS* NL+;

productType : 'PRODUCT_TYPE' EQUALS quoted WS* NL+;

instrumentHostId : 'INSTRUMENT_HOST_ID' EQUALS '"' WORD '"' WS* NL+;

instrumentHostName : 'INSTRUMENT_HOST_NAME' EQUALS '"' words '"' WS* NL+;

instrumentId : 'INSTRUMENT_ID' EQUALS '"' WORD '"' WS* NL+;

targetName : 'TARGET_NAME' EQUALS '"' WORD '"' WS* NL+;

mslCalibrationStandardName: 'MSL:CALIBRATION_STANDARD_NAME' EQUALS '"' words '"' WS* NL+;

missionPhaseName : 'MISSION_PHASE_NAME' EQUALS '"' words '"' WS* NL+;

productCreationTime : 'PRODUCT_CREATION_TIME' EQUALS utcDate WS* NL+;

startTime : 'START_TIME' EQUALS utcDate WS* NL+;

stopTime : 'STOP_TIME' EQUALS optionalUTCDate WS* NL+;

spacecraftClockStartCount : 'SPACECRAFT_CLOCK_START_COUNT' EQUALS '"' clockCount '"' WS* NL+;

spacecraftClockStopCount : 'SPACECRAFT_CLOCK_STOP_COUNT' EQUALS optionalClockCount WS* NL+;

object :
    objectHeader
    objectProperties
    objectEnd;

objectHeader : 'OBJECT' EQUALS WORD WS* NL+;

objectEnd : 'END_OBJECT' EQUALS WORD WS* NL+;

objectProperties : objectProperty
                 | objectProperty objectProperties;

objectProperty : objectBytes
               | objectRows
               | objectRowBytes
               | objectFields
               | objectFieldDelimiter
               | objectStructure
               | objectHeaderType
               | objectDescription;

objectBytes : WS* 'BYTES' EQUALS INUMBER WS* NL+;

objectRows : WS* 'ROWS' EQUALS INUMBER WS* NL+;

objectRowBytes : WS* 'ROW_BYTES' EQUALS INUMBER WS* NL+;

objectFields : WS* 'FIELDS' EQUALS INUMBER WS* NL+;

objectFieldDelimiter : WS* 'FIELD_DELIMITER' EQUALS '"' WORD '"' WS* NL+;

objectHeaderType : WS* 'HEADER_TYPE' EQUALS word WS* NL+;

objectDescription : WS* 'DESCRIPTION' EQUALS quoted WS* NL+;

objectStructure : WS* '^STRUCTURE' EQUALS '"' filename '"' WS* NL+;

fileTuple : '(' WS* '"' filename '"' WS* ',' INUMBER ')';

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

identifierList : '{' identifierEntries '}'
               | identifierEntry;

identifierEntries : WS* identifierEntry WS*
                  | WS* identifierEntry WS* ',' WS* NL* identifierEntries;

identifierEntry : '"' WS* word WS* '"';

optionalUTCDate : 'UNK'
                | '"UNK"'
                | utcDate;

utcDate : INUMBER '-' INUMBER '-' INUMBER 'T' INUMBER ':' INUMBER ':' INUMBER ('.' INUMBER)?;

optionalClockCount : 'UNK'
                   | '"UNK"'
                   | '"' clockCount '"';

clockCount : INUMBER ('.' INUMBER)?;

end : 'END' (WS* NL+)+;
