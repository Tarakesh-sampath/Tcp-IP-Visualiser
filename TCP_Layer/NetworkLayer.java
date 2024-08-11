package TCP_Layer;

import java.util.Random;

public class NetworkLayer {
    private Random rand = new Random();

    public int traffic() {
        return rand.nextInt(10);
    }

    public String encapsulate(String data) {
        return new StringBuilder().append("NetworkHeader{").append(data).append("}").toString();
    }

    public String decapsulate(String data) {
        if (data == null || !data.contains("{") || !data.contains("}")) {
            throw new IllegalArgumentException("Invalid data format");
        }
        return data.substring(data.indexOf('{') + 1, data.lastIndexOf('}'));
    }
}
