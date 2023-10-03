package github.soltaufintel.amalia.web.templating.field;

public class Combobox extends AbstractField {
	private String items;
	/** >0: Listbox (multi-select) */
	private int size;
	private boolean multiple = false;
	
	public Combobox(int sort, String id, String label, int width, String items) {
		super(sort, id, label, width);
		this.items = items;
	}

    public Combobox(String id, String label, int width, String items) {
        this(0, id, label, width, items);
    }

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
}
