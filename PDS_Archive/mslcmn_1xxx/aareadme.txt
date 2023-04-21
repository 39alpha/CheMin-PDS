PDS_VERSION_ID          = PDS3
RECORD_TYPE             = STREAM
OBJECT                  = TEXT
  PUBLICATION_DATE      = 2013-02-13
  NOTE                  = "N/A"
END_OBJECT              = TEXT

END

                              AAREADME.TXT
                             FEBRUARY 27, 2013

==============================================================================
INTRODUCTION
==============================================================================

This archive contains Reduced Data Record (RDR) instrument data acquired by 
the Chemistry and Mineralogy (CheMin) instrument on the Mars Science 
Laboratory rover.

==============================================================================
VOLUME SET INFORMATION
==============================================================================

CheMin RDR data are stored in an archive volume having the directory structure 
illustrated below.  Raw CheMin data products (EDRs) are stored in a separate
archive volume not described here.

   root
    |
    |- AAREADME.TXT                Introduction to the archive (this file)
    |- ERRATA.TXT                  Release notes and errata for the archive
    |- VOLDESC.CAT                 Description of the contents of the archive 
    |
    |- CALIB
    |     |
    |     |- CALINFO.TXT           Description of the CALIBRATION directory
    |     |- CALREPORT.PDF,.TXT    CheMin calibration report
    |     |- CALREPORT.LBL         PDS label describing CALREPORT.*
    |
    |- CATALOG
    |     |
    |     |- CATINFO.TXT           Description of the CATALOG directory
    |     |- CHEMIN_INST.CAT       Instrument description
    |     |- CHEMIN_L1_RDRDS.CAT   Level 1 data set description
    |     |- CHEMIN_L2_RDRDS.CAT   Level 2 data set description
    |     |- CHEMIN_REF.CAT        References mentioned in CheMin*.CAT files
    |     |- MSL_INSTHOST.CAT      Instrument host (rover) description 
    |     |- MSL_MISSION.CAT       Mission description
    |     |- MSL_REF.CAT           References mentioned in MSL*.CAT files
    |     |- PERSON.CAT            Relevant CheMin personnel
    |
    |- DATA
    |     |
    |     |- RDR4                  Directory of Level 1 data, containing XRD and energy data
    |     |    |                   
    |     |    |- *.CSV            CheMin data products
    |     |    |- *.LBL            PDS labels describing data products
    |     |
    |     |- RDR5                  Directory of Level 2 mineral identification and abundance data
    |          |                   
    |          |- *.CSV            CheMin data products
    |          |- *.LBL            PDS labels describing data products
    |      
    |
    |- DOCUMENT
    |     |
    |     |- DOCINFO.TXT                Description of the DOCUMENT directory
    |     |- CHEMIN_BASICS.PDF,.HTM     CheMin basic calibration information
    |     |- CHEMIN_BASICS.LBL          PDS label for CHEMIN_BASICS.*
    |     |- CHEMIN_RDRSIS.PDF,.HTM     CheMin RDR data product specification
    |     |- CHEMIN_RDRSIS.LBL          PDS label for CHEMIN_RDRSIS.*
    |     |- CHEMIN_RDR_AVSIS_PDF,.HTM  RDR Archive SIS 
    |     |- CHEMIN_RDR_AVSIS.LBL       PDS label for CHEMIN_RDR_AVSIS.*
    |     |- PDSDD.FUL                  PDS Data Dictionary
    |     |- PDSDD.LBL                  PDS label for PDSDD.FUL
    |
    |- EXTRAS
    |     |
    |     |- EXTRINFO.TXT          Description of the EXTRAS directory
    |     |- XRDINFO.PDF           A list of software capable of processing
    |                              CheMin XRD data
    |
    |- INDEX
    |     |
    |     |- INDXINFO.TXT          Description of the INDEX directory
    |     |- INDEX.TAB             Table of data products in the archive
    |     |- INDEX.LBL             PDS label describing INDEX.TAB
    |
    |- LABEL
          |
          |- LABINFO.TXT           Description of LABEL directory
          |- CHEMIN_EDH.FMT        Format of RDR energy products
          |- CHEMIN_MIN.FMT        Format of mineral abundance products
          |- CHEMIN_XRD.FMT        Format of X-ray diffraction products     
    

==============================================================================
FILE FORMAT
==============================================================================

CheMin RDR data files are stored as ASCII tables with file names ending in
CSV. The tables are in comma-separated-value format, meaning that fields in
a row are separated by commas, and the fields are not padded with spaces to
be the same length in each row. Hence the lengths of rows in a table 
may vary. These files may be read in a text editor and are easily loaded
into spreadsheet programs.

Each table is accompanied by a detached PDS label with the same name but the 
extension LBL, which describes the format and content of the table.
PDS labels are ASCII text files that may be read in a text editor.

The label file may include a pointer to a format file, with a name ending 
in FMT. (For example, such a pointer might be ^STRUCTURE = 
"CHEMIN_XRD.FMT".) Format files are fragments of labels that are intended to 
be read by software as if they were inserted into the label where the 
pointer statement is located. They describe the details of each field in
the table. In this way field definitions are stored in only one place and 
do not have to be repeated in every data product's label. Format files are 
located in the LABEL directory. 

Detailed documentation on data file contents is available in the CheMin RDR 
Software Interface Specification (CHEMIN_RDRSIS) in the DOCUMENT directory.

All text files in the archive, including PDS labels, are delimited with 
carriage-return (ASCII 13) line-feed (ASCII 10) pairs at the end of the line.

PDS requires documentation to be archived as ASCII text for long-term 
readability. However, some documents contain figures and other elements that 
cannot be rendered as ASCII text, and these are given in PDF format for the
user's convenience. A text or HTML version is also provided to meet the ASCII 
text requirement, with images stored as separate files.

==============================================================================
COGNIZANT PERSONNEL
==============================================================================

CheMin RDR data products are generated and assembled into archive volumes by 
the MSL CheMin Science Team, and delivered to Planetary Data System 
Geosciences Node. The Geosciences Node releases the archives to the public 
according to the MSL Project release schedule.

CheMin Science Team Archive Representative: David Vaniman, Planetary Science
Institute, dvaniman@psi.edu.

PDS Geosciences Node Representative: Susan Slavney, Washington University,
Susan.Slavney@wustl.edu. 

