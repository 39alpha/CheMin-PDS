package org.thirtyninealpharesearch.chemin.pds3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import org.thirtyninealpharesearch.chemin.ErrorStrategy;
import org.thirtyninealpharesearch.chemin.ErrorListener;
import org.thirtyninealpharesearch.chemin.pds3.StructureParser.*;
import org.thirtyninealpharesearch.chemin.ParseException;
import org.thirtyninealpharesearch.chemin.SemanticException;

public class Structure extends StructureBaseListener {
    public static Structure parseResource(String filename) throws IOException {
        String resource = "org/thirtyninealpharesearch/chemin/formats/" + filename.toLowerCase();
        InputStream stream = Structure.class.getClassLoader().getResourceAsStream(resource);
        return Structure.parseStream(filename, stream);
    }

    public static Structure parseFile(String filename) throws IOException {
        return Structure.parseStream(filename, CharStreams.fromFileName(filename));
    }

    public static Structure parseStream(InputStream stream) throws IOException {
        return Structure.parseStream(CharStreams.fromStream(stream));
    }

    public static Structure parseStream(String filename, InputStream stream) throws IOException {
        return Structure.parseStream(filename, CharStreams.fromStream(stream));
    }

    public static Structure parseStream(CharStream stream) throws IOException {
        return Structure.parseStream("<unknown stream>", stream);
    }

    public static Structure parseStream(String filename, CharStream stream) throws IOException {
        ErrorListener listener = new ErrorListener(filename);
        Structure fmt = new Structure(filename, listener);

        StructureLexer lexer = new StructureLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StructureParser parser = new StructureParser(tokens);

        parser.setErrorHandler(new ErrorStrategy());
        parser.removeErrorListeners();
        parser.addErrorListener(listener);

        ParseTreeWalker walker = new ParseTreeWalker();
        try {
            walker.walk(fmt, parser.structure());
            return fmt;
        } catch (Exception e) {
            listener.reportErrors();
            return null;
        }
    }

    public class Object {
        public String Name;
        public String DataType;
        public String Unit;
        public Integer Bytes;
        public String Format;
        public String Description;

        public String getName() {
            return Name;
        }

        public String getDataType() {
            return DataType;
        }

        public String getUnit() {
            return Unit;
        }

        public Integer getBytes() {
            return Bytes;
        }

        public String getFormat() {
            return Format;
        }

        public String getDescription() {
            return Description;
        }
    }

    public String filename;
    public ArrayList<Object> Objects;

    public static Map<String,String> DATA_TYPES = Map.ofEntries(
        Map.entry("ASCII_REAL", "ASCII_Real"),
        Map.entry("CHARACTER", "ASCII_String")
    );

    protected Object object;
    protected ErrorListener listener;

    public Structure(String filename, ErrorListener listener) {
        this.filename = filename;
        this.listener = listener;
    }

    public Structure(String filename) {
        this.filename = filename;
        this.listener = new ErrorListener(filename);
    }

    public String getFilename() {
        return filename;
    }

    @Override public void exitObject(@NotNull ObjectContext ctx) {
        if (object == null) {
            notifyListener(ctx, new ParseException("unexpected END OBJECT"));
            return;
        }
        if (Objects == null) {
            Objects = new ArrayList<Object>();
        }
        Objects.add(object);
        object = null;
    }

    @Override public void enterObjectHeader(@NotNull ObjectHeaderContext ctx) {
        if (object == null) {
            object = new Object();
        } else {
            notifyListener(ctx, "illegal start of object; perhaps you forgot to END OBJECT");
        }
        object.Name = ctx.field().getText();
    }

    @Override public void enterObjectName(@NotNull ObjectNameContext ctx) {
        guard(ctx, "NAME", (Object object) -> object.Name);
        object.Name = ctx.quoted().unquoted().getText();
    }

    @Override public void enterObjectDataType(@NotNull ObjectDataTypeContext ctx) {
        guard(ctx, "DATA_TYPE", (Object object) -> object.DataType);
        String dataType = ctx.type().getText();
        object.DataType = Structure.DATA_TYPES.get(dataType);
        if (object.DataType == null) {
            notifyListener(ctx, "unrecognized data type " + dataType);
        }
    }

    @Override public void enterObjectUnit(@NotNull ObjectUnitContext ctx) {
        guard(ctx, "UNIT", (Object object) -> object.Unit);
        object.Unit = ctx.quoted().unquoted().getText();
    }

    @Override public void enterObjectBytes(@NotNull ObjectBytesContext ctx) {
        guard(ctx, "BYTES", (Object object) -> object.Bytes);
        object.Bytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectFormat(@NotNull ObjectFormatContext ctx) {
        guard(ctx, "FORMAT", (Object object) -> object.Format);
        object.Format = ctx.quoted().unquoted().getText();
    }

    @Override public void enterObjectDescription(@NotNull ObjectDescriptionContext ctx) {
        guard(ctx, "DESCRIPTION", (Object object) -> object.Description);
        object.Description = ctx.quoted().unquoted().getText();
    }

    public ArrayList<Object> getObjects() {
        return Objects;
    }

    protected <T> void guard(ParserRuleContext ctx, String name, Function<Object,T> callback) {
        if (object == null) {
            notifyListener(ctx, "unexpected " + name + " outside of OBJECT");
        } else if (callback.apply(object) != null) {
            notifyListener(ctx, "duplicate " + name + " encounterd");
        }
    }

    protected void notifyListener(ParserRuleContext ctx, Exception e) {
        if (listener != null) {
            listener.error(ctx, e);
        }
    }

    protected void notifyListener(ParserRuleContext ctx, String msg) {
        notifyListener(ctx, new Exception(msg));
    }
}
