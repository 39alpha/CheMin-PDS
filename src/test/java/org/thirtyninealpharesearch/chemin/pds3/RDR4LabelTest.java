package org.thirtyninealpharesearch.chemin.pds3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RDR4LabelTest
{
    @Test
    public void shouldParsePDSVersionId()
    {
        String filename = "src/test/data/test.lbl";
        RDR4Label label = assertDoesNotThrow(
            () -> RDR4Label.parseFile(filename),
            "Should Not Throw"
        );
        assertEquals(label.getFilename(), filename);
        assertEquals(label.getPDSVersionId(), "PDS3");
    }
}
