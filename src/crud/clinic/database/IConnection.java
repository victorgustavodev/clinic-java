package crud.clinic.database;

import java.sql.Connection;

public interface IConnection {
	
	public Connection getConnection();
	
	public void closeConnection();
}
