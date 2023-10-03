package github.soltaufintel.amalia.web.templating.field;

public class Textarea extends AbstractField {
	private int rows = 4;
	
	public Textarea(int sort, String id, String label, int width) {
		super(sort, id, label, width);
	}

    public Textarea(String id, String label, int width) {
        this(0, id, label, width);
    }

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
}
