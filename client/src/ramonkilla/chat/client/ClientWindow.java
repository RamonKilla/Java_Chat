package ramonkilla.chat.client;

import jdk.dynalink.beans.StaticClass;
import ramonkilla.chat.network.TCPNetwork_listener;
import ramonkilla.chat.network.TCP_Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.FileNameMap;

public class ClientWindow extends JFrame implements ActionListener, TCPNetwork_listener {
    private static final String IP_ADDR = "6.tcp.eu.ngrok.io";
    private static final int PORT = 17282;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }
    JPanel cards;
    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField();
    private final JTextField fieldInput = new JTextField();

    private final JLabel labelName = new JLabel("NickName");
    private final JLabel labelIp = new JLabel("IP");
    private final JLabel labelPort = new JLabel("Port");

    private final JPanel panelIp = new JPanel();
    private final JTextField fieldIP = new JTextField();
    private TCP_Network connection;
    private ClientWindow(){
        cards = new JPanel(new CardLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldIP, BorderLayout.EAST);
        add(fieldNickname, BorderLayout.PAGE_START);
        cards.add(panelIp, IP_ADDR);
        setVisible(true);
        try {
            connection = new TCP_Network(this, IP_ADDR, PORT);

        }catch (IOException e){
            printMsg("Connection Exception: "+ e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + msg);
    }

    @Override
    public void onConnectionRead(TCP_Network tcpNetwork) {
        printMsg("Connection ready...");
    }

    @Override
    public void onRecieveString(TCP_Network tcpNetwork, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCP_Network tcpNetwork) {
        printMsg("Connection Closed: ");
    }

    @Override
    public void onException(TCP_Network tcpNetwork, Exception e) {
        printMsg("Connection Exception: "+ e);
    }
    private synchronized void printMsg(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
