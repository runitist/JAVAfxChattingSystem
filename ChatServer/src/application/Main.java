package application;
	
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import application.Model.ClientImple;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Main extends Application {
	
	public static ExecutorService threadpool;//���� �����带 ȿ������ ������Ǯ�� ����ϵ��� ���ִ� ��ü.
	public static Vector<ClientImple> clients = new Vector<ClientImple>();//���� Ŭ���̾�Ʈ ��ü�� ��������ϴ� ����

	ServerSocket serverSocket;
	
	//���� ���� ���� �޼ҵ�. ä�� ���α׷��� ����. ��û�� ����.
	public void startServer(String IP, int port) {
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(IP, port));//���� ������ ���� ��İ� �ּҸ� ����
		} catch (Exception e) {
			e.printStackTrace();
			if(!serverSocket.isClosed()) {
				stopServer();
			}
		}
		
		Runnable thread = new Runnable() {//����ó�� ������.
			
			@Override
			public void run() {
				while(true) {
					try {
						Socket socket = serverSocket.accept();//���������� �ݺ��Ǹ�, Ŭ���̾�Ʈ ��û�� ��ٸ�.
						clients.add(new ClientImple(socket));//��û�� �´ٸ� ���Ϳ� Ŭ���̾�Ʈ ��ü�� ����.
						System.out.println("[Ŭ���̾�Ʈ ����]"+socket.getRemoteSocketAddress()+": "+Thread.currentThread().getName());
					} catch (Exception e) {
						if(!serverSocket.isClosed()) {
							stopServer();
						}
						break;
					}
				}
				
			}
		};
		threadpool = Executors.newCachedThreadPool();
		threadpool.submit(thread);
	}
	
	//���� ���� ���� �޼ҵ�
	public void stopServer() {
		try {
			Iterator<ClientImple> iterator = clients.iterator();
			while (iterator.hasNext()) {
				iterator.next().socket.close();
				iterator.remove();
			}
			
			if(serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
			if(threadpool != null && threadpool.isShutdown()) {
				threadpool.shutdown();
			}
		} catch (Exception e) {
			
		}
	}
	
	//GUI ���� �� ���α׷� ���� ������ ��� �޼ҵ�
	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(5));
		
		TextArea textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("�������", 15));
		root.setCenter(textArea);
		
		Button toggleButton = new Button("�����ϱ�");
		toggleButton.setMaxWidth(Double.MAX_VALUE);
		BorderPane.setMargin(toggleButton, new Insets(1, 0, 0, 0));
		root.setBottom(toggleButton);
		
		String IP = "127.0.0.1";
		int port = 9876;
		
		toggleButton.setOnAction(event -> {
			if(toggleButton.getText().equals("�����ϱ�")) {
				startServer(IP, port);
				Platform.runLater(()->{//GUI�� �غ�� ���� �Ʒ� ���� ����.
					String message = String.format("[��������]\n", IP, port);
					textArea.appendText(message);
					toggleButton.setText("�����ϱ�");
				});
			}else {
				stopServer();
				Platform.runLater(()->{
					String message = String.format("[���� ����]\n", IP, port);
					textArea.appendText(message);
					toggleButton.setText("�����ϱ�");
				});
			}
		});
		Scene scene = new Scene(root, 400, 400);
		primaryStage.setTitle("[ä�� ����]");
		primaryStage.setOnCloseRequest(event->stopServer());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
