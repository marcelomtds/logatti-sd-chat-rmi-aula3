package principal;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionDB {

	private static Connection connection;

	public Connection getConnetion() {
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/rmi", "postgres", "12345");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

}
