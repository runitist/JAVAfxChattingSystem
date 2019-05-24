package application.Model;

public interface Client {

	// 클라이언트로부터 메시지를 전달받는 메소드.
	public void receive();
	
	//클라이언트에게 메시지를 전달하는 메소드.
	public void send(String message);
}
