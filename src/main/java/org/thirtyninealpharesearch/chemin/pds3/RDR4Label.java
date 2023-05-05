package org.thirtyninealpharesearch.chemin.pds3;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import org.thirtyninealpharesearch.chemin.ErrorStrategy;
import org.thirtyninealpharesearch.chemin.ErrorListener;
import org.thirtyninealpharesearch.chemin.pds3.RDR4LabelParser.*;
import org.thirtyninealpharesearch.chemin.ParseException;
import org.thirtyninealpharesearch.chemin.SemanticException;

public class RDR4Label extends RDR4LabelBaseListener {
    public static RDR4Label parseFile(String filename, boolean silent) throws IOException {
        ErrorListener listener = new ErrorListener(filename);

        ANTLRFileStream in = new ANTLRFileStream(filename);
        RDR4LabelLexer lexer = new RDR4LabelLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RDR4LabelParser parser = new RDR4LabelParser(tokens);

        parser.setErrorHandler(new ErrorStrategy());
        parser.removeErrorListeners();
        parser.addErrorListener(listener);

        RDR4Label label = new RDR4Label(filename, listener);

        ParseTreeWalker walker = new ParseTreeWalker();
        try {
            walker.walk(label, parser.label());
            return label;
        } catch (Exception e) {
            listener.reportErrors();
            return null;
        }
    }

    public static RDR4Label parseFile(String filename) throws IOException {
        return RDR4Label.parseFile(filename, false);
    }

    public class ObjectLink {
        public String name;
        public String filename;
        public int index;

        public ObjectLink(String name, String filename, int index) {
            this.name = name;
            this.filename = filename;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public String getFilename() {
            return filename;
        }

        public int getIndex() {
            return index;
        }
    }

    public class Object {
        public String Name;
        public String End;
        public Integer Bytes;
        public Integer Rows;
        public Integer RowBytes;
        public Integer Fields;
        public String FieldDelimiter;
        public String HeaderType;
        public String Description;

        public String getName() {
            return Name;
        }

        public String getEnd() {
            return End;
        }

        public Integer getBytes() {
            return Bytes;
        }

        public Integer getRows() {
            return Rows;
        }

        public Integer getRowBytes() {
            return RowBytes;
        }

        public Integer getFields() {
            return Fields;
        }

        public String getFieldDelimiter() {
            return FieldDelimiter;
        }

        public String getHeaderType() {
            return HeaderType;
        }

        public String getDescription() {
            return Description;
        }
    }

    public String filename;
    public String PDSVersionId;
    public String RecordType;
    public Integer RecordBytes;
    public Integer FileRecords;
    public HashMap<String, ObjectLink> ObjectLinks;
    public String DataSetId;
    public String ProductId;
    public String ProductVersionId;
    public String ReleaseId;
    public ArrayList<String> SourceProductId;
    public String ProductType;
    public String InstrumentHostId;
    public String InstrumentHostName;
    public String InstrumentId;
    public String TargetName;
    public String MSLCalibrationStandardName;
    public String MissionPhaseName;
    public String ProductCreationTime;
    public String StartTime;
    public String StopTime;
    public String SpacecraftClockStartCount;
    public String SpacecraftClockStopCount;
    public HashMap<String, Object> Objects;

    protected Object object;
    protected ErrorListener listener;

    public RDR4Label(String filename, ErrorListener listener) {
        this.filename = filename;
        this.listener = listener;
    }

    public RDR4Label(String filename) {
        this.filename = filename;
        this.listener = null;
    }

    public String getFilename() {
        return filename;
    }

    @Override public void exitLabel(@NotNull LabelContext ctx) {
        for (Object object : Objects.values()) {
            if (ObjectLinks == null || ObjectLinks.isEmpty() || ObjectLinks.get(object.getName()) == null) {
                String msg = String.format("Object of name \"%s\" defined, but no object link found", object.getName());
                notifyListener(ctx, new SemanticException(msg));
            }
        }

        for (ObjectLink link : ObjectLinks.values()) {
            if (Objects == null || Objects.isEmpty() || Objects.get(link.getName()) == null) {
                String msg = String.format("Object link of name \"%s\" defined, but no corresponding object found", link.getName());
                notifyListener(ctx, new SemanticException(msg));
            }
        }
    }

