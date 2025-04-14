package MaHoa;

import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

public class RSAExample {
    public static void main(String[] args) {
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String originalString = "Hello, World!";

            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = encryptCipher.doFinal(originalString.getBytes());
            String encryptedString = Base64.getEncoder().encodeToString(encryptedBytes);
            System.out.println("Chuỗi đã mã hóa: " + encryptedString);

            Cipher dercryptCipher = Cipher.getInstance("RSA");
            dercryptCipher.init(Cipher.DECRYPT_MODE,privateKey);
            byte[] decryptedBytes = dercryptCipher.doFinal(Base64.getDecoder().decode(encryptedString));
            String decryptedString = new String(decryptedBytes);
            System.out.println("Chuỗi đã giải mã: " + decryptedString);




        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
