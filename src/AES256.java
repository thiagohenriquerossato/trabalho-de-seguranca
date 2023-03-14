import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES256 {
    private SecretKey key;
    private static AES256 instance = null;

    private int KEY_SIZE = 256;
    private int T_LEN = 128;
    private Cipher encryptionCipher;


    private AES256() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256);
        key = generator.generateKey();

        encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
    }

    public String getKey(){
        return encode(key.getEncoded());
    }

    public String getIV(){
        try {
            encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return encode(encryptionCipher.getIV());
    }

    public static AES256 getInstance() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        if (instance == null) {
            instance = new AES256();
        }
        return instance;
    }

    public String encrypt(String message) throws Exception{

        byte[] messageInBytes = message.getBytes();
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedMessage, String decryptionKeyString, String ivString) throws Exception{

        byte[] decryptionKeyByte = decode(decryptionKeyString);
        SecretKey decryptionKey = new SecretKeySpec(decryptionKeyByte, "AES");
        byte[]iv = decode(ivString);
        byte[] messageInBytes = decode(encryptedMessage);

        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, iv);

        decryptionCipher.init(Cipher.DECRYPT_MODE, decryptionKey, spec);

        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);

        return new String(decryptedBytes);

    }

    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }


}