package crud.clinic.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import crud.clinic.database.IConnection;
import crud.clinic.model.Exame;

public class ExameDAO implements IEntityDAO<Exame>{
	private IConnection conn;
	
	public ExameDAO(IConnection connection) {
		this.conn = connection;
	}
	
	@Override
	public void create(Exame exame) {
		String sql = "INSERT INTO exame (descricao, data) VALUES (?, ?);";
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement(sql);
			
			pstm.setString(1, exame.getDescricao());
			pstm.setTimestamp(2, Timestamp.valueOf(exame.getData()));
		
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Exame findById(Long id) {
		Exame exame = null;
		
		String sql = "SELECT * FROM exame WHERE id = ?;";
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement(sql);
			pstm.setLong(1, id);
			
			ResultSet rs = pstm.executeQuery();
			
			
			if(rs.next()) {
				exame = new Exame();
				
				exame.setId(rs.getLong("id"));
				exame.setDescricao(rs.getString("descricao"));
				
				exame.setData(rs.getTimestamp("data").toLocalDateTime());
			}
			pstm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exame;
	}
	
	@Override
	public void delete(Exame exame) {
		
		String sql = "DELETE FROM exame WHERE id = ?;";
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement(sql);
			
			pstm.setLong(1, exame.getId());
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Exame> findAll() {
		List<Exame> exames = new ArrayList<>();
		
		String sql = "SELECT * FROM exame;";
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			
			while(rs.next()) {
				
				Long id = rs.getLong("id");
				String descricao = rs.getString("descricao");
				java.time.LocalDateTime data = rs.getTimestamp("data").toLocalDateTime();
				
				exames.add(new Exame(id, descricao, data));
			}
			pstm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exames;
	}

	@Override
	public void update(Exame exame) {
		
		String sql = "UPDATE exame SET descricao = ?, data = ? WHERE id = ?;";
		try {
			PreparedStatement pstm = conn.getConnection()
					.prepareStatement(sql);
			
			
			pstm.setString(1, exame.getDescricao());
			pstm.setTimestamp(2, Timestamp.valueOf(exame.getData()));
			pstm.setLong(3, exame.getId());
			
			pstm.execute();
			pstm.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}