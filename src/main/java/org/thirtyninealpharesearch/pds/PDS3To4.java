package org.thirtyninealpharesearch.pds;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class PDS3To4
{
    public static void main(String[] args)
    {
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

        Label label = null;
        try {
            label = LabelBuilder.parseFile(labelFilename);
        } catch (IOException e) {
            System.err.printf("Error Reading %s\n", labelFilename);
            System.exit(1);
        }

        Template template = null;
        try {
            template = Velocity.getTemplate(templateFilename);
        } catch (Exception e) {
            System.err.printf("Error Reading %s\n", templateFilename);
            System.exit(1);
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        template.merge(context, writer);

        try {
            writer.flush();
        } catch (Exception e) {
            System.err.println("Failed to flush template");
            System.exit(1);
        }

        try {
            writer.close();
        } catch (Exception e) {
            System.err.println("Failed to close writer");
            System.exit(1);
        }

        System.out.printf("PDS Version: %s", label.PDSVersionId);
    }
}
