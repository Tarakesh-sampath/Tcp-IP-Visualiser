package TCP_Layer;

public class DataLinkLayer {
    public String encapsulate(String data) {
        return "DataLinkHeader{" + data + "}";
    }

    public String decapsulate(String data) {
        if (data == null || !data.contains("{") || !data.contains("}")) {
            throw new IllegalArgumentException("Invalid data format");
        }
        return data.substring(data.indexOf('{') + 1, data.lastIndexOf('}'));
    }
}
