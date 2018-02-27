package cn.ichi.android.serialport;

public interface SerialPortClientListener {
    void onData(byte[] data);
    void onError(int id, String message);
    void onConnected(int id);
    void onSended(int id);
    void onClosed(int id);
}
