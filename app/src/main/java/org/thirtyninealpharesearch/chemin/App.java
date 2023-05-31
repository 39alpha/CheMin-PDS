package org.thirtyninealpharesearch.chemin;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

import org.thirtyninealpharesearch.chemin.pds3.Label;
import org.thirtyninealpharesearch.chemin.pds3.Label.LabelType;

@Command(name="pds3to4", mixinStandardHelpOptions=true, version="1.0-alpha",
         description="Convert PDS3 label files to PDS4")
public class App implements Callable<Integer> {
    @Parameters(index="0", paramLabel="PDS3-LABEL", description="path to label file")
    private String labelFilename;

    @Option(names={"-o", "--output"}, paramLabel="FILE", description="path to output file")
    private String outputFilename = null;

    @Option(names={"-t", "--template"}, paramLabel="TEMPLATE", description="path to template file")
    private String templateFilename = null;

    @Option(names={"-f", "--format"}, paramLabel="FORMAT", description="path to fmt file")
    private String formatFilename = null;

    @Option(names={"-l", "--label-type"}, paramLabel="TYPE", description="label type (rda, re1 or min)")
    private String labelType = null;

    public static void run(Label label, String templateFilename, Writer writer) throws Exception {
        VelocityContext context = new VelocityContext();
        context.put("label", label);

        Template template = Velocity.getTemplate(templateFilename);
        template.merge(context, writer);
    }

    public static void run(Label label, LabelType labelType, Writer writer) throws Exception {
        if (labelType == LabelType.UNKNOWN) {
            labelType = label.inferLabelType();
        }

        String templateFilename = null;
        switch(labelType) {
            case RDA:
                templateFilename = "org/thirtyninealpharesearch/chemin/pds4/RDA.vm";
                break;
            case RE1:
                templateFilename = "org/thirtyninealpharesearch/chemin/pds4/RE1.vm";
                break;
            case MIN:
                templateFilename = "org/thirtyninealpharesearch/chemin/pds4/MIN.vm";
                break;
            case UNKNOWN:
                throw new Exception("cannot infer label type from file; please provider --label-type argument");
        }

        App.run(label, templateFilename, writer);
    }

    @Override
    public Integer call() throws Exception {
        if (templateFilename == null) {
            Velocity.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
            Velocity.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
            Velocity.init();
        }

        if (outputFilename == null) {
            outputFilename = FilenameUtils.getBaseName(labelFilename) + ".xml";
        }

        Label label = null;
        try {
            label = Label.parseFile(labelFilename, formatFilename);
        } catch (Exception e) {
            System.err.println("error: failed to parse label file \"" + labelFilename + "\"");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        File pds4File = new File(outputFilename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(pds4File));

        try {
            if (templateFilename != null) {
                App.run(label, templateFilename, writer);
            } else {
                App.run(label, this.getLabelType(), writer);
            }
            writer.flush();
            writer.close();

            return new Validator().process(new String[]{outputFilename});
        } catch (Exception e) {
            System.err.println("error: " + e.getMessage());
            return 1;
        }
    }

    protected Label.LabelType getLabelType() throws Exception {
        if (labelType == null) {
            return Label.LabelType.UNKNOWN;
        }

        switch (labelType.toLowerCase()) {
            case "rda":
                return Label.LabelType.RDA;
            case "re1":
                return Label.LabelType.RE1;
            case "min":
                return Label.LabelType.MIN;
            default:
                throw new Exception("unrecognized label type");
        }
    }

    public static void main(String... args)
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "ERROR");
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "false");

        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
