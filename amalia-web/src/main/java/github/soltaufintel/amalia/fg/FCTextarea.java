package github.soltaufintel.amalia.fg;

import com.github.template72.data.DataMap;

public class FCTextarea extends FCInput {
    private final int rows;
    private String value;
    
    public FCTextarea(String id, String label, int width, int rows) {
        super(id, label, width);
        this.rows = rows;
    }

    public int getRows() {
        return rows;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public void fill(DataMap model) {
        super.fill(model);
        model.putInt("rows", rows);
        model.put("value", value);
    }
}
