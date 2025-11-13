package github.soltaufintel.amalia.ckeditor.image;

public class CKEditorImage {
    private String filename;
    private byte[] image;

    public CKEditorImage() {
    }

    public CKEditorImage(String filename, byte[] image) {
        this.filename = filename;
        this.image = image;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
