package principal;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TestClient {

	static String nome;
	static Connection connection;
	static Statement stmt;
	static JTextArea jtTextArea = new JTextArea(30, 50);
	static IChatAula objChat;

	public static void main(String[] args) throws SQLException {

		lerNome();
		criarTela();
		criarConexaoDB();
		criarConexaoRMI();
		refreshChat();

	}

	private static void criarConexaoRMI() {
		try {
			objChat = (IChatAula) Naming.lookup("rmi://localhost:8282/chat");
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	private static void refreshChat() {
		try {
			while (true) {
				recuperarChatDB();
				recuperarChatRMI();
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void recuperarChatDB() {
		String sql = String.format("select message from chat");
		try {
			ResultSet rs = stmt.executeQuery(sql);
			jtTextArea.setText("");
			while (rs.next()) {
				jtTextArea.append(rs.getString("message") + "\n");
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void recuperarChatRMI() {
		try {
			System.out.println(returnMessage(objChat.retrieveMessage()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private static void inserirMensagemDB(final String msg) {
		String sqlMessage = String.format("insert into chat (message) values ('%s')", formatMessage(msg));
		try {
			stmt.executeUpdate(sqlMessage);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void inserirMensagemRMI(final String msg) {
		try {
			objChat = (IChatAula) Naming.lookup("rmi://localhost:8282/chat");
			objChat.sendMessage(new Message(nome, formatMessage(msg)));
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	private static void criarConexaoDB() {
		connection = new ConnectionDB().getConnetion();
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static void lerNome() {
		nome = JOptionPane.showInputDialog("Bem vindo ao chat. Qual é o seu nome?");
	}

	private static void criarTela() {
		JFrame jFrame = new JFrame("Chat");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setResizable(false);

		JPanel jPanel = new JPanel(new GridBagLayout());
		JPanel jPanel2 = new JPanel(new GridBagLayout());
		JLabel jLabel = new JLabel("Chat");
		JScrollPane jScrollPane = new JScrollPane(jtTextArea);
		JLabel jLabel2 = new JLabel(String.format("%s, digite uma mensagem: ", nome));
		final JTextField jTextField = new JTextField(40);
		JButton jButton = new JButton("Enviar");

		GridBagConstraints gridBagConstraints = new GridBagConstraints();

		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		jPanel.add(jLabel, gridBagConstraints);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		jPanel.add(jScrollPane, gridBagConstraints);

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.weightx = 1;
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.ipadx = 10;
		jPanel2.add(jLabel2, gridBagConstraints2);

		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 0;
		jPanel2.add(jTextField, gridBagConstraints2);

		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 1;
		jPanel2.add(jButton, gridBagConstraints2);

		jButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				inserirMensagemDB(jTextField.getText());
				inserirMensagemRMI(jTextField.getText());
				jTextField.setText("");
				jTextField.requestFocus();
				recuperarChatDB();
				recuperarChatRMI();
			}
		});
		jFrame.add(jPanel, BorderLayout.CENTER);
		jFrame.add(jPanel2, BorderLayout.SOUTH);
		jFrame.pack();
		jFrame.setVisible(true);
		jTextField.requestFocus();

		jFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				gerarRelatorio();
			}
		});

	}

	private static void gerarRelatorio() {

		try {
			PrintWriter writer = new PrintWriter(String.format("C:\\Users\\Marcelo\\Desktop\\relatorio-chat-%s.txt",
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss"))), "UTF-8");
			for (Message msg : objChat.retrieveMessage()) {
				writer.println(msg.getMessage());
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException | RemoteException e) {
			e.printStackTrace();
		}
	}

	private static String formatMessage(final String msg) {
		return String.format("%s - %s: %s", nome,
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), msg);
	}

	private static String returnMessage(List<Message> list) {
		String valor = "";
		for (Message message : list) {
			valor += formatMessage(message.getMessage() + "\n");
		}
		return valor;
	}

}
