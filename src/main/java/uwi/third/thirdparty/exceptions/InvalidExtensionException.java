package uwi.third.thirdparty.exceptions;

public class InvalidExtensionException extends Exception {

	private static final long serialVersionUID = 1L;

	public  InvalidExtensionException() {
		 
	 }
	public InvalidExtensionException(String message) {
       super(message);
   }

   public InvalidExtensionException(String message, Throwable cause) {
       super(message, cause);
   }
}
