package principal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {

	private static final long serialVersionUID = -2140876206291769116L;

	private String user;
	private String message;

	private static List<Message> messages = new ArrayList<Message>();

	public Message(String user, String message) {
		super();
		this.user = user;
		this.message = message;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static List<Message> getListMessage() {
		return Message.messages;
	}

	public static void addMessage(Message msg) {
		Message.messages.add(msg);
	}

}
