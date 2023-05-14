package github.soltaufintel.amalia.web.image;

import java.io.ByteArrayOutputStream;

public class BinaryData {
	private final byte[] data;
	private final String filename;
	
	public BinaryData(ByteArrayOutputStream stream, String filename) {
		data = stream == null ? null : stream.toByteArray();
		this.filename = filename;
	}

	public byte[] getData() {
		return data;
	}

	public String getFilename() {
		return filename;
	}
}
