grammar RDR4Label;

NL: [\r\n];
WS: [ \t]+;
WORD: ~[\-0-9 \t\r\n]~[. \t\r\n]*;

label:
    pdsversion
    end
    EOF;

pdsversion : 'PDS_VERSION_ID' WS* '=' WS* WORD NL;

end : 'END' NL;
