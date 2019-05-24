package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	Socket socket;

	//Main의 생성자가 실행될때, 클라이언트 인스턴스 생성 후 해당 소켓에 따른 바인딩.
	public Client(Socket socket) {
		this.socket = socket;
		receive();
	}

	public void receive() {
		Runnable thread = new Runnable() {//인스턴스 생성후 수신용 스레드가 생성됨.
			public void run() {
				try {
					while (true) {
						InputStream in = socket.getInputStream();//소켓에서 직렬화된 byte 배열 정보를 얻어오는 스트림.
						byte[] buffer = new byte[512];
						int length = in.read(buffer);
						while (length == -1)
							throw new IOException();
						System.out.println("[메시지 수신 성공] " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName());
						String message = new String(buffer, 0, length, "UTF-8");
						for (Client client : Main.clients) {
							client.send(message);//여기서부터 send 쓰레드 생성
						}
					}
				} catch (Exception e) {
					try {
						System.out.println("[메시지 수신 오류] " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName());
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		Main.threadpool.submit(thread);//생성된 쓰레드를 안전하게 실행시켜주는 풀에 넣음.
	}

	public void send(String message) {
		Runnable thread = new Runnable() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes();
					out.write(buffer);
					out.flush();
					System.out.println("[메시지 송신 성공] " + socket.getRemoteSocketAddress() + ": "
							+ Thread.currentThread().getName());
				} catch (Exception e) {
					try {
						System.out.println("[메시지 송신 오류] " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName());
						Main.clients.remove(Client.this);
						socket.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		Main.threadpool.submit(thread);
	}

}
