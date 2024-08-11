import Packets.*;
import TCP_Layer.*;
import java.io.*;
import java.net.*;

public class Receiver {
    private DataLinkLayer dataLinkLayer = new DataLinkLayer();
    private NetworkLayer networkLayer = new NetworkLayer();
    private TransportLayer transportLayer = new TransportLayer();
    private ApplicationLayer applicationLayer = new ApplicationLayer();

    public CoreData receive(Packet packet) {
        String data = packet.getData();
        data = dataLinkLayer.decapsulate(data);
        data = networkLayer.decapsulate(data);
        data = transportLayer.decapsulate(data);
        data = applicationLayer.decapsulate(data);
        return new CoreData(data);
    }

    private boolean performHandshake(ObjectOutputStream out, ObjectInputStream in, InetAddress add,boolean DO) throws Exception {
        // Receive SYN
        Packet SYN = (Packet) in.readObject();
        if(DO){
            System.out.println("Received SYN: " + SYN.SYN);
        }
        else{
            System.out.println("Received FIN: " + SYN.SYN);
        }
        // Network traffic simulation
        Thread.sleep(networkLayer.traffic() * 1000);

        if (SYN.SYN == 1) {
            Packet SYN_ACK = new Packet(1, 1, SYN.S_add, add, "");
            out.writeObject(SYN_ACK);
            out.flush();
            if(DO){
                System.out.println("Sent: SYN_ACK");
            }
            else{
                System.out.println("Sent: FIN_ACK");
            }
            
        }

        // Receive ACK
        Packet ACK = (Packet) in.readObject();
        System.out.println("Received ACK: " + ACK.ACK);
        return ACK.ACK == 1;
    }

    public void runReceiver() {
        try (ServerSocket serverSocket = new ServerSocket(12345);
             Socket socket = serverSocket.accept();
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("\n\n#Three-way handshake#\n");
            if (!performHandshake(out, in, serverSocket.getInetAddress(),true)) {
                System.out.println("Handshake failed.");
                return;
            }
            System.out.println("\n#Three-way handshake completed#\n\n");

            StringBuilder receivedData = new StringBuilder();
            // Receive data
            while (true) {
                try {
                    Packet packet = (Packet) in.readObject();
                    if (packet == null || packet.getData() == null || packet.getData().isEmpty()) {
                        out.writeObject(new Packet(0)); // Send termination signal
                        out.flush();
                        break;
                    } else {
                        out.writeObject(new Packet(1, packet.S_add, packet.D_add));
                        out.flush();
                    }
                    CoreData receivedChunk = receive(packet);
                    System.out.println("Recived Encapsulated packet: "+ receivedChunk.getData());
                    receivedData.append(receivedChunk.getData());
                } catch (EOFException e) {
                    // Handle end of stream
                    System.out.println("EOFException: End of stream reached.");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out.println("#Received data completely#");
            //unhand shake function
            System.out.println("Reassembled Data: " + receivedData.toString());
            System.out.println("\n\n#unsynchronizing connection#\n");
            do{
                if (performHandshake(out, in, serverSocket.getInetAddress(),false)) {
                    System.out.println("Connection terminated.");
                    return;
                }
            }while(!performHandshake(out, in, serverSocket.getInetAddress(),false));
            System.out.println("\n#unsynchronizing connection completed#\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Receiver().runReceiver();
    }
}
