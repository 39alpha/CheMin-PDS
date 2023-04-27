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
        public Integer field_count;
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

        public Integer getFieldCount() {
            return field_count;
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

    @Override public void enterPdsversion(@NotNull PdsversionContext ctx) {
        PDSVersionId = ctx.word().getText();
    }

    public String getPDSVersionId() {
        return PDSVersionId;
    }

    @Override public void enterRecordtype(@NotNull RecordtypeContext ctx) {
        RecordType = ctx.word().getText();
    }

    public String getRecordType() {
        return RecordType;
    }

    @Override public void enterRecordbytes(@NotNull RecordbytesContext ctx) {
        RecordBytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    public int getRecordBytes() {
        return RecordBytes;
    }

    @Override public void enterFilerecords(@NotNull FilerecordsContext ctx) {
        FileRecords = Integer.parseInt(ctx.INUMBER().getText());
    }

    public int getFileRecords() {
        return FileRecords;
    }

    @Override public void enterHeader(@NotNull HeaderContext ctx) {
        String filename = ctx.filetuple().filename().getText();
        int index = Integer.parseInt(ctx.filetuple().INUMBER().getText());
        Header = new LinkEntry(filename, index);
    }

    public LinkEntry getHeader() {
        return Header;
    }

    @Override public void enterSpreadsheet(@NotNull SpreadsheetContext ctx) {
        String filename = ctx.filetuple().filename().getText();
        int index = Integer.parseInt(ctx.filetuple().INUMBER().getText());
        Spreadsheet = new LinkEntry(filename, index);
    }

    public LinkEntry getSpreadsheet() {
        return Spreadsheet;
    }

    @Override public void enterDatasetid(@NotNull DatasetidContext ctx) {
        DataSetId = ctx.hyphenatedword().getText();
    }

    public String getDataSetId() {
        return DataSetId;
    }

    @Override public void enterProductid(@NotNull ProductidContext ctx) {
        ProductId = ctx.word().getText();
    }

    public String getProductId() {
        return ProductId;
    }

    @Override public void enterProductversionid(@NotNull ProductversionidContext ctx) {
        ProductVersionId = ctx.VERSION().getText();
    }

    public String getProductVersionId() {
        return ProductVersionId;
    }

    @Override public void enterReleaseid(@NotNull ReleaseidContext ctx) {
        ReleaseId = ctx.INUMBER().getText();
    }

    public String getReleaseId() {
        return ReleaseId;
    }

    @Override public void enterSourceproductid(@NotNull SourceproductidContext ctx) {
        SourceProductId = new ArrayList<String>();
        IdentifierentriesContext idc = ctx.identifierlist().identifierentries();
        while (idc != null) {
            String entry = idc.identifierentry().word().getText();
            SourceProductId.add(entry);

            idc = idc.identifierentries();
        }
    }

    public ArrayList<String> getSourceProductId() {
        return SourceProductId;
    }

    @Override public void enterProducttype(@NotNull ProducttypeContext ctx) {
        ProductType = ctx.WORD().getText();
    }

    public String getProductType() {
        return ProductType;
    }

    @Override public void enterInstrumenthostid(@NotNull InstrumenthostidContext ctx) {
        InstrumentHostId = ctx.WORD().getText();
    }

    public String getInstrumentHostId() {
        return InstrumentHostId;
    }

    @Override public void enterInstrumenthostname(@NotNull InstrumenthostnameContext ctx) {
        InstrumentHostName = ctx.words().getText();
    }

    public String getInstrumentHostName() {
        return InstrumentHostName;
    }

    @Override public void enterInstrumentid(@NotNull InstrumentidContext ctx) {
        InstrumentId = ctx.WORD().getText();
    }

    public String getInstrumentId() {
        return InstrumentId;
    }

    @Override public void enterTargetname(@NotNull TargetnameContext ctx) {
        TargetName = ctx.WORD().getText();
    }

    public String getTargetName() {
        return TargetName;
    }

    @Override public void enterMslcalibrationstandardname(@NotNull MslcalibrationstandardnameContext ctx) {
        MSLCalibrationStandardName = ctx.words().getText();
    }

    public String getMSLCalibrationStandardName() {
        return MSLCalibrationStandardName;
    }

    @Override public void enterMissionphasename(@NotNull MissionphasenameContext ctx) {
        MissionPhaseName = ctx.words().getText();
    }

    public String getMissionPhaseName() {
        return MissionPhaseName;
    }

    @Override public void enterProductcreationtime(@NotNull ProductcreationtimeContext ctx) {
        ProductCreationTime = ctx.utcdate().getText();
    }

    public String getProductCreationTime() {
        return ProductCreationTime;
    }

    @Override public void enterStarttime(@NotNull StarttimeContext ctx) {
        StartTime = ctx.utcdate().getText();
    }

    public String getStartTime() {
        return StartTime;
    }

    @Override public void enterStoptime(@NotNull StoptimeContext ctx) {
        OptionalutcdateContext c = ctx.optionalutcdate();
        if (c.utcdate() != null) {
            StopTime = c.getText();
        } else {
            StopTime = "UNK";
        }
    }

    public String getStopTime() {
        return StopTime;
    }

    @Override public void enterSpacecraftclockstartcount(@NotNull SpacecraftclockstartcountContext ctx) {
        SpacecraftClockStartCount = ctx.clockcount().getText();
    }

    public String getSpacecraftClockStartCount() {
        return SpacecraftClockStartCount;
    }

    @Override public void enterSpacecraftclockstopcount(@NotNull SpacecraftclockstopcountContext ctx) {
        OptionalclockcountContext c = ctx.optionalclockcount();
        if (c.clockcount() != null) {
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

    @Override public void enterObjectheader(@NotNull ObjectheaderContext ctx) {
        lastObject().name = ctx.WORD().getText();
    }

    @Override public void enterObjectbytes(@NotNull ObjectbytesContext ctx) {
        lastObject().bytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectrows(@NotNull ObjectrowsContext ctx) {
        lastObject().rows = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectrowbytes(@NotNull ObjectrowbytesContext ctx) {
        lastObject().row_bytes = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectfieldcount(@NotNull ObjectfieldcountContext ctx) {
        lastObject().field_count = Integer.parseInt(ctx.INUMBER().getText());
    }

    @Override public void enterObjectfielddelimiter(@NotNull ObjectfielddelimiterContext ctx) {
        lastObject().field_delimiter = ctx.WORD().getText();
    }

    @Override public void enterObjectheadertype(@NotNull ObjectheadertypeContext ctx) {
        lastObject().header_type = ctx.word().getText();
    }

    @Override public void enterObjectdescription(@NotNull ObjectdescriptionContext ctx) {
        lastObject().description = ctx.quoted().notquoted().getText();
    }

    @Override public void enterObjectend(@NotNull ObjectendContext ctx) {
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
