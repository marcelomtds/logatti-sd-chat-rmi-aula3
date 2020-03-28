package principal;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ChatAula extends UnicastRemoteObject implements IChatAula {

	private static final long serialVersionUID = -8788460791622357200L;

	protected ChatAula() throws RemoteException {
		super();
	}

	public void sendMessage(Message msg) throws RemoteException {
		Message.addMessage(msg);
	}

	public List<Message> retrieveMessage() throws RemoteException {
		return Message.getListMessage();
	}

}
