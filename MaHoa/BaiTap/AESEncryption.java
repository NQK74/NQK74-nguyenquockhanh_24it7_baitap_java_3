package MaHoa.BaiTap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESEncryption implements Encryptable{
    private SecretKey secretKey;


    public AESEncryption() throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        this.secretKey = keyGenerator.generateKey();
    }

    public AESEncryption(byte[] key) {
        this.secretKey = new SecretKeySpec(key, "AES");
    }
    public SecretKey getSecretKey() {
        return secretKey;
    }

    @Override
    public String encrypt(String data)  throws Exception{
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes= cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String encryptedData) throws Exception {
        if (encryptedData == null || encryptedData.isEmpty()) {
            throw new IllegalArgumentException("Dữ liệu mã hóa không được để trống");
        }
        try {
            byte[] decodeBytes = Base64.getDecoder().decode(encryptedData);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(decodeBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Dữ liệu mã hóa không phải định dạng Base64 hợp lệ");
        }
    }

    public String getKeyAsBase64(){
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}
