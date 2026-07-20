package github.soltaufintel.amalia.fg;

import java.util.ArrayList;
import java.util.List;

import com.github.template72.data.DataMap;

import github.soltaufintel.amalia.web.action.Escaper;

public abstract class FCBaseItem implements FCItem {
    private final List<FCItem> items = new ArrayList<>();
    private final String id;
    private String label;
    private boolean soloGroup = false;
    private String hint = null;
    
    public FCBaseItem(String id, String label) {
        this.id = id;
        this.label = label;
    }
    
//    @Override
    public List<FCItem> getItems() {
        return items;
    }
    
    @Override
    public String render(FormularComponentTemplates templates) {
        DataMap model = new DataMap();
        model.putInt("indent", templates.getIndent());
        model.put("id", esc(id));
        model.put("label", esc(label));
        model.put("attrs", "");
        model.put("hint", esc(hint));
        fill(model);
        String html = templates.render(getTemplateFile(), model);
        if (soloGroup) {
            html = FCGroup.surround(html);
        }
        return html;
    }
    
    protected String getTemplateFile() {
        return getClass().getSimpleName();
    }

    protected abstract void fill(DataMap model);

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }
    
    protected final String esc(String text) {
        return Escaper.esc(text);
    }
    
    @Override
    public void setSoloGroup(boolean v) {
        soloGroup = v;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
