package org.thirtyninealpharesearch.pds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LabelBuilderTest
{
    @Test
    public void shouldParsePDSVersionId()
    {
        Label label = assertDoesNotThrow(
            () -> LabelBuilder.parseFile("src/test/data/test.lbl"),
            "Should Not Throw"
        );
        assertEquals(label.PDSVersionId, "PDS3");
    }
}
