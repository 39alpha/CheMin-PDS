package org.thirtyninealpharesearch.chemin;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
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

@Command(name="pds3to4", mixinStandardHelpOptions=true, version="0.0.1-SNAPSHOT",
         description="Convert PDS3 label files to PDS4")
public class App implements Callable<Integer> {
    @Parameters(index="0", paramLabel="[PDS3-LABEL|DIR]", description="path to label file")
    public String labelFilename;

    @Option(names={"-o", "--output"}, paramLabel="FILE", description="path to output file")
    public String outputFilename = null;

    @Option(names={"-t", "--template"}, paramLabel="TEMPLATE", description="path to template file")
    public String templateFilename = null;

    @Option(names={"-f", "--format"}, paramLabel="FORMAT", description="path to fmt file")
    public String formatFilename = null;

    @Option(names={"-l", "--label-type"}, paramLabel="TYPE", description="label type (rda, re1 or min)")
    public String labelType = null;

    @Option(names={"-n", "--no-validate"}, description="skip NASA PDS validation")
    public boolean noValidate = false;

    @Option(names={"-p", "--print-report"}, description="print validation reports to standard output instead of to file")
    public boolean printReport = false;

    @Option(names={"-d", "--delete-report"}, description="remove validation reports for successful validations")
    public boolean deleteReport = false;

    @Option(names={"-r", "--recursive"}, description="process each .lbl file in a directory")
    public boolean recursive = false;

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

    public static int validate(String filename, boolean printReport, boolean deleteReport) throws Exception {
        String reportFilename = null;
        String[] args = null;
        if (printReport) {
            args = new String[]{filename};
        } else {
            reportFilename = filename + ".report";
            args = new String[]{"-r", reportFilename, filename};
        }

        int exitCode = new Validator().process(args);
        if (exitCode != 0) {
            String errorFilename = filename + ".err";
            Files.move(Paths.get(filename), Paths.get(errorFilename), StandardCopyOption.REPLACE_EXISTING);
            throw new Exception("generated file failed to validate");
        } else if (deleteReport && reportFilename != null) {
            Files.delete(Paths.get(reportFilename));
        }

        return exitCode;
    }

    public int run(String filename) throws Exception {
        Label label = Label.parseFile(filename, formatFilename);

        String outputFilename = this.outputFilename;
        if (outputFilename == null) {
            outputFilename = FilenameUtils.concat(
                FilenameUtils.getFullPath(filename),
                FilenameUtils.getBaseName(filename) + ".xml"
            );
        }
        File pds4File = new File(outputFilename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(pds4File));

        try {
            if (templateFilename != null) {
                App.run(label, templateFilename, writer);
            } else {
                App.run(label, this.getLabelType(), writer);
            }
        } finally {
            writer.flush();
            writer.close();
        }

        return noValidate ? 0 : App.validate(outputFilename, printReport, deleteReport);
    }

    public int runRecursive(String directory) throws Exception {
        int failures = 0;
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            throw new Exception(dir.getPath() + " is not a directory");
        }
        for (File entry : dir.listFiles()) {
            String path = entry.getPath();
            try {
                if (entry.isFile() && FilenameUtils.getExtension(path).toLowerCase().equals("lbl")) {
                    failures += run(path);
                } else if (entry.isDirectory()) {
                    failures += runRecursive(path);
                }
            } catch (Exception e) {
                System.err.println("ERROR: " + path + " failed to process");
                System.err.println("       " + e.getMessage());
                failures += 1;
            }
        }
        return failures;
    }

    @Override
    public Integer call() throws Exception {
        if (templateFilename == null) {
            Velocity.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
            Velocity.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
            Velocity.init();
        }

        try {
            return recursive ? runRecursive(labelFilename) : run(labelFilename);
        } catch (Exception e) {
            System.err.println("ERROR: " + labelFilename + " failed to process");
            System.err.println("       " + e.getMessage());
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
