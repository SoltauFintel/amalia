package github.soltaufintel.amalia.fg;

import java.util.Collection;
import java.util.List;

import com.github.template72.data.DataMap;

import github.soltaufintel.amalia.web.action.ComboboxCreator;
import github.soltaufintel.amalia.web.action.IdAndLabel;

public class FCCombobox extends FCBaseItem {
    private final int width;
    private boolean withEmptyItem = false;
    private ComboboxCreator1 cc;
    
    public FCCombobox(String id, String label, int width) {
        super(id, label);
        this.width = width;
    }

    public void entries(Collection<String> entries, String selected) {
        cc = model -> ComboboxCreator.combobox("list", entries, selected, withEmptyItem, model);
    }
    
    public void entries(Collection<String> entries, Collection<String> selected) {
        cc = model -> ComboboxCreator.combobox("list", entries, selected, withEmptyItem, model);
    }
    
    public void idAndLabelEntries(Collection<IdAndLabel> entries, String selected) {
        cc = model -> ComboboxCreator.combobox_idAndLabel("list", entries, selected, withEmptyItem, model);
    }

    /**
     * @param entries -
     * @param selected ID list
     */
    public void idAndLabelEntries(Collection<IdAndLabel> entries, List<String> selected) {
        cc = model -> ComboboxCreator.combobox_idAndLabel("list", entries, selected, withEmptyItem, model);
    }
    
    public interface ComboboxCreator1 {
        void render(DataMap model);
    }

    public boolean isWithEmptyItem() {
        return withEmptyItem;
    }

    public void setWithEmptyItem(boolean withEmptyItem) {
        this.withEmptyItem = withEmptyItem;
    }

    @Override
    protected void fill(DataMap model) {
        model.putInt("width", width);
        cc.render(model);
    }
    
    @Override
    protected String getTemplateFile() {
        return super.getTemplateFile() + "1";
    }
}
