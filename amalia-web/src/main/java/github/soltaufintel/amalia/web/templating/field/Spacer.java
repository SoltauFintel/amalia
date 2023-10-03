package github.soltaufintel.amalia.web.templating.field;

public class Spacer extends AbstractField {

	public Spacer(int sort, int width) {
		super(sort, "", "", width);
	}

	public Spacer(int width) {
        this(0, width);
    }
}
