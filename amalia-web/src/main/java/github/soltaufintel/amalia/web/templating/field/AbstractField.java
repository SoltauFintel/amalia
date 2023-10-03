package github.soltaufintel.amalia.web.templating.field;

import java.util.List;

import com.github.template72.data.DataCondition;
import com.github.template72.data.DataMap;
import com.github.template72.data.IDataItem;

import github.soltaufintel.amalia.web.templating.ColumnFormularGenerator;

public abstract class AbstractField {
	private int sort;
	private String id;
	private String label;
	private int width;
	
	public AbstractField(int sort, String id, String label, int width) {
		this.sort = sort;
		this.id = id;
		this.label = label;
		this.width = width;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public static void sort(List<AbstractField> fields) {
	    fields.sort((a, b) -> a.getSort() - b.getSort());
	}
	
    public static void generate(List<AbstractField> fields, boolean edit, DataMap model, ColumnFormularGenerator gen) {
        boolean first = true;
        for (AbstractField field : fields) {
            if (field instanceof Textarea a) {
                gen.textarea(field.getId(), field.getLabel(), field.getWidth(), a.getRows(), first, edit);
            } else if (field instanceof Textfield) {
                gen.textfield(field.getId(), field.getLabel(), field.getWidth(), first, edit);
            } else if (field instanceof Combobox c) {
                if (c.getSize() > 0) {
                    gen.listbox(field.getId(), field.getLabel(), field.getWidth(), c.getItems(), first, c.getSize(), c.isMultiple());
                } else {
                    gen.combobox(field.getId(), field.getLabel(), field.getWidth(), c.getItems(), first);
                }
            } else if (field instanceof Checkbox) {
                gen.checkbox(field.getId(), field.getLabel(), field.getWidth(), first, isChecked(edit, model, field));
            } else if (field instanceof Spacer) {
                gen.spacer(field.getWidth());
            } else if (field instanceof Empty) {
                gen.empty(field.getWidth());
            }
            first = false;
        }
    }
    
    public static boolean isChecked(boolean edit, DataMap model, AbstractField field) {
        if (edit) {
            IDataItem f = model.get(field.getId());
            if (f instanceof DataCondition c) {
                return c.isTrue();
            }
        }
        return false;
    }
}
