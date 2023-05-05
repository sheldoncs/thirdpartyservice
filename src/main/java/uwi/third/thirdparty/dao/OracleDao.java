package uwi.third.thirdparty.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import uwi.third.thirdparty.config.OracleConfig;
import uwi.third.thirdparty.entity.Student;
import uwi.third.thirdparty.exceptions.RecordNotFoundException;
import uwi.third.thirdparty.util.DteFormatter;
import uwi.third.thirdparty.util.NewDateFormatterImpl;



@Repository
public class OracleDao {

	private static final int  SPRIDEN_ID = 0;
	private static final int  GOBTPAC_PIDM = 1;
	private static final int  SPRIDEN_FIRST_NAME = 2;
	private static final int  SPRIDEN_LAST_NAME = 3;
	private static final int  THIRD_PARTY_ID = 4;
	private static final int  NEW_THIRD_PARTY_ID =5;
	private static final int  CA_EMAIL_ADDRESS =6;
	private static final int  STATUS =7;
	
	private final int errorCntr = 0;
    Logger logger = LoggerFactory.getLogger(OracleDao.class);
	
    @SuppressWarnings("unused")
	@Autowired
    private final OracleConfig service;

    @Autowired
    private NewDateFormatterImpl df;
    
    private Connection conn;
	
    private String strDate;
    
    @Value("${error}")
	private String ERROR_FILE_DIRECTORY;
	
	@Value("${bulk}")
	private String BULK_FILE_DIRECTORY;
    
