package github.soltaufintel.amalia.fg;

/**
 * Formular component item
 */
public interface FCItem {

    String render(FormularComponentTemplates templates);

    void setSoloGroup(boolean v);
}
