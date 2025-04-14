package MaHoa;

public class Base64Example {
    public static void main(String[] args) {
        String originalString = "Hello, World!";

        String encodedString = java.util.Base64.getEncoder().encodeToString(originalString.getBytes());
        System.out.println("Chuoi ma hoa : " + encodedString);

        byte[] dedcodedBytes = java.util.Base64.getDecoder().decode(encodedString);
        String decodedString = new String(dedcodedBytes);
        System.out.println("Chuoi giai ma : " + decodedString);
    }
}
