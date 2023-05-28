package org.thirtyninealpharesearch.chemin.pds3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import org.thirtyninealpharesearch.chemin.pds3.Structure.Object;

public class StructureTest
{
    @Test
    public void cheminXRD() {
        String filename = "src/test/data/pds3/xrd.fmt";

        Structure fmt = assertDoesNotThrow(() -> Structure.parseFile(filename), "Should Not Throw");
        assertNotNull(fmt);
        assertEquals(filename, fmt.getFilename());

        ArrayList<Object> objects = fmt.getObjects();
        assertNotNull(objects);
        assertEquals(2, objects.size());

        Object twoTheta = objects.get(0);
        assertEquals("2-THETA", twoTheta.getName());
        assertEquals("ASCII_Real", twoTheta.getDataType());
        assertEquals("DEGREES", twoTheta.getUnit());
        assertEquals(6, twoTheta.getBytes());
        assertEquals("F6.2", twoTheta.getFormat());

        Object intensity = objects.get(1);
        assertEquals("INTENSITY", intensity.getName());
        assertEquals("ASCII_Real", intensity.getDataType());
        assertEquals("COUNTS", intensity.getUnit());
        assertEquals(7, intensity.getBytes());
        assertEquals("F7.0", intensity.getFormat());
    }

    @Test
    public void cheminMIN() {
        String filename = "src/test/data/pds3/min.fmt";

        Structure fmt = assertDoesNotThrow(() -> Structure.parseFile(filename), "Should Not Throw");
        assertNotNull(fmt);
        assertEquals(filename, fmt.getFilename());

        ArrayList<Object> objects = fmt.getObjects();
        assertNotNull(objects);
        assertEquals(3, objects.size());

        Object mineral = objects.get(0);
        assertEquals("MINERAL", mineral.getName());
        assertEquals("ASCII_String", mineral.getDataType());
        assertEquals("TEXT", mineral.getUnit());
        assertEquals(16, mineral.getBytes());
        assertEquals("A16", mineral.getFormat());

        Object percent = objects.get(1);
        assertEquals("PERCENT", percent.getName());
        assertEquals("ASCII_Real", percent.getDataType());
        assertEquals("WEIGHT_PERCENT", percent.getUnit());
        assertEquals(9, percent.getBytes());
        assertEquals("F9.2", percent.getFormat());

        Object error = objects.get(2);
        assertEquals("ERROR", error.getName());
        assertEquals("ASCII_Real", error.getDataType());
        assertEquals("ESTIMATED_ERROR", error.getUnit());
        assertEquals(7, error.getBytes());
        assertEquals("F7.2", error.getFormat());
    }

    @Test
    public void cheminEDH() {
        String filename = "src/test/data/pds3/edh.fmt";

        Structure fmt = assertDoesNotThrow(() -> Structure.parseFile(filename), "Should Not Throw");
        assertNotNull(fmt);
        assertEquals(filename, fmt.getFilename());

        ArrayList<Object> objects = fmt.getObjects();
        assertNotNull(objects);
        assertEquals(2, objects.size());

        Object energy = objects.get(0);
        assertEquals("ENERGY", energy.getName());
        assertEquals("ASCII_Real", energy.getDataType());
        assertEquals("KEV", energy.getUnit());
        assertEquals(8, energy.getBytes());
        assertEquals("F8.5", energy.getFormat());

        Object intensity = objects.get(1);
        assertEquals("INTENSITY", intensity.getName());
        assertEquals("ASCII_Real", intensity.getDataType());
        assertEquals("COUNT", intensity.getUnit());
        assertEquals(8, intensity.getBytes());
        assertEquals("F8.0", intensity.getFormat());
    }
}
