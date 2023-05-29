package org.thirtyninealpharesearch.chemin;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;

public class ErrorStrategy extends DefaultErrorStrategy {
    @Override protected void reportInputMismatch(Parser parser, InputMismatchException e) {
        Vocabulary v = parser.getVocabulary();
        String msg = "mismatched input " + getTokenErrorDisplay(e.getOffendingToken()) + " expecting " + e.getExpectedTokens().toString(v);
        parser.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }
}