	public OracleDao(OracleConfig service) {

		this.service = service;
		
		try {

			logger.info("Starting Oracle Db");
			Class.forName( service.getDriver());
			conn = DriverManager.getConnection(service.getConnect(), 
															service.getUsername(), 
															service.getPassword());
			
			conn.setAutoCommit(true);
			logger.info("Oracle Db Started");
		} catch (ClassNotFoundException e) {
			logger.info("Driver class not found - {}", e.getMessage());
		} catch (SQLException ex) {
			logger.info("Connection error - {}", ex.getMessage());
		}
        
	}
	private int getPidmFromGobTPac(String external_user) {
		String selectStatement = "select gobtpac_pidm from gobtpac where gobtpac_external_user = ?";
		int pidm = 0;
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, external_user);
			
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				pidm = rs.getInt(1);
			}
			prepStmt.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return pidm;
	}
	public boolean externalUserExist(String external_user, int pidm){
		boolean found=false;
		String selectStatement = "select * from gobtpac where gobtpac_external_user = ? and gobtpac_pidm = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, external_user);
			prepStmt.setInt(2, pidm);
			
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				found=true;
				}
			prepStmt.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return found;
	}
	public Student deleteFromGorpaud(String external_user) {
		
		 Student student = getStudent(this.getPidmFromGobTPac(external_user));
		String selectStatement = "delete from gorpaud where gorpaud_external_user = ? and gorpaud_chg_ind in ('I','P')";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, external_user);
			logger.info("Delete {} from {} records", external_user, "'GORPAUD'");
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (SQLException e) {
			logger.info("Exception deleting {} from GORPAUD - {}", external_user, e.getMessage());
		}
		return student;
	}
	
	public void  deleteFromGobtPac(String external_user) {
		String selectStatement = "delete from gobtpac where gobtpac_external_user = ? ";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, external_user);
			logger.info("Delete {} from {} records", external_user, "'GOBTPAC'");
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (SQLException e) {
			logger.info("Exception deleting {} from GOBTPAC - {}", external_user, e.getMessage());
		}
	}
	public void updateThirdPartyId(long pidm, String third_party_id) throws SQLException {

		CallableStatement callStmt = conn.prepareCall("{CALL gb_third_party_access.p_update(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
		callStmt.setLong(1, pidm);
		callStmt.setString(2, "N");
		callStmt.setString(3, "N");
		callStmt.setString(4, "SVC_UPDATE");
		callStmt.setString(5, null);
		callStmt.setString(6, null);
		callStmt.setString(7, third_party_id);
		callStmt.setString(8, null);
		callStmt.setString(9, null);
		callStmt.setString(10, null);
		callStmt.setString(11, null);
		callStmt.setString(12, null);
		callStmt.setString(13, null);
		callStmt.setString(14, "Y");
		callStmt.setString(15, "N");
		callStmt.setString(16, "N");
		callStmt.setString(17, "Y");
		callStmt.registerOutParameter(18, Types.VARCHAR);
		
		callStmt.executeQuery();
	    callStmt.close();

	    Student student = getStudent(pidm);
	    logger.info("Third Party ID {} updated for student {} {} with spriden ID {}", third_party_id, student.getFirstName(), student.getLastName(), student.getStudentId());
	}
	public Student getStudent(long pidm) {
		if(pidm == 0) {
			throw new RecordNotFoundException("Record Not Found");
		}
	   
		Student student = new Student();
		String sqlstmt = "select distinct a.spriden_id, a.spriden_first_name, a.spriden_last_name, b.gobtpac_external_user, c.goremal_email_address from spriden a, gobtpac b, goremal c  "
				                + "where a.spriden_pidm = b.gobtpac_pidm " 
				                + "and a.spriden_pidm = c.goremal_pidm " 
				                + "and c.goremal_emal_code =  'CA' "
				                + "and a.spriden_pidm = ?";
		
		try {
            PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
            prepStmt.setLong(1, pidm);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				student.setStudentId(rs.getString(1));
				student.setFirstName(rs.getString(2));
				student.setLastName(rs.getString(3));
				student.setThirdPartyId(rs.getString(4));
				student.setEmail(rs.getString(5));
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (RecordNotFoundException e) {
			e.printStackTrace();
		}
		return student;
	}
	
	public void massUpdates(ArrayList<Student>  students) throws NumberFormatException, IOException {
		
	      students.stream().forEach(student-> {
	    	  try {
		    	  String thirdParty = student.getEmail().substring(0, student.getEmail().indexOf("@"));
		    	  if (!externalUserExist(thirdParty,student.getPidm())) {
		    		  logger.info("Updating ---> {} {} {} {} {} {}",  student.getStudentId(), student.getPidm(), student.getFirstName(), student.getLastName(),student.getThirdPartyId());
		    		  logger.info("Removing from 'GORPAUD' third party id - {} {} {}", student.getThirdPartyId(), student.getFirstName(),student.getLastName());
					  deleteFromGorpaud(student.getThirdPartyId());
					  logger.info("Updating student record for {} {} from old {} to new third pary id {}", student.getFirstName(),student.getLastName(), student.getThirdPartyId(), student.getNewThirdPartyId());
					  updateThirdPartyId(student.getPidm(), student.getNewThirdPartyId());
		    	  }
	    	  } catch (SQLException ex) {
					// TODO Auto-generated catch block
	    		     logger.info(ex.getMessage());
					String fileName = ERROR_FILE_DIRECTORY + "errorrecs_"+getFormattedDate() + ".txt";
				    BufferedWriter writer;
					try {
						writer = new BufferedWriter(new FileWriter(fileName,true));
					    writer.write("Error Counter = "+ errorCntr);
					    writer.newLine();
					    writer.write("SPRIDEN_ID"+","+"FIRST_NAME"+","+"LAST_NAME"+","+"THIRD_PARTY_ID"+","+"NEW_THIRD_PARTY_ID");
					    writer.newLine();
					    writer.write(student.getStudentId()+","+student.getFirstName()+","+student.getLastName()+","+student.getLastName()+","+student.getNewThirdPartyId());
					    writer.newLine();
					    writer.write(ex.getMessage());
					    writer.newLine();
					    writer.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						logger.info("");
						logger.info("");
				}	
				   
	    	  
	    	
	      });
		
	}
	public void thirdPartyMassUpdates (String path) throws NumberFormatException, IOException {
		//Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
		//"\\csv\\updates\\matthew_jordan.csv"
		
		Scanner sc = new Scanner(new File(path));  
		String splitBy = ",";
		int cnt = 0;
		int errorCntr = 0;
		int cntr = 0;
		String[] errorFile =null;
		logger.info("Start time - {}", System.currentTimeMillis());
		while (sc.hasNext())  //returns a boolean value  
		{  
			try {
			String line = sc.next();
			line = line.replaceAll("\\s", "");
			line =
		        Normalizer.normalize(line, Form.NFD)
		            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			line=line.replace("'","");
			line= line.replace(" ", "");
//			line=line.replace("-", "");
			line=line.replace("?", "");
			String[] student = line.split(splitBy);
			errorFile = student;
			if (cnt > 0) {
				
				        String email = student[CA_EMAIL_ADDRESS];
						String thirdParty = email.substring(0, email.indexOf("@"));
						System.out.println(thirdParty);
						//remove comment when live
						
						if (!externalUserExist(thirdParty,Integer.parseInt(student[GOBTPAC_PIDM]))) {
							   cntr++;
							   logger.info("{} {} {} {} {} {} {}", cntr, student[0], student[1], student[2], student[3], student[4], student[5]);
								
							   logger.info("{}", student[SPRIDEN_ID]+","+student[SPRIDEN_FIRST_NAME]+","+student[SPRIDEN_LAST_NAME]);
							   logger.info("Removing from 'GORPAUD' student record with original third party id - {}", student[THIRD_PARTY_ID]);
							   deleteFromGorpaud(student[THIRD_PARTY_ID]);
							   logger.info("Removing any student record with new third party id - {}", student[NEW_THIRD_PARTY_ID]);
							   deleteFromGorpaud(student[NEW_THIRD_PARTY_ID]);
							   logger.info("Updating new third parTy record for {} {} from {} to new third pary id {}", student[2],student[3], student[THIRD_PARTY_ID], student[NEW_THIRD_PARTY_ID]);
							   updateThirdPartyId(Integer.parseInt(student[GOBTPAC_PIDM]), student[NEW_THIRD_PARTY_ID]);	
							   logger.info("");
							   logger.info("");
						}
						
			 }
			} catch (Exception sqle) {
				
			logger.info(sqle.getMessage());
				
				String fileName = ERROR_FILE_DIRECTORY + "errorrecs_"+getFormattedDate() + ".txt";
			    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,true));
			    errorCntr++;
			    writer.write("Error Counter = "+ errorCntr);
			    writer.newLine();
			    writer.write("SPRIDEN_ID"+","+"FIRST_NAME"+","+"LAST_NAME"+","+"THIRD_PARTY_ID"+","+"NEW_THIRD_PARTY_ID");
			    writer.newLine();
			    writer.write(errorFile[SPRIDEN_ID]+","+errorFile[SPRIDEN_FIRST_NAME]+","+errorFile[SPRIDEN_LAST_NAME]+","+errorFile[THIRD_PARTY_ID]+","+errorFile[NEW_THIRD_PARTY_ID]);
			    writer.newLine();
			    writer.write(sqle.getMessage());
			    writer.newLine();
			    writer.close();
				logger.info("");
				logger.info("");
			}
			cnt++;
			
		}   
		sc.close();  //closes the scanner
		logger.info("End Time: {}", System.currentTimeMillis());

	}
	
	public void setFormattedDate () {
		DteFormatter formatter = new DteFormatter();
		strDate = formatter.printDate().replace(" ", "-");
		strDate = strDate.replace(":", "-");	
	}
	
	private String getFormattedDate () {
		  return strDate;
	}
	
   public Long findPidm(String studentId) {
		
		Long pidm = 0L;

		String sqlstmt = "select spriden_pidm, spriden_id from spriden where spriden_id = ?";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, studentId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				pidm = rs.getLong(1);
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return pidm;
	}

   public boolean receiptExist(long pidm, String receiptNo){
		
		boolean found = false;
		
		String sql = "SELECT tbraccd_pidm, tbraccd_payment_id "
				+ "FROM tbraccd where tbraccd_pidm = ? and tbraccd_payment_id = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			prepStmt.setLong(1, pidm);
            prepStmt.setString(2, receiptNo);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				found = true;
			}
			
			

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return found;
	}
	public Connection getConnection() {
    	return conn;
    }
	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}	
