package github.soltaufintel.amalia.base;

import java.util.UUID;
import java.util.zip.CRC32;

public class IdGenerator {
	
	private IdGenerator() {
	}
	
	public static String genId() {
    	return UUID.randomUUID().toString().replace("-", "");
    }
	
	public static String createId6() {
		return code6(genId());
	}

	public static String code6(String str) {
		CRC32 crc = new CRC32();
		crc.update(str.getBytes());
		String ret = "000000" + Integer.toString((int) crc.getValue(), 36).toLowerCase().replace("-", "");
		return ret.substring(ret.length() - 6);
	}
	
	public static void checkId6(String id) {
		if (id == null || id.trim().length() != 6) {
			throw new RuntimeException("Illegal id");
		}
		for (int i = 0; i < id.length(); i++) {
			char c = id.charAt(i);
			if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))) {
				throw new RuntimeException("Illegal id");
			}
		}
	}
}
