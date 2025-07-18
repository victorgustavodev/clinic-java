package crud.prontuario.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import crud.prontuario.database.IConnection;
import crud.prontuario.exception.DAOException;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

public class ExameDAO implements IEntityDAO<Exame>{
	private IConnection conn;
	
	public ExameDAO(IConnection connection) {
		this.conn = connection;
	}
	
	@Override
	public void create(Exame exame) {
		
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement("INSERT INTO exames (descricao, data_exame, paciente_id) VALUES (?, ?, ?);");
			
			pstm.setString(1, exame.getDescricao());
			pstm.setTimestamp(2, Timestamp.valueOf(exame.getData()));
			pstm.setLong(3, exame.getpaciente().getId());
			
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			throw new DAOException("Não foi possivel criar o exame. \nErro: " + e);
		}
	}
	
	@Override
	public Exame findById(Long id) {
		Exame exame = null;
		
		String sql = "SELECT * FROM exames WHERE id = ?;";
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement(sql);
			pstm.setLong(1, id);
			
			ResultSet rs = pstm.executeQuery();
			
			
			if(rs.next()) {
				exame = new Exame();
				
				exame.setId(rs.getLong("id"));
				exame.setDescricao(rs.getString("descricao"));
				
				exame.setData(rs.getTimestamp("data_exame").toLocalDateTime());
			}
			pstm.close();
		} catch (SQLException e) {
			throw new DAOException("Não foi possivel localizar o exame. \nErro: " + e);
		}
		return exame;
	}
	
	@Override
	public void delete(Exame exame) {
		
		String sql = "DELETE FROM exames WHERE id = ?;";
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement(sql);
			pstm.setLong(1, exame.getId());
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			throw new DAOException("Não foi possivel deletar o exame. \nErro: " + e);
		}
	}

	@Override	
	public List<Exame> findAll() {
        List<Exame> exames = new ArrayList<>();

        try {
            PreparedStatement pstm = conn.getConnection()
                    .prepareStatement("SELECT * FROM EXAMES;");
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Exame exame = new Exame();
                exame.setId(rs.getLong("id"));
                exame.setDescricao(rs.getString("descricao"));
                exame.setData(rs.getTimestamp("data_exame").toLocalDateTime());

                Paciente paciente = new Paciente();
                paciente.setId(rs.getLong("paciente_id"));
                exame.setpaciente(paciente);

                exames.add(exame);
            }
            pstm.close();
        } catch (SQLException e) {
        	throw new DAOException("Não foi possivel listar os exames. \nErro: " + e);
        }
        return exames;
    }

	@Override
	public void update(Exame exame) {
		
		String sql = "UPDATE exames SET descricao = ?, data_exame = ? WHERE id = ?;";
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement(sql);
			
			pstm.setString(1, exame.getDescricao());
			pstm.setTimestamp(2, Timestamp.valueOf(exame.getData()));
			pstm.setLong(3, exame.getId());
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			throw new DAOException("Não foi possivel atualizar o exame. \nErro: " + e);
		}
	}

	@Override
	public Exame findByCPF(String s) {
		// TODO Auto-generated method stub
		return null;
	}

}