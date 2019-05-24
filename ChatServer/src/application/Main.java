package application;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

//학습용이라 나중에 리펙토링이 필요.
public class Main extends Application {

	public static ExecutorService threadpool;// 여러 스레드를 효율적인 쓰레드풀로 사용하도록 해주는 객체.
	public static Vector<Client> clients = new Vector<Client>();// 여러 클라이언트 객체를 저장관리하는 벡터
	//쓰레드 동기화 처리가 된 리스트가 벡터다.

	private ServerSocket serverSocket;

	// 서버 소켓 실행 메소드. 채팅 프로그램의 시작. 요청을 받음.
	public void startServer(String IP, int port) {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(IP, port));// 서버 소켓의 실행 방식과 주소를 지정
		} catch (Exception e) {
			e.printStackTrace();
			if (!serverSocket.isClosed()) {
				stopServer();
			}
		}

		Runnable thread = new Runnable() {// 병렬처리 쓰레드.
			@Override
			public void run() {
				while (true) {
					try {
						Socket socket = serverSocket.accept();// 병렬적으로 반복되며, 클라이언트 요청을 기다림.
						clients.add(new Client(socket));// 요청이 온다면 벡터에 클라이언트 객체를 생성하여 넣음.
						System.out.println("[클라이언트 접속]" + socket.getRemoteSocketAddress() + ": "
								+ Thread.currentThread().getName());
					} catch (Exception e) {
						if (!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
			}
		};
		threadpool = Executors.newCachedThreadPool();//모든 쓰레드를 관리하는 풀을 실행시킴.
		threadpool.submit(thread);
	}

	// 서버 소켓 종료 메소드
	public void stopServer() {
		try {
			Iterator<Client> iterator = clients.iterator();
			while (iterator.hasNext()) {
				iterator.next().socket.close();
				iterator.remove();
			}

			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			if (threadpool != null && threadpool.isShutdown()) {
				threadpool.shutdown();
			}
		} catch (Exception e) {

		}
	}

	// GUI 생성 및 프로그램 실행 로직이 담긴 메소드. 아래부턴 GUI 뷰단.
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));

		TextArea textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("나눔고딕", 15));
		root.setCenter(textArea);

		Button toggleButton = new Button("시작하기");
		toggleButton.setMaxWidth(Double.MAX_VALUE);
		BorderPane.setMargin(toggleButton, new Insets(1, 0, 0, 0));
		root.setBottom(toggleButton);

		String IP = "127.0.0.1";
		int port = 9876;

		toggleButton.setOnAction(event -> {
			if (toggleButton.getText().equals("시작하기")) {
				startServer(IP, port);
				Platform.runLater(() -> {// GUI가 준비된 이후 아래 로직 실행.
					String message = String.format("[서버시작]\n", IP, port);
					textArea.appendText(message);
					toggleButton.setText("종료하기");
				});
			} else {
				stopServer();
				Platform.runLater(() -> {
					String message = String.format("[서버 종료]\n", IP, port);
					textArea.appendText(message);
					toggleButton.setText("시작하기");
				});
			}
		});
		Scene scene = new Scene(root, 400, 400);
		primaryStage.setTitle("[채팅 서버]");
		primaryStage.setOnCloseRequest(event -> stopServer());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
