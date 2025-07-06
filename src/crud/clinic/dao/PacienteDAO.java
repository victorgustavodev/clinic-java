package crud.clinic.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import com.mysql.cj.protocol.Resultset;

import crud.clinic.database.IConnection;
import crud.clinic.model.Paciente;

public class PacienteDAO implements IEntityDAO<Paciente>{

	private IConnection conn;
	
	public PacienteDAO(IConnection connection) {
		this.conn = connection;
	}
	
	@Override
	public void create(Paciente t) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("INSERT INTO PACIENTES VALUES (?, ?, ?);");
			pstm.setLong(1, t.getId());
			pstm.setString(2, t.getNome());
			pstm.setString(3, t.getCpf());
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Paciente findById(Long id) {
		// TODO Auto-generated method stub
		Paciente p = null;
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("SELECT * FROM PACIENTES WHERE ID = ?;");
			pstm.setLong(1, id);
			ResultSet rs = pstm.executeQuery();
			while(rs.next()) {
				p = new Paciente();
				p.setCpf(rs.getString("cpf"));
				p.setId(rs.getLong("id"));
				p.setNome(rs.getString("nome"));
			}
			pstm.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

	@Override
	public void delete(Paciente t) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("DELETE FROM PACIENTES WHERE ID = ?;");
			pstm.setLong(1, t.getId());
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<Paciente> findAll() {
		// TODO Auto-generated method stub
		List<Paciente> pacientes = new ArrayList<Paciente>();
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("SELECT * FROM PACIENTES;");
			ResultSet rs = pstm.executeQuery();
			while(rs.next()) {
				pacientes.add(new Paciente(rs.getLong("id"), rs.getString("nome"), rs.getString("cpf")));
			}
			pstm.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pacientes;
	}

	@Override
	public void update(Paciente t) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("UPDATE PACIENTES SET NOME = ?, CPF = ? WHERE ID = ?;");
			pstm.setString(1, t.getNome());
			pstm.setString(2, t.getCpf());
			pstm.setLong(3, t.getId());
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
