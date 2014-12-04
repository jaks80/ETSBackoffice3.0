package com.ets.fe.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Yusuf
 */
public class CheckInput extends PlainDocument {

    public static final String FLOAT = "0123456789.";

    protected String acceptedChars = null;

    protected boolean negativeAccepted = true;

    public CheckInput() {
        this(FLOAT);
    }

    public CheckInput(String acceptedchars) {
        acceptedChars = acceptedchars;
    }

    public void setNegativeAccepted(boolean negativeaccepted) {
        if (acceptedChars.equals(FLOAT)) {
            negativeAccepted = negativeaccepted;
            acceptedChars += "-";
        }
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }

        for (int i = 0; i < str.length(); i++) {
            if (acceptedChars.indexOf(str.valueOf(str.charAt(i))) == -1) {
                return;
            }
        }

        if (acceptedChars.equals(FLOAT) || (acceptedChars.equals(FLOAT + "-") && negativeAccepted)) {
            if (str.indexOf(".") != -1) {
                if (getText(0, getLength()).indexOf(".") != -1) {
                    return;
                }
            }
        }

        if (negativeAccepted && str.indexOf("-") != -1) {
            if (str.indexOf("-") != 0 || offset != 0) {
                return;
            }
        }

        super.insertString(offset, str, attr);
    }
}
