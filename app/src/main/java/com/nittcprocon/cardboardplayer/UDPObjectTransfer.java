package com.nittcprocon.cardboardplayer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class UDPObjectTransfer {

    /**
     * 指定ポートでUDPソケットを開いてオブジェクトの受信を待機する。
     *
     * @param port       受信待機ポート。送信側と揃える。
     * @param bufferSize 受信時のバッファーサイズ。小さすぎると受信に失敗するので、適当に大きめな値(1024～8192くらい？)を指定する。
     * @return オブジェクト
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object receive(int port, int bufferSize)
            throws IOException, ClassNotFoundException {
        try (DatagramSocket clientSocket = new DatagramSocket(port)) {
            return receive(clientSocket, bufferSize);
        }
    }

    /**
     * 指定UDPソケットを使ってオブジェクトの受信を待機する。
     *
     * @param clientSocket 事前作成したUDPソケット
     * @param bufferSize   受信時のバッファーサイズ。小さすぎると受信に失敗するので、適当に大きめな値(1024～8192くらい？)を指定する。
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object receive(DatagramSocket clientSocket, int bufferSize)
            throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, bufferSize);
        clientSocket.receive(packet);
        return convertFromBytes(buffer, 0, packet.getLength());
    }

    /**
     * バイト配列をオブジェクトに変換する。
     *
     * @param bytes  バイト配列
     * @param offset 読み込み開始位置
     * @param length 読み込むデータの長さ
     * @return 復元されたオブジェクト
     * @throws IOException            デシリアライズに失敗した時に発生する
     * @throws ClassNotFoundException デシリアライズに失敗した時に発生する
     */
    private static Object convertFromBytes(byte[] bytes, int offset, int length)
            throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes, offset, length);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }
}