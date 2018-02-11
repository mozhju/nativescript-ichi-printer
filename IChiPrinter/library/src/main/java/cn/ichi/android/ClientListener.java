package cn.ichi.android;

public interface ClientListener {
    void onData(byte[] data);
    void onError(int id, String message);
    void onConnected(int id);
    void onSended(int id);
    void onClosed(int id);
}
