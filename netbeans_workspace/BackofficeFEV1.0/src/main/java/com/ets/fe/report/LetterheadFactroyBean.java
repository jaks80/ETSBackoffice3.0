package com.ets.fe.report;

import com.ets.fe.Application;
import com.ets.fe.report.model.Letterhead;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Yusuf
 */
public class LetterheadFactroyBean {
   

    public static Collection getBeanCollection() {
        List<Letterhead> letterheads = new ArrayList<>();
        letterheads.add(Application.getLetterhead());
        return letterheads;
    }
   
}
