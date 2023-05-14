package uwi.third.thirdparty.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import uwi.third.thirdparty.entity.Student;
import uwi.third.thirdparty.exceptions.RecordNotFoundException;
import uwi.third.thirdparty.service.contract.ThirdPartyService;
import uwi.third.thirdparty.util.ResponseDetails;
import uwi.third.thirdparty.util.ResponseStatus;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")

@RestController
public class ThirdPartyController {

	private final ThirdPartyService thirdPartyService;
	
	@Autowired
	public ThirdPartyController(ThirdPartyService thirdPartyService) {
	    this.thirdPartyService = thirdPartyService;
	}
	
	@RequestMapping(
	        value = "/update/{tpId}/{stId}",
	        method = RequestMethod.POST,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public  ResponseEntity<ResponseDetails<Boolean>> update(String tpId, String stuId) throws IOException {
		
		try {
			boolean response = thirdPartyService.thirdPartyUpdate(tpId, stuId);
			ResponseDetails<Boolean> responseDetails = new ResponseDetails<Boolean>(ResponseStatus.SUCCESS, response,"Successful Transaction");
			return new ResponseEntity<ResponseDetails<Boolean>>( responseDetails,HttpStatus.OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDetails<Boolean>>( new ResponseDetails<Boolean>(ResponseStatus.BAD_REQUEST, false,"UnSuccessful Request"),HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(
	        value = "/thirdparty/deletion/{tpId}",
	        method = RequestMethod.DELETE,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public  ResponseEntity<ResponseDetails<Boolean>> deleteThirdParty(@PathVariable String tpId) throws IOException {
		
		try {
			Boolean success =thirdPartyService.thirdPartyDelete(tpId);
			ResponseDetails<Boolean> responseDetails = new ResponseDetails<Boolean>(ResponseStatus.SUCCESS, success,"Successful Transaction");
			return new ResponseEntity<ResponseDetails<Boolean>>( responseDetails,HttpStatus.OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDetails<Boolean>>( new ResponseDetails<Boolean>(ResponseStatus.BAD_REQUEST, false,"UnSuccessful Request"),HttpStatus.BAD_REQUEST);
	}
	@RequestMapping(
	        value = "/thirdparty/mass/update",
	        method = RequestMethod.POST,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	
	public  ResponseEntity<ResponseDetails<Boolean>> massUpdate() throws IOException {
		
		try {
			boolean response = thirdPartyService.massUpdate();
			ResponseDetails<Boolean> responseDetails = new ResponseDetails<Boolean>(ResponseStatus.SUCCESS, response,"Successful Transaction");
			return new ResponseEntity<ResponseDetails<Boolean>>( responseDetails,HttpStatus.OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDetails<Boolean>>( new ResponseDetails<Boolean>(ResponseStatus.BAD_REQUEST, false,"UnSuccessful Request"),HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(
	        value = "/thirdparty/student/{studentId}",
	        method = RequestMethod.GET,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<ResponseDetails<Student>> getStudent(@PathVariable String studentId)  {
		String errorMessage = "";
		try {
			Student student = thirdPartyService.getStudent(studentId);
			ResponseDetails<Student> responseDetails = new ResponseDetails<Student>(ResponseStatus.SUCCESS, student,"Successful Query");
			if (student != null) {
			  return new ResponseEntity<ResponseDetails<Student>>( responseDetails,HttpStatus.OK);
			} else {
				throw new RecordNotFoundException("Student Record Not Found");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RecordNotFoundException e) {
			errorMessage = e.getMessage();
			e.printStackTrace();
		} 
		return new ResponseEntity<ResponseDetails<Student>>( new ResponseDetails<Student>(ResponseStatus.BAD_REQUEST, null,errorMessage),HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(
	        value = "/thirdparty/swap",
	        method = RequestMethod.POST,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<ResponseDetails<List<Student>>> swap(@RequestBody Object obj)  {
		String errorMessage = "";
		try {
			List<Student> students = thirdPartyService.swap(obj);
			ResponseDetails<List<Student>> responseDetails = new ResponseDetails<List<Student>>(ResponseStatus.SUCCESS, students,"Successful Query");
			System.out.println(students);
			return new ResponseEntity<ResponseDetails<List<Student>>>(responseDetails, HttpStatus.OK);
		} catch (JsonProcessingException | SQLException e) {
			errorMessage = e.getMessage();
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDetails<List<Student>>>( new ResponseDetails<List<Student>>(ResponseStatus.BAD_REQUEST, null,errorMessage),HttpStatus.BAD_REQUEST);
	}
}
