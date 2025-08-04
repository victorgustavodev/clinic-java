package crud.prontuario.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	    // A query SQL está correta.
	    String sql = "INSERT INTO exames (descricao, data_exame, paciente_id) VALUES (?, ?, ?)";

	    try {
	        PreparedStatement pstm = conn.getConnection().prepareStatement(sql);

	        // Define a descrição (parâmetro 1)
	        pstm.setString(1, exame.getDescricao());

	        // --- CORREÇÃO APLICADA AQUI ---
	        // Converte o LocalDate do objeto Exame para um java.sql.Date
	        java.sql.Date dataSql = java.sql.Date.valueOf(exame.getDataExame()); // Supondo que o getter se chame getDataExame()
	        
	        // Define a data usando setDate (parâmetro 2)
	        pstm.setDate(2, dataSql);
	        
	        // Define o ID do paciente (parâmetro 3)
	        pstm.setLong(3, exame.getPaciente().getId());

	        pstm.execute();
	        pstm.close();

	    } catch (SQLException e) {
	        // O tratamento da exceção está bom.
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
				
				exame.setDataExame(null);
			}
			pstm.close();
		} catch (SQLException e) {
			throw new DAOException("Não foi possivel localizar o exame. \nErro: " + e);
		}
		return exame;
	}
	
	// Adicione este método dentro da sua classe ExameDAO.java
	@Override
	public List<Exame> findByPacienteName(String nome) {
	    List<Exame> examesEncontrados = new ArrayList<>();
	    
	    // SQL com JOIN para buscar exames pelo nome do paciente
	    String sql = "SELECT e.id, e.descricao, e.data_exame, p.id as paciente_id, p.nome as paciente_nome, p.cpf as paciente_cpf " +
	                 "FROM exames e " +
	                 "JOIN pacientes p ON e.paciente_id = p.id " +
	                 "WHERE p.nome LIKE ?";

	    try {
	        PreparedStatement pstm = conn.getConnection().prepareStatement(sql);
	        // Usa o operador LIKE para buscas parciais (ex: "jo" encontra "João")
	        pstm.setString(1, "%" + nome + "%"); 
	        
	        ResultSet rs = pstm.executeQuery();

	        while (rs.next()) {
	            // Cria um objeto Paciente com os dados retornados da tabela de pacientes
	            Paciente paciente = new Paciente();
	            paciente.setId(rs.getLong("paciente_id"));
	            paciente.setNome(rs.getString("paciente_nome"));
	            paciente.setCpf(rs.getString("paciente_cpf"));
	            
	            // Cria o objeto Exame com seus dados
	            Exame exame = new Exame();
	            exame.setId(rs.getLong("id"));
	            exame.setDescricao(rs.getString("descricao"));
	            exame.setDataExame(rs.getDate("data_exame").toLocalDate());
	            
	            // Associa o objeto paciente completo ao exame
	            exame.setPaciente(paciente);
	            
	            examesEncontrados.add(exame);
	        }
	        
	        pstm.close();
	        rs.close();

	    } catch (SQLException e) {
	        throw new DAOException("Erro ao buscar exames por nome de paciente.", e);
	    }
	    
	    return examesEncontrados;
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
                exame.setDataExame(null);

                Paciente paciente = new Paciente();
                paciente.setId(rs.getLong("paciente_id"));
                exame.setPaciente(paciente);

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
//			pstm.setTimestamp(2, Timestamp.valueOf(exame.getDataExame()));
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