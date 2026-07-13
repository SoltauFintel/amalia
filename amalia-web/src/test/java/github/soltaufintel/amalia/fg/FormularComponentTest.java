package github.soltaufintel.amalia.fg;

import java.util.List;

import org.junit.Test;

public class FormularComponentTest {
    private final boolean development = false; // TODO

    @Test
    public void test() {
        var templates = new FormularComponentTemplates(development);
        var f = new FormularComponent(templates);
        
        f.input("titel", "Titel", 4);
        f.input("age", "Alter", 4);
        f.combo("co1", "die combobox", 4).entries(List.of("A", "B"), "A");
        
        String output = f.run();
        
        System.out.println(output);
    }

    @Test
    public void twoInputIn1Line() {
        var templates = new FormularComponentTemplates(development);
        var f = new FormularComponent(templates);
        
        f.group();
        f.input("titel", "Titel", 4);
        f.input("age", "Alter", 4);
        f.groupEnd();
        
        String output = f.run();
        
        System.out.println(output);
    }
}
