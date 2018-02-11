package cn.ichi.android;

import java.io.IOException;
import java.net.Socket;

import cn.ichi.android.Client;
import cn.ichi.android.ClientListener;
import cn.ichi.android.network.TcpClient;
import cn.ichi.android.network.TcpClientListener;

public class Printer {
    private ClientListener mListener;
    private Client mClient;

    /***
     *
     * @param listener
     * @param type  0: TCP, 1: USB, 2: Bluetooth, 3: serial port,
     */
    public Printer(ClientListener listener, final int type) {
        mListener = listener;

        switch (type) {
            case 0:
                tcpClient();
                break;

            case 1:
                usbClient();
                break;

            case 2:
                bluetoothClient();
                break;

            case 3:
                serialPortClient();
                break;

            case 4:
                break;

            default:
                break;
        }
    }

    private void tcpClient() {
        mClient = new TcpClient(new TcpClientListener() {
            @Override
            public void onData(byte[] data) {
                mListener.onData(data);
            }

            @Override
            public void onError(int id, String message) {
                mListener.onError(id, message);
            }

            @Override
            public void onConnected(int id){
                mListener.onConnected(id);
            }

            @Override
            public void onSended(int id){
                mListener.onSended(id);
            }

            @Override
            public void onClosed(int id){
                mListener.onClosed(id);
            }
        });
    }

    private  void usbClient(){
        // TODO
    }

    private  void bluetoothClient(){
        // TODO
    }

    private  void serialPortClient() {
        // TODO
    }

    public int connect(final String serverName, final int port) {
        return mClient.connect(serverName, port);
    }

    public int close() {
        return mClient.close();
    }

    public int send(final byte[] data) {
        return mClient.send(data);
    }

    public int receive() {
        return mClient.receive();
    }
}