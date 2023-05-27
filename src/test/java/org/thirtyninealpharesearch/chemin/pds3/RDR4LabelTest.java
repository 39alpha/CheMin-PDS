package org.thirtyninealpharesearch.chemin.pds3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

import org.thirtyninealpharesearch.chemin.pds3.RDR4Label.ObjectLink;
import org.thirtyninealpharesearch.chemin.pds3.RDR4Label.Object;

public class RDR4LabelTest
{
    public String testData(String filename) {
        return FilenameUtils.concat("src/test/data", filename);
    }

    @Test
    public void shouldParse() {
        String filename = testData("pds3/rdr4_test.lbl");

        RDR4Label label = assertDoesNotThrow(() -> RDR4Label.parseFile(filename), "Should Not Throw");
        assertNotNull(label);
        assertEquals(filename, label.getFilename());
        assertEquals("PDS3", label.getPDSVersionId());
        assertEquals("The label was revised for some reason", label.getLabelRevisionNote());
        assertEquals("STREAM", label.getRecordType());
        assertEquals(12288, label.getRecordBytes());
        assertEquals(981, label.getFileRecords());

        HashMap<String, ObjectLink> links = label.getObjectLinks();
        assertNotNull(links);
        assertEquals(2, links.size());

        ObjectLink header_link = links.get("HEADER");
        assertNotNull(header_link);
        assertEquals("HEADER", header_link.getName());
        assertEquals("RDR4_TEST.CSV", header_link.getFilename());
        assertEquals(1, header_link.getIndex());

        ObjectLink spreadsheet_link = links.get("SPREADSHEET");
        assertNotNull(spreadsheet_link);
        assertEquals("SPREADSHEET", spreadsheet_link.getName());
        assertEquals("RDR4_TEST.CSV", spreadsheet_link.getFilename());
        assertEquals(2, spreadsheet_link.getIndex());

        assertEquals("MSL-M-CHEMIN-4-RDR-V1.0", label.getDataSetId());
        assertEquals("CMA_404470826RDA00790050104CH11503P1", label.getProductId());
        assertEquals("V1.0", label.getProductVersionId());
        assertEquals("0002", label.getReleaseId());
        assertEquals(38, label.getSourceProductId().size());
        assertEquals("CHEMIN_RDA", label.getProductType());
        assertEquals("MSL", label.getInstrumentHostId());
        assertEquals("MARS SCIENCE LABORATORY", label.getInstrumentHostName());
        assertEquals("CHEMIN", label.getInstrumentId());
        assertEquals("MARS", label.getTargetName());
        assertEquals("N/A", label.getMSLCalibrationStandardName());
        assertEquals("PRIMARY SURFACE MISSION", label.getMissionPhaseName());
        assertEquals("2013-02-25T19:45:00", label.getProductCreationTime());
        assertEquals("2012-10-25T21:03:42.206", label.getStartTime());
        assertEquals("UNK", label.getStopTime());
        assertEquals("404470826.52111", label.getSpacecraftClockStartCount());
        assertEquals("UNK", label.getSpacecraftClockStopCount());

        HashMap<String, Object> objects = label.getObjects();
        assertNotNull(objects);
        assertEquals(2, objects.size());

        Object header = objects.get("HEADER");
        assertNotNull(header);
        assertEquals("HEADER", header.getName());
        assertEquals("HEADER", header.getEnd());
        assertEquals(19, header.getBytes());
        assertNull(header.getRows());
        assertNull(header.getRowBytes());
        assertNull(header.getFields());
        assertNull(header.getFieldDelimiter());
        assertEquals("TEXT", header.getHeaderType());
        assertEquals("This header record contains column headings\r\n for the following table.", header.getDescription());

        Object spreadsheet = objects.get("SPREADSHEET");
        assertNotNull(spreadsheet);
        assertEquals("SPREADSHEET", spreadsheet.getName());
        assertEquals("SPREADSHEET", spreadsheet.getEnd());
        assertNull(spreadsheet.getBytes());
        assertEquals(980, spreadsheet.getRows());
        assertEquals(255, spreadsheet.getRowBytes());
        assertEquals(2, spreadsheet.getFields());
        assertEquals("COMMA", spreadsheet.getFieldDelimiter());
        assertNull(spreadsheet.getHeaderType());
        assertEquals("This table contains diffraction-all K-alpha\r\n diffraction data for the fourth scooped soil sample from the Rocknest target,\r\n analyzed in CheMin cell number 1a (Kapton window). The table represents\r\n results from sequences uploaded from sol00077 to sol00088, including 6,840\r\n individual 10-second frames in 38 minor frames of 180 individual 10-second\r\n frames. CCD temperatures during data collection were ~-50 degrees centigrade.\r\n Column 1 of the table lists 2-theta from 3.00 to 51.95 degrees cobalt\r\n K-alpha, in increments of 0.05 degrees (980 entries). Column 2 lists the\r\n intensity of the diffraction for each 2-theta value in column 1.", spreadsheet.getDescription());

        assertNotNull(spreadsheet.getStructure());
        ArrayList<Structure.Object> fields = spreadsheet.getStructure().getObjects();
        assertNotNull(fields);
        assertEquals(2, fields.size());

        Structure.Object twoTheta = fields.get(0);
        assertEquals("2-THETA", twoTheta.getName());
        assertEquals("ASCII_REAL", twoTheta.getDataType());
        assertEquals("DEGREES", twoTheta.getUnit());
        assertEquals(6, twoTheta.getBytes());
        assertEquals("F6.2", twoTheta.getFormat());

        Structure.Object intensity = fields.get(1);
        assertEquals("INTENSITY", intensity.getName());
        assertEquals("ASCII_REAL", intensity.getDataType());
        assertEquals("COUNTS", intensity.getUnit());
        assertEquals(7, intensity.getBytes());
        assertEquals("F7.0", intensity.getFormat());

        assertEquals("rdr4_test", label.getLogicalIdentifier());
    }

    @Test
    public void parseAbnormalFile() {
        String filename = testData("pds3/rdr4/cma_602259727re123070732502ch00113p1.lbl");
        RDR4Label label = assertDoesNotThrow(() -> RDR4Label.parseFile(filename), "Should Not Throw");
        assertNotNull(label);
        assertEquals("CHEMIN_RE1", label.getProductType());
        assertEquals("UNK", label.getStopTime());
    }

    @Test
    public void parsesAllLabelFiles() {
        File folder = new File(testData("pds3/rdr4"));
        for (File entry : folder.listFiles()) {
            if (entry.isFile()) {
                RDR4Label label = assertDoesNotThrow(() -> RDR4Label.parseFile(entry.getPath()), "Should Not Throw");
                assertNotNull(label);
            }
        }
    }
}
