package org.thirtyninealpharesearch.chemin;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import org.apache.commons.io.FilenameUtils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import org.thirtyninealpharesearch.chemin.pds3.RDR4Label;

@Command(name="pds3to4", mixinStandardHelpOptions=true, version="1.0-alpha",
         description="Convert PDS3 label files to PDS4")
public class PDS3To4 implements Callable<Integer> {
    @Parameters(index="0", paramLabel="PDS3-LABEL", description="path to label file")
    private String labelFilename;

    @Option(names={"-o", "--output"}, paramLabel="FILE", description="path to output file")
    private String outputFilename = null;

    @Option(names={"-t", "--template"}, paramLabel="TEMPLATE", description="path to template file")
    private String templateFilename = null;

    @Override
    public Integer call() throws Exception {
        if (templateFilename == null) {
            templateFilename = "org/thirtyninealpharesearch/pds4/RDR4.vm";
            Velocity.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
            Velocity.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
            Velocity.init();
        }

        if (outputFilename == null) {
            outputFilename = FilenameUtils.getBaseName(labelFilename) + ".xml";
        }

        RDR4Label label = RDR4Label.parseFile(labelFilename);

        VelocityContext context = new VelocityContext();
        context.put("label", label);

        Template template = Velocity.getTemplate(templateFilename);
        File pds4File = new File(outputFilename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(pds4File));
        template.merge(context, writer);
        writer.flush();

        int exitCode = new Validator().process(new String[]{outputFilename});

        writer.close();

        return exitCode;
    }

    public static void main(String... args)
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "ERROR");
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "false");

        int exitCode = new CommandLine(new PDS3To4()).execute(args);
        System.exit(exitCode);
    }
}
