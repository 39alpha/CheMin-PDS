package org.thirtyninealpharesearch.chemin.pds3;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import org.apache.commons.io.FilenameUtils;

import org.thirtyninealpharesearch.chemin.ErrorListener;
import org.thirtyninealpharesearch.chemin.ErrorStrategy;
import org.thirtyninealpharesearch.chemin.ParseException;
import org.thirtyninealpharesearch.chemin.SemanticException;
import org.thirtyninealpharesearch.chemin.Utils;
import org.thirtyninealpharesearch.chemin.pds3.IndexParser.*;

public class Index extends IndexBaseListener {
    public static Index parseFile(String path) throws IOException {
        ErrorListener listener = new ErrorListener(path);

        ANTLRFileStream in = new ANTLRFileStream(path);
        IndexLexer lexer = new IndexLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        IndexParser parser = new IndexParser(tokens);

        parser.setErrorHandler(new ErrorStrategy());
        parser.removeErrorListeners();
        parser.addErrorListener(listener);

        Index index = new Index(path, listener);

        ParseTreeWalker walker = new ParseTreeWalker();

        try {
            walker.walk(index, parser.index());
        } catch (Exception e) {
            listener.error(e);
        }

        if (listener.hasErrors()) {
            listener.reportErrors();
            throw new IOException(String.format("failed to parse \"%s\"", path));
        }

        return index;
    }

    public class Column {
        public Integer Number;
        public String Name;
        public String DataType;
        public Integer StartByte;
        public Integer Bytes;
        public String UnknownConstant;
        public String Description;

        public Integer getNumber() {
            return Number;
        }

        public String getName() {
            return Name;
        }

        public String getDataType() {
            return DataType;
        }

        public Integer getStartByte() {
            return StartByte;
        }

        public Integer getBytes() {
            return Bytes;
        }

        public String getUnknownConstant() {
            return UnknownConstant;
        }

        public String getDescription() {
            return Description;
        }

        public void validate() throws Exception {
            for (Field field : Column.class.getDeclaredFields()) {
                if (field.get(this) == null) {
                    throw new Exception(String.format("no %s field found for column", field.getName()));
                }
            }
        }
    }

    public class Table {
        public String InterchangeFormat;
        public Integer Rows;
        public Integer RowBytes;
        public Integer NumColumns;
        public String IndexType;
        public ArrayList<Column> Columns;

        public String getInterchangeFormat() {
            return InterchangeFormat;
        }

        public Integer getRows() {
            return Rows;
        }

        public Integer getRowBytes() {
            return RowBytes;
        }

        public Integer getNumColumns() {
            return NumColumns;
        }

        public String getIndexType() {
            return IndexType;
        }

        public ArrayList<Column> getColumns() {
            return Columns;
        }

        public void validate() throws Exception {
            String[] fields = {
                "InterchangeFormat",
                "Rows",
                "RowBytes",
                "NumColumns",
                "IndexType",
            };

            for (String field_name : fields) {
                Field field = Label.class.getField(field_name);
                if (field.get(this) == null) {
                    throw new Exception(String.format("no %s field found for field", field_name));
                }
            }

            if (Columns == null || Columns.size() == 0) {
                throw new Exception("table has no columns");
            } else if (Columns.size() != NumColumns) {
                throw new Exception(
                    String.format("expected %d columns in table, found %d",
                        NumColumns,
                        Columns.size()
                    )
                );
            }
            for (Column c : Columns) {
                c.validate();
            }

        }
    }

    public String path;

    public String PDSVersionId;
    public String RecordType;
    public Integer RecordBytes;
    public Integer FileRecords;
    public String IndexTable;
    public String VolumeId;
    public ArrayList<String> DataSetId;
    public String MissionName;
    public String InstrumentHostName;
    public String InstrumentHostId;
    public String InstrumentName;
    public String InstrumentId;
    public String TargetName;
    public Table Table;

    protected Object object;
    protected ErrorListener listener;

    public Index(String path, ErrorListener listener) {
        this.path = path;
        this.listener = listener;
    }

    public Index(String path) {
        this.path = path;
        this.listener = new ErrorListener(path);
    }

    public String getPath() {
        return path;
    }

    public String getFilename() {
        return FilenameUtils.getName(path);
    }

    @Override public void exitIndex(@NotNull IndexContext ctx) {
        // try {
        //     for (Field field : Column.class.getDeclaredFields()) {
        //         if (field.get(this) == null) {
        //             notifyListener(ctx, String.format("no %s field found for column", field.getName()));
        //         }
        //     }
        // } catch (IllegalAccessException e) {
        // }
        //
        // try {
        //     Table.validate();
        // } catch (Exception e) {
        //     notifyListener(ctx, e.getMessage());
        // }
    }

