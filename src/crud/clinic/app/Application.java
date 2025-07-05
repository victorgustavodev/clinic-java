package crud.clinic.app;

import crud.clinic.dao.IEntityDAO;
import crud.clinic.dao.PacienteDAO;
import crud.clinic.database.DBConnectionMySQL;
import crud.clinic.model.Paciente;

public class Application {
	
	public static void main(String[] args) {
		
		IEntityDAO<Paciente> dao = 
				new PacienteDAO(new DBConnectionMySQL());
		
		Paciente p = dao.findById(2L);
		
		p.setNome("Joao da Silva");
		p.setCpf("222");
		
		dao.update(p);
		
	}
}
