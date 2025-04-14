package MaHoa;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class HashExample {
    public static void main(String[] args) {
        try{
            String originalString = "Mat kha 123";
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(originalString.getBytes());

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                stringBuilder.append(String.format("%02x", b));
            }

            System.out.println("MD5 hash: " + stringBuilder.toString());

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] sha256Bytes = sha256.digest(originalString.getBytes());

            StringBuilder sha256HexString = new StringBuilder();
            for (byte b : sha256Bytes) {
                sha256HexString.append(String.format("%02x", b));
            }

            System.out.println("SHA-256 hash: " + sha256HexString.toString());
        }catch (NoSuchAlgorithmException e){
            System.err.println("Lỗi: " + e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
