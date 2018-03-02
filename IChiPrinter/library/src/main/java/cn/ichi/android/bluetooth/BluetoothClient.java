package cn.ichi.android.bluetooth;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cn.ichi.android.Client;

public class BluetoothClient implements Client {
    private BluetoothClientListener mListener;
    private ExecutorService mExecutor;
    private BlueToothPrinter mPrinter;
    private byte[] mBuffer;
    private AtomicInteger mId;

    private static final int mPollSize = 5;
    private static final int mBufferSize = 8 * 1024 * 1024;

    BluetoothClient(BlueToothPrinter printer, AtomicInteger id, BluetoothClientListener listener) {
        mListener = listener;
        mPrinter = printer;
        mId = id;
        mExecutor = Executors.newFixedThreadPool(mPollSize);
        mBuffer = new byte[mBufferSize];
    }

    public BluetoothClient(BluetoothClientListener listener) {
        mListener = listener;
        mExecutor = Executors.newFixedThreadPool(mPollSize);
        mBuffer = new byte[mBufferSize];
        mId = new AtomicInteger();
    }

    @Override
    public int connect(final String serverName, final int port) {
        final int id = mId.getAndIncrement();
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mPrinter = new BlueToothPrinter(serverName);
                    if (mPrinter.connectPrinter()) {
                        mListener.onConnected(id);
                    } else {
                        mListener.onError(id, mPrinter.getErrorMsg());
                    }
                } catch (Exception e) {
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
                    mPrinter.closeDevice();
                    mListener.onClosed(id);
                } catch (Exception e) {
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
                    if (mPrinter.sendMessage(data)) {
                        mListener.onSent(id);
                    } else {
                        mListener.onError(id, mPrinter.getErrorMsg());
                    }
                } catch (Exception e) {
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
                    int size = mPrinter.receiveMessage(mBuffer);
                    byte [] sub =null;
                    if (size > 0) {
                        sub = Arrays.copyOfRange(mBuffer, 0, size);
                    } else {
                        mListener.onError(id, mPrinter.getErrorMsg());
                    }
                    mListener.onData(sub);
                } catch (Exception e) {
                    mListener.onError(id, e.getMessage());
                }
            }
        });
        return id;
    }
}