    @Override public void enterPdsVersionId(@NotNull PdsVersionIdContext ctx) {
        if (PDSVersionId != null) {
            notifyListener(ctx, "duplicate PDS_VERSION encountered");
        }
        if (ctx.word() != null) {
            PDSVersionId = ctx.word().getText();
        }
    }

    public String getPDSVersionId() {
        return PDSVersionId;
    }

    @Override public void enterRecordType(@NotNull RecordTypeContext ctx) {
        if (RecordType != null) {
            notifyListener(ctx, "duplicate RECORD_TYPE encountered");
        }
        if (ctx.word() != null) {
            RecordType = ctx.word().getText();
        }
    }

    public String getRecordType() {
        return RecordType;
    }

    @Override public void enterRecordBytes(@NotNull RecordBytesContext ctx) {
        if (RecordBytes != null) {
            notifyListener(ctx, "duplicate RECORD_BYTES encountered");
        }
        if (ctx.INUMBER() != null) {
            RecordBytes = Integer.parseInt(ctx.INUMBER().getText());
        }
    }

    public int getRecordBytes() {
        return RecordBytes;
    }

    @Override public void enterFileRecords(@NotNull FileRecordsContext ctx) {
        if (FileRecords != null) {
            notifyListener(ctx, "duplicate FILE_RECORDS encountered");
        }
        if (ctx.INUMBER() != null) {
            FileRecords = Integer.parseInt(ctx.INUMBER().getText());
        }
    }

    public int getFileRecords() {
        return FileRecords;
    }

    @Override public void enterIndexTable(@NotNull IndexTableContext ctx) {
        if (IndexTable != null) {
            notifyListener(ctx, "duplicate ^INDEX_TABLE encountered");
        }
        if (ctx.quoted() != null && ctx.quoted().unquoted() != null) {
            IndexTable = ctx.quoted().unquoted().getText();
        }
    }

    public String getIndexTable() {
        return IndexTable;
    }

    @Override public void enterVolumeId(@NotNull VolumeIdContext ctx) {
        if (VolumeId != null) {
            notifyListener(ctx, "duplicate VOLUME_ID encountered");
        }
        if (ctx.quoted() != null && ctx.quoted().unquoted() != null) {
            VolumeId = ctx.quoted().unquoted().getText();
        }
    }

    public String getVolumeId() {
        return VolumeId;
    }

    @Override public void enterDataSetId(@NotNull DataSetIdContext ctx) {
        if (DataSetId != null) {
            notifyListener(ctx, "duplicate DATA_SET_ID encountered");
        }
        DataSetId = new ArrayList<String>();
        if (ctx.identifierList() != null) {
            IdentifierEntriesContext idc = ctx.identifierList().identifierEntries();
            if (idc == null) {
                if (ctx.identifierList().identifierEntry() != null) {
                    IdentifierEntryContext entry = ctx.identifierList().identifierEntry();
                    if (entry.hyphenatedWord() != null) {
                        DataSetId.add(entry.hyphenatedWord().getText());
                    }
                }
            } else if (idc != null) {
                while (idc != null) {
                    String entry = idc.identifierEntry().hyphenatedWord().getText();
                    DataSetId.add(entry);

                    idc = idc.identifierEntries();
                }
            }
        }
    }

    public ArrayList<String> getDataSetId() {
        return DataSetId;
    }

    @Override public void enterMissionName(@NotNull MissionNameContext ctx) {
        if (MissionName != null) {
            notifyListener(ctx, "duplicate MISSION_NAME encountered");
        }
        if (ctx.quoted() != null && ctx.quoted().unquoted() != null) {
            MissionName = ctx.quoted().unquoted().getText();
        }
    }

    public String getMissionName() {
        return MissionName;
    }

    @Override public void enterInstrumentHostName(@NotNull InstrumentHostNameContext ctx) {
        if (InstrumentHostName != null) {
            notifyListener(ctx, "duplicate INSTRUMENT_HOST_NAME encountered");
        }
        if (ctx.quoted() != null && ctx.quoted().unquoted() != null) {
            InstrumentHostName = ctx.quoted().unquoted().getText();
        }
    }

    public String getInstrumentHostName() {
        return InstrumentHostName;
    }

    @Override public void enterInstrumentHostId(@NotNull InstrumentHostIdContext ctx) {
        if (InstrumentHostId != null) {
            notifyListener(ctx, "duplicate INSTRUMENT_HOST_ID encountered");
        }
        if (ctx.quoted() != null && ctx.quoted().unquoted() != null) {
            InstrumentHostId = ctx.quoted().unquoted().getText();
        }
    }

    public String getInstrumentHostId() {
        return InstrumentHostId;
    }

