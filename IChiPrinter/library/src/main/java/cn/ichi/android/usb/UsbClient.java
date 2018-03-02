package cn.ichi.android.usb;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cn.ichi.android.Client;

public class UsbClient implements Client {
    private UsbClientListener mListener;
    private ExecutorService mExecutor;
    private UsbPrinter mUsbPrinter;
    private byte[] mBuffer;
    private AtomicInteger mId;

    private static final int mPollSize = 5;
    private static final int mBufferSize = 8 * 1024 * 1024;

    UsbClient(UsbPrinter usbPrinter, AtomicInteger id, UsbClientListener listener) {
        mListener = listener;
        mUsbPrinter = usbPrinter;
        mId = id;
        mExecutor = Executors.newFixedThreadPool(mPollSize);
        mBuffer = new byte[mBufferSize];
    }

    public UsbClient(UsbClientListener listener) {
        mListener = listener;
        mExecutor = Executors.newFixedThreadPool(mPollSize);
        mBuffer = new byte[mBufferSize];
        mId = new AtomicInteger();
    }

    @Override
    public int connect(final String printerName, final int port) {
        final int id = mId.getAndIncrement();
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mUsbPrinter = new UsbPrinter(printerName);
                    if (mUsbPrinter.connectPrinter()) {
                        mListener.onConnected(id);
                    } else {
                        mListener.onError(id, mUsbPrinter.getErrorMsg());
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
                    mUsbPrinter.closeDevice();
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
                    if (mUsbPrinter.sendMessage(data)) {
                        mListener.onSent(id);
                    } else {
                        mListener.onError(id, mUsbPrinter.getErrorMsg());
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
                    int size = mUsbPrinter.receiveMessage(mBuffer);
                    byte [] sub = null;
                    if (size > 0) {
                        sub = Arrays.copyOfRange(mBuffer, 0, size);
                    } else {
                        mListener.onError(id, mUsbPrinter.getErrorMsg());
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
