package application.Model;

public interface Client {

	// Ŭ���̾�Ʈ�κ��� �޽����� ���޹޴� �޼ҵ�.
	public void receive();
	
	//Ŭ���̾�Ʈ���� �޽����� �����ϴ� �޼ҵ�.
	public void send(String message);
}
