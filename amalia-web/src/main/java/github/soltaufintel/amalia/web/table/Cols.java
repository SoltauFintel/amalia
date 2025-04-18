package github.soltaufintel.amalia.web.table;

import java.util.ArrayList;

public class Cols extends ArrayList<Col> {
    
    public static Cols of(Col ...cols) {
        Cols ret = new Cols();
        for (Col i : cols) {
            if (!i.isRemove()) {
                ret.add(i);
            }
        }
        return ret;
    }
}
