package ramonkilla.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCP_Network {
    private final Socket socket;
    private final Thread rxThread;
    private final TCPNetwork_listener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCP_Network(TCPNetwork_listener eventListener, String ipAddr, int port) throws  IOException{
        this(eventListener, new Socket(ipAddr, port));
    }
    public TCP_Network(TCPNetwork_listener eventListener ,Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        socket.getInputStream();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionRead(TCP_Network.this);
                    while (!rxThread.isInterrupted()){
                        eventListener.onRecieveString(TCP_Network.this, in.readLine());

                    }
                }catch (IOException e){
                    eventListener.onException(TCP_Network.this, e);

                } finally {
                    eventListener.onDisconnect(TCP_Network.this);
                }
            }
        });
        rxThread.start();

    }
    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n" );
            out.flush();
        }catch (IOException e){
            eventListener.onException(TCP_Network.this, e);
            disconnect();
        }

    }
    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e){
            eventListener.onException(TCP_Network.this, e);
        }


    }
    @Override
    public String toString(){
        return "TCPConnection: "+ socket.getInetAddress() + ": " + socket.getPort();
    }
}
