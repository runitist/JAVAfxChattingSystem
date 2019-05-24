package application.Model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import application.Main;


public class ServerImple implements Server {
	
	public Socket socket;
	
	public ServerImple(Socket socket) {
		this.socket = socket;
		receive();
	}
	
	public void receive() {
		Runnable thread  = new Runnable() {
			
			@Override
			public void run() {
				try {
					while(true) {
						InputStream in = socket.getInputStream();
						byte[] buffer = new byte[512];
						int length = in.read(buffer);
						while(length == -1) {
							throw new IOException();
						}
						System.out.println("[�޽������� ����]"+socket.getRemoteSocketAddress() +": "+ Thread.currentThread().getName());
						String message = new String(buffer, 0, length, "UTF-8");
						for(Server client : Main.clients) {
							client.send(message);
						}
					}
				}catch(Exception e){
					try {
						System.out.println("[�޽��� ���� ����]"+socket.getRemoteSocketAddress()+": "+Thread.currentThread().getName());
					}catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		};
		
		Main.threadpool.submit(thread);//static�� ������Ǯ�� ����� Ǯ�� runnable ��ü�� �������.
	}
	
	public void send(String message) {
		Runnable thread = new Runnable() {
			
			@Override
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
					
				} catch (Exception e) {
					try {
						System.out.println("�޽��� �۽� ����"+socket.getRemoteSocketAddress() +": "+ Thread.currentThread());
						Main.clients.remove(ServerImple.this);
						socket.close();
					}catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
			}
		};
		Main.threadpool.submit(thread);
	}
	
}
