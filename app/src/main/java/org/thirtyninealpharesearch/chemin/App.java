package org.thirtyninealpharesearch.chemin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

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

import org.thirtyninealpharesearch.chemin.pds3.Index;
import org.thirtyninealpharesearch.chemin.pds3.Label;
import org.thirtyninealpharesearch.chemin.pds3.LabelType;

@Command(name="pds3to4", mixinStandardHelpOptions=true, version="0.2.0",
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

    @Option(names={"-l", "--label-type"}, paramLabel="TYPE", description="label type (index, rda, re1, min)")
    public String labelType = null;

    @Option(names={"-n", "--no-validate"}, description="skip NASA PDS validation")
    public boolean noValidate = false;

    @Option(names={"-p", "--print-report"}, description="print validation reports to standard output instead of to file")
    public boolean printReport = false;

    @Option(names={"-d", "--delete-report"}, description="remove validation reports for successful validations")
    public boolean deleteReport = false;

    @Option(names={"-r", "--recursive"}, description="process each .lbl file in a directory")
    public boolean recursive = false;

    @Option(names={"-s", "--schematron"}, description="a schematron file to use for validation")
    public String schematron = null;

    @Option(names={"-x", "--xml-schema"}, description="an XML schema for validation")
    public String schema = null;

    public static void run(Label label, String templateFilename, Writer writer) throws Exception {
        VelocityContext context = new VelocityContext();
        context.put("label", label);

        Template template = Velocity.getTemplate(templateFilename);
        template.merge(context, writer);
    }

    public static void run(Index index, String templateFilename, Writer writer) throws Exception {
        VelocityContext context = new VelocityContext();
        context.put("label", index);

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
            case INDEX:
                throw new Exception("cannot process label file as an index");
            case UNKNOWN:
                throw new Exception("cannot infer label type from file; please provider --label-type argument");
        }

        App.run(label, templateFilename, writer);
    }

    public static void run(Index index, Writer writer) throws Exception {
        String templateFilename = "org/thirtyninealpharesearch/chemin/pds4/INDEX.vm";
        App.run(index, templateFilename, writer);
    }

    public static int validate(
        String filename,
        String schema,
        String schematron,
        boolean printReport,
        boolean deleteReport
    ) throws Exception {
        ArrayList<String> args = new ArrayList<String>();
        args.add(filename);

        String reportFilename = null;
        if (!printReport) {
            reportFilename = filename + ".report";
            args.add("-r");
            args.add(reportFilename);
        }

        if (schema != null) {
            args.add("-x");
            args.add(schema);
        }

        if (schematron != null) {
            args.add("-S");
            args.add(schematron);
        }

        String[] arguments = args.toArray(new String[args.size()]);

        int exitCode = new Validator().process(arguments);
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
        if (FilenameUtils.getName(filename).toLowerCase() == "index.lbl") {
            return this.processIndex(filename);
        } else {
            return this.processLabel(filename);
        }
    }

    public int processIndex(String filename) throws Exception {
        Index index = Index.parseFile(filename);

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
                App.run(index, templateFilename, writer);
            } else {
                App.run(index, writer);
            }
        } finally {
            writer.flush();
            writer.close();
        }

        return noValidate ? 0 : App.validate(outputFilename, schema, schematron, printReport, deleteReport);
    }

    public int processLabel(String filename) throws Exception {
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

        return noValidate ? 0 : App.validate(outputFilename, schema, schematron, printReport, deleteReport);
    }

    public int runRecursive(String directory) throws Exception {
        int failures = 0;
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            throw new Exception(dir.getPath() + " is not a directory");
        }
        Pattern pattern = Pattern.compile("cm[a-b]_\\w{9}(rda|re1|min)\\d{4}\\w{3}\\w{4}\\w{7}\\w\\w\\.lbl$", Pattern.CASE_INSENSITIVE);
        for (File entry : dir.listFiles()) {
            String path = entry.getPath();
            try {
                if (entry.isFile() && pattern.matcher(path).find()) {
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

    protected LabelType getLabelType() throws Exception {
        if (labelType == null) {
            return LabelType.UNKNOWN;
        }

        switch (labelType.toLowerCase()) {
            case "rda":
                return LabelType.RDA;
            case "re1":
                return LabelType.RE1;
            case "min":
                return LabelType.MIN;
            case "index":
                return LabelType.INDEX;
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
