package github.soltaufintel.amalia.fg;

import java.util.ArrayList;
import java.util.List;

public class FCGroup implements FCItem {
    private final List<FCItem> items = new ArrayList<>();

    public void add(FCItem item) {
        items.add(item);
    }

    @Override
    public String render(FormularComponentTemplates templates) {
        StringBuilder sb = new StringBuilder();
        for (FCItem item : items) {
            sb.append(item.render(templates));
        }
        return surround(sb.toString());
    }

    public static String surround(String html) {
        return "\n<div class=\"form-group\">\n" + html + "</div>\n";
    }

    @Override
    public void setSoloGroup(boolean v) {
    }
}
