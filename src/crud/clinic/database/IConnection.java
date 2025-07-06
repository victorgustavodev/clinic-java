package crud.clinic.database;

import java.sql.Connection;

public interface IConnection {
    Connection getConnection();
    void closeConnection(Connection conn);
}
