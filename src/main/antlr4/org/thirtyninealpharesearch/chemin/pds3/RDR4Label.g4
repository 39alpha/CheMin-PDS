grammar RDR4Label;

NL: [\r\n];
WS: [ \t]+;
WORD: ~[,.\-0-9 \t\r\n("]~[. \-0-9\t\r\n("]*;
EQUALS: [ \t]* '=' [ \t]*;
VERSION: 'V' [0-9]+ ('.' [0-9])*;

INUMBER : INTEGER;

fragment INTEGER : '0'..'9'+;
fragment FLOAT   : '0'..'9'+ '.' '0'..'9'*;

label:
    pdsversion
    recordtype
    recordbytes
    filerecords
    header
    spreadsheet
    datasetid
    productid
    productversionid
    releaseid
    sourceproductid
    producttype
    instrumenthostid
    instrumenthostname
    instrumentid
    targetname
    mslcalibrationstandardname
    missionphasename
    productcreationtime
    starttime
    stoptime
    spacecraftclockstarttime
    spacecraftclockstoptime
    object*
    end
    EOF;

pdsversion : 'PDS_VERSION_ID' EQUALS word WS* NL+;

recordtype : 'RECORD_TYPE' EQUALS word WS* NL+;

recordbytes : 'RECORD_BYTES' EQUALS INUMBER WS* NL+;

filerecords : 'FILE_RECORDS' EQUALS INUMBER WS* NL+;

header : '^HEADER' EQUALS filetuple WS* NL+;

spreadsheet : '^SPREADSHEET' EQUALS filetuple WS* NL+;

datasetid : 'DATA_SET_ID' EQUALS '"' hyphenatedword '"' WS* NL+;

productid : 'PRODUCT_ID' EQUALS '"' word '"' WS* NL+;

productversionid : 'PRODUCT_VERSION_ID' EQUALS '"' VERSION '"' WS* NL+;

releaseid : 'RELEASE_ID' EQUALS '"' INUMBER '"' WS* NL+;

sourceproductid : 'SOURCE_PRODUCT_ID' EQUALS identifierlist WS* NL+;

producttype : 'PRODUCT_TYPE' EQUALS '"' WORD '"' WS* NL+;

instrumenthostid : 'INSTRUMENT_HOST_ID' EQUALS '"' WORD '"' WS* NL+;

instrumenthostname : 'INSTRUMENT_HOST_NAME' EQUALS '"' words '"' WS* NL+;

instrumentid : 'INSTRUMENT_ID' EQUALS '"' WORD '"' WS* NL+;

targetname : 'TARGET_NAME' EQUALS '"' WORD '"' WS* NL+;

mslcalibrationstandardname: 'MSL:CALIBRATION_STANDARD_NAME' EQUALS '"' words '"' WS* NL+;

missionphasename : 'MISSION_PHASE_NAME' EQUALS '"' words '"' WS* NL+;

productcreationtime : 'PRODUCT_CREATION_TIME' EQUALS utcdate WS* NL+;

starttime : 'START_TIME' EQUALS utcdate WS* NL+;

stoptime : 'STOP_TIME' EQUALS optionalutcdate WS* NL+;

spacecraftclockstarttime : 'SPACECRAFT_CLOCK_START_COUNT' EQUALS '"' clockcount '"' WS* NL+;

spacecraftclockstoptime : 'SPACECRAFT_CLOCK_STOP_COUNT' EQUALS optionalclockcount WS* NL+;

object :
    objectheader
    objectfields
    objectend;

objectheader : 'OBJECT' EQUALS WORD WS* NL+;

objectend : 'END_OBJECT' EQUALS WORD WS* NL+;

objectfields : objectfield
             | objectfield objectfields;

objectfield : objectbytes
            | objectrows
            | objectrowbytes
            | objectfieldcount
            | objectfielddelimiter
            | objectstructure
            | objectheadertype
            | objectdescription;

objectbytes : WS* 'BYTES' EQUALS INUMBER WS* NL+;

objectrows : WS* 'ROWS' EQUALS INUMBER WS* NL+;

objectrowbytes : WS* 'ROW_BYTES' EQUALS INUMBER WS* NL+;

objectfieldcount : WS* 'FIELDS' EQUALS INUMBER WS* NL+;

objectfielddelimiter : WS* 'FIELD_DELIMITER' EQUALS '"' WORD '"' WS* NL+;

objectheadertype : WS* 'HEADER_TYPE' EQUALS word WS* NL+;

objectdescription : WS* 'DESCRIPTION' EQUALS quoted WS* NL+;

objectstructure : WS* '^STRUCTURE' EQUALS '"' filename '"' WS* NL+;

filetuple : '(' WS* '"' filename '"' WS* ',' INUMBER ')';

filename : word '.' word;

quoted : '"' ~'"'+ '"';

hyphenatedword : WORD
               | INUMBER
               | VERSION
               | WORD '-' hyphenatedword
               | INUMBER '-' hyphenatedword
               | VERSION '-' hyphenatedword;

word : WORD
     | WORD word
     | WORD INUMBER word*;

words : word
      | word WS+ words;

identifierlist : '{' identifierentries '}';

identifierentries : WS* identifierentry WS*
                  | WS* identifierentry WS* ',' NL* identifierentries;

identifierentry : '"' word '"';

optionalutcdate : '"UNK"'
                | utcdate;

utcdate : INUMBER '-' INUMBER '-' INUMBER 'T' INUMBER ':' INUMBER ':' INUMBER ('.' INUMBER)?;

optionalclockcount : '"UNK"'
                   | '"' clockcount '"';

clockcount : INUMBER ('.' INUMBER)?;

end : 'END' NL+;
