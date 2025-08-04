package crud.prontuario.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
                String urlSemDb = "jdbc:mysql://" + ADDRESS + ":" + PORT + "/?useSSL=false&allowPublicKeyRetrieval=true";
                connection = DriverManager.getConnection(urlSemDb, USERNAME, PASSWORD);

                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE);
                }

                connection.close();

                String urlComDb = "jdbc:mysql://" + ADDRESS + ":" + PORT + "/" + DATABASE + "?useSSL=false&allowPublicKeyRetrieval=true";
                connection = DriverManager.getConnection(urlComDb, USERNAME, PASSWORD);

                try (Statement stmt = connection.createStatement()) {
                    String sqlPacientes = """
                        CREATE TABLE IF NOT EXISTS pacientes (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            nome VARCHAR(255) NOT NULL,
                            cpf VARCHAR(14) NOT NULL UNIQUE,
                            dataDeNascimento DATE NOT NULL
                        );
                    """;
                    stmt.executeUpdate(sqlPacientes);

                    String sqlExames = """
                        CREATE TABLE IF NOT EXISTS exames (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            descricao VARCHAR(255) NOT NULL,
                            data_exame DATETIME NOT NULL,
                            paciente_id BIGINT,
                            FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE
                        );
                    """;
                    stmt.executeUpdate(sqlExames);
                }

            } catch (SQLException e) {
                throw new RuntimeException("Erro ao conectar ou configurar o banco de dados: " + e.getMessage(), e);
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
