package github.soltaufintel.amalia.ckeditor.image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.image.BinaryData;

public class ImageServiceDispatcher implements ImageService {
    public boolean infoLog = false;
    private final List<ImageService> imageServices = new ArrayList<>();

    public void add(ImageService imageService) {
        imageServices.add(imageService);
    }

    @Override
    public String saveImage(Context ctx, InputStream content, String filename) {
        if (infoLog) {
            Logger.info("saveImage: " + ctx.path() + (ctx.req.queryString() == null ? "" : ("?" + ctx.req.queryString())));
        }
        for (ImageService i : imageServices) {
            String url = i.saveImage(ctx, content, filename);
            if (url != null) {
                return url;
            } // else: not relevant
        }
        throw new RuntimeException("No relevant ImageService found for saving image");
    }
   
    @Override
    public BinaryData loadImage(Context ctx) {
        if (infoLog) {
            Logger.info("loadImage: " + ctx.path() + (ctx.req.queryString() == null ? "" : ("?" + ctx.req.queryString())));
        }
        for (ImageService i : imageServices) {
            BinaryData bd = i.loadImage(ctx);
            if (bd != null) {
                return bd;
            }
        }
        throw new RuntimeException("No relevant ImageService found for loading image");
    }
}
