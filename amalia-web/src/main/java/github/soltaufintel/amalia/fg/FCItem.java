package github.soltaufintel.amalia.fg;

import java.util.List;

/**
 * Formular component item
 */
public interface FCItem {

    String render(FormularComponentTemplates templates);
    
    List<FCItem> getItems();
}
