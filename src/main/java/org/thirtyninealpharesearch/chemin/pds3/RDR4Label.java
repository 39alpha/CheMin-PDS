package org.thirtyninealpharesearch.chemin.pds3;

import java.util.ArrayList;
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

    public class LinkEntry {
        public String filename;
        public int index;

        public LinkEntry(String filename, int index) {
            this.filename = filename;
            this.index = index;
        }

        public String getFilename() {
            return filename;
        }

        public int getIndex() {
            return index;
        }
    }

    public class Object {
        public String name;
        public String end;
        public Integer bytes;
        public Integer rows;
        public Integer row_bytes;
        public Integer fields;
        public String field_delimiter;
        public String header_type;
        public String description;

        public String getName() {
            return name;
        }

        public String getEnd() {
            return end;
        }

        public Integer getBytes() {
            return bytes;
        }

        public Integer getRows() {
            return rows;
        }

        public Integer getRowBytes() {
            return row_bytes;
        }

        public Integer getFields() {
            return fields;
        }

        public String getFieldDelimiter() {
            return field_delimiter;
        }

        public String getHeaderType() {
            return header_type;
        }

        public String getDescription() {
            return description;
        }
    }

    public String filename;
    public String PDSVersionId;
    public String RecordType;
    public Integer RecordBytes;
    public Integer FileRecords;
    public LinkEntry Header;
    public LinkEntry Spreadsheet;
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
    public ArrayList<Object> Objects;

    public RDR4Label(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
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

    @Override public void enterHeader(@NotNull HeaderContext ctx) {
        String filename = ctx.fileTuple().filename().getText();
        int index = Integer.parseInt(ctx.fileTuple().INUMBER().getText());
        Header = new LinkEntry(filename, index);
    }

    public LinkEntry getHeader() {
        return Header;
    }

    @Override public void enterSpreadsheet(@NotNull SpreadsheetContext ctx) {
        String filename = ctx.fileTuple().filename().getText();
        int index = Integer.parseInt(ctx.fileTuple().INUMBER().getText());
        Spreadsheet = new LinkEntry(filename, index);
    }

    public LinkEntry getSpreadsheet() {
        return Spreadsheet;
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
        if (Objects == null) {
            Objects = new ArrayList<Object>();
        }
        Objects.add(new Object());
    }

    @Override public void enterObjectHeader(@NotNull ObjectHeaderContext ctx) {
        lastObject().name = ctx.WORD().getText();
    }

    @Override public void enterObjectBytes(@NotNull ObjectBytesContext ctx) {
        lastObject().bytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectRows(@NotNull ObjectRowsContext ctx) {
        lastObject().rows = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectRowBytes(@NotNull ObjectRowBytesContext ctx) {
        lastObject().row_bytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectFields(@NotNull ObjectFieldsContext ctx) {
        lastObject().fields = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectFieldDelimiter(@NotNull ObjectFieldDelimiterContext ctx) {
        lastObject().field_delimiter = ctx.WORD().getText();
    }

    @Override public void enterObjectHeaderType(@NotNull ObjectHeaderTypeContext ctx) {
        lastObject().header_type = ctx.word().getText();
    }

    @Override public void enterObjectDescription(@NotNull ObjectDescriptionContext ctx) {
        lastObject().description = ctx.quoted().notquoted().getText();
    }

    @Override public void enterObjectEnd(@NotNull ObjectEndContext ctx) {
        lastObject().end = ctx.WORD().getText();
    }

    private Object lastObject() {
        if (Objects == null || Objects.size() == 0) {
            return null;
        }
        return Objects.get(Objects.size() - 1);
    }

    public ArrayList<Object> getObjects() {
        return Objects;
    }
}
