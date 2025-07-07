package crud.prontuario.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import crud.prontuario.database.IConnection;
import crud.prontuario.model.Paciente;

public class PacienteDAO implements IEntityDAO<Paciente> {

	private IConnection conn;

	public PacienteDAO(IConnection connection) {
		this.conn = connection;
	}

	@Override
	public void create(Paciente t) {
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("INSERT INTO PACIENTES (nome, cpf) VALUES (?, ?);");
			pstm.setString(1, t.getNome());
			pstm.setString(2, t.getCpf());
			pstm.executeUpdate();
			pstm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Paciente findById(Long id) {
		// TODO Auto-generated method stub
		Paciente p = null;
		try {
			PreparedStatement pstm = conn.getConnection().prepareStatement("SELECT * FROM PACIENTES WHERE ID = ?;");
			pstm.setLong(1, id);
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
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

	
	public Paciente findByCPF(String cpf) {
		Paciente paciente = null;
		try {
			PreparedStatement pstm = conn.getConnection().prepareStatement("SELECT * FROM PACIENTES WHERE cpf = ?;");
			pstm.setString(1, cpf);
			ResultSet rs = pstm.executeQuery();
			if (rs.next()) {
				paciente = new Paciente(rs.getLong("id"), rs.getString("nome"), rs.getString("cpf"));
			}
			rs.close();
			pstm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return paciente;
	}
	

	@Override
	public void delete(Paciente t) {
		// TODO Auto-generated method stub
		
		try {
			PreparedStatement pstm = conn.getConnection().prepareStatement("DELETE FROM EXAMES WHERE paciente_id = ?;");
			pstm.setLong(1, t.getId());
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			PreparedStatement pstm = conn.getConnection().prepareStatement("DELETE FROM PACIENTES WHERE ID = ?;");
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
			PreparedStatement pstm = conn.getConnection().prepareStatement("SELECT * FROM PACIENTES;");
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
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
