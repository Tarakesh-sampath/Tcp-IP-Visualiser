package Packets;
import java.io.Serializable;
import java.net.InetAddress;

public class Packet implements Serializable {
    private String data;
    public InetAddress S_add;
    public InetAddress D_add;
    public int SYN;
    public int ACK;
    public int FIN;

    public Packet(int SYN, int ACK, InetAddress S_add, InetAddress D_add, String data) {
        this.SYN = SYN;
        this.ACK = ACK;
        this.S_add = S_add;
        this.D_add = D_add;
        this.data = data;
    }

    public Packet(InetAddress S_add, int FIN, InetAddress D_add) {
        this.FIN = FIN;
        this.S_add = S_add;
        this.D_add = D_add;
    }
    public Packet(InetAddress S_add, int FIN,int ACK, InetAddress D_add) {
        this.FIN = FIN;
        this.ACK = ACK;
        this.S_add = S_add;
        this.D_add = D_add;
    }

    public Packet(int ACK,InetAddress S_add, InetAddress D_add) {
        this.ACK = ACK;
        this.S_add = S_add;
        this.D_add = D_add;
    }

    public Packet(InetAddress S_add, int SYN) {
        this.SYN = SYN;
        this.S_add = S_add;
    }
    public Packet(int ACK) {
        this.ACK = ACK;
    }

    public Packet(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
