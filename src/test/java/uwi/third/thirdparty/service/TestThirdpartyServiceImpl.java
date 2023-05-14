package uwi.third.thirdparty.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import uwi.third.thirdparty.config.OracleConfig;
import uwi.third.thirdparty.dao.OracleDao;


@TestPropertySource(locations="classpath:application.properties")
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)

public class TestThirdpartyServiceImpl {

	@InjectMocks
	private ThirdpartyServiceImpl thirdpartyService;
		
	@Mock
	private OracleDao dao;
	
	@Mock
	private OracleConfig oracleConfig;
	
    @Mock
    private Connection conn;
    
    @Mock
    private FileStorageProperties fileStorageProperties;
    
    @Mock
    private PreparedStatement prepStmt;
    
    
	@Before
	public void setup() throws SQLException {
		MockitoAnnotations.openMocks(this);
		
	}
	
	
	@Test
	public void testthirdPartyDelete() throws SQLException {
		
		String tpId = "rick.gaskin";
		String studentId = "400003453";
		long pidm = 654325432;
		String downloadDir = "c:/temp";
		
		dao = mock(OracleDao.class);
		conn = mock(Connection.class); 
		prepStmt = mock(PreparedStatement.class);
//		fileStorageProperties = mock(FileStorageProperties.class); 
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
