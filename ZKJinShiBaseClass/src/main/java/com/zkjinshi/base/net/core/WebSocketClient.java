package com.zkjinshi.base.net.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.zkjinshi.base.util.Constants;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicLineParser;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLException;

/**
 * WebSocketClient操作客户端
 * 开发者：JimmyZhang
 * 日期：2015/8/15
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class WebSocketClient {

    private static final String TAG = "WebSocketClient";
    private final Object mSendLock = new Object();
    private URI mURI;
    private Listener mListener;
    private Socket mSocket;
    private Thread mThread;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private HybiParser mParser;
    private boolean isConnect;
    private int connectTimes;// 连接次数

    public WebSocketClient(URI uri, Listener listener) {
        mURI = uri;
        mListener = listener;
        mParser = new HybiParser(this);

        mHandlerThread = new HandlerThread("websocket-thread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public Listener getListener() {
        return mListener;
    }

    public boolean isConnected() {
        return isConnect;
    }

    public void connect() {
        isConnect = false;
        if (mThread != null && mThread.isAlive()) {
            return;
        }

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String secret = createSecret();

                    int port = (mURI.getPort() != -1) ? mURI.getPort() : (mURI.getScheme().equals("wss") ? 443 : 80);

                    String path = TextUtils.isEmpty(mURI.getPath()) ? "/" : mURI.getPath();
                    if (!TextUtils.isEmpty(mURI.getQuery())) {
                        path += "?" + mURI.getQuery();
                    }

                    String originScheme = mURI.getScheme().equals("wss") ? "https" : "http";
                    URI origin = new URI(originScheme, "//" + mURI.getHost(), null);

                    SocketFactory factory = mURI.getScheme().equals("wss") ? WebSocketFactory.getInstance().getSSLSocketFactory() : SocketFactory.getDefault();
                    mSocket = factory.createSocket(mURI.getHost(), port);

                    PrintWriter out = new PrintWriter(mSocket.getOutputStream());
                    out.print("GET " + path + " HTTP/1.1\r\n");
                    out.print("Upgrade: websocket\r\n");
                    out.print("Connection: Upgrade\r\n");
                    out.print("Host: " + mURI.getHost() + "\r\n");
                    out.print("Origin: " + origin.toString() + "\r\n");
                    out.print("Sec-WebSocket-Key: " + secret + "\r\n");
                    out.print("Sec-WebSocket-Version: 13\r\n");
                    out.print("\r\n");
                    out.flush();

                    WebSocketInputStream stream = new WebSocketInputStream(mSocket.getInputStream());

                    StatusLine statusLine = parseStatusLine(readLine(stream));
                    if (statusLine == null) {
                        throw new HttpException("Received no reply from server.");
                    } else if (statusLine.getStatusCode() != HttpStatus.SC_SWITCHING_PROTOCOLS) {
                        throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                    }

                    String line;
                    boolean validated = false;

                    while (!TextUtils.isEmpty(line = readLine(stream))) {
                        Header header = parseHeader(line);
                        if (header.getName().equals("Sec-WebSocket-Accept")) {
                            String expected = createSecretValidation(secret);
                            String actual = header.getValue().trim();

                            if (!expected.equals(actual)) {
                                throw new HttpException("Bad Sec-WebSocket-Accept header value.");
                            }

                            validated = true;
                        }
                    }

                    if (!validated) {
                        throw new HttpException("No Sec-WebSocket-Accept header.");
                    }

                    mListener.onConnect();
                    isConnect = true;
                    connectTimes = 1;
                    mParser.start(stream);


                } catch (EOFException ex) {
                    Log.e(Constants.ZKJINSHI_BASE_TAG, TAG + "-WebSocket EOF! message:" + ex.getMessage());
                    mListener.onDisconnect(0, "EOF");

                } catch (SSLException ex) {
                    Log.e(Constants.ZKJINSHI_BASE_TAG, TAG + "-Websocket SSL error! message:" + ex.getMessage());
                    mListener.onDisconnect(0, "SSL");

                } catch (Exception ex) {
                    mListener.onError(ex);
                }
            }
        });
        mThread.start();
    }

    /**
     * 延时重连，减低耗电量
     */
    public void reconnect() {
        disconnect();
        connectTimes++;
        if (connectTimes <= 300) {
            connectTimes = (connectTimes + 3) * 2;
        }
        try {
            Thread.sleep(connectTimes * 1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        connect();
    }

    public void disconnect() {
        if (mSocket != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != mSocket) {
                            mSocket.close();
                            mSocket = null;
                        }
                    } catch (IOException ex) {
                        Log.e(Constants.ZKJINSHI_BASE_TAG, TAG + "-Error while disconnecting:" + ex.getMessage());
                        mListener.onError(ex);
                    }
                }
            });
        }
    }

    public void sendMessage(String data) {
        sendFrame(mParser.frame(data));
    }

    public void sendMessage(byte[] data) {
        sendFrame(mParser.frame(data));
    }

    private StatusLine parseStatusLine(String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }
        return BasicLineParser.parseStatusLine(line, new BasicLineParser());
    }

    private Header parseHeader(String line) {
        return BasicLineParser.parseHeader(line, new BasicLineParser());
    }

    private String readLine(WebSocketInputStream reader) throws IOException {
        int readChar = reader.read();
        if (readChar == -1) {
            return null;
        }
        StringBuilder string = new StringBuilder("");
        while (readChar != '\n') {
            if (readChar != '\r') {
                string.append((char) readChar);
            }

            readChar = reader.read();
            if (readChar == -1) {
                return null;
            }
        }
        return string.toString();
    }

    private String createSecret() {
        byte[] nonce = new byte[16];
        for (int i = 0; i < 16; i++) {
            nonce[i] = (byte) (Math.random() * 256);
        }
        return Base64.encodeToString(nonce, Base64.DEFAULT).trim();
    }

    private String createSecretValidation(String secret) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update((secret + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes());
            return Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    void sendFrame(final byte[] frame) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (mSendLock) {
                        if (null != mSocket) {
                            OutputStream outputStream = mSocket.getOutputStream();
                            outputStream.write(frame);
                            outputStream.flush();
                        }
                    }
                } catch (IOException e) {
                    mListener.onError(e);
                }
            }
        });
    }

    public interface Listener {

        public void onConnect();

        public void onMessage(String message);

        public void onMessage(byte[] data);

        public void onDisconnect(int code, String reason);

        public void onError(Exception error);
    }
}
