package org.thirtyninealpharesearch.chemin;

public class SemanticException extends CheminException {
    public SemanticException() {
        super();
    }

    public SemanticException(String message) {
        super(message);
    }

    public SemanticException(String message, Throwable cause) {
        super(message, cause);
    }

    public SemanticException(Throwable cause) {
        super(cause);
    }
}
