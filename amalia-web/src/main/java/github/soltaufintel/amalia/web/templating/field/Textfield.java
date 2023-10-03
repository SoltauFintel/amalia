package github.soltaufintel.amalia.web.templating.field;

public class Textfield extends AbstractField {

	public Textfield(int sort, String id, String label, int width) {
		super(sort, id, label, width);
	}

	public Textfield(String id, String label, int width) {
        this(0, id, label, width);
    }
}
