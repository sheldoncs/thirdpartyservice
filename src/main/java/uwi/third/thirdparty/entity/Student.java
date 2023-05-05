package uwi.third.thirdparty.entity;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Component
public class Student {
 String studentId;
 String firstName;
 String lastName;
 String thirdPartyId;
 String newThirdPartyId;
 String email;
 String status;
 int pidm;
}
