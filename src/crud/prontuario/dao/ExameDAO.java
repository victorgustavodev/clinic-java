package crud.prontuario.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import crud.prontuario.database.IConnection;
import crud.prontuario.exception.DAOException;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

public class ExameDAO implements IEntityDAO<Exame> {
	private IConnection conn;

	public ExameDAO(IConnection connection) {
		this.conn = connection;
	}

	@Override
	public void create(Exame exame) {
		String sql = "INSERT INTO exames (descricao, data_exame, paciente_id) VALUES (?, ?, ?)";

		try {
			PreparedStatement pstm = conn.getConnection().prepareStatement(sql);
			pstm.setString(1, exame.getDescricao());
            pstm.setDate(2, Date.valueOf(exame.getDataExame()));
			pstm.setLong(3, exame.getPaciente().getId());

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
			PreparedStatement pstm = conn.getConnection().prepareStatement(sql);
			pstm.setLong(1, id);

			ResultSet rs = pstm.executeQuery();

			if (rs.next()) {
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

	public List<Exame> findByPacienteName(String nome) {
		List<Exame> examesEncontrados = new ArrayList<>();

		String sql = "SELECT e.id, e.descricao, e.data_exame, " +
					 "p.id as paciente_id, p.nome as paciente_nome, p.cpf as paciente_cpf, p.dataDeNascimento as paciente_nasc " +
					 "FROM exames e " +
					 "JOIN pacientes p ON e.paciente_id = p.id " +
					 "WHERE p.nome LIKE ?";

		// Usando try-with-resources para garantir que PreparedStatement e ResultSet sejam fechados
		try (PreparedStatement pstm = conn.getConnection().prepareStatement(sql)) {
			pstm.setString(1, "%" + nome + "%");

			try (ResultSet rs = pstm.executeQuery()) {
				while (rs.next()) {
					// Cria o objeto Paciente com os dados completos
					Paciente paciente = new Paciente();
					paciente.setId(rs.getLong("paciente_id"));
					paciente.setNome(rs.getString("paciente_nome"));
					paciente.setCpf(rs.getString("paciente_cpf"));
					
					// CORRIGIDO: Carrega a data de nascimento do paciente
					Date dataNasc = rs.getDate("paciente_nasc");
					if (dataNasc != null) {
						paciente.setDataDeNascimento(dataNasc.toLocalDate());
					}

					// Cria o objeto Exame
					Exame exame = new Exame();
					exame.setId(rs.getLong("id"));
					exame.setDescricao(rs.getString("descricao"));
					exame.setDataExame(rs.getDate("data_exame").toLocalDate());
					exame.setPaciente(paciente);

					examesEncontrados.add(exame);
				}
			}
		} catch (SQLException e) {
			throw new DAOException("Erro ao buscar exames por nome de paciente.", e);
		}

		return examesEncontrados;
	}

	@Override
	public void delete(Exame exame) {

		String sql = "DELETE FROM exames WHERE id = ?;";
		try {
			PreparedStatement pstm = conn.getConnection().prepareStatement(sql);
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
		String sql = "SELECT e.id, e.descricao, e.data_exame, " +
					 "p.id as paciente_id, p.nome as paciente_nome, p.cpf as paciente_cpf, p.dataDeNascimento as paciente_nasc " +
					 "FROM exames e " +
					 "LEFT JOIN pacientes p ON e.paciente_id = p.id";

		try (PreparedStatement pstm = conn.getConnection().prepareStatement(sql);
			 ResultSet rs = pstm.executeQuery()) {

			while (rs.next()) {
				Paciente paciente = new Paciente();
				paciente.setId(rs.getLong("paciente_id"));
				paciente.setNome(rs.getString("paciente_nome"));
				paciente.setCpf(rs.getString("paciente_cpf"));
				
				Date dataNasc = rs.getDate("paciente_nasc");
				if (dataNasc != null) {
					paciente.setDataDeNascimento(dataNasc.toLocalDate());
				}
				
				Exame exame = new Exame();
				exame.setId(rs.getLong("id"));
				exame.setDescricao(rs.getString("descricao"));
				exame.setDataExame(rs.getDate("data_exame").toLocalDate());
				exame.setPaciente(paciente);
				
				exames.add(exame);
			}
		} catch (SQLException e) {
			throw new DAOException("Não foi possível listar os exames.", e);
		}
		return exames;
	}

	@Override
	public void update(Exame exame) {
		String sql = "UPDATE exames SET descricao = ?, data_exame = ?, paciente_id = ? WHERE id = ?;";

		try (PreparedStatement pstm = conn.getConnection().prepareStatement(sql)) {
			pstm.setString(1, exame.getDescricao());
			pstm.setDate(2, Date.valueOf(exame.getDataExame()));
			pstm.setLong(3, exame.getPaciente().getId());
			pstm.setLong(4, exame.getId()); // O WHERE id é o último parâmetro
			pstm.execute();
		} catch (SQLException e) {
			throw new DAOException("Não foi possível atualizar o exame.", e);
		}
	}

	@Override
	public Exame findByCPF(String s) {
		// TODO Auto-generated method stub
		return null;
	}

}