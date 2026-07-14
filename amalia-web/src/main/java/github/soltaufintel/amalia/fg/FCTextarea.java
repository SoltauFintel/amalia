package github.soltaufintel.amalia.fg;

public class FCTextarea extends FCInput {
    private final int rows;
    
    public FCTextarea(String id, String label, int width, int rows) {
        super(id, label, width);
        this.rows = rows;
    }

    public int getRows() {
        return rows;
    }
}
