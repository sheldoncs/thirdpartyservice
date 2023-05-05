package uwi.third.thirdparty.util;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseDetails<T> {

	private final int status;
	private String message;
	private final T data;
	public ResponseDetails(int status, T data, String message) {
		this.status = status;
		this.data = data;
		this.message = message;
	}
	
	
}