    @Override public void enterInstrumentName(@NotNull InstrumentNameContext ctx) {
        if (InstrumentName != null) {
            notifyListener(ctx, "duplicate INSTRUMENT_NAME encountered");
        }
        if (ctx.quoted() != null && ctx.quoted().unquoted() != null) {
            InstrumentName = ctx.quoted().unquoted().getText();
        }
    }

    public String getInstrumentName() {
        return InstrumentName;
    }

    @Override public void enterInstrumentId(@NotNull InstrumentIdContext ctx) {
        if (InstrumentId != null) {
            notifyListener(ctx, "duplicate INSTRUMENT_ID encountered");
        }
        if (ctx.quoted() != null && ctx.quoted().unquoted() != null) {
            InstrumentId = ctx.quoted().unquoted().getText();
        }
    }

    public String getInstrumentId() {
        return InstrumentId;
    }

    @Override public void enterTargetName(@NotNull TargetNameContext ctx) {
        if (TargetName != null) {
            notifyListener(ctx, "duplicate TARGET_NAME encountered");
        }
        if (ctx.quoted() != null && ctx.quoted().unquoted() != null) {
            TargetName = ctx.quoted().unquoted().getText();
        }
    }

    public String getTargetName() {
        return TargetName;
    }

    @Override public void exitTable(@NotNull TableContext ctx) {
        if (Table == null) {
            notifyListener(ctx, new ParseException("unexpected END_OBJECT"));
            return;
        }
    }

    @Override public void enterTableHeader(@NotNull TableHeaderContext ctx) {
        if (Table == null) {
            Table = new Table();
        } else {
            notifyListener(ctx, "illegal start of object; perhaps you forgot to END_OBJECT");
        }
    }

    @Override public void enterTableInterchangeFormat(@NotNull TableInterchangeFormatContext ctx) {
        if (Table == null) {
            notifyListener(ctx, "unexpected INTERCHANGE_FORMAT outside of OBJECT");
        } else if (Table.InterchangeFormat != null) {
            notifyListener(ctx, "duplicate INTERCHANGE_FORMAT encountered");
        }
        if (ctx.word() != null) {
            Table.InterchangeFormat = ctx.word().getText();
        }
    }

    @Override public void enterTableRows(@NotNull TableRowsContext ctx) {
        if (Table == null) {
            notifyListener(ctx, "unexpected ROWS outside of OBJECT");
        } else if (Table.Rows != null) {
            notifyListener(ctx, "duplicate ROWS encountered");
        }
        if (ctx.INUMBER() != null) {
            Table.Rows = Integer.parseInt(ctx.INUMBER().getText());
        }
    }

    @Override public void enterTableRowBytes(@NotNull TableRowBytesContext ctx) {
        if (Table == null) {
            notifyListener(ctx, "unexpected ROW_BYTES outside of OBJECT");
        } else if (Table.RowBytes != null) {
            notifyListener(ctx, "duplicate ROW_BYTES encountered");
        }
        if (ctx.INUMBER() != null) {
            Table.RowBytes = Integer.parseInt(ctx.INUMBER().getText());
        }
    }

    @Override public void enterTableColumns(@NotNull TableColumnsContext ctx) {
        if (Table == null) {
            notifyListener(ctx, "unexpected COLUMNS outside of OBJECT");
        } else if (Table.NumColumns != null) {
            notifyListener(ctx, "duplicate COLUMNS encountered");
        }
        if (ctx.INUMBER() != null) {
            Table.NumColumns = Integer.parseInt(ctx.INUMBER().getText());
        }
    }

    @Override public void enterTableIndexType(@NotNull TableIndexTypeContext ctx) {
        if (Table == null) {
            notifyListener(ctx, "unexpected INDEX_TYPE outside of OBJECT");
        } else if (Table.IndexType != null) {
            notifyListener(ctx, "duplicate INDEX_TYPE encountered");
        }
        if (ctx.word() != null) {
            Table.IndexType = ctx.word().getText();
        }
    }

    @Override public void enterTableEnd(@NotNull TableEndContext ctx) {
        if (Table == null) {
            notifyListener(ctx, "unexpected END_OBJECT outside of OBJECT");
        }
    }

    private Column column;

    @Override public void enterColumn(@NotNull ColumnContext ctx) {
        if (Table == null) {
            notifyListener(ctx, "unexpected OBJECT = COLUMN outside of OBJECT");
        } else if (column != null) {
            notifyListener(ctx, "unexpected OBJECT = COLUMN; did you forget to END_OBJECT = COLUMN?");
        }

        column = new Column();
    }

    @Override public void exitColumn(@NotNull ColumnContext ctx) {
        if (Table == null) {
            notifyListener(ctx, "unexpected END_OBJECT = COLUMN outside of OBJECT");
        }
        if (column == null) {
            notifyListener(ctx, "unexpected END_OBJECT = COLUMN; did you forget OBJECT = COLUMN?");
        }

        if (Table.Columns == null) {
            Table.Columns = new ArrayList<Column>();
        }
        Table.Columns.add(column);
        column = null;
    }

