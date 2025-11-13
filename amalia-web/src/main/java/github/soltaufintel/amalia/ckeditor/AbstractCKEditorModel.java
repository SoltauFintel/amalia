package github.soltaufintel.amalia.ckeditor;

import java.util.ArrayList;
import java.util.List;

import github.soltaufintel.amalia.base.IdGenerator;
import github.soltaufintel.amalia.ckeditor.image.CKEditorImage;

/**
 * Text and images for CKEditor
 * 
 * Hält die Daten und ist gleichzeitig für die Persistenz zuständig.
 */
public abstract class AbstractCKEditorModel {
    private String type = "seite";
    private String html;
    private final List<CKEditorImage> images = new ArrayList<>();
    private int version;
    
    public AbstractCKEditorModel() {
    }
    
    public AbstractCKEditorModel(int version, String html) {
        this.version = version;
        this.html = html == null ? "" : html;
    }
    
    public abstract String getId();
    
    public String getHTML() {
        return html == null ? "" : html;
    }
    
    public void setHTML(String html) {
        this.html = html;
        setVersion(getVersion() + 1);
        saveHTML(html, getVersion());
    }
    
    protected abstract void saveHTML(String html, int version);

    public List<CKEditorImage> getImages() {
        return images;
    }
    
    public void addImage(CKEditorImage image) {
        makeUniqueFilename(image);
        saveImage(image);
        images.add(image);
    }

    protected void makeUniqueFilename(CKEditorImage image) {
        String filename = image.getFilename();
        int o = filename.lastIndexOf("/");
        if (o >= 0) {
            filename = filename.substring(o + 1);
        }
        if (filename.isBlank()) {
            filename = "image.png";
        }
        for (CKEditorImage i : getImages()) {
            if (i.getFilename().equals(filename)) { // Name schon vergeben!
                o = filename.lastIndexOf(".");
                String newFilename;
                if (o >= 0) {
                    newFilename = filename.substring(0, o) + "-" + IdGenerator.createId6() + filename.substring(o);
                } else {
                    newFilename = filename + "-" + IdGenerator.createId6();
                }
                if (images.stream().anyMatch(j -> j.getFilename().equals(newFilename))) {
                    throw new RuntimeException("Image filename already in use. Choose another filename!");
                }
                image.setFilename(newFilename);
                return;
            }
        }
        if (!image.getFilename().equals(filename)) {
            image.setFilename(filename);
        }
    }
    
    protected abstract void saveImage(CKEditorImage image);

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int vh() {
        return 320; // Optimierung der CKEditor Höhe
    }
}