public void insertAdhoc(String Id, String detailCode, String amount) {
	float balance = 0;
	if (detailCode.equals("CSBW")) {
		balance = -1 * Float.parseFloat(amount);
	} else if (detailCode.equals("NSBW")) {
		balance = Float.parseFloat(amount);
	}
	
	String insertStatement = "INSERT INTO tbraccd (tbraccd_pidm,TBRACCD_TRAN_NUMBER,tbraccd_term_code, "
			+ "tbraccd_detail_code,"
			+ " tbraccd_user,tbraccd_entry_date,tbraccd_amount,tbraccd_balance,tbraccd_effective_date,tbraccd_desc,tbraccd_srce_code, "
			+ "tbraccd_acct_feed_ind,tbraccd_activity_Date, tbraccd_session_number,tbraccd_document_number,tbraccd_trans_date, "
			+ "tbraccd_invoice_number,tbraccd_atyp_code,tbraccd_atyp_seqno,tbraccd_curr_code)"
			+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

	try {
		
		long pidm = getPidm(Id);
		
		PreparedStatement prepStmt = conn.prepareStatement(insertStatement);
		prepStmt.setLong(1, pidm);
		prepStmt.setInt(2, calcStudentNewTransNumber(pidm));
		prepStmt.setString(3, getCurrentTerm());
		prepStmt.setString(4, detailCode);
		prepStmt.setString(5, "CCCASHIER");
		prepStmt.setTimestamp(6, java.sql.Timestamp.valueOf(df.printDate()));
		prepStmt.setFloat(7, Float.parseFloat(amount));
		prepStmt.setFloat(8, balance);
		prepStmt.setTimestamp(9,
				java.sql.Timestamp.valueOf(df.printDate()));
		prepStmt.setString(10, detailCode.equals("NSBW")? "Student Balances in Credit": "Student Balance for Collection");
		prepStmt.setString(11, "T");
		prepStmt.setString(12, "Y");
		prepStmt.setTimestamp(13,
				java.sql.Timestamp.valueOf(df.printDate()));
		prepStmt.setDouble(14, 0);
		prepStmt.setString(15, null);
		prepStmt.setDate(16, java.sql.Date.valueOf(df.getSimpleDate()));
		prepStmt.setString(17, null);
		prepStmt.setString(18, "TM");
		prepStmt.setString(19, "");
		prepStmt.setString(20, "");
		

		prepStmt.executeUpdate();
		prepStmt.close();
		
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
public void updateTransactionWithPidm(String id, String paymentId, int transNo) {
	long pidm = getPidm(id);
	//%s %2d
	String updateStatement = "UPDATE TBRACCD SET tbraccd_pidm = ? " +
			"WHERE tbraccd_payment_id = ?  AND tbraccd_detail_code = 'SPAY' " +
			"AND tbraccd_tran_number = ? AND tbraccd_pidm = 0";
//where tbraccd_detail_code = 'SPAY' and tbraccd_pidm = ? and tbraccd_tran_number = ?
	try {
		PreparedStatement prepStmt = conn.prepareStatement(updateStatement);
		prepStmt.setLong(1, pidm);
		prepStmt.setString(2, paymentId);
		prepStmt.setInt(3, transNo);
		prepStmt.executeUpdate();
		
		prepStmt.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	private long getPidm(String id) {

		int pidm = 0;

		String sqlstmt = "select spriden_pidm, spriden_id from spriden where spriden_id = ?";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, id);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				pidm = rs.getInt(1);
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return pidm;

	}
	private boolean isChsbStudent(long pidm) {
		boolean found = false;
		String sqlstmt = "select SGRSATT_ATTS_CODE from SGRSATT where SGRSATT_PIDM = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setLong(1, pidm);
			ResultSet rs = prepStmt.executeQuery();
		    if (rs.next()) {
			  if (rs.getString(1).equals("CHSB")) {
				  found = true;
			  }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return found;
	}
	public String getCurrentTerm() {
		String term = null;

		String sqlstmt = "select min(stvterm_code) as maxtermcode from stvterm where stvterm_start_date <= ? and stvterm_end_date >= ? and stvterm_code not in ('201905') and stvterm_desc not like  '%Year%Long%'";
		
		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
            prepStmt.setDate(1, java.sql.Date.valueOf((df.getSimpleDate())));
			prepStmt.setDate(2, java.sql.Date.valueOf(df.getSimpleDate()));

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				term = rs.getString(1);
			}

			if ((term != null) && (term.indexOf("40") >= 0)) {

				term = null;

			}
			if (term == null) {

				sqlstmt = "select min(stvterm_code) as maxtermcode from stvterm where stvterm_start_date >= ? and stvterm_code <> '201905'";
				
				PreparedStatement prepStmt1 = conn.prepareStatement(sqlstmt);
				prepStmt1.setDate(1,
						java.sql.Date.valueOf((df.getSimpleDate())));

				ResultSet rs1 = prepStmt1.executeQuery();
				while (rs1.next()) {
					term = rs1.getString(1);
				}

				rs1.close();
				prepStmt1.close();
			}

			/* Only use for XRUN */
			rs.close();

			prepStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return term;
	}
	public String getSemesterPaid(String receiptno) {
		String sql = "SELECT tbraccd_term_code "
				+ "FROM taismgr.tbraccd " + "WHERE tbraccd_payment_id = ?";
        String semester = "";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			prepStmt.setString(1, receiptno);

			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				semester = rs.getString(1);
				logger.info("receipt no -{} semester - {}", receiptno, semester);
			}
            return semester;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	private int calcStudentNewTransNumber(long pidm) {
        int trans_number = 0;
		String sql = "SELECT Max(tbraccd_tran_number) as max_trans_number "
				+ "FROM taismgr.tbraccd " + "WHERE tbraccd_pidm = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			prepStmt.setLong(1, pidm);

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				trans_number = rs.getInt(1);
			}
           rs.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return trans_number + 1;
	}
//  public void manualTransactionInsert(String id, String transId, String amt){
//    	
//	  Transactions student = new Transactions();
//    	student.setstudentid(id);
//    	student.settransactionamt(amt);
//    	student.setreceiptno(transId);
//    	
//    	insertTransaction(student);
//    	
//    }
}
