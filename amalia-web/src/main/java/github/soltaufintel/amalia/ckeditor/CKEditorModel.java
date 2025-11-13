package github.soltaufintel.amalia.ckeditor;

// id ist 1 String Wert
public abstract class CKEditorModel extends AbstractCKEditorModel {
    private String id;
    
    public CKEditorModel() {
    }
    
    public CKEditorModel(String type, int version, String html, String id) {
        super(version, html);
        this.id = id;
        setType(type);
    }
    
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
