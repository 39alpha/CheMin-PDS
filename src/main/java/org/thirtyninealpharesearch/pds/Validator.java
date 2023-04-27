package org.thirtyninealpharesearch.pds;

import gov.nasa.pds.validate.ValidateLauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class Validator {
    private final String registeredProducts = "/util/registered_context_products.json";
    private ValidateLauncher v = null;

    public Validator() throws Exception {
        v = new ValidateLauncher();
        v.setRegisteredProductsFile(this.getResourceAsFile(registeredProducts));
    }

    private File getResourceAsFile(String resourcePath) throws IOException {
        InputStream in = getClass().getResourceAsStream(resourcePath);
        if (in == null) {
            return null;
        }

        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
        tempFile.deleteOnExit();

        IOUtils.copy(in, new FileOutputStream(tempFile));
        return tempFile;
    }

    public int process(String[] args) throws Exception {
        return v.processMain(args);
    }
}
