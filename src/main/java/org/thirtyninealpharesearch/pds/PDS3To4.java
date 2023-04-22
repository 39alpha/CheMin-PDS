package org.thirtyninealpharesearch.pds;

import java.io.IOException;

public class PDS3To4
{
    public static void main(String[] args)
    {
        String filename = args[0];

        Label label = null;
        try {
            label = LabelBuilder.parseFile(filename);
        } catch (IOException e) {
            System.err.printf("Error Reading %s\n", filename);
            System.exit(1);
        }

        System.out.printf("PDS Version: %s", label.PDSVersionId);
    }
}
