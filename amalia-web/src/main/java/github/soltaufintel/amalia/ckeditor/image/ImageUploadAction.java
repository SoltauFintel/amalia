package github.soltaufintel.amalia.ckeditor.image;

import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;

import github.soltaufintel.amalia.web.image.AbstractImageUpload;

public class ImageUploadAction extends AbstractImageUpload {
    public static ImageService imageService;
    private String imageUrl;
    /* TODO Amalia AbstractImageUpload:
     * part.getSize() > limit -> Exception
     */
    
    @Override
    protected void saveImage(InputStream content, String filename) throws IOException {
        imageUrl = imageService.saveImage(ctx, content, filename);
        ctx.contentTypeJSON(); // TODO <>Amalia ctx.res.type("application/json");
    }
    
    @Override
    protected String render() {
        return new Gson().toJson(new SuccessJSON(imageUrl));
    }
    
    public static record SuccessJSON(String url) {}
    
    @Override
    protected String getId() {
        return "upload";
    }
}
