package ramonkilla.chat.network;

public interface TCPNetwork_listener {
    void onConnectionRead(TCP_Network tcpNetwork);
    void onRecieveString(TCP_Network tcpNetwork, String value);
    void onDisconnect(TCP_Network tcpNetwork);
    void onException(TCP_Network tcpNetwork, Exception e);
}
