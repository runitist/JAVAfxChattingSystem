package application.Model;

public interface Server {

	// Ŭ���̾�Ʈ�κ��� �޽����� ���޹޴� �޼ҵ�.
	public void receive();
	
	//Ŭ���̾�Ʈ���� �޽����� �����ϴ� �޼ҵ�.
	public void send(String message);
}
