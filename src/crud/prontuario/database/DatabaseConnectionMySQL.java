package crud.prontuario.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import crud.prontuario.utils.Config;

public class DatabaseConnectionMySQL implements IConnection {

    private final String USERNAME = Config.get("DB_USERNAME");
    private final String PASSWORD = Config.get("DB_PASSWORD");
    private final String ADDRESS = Config.get("DB_ADDRESS");
    private final String PORT = Config.get("DB_PORT");
    private final String DATABASE = Config.get("DB_NAME");

    private Connection connection;

    @Override
    public Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:mysql://" + ADDRESS + ":" + PORT + "/" + DATABASE;
                connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
           } catch (SQLException e) {
                System.err.println("Erro ao conectar no banco de dados: " + e.getMessage());
            }
        }
        return connection;
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão encerrada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }
}
