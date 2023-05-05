package uwi.third.thirdparty.entity;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Component
public class Swap {
	private String studentIdA;
	private String thirdPartyIdA;
	private String studentIdB;
	private String thirdPartyIdB;
}
