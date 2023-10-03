package github.soltaufintel.amalia.web.templating.field;

public class Empty extends AbstractField {

	public Empty(int sort, int width) {
		super(sort, "", "", width);
	}

	public Empty(int width) {
        this(0, width);
    }
}
