package github.soltaufintel.amalia.fg;

import java.util.ArrayList;
import java.util.List;

import com.github.template72.data.DataMap;

import github.soltaufintel.amalia.base.StringService;
import github.soltaufintel.amalia.web.action.Action;

public class FormularComponent extends Action {
    private final FormularComponentTemplates templates;
    private String html;
    private String action;
    private final List<FCItem> items = new ArrayList<>();
    private FCGroup group;

    public FormularComponent(FormularComponentTemplates templates) {
        this.templates = templates;
    }

    public FCInput input(String id, String label, int size) {
        return add(new FCInput(id, label, size));
    }

    public FCCombobox combo(String id, String label, int size) {
        return add(new FCCombobox(id, label, size));
    }
    
    // TODO checkbox
    // TODO textarea
    
    private <ITEM extends FCItem> ITEM add(ITEM item) {
        if (group == null) {
            item.setSoloGroup(true); // auto group
            items.add(item);
        } else {
            group.add(item);
        }
        return item;
    }

    @Override
    protected void execute() {
        String itemsHTML = "";
        for (FCItem item : items) {
            itemsHTML += item.render(templates);
        }
        var model = new DataMap();
        model.put("items", itemsHTML);
        String form = "";
        if (!StringService.isNullOrEmpty(action)) {
            form = " action=\"" + action + "\"";
        }
        model.put("form", form);
        html = templates.render("form", model);
    }

    @Override
    protected String render() {
        return html;
    }

    // start explicit group
    public void group() {
        if (group != null) {
            throw new RuntimeException("There's a group. Missing groupEnd()!");
        }
        group = new FCGroup();
    }
    
    // end explicit group
    public void groupEnd() {
        if (group == null) {
            throw new RuntimeException("There's no group");
        }
        items.add(group);
        group = null;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
