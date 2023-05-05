package uwi.third.thirdparty.service.contract;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import uwi.third.thirdparty.entity.Student;



public interface ThirdPartyService {
	public boolean thirdPartyUpdate(String tpId, String stuId) throws SQLException;
	public boolean thirdPartyDelete(String tpId) throws SQLException;
	public Student getStudent(String stuId) throws SQLException;
	public boolean massUpdate() throws SQLException, NumberFormatException, IOException;
	public List<Student> swap(Object obj) throws SQLException, JsonProcessingException;  
}
