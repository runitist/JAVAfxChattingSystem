package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
	Socket socket;

	//Main�� �����ڰ� ����ɶ�, Ŭ���̾�Ʈ �ν��Ͻ� ���� �� �ش� ���Ͽ� ���� ���ε�.
	public Client(Socket socket) {
		this.socket = socket;
		receive();
	}

	public void receive() {
		Runnable thread = new Runnable() {//�ν��Ͻ� ������ ���ſ� �����尡 ������.
			public void run() {
				try {
					while (true) {
						InputStream in = socket.getInputStream();//���Ͽ��� ����ȭ�� byte �迭 ������ ������ ��Ʈ��.
						byte[] buffer = new byte[512];
						int length = in.read(buffer);
						while (length == -1)
							throw new IOException();
						System.out.println("[�޽��� ���� ����] " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName());
						String message = new String(buffer, 0, length, "UTF-8");
						for (Client client : Main.clients) {
							client.send(message);//���⼭���� send ������ ����
						}
					}
				} catch (Exception e) {
					try {
						System.out.println("[�޽��� ���� ����] " + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName());
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		Main.threadpool.submit(thread);//������ �����带 �����ϰ� ��������ִ� Ǯ�� ����.
	}

	public void send(String message) {
		Runnable thread = new Runnable() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes();
					out.write(buffer);
					out.flush();
					System.out.println("[�޽��� �۽� ����] " + socket.getRemoteSocketAddress() + ": "
							+ Thread.currentThread().getName());
				} catch (Exception e) {
					try {
						System.out.println("[�޽��� �۽� ����] " + socket.getRemoteSocketAddress() + ": "
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
