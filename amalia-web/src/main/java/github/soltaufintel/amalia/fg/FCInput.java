package github.soltaufintel.amalia.fg;

import com.github.template72.data.DataMap;

public class FCInput extends FCBaseItem {
    private final int width;
    
    public FCInput(String id, String label, int width) {
        super(id, label);
        this.width = width;
    }

    @Override
    public void fill(DataMap model) {
        model.putInt("width", width);
    }
}
