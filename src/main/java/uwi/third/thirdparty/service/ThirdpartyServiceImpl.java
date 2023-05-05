package uwi.third.thirdparty.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uwi.third.thirdparty.dao.OracleDao;
import uwi.third.thirdparty.entity.RequestObject;
import uwi.third.thirdparty.entity.Student;
import uwi.third.thirdparty.service.contract.ThirdPartyService;

@Service
public class ThirdpartyServiceImpl implements ThirdPartyService {

	@Autowired
	private OracleDao dao;
	
	
	
    private final Path fileStorageLocation;
	    
	private final FileStorageProperties fileStorageProperties;
	private Path targetLocation;
	private Path newFileLocation;
	
	@Autowired
	 public ThirdpartyServiceImpl(FileStorageProperties fileStorageProperties) {
	        this.fileStorageLocation = Paths.get(fileStorageProperties.getDownloadDir())
	                .toAbsolutePath().normalize();
	        
	        this.fileStorageProperties = fileStorageProperties;
	        
	}
	
	
	@Override
	public boolean thirdPartyUpdate(String tpId, String stuId) throws SQLException {
		long pidm = dao.findPidm(stuId);
		dao.updateThirdPartyId(pidm, tpId);
		return true;
	}

	@Override
	public boolean thirdPartyDelete(String tpId) throws SQLException {
		  dao.deleteFromGorpaud(tpId);
		  dao.deleteFromGobtPac(tpId);
		  return true;
	}

	@Override
	public Student getStudent(String stuId) throws SQLException {
		return dao.getStudent(dao.findPidm(stuId));
	}

	
	@Override
	public boolean massUpdate() throws SQLException, NumberFormatException, IOException {
		
		ArrayList<Student> students = readBulkFile();
		dao.massUpdates(students);
		return true;
	}
    
	private ArrayList<Student> readBulkFile() {
		
		ArrayList<Student> students = new ArrayList<Student>();
 		String delimiter = ",";
		Path fileLocation = fileStorageLocation.resolve(fileStorageProperties.getDownloadFile());
		try {
	         File file = new File(fileLocation.toString());
	         FileReader fr = new FileReader(file);
	         BufferedReader br = new BufferedReader(fr);
	         String line = "";
	         String[] tempArr;
	         while((line = br.readLine()) != null) {
	            tempArr = line.split(delimiter);
	            Student student = new Student();
	            student.setStudentId(tempArr[0]);
	            student.setPidm(Integer.parseInt(tempArr[1]));
	            student.setFirstName(tempArr[2]);
	            student.setLastName(tempArr[3]);
	            student.setThirdPartyId(tempArr[4]);
	            student.setNewThirdPartyId(tempArr[5]);
	            student.setEmail(tempArr[6]);
	            student.setStatus(tempArr[7]);
	            students.add(student);
	         }
	         br.close();
	         } catch(IOException ioe) {
	            ioe.printStackTrace();
	         }
		  return students;
	}


	@Override
	public List<Student> swap(Object obj) throws SQLException, JsonProcessingException {
		int cnt = 0; 
		String tpId="";
		List<Student> swappedStudents = new ArrayList<Student>();
		List<Student> students = getTransactions(obj);
		List<String> tpIDs = students.stream().map(student->{
			return student.getThirdPartyId();
			}).collect(Collectors.toList());
		for (Student student :students) {
			dao.deleteFromGorpaud(student.getThirdPartyId());
			dao.deleteFromGobtPac(student.getThirdPartyId());
            tpId = cnt == 0 ? tpIDs.get(1): tpIDs.get(0); 
            student.setThirdPartyId(tpId);
            swappedStudents.add(student);
            dao.updateThirdPartyId(dao.findPidm(student.getStudentId()), tpId);
            cnt++;
		}
		System.out.println(swappedStudents);
		return swappedStudents;
	}
	private List<Student> getTransactions(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String JSONString = mapper.writeValueAsString(obj);
		GsonBuilder builder = new GsonBuilder(); ;
		Gson gson = builder.create();
		
		RequestObject requestObject  = gson.fromJson(JSONString, RequestObject.class);  
   	    List<Student> transactionArrayList = requestObject.getData();
   	  
		return transactionArrayList;
		
	}
}
