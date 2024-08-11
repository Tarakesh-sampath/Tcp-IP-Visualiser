import Packets.*;
import TCP_Layer.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Sender {
    private DataLinkLayer dataLinkLayer = new DataLinkLayer();
    private NetworkLayer networkLayer = new NetworkLayer();
    private TransportLayer transportLayer = new TransportLayer();
    private ApplicationLayer applicationLayer = new ApplicationLayer();
    
    public Packet send(CoreData coreData, Packet pac) {
        String data = applicationLayer.encapsulate(coreData);
        data = transportLayer.encapsulate(data);
        data = networkLayer.encapsulate(data);
        data = dataLinkLayer.encapsulate(data);
        return new Packet(pac.SYN, pac.ACK, pac.S_add, pac.D_add, data);
    }

    private Packet performHandshake(ObjectOutputStream out, ObjectInputStream in, InetAddress add,boolean DO) throws Exception {
        // Send SYN
        Thread.sleep(networkLayer.traffic() * 1000);
        Packet SYN = new Packet(add, 1);
        out.writeObject(SYN);
        out.flush();
        if(DO){
            System.out.println("Sent: SYN");
        }
        else{
            System.out.println("Sent: FIN");
        }
        // Receive SYN-ACK
        Packet SYN_ACK = (Packet) in.readObject();
        if(DO){
            System.out.println("Received SYN, ACK: " + SYN_ACK.SYN + "," + SYN_ACK.ACK);
        }
        else{
            System.out.println("Received FIN, ACK: " + SYN_ACK.SYN + "," + SYN_ACK.ACK);
        }
        // Send ACK
        Thread.sleep(networkLayer.traffic() * 1000);
        if (SYN_ACK.SYN == SYN_ACK.ACK && SYN_ACK.SYN == 1) {
            Packet ACK = new Packet(SYN_ACK.SYN, SYN_ACK.ACK, SYN.S_add, SYN_ACK.D_add, "");
            out.writeObject(ACK);
            out.flush();
            System.out.println("Sent: ACK");
            return SYN_ACK;
        }
        System.out.println("Error: No ACK, Connection unstable");
        return new Packet(0); 
    }
    public void runSender() {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("\n\n#Three-way handshake#\n");
            Packet result = performHandshake(out, in, socket.getInetAddress(),true);
            if (result.ACK == 0) {
                System.out.println("Handshake failed. Exiting...");
                return;
            }
            System.out.println("\n#Three-way handshake completed#\n\n");

            System.out.print("Enter the data to send: ");
            String inputData = scanner.nextLine();
            
            // Split the input data into chunks (simulating packets)
            int packetSize = 5; // Adjust the packet size as needed
            int totalPackets = (int) Math.ceil((double) inputData.length() / packetSize);
            
            // Sending packets
            for (int i = 0; i < totalPackets; i++) {
                int start = i * packetSize;
                int end = Math.min(start + packetSize, inputData.length());
                String chunk = inputData.substring(start, end);
                CoreData coreData = new CoreData(chunk);
                Packet packet = send(coreData, result);
                System.out.println("Encapsulated Packet Data: " + packet.getData());
                out.writeObject(packet);
                out.flush();
                
                Packet ackPacket = (Packet) in.readObject();
                if (ackPacket.ACK == 0) {
                    i--; // Retransmit the packet if ACK is not received
                }
            }
            System.out.println("#datasent completely#\n\n#unsynchronizing connection#\n");
            // Unperform handshake
            do{
                result = performHandshake(out, in, socket.getInetAddress(),false);
                if(result.ACK == 1){
                    System.out.println("Connection terminated. Exiting...");
                    return;
                }
            }while(result.ACK == 0);
            System.out.println("\n\n#unsynchronizing connection completed#\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Sender().runSender();
    }
}
