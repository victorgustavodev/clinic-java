package crud.prontuario.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Exame {

	private Long id;
	private String descricao;
	private LocalDateTime data;
	private Paciente paciente;
	
	public Exame() {}

	public Exame(String descricao, LocalDateTime data, Paciente paciente) {	
		this.descricao = descricao;
		this.data = data;
		this.paciente = paciente;
	}
	
	public Exame(String descricao, LocalDateTime data) {
		this.descricao = descricao;
		this.data = data;
	}	

	public Paciente getpaciente() {
		return paciente;
	}

	public void setpaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(LocalDateTime data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Exame [id = " + id + ", descricao = " + descricao + ", data = " + data + ", Id do paciente = " + paciente.getId() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Exame other = (Exame) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
	
}
