package github.soltaufintel.amalia.web.image;

import java.io.ByteArrayOutputStream;

public class BinaryData {
    public static final BinaryData NULL = new BinaryData((byte[]) null, null);
    private final byte[] data;
    private final String filename;
    
    public BinaryData(ByteArrayOutputStream stream, String filename) {
        this(stream == null ? null : stream.toByteArray(), filename);
    }

    public BinaryData(byte[] data, String filename) {
        this.data = data;
        this.filename = filename;
    }

    public byte[] getData() {
        return data;
    }

    public String getFilename() {
        return filename;
    }
}
