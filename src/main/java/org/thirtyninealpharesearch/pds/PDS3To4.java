package org.thirtyninealpharesearch.pds;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import org.thirtyninealpharesearch.pds3.RDR4Label;

public class PDS3To4 {
    private int run(String[] args) throws Exception {
        String labelFilename = args[0];

        String templateFilename = "org/thirtyninealpharesearch/pds4/RDR4.vm";
        if (args.length >= 2) {
            templateFilename = args[1];
        } else {
            Velocity.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
            Velocity.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
            Velocity.init();
        }

        RDR4Label label = RDR4Label.parseFile(labelFilename);

        VelocityContext context = new VelocityContext();
        context.put("label", label);

        Template template = Velocity.getTemplate(templateFilename);
        File pds4File = new File("temp.xml");
        BufferedWriter writer = new BufferedWriter(new FileWriter(pds4File));
        template.merge(context, writer);
        writer.flush();

        int exitCode = new Validator().process(new String[]{"temp.xml"});

        writer.close();

        return exitCode;
    }

    public static void main(String[] args)
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "ERROR");
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "false");

        try {
            int exitCode = new PDS3To4().run(args);
            System.exit(exitCode);
        } catch (Exception e) {
            System.err.println("Failed to close writer");
            System.exit(1);
        }
    }
}
