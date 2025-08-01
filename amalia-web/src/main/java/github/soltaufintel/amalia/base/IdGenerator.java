package github.soltaufintel.amalia.base;

import java.math.BigInteger;
import java.util.UUID;
import java.util.zip.CRC32;

public class IdGenerator {
    
    private IdGenerator() {
    }
    
    /**
     * Generates a GUID.
     * @return 32 char long GUID
     */
    public static String genId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * Generates a short ID that is as good as a GUID. It looks nicer.
     * With a million generations, there are approximately 220 collisions. It's better to use createId25.
     * @return 6 char long ID
     */
    public static String createId6() {
        return code6(genId());
    }

    /**
     * Converts a string to a 6 char long ID.
     * @param str any string
     * @return 6 char long ID
     */
    public static String code6(String str) {
        CRC32 crc = new CRC32();
        crc.update(str.getBytes());
        String ret = "000000" + Integer.toString((int) crc.getValue(), 36).toLowerCase().replace("-", "");
        return ret.substring(ret.length() - 6);
    }
    
    /**
     * Checks id whether it is a valid 6 char long ID. If not a RuntimeException is thrown.
     * @param id -
     * @throws RuntimeException "Illegal ID"
     */
    public static void checkId6(String id) {
        if (id == null || id.trim().length() != 6) {
            throw new RuntimeException("Illegal id");
        }
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))) {
                throw new RuntimeException("Illegal ID");
            }
        }
    }
    
    /**
     * Creates a 25 char long ID.
     * @return letters are in lowercase
     */
    public static String createId25() {
        UUID uuid = UUID.randomUUID();
        // A UUID consists of two 64-bit numbers: mostSigBits and leastSigBits.
        // To treat them as a single 128-bit number, we shift mostSigBits 64 bits to the left and add leastSigBits.
        BigInteger val = new BigInteger(1, BigInteger.valueOf(uuid.getMostSignificantBits()).toByteArray());
        val = val.shiftLeft(64);
        val = val.add(new BigInteger(1, BigInteger.valueOf(uuid.getLeastSignificantBits()).toByteArray()));
        return val.toString(36);
    }
}
