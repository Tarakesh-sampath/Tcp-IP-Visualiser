package TCP_Layer;

import Packets.CoreData;

public class ApplicationLayer {
    public String encapsulate(CoreData data) {
        return "AppHeader{" + data.getData() + "}";
    }

    public String decapsulate(String data) {
        if (data == null || !data.contains("{") || !data.contains("}")) {
            throw new IllegalArgumentException("Invalid data format");
        }
        return data.substring(data.indexOf('{') + 1, data.lastIndexOf('}'));
    }
}
