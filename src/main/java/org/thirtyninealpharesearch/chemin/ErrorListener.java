package org.thirtyninealpharesearch.chemin;

import java.util.ArrayList;
import org.antlr.v4.runtime.*;

public class ErrorListener extends ConsoleErrorListener {
    private class Error {
        public String filename;
        public int line;
        public int charPositionInLine;
        public String msg;
        public RecognitionException err;

        public Error(String filename, int line, int charPositionInLine, String msg, RecognitionException err) {
            this.filename = filename;
            this.line = line;
            this.charPositionInLine = charPositionInLine;
            this.msg = msg;
            this.err = err;
        }

        public String toString() {
            return String.format("%s:%d:%d\t%s", filename, line, charPositionInLine, msg);
        }
    }

    protected String filename;
    protected ArrayList<Error> errors;

    public ErrorListener(String filename) {
        super();
        this.filename = filename;
        errors = new ArrayList<Error>();
    }

    @Override public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        errors.add(new Error(this.filename, line, charPositionInLine, msg, e));
    }

    public void reportErrors() {
        if (errors != null && errors.size() != 0) {
            System.err.printf("Failed to parse file:\n", filename);
            for (Error error : errors) {
                System.err.println("  " + error.toString());
            }
        }
    }
}
