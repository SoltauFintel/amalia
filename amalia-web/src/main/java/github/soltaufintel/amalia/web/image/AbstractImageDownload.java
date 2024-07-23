package github.soltaufintel.amalia.web.image;

import java.io.IOException;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.route.Route;

public abstract class AbstractImageDownload extends Route<Object> {
    private Object response;
    
    @Override
    protected void execute() {
        // load image
        BinaryData r = getLoader().load();
        byte[] data = r.getData();

        if (data != null) {
            initHeader(r.getFilename());
            
            // resize image
            int width = getWidth();
            if (width > 0) {
                try {
                    data = resize(width, data, isSpecial());
                } catch (IOException ex) {
                    throw new RuntimeException("Error resizing image!", ex);
                }
            }
        }
        
        response = data;
    }

    protected void initHeader(String filename) {
        ctx.res.header("Content-Type", getContentType(filename));
        ctx.res.header("Cache-Control", "max-age=" + (15 * 60)); // 15 minutes
    }

    protected String getContentType(String filename) {
        String ct = "image/jpeg";
        if (filename != null) {
            String e = filename.toLowerCase();
            if (e.endsWith(".png")) {
                ct = "image/png";
            } else if (e.endsWith(".gif")) {
                ct = "image/gif";
            }
        }
        return ct;
    }

    protected abstract IBinaryDataLoader getLoader();
    
    /**
     * @return resize if greater 0
     */
    protected int getWidth() {
        String w = ctx.queryParam("w");
        try {
            return Integer.parseInt(w);
        } catch (Exception e1) {
            return 0;
        }
    }

    protected byte[] resize(final int w, byte[] data, boolean special) throws IOException {
        int orientation = Exif.getOrientation(data);
        javaxt.io.Image img = new javaxt.io.Image(data);
        int height = img.getHeight() * w / img.getWidth();
        if (orientation != 0) {
            img.rotate(orientation);
        }
        if (special) {
            int h = 151; // TO-DO Was hat es sich damit?   w=200
            if (orientation == 90 || orientation == 270) {
                Logger.info("Ast special 1, " + ctx.pathParam("foto"));
                int x = img.getWidth() * h / img.getHeight();
                img.resize(x, h);
            } else {
                Logger.info("Ast special 2, " + ctx.pathParam("foto"));
                int x = w;
                if (img.getHeight() >= img.getWidth() // Bild höher als breit
                        || height > h) {
                    x = img.getWidth() * h / img.getHeight();
                    height = h;
                }
                img.resize(x, height);
            }
        } else {
            if (orientation == 90 || orientation == 270) {
                Logger.info("Ast 1," + ctx.pathParam("foto"));
                img.resize(height, w);
            } else {
                Logger.info("Ast 2," + ctx.pathParam("foto"));
                img.resize(w, height);
            }
        }
        return img.getByteArray();
    }

    protected boolean isSpecial() {
        return getWidth() == 200;
    }

    @Override
    protected Object render() {
        return response;
    }
}
