package MaHoa.BaiTap;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAEncryption implements Encryptable {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSAEncryption() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }
    public RSAEncryption(byte[] publicKeyBytes, byte[] privateKeyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        if (publicKeyBytes != null) {
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = keyFactory.generatePublic(publicKeySpec);
        }
        if (privateKeyBytes != null) {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            this.privateKey = keyFactory.generatePrivate(privateKeySpec);
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodeBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodeBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public String getPublicKeyString() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    public String getPrivateKeyString() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
}
