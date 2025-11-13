package github.soltaufintel.amalia.ckeditor.image;

import java.io.InputStream;

import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.image.BinaryData;

public interface ImageService {

    String saveImage(Context ctx, InputStream content, String filename);

    BinaryData loadImage(Context ctx);
    
    String getImageUploadLink(Context ctx);
}
