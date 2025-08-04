package crud.prontuario.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Paciente {

	private Long id;
	private String nome;
	private String cpf;
	private LocalDate dataDeNascimento;

	private List<Exame> exames = new ArrayList<Exame>();
	private Paciente p;

	public Paciente() {
	}

	public Paciente(Paciente p) {
		this.p = new Paciente(p.getNome(), p.getCpf(), p.getDataDeNascimento());
		}

	public Paciente(String cpf) {
		this.cpf = cpf;
	}

	public Paciente(String nome, String cpf) {
		this.nome = nome;
		this.cpf = cpf;
	}
	
	public Paciente(String nome, String cpf, LocalDate dataDeNascimento) {
		this.nome = nome;
		this.cpf = cpf;
		this.dataDeNascimento = dataDeNascimento;
	}
	
	public Paciente(Long id, String nome, String cpf) {
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
	}
	
	public Paciente(Long id, String nome, String cpf, List<Exame> exames) {
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
		this.exames = exames;
	}
	
	public LocalDate getDataDeNascimento() {
		return dataDeNascimento;
	}

	public void setDataDeNascimento(LocalDate dataDeNascimento) {
		this.dataDeNascimento = dataDeNascimento;
	}

	public Paciente(Exame exames) {
		this.exames = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public List<Exame> getExames() {
		return exames;
	}

	public void setExames(List<Exame> exames) {
		this.exames = exames;
	}

	public void addExame(Exame e) {
		exames.add(e);
	}
		
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Paciente [id=").append(id)
	      .append(", nome=").append(nome)
	      .append(", cpf=").append(cpf)
	      .append(", data de nascimento=")
	      .append("]\n");

	    if (exames != null && !exames.isEmpty()) {
	        for (Exame exame : exames) {
	            sb.append("  - ").append(exame).append("\n");
	        } 
	    } 

	    return sb.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(cpf);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Paciente other = (Paciente) obj;
		return Objects.equals(cpf, other.cpf);
	}

}
