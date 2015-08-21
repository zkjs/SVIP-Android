package com.zkjinshi.base.net.core;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.zkjinshi.base.net.listener.SendListener;
import com.zkjinshi.base.net.listener.SocketListener;
import com.zkjinshi.base.net.status.PacketUtil;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicLineParser;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.SocketFactory;
import javax.net.ssl.SSLException;

import static com.zkjinshi.base.net.core.SocketStatus.WEBSOCKET_CONNECT_SUCCESS;
import static com.zkjinshi.base.net.core.SocketStatus.WEBSOCKET_EOF;
import static com.zkjinshi.base.net.core.SocketStatus.WEBSOCKET_SEND_RES;
import static com.zkjinshi.base.net.core.SocketStatus.WEBSOCKET_SEND_TIMEOUT;
import static com.zkjinshi.base.net.core.SocketStatus.WEBSOCKET_SSL_ERROR;
import static com.zkjinshi.base.net.core.SocketStatus.WEBSOCKET_TEXT_READ;

/**
 * 网络请求自定义异步线程
 * 开发者:JimmyZhang
 * 日期：2015/7/19
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class SocketAsyncTask extends AsyncTask<String, SocketStatus, SocketStatus>  {
	
	public static final String TAG = "SocketAsyncTask";

	private boolean mMasking = true;
	private boolean mFinal;
	private boolean mMasked;

	private int mStage;
	private int mOpcode;
	private int mLengthSize;
	private int mLength;
	private int mMode;

	private byte[] mMask = new byte[0];
	private byte[] mPayload = new byte[0];

	private boolean mClosed = false;

	private ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();

	private static final int BYTE = 255;
	private static final int FIN = 128;
	private static final int MASK = 128;
	private static final int RSV1 = 64;
	private static final int RSV2 = 32;
	private static final int RSV3 = 16;
	private static final int OPCODE = 15;
	private static final int LENGTH = 127;

	private static final int MODE_TEXT = 1;
	private static final int MODE_BINARY = 2;

	private static final int OP_CONTINUATION = 0;
	private static final int OP_TEXT = 1;
	private static final int OP_BINARY = 2;
	private static final int OP_CLOSE = 8;
	private static final int OP_PING = 9;
	private static final int OP_PONG = 10;

	private ExecutorService mReadThreadPool;
	private ExecutorService mSendThreadPool;
	private SendTimeOutThread mSendTimeOutThread;

	private AlarmManager mAlarmManager;
	private PendingIntent mPendingIntent;

	private final List<Integer> OPCODES = Arrays.asList(OP_CONTINUATION,
			OP_TEXT, OP_BINARY, OP_CLOSE, OP_PING, OP_PONG);

	private final List<Integer> FRAGMENTED_OPCODES = Arrays.asList(
			OP_CONTINUATION, OP_TEXT, OP_BINARY);
	
	private int mHeartbeat = 60000;
	private final Object mSLMLock = new Object();
	private final Object mSendLock = new Object();
	
	private int readThreadLimitNum = 10;
	private int sendThreadLimitNum = 10;
	private URI url;
	private Socket socket;
	private Context context;
	private SocketListener socketListener;
	private Handler handler;
	
	private String createSecret() {
		byte[] nonce = new byte[16];
		for (int i = 0; i < 16; i++) {
			nonce[i] = (byte) (Math.random() * 256);
		}
		return Base64.encodeToString(nonce, Base64.DEFAULT).trim();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
	private String readLine(SocketDataInputStream reader)
			throws IOException {
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
	
	private StatusLine parseStatusLine(String line) {
		if (TextUtils.isEmpty(line)) {
			return null;
		}
		return BasicLineParser.parseStatusLine(line, new BasicLineParser());
	}
	
	private Header parseHeader(String line) {
		return BasicLineParser.parseHeader(line, new BasicLineParser());
	}

	private String createSecretValidation(String secret) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update((secret + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
					.getBytes());
			return Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected SocketStatus doInBackground(String... params) {
		mReadThreadPool = Executors.newFixedThreadPool(readThreadLimitNum);
		mSendThreadPool = Executors.newFixedThreadPool(sendThreadLimitNum);
		mSendTimeOutThread = new SendTimeOutThread();
		mSendTimeOutThread.start();
		SocketStatus socketStatus = null;
		try {
			String secret = createSecret();

			int port = (url.getPort() != -1) ? url.getPort() : (url
					.getScheme().equals("wss") ? 443 : 80);

			String path = TextUtils.isEmpty(url.getPath()) ? "/" : url
					.getPath();
			if (!TextUtils.isEmpty(url.getQuery())) {
				path += "?" + url.getQuery();
			}

			String originScheme = url.getScheme().equals("wss") ? "https"
					: "http";
			URI origin = new URI(originScheme, "//" + url.getHost(), null);

			SocketFactory factory = url.getScheme().equals("wss") ? WebSSLSocketFactory.getInstance().getSSLSocketFactory()
					: SocketFactory.getDefault();
			socket = factory.createSocket(url.getHost(), port);

			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.print("GET " + path + " HTTP/1.1\r\n");
			out.print("Upgrade: websocket\r\n");
			out.print("Connection: Upgrade\r\n");
			out.print("Host: " + url.getHost() + "\r\n");
			out.print("Origin: " + origin.toString() + "\r\n");
			out.print("Sec-WebSocket-Key: " + secret + "\r\n");
			out.print("Sec-WebSocket-Version: 13\r\n");
			out.print("\r\n");
			out.flush();

			SocketDataInputStream stream = new SocketDataInputStream(
					socket.getInputStream());

			// Read HTTP response status line.
			StatusLine statusLine = parseStatusLine(readLine(stream));
			if (statusLine == null) {
				throw new HttpException("Received no reply from server.");
			} else if (statusLine.getStatusCode() != HttpStatus.SC_SWITCHING_PROTOCOLS) {
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}

			// Read HTTP response headers.
			String line;
			boolean validated = false;

			while (!TextUtils.isEmpty(line = readLine(stream))) {
				Header header = parseHeader(line);
				if (header.getName().equals("Sec-WebSocket-Accept")) {
					String expected = createSecretValidation(secret);
					String actual = header.getValue().trim();

					if (!expected.equals(actual)) {
						throw new HttpException(
								"Bad Sec-WebSocket-Accept header value.");
					}

					validated = true;
				}
			}

			if (!validated) {
				throw new HttpException("No Sec-WebSocket-Accept header.");
			}

			// 登录成功后，启动心跳
			mAlarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			mPendingIntent = PendingIntent.getBroadcast(context, 0,
					new Intent("HEARTBEAT"),
					PendingIntent.FLAG_UPDATE_CURRENT);
			// 启动心跳定时器
			mAlarmManager.cancel(mPendingIntent);
			long triggerAtTime = SystemClock.elapsedRealtime() + mHeartbeat;
			mAlarmManager.setRepeating(
					AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
					mHeartbeat, mPendingIntent);
			socketStatus = new SocketStatus(
					WEBSOCKET_CONNECT_SUCCESS, null);
			publishProgress(socketStatus);
			this.start(stream);

		} catch (EOFException ex) {
			Log.e(TAG, "WebSocket EOF!");
			socketStatus = new SocketStatus(WEBSOCKET_EOF,
					null);
		} catch (SSLException ex) {
			Log.e(TAG, "Websocket SSL error!");
			ex.printStackTrace();
			socketStatus = new SocketStatus(
					WEBSOCKET_SSL_ERROR, null);
		} catch (Exception ex) {
			socketListener.onError(ex);
		}
		return socketStatus;
	}

	@Override
	protected void onProgressUpdate(SocketStatus... socketStatuses) {
		try{
			JSONObject packet;
			Integer type;
			Long timestamp;
			switch (socketStatuses[0].getCode()) {
				case WEBSOCKET_CONNECT_SUCCESS:
					socketListener.onConnect();
					break;
				case WEBSOCKET_EOF:
					cancelHeartbeat();
					socketListener.onDisconnect(0, "EOF");
					break;
				case WEBSOCKET_SSL_ERROR:
					cancelHeartbeat();
					socketListener.onDisconnect(0, "SSL");
					break;
				case WEBSOCKET_TEXT_READ:
					packet = socketStatuses[0].getMsg();
					type = packet.getInt("type");
					if (MapContext.getInstance().getResponseListenerMap().containsKey(type)) {
						MapContext.getInstance().getResponseListenerMap().get(type).onRead(true, packet);
					}
					break;
				case WEBSOCKET_SEND_RES:
					packet = socketStatuses[0].getMsg();
					type = packet.getInt("type");
					timestamp = packet.getLong("timestamp");
					synchronized (mSLMLock) {
						SendListener sendListener = MapContext.getInstance().getRequestListenerMap().get(type + "/"
								+ timestamp);
						MapContext.getInstance().getRequestListenerMap().remove(type + "/" + timestamp);
						sendListener.onSend(true, packet);
					}
					break;
				case WEBSOCKET_SEND_TIMEOUT:
					packet = socketStatuses[0].getMsg();
					type = packet.getInt("type");
					timestamp = packet.getLong("timestamp");
					synchronized (mSLMLock) {
						SendListener sendListener = MapContext.getInstance().getRequestListenerMap().get(type + "/"
								+ timestamp);
						MapContext.getInstance().getRequestListenerMap().remove(type + "/" + timestamp);
						sendListener.onSend(false, null);
					}
					break;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private byte[] mask(byte[] payload, byte[] mask, int offset) {
		if (mask.length == 0)
			return payload;

		for (int i = 0; i < payload.length - offset; i++) {
			payload[offset + i] = (byte) (payload[offset + i] ^ mask[i % 4]);
		}
		return payload;
	}

	public void start(SocketDataInputStream stream) throws IOException,
			InterruptedException {
		while (true) {
			if (stream.available() == -1)
				break;
			switch (mStage) {
			case 0:
				parseOpcode(stream.readByte());
				break;
			case 1:
				parseLength(stream.readByte());
				break;
			case 2:
				parseExtendedLength(stream.readBytes(mLengthSize));
				break;
			case 3:
				mMask = stream.readBytes(4);
				mStage = 4;
				break;
			case 4:
				mPayload = stream.readBytes(mLength);
				emitFrame();
				mStage = 0;
				break;
			}
		}
		socketListener.onDisconnect(0, "EOF");
	}

	private void parseOpcode(byte data) throws ProtocolError {
		boolean rsv1 = (data & RSV1) == RSV1;
		boolean rsv2 = (data & RSV2) == RSV2;
		boolean rsv3 = (data & RSV3) == RSV3;

		if (rsv1 || rsv2 || rsv3) {
			throw new ProtocolError("RSV not zero");
		}

		mFinal = (data & FIN) == FIN;
		mOpcode = (data & OPCODE);
		mMask = new byte[0];
		mPayload = new byte[0];

		if (!OPCODES.contains(mOpcode)) {
			throw new ProtocolError("Bad opcode");
		}

		if (!FRAGMENTED_OPCODES.contains(mOpcode) && !mFinal) {
			throw new ProtocolError("Expected non-final packet");
		}

		mStage = 1;
	}

	private void parseLength(byte data) {
		mMasked = (data & MASK) == MASK;
		mLength = (data & LENGTH);

		if (mLength >= 0 && mLength <= 125) {
			mStage = mMasked ? 3 : 4;
		} else {
			mLengthSize = (mLength == 126) ? 2 : 8;
			mStage = 2;
		}
	}

	private void parseExtendedLength(byte[] buffer) throws ProtocolError {
		mLength = getInteger(buffer);
		mStage = mMasked ? 3 : 4;
	}

	public byte[] frame(String data) {
		return frame(data, OP_TEXT, -1);
	}

	public byte[] frame(byte[] data) {
		return frame(data, OP_BINARY, -1);
	}

	private byte[] frame(byte[] data, int opcode, int errorCode) {
		return frame((Object) data, opcode, errorCode);
	}

	private byte[] frame(String data, int opcode, int errorCode) {
		return frame((Object) data, opcode, errorCode);
	}

	private byte[] frame(Object data, int opcode, int errorCode) {
		if (mClosed)
			return null;

		Log.d(TAG, "Creating frame for: " + data + " op: " + opcode
				+ " err: " + errorCode);

		byte[] buffer = (data instanceof String) ? decode((String) data)
				: (byte[]) data;
		int insert = (errorCode > 0) ? 2 : 0;
		int length = buffer.length + insert;
		int header = (length <= 125) ? 2 : (length <= 65535 ? 4 : 10);
		int offset = header + (mMasking ? 4 : 0);
		int masked = mMasking ? MASK : 0;
		byte[] frame = new byte[length + offset];

		frame[0] = (byte) ((byte) FIN | (byte) opcode);

		if (length <= 125) {
			frame[1] = (byte) (masked | length);
		} else if (length <= 65535) {
			frame[1] = (byte) (masked | 126);
			frame[2] = (byte) Math.floor(length / 256);
			frame[3] = (byte) (length & BYTE);
		} else {
			frame[1] = (byte) (masked | 127);
			frame[2] = (byte) (((int) Math.floor(length / Math.pow(2, 56))) & BYTE);
			frame[3] = (byte) (((int) Math.floor(length / Math.pow(2, 48))) & BYTE);
			frame[4] = (byte) (((int) Math.floor(length / Math.pow(2, 40))) & BYTE);
			frame[5] = (byte) (((int) Math.floor(length / Math.pow(2, 32))) & BYTE);
			frame[6] = (byte) (((int) Math.floor(length / Math.pow(2, 24))) & BYTE);
			frame[7] = (byte) (((int) Math.floor(length / Math.pow(2, 16))) & BYTE);
			frame[8] = (byte) (((int) Math.floor(length / Math.pow(2, 8))) & BYTE);
			frame[9] = (byte) (length & BYTE);
		}

		if (errorCode > 0) {
			frame[offset] = (byte) (((int) Math.floor(errorCode / 256)) & BYTE);
			frame[offset + 1] = (byte) (errorCode & BYTE);
		}
		System.arraycopy(buffer, 0, frame, offset + insert, buffer.length);

		if (mMasking) {
			byte[] mask = { (byte) Math.floor(Math.random() * 256),
					(byte) Math.floor(Math.random() * 256),
					(byte) Math.floor(Math.random() * 256),
					(byte) Math.floor(Math.random() * 256) };
			System.arraycopy(mask, 0, frame, header, mask.length);
			mask(frame, mask, offset);
		}

		return frame;
	}

	public void ping(String message) {
		send(frame(message, OP_PING, -1));
	}

	public void close(int code, String reason) {
		if (mClosed)
			return;
		send(frame(reason, OP_CLOSE, code));
		mClosed = true;
	}

	private void emitFrame() throws IOException, InterruptedException {
		byte[] payload = mask(mPayload, mMask, 0);
		int opcode = mOpcode;

		if (opcode == OP_CONTINUATION) {
			if (mMode == 0) {
				throw new ProtocolError("Mode was not set.");
			}
			mBuffer.write(payload);
			if (mFinal) {
				byte[] message = mBuffer.toByteArray();
				if (mMode == MODE_TEXT) {
					// 插入读队列
					QueueContext.getInstance().getResponseQueue()
							.put(encode(message));
					mReadThreadPool.execute(new ReadThread());
				} else {
					// 未开放Byte型
					Log.e(TAG, "未开放Byte型");
				}
				reset();
			}

		} else if (opcode == OP_TEXT) {
			if (mFinal) {
				// 插入读队列
				String messageText = encode(payload);
				QueueContext.getInstance().getResponseQueue()
						.put(messageText);
				mReadThreadPool.execute(new ReadThread());
			} else {
				mMode = MODE_TEXT;
				mBuffer.write(payload);
			}

		} else if (opcode == OP_BINARY) {
			if (mFinal) {
				// 未开放Byte型
				Log.e(TAG, "未开放Byte型");
			} else {
				mMode = MODE_BINARY;
				mBuffer.write(payload);
			}

		} else if (opcode == OP_CLOSE) {
			int code = (payload.length >= 2) ? 256 * payload[0]
					+ payload[1] : 0;
			String reason = (payload.length > 2) ? encode(slice(payload, 2))
					: null;
			Log.d(TAG, "Got close op! " + code + " " + reason);
			socketListener.onDisconnect(code, reason);

		} else if (opcode == OP_PING) {
			if (payload.length > 125) {
				throw new ProtocolError("Ping payload too large");
			}
			Log.d(TAG, "Sending pong!!");
			sendFrame(frame(payload, OP_PONG, -1));

		} else if (opcode == OP_PONG) {
			String message = encode(payload);
			// FIXME: Fire callback...
			Log.d(TAG, "Got pong! " + message);
		}
	}

	private void reset() {
		mMode = 0;
		mBuffer.reset();
	}

	private String encode(byte[] buffer) {
		try {
			return new String(buffer, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] decode(String string) {
		try {
			return (string).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private int getInteger(byte[] bytes) throws ProtocolError {
		long i = byteArrayToLong(bytes, 0, bytes.length);
		if (i < 0 || i > Integer.MAX_VALUE) {
			throw new ProtocolError("Bad integer: " + i);
		}
		return (int) i;
	}

	@SuppressLint("NewApi")
	private byte[] slice(byte[] array, int start) {
		return Arrays.copyOfRange(array, start, array.length);
	}

	private long byteArrayToLong(byte[] b, int offset, int length) {
		if (b.length < length)
			throw new IllegalArgumentException(
					"length must be less than or equal to b.length");

		long value = 0;
		for (int i = 0; i < length; i++) {
			int shift = (length - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	public class ProtocolError extends IOException {
		public ProtocolError(String detailMessage) {
			super(detailMessage);
		}
	}

	public void send(JSONObject msg, SendListener sendListener) {
		try {
			Integer type = msg.getInt("type") + 1;
			Long timestamp = msg.getLong("timestamp");
			sendListener.setType(type);
			sendListener.setTimestamp(timestamp);
			synchronized (mSLMLock) {
				MapContext.getInstance().getRequestListenerMap().put(type + "/" + timestamp, sendListener);
			}
			QueueContext.getInstance().getRequestQueue().put(msg);
			mSendThreadPool.execute(new SendThread());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendHeartbeat() {
		try {
			JSONObject heartbeat = new JSONObject();
			heartbeat.put("type", PacketUtil.MSG_Heartbeat);
			QueueContext.getInstance().getRequestQueue().put(heartbeat);
			mSendThreadPool.execute(new SendThread());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void send(String data) {
		sendFrame(frame(data));
	}

	public void send(byte[] data) {
		sendFrame(frame(data));
	}

	private void sendFrame(final byte[] frame) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (mSendLock) {
						if (socket == null) {
							throw new IllegalStateException(
									"Socket not connected");
						}
						OutputStream outputStream = socket
								.getOutputStream();
						outputStream.write(frame);
						outputStream.flush();
					}
				} catch (IOException e) {
					socketListener.onError(e);
				}
			}
		});
	}

	private void cancelHeartbeat() {
		mAlarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		mPendingIntent = PendingIntent.getBroadcast(context, 0,
				new Intent("HEARTBEAT"), PendingIntent.FLAG_UPDATE_CURRENT);
		mAlarmManager.cancel(mPendingIntent);
	}

	private class ReadThread implements Runnable {
		@Override
		public void run() {
			Integer type = null;
			Long timestamp = null;
			String packetBody;
			JSONObject packet;
			try {
				packetBody = QueueContext.getInstance().getResponseQueue()
						.take();
				packet = new JSONObject(packetBody);
				Log.d("收到数据包：", packet.toString());
				type = packet.getInt("type");
				if(type != PacketUtil.MSG_Heartbeat){
					timestamp = packet.getLong("timestamp");
				}

				if (MapContext.getInstance().getResponseListenerMap().containsKey(type)) {
					publishProgress(new SocketStatus(
							WEBSOCKET_TEXT_READ, packet));

				} else {
					synchronized (mSLMLock) {
						if (MapContext.getInstance().getRequestListenerMap().containsKey(type + "/"
								+ timestamp)) {
							publishProgress(new SocketStatus(
									WEBSOCKET_SEND_RES, packet));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class SendThread implements Runnable {

		@Override
		public void run() {
			JSONObject packet;
			try {
				packet = QueueContext.getInstance().getRequestQueue()
						.take();
				synchronized (this) {
					send(packet.toString());
					Log.d("发送数据包：", packet.toString());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class SendTimeOutThread extends Thread {

		@Override
		public void run() {
			Set<String> keySet;
			String key;
			Integer type;
			Long timestamp;
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (mSLMLock) {
					keySet = MapContext.getInstance().getRequestListenerMap().keySet();
					if (keySet != null) {
						for (Iterator it = keySet.iterator(); it.hasNext();) {
							key = (String) it.next();
							timestamp = MapContext.getInstance().getRequestListenerMap().get(key)
									.getTimestamp();
							type = MapContext.getInstance().getRequestListenerMap().get(key).getType();
							if (timestamp + 10000 < System
									.currentTimeMillis()) {
								JSONObject msg = new JSONObject();
								try {
									msg.put("type", type);
									msg.put("timestamp", timestamp);
								}catch (Exception e) {
									e.printStackTrace();
								}
								publishProgress(new SocketStatus(
										WEBSOCKET_SEND_TIMEOUT,
										msg));
							}
						}
					}
				}
			}
		}
	}
	
	public void disconnect(){
		if (socket != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					try {
						socket.close();
						socket = null;
					} catch (IOException ex) {
						Log.d(TAG, "Error while disconnecting", ex);
						socketListener.onError(ex);
					}
				}
			});
		}
	}

	public void setReadThreadLimitNum(int readThreadLimitNum) {
		this.readThreadLimitNum = readThreadLimitNum;
	}

	public void setSendThreadLimitNum(int sendThreadLimitNum) {
		this.sendThreadLimitNum = sendThreadLimitNum;
	}

	public void setUrl(URI url) {
		this.url = url;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setSocketListener(SocketListener socketListener) {
		this.socketListener = socketListener;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Socket getSocket() {
		return socket;
	}
}
