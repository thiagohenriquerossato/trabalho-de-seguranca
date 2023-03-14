import java.util.Base64;

public class Vernam {
    public String verEncrypt(String s) {
        String key = "key";
        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
    }

    public String verDecrypt(String s) {
        String key = "key";
        return new String(xorWithKey(base64Decode(s), key.getBytes()));
    }

    private byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }

    private byte[] base64Decode(String s) {
        return Base64.getDecoder().decode(s);
    }

    private String base64Encode(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }
}