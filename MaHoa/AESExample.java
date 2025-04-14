package MaHoa;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class AESExample {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            // Tạo khóa AES
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128); // 128, 192 hoặc 256 bit
            SecretKey secretKey = keyGenerator.generateKey();

            String originalString = sc.nextLine();

            // Mã hóa
            Cipher encryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = encryptCipher.doFinal(originalString.getBytes());
            String encryptedString = Base64.getEncoder().encodeToString(encryptedBytes);
            System.out.println("Chuỗi đã mã hóa: " + encryptedString);

            // Giải mã
            Cipher decryptCipher = Cipher.getInstance("AES");
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = decryptCipher.doFinal(Base64.getDecoder().decode(encryptedString));
            String decryptedString = new String(decryptedBytes);
            System.out.println("Chuỗi đã giải mã: " + decryptedString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
