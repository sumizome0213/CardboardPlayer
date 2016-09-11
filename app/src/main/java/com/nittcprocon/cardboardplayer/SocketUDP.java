package com.nittcprocon.cardboardplayer;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class SocketUDP {
    private DatagramSocket receiveSocket;
    private String message;
    private int port;

    //別スレッドで実行がほぼ必須
    public String getMessage(int ports){

        port = ports;

        Thread thread=new Thread(){
            public void run() {
                // portを監視するUDPソケットを生成
                // DatagramSocket receiveSocket = null;
                try {
                    receiveSocket = new DatagramSocket(port);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                // 受け付けるデータバッファとUDPパケットを作成
                byte receiveBuffer[] = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                try {

                    receiveSocket.receive(receivePacket);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // 受信したデータをログへ出力
                message = new String(receivePacket.getData(),0, receivePacket.getLength());
                Log.d("UDPMessage", message);
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.d("UDPMessage",e.toString());
        }

        return message;
    }
}
