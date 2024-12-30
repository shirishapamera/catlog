// Main.java
import java.math.BigInteger;
import java.util.*;

class Main {
    static class Point {
        BigInteger x;
        BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        // Test case 1
        String json1 = "{\"keys\":{\"n\":4,\"k\":3},\"1\":{\"base\":\"10\",\"value\":\"4\"},\"2\":{\"base\":\"2\",\"value\":\"111\"},\"3\":{\"base\":\"10\",\"value\":\"12\"},\"6\":{\"base\":\"4\",\"value\":\"213\"}}";
        
        // Test case 2
        String json2 = "{\"keys\":{\"n\":10,\"k\":7},\"1\":{\"base\":\"6\",\"value\":\"13444211440455345511\"},\"2\":{\"base\":\"15\",\"value\":\"aed7015a346d63\"},\"3\":{\"base\":\"15\",\"value\":\"6aeeb69631c227c\"},\"4\":{\"base\":\"16\",\"value\":\"e1b5e05623d881f\"},\"5\":{\"base\":\"8\",\"value\":\"316034514573652620673\"},\"6\":{\"base\":\"3\",\"value\":\"2122212201122002221120200210011020220200\"},\"7\":{\"base\":\"3\",\"value\":\"20120221122211000100210021102001201112121\"},\"8\":{\"base\":\"6\",\"value\":\"20220554335330240002224253\"},\"9\":{\"base\":\"12\",\"value\":\"45153788322a1255483\"},\"10\":{\"base\":\"7\",\"value\":\"1101613130313526312514143\"}}";

        System.out.println("Secret for test case 1: " + findSecret(json1));
        System.out.println("Secret for test case 2: " + findSecret(json2));
    }

    private static Map<String, String> parseJsonObject(String json, String key) {
        Map<String, String> result = new HashMap<>();
        
        // Find the object for the given key
        int start = json.indexOf("\"" + key + "\":{");
        if (start == -1) return result;
        
        start = json.indexOf("{", start) + 1;
        int count = 1;
        int end = start;
        
        while (count > 0 && end < json.length()) {
            if (json.charAt(end) == '{') count++;
            if (json.charAt(end) == '}') count--;
            end++;
        }
        
        String object = json.substring(start, end - 1);
        String[] pairs = object.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String k = keyValue[0].trim().replace("\"", "");
                String v = keyValue[1].trim().replace("\"", "");
                result.put(k, v);
            }
        }
        
        return result;
    }

    private static BigInteger findSecret(String jsonStr) {
        // Get k from keys object
        Map<String, String> keys = parseJsonObject(jsonStr, "keys");
        int k = Integer.parseInt(keys.get("k"));
        
        List<Point> points = new ArrayList<>();
        
        // Parse each point
        for (int i = 1; i <= 10 && points.size() < k; i++) {
            Map<String, String> point = parseJsonObject(jsonStr, String.valueOf(i));
            if (!point.isEmpty()) {
                int base = Integer.parseInt(point.get("base"));
                String value = point.get("value");
                
                BigInteger x = new BigInteger(String.valueOf(i));
                BigInteger y = new BigInteger(value, base);
                
                points.add(new Point(x, y));
            }
        }
        
        return lagrangeInterpolation(points);
    }

    private static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger result = BigInteger.ZERO;
        BigInteger x = BigInteger.ZERO;  // We want to find f(0)
        
        for (int i = 0; i < points.size(); i++) {
            BigInteger term = points.get(i).y;
            
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger numerator = x.subtract(points.get(j).x);
                    BigInteger denominator = points.get(i).x.subtract(points.get(j).x);
                    term = term.multiply(numerator).divide(denominator);
                }
            }
            
            result = result.add(term);
        }
        
        return result;
    }
}
