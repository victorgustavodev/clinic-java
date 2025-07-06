package crud.clinic.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionMySQL implements IConnection {

    private final String USERNAME = "root";
    private final String PASSWORD = "root";
    private final String ADDRESS = "localhost";
    private final String PORT = "3306";
    private final String DATABASE = "mydb";

    @Override
    public Connection getConnection() {
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                "jdbc:mysql://" + ADDRESS + ":" + PORT + "/" + DATABASE + "?useSSL=false&serverTimezone=UTC",
                USERNAME,
                PASSWORD
            );
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erro na conexão com o banco: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexão fechada com sucesso.");
            } catch (SQLException e) {
                System.out.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
