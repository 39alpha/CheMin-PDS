package org.thirtyninealpharesearch.chemin;

public class CheminException extends Exception {
    public CheminException() {
        super();
    }

    public CheminException(String message) {
        super(message);
    }

    public CheminException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheminException(Throwable cause) {
        super(cause);
    }
}
