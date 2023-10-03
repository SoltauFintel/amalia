package github.soltaufintel.amalia.web.templating.field;

public class Checkbox extends AbstractField {

	public Checkbox(int sort, String id, String label) {
		super(sort, id, label, 3);
	}

	public Checkbox(String id, String label) {
        super(0, id, label, 3);
    }
}
