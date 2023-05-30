package org.thirtyninealpharesearch.chemin.pds3;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import org.apache.commons.io.FilenameUtils;

import org.thirtyninealpharesearch.chemin.ErrorStrategy;
import org.thirtyninealpharesearch.chemin.ErrorListener;
import org.thirtyninealpharesearch.chemin.pds3.LabelParser.*;
import org.thirtyninealpharesearch.chemin.ParseException;
import org.thirtyninealpharesearch.chemin.SemanticException;

public class Label extends LabelBaseListener {
    public static Label parseFile(String filename, String format) throws IOException {
        ErrorListener listener = new ErrorListener(filename);

        ANTLRFileStream in = new ANTLRFileStream(filename);
        LabelLexer lexer = new LabelLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LabelParser parser = new LabelParser(tokens);

        parser.setErrorHandler(new ErrorStrategy());
        parser.removeErrorListeners();
        parser.addErrorListener(listener);

        Label label = new Label(filename, format, listener);

        ParseTreeWalker walker = new ParseTreeWalker();

        walker.walk(label, parser.label());
        if (listener.hasErrors()) {
            listener.reportErrors();
            throw new IOException(String.format("failed to parse \"%s\"", filename));
        }
        return label;
    }

    public static Label parseFile(String filename) throws IOException {
        return Label.parseFile(filename, null);
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
        public Structure Structure;

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

        public Structure getStructure() {
            return Structure;
        }
    }

    public String filename;
    public String format;

    public String PDSVersionId;
    public String LabelRevisionNote;
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

    public static String[] REQUIRED_PROPERTIES = {
        "PDSVersionId",
        "RecordType",
        "RecordBytes",
        "FileRecords",
        "DataSetId",
        "ProductId",
        "ProductVersionId",
        "ReleaseId",
        "SourceProductId",
        "ProductType",
        "InstrumentHostId",
        "InstrumentHostName",
        "InstrumentId",
        "TargetName",
        "MSLCalibrationStandardName",
        "MissionPhaseName",
        "ProductCreationTime",
        "StartTime",
        "StopTime",
        "SpacecraftClockStartCount",
        "SpacecraftClockStopCount",
    };

    protected Object object;
    protected ErrorListener listener;

    public Label(String filename, String format, ErrorListener listener) {
        this.filename = filename;
        this.format = format;
        this.listener = listener;
    }

    public Label(String filename, String format) {
        this.filename = filename;
        this.format = format;
        this.listener = new ErrorListener(filename);
    }

    public Label(String filename, ErrorListener listener) {
        this.filename = filename;
        this.format = null;
        this.listener = listener;
    }

    public Label(String filename) {
        this.filename = filename;
        this.format = null;
        this.listener = new ErrorListener(filename);
    }

    public String getFilename() {
        return filename;
    }

    public String getFormat() {
        return format;
    }

    @Override public void exitLabel(@NotNull LabelContext ctx) {
        for (String field_name : Label.REQUIRED_PROPERTIES) {
            try {
                Field field = Label.class.getField(field_name);
                if (field.get(this) == null) {
                    notifyListener(ctx, "no %s field found");
                }
            } catch (Exception e) {
            }
        }

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
        if (PDSVersionId != null) {
            notifyListener(ctx, "duplicate PDS_VERSION encountered");
        }
        PDSVersionId = ctx.word().getText();
    }

    public String getPDSVersionId() {
        return PDSVersionId;
    }

    @Override public void enterLabelRevisionNote(@NotNull LabelRevisionNoteContext ctx) {
        if (LabelRevisionNote != null) {
            notifyListener(ctx, "duplicate LABEL_REVISION_NOTE encountered");
        }
        LabelRevisionNote = ctx.quoted().unquoted().getText();
    }

    public String getLabelRevisionNote() {
        return LabelRevisionNote;
    }

    @Override public void enterRecordType(@NotNull RecordTypeContext ctx) {
        if (RecordType != null) {
            notifyListener(ctx, "duplicate RECORD_TYPE encountered");
        }
        RecordType = ctx.word().getText();
    }

    public String getRecordType() {
        return RecordType;
    }

