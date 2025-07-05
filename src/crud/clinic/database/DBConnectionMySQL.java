package crud.clinic.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionMySQL implements IConnection {

	private final String USERNAME = "root";
	private final String PASSWORD = "root";
	private final String ADDRESS = "localhost";
	private final String PORT = "3306";
	private final String DATABASE = "clinic";
	
	@Override
	public Connection getConnection() {
		// TODO Auto-generated method stub
		try {
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/"+DATABASE, USERNAME, PASSWORD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void closeConnection() {
		// TODO Auto-generated method stub
		
	}

}
