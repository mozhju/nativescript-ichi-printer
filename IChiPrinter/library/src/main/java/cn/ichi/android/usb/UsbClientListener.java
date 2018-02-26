package cn.ichi.android.usb;

public interface UsbClientListener {
    void onData(byte[] data);
    void onError(int id, String message);
    void onConnected(int id);
    void onSended(int id);
    void onClosed(int id);
}