    @Override public void enterRecordBytes(@NotNull RecordBytesContext ctx) {
        if (RecordBytes != null) {
            notifyListener(ctx, "duplicate RECORD_BYTES encountered");
        }
        RecordBytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    public int getRecordBytes() {
        return RecordBytes;
    }

    @Override public void enterFileRecords(@NotNull FileRecordsContext ctx) {
        if (FileRecords != null) {
            notifyListener(ctx, "duplicate FILE_RECORDS encountered");
        }
        FileRecords = Integer.parseInt(ctx.INUMBER().getText());
    }

    public int getFileRecords() {
        return FileRecords;
    }

    @Override public void enterObjectLink(@NotNull ObjectLinkContext ctx) {
        String name = ctx.WORD().getText();
        String filename = ctx.fileTuple().filename().getText().trim();
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

    @Override public void enterDataSetId(@NotNull DataSetIdContext ctx) {
        if (DataSetId != null) {
            notifyListener(ctx, "duplicate DATA_SET_ID encountered");
        }
        DataSetId = ctx.hyphenatedWord().getText();
    }

    public String getDataSetId() {
        return DataSetId;
    }

    @Override public void enterProductId(@NotNull ProductIdContext ctx) {
        if (ProductId != null) {
            notifyListener(ctx, "duplicate PRODUCT_ID encountered");
        }
        ProductId = ctx.word().getText();
    }

    public String getProductId() {
        return ProductId;
    }

    @Override public void enterProductVersionId(@NotNull ProductVersionIdContext ctx) {
        if (ProductVersionId != null) {
            notifyListener(ctx, "duplicate PRODUCT_VERSION_ID encountered");
        }
        ProductVersionId = ctx.VERSION().getText();
    }

    public String getProductVersionId() {
        return ProductVersionId;
    }

    @Override public void enterReleaseId(@NotNull ReleaseIdContext ctx) {
        if (ReleaseId != null) {
            notifyListener(ctx, "duplicate RELEASE_ID encountered");
        }
        ReleaseId = ctx.INUMBER().getText();
    }

    public String getReleaseId() {
        return ReleaseId;
    }

    @Override public void enterSourceProductId(@NotNull SourceProductIdContext ctx) {
        if (SourceProductId != null) {
            notifyListener(ctx, "duplicate SOURCE_PRODUCT_ID encountered");
        }
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
        if (ProductType != null) {
            notifyListener(ctx, "duplicate PRODUCT_TYPE encountered");
        }
        ProductType = ctx.quoted().unquoted().getText();
    }

    public String getProductType() {
        return ProductType;
    }

    @Override public void enterInstrumentHostId(@NotNull InstrumentHostIdContext ctx) {
        if (InstrumentHostId != null) {
            notifyListener(ctx, "duplicate INSTRUMENT_HOST_ID encountered");
        }
        InstrumentHostId = ctx.WORD().getText();
    }

    public String getInstrumentHostId() {
        return InstrumentHostId;
    }

    @Override public void enterInstrumentHostName(@NotNull InstrumentHostNameContext ctx) {
        if (InstrumentHostName != null) {
            notifyListener(ctx, "duplicate INSTRUMENT_HOST_NAME encountered");
        }
        InstrumentHostName = ctx.words().getText();
    }

    public String getInstrumentHostName() {
        return InstrumentHostName;
    }

    @Override public void enterInstrumentId(@NotNull InstrumentIdContext ctx) {
        if (InstrumentId != null) {
            notifyListener(ctx, "duplicate INSTRUMENT_ID encountered");
        }
        InstrumentId = ctx.WORD().getText();
    }

    public String getInstrumentId() {
        return InstrumentId;
    }

    @Override public void enterTargetName(@NotNull TargetNameContext ctx) {
        if (TargetName != null) {
            notifyListener(ctx, "duplicate TARGET_NAME encountered");
        }
        TargetName = ctx.WORD().getText();
    }

    public String getTargetName() {
        return TargetName;
    }

    @Override public void enterMslCalibrationStandardName(@NotNull MslCalibrationStandardNameContext ctx) {
        if (MSLCalibrationStandardName != null) {
            notifyListener(ctx, "duplicate MSL:CALIBRATION_STANDARD_NAME encountered");
        }
        MSLCalibrationStandardName = ctx.words().getText();
    }

    public String getMSLCalibrationStandardName() {
        return MSLCalibrationStandardName;
    }

    @Override public void enterMissionPhaseName(@NotNull MissionPhaseNameContext ctx) {
        if (MissionPhaseName != null) {
            notifyListener(ctx, "duplicate MISSION_PHASE_NAME encountered");
        }
        MissionPhaseName = ctx.words().getText();
    }

    public String getMissionPhaseName() {
        return MissionPhaseName;
    }

    @Override public void enterProductCreationTime(@NotNull ProductCreationTimeContext ctx) {
        if (ProductCreationTime != null) {
            notifyListener(ctx, "duplicate PRODUCT_CREATION_TIME encountered");
        }
        ProductCreationTime = ctx.utcDate().getText();
    }

    public String getProductCreationTime() {
        return ProductCreationTime;
    }

    @Override public void enterStartTime(@NotNull StartTimeContext ctx) {
        if (StartTime != null) {
            notifyListener(ctx, "duplicate START_TIME encountered");
        }
        StartTime = ctx.utcDate().getText();
    }

    public String getStartTime() {
        return StartTime;
    }

    @Override public void enterStopTime(@NotNull StopTimeContext ctx) {
        if (StopTime != null) {
            notifyListener(ctx, "duplicate STOP_TIME encountered");
        }
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
        if (SpacecraftClockStartCount != null) {
            notifyListener(ctx, "duplicate SPACECRAFT_CLOCK_START_COUNT encountered");
        }
        SpacecraftClockStartCount = ctx.clockCount().getText();
    }

    public String getSpacecraftClockStartCount() {
        return SpacecraftClockStartCount;
    }

    @Override public void enterSpacecraftClockStopCount(@NotNull SpacecraftClockStopCountContext ctx) {
        if (SpacecraftClockStopCount != null) {
            notifyListener(ctx, "duplicate SPACECRAFT_CLOCK_STOP_COUNT encountered");
        }
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
        if (object == null) {
            object = new Object();
        } else {
            notifyListener(ctx, "illegal start of object; perhaps you forgot to END OBJECT");
        }
        object.Name = ctx.WORD().getText();
    }

    @Override public void enterObjectBytes(@NotNull ObjectBytesContext ctx) {
        if (object == null) {
            notifyListener(ctx, "unexpected BYTES outside of OBJECT");
        } else if (object.Bytes != null) {
            notifyListener(ctx, "duplicate BYTES encountered");
        }
        object.Bytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectRows(@NotNull ObjectRowsContext ctx) {
        if (object == null) {
            notifyListener(ctx, "unexpected ROWS outside of OBJECT");
        } else if (object.Rows != null) {
            notifyListener(ctx, "duplicate ROWS encountered");
        }
        object.Rows = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectRowBytes(@NotNull ObjectRowBytesContext ctx) {
        if (object == null) {
            notifyListener(ctx, "unexpected ROW_BYTES outside of OBJECT");
        } else if (object.RowBytes != null) {
            notifyListener(ctx, "duplicate ROW_BYTES encountered");
        }
        object.RowBytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectFields(@NotNull ObjectFieldsContext ctx) {
        if (object == null) {
            notifyListener(ctx, "unexpected FIELDS outside of OBJECT");
        } else if (object.Fields != null) {
            notifyListener(ctx, "duplicate FIELDS encountered");
        }
        object.Fields = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectFieldDelimiter(@NotNull ObjectFieldDelimiterContext ctx) {
        if (object == null) {
            notifyListener(ctx, "unexpected FIELD_DELIMITER outside of OBJECT");
        } else if (object.FieldDelimiter != null) {
            notifyListener(ctx, "duplicate FIELD_DELIMITER encountered");
        }
        object.FieldDelimiter = ctx.WORD().getText();
    }

    @Override public void enterObjectHeaderType(@NotNull ObjectHeaderTypeContext ctx) {
        if (object == null) {
            notifyListener(ctx, "unexpected HEADER_TYPE outside of OBJECT");
        } else if (object.HeaderType != null) {
            notifyListener(ctx, "duplicate HEADER_TYPE encountered");
        }
        object.HeaderType = ctx.word().getText();
    }

    @Override public void enterObjectDescription(@NotNull ObjectDescriptionContext ctx) {
        if (object == null) {
            notifyListener(ctx, "unexpected DESCRIPTION outside of OBJECT");
        } else if (object.Description != null) {
            notifyListener(ctx, "duplicate DESCRIPTION encountered");
        }
        object.Description = ctx.quoted().unquoted().getText();
    }

    @Override public void enterObjectStructure(@NotNull ObjectStructureContext ctx) {
        if (object == null) {
            notifyListener(ctx, "unexpected ^STRUCTURE outside of OBJECT");
        } else if (object.Structure != null) {
            notifyListener(ctx, "duplicate ^STRUCTURE encountered");
        }

        if (format != null) {
            try {
                object.Structure = Structure.parseFile(format);
            } catch (Exception e) {
                notifyListener(ctx, e);
            }
        } else {
            String filename = ctx.filename().getText().trim();
            try {
                object.Structure = Structure.parseResource(filename);
                return;
            } catch (Exception e) {
                notifyListener(ctx, e);
            }

            try {
                object.Structure = Structure.parseFile(filename);
                return;
            } catch (Exception e) {
                notifyListener(ctx, e);
            }

            try {
                object.Structure = Structure.parseFile(filename.toLowerCase());
                return;
            } catch (Exception e) {
                notifyListener(ctx, e);
            }
        }
    }

    @Override public void enterObjectEnd(@NotNull ObjectEndContext ctx) {
        if (object == null || object.Name == null) {
            notifyListener(ctx, "unexpected END_OBJECT outside of OBJECT");
        }
        object.End = ctx.WORD().getText();
        if (!object.End.equals(object.Name)) {
            String msg = String.format("END_OBJECT = \"%s\" has a different name than OBJECT = \"%s\"", object.End, object.Name);
            notifyListener(ctx, msg);
        }
    }

    public HashMap<String,Object> getObjects() {
        return Objects;
    }

    protected void notifyListener(ParserRuleContext ctx, Exception e) {
        if (listener != null) {
            listener.error(ctx, e);
        }
    }

    protected void notifyListener(ParserRuleContext ctx, String msg) {
        notifyListener(ctx, new Exception(msg));
    }

    public String getLogicalIdentifier() {
        return FilenameUtils.getBaseName(filename);
    }
}
