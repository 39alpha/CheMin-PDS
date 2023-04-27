package org.thirtyninealpharesearch.pds;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import org.thirtyninealpharesearch.pds3.RDR4Label;

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

        try {
            RDR4Label label = RDR4Label.parseFile(labelFilename);
            context.put("label", label);

            Template template = Velocity.getTemplate(templateFilename);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
            template.merge(context, writer);
            writer.flush();

            new Validator().process(new String[]{"temp.xml"});

            writer.close();
        } catch (Exception e) {
            System.err.println("Failed to close writer");
            System.exit(1);
        }
    }
}
