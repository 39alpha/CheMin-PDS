package org.thirtyninealpharesearch.chemin.pds3

import spock.lang.Specification

class LabelTest extends Specification {
    def testData(filename) {
        return "src/test/data/" + filename
    }

    def filesInDirectory(path) {
        def files = []
        def folder = new File(testData(path))
        for (entry : folder.listFiles()) {
            if (entry.isFile()) {
                println(entry.getPath())
                files.add entry.path
            }
        }
        return files
    }

    def "parses basic"() {
        setup:
        def filename = testData "pds3/rdr4_test.lbl"

        when:
        def label = Label.parseFile filename

        then:
        label != null
        label.getFilename() == filename
        label.getPDSVersionId() == "PDS3"
        label.getLabelRevisionNote() == "The label was revised for some reason"
        label.getRecordType() == "STREAM"
        label.getRecordBytes() == 12288
        label.getFileRecords() == 981
        label.getDataSetId() == "MSL-M-CHEMIN-4-RDR-V1.0"
        label.getProductId() == "CMA_404470826RDA00790050104CH11503P1"
        label.getProductVersionId() == "V1.0"
        label.getReleaseId() == "0002"
        label.getSourceProductId().size() == 38
        label.getProductType() == "CHEMIN_RDA"
        label.getInstrumentHostId() == "MSL"
        label.getInstrumentHostName() == "MARS SCIENCE LABORATORY"
        label.getInstrumentId() == "CHEMIN"
        label.getTargetName() == "MARS"
        label.getMSLCalibrationStandardName() == "N/A"
        label.getMissionPhaseName() == "PRIMARY SURFACE MISSION"
        label.getProductCreationTime() == "2013-02-25T19:45:00"
        label.getStartTime() == "2012-10-25T21:03:42.206"
        label.getStopTime() == "UNK"
        label.getSpacecraftClockStartCount() == "404470826.52111"
        label.getSpacecraftClockStopCount() == "UNK"

        def links = label.getObjectLinks()
        links != null
        links.size() == 2

        def headerLink = links.get "HEADER"
        headerLink != null
        headerLink.getName() == "HEADER"
        headerLink.getFilename() == "RDR4_TEST.CSV"
        headerLink.getIndex() == 1

        def spreadsheetLink = links.get("SPREADSHEET")
        spreadsheetLink != null
        spreadsheetLink.getName() == "SPREADSHEET"
        spreadsheetLink.getFilename() == "RDR4_TEST.CSV"
        spreadsheetLink.getIndex() == 2

        def objects = label.getObjects()
        objects != null
        objects.size() == 2

        def header = objects.get "HEADER"
        header != null
        header.getName() == "HEADER"
        header.getEnd() == "HEADER"
        header.getBytes() == 19
        header.getRows() == null
        header.getRowBytes() == null
        header.getFields() == null
        header.getFieldDelimiter() == null
        header.getHeaderType() == "TEXT"
        header.getDescription() == "This header record contains column headings\r\n for the following table."

        def spreadsheet = objects.get "SPREADSHEET"
        spreadsheet != null
        spreadsheet.getName() == "SPREADSHEET"
        spreadsheet.getEnd() == "SPREADSHEET"
        spreadsheet.getBytes() == null
        spreadsheet.getRows() == 980
        spreadsheet.getRowBytes() == 255
        spreadsheet.getFields() == 2
        spreadsheet.getFieldDelimiter() == "COMMA"
        spreadsheet.getHeaderType() == null
        spreadsheet.getDescription() == "This table contains diffraction-all K-alpha\r\n diffraction data for the fourth scooped soil sample from the Rocknest target,\r\n analyzed in CheMin cell number 1a (Kapton window). The table represents\r\n results from sequences uploaded from sol00077 to sol00088, including 6,840\r\n individual 10-second frames in 38 minor frames of 180 individual 10-second\r\n frames. CCD temperatures during data collection were ~-50 degrees centigrade.\r\n Column 1 of the table lists 2-theta from 3.00 to 51.95 degrees cobalt\r\n K-alpha, in increments of 0.05 degrees (980 entries). Column 2 lists the\r\n intensity of the diffraction for each 2-theta value in column 1."

        def structure = spreadsheet.getStructure()
        structure != null

        def fields = structure.getObjects()
        fields != null
        fields.size() == 2

        def twoTheta = fields.get 0
        twoTheta.getName() == "2-THETA"
        twoTheta.getDataType() == "ASCII_Real"
        twoTheta.getUnit() == "DEGREES"
        twoTheta.getBytes() == 6
        twoTheta.getFormat() == "F6.2"
        twoTheta.getDescription() == "2-theta"

        def intensity = fields.get 1
        intensity.getName() == "INTENSITY"
        intensity.getDataType() == "ASCII_Real"
        intensity.getUnit() == "COUNTS"
        intensity.getBytes() == 7
        intensity.getFormat() == "F7.0"
        intensity.getDescription() == "The intensity of the diffraction for each 2-theta value in column 1"

        label.getLogicalIdentifier() == "rdr4_test"
    }

    def "parse abnormal file"() {
        setup:
        def filename = testData "pds3/rdr4/cma_602259727re123070732502ch00113p1.lbl"

        when:
        def label = Label.parseFile filename

        then:
        label != null
        label.getProductType() == "CHEMIN_RE1"
        label.getStopTime() == "UNK"
    }

    def "parse all rdr4 label files"() {
        expect:
        println filename
        def label = Label.parseFile(filename)

        where:
        filename << filesInDirectory("pds3/rdr4")
    }

    def "parse all rdr5 label files"() {
        expect:
        println filename
        def label = Label.parseFile(filename)

        where:
        filename << filesInDirectory("pds3/rdr5")
    }
}