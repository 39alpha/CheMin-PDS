package org.thirtyninealpharesearch.pds;

import gov.nasa.pds.validate.ValidateLauncher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class PDS3To4
{
    public static void main(String[] args)
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "ERROR");
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "false");

        String labelFilename = args[0];

        String templateFilename = "org/thirtyninealpharesearch/pds/example.vm";
        if (args.length >= 2) {
            templateFilename = args[1];
        } else {
            Velocity.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
            Velocity.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
            Velocity.init();
        }

        VelocityContext context = new VelocityContext();

        ArrayList<String> list = new ArrayList<String>();
        list.add("Cole");
        list.add("Doug");
        list.add("Tucker");
        context.put("list", list);

        try {
            Label label = LabelBuilder.parseFile(labelFilename);
            System.out.printf("PDS Version: %s\n", label.PDSVersionId);

            Template template = Velocity.getTemplate(templateFilename);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
            template.merge(context, writer);
            writer.flush();

            File registeredProductsFile = PDS3To4.getResourceAsFile("/util/registered_context_products.json");
            ValidateLauncher v = new ValidateLauncher();
            v.setRegisteredProductsFile(registeredProductsFile);
            v.processMain(new String[]{"temp.xml"});

            writer.close();
        } catch (Exception e) {
            System.err.println("Failed to close writer");
            System.exit(1);
        }
    }

    public static File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = PDS3To4.class.getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            IOUtils.copy(in, new FileOutputStream(tempFile));
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
