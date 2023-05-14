package uwi.third.thirdparty.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;


@Configuration
@Service
@Getter
@Setter
@ConfigurationProperties(prefix="oracle")
public class OracleConfig {

	private String driver;
	
	private String connect;
	
	private String username;
	
	private String password;

	@Override
	public String toString() {
		return "ExternalService [driver=" + driver + ", connect=" + connect + ", username=" + username + ", password="
				+ password + "]";
	}

	
	
	
}
