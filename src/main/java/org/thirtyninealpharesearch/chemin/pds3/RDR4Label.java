package org.thirtyninealpharesearch.chemin.pds3;

import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import org.thirtyninealpharesearch.chemin.pds3.RDR4LabelParser.*;

public class RDR4Label extends RDR4LabelBaseListener {
    public static RDR4Label parseFile(String filename) throws IOException {
        ANTLRFileStream in = new ANTLRFileStream(filename);
        RDR4LabelLexer lexer = new RDR4LabelLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RDR4LabelParser parser = new RDR4LabelParser(tokens);

        RDR4Label label = new RDR4Label(filename);

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(label, parser.label());

        return label;
    }

    public String filename;
    public String PDSVersionId;

    public RDR4Label(String filename) {
        this.filename = filename;
    }

    @Override public void enterPdsversion(@NotNull PdsversionContext ctx) {
        PDSVersionId = ctx.WORD().getText();
    }

    public String getFilename() {
        return filename;
    }

    public String getPDSVersionId() {
        return PDSVersionId;
    }
}
