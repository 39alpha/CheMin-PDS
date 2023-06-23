package org.thirtyninealpharesearch.chemin.pds3

import spock.lang.*

class IndexTest extends Specification {
    def testData(filename) {
        return "src/test/data/" + filename
    }

    def "parses index"() {
        setup:
        def filename = "index.lbl"
        def path = testData ("pds3/" + filename)

        when:
        def index = Index.parseFile path

        then:
        index != null
        index.getPath() == path
        index.getFilename() == filename
        index.getPDSVersionId() == "PDS3"
        index.getRecordType() == "FIXED_LENGTH"
        index.getRecordBytes() == 230
        index.getFileRecords() == 253
        index.getIndexTable() == "INDEX.TAB"
        index.getVolumeId() == "MSLCMN_1XXX"

        def data_set_id = index.getDataSetId()
        data_set_id != null
        data_set_id.size() == 2
        data_set_id.get(0) == "MSL-M-CHEMIN-4-RDR-V1.0"
        data_set_id.get(1) == "MSL-M-CHEMIN-5-RDR-V1.0"

        index.getMissionName() == "MARS SCIENCE LABORATORY"

        index.getInstrumentHostName() == "MARS SCIENCE LABORATORY"
        index.getInstrumentHostId() == "MSL"
        index.getInstrumentName() == "CHEMISTRY AND MINERALOGY INSTRUMENT"
        index.getInstrumentId() == "CHEMIN"
        index.getTargetName() == "MARS"

        def table = index.getTable();
        table != null
        table.getInterchangeFormat() == "ASCII"
        table.getRows() == 253
        table.getNumColumns() == 10
        table.getRowBytes() == 230
        table.getIndexType() == "SINGLE"

        def columns = table.getColumns();
        columns != null
        columns.size() == 10

        columns.get(0).getNumber() == 1
        columns.get(0).getName() == "VOLUME_ID"
        columns.get(0).getDataType() == "ASCII_String"
        columns.get(0).getStartByte() == 2
        columns.get(0).getBytes() == 12
        columns.get(0).getDescription() == "The identifier of the volume on which the product is \r\n   stored."
    }
}