    @Override public void enterColumnNumber(@NotNull ColumnNumberContext ctx) {
        if (column == null) {
            notifyListener(ctx, "unexpected COLUMN_NUMBER outside of OBJECT = COLUMN");
            return;
        } else if (column.Number != null) {
            notifyListener(ctx, "duplicate COLUMN_NUMBER encountered");
        }
        if (ctx.INUMBER() != null) {
            column.Number = Integer.parseInt(ctx.INUMBER().getText());
        }
    }

    @Override public void enterColumnName(@NotNull ColumnNameContext ctx) {
        if (column == null) {
            notifyListener(ctx, "unexpected NAME outside of OBJECT = COLUMN");
            return;
        } else if (column.Name != null) {
            notifyListener(ctx, "duplicate NAME encountered");
        }
        if (ctx.columnNameHack() != null) {
            column.Name = ctx.columnNameHack().getText();
        }
    }

    @Override public void enterColumnDataType(@NotNull ColumnDataTypeContext ctx) {
        if (column == null) {
            notifyListener(ctx, "unexpected DATA_TYPE outside of OBJECT = COLUMN");
            return;
        } else if (column.DataType != null) {
            notifyListener(ctx, "duplicate DATA_TYPE encountered");
        }
        if (ctx.word() != null) {
            column.DataType = ctx.word().getText();
        }
    }

    @Override public void enterColumnStartByte(@NotNull ColumnStartByteContext ctx) {
        if (column == null) {
            notifyListener(ctx, "unexpected START_BYTE outside of OBJECT = COLUMN");
            return;
        } else if (column.StartByte != null) {
            notifyListener(ctx, "duplicate START_BYTE encountered");
        }
        if (ctx.INUMBER() != null) {
            column.StartByte = Integer.parseInt(ctx.INUMBER().getText());
        }
    }

    @Override public void enterColumnBytes(@NotNull ColumnBytesContext ctx) {
        if (column == null) {
            notifyListener(ctx, "unexpected BYTES outside of OBJECT = COLUMN");
            return;
        } else if (column.Bytes != null) {
            notifyListener(ctx, "duplicate BYTES encountered");
        }
        if (ctx.INUMBER() != null) {
            column.Bytes = Integer.parseInt(ctx.INUMBER().getText());
        }
    }

    @Override public void enterColumnUnknownConstant(@NotNull ColumnUnknownConstantContext ctx) {
        if (column == null) {
            notifyListener(ctx, "unexpected UNKNOWN_CONSTANT outside of OBJECT = COLUMN");
            return;
        } else if (column.UnknownConstant != null) {
            notifyListener(ctx, "duplicate UNKNOWN_CONSTANT encountered");
        }
        if (ctx.notws() != null) {
            column.UnknownConstant = ctx.notws().getText();
        }
    }

    @Override public void enterColumnDescription(@NotNull ColumnDescriptionContext ctx) {
        if (column == null) {
            notifyListener(ctx, "unexpected DESCRIPTION outside of OBJECT = COLUMN");
            return;
        } else if (column.Description != null) {
            notifyListener(ctx, "duplicate DESCRIPTION encountered");
        }
        if (ctx.quoted() != null && ctx.quoted().unquoted() != null) {
            column.Description = ctx.quoted().unquoted().getText();
        }
    }

    public Table getTable() {
        return Table;
    }

    protected void notifyListener(ParserRuleContext ctx, Exception e) {
        notifyListener(ctx, e.getMessage());
    }

    protected void notifyListener(ParserRuleContext ctx, String msg) {
        if (listener != null) {
            listener.error(ctx, msg);
        }
    }

    // public String getModificationDate() throws java.text.ParseException {
    //     if (isRevision()) {
    //         Pattern pattern = Pattern.compile("\\s*(\\d{4}-\\d{2}-\\d{2})\\s+");
    //         Matcher matcher = pattern.matcher(LabelRevisionNote);
    //         if (matcher.find()) {
    //             return matcher.group(1);
    //         } else if (ProductCreationTime != null) {
    //             return getProductCreationDate();
    //         }
    //     }
    //     return null;
    // }
    //
    // public String getModificationDescription() {
    //     if (isRevision()) {
    //         Pattern pattern = Pattern.compile("\\s*\\d{4}-\\d{2}-\\d{2}\\s+(.*)", Pattern.DOTALL);
    //         Matcher matcher = pattern.matcher(LabelRevisionNote);
    //         if (matcher.find()) {
    //             return matcher.group(1);
    //         }
    //     }
    //     return null;
    // }
}
