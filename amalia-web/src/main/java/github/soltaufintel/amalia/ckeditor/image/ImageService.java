package github.soltaufintel.amalia.ckeditor.image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.function.Supplier;

import github.soltaufintel.amalia.base.FileService;
import github.soltaufintel.amalia.base.IdGenerator;
import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.image.BinaryData;
import spark.utils.IOUtils;

/**
 * Service for loading and saving an image
 */
public interface ImageService {

    /**
     * Uploading the image as a file to the server.
     * This is not the same as permanent storage, e.g., in a database or Git repository.
     * 
     * @param ctx      -
     * @param content  image content
     * @param filename filename from image upload action
     * @return image download URL, null if not relevant
     */
    String saveImage(Context ctx, InputStream content, String filename);
    
    default String _saveImage(boolean relevant, InputStream content, String filename,
            Function<String, File> getFile, Function<String, String> getPath) {
        if (!relevant) {
            return null;
        }
        byte[] image;
        try {
            image = IOUtils.toByteArray(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int o = filename.lastIndexOf(".");
        String unique = "-" + IdGenerator.createId25();
        if (o >= 0) {
            filename = filename.substring(0, o) + unique + filename.substring(o);
        } else {
            filename += unique;
        }
        FileService.saveBinaryFile(getFile.apply(filename), image);
        return getPath.apply(filename);
    }
    
    /**
     * @param ctx -
     * @return null if not relevant
     */
    BinaryData loadImage(Context ctx);
    
    default BinaryData _loadImage(boolean relevant, Supplier<String> idSupplier,
            Supplier<String> filenameSupplier, GetFile fromService, GetFile fromUploadDir) {
        if (!relevant) {
            return null;
        }
        byte[] img = null;
        String id = idSupplier.get();
        String filename = filenameSupplier.get();
        var file = fromService.getFile(id, filename);
        if (!file.isFile()) {
            // Image ist noch im upload dir.
            file = fromUploadDir.getFile(id, filename);
        } // kein else if !
        if (file.isFile()) {   
            img = FileService.loadBinaryFile(file);
        }
        return new BinaryData(img, filename);
    }
    
    public interface GetFile {
        
        File getFile(String id, String filename);
    }
}
