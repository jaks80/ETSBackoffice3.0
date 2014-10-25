package etsbackoffice.businesslogic;

import javax.swing.text.*;
import java.awt.Toolkit;

public class DocumentSizeFilterLowerCase extends DocumentFilter{
int maxCharacters;
    boolean DEBUG = false;

    public DocumentSizeFilterLowerCase(int maxChars) {
        maxCharacters = maxChars;
    }

    @Override
    public void insertString(FilterBypass fb, int offs,String str, AttributeSet a)
        throws BadLocationException {
        if (DEBUG) {
            System.out.println("in DocumentSizeFilter's insertString method");
        }
       if ((fb.getDocument().getLength() + str.length()) <= maxCharacters){
         super.insertString(fb, offs, str, a);
       }
        else
            Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void replace(FilterBypass fb, int offs,
                        int length,
                        String str, AttributeSet a)
        throws BadLocationException {
        if (DEBUG) {
            System.out.println("in DocumentSizeFilter's replace method");
        }
        if ((fb.getDocument().getLength() + str.length()
             - length) <= maxCharacters)
            super.replace(fb, offs, length, str.toLowerCase(), a);
        else
            Toolkit.getDefaultToolkit().beep();
    }

}
