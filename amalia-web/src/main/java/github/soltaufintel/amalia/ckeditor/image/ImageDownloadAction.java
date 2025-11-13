package github.soltaufintel.amalia.ckeditor.image;

import github.soltaufintel.amalia.web.image.AbstractImageDownload;
import github.soltaufintel.amalia.web.image.IBinaryDataLoader;

public class ImageDownloadAction extends AbstractImageDownload {

    @Override
    protected IBinaryDataLoader getLoader() {
        return () -> ImageUploadAction.imageService.loadImage(ctx);
    }
}
