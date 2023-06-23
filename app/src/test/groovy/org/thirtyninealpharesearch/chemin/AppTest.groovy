package org.thirtyninealpharesearch.chemin

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import org.thirtyninealpharesearch.chemin.App;
import org.thirtyninealpharesearch.chemin.pds3.Index;
import org.thirtyninealpharesearch.chemin.pds3.Label;
import org.thirtyninealpharesearch.chemin.pds3.LabelType;

import spock.lang.*

class AppTest extends Specification {
    @TempDir
    def tempDir

    def testData(filename) {
        return "src/test/data/" + filename
    }

    def copyPathToTemp(path) {
        def exts = ['lbl', 'csv']
        def folder = new File(path.toString())
        for (entry : folder.listFiles()) {
            if (entry.isFile() && exts.any{entry.getPath().endsWith(it)}) {
                def file = entry.toPath()
                def newPath = tempDir.resolve file.getFileName()
                Files.copy file, newPath
            }
        }
    }

    def countExtensions(ext) {
        def count = 0
        def folder = new File(tempDir.toString())
        for (entry : folder.listFiles()) {
            if (entry.isFile() && entry.getPath().endsWith(ext)) {
                count += 1
            }
        }
        return count
    }

    def setupSpec() {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        Velocity.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
    }

    def "label xml generation"() {
        setup:
        def label = Label.parseFile testData(labelFilename)
        def expectedPath = Paths.get testData(expectedFilename)
        def expected = new String(Files.readAllBytes(expectedPath))
        def writer = new StringWriter()
        def app = new App()

        when:
        app.run(label, labelType, writer)

        then:
        writer.toString().equals(expected)

        where:
        labelFilename                                   | labelType       || expectedFilename
        "pds3/cma_404470826rda00790050104ch11503p1.lbl" | LabelType.RDA   || "pds3/cma_404470826rda00790050104ch11503p1.xml"
        "pds3/cma_404655589re100810050104ch12060p1.lbl" | LabelType.RE1   || "pds3/cma_404655589re100810050104ch12060p1.xml"
        "pds3/cma_404470826min00790050104ch11503p1.lbl" | LabelType.MIN   || "pds3/cma_404470826min00790050104ch11503p1.xml"
    }

    def "index xml generation"() {
        setup:
        def index = Index.parseFile testData("pds3/index.lbl")
        def expectedPath = Paths.get testData("pds3/index.xml")
        def expected = new String(Files.readAllBytes(expectedPath))
        def writer = new StringWriter()
        def app = new App()

        when:
        app.run(index, writer)

        then:
        writer.toString().equals(expected);
    }

    def "process labels"() {
        setup:
        def dir = new File(testData(path))
        copyPathToTemp(dir)

        def app = new App()
        app.schema = testData("pds3/schemas/PDS4_MSL_1J00_1100.xsd")
        app.schematron = testData("pds3/schemas/PDS4_MSL_1J00_1100.sch")

        when:
        app.runRecursive tempDir.toString()

        then:
        countExtensions("lbl") == labelCount
        countExtensions("csv") == labelCount
        countExtensions("xml") == labelCount - failures
        countExtensions("xml.err") == failures
        countExtensions("xml.report") == labelCount

        where:
        path       || labelCount | failures
        "pds3/rda" || 62         | 2
        "pds3/re1" || 132        | 34
        "pds3/min" || 53         | 2
    }
}