    @Override public void enterPdsVersionId(@NotNull PdsVersionIdContext ctx) {
        PDSVersionId = ctx.word().getText();
    }

    public String getPDSVersionId() {
        return PDSVersionId;
    }

    @Override public void enterRecordType(@NotNull RecordTypeContext ctx) {
        RecordType = ctx.word().getText();
    }

    public String getRecordType() {
        return RecordType;
    }

    @Override public void enterRecordBytes(@NotNull RecordBytesContext ctx) {
        RecordBytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    public int getRecordBytes() {
        return RecordBytes;
    }

    @Override public void enterFileRecords(@NotNull FileRecordsContext ctx) {
        FileRecords = Integer.parseInt(ctx.INUMBER().getText());
    }

    public int getFileRecords() {
        return FileRecords;
    }

    @Override public void enterObjectLink(@NotNull ObjectLinkContext ctx) {
        String name = ctx.WORD().getText();
        String filename = ctx.fileTuple().filename().getText();
        int index = Integer.parseInt(ctx.fileTuple().INUMBER().getText());
        ObjectLink link = new ObjectLink(name, filename, index);

        if (ObjectLinks == null) {
            ObjectLinks = new HashMap<String, ObjectLink>();
        }
        ObjectLinks.put(name, link);
    }

    public HashMap<String, ObjectLink> getObjectLinks() {
        return ObjectLinks;
    }

    @Override public void enterDatasetId(@NotNull DatasetIdContext ctx) {
        DataSetId = ctx.hyphenatedWord().getText();
    }

    public String getDataSetId() {
        return DataSetId;
    }

    @Override public void enterProductId(@NotNull ProductIdContext ctx) {
        ProductId = ctx.word().getText();
    }

    public String getProductId() {
        return ProductId;
    }

    @Override public void enterProductVersionId(@NotNull ProductVersionIdContext ctx) {
        ProductVersionId = ctx.VERSION().getText();
    }

    public String getProductVersionId() {
        return ProductVersionId;
    }

    @Override public void enterReleaseId(@NotNull ReleaseIdContext ctx) {
        ReleaseId = ctx.INUMBER().getText();
    }

    public String getReleaseId() {
        return ReleaseId;
    }

    @Override public void enterSourceProductId(@NotNull SourceProductIdContext ctx) {
        SourceProductId = new ArrayList<String>();
        IdentifierEntriesContext idc = ctx.identifierList().identifierEntries();
        while (idc != null) {
            String entry = idc.identifierEntry().word().getText();
            SourceProductId.add(entry);

            idc = idc.identifierEntries();
        }
    }

    public ArrayList<String> getSourceProductId() {
        return SourceProductId;
    }

    @Override public void enterProductType(@NotNull ProductTypeContext ctx) {
        ProductType = ctx.WORD().getText();
    }

    public String getProductType() {
        return ProductType;
    }

    @Override public void enterInstrumentHostId(@NotNull InstrumentHostIdContext ctx) {
        InstrumentHostId = ctx.WORD().getText();
    }

    public String getInstrumentHostId() {
        return InstrumentHostId;
    }

    @Override public void enterInstrumentHostName(@NotNull InstrumentHostNameContext ctx) {
        InstrumentHostName = ctx.words().getText();
    }

    public String getInstrumentHostName() {
        return InstrumentHostName;
    }

    @Override public void enterInstrumentId(@NotNull InstrumentIdContext ctx) {
        InstrumentId = ctx.WORD().getText();
    }

    public String getInstrumentId() {
        return InstrumentId;
    }

    @Override public void enterTargetName(@NotNull TargetNameContext ctx) {
        TargetName = ctx.WORD().getText();
    }

    public String getTargetName() {
        return TargetName;
    }

    @Override public void enterMslCalibrationStandardName(@NotNull MslCalibrationStandardNameContext ctx) {
        MSLCalibrationStandardName = ctx.words().getText();
    }

    public String getMSLCalibrationStandardName() {
        return MSLCalibrationStandardName;
    }

    @Override public void enterMissionPhaseName(@NotNull MissionPhaseNameContext ctx) {
        MissionPhaseName = ctx.words().getText();
    }

    public String getMissionPhaseName() {
        return MissionPhaseName;
    }

    @Override public void enterProductCreationTime(@NotNull ProductCreationTimeContext ctx) {
        ProductCreationTime = ctx.utcDate().getText();
    }

    public String getProductCreationTime() {
        return ProductCreationTime;
    }

    @Override public void enterStartTime(@NotNull StartTimeContext ctx) {
        StartTime = ctx.utcDate().getText();
    }

    public String getStartTime() {
        return StartTime;
    }

    @Override public void enterStopTime(@NotNull StopTimeContext ctx) {
        OptionalUTCDateContext c = ctx.optionalUTCDate();
        if (c.utcDate() != null) {
            StopTime = c.getText();
        } else {
            StopTime = "UNK";
        }
    }

    public String getStopTime() {
        return StopTime;
    }

    @Override public void enterSpacecraftClockStartCount(@NotNull SpacecraftClockStartCountContext ctx) {
        SpacecraftClockStartCount = ctx.clockCount().getText();
    }

    public String getSpacecraftClockStartCount() {
        return SpacecraftClockStartCount;
    }

    @Override public void enterSpacecraftClockStopCount(@NotNull SpacecraftClockStopCountContext ctx) {
        OptionalClockCountContext c = ctx.optionalClockCount();
        if (c.clockCount() != null) {
            SpacecraftClockStopCount = c.getText();
        } else {
            SpacecraftClockStopCount = "UNK";
        }
    }

    public String getSpacecraftClockStopCount() {
        return SpacecraftClockStopCount;
    }

    @Override public void enterObject(@NotNull ObjectContext ctx) {
        if (object == null) {
            object = new Object();
        }
    }

    @Override public void exitObject(@NotNull ObjectContext ctx) {
        if (object == null) {
            notifyListener(ctx, new ParseException("unexpected END OBJECT"));
            return;
        }
        if (Objects == null) {
            Objects = new HashMap<String,Object>();
        }
        Objects.put(object.getName(), object);
        object = null;
    }

    @Override public void enterObjectHeader(@NotNull ObjectHeaderContext ctx) {
        object.Name = ctx.WORD().getText();
    }

    @Override public void enterObjectBytes(@NotNull ObjectBytesContext ctx) {
        object.Bytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectRows(@NotNull ObjectRowsContext ctx) {
        object.Rows = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectRowBytes(@NotNull ObjectRowBytesContext ctx) {
        object.RowBytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectFields(@NotNull ObjectFieldsContext ctx) {
        object.Fields = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectFieldDelimiter(@NotNull ObjectFieldDelimiterContext ctx) {
        object.FieldDelimiter = ctx.WORD().getText();
    }

    @Override public void enterObjectHeaderType(@NotNull ObjectHeaderTypeContext ctx) {
        object.HeaderType = ctx.word().getText();
    }

    @Override public void enterObjectDescription(@NotNull ObjectDescriptionContext ctx) {
        object.Description = ctx.quoted().unquoted().getText();
    }

    @Override public void enterObjectEnd(@NotNull ObjectEndContext ctx) {
        object.End = ctx.WORD().getText();
    }

    public HashMap<String,Object> getObjects() {
        return Objects;
    }

    protected void notifyListener(ParserRuleContext ctx, Exception e) {
        if (listener != null) {
            listener.error(ctx, e);
        }
    }
}
