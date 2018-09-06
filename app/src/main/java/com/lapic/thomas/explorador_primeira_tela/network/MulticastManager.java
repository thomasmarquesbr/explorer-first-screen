package com.lapic.thomas.explorador_primeira_tela.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class MulticastManager {

    private final Context mContext;
    private final String TAG;
    private final int MULTICAST_PORT;
    private final Handler incomingMessageHandler;
    private boolean sending = false;
    private boolean receiveMessages = false;
    private String MY_LOCK = "mylock";
    private InetAddress group;
    private MulticastSocket clientSocket;
    private MulticastSocket serverSocket;
    protected Message incomingMessage;
    private Thread receiverThread;
    private Thread senderThread;
    private String MULTICAST_ADDRESS;
    private boolean keepAlive = false;
    private int TIME_KEEP_ALIVE = 2000;

    protected abstract Runnable getIncomingMessageAnalyseRunnable();

    public MulticastManager(Context context, String tag, String multicastIp, int multicastPort) {
        if(context == null || tag == null || tag.length() == 0 ||
                multicastPort <= 1024 || multicastPort > 49151)
            throw new IllegalArgumentException();
        this.mContext = context.getApplicationContext();
        TAG = tag;
        MULTICAST_ADDRESS = multicastIp;
        MULTICAST_PORT = multicastPort;
        incomingMessageHandler = new Handler(Looper.getMainLooper());
    }

    public void sendMessage(final boolean KeepAlive, final String message) throws IOException {

        Runnable sender = new Runnable() {
            @Override
            public void run() {
                byte[] buf = new byte[4098];
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, MULTICAST_PORT);
                do {
                    message.replaceAll("\"","");
                    packet.setData(message.getBytes(), 0, message.length());
                    try {
                        serverSocket.send(packet);
                        Log.d(TAG, "mensagem enviada para " + MULTICAST_ADDRESS + ":" + MULTICAST_PORT + " - "+  message);
                        Thread.sleep(TIME_KEEP_ALIVE);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (sending && keepAlive);
                sending = false;
                senderThread = null;
            }
        };

        keepAlive = KeepAlive;
        group = InetAddress.getByName(MULTICAST_ADDRESS);
        serverSocket = new MulticastSocket(MULTICAST_PORT);
        serverSocket.joinGroup(group);

        if (sending)
            return;
        sending = true;
        if(senderThread == null)
            senderThread = new Thread(sender);

        if(!senderThread.isAlive())
            senderThread.start();

    }

    public void stopKeepAlive() {
        keepAlive = false;
    }

    public void startMessageReceiver() {
        Runnable receiver = new Runnable() {
            @Override
            public void run() {
                WifiManager wm = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiManager.MulticastLock multicastLock = wm.createMulticastLock(MY_LOCK);
                multicastLock.acquire();
                while (receiveMessages) {
                    try {
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        clientSocket.receive(packet);
                        String message = new String(packet.getData()).trim();
                        incomingMessage = new Message(TAG, message, packet.getAddress(), System.currentTimeMillis()/1000);
                        incomingMessageHandler.post(getIncomingMessageAnalyseRunnable());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            clientSocket = new MulticastSocket(MULTICAST_PORT);
            clientSocket.joinGroup(group);
            clientSocket.setBroadcast(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        receiveMessages = true;
        if(receiverThread == null)
            receiverThread = new Thread(receiver);

        if(!receiverThread.isAlive())
            receiverThread.start();

    }

    public void stopMessageReceiver() {
        receiveMessages = false;
    }

}
