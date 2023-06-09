package uwi.third.thirdpartyid;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import uwi.third.thirdparty.config.OracleConfig;
import uwi.third.thirdparty.dao.OracleDao;
import uwi.third.thirdparty.service.ThirdpartyServiceImpl;

@RunWith(SpringRunner.class)
//@SpringBootTest
@ContextConfiguration
@SpringBootTest(classes = TestConfiguration.class)

class ThirdpartyidApplicationTests {

	@Mock
	private ThirdpartyServiceImpl thirdpartyService;
	
	@Mock
	private OracleDao dao;
	
	@Mock
	private OracleConfig oracleConfig;
	
    @Mock
    private Connection conn;
    
    @Mock
    private PreparedStatement prepStmt;
    
    String tpId = "rick.gaskin";
	String studentId = "400003453";
	long pidm = 654325432;
	
	@Before
	void setup() throws SQLException {
		MockitoAnnotations.openMocks(this);

	}
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void testthirdPartyDelete() throws SQLException {
		
		dao = mock(OracleDao.class);
		conn = mock(Connection.class); 
		prepStmt = mock(PreparedStatement.class);
		oracleConfig = mock(OracleConfig.class);
		
		when(dao.getConnection()).thenReturn(conn);
		when(conn.prepareStatement("")).thenReturn(prepStmt);
		when(dao.findPidm(studentId)).thenReturn(pidm);
		
		thirdpartyService.thirdPartyDelete(tpId);
		verify(prepStmt).setString(1, tpId);
//		verify(dao).deleteFromGobtPac(tpId);
//		verify(dao).deleteFromGorpaud(tpId);
//		assertEquals(true, successful);
	}

}
