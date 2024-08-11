package TCP_Layer;

public class TransportLayer {
    private int sequenceNumber = 0;
    private int ackNumber = 0;

    public String encapsulate(String data) {
        sequenceNumber += data.length(); // Simulate sequence number increment
        return new StringBuilder()
                .append("TransportHeader{Seq=").append(sequenceNumber)
                .append(", Ack=").append(ackNumber)
                .append(", ").append(data)
                .append("}").toString();
    }

    public String decapsulate(String data) {
        if (data == null || !data.contains("Seq=") || !data.contains("Ack=")) {
            throw new IllegalArgumentException("Invalid data format");
        }

        int seqStart = data.indexOf("Seq=") + 4;
        int seqEnd = data.indexOf(',', seqStart);
        sequenceNumber = Integer.parseInt(data.substring(seqStart, seqEnd).trim());

        int ackStart = data.indexOf("Ack=") + 4;
        int ackEnd = data.indexOf(',', ackStart);
        ackNumber = Integer.parseInt(data.substring(ackStart, ackEnd).trim());

        return data.substring(data.indexOf(',', ackEnd) + 1, data.lastIndexOf('}')).trim();
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getAckNumber() {
        return ackNumber;
    }
}
