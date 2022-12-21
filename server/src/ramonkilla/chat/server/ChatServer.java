package ramonkilla.chat.server;

import ramonkilla.chat.network.TCPNetwork_listener;
import ramonkilla.chat.network.TCP_Network;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPNetwork_listener {
    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCP_Network> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server Running...");
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            while (true){
                try {
                    new TCP_Network(this, serverSocket.accept());
                } catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionRead(TCP_Network tcpNetwork) {
        connections.add(tcpNetwork);
        sendToAllConnections("Client connected: " + tcpNetwork);
    }

    @Override
    public synchronized void onRecieveString(TCP_Network tcpNetwork, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCP_Network tcpNetwork) {
        connections.remove(tcpNetwork);
        sendToAllConnections("Client disconnected: " + tcpNetwork);
    }

    @Override
    public synchronized void onException(TCP_Network tcpNetwork, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }
    private void sendToAllConnections(String value){
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++){
            connections.get(i).sendString(value);
        }
    }
}
