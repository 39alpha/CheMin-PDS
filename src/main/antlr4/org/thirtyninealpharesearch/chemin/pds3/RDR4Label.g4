grammar RDR4Label;

NL: [\r\n];
WS: [ \t]+;
WORD: ~[,.\-0-9 \t\r\n("^]~[. \-0-9\t\r\n("]*;
EQUALS: [ \t]* '=' [ \t]*;
VERSION: 'V' [0-9]+ ('.' [0-9])*;

INUMBER : INTEGER;

fragment INTEGER : '0'..'9'+;
fragment FLOAT   : '0'..'9'+ '.' '0'..'9'*;

label:
    pdsVersionId
    recordType
    recordBytes
    fileRecords
    objectLink*
    datasetId
    productId
    productVersionId
    releaseId
    sourceProductId
    productType
    instrumentHostId
    instrumentHostName
    instrumentId
    targetName
    mslCalibrationStandardName
    missionPhaseName
    productCreationTime
    startTime
    stopTime
    spacecraftClockStartCount
    spacecraftClockStopCount
    object*
    end
    EOF;

pdsVersionId : 'PDS_VERSION_ID' EQUALS word WS* NL+;

recordType : 'RECORD_TYPE' EQUALS word WS* NL+;

recordBytes : 'RECORD_BYTES' EQUALS INUMBER WS* NL+;

fileRecords : 'FILE_RECORDS' EQUALS INUMBER WS* NL+;

objectLink : '^' WORD EQUALS fileTuple WS* NL+;

datasetId : 'DATA_SET_ID' EQUALS '"' hyphenatedWord '"' WS* NL+;

productId : 'PRODUCT_ID' EQUALS '"' word '"' WS* NL+;

productVersionId : 'PRODUCT_VERSION_ID' EQUALS '"' VERSION '"' WS* NL+;

releaseId : 'RELEASE_ID' EQUALS '"' INUMBER '"' WS* NL+;

sourceProductId : 'SOURCE_PRODUCT_ID' EQUALS identifierList WS* NL+;

productType : 'PRODUCT_TYPE' EQUALS '"' WORD '"' WS* NL+;

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

filename : word '.' word;

quoted : '"' notquoted '"';

notquoted : ~'"'+;

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

identifierList : '{' identifierEntries '}';

identifierEntries : WS* identifierEntry WS*
                  | WS* identifierEntry WS* ',' NL* identifierEntries;

identifierEntry : '"' word '"';

optionalUTCDate : '"UNK"'
                | utcDate;

utcDate : INUMBER '-' INUMBER '-' INUMBER 'T' INUMBER ':' INUMBER ':' INUMBER ('.' INUMBER)?;

optionalClockCount : '"UNK"'
                   | '"' clockCount '"';

clockCount : INUMBER ('.' INUMBER)?;

end : 'END' NL+;
