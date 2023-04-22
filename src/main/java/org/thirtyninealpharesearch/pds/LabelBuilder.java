package org.thirtyninealpharesearch.pds;

import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

public class LabelBuilder extends LabelBaseListener {
    public static Label parseFile(String filename) throws IOException {
        ANTLRFileStream in = new ANTLRFileStream(filename);
        LabelLexer lexer = new LabelLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LabelParser parser = new LabelParser(tokens);
        LabelBuilder builder = new LabelBuilder(filename);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(builder, parser.label());

        return builder.data;
    }

    private Label data;
    private String filename;

    public LabelBuilder(String filename) {
        this.filename = filename;
        this.data = new Label();
    }

    @Override public void enterPdsversion(@NotNull LabelParser.PdsversionContext ctx) {
        this.data.PDSVersionId = ctx.WORD().getText();
    }
}