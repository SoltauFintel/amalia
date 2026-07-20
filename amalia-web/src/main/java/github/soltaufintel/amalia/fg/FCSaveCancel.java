package github.soltaufintel.amalia.fg;

import com.github.template72.data.DataMap;

public class FCSaveCancel extends FCBaseItem {
    private final String cancellink;
    
    public FCSaveCancel(String cancellink) {
        super("", "");
        this.cancellink = cancellink;
    }

    @Override
    protected void fill(DataMap model) {
        model.put("save", "Speichern");
        model.put("cancel", "Abbruch");
        model.put("cancellink", cancellink);
    }
}
