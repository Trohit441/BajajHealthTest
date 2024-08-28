import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class HashCodeGenerator {

    public static void main(String[] args) {
        
        String[] testArgs = {"240345920069", "C:\\Users\\Rohit\\Desktop\\Bajaj Test\\Test.json"};

        String prn = testArgs[0].toLowerCase().replaceAll("\\s+", "");  // convert PRN to lower case and remove spaces
        String jsonFilePath = testArgs[1];
        String destinationValue = "";

        try {
            destinationValue = findFirstDestinationValue(new File(jsonFilePath));
            if (destinationValue == null) {
                System.out.println("No destination key found in the JSON file.");
                return;
            }
        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
            return;
        }

        String randomString = generateRandomString(8);
        String inputString = prn + destinationValue + randomString;
        String hash = generateMD5Hash(inputString);

        System.out.println(hash + ";" + randomString);
    }

    // Method to find the first instance of "destination" key in the JSON file
    private static String findFirstDestinationValue(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonFile);
        return findDestinationInNode(rootNode);
    }

    // Recursive method to traverse the JSON and find the "destination" key
    private static String findDestinationInNode(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (entry.getKey().equals("destination")) {
                    return entry.getValue().asText();
                } else {
                    String found = findDestinationInNode(entry.getValue());
                    if (found != null) {
                        return found;
                    }
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                String found = findDestinationInNode(arrayElement);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    // Method to generate a random alphanumeric string of a specified length
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }

    // Method to generate an MD5 hash for a given input string
    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
