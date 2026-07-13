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

    public FormularComponent(FormularComponentTemplates templates) {
        this.templates = templates;
    }

    public FCInput input(String id, String label, int size) {
        var c = new FCInput(id, label, size);
        items.add(c);
        return c;
    }

    public FCCombobox combo(String id, String label, int size) {
        var c = new FCCombobox(id, label, size);
        items.add(c);
        return c;
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
}
