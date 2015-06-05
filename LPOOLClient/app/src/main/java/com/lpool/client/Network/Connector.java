package com.lpool.client.Network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by André on 03/06/2015.
 */
public class Connector {

    public enum EndReason
    {
        BLACK_BALL_SCORED_AS_LAST,
        BLACK_BALL_SCORED_ACCIDENTALLY,
        TIMEOUT,
        DISCONNECT
    }

    public enum ProtocolCmd {
        ANGLE, // angle
        FIRE, // force[0, 1] x-spin[-1, 1] y-spin[-1, 1]
        PING,
        PONG,
        JOIN,
        QUIT,
        KICK,
        MOVECB, // x-pos[0, 1] y-pos[0, 1]
        PLACECB, // x-pos[0, 1] y-pos[0, 1]
        PLAY,
        WAIT,
        BIH,
        END // winner(boolean) End.Reason
    };

    // TCP
    private volatile Socket socket;
    // UDP
    private DatagramSocket datagramSocket;

    private String serverIP;
    private int serverPort;
    private Boolean running = true;

    private ArrayList<Receiver> receivers;

    public Connector(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
        running = true;
        receivers = new ArrayList<Receiver>();
        initializeClientThread(); // Initializes receiver/heartbeat thread
        System.out.println("Connecting to " + serverIP + " " + serverPort);
    }

    public Boolean sendUDPMessage(String message) {

        if(!running)
            return false;

        if (socket == null || datagramSocket == null)
            return false;
        try {
            String data = message;
            byte[] msg = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(serverIP), serverPort);
            datagramSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Boolean sendTCPMessage(String message) {

        if(!running)
            return false;

        if (socket == null)
            return false;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            pw.println(message);
            System.out.println("Sent " + message);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    class ClientThread implements Runnable {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIP);
                socket = new Socket(serverAddr, serverPort);
                datagramSocket = new DatagramSocket(serverPort);
                initializeReceiverThread();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    class receiverThread implements Runnable {
        @Override
        public void run() {
            while (running)
            {
                try {
                    System.out.println("Receiving...");
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = br.readLine();
                    if (str == null) {
                        System.out.println("received null");
                        break;
                    }
                    System.out.println("Received message \"" + str + "\"");
                    if(str.equals(ProtocolCmd.PING.ordinal() + ""))
                    {
                        System.out.println("Got PING");
                        sendTCPMessage("" + ProtocolCmd.PONG.ordinal());
                        System.out.println("Sent PONG");
                    }
                    else
                        spreadMessage(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void spreadMessage(String message) {
        for(int i = 0; i < receivers.size(); i++) {
            if(receivers.get(i) != null)
                receivers.get(i).getMessage(message);
        }
    }

    private void initializeClientThread() {
        new Thread(new ClientThread()).start();
    }

    private void initializeReceiverThread() { new receiverThread().run();}

    public void addReceiver(Receiver receiver) {
        receivers.add(receiver);
    }

    public void removeReceiver(Receiver receiver) {
        if(receivers.indexOf(receiver) != -1) {
            receivers.remove(receiver);
        }
    }

    public void stop() {
        running = false;
        receivers.clear();
    }

    public void restart() {
        running = true;
        initializeClientThread();
        initializeReceiverThread();
    }

    public void disconnect() {
        stop();
        try {
            if(datagramSocket != null) {
                datagramSocket.disconnect();
            if(socket != null)
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
