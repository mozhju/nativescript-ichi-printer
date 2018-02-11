package cn.ichi.android.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cn.ichi.android.Client;

public class TcpClient implements Client {
    private TcpClientListener mListener;
    private ExecutorService mExecutor;
    private Socket mSocket;
    private byte[] mBuffer;
    private AtomicInteger mId;

    private static final int mPollSize = 5;
    private static final int mBufferSize = 8 * 1024 * 1024;

    TcpClient(Socket socket, AtomicInteger id, TcpClientListener listener) {
        mListener = listener;
        mSocket = socket;
        mId = id;
        mExecutor = Executors.newFixedThreadPool(mPollSize);
        mBuffer = new byte[mBufferSize];
    }

    public TcpClient(TcpClientListener listener) {
        mListener = listener;
        mExecutor = Executors.newFixedThreadPool(mPollSize);
        mBuffer = new byte[mBufferSize];
        mId = new AtomicInteger();
    }

    public Socket getNativeSocket() {
        return mSocket;
    }

    @Override
    public int connect(final String serverName, final int port) {
        final int id = mId.getAndIncrement();
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(serverName, port);
                    mListener.onConnected(id);
                } catch (IOException e) {
                    mListener.onError(id, e.getMessage());
                }
            }
        });
        return id;
    }

    @Override
    public int close() {
        final int id = mId.getAndIncrement();
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket.close();
                    mListener.onClosed(id);
                } catch (IOException e) {
                    mListener.onError(id, e.getMessage());
                }

            }
        });
        return id;
    }

    @Override
    public int send(final byte[] data) {
        final int id = mId.getAndIncrement();
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket.getOutputStream().write(data);
                    mListener.onSended(id);
                } catch (IOException e) {
                    mListener.onError(id, e.getMessage());
                }
            }
        });
        return id;
    }

    @Override
    public int receive() {
        final int id = mId.getAndIncrement();
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    int size = mSocket.getInputStream().read(mBuffer);
                    byte [] sub =null;
                    if (size > 0) {
                        sub = Arrays.copyOfRange(mBuffer, 0, size);
                    }
                    mListener.onData(sub);
                } catch (IOException e) {
                    mListener.onError(id, e.getMessage());
                }
            }
        });
        return id;
    }
}
